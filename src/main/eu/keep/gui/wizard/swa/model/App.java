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
package eu.keep.gui.wizard.swa.model;

public class App {

    public final boolean newApp;
    public final String app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions;

    public App() {
        this(false, null, null, null, null, null, null, null, null, null, null);
    }

    public App(boolean newApp, String app_id, String name, String version, String description, String creator,
               String release_date, String license, String language_id, String reference, String user_instructions) {
        this.newApp = newApp;
        this.app_id = app_id;
        this.name = name;
        this.version = version;
        this.description = description;
        this.creator = creator;
        this.release_date = release_date;
        this.license = license;
        this.language_id = language_id;
        this.reference = reference;
        this.user_instructions = user_instructions;
    }

    public boolean isDummy() {
        return app_id == null;
    }

    @Override
    public String toString() {
        if(name == null) {
            return "<select>";
        }
        else if(version == null) {
            return name;
        }
        return name + " " + version;
    }
}
