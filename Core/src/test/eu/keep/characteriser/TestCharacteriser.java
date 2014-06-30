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

package eu.keep.characteriser;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.keep.downloader.db.DBRegistry;
import eu.keep.downloader.db.SoftwareArchivePrototype;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.util.FileUtilities;

/**
 * A Junit Test class for {@link eu.keep.characteriser.Characteriser}
 * @author David Michel
 * @author Bram Lohman
 */
public class TestCharacteriser {

    protected Logger                logger         = Logger.getLogger(this.getClass());
    protected final static String   propertiesFile = "test.properties";
    protected static Properties     props;

    private SoftwareArchivePrototype mockSWA = mock(SoftwareArchivePrototype.class);
    
    Characteriser characteriser;
	File testJPG;
	File testXML;

    @Before
    public void setUp() throws Exception {

        // read properties file
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("eu/keep/" + propertiesFile);
        props = FileUtilities.getProperties(is);

		// SoftwareArchivePrototype
		List<DBRegistry> registries = new ArrayList<DBRegistry>();
		DBRegistry registry = new DBRegistry();
		registry.setName("PRONOM/PCR");
		registry.setRegistryId(2);
		registry.setEnabled(true);
		registry.setClassName("eu.keep.characteriser.registry.PronomRegistry");
		registries.add(registry);		
		when(mockSWA.getRegistries()).thenReturn(registries);
		
        // setup the characteriser
		characteriser = new Characteriser(props) {
			protected SoftwareArchivePrototype createSWA(Properties props) {
		    	return TestCharacteriser.this.mockSWA;
			}
		};

        testJPG = new File("testData/digitalObjects/x86/lena.jpg");
        testXML = new File("testData/digitalObjects/x86/build.xml");
        logger.info("Set up Characteriser test class");
    }

    @Test
    public void testCharacterise() {

    	// Characterise test file
    	List<Format> formats = new ArrayList<Format>();
    	try {
    		formats = characteriser.characterise(testJPG);
    	} catch (IOException e) {
    		logger.error("Cannot characterise test file: " + e.getMessage());
    		fail("Cannot characterise test file: " + testJPG.getAbsolutePath());
    	}

    	assertEquals("the list of expected and resulting formats should have the same size",1,formats.size());
    	assertEquals("Formats name should be identical","JPEG File Interchange Format",formats.get(0).getName());
    	assertEquals("Formats mme type should be identical","image/jpeg",formats.get(0).getMimeType());
    }
     
	@Test
    public void testGetFileInfo() {
		Map<String, List<String>> info = new HashMap<String, List<String>>();
 
		List<String> infoTrue = new ArrayList<String>();
		infoTrue.add("true");
		
		List<String> infoSize = new ArrayList<String>();
		infoSize.add("22241");
		info.put("size", infoSize);
		
		List<String> infoChecksum = new ArrayList<String>();
		infoChecksum.add("");
		info.put("md5checksum", infoChecksum);
		
		List<String> infoLastModified = new ArrayList<String>();
		infoLastModified.add("");
		info.put("md5checksum", infoLastModified);
		
		
		
		try {
			info = characteriser.getFileInfo(testXML);
		} catch (IOException e) {
			fail("Unexpected error:" + e.getMessage());
		}
		
		assertEquals("Metadata list not right size.", 6, info.size());
		assertEquals("Incorrect Well-formedness returned", "true", info.get("well-formed").get(0));
		assertEquals("Incorrect validity returned", "true", info.get("valid").get(0));
		assertEquals("Incorrect size returned", "23276", info.get("size").get(0));
		assertEquals("Incorrect filename returned", "testData\\digitalObjects\\x86\\build.xml", info.get("filename").get(0));
		assertEquals("Incorrect md5checksum returned", "368ed07fa017e3a58df25cb7949b783c", info.get("md5checksum").get(0));
	}
	
	@Test
    public void testGetFileMetadata() {

		Map<String, List<String>> info = new HashMap<String, List<String>>();
		List<String> infoXML = new ArrayList<String>();
		infoXML.add("XML");
		info.put("markupBasis", infoXML);
		List<String> infoVersion = new ArrayList<String>();
		infoVersion.add("1.0");
		info.put("markupBasisVersion", infoVersion);
		List<String> infoChar = new ArrayList<String>();
		infoChar.add("UTF-8");
		info.put("charset", infoChar);

		try {
			assertEquals("Wrong file info", info, characteriser.getTechMetadata(testXML));
		} catch (IOException e) {
			fail("Unexpected error:" + e.getMessage());
			}
	}
	
	@Test
    public void testGeneratePathway() {
	
		Format form = new Format("Extensible Markup Language", "text/xml");
		
		try {
			assertTrue("Expected empty pathway list", characteriser.generatePathway(form).isEmpty());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail("Unexpected error:" + e.getMessage());
		}
	}
	
	@Test
    public void testGetRegistries() {
		
		List<DBRegistry> db =  new ArrayList<DBRegistry>();
		try {
			db = characteriser.getRegistries();
		} catch (IOException e) {
			fail("Unexpected error:" + e.getMessage());
		}
		
		assertEquals("Incorrect number of registries", 1, db.size());
		DBRegistry reg = db.get(0);
		assertEquals("Incorrect registry ID", 2, reg.getRegistryID());
		assertEquals("Incorrect registry name", "PRONOM/PCR", reg.getName());
		assertEquals("Incorrect registry class", "eu.keep.characteriser.registry.PronomRegistry", reg.getClassName());
	}

	@Test
    public void testGetPathwayFromFile() {
	
		File pwFile = new File("testData/digitalObjects/text.txt.xml");
		
        // Validation file
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("eu/keep/resources/external/PathwaySchema.xsd");
        StreamSource validationSchema = new StreamSource(is);

        Pathway pw = null;
		try {
			pw = characteriser.getPathwayFromFile(pwFile, validationSchema);
		} catch (IOException e) {
			fail("Unexpected error:" + e.getMessage());
		}

		assertEquals("Incorrect object ID", "FFT-1008", pw.getObjectFormat().getId());
		assertEquals("Incorrect object name", "Plain text file", pw.getObjectFormat().getName());
		assertEquals("Incorrect application version", "1.0", pw.getApplication().getVersion());
		assertEquals("Incorrect OS description", "Default operating system for QBasic", pw.getOperatingSystem().getDescription());
		assertEquals("Incorrect platform ID", "HPF-1004", pw.getHardwarePlatform().getId());
	}
}