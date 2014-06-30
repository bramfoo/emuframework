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

import eu.keep.gui.util.DBUtil;
import eu.keep.gui.util.RBLanguages;
import eu.keep.gui.wizard.swa.model.Img;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class SWAWizardRemove extends JFrame {

    private static Logger logger = Logger.getLogger(DBUtil.class.getName());

    public SWAWizardRemove() {
        super("Software Archive wizard");

        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        super.setResizable(false);
        super.setSize(500, 100);

        initGUI();

        super.setVisible(true);
    }

    private void initGUI() {
        Vector<Vector<String>> swData = DBUtil.query(DBUtil.DB.SWA,
                "SELECT image_id,description, imageformat_id, platform_id " +
                "FROM softwarearchive.images"
        );
        Vector<Img> swVec = new Vector<Img>();
        for(Vector<String> row : swData) {
            swVec.add(new Img(row.get(0), row.get(1), row.get(2), row.get(3)));
        }

        super.setLayout(new BorderLayout(5, 5));
        super.add(new JLabel(" "), BorderLayout.NORTH);
        super.add(new JLabel(" "), BorderLayout.WEST);
        super.add(new JLabel(" "), BorderLayout.EAST);
        super.add(new JLabel(" "), BorderLayout.SOUTH);

        final JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        final JComboBox swCombo = new JComboBox(swVec);
        final JButton delete = new JButton(RBLanguages.get("delete"));

        mainPanel.add(swCombo, BorderLayout.CENTER);
        mainPanel.add(delete, BorderLayout.EAST);

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final Img img = (Img) swCombo.getSelectedItem();

                Object[] options = {RBLanguages.get("yes"), RBLanguages.get("cancel")};
                int returnValue = JOptionPane.showOptionDialog(SWAWizardRemove.this,
                        RBLanguages.get("sure_remove") + ": " + img + "?\n" + RBLanguages.get("warn_no_undo") + "!",
                        RBLanguages.get("delete") + " " + RBLanguages.get("software"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

                if (returnValue == JOptionPane.YES_OPTION) {
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DBUtil.update(DBUtil.DB.SWA, "DELETE FROM softwarearchive.opsys_images WHERE image_id=?", img.image_id);
                                DBUtil.update(DBUtil.DB.SWA, "DELETE FROM softwarearchive.apps_images WHERE image_id=?", img.image_id);
                                DBUtil.update(DBUtil.DB.SWA, "DELETE FROM softwarearchive.imageblobs WHERE image_id=?", img.image_id);
                                DBUtil.update(DBUtil.DB.SWA, "DELETE FROM softwarearchive.images WHERE image_id=?", img.image_id);

                                JOptionPane.showMessageDialog(SWAWizardRemove.this,
                                        RBLanguages.get("successfully_deleted") + ": " + swCombo.getSelectedItem(),
                                        "",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(SWAWizardRemove.this,
                                        RBLanguages.get("could_not_remove") + ": " + swCombo.getSelectedItem() + ": " + ex.getMessage(),
                                        "",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    })).start();

                    SWAWizardRemove.this.dispose();
                }
            }
        });

        super.add(mainPanel, BorderLayout.CENTER);
    }
}
