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

import eu.keep.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class InsertDialog extends JDialog {

    private GUI parent;
    private DBDataTableModel dataModel;
    private java.util.List<JTextField> inputFields;
    private JButton cancelButton;
    private JButton okButton;

    public InsertDialog(GUI gui, DBDataTableModel model) {
        parent = gui;
        dataModel = model;
        inputFields = new ArrayList<JTextField>();

        parent.showGlassPane();

        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                InsertDialog.this.close();
            }
        });

        initGUI();
        initActionListeners();

        super.pack();

        int parentX = parent.getX();
        int parentY = parent.getY();
        int parentW = parent.getWidth();
        int parentH = parent.getHeight();

        super.setLocation(parentX + (parentW / 2) - (getWidth() / 2),
                parentY + (parentH / 2) - (getHeight() / 2));

        super.setTitle("Insert new record in: " + model.selectedTableName);
        super.setResizable(false);
        super.setVisible(true);
    }

    private void close() {
        parent.hideGlassPane();
        this.dispose();
    }

    private JComponent createInputPanel(String colName) {
        JPanel panel = new JPanel();
        JTextField input = new JTextField();
        input.setPreferredSize(new Dimension(300, 25));
        inputFields.add(input);
        JLabel label = new JLabel(colName);
        label.setPreferredSize(new Dimension(200, 25));
        panel.add(label);
        panel.add(input);
        return panel;
    }

    private void initActionListeners() {
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InsertDialog.this.close();
            }
        });

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.lock("Trying to insert a new record...");
                java.util.List<String> data = new ArrayList<String>();
                for(JTextField field : inputFields) {
                    String s = field.getText().trim();
                    if(s.isEmpty()) {
                        s = null;
                    }
                    data.add(s);
                }
                try {
                    dataModel.insert(data);
                    parent.unlock("Successfully inserted a new record.");
                    InsertDialog.this.close();
                } catch (Exception ex) {
                    parent.unlock("ERROR: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }

    private void initGUI() {

        super.setLayout(new GridLayout(0, 1));

        for(String colName : dataModel.columnNames) {
            super.add(createInputPanel(colName));
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        cancelButton = new JButton("cancel");
        okButton = new JButton("OK");
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        super.add(buttonPanel);
    }
}
