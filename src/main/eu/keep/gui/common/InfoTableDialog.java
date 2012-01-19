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
import eu.keep.gui.util.InfoTableCell;
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

    public InfoTableDialog(GUI gui, File file, String[][] dataList) {
        String[] colNames = {"attribute", "value"};
        InfoTableCell[][] tableData = new InfoTableCell[dataList.length][];

        if (dataList.length == 0) {
            colNames = new String[]{""};
            tableData = new InfoTableCell[][]{{new InfoTableCell("No additional information about the file(s) available.", false)}};
        } else {
            for (int row = 0; row<dataList.length; row++) {
            	String[] dataRow = dataList[row];
            	InfoTableCell[] tableRow = new InfoTableCell[dataRow.length]; 
            	for (int col = 0; col<dataRow.length; col++) {
            		String text = dataRow[col];
            		tableRow[col] = new InfoTableCell(text, false);           		
            	}
            	tableData[row] = tableRow;
            }    
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

        initGUI(colNames, tableData);

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

    	// load meta data
        java.util.List<java.util.List<InfoTableCell>> dataList = new ArrayList<java.util.List<InfoTableCell>>();
        initEmuData(emu, dataList);
        initSoftwareData(sw, dataList);

        String[] colNames = {"key", "value"};
        InfoTableCell[][] tableData = new InfoTableCell[dataList.size()][];
        int index = 0;
        for (java.util.List<InfoTableCell> row : dataList) {
            tableData[index] = row.toArray(new InfoTableCell[row.size()]);
            index++;
        }
        if (tableData.length == 0) {
            colNames = new String[]{""};
            tableData = new InfoTableCell[][]{{new InfoTableCell("No additional information about the file(s) available.", false)}};
        }

        // link to parent GUI
        parent = gui;
        parent.setEnabled(false);
        parent.getGlassPane().setVisible(true);

        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                InfoTableDialog.this.close();
            }
        });

        initGUI(colNames, tableData);

        int parentX = parent.getX();
        int parentY = parent.getY();
        int parentW = parent.getWidth();
        int parentH = parent.getHeight();

        super.setLocation(parentX + (parentW / 2) - (getWidth() / 2),
                parentY + (parentH / 2) - (getHeight() / 2));

        super.setTitle(file == null ? "no object" : ("file: " + file.getName()));
        super.setResizable(false);
        super.setVisible(true);
    }

    /**
     * Extract metadata from an EmulatorPackage object and add it to the dataList
     * @param emu the EmulatorPackage object
     * @param dataList the list of metadata to be displayed in the table.
     */
	private void initEmuData(EmulatorPackage emu, java.util.List<java.util.List<InfoTableCell>> dataList) {
		java.util.List<InfoTableCell> row;
		EmulatorPackage.Emulator e = emu.getEmulator();

		// Add Section Title: "Emulator"
        row = new ArrayList<InfoTableCell>();
        row.add(new InfoTableCell("Emulator", true));
        row.add(new InfoTableCell("", true));
        dataList.add(row);

        row = new ArrayList<InfoTableCell>();
        row.add(new InfoTableCell("Name", false));
        row.add(new InfoTableCell(e.getName(), false));
        dataList.add(row);

        row = new ArrayList<InfoTableCell>();
        row.add(new InfoTableCell("Version", false));
        row.add(new InfoTableCell(e.getVersion(), false));
        dataList.add(row);

        row = new ArrayList<InfoTableCell>();
        row.add(new InfoTableCell("Description", false));
        row.add(new InfoTableCell(e.getDescription(), false));
        dataList.add(row);

        row = new ArrayList<InfoTableCell>();
        row.add(new InfoTableCell("Exe Type", false));
        row.add(new InfoTableCell(e.getExecutable().getType(), false));
        dataList.add(row);

        row = new ArrayList<InfoTableCell>();
        row.add(new InfoTableCell("User instructions", false));
        row.add(new InfoTableCell(e.getUserInstructions(), false));
        dataList.add(row);
	}

	/**
     * Extract metadata from a SoftwarePackage object and add it to the dataList
	 * @param sw the SoftwarePackage object
     * @param dataList the list of metadata to be displayed in the table.
	 */
	private void initSoftwareData(SoftwarePackage sw, java.util.List<java.util.List<InfoTableCell>> dataList) {
		java.util.List<InfoTableCell> row;
		
		for (ApplicationType app : sw.getApp()) {

            row = new ArrayList<InfoTableCell>();
            row.add(new InfoTableCell("", false));
            row.add(new InfoTableCell("", false));
            dataList.add(row);

    		// Add Section Title: "Application"
            row = new ArrayList<InfoTableCell>();
            row.add(new InfoTableCell(app.getName(), true));
            row.add(new InfoTableCell("", true));
            dataList.add(row);

            row = new ArrayList<InfoTableCell>();
            row.add(new InfoTableCell("Version", false));
            row.add(new InfoTableCell(app.getVersion(), false));
            dataList.add(row);

            row = new ArrayList<InfoTableCell>();
            row.add(new InfoTableCell("Description", false));
            row.add(new InfoTableCell(app.getDescription(), false));
            dataList.add(row);

            row = new ArrayList<InfoTableCell>();
            row.add(new InfoTableCell("Creator", false));
            row.add(new InfoTableCell(app.getCreator(), false));
            dataList.add(row);

            row = new ArrayList<InfoTableCell>();
            row.add(new InfoTableCell("Language", false));
            row.add(new InfoTableCell((Language.valueOf(app.getLanguageId())).getLanguageName(), false));
            dataList.add(row);

            row = new ArrayList<InfoTableCell>();
            row.add(new InfoTableCell("License", false));
            row.add(new InfoTableCell(app.getLicense(), false));
            dataList.add(row);

            row = new ArrayList<InfoTableCell>();
            row.add(new InfoTableCell("Release Date", false));
            row.add(new InfoTableCell(app.getReleaseDate(), false));
            dataList.add(row);

            row = new ArrayList<InfoTableCell>();
            row.add(new InfoTableCell("Reference(s)", false));
            row.add(new InfoTableCell(app.getReference(), false));
            dataList.add(row);

            row = new ArrayList<InfoTableCell>();
            row.add(new InfoTableCell("User instructions", false));
            row.add(new InfoTableCell(app.getUserInstructions(), false));
            dataList.add(row);
        }
	}

	
	private void close() {
        parent.getGlassPane().setVisible(false);
        parent.setEnabled(true);
        this.dispose();
    }

    private void initGUI(Object[] colNames, InfoTableCell[][] data) {
        TableModel model = new InfoTableModel(colNames, data);
    	final JTable table = new JTable(model);
        table.setDefaultRenderer(Object.class, new LineWrapCellRenderer());
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(new Color(UIManager.getColor("background").getRGB()));
                
        // Set row height, depending on amount of text in 2nd column
        int totalHeight = 57; // minimum height, when there are no data rows
        for (int i=0; i<data.length; i++) {   	
        	int lines = 0;
        	if (data[i].length > 1 && data[i][1].getText() != null) {        		
            	String[] textLines = data[i][1].getText().split("\n"); // Individual new-line-separated lines in the cell's text
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

        addMouseListenersForURLs(table);

        super.setSize(new Dimension(750, Math.min(totalHeight, 600)));
        super.setLayout(new BorderLayout(5, 5));
        super.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    /**
     * Add Mouse Listeners to table cells, to deal with URLs.
     * If a cell contains a URL in its text, make the cursor a HandCursor. 
     * If the mouse is then clicked, display a popup menu with clickable links. 
     * @param table the table
     */
	private void addMouseListenersForURLs(final JTable table) {

		// MouseMotionListener: if cell contains a URL in its text, make the cursor a HandCursor. 
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

        // MouseListener: if mouse is pressed on a cell containing URLs, display a popup menu with clickable links
        table.addMouseListener(new MouseAdapter() {
   			@Override
			public void mouseExited(MouseEvent e) {
                InfoTableDialog.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                java.util.List<String> urls = getUrls(table, e.getPoint());
                if(urls.isEmpty()) {
                    // if there are no urls in the current cell, do nothing
                    return;
                }

                final JPopupMenu popUp = new JPopupMenu();
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

        });

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
        String cellValue = ((InfoTableCell)table.getValueAt(row, col)).getText();

        if (!cellValue.toLowerCase().contains("http://")) {
            // if there's no 'http://' in the table-cell, return the empty List
            return urls;
        }

        // split the string on its white spaces
        String[] tokens = cellValue.split("[.,?!]?\\s++[.,?!]?");

        // add all substring that "look like" an url
        for (String t : tokens) {
            if (t.toLowerCase().startsWith("http://")) {
                urls.add(t);
            }
        }

        return urls;
    }
}
