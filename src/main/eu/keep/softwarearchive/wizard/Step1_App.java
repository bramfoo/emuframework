package eu.keep.softwarearchive.wizard;

import eu.keep.softwarearchive.util.DBUtil;
import eu.keep.util.Language;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class Step1_App extends JPanel {

    protected App app = new App();

    Step1_App(final SWAWizard parent) {
        super.setLayout(new BorderLayout(5, 5));

        final Dimension d = new Dimension(320, 25);

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final String explanation = "Select an existing application which you want to " +
                "associate with a certain file format, or create a new application.";

        final Vector<Vector<String>> appData = DBUtil.query(DBUtil.DB.SWA,
                "SELECT app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions from softwarearchive.apps");
        final Vector<App> existingApps = new Vector<App>();
        existingApps.add(app);
        for(Vector<String> row : appData) {
            existingApps.add(new App(false, row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5), row.get(6), row.get(7), row.get(8), row.get(9)));
        }

        final JComboBox appsCombo = new JComboBox(existingApps);
        appsCombo.setPreferredSize(d);

        final JTextField txtName = SWAWizard.createTxtField(d, true);
        final JTextField txtVersion = SWAWizard.createTxtField(d, false);
        final JTextField txtDescription = SWAWizard.createTxtField(d, false);
        final JTextField txtCreator = SWAWizard.createTxtField(d, false);
        final JTextField txtReleaseDate = SWAWizard.createTxtField(d, false);
        final JTextField txtLicense = SWAWizard.createTxtField(d, false);
        final JComboBox langIdCombo = new JComboBox(Language.values());
        langIdCombo.setSelectedItem(Language.en);
        langIdCombo.setPreferredSize(d);
        final JTextField txtReference = SWAWizard.createTxtField(d, false);
        final JTextField txtUserInstructions = SWAWizard.createTxtField(d, false);

        final JComponent[] allNewAppFields = {txtName, txtVersion, txtDescription, txtCreator,
                txtReleaseDate, txtLicense, langIdCombo, txtReference, txtUserInstructions};

        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel("<html>" + explanation + "</html>"),      "span 2 1 wrap" );
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel("select an existing application: ")                       );
        center.add(appsCombo,                                           "wrap"          );
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel("or create a new application"),           "span 2 1 wrap" );
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel("name: ")                                                 );
        center.add(txtName, "wrap"                                                      );
        center.add(new JLabel("version: ")                                              );
        center.add(txtVersion,                                          "wrap"          );
        center.add(new JLabel("description: ")                                          );
        center.add(txtDescription,                                      "wrap"          );
        center.add(new JLabel("creator: ")                                              );
        center.add(txtCreator,                                          "wrap"          );
        center.add(new JLabel("release date: ")                                         );
        center.add(txtReleaseDate,                                      "wrap"          );
        center.add(new JLabel("license: ")                                              );
        center.add(txtLicense,                                          "wrap"          );
        center.add(new JLabel("language id: ")                                          );
        center.add(langIdCombo,                                         "wrap"          );
        center.add(new JLabel("reference: ")                                            );
        center.add(txtReference,                                        "wrap"          );
        center.add(new JLabel("user instructions: ")                                    );
        center.add(txtUserInstructions,                                 "wrap"          );

        final JButton next = new JButton("<html>&rarr;</html>");
        next.setEnabled(false);

        buttons.add(next);

        super.add(center, BorderLayout.CENTER);
        super.add(buttons, BorderLayout.SOUTH);

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
                parent.log("2/5, selected app: " + app.name + ", id: " + app.app_id);
                parent.validate();
                parent.repaint();
            }
        });

        appsCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app = (App)appsCombo.getSelectedItem();
                if(app.isDummy()) {
                    next.setEnabled(!txtName.getText().trim().isEmpty() && !txtName.getText().equals(SWAWizard.MANDATORY_MESSAGE));
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
