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
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.swing.*;

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
    public File clickedFile;
    private GUI parent;
    private JButton autoStart;
    private JButton checkEnvironment;
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
        autoStart = new JButton("auto start");
        checkEnvironment = new JButton("check environment");

        autoStart.setEnabled(false);
        checkEnvironment.setEnabled(false);

        autoStart.addActionListener(this);
        checkEnvironment.addActionListener(this);

        buttonPanel.add(autoStart);
        buttonPanel.add(checkEnvironment);

        final File[] roots = File.listRoots();
        final JComboBox rootsCombo = new JComboBox(roots);

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

        
        super.setPreferredSize(new Dimension((GUI.WIDTH_UNIT * 30) - 30, GUI.HEIGHT));
        super.add(rootsCombo, BorderLayout.NORTH);
        super.add(new JScrollPane(tree), BorderLayout.CENTER);
        super.add(buttonPanel, BorderLayout.SOUTH);
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
						info = new JMenuItem("properties");
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

        if (e.getSource() == checkEnvironment) {
            parent.clear();
            parent.lock("Characterizing file: " + selectedFile + ", please wait...");
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        java.util.List<Format> formats = parent.model.characterise(selectedFile);
                        if (formats.isEmpty()) {
                            parent.unlock("Could not determine the format of file: " + selectedFile);
                        } else {
                            parent.unlock("Done, found " + formats.size() + " possible format(s)");
                            parent.loadFormats(formats);
                        }
                    } catch (IOException ex) {
                        parent.unlock("ERROR :: " + ex.getMessage());
                    }
                }
            })).start();
        }

        if (e.getSource() == info) {
            parent.lock("Getting meta data from file: " + clickedFile + ", please wait...");
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
                        parent.unlock("Done.");
                    } catch (IOException ex) {
                        parent.unlock("ERROR : " + ex.getMessage());
                    }
                }
            })).start();
        }
    }

    /**
     * Start the automatic emulation process on the selected file.
     */
	private void doAutoStart() {
		parent.clear();
		checkEnvironment.setEnabled(false);

		parent.lock("Preparing to start emulation process for: " + selectedFile + ", please wait...");
		(new Thread(new Runnable() {
		    @Override
		    public void run() {
		        try {
		            java.util.List<Format> formats = parent.model.characterise(selectedFile);

		            if (formats.isEmpty()) {
		                parent.unlock("Could not determine the format of file: " + selectedFile);
		            }
		            else {
		                parent.loadFormats(formats);
		                parent.getConfigPanel().enableOptions(false);
		                Format frmt = formats.get(0);

		                // find dependencies
		                java.util.List<Pathway> paths = parent.model.getPathways(frmt);
		                parent.getConfigPanel().enableOptions(false);

		                if (paths.isEmpty()) {
		                    parent.unlock("Didn't find any suitable dependency for format: " + frmt + " with the current set of acceptable Languages.");
		                }
		                else {
		                    parent.getConfigPanel().loadPathways(paths);
		                    parent.getConfigPanel().enableOptions(false);

		                    // find emus
		                    Pathway path = paths.get(0);

		                    if (!parent.model.isPathwaySatisfiable(path)) {
		                        parent.unlock("Sorry, " + path + " is not satisfiable");
		                    }
		                    else {
		                        Map<EmulatorPackage, List<SoftwarePackage>> emuMap = parent.model.matchEmulatorWithSoftware(path);
		                        if (emuMap.isEmpty()) {
		                            parent.unlock("Didn't find an emulator for dependency: " + paths);
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
		                                parent.unlock("Sorry, could not find a software package for: " + path);
		                            }
		                            else {
		                                parent.getConfigPanel().loadSoftware(swList);
		                                parent.getConfigPanel().enableOptions(false);

		                                // prepare config
		                                SoftwarePackage swPack = swList.get(0);
		                                int lastConfiguredID = parent.model.prepareConfiguration(selectedFile, emu, swPack, path);

		                                Map<String, List<Map<String, String>>> configMap = parent.model.getEmuConfig(lastConfiguredID);

		                                if (configMap.isEmpty()) {
		                                    parent.unlock("Sorry, could not find a configuration for: " + swPack.getDescription());
		                                } else {
		                                    parent.getConfigPanel().loadConfiguration(configMap);
		                                    parent.getConfigPanel().enableOptions(false);

		                                    // run
		                                    parent.model.setEmuConfig(configMap, lastConfiguredID);
		                                    parent.model.runEmulationProcess(lastConfiguredID);
		                                    parent.unlock("Emulation process started.");

		                                    new InfoTableDialog(parent, selectedFile, emu, swPack);
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        } catch (IOException ex) {
		            parent.unlock("ERROR : " + ex.getMessage());
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
    
    public void setEnabled(boolean enabled) {
        this.autoStart.setEnabled(enabled);
    }
}
