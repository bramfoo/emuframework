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

package eu.keep.characteriser.registry;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.keep.softwarearchive.pathway.Pathway;

/**
 * This class represents PRONOM's implementation of the
 * {@code Registry} interface {@link eu.keep.characteriser.registry.Registry}.
 * Currently a stub since the registry has not been updated yet for
 * allowing this interaction between the two systems.
 * Returns an empty list of Pathway objects
 * 
 * @author Bram Lohman
 * @author David Michel
 */
public class PronomRegistry implements Registry {

    private static final Logger logger = Logger.getLogger(PronomRegistry.class.getName());

    /**
     * @inheritDoc
     */
    @Override
    public List<Pathway> getEmulationPathWays(String fileFormat) {

        List<Pathway> pathways = new ArrayList<Pathway>();

        // TODO: get emulation pathway via webservices
        logger.info("Querying the PRONOM technical registry for an emulation pathway for format "
                + fileFormat + "...");

        logger.info("PRONOM provides the following pathways for format "
                + fileFormat + ": " + pathways);
        return pathways;
    }

}
