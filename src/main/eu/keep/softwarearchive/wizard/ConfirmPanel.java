package eu.keep.softwarearchive.wizard;

import eu.keep.softwarearchive.util.DBUtil;
import eu.keep.softwarearchive.wizard.model.*;
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
    private final SWAWizard wizard;

    ConfirmPanel(final SWAWizard parent) {
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
        final JButton cancel = new JButton("cancel");
        final JButton confirm = new JButton("confirm");

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
                parent.log("5/5, select a file format");
                parent.validate();
                parent.repaint();
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Yes, exit wizard", "No, don't exit"};
                int returnValue = JOptionPane.showOptionDialog(parent,
                        "Are you sure you want to cancel this operation?\n\nAll information will be discarded!",
                        "Cancel?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

                if (returnValue == JOptionPane.YES_OPTION) {
                    parent.dispose();
                }
            }
        });

        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = {"Yes", "Cancel"};
                int returnValue = JOptionPane.showOptionDialog(parent,
                        "Are you sure you want to commit the changes?",
                        "Commit changes?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

                if (returnValue == JOptionPane.YES_OPTION) {
                    previous.setEnabled(false);
                    cancel.setEnabled(false);
                    confirm.setEnabled(false);

                    parent.log("committing changes, please wait...");
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

                                // all went okay!
                                parent.log("Successfully committed changes!");

                            }
                            catch (Exception ex) {
                                parent.log("ERROR: " + ex.getMessage());
                            }

                            parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            parent.setEnabled(true);
                        }
                    })).start();
                } else {
                    parent.log("commit canceled");
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
                "<html>\n" +
                        "<h2>Please confirm the following changes to the Software Archive:</h2>\n" +
                        "<ul>\n" +
                        "  <li>%s: <b>%s</b>;</li>\n" +
                        "  <li><b>%s</b> will run on the %s operating system: <b>%s</b>;</li>\n" +
                        "  <li>the disk images associated with <b>%s</b> will have <b>%s</b> as its platform and <b>%s</b> as its file system;</li>\n" +
                        "  <li>the zipped file containing <b>%s</b> and <b>%s</b> is located at: <b>%s</b>;</li>\n" +
                        "  <li>the file format <b>%s</b> will be associated with <b>%s</b>.</li>\n" +
                        "</ul>\n" +
                        "</html>",
                (newApp ? "you added the new application" : "you selected the existing application"), appName,  // <li> 1
                appName, (newOS ? "newly added" : "existing"), osName,                                          // <li> 2
                osName, platform, imageFormat,                                                                  // <li> 3
                osName, appName, zipPath,                                                                       // <li> 4
                fileFormat, appName                                                                             // <li> 5
        );

        explanation.setText(text);
    }

    private void execute(DBUtil.DB db, String onSuccess, String onError, String sql, String... params) {

        int count = DBUtil.insert(db, sql, params);

        if (count == 1) {
            logger.info(onSuccess);
        } else {
            logger.error(onError);
            throw new RuntimeException(onError);
        }
    }
}
