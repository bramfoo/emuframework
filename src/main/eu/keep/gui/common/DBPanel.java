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
    private JButton removeButton;
    private JButton insertButton;
    private boolean tableSelected;

    public DBPanel(GUI gui, String dbUrl, String schema, String dbUsr, String dbPwd) {
        parent = gui;

        try {
            conn = DriverManager.getConnection(dbUrl, dbUsr, dbPwd);
        } catch (SQLException e) {
            conn = null;
            logger.warn("Could not connect to the database: " + e.getMessage());
        }

        dataModel = new DBDataTableModel(parent, conn);
        tableSelected = false;

        initGUI();
        initActionListeners(schema);
    }

    private void clear() {
        dataModel.clear();
        insertButton.setEnabled(false);
        removeButton.setEnabled(false);
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
                    parent.lock("Loading table: " + schema + "." + selectedTable + ", please wait...");
                    tableSelected = true;
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DBPanel.this.dataModel.load(schema + "." + selectedTable);
                            insertButton.setEnabled(true);
                            removeButton.setEnabled(false);
                            parent.unlock("Loaded table: " + schema + "." + selectedTable);
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
                    parent.lock("Loading view: " + schema + "." + selectedView + ", please wait...");
                    tableSelected = false;
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DBPanel.this.dataModel.load(schema + "." + selectedView);
                            insertButton.setEnabled(false);
                            removeButton.setEnabled(false);
                            parent.unlock("Loaded view: " + schema + "." + selectedView);
                        }
                    })).start();
                }
            }
        });

        dataTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                removeButton.setEnabled(tableSelected);
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

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(dataModel == null) {
                    return;
                }
                new InsertDialog(parent, dataModel);
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int value = JOptionPane.showConfirmDialog(parent,
                        "Are you sure you want to remove the selected record?\n\n" +
                        "This operation cannot be undone!\n", "",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if(value == JOptionPane.YES_OPTION) {
                    parent.lock("Trying to remove a record...");
                    try {
                        DBPanel.this.dataModel.removeSelected();
                        parent.unlock("Successfully removed the record.");
                        removeButton.setEnabled(false);
                    } catch (Exception ex) {
                        parent.unlock("ERROR: " + ex.getMessage());
                    }
                }
                else {
                    parent.unlock("Remove record cancelled.");
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
        JPanel dataButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        removeButton = new JButton("remove");
        insertButton = new JButton("insert");
        dataButtonPanel.add(insertButton);
        dataButtonPanel.add(removeButton);

        super.add(tablePanel, BorderLayout.NORTH);
        super.add(new JScrollPane(dataTable), BorderLayout.CENTER);
        super.add(dataButtonPanel, BorderLayout.SOUTH);

        clear();
    }
    
//    /**
//     * Enable/disable this panel, and all of its children
//     * @param enabled true to enable, false to disable
//     */
//    @Override
//    public void setEnabled(boolean enabled) {
//    	super.setEnabled(enabled);
//
//    	tableDropDown.setEnabled(enabled);
//    	viewDropDown.setEnabled(enabled);
//    	dataTable.setEnabled(enabled);
//    	removeButton.setEnabled(enabled);
//    	insertButton.setEnabled(enabled);
//    }
//    

}
