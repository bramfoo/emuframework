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
package eu.keep.gui.settings;

import eu.keep.gui.GUI;
import eu.keep.gui.util.CheckBoxList;
import eu.keep.gui.util.LanguageCheckBox;
import eu.keep.util.Language;

import javax.swing.*;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.List;

/**
 * Frame to change Language Settings
 * @author nooe
 */
public class LanguageSettingsFrame extends JFrame {

    private static final Logger logger = Logger.getLogger(LanguageSettingsFrame.class.getName());

    private final GUI parent;
    private final Properties properties;
    private final String fileName;

    private Set<Language> availableLanguages = new HashSet<Language>();
    private Set<Language> acceptedLanguages = new HashSet<Language>();
    private static final String acceptedLanguagesProperty = "accepted.languages";
    private CheckBoxList<JCheckBox> checkBoxList = new CheckBoxList<JCheckBox>();

    private static final String select_all_label = "Select all";
    
    /**
     * Constructor
     * @param p parent GUI
     * @param fn filename of user.properties file
     */
    public LanguageSettingsFrame(GUI p, String fn) {
        super("language settings");

        parent = p;
        parent.setEnabled(false);
        parent.getGlassPane().setVisible(true);

        // read the properties file
        fileName = fn;
        properties = new Properties();
        try {
            properties.load(new FileInputStream(fileName));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            this.close();
        }
          
        initFrame();        
    }

    /**
     * Initialise this frame
     */
    private void initFrame() {
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageSettingsFrame.this.close();
            }
        });

        super.setResizable(false);
        super.setVisible(true);
        
        super.setSize(new Dimension(500, 400));
        int parentX = parent.getX();
        int parentY = parent.getY();
        int parentW = parent.getWidth();
        int parentH = parent.getHeight();
        super.setLocation(parentX + (parentW / 2) - (getWidth() / 2),
                parentY + (parentH / 2) - (getHeight() / 2));
        
        super.setLayout(new BorderLayout(5, 5));
        super.add(new JLabel("  "), BorderLayout.NORTH);
        super.add(new JLabel("  "), BorderLayout.WEST);
        super.add(new JLabel("  "), BorderLayout.EAST);

        // Add child Panels
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        initLanguagePanel(mainPanel);
        initAcceptedLanguagesPanel(mainPanel);
        initButtonPanel(mainPanel);
        super.add(mainPanel, BorderLayout.CENTER);
    }

	/**
	 * Initialise the Language Panel with a list of GUI languages
	 * @param mainPanel the GUI's mainPanel object
	 */
	private void initLanguagePanel(JPanel mainPanel) {		

		// Buttons for available language options
        JRadioButton english = new JRadioButton(Language.en.getLanguageName());
        JRadioButton german = new JRadioButton(Language.de.getLanguageName());
        JRadioButton french = new JRadioButton(Language.fr.getLanguageName());
        JRadioButton dutch = new JRadioButton(Language.nl.getLanguageName());
        english.setSelected(true);
        german.setEnabled(false);
        french.setEnabled(false);
        dutch.setEnabled(false);
        
        ButtonGroup group = new ButtonGroup();
        group.add(english);
        group.add(german);
        group.add(french);
        group.add(dutch);

        JPanel buttonGroupPanel = new JPanel(new GridLayout(0, 1));
        buttonGroupPanel.add(english);
        buttonGroupPanel.add(german);
        buttonGroupPanel.add(french);
        buttonGroupPanel.add(dutch);
		
        // User instructions
        JLabel languageInstructions = new JLabel("Select language for this user interface:");
        
        // Add everything together
        JPanel guiLanguagePanel = new JPanel(new GridLayout(0, 2));
        guiLanguagePanel.add(languageInstructions);
        guiLanguagePanel.add(buttonGroupPanel);
		mainPanel.add(guiLanguagePanel, BorderLayout.NORTH);
	}

	/**
	 * Initialise the AcceptedLanguages Panel with a list of check buttons for each available language
	 * @param mainPanel the GUI's mainPanel object
	 */
	private void initAcceptedLanguagesPanel(JPanel mainPanel) {

		boolean allLanguagesAccepted = initAcceptedLanguages();

		// User instructions
		JLabel acceptedLanguageInstructions = new JLabel("<html>" +
				"Select the acceptable languages for emulators and software. <br><br>" +
				"Deselecting languages here means that emulators and software that use " +
				"this language will not be shown or selected by the Emulation Framework." +
				"</html>");

		// List of checkboxes for each available language
		JPanel checkBoxesPanel = new JPanel(new GridLayout(0,1));

		// Select all checkbox
		final JCheckBox selectAllBox = new JCheckBox(select_all_label); // final so it can be accessed in anonymous inner classes
		selectAllBox.setSelected(allLanguagesAccepted);
		selectAllBox.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean isSelectAll = selectAllBox.isSelected();
				logger.debug("New state of select all checkbox = " + isSelectAll);
				// Propagate state of 'select all' checkbox to all individual language checkboxes
				LanguageSettingsFrame.this.checkBoxList.changeAllSelections(isSelectAll);
				if (isSelectAll) {
					acceptedLanguages.addAll(availableLanguages);
				}
				else {
					acceptedLanguages.clear();
				}
			}
		});
		checkBoxesPanel.add(selectAllBox);

		// Individual language checkboxes
		List<LanguageCheckBox> checkBoxes = new ArrayList<LanguageCheckBox>();
		for (Language language : availableLanguages) {
			LanguageCheckBox languageCheckBox = new LanguageCheckBox(language);

			// Show current state of each available language
			if (acceptedLanguages.contains(language)) {
				languageCheckBox.setSelected(true);
			} else {
				languageCheckBox.setSelected(false);
			}

			languageCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					// Add or remove this language from the set of accepted Languages
					Language language = ((LanguageCheckBox)e.getSource()).getLanguage();
					if (((LanguageCheckBox)e.getSource()).isSelected()) {
						LanguageSettingsFrame.this.acceptedLanguages.add(language);
					} 
					else {
						LanguageSettingsFrame.this.acceptedLanguages.remove(language);						
					}
					
					// Set the state of the 'select all' button according to the state of the individual boxes
					if (LanguageSettingsFrame.this.checkBoxList.isAllSelected()) {
						selectAllBox.setSelected(true);
					} else {
						selectAllBox.setSelected(false);
					}
				}				
			});
			
			checkBoxes.add(languageCheckBox);
		}
		// Have to call toArray() with empty LanguageCheckBox array, to make sure the returned array contains LanguageCheckBox objects.
		checkBoxList.setListData(checkBoxes.toArray(new LanguageCheckBox[0]));
		checkBoxesPanel.add(checkBoxList);

		// Add everything together
		JPanel accepedLanguagesPanel = new JPanel(new GridLayout(0, 2));
		accepedLanguagesPanel.add(acceptedLanguageInstructions);
		accepedLanguagesPanel.add(checkBoxesPanel);
		mainPanel.add(accepedLanguagesPanel, BorderLayout.CENTER);
	}

	/**
	 * Define the initial set of accepted languages.
	 * @return true if all available languages are selected, false otherwise
	 */
	private boolean initAcceptedLanguages() {
		// Get all available languages, from both EmulatorArchive and SoftwareArchive
		try {
			availableLanguages.addAll(parent.model.getEmulatorLanguages());
			availableLanguages.addAll(parent.model.getSoftwareLanguages());       	
		} catch (Exception e) {
			String errorMessage = "";
			if (e instanceof IOException) {
				errorMessage = "Cannot initialise Language Settings Window: EmulatorArchive or SoftwareArchive could not be contacted.";				
			} 
			else if (e instanceof IllegalArgumentException) {
				errorMessage = "Cannot initialise Language Settings Window: EmulatorArchive or SoftwareArchive contains invalid language ID.";	
			} 
			else {
				errorMessage = "Cannot initialise Language Settings Window: unknown problem. See system log-file for further info.";								
			}
			logger.error(errorMessage + ": " + e.getMessage());
			JOptionPane.showMessageDialog(parent, errorMessage, "", JOptionPane.ERROR_MESSAGE);
            this.close();
		}
		logger.debug("Total of " + availableLanguages.size() + " languages available in Emulator and Software Archives.");

		// Get the current set of accepted Languages from the Kernel
		logger.trace("Reading current acceptable languages from Kernel");
		acceptedLanguages.addAll(parent.model.getAcceptedLanguages());

		// Return true if all available languages are selected
		return (acceptedLanguages.equals(availableLanguages));
	}

	/**
	 * Initialise the Button Panel with a Save and a Cancel button
	 * @param mainPanel the GUI's mainPanel object
	 */
	private void initButtonPanel(JPanel mainPanel) {
        // Save button
        JButton save = new JButton("save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String parentMessage = "Successfully saved the new settings.";
                try {
                	LanguageSettingsFrame.this.acceptLanguages();
                    LanguageSettingsFrame.this.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(parent, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                    parentMessage = "<html>New settings saved successfully in the Emulation Framework, " +
                    		"but could not be saved for future restart.<br> Next time the Emulation Framework starts" +
                    		"the settings will be the same as before the last change.</html>";
                    LanguageSettingsFrame.this.close();
                } catch (IllegalArgumentException iae) {
                    JOptionPane.showMessageDialog(parent, iae.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                    parentMessage = " ";
                }
                parent.unlock(parentMessage);
            }
        });

        // Cancel button
        JButton cancel = new JButton("cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LanguageSettingsFrame.this.close();
            }
        });

        // Add everything together
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(save);
        buttonPanel.add(cancel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Process the selected Languages: update Kernel and save selection in user.properties
	 * @throws IOException if the user.properties file could not be updated.
	 * @throws FileNotFoundException if the user.properties file could not be opened.
	 */
	private void acceptLanguages() throws IOException {
		// Collect the selected languages
		StringBuilder newProperty = new StringBuilder();
		if (checkBoxList.isNoneSelected()) {
			String errorMessage = "<html>" +
					"You have selected no acceptable languages for emulators and software.<br>" + 
					"Please select at least one acceptable language." + 
					"</html>";
			throw new IllegalArgumentException(errorMessage);
		} else if (checkBoxList.isAllSelected()) {
			newProperty.append("all");
		} 
		else {
			for (Language acceptedLanguage : acceptedLanguages) {
				newProperty.append(acceptedLanguage.getLanguageId());
				newProperty.append(",");
			}
		}

		// Update Kernel
		parent.lock("Saving the new language settings...");
		parent.model.setAcceptedLanguages(acceptedLanguages);

		// Save selection in user.properties
		properties.setProperty(acceptedLanguagesProperty, newProperty.toString());
		properties.store(new FileOutputStream(fileName), null);		
	}

	/**
	 * Close this Frame
	 */
	private void close() {
        parent.getGlassPane().setVisible(false);
        parent.setEnabled(true);
        this.dispose();
    }
	
}
