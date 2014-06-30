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

import eu.keep.gui.util.RBLanguages;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.List;

public class SettingsFrame extends JFrame {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final GUI parent;
    private final Properties properties;
    private final String fileName;
    private final Map<String, EFPropertyEditor> valueMap;
    
    public SettingsFrame(GUI p, String fn, List<EFProperty> editableProperties) {
        super(RBLanguages.get("settings"));

        parent = p;
        fileName = fn;
        valueMap = new LinkedHashMap<String, EFPropertyEditor>();

        parent.showGlassPane();

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

        initGUI(editableProperties);

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

    private void initGUI(List<EFProperty> editableProperties) {
        super.setSize(new Dimension(600, 400));
        super.setLayout(new BorderLayout(5, 5));
        super.add(new JLabel("  "), BorderLayout.NORTH);
        super.add(new JLabel("  "), BorderLayout.WEST);
        super.add(new JLabel("  "), BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        JPanel middlePanel = new JPanel(new BorderLayout(5, 5));
        JPanel settingsPanel = new JPanel(new GridLayout(0, 2));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Add all editable Properties
        for (EFProperty editableProperty : editableProperties) {
        	
          // Get current value for property from Properties collection
          editableProperty.setValue(properties);
          logger.info("Found editable property: key = " + editableProperty.getKey() + "; value = " + editableProperty.getValue());

          // Create suitable editor component, depending on propertyType
          EFPropertyEditor editor = null;
          switch (editableProperty.getType()) {
			case STRING:
				editor = new StringPropertyEditor(editableProperty.getValue());
				break;
			case SOFTWARE_ARCHIVE_URL:
				editor = new UrlPropertyEditor(editableProperty.getValue(), "/softwarearchive/");
				break;
			case EMULATOR_ARCHIVE_URL:
				editor = new UrlPropertyEditor(editableProperty.getValue(), "/emulatorarchive/");
				break;
			default:
				throw new IllegalArgumentException("invalid property type: " + editableProperty.getType());
          }
          
          // Add label and editor to panel
          settingsPanel.add(new JLabel(editableProperty.getDescription()));
          settingsPanel.add((JComponent)editor);
          String key = editableProperty.getKey();          
          valueMap.put(key, editor);
        }
        
        middlePanel.add(settingsPanel, BorderLayout.SOUTH);
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        JLabel instructions = new JLabel();
        RBLanguages.set(instructions, "settings_instructions");

        mainPanel.add(instructions, BorderLayout.NORTH);

        JButton save = new JButton();
        RBLanguages.set(save, "save");

        JButton cancel = new JButton();
        RBLanguages.set(cancel, "cancel");

        save.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		List<String> allValidationErrors = new ArrayList<String>();

        		// Save the new Properties
        		for(String key : valueMap.keySet()) {
        			EFPropertyEditor editor = valueMap.get(key);
        			List<String> validationErrors = editor.validateInput();
        			if (validationErrors.isEmpty()) {
        				properties.setProperty(key, editor.getValue());
        			}
        			else {
        				allValidationErrors.addAll(validationErrors);
        			}
        		}

        		if (allValidationErrors.isEmpty()) {
        			try {
        				properties.store(new FileOutputStream(fileName), null);

        				// Restart the Kernel
        				parent.lock(RBLanguages.get("restarting_ef") + " " + RBLanguages.get("log_please_wait") + "...");
        				(new Thread(new Runnable() {
        					@Override
        					public void run() {
        						parent.reloadModel();
        					}
        				})).start();
        			} 
        			catch (IOException ex) {
        				JOptionPane.showMessageDialog(parent, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
        				parent.unlock(ex.getMessage());
        			}
        			SettingsFrame.this.dispose();                    	
        		}
        		else {
        			// Display a popup-window with the errors
        			StringBuilder message = new StringBuilder();
        			message.append(RBLanguages.get("invalid_URL_header"));
        			for (String validationError : allValidationErrors) {
        				logger.info("validation Error: " + validationError);
        				message.append(validationError);
        			}
        			message.append(RBLanguages.get("invalid_URL_footer"));
        			
        			logger.info("total validation message = " + message.toString());
        			
        			JOptionPane.showMessageDialog(SettingsFrame.this, message.toString(), "", JOptionPane.ERROR_MESSAGE);                	
        		}
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
