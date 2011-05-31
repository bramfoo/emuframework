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

package eu.keep.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import eu.keep.controller.emulatorConfig.SimpleTemplateBuilder;
import eu.keep.controller.emulatorConfig.TemplateBuilder;
import eu.keep.softwarearchive.pathway.ApplicationType;
import eu.keep.softwarearchive.pathway.HardwarePlatformType;
import eu.keep.softwarearchive.pathway.ObjectFormatType;
import eu.keep.softwarearchive.pathway.OperatingSystemType;
import eu.keep.softwarearchive.pathway.Pathway;

/**
 * A Junit Test class for {@link eu.keep.controller.ConfigEnv}
 * @author Bram Lohman
 */
public class TestConfigEnv {
	
	ConfigEnv ce;
	
	@Before
    public void setup() {

	}
	
	@Test
    public void testGettersSetters() {

		File dir = new File("testData/digitalObjects/");
		File exec = new File("testData/digitalObjects/text.txt.xml");
		TemplateBuilder tb = null;
		try {
			tb = new SimpleTemplateBuilder(dir);
		} catch (IOException e) {
			fail("Unexpected error:" + e.getMessage());
		}
        Pathway pw = new Pathway();
    	ObjectFormatType objF = new ObjectFormatType();
    	ApplicationType app = new ApplicationType();
    	OperatingSystemType opsys = new OperatingSystemType();
    	HardwarePlatformType hpf = new HardwarePlatformType();
    	pw.setObjectFormat(objF);
    	pw.setApplication(app);
    	pw.setOperatingSystem(opsys);
    	pw.setHardwarePlatform(hpf);
        pw.getObjectFormat().setName("Plain text");
        pw.getApplication().setName("EDIT");
        pw.getOperatingSystem().setName("MS-DOS");
        pw.getHardwarePlatform().setName("x86");

		ce = new ConfigEnv(dir, exec, tb, false, true, false, pw);

		assertEquals("Getter not returning same object", dir, ce.getEmuDir());
		assertEquals("Getter not returning same object", exec, ce.getEmuExec());
		assertEquals("Getter not returning same object", tb, ce.getTemplateBuilder());
		assertFalse("Getter not returning same object", ce.hasCliTemplate());
		assertTrue("Getter not returning same object", ce.hasXmlTemplate());
		assertFalse("Getter not returning same object", ce.hasPropsTemplate());
		assertEquals("Getter not returning same object", pw, ce.getPathway());
		assertNotNull("Options not set", ce.getOptions());
		assertTrue("Options not empty", ce.getOptions().isEmpty());
		
		Map<String, List<Map<String, String>>> options = new HashMap<String, List<Map<String, String>>>();
		List<Map<String, String>> option = new ArrayList<Map<String, String>>();
		Map<String, String> values = new HashMap<String, String>();
		values.put("key", "value");
		option.add(values);
		options.put("option", option);

		ce.setOptions(options);
		Map<String, List<Map<String, String>>> optionsOut = ce.getOptions();
		assertEquals("Options not the same", "value", optionsOut.get("option").get(0).get("key"));
	}	
}
