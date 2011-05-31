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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.keep.downloader.db.DBRegistry;
import eu.keep.downloader.db.DBUtil;
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

    Characteriser characteriser;
	File testPDF;
	File testXML;

    @Before
    public void setUp() throws Exception {

        // read properties file
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("eu/keep/" + propertiesFile);
        props = FileUtilities.getProperties(is);

        // set up connection to databases
        String dbLocation = props.getProperty("test.h2.db.url");
        String driverName = props.getProperty("test.h2.db.driver");
        String dbUrl = props.getProperty("test.h2.jdbc.prefix") + dbLocation + props.getProperty("test.h2.db.exists");
        String dbUrlEngine = dbUrl + props.getProperty("test.h2.db.schema");
        String dbUser = props.getProperty("test.h2.db.user");
        String dbPasswd = props.getProperty("test.h2.db.userpassw");

        // Connect to database
        boolean result = true;
        int connectionAttempts = 5;
        Connection localDBConnection;
        localDBConnection = DBUtil.establishConnection(driverName, dbUrlEngine, dbUser,
                dbPasswd, connectionAttempts);
        result &=  localDBConnection != null ? true : false;

        if (!result) {
            throw new IOException("Cannot connect to database " + dbLocation);
        }

        // setup the characteriser
        characteriser = new Characteriser(localDBConnection);

        testPDF = new File("testData/digitalObjects/x86/acro1_0.pdf");
        testXML = new File("testData/digitalObjects/x86/build.xml");
        logger.info("Set up Characteriser test class");
    }

    @Test
    public void testCharacterise() {

        // Build expected list of formats for that test file
        List<Format> expectedFormats = new ArrayList<Format>();

        Format f1 = new Format("Portable Document Format","application/pdf");
        expectedFormats.add(f1);

        // Characterise test file
        List<Format> formats = new ArrayList<Format>();
        try {
            formats = characteriser.characterise(testPDF);
        } catch (IOException e) {
            fail("Cannot characterise test file: " + testPDF.getAbsolutePath());
        }

        assertEquals("the list of expected and resulting formats should have the same size",expectedFormats.size(),formats.size());

        if(expectedFormats.size() == formats.size()) {
            Iterator<Format> it = expectedFormats.iterator();
            for(Format f: formats) {
                Format format = it.next();
                assertEquals("Formats name should be identical",format.getName(),f.getName());
                assertEquals("Formats mme type should be identical",format.getMimeType(),f.getMimeType());

            }
        }
    }
    
	@Test
    public void testGetFileInfo() {
		Map<String, List<String>> info = new HashMap<String, List<String>>();
		List<String> infoTrue = new ArrayList<String>();
		infoTrue.add("true");
		info.put("well-formed", infoTrue);
		info.put("valid", infoTrue);
		List<String> infoSize = new ArrayList<String>();
		infoSize.add("22241");
		info.put("size", infoSize);

		try {
			assertEquals("Wrong file info", info, characteriser.getFileInfo(testXML));
		} catch (IOException e) {
			fail("Unexpected error:" + e.getMessage());
			}
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
		
		assertEquals("Incorrect number of registries", 2, db.size());
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