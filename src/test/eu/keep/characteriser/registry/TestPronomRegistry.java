/*
* $Revision: 758 $ $Date: 2011-05-17 16:27:52 +0200 (Tue, 17 May 2011) $
* $Author: BLohman $
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

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * A Junit Test class for {@link eu.keep.characteriser.registry.PronomRegistry}
 * @author Bram Lohman
 */
public class TestPronomRegistry {

	
	@Test
    public void testGetEmulationPathWays() {
		Registry pr = new PronomRegistry();
		assertTrue("Expected empty pathway list", pr.getEmulationPathWays("Portable Document Format").isEmpty());
    }

}