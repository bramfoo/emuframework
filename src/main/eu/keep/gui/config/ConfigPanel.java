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
package eu.keep.gui.config;

import antlr.RuleBlock;
import eu.keep.characteriser.Format;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;
import eu.keep.gui.GUI;
import eu.keep.gui.common.InfoTableDialog;
import eu.keep.gui.explorer.FileExplorerPanel;
import eu.keep.gui.util.RBLanguages;
import eu.keep.softwarearchive.pathway.OperatingSystemType;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigPanel extends JPanel {

    protected GUI parent;
    protected FileExplorerPanel explorerPanel;

    // drop down #1
    private JComboBox formatsDropDown;
    public JButton findDependencies;

    // drop down #2
    private JComboBox pathwaysDropDown;
    private JButton findEmus;

    // drop down #3
    private JComboBox emulatorsDropDown;
    private JButton findSoftware;

    // drop down #4
    private JComboBox softwareDropDown;
    private JButton prepareConfig;

    // config tree
    private DefaultMutableTreeNode configRoot;
    private ConfigTreeModel configModel;
    private JTree configTree;
    private HighlightTextField configTxt;
    private JButton startConfig;
    private JButton saveConfig;

    private Integer lastConfiguredID;

    public ConfigPanel(GUI gui) {
        parent = gui;
        setupGUI();
        initActionListeners();
    }

    public void clear() {
        configModel.reload();
        setEnableFormats(false);
        setEnablePathways(false);
        setEnableEmus(false);
        setEnableSoftware(false);
        setEnableConfig(false);
    }

    private void initActionListeners() {

        //TODO final int rememberedID;

        // find pathways button
        findDependencies.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Format frmt = ((FormatWrapper)formatsDropDown.getSelectedItem()).format;
                parent.lock(RBLanguages.get("find_dependencies_for") + ": " + frmt + ", " + RBLanguages.get("log_please_wait") + "...");
                (new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            List<Pathway> paths = parent.model.getPathways(frmt);
                            if (paths.isEmpty()) {
                            	String warning = RBLanguages.get("error_no_suitable_dependency") + ": " + frmt;
                                parent.displayMessage(parent, warning, warning, JOptionPane.WARNING_MESSAGE);
                            } else {
                                parent.unlock(RBLanguages.get("num_dependencies") + ": " + paths.size());
                                ConfigPanel.this.loadPathways(paths);
                            }
                        } catch (IOException e1) {
                        	String error = RBLanguages.get("error") + ": " + e1.getMessage();
                            parent.displayMessage(parent, error, error, JOptionPane.ERROR_MESSAGE);
                            e1.printStackTrace();
                        }

                    	ConfigPanel.this.findPathwaysForFormat(frmt);
                    }
                })).start();
            }
        });

        // find emulators button
        findEmus.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {

                PathwayWrapper selectedPathway = (PathwayWrapper) pathwaysDropDown.getSelectedItem();
        		final Pathway path = selectedPathway.pathway;
        		parent.lock(RBLanguages.get("load_dependencies") + ": " + path + ", " + RBLanguages.get("log_please_wait") + "...");

        		(new Thread(new Runnable() {
        			@Override
        			public void run() {
        				try {
        					String message = RBLanguages.get("path_not_satisfiable");

        					if (parent.model.isPathwaySatisfiable(path)) {

        						Map<EmulatorPackage, List<SoftwarePackage>> emuMap = parent.model.matchEmulatorWithSoftware(path);
        						ConfigPanel.this.loadEmus(emuMap);

        						if (!emuMap.isEmpty()) {
        							message = RBLanguages.get("num_emulators") + ": " + emuMap.size();
        							ConfigPanel.this.loadEmus(emuMap);
        						}
        					}
        					parent.unlock(message);
        				} catch (IOException e1) {
        					String error = RBLanguages.get("error") + ": " + e1.getMessage();
        					parent.displayMessage(parent, error, error, JOptionPane.ERROR_MESSAGE);
        				}
        			}
        		})).start();
        	}
        });

        // find software button
        findSoftware.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final EmulatorPackageWrapper swObj = (EmulatorPackageWrapper) emulatorsDropDown.getSelectedItem();
                final EmulatorPackage emu = swObj.emulatorPackage;
                final PathwayWrapper selectedPathway = (PathwayWrapper) pathwaysDropDown.getSelectedItem();
                final Pathway path = selectedPathway.pathway;

                parent.lock(RBLanguages.get("finding_software_for") + ": " + emu.getEmulator().getName() + " " +
                        emu.getEmulator().getVersion() + ", " + RBLanguages.get("path") +
                        ": " + path + ", " + RBLanguages.get("log_please_wait") + "...");

                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<SoftwarePackage> swList = swObj.softwareList;

                        if (swList.isEmpty()) {
                        	String warning = RBLanguages.get("no_software_for") + ": " + selectedPathway.toString();
                            parent.displayMessage(parent, warning, warning, JOptionPane.WARNING_MESSAGE);
                        }
                        else {
                            parent.unlock(RBLanguages.get("num_software_found") + ": " + selectedPathway.toString());
                            ConfigPanel.this.loadSoftware(swList);
                        }
                    }
                })).start();
            }
        });

        // prepare configuration button
        prepareConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final SoftwarePackage swPack = ((SoftwarePackageWrapper) softwareDropDown.getSelectedItem()).softwarePackage;
                final EmulatorPackageWrapper swObj = (EmulatorPackageWrapper) emulatorsDropDown.getSelectedItem();
                final EmulatorPackage emu = swObj.emulatorPackage;

                parent.lock(RBLanguages.get("load_configuration") + ": " + swPack.getDescription() + ", " + RBLanguages.get("log_please_wait") + "...");

                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            lastConfiguredID = parent.model.prepareConfiguration(explorerPanel.selectedFile, emu, swPack,
                                    ((PathwayWrapper) pathwaysDropDown.getSelectedItem()).pathway);

                            Map<String, List<Map<String, String>>> configMap = parent.model.getEmuConfig(lastConfiguredID);

                            if (configMap.isEmpty()) {
                            	String warning = RBLanguages.get("no_configuration_for") + ": " + swPack.getDescription();
                                parent.displayMessage(parent, warning, warning, JOptionPane.WARNING_MESSAGE);
                            } else {
                                parent.unlock(RBLanguages.get("num_configurations") + ": " + configMap.size());
                                ConfigPanel.this.loadConfiguration(configMap);
                            }

                        } catch (IOException e1) {
                        	String error = RBLanguages.get("error") + ": " + e1.getMessage();
                            parent.displayMessage(parent, error, error, JOptionPane.ERROR_MESSAGE);
                        }
                    }
                })).start();
            }
        });

        configTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                configTxt.setText("");
                saveConfig.setEnabled(false);
                TreePath path = e.getPath();
                if (path == null) return;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node.isLeaf()) {
                    configTxt.setText(node.toString());
                    saveConfig.setEnabled(true);
                }
            }
        });

        saveConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newValue = configTxt.getText();
                configModel.save(configTree.getSelectionPath(), newValue);
                configTxt.setText(newValue);
                configTree.grabFocus();
            }
        });

        startConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final SoftwarePackage sw = ((SoftwarePackageWrapper) softwareDropDown.getSelectedItem()).softwarePackage;
                final EmulatorPackageWrapper swObj = (EmulatorPackageWrapper) emulatorsDropDown.getSelectedItem();
                final EmulatorPackage emu = swObj.emulatorPackage;

                parent.lock(RBLanguages.get("start_custom_configuration") + ", " + RBLanguages.get("log_please_wait") + "...");

                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            parent.model.setEmuConfig(configModel.getMap(), lastConfiguredID);
                            parent.model.runEmulationProcess(lastConfiguredID);
                            parent.unlock(RBLanguages.get("emulation_started"));

                            new InfoTableDialog(
                                    ConfigPanel.this.parent,
                                    ConfigPanel.this.explorerPanel.selectedFile,
                                    emu,
                                    sw
                            );

                        } catch (Exception ex) {
                        	String error = RBLanguages.get("error") + ": " + ex.getMessage();
                            parent.displayMessage(parent, error, error, JOptionPane.ERROR_MESSAGE);
                        }
                    }
                })).start();
            }
        });
    }

    public void loadConfiguration(Map<String, List<Map<String, String>>> map) {
        setEnableConfig(true);
        configModel.load(map);
        configTree.expandRow(0);
    }

    public void loadEmus(Map<EmulatorPackage, List<SoftwarePackage>> emuMap) {
        setEnableEmus(true);
        for (Map.Entry<EmulatorPackage, List<SoftwarePackage>> entry : emuMap.entrySet()) {
            emulatorsDropDown.addItem(new EmulatorPackageWrapper(entry));
        }
    }

    public void loadFormats(List<Format> formatList) {
        clear();
//        explorerPanel.setEnabled(false);
        setEnableFormats(true);
        for (Format f : formatList) {
            formatsDropDown.addItem(new FormatWrapper(f));
        }
    }

    /**
     * Initialise the ConfigPanel for when a user wants to start an environment without Digital Object
     */
    public void loadNoObject() {
    	
    	// Clear the entire panel
    	clear();
    	
    	// Load the Formats dropdown with a dummy element 'no object'
    	List<Format> formats = new ArrayList<Format>();
    	Format noObjectFmt = new Format("no object", "-");
    	formats.add(noObjectFmt);
    	this.loadFormats(formats);
    	
        // Load all possible pathways in the Pathways dropdown
        findPathwaysForFormat(noObjectFmt);

    	// Disable the Formats dropdown and the 'find dependencies' button
        formatsDropDown.setEnabled(false);
        findDependencies.setEnabled(false);	
    }
    
    public void loadPathways(List<Pathway> paths) {
        setEnablePathways(true);
        pathwaysDropDown.removeAllItems();
        for (Pathway p : paths) {
            pathwaysDropDown.addItem(new PathwayWrapper(p));
        }
    }

    public void loadSoftware(List<SoftwarePackage> swList) {
        setEnableSoftware(true);
        softwareDropDown.removeAllItems();
        for (SoftwarePackage s : swList) {
            softwareDropDown.addItem(new SoftwarePackageWrapper(s));
        }
    }

    /**
     * Find pathways given an input file Format and load them into the Pathways dropdown
     * @param frmt the input file Format
     */
    private void findPathwaysForFormat(final Format frmt) {
		try {
		    List<Pathway> paths = parent.model.getPathways(frmt);
		    if (paths.isEmpty()) {
		    	String warning = "Didn't find any suitable dependency for format: " + frmt + " with the current set of acceptable Languages.";
		        parent.displayMessage(parent, warning, warning, JOptionPane.WARNING_MESSAGE);
		    } else {
		        parent.unlock("Found " + paths.size() + " suitable dependencies");
		        loadPathways(paths);
		    }
		} catch (IOException e1) {
		    parent.displayMessage(parent, "ERROR: " + e1.getMessage(), "ERROR: " + e1.getMessage(), JOptionPane.ERROR_MESSAGE);
		    e1.printStackTrace();
		}
	}

    // drop down #1
    private void setEnableFormats(boolean enable) {
        // disable all components beneath this drop down box
        setEnablePathways(false);

        // reset and enable or disable components
        formatsDropDown.removeAllItems();
        formatsDropDown.setEnabled(enable);
        findDependencies.setEnabled(enable);
    }

    // drop down #2
    private void setEnablePathways(boolean enable) {
        // disable all components beneath this drop down box
        setEnableEmus(false);

        // reset and enable or disable components
        pathwaysDropDown.removeAllItems();
        pathwaysDropDown.setEnabled(enable);
        findEmus.setEnabled(enable);
    }

    // drop down #3
    private void setEnableEmus(boolean enable) {
        // disable all components beneath this drop down box
        setEnableSoftware(false);

        // reset and enable or disable components
        emulatorsDropDown.removeAllItems();
        emulatorsDropDown.setEnabled(enable);
                
        if (enable) {
        	// If the selected pathway does not define any OS or application, there's no need to 
        	// select anything in the software dropdown, and the user can simply proceed to the 'Prepare Config'stage.
    		Pathway path = ((PathwayWrapper) pathwaysDropDown.getSelectedItem()).pathway;
    		if (path.getOperatingSystem().getId().equalsIgnoreCase("-1") && 
    			path.getApplication().getId().equalsIgnoreCase("-1")) {
    			bypassSoftwareSelection();
    		}
    		else {
    			findSoftware.setEnabled(true);
    		}
        }
        else {
            findSoftware.setEnabled(false);        	
        }
    }

    // drop down #4
    private void setEnableSoftware(boolean enable) {
        // disable all components beneath this drop down box
        setEnableConfig(false);

        // reset and enable or disable components
        softwareDropDown.removeAllItems();
        softwareDropDown.setEnabled(enable);
        prepareConfig.setEnabled(enable);
    }

    // config tree
    private void setEnableConfig(boolean enable) {
        // reset and enable or disable components
        if(!enable) {
            configTree.collapseRow(0);
        }
        configRoot.removeAllChildren();
        configTree.removeAll();
        configTxt.setText("");
        startConfig.setEnabled(enable);
        
        // Save Config button is only ever enabled by selecting leaf node in configTree
        saveConfig.setEnabled(false);

        if (enable) {
        	// If the selected pathway does not define any OS or application, there's no need to 
        	// select anything in the software dropdown, and the user can simply proceed to the 'Prepare Config'stage.
    		Pathway path = ((PathwayWrapper) pathwaysDropDown.getSelectedItem()).pathway;
    		if (path.getOperatingSystem().getId().equalsIgnoreCase("-1") && 
    			path.getApplication().getId().equalsIgnoreCase("-1")) {
    			bypassSoftwareSelection();
    		}
        }
    }

	private void bypassSoftwareSelection() {
		findSoftware.setEnabled(false);
		softwareDropDown.removeAllItems();
		// Add dummy software package to dropdown
		SoftwarePackage sp = new SoftwarePackage();
		sp.setId("0");
		sp.setDescription("N/A");
		sp.setFormat("0");  
		SoftwarePackageWrapper spw = new SoftwarePackageWrapper(sp);
		softwareDropDown.addItem(spw);
		softwareDropDown.setSelectedItem(spw);
		softwareDropDown.setEnabled(false);
		prepareConfig.setEnabled(true);
	}

    private void setupGUI() {
        super.setLayout(new BorderLayout(2, 2));

        // Empty panel to create small gap between tabs of JTabbedPane and top components in this GUI
        JPanel emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(GUI.WIDTH,2));
        super.add(emptyPanel, BorderLayout.NORTH);
        
        explorerPanel = new FileExplorerPanel(parent);
        super.add(explorerPanel, BorderLayout.WEST);

        JPanel topRightPanel = new JPanel(new GridLayout(4, 1, 1, 1));

        // the 'format' components
        JPanel formatsPanel = new JPanel();
        formatsPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        JLabel formatsLabel = new JLabel();
        RBLanguages.set(formatsLabel, "found_formats");

        formatsDropDown = new JComboBox();
        findDependencies = new JButton();
        RBLanguages.set(findDependencies, "find_dependencies");

        formatsLabel.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 14, 25));
        formatsDropDown.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 36, 25));
        findDependencies.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 15, 25));
        formatsPanel.add(formatsLabel);
        formatsPanel.add(formatsDropDown);
        formatsPanel.add(findDependencies);
        topRightPanel.add(formatsPanel);

        // the 'pathway' components
        JPanel pathwaysPanel = new JPanel();
        pathwaysPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        JLabel pathwaysLabel = new JLabel();
        RBLanguages.set(pathwaysLabel, "dependencies");

        pathwaysDropDown = new JComboBox();
        findEmus = new JButton();
        RBLanguages.set(findEmus, "find_emulators");

        pathwaysLabel.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 14, 25));
        pathwaysDropDown.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 36, 25));
        findEmus.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 15, 25));
        pathwaysPanel.add(pathwaysLabel);
        pathwaysPanel.add(pathwaysDropDown);
        pathwaysPanel.add(findEmus);
        topRightPanel.add(pathwaysPanel);

        // the 'emulator' components
        JPanel emuPanel = new JPanel();
        emuPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        JLabel emuLabel = new JLabel();
        RBLanguages.set(emuLabel, "emulators");

        emulatorsDropDown = new JComboBox();

        findSoftware = new JButton();
        RBLanguages.set(findSoftware, "find_software");

        emuLabel.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 14, 25));
        emulatorsDropDown.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 36, 25));
        findSoftware.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 15, 25));
        emuPanel.add(emuLabel);
        emuPanel.add(emulatorsDropDown);
        emuPanel.add(findSoftware);
        topRightPanel.add(emuPanel);

        // the 'software' components
        JPanel swPanel = new JPanel();
        swPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        JLabel swLabel = new JLabel();
        RBLanguages.set(swLabel, "software");

        softwareDropDown = new JComboBox();
        prepareConfig = new JButton();
        RBLanguages.set(prepareConfig, "prepare_config");

        swLabel.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 14, 25));
        softwareDropDown.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 36, 25));
        prepareConfig.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 15, 25));
        swPanel.add(swLabel);
        swPanel.add(softwareDropDown);
        swPanel.add(prepareConfig);
        topRightPanel.add(swPanel);

        // config tree
        configRoot = new DefaultMutableTreeNode("");
        configModel = new ConfigTreeModel(configRoot);
        configTree = new JTree(configModel);
        configTree.setCellRenderer(new ConfigTreeCellRenderer());
        configTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Textfield and buttons beneath the config tree
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        configTxt = new HighlightTextField();
        bottomPanel.add(configTxt, BorderLayout.CENTER);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        startConfig = new JButton();
        RBLanguages.set(startConfig, "start");

        buttonsPanel.add(startConfig);
        saveConfig = new JButton();
        RBLanguages.set(saveConfig, "save");

        buttonsPanel.add(saveConfig);
        bottomPanel.add(buttonsPanel, BorderLayout.EAST);

        // reset all components
        clear();
        
        JPanel rightMainPanel = new JPanel(new BorderLayout(5, 5));
        rightMainPanel.add(topRightPanel, BorderLayout.NORTH);
        rightMainPanel.add(new JScrollPane(configTree), BorderLayout.CENTER);
        rightMainPanel.add(bottomPanel, BorderLayout.SOUTH);
        super.add(rightMainPanel, BorderLayout.EAST);
    }

    public void enableOptions(boolean enabled) {
        formatsDropDown.setEnabled(enabled);
        findDependencies.setEnabled(enabled);
        pathwaysDropDown.setEnabled(enabled);
        findEmus.setEnabled(enabled);
        emulatorsDropDown.setEnabled(enabled);
        findSoftware.setEnabled(enabled);
        softwareDropDown.setEnabled(enabled);
        prepareConfig.setEnabled(enabled);
        configTree.setEnabled(enabled);
        configTxt.setEnabled(enabled);
        startConfig.setEnabled(enabled);
        saveConfig.setEnabled(enabled);
    }
    

	/**
     * Wrapper used to display EmulatorPackage in GUI
     */
    class EmulatorPackageWrapper {

        final EmulatorPackage emulatorPackage;
        final List<SoftwarePackage> softwareList;

        EmulatorPackageWrapper(Map.Entry<EmulatorPackage, List<SoftwarePackage>> entry) {
            emulatorPackage = entry.getKey();
            softwareList = entry.getValue();
        }

        @Override
        public String toString() {
            return emulatorPackage.getEmulator().getName() + " " + emulatorPackage.getEmulator().getVersion() + 
            		" (" + emulatorPackage.getEmulator().getExecutable().getType() + ")";
        }
    }

    
    /**
     * Wrapper used to display SoftwarePackage in GUI
     */
    class SoftwarePackageWrapper {

        final SoftwarePackage softwarePackage;

        SoftwarePackageWrapper(SoftwarePackage s) {
            softwarePackage = s;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder(softwarePackage.getDescription());

//            if(!softwarePackage.getOs().isEmpty()) {
//                b.append(", ");
//                for(OperatingSystemType os : softwarePackage.getOs()) {
//                    b.append(os.getName()).append(" ").append(os.getVersion()).append(" ");
//                }
//            }

            return b.toString();
        }
    }

    
    /**
     * Wrapper used to display Pathway in GUI
     */
    class PathwayWrapper {

        final Pathway pathway;

        PathwayWrapper(Pathway p) {
            pathway = p;
        }

        @Override
        public String toString() {
            return pathway.getApplication().getName() + " -> " +
                   pathway.getOperatingSystem().getName() + " -> " +
                   pathway.getHardwarePlatform().getName();
        }
    }

    
    /**
     * Wrapper used to display Format in GUI
     */
    class FormatWrapper {

        final Format format;

        FormatWrapper(Format f) {
            format = f;
        }

        @Override
        public String toString() {
            return format.getName() + " " + format.getReportingTools().toString();
        }
    }

}