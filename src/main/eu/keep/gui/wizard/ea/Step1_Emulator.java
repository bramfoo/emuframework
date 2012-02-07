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
import eu.keep.gui.util.RBLanguages;
import eu.keep.gui.wizard.ea.model.Emulator;
import eu.keep.gui.wizard.swa.SWAWizardAdd;
import eu.keep.util.Language;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;

public class Step1_Emulator extends JPanel {

    private EAWizardAdd parent;
    protected Emulator emu = null;

    public Step1_Emulator(final EAWizardAdd parent) {
                super.setLayout(new BorderLayout(5, 5));

        final Dimension d = new Dimension(320, 25);

        this.parent = parent;

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final String explanation = RBLanguages.get("ea_add_emu_explanation");

        Vector<Vector<String>> emuIDs = DBUtil.query(DBUtil.DB.EA, "select emulator_id from emulatorarchive.emulators");
        final String emulator_id = DBUtil.createUniqueIntID(emuIDs, 0);

        final JTextField txtName = SWAWizardAdd.createTxtField(d, true);
        final JTextField txtVersion = SWAWizardAdd.createTxtField(d, false);
        final JComboBox exeCombo = new JComboBox(new String[]{"exe", "jar", "ELF"});
        exeCombo.setPreferredSize(d);
        final JButton btnExe = new JButton(RBLanguages.get("browse") + "...");
        btnExe.setPreferredSize(d);
        final JTextField txtDescription = SWAWizardAdd.createTxtField(d, false);
        final JComboBox langIdCombo = new JComboBox(Language.values());
        langIdCombo.setSelectedItem(Language.en);
        langIdCombo.setPreferredSize(d);
        final JButton btnFolder = new JButton(RBLanguages.get("browse") + "...");
        btnFolder.setPreferredSize(d);
        final JTextField txtUserInstructions = SWAWizardAdd.createTxtField(d, false);

        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel("<html>" + explanation + "</html>"),      "span 2 1 wrap" );
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel("name: ")                                                 );
        center.add(txtName,                                             "wrap"          );
        center.add(new JLabel("version: ")                                              );
        center.add(txtVersion,                                          "wrap"          );
        center.add(new JLabel("executable type: ")                                      );
        center.add(exeCombo,                                            "wrap"          );
        center.add(new JLabel("emulator executable: ")                                  );
        center.add(btnExe,                                              "wrap"          );
        center.add(new JLabel("description: ")                                          );
        center.add(txtDescription,                                      "wrap"          );
        center.add(new JLabel("language: ")                                             );
        center.add(langIdCombo,                                         "wrap"          );
        center.add(new JLabel("emulator installation folder: ")                         );
        center.add(btnFolder,                                           "wrap"          );
        center.add(new JLabel("user instructions: ")                                    );
        center.add(txtUserInstructions,                                 "wrap"          );

        final JButton previous = new JButton("<html>&larr;</html>");
        final JButton next = new JButton("<html>&rarr;</html>");
        next.setEnabled(false);

        buttons.add(previous);
        buttons.add(next);

        super.add(center, BorderLayout.CENTER);
        super.add(buttons, BorderLayout.SOUTH);

        btnExe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int returnValue = fc.showOpenDialog(parent);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    btnExe.setText(file.getAbsolutePath());
                    validate(txtName, btnExe, btnFolder, next);
                }
            }
        });

        btnFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fc.showOpenDialog(parent);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File folder = fc.getSelectedFile();
                    btnFolder.setText(folder.getAbsolutePath());
                    validate(txtName, btnExe, btnFolder, next);
                }
            }
        });

        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validate(txtName, btnExe, btnFolder, next);
            }
        });

        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step1);
                parent.add(parent.step0, BorderLayout.CENTER);
                parent.log("");
                parent.validate();
                parent.repaint();
            }
        });

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                createEmulator(emulator_id, txtName, txtVersion, exeCombo, btnExe, txtDescription, 
                		langIdCombo, btnFolder, txtUserInstructions, next);

                parent.remove(parent.step1);
                parent.add(parent.step2, BorderLayout.CENTER);
                parent.log("2/2, " + RBLanguages.get("select_hardware"));
                parent.validate();
                parent.repaint();
            }
        });
    }

    private void validate(JTextField txtName, JButton btnExe, JButton btnFolder, JButton next) {

        next.setEnabled(false);
        parent.log("");

        String name = txtName.getText();
        File exe = new File(btnExe.getText());
        File folder = new File(btnFolder.getText());

        // Validation
        if(name.equals(EAWizardAdd.MANDATORY_MESSAGE) || name.isEmpty()) return;

        if(!exe.exists() || !folder.exists()) return;

        if(!(new File(folder, "templateCLI.ftl").exists() ||
                new File(folder, "templateXML.ftl").exists() ||
                new File(folder, "templateProps.ftl").exists())) {
            parent.log(RBLanguages.get("no_valid_template") + ": " + folder);
            return;
        }

        next.setEnabled(true);
    }
    
    private void createEmulator(String emulator_id, JTextField txtName, JTextField txtVersion, JComboBox exeCombo,
                             JButton btnExe, JTextField txtDescription, JComboBox langIdCombo,
                             JButton btnFolder, JTextField txtUserInstructions, JButton next) {

    	File exe = new File(btnExe.getText());
        File folder = new File(btnFolder.getText());

        String name = txtName.getText();
        String version = txtVersion.getText();
        String exec_type = (String)exeCombo.getSelectedItem();
        String exec_name = exe.getName();
        String description = txtDescription.getText();
        String language_id = ((Language)langIdCombo.getSelectedItem()).getLanguageId();
        String package_name = folder.getName() + "Package.zip";
        String package_type = "zip";
        String package_version = "1";
        String _package = new File(folder, package_name).getAbsolutePath();
        String user_instructions = txtUserInstructions.getText();
    	
        emu = new Emulator(folder, emulator_id, name, version, exec_type, exec_name, description, language_id,
                package_name, package_type, package_version, _package, user_instructions);
    }
}
