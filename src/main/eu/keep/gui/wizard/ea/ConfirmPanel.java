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
import eu.keep.gui.wizard.ea.model.*;
import eu.keep.gui.wizard.ea.model.ImageFormat;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ConfirmPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(ConfirmPanel.class.getName());

    private final JLabel explanation;
    private final EAWizardAdd parent;

    ConfirmPanel(final EAWizardAdd parent) {
        super.setLayout(new BorderLayout(5, 5));

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        this.parent = parent;
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
                parent.add(parent.step2, BorderLayout.CENTER);
                parent.log("2/2, select hardware and image format");
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

                    parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    parent.log("committing changes, please wait...");
                    parent.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    parent.setEnabled(false);

                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final Emulator emu = parent.step1.emu;
                                final Hardware hardware = parent.step2.hardware;
                                final ImageFormat format = parent.step2.format;

                                java.util.List<File> filesToZip = new ArrayList<File>();
                                File emuRoot = new File(emu._package).getParentFile();
                                readRecursive(emuRoot, filesToZip);
                                createZipFile(filesToZip, emu._package);

                                execute(
                                        DBUtil.DB.EA, // database
                                        "successfully inserted " + emu.name + " in the database", // success
                                        "could not insert " + emu.name + " in the database", // error
                                        "INSERT INTO emulatorarchive.emulators " +
                                                "(emulator_id, name, version, exec_type, exec_name, description, language_id, package_name, package_type, package_version, package, user_instructions) " +
                                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, FILE_READ(?), ?)", // sql
                                        emu.emulator_id, emu.name, emu.version, emu.exec_type, emu.exec_name, emu.description, emu.language_id,
                                                emu.package_name, emu.package_type, emu.package_version, emu._package, emu.user_instructions // params
                                );

                                execute(
                                        DBUtil.DB.EA, // database
                                        "successfully associated the emulator with the hardware", // success
                                        "could not associate the emulator with the hardware", // error
                                        "INSERT INTO emulatorarchive.emus_hardware (emulator_id, hardware_id) VALUES(?, ?)", // sql
                                        emu.emulator_id, hardware.hardware_id // params
                                );

                                execute(
                                        DBUtil.DB.EA, // database
                                        "successfully associated the emulator with the disk image format", // success
                                        "could not associate the emulator with the disk image format", // error
                                        "INSERT INTO emulatorarchive.emus_imageformats (emulator_id, imageformat_id) VALUES(?, ?)", // sql
                                        emu.emulator_id, format.imageformat_id // params
                                );

                                // Yay, all went okay!
                                parent.log("Successfully committed changes!");

                            } catch (Exception ex) {
                                parent.log("ERROR: " + ex.getMessage());
                                confirm.setEnabled(false);
                            }

                            parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            parent.setEnabled(true);
                            parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    })).start();
                } else {
                    parent.log("commit canceled");
                }
            }
        });
    }

    private void createZipFile(List<File> filesToZip, String zipFileName) throws IOException {

        String root = new File(zipFileName).getParentFile().getAbsolutePath();

        byte[] buf = new byte[1024];

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));

        String regex = "^" +            // the start of the input
                Pattern.quote(root) +   // escape any possible meta characters in the root-path
                "[\\\\/]?";             // an optional trailing back- or forward-slash

        for (File file : filesToZip) {

            FileInputStream in = new FileInputStream(file.getAbsolutePath());

            String relativePath = file.getAbsolutePath().replaceAll(regex, "");
            out.putNextEntry(new ZipEntry(relativePath));

            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.closeEntry();
            in.close();
        }

        out.close();

    }

    private void readRecursive(File folder, java.util.List<File> files) {
        File[] contents = folder.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!f.isDirectory()) {
                    files.add(f);
                } else {
                    readRecursive(f, files);
                }
            }
        }
    }

    public void init() {

        final Emulator emu = parent.step1.emu;
        final String hardware = parent.step2.hardware.name;
        final String format = parent.step2.format.name;

        String text = String.format(
                "<html>\n" +
                        "<h3>Please confirm the following changes to the Emulator Archive:</h3>\n" +
                        " <ul>" +
                        "  <li>the emulator <b>%s</b> is going to be added to the Emulator Archive;</li>" +
                        "  <li><b>%s</b> is installed in the directory <b>%s</b>;</li>" +
                        "  <li>which emulates <b>%s</b> hardware;</li>" +
                        "  <li>and whose disk image file format is <b>%s</b>;</li>" +
                        "</html>",
                emu.name,               // <li> 1
                emu.name, emu.folder,   // <li> 2
                hardware,               // <li> 3
                format                  // <li> 4
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
