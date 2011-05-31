/*
* $Revision: 445 $ $Date: 2010-08-11 14:04:15 +0200 (Wed, 11 Aug 2010) $
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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.log4j.Logger;

/**
 * Utilities to generate a disk image.
 * Currently supports very simplistic disks
 * Information taken from <a href="URL#http://en.wikipedia.org/wiki/FAT_16">http://en.wikipedia.org/wiki/FAT_16</a>
 * and Linux's mkdosfs
 * @author Bram Lohman
 */
public abstract class DiskImage {

    protected Logger logger;

    // Boot sector
    protected int[]    jumpStart           = new int[]{0xEB, 0x3C, 0x90};
    protected int[]    OEMname             = new int[]{0x62, 0x72, 0x61, 0x6D, 0x66, 0x6F, 0x6F, 0x00}; // 'bramfoo'
    protected int[]    bytesPerSector      = new int[]{0x00, 0x02};
    protected int[]    sectorsPerCluster;
    protected int[]    reservedSectorCount = new int[]{0x01, 0x00};
    protected int[]    numFATs             = new int[]{0x02};
    protected int[]    numRootDirs;
    protected int[]    totalSectors1;
    protected int[]    mediaDescriptor;
    protected int[]    sectorsPerFAT;
    protected int[]    sectorsPerTrack;
    protected int[]    numHeads;
    protected int[]    numHiddenSectors    = new int[]{0x00, 0x00, 0x00, 0x00};
    protected int[]    totalSectors2       = new int[]{0x00, 0x00, 0x00, 0x00};

    // Extended BIOS parameter block
    protected int[]    driveNum            = new int[]{0x00};
    protected int[]    reserved            = new int[]{0x00};
    protected int[]    extBootSig          = new int[]{0x29};
    protected int[]    id;
    protected int[]    volumeLabel         = new int[]{0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
            0x20, 0x20, 0x20, 0x20       };
    protected int[]    FATType;
    protected int[]    OSbootcode          = new int[]{0x0E, 0x1F, 0xBE, 0x5B, 0x7C, 0xAC, 0x22,
            0xC0, 0x74, 0x0B, 0x56, 0xB4, 0x0E, 0xBB, 0x07, 0x00, 0xCD, 0x10, 0x5E, 0xEB, 0xF0,
            0x32, 0xE4, 0xCD, 0x16, 0xCD, 0x19, 0xEB, 0xFE, 0x54, 0x68, 0x69, 0x73, 0x20, 0x69,
            0x73, 0x20, 0x6E, 0x6F, 0x74, 0x20, 0x61, 0x20, 0x62, 0x6F, 0x6F, 0x74, 0x61, 0x62,
            0x6C, 0x65, 0x20, 0x64, 0x69, 0x73, 0x6B, 0x2E, 0x20, 0x20, 0x50, 0x6C, 0x65, 0x61,
            0x73, 0x65, 0x20, 0x69, 0x6E, 0x73, 0x65, 0x72, 0x74, 0x20, 0x61, 0x20, 0x62, 0x6F,
            0x6F, 0x74, 0x61, 0x62, 0x6C, 0x65, 0x20, 0x66, 0x6C, 0x6F, 0x70, 0x70, 0x79, 0x20,
            0x61, 0x6E, 0x64, 0x0D, 0x0A, 0x70, 0x72, 0x65, 0x73, 0x73, 0x20, 0x61, 0x6E, 0x79,
            0x20, 0x6B, 0x65, 0x79, 0x20, 0x74, 0x6F, 0x20, 0x74, 0x72, 0x79, 0x20, 0x61, 0x67,
            0x61, 0x69, 0x6E, 0x20, 0x2E, 0x2E, 0x2E, 0x20, 0x0D, 0x0A, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    protected int[]    bootSectorSig       = new int[]{0x55, 0xAA};

    protected int maxDigObjLength;

    /**
     * Creates a standard boot sector (of size 0x24)
     * @return int[] containing the standard boot sector
     */
    protected int[] createBootSector() {
        int[] bootSector = new int[0x24];
        Arrays.fill(bootSector, 0x00);

        // Create the boot sector
        System.arraycopy(jumpStart, 0x00, bootSector, 0x00, 3);
        System.arraycopy(OEMname, 0x00, bootSector, 0x03, 8);
        System.arraycopy(bytesPerSector, 0x00, bootSector, 0x0B, 2);
        System.arraycopy(sectorsPerCluster, 0x00, bootSector, 0x0D, 1);
        System.arraycopy(reservedSectorCount, 0x00, bootSector, 0x0E, 2);
        System.arraycopy(numFATs, 0x00, bootSector, 0x10, 1);
        System.arraycopy(numRootDirs, 0x00, bootSector, 0x11, 2);
        System.arraycopy(totalSectors1, 0x00, bootSector, 0x13, 2);
        System.arraycopy(mediaDescriptor, 0x00, bootSector, 0x15, 1);
        System.arraycopy(sectorsPerFAT, 0x00, bootSector, 0x16, 2);
        System.arraycopy(sectorsPerTrack, 0x00, bootSector, 0x18, 2);
        System.arraycopy(numHeads, 0x00, bootSector, 0x1A, 2);
        System.arraycopy(numHiddenSectors, 0x00, bootSector, 0x1C, 4);
        System.arraycopy(totalSectors2, 0x00, bootSector, 0x20, 4);

        return bootSector;
    }

    /**
     * Creates a standard Extended BIOS Parameter Block (of size 0x1DC)
     * @return an array containing the standard Extended BIOS Parameter Block
     */
    protected int[] createExtBIOSBlock() {
        int[] extBIOSBlock = new int[0x1DC];
        Arrays.fill(extBIOSBlock, 0x00);

        // Create the Extended BIOS parameter block
        // NOTE: The offsets given here subtract an offset of 0x24 (size of boot sector) compared to
        // reference values
        System.arraycopy(driveNum, 0x00, extBIOSBlock, 0x00, 1);
        System.arraycopy(reserved, 0x00, extBIOSBlock, 0x01, 1);
        System.arraycopy(extBootSig, 0x00, extBIOSBlock, 0x02, 1);
        System.arraycopy(id, 0x00, extBIOSBlock, 0x03, 4);
        System.arraycopy(volumeLabel, 0x00, extBIOSBlock, 0x07, 11);
        System.arraycopy(FATType, 0x00, extBIOSBlock, 0x12, 8);
        System.arraycopy(OSbootcode, 0x00, extBIOSBlock, 0x1A, 448);
        System.arraycopy(bootSectorSig, 0x00, extBIOSBlock, 0x1DA, 2);

        return extBIOSBlock;
    }

    /**
     * Creates a standard File Allocation Table (of approx. size 0x1200, but
     * this may be variable)
     * @param header array of int representing the FAT header
     * @return an array containing the standard File Allocation Table
     */
    protected int[] createFileAllocationTable(int[] header) {
        // Calculate the FAT size
        int sectorBytes = hexToDecTwoByte(bytesPerSector);
        int fatSectors = hexToDecTwoByte(sectorsPerFAT);
        logger.debug("FAT size calculated at: 0x" + Integer.toHexString(sectorBytes * fatSectors));
        int[] fat = new int[sectorBytes * fatSectors];
        Arrays.fill(fat, 0x00);

        // Copy the FAT header at the start (contains media type and filler)
        System.arraycopy(header, 0x00, fat, 0x00, header.length);

        return fat;
    }

    /**
     * Updates the Root Directory table with a 32-byte entry consisting of the
     * file's metadata
     * @param digObjDisk A file representing a floppy disk
     * @param digObj The file whose metadata is to be added to the floppy disk's
     *            Root Directory
     * @param offset The start of the Root Directory
     * @throws IOException 
     */
    protected void updateRootDirectory(RandomAccessFile digObjDisk, File digObj, int offset) throws IOException
    {
    	updateRootDirectory(digObjDisk, digObj, digObj.getName(), offset);
    }

    /**
     * Updates the Root Directory table with a 32-byte entry consisting of the
     * file's metadata
     * @param digObjDisk A file representing a floppy disk
     * @param digObj The file whose metadata is to be added to the floppy disk's
     *            Root Directory
     * @param fileName The name of the file to appear on disk
     * @param offset The start of the Root Directory
     * @throws IOException 
     */
    protected void updateRootDirectory(RandomAccessFile digObjDisk, File digObj, String fileName, int offset) throws IOException {

        String paddedBasename;
        String paddedExtname;
        String[] filename = new String[2];
        
        // File name
        // (a) Split given filename on '.' (period) character
        // (b) Pick a basename and extension if the split results in more/less than 2 values
        // (c) Pad with spaces (for a total of 8 and 3 characters, respectively)
        // (d) Convert characters to upper case
        String[] splitFilename = fileName.split("[.]"); // (a)
        
        // (b)
        if (splitFilename.length < 1)
        {
            // No proper filename was given, so we'll choose one
            filename[0] = "default";
            filename[1] = "ext";
        }
        else if (splitFilename.length == 1)
        {
            // No extension was given
            filename[0] = splitFilename[0];
            filename[1] = "";
        }
        else if (splitFilename.length == 2)
        {
            filename[0] = splitFilename[0];
            filename[1] = splitFilename[1];
        }
        else 
        {
            // Multiple periods encountered; we'll just pick the last two as filename/ext
            filename[0] = splitFilename[splitFilename.length - 2];
            filename[1] = splitFilename[splitFilename.length - 1];
        }
            
        paddedBasename = String.format("%1$-8.8s", filename[0]).toUpperCase(); // (c),
                                                                                    // (d)
        int[] padBasenameArray = new int[paddedBasename.length()];
        for (int i = 0; i < paddedBasename.length(); i++)
            padBasenameArray[i] = paddedBasename.charAt(i);
        paddedExtname = String.format("%1$-3.3s", filename[1]).toUpperCase(); // (c),
                                                                                   // (d)
        int[] padExtnameArray = new int[paddedExtname.length()];
        for (int i = 0; i < paddedExtname.length(); i++)
            padExtnameArray[i] = paddedExtname.charAt(i);

        digObjDisk.seek(offset);
        for (int i = 0; i < padBasenameArray.length; i++)
        {
            // 0x00 - 0x07
            digObjDisk.writeByte(padBasenameArray[i]);
        }
        for (int i = 0; i < padExtnameArray.length; i++)
        {
            // 0x08 - 0x0A
            digObjDisk.writeByte(padExtnameArray[i]);
        }

        // File attributes - no bits set
        digObjDisk.writeByte(0x00); // 0x0B

        // Reserved
        digObjDisk.writeByte(0x00); // 0x0C

        // Creation time and date - set to 0
        digObjDisk.writeByte(0x00); // 0x0D
        digObjDisk.writeByte(0x00); // 0x0E
        digObjDisk.writeByte(0x00); // 0x0F
        digObjDisk.writeByte(0x00); // 0x10
        digObjDisk.writeByte(0x00); // 0x11

        // Access date and EA-Index - set to 0
        digObjDisk.writeByte(0x00); // 0x12
        digObjDisk.writeByte(0x00); // 0x13
        digObjDisk.writeByte(0x00); // 0x14
        digObjDisk.writeByte(0x00); // 0x15

        int[] digObjModTime = lastModifiedDateTime(digObj.lastModified());
        for (int i = 0; i < digObjModTime.length; i++)
        {
            digObjDisk.writeByte(digObjModTime[i]);
        }
        
        // First cluster - this will always be 02 as this is the only file
        digObjDisk.writeByte(0x02); // 0x1A
        digObjDisk.writeByte(0x00); // 0x1B

        // File size (little-endian)
        int[] fileSize = decToHexFourByte((int)digObj.length());
        digObjDisk.write(fileSize[0]);	// 0x1C
        digObjDisk.write(fileSize[1]);  // 0x1D
        digObjDisk.write(fileSize[2]);  // 0x1E
        digObjDisk.write(fileSize[3]);  // 0x1f
    }

    /**
     * Raw cast to byte
     * @param intArray
     * @return byte[] 
     */
    protected byte[] intArrayToByteArray(int[] intArray)
    {
        byte[] byteArray = new byte[intArray.length];
        for (int i = 0; i < intArray.length; i++)
        {
            byteArray[i] = (byte) intArray[i];
        }
        return byteArray;        
    }
    
    /**
     * Generates the Last Modified Date/Time specific encoding for FAT systems
     * @param time Time to encode
     * @return Integer array representing the give time in the FAT-specific encoding
     */
    protected int[] lastModifiedDateTime(long time)
    {
        int[] modifiedDateTime = new int[4];

        // Last modified time (written in little-endian)
        Calendar modDate = Calendar.getInstance();
        modDate.clear();
        modDate.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.debug("Using file's 'last modified' time/date stamp of: "
                + sdf.format(modDate.getTime()));
        int binTime = (modDate.get(Calendar.HOUR_OF_DAY)) << 11;
        binTime += (modDate.get(Calendar.MINUTE)) << 5;
        binTime += modDate.get(Calendar.SECOND) / 2;
        modifiedDateTime[0] = binTime & 0xFF;
        modifiedDateTime[1] = ((binTime & 0xFF00) >> 8);

        // Last modified date (written in little-endian)
        int binDate = (modDate.get(Calendar.YEAR) - 1980) << 9;
        binDate += (modDate.get(Calendar.MONTH) + 1) << 5;
        binDate += modDate.get(Calendar.DAY_OF_MONTH);
        modifiedDateTime[2] = binDate & 0xFF;
        modifiedDateTime[3] = ((binDate & 0xFF00) >> 8);

        return modifiedDateTime;
    }

    /**
     * Convert a decimal to a two-byte array
     */
    protected int[] decToHexTwoByte(int dec) {
        int[] result = new int[2];
        result[1] = (dec & 0xFF00) >> 8;
        result[0] = dec & 0xFF;
        return result;          
    }

    /**
     * Convert a hexadecimal two-byte array to a decimal value
     */
    protected int hexToDecTwoByte(int[] hex) {
        return (hex[1] << 8) + hex[0];
    }

    /**
     * Convert a decimal to a four-byte array
     */
    protected int[] decToHexFourByte(int dec) {
        int[] result = new int[4];
        result[3] = (dec & 0xFF000000) >> 24;
        result[2] = (dec & 0xFF0000) >> 16;
        result[1] = (dec & 0xFF00) >> 8;
        result[0] = dec & 0xFF;
        return result;          
    }

    /**
     * Convert a hexadecimal four-byte array to a decimal value
     */    
    protected int hexToDecFourByte(int[] hex) {
        return (hex[3] << 24) + (hex[2] << 16) + (hex[1] << 8) + hex[0];
    }

    /**
     * Creates a standard empty disk
     * Generates the boot sector, Extended BIOS Parameter Block, two FATs, and
     * sets the Volume Label to 'EMPTYDISK'
     * @param diskFile Handle to the target disk file
     * @return a File consisting of an empty disk of specific size
     * @throws IOException 
     */
    public abstract RandomAccessFile createEmptyDisk(File diskFile) throws IOException;

    /**
     * Creates a standard disk containing the specified file
     * Generates the boot sector, Extended BIOS Parameter Block, two FATs,
     * then inject the specified file into the Data Region, updating the FATs
     * and the Root Directory Table, and sets the Volume Label to 'ATOMIC_DO'
     * @param diskFile file representing the disk image
     * @param digObj file representing the digital object
     * @return a File consisting of an disk of specific size containing the
     *         specified file
     * @throws IOException
     */
    public abstract RandomAccessFile injectDigitalObject(File diskFile, File digObj) throws IOException;
}
