/*
 * $Revision: 803 $ $Date: 2011-05-31 00:27:06 +0100 (Tue, 31 May 2011) $
 * $Author: BLohman $
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
import eu.keep.softwarearchive.pathway.ApplicationType;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigPanel extends JPanel {

    protected GUI parent;

    protected FileExplorerPanel explorerPanel;

    // drop down #1
    private JComboBox formatsDropDown;
    private JButton findPathways;

    // drop down #2
    private JComboBox pathwaysDropDown;
    private JButton loadEmus;

    // drop down #3
    private JComboBox emulatorsDropDown;
    private JButton loadSoftware;

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

        // find pathways button
        findPathways.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Format frmt = (Format) formatsDropDown.getSelectedItem();
                parent.lock("Finding dependencies for: " + frmt + ", please wait...");
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<Pathway> paths = parent.model.getPathways(frmt);
                            if (paths.isEmpty()) {
                                parent.unlock("Didn't find any suitable dependency for format: " + frmt);
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

        // load emulators button
        loadEmus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Pathway path = ((PathwayWrapper) pathwaysDropDown.getSelectedItem()).pathway;
                parent.lock("Loading configuration for dependency: " + path + ", please wait...");
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!parent.model.isPathwaySatisfiable(path)) {
                                parent.unlock("Sorry, " + path + " is not satisfiable");
                            } else {
                                Map<EmulatorPackage, List<SoftwarePackage>> emuMap = parent.model.matchEmulatorWithSoftware(path);
                                if (emuMap.isEmpty()) {
                                    parent.unlock("Didn't find an emulator for dependency: " + new PathwayWrapper(path).toString());
                                } else {
                                    parent.unlock("Found " + emuMap.size() + " suitable emulators");
                                    ConfigPanel.this.loadEmus(emuMap);
                                }
                            }
                        } catch (IOException e1) {
                            parent.unlock("ERROR: " + e1.getMessage());
                        }
                    }
                })).start();
            }
        });

        // load software button
        loadSoftware.addActionListener(new ActionListener() {
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
                            Integer id = parent.model.prepareConfiguration(explorerPanel.selectedFile, emu, swPack,
                                    ((PathwayWrapper) pathwaysDropDown.getSelectedItem()).pathway);
                            Map<String, List<Map<String, String>>> configMap = parent.model.getEmuConfig(id);

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
                            Integer id = parent.model.prepareConfiguration(explorerPanel.selectedFile, emu, sw,
                                    ((PathwayWrapper) pathwaysDropDown.getSelectedItem()).pathway);
                            parent.model.setEmuConfig(configModel.getMap(), id);
                            parent.model.runEmulationProcess(id);
                            parent.unlock("Emulation process started.");

                            // TODO refactor: quick-and-dirty hack ahead!
                            List<List<String>> dataList = new ArrayList <List<String>>();

                            List<String> row = new ArrayList<String>();

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

                            for(ApplicationType app : sw.getApp()) {

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
                                row.add(app.getLanguage());
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
                            }

                            // load meta data
                            String[][] data = new String[dataList.size()][];
                            int index = 0;
                            for(List<String> rw: dataList) {
                                data[index] = rw.toArray(new String[rw.size()]);
                                index++;
                            }

                            new InfoTableDialog(ConfigPanel.this.parent, ConfigPanel.this.explorerPanel.selectedFile,
                                    new String[]{"key","value"}, data);
                        } catch (Exception ex) {
                            parent.unlock("ERROR: " + ex.getMessage());
                        }
                    }
                })).start();
            }
        });
    }

    private void loadConfiguration(Map<String, List<Map<String, String>>> map) {
        setEnableConfig(true);
        configModel.load(map);
        configTree.expandRow(0);
    }

    private void loadEmus(Map<EmulatorPackage, List<SoftwarePackage>> emuMap) {
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
            formatsDropDown.addItem(f);
        }
    }

    public void loadPathways(List<Pathway> paths) {
        setEnablePathways(true);
        pathwaysDropDown.removeAllItems();
        for (Pathway p : paths) {
            pathwaysDropDown.addItem(new PathwayWrapper(p));
        }
    }

    private void loadSoftware(List<SoftwarePackage> swList) {
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
        findPathways.setEnabled(enable);
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
        loadEmus.setEnabled(enable);
    }

    // drop down #3
    private void setEnableEmus(boolean enable) {
        // disable all components beneath this drop down box
        setEnableSoftware(false);
        setEnableConfig(false);

        // reset and enable or disable components
        emulatorsDropDown.removeAllItems();
        emulatorsDropDown.setEnabled(enable);
        loadSoftware.setEnabled(enable);
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
        findPathways = new JButton("Find dependencies");
        formatsLabel.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 2, 25));
        formatsDropDown.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 6, 25));
        findPathways.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 3, 25));
        formatsPanel.add(formatsLabel);
        formatsPanel.add(formatsDropDown);
        formatsPanel.add(findPathways);
        topRightPanel.add(formatsPanel);

        // the 'pathway' components
        JPanel pathwaysPanel = new JPanel();
        pathwaysPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        JLabel pathwaysLabel = new JLabel("Dependencies:");
        pathwaysDropDown = new JComboBox();
        loadEmus = new JButton("Load emulators");
        pathwaysLabel.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 2, 25));
        pathwaysDropDown.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 6, 25));
        loadEmus.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 3, 25));
        pathwaysPanel.add(pathwaysLabel);
        pathwaysPanel.add(pathwaysDropDown);
        pathwaysPanel.add(loadEmus);
        topRightPanel.add(pathwaysPanel);

        // the 'emulator' components
        JPanel emuPanel = new JPanel();
        emuPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        JLabel emuLabel = new JLabel("Emulators:");
        emulatorsDropDown = new JComboBox();
        loadSoftware = new JButton("Load software");
        emuLabel.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 2, 25));
        emulatorsDropDown.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 6, 25));
        loadSoftware.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 3, 25));
        emuPanel.add(emuLabel);
        emuPanel.add(emulatorsDropDown);
        emuPanel.add(loadSoftware);
        topRightPanel.add(emuPanel);

        // the 'software' components
        JPanel swPanel = new JPanel();
        swPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        JLabel swLabel = new JLabel("Software:");
        softwareDropDown = new JComboBox();
        prepareConfig = new JButton("Prepare config");
        swLabel.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 2, 25));
        softwareDropDown.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 6, 25));
        prepareConfig.setPreferredSize(new Dimension(GUI.WIDTH_UNIT * 3, 25));
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

    class EmulatorPackageWrapper {

        final EmulatorPackage emulatorPackage;
        final List<SoftwarePackage> softwareList;

        EmulatorPackageWrapper(Map.Entry<EmulatorPackage, List<SoftwarePackage>> entry) {
            emulatorPackage = entry.getKey();
            softwareList = entry.getValue();
        }

        @Override
        public String toString() {
            return emulatorPackage.getEmulator().getName() + " " + emulatorPackage.getEmulator().getVersion() + " (" + emulatorPackage.getEmulator().getExecutable().getType() + ")";
        }
    }

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
}