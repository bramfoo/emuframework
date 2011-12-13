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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.keep.softwarearchive.SwLanguageList;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;


/**
 * A Junit Test class for
 * {@link eu.keep.downloader.db.SoftwareArchivePrototype}
 * NOTE: these tests require an instance of the softwareArchive webservices server running. 
 * They also assume that the data in the softwareArchive is set up using the ant-target db.populate
 * (i.e. with free software only, no proprietary software).
 * Test testSetRegistries will try to insert test data into the softwareArchive database. For this
 * reason, it is ignored by default.
 * @author Bram Lohman
 */
@Ignore("Integration tests of the server webservices requiring an instance of the server running")
public class TestSoftwareArchivePrototype {

	private SoftwareArchivePrototype swa;
	private final static String propertiesFile = "test.properties";
	private static Properties props;

	@Before
	public void setUp() throws Exception {

		//TODO Launch server in a separate thread in setup() and kill thread in tearDown()

		// read test.properties
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("eu/keep/" + propertiesFile);
			props = getProperties(is);
		}
		catch(IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}

		// Launch client
		swa = new SoftwareArchivePrototype(props);
	}

	@Test
	public void testGetSoftwarePackage() {
		try {
			List<SoftwarePackage> crud = swa.getSoftwarePackageList();		
			assertEquals("Incorrect number of software images. ", 2, crud.size());
			assertEquals("Unexpected package ID", "IMG-1000", crud.get(0).getId());
			assertEquals("Unexpected package ID", "IMG-1001", crud.get(1).getId());
		} 
		catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());			
		}
	}

	@Test
	public void testGetRegistries() {
		try {
			List<DBRegistry> regListdb = swa.getRegistries();

			assertFalse("List shouldn't be empty", regListdb.isEmpty());
			assertEquals("Wrong size", 2, regListdb.size());
			assertEquals("Wrong registry ID", 2, regListdb.get(0).getRegistryID());
			assertEquals("Wrong registry ID", 3, regListdb.get(1).getRegistryID());

			// Junit4 does not guarantee the order of the unittests, so it is possible that
			// this test is run after testSetRegistries.
			assertTrue("Wrong registry name", 
					(regListdb.get(0).getName().equals("PRONOM/PCR") || regListdb.get(0).getName().equals("Test Registry 2")));
			assertTrue("Wrong registry name", 
					regListdb.get(1).getName().equals("UDFR") || regListdb.get(1).getName().equals("Test Registry 3"));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Ignore("WARNING: this test will insert test data in the LIVE softwareArchive. ENABLE WITH EXTREME CAUTION!!!")
	@Test
	public void testSetRegistries() {

		try {
			List<DBRegistry> regList = new ArrayList<DBRegistry>();

			DBRegistry testReg2 = new DBRegistry();
			testReg2.setRegistryID(2);
			testReg2.setName("Test Registry 2");
			testReg2.setUrl("http://www.test2.com");
			testReg2.setClassName("eu.keep.registry.TestRegistryTwo");
			testReg2.setEnabled(false);
			testReg2.setDescription("Test Registry 2 dummy data");
			testReg2.setComment("Test Registry 2 dummy comment");
			regList.add(testReg2);

			DBRegistry testReg3 = new DBRegistry();
			testReg3.setRegistryID(3);
			testReg3.setName("Test Registry 3");
			testReg3.setUrl("http://www.testThree.eu");
			testReg3.setClassName("eu.keep.registry.TestRegistryThree");
			testReg3.setEnabled(true);
			testReg3.setDescription("Test Registry 3 dummy data");
			testReg3.setComment("Test Registry 3 dummy comment");
			regList.add(testReg3);

			// This will erase the current contents and replace them with the above
			swa.updateRegistries(regList);

			List<DBRegistry> regListdb;
			regListdb = swa.getRegistries();

			assertEquals(testReg2.getRegistryID(), regListdb.get(0).getRegistryID());
			assertEquals(testReg2.getName(), regListdb.get(0).getName());
			assertEquals(testReg2.getUrl(), regListdb.get(0).getUrl());
			assertEquals(testReg2.getClassName(), regListdb.get(0).getClassName());
			assertFalse(regListdb.get(0).isEnabled());
			assertEquals(testReg2.getDescription(), regListdb.get(0).getDescription());
			assertEquals(testReg2.getComment(), regListdb.get(0).getComment());

			assertEquals(testReg3.getRegistryID(), regListdb.get(1).getRegistryID());
			assertEquals(testReg3.getName(), regListdb.get(1).getName());
			assertEquals(testReg3.getUrl(), regListdb.get(1).getUrl());
			assertEquals(testReg3.getClassName(), regListdb.get(1).getClassName());
			assertTrue(regListdb.get(1).isEnabled());
			assertEquals(testReg3.getDescription(), regListdb.get(1).getDescription());
			assertEquals(testReg3.getComment(), regListdb.get(1).getComment());
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testGetLanguages() {
		try {
			SwLanguageList languageList = swa.getSoftwareLanguages();			
			assertEquals("Incorrect number of software languages. ", 1, languageList.getLanguageIds().size());
			assertEquals("Unexpected package ID", "en", languageList.getLanguageIds().get(0));
		} 
		catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());			
		}
		
	}
	
	private Properties getProperties(InputStream inputStream) throws IOException {
		// Read the properties file
		Properties props = new Properties();

		try {
			props.load(inputStream);
			System.out.println("Correctly read properties file: " + inputStream);
		} catch (IOException e) {
			System.out.println("Failed to read properties file [" + inputStream + "]: "
					+ e.toString());
			throw e;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
				// Hmm... hoping not to get this far into catching exceptions;
				// we'll just log it and proceed...
				System.out.println("Failed to close open file: [" + inputStream + "]: "
						+ e.toString());
			}
		}
		return props;
	}

	
}