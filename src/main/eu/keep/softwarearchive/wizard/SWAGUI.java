/*
 * $Revision: 21 $ $Date: 2011-06-22 11:13:07 +0200 (Wed, 22 Jun 2011) $
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
package eu.keep.softwarearchive.wizard;

import eu.keep.softwarearchive.wizard.tabs.*;

import javax.swing.*;
import java.awt.*;

public class SWAGUI extends JFrame {

    private JLabel logLabel;
    private JTabbedPane tabs;

    private FileFormat fileFormat;
    private PronomFileFormat pronomFileFormat;
    private App app;
    private OS os;
    private DiskImage diskImage;

    public SWAGUI() {
        super("Add software to the SWA");

        super.setDefaultCloseOperation(EXIT_ON_CLOSE); // TODO dispose instead
        super.setResizable(false);
        super.setSize(500, 600);

        initGUI();

        super.setVisible(true);
    }

    public void enableTabIndex(int index) {
        tabs.setEnabledAt(index, true);
        tabs.setSelectedIndex(index);
    }

    public String getAppID() {
        return app.appID;
    }

    public String getAppName() {
        return app.appName;
    }

    public String getFormatIDCef() {
        return fileFormat.formatIDCef;
    }

    public String getFormatIDSwa() {
        return fileFormat.formatIDSwa;
    }

    public String getFormatName() {
        return fileFormat.formatName;
    }

    public String getOSID() {
        return os.osID;
    }

    public String getOSName() {
        return os.osName;
    }


    private void initGUI() {
        super.setLayout(new BorderLayout(5, 5));
        logLabel = new JLabel(" 1/?");
        tabs = new JTabbedPane();

        fileFormat = new FileFormat(this);
        pronomFileFormat = new PronomFileFormat(this);
        app = new App(this);
        os = new OS(this);
        diskImage = new DiskImage(this);

        tabs.add("1", fileFormat);
        tabs.add("2", pronomFileFormat);
        tabs.add("3", app);
        tabs.add("4", os);
        tabs.add("5", diskImage);

        for(int i = 1; i < tabs.getTabCount(); i++) {
            tabs.setEnabledAt(i, false);
        }

        super.add(tabs, BorderLayout.CENTER);
        super.add(logLabel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SWAGUI();
            }
        });
    }
}
