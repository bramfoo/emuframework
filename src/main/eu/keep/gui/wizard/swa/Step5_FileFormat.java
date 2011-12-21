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

import edu.harvard.hul.ois.fits.FitsOutput;
import eu.keep.characteriser.FitsTool;
import eu.keep.characteriser.Format;
import eu.keep.gui.util.DBUtil;
import eu.keep.gui.wizard.swa.model.FileFormat;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

public class Step5_FileFormat extends JPanel {

    private static final String FITS_HOME = "eu/keep/resources/fits";

    protected FileFormat fileFormat = new FileFormat();

    Step5_FileFormat(final SWAWizard parent) {
        super.setLayout(new BorderLayout(5, 5));

        final Dimension d = new Dimension(320, 25);

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final String explanation = "<h2>Step 5</h2>\n" +
                "<p>Select an existing file format that will be " +
                "associated with the application selected in step 1/5, or create " +
                "a new file format by pointing to a local file on your hard disk</p>";

        final Vector<Vector<String>> fileFormatData = DBUtil.query(DBUtil.DB.SWA,
                "SELECT fileformat_id, name, version, description, reference from softwarearchive.fileformats");
        final Vector<FileFormat> existingFileFormats = new Vector<FileFormat>();
        existingFileFormats.add(fileFormat);
        for(Vector<String> row : fileFormatData) {
            existingFileFormats.add(new FileFormat(false, row.get(0), row.get(1), row.get(2), row.get(3), row.get(4)));
        }

        final JComboBox formatsCombo = new JComboBox(existingFileFormats);
        formatsCombo.setPreferredSize(d);

        final JButton browse = new JButton("browse...");
        final JComboBox fitsOut = new JComboBox();
        fitsOut.setEnabled(false);
        fitsOut.setPreferredSize(d);

        final JTextField txtVersion = SWAWizard.createTxtField(d, false);
        final JTextField txtDescription = SWAWizard.createTxtField(d, false);
        final JTextField txtReference = SWAWizard.createTxtField(d, false);

        final JComponent[] allNewFields = {browse, fitsOut, txtVersion, txtDescription, txtReference};

        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel("<html>" + explanation + "</html>"),      "span 2 1 wrap" );
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel("select an existing file format: ")                       );
        center.add(formatsCombo,                                        "wrap"          );
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel("or create a new file format"),           "span 2 1 wrap" );
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(browse                                                               );
        center.add(fitsOut, "wrap"                                                      );
        center.add(new JLabel("version: ")                                              );
        center.add(txtVersion,                                          "wrap"          );
        center.add(new JLabel("description: ")                                          );
        center.add(txtDescription,                                      "wrap"          );
        center.add(new JLabel("reference: ")                                            );
        center.add(txtReference,                                        "wrap"          );

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
                parent.remove(parent.step5);
                parent.add(parent.step4, BorderLayout.CENTER);
                parent.log("4/5, select the ZIP file");
                parent.validate();
                parent.repaint();
            }
        });

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileFormat.isDummy()) {
                    String id = DBUtil.createUniqueStringID(fileFormatData, 0);
                    fileFormat = new FileFormat(true, id, ((FitsOutWrapper)fitsOut.getSelectedItem()).name, txtVersion.getText(),
                            txtDescription.getText(), txtReference.getText());
                }
                parent.remove(parent.step5);
                parent.confirm.init();
                parent.add(parent.confirm, BorderLayout.CENTER);
                parent.log("Please confirm SWA changes");
                parent.validate();
                parent.repaint();
            }
        });

        formatsCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileFormat = (FileFormat)formatsCombo.getSelectedItem();
                if(fileFormat.isDummy()) {
                    String s = fitsOut.getSelectedItem() != null ? ((FitsOutWrapper)fitsOut.getSelectedItem()).name : "";
                    next.setEnabled(!s.isEmpty() && !s.equals(SWAWizard.MANDATORY_MESSAGE));
                    for(JComponent c : allNewFields) {
                        c.setEnabled(true);
                    }
                }
                else {
                    next.setEnabled(true);
                    for(JComponent c : allNewFields) {
                        c.setEnabled(false);
                    }
                }
            }
        });

        fitsOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                next.setEnabled(true);
            }
        });

        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(parent);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    final File f = fileChooser.getSelectedFile();

                    parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                    parent.log("5/5, Characterizing file: " + f.getName() + ", please wait...");

                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FitsTool tool = new FitsTool(FITS_HOME);
                                FitsOutput output = tool.examine(f);
                                java.util.List<Format> formats = tool.getFormats(output);
                                fitsOut.removeAllItems();

                                for(Format frmt : formats) {
                                    String display = frmt.name;
                                    final int maxSize = 45;
                                    if(display.length() > maxSize) {
                                        display = display.substring(0, maxSize) + "...";
                                    }
                                    fitsOut.addItem(new FitsOutWrapper(display, frmt.name));
                                }
                                fitsOut.setEnabled(true);
                                next.setEnabled(true);
                                parent.log("5/5, Finished characterizing.");

                            } catch(Exception ex) {
                                next.setEnabled(false);
                                parent.log("5/5, Something went wrong while characterizing " + f.getName() + ", aborted operation.");
                            }
                            parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    })).start();
                }
            }
        });
    }

    private static class FitsOutWrapper {

        final String display;
        final String name;

        public FitsOutWrapper(String display, String name) {
            this.display = display;
            this.name = name;
        }

        @Override
        public String toString() {
            return display;
        }
    }
}
