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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class HighlightTextField extends JTextField implements ActionListener, KeyListener {

    private static final Color HL_COLOR = Color.YELLOW;

    public HighlightTextField() {
        super();
        super.addActionListener(this);
        super.addKeyListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.setBackground(Color.WHITE);
    }

    @Override
    public void setText(String t) {
        super.setBackground(Color.WHITE);
        super.setText(t);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyCode() != KeyEvent.VK_ENTER) {
            super.setBackground(HL_COLOR);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() != KeyEvent.VK_ENTER) {
            super.setBackground(HL_COLOR);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() != KeyEvent.VK_ENTER) {
            super.setBackground(HL_COLOR);
        }
    }
}
