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
import eu.keep.gui.wizard.swa.model.ImageFormat;
import eu.keep.gui.wizard.swa.model.Platform;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class Step3_HardwareAndFormat extends JPanel {

    protected Platform platform;
    protected ImageFormat imageFormat;

    Step3_HardwareAndFormat(final SWAWizardAdd parent) {
        super.setLayout(new BorderLayout(5, 5));

        final Dimension d = new Dimension(320, 25);

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final String explanation = RBLanguages.get("swa_hw_and_fs_explanation");

        final Vector<Vector<String>> platformData = DBUtil.query(DBUtil.DB.SWA,
                "SELECT platform_id, name, description, creator, production_start, production_end, reference FROM softwarearchive.platforms");
        final Vector<Platform> existingPlatforms = new Vector<Platform>();
        platform = new Platform();
        existingPlatforms.add(platform);
        for(Vector<String> row : platformData) {
            existingPlatforms.add(new Platform(row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5), row.get(6)));
        }

        final Vector<Vector<String>> fileSystemData = DBUtil.query(DBUtil.DB.SWA,
                "SELECT imageformat_id, name FROM softwarearchive.imageformats");
        final Vector<ImageFormat> existingFileSystems = new Vector<ImageFormat>();
        imageFormat = new ImageFormat();
        existingFileSystems.add(imageFormat);
        for(Vector<String> row : fileSystemData) {
            existingFileSystems.add(new ImageFormat(row.get(0), row.get(1)));
        }

        final JComboBox platformCombo = new JComboBox(existingPlatforms);
        platformCombo.setPreferredSize(d);

        final JComboBox imageFormatCombo = new JComboBox(existingFileSystems);
        imageFormatCombo.setPreferredSize(d);

        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(new JLabel("<html>" + explanation + "</html>"),              "span 2 1 wrap" );
        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(new JLabel(RBLanguages.get("hardware_platform") + ": ")                      );
        center.add(platformCombo,                                               "wrap"          );
        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(new JLabel(RBLanguages.get("file_system") + ": ")                            );
        center.add(imageFormatCombo,                                            "wrap"          );

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
                parent.remove(parent.step3);
                parent.add(parent.step2, BorderLayout.CENTER);
                parent.log("2/5, " + RBLanguages.get("select_os"));
                parent.validate();
                parent.repaint();
            }
        });

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step3);
                parent.add(parent.step4, BorderLayout.CENTER);
                parent.log("4/5, " + RBLanguages.get("select_zip"));
                parent.validate();
                parent.repaint();
            }
        });

        platformCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                platform = (Platform)platformCombo.getSelectedItem();
                imageFormat = (ImageFormat)imageFormatCombo.getSelectedItem();
                next.setEnabled(!platform.isDummy() && !imageFormat.isDummy());
            }
        });

        imageFormatCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                platform = (Platform)platformCombo.getSelectedItem();
                imageFormat = (ImageFormat)imageFormatCombo.getSelectedItem();
                next.setEnabled(!platform.isDummy() && !imageFormat.isDummy());
            }
        });
    }
}
