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
import eu.keep.gui.wizard.swa.model.*;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class ConfirmPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(ConfirmPanel.class.getName());

    private final JLabel explanation;
    private final SWAWizardAdd wizard;

    ConfirmPanel(final SWAWizardAdd parent) {
        super.setLayout(new BorderLayout(5, 5));

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        wizard = parent;
        explanation = new JLabel();

        center.add(new JLabel(" "), "wrap"); // empty line
        center.add(explanation, "span 2 1 wrap");
        center.add(new JLabel(" "), "wrap"); // empty line
        center.add(new JLabel(" "), "wrap"); // empty line

        final JButton previous = new JButton("<html>&larr;</html>");
        final JButton cancel = new JButton(RBLanguages.get("cancel"));
        final JButton confirm = new JButton(RBLanguages.get("confirm"));

        buttons.add(previous);
        buttons.add(cancel);
        buttons.add(confirm);

        super.add(center, BorderLayout.CENTER);
        super.add(buttons, BorderLayout.SOUTH);

        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.confirm);
                parent.add(parent.step5, BorderLayout.CENTER);
                parent.log("5/5, " + RBLanguages.get("select_file_format"));
                parent.validate();
                parent.repaint();
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {RBLanguages.get("yes_exit"), RBLanguages.get("no_exit")};
                int returnValue = JOptionPane.showOptionDialog(parent,
                        RBLanguages.get("sure_cancel") + "\n\n" + RBLanguages.get("all_information_discarded") + "!",
                        RBLanguages.get("cancel") + "?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

                if (returnValue == JOptionPane.YES_OPTION) {
                    parent.dispose();
                }
            }
        });

        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {RBLanguages.get("yes"), RBLanguages.get("cancel")};
                int returnValue = JOptionPane.showOptionDialog(parent,
                        RBLanguages.get("sure_commit"),
                        RBLanguages.get("commit_changes") + "?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

                if (returnValue == JOptionPane.YES_OPTION) {
                    previous.setEnabled(false);
                    cancel.setEnabled(false);
                    confirm.setEnabled(false);

                    parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    parent.log(RBLanguages.get("committing_changes") + ", " + RBLanguages.get("log_please_wait") + "...");
                    parent.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    parent.setEnabled(false);

                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final App app = parent.step1.app;
                                final OpSys opsys = parent.step2.opSys;
                                final Platform platform = parent.step3.platform;
                                final ImageFormat imageFormat = parent.step3.imageFormat;
                                final ImageBlob blob = parent.step4.imageBlob;
                                final FileFormat fileFormat = parent.step5.fileFormat;

                                // handle a new app
                                if (app.newApp) {
                                    execute(
                                            DBUtil.DB.SWA,
                                            "Inserted " + app.name + " in table: 'softwarearchive.apps'",
                                            "Could not insert " + app.name + " in table: 'softwarearchive.apps'",
                                            "INSERT INTO softwarearchive.apps " +
                                                    "(app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) " +
                                                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                            app.app_id, app.name, app.version, app.description, app.creator, app.release_date, app.license,
                                                    app.language_id, app.reference, app.user_instructions
                                    );
                                }

                                // handle a new opsys
                                if (opsys.newOpSys) {
                                    execute(
                                            DBUtil.DB.SWA,
                                            "Inserted " + opsys.name + " in table: 'softwarearchive.opsys'",
                                            "Could not insert " + opsys.name + " in table: 'softwarearchive.opsys'",
                                            "INSERT INTO softwarearchive.opsys " +
                                                    "(opsys_id, name,version, description, creator, release_date, license, language_id, reference, user_instructions) " +
                                                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                            opsys.opsys_id, opsys.name, opsys.version, opsys.description, opsys.creator, opsys.release_date, opsys.license,
                                                    opsys.language_id, opsys.reference, opsys.user_instructions
                                    );
                                }

                                // associate platform with OS
                                execute(
                                        DBUtil.DB.SWA,
                                        "Associated OS: " + opsys.opsys_id + " with PLATFORM: " + platform.platform_id + " in table: 'softwarearchive.opsys_platform'",
                                        "Could not associate OS: " + opsys.opsys_id + " with PLATFORM: " + platform.platform_id + " in table: 'softwarearchive.opsys_platform'",
                                        "INSERT INTO softwarearchive.opsys_platform (opsys_id, platform_id) VALUES(?, ?)",
                                        opsys.opsys_id, platform.platform_id
                                );

                                // create a new entry in the 'images' table
                                final Vector<Vector<String>> imageData = DBUtil.query(DBUtil.DB.SWA,
                                        "SELECT image_id, description, imageformat_id, platform_id FROM softwarearchive.images");
                                String imgID = DBUtil.createUniqueStringID(imageData, 0);
                                String description = opsys.name;
                                if(opsys.description != null && !opsys.description.isEmpty()) {
                                    description += " " + opsys.description;
                                }
                                execute(
                                        DBUtil.DB.SWA,
                                        "Successfully added a new entry in the 'images' table",
                                        "Could not add a new entry in the 'images' table",
                                        "INSERT INTO softwarearchive.images (image_id, description, imageformat_id, platform_id) VALUES(?, ?, ?, ?)",
                                        imgID, description, imageFormat.imageformat_id, platform.platform_id
                                );

                                // add the zip file as a BLOB
                                execute(
                                        DBUtil.DB.SWA,
                                        "Successfully added " + blob.zipFile.getAbsolutePath() + " to 'softwarearchive.imageblobs'",
                                        "Could not add " + blob.zipFile.getAbsolutePath() + " to 'softwarearchive.imageblobs'",
                                        "INSERT INTO softwarearchive.imageblobs (image_id, image) VALUES(?, FILE_READ(?))",
                                        imgID, blob.zipFile.getAbsolutePath()
                                );

                                // add a new file format, if no existing was selected
                                if(fileFormat.newFormat) {
                                    execute(
                                            DBUtil.DB.SWA,
                                            "Successfully added " + fileFormat.name + " to 'softwarearchive.fileformats'",
                                            "Could not add " + fileFormat.name + " to 'softwarearchive.fileformats'",
                                            "INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES(?, ?, ?, ?, ?)",
                                            fileFormat.fileformat_id, fileFormat.name, fileFormat.version, fileFormat.description, fileFormat.reference
                                    );
                                }

                                // associate app with opsys
                                execute(
                                        DBUtil.DB.SWA,
                                        "Successfully associated app (" + app.app_id + ") with os (" + opsys.opsys_id + ")",
                                        "Could not associate app (" + app.app_id + ") with os (" + opsys.opsys_id + ")",
                                        "INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES(?, ?)",
                                        app.app_id, opsys.opsys_id
                                );

                                // associate app with image
                                execute(
                                        DBUtil.DB.SWA,
                                        "Successfully associated app (" + app.app_id + ") with image (" + imgID + ") in 'softwarearchive.apps_images'",
                                        "Could not associate app (" + app.app_id + ") with image (" + imgID + ") in 'softwarearchive.apps_images'",
                                        "INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES(?, ?)",
                                        app.app_id, imgID
                                );

                                // associate opsys with image
                                execute(
                                        DBUtil.DB.SWA,
                                        "Successfully associated os (" + opsys.opsys_id + ") with image (" + imgID + ") in 'softwarearchive.opsys_images'",
                                        "Could not associated os (" + opsys.opsys_id + ") with image (" + imgID + ") in 'softwarearchive.opsys_images'",
                                        "INSERT INTO softwarearchive.opsys_images (opsys_id, image_id) VALUES(?, ?)",
                                        opsys.opsys_id, imgID
                                );

                                // associate file format with app
                                execute(
                                        DBUtil.DB.SWA,
                                        "Successfully associated file format (" + fileFormat.fileformat_id + ") with app (" + app.app_id + ") in 'softwarearchive.fileformats_apps'",
                                        "Could not associate file format (" + fileFormat.fileformat_id + ") with app (" + app.app_id + ") in 'softwarearchive.fileformats_apps'",
                                        "INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES(?, ?)",
                                        fileFormat.fileformat_id, app.app_id
                                );

                                // Yay, all went okay!
                                parent.log(RBLanguages.get("committed_changes") + "!");

                            }
                            catch (Exception ex) {
                                parent.log(RBLanguages.get("error") + ": " + ex.getMessage());
                            }

                            parent.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            parent.setEnabled(true);
                            parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    })).start();
                }
                else {
                    parent.log(RBLanguages.get("commit_canceled"));
                }
            }
        });
    }

    public void init() {

        boolean newApp = wizard.step1.app.newApp;
        String appName = wizard.step1.app.name;

        boolean newOS = wizard.step2.opSys.newOpSys;
        String osName = wizard.step2.opSys.name;

        String platform = wizard.step3.platform.name;
        String imageFormat = wizard.step3.imageFormat.name;

        String zipPath = wizard.step4.imageBlob.zipFile.getAbsolutePath();

        String fileFormat = wizard.step5.fileFormat.name;

        String text = String.format(
                RBLanguages.get("swa_confirm_commit"),
                (newApp ? RBLanguages.get("added_new_application") : RBLanguages.get("selected_existing_application")), appName,  // <li> 1
                appName, (newOS ? RBLanguages.get("newly_added") : RBLanguages.get("existing")), osName,                          // <li> 2
                osName, platform, imageFormat,                                                                  // <li> 3
                osName, appName, zipPath,                                                                       // <li> 4
                fileFormat, appName                                                                             // <li> 5
        );

        explanation.setText(text);
    }


    private void execute(DBUtil.DB db, String onSuccess, String onError, String sql, Object... params) {

        int count = DBUtil.update(db, sql, params);

        if (count == 1) {
            logger.info(onSuccess);
        } else {
            logger.error(onError);
            throw new RuntimeException(onError);
        }
    }
}
