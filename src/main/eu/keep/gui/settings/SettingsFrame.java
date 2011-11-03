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

import eu.keep.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class SettingsFrame extends JFrame {

    private final GUI parent;
    private final Properties properties;
    private final String fileName;
    private final Map<String, JTextField> valueMap;

    public SettingsFrame(GUI p, String fn, String instructions, String[] keys) {
        super("settings");

        parent = p;
        fileName = fn;
        valueMap = new LinkedHashMap<String, JTextField>();

        parent.setEnabled(false);
        parent.getGlassPane().setVisible(true);

        // read the properties file
        properties = new Properties();
        try {
            properties.load(new FileInputStream(fileName));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            this.close();
        }

        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SettingsFrame.this.close();
            }
        });

        initGUI(instructions, keys);

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
        parent.getGlassPane().setVisible(false);
        parent.setEnabled(true);
        this.dispose();
    }

    private void initGUI(String instructions, String[] keys) {
        super.setSize(new Dimension(500, 400));
        super.setLayout(new BorderLayout(5, 5));
        super.add(new JLabel("  "), BorderLayout.NORTH);
        super.add(new JLabel("  "), BorderLayout.WEST);
        super.add(new JLabel("  "), BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        JPanel middlePanel = new JPanel(new BorderLayout(5, 5));
        JPanel settingsPanel = new JPanel(new GridLayout(0, 2));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        for(String key : keys) {
            JTextField txt = new JTextField(properties.getProperty(key));
            settingsPanel.add(new JLabel(key));
            settingsPanel.add(txt);
            valueMap.put(key, txt);
        }

        middlePanel.add(settingsPanel, BorderLayout.SOUTH);
        mainPanel.add(middlePanel, BorderLayout.CENTER);
        mainPanel.add(new JLabel(instructions), BorderLayout.NORTH);

        JButton save = new JButton("save");
        JButton cancel = new JButton("cancel");

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    for(String key : valueMap.keySet()) {
                        properties.setProperty(key, valueMap.get(key).getText());
                    }
                    properties.store(new FileOutputStream(fileName), null);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(parent, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                }
                SettingsFrame.this.close();
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsFrame.this.close();
            }
        });

        buttonPanel.add(save);
        buttonPanel.add(cancel);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        super.add(mainPanel, BorderLayout.CENTER);
    }
}
