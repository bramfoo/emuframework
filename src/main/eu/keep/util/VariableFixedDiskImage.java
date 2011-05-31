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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Timer;

import org.apache.log4j.Logger;

/**
 * Utilities to generate a hard disk image.
 * Currently supports very simplistic unpartitioned MB FAT16 fixed disks up to 128MB
 * References: http://www.win.tue.nl/~aeb/linux/fs/fat/fat-1.html
 * @author Bram Lohman
 */
public class VariableFixedDiskImage extends DiskImage {

    private final int BUFFER_SIZE = 8192;        // Assuming large files

    // Hack: global variables that may be changed anywhere (naughty, naughty) to be used by multiple methods 
    private int part1start;
    private int part1fat1Location;
    private int part1fatLength;
    private int part1RootDirLoc;
    private int part1DataRegionLoc;
    
    /**
     * Constructor
     */
    public VariableFixedDiskImage() {
        logger = Logger.getLogger(this.getClass());
        
        // These are defaults for disks < 128MB
        sectorsPerCluster   = new int[]{04};
        numRootDirs         = new int[]{0x00, 0x02};
        mediaDescriptor     = new int[]{0xF8};  // Fixed disk
        sectorsPerTrack     = new int[]{0x20, 0x00};
        numHeads            = new int[]{0x40, 0x00};
        id                  = new int[]{0xE3, 0xF7, 0xBB, 0x6D};
        FATType             = new int[]{0x46, 0x41, 0x54, 0x31, 0x36, 0x20, 0x20, 0x20};        // 'FAT16   '
    }

    /**
     * Set the remaining parameters of the boot sector
     * @param dTotalSectors Total sectors (in decimal)
     */
    protected void setParams(int dTotalSectors) {
    	logger.debug("Total sectors (dec): " + dTotalSectors);
    	
    	if (dTotalSectors < 65535)
    	{
    		logger.debug("Setting total sectors in param 1");
	        totalSectors1 = decToHexTwoByte(dTotalSectors);
	        totalSectors2 = new int[]{0x00, 0x00, 0x00, 0x00};
    	}
    	else
    	{
    		logger.debug("Setting total sectors in param 2 (setting param 1 to [0x00, 0x00]");
    		totalSectors1 = new int[]{0x00, 0x00};
    		totalSectors2 = decToHexFourByte(dTotalSectors);
    	}
    	
    	// Given the disk consists of a boot sector (1), 2 FATs (variable),
    	// Root Directory (usually 32 sectors) and a Data Region (variable),
    	// the sectorsPerFAT can be calculated using the following:
    	// (a) 2F + D = dTotalSectors - (1 + 32) (where D is also modular in the sectorsPerCluster, e.g. 4)
    	// (b) F * 256 * sectorsPerCluster < D (where 256 is in FAT16 (2 bytes/entry) the number  
    	//                                      of cluster entries it can hold in one FAT sector) 
    	// so  F = ceil((dTotalSectors - 33)/(256*sectorsPerCluster + 2))
    	// and D = (dTotalSectors - 33) -2F
        
    	int dSpF = (int) Math.ceil( ((double) dTotalSectors - 33)/((double)256 * sectorsPerCluster[0] + 2) );
    	logger.debug("sectors per FAT (dec): " + dSpF);
        sectorsPerFAT = decToHexTwoByte(dSpF);
        
        maxDigObjLength = (dTotalSectors - 33 - 2 * hexToDecTwoByte(sectorsPerFAT)) * 512;
        logger.debug("Max. data length: " + maxDigObjLength);
    }

    /**
     * Creates an empty 20MB x86 fixed disk
     * Generates the boot sector, Extended BIOS Parameter Block, two FATs, and
     * sets the Volume Label to 'EMPTYDISK'
     * @param diskFile The target file location
     * @return a file containing an empty 20MB x86 fixed disk
     * @throws IOException If an I/O error occurs
     */
    public RandomAccessFile createEmptyDisk(File diskFile) throws IOException {
    	return createEmptyDisk(diskFile, 20);
    }

    /**
     * Creates an empty x86 fixed disk of approximately the given size
     * Generates the boot sector, Extended BIOS Parameter Block, two FATs, and
     * sets the Volume Label to 'EMPTYDISK'
     * @param diskFile The target file location
     * @param approxSizeInMB The approximate size, given in megabytes (MB). 1MB = 1024 bytes.
     * @return a file containing an empty x86 fixed disk of given size
     * @throws IOException If an I/O error occurs
     */
    public RandomAccessFile createEmptyDisk(File diskFile, int approxSizeInMB) throws IOException {

    	logger.debug("Using size (MB): " + approxSizeInMB);
    	if (approxSizeInMB < 16 || approxSizeInMB > 128)
    		throw new IllegalArgumentException("Size must be between 16MB and 128MB");

        logger.debug("Creating empty " + approxSizeInMB + "MB disk");

    	logger.debug("Setting disk parameters...");
    	int dTotalSectors = 2 * approxSizeInMB * 1000; // 2 sectors of 512 bytes = 1 MB, 1000 kB in a MB
    	setParams(dTotalSectors);
    	
    	RandomAccessFile emptyDisk = new RandomAccessFile(diskFile, "rwd");
    	emptyDisk.setLength(approxSizeInMB * 1000 * 1024);

    	// Create MBR for disk
        logger.debug("Generating master boot record and extended BIOS block for drive");        
        int[] bootSector = createBootSector();
        int[] extBIOSBlock = createExtBIOSBlock();

        // Note: 16 heads and 63 sect/track are manufacturers defaults, see http://en.wikipedia.org/wiki/Cylinder-head-sector
        // More info:
        // http://mirror.href.com/thestarman/asm/mbr/PartTables3.htm
        // http://www.maverick-os.dk/FileSystemFormats/FAT16_FileSystem.html
        int heads = 16;
        int spt = 63;
        logger.debug("Using fixed disk defaults: heads: " + heads + "; sect/track: " + spt);
        // DOS seems to want to offset the starting sector into the first cylinder
        int sectorOffset = 63;
        int startSector = 0 + sectorOffset;
        // The last sector needs to be calculated:
        int cylSize = heads * spt;
        int numCyls = (int) Math.floor((double)(approxSizeInMB * 1000 * 1024 / 512) / (double) cylSize);
        int partSizeSectors = ((cylSize * numCyls) / 1) - sectorOffset;
        logger.debug("Cylinder size: " + cylSize + "; number of cylinders: " + numCyls + "; partitionSize: " + partSizeSectors);
        int lastSector = startSector + partSizeSectors - 1;
        
        logger.debug("Partition table generation for disk of " + approxSizeInMB + " MB: first sector: " + startSector + "; last sector: " + lastSector);
        int[] partitionTable = createPartitionTable(approxSizeInMB, startSector, lastSector, heads, spt); // Single partition, full disk

        // Set up the boot sector/BIOS block
        emptyDisk.write(intArrayToByteArray(bootSector));
        emptyDisk.write(intArrayToByteArray(extBIOSBlock));

        // Set up the partition table
        emptyDisk.seek(0x01BE);
        emptyDisk.write(intArrayToByteArray(partitionTable));
        
        // Create the disk FATs
        // Set the media type to fixed disk, then add 3 bytes filler for FAT16
        int[] header = new int[]{0xF8, 0xFF, 0xFF, 0xFF}; // F8 -> fixed disk; mandatory FF values

        for (int j = 0; j < numFATs[0]; j++) {
            int[] fat = createFileAllocationTable(header);
            emptyDisk.seek(0x200 + j * fat.length);
            emptyDisk.write(intArrayToByteArray(fat));
        }

        // Create the partition boot sector
        // This has different values from the MBR
        logger.debug("Setting partition parameters...");
        setParams(partSizeSectors);
        // mkdosfs seems to repeat the default heads, spt values of 0x20, 0x40
//        sectorsPerTrack = decToHexTwoByte(spt);
//        numHeads = decToHexTwoByte(heads);
        numHiddenSectors = new int[]{0x00, 0x00, 0x00, 0x00};
        driveNum = new int[]{0x80};
        id = new int[]{0xE3, 0xF7, 0xBB, 0x6E};
        volumeLabel = new int[]{0x50, 0x41, 0x52, 0x54, 0x49, 0x54, 0x49, 0x4F, 0x4E, 0x31, 0x20};

        logger.debug("Generating boot sector and extended BIOS block for partition 1");
        bootSector = createBootSector();
        extBIOSBlock = createExtBIOSBlock();

        emptyDisk.seek(startSector * 512);
        emptyDisk.write(intArrayToByteArray(bootSector));
        emptyDisk.write(intArrayToByteArray(extBIOSBlock));

        logger.debug("Writing FAT(s) in partition table");
        for (int j = 0; j < numFATs[0]; j++) {
            int[] fat = createFileAllocationTable(header);
            part1fatLength = fat.length;     // Needed for d.o. injection
            emptyDisk.seek((startSector + 1) * 512 +  j * fat.length);
            emptyDisk.write(intArrayToByteArray(fat));
        }
        part1start = startSector * 512;
        part1fat1Location = part1start + (hexToDecTwoByte(reservedSectorCount)) * 512; // Needed for d.o. injection
        part1RootDirLoc = part1fat1Location + (2*hexToDecTwoByte(sectorsPerFAT)) * 512;
        part1DataRegionLoc = part1RootDirLoc + hexToDecTwoByte(numRootDirs) * 32; 
        logger.debug("Partition addresses: FAT1 [0x" + Integer.toHexString(part1fat1Location) + "]; Root Dir: [0x" + Integer.toHexString(part1RootDirLoc) + "]; Data region: [0x" + Integer.toHexString(part1DataRegionLoc) + "]");
        
        // Change the volume label to 'EMPTYDISK'
        logger.debug("Changing disk volume label");
        int[] labelEmpty = new int[]{0x45, 0x4D, 0x50, 0x54, 0x59, 0x44, 0x49, 0x53, 0x4B, 0x20, 0x20};
        emptyDisk.seek(startSector * 512 + 0x2B);
        for (int i = 0; i < labelEmpty.length; i++)
        {
            emptyDisk.writeByte(labelEmpty[i]);
        }

        emptyDisk.close();
        logger.debug("Empty " + approxSizeInMB + "MB disk ready");
        return emptyDisk;
    }

    /**
     * Creates a 20MB x86 fixed disk containing the specified file
     * Generates the boot sector, Extended BIOS Parameter Block, two FATs,
     * then inject the specified file into the Data Region, updating the FATs
     * and the
     * Root Directory Table, and sets the Volume Label to 'ATOMIC_DO'
     * @param diskFile The target file location
     * @param digObj file representing the digital object
     * @return a file containing an 20MB x86 fixed disk containing the
     *         specified file
     * @throws IOException If an I/O error occurs
     */
    public RandomAccessFile injectDigitalObject(File diskFile, File digObj) throws IOException {
    	return injectDigitalObject(diskFile, digObj, 20);
    }

    
    /**
     * Creates a x86 fixed disk of approximately the given size containing the specified file
     * Generates the boot sector, Extended BIOS Parameter Block, two FATs,
     * then inject the specified file into the Data Region, updating the FATs
     * and the
     * Root Directory Table, and sets the Volume Label to 'ATOMIC_DO'
     * @param diskFile The target file location
     * @param digObj file representing the digital object
     * @param approxSizeInMB The approximate size, given in megabytes (MB). 1MB = 1024 bytes.
     * @return a file containing a x86 fixed disk of approximately the given size containing the
     *         specified file
     * @throws IOException If an I/O error occurs
     */
    public RandomAccessFile injectDigitalObject(File diskFile, File digObj, int approxSizeInMB) throws IOException {

    	if (approxSizeInMB < 16 || approxSizeInMB > 128)
    		throw new IllegalArgumentException("Size must be between 16MB and 128MB");
    	
        // Get the file as a byte stream
        InputStream is = new FileInputStream(digObj);

        // Check file size; maximum allowed size depends on disk size, make an approximation here
        maxDigObjLength = (2 * approxSizeInMB * 1000 - 256) * 512; // Random number (256) sectors reserved
        long length = digObj.length();

        
        if (length > maxDigObjLength) {
            logger.error("Digital object (" + length + ") is larger than disk size (" + maxDigObjLength + ") allows");
            throw new IOException("Digital object (" + length + ") is larger than disk size (" + maxDigObjLength + ") allows");
        }

        logger.warn("Wrapping selected digital object in disk, please be patient...");
        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, "...");

        // Start with an empty disk
        createEmptyDisk(diskFile, approxSizeInMB);
        RandomAccessFile digObjDisk = new RandomAccessFile(diskFile, "rwd");

        // Inject the digital object into the disk --this is an extremely simple
        // case as we know we have an empty disk available, so we only need the
        // data region location and FAT table location(s)
        // Read in the bytes
        logger.debug("Injecting file into data region, located at 0x" + Integer.toHexString(part1DataRegionLoc));

        // Use a buffer to speed things up
        digObjDisk.seek(part1DataRegionLoc);
        byte[] buffer = new byte[BUFFER_SIZE];
        int count;
        while ((count = is.read(buffer)) > 0) {
            digObjDisk.write(buffer, 0, count);
        }

        // Close the input stream
        is.close();

        // Update the FAT: determine the number of clusters used, write these as
        // 'used'
        int intBytesPerSector = hexToDecTwoByte(bytesPerSector);
        int intSectorsPerCluster = sectorsPerCluster[0];
        
        int numClusters = (int) Math.ceil((double) length / (intBytesPerSector *  intSectorsPerCluster)) ;
        logger.debug("Updating FAT with " + numClusters + " clusters (file length/bytes per cluster: "
                + length + "/" + intBytesPerSector * intSectorsPerCluster);

        logger.debug("Updating partition 1 FAT table(s), located at 0x" + Integer.toHexString(part1fat1Location));
        digObjDisk.seek(part1fat1Location + 0x4); // First byte after F8 FF FF FF in FAT 1 in partition 1
        int i;
        int[] bytes = new int[2];
        // Cluster 1 does not exist; cluster 2 will always point to 3 unless it
        // contains 0xFFFF, so start at 3 and end at the second-to-last one (so numClust +2, not +3)
        // as this will be set manually to 0xFFFF below
        for (i = 3; i < numClusters + 2; i++) {
            bytes = fat16cluster(i);
            for (int j = 0; j < 2; j++)
            {
                digObjDisk.writeByte(bytes[j]);
            }
        }
        
        logger.debug("Clusters remaining: " + (numClusters - i + 3));
        // Last cluster should end with 0xFFFF to mark end of file
        bytes = new int[]{0xFF, 0xFF};
        for (int j = 0; j < 2; j++) {
            digObjDisk.writeByte(bytes[j]);
        }
        
        // Copy FAT 1 over FAT 2 in partition 1
        byte[] fat1 = new byte[part1fatLength]; 
        digObjDisk.seek(part1fat1Location);
        digObjDisk.read(fat1);
        digObjDisk.seek(part1fat1Location + part1fatLength);
        digObjDisk.write(fat1);

        // Write the file into the root directory
        // Note: MS-DOS (and other filesystems??) write partition label info plus time/date here as well
        logger.debug("Updating Root Directory, located at 0x" + Integer.toHexString(part1RootDirLoc));
        int[] labelAtomic = new int[]{0x41, 0x54, 0x4F, 0x4D, 0x49, 0x43, 0x5F, 0x44, 0x4F, 0x20, 0x20};
        digObjDisk.seek(part1RootDirLoc);
        for (int j = 0; j < labelAtomic.length; j++)
        {
            digObjDisk.writeByte(labelAtomic[j]);
        }
        int[] attrib = new int[]{0x28}; // Volume, archive
        for (int j = 0; j < attrib.length; j++)
        {
            digObjDisk.writeByte(attrib[j]);
        }
        int[] digObjModTime = lastModifiedDateTime(System.currentTimeMillis());
        digObjDisk.seek(part1RootDirLoc + 0x16);
        for (int j = 0; j < digObjModTime.length; j++)
        {
            digObjDisk.writeByte(digObjModTime[j]);
        }
        // Add file info
        updateRootDirectory(digObjDisk, digObj, part1RootDirLoc + 0x20);

        // Change the volume label to 'ATOMIC_DO'
        logger.debug("Changing partition volume label");
        digObjDisk.seek(part1start + 0x2B);
        for (int j = 0; j < labelAtomic.length; j++)
        {
            digObjDisk.writeByte(labelAtomic[j]);
        }

        // Stop the timer
        t.cancel();
        logger.warn("Wrapping complete!");
        digObjDisk.close();
        return digObjDisk;
    }

    /**
     * This function transforms a single cluster reference into a little-endian
     * two-byte sequence, e.g. cluster 3 is returned as {03, 00} and cluster 30000
     * is returned as {30, 75} 
     * 
     * @param cluster number of clusters
     * @return int[] Array containing the clusters in FAT16 encoding
     */
    protected int[] fat16cluster(int cluster) {
        int[] result = new int[2];

        // Divide into high and low byte
        int highByte = (cluster & 0xFF00) >> 8;
        int lowByte = cluster & 0x00FF;
        
        // Little endian
        result[0] = lowByte;
        result[1] = highByte;

        return result;
    }
    
    /**
     * Create a partition table for a FAT16 disk
     * Information taken from http://en.wikipedia.org/wiki/Master_boot_record
     * @param sizeMB Size of partition
     * @param firstSector First sector of partition (in LBA format)
     * @param lastSector Last sector of partition (in LBA format)
     * @param numHeads Number of heads of the partition
     * @param sectPTrack Sectors per track
     * @return int[] Array containing the hexadecimal partition table information, generated using the given geometry
     */
    protected int[] createPartitionTable(int sizeMB, int firstSector, int lastSector, int numHeads, int sectPTrack) {

        int[] partTable = new int[64];
    	int[] partType;
        int[] bootStatus          = new int[]{0x00};     // non-bootable
        int[] CHSaddressFirstSect;
        int[] CHSaddressLastSect;
        int[] LBAFirstSect;
        int[] numSect;
        
    	logger.debug("Creating partition table; first/last sector, size (MB): " + firstSector + "/" + lastSector + ", " + sizeMB);
    	logger.debug("Heads: " + numHeads + "; sectors/track: " + sectPTrack);
        
     
        CHSaddressFirstSect = LBAtoCHSpartitionTable(firstSector, numHeads, sectPTrack); // First absolute sector

        if (sizeMB < 32)
        {
        	partType            = new int[]{0x04}; // Partition type (FAT16 < 32M)
        }
        else
        {
        	partType            = new int[]{0x06}; // Partition type (FAT16 > 32M)
        }

        CHSaddressLastSect  = LBAtoCHSpartitionTable(lastSector, numHeads, sectPTrack); // Last absolute sector
        LBAFirstSect        = decToHexFourByte(firstSector); // LBA of first absolute sector
        numSect             = decToHexFourByte(lastSector - firstSector + 1); // Number of sectors
        
        System.arraycopy(bootStatus, 0x00, partTable, 0x00, 1);
        System.arraycopy(CHSaddressFirstSect, 0x00, partTable, 0x01, 3);
        System.arraycopy(partType, 0x00, partTable, 0x04, 1);
        System.arraycopy(CHSaddressLastSect, 0x00, partTable, 0x05, 3);
        System.arraycopy(LBAFirstSect, 0x00, partTable, 0x08, 4);
        System.arraycopy(numSect, 0x00, partTable, 0x0C, 4);
        
        return partTable;
    }

    /**
     * Translate the sector number (in LBA) to CHS value, using the encoding used in the partition table
     * Information taken from http://en.wikipedia.org/wiki/Master_boot_record
     * @param sectorLBA Number of sectors, in LBA
     * @param numHeads Number of heads
     * @param sectPTrack Sectors per track
     * @return int[] Array containing the hexadecimal CHS values, generated using the given geometry
     */
    public int[] LBAtoCHSpartitionTable(int sectorLBA, int numHeads, int sectPTrack)
    {
    	// This has the form hhhhhhhh ccsssss cccccccc
    	// where h=head, s=sector c=cylinder
    	int[] result = new int[3];
    	
    	int[] sectorCHS = LBAtoCHS(sectorLBA, numHeads, sectPTrack);
    	
    	// Modify bytes into bit-form above
    	result[0] = sectorCHS[1];  // head
    	result[1] = (sectorCHS[0] & 0xC0) + (sectorCHS[2] & 0x3F); // ccssssss
    	result[2] = sectorCHS[0] & 0xFF; // cylinder
    	
    	logger.debug("LBA " + sectorLBA + " == CHS (partition table format): (" + result[0] + "," + result[1] + "," + result[2] + ")");
    	return result;
    }

    /**
     * Convert the sector number (in LBA) to CHS values
     * From http://en.wikipedia.org/wiki/CHS_conversion
     * @param sectorLBA Sector number
     * @param numHeads Number of heads
     * @param sectPTrack Sectors per track
     * @return CHS values for the sector number, generated using the given geometry
     */
    public int[] LBAtoCHS(int sectorLBA, int numHeads, int sectPTrack)
    {
    	int[] sectorCHS = new int[3];
    	
    	sectorCHS[0] = sectorLBA / (numHeads * sectPTrack); // cylinder
    	sectorCHS[1] = (sectorLBA / sectPTrack) % numHeads; // head
    	sectorCHS[2] = (sectorLBA % sectPTrack) + 1; // sector
    	
    	logger.debug("LBA " + sectorLBA + " == CHS (" + sectorCHS[0] + "," + sectorCHS[1] + "," + sectorCHS[2] + ")");
    	return sectorCHS;
    }
}