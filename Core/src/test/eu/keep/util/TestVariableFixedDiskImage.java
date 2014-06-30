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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
 * A Junit Test class for {@link eu.keep.util.VariableFixedDiskImage}
 * @author Bram Lohman
 */
public class TestVariableFixedDiskImage {
    
    Logger logger;

    private VariableFixedDiskImage vfdi;
    InputStream             refInput;
    File injectedDisk;


    @Before
    public void setUp() {
        logger = Logger.getLogger(this.getClass());
        vfdi = new VariableFixedDiskImage();

        // 20MB fixed disk image created using mkdosfs and object inserted 
        // Note: EXTRACT.EXE cannot insert objects into partitioned disks
        // Contains standard header, FAT, Root Directory table and a single text file 'ALPHABIG.TXT'
        File refDisk = new File("testData" + File.separator + "HardDiskImage" + File.separator
                + "hd20MB_alphabet.img");
        assertTrue(refDisk.exists());
        assertTrue(refDisk.canRead());
        try {
            refInput = new FileInputStream(refDisk);
        }
        catch (FileNotFoundException e) {
            fail();
        }

        injectedDisk = new File(System.getProperty("java.io.tmpdir"), "varfixedTest.img");
    }

    @After
    public void tearDown() {
        injectedDisk.delete();
    }

    @Test
    public void testCreateBootSector() {
        int data;
        vfdi.setParams(40000);
        int[] bootSec = vfdi.createBootSector();

        // Compare the generated boot sector and the reference disk
        for (int i = 0x00; i < 0x03; i++) {

            try {
                data = refInput.read();
                assertEquals("At position 0x" + Integer.toHexString(i), Integer.toHexString(data),
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
                assertEquals("At position 0x" + Integer.toHexString(i), Integer.toHexString(data),
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
        vfdi.setParams(40000);
        int[] biosBlock = vfdi.createExtBIOSBlock();

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

        // Stop before partition table
        for (int i = 0x12; i < 0x19B; i++) {
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

    @Test
    public void testPartitionTable() {
        int data;
        vfdi.setParams(2 * 1000 * 20);  // 20MB reference disk
        int[] biosBlock = vfdi.createPartitionTable(20, 63, 39311, 16, 63);

        // Skip to section in test disk
        try {
            refInput.skip(0x1BE);
        }
        catch (IOException e1) {
            fail();
        }

        // Compare
        for (int i = 0x00; i < 0x40; i++) {

            try {
                data = refInput.read();
                assertEquals("At position 0x" + Integer.toHexString(i), Integer.toHexString(data),
                        Integer.toHexString(biosBlock[i]));
            }
            catch (IOException e) {
                fail();
            }
        }
    }

    // No reference data to compare with, so ignore as test
    @Ignore
    public void testCreateEmptyDisk() {
        try {
            vfdi.createEmptyDisk(new File(System.getProperty("java.io.tmpdir"), "varfixedEmptyTest.img"));
        }
        catch (IOException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testInjectDigitalObject() {
        int dataRef;
        InputStream injInput = null;
        int dataInject;
        File alphabet = new File("testData" + File.separator + "HardDiskImage" + File.separator + "alphaBig.txt");
        assertTrue(alphabet.exists());
        assertTrue(alphabet.canRead());

        try {
            vfdi.injectDigitalObject(injectedDisk, alphabet, 20);
            injInput = new FileInputStream(injectedDisk);

        }
        catch (IOException e) {
            fail(e.toString());
        }
        
        if (injInput == null)
        {
            fail("Cannot open readable stream to image file under test");
        }

        // Compare disk up to Root Directory
        for (int i = 0x0; i < 0x11C16; i++) {
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

        // Skip partition label modification date/time in Root Directory Table
        for (int i = 0x11C16; i < 0x11C1A; i++) {
            try {
                dataRef = refInput.read();
                dataInject = injInput.read();
            }
            catch (IOException e) {
                fail();
            }
        }

        // Compare disk
        for (int i = 0x11C1A; i < 0x11C36; i++) {
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
        for (int i = 0x11C36; i < 0x11C3A; i++) {
            try {
                dataRef = refInput.read();
                dataInject = injInput.read();
                }
            catch (IOException e) {
                fail();
            }
        }

        // Compare data region (rest of the disk, 0x11C3A - 0x1387FFF)
        // Do this using a buffer to speed things up
            try {
                byte[] bufferRef = new byte[8192];
                byte[] bufferInj = new byte[8192];
                int count;
                int i = 0;
                while ((count = refInput.read(bufferRef)) > 0) {
                    injInput.read(bufferInj, 0, count);
                    assertTrue("Starting at offset 0x" + Integer.toHexString(0x11C3A + i*8192) + " data differs", Arrays.equals(bufferRef, bufferInj));
                    i++;
                }

            }
            catch (IOException e) {
                fail();
            }
    }
}
