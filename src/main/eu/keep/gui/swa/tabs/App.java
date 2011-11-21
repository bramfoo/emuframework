package eu.keep.gui.swa.tabs;

import eu.keep.gui.swa.SWAGUI;
import eu.keep.gui.util.DBUtil;
import eu.keep.util.Language;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

public class App extends JPanel {

    private final String explanation =
            "Either select an existing application that should be used to render " +
            "the format from step #1, or create a new application by entering " +
            "the relevant fields.<br><br>";

    private final String getAppsQuery = "SELECT app_id, name FROM softwarearchive.apps";
    private final String insertAppQuery = "INSERT INTO softwarearchive.apps " +
            "(app_id, name, version, description, creator, release_date, license, language_id, reference) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final String insertFormatApp = "INSERT INTO softwarearchive.fileformats_apps (fileformat_id, app_id) VALUES(?, ?)";

    private final SWAGUI parent;

    public String appID;
    public String appName;

    public App(SWAGUI p) {
        parent = p;
        initGUI();
    }

    private void initGUI() {

        super.setLayout(new MigLayout());

        final Vector<Vector<String>> data = DBUtil.query(DBUtil.DB.SWA, getAppsQuery);
        Vector<AppIDName> temp = new Vector<AppIDName>();
        final AppIDName first = new AppIDName(null, null);
        temp.add(first);
        for(Vector<String> row : data) {
            temp.add(new AppIDName(row.get(0), row.get(1)));
        }

        Language[] allLanguages = Language.values();
        Vector<LanguageOption> languageOptions = new Vector<LanguageOption>();
        for (int i=0; i<allLanguages.length; i++) {
        	languageOptions.add(new LanguageOption(allLanguages[i]));
        }
                
        Dimension d = new Dimension(320, 25);

        final JComboBox apps = new JComboBox(temp);
        apps.setPreferredSize(d);

        final JTextField name = new JTextField();
        name.setPreferredSize(d);

        final JTextField version = new JTextField();
        version.setPreferredSize(d);

        final JTextField description = new JTextField();
        description.setPreferredSize(d);

        final JTextField creator = new JTextField();
        creator.setPreferredSize(d);

        final JTextField release_date = new JTextField();
        release_date.setPreferredSize(d);

        final JTextField license = new JTextField();
        license.setPreferredSize(d);

        final JComboBox language = new JComboBox(languageOptions);
        language.setPreferredSize(d);

        final JTextField reference = new JTextField();
        reference.setPreferredSize(d);

        final JButton ok = new JButton("ok");
        ok.setEnabled(false);

        super.add(new JLabel("<html>" + explanation + "</html>"),               "span 2 1 wrap");
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel("select an existing application: ")                               );
        super.add(apps,                                                         "wrap"         );
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel("Create a new app"),                               "wrap"         );
        super.add(new JLabel("name:")                                                          );
        super.add(name,                                                         "wrap"         );
        super.add(new JLabel("version:")                                                       );
        super.add(version,                                                      "wrap"         );
        super.add(new JLabel("description:")                                                   );
        super.add(description,                                                  "wrap"         );
        super.add(new JLabel("creator:")                                                       );
        super.add(creator,                                                      "wrap"         );
        super.add(new JLabel("release date:")                                                  );
        super.add(release_date,                                                 "wrap"         );
        super.add(new JLabel("license:")                                                       );
        super.add(license,                                                      "wrap"         );
        super.add(new JLabel("language:")                                                      );
        super.add(language,                                                     "wrap"         );
        super.add(new JLabel("reference:")                                                     );
        super.add(reference,                                                    "wrap"         );
        super.add(new JLabel()                                                                 ); // empty cell
        super.add(ok,                                                           "align right"  );

        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                AppIDName app = (AppIDName)apps.getSelectedItem();
                String newName = name.getText().trim();

                if(!newName.isEmpty()) {
                    ok.setEnabled(true);
                    apps.setSelectedItem(first);
                }
                else {
                    ok.setEnabled(app.name != null);
                }
            }
        });

        apps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AppIDName app = (AppIDName)apps.getSelectedItem();
                String newName = name.getText().trim();

                if(app.name != null) {
                    ok.setEnabled(true);

                    name.setText("");
                    version.setText("");
                    description.setText("");
                    creator.setText("");
                    release_date.setText("");
                    license.setText("");
                    language.setSelectedIndex(0);
                    reference.setText("");
                }
                else {
                    ok.setEnabled(!newName.isEmpty());
                }
            }
        });

        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppIDName app = (AppIDName)apps.getSelectedItem();

                if(app.name != null) {
                    appID = app.id;
                    appName = app.name;
                }
                else {
                    appID = DBUtil.createUniqueStringID(data, 0);
                    appName = name.getText().trim();

                    int records = DBUtil.insert(DBUtil.DB.SWA, insertAppQuery,
                            appID,
                            appName,
                            version.getText().trim(),
                            description.getText().trim(),
                            creator.getText().trim(),
                            release_date.getText().trim(),
                            license.getText().trim(),
                            ((LanguageOption)language.getSelectedItem()).id,
                            reference.getText().trim()
                    );

                    records += DBUtil.insert(DBUtil.DB.SWA, insertFormatApp, // TODO move from if-stat
                            parent.getFormatIDSwa(),
                            parent.getAppID()
                    );

                    if(records != 1) {
                        // TODO warn
                    }
                    else {
                        // TODO log
                    }
                }

                apps.setEnabled(false);
                name.setEnabled(false);
                version.setEnabled(false);
                description.setEnabled(false);
                creator.setEnabled(false);
                release_date.setEnabled(false);
                license.setEnabled(false);
                language.setEnabled(false);
                reference.setEnabled(false);
                ok.setEnabled(false);

                parent.enableTabIndex(3);
            }
        });
    }

    private static class AppIDName {

        final String id;
        final String name;

        AppIDName(String i, String nm) {
            id = i;
            name = nm;
        }

        @Override
        public String toString() {
            return name == null ? "" : name;
        }
    }
    
}
