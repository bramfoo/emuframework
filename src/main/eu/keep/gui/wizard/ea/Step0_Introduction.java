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
package eu.keep.gui.wizard.ea;

import eu.keep.gui.util.RBLanguages;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Step0_Introduction extends JPanel {

    public Step0_Introduction(final EAWizardAdd parent) {

        super.setLayout(new BorderLayout(5, 5));

        final Dimension d = new Dimension(320, 25);

        final String explanation = RBLanguages.get("ea_explanation");

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final JButton next = new JButton("<html>&rarr;</html>");

        center.add(new JLabel(explanation));
        buttons.add(next);

        super.add(center);
        super.add(buttons, BorderLayout.SOUTH);

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step0);
                parent.add(parent.step1, BorderLayout.CENTER);
                parent.log("1/2, " + RBLanguages.get("select_emulator"));
                parent.validate();
                parent.repaint();
            }
        });
    }
}
