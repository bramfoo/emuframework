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

package eu.keep.kernel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.keep.characteriser.Format;
import eu.keep.downloader.Downloader;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage.Emulator.Executable;
import eu.keep.softwarearchive.pathway.ApplicationType;
import eu.keep.softwarearchive.pathway.HardwarePlatformType;
import eu.keep.softwarearchive.pathway.ObjectFormatType;
import eu.keep.softwarearchive.pathway.OperatingSystemType;
import eu.keep.softwarearchive.pathway.Pathway;

/**
 * A Junit Test class for {@link eu.keep.kernel.Kernel}
 * @author Bram Lohman
 */
public class TestKernel {

    private Logger logger = Logger.getLogger(this.getClass());
    private Kernel kernel;
    final Downloader mockDL = mock(Downloader.class);

    Format mockFrm = mock(Format.class);

	List<Pathway> pwList;
	Pathway pw1;
	
	List<EmulatorPackage> empList;
	EmulatorPackage emp1;
	
	private File digObj;


    @Before
    public void setUp() throws IOException {
        logger.info("Preparing mock objects for Kernel test class");

    	// Format
    	when(mockFrm.getName()).thenReturn("plain text");
    	
    	// Pathway
        pw1 = new Pathway();
    	ObjectFormatType objF = new ObjectFormatType();
    	ApplicationType app = new ApplicationType();
    	app.setId("APP-1");
    	app.setLanguageId("nl");
    	OperatingSystemType opsys = new OperatingSystemType();
    	opsys.setId("OPS-1");
    	opsys.setLanguageId("en");
    	HardwarePlatformType hpf = new HardwarePlatformType();
    	pw1.setObjectFormat(objF);
    	pw1.setApplication(app);
    	pw1.setOperatingSystem(opsys);
    	pw1.setHardwarePlatform(hpf);
        pw1.getObjectFormat().setName("Plain text");
        pw1.getApplication().setName("EDIT");
        pw1.getOperatingSystem().setName("MS-DOS");
        pw1.getHardwarePlatform().setName("x86");

    	pwList = new ArrayList<Pathway>();
    	pwList.add(pw1);
    	
    	
    	// EmulatorPackage
    	emp1 = mock(EmulatorPackage.class);
    	EmulatorPackage.Package pck = mock(EmulatorPackage.Package.class);
    	EmulatorPackage.Emulator em = mock(EmulatorPackage.Emulator.class);
    	Executable exec = mock(Executable.class);
    	when(emp1.getPackage()).thenReturn(pck);
    	when(emp1.getEmulator()).thenReturn(em);
    	when(pck.getId()).thenReturn(100);
    	when(em.getName()).thenReturn("mockEmu");
    	when(em.getVersion()).thenReturn("0.99");
    	when(em.getExecutable()).thenReturn(exec);
    	when(exec.getType()).thenReturn("exe");
    	empList = new ArrayList<EmulatorPackage>();
    	empList.add(emp1);
    	
    	// Downloader
    	when(mockDL.getPathwayByFileFormat("plain text")).thenReturn(pwList);
    	when(mockDL.getEmulatorsByHardware("x86")).thenReturn(empList);
    	when(mockDL.getWhitelistedEmus()).thenReturn(empList);
    	when(mockDL.whiteListEmulator(2)).thenReturn(true);
    	when(mockDL.unListEmulator(2)).thenReturn(true);
    	
    	// Kernel
            kernel = new Kernel("testKernel.properties"){
                protected Downloader createDownloader(Properties props, Connection conn) {
                	return mockDL;
                }
            };
            logger.info("Set up kernel test class");
        
        digObj = new File("./testData/templateCLI.ftl");
    }

    @Test
    public void testGetCoreSettings() {
    	Properties props = kernel.getCoreSettings();
    	assertEquals("Wrong test URL.", "./testData/database/db/EF_Test", props.getProperty("h2.db.url"));
    	assertEquals("Wrong db options.", ";AUTO_SERVER=TRUE;IFEXISTS=FALSE", props.getProperty("h2.db.server") + props.getProperty("h2.db.exists"));
    	assertEquals("Wrong test user.", "test", props.getProperty("h2.db.user"));
    }

    @Test
    public void testObserver() {
    	CoreObserver mockObs = mock(CoreObserver.class);

    	// Ensure no exceptions occur
    	kernel.registerObserver(mockObs);
    	kernel.removeObserver(mockObs);
    }

    @Test
    public void testCharacterise() throws IOException {
    	
    	List<Format> f = kernel.characterise(digObj);
    	System.out.println("Forms: " + f);
    	
    	assertFalse("Format list not right size.", f.isEmpty());
    	assertEquals("Format list not right size.", 1, f.size());
    	assertEquals("Format not plain text", "Plain text", f.get(0).getName());
    	assertEquals("Format mime not text", "text/plain", f.get(0).getMimeType());
    	assertFalse("Reporting tool list not right size.", f.get(0).getReportingTools().isEmpty());
    	assertEquals("Reporting tool list not right size.", 1, f.get(0).getReportingTools().size());
    	assertEquals("Reporting tool not Jhove.", "Jhove 1.5", f.get(0).getReportingTools().get(0));
    }

    @Test
    public void testGetTechMD() throws IOException {
    	Map<String, List<String>> md = kernel.getTechMetadata(digObj);
    	
    	assertFalse("Metadata list not right size.", md.isEmpty());
    	assertEquals("Metadata list not right size.", 2, md.size());
    	assertTrue("Metadata doesn't contain linebreak.", md.containsKey("linebreak"));
    	assertFalse("Linebreak list not right size.", md.get("linebreak").isEmpty());
    	assertEquals("Linebreak list not right size.", 1, md.get("linebreak").size());
    	assertEquals("Linebreak contents not correct.", "LF", md.get("linebreak").get(0));
    }

    @Test
    public void testGetFileInfo() throws IOException {
    	Map<String, List<String>> md = kernel.getFileInfo(digObj);
    	
    	assertFalse("Metadata list not right size.", md.isEmpty());
    	assertEquals("Metadata list not right size.", 3, md.size());
    	assertTrue("Metadata doesn't contain linebreak.", md.containsKey("size"));
    	assertFalse("Size list not right size.", md.get("size").isEmpty());
    	assertEquals("Size list not right size.", 1, md.get("size").size());
    	assertEquals("Size contents not correct.", "1254", md.get("size").get(0));
    }

    @Test
    public void testAutoSelectFormat() throws IOException {

    	Format mockForm2 = mock(Format.class);
    	
    	ArrayList<Format> frms = new ArrayList<Format>();
    	frms.add(mockFrm);
    	frms.add(mockForm2);

    	assertEquals("First in list not returned.", mockFrm, kernel.autoSelectFormat(frms));
    }

    @Test
    public void testGetPathways() throws IOException {
    	assertEquals("Expected pathway list not returned.", pwList, kernel.getPathways(mockFrm));
    	
    	// Change language on test Application to french, and test that it is not returned.
    	pw1.getApplication().setLanguageId("fr");
    	assertEquals("Pathway with Application with unaccepted language returned. ", 0, kernel.getPathways(mockFrm).size());
    }

    
    @Test
    @Ignore
    public void testAutoSelectPathway() throws IOException {
            // satisfiable pathway: "Plain text", "EDIT", "MSDOS", "x86"
            // unsatisfiable pathway
            Pathway p2 = new Pathway();
        	ObjectFormatType objF2 = new ObjectFormatType();
        	ApplicationType app2 = new ApplicationType();
        	OperatingSystemType opsys2 = new OperatingSystemType();
        	HardwarePlatformType hpf2 = new HardwarePlatformType();
        	p2.setObjectFormat(objF2);
        	p2.setApplication(app2);
        	p2.setOperatingSystem(opsys2);
        	p2.setHardwarePlatform(hpf2);
            p2.getObjectFormat().setName("Excel");
            p2.getApplication().setName("MS Excel");
            p2.getOperatingSystem().setName("Windows 95");
            p2.getHardwarePlatform().setName("x86");
            Pathway p3 = new Pathway();
        	ObjectFormatType objF3 = new ObjectFormatType();
        	ApplicationType app3 = new ApplicationType();
        	OperatingSystemType opsys3 = new OperatingSystemType();
        	HardwarePlatformType hpf3 = new HardwarePlatformType();
        	p3.setObjectFormat(objF3);
        	p3.setApplication(app3);
        	p3.setOperatingSystem(opsys3);
        	p3.setHardwarePlatform(hpf3);
            p3.getObjectFormat().setName("Excel");
            p3.getApplication().setName("MS Excel");
            p3.getOperatingSystem().setName("Windows 95");
            p3.getHardwarePlatform().setName("x86");

            // Create a list of pathways
            List<Pathway> pathways = new ArrayList<Pathway>();
            pathways.add(pw1);
            pathways.add(p2);
            pathways.add(p3);

            // make selection
            Pathway selection = kernel.autoSelectPathway(pathways);

            // assert selection
            assertTrue("selection unexpected.", selection.equals(pw1));
            assertTrue("auto-selected pathway must be satisfiable.",kernel.isPathwaySatisfiable(selection));
    }

    @Test
    public void testGetPathwaysFromFile() throws IOException {

        File metadata = new File("./testData/digitalObjects/text.txt.xml");
        Pathway pathway = kernel.extractPathwayFromFile(metadata);

        logger.info("Pathway loaded from file: " + pathway);

        Pathway p1 = new Pathway();
    	ObjectFormatType objF = new ObjectFormatType();
    	ApplicationType app = new ApplicationType();
    	OperatingSystemType opsys = new OperatingSystemType();
    	HardwarePlatformType hpf = new HardwarePlatformType();
    	p1.setObjectFormat(objF);
    	p1.setApplication(app);
    	p1.setOperatingSystem(opsys);
    	p1.setHardwarePlatform(hpf);
    	p1.getObjectFormat().setId("FFT-1008");
    	p1.getObjectFormat().setName("Plain text file");
    	p1.getApplication().setId("APP-2003");
    	p1.getApplication().setName("QBasic");
    	p1.getOperatingSystem().setId("OPS-2000");
        p1.getOperatingSystem().setName("MS-DOS");
        p1.getHardwarePlatform().setId("HPF-1004");
        p1.getHardwarePlatform().setName("x86");

        assertEquals("Pathway identifier not the same.", p1.getObjectFormat().getId(), pathway.getObjectFormat().getId());
        assertEquals("Pathway identifier not the same.", p1.getApplication().getId(), pathway.getApplication().getId());
        assertEquals("Pathway identifier not the same.", p1.getOperatingSystem().getId(), pathway.getOperatingSystem().getId());
        assertEquals("Pathway identifier not the same.", p1.getHardwarePlatform().getId(), pathway.getHardwarePlatform().getId());
        assertEquals("Pathway name not the same.", p1.getObjectFormat().getName(), pathway.getObjectFormat().getName());
        assertEquals("Pathway name not the same.", p1.getApplication().getName(), pathway.getApplication().getName());
        assertEquals("Pathway name not the same.", p1.getOperatingSystem().getName(), pathway.getOperatingSystem().getName());
        assertEquals("Pathway name not the same.", p1.getHardwarePlatform().getName(), pathway.getHardwarePlatform().getName());
    }

    @Test
    public void testGetPathwaysFromEmptyDigitalObjectMetadata() throws IOException {

        File metadata = new File("./testData/digitalObjects/text_nopathways.txt.xml");
        Pathway pathway = kernel.extractPathwayFromFile(metadata);

        logger.info("Pathway loaded from file: " + pathway);

        assertTrue("Pathway identifier not empty.", pathway.getObjectFormat().getId().isEmpty());
        assertTrue("Pathway identifier not empty.", pathway.getApplication().getId().isEmpty());
        assertTrue("Pathway identifier not empty.", pathway.getOperatingSystem().getId().isEmpty());
        assertTrue("Pathway identifier not empty.", pathway.getHardwarePlatform().getId().isEmpty());        
        assertTrue("Pathway name not empty.", pathway.getObjectFormat().getId().isEmpty());        
        assertTrue("Pathway name not empty.", pathway.getApplication().getId().isEmpty());
        assertTrue("Pathway name not empty.", pathway.getOperatingSystem().getId().isEmpty());
        assertTrue("Pathway name not empty.", pathway.getHardwarePlatform().getId().isEmpty());        
    }
    
    
    @Test
    public void testGetEmulatorsByPathway() throws IOException {
    	List<EmulatorPackage> l = kernel.getEmulatorsByPathway(pw1);
    	assertEquals("List not correct.", empList, l);
    }
    
    @Test
    public void testWhiteListedEmus() throws IOException {
    	assertEquals("Expected packages not returned.", empList, kernel.getWhitelistedEmus());
    }
        
    @Test
    public void testWhitelistEmulator() throws IOException {
    	assertTrue("Whitelisting not succesfull.", kernel.whiteListEmulator(2));
    }

    @Test
    public void testunListEmulator() throws IOException {
    	assertTrue("Unlisting not succesfull.", kernel.unListEmulator(2));
    }
}