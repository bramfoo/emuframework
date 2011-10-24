/*
 * $Revision: 21 $ $Date: 2011-06-22 11:13:07 +0200 (Wed, 22 Jun 2011) $
 * $Author: bkiers $
 * $header:
 * Copyright (c) 2009-2011 Tessella plc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information about this project, visit
 *   http://www.keep-project.eu/
 *   http://emuframework.sourceforge.net/
 * or contact us via email:
 *   blohman at users.sourceforge.net
 *   dav_m at users.sourceforge.net
 *   bkiers at users.sourceforge.net
 * Developed by:
 *   Tessella plc <www.tessella.com>
 *   Koninklijke Bibliotheek <www.kb.nl>
 *   KEEP <www.keep-project.eu>
 * Project Title: Core Emulation Framework (Core EF)$
 */
package eu.keep.gui.util;

import eu.keep.gui.GUI;
import eu.keep.util.FileUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public final class DBUtil {

    private static Logger logger = Logger.getLogger(DBUtil.class.getName());
    private static Properties p = null;

    static {
        String fileName = "eu/keep/" + GUI.PROP_FILE_NAME;

        try {
            if(!new File(fileName).exists()) {
                fileName = "C:\\BK\\IntelliJ\\SF\\core\\build\\classes\\eu\\keep\\gui.properties";
                // TODO:remove throw new FileNotFoundException(fileName);
            }

            /* TODO:remove
            InputStream is = DBUtil.class.getClass().getClassLoader().getResourceAsStream(fileName);

            if(is == null) {
                is = new FileInputStream(fileName);
            }

            p = FileUtilities.getProperties(is);
            //*/

            p = FileUtilities.getProperties(new FileInputStream(new File(fileName))); // TODO:remove

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static enum DB {

        CEF(p.getProperty("ef.jdbc.prefix") + DBUtil.p.getProperty("ef.db.url") + p.getProperty("ef.db.exists") + p.getProperty("ef.db.server"),
                p.getProperty("ef.db.schema.name"),
                p.getProperty("ef.db.admin"),
                p.getProperty("ef.db.adminpassw")),
        EA(p.getProperty("ea.jdbc.prefix") + p.getProperty("ea.db.url") + p.getProperty("ea.db.exists") + p.getProperty("ea.db.server"),
                p.getProperty("ea.db.schema.name"),
                p.getProperty("ea.db.admin"),
                p.getProperty("ea.db.adminpassw")),
        SWA(p.getProperty("swa.jdbc.prefix") + p.getProperty("swa.db.url")  + p.getProperty("swa.db.exists")  + p.getProperty("swa.db.server"),
                p.getProperty("swa.db.schema.name"),
                p.getProperty("swa.db.admin"),
                p.getProperty("swa.db.adminpassw"));

        public final Connection conn;

        private DB(String url, String schema, String usr, String pwd) {
            Connection c;
            try {
                c = DriverManager.getConnection(url, usr, pwd);
            } catch (SQLException e) {
                c = null;
                logger.warning("Could not connect to the database: " + e.getMessage());
            }
            conn = c;
        }
    }

    // no need to instantiate this class
    private DBUtil() {
    }

    /**
     * <p>Returns a unique string based on zero or more existing records. For example, if the
     * records <code>existingData</code> contains the following data:</p>
     *
     * <pre>
     * data = [
     *   [abc-1, "foo"],
     *   [abc-2, "bar"],
     *   [abc-5, "baz"],
     * ]
     * </pre>
     *
     * <p><code>createUniqueStringID(data, 0, 0)</code> will return
     * <code>"abc-6"</code> (the largest number + 1 + 0). And
     * <code>createUniqueStringID(data, 0, 3)</code> will return
     * <code>"abc-9"</code> (the largest number + 1 + 3).</p>
     *
     * @param existingData      the existing records.
     * @param indexID           the index of the rows in <code>existingData</code> which
     *                          represents the column-index of the identifier.
     * @return                  a unique string based on zero or more existing records.
     * @throws RuntimeException when the identifier does not match the regex-pattern:
     *                          <code>[a-zA-Z]+-\d+</code> (one or more letters followed
     *                          by a hyphen, followed by one or more digits)
     * @see                     #createUniqueStringID(java.util.Vector, int, int)
     */
    public static String createUniqueStringID(Vector<Vector<String>> existingData, int indexID)
            throws RuntimeException {
        return createUniqueStringID(existingData, indexID, 0);
    }

    /**
     * <p>Returns a unique string based on zero or more existing records. For example, if the
     * records <code>existingData</code> contains the following data:</p>
     *
     * <pre>
     * data = [
     *   [abc-1, "foo"],
     *   [abc-2, "bar"],
     *   [abc-5, "baz"],
     * ]
     * </pre>
     *
     * <p><code>createUniqueStringID(data, 0, 0)</code> will return
     * <code>"abc-6"</code> (the largest number + 1 + 0). And
     * <code>createUniqueStringID(data, 0, 3)</code> will return
     * <code>"abc-9"</code> (the largest number + 1 + 3).</p>
     *
     * @param existingData      the existing records.
     * @param indexID           the index of the rows in <code>existingData</code> which
     *                          represents the column-index of the identifier.
     * @param toAdd             the number to add
     * @return                  a unique string based on zero or more existing records.
     * @throws RuntimeException when the identifier does not match the regex-pattern:
     *                          <code>[a-zA-Z]+-\d+</code> (one or more letters followed
     *                          by a hyphen, followed by one or more digits)
     * @see                     #createUniqueStringID(java.util.Vector, int)
     */
    public static String createUniqueStringID(Vector<Vector<String>> existingData, int indexID, int toAdd)
            throws RuntimeException {
        String firstID = existingData.get(0).get(indexID);

        if(!firstID.matches("[a-zA-Z]+-\\d+")) {
            throw new RuntimeException("Expecting ID: 'LETTERS-DIGITS', like: 'FFT-123'");
        }

        String[] tokens = firstID.split("-");
        String prefix = tokens[0];

        TreeSet<Integer> numbers = new TreeSet<Integer>();

        for(Vector<String> row : existingData) {
            tokens = row.get(indexID).split("-");
            int n = Integer.parseInt(tokens[1]);
            numbers.add(n);
        }

        return prefix + "-" + (numbers.last() + 1 + toAdd);
    }

    public static Vector<String> getColumn(Vector<Vector<String>> data, int column) {
        Vector<String> col = new Vector<String>();
        for(Vector<String> row : data) {
            col.add(row.get(column));
        }
        return col;
    }

    public static int insert(DB db, String sql, Object... values) {
        try {
            PreparedStatement stat = db.conn.prepareStatement(sql);
            for(int i = 0; i < values.length; i++) {
                if(values[i] instanceof String) {
                    stat.setString(i + 1, (String)values[i]);
                }
                else if(values[i] instanceof Integer) {
                    stat.setInt(i + 1, (Integer)values[i]);
                }
                else {
                    // TODO warn/error?
                }
            }
            return stat.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // TODO: read-only (SQL injections) or use PreparedStatement
    public static Vector<Vector<String>> query(DB db, String sql) throws RuntimeException {
        if(db.conn == null) {
            throw new RuntimeException("No connection to: " + db);
        }

        Vector<Vector<String>> data = new Vector<Vector<String>>();
        List<Integer> columnTypes = new ArrayList<Integer>();

        try {
            Statement stmt = db.conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();

            for (int i = 1; i <= numberOfColumns; i++) {
                int type = rsmd.getColumnType(i);
                columnTypes.add(type);
            }

            while (rs.next()) {
                Vector<String> row = new Vector<String>();

                for (int i = 1; i <= numberOfColumns; i++) {

                    int type = columnTypes.get(i-1);

                    if (type != java.sql.Types.BLOB) {
                        row.add(rs.getString(i));
                    }
                    else {
                        // TODO warn?
                    }
                }
                data.add(row);
            }

        } catch (Exception e) {
            logger.warning("Could not execute query: " + sql + ", " + e.getMessage());
        }

        return data;
    }
}
