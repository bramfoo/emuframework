/*
* $Revision: 803 $ $Date: 2011-05-31 00:27:06 +0100 (Tue, 31 May 2011) $
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

package eu.keep.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * A Junit Test class for {@link eu.keep.util.FloppyDiskImage}
 * @author Bram Lohman
 */
public class TestFloppyDiskImage {

    private FloppyDiskImage fdi;
    InputStream             refInput;
    File injectedDisk;

    @Before
    public void setUp() {
        fdi = new FloppyDiskImage();

        // 1.44MB floppy disk image created using the 'EXTRACT.EXE' tool from
        // WinImage
        // Contains standard header, FAT, Root Directory table and a single text
        // file
        // 'ALPHA.TXT'
        File refDisk = new File("testData" + File.separator + "FloppyDiskImage" + File.separator
                + "floppy144_alphabet.img");
        assertTrue(refDisk.exists());
        assertTrue(refDisk.canRead());
        try {
            refInput = new FileInputStream(refDisk);
        }
        catch (FileNotFoundException e) {
            fail();
        }

        injectedDisk = new File(System.getProperty("java.io.tmpdir"), "floppyTest.img");
    }
    
    @After
    public void tearDown() {
        injectedDisk.delete();
    }


    // This method writes an image to an actual file, for manual perusal. Not intended to be run as unit test
    @Ignore
    public void manualImageTest()
    {
        try {
            File diskfile = new File("fdFile.img");
            File alphabet = new File("testData" + File.separator + "FloppyDiskImage" + File.separator + "alpha.txt");
            fdi.injectDigitalObject(diskfile, alphabet);
        }
        catch (IOException e) {
            fail();
        }
    }
    
    @Test
    public void testCreateBootSector() {
        int data;
        int[] bootSec = fdi.createBootSector();

        // Compare the generated boot sector and the reference disk
        for (int i = 0x00; i < 0x03; i++) {

            try {
                data = refInput.read();
                assertEquals("At position " + Integer.toHexString(i), Integer.toHexString(data),
                        Integer.toHexString(bootSec[i]));
            }
            catch (IOException e) {
                fail();
            }
        }

        // Skip the OEM name
        for (int i = 0x03; i < 0x0B; i++) {
            try {
                data = refInput.read();
            }
            catch (IOException e) {
                fail();
            }
        }

        for (int i = 0x0B; i < 0x24; i++) {
            try {
                data = refInput.read();
                assertEquals("At position " + Integer.toHexString(i), Integer.toHexString(data),
                        Integer.toHexString(bootSec[i]));
            }
            catch (IOException e) {
                fail();
            }
        }
    }

    @Test
    public void testCreateExtBIOSBlock() {
        int data;
        int[] biosBlock = fdi.createExtBIOSBlock();

        // Skip to section in test disk
        try {
            refInput.skip(0x24);
        }
        catch (IOException e1) {
            fail();
        }

        // Compare
        for (int i = 0x00; i < 0x03; i++) {

            try {
                data = refInput.read();
                assertEquals("At position " + Integer.toHexString(i), Integer.toHexString(data),
                        Integer.toHexString(biosBlock[i]));
            }
            catch (IOException e) {
                fail();
            }
        }

        // Skip the ID, volume label
        for (int i = 0x03; i < 0x12; i++) {
            try {
                data = refInput.read();
            }
            catch (IOException e) {
                fail();
            }
        }

        for (int i = 0x12; i < 0x1DC; i++) {
            try {
                data = refInput.read();
                assertEquals("At position " + Integer.toHexString(i), Integer.toHexString(data),
                        Integer.toHexString(biosBlock[i]));
            }
            catch (IOException e) {
                fail();
            }
        }
    }

    @Ignore
    @Test
    public void testCreateFileAllocationTable() {
        // This is tested in the 'injectDigitalObject' test
    }

    @Test
    public void testInjectDigitalObject() {
        int dataRef;
        InputStream injInput = null;
        int dataInject;
        File alphabet = new File("testData" + File.separator + "FloppyDiskImage" + File.separator + "alpha.txt");
        assertTrue(alphabet.exists());
        assertTrue(alphabet.canRead());

        try {
            fdi.injectDigitalObject(injectedDisk, alphabet);
            injInput = new FileInputStream(injectedDisk);
        }
        catch (IOException e) {
            fail();
        }
        
        if (injInput == null)
        {
            fail("Cannot open readable stream to image file under test");
        }

        // Skip to FAT
        for (int i = 0x00; i < 0x200; i++) {
            try {
                dataRef = refInput.read();
                dataInject = injInput.read();
            }
            catch (IOException e) {
                fail();
            }
        }

        // Compare disk
        for (int i = 0x200; i < 0x2616; i++) {
            try {
                dataRef = refInput.read();
                dataInject = injInput.read();
                assertEquals("At position " + Integer.toHexString(i), Integer.toHexString(dataRef),
                        Integer.toHexString(dataInject));
            }
            catch (IOException e) {
                fail();
            }
        }

        // Skip file modification date/time in Root Directory Table
        for (int i = 0x2616; i < 0x261A; i++) {
            try {
                dataRef = refInput.read();
                dataInject = injInput.read();
            }
            catch (IOException e) {
                fail();
            }
        }

        // Compare data region (rest of the disk, 0x261A - 0x168000)
        // Do this using a buffer to speed things up
            try {
                byte[] bufferRef = new byte[512];
                byte[] bufferInj = new byte[512];
                int count;
                int i = 0;
                while ((count = refInput.read(bufferRef)) > 0) {
                    injInput.read(bufferInj, 0, count);
                    assertTrue("Starting at offset 0x" + Integer.toHexString(0x261A + i*512) + " data differs", Arrays.equals(bufferRef, bufferInj));
                    i++;
                }
            }
            catch (IOException e) {
                fail();
            }

        // Byte-by-byte comparison style (slow)
//        // Compare disk
//        for (int i = 0x261A; i < 0x168000; i++) {
//            try {
//                dataRef = refInput.read();
//                dataInject = injInput.read();
//                assertEquals("At position " + Integer.toHexString(i), Integer.toHexString(dataRef),
//                        Integer.toHexString(dataInject));
//            }
//            catch (IOException e) {
//                fail();
//            }
//        }
    }
}
