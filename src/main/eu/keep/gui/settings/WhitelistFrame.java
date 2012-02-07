/*
 * $Revision: 31 $ $Date: 2011-10-19 12:10:56 +0200 (Wed, 19 Oct 2011) $
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
package eu.keep.gui.settings;

import eu.keep.downloader.Downloader;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;
import eu.keep.gui.GUI;
import eu.keep.gui.util.DBUtil;
import eu.keep.gui.util.RBLanguages;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class WhitelistFrame extends JFrame {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final GUI parent;
    private final Properties properties;
    private final String fileName;
    private Object[][] data;
    
    private EmuTableModel model;
    
    private Set<Integer> originalEmuIDs = new HashSet<Integer>();

    
    /**
     * Constructor
     * @param p parent GUI
     * @param fn name of Properties file
     */
    public WhitelistFrame(GUI p, String fn) {
        super(RBLanguages.get("emulator_whitelist"));

        parent = p;
        fileName = fn;

        parent.showGlassPane();

        // read the properties file
        properties = new Properties();
        try {
            properties.load(new FileInputStream(fileName));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            this.close();
        }

        Vector<Vector<String>> enabledEmus = DBUtil.query(DBUtil.DB.CEF,
                "SELECT emulator_id FROM engine.emulator_whitelist ORDER BY emulator_id");
        for(Vector<String> row : enabledEmus) {
            originalEmuIDs.add(Integer.valueOf(row.get(0)));
        }

        try {
            Downloader downloader = new Downloader(properties, DBUtil.DB.EA.conn);
            List<EmulatorPackage> emus = downloader.getEmulatorPackages();
            data = new Object[emus.size()][3];

            int index = 0;
            for(EmulatorPackage ep : emus) {
                Integer id = ep.getPackage().getId();
                String exeType = ep.getEmulator().getExecutable().getType().toLowerCase();
                String os = exeType.contains("exe") ? " (Windows)" : exeType.contains("elf") ? " (Linux)" : "";
                String name = ep.getEmulator().getName() + " " + ep.getEmulator().getVersion() + os;

                data[index][0] = id;
                data[index][1] = name;
                data[index][2] = originalEmuIDs.contains(id);

                index++;
            }
            
        } catch(Exception e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            this.close();
        }

        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                WhitelistFrame.this.close();
            }
        });

        initGUI();

        int parentX = parent.getX();
        int parentY = parent.getY();
        int parentW = parent.getWidth();
        int parentH = parent.getHeight();

        super.setLocation(parentX + (parentW / 2) - (getWidth() / 2),
                parentY + (parentH / 2) - (getHeight() / 2));

        super.setResizable(false);
        super.setVisible(true);
    }

    private void close() {
        parent.hideGlassPane();
        this.dispose();
    }

    private void initGUI() {
        super.setSize(new Dimension(600, 400));
        super.setLayout(new BorderLayout(5, 5));

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));

        model = new EmuTableModel(data);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(1).setPreferredWidth(500);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = initButtonPanel();
        mainPanel.add(buttons, BorderLayout.SOUTH);
        
        super.add(mainPanel, BorderLayout.CENTER);
    }

	/**
	 * Initialise the Button Panel with a Save and a Cancel button
	 * @param mainPanel the GUI's mainPanel object
	 */
	private JPanel initButtonPanel() {
        // Save button
        JButton save = new JButton(RBLanguages.get("save"));
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	// Save the changes
            	WhitelistFrame.this.saveChanges();
            	
                String parentMessage = RBLanguages.get("saved_whitelist");
                parent.unlock(parentMessage);
                WhitelistFrame.this.close();
            }
        });

        // Cancel button
        JButton cancel = new JButton(RBLanguages.get("cancel"));
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WhitelistFrame.this.close();
            }
        });

        // Add everything together
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(save);
        buttonPanel.add(cancel);
        
        return buttonPanel;
	}

	/**
	 * Save the changes to the database
	 */
	private void saveChanges() {

		for (Map.Entry<Integer, String> addedEmulator : model.addedEmulators.entrySet()) {
			
			logger.info("Adding emulator with ID " + addedEmulator.getKey() + " to whitelist");
			
			int affected = DBUtil.update(DBUtil.DB.CEF,
					"INSERT INTO engine.emulator_whitelist (emulator_id, emulator_descr) VALUES (?, ?)",
					addedEmulator.getKey(), addedEmulator.getValue()
					);

			if(affected != 1) {
				logger.warn("Expected 1 row in the database to be affected, but the numbers is: " + affected);
			}
		}

		for (Integer removedEmulator : model.removedEmulators) {
			
			logger.info("Removing emulator with ID " + removedEmulator + " from whitelist");
			
			int affected = DBUtil.update(DBUtil.DB.CEF,
					"DELETE FROM engine.emulator_whitelist WHERE emulator_id=?",
					removedEmulator
					);

			if(affected != 1) {
				logger.warn("Expected 1 row in the database to be affected, but the numbers is: " + affected);
			}
		}

	}

	
    class EmuTableModel extends AbstractTableModel {

        private String[] columnNames = {"id", "emulator", "enabled"};
        private Object[][] data;

		private Map<Integer, String> addedEmulators = new HashMap<Integer, String>();
        private Set<Integer> removedEmulators = new HashSet<Integer>();
        
        public EmuTableModel(Object[][] d) {
        	data = d;
        	addTableListener();
        }

		private void addTableListener() {
			this.addTableModelListener(new TableModelListener() {
        		@Override
        		public void tableChanged(TableModelEvent e) {

        			// Find the entry that was edited
        			int row = e.getFirstRow();
        			Integer id = (Integer)EmuTableModel.this.getValueAt(row, 0);
        			String name = (String)EmuTableModel.this.getValueAt(row, 1);
        			Boolean enabled = (Boolean)EmuTableModel.this.getValueAt(row, 2);

        			if (WhitelistFrame.this.originalEmuIDs.contains(id)) {
        				// This emulator was originally whitelisted
        				
        				if (enabled) {
        					// This emulator must not be removed 
        					// (it could previously have been added to the 'removedEmulators' Set)
        					logger.info("Removing emulator with ID " + id + " from remove list");
        					removedEmulators.remove(id);
        				} else {
            				// This emulator must now be removed
        					logger.info("Adding emulator with ID " + id + " to remove list");
            				removedEmulators.add(id);        				
        				}	
        			}
        			else {
        				// This emulator was originally not whitelisted
        				
        				if (enabled) {
        					// This emulator must now be added
        					logger.info("Adding emulator with ID " + id + " to add list");
        					addedEmulators.put(id, name);
        				}
        				else {
        					// This emulator must not be added
        					// (it could previously have been added to the 'addedEmulators' Map)
        					logger.info("Removing emulator with ID " + id + " from add list");
        					addedEmulators.remove(id);
        				}
        			}
        			
        		}
        	});
		}

		
        public Map<Integer, String> getAddedEmulators() {
			return addedEmulators;
		}

		public Set<Integer> getRemovedEmulators() {
			return removedEmulators;
		}
        
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return col == 2;
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }
}
