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

package eu.keep.downloader.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.junit.Before;
import org.junit.Test;

/**
 * A Junit Test class for {@link eu.keep.downloader.db.DBRegistry}
 * @author Bram Lohman
 */
public class TestDBRegistry {
	
	DBRegistry db;
	
	@Before
    public void setup() {

		db = new DBRegistry();
	}
	
	@Test
    public void testGettersSetters() {

		String cn = "className";
		String cmmt = "cmmt";
		String descr = "descr";
		boolean en = true;
		String nm = "name";
		int id = 1;
		String tv = "transview";
		String url = "url";

		db.setClassName(cn);
		db.setComment(cmmt);
		db.setDescription(descr);
		db.setEnabled(en);
		db.setName(nm);
		db.setRegistryID(id);
		db.setTranslationView(tv);
		db.setUrl(url);
		
		assertEquals("Getter not returning same object", cn, db.getClassName());
		assertEquals("Getter not returning same object", cmmt, db.getComment());
		assertEquals("Getter not returning same object", descr, db.getDescription());
		assertTrue("Getter not returning same object", db.isEnabled());
		assertEquals("Getter not returning same object", nm, db.getName());
		assertEquals("Getter not returning same object", id, db.getRegistryID());
		assertEquals("Getter not returning same object", tv, db.getTranslationView());
		assertEquals("Getter not returning same object", url, db.getUrl());
	}	
}
