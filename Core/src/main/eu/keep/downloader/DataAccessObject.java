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

package eu.keep.downloader;

import java.sql.SQLException;
import java.util.Map;

/**
 * Interface definition for DAOs for the local database.
 * @author Bram Lohman
 */
public interface DataAccessObject {

    /**
     * Select the whitelisted emulator IDs from the database
     * @return Map<Integer, String> a key-value pair of emulator IDs and emulator names
     */
    public Map<Integer, String> getWhitelistedEmus() throws SQLException;

    /**
     * Adds an emulator ID to the whitelist
     * (list of emulators that will be used for rendering a digital object)
     * @param i Unique ID of emulator
     * @param descr Description for emulator
     * @return True if ID successfully added to whitelist, false otherwise
     * @throws SQLException 
     */
    public boolean whiteListEmulator(Integer i, String descr) throws SQLException;

    /**
     * Removes an emulator ID from the whitelist
     * (list of emulators that will be used for rendering a digital object)
     * @param i Unique ID of emulator
     * @return True if ID successfully removed from whitelist, false otherwise
     * @throws SQLException 
     */
    public boolean unListEmulator(Integer i) throws SQLException;

}
