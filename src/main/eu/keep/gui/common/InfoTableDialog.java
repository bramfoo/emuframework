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

import javax.swing.*;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
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
            row.add(app.getLanguage().getLanguageName());
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
    	JTable table = new JTable(model);
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
                    int linesForTextLine = Math.max((int)Math.ceil((double)(textLines[j].length()) / new Double(62)), 1);   
        			lines = lines + linesForTextLine;        			
        		}
        	}
        	int rowHeight = 16*lines + 9;
        	totalHeight = totalHeight + rowHeight;
        	table.setRowHeight(i, rowHeight);
        }
        
        super.setSize(new Dimension(750, Math.min(totalHeight, 600)));
        super.setLayout(new BorderLayout(5, 5));
        super.add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
