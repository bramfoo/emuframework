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
package eu.keep.gui.explorer;

import eu.keep.characteriser.Format;
import eu.keep.gui.GUI;
import eu.keep.gui.common.InfoTableDialog;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileExplorerPanel extends JPanel implements ActionListener {

    public File selectedFile = null;
    private GUI parent;
    private JButton autoStart;
    private JButton characterize;
    private JButton info;

    public FileExplorerPanel(GUI p) {
        parent = p;
        initGUI();
    }

    private void initGUI() {

        super.setLayout(new BorderLayout(5, 5));

        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        autoStart = new JButton("auto start");
        characterize = new JButton("characterize");
        info = new JButton("info");

        autoStart.setEnabled(false);
        characterize.setEnabled(false);
        info.setEnabled(false);

        autoStart.addActionListener(this);
        characterize.addActionListener(this);
        info.addActionListener(this);

        buttonPanel.add(autoStart);
        buttonPanel.add(characterize);
        buttonPanel.add(info);

        FileNode dummyRoot = new FileNode(new File(""));
        File[] roots = File.listRoots();

        for (File f : roots) {
            FileNode node = new FileNode(f);
            node.discover(2);
            dummyRoot.add(node);
        }

        JTree tree = new JTree(dummyRoot);
        tree.setCellRenderer(new ExplorerTreeCellRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setRootVisible(false);

        // attach listeners for tree-value changes and tree-expanding-actions
        tree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent e) {
                TreePath path = e.getPath();
                if (path == null) return;
                FileNode last = (FileNode) path.getLastPathComponent();
                for (Enumeration children = last.children(); children.hasMoreElements();) {
                    FileNode child = (FileNode) children.nextElement();
                    child.removeAllChildren();
                    child.discover(1);
                }
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                /* ignored */
            }
        });

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                parent.clear();
                TreePath path = e.getPath();
                if (path == null) return;
                FileNode last = (FileNode) path.getLastPathComponent();
                last.discover(1);
                FileExplorerPanel.this.select(last.getFile());
            }
        });

        super.setPreferredSize(new Dimension((GUI.WIDTH_UNIT * 6) - 30, GUI.HEIGHT));
        super.add(new JScrollPane(tree), BorderLayout.CENTER);
        super.add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == autoStart) {
            parent.clear();
            parent.lock("Preparing to start emulation process for: " + selectedFile + ", please wait...");
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean success = parent.model.start(selectedFile);
                        if(success) {
                            // TODO descriptive meta data in a new tab
                        }
                        parent.unlock("Done.");
                    } catch (IOException ex) {
                        parent.unlock("ERROR: " + ex.getMessage());
                    }
                }
            })).start();
        }

        if (e.getSource() == characterize) {
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
                            parent.loadFormats(formats);
                            parent.unlock("Done, found " + formats.size() + " possible format(s)");
                        }
                    } catch (IOException ex) {
                        parent.unlock("ERROR :: " + ex.getMessage());
                    }
                }
            })).start();
        }

        if (e.getSource() == info) {
            parent.lock("Getting meta data from file: " + selectedFile + ", please wait...");
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<String, java.util.List<String>> techMetaData = parent.model.getTechMetadata(selectedFile);
                        Map<String, java.util.List<String>> descMetaData = parent.model.getFileInfo(selectedFile);

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

                        new InfoTableDialog(parent, selectedFile, new String[]{"key", "value"}, data);
                        parent.unlock("Done.");
                    } catch (IOException ex) {
                         parent.unlock("ERROR :: " + ex.getMessage());
                    }
                }
            })).start();
        }
    }

    void select(File file) {
        boolean isFile = file.isFile();
        autoStart.setEnabled(isFile);
        characterize.setEnabled(isFile);
        info.setEnabled(isFile);
        selectedFile = isFile ? file : null;
    }

    public void setEnabled(boolean enabled) {
        this.autoStart.setEnabled(enabled);
    }
}
