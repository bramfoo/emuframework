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
package eu.keep.gui.explorer;

import eu.keep.characteriser.Format;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;
import eu.keep.gui.GUI;
import eu.keep.gui.common.InfoTableDialog;
import eu.keep.gui.util.RBLanguages;
import eu.keep.softwarearchive.pathway.ObjectFormatType;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class FileExplorerPanel extends JPanel implements ActionListener {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public File selectedFile;
    private File clickedFile;
    private GUI parent;

    private JPanel explorerPanel;
    private JPanel noObjectPanel;
    private JButton autoStart;
    private JButton checkEnvironment;
    private JButton startWithoutObject;
    private JMenuItem info;
    private FileTree tree;
    
    public FileExplorerPanel(GUI p) {
        selectedFile = null;
        parent = p;
        initGUI();
    }

    private void initGUI() {

        super.setLayout(new BorderLayout(5, 5));

        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 5));
        explorerPanel = initExplorerPanel();
        noObjectPanel = initNoObjectPanel();
        
        super.add(explorerPanel, BorderLayout.SOUTH);
        super.add(noObjectPanel, BorderLayout.NORTH);
    }

    /**
     * Initialise the FileExplorer Panel
     * @return the FileExplorer Panel, containing a dropdown to select the root file-system,
     * 			a fileTree to browse to the desired file, and two buttons to start emulation
     * 			automatically or to select pathways, emulators and software manually.
     */
	private JPanel initExplorerPanel() {

		// Roots dropdown
        final File[] roots = File.listRoots();
        final JComboBox rootsCombo = new JComboBox(roots);

        // FileTree
        tree = null;
        if(roots.length > 0) {
            File start = roots[0];
            for(File root : roots) {
                String name = root.getAbsolutePath().toUpperCase();
                // skip possible disk drives in case of Windows
                if(!(name.startsWith("A:") || name.startsWith("B:"))) {
                    start = root;
                    break;
                }
            }
            rootsCombo.setSelectedItem(start);
            initFileTree(start);
        }
        else {
            logger.error("Could not read the file system.");
        }

        rootsCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.setRoot(new FileTreeNode((File)rootsCombo.getSelectedItem()));
            }
        });

		// button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 1));
        autoStart = new JButton();
        RBLanguages.set(autoStart, "autoStart");

        checkEnvironment = new JButton();
        RBLanguages.set(checkEnvironment, "checkEnvironment");

        autoStart.setEnabled(false);
        checkEnvironment.setEnabled(false);

        autoStart.addActionListener(this);
        checkEnvironment.addActionListener(this);

        buttonPanel.add(autoStart);
        buttonPanel.add(checkEnvironment);


        // Add everything together
        JPanel explorerPanel = new JPanel(new BorderLayout(5, 5));    
        explorerPanel.setPreferredSize(new Dimension((GUI.WIDTH_UNIT * 40) - 30, GUI.HEIGHT-200));
        Border border = new TitledBorder("");
        
        JPanel north = new JPanel(new BorderLayout(5, 5));

        JLabel explanation = new JLabel();
        explanation.setFont(new Font(null, Font.BOLD, 12));
        RBLanguages.set(explanation, "start_environment");
        north.add(explanation, BorderLayout.NORTH);
        explorerPanel.setBorder(border);

        north.add(rootsCombo, BorderLayout.SOUTH);

        explorerPanel.add(north, BorderLayout.NORTH);
        explorerPanel.add(new JScrollPane(tree), BorderLayout.CENTER);
        explorerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
		return explorerPanel;
	}

    /**
     * Initialise the FileExplorer Panel
     * @return the FileExplorer Panel, containing a dropdown to select the root file-system,
     * 			a fileTree to browse to the desired file, and two buttons to start emulation
     * 			automatically or to select pathways, emulators and software manually.
     */
	private JPanel initNoObjectPanel() {
		
		// button panel
        startWithoutObject = new JButton();
        RBLanguages.set(startWithoutObject, "start");
        startWithoutObject.setEnabled(true);
        startWithoutObject.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 1));
        buttonPanel.add(startWithoutObject);

        // Add everything together
        JPanel noObjectPanel = new JPanel();
        noObjectPanel.setLayout(new BorderLayout(5, 5));
        Border noObjectBorder = new TitledBorder("");
        noObjectPanel.setBorder(noObjectBorder);

        JLabel explanation = new JLabel();
        explanation.setFont(new Font(null, Font.BOLD, 12));
        RBLanguages.set(explanation, "start_environment_without");

        noObjectPanel.add(explanation, BorderLayout.NORTH);
        noObjectPanel.add(buttonPanel, BorderLayout.SOUTH);

		return noObjectPanel;
	}
		
    /**
     * Initialise the fileTree
     * @param start the root directory at the top of the tree
     */
	private void initFileTree(File start) {
		tree = new FileTree(new FileTreeNode(start));

		// Add mouse listeners
		tree.addMouseListener(new MouseAdapter() {
		    @Override
			public void mouseClicked(MouseEvent e) {
				// Only Left button click events are relevant: select file and possibly fire of autostart
				if (e.getButton() == MouseEvent.BUTTON1) {                	
					try {
						parent.getConfigPanel().clear();
						File selected = tree.getSelectedFile();
						if(selected != null) {
							select(selected);
							// Double-click fires off autoStart workflow
							if(selected.isFile() && e.getClickCount() >= 2) {
								doAutoStart();
							}                            
						}
					}
					catch(Exception ex) {
						logger.warn("warning: " + ExceptionUtils.getStackTrace(ex));
					}
				} 
			}            	
			@Override
			public void mousePressed(MouseEvent e) {
				processPopupEvents(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				processPopupEvents(e);            		
			}
			private void processPopupEvents(MouseEvent e) throws HeadlessException {
				// Only Right button click events are relevant: show popup menu with info link
				if (e.getButton() == MouseEvent.BUTTON3 && e.isPopupTrigger()) {
					File clicked = tree.getClickedFile(e);
					if (clicked.isFile()) {
						clickedFile = clicked;
						final JPopupMenu popUp = new JPopupMenu();                        
						info = new JMenuItem();
                        RBLanguages.set(info, "properties");

						info.addActionListener(FileExplorerPanel.this);
						popUp.add(info);
						popUp.show((Component)e.getSource(), e.getX(), e.getY());
					}
				}
			}
		});
	}

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == autoStart) {
            if(selectedFile != null) {
            	doAutoStart();
            }
        } 
        else if (e.getSource() == checkEnvironment) {
            parent.getConfigPanel().clear();
            parent.lock(RBLanguages.get("characterizing_file") + ": " + selectedFile + ", " + RBLanguages.get("log_please_wait") + "...");
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        java.util.List<Format> characteriserFormats = parent.model.characterise(selectedFile);
                        java.util.List<ObjectFormatType> allFormats = parent.model.getAllFileFormatsFromArchive();

                        // Display warning if either list is empty
                        if (characteriserFormats.isEmpty()) {
                            String warning = RBLanguages.get("could_not_determine_format") + ": " + selectedFile;                        		
                        	if (allFormats.isEmpty()) {
                        		// Both lists empty!
                        		warning = warning + " " + RBLanguages.get("error_and_not_download_all_formats");
                            	logger.error(warning);
                                parent.displayMessage(parent, warning, warning, JOptionPane.ERROR_MESSAGE);
                        	}
                        	else {
                        		// Only the characteriserFormats empty!
                        		warning = warning + ". " + RBLanguages.get("manually_select_file_fomat");
                            	logger.warn(warning);
                                parent.loadFormats(characteriserFormats, allFormats);
                                parent.displayMessage(parent, warning, warning, JOptionPane.WARNING_MESSAGE);
                        	}
                        } 
                        
                        else {
                        	if (allFormats.isEmpty()) {
                        		// Only list of all supported file formats empty!
                        		
                            	// FITS output is available, so no need to display error in GUI (?). 
                            	// However, do log a warning
                            	logger.warn("Managed to characterise file " + selectedFile.getAbsolutePath() + 
                            			", but could not download the list of all supported file formats from the software archive. " +
                            			"This probably indicates a problem with the (connection to) the software archive.");
                                parent.unlock(RBLanguages.get("number_of_formats") + ": " + characteriserFormats.size() + 
                                		". " + RBLanguages.get("error_donwload_all_formats"));                        		
                        	}
                        	else {
                        		// Neither list empty! 
                        		logger.debug("Successfully characterised file " + selectedFile.getAbsolutePath() + 
                        				" and downloaded list of all supported file formats.");
                                parent.unlock(RBLanguages.get("number_of_formats") + ": " + characteriserFormats.size() + 
                                		". " + RBLanguages.get("suggest_manually_select_file_fomat"));                      		
                        	}
                        	parent.loadFormats(characteriserFormats, allFormats);
                        }
                        
                    } catch (IOException ex) {
                    	String error = RBLanguages.get("error") + ": " + ex.getMessage();
                        parent.displayMessage(parent, error, error, JOptionPane.ERROR_MESSAGE);
                    }
                }
            })).start();
        }
        else if (e.getSource() == startWithoutObject) {

        	// Clear the ExplorerPanel
        	clearExplorerPanel();
        	enableExplorerPanel(false);    	

        	parent.lock("Downloading all supported dependencies. Please wait...");

        	(new Thread(new Runnable() {
        		@Override
        		public void run() {
        			// Prepare the configPanel for No Object
        			parent.getConfigPanel().loadNoObject();                        
        		}
        	})).start();
        
        }
        else if (e.getSource() == info) {
            parent.lock(RBLanguages.get("getting_meta_data") + ": " + clickedFile + ", " + RBLanguages.get("log_please_wait") + "...");
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<String, java.util.List<String>> techMetaData = parent.model.getTechMetadata(clickedFile);
                        Map<String, java.util.List<String>> descMetaData = parent.model.getFileInfo(clickedFile);

                        String[][] data = new String[techMetaData.size() + descMetaData.size()][];

                        int index = 0;

                        for(Map.Entry<String, java.util.List<String>> entry : techMetaData.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue().toString();
                            data[index++] = new String[]{key, value.substring(1, value.length()-1)};
                        }

                        for(Map.Entry<String, java.util.List<String>> entry : descMetaData.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue().toString();
                            data[index++] = new String[]{key, value.substring(1, value.length()-1)};
                        }

                        new InfoTableDialog(parent, clickedFile, data);
                        parent.unlock(RBLanguages.get("done"));
                    } catch (IOException ex) {
                    	String error = RBLanguages.get("error") + ": " + ex.getMessage();
                        parent.displayMessage(parent, error, error, JOptionPane.ERROR_MESSAGE);
                    }
                }
            })).start();
        }
    }

    /**
     * Start the automatic emulation process on the selected file.
     */
	private void doAutoStart() {
		parent.getConfigPanel().clear();
		checkEnvironment.setEnabled(false);

		parent.lock(RBLanguages.get("preparing_start_emulation") + ": " + selectedFile + ", " + RBLanguages.get("log_please_wait") + "...");
		(new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	String warning = "";
		        try {
		            java.util.List<Format> formats = parent.model.characterise(selectedFile);

		            if (formats.isEmpty()) {
		            	warning = RBLanguages.get("could_not_determine_format") + ": " + selectedFile;
		                parent.displayMessage(parent, warning, warning, JOptionPane.WARNING_MESSAGE);
		            }
		            else {
		                parent.loadFormats(formats);
		                parent.getConfigPanel().enableOptions(false);
		                Format frmt = formats.get(0);

		                // find dependencies
		                java.util.List<Pathway> paths = parent.model.getPathways(frmt);
		                parent.getConfigPanel().enableOptions(false);

		                if (paths.isEmpty()) {
		                	warning = RBLanguages.get("didnt_find_dependency") + ": " + frmt + ".";
		                    parent.displayMessage(parent, warning, warning, JOptionPane.WARNING_MESSAGE);
		                }
		                else {
		                    parent.getConfigPanel().loadPathways(paths);
		                    parent.getConfigPanel().enableOptions(false);

		                    // find emus
		                    Pathway path = paths.get(0);

		                    if (!parent.model.isPathwaySatisfiable(path)) {
		                    	warning = RBLanguages.get("not_satisfiable_path") + ": " + path;
			                    parent.displayMessage(parent, warning, warning, JOptionPane.WARNING_MESSAGE);
		                    }
		                    else {
		                        Map<EmulatorPackage, List<SoftwarePackage>> emuMap = parent.model.matchEmulatorWithSoftware(path);
		                        if (emuMap.isEmpty()) {
		                        	warning = RBLanguages.get("didnt_find_emulator") + ": " + paths;
				                    parent.displayMessage(parent, warning, warning, JOptionPane.WARNING_MESSAGE);
		                        }
		                        else {
		                            parent.getConfigPanel().loadEmus(emuMap);
		                            parent.getConfigPanel().enableOptions(false);

		                            // find software
		                            List<SoftwarePackage> swList = null;
		                            EmulatorPackage emu = null;

		                            for(Map.Entry<EmulatorPackage, List<SoftwarePackage>> entry : emuMap.entrySet()) {
		                                emu = entry.getKey();
		                                swList = entry.getValue();
		                                if(!swList.isEmpty()) {
		                                    break;
		                                }
		                            }

		                            if (swList == null || swList.isEmpty()) {
		                            	warning = RBLanguages.get("no_software_package") + ": " + path;
		    		                    parent.displayMessage(parent, warning, warning, JOptionPane.WARNING_MESSAGE);
		                            }
		                            else {
		                                parent.getConfigPanel().loadSoftware(swList);
		                                parent.getConfigPanel().enableOptions(false);

		                                // prepare config
		                                SoftwarePackage swPack = swList.get(0);
		                                int lastConfiguredID = parent.model.prepareConfiguration(selectedFile, emu, swPack, path);

		                                Map<String, List<Map<String, String>>> configMap = parent.model.getEmuConfig(lastConfiguredID);

		                                if (configMap.isEmpty()) {
		                                	warning = RBLanguages.get("no_configuration") + ": " + swPack.getDescription();
		        		                    parent.displayMessage(parent, warning, warning, JOptionPane.WARNING_MESSAGE);
		                                } else {
		                                    parent.getConfigPanel().loadConfiguration(configMap);
		                                    parent.getConfigPanel().enableOptions(false);

		                                    // run
		                                    parent.model.setEmuConfig(configMap, lastConfiguredID);
		                                    parent.model.runEmulationProcess(lastConfiguredID);
		                                    parent.unlock(RBLanguages.get("emulation_started"));

		                                    new InfoTableDialog(parent, selectedFile, emu, swPack);
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        } catch (IOException ex) {
		        	String error = RBLanguages.get("error") + ": " + ex.getMessage();
                    parent.displayMessage(parent, error, error, JOptionPane.ERROR_MESSAGE);
		        }
		        parent.getConfigPanel().enableOptions(true);
		    }
		})).start();
	}

    private void select(File file) {
        if(file != null) {
            boolean isFile = file.isFile();
            autoStart.setEnabled(isFile);
            checkEnvironment.setEnabled(isFile);
            selectedFile = isFile ? file : null;
        }
    }
    
    /**
     * Enable/Disable this panel
     * @param enabled true to enable this panel, false to disable
     */
    @Override
    public void setEnabled(boolean enabled) {
    	this.enableExplorerPanel(enabled);
    	this.enableNoObjectPanel(enabled);
    }
        
    /**
     * Enable/Disable the FileExplorer panel
     * @param enabled true to enable the FileExplorer panel, false to disable
     */
    private void enableExplorerPanel(boolean enabled) {
        this.autoStart.setEnabled(enabled);
        this.checkEnvironment.setEnabled(enabled);    	
    }
    
    /**
     * Clear the FileExplorer panel
     */
    private void clearExplorerPanel() {
    	tree.clearSelection();
    	selectedFile = null;
    	clickedFile = null;
    }
    
    /**
     * Enable/Disable the NoObject panel
     * @param enabled true to enable the NoObject panel, false to disable
     */
    private void enableNoObjectPanel(boolean enabled) {
    	this.startWithoutObject.setEnabled(enabled);
    }
        
}
