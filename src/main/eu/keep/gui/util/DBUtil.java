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

import eu.keep.util.FileUtilities;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.*;

public final class DBUtil {

    private static Logger logger = Logger.getLogger(DBUtil.class.getName());
    private static String propFileName = "eu/keep/gui.properties";
    private static Properties props = null;

    static {

        if(!new File(propFileName).exists()) {
            throw new RuntimeException("Could not locate: " + propFileName);
        }
        try {
            props = FileUtilities.getProperties(new FileInputStream(propFileName));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static enum DB {

        CEF(props.getProperty("ef.jdbc.prefix") + props.getProperty("ef.db.url") + props.getProperty("ef.db.exists") + props.getProperty("ef.db.server"),
                props.getProperty("ef.db.schema.name"),
                props.getProperty("ef.db.admin"),
                props.getProperty("ef.db.adminpassw")),

        EA(props.getProperty("ea.jdbc.prefix") + props.getProperty("ea.db.url") + props.getProperty("ea.db.exists") + props.getProperty("ea.db.server"),
                props.getProperty("ea.db.schema.name"),
                props.getProperty("ea.db.admin"),
                props.getProperty("ea.db.adminpassw")),

        SWA(props.getProperty("swa.jdbc.prefix") + props.getProperty("swa.db.url") + props.getProperty("swa.db.exists") + props.getProperty("swa.db.server"),
                props.getProperty("swa.db.schema"),
                props.getProperty("swa.db.admin"),
                props.getProperty("swa.db.adminpassw"));

        public Connection conn;
        private final String url;
        private final String schema;
        private final String usr;
        private final String pwd;

        private DB(String url, String schema, String usr, String pwd) {
            this.url = url;
            this.schema = schema;
            this.usr = usr;
            this.pwd = pwd;
        }

        public void connect() {
            if(url == null || schema == null || usr == null || pwd == null) {

                String message = String.format("Invalid data in %s: url=%s, schema=%s, usr=%s, pwd=%s",
                        propFileName, url, schema, usr, pwd);

                logger.error(message);
                throw new RuntimeException(message);
            }
            else {
                try {
                    conn = DriverManager.getConnection(url, usr, pwd);
                } catch (SQLException e) {
                    String message = "Could not connect to the database: " + e.getMessage();
                    logger.error(message);
                    throw new RuntimeException(message);
                }
            }
        }

        public void disconnect() {
            try {
                conn.close();
            } catch (SQLException e) {
                String message = "Could not disconnect connection to the database: " + e.getMessage();
                logger.error(message);
                throw new RuntimeException(message);
            }
        }
    }


    // no need to instantiate this class
    private DBUtil() {
    }

    /**
     * <props>Returns a unique string based on zero or more existing records. For example, if the
     * records <code>existingData</code> contains the following data:</props>
     *
     * <pre>
     * data = [
     *   [abc-1, "foo"],
     *   [abc-2, "bar"],
     *   [abc-5, "baz"],
     * ]
     * </pre>
     *
     * <props><code>createUniqueStringID(data, 0, 0)</code> will return
     * <code>"abc-6"</code> (the largest number + 1 + 0). And
     * <code>createUniqueStringID(data, 0, 3)</code> will return
     * <code>"abc-9"</code> (the largest number + 1 + 3).</props>
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
     * <props>Returns a unique string based on zero or more existing records. For example, if the
     * records <code>existingData</code> contains the following data:</props>
     *
     * <pre>
     * data = [
     *   [abc-1, "foo"],
     *   [abc-2, "bar"],
     *   [abc-5, "baz"],
     * ]
     * </pre>
     *
     * <props><code>createUniqueStringID(data, 0, 0)</code> will return
     * <code>"abc-6"</code> (the largest number + 1 + 0). And
     * <code>createUniqueStringID(data, 0, 3)</code> will return
     * <code>"abc-9"</code> (the largest number + 1 + 3).</props>
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


    // todo javadoc
    public static String createUniqueIntID(Vector<Vector<String>> existingData, int indexID)
            throws RuntimeException {
        return createUniqueIntID(existingData, indexID, 0);
    }

    // todo javadoc
    public static String createUniqueIntID(Vector<Vector<String>> existingData, int indexID, int toAdd)
            throws RuntimeException {

        if(existingData.isEmpty()) {
            throw new RuntimeException("existingData is empty: cannot create a unique ID");
        }

        TreeSet<Integer> numbers = new TreeSet<Integer>();

        for(Vector<String> row : existingData) {
            String id = row.get(indexID);
            if(!id.matches("\\d+")) {
                throw new RuntimeException("Expecting an ID consisting only of numbers, encountered: " + id);
            }
            numbers.add(Integer.valueOf(id));
        }

        return String.valueOf(numbers.last() + 1 + toAdd);
    }

    // todo javadoc
    public static Vector<String> getColumn(Vector<Vector<String>> data, int columnIndex) {
        Vector<String> col = new Vector<String>();
        for(Vector<String> row : data) {
            col.add(row.get(columnIndex));
        }
        return col;
    }

    // todo javadoc
    public static int update(DB db, String sql, Object... values) {

        db.connect();

        int retVal = -1;

        try {
            db.conn.setReadOnly(false);
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

            retVal = stat.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.disconnect();

        return retVal;
    }

    // todo javadoc
    public static Vector<Vector<String>> query(DB db, String sql) throws RuntimeException {

        db.connect();

        if(db.conn == null) {
            throw new RuntimeException("No connection to: " + db);
        }

        Vector<Vector<String>> data = new Vector<Vector<String>>();
        List<Integer> columnTypes = new ArrayList<Integer>();

        try {
            db.conn.setReadOnly(true);
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

                    if (type != Types.BLOB) {
                        row.add(rs.getString(i));
                    }
                    else {
                        // TODO warn?
                    }
                }
                data.add(row);
            }

        } catch (Exception e) {
            logger.warn("Could not execute query: " + sql + ", " + e.getMessage());
        }

        db.disconnect();

        return data;
    }
}
