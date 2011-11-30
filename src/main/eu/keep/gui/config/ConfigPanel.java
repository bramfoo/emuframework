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

import eu.keep.characteriser.Format;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;
import eu.keep.gui.GUI;
import eu.keep.gui.common.InfoTableDialog;
import eu.keep.gui.explorer.FileExplorerPanel;
import eu.keep.softwarearchive.pathway.OperatingSystemType;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConfigPanel extends JPanel {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

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
                parent.lock("Finding dependencies for: " + frmt + ", please wait...");
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<Pathway> paths = parent.model.getPathways(frmt);
                            if (paths.isEmpty()) {
                                parent.unlock("Didn't find any suitable dependency for format: " + frmt + " with the current set of acceptable Languages.");
                            } else {
                                parent.unlock("Found " + paths.size() + " suitable dependencies");
                                ConfigPanel.this.loadPathways(paths);
                            }
                        } catch (IOException e1) {
                            parent.unlock("ERROR: " + e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                })).start();
            }
        });

        // find emulators button
        findEmus.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		final Pathway path = ((PathwayWrapper) pathwaysDropDown.getSelectedItem()).pathway;
        		parent.lock("Loading configuration for dependency: " + path + ", please wait...");
        		(new Thread(new Runnable() {
        			@Override
        			public void run() {
        				try {
        					if (!parent.model.isPathwaySatisfiable(path)) {
        						parent.unlock("Sorry, path is not satisfiable given the available emulators, " +
        								"software images and acceptable languages.");
        					} else {                            	
        						Map<EmulatorPackage, List<SoftwarePackage>> emuMap = parent.model.matchEmulatorWithSoftware(path);
        						parent.unlock("Found " + emuMap.size() + " suitable emulators");
        						ConfigPanel.this.loadEmus(emuMap);
        					}
        				} catch (IOException e1) {
        					parent.unlock("ERROR: " + e1.getMessage());
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
                final Pathway path = ((PathwayWrapper) pathwaysDropDown.getSelectedItem()).pathway;
                parent.lock("Finding software packages for " + emu.getEmulator().getName() + " " +
                        emu.getEmulator().getVersion() + " and path: " + path + ", please wait...");
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<SoftwarePackage> swList = swObj.softwareList;
                        if (swList.isEmpty()) {
                            parent.unlock("Sorry, could not find a software package for: " + new PathwayWrapper(path).toString());
                        } else {
                            parent.unlock("Found " + swList.size() + " software packages for " + new PathwayWrapper(path).toString());
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

                parent.lock("Loading configuration for: " + swPack.getDescription() + ", please wait...");

                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            lastConfiguredID = parent.model.prepareConfiguration(explorerPanel.selectedFile, emu, swPack,
                                    ((PathwayWrapper) pathwaysDropDown.getSelectedItem()).pathway);

                            Map<String, List<Map<String, String>>> configMap = parent.model.getEmuConfig(lastConfiguredID);

                            if (configMap.isEmpty()) {
                                parent.unlock("Sorry, could not find a configuration for: " + swPack.getDescription());
                            } else {
                                parent.unlock("Found " + configMap.size() + " configurations for: " + swPack.getDescription());
                                ConfigPanel.this.loadConfiguration(configMap);
                            }

                        } catch (IOException e1) {
                            parent.unlock("ERROR: " + e1.getMessage());
                        }
                    }
                })).start();
            }
        });

        configTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                configTxt.setText("");
                TreePath path = e.getPath();
                if (path == null) return;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node.isLeaf()) {
                    configTxt.setText(node.toString());
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

                parent.lock("Starting emulator with custom configuration, please wait...");

                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            parent.model.setEmuConfig(configModel.getMap(), lastConfiguredID);
                            parent.model.runEmulationProcess(lastConfiguredID);
                            parent.unlock("Emulation process started.");

                            new InfoTableDialog(
                                    ConfigPanel.this.parent,
                                    ConfigPanel.this.explorerPanel.selectedFile,
                                    emu,
                                    sw
                            );

                        } catch (Exception ex) {
                            parent.unlock("ERROR: " + ex.getMessage());
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
        emulatorsDropDown.removeAllItems();
        for (Map.Entry<EmulatorPackage, List<SoftwarePackage>> entry : emuMap.entrySet()) {
            emulatorsDropDown.addItem(new EmulatorPackageWrapper(entry));
        }
    }

    public void loadFormats(List<Format> formatList) {
        clear();
        explorerPanel.setEnabled(false);
        setEnableFormats(true);
        formatsDropDown.removeAllItems();
        for (Format f : formatList) {
            formatsDropDown.addItem(new FormatWrapper(f));
        }
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

    // drop down #1
    private void setEnableFormats(boolean enable) {
        // disable all components beneath this drop down box
        setEnablePathways(false);
        setEnableEmus(false);
        setEnableSoftware(false);
        setEnableConfig(false);

        // reset and enable or disable components
        formatsDropDown.removeAllItems();
        formatsDropDown.setEnabled(enable);
        findDependencies.setEnabled(enable);
    }

    // drop down #2
    private void setEnablePathways(boolean enable) {
        // disable all components beneath this drop down box
        setEnableEmus(false);
        setEnableSoftware(false);
        setEnableConfig(false);

        // reset and enable or disable components
        pathwaysDropDown.removeAllItems();
        pathwaysDropDown.setEnabled(enable);
        findEmus.setEnabled(enable);
    }

    // drop down #3
    private void setEnableEmus(boolean enable) {
        // disable all components beneath this drop down box
        setEnableSoftware(false);
        setEnableConfig(false);

        // reset and enable or disable components
        emulatorsDropDown.removeAllItems();
        emulatorsDropDown.setEnabled(enable);
        findSoftware.setEnabled(enable);
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
        saveConfig.setEnabled(enable);
    }

    private void setupGUI() {
        super.setLayout(new BorderLayout(2, 2));

        JPanel rightMainPanel = new JPanel(new BorderLayout(5, 5));

        explorerPanel = new FileExplorerPanel(parent);
        super.add(explorerPanel, BorderLayout.WEST);

        JPanel topRightPanel = new JPanel(new GridLayout(4, 1, 1, 1));

        // the 'format' components
        JPanel formatsPanel = new JPanel();
        formatsPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        JLabel formatsLabel = new JLabel("Found formats:");
        formatsDropDown = new JComboBox();
        findDependencies = new JButton("Find dependencies");
        formatsLabel.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 9, 25));
        formatsDropDown.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 41, 25));
        findDependencies.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 15, 25));
        formatsPanel.add(formatsLabel);
        formatsPanel.add(formatsDropDown);
        formatsPanel.add(findDependencies);
        topRightPanel.add(formatsPanel);

        // the 'pathway' components
        JPanel pathwaysPanel = new JPanel();
        pathwaysPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        JLabel pathwaysLabel = new JLabel("Dependencies:");
        pathwaysDropDown = new JComboBox();
        findEmus = new JButton("Find emulators");
        pathwaysLabel.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 9, 25));
        pathwaysDropDown.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 41, 25));
        findEmus.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 15, 25));
        pathwaysPanel.add(pathwaysLabel);
        pathwaysPanel.add(pathwaysDropDown);
        pathwaysPanel.add(findEmus);
        topRightPanel.add(pathwaysPanel);

        // the 'emulator' components
        JPanel emuPanel = new JPanel();
        emuPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        JLabel emuLabel = new JLabel("Emulators:");
        emulatorsDropDown = new JComboBox();
        findSoftware = new JButton("Find software");
        emuLabel.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 9, 25));
        emulatorsDropDown.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 41, 25));
        findSoftware.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 15, 25));
        emuPanel.add(emuLabel);
        emuPanel.add(emulatorsDropDown);
        emuPanel.add(findSoftware);
        topRightPanel.add(emuPanel);

        // the 'software' components
        JPanel swPanel = new JPanel();
        swPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        JLabel swLabel = new JLabel("Software:");
        softwareDropDown = new JComboBox();
        prepareConfig = new JButton("Prepare config");
        swLabel.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 9, 25));
        softwareDropDown.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 41, 25));
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
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        configTxt = new HighlightTextField();
        bottomPanel.add(configTxt, BorderLayout.CENTER);

        // buttons beneath the config tree
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        startConfig = new JButton("start");
        buttonsPanel.add(startConfig);
        saveConfig = new JButton("save");
        buttonsPanel.add(saveConfig);
        bottomPanel.add(buttonsPanel, BorderLayout.EAST);

        // reset all components
        clear();

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

            if(!softwarePackage.getOs().isEmpty()) {
                b.append(", ");
                for(OperatingSystemType os : softwarePackage.getOs()) {
                    b.append(os.getName()).append(" ").append(os.getVersion()).append(" ");
                }
            }

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