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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.keep.downloader.db.EmulatorArchivePrototype;
import eu.keep.downloader.db.H2DataAccessObject;
import eu.keep.downloader.db.SoftwareArchivePrototype;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage.Emulator;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage.Package;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage.Emulator.Executable;
import eu.keep.softwarearchive.pathway.ObjectFormatType;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;
import eu.keep.util.FileUtilities;


/**
 * A Junit Test class for
 * {@link eu.keep.downloader.Downloader}
 * @author Bram Lohman
 */
public class TestDownloader {

    protected Logger              logger         = Logger.getLogger(this.getClass());

    protected static Properties   props;
    String emulator;
    String version;
    String descr;
    String hardware;
    
    String fileFormat;
    String ffID;
    Pathway pw;

    final SoftwareArchive mockSWA = mock(SoftwareArchivePrototype.class);
    final EmulatorArchive mockEA = mock(EmulatorArchivePrototype.class);
    final DataAccessObject mockDAO = mock(H2DataAccessObject.class);
    Downloader dl;

    @Before
    public void setUp() throws Exception {
        logger.info("Preparing mock objects for downloader test class");
        
        // Emulator archive
        ArrayList<EmulatorPackage> emp = new ArrayList<EmulatorPackage>();
        EmulatorPackage ep = new EmulatorPackage();
        Emulator em = new Emulator();
        Executable ex = new Executable();
        ex.setName("uae");
        ex.setLocation(null);
        Package pk = new Package();
        pk.setName("DioscuriPack");
        emulator = "Dioscuri";
        em.setName(emulator);
        version = "0.6";
        em.setVersion(version);
        descr = "Modular";
        em.setDescription(descr);
        em.setExecutable(ex);
        ep.setEmulator(em);
        ep.setPackage(pk);
        emp.add(ep);
        when(mockEA.getEmulatorPackages()).thenReturn(emp);
        
        Set<String> hw = new HashSet<String>();
        hardware = "x86";
        hw.add(hardware);        
        when(mockEA.getSupportedHardware()).thenReturn(hw);
        
        when(mockEA.getEmulatorsByHardware("x86")).thenReturn(emp);
        when(mockEA.getEmulatorPackage(new Integer(1))).thenReturn(emp.get(0));
        
        InputStream is = new FileInputStream(new File("./testData/LinUAE_0829Package.zip"));
        when(mockEA.downloadEmulatorPackage(new Integer(1))).thenReturn(is);
        
        
        // DAO
        Map<Integer, String> wl = new HashMap<Integer, String>();
        wl.put(1, "Dioscuri");
        when(mockDAO.getWhitelistedEmus()).thenReturn(wl);
		when(mockDAO.whiteListEmulator(1, emulator + " " + version + ": " + descr)).thenReturn(true);
		when(mockDAO.unListEmulator(1)).thenReturn(true);
		when(mockDAO.unListEmulator(2)).thenReturn(false);

        // SoftwareArchive
		fileFormat = "WordPerfect";
        List<Pathway> pws = new ArrayList<Pathway>();
        pw = new Pathway();
        ObjectFormatType obj = new ObjectFormatType();
        ffID = "FFT-1000";
        obj.setId(ffID);
        pw.setObjectFormat(obj);
        pws.add(pw);
        when(mockSWA.getPathwayByFileFormat(fileFormat)).thenReturn(pws);
        when(mockSWA.getSoftwareFormat(ffID)).thenReturn(hardware);
        List<SoftwarePackage> swl = new ArrayList<SoftwarePackage>();
        SoftwarePackage sw = new SoftwarePackage();
        sw.setId("SW-1000");
        sw.setDescription("u a e");
        swl.add(sw);
        when(mockSWA.getSoftwarePackageByPathway(pw)).thenReturn(swl);
        when(mockSWA.getSoftwarePackageList()).thenReturn(swl);
        when(mockSWA.getSoftwarePackage("SW-1000")).thenReturn(sw);
        when(mockSWA.downloadSoftware("SW-1000")).thenReturn(is);
		
        dl = new Downloader(props, null){
            protected SoftwareArchive createSWA(Properties props) {
                return mockSWA;
              }
            protected EmulatorArchive createEA(Properties props) {
                return mockEA;
              }
            protected DataAccessObject createDAO(Connection conn) {
                return mockDAO;
              }
          };
    }
    
	@Test
    public void testGetEmulatorPackages() throws Exception {
		List<EmulatorPackage> crud = dl.getEmulatorPackages();
		assertEquals("Unexpected package name", emulator, crud.get(0).getEmulator().getName());
	}

	@Test
    public void testGetSupportedHardware() throws Exception {
		Set<String> crud = dl.getSupportedHardware();
		assertTrue("Expected x86 hardware", crud.contains(hardware));
	}

	@Test
    public void testGetEmulatorsByHardware() throws Exception {
		List<EmulatorPackage> crud = dl.getEmulatorsByHardware(hardware);
		assertEquals("Unexpected package name", emulator, crud.get(0).getEmulator().getName());
	}

	@Test
    public void testGetEmuExec() throws Exception {
		File tmp = new File(System.getProperty("java.io.tmpdir"));
		File dir = FileUtilities.GenerateUniqueDir(tmp);
		File out = dl.getEmuExec(new Integer(1), dir);
		assertTrue("Expected package binary", out.exists());
	}

	@Test
    public void testGetWhitelistedEmus() throws Exception {
		List<EmulatorPackage> crud = dl.getWhitelistedEmus();
		assertEquals("Unexpected package name", emulator, crud.get(0).getEmulator().getName());
	}

	@Test
    public void testWhitelistEmu() throws Exception {
		boolean result = dl.whiteListEmulator(1);
		assertTrue("Expected success", result);
	}
	
	@Test
    public void testUnlistEmu() throws Exception {
		boolean result = dl.unListEmulator(1);
		assertTrue("Expected success", result);

		result = dl.unListEmulator(2);
		assertFalse("Expected false", result);

	}

	@Test
    public void testGetPathwayByFileFormat() throws Exception {
		List<Pathway> result = dl.getPathwayByFileFormat(fileFormat);
		assertEquals("Wrong ID expected", ffID, result.get(0).getObjectFormat().getId());
	}

	@Test
    public void testGetSoftwarePackageFormat() throws Exception {
		String result = dl.getSoftwarePackageFormat(ffID);
		assertEquals("Expected x86 hardware", hardware, result);
	}

	@Test
    public void testGetSoftwarePackageListPW() throws Exception {
		List<SoftwarePackage> result = dl.getSoftWarePackageList(pw);
		assertEquals("Wrong package returned", "SW-1000", result.get(0).getId());
	}

	@Test
    public void testGetSoftwarePackageList() throws Exception {
		List<SoftwarePackage> result = dl.getSoftwarePackageList();
		assertEquals("Wrong package returned", "SW-1000", result.get(0).getId());
	}

	@Test
    public void testGetSoftwareImage() throws Exception {
		File tmp = new File(System.getProperty("java.io.tmpdir"));
		File dir = FileUtilities.GenerateUniqueDir(tmp);
		File out = dl.getSoftwareImage("SW-1000", dir);
		assertTrue("Expected software binary", out.exists());
	}

	
}