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

import eu.keep.gui.util.DBUtil;
import eu.keep.gui.wizard.ea.model.Emulator;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class EAWizardRemove extends JFrame {

    private static Logger logger = Logger.getLogger(DBUtil.class.getName());

    public EAWizardRemove() {
        super("Emulator Archive wizard");

        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        super.setResizable(false);
        super.setSize(500, 100);

        initGUI();

        super.setVisible(true);
    }

    private void initGUI() {
        Vector<Vector<String>> emuData = DBUtil.query(DBUtil.DB.EA,
                "SELECT emulator_id, name, version, exec_type, exec_name, description, language_id, package_name, package_type, package_version, user_instructions " +
                "FROM emulatorarchive.emulators"
        );
        Vector<Emulator> emuVec = new Vector<Emulator>();
        for(Vector<String> row : emuData) {
            emuVec.add(new Emulator(null, row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5),
                    row.get(6), row.get(7), row.get(8), row.get(9), null, row.get(10)));
        }

        super.setLayout(new BorderLayout(5, 5));
        super.add(new JLabel(" "), BorderLayout.NORTH);
        super.add(new JLabel(" "), BorderLayout.WEST);
        super.add(new JLabel(" "), BorderLayout.EAST);
        super.add(new JLabel(" "), BorderLayout.SOUTH);

        final JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        final JComboBox emuCombo = new JComboBox(emuVec);
        final JButton delete = new JButton("delete");

        mainPanel.add(emuCombo, BorderLayout.CENTER);
        mainPanel.add(delete, BorderLayout.EAST);

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final Emulator emu = (Emulator)emuCombo.getSelectedItem();

                Object[] options = {"Yes", "Cancel"};
                int returnValue = JOptionPane.showOptionDialog(EAWizardRemove.this,
                        "Are you sure you want to remove " + emu + "?\n This operation cannot be undone!",
                        "Delete Emulator",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

                if (returnValue == JOptionPane.YES_OPTION) {
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DBUtil.update(DBUtil.DB.EA, "DELETE FROM emulatorarchive.emus_imageformats WHERE emulator_id=?", emu.emulator_id);
                                DBUtil.update(DBUtil.DB.EA, "DELETE FROM emulatorarchive.emus_hardware WHERE emulator_id=?", emu.emulator_id);
                                DBUtil.update(DBUtil.DB.EA, "DELETE FROM emulatorarchive.emulators WHERE emulator_id=?", emu.emulator_id);
                                DBUtil.update(DBUtil.DB.CEF, "DELETE FROM engine.emulator_whitelist WHERE emulator_id=?", emu.emulator_id);
                                JOptionPane.showMessageDialog(EAWizardRemove.this,
                                        "Successfully deleted: " + emuCombo.getSelectedItem(),
                                        "Error",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                            catch(Exception ex) {
                                JOptionPane.showMessageDialog(EAWizardRemove.this,
                                        "Could not remove " + emuCombo.getSelectedItem() + ": " + ex.getMessage(),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    })).start();

                    EAWizardRemove.this.dispose();
                }
            }
        });

        super.add(mainPanel, BorderLayout.CENTER);
    }
}
