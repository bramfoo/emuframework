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
import eu.keep.gui.util.RBLanguages;
import eu.keep.util.Language;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

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
    private final Properties kernelProperties;
    private final Properties guiProperties;
    private final String kernelFileName;
    private final String guiFileName;
    private Language guiLanguage = Language.en;

    private Set<Language> availableLanguages = new HashSet<Language>();
    private Set<Language> acceptedLanguages = new HashSet<Language>();
    private static final String acceptedLanguagesProperty = "accepted.languages";
    private CheckBoxList<JCheckBox> checkBoxList = new CheckBoxList<JCheckBox>();

    private static final String select_all_label = "Select all";

    /**
     * Constructor
     *
     * @param p        parent GUI
     * @param fnKernel filename of user.properties file
     * @param fnGUI    filename of gui.properties file
     */
    public LanguageSettingsFrame(GUI p, String fnKernel, String fnGUI) {
        super(RBLanguages.get("language_settings"));

        parent = p;
        parent.showGlassPane();

        // read the properties file
        kernelFileName = fnKernel;
        guiFileName = fnGUI;
        kernelProperties = new Properties();
        guiProperties = new Properties();

        try {
            kernelProperties.load(new FileInputStream(kernelFileName));
            guiProperties.load(new FileInputStream(guiFileName));
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
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LanguageSettingsFrame.this.close();
            }
        });

        this.setResizable(false);
        this.setVisible(true);        
        this.setSize(new Dimension(500, 400));
        int parentX = parent.getX();
        int parentY = parent.getY();
        int parentW = parent.getWidth();
        int parentH = parent.getHeight();
        this.setLocation(parentX + (parentW / 2) - (getWidth() / 2),
                parentY + (parentH / 2) - (getHeight() / 2));
        
        this.setLayout(new BorderLayout(5, 0));
        this.add(new JLabel(" "), BorderLayout.NORTH);
        this.add(new JLabel(" "), BorderLayout.WEST);
        this.add(new JLabel(" "), BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new MigLayout(
        		"wrap 2", // Layout Constraints
        		"[]20[]", // Column constraints
        		"[top]50[top]push[bottom]") // Row constraints
        );

        // Add child Panels
        addLanguagePanel(mainPanel);
        addAcceptedLanguagesPanel(mainPanel);
        addButtonPanel(mainPanel);
        this.add(mainPanel, BorderLayout.CENTER);
    }

	/**
	 * Initialise the Language Panel with a list of GUI languages
	 * @param mainPanel the GUI's mainPanel object
	 */
	private void addLanguagePanel(JPanel mainPanel) {		

        guiLanguage = Language.valueOf(guiProperties.getProperty("language"));

		// Buttons for available language options
        final JRadioButton english = new JRadioButton(Language.en.getLanguageName(), guiLanguage == Language.en);
//        final JRadioButton german = new JRadioButton(Language.de.getLanguageName(), guiLanguage == Language.de);
        final JRadioButton french = new JRadioButton(Language.fr.getLanguageName(), guiLanguage == Language.fr);
        final JRadioButton dutch = new JRadioButton(Language.nl.getLanguageName(), guiLanguage == Language.nl);

        english.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiLanguage = Language.en;
            }
        });

//        german.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                guiLanguage = Language.de;
//            }
//        });

        french.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiLanguage = Language.fr;
            }
        });

        dutch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiLanguage = Language.nl;
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(english);
//        group.add(german);
        group.add(french);
        group.add(dutch);

        JPanel buttonGroupPanel = new JPanel(new GridLayout(0, 1));
        buttonGroupPanel.add(english);
//        buttonGroupPanel.add(german);
        buttonGroupPanel.add(french);
        buttonGroupPanel.add(dutch);
        
        // User instructions
        JLabel languageInstructions = new JLabel(RBLanguages.get("language"));
        
        // Add everything to the main panel
        mainPanel.add(languageInstructions);
        mainPanel.add(buttonGroupPanel);
	}

	/**
	 * Initialise the AcceptedLanguages Panel with a list of check buttons for each available language
	 * @param mainPanel the GUI's mainPanel object
	 */
	private void addAcceptedLanguagesPanel(JPanel mainPanel) {

		boolean allLanguagesAccepted = initAcceptedLanguages();

		// User instructions
		JLabel acceptedLanguageInstructions = new JLabel(RBLanguages.get("user_instructions_languages"));
		
		// List of checkboxes for each available language
		JPanel checkBoxesPanel = new JPanel(new MigLayout(
        		"wrap 1", // Layout Constraints
        		"0[]", // Column constraints
        		"0[top][center][top]") // Row constraints
        );
		
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
		
		// Horizontal line to divide 'select all' checkbox from list below
		JSeparator line = new JSeparator(SwingConstants.HORIZONTAL);
		line.setPreferredSize(new Dimension(100,1));
		checkBoxesPanel.add(line);
		
		
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

		// Add everything to the main panel
		mainPanel.add(acceptedLanguageInstructions);
		mainPanel.add(checkBoxesPanel);
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
	private void addButtonPanel(JPanel mainPanel) {
        // Save button
        JButton save = new JButton(RBLanguages.get("save"));
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String parentMessage = "";
                try {
                	LanguageSettingsFrame.this.acceptLanguages();
                    parentMessage = RBLanguages.get("successfully_saved_settings");
                    LanguageSettingsFrame.this.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(parent, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                    parentMessage = RBLanguages.get("successfully_saved_settings_session_only");
                    LanguageSettingsFrame.this.close();
                } catch (IllegalArgumentException iae) {
                    JOptionPane.showMessageDialog(parent, iae.getMessage(), "", JOptionPane.ERROR_MESSAGE);
                    parentMessage = iae.getMessage();
                }
                parent.unlock(parentMessage);
            }
        });

        // Cancel button
        JButton cancel = new JButton(RBLanguages.get("cancel"));
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
        mainPanel.add(buttonPanel, new CC().cell(1,2).alignX("right").alignY("bottom"));
	}

	/**
	 * Process the selected Languages: update Kernel and save selection in user.properties
	 * @throws IOException if the user.properties file could not be updated.
	 * @throws FileNotFoundException if the user.properties file could not be opened.
	 */
	private void acceptLanguages() throws IOException {

        // set the new GUI language and save the choice to the properties file
        RBLanguages.change(guiLanguage);
        guiProperties.setProperty("language", guiLanguage.getLanguageId());

		// Collect the selected languages
		StringBuilder newProperty = new StringBuilder();
		if (checkBoxList.isNoneSelected()) {
			String errorMessage = RBLanguages.get("no_acceptable_languages");
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
		parent.lock(RBLanguages.get("saving_new_language_settings") + "...");
		parent.model.setAcceptedLanguages(acceptedLanguages);

		// Save selection in user.properties
		kernelProperties.setProperty(acceptedLanguagesProperty, newProperty.toString());
		kernelProperties.store(new FileOutputStream(kernelFileName), null);

        // store the GUI-language
        guiProperties.store(new FileOutputStream(guiFileName), null);
	}

	/**
	 * Close this Frame
	 */
	private void close() {
        parent.hideGlassPane();
        this.dispose();
    }
	
}
