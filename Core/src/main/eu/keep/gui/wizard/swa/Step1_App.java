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
import eu.keep.gui.wizard.swa.model.App;
import eu.keep.util.Language;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

public class Step1_App extends JPanel {

    protected App app = new App();

    Step1_App(final SWAWizardAdd parent) {
        super.setLayout(new BorderLayout(5, 5));

        final Dimension d = new Dimension(320, 25);

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final String explanation = RBLanguages.get("swa_app_explanation");

        final Vector<Vector<String>> appData = DBUtil.query(DBUtil.DB.SWA,
                "SELECT app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions from softwarearchive.apps");
        final Vector<App> existingApps = new Vector<App>();
        existingApps.add(app);
        for(Vector<String> row : appData) {
            existingApps.add(new App(false, row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5), row.get(6), row.get(7), row.get(8), row.get(9)));
        }

        final JComboBox appsCombo = new JComboBox(existingApps);
        appsCombo.setPreferredSize(d);

        final JTextField txtName = SWAWizardAdd.createTxtField(d, true);
        final JTextField txtVersion = SWAWizardAdd.createTxtField(d, false);
        final JTextField txtDescription = SWAWizardAdd.createTxtField(d, false);
        final JTextField txtCreator = SWAWizardAdd.createTxtField(d, false);
        final JTextField txtReleaseDate = SWAWizardAdd.createTxtField(d, false);
        final JTextField txtLicense = SWAWizardAdd.createTxtField(d, false);
        final JComboBox langIdCombo = new JComboBox(Language.values());
        langIdCombo.setSelectedItem(Language.en);
        langIdCombo.setPreferredSize(d);
        final JTextField txtReference = SWAWizardAdd.createTxtField(d, false);
        final JTextField txtUserInstructions = SWAWizardAdd.createTxtField(d, false);

        final JComponent[] allNewAppFields = {txtName, txtVersion, txtDescription, txtCreator,
                txtReleaseDate, txtLicense, langIdCombo, txtReference, txtUserInstructions};

        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel("<html>" + explanation + "</html>"),      "span 2 1 wrap" );
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(RBLanguages.get("select_existing_application") + ": ")    );
        center.add(appsCombo,                                           "wrap"          );
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(RBLanguages.get("new_application")),      "span 2 1 wrap" );
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(RBLanguages.get("name") + ": ")                           );
        center.add(txtName, "wrap"                                                      );
        center.add(new JLabel(RBLanguages.get("version") + ": ")                        );
        center.add(txtVersion,                                          "wrap"          );
        center.add(new JLabel(RBLanguages.get("description") + ": ")                    );
        center.add(txtDescription,                                      "wrap"          );
        center.add(new JLabel(RBLanguages.get("creator") + ": ")                        );
        center.add(txtCreator,                                          "wrap"          );
        center.add(new JLabel(RBLanguages.get("release_date") + ": ")                   );
        center.add(txtReleaseDate,                                      "wrap"          );
        center.add(new JLabel(RBLanguages.get("license") + ": ")                        );
        center.add(txtLicense,                                          "wrap"          );
        center.add(new JLabel(RBLanguages.get("language") + ": ")                       );
        center.add(langIdCombo,                                         "wrap"          );
        center.add(new JLabel(RBLanguages.get("reference") + ": ")                      );
        center.add(txtReference,                                        "wrap"          );
        center.add(new JLabel(RBLanguages.get("user_instructions") + ": ")              );
        center.add(txtUserInstructions,                                 "wrap"          );

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
                parent.remove(parent.step1);
                parent.add(parent.step0, BorderLayout.CENTER);
                parent.log(RBLanguages.get("introduction"));
                parent.validate();
                parent.repaint();
            }
        });

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(app.isDummy()) {
                    String id = DBUtil.createUniqueStringID(appData, 0);
                    app = new App(true, id, txtName.getText(), txtVersion.getText(), txtDescription.getText(),
                            txtCreator.getText(), txtReleaseDate.getText(), txtLicense.getText(),
                            ((Language)langIdCombo.getSelectedItem()).getLanguageId(), txtReference.getText(),
                            txtUserInstructions.getText()
                    );
                }
                parent.remove(parent.step1);
                parent.add(parent.step2, BorderLayout.CENTER);
                parent.log("2/5, " + RBLanguages.get("select_os"));
                parent.validate();
                parent.repaint();
            }
        });

        appsCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app = (App)appsCombo.getSelectedItem();
                if(app.isDummy()) {
                    next.setEnabled(!txtName.getText().trim().isEmpty() && !txtName.getText().equals(SWAWizardAdd.MANDATORY_MESSAGE));
                    for(JComponent c : allNewAppFields) {
                        c.setEnabled(true);
                    }
                }
                else {
                    next.setEnabled(true);
                    for(JComponent c : allNewAppFields) {
                        c.setEnabled(false);
                    }
                }
            }
        });

        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(!txtName.getText().trim().isEmpty()) {
                    next.setEnabled(true);
                }
                else {
                    next.setEnabled(false);
                }
            }
        });
    }
}
