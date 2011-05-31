/*
* $Revision: 604 $ $Date: 2010-11-18 15:53:37 +0100 (Thu, 18 Nov 2010) $
* $Author: DMichel $
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
import java.util.List;

/**
 * A Junit Test class for {@link eu.keep.util.DiskUtilities }
 * @author Bram Lohman
 */
public class TestDiskUtilities {

    File refDisk;

    @Before
    public void setUp() {
    	 refDisk = new File("testData/HardDiskImage/hd20MB_alphabet.img");
         assertTrue("Reference file does not exist", refDisk.exists());
    }

    @Test
    public void testDetermineCHS() throws IOException {
        List<Integer> chs = DiskUtilities.determineCHS(refDisk);
        assertEquals("Cylinders do not match", new Integer(19), chs.get(0));
        assertEquals("Heads do not match", new Integer(64), chs.get(1));
        assertEquals("Sectors do not match", new Integer(32), chs.get(2));
    }
    
    @Test
    public void testIsISO9660() throws IOException {
        assertFalse("Disk is not in ISO9660", DiskUtilities.isISO9660(refDisk));
    }
}