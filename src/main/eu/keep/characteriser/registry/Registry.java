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

import java.util.List;

import eu.keep.softwarearchive.pathway.Pathway;

/**
 * This represents the {@code Registry} interface for technical registries that
 * hold information about file formats and associated technical environments with
 * digital preservation,  such as migration and emulation, in mind.
 * Note that only one such registry exists at the moment (PRONOM:
 * <a href="URL#http://www.nationalarchives.goc.uk/PRONOM">http://www.nationalarchives.goc.uk/PRONOM/</a>)
 * although more recent version exists under the name PCR developed within the
 * Planets project, see website <a href="URL#http://www.planets-project.eu">http://www.planets-project.eu</a>
 * <p>
 * Here, the main usage is to retrieve emulation pathways given a file format
 * (previously determined by the characteriser, see {@link eu.keep.characteriser}
 *
 * @author Bram Lohman
 * @author David Michel
 *
 * @see eu.keep.softwarearchive.pathway.Pathway
 */
public interface Registry {

    /**
     * Query the registry for pathways given a file format
     * @param fileFormat String representing the file format
     * @return List<Pathway> A list of possible Pathway objects
     */
    public List<Pathway> getEmulationPathWays(String fileFormat);

}
