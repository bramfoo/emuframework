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
package eu.keep.gui.common;

import eu.keep.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class InfoTableDialog extends JDialog {

    private GUI parent;

    public InfoTableDialog(GUI gui, File file, Object[] colNames, Object[][] data) {

        super(gui);

        if(data.length == 0) {
            colNames = new String[]{""};
            data = new String[][]{{""}, {"No additional information about the file(s) available."}, {""}};
        }

        parent = gui;

        parent.setEnabled(false);
        parent.getGlassPane().setVisible(true);

        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                InfoTableDialog.this.close();
            }
        });

        initGUI(colNames, data);

        int parentX = parent.getX();
        int parentY = parent.getY();
        int parentW = parent.getWidth();
        int parentH = parent.getHeight();

        super.setLocation(parentX + (parentW / 2) - (getWidth() / 2),
                parentY + (parentH / 2) - (getHeight() / 2));

        super.setTitle(file.getName());
        super.setResizable(false);
        super.setVisible(true);
    }

    private void close() {
        parent.getGlassPane().setVisible(false);
        parent.setEnabled(true);
        this.dispose();
    }

    private void initGUI(Object[] colNames, Object[][] data) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(500, 350));
        JTable table = new JTable(data, colNames);
        super.setSize(new Dimension(500, 400));
        super.setLayout(new BorderLayout(5, 5));
        panel.add(new JScrollPane(table), BorderLayout.NORTH);
        super.add(panel, BorderLayout.CENTER);
    }
}
