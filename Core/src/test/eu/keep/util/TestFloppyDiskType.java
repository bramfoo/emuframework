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

package eu.keep.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A Junit Test class for {@link eu.keep.util.FloppyDiskType}
 * @author Bram Lohman
 */
public class TestFloppyDiskType {

	private Map<FloppyDiskType, File> testData;
	File refDisk;
	
    @Before
    public void setUp() {
        
    	// Note: as this is a hashmap, only one of each type can be inserted
    	// Note 2: most types commented out, because proprietary formats.
    	testData = new HashMap<FloppyDiskType, File>();
//    	testData.put(FloppyDiskType.AMG3_5_880, new File("testData/digitalObjects/Amiga/ASI001.ADF"));
    	testData.put(FloppyDiskType.FAT3_5_1440, new File("testData/FloppyDiskImage/floppy144_alphabet.img"));
//    	testData.put(FloppyDiskType.C64TAPE, new File("testData/digitalObjects/C64/arkanoid.t64"));
//    	testData.put(FloppyDiskType.C645_25_170, new File("testData/digitalObjects/C64/IKPlus.d64"));
//    	testData.put(FloppyDiskType.AMS3_180, new File("testData/digitalObjects/Amstrad/The_Rocky_Horror_Show.dsk"));
//    	testData.put(FloppyDiskType.AMSTAPE, new File("testData/digitalObjects/Amstrad/Starglider.cdt"));
    }
	
    @Test
    public void testFloppyDiskType() throws IOException {

    	for (FloppyDiskType fdt : testData.keySet())
    	{
    		refDisk = testData.get(fdt);
    		assertTrue("File does not exist: " + refDisk, refDisk.exists());
    		assertEquals("Disk not identified as " + fdt.toString(), fdt, FloppyDiskType.getDiskType(refDisk));
    	}
    }
}
