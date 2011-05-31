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

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ConfigTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final Icon NONE = new ImageIcon();
    private static final Icon BULLET = new ImageIcon("eu/keep/resources/icons/config.png");

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                        boolean sel, boolean expanded, boolean leaf,
                        int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);

        if (leaf) {
            super.setIcon(NONE);
        }
        else {
            super.setIcon(BULLET);
        }

        return this;
    }
}
