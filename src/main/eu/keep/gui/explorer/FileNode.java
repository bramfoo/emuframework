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

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileNode extends DefaultMutableTreeNode {

    private static final Comparator<File> FILE_ABC_COMPARATOR = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            // Directories come before files.
            if(f1.isDirectory() && !f2.isDirectory()) {
                return -1;
            }
            if(!f1.isDirectory() && f2.isDirectory()) {
                return 1;
            }

            // Both are the same: either directories, or files.
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    };

    public FileNode(File f) {
        super(f);
    }

    public void discover(int layersToGo) {
        if (layersToGo == 0 || this.getFile().isFile()) return;

        File[] contents = this.getFile().listFiles();

        if (contents == null) return;

        Arrays.sort(contents, FILE_ABC_COMPARATOR);

        super.removeAllChildren();

        for (File f : contents) {
            if (f.isHidden()) continue;
            FileNode child = new FileNode(f);
            super.add(child);
            child.discover(layersToGo - 1);
        }
    }

    public File getFile() {
        return (File) super.getUserObject();
    }

    @Override
    public String toString() {
        String name = getFile().getName();
        if (name.trim().isEmpty()) {
            name = getFile().getAbsolutePath();
        }
        return name;
    }
}