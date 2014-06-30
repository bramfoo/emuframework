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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.keep.emulatorarchive.EmulatorArchivePortType;
import eu.keep.emulatorarchive.EmulatorArchiveService;
import eu.keep.emulatorarchive.EmulatorPackageList;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage.Emulator;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage.Package;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage.Emulator.Executable;


/**
 * A Junit Test class for
 * {@link eu.keep.downloader.db.EmulatorArchivePrototype}
 * @author Bram Lohman
 */
// FIXME: Fix mocking of Client timeout policy/MTOM
@Ignore("Integration tests of the server webservices requiring an instance of the server running")
public class TestEmulatorArchivePrototype {

    protected Logger              logger         = Logger.getLogger(this.getClass());

    final EmulatorArchiveService mockSS = mock(EmulatorArchiveService.class);
    final EmulatorArchivePortType mockPort = mock(EmulatorArchivePortType.class);

    EmulatorArchivePrototype ea;
    String emulator;
    String version;
    String descr;
    String hardware;

    @Before
    public void setUp() throws Exception {
        logger.info("Preparing mock objects for EA prototype test class");
        
        // Prototype
        Properties props = new Properties();
        props.setProperty("emulator.archive.url", "foo");
        ea = new EmulatorArchivePrototype(props){
            protected EmulatorArchiveService getEmulatorArchiveService(URL url, QName qname) {
            	return mockSS;
            }
            protected EmulatorArchivePortType getEmulatorArchivePortType(QName qname, Class<EmulatorArchivePortType> clazz){
            	return mockPort;
            }
          };
          
          // Emulator archive
          EmulatorPackageList emp = new EmulatorPackageList();
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
          emp.getEmulatorPackage().add(ep);
          when(mockPort.getEmulatorPackage(1)).thenReturn(ep);
          when(mockPort.getEmulatorPackageList(1)).thenReturn(emp);
    }

	@Test
    public void testGetEmulatorPackage() throws Exception {
		EmulatorPackage crud = ea.getEmulatorPackage(1);
		assertEquals("Unexpected package name", emulator, crud.getEmulator().getName());
	}

    
	@Test
    public void testGetEmulatorPackages() throws Exception {
		List<EmulatorPackage> crud = ea.getEmulatorPackages();
		assertEquals("Unexpected package name", emulator, crud.get(0).getEmulator().getName());
	}
}