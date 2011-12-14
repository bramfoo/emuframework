/*
 * $Revision$ $Date$
 * $Author$
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
package eu.keep.gui;

import eu.keep.characteriser.Format;
import eu.keep.gui.common.DBPanel;
import eu.keep.gui.config.ConfigPanel;

import javax.swing.*;

import java.awt.*;
import java.util.List;
import java.util.Properties;

/**
 * The main panel the GUI frame holds.
 */
public class MainPanel extends JPanel {

    protected GUI parent;
    protected ConfigPanel configPanel = null;
    private final boolean eaAdminTab;
    private final boolean swaAdminTab;
    protected JTabbedPane tabbedPane;
    private DBPanel eaPanel = null;
    private DBPanel swaPanel = null;

    MainPanel(GUI gui, boolean eaAdmin, boolean swaAdmin) {
        parent = gui;
        eaAdminTab = eaAdmin;
        swaAdminTab = swaAdmin;
        initGUI();
    }

    void clear() {
        configPanel.clear();
    }

    private void initGUI() {
        super.setLayout(new BorderLayout(5, 5));
        tabbedPane = new JTabbedPane();

        configPanel = new ConfigPanel(parent);
        tabbedPane.addTab("Emulation Framework", null, configPanel, "Configure and start an emulator.");

        Properties p = parent.guiProps;

        if(eaAdminTab) {
            // The emulator archive database tab.
            eaPanel = new DBPanel(parent,
                    p.getProperty("ea.jdbc.prefix") + p.getProperty("ea.db.url") + p.getProperty("ea.db.exists") + p.getProperty("ea.db.server"),
                    p.getProperty("ea.db.schema.name"),
                    p.getProperty("ea.db.admin"),
                    p.getProperty("ea.db.adminpassw")
            );
            tabbedPane.addTab("Emulator Archive", null, eaPanel, "Manage the emulator archive database.");
        }
        
        if (swaAdminTab) {
            // The software archive database tab.
            swaPanel = new DBPanel(parent,
                    p.getProperty("swa.jdbc.prefix") + p.getProperty("swa.db.url")  + p.getProperty("swa.db.exists")  + p.getProperty("swa.db.server"),
                    p.getProperty("swa.db.schema.name"),
                    p.getProperty("swa.db.admin"),
                    p.getProperty("swa.db.adminpassw")
            );
            tabbedPane.addTab("Software Archive", null, swaPanel, "Manage the software archive database.");
        }

        super.add(tabbedPane, BorderLayout.CENTER);
    }

    void loadFormats(List<Format> formats) {
        configPanel.loadFormats(formats);
    }
    
    
}