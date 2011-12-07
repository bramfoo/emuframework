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

import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;
import eu.keep.gui.GUI;
import eu.keep.softwarearchive.pathway.ApplicationType;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;
import eu.keep.util.Language;

import javax.swing.*;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.util.*;

public class InfoTableDialog extends JFrame {

    private static Logger logger = Logger.getLogger(InfoTableDialog.class.getName());

    private GUI parent;

    public InfoTableDialog(GUI gui, File file, String[][] data) {
        String[] colNames = {"attribute", "value"};

        if (data.length == 0) {
            colNames = new String[]{""};
            data = new String[][]{{"No additional information about the file(s) available."}};
        }

        parent = gui;

        parent.setEnabled(false);
        parent.getGlassPane().setVisible(true);

        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                InfoTableDialog.this.close();
            }
        });

        initGUI(colNames, data);

        int parentX = parent.getX();
        int parentY = parent.getY();
        int parentW = parent.getWidth();
        int parentH = parent.getHeight();

        super.setLocation(parentX + (parentW / 2) - (getWidth() / 2),
                parentY + (parentH / 2) - (getHeight() / 2));

        super.setTitle("file: " + file.getName());
        super.setResizable(false);
        super.setVisible(true);
    }

    public InfoTableDialog(GUI gui, File file, EmulatorPackage emu, SoftwarePackage sw) {
        // TODO remove from c-tor
        java.util.List<java.util.List<String>> dataList = new ArrayList<java.util.List<String>>();

        java.util.List<String> row;

        EmulatorPackage.Emulator e = emu.getEmulator();

        row = new ArrayList<String>();
        row.add("Emulator");
        row.add("");
        dataList.add(row);

        row = new ArrayList<String>();
        row.add("Name");
        row.add(e.getName());
        dataList.add(row);

        row = new ArrayList<String>();
        row.add("Version");
        row.add(e.getVersion());
        dataList.add(row);

        row = new ArrayList<String>();
        row.add("Description");
        row.add(e.getDescription());
        dataList.add(row);

        row = new ArrayList<String>();
        row.add("Exe Type");
        row.add(e.getExecutable().getType());
        dataList.add(row);

        row = new ArrayList<String>();
        row.add("User instructions");
        row.add(e.getUserInstructions());
        dataList.add(row);

        for (ApplicationType app : sw.getApp()) {

            row = new ArrayList<String>();
            row.add("");
            row.add("");
            dataList.add(row);

            row = new ArrayList<String>();
            row.add(app.getName());
            row.add("");
            dataList.add(row);

            row = new ArrayList<String>();
            row.add("Version");
            row.add(app.getVersion());
            dataList.add(row);

            row = new ArrayList<String>();
            row.add("Description");
            row.add(app.getDescription());
            dataList.add(row);

            row = new ArrayList<String>();
            row.add("Creator");
            row.add(app.getCreator());
            dataList.add(row);

            row = new ArrayList<String>();
            row.add("Language");
            row.add((Language.valueOf(app.getLanguageId())).getLanguageName());
            dataList.add(row);

            row = new ArrayList<String>();
            row.add("License");
            row.add(app.getLicense());
            dataList.add(row);

            row = new ArrayList<String>();
            row.add("Release Date");
            row.add(app.getReleaseDate());
            dataList.add(row);

            row = new ArrayList<String>();
            row.add("Reference(s)");
            row.add(app.getReference());
            dataList.add(row);

            row = new ArrayList<String>();
            row.add("User instructions");
            row.add(app.getUserInstructions());
            dataList.add(row);
        }

        // load meta data
        Object[] colNames = {"key", "value"};
        String[][] data = new String[dataList.size()][];
        int index = 0;
        for (java.util.List<String> rw : dataList) {
            data[index] = rw.toArray(new String[rw.size()]);
            index++;
        }

        if (data.length == 0) {
            colNames = new String[]{""};
            data = new String[][]{{"No additional information about the file(s) available."}};
        }

        parent = gui;

        parent.setEnabled(false);
        parent.getGlassPane().setVisible(true);

        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                InfoTableDialog.this.close();
            }
        });

        initGUI(colNames, data);

        int parentX = parent.getX();
        int parentY = parent.getY();
        int parentW = parent.getWidth();
        int parentH = parent.getHeight();

        super.setLocation(parentX + (parentW / 2) - (getWidth() / 2),
                parentY + (parentH / 2) - (getHeight() / 2));

        super.setTitle("file: " + file.getName());
        super.setResizable(false);
        super.setVisible(true);
    }

    private void close() {
        parent.getGlassPane().setVisible(false);
        parent.setEnabled(true);
        this.dispose();
    }

    private void initGUI(Object[] colNames, Object[][] data) {
        TableModel model = new InfoTableModel(colNames, data);
    	final JTable table = new JTable(model);
        final JPopupMenu popUp = new JPopupMenu();
        table.setDefaultRenderer(Object.class, new LineWrapCellRenderer());
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Set row height, depending on amount of text in 2nd column
        int numberOfDataRows = data.length;
        
        int totalHeight = 57; // minimum height, when there are no data rows
        for (int i=0; i<numberOfDataRows; i++) {   	
        	int lines = 0;
        	if (data[i].length > 1 && (String)data[i][1] != null) {
            	String[] textLines = ((String)data[i][1]).split("\n"); // Individual new-line-separated lines in the cell's text
        		for (int j=0; j<textLines.length; j++) {
                 	// 62 is nominal number of characters that will fit on one row 
                    int linesForTextLine = Math.max((int)Math.ceil((double)(textLines[j].length()) / 62d), 1);
        			lines = lines + linesForTextLine;        			
        		}
        	}
        	int rowHeight = 16*lines + 9;
        	totalHeight = totalHeight + rowHeight;
        	table.setRowHeight(i, rowHeight);
        }

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                java.util.List<String> urls = getUrls(table, e.getPoint());

                if(urls.isEmpty()) {
                    // if there are no urls in the current cell, do nothing
                    return;
                }

                for(final String u : urls) {
                    JMenuItem url = new JMenuItem(u);
                    url.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            popUp.setVisible(false);
                            try {
                                Desktop.getDesktop().browse(new URI(u));
                            } catch(Exception ex) {
                                JOptionPane.showMessageDialog(InfoTableDialog.this,
                                        "The underlying operating system could not open " + u +
                                        "\n\nMore info: " + ex.getMessage(),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                    popUp.add(url);
                }

                JMenuItem cancel = new JMenuItem("cancel");
                cancel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // remove all previous menu items in the
                        popUp.removeAll();
                        // and hide the pop-up menu
                        popUp.setVisible(false);
                    }
                });

                popUp.add(cancel);

                popUp.setLocation(MouseInfo.getPointerInfo().getLocation());

                popUp.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                InfoTableDialog.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                InfoTableDialog.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                java.util.List<String> urls = getUrls(table, e.getPoint());
                if(!urls.isEmpty()) {
                    InfoTableDialog.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }
        });

        super.setSize(new Dimension(750, Math.min(totalHeight, 600)));
        super.setLayout(new BorderLayout(5, 5));
        super.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    /**
     * Returns a <code>List</code> with all sub-strings that look like an
     * url (if it starts with "http://", case insensitively) from the cell
     * the mouse pointer currently is hovering over (denoted by point
     * <code>p</code>), or an empty <code>List</code> if there's no such
     * sub-string.
     *
     * @param table the table
     * @param p     the <code>Point</code> where the mouse pointer is currently
     *              hovering over
     * @return      a <code>List</code> with all sub-strings that look like an
     *              url (if it starts with "http://", case insensitively), or
     *              an empty <code>List</code> if there's no such sub-string.
     */
    private java.util.List<String> getUrls(JTable table, Point p) {
        java.util.List<String> urls = new ArrayList<String>();

        // get the string over which the mouse pointer is currently hovering
        int row = table.rowAtPoint(p);
        int col = table.columnAtPoint(p);
        String cellValue = String.valueOf(table.getValueAt(row, col));

        if (!cellValue.toLowerCase().contains("http://")) {
            // if there's no 'http://' in the table-cell, return the empty List
            return urls;
        }

        // split the string on its white spaces
        String[] tokens = cellValue.split("\\s++");

        // add all substring that "look like" an url
        for (String t : tokens) {
            if (t.toLowerCase().startsWith("http://")) {
                urls.add(t);
            }
        }

        return urls;
    }
}
