package eu.keep.gui.swa.tabs;

import eu.keep.gui.swa.SWAGUI;
import eu.keep.gui.util.DBUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

public class OS extends JPanel {

    private final String explanation =
            "Either select an existing operating system that should be used to run " +
            "the application from step #3, or create a new operating system by entering " +
            "the relevant fields below.<br><br>";

    private final String getOSQuery = "SELECT opsys_id, name FROM softwarearchive.opsys";

    private final String insertNewOS = "INSERT INTO softwarearchive.opsys " +
            "(opsys_id, name, version, description, creator, release_date, license, language, reference) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final String getPlatformsQuery = "SELECT platform_id, name, description FROM softwarearchive.platforms";

    private final String insertOSPlatform = "INSERT INTO softwarearchive.opsys_platform (opsys_id, platform_id) VALUES(?, ?)";

    private final String insertAppOS = "INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES(?, ?)";

    private final SWAGUI parent;

    public String osID;
    public String osName;

    public OS(SWAGUI p) {
        parent = p;
        initGUI();
    }

    private void initGUI() {

        super.setLayout(new MigLayout());

        final Vector<Vector<String>> osData = DBUtil.query(DBUtil.DB.SWA, getOSQuery);
        Vector<OSIDName> tempOS = new Vector<OSIDName>();
        final OSIDName firstOS = new OSIDName(null, null);
        tempOS.add(firstOS);
        for(Vector<String> row : osData) {
            tempOS.add(new OSIDName(row.get(0), row.get(1)));
        }

        final Vector<Vector<String>> platformsData = DBUtil.query(DBUtil.DB.SWA, getPlatformsQuery);
        Vector<PlatformIDNameDescription> tempPlatforms = new Vector<PlatformIDNameDescription>();
        final PlatformIDNameDescription firstPlatform = new PlatformIDNameDescription(null, null, null);
        tempPlatforms.add(firstPlatform);
        for(Vector<String> row : platformsData) {
            tempPlatforms.add(new PlatformIDNameDescription(row.get(0), row.get(1), row.get(2)));
        }

        Dimension d = new Dimension(320, 25);

        final JComboBox osses = new JComboBox(tempOS);
        osses.setPreferredSize(d);

        final JComboBox platforms = new JComboBox(tempPlatforms);
        platforms.setPreferredSize(d);

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

        final JTextField language = new JTextField();
        language.setPreferredSize(d);

        final JTextField reference = new JTextField();
        reference.setPreferredSize(d);

        final JButton ok = new JButton("ok");
        ok.setEnabled(false);

        super.add(new JLabel("<html>" + explanation + "</html>"),               "span 2 1 wrap");
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel("select an existing OS: ")                                        );
        super.add(osses,                                                        "wrap"         );
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel("Create a new OS"),                                "wrap"         );
        super.add(new JLabel("platform:")                                                      );
        super.add(platforms,                                                    "wrap"         );
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

                String newName = name.getText().trim();

                if (!newName.isEmpty()) {
                    PlatformIDNameDescription selected = (PlatformIDNameDescription)platforms.getSelectedItem();
                    ok.setEnabled(selected.name != null);
                    osses.setSelectedItem(firstOS);
                }
                else {
                    OSIDName app = (OSIDName)osses.getSelectedItem();
                    ok.setEnabled(app.name != null);
                }
            }
        });

        osses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                OSIDName app = (OSIDName)osses.getSelectedItem();
                String newName = name.getText().trim();

                if (app.name != null) {
                    platforms.setSelectedItem(firstPlatform);

                    ok.setEnabled(true);

                    name.setText("");
                    version.setText("");
                    description.setText("");
                    creator.setText("");
                    release_date.setText("");
                    license.setText("");
                    language.setText("");
                    reference.setText("");
                } else {
                    ok.setEnabled(!newName.isEmpty());
                }
            }
        });

        platforms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                PlatformIDNameDescription selectedPlatform = (PlatformIDNameDescription)platforms.getSelectedItem();

                if(selectedPlatform.name != null) {
                    String newName = name.getText().trim();
                    osses.setSelectedItem(firstOS);
                    ok.setEnabled(!newName.isEmpty());
                }
                else {
                    OSIDName app = (OSIDName)osses.getSelectedItem();
                    ok.setEnabled(app.name != null);
                }
            }
        });

        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OSIDName app = (OSIDName)osses.getSelectedItem();

                if(app.name != null) {
                    osID = app.id;
                    osName = app.name;
                }
                else {
                    osID = DBUtil.createUniqueStringID(osData, 0);
                    osName = name.getText().trim();

                    PlatformIDNameDescription selectedPlatform = (PlatformIDNameDescription)platforms.getSelectedItem();

                    int records = DBUtil.insert(DBUtil.DB.SWA, insertNewOS,
                            osID,
                            osName,
                            version.getText().trim(),
                            description.getText().trim(),
                            creator.getText().trim(),
                            release_date.getText().trim(),
                            license.getText().trim(),
                            language.getText().trim(),
                            reference.getText().trim()
                    );

                    records += DBUtil.insert(DBUtil.DB.SWA, insertOSPlatform,
                            osID,
                            selectedPlatform.id
                    );


                    records += DBUtil.insert(DBUtil.DB.SWA, insertAppOS,
                            parent.getAppID(),
                            osID
                    );

                    if(records != 3) {
                        // TODO warn
                    }
                    else {
                        // TODO log
                    }
                }

                osses.setEnabled(false);
                platforms.setEnabled(false);
                name.setEnabled(false);
                version.setEnabled(false);
                description.setEnabled(false);
                creator.setEnabled(false);
                release_date.setEnabled(false);
                license.setEnabled(false);
                language.setEnabled(false);
                reference.setEnabled(false);
                ok.setEnabled(false);

                parent.enableTabIndex(4);
            }
        });
    }

    private static class OSIDName {

        final String id;
        final String name;

        OSIDName(String i, String nm) {
            id = i;
            name = nm;
        }

        @Override
        public String toString() {
            return name == null ? "" : name;
        }
    }

    private static class PlatformIDNameDescription {

        final String id;
        final String name;
        final String description;

        PlatformIDNameDescription(String i, String nm, String des) {
            id = i;
            name = nm;
            description = des;
        }

        @Override
        public String toString() {
            return name == null ? "" : name + ", " + description;
        }
    }
}
