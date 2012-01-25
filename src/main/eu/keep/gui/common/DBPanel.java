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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import eu.keep.gui.util.RBLanguages;
import org.apache.log4j.Logger;

public class DBPanel extends JPanel {

    private static Logger logger = Logger.getLogger(DBPanel.class.getName());

    // TODO remove from GUI
    private Connection conn;

    protected final GUI parent;
    private DBDataTableModel dataModel;
    private JComboBox tableDropDown;
    private JComboBox viewDropDown;
    private JTable dataTable;
    private boolean tableSelected;

    public DBPanel(GUI gui, String dbUrl, String schema, String dbUsr, String dbPwd) {
        parent = gui;

        try {
            conn = DriverManager.getConnection(dbUrl, dbUsr, dbPwd);
        } catch (SQLException e) {
            conn = null;
            logger.warn(RBLanguages.get("log_error_connect_db") + ": " + e.getMessage());
        }

        dataModel = new DBDataTableModel(parent, conn);
        tableSelected = false;

        initGUI();
        initActionListeners(schema);
    }

    private void clear() {
        dataModel.clear();
    }

    // TODO move to central DB- or util-class
    /*
     * type = "TABLE" or "VIEW"
     */
    private String[] getNames(String type) {
        List<String> list = new ArrayList<String>();
        list.add("");
        try {
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet resultSet = dbmd.getTables(null, null, "%", new String[]{type});
            while (resultSet.next()) {
                String tableName = resultSet.getString(3);
                list.add(tableName);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return list.toArray(new String[list.size()]);
    }

    private void initActionListeners(final String schema) {

        tableDropDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActionListener action = viewDropDown.getActionListeners()[0];
                viewDropDown.removeActionListener(action);
                viewDropDown.setSelectedIndex(0);
                viewDropDown.addActionListener(action);

                final String selectedTable = (String)tableDropDown.getSelectedItem();
                if(selectedTable.isEmpty() || conn == null) {
                    clear();
                }
                else {
                    parent.lock(RBLanguages.get("log_loading_table") + ": " + schema + "." + selectedTable + ", " +
                            RBLanguages.get("log_please_wait") + "...");
                    tableSelected = true;
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DBPanel.this.dataModel.load(schema + "." + selectedTable);
                            parent.unlock(RBLanguages.get("log_loaded_table") + ": " + schema + "." + selectedTable);
                        }
                    })).start();
                }
            }
        });

        viewDropDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActionListener action = tableDropDown.getActionListeners()[0];
                tableDropDown.removeActionListener(action);
                tableDropDown.setSelectedIndex(0);
                tableDropDown.addActionListener(action);

                final String selectedView = (String)viewDropDown.getSelectedItem();
                if(selectedView.isEmpty() || conn == null) {
                    clear();
                }
                else {
                    parent.lock(RBLanguages.get("log_loading_view") + ": " + schema + "." + selectedView + ", " +
                            RBLanguages.get("log_please_wait") + "...");
                    tableSelected = false;
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DBPanel.this.dataModel.load(schema + "." + selectedView);
                            parent.unlock(RBLanguages.get("log_loaded_view") + ": " + schema + "." + selectedView);
                        }
                    })).start();
                }
            }
        });

        dataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	if (dataTable.isEnabled()) {
                    dataModel.remember(dataTable.getSelectedRow());            		
            	}
            }
        });
    }

    private void initGUI() {
        super.setLayout(new BorderLayout(5, 5));

        JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        JLabel tableLabel = new JLabel("TABLE: ");
        tableDropDown = new JComboBox(getNames("TABLE"));
        tableDropDown.setPreferredSize(new Dimension(200, 25));
        tablePanel.add(tableLabel);
        tablePanel.add(tableDropDown);

        JLabel viewLabel = new JLabel("    VIEW: ");
        viewDropDown = new JComboBox(getNames("VIEW"));
        viewDropDown.setPreferredSize(new Dimension(200, 25));
        tablePanel.add(viewLabel);
        tablePanel.add(viewDropDown);

        dataTable = new JTable(dataModel);

        super.add(tablePanel, BorderLayout.NORTH);
        super.add(new JScrollPane(dataTable), BorderLayout.CENTER);

        clear();
    }
    
}
