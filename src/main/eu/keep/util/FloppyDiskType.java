package eu.keep.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Enumeration of the different floppy disk types supported. Each floppy disk
 * has as parameters the FileSystem (e.g. Amiga, C64, FAT), the media (e.g. 3.5" disk,
 * 5.25" disk), and the size of the media (e.g. 720KB, 1.44 MB), each of which is an
 * enumeration as well.
 * @author Bram Lohman
 *
 */
public enum FloppyDiskType {

	AMG3_5_880(FileSystem.AMIGA, Media.DISK3_5, Size.KB880),
	AMG3_5_1760(FileSystem.AMIGA, Media.DISK3_5, Size.KB1760),
	AMS3_180(FileSystem.AMSTRAD, Media.DISK3, Size.KB180),
	AMSTAPE(FileSystem.AMSTRAD, Media.TAPE, null),
	BBC_XX_XX(FileSystem.BBCMICRO, Media.DISK3, Size.KB200), // FIXME: Details (media) to be sorted out
	C64TAPE(FileSystem.C64, Media.TAPE, null),
	C645_25_170(FileSystem.C64, Media.DISK5_25, Size.KB170),
	C645_25_340(FileSystem.C64, Media.DISK5_25, Size.KB340),
	FAT3_5_640(FileSystem.FAT, Media.DISK3_5, Size.KB640),
	FAT3_5_720(FileSystem.FAT, Media.DISK3_5, Size.KB720),
	FAT3_5_1440(FileSystem.FAT, Media.DISK3_5, Size.KB1440),
	FAT3_5_2880(FileSystem.FAT, Media.DISK3_5, Size.KB2880),
	FAT5_25_160(FileSystem.FAT, Media.DISK5_25, Size.KB160),
	FAT5_25_180(FileSystem.FAT, Media.DISK5_25, Size.KB180),
	FAT5_25_320(FileSystem.FAT, Media.DISK5_25, Size.KB320),
	FAT5_25_360(FileSystem.FAT, Media.DISK5_25, Size.KB360),
	FAT5_25_1200(FileSystem.FAT, Media.DISK5_25, Size.KB1200),
	THOMSON_CART_XX(FileSystem.THOMSON, Media.CARTRIDGE, Size.KB16),
	THOMSON_DISK_XX(FileSystem.THOMSON, Media.DISK3, Size.KB80), // FIXME: check media
	THOMSON_TAPE_XX(FileSystem.THOMSON, Media.TAPE, Size.KB16), // FIXME: check size, seems variable
	UNDEFINED(null, null, null);

    private enum FileSystem {AMIGA, AMSTRAD, BBCMICRO, C64, FAT, THOMSON}
    private enum Media {CARTRIDGE, DISK3, DISK3_5, DISK5_25, TAPE};
    private enum Size {KB16, KB80, KB160, KB170, KB180, KB200, KB320, KB340, KB360, KB640, KB720, KB880, KB1200, KB1440, KB1760, KB2880}

	private static final Logger logger = Logger.getLogger("eu.keep.util.DiskType");

    public final FileSystem fs;
    public final Media media;
    public final Size size;

	private static byte[]    signature = new byte[14];	// Size of longest signature (see below)
	private static byte[]    signature2 = new byte[14];	// Size of longest signature (see below)

	// These signatures are located at the start of the image, unless specifically stated otherwise
	private static byte[] FATsignature = new byte[]{(byte) 0xEB, 0x3C, (byte) 0x90}; // Jump instruction
	private static byte[] AMDsignature = new byte[]{0x44, 0x4F, 0x53}; // 'DOS'
	private static byte[] DSKsignature = new byte[]{0x4D, 0x56, 0x20, 0x2D, 0x20, 0x43, 0x50, 0x43, 0x45, 0x4D, 0x55}; // 'MV - CPCEMU'
	private static byte[] CDTsignature = new byte[]{0x5A, 0x58, 0x54, 0x61, 0x70, 0x65, 0x21, 0x1A}; // 'ZXTape!'
	private static byte[] T64signature = new byte[]{0x43, 0x36, 0x34, 0x53, 0x20, 0x74, 0x61, 0x70, 0x65, 0x20, 0x66, 0x69, 0x6C, 0x65}; // 'C64 tape file'
	private static byte[] C64signature1 = new byte[]{0x12, 0x01, 0x41}; // C64 directory track (track 18/0), at 0x16500
	private static byte[] C64signature2 = new byte[]{(byte) 0xA0, (byte) 0xA0, (byte) 0xA0}; // C64 padding at offset 0xA7 in directory track

	/**
	 * Constructor
	 * @param fs Filesystem type
	 * @param media Media type
	 * @param size Size type
	 */
    private FloppyDiskType(FileSystem fs, Media media, Size size) {
        this.fs = fs;
    	this.media = media;
    	this.size = size;
    }

    public String toString()
    {
    	return String.format("Image type %s %s of size %s", 
    			fs, 
    			media == null ? "-" : media.toString().replaceAll("([^\\d_]+)([\\d_]*)", "$2 $1"), 
    			size == null ? "-" : size.toString().replaceAll("(\\D+)(\\d+)", "$2 $1"));
    }

    // FIXME: BBCMICRO to be added
    /**
     * Method to determine the FloppyDiskType given a image file. The file
     * is checked for various signatures (usually at location 0x0), which are compared
     * to known FAT/Amiga/C64 signatures. 
     * @param diskFile Input image file
     * @return FlopyDiskType Best-guess of the type of floppy disk
     * @throws IOException If an I/O error occurs
     */
    public static FloppyDiskType getDiskType(File diskFile) throws IOException {
    	FloppyDiskType result = UNDEFINED;
		RandomAccessFile img = new RandomAccessFile(diskFile, "r");
		
		// Very basic disk-type check
		// NOTE: This is metadata info and should be handled by FITS/DROID!!
		img.seek(0);
		img.read(signature);
		if (arrayEquals(FATsignature, signature))
		{
			// Highly likely this is a FAT-formatted disk
			List<Integer> chs = DiskUtilities.determineCHS(diskFile);
			logger.debug("FAT disk with geometry: " + chs.toString());
			if (chs.get(1) != 2)
				return UNDEFINED;
			if(chs.get(0) == 80) {
				switch (chs.get(2)) {
				case 8:
					result = FAT3_5_640; // FIXME: also FAT5_25_320
					break;
				case 9:
					result = FAT3_5_720;
					break;
				case 15:
					result = FAT5_25_1200;
					break;
				case 18:
					result = FAT3_5_1440;
					break;
				case 36:
					result = FAT3_5_2880;
					break;
				default:
					result = UNDEFINED;
					break;
				}
			}
			else if(chs.get(0) == 40) {
				switch (chs.get(2)) {
				case 8:
					result = FAT5_25_160; // FIXME: also FAT5_25_320
					break;
				case 9:
					result = FAT5_25_180; // FIXME: also FAT5_25_360
					break;
				default:
					result = UNDEFINED;
					break;
				}
			}
			logger.debug(result.toString());
			return result;
		}
		else if (arrayEquals(AMDsignature, signature))
		{
			// Highly likely this is a AMD-formatted disk
			int numblocks = (int) diskFile.length()/512;
			logger.debug("Amiga disk has " + numblocks + " blocks");
			// Check whether it's closest to a DD (1760 blocks) or HD (3520 blocks) disk
			result = Math.min(Math.abs(1760 - numblocks), Math.abs(3520 - numblocks)) == Math.abs(1760 - numblocks) ? AMG3_5_880 : AMG3_5_1760;
			logger.debug(result.toString());
			return result;
		}
		else if (arrayEquals(T64signature, signature))
		{
			// Highly likely this is a T64-formatted disk
			result = C64TAPE;
			logger.debug(result.toString());
			return result;
		}
		else if (arrayEquals(DSKsignature, signature))
		{
			// Highly likely this is a DSK-formatted disk
			result = AMS3_180;
			logger.debug(result.toString());
			return result;
		}
		else if (arrayEquals(CDTsignature, signature))
		{
			// Highly likely this is a CDT-formatted disk
			result = AMSTAPE;
			logger.debug(result.toString());
			return result;
		}

		img.seek(0x16500);	// C64 directory track (track 18/0)
		img.read(signature);
		img.seek(0x165A7);	// C64 padding (track 18/0/0xA7)
		img.read(signature2);
		if (arrayEquals(C64signature1, signature) || arrayEquals(C64signature2, signature2)) // Let's not be too picky
		{
			// Highly likely this is a C64-formatted disk
			// There are 4 expected sizes:
			logger.debug("C64 disk has size " + diskFile.length());
			switch ((int) diskFile.length()) {
			case 174848:	// 35 tracks, no errors
			case 175531:	// 35 tracks, 683 error bytes
			case 196608:	// 40 tracks, no errors
			case 197376:	// 40 tracks, 768 error bytes
			case 205312:	// 42 tracks, no errors
			case 206114:	// 42 tracks, 802 error bytes
				result = C645_25_170;
				break;
			case 349696:	// 70 tracks, no errors
			case 351062:	// 70 tracks, 1366 error bytes
				result = C645_25_340;
				break;
			default:
				result = UNDEFINED;
				break;
			}
			logger.debug(result.toString());
			return result;
		}
		return result;		
    }

    /**
     * Compares two arrays for equality. Two arrays are considered equal if 
     * for the length of the first array, all corresponding pairs
     * of elements in the two arrays are equal
     * @param first One array tested for equality. Determines length of equality test
     * @param second Other array tested for equality
     * @return <tt>true</tt> if the contents of the two arrays up to the length of the first array are equal
     */
	private static boolean arrayEquals(byte[] first, byte[] second) {

		if (first.length > second.length)
			return false;

		for (int i = 0; i < first.length; i++)
			if (first[i] != second[i])
				return false;

		return true;
    }
}
