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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Timer;

import org.apache.log4j.Logger;

/**
 * Utilities to generate a floppy disk image.
 * Currently supports very simplistic 1.44 MB FAT12 floppy disks
 * @author Bram Lohman
 */
public class FloppyDiskImage extends DiskImage {
    
    public static final int MAX_FILE_SIZE = 1457664;
    private final int BUFFER_SIZE = 512;

    /**
     * Constructor
     */
    public FloppyDiskImage() {
        logger = Logger.getLogger(this.getClass());
        
        // Set defaults for this disk
        sectorsPerCluster   = new int[]{01};
        numRootDirs         = new int[]{0xE0, 0x00};
        totalSectors1       = new int[]{0x40, 0x0B};
        mediaDescriptor     = new int[]{0xF0};  // Floppy disk (1.44/2.88MB)
        sectorsPerFAT       = new int[]{0x09, 0x00};
        sectorsPerTrack     = new int[]{0x12, 0x00};
        numHeads            = new int[]{0x02, 0x00};
        id                  = new int[]{0xEF, 0xC0, 0x12, 0xE1};
        FATType             = new int[]{0x46, 0x41, 0x54, 0x31, 0x32, 0x20, 0x20, 0x20};        // 'FAT12   '
        
        maxDigObjLength = MAX_FILE_SIZE;
    }

    /**
     * Creates a standard empty 1.44MB x86 floppy disk
     * Generates the boot sector, Extended BIOS Parameter Block, two FATs, and
     * sets the Volume Label to 'EMPTYDISK'
     * @param diskFile The target file location
     * @return a File consisting of an empty 1.44MB x86 floppy disk image
     * @throws IOException if an I/O error occurs
     */
    public RandomAccessFile createEmptyDisk(File diskFile) throws IOException {
        logger.debug("Creating empty 1.44MB disk");
        RandomAccessFile emptyDisk = new RandomAccessFile(diskFile, "rwd");
        emptyDisk.setLength(0x168000);

        int[] bootSector = createBootSector();
        int[] extBIOSBlock = createExtBIOSBlock();

        // Set up the boot sector/BIOS block
        emptyDisk.write(intArrayToByteArray(bootSector));
        emptyDisk.write(intArrayToByteArray(extBIOSBlock));

        // Create the number of FATs required
        // Set the media type to floppy, then add filler (2 bytes for FAT12)
        int[] header = new int[]{0xF0, 0xFF, 0xFF}; // F0 -> Floppy; mandatory FF values

        for (int j = 0; j < numFATs[0]; j++) {
            int[] fat = createFileAllocationTable(header);
            emptyDisk.seek(0x200 + j * fat.length);
            emptyDisk.write(intArrayToByteArray(fat));
        }

        // Change the volume label to 'EMPTYDISK'
        int[] labelEmpty = new int[]{0x45, 0x4D, 0x50, 0x54, 0x59, 0x44, 0x49, 0x53, 0x4B, 0x20, 0x20};
        emptyDisk.seek(0x2B);
        for (int i = 0; i < labelEmpty.length; i++)
        {
            emptyDisk.writeByte(labelEmpty[i]);
        }
        
        emptyDisk.close();
        logger.debug("Empty 1.44MB disk ready");
        return emptyDisk;
    }

    /**
     * Creates a standard 1.44MB x86 floppy disk containing the specified file
     * Generates the boot sector, Extended BIOS Parameter Block, two FATs,
     * then inject the specified file into the Data Region, updating the FATs
     * and the
     * Root Directory Table, and sets the Volume Label to 'ATOMIC_DO'
     * @param diskFile The target file location
     * @param digObj file representing the digital object
     * @return File reference to a 1.44MB x86 floppy disk containing the
     *         specified file
     * @throws IOException if an I/O error occurs
     */
    public RandomAccessFile injectDigitalObject(File diskFile, File digObj) throws IOException {
    	return injectDigitalObject(diskFile, digObj, digObj.getName());
    }
    
    /**
     * Creates a standard 1.44MB x86 floppy disk containing the specified file
     * Generates the boot sector, Extended BIOS Parameter Block, two FATs,
     * then inject the specified file into the Data Region, updating the FATs
     * and the
     * Root Directory Table, and sets the Volume Label to 'ATOMIC_DO'
     * @param diskFile The target file location
     * @param digObj file representing the digital object
     * @param fileName Filename to be created on disk
     * @return File reference to a 1.44MB x86 floppy disk containing the
     *         specified file, with the specified filename
     * @throws IOException if an I/O error occurs
     */
    public RandomAccessFile injectDigitalObject(File diskFile, File digObj, String fileName) throws IOException {

        // Get the file as a byte stream
        InputStream is = new FileInputStream(digObj);

        // Check file size; maximum allowed size is 1457664d (0x168000 max disk
        // bytes - 0x4200 reserved bytes)
        long length = digObj.length();

        if (length > maxDigObjLength) {
            logger.error("Digital object (" + length + ") is larger than disk size (" + maxDigObjLength + ") allows");
            throw new IOException("Digital object is larger than disk size allows");
        }

        logger.info("Wrapping selected digital object in disk, please be patient...");
        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, "...");

        // Start with an empty disk
        createEmptyDisk(diskFile);
        RandomAccessFile digObjDisk = new RandomAccessFile(diskFile, "rwd");

        // Inject the digital object into the disk --this is an extremely simple
        // case as we know we have an empty disk available, so don't need to look at
        // the FAT
        // Starting at position 0x4200 (boot sector + 2 FATs + root directory),
        // read each byte into the disk
        // Read in the bytes

        digObjDisk.seek(0x4200);
        byte[] buffer = new byte[BUFFER_SIZE];
        int count;
        while ((count = is.read(buffer)) > 0) {
            digObjDisk.write(buffer, 0, count);
        }

        // Close the input stream
        is.close();

        // Update the FAT: determine the number of clusters used, write these as
        // 'used'
        int intBytesPerSector = (bytesPerSector[1]) << 8 + bytesPerSector[0];
        int intSectorsPerCluster = sectorsPerCluster[0];

        int numClusters = (int) Math.ceil((double) length / (intBytesPerSector *  intSectorsPerCluster)) ;
        logger.debug("Updating FAT with " + numClusters + " clusters (file length/sector size: "
                + length + "/" + intBytesPerSector * intSectorsPerCluster);

        digObjDisk.seek(0x203); // First byte after F0 FF FF in FAT 1
        int i;
        int[] bytes = new int[3];
        // Cluster 1 does not exist; cluster 2 will always point to 3 unless it
        // contains 0xFFF,
        // so start at 3; do two clusters at a time to write 6 bytes
        for (i = 3; i <= numClusters - 1; i += 2) {
            bytes = fat12clustervoodoo(i, i + 1);
            for (int j = 0; j < bytes.length; j++)
            {
                digObjDisk.writeByte(bytes[j]);
            }
        }

        logger.debug("Cluster remaining: " + (numClusters - i));
        // Last three bytes should end with 0xFFF
        if (i % 2 == 0) {
            bytes = fat12clustervoodoo(0x000, 0xFFF);
            for (int j = 0; j < bytes.length; j++)
            {
                digObjDisk.writeByte(bytes[j]);
            }
        }
        else {
            bytes = fat12clustervoodoo(i, 0xFFF);
            for (int j = 0; j < bytes.length; j++)
            {
                digObjDisk.writeByte(bytes[j]);
            }
        }

        // Copy FAT 1 over FAT 2
        int[] fat1 = new int[0x1200]; 
        digObjDisk.seek(0x200);
        for (int j = 0; j < fat1.length; j++)
        {
            fat1[j] = digObjDisk.readUnsignedByte();
        }
        digObjDisk.seek(0x1400);
        for (int j = 0; j < fat1.length; j++)
        {
            digObjDisk.writeByte(fat1[j]);
        }
        
        // Write the file into the root directory
        updateRootDirectory(digObjDisk, digObj, fileName, 0x2600);

        // Change the volume label to 'ATOMIC_DO'
        int[] labelAtomic = new int[]{0x41, 0x54, 0x4F, 0x4D, 0x49, 0x43, 0x5F, 0x44, 0x4F, 0x20, 0x20};
        digObjDisk.seek(0x2B);
        for (int j = 0; j < labelAtomic.length; j++)
        {
            digObjDisk.writeByte(labelAtomic[j]);
        }

        // Stop the timer
        t.cancel();
        logger.info("Wrapping complete!");

        digObjDisk.close();
        return digObjDisk;
    }

    /**
     * This function performs the voodoo to transform two cluster references
     * into a little-endian three-byte sequence, e.g. cluster 3 and 4 are
     * returned as
     * {03,40,00}, as is written into the FAT
     * @param a Cluster value
     * @param b Cluster value
     * @return Integer array containing clusters in FAT encoding
     */
    private int[] fat12clustervoodoo(int a, int b) {
        int[] result = new int[3];

        // Create zero-padded string from clusters
        String padCluster1 = String.format("%03x", a);
        String padCluster2 = String.format("%03x", b);

        // The strings are split into 3 bytes according to the following
        // example:
        // {uv, wx, yz} <- (xuv),(yzw)
        String byte1 = padCluster1.substring(1); // Last two digits of cluster1
        String byte2 = padCluster2.substring(2) + padCluster1.substring(0, 1); // Last
                                                                               // digit
                                                                               // of
                                                                               // cluster2
                                                                               // and
                                                                               // first
                                                                               // digit
                                                                               // of
                                                                               // cluster1
        String byte3 = padCluster2.substring(0, 2); // First two digits of
                                                    // cluster2

        // Convert the Strings to int[]
        result[0] = Integer.parseInt(byte1, 16);
        result[1] = Integer.parseInt(byte2, 16);
        result[2] = Integer.parseInt(byte3, 16);

        return result;
    }
}