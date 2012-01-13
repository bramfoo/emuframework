/*
 * $Revision: 114 $ $Date: 2011-12-01 12:25:35 +0100 (Thu, 01 Dec 2011) $
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
package eu.keep.gui.explorer;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileTree extends JTree {

    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel fileTreeModel;
    protected FileSystemView fsv;
    protected final boolean onWindows;

    public FileTree(FileTreeNode root) {
        super(new DefaultTreeModel(new DefaultMutableTreeNode("root")));
        fileTreeModel = (DefaultTreeModel)treeModel;

        String os = System.getProperty("os.name").toLowerCase();
        onWindows = os.contains("win");

        setRoot(root);

        if (onWindows) {
            fsv = FileSystemView.getFileSystemView();
        }
        setCellRenderer(new FileTreeCellRenderer());
        setEditable(false);

        initListeners();
    }

    public File getSelectedFile() {
    	DefaultMutableTreeNode lastSelectedNode = (DefaultMutableTreeNode)super.getLastSelectedPathComponent();    	
    	if (lastSelectedNode == null) {
    		return null;
    	} else {
    		FileTreeNode node = (FileTreeNode)lastSelectedNode.getUserObject();
    		return node == null ? null : node.file;
    	}
    }

    public File getClickedFile(MouseEvent e) {
    	DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode)super.getPathForLocation(e.getX(), e.getY()).getLastPathComponent();
    	if (clickedNode == null) {
    		return null;
    	} else {
    		FileTreeNode node = (FileTreeNode)clickedNode.getUserObject();
    		return node == null ? null : node.file;
    	}
    }
    
    private void initListeners() {
        addTreeExpansionListener(new TreeExpansionListener() {
            public void treeCollapsed(TreeExpansionEvent event) {
                /* ignore */
            }
            public void treeExpanded(TreeExpansionEvent event) {
                TreePath path = event.getPath();
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                treeNode.removeAllChildren();
                populateSubTree(treeNode);
                fileTreeModel.nodeStructureChanged(treeNode);
            }
        });

        addMouseListener(new FileTreeListener(this));
    }

    private void populateSubTree(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();

        if (userObject instanceof FileTreeNode) {
            FileTreeNode fileTreeNode = (FileTreeNode)userObject;
            File[] files = fileTreeNode.file.listFiles();

            if(files == null) {
                // insufficient rights to read in this directory!
                node.removeAllChildren();
                return;
            }

            Arrays.sort(files, new Comparator<File>() {
                public int compare(File a, File b) {
                    boolean aDir = a.isDirectory();
                    boolean bDir = b.isDirectory();

                    if (aDir == bDir) {
                        return a.getName().compareToIgnoreCase(b.getName());
                    }
                    else if (aDir && !bDir) {
                        // dirs come before files
                        return -1;
                    }
                    else {
                        // 'a' is a file, 'b' a dir (file comes later)
                        return 1;
                    }
                }
            });

            for (File file : files) {
                if (file.isHidden()) continue;

                FileTreeNode subFile = new FileTreeNode(file);
                DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(subFile);

                if (file.isDirectory()) {
                    subNode.add(new DefaultMutableTreeNode(""));
                }
                node.add(subNode);
            }
        }
    }

    public void setRoot(FileTreeNode root) {
        rootNode = new DefaultMutableTreeNode(root);
        populateSubTree(rootNode);
        fileTreeModel.setRoot(rootNode);
    }

    private class FileTreeCellRenderer extends DefaultTreeCellRenderer {

        // used get icons for OS-es other than Windows
        private JFileChooser fileChooser;

        public FileTreeCellRenderer() {
            fileChooser = new JFileChooser();
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            Object userObject = ((DefaultMutableTreeNode)value).getUserObject();

            if (userObject instanceof FileTreeNode) {
                FileTreeNode fileTreeNode = (FileTreeNode)userObject;

                if (onWindows) {
                    try {
                        setIcon(fsv.getSystemIcon(fileTreeNode.file));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        setIcon(fileChooser.getIcon(fileTreeNode.file));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return this;
        }
    }
}
