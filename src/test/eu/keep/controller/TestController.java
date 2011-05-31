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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import eu.keep.softwarearchive.pathway.HardwarePlatformType;
import eu.keep.softwarearchive.pathway.ObjectFormatType;
import eu.keep.softwarearchive.pathway.Pathway;

/**
 * A Junit Test class for {@link eu.keep.controller.Controller}
 * @author Bram Lohman
 */
public class TestController {
	
	Controller ctrl;
	File emuDir;
	File emuExec;
	List<File> digObjs;
	List<File> swImgs;
	Pathway mockedPW;
	
	@Before
    public void setup() {
		
		ctrl = new Controller();
		
		emuDir = new File("testData/");
		emuExec = new File("testData/digitalObjects/text.txt.xml");
		
		mockedPW = mock(Pathway.class);
		ObjectFormatType mockedObj = mock(ObjectFormatType.class);
		HardwarePlatformType mockedHW = mock(HardwarePlatformType.class);
		when(mockedPW.getObjectFormat()).thenReturn(mockedObj);
		when(mockedPW.getHardwarePlatform()).thenReturn(mockedHW);
		when(mockedObj.getId()).thenReturn("MOCK-0001");
		when(mockedHW.getName()).thenReturn("MOCK-0002");
		
		digObjs = new ArrayList<File>();
		digObjs.add(new File("testData/digitalObjects/x86/build.xml"));

		swImgs = new ArrayList<File>();
		swImgs.add(new File("testData/digitalObjects/HardDiskImage/FreeDOS09_blocek.img.zip"));
	}
	
	@Test(expected=IOException.class)
    public void testGetEmuConfig() throws Exception {
		
		Integer i = null;
		ctrl.getEmuConfig(i);
	}

	@Test
    public void testGetEmuConfig2() throws IOException  {

		Integer i;
		i = ctrl.prepareConfiguration(emuDir, digObjs, swImgs, emuExec, mockedPW);
		assertEquals("Incorrect configuration number", 1, i.intValue());
		
		Map<String, List<Map<String, String>>> opts;
		opts = ctrl.getEmuConfig(i);
		
		assertTrue("Root node not found", opts.containsKey("root"));
		assertFalse("Root node should not be empty", opts.get("root").isEmpty());
		assertEquals("Root->digobj not set correctly", digObjs.get(0).getAbsolutePath(), opts.get("root").get(0).get("digobj"));
		assertEquals("Root->configFile not set correctly", "noConfFileDefined", opts.get("root").get(0).get("configFile"));
		assertEquals("Root->configDir not set correctly", emuDir.toString(), opts.get("root").get(0).get("configDir"));
	}

	@Test(expected=IOException.class)
    public void testSetEmuConfig() throws Exception {

		ctrl.setEmuConfig(null, null);
	}	

	@Test
    public void testSetEmuConfig2() throws IOException  {

		Integer i;
		i = ctrl.prepareConfiguration(emuDir, digObjs, swImgs, emuExec, mockedPW);
		assertEquals("Incorrect configuration number", 1, i.intValue());
		
		Map<String, List<Map<String, String>>> opts = new HashMap<String, List<Map<String, String>>>();
		List<Map<String, String>> opt = new ArrayList<Map<String, String>>();
		Map<String, String> vals = new HashMap<String, String>();
		vals.put("key1", "value1");
		opt.add(vals);
		opts.put("option1", opt);

		ctrl.setEmuConfig(opts, i);
		
		assertEquals("Options not set correctly", "value1", ctrl.getEmuConfig(i).get("option1").get(0).get("key1"));
	}

	@Test(expected=IOException.class)
    public void testRunEmulationProcess() throws Exception {

		ctrl.runEmulationProcess(null);
	}	

	@Test
    public void testPrepareConfiguration() throws IOException  {

		Integer i;
		i = ctrl.prepareConfiguration(emuDir, digObjs, swImgs, emuExec, mockedPW);
		assertEquals("Incorrect configuration number", 1, i.intValue());
		
		i = ctrl.prepareConfiguration(emuDir, digObjs, swImgs, emuExec, mockedPW);
		assertEquals("Incorrect configuration number", 2, i.intValue());
	}
}
