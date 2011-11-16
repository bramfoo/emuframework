/*
 * $Revision$ $Date$
 * $Author$
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
package eu.keep.gui.common;

import eu.keep.gui.*;
import org.apache.log4j.Logger;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class DBDataTableModel extends DefaultTableModel implements TableModelListener {

    private static final Logger logger = Logger.getLogger(DBDataTableModel.class.getName());

    private static final String BLOB_LABEL = "<<<BLOB>>>";

    protected final GUI parent;
    private Connection conn;

    protected String selectedTableName;
    protected Vector<String> columnNames;
    protected List<Integer> columnTypes;
    protected Vector<String> selectedRowBackup;

    public DBDataTableModel(GUI p, Connection c) {
        parent = p;
        conn = c;
        selectedTableName = null;
        columnNames = new Vector<String>();
        selectedRowBackup = new Vector<String>();
        columnTypes = new ArrayList<Integer>();
    }

    public void clear() {
        selectedTableName = null;
        columnNames = new Vector<String>();
        columnTypes = new ArrayList<Integer>();

        // Remove the listener, otherwise it would fire all
        // the upcoming `removeRow(i)`s.
        super.removeTableModelListener(this);

        // Remove the column names.
        super.setColumnIdentifiers(new Object[]{});

        // Remove the data itself.
        for (int i = super.getRowCount() - 1; i >= 0; i--) {
            super.removeRow(i);
        }
    }

    // TODO remove from model
    private Vector<Vector<String>> getData(String table) {
        Vector<Vector<String>> data = new Vector<Vector<String>>();
        String query = "select * from " + table;

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            Vector<String> row = new Vector<String>();

            for (int i = 1; i <= numberOfColumns; i++) {
                String columnName = rsmd.getColumnName(i);
                row.add(columnName);
                int type = rsmd.getColumnType(i);
                columnTypes.add(type);
            }
            data.add(row);

            while (rs.next()) {
                row = new Vector<String>();

                for (int i = 1; i <= numberOfColumns; i++) {

                    int type = columnTypes.get(i-1);

                    if (type == java.sql.Types.BLOB) {
                        row.add(rs.getBlob(i) == null ? null : BLOB_LABEL);
                    }
                    else if (type == java.sql.Types.CLOB) {
                    	Clob clob = rs.getClob(i);
                    	if (clob == null) {
                    		row.add("<null>");
                    	} else {
                    		StringBuffer str = new StringBuffer();
                    		String strng;                    
                    		BufferedReader bufferRead = new BufferedReader(clob.getCharacterStream());
                    		while ((strng=bufferRead.readLine())!=null) {
                    			logger.debug("read line from clob: " + strng);
                    			str.append("\n");
                    			str.append(strng);
                    		}
                    		logger.debug("converted clob to string: " + str.toString());
                    		row.add(str.toString());
                    	}
                    }
                    else {
                        row.add(rs.getString(i));
                    }
                }
                data.add(row);
            }

        } catch (Exception e) {
            logger.warn("Could not execute query: " + query + ", " + e.getMessage());
        }

        return data;
    }

    // TODO remove from model
    public void insert(List<String> data) throws Exception {

        logger.info("Data to insert: " + data + "\ncolumnNames=" + columnNames + "\ncolumnTypes=" + columnTypes);

        StringBuilder sql = new StringBuilder("INSERT INTO " + selectedTableName + " VALUES(");

        for(int i = 0; i < data.size(); i++) {
            sql.append("?");
            if(i < data.size()-1) {
                sql.append(", ");
            }
        }

        sql.append(")");

        logger.info("Executing query: " + sql);

        PreparedStatement prepStat = conn.prepareStatement(sql.toString());

        for(int i = 0; i < data.size(); i++) {
            setValue(prepStat, i+1, columnTypes.get(i), data.get(i));
        }

        prepStat.executeUpdate();
        conn.commit();
        prepStat.close();
        reload();
    }

    public void load(String table) {

        logger.info("Loading data from table: " + table);

        // Clear any existing data in this model.
        clear();

        // Remember the currently selected table.
        selectedTableName = table;

        // Get the new data
        Vector<Vector<String>> data = getData(selectedTableName);

        if (data.size() > 0) {
            columnNames = data.remove(0);
            super.setColumnIdentifiers(columnNames);

            for (Vector<String> row : data) {
                super.addRow(row);
            }
        } else {
            logger.warn("Could not get any data from " + table);
        }

        // Only add the table model listener when al data has been loaded.
        // Otherwise all the `addRow(row)`s above would cause events to be
        // fired that don't need any actions.
        super.addTableModelListener(this);
    }

    private void reload() {
        load(this.selectedTableName);
    }

    public void remember(int row) {
        selectedRowBackup = new Vector<String>((Vector<String>) super.getDataVector().get(row));
    }

    // TODO remove from model, proper 'throws' declaration. Also serious code-cleanup needed!
    public void removeSelected() throws Exception {

        StringBuilder sql = new StringBuilder("DELETE FROM " + selectedTableName + " WHERE ");

        for (int i = 0; i < columnNames.size(); i++) {
            if(columnTypes.get(i) == java.sql.Types.BLOB || selectedRowBackup.get(i) == null) continue;
            if(i > 0) sql.append(" AND ");
            sql.append(columnNames.get(i)).append("=?");
        }

        PreparedStatement prepStat = conn.prepareStatement(sql.toString());

        int paramIndex = 1;

        for(int i = 0; i < selectedRowBackup.size(); i++) {
            if(columnTypes.get(i) == java.sql.Types.BLOB || selectedRowBackup.get(i) == null) continue;
            setValue(prepStat, paramIndex, columnTypes.get(i), selectedRowBackup.get(i));
            paramIndex++;
        }

        prepStat.executeUpdate();
        conn.commit();
        prepStat.close();
        reload();
    }

    // TODO remove from model, proper 'throws' declaration. Also serious code-cleanup needed!
    public int saveRecord(Vector<String> oldValues, int column, String newValue,
                           Vector<String> columnNames, List<Integer> columnTypes)
            throws Exception {

        StringBuilder sql = new StringBuilder("UPDATE " + selectedTableName);

        sql.append(" SET ").append(columnNames.get(column)).append("=? WHERE ");

        for (int i = 0; i < columnNames.size(); i++) {
            if(columnTypes.get(i) == java.sql.Types.BLOB || oldValues.get(i) == null) continue;
            if(i > 0) sql.append(" AND ");
            sql.append(columnNames.get(i)).append("=?");
        }

        logger.info("Executing query: " + sql);
        logger.info("     - new data: " + newValue);
        logger.info("     - old data: " + oldValues);

        PreparedStatement prepStat = conn.prepareStatement(sql.toString());

        int paramIndex = 1;

        setValue(prepStat, paramIndex, columnTypes.get(column), newValue);

        for(int i = 0; i < oldValues.size(); i++) {
            if(columnTypes.get(i) == java.sql.Types.BLOB || oldValues.get(i) == null) continue;
            paramIndex++;
            setValue(prepStat, paramIndex, columnTypes.get(i), oldValues.get(i));
        }

        int retVal = prepStat.executeUpdate();

        conn.commit();

        prepStat.close();

        logger.info("Row(s) affected: " + retVal);

        return retVal;
    }

    // TODO remove from model
    private void setValue(PreparedStatement stat, int index, int type, String value)
            throws SQLException, FileNotFoundException {

        if(value == null || value.trim().isEmpty()) {
            stat.setNull(index, type);
        }
        else {
            switch (type) {
                case java.sql.Types.BLOB:
                    stat.setBlob(index, new FileInputStream(new File(value)));
                    break;
                case java.sql.Types.INTEGER:
                case java.sql.Types.DECIMAL:
                case java.sql.Types.NUMERIC:
                    stat.setInt(index, new Double(value).intValue());
                    break;
                case java.sql.Types.VARCHAR:
                    stat.setString(index, value);
                    break;
                default:
                    throw new RuntimeException("Could not insert: '" + value + "', type '" +
                            type + "' not accounted for in saveRecord(...)");
            }
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {

        final int row = e.getFirstRow();
        final int col = e.getColumn();

        final String oldValue = this.selectedRowBackup.get(col);
        final String newValue = ((Vector<String>)super.getDataVector().get(row)).get(col);

        if(newValue.equals(oldValue)) {
            // stop if no data changed
            return;
        }

        if(columnTypes.get(col) == java.sql.Types.BLOB && newValue.equals(BLOB_LABEL)) {
            // stop if the blob didn't change
            return;
        }

        parent.lock("Saving row: " + (row + 1) + ", please wait...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    saveRecord(new Vector<String>(selectedRowBackup), col, newValue, columnNames, columnTypes);
                    parent.unlock("Saved row: " + (row+1));
                } catch (Exception ex) {
                    parent.unlock("ERROR: " + ex.getMessage());
                    DBDataTableModel.this.setValueAt(oldValue, row, col);
                }
            }
        }).start();
    }
}
