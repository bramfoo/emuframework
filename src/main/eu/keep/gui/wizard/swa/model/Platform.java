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

public class Platform {

    public final String platform_id, name, description, creator, production_start, production_end, reference;

    public Platform() {
        this(null, null, null, null, null, null, null);
    }

    public Platform(String platform_id, String name, String description, String creator, String production_start, String production_end, String reference) {
        this.platform_id = platform_id;
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.production_start = production_start;
        this.production_end = production_end;
        this.reference = reference;
    }

    public boolean isDummy() {
        return platform_id == null;
    }

    @Override
    public String toString() {
        if(name == null) {
            return "<select>";
        }
        return name;
    }
}
