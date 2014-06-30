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
import eu.keep.gui.wizard.swa.model.ImageBlob;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;

public class Step4_ZippedImage extends JPanel {

    protected ImageBlob imageBlob = null;

    Step4_ZippedImage(final SWAWizardAdd parent) {
        super.setLayout(new BorderLayout(5, 5));

        final Dimension d = new Dimension(320, 25);

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final String explanation = RBLanguages.get("swa_zip_explanation");

        final Vector<Vector<String>> blobIDs = DBUtil.query(DBUtil.DB.SWA, "SELECT image_id FROM softwarearchive.imageblobs");
        final String blobID = DBUtil.createUniqueStringID(blobIDs,  0);

        final JTextField txtPath = SWAWizardAdd.createTxtField(d, true);
        final JButton browse = new JButton(RBLanguages.get("browse") + "...");

        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(new JLabel("<html>" + explanation + "</html>"),              "span 2 1 wrap" );
        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(txtPath,                                                     "span 2 1 wrap" );
        center.add(browse,                                                      "wrap"          );

        final JButton previous = new JButton("<html>&larr;</html>");
        final JButton next = new JButton("<html>&rarr;</html>");

        next.setEnabled(false);

        buttons.add(previous);
        buttons.add(next);

        super.add(center, BorderLayout.CENTER);
        super.add(buttons, BorderLayout.SOUTH);

        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step4);
                parent.add(parent.step3, BorderLayout.CENTER);
                parent.log("3/5, " + RBLanguages.get("select_hp_and_fs"));
                parent.validate();
                parent.repaint();
            }
        });

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step4);
                parent.add(parent.step5, BorderLayout.CENTER);
                parent.log("5/5, " + RBLanguages.get("select_file_format"));
                parent.validate();
                parent.repaint();
            }
        });

        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("ZIP " + RBLanguages.get("files"), "zip");
                fileChooser.setFileFilter(filter);

                int returnValue = fileChooser.showOpenDialog(parent);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    txtPath.setText(f.toString());
                    txtPath.setBackground(Color.WHITE);
                    next.setEnabled(true);
                    imageBlob = new ImageBlob(blobID, f);
                }
            }
        });

        txtPath.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                File f = new File(txtPath.getText().trim());
                if(f.exists() && f.isFile() && f.getName().toLowerCase().endsWith(".zip")) {
                    txtPath.setBackground(Color.WHITE);
                    imageBlob = new ImageBlob(blobID, f);
                    next.setEnabled(true);
                }
                else {
                    txtPath.setBackground(SWAWizardAdd.LIGHT_RED);
                    imageBlob = null;
                    next.setEnabled(false);
                }
            }
        });
    }
}
