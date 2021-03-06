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
package eu.keep.gui.wizard.ea.model;

import java.io.File;

public class Emulator {

    public File folder;
    public final String emulator_id, name, version, exec_type, exec_name, description, language_id, package_name,package_type, package_version, _package, user_instructions;

    public Emulator(File folder, String emulator_id, String name, String version, String exec_type, String exec_name,
                    String description, String language_id, String package_name, String package_type,
                    String package_version, String _package, String user_instructions) {
        this.folder = folder;
        this.emulator_id = emulator_id;
        this.name = name;
        this.version = version;
        this.exec_type = exec_type;
        this.exec_name = exec_name;
        this.description = description;
        this.language_id = language_id;
        this.package_name = package_name;
        this.package_type = package_type;
        this.package_version = package_version;
        this._package = _package;
        this.user_instructions = user_instructions;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", name, version, exec_type);
    }
}
