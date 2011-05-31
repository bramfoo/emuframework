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

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.keep.softwarearchive.SoftwareArchivePortType;
import eu.keep.softwarearchive.SoftwareArchiveService;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;


/**
 * A Junit Test class for
 * {@link eu.keep.downloader.db.SoftwareArchivePrototype}
 * @author Bram Lohman
 */
// FIXME: Fix mocking of Client timeout policy/MTOM
@Ignore
public class TestSoftwareArchivePrototype {

    protected Logger              logger         = Logger.getLogger(this.getClass());

    final SoftwareArchiveService mockSS = mock(SoftwareArchiveService.class);
    final SoftwareArchivePortType mockPort = mock(SoftwareArchivePortType.class);

    SoftwareArchivePrototype swa;

    @Before
    public void setUp() throws Exception {
        logger.info("Preparing mock objects for SWA prototype test class");
        
        // Prototype
        Properties props = new Properties();
        props.setProperty("software.archive.url", "foo");
        swa = new SoftwareArchivePrototype(props){
            protected SoftwareArchiveService getSoftwareArchiveService(URL url, QName qname) {
            	return mockSS;
            }
            protected SoftwareArchivePortType getSoftwareArchivePortType(QName qname, Class<SoftwareArchivePortType> clazz){
            	return mockPort;
            }
          };
    }
    
	@Test
    public void testGetEmulatorPackage() throws Exception {
		List<SoftwarePackage> crud = swa.getSoftwarePackageList();
		assertEquals("Unexpected package name", "ID", crud.get(0).getId());
	}

}