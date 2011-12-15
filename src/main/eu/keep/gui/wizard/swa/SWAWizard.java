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
package eu.keep.gui.wizard.swa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SWAWizard extends JFrame {

    public static final Color LIGHT_RED = new Color(255, 212, 222);

    protected static final String MANDATORY_MESSAGE = "<mandatory field>";

    protected Step1_App step1;
    protected Step2_OpSys step2;
    protected Step3_HardwareAndFormat step3;
    protected Step4_ZippedImage step4;
    protected Step5_FileFormat step5;
    protected ConfirmPanel confirm;

    private JLabel logLabel;

    public SWAWizard() {
        super("SWA wizard");

        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        super.setResizable(false);
        super.setSize(500, 600);

        step1 = new Step1_App(this);
        step2 = new Step2_OpSys(this);
        step3 = new Step3_HardwareAndFormat(this);
        step4 = new Step4_ZippedImage(this);
        step5 = new Step5_FileFormat(this);
        confirm = new ConfirmPanel(this);

        logLabel = new JLabel("1/5, select an application");

        super.setLayout(new BorderLayout(5, 5));

        super.add(step1, BorderLayout.CENTER);
        super.add(logLabel, BorderLayout.SOUTH);

        super.setVisible(true);
    }

    public static JTextField createTxtField(Dimension d, boolean mandatory) {
        final JTextField txt = new JTextField();
        if(mandatory) {
            txt.setBackground(SWAWizard.LIGHT_RED);
            txt.setText(MANDATORY_MESSAGE);

            txt.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if(txt.getText().trim().equals(MANDATORY_MESSAGE)) {
                        txt.setText("");
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if(txt.getText().trim().isEmpty()) {
                        txt.setText(MANDATORY_MESSAGE);
                    }
                }
            });

            txt.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String s = txt.getText().trim();
                    if(s.isEmpty() || s.equals(MANDATORY_MESSAGE)) {
                        txt.setBackground(SWAWizard.LIGHT_RED);
                    }
                    else {
                        txt.setBackground(Color.WHITE);
                    }
                }
            });
        }
        txt.setPreferredSize(d);
        return txt;
    }

    protected synchronized void log(String message) {
        logLabel.setText(message);
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SWAWizard();
            }
        });
    }
}
