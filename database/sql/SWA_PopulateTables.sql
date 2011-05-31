-- SWA_PopulateTables.sql: Populates tables with test data for schema 'softwarearchive'
--                         This script needs to be run as system/root/administrator
--                         For H2 this is 'sa'
--
-- $Revision$ $Date$ $Author$
--
-- /*
-- $header:
-- * Copyright (c) 2009-2011 Tessella plc.
-- * Licensed under the Apache License, Version 2.0 (the "License");
-- * you may not use this file except in compliance with the License.
-- * You may obtain a copy of the License at
-- *
-- * http://www.apache.org/licenses/LICENSE-2.0
-- *
-- * Unless required by applicable law or agreed to in writing, software
-- * distributed under the License is distributed on an "AS IS" BASIS,
-- * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- * See the License for the specific language governing permissions and
-- * limitations under the License.
-- *
-- * For more information about this project, visit
-- *   http://www.keep-project.eu/
-- *   http://emuframework.sourceforge.net/
-- * or contact us via email:
-- *   blohman at users.sourceforge.net
-- *   dav_m at users.sourceforge.net
-- *   bkiers at users.sourceforge.net
-- * Developed by:
-- *   Tessella plc <www.tessella.com>
-- *   Koninklijke Bibliotheek <www.kb.nl>
-- *   KEEP <www.keep-project.eu>
-- * Project Title: Software Archive (SWA)$
-- */
--
-- Author: David Michel
--
-- Arguments:
--             none (H2 doesn't accept any)
--

-- populate tables

-- File format table
-- Contains the name of known file formats in the Software Archive
-- Example: FFT-1009 (ID), JPEG File Interchange Format (name), PDF reader for DOS (description)
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1000', 'Amiga Disk Image', null, 'Amiga Disk File aka ADF is a file format used by Amiga computers and emulators to store images of disks. It has been around almost as long as the Amiga itself, although it was not initially called by any particular name. Before it was known as ADF, it was used in commercial game production, backup and disk virtualization. Technically speaking, ADF is not really a file format but actually a track-by-track dump of the disk data as read by the Amiga operating system, and so the "format" is really fixed-width AmigaDOS data tracks appended one after another and held in a file.', 'http://en.wikipedia.org/wiki/Amiga_Disk_File'); 
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1001', 'Amstrad Tape Image', null, 'The ".CDT" tape image file format is identical to the ".TZX" file format designed by Tomaz Kac and used by Spectrum emulators. The filename has a different extension to differentiate between tape-images for use on the Amstrad and tape-images for use on the Spectrum. The most recent version of this format can be found at World of Spectrum.', 'http://www.cpcwiki.eu/index.php/Format:CDT_tape_image_file_format http://www.worldofspectrum.org/');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1002', 'Amstrad Disk Image', null, 'Disk image format of both single sided- and double sided ".DSK" images', 'http://www.cpctech.org.uk/docs/dsk.html');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1003', 'BBC Micro Image', null, 'Originally, BBC Games were stored in either Tape or Disc format. In the world of emulators, the file format for BBC software can vary considerably, and so a standard type of format is desirable. For the BBC, there are two typical formats: Raw Tape Format/.INF, and Disk format .SSD/.DSD/.IMG. Most emulators will only use Disk format games. .SSD stands for Single-sided Disk. .DSD stands for Double-sided Disk. .IMG is an older format extension that stands for BBC disk IMaGe. .SSD and .DSD is preferred to avoid confusion as .IMG is also sometimes used to indicated a graphics format. .IMG is functionally the equivalent of .SSD and .DSD and .IMG files can be renamed .SSD or .DSD (depending on type of IMG file). All of the games on this site are in Disk format (.IMG Extension).', 'http://www.bbcmicrogames.com/GettingStarted.html');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1004', 'Commodore C64 Tape Image', null, '".t64" tape image designed by Miha  Peternel which is used with his C64s emulator.', 'http://www.c64.com');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1005', 'Commodore C64 Disk Image', null, '".d64" disk image file', null);
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1006', 'Portable Document Format', null, 'Portable Document Format (PDF) is a file format created by Adobe Systems in 1993 for document exchange. PDF is used for representing two-dimensional documents in a manner independent of the application software, hardware, and operating system.', 'http://en.wikipedia.org/wiki/PDF_(Adobe)');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1007', 'Extensible Markup Language', null, 'XML (Extensible Markup Language) is a set of rules for encoding documents electronically. It is defined in the produced by the W3C and several other related specifications; all are fee-free open standards.', 'http://en.wikipedia.org/wiki/XML');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1008', 'Plain text', null, 'A text file (sometimes spelled "textfile": an old alternate name is "flatfile") is a kind of computer file that is structured as a sequence of lines.', 'http://en.wikipedia.org/wiki/.txt');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1009', 'JPEG File Interchange Format', null, 'JPEG is a commonly used method of lossy compression for photographic images. The degree of compression can be adjusted, allowing a selectable tradeoff between storage size and image quality.', 'http://en.wikipedia.org/wiki/JPEG');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1010', 'Windows Bitmap', null, 'The BMP file format, sometimes called bitmap or DIB file format (for device-independent bitmap), is an image file format used to store bitmap digital images, especially on Microsoft Windows and OS/2 operating systems.', 'http://en.wikipedia.org/wiki/.bmp');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1011', 'Graphics Interchange Format', null, 'The Graphics Interchange Format (GIF) is a bitmap image format that was introduced by CompuServe in 1987 and has since come into widespread usage on the World Wide Web due to its wide support and portability.', 'http://en.wikipedia.org/wiki/.gif');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1012', 'Tagged Image File Format', null, 'Tagged Image File Format (abbreviated TIFF) is a file format for storing images, including photographs and line art.', 'http://en.wikipedia.org/wiki/TIFF');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1013', 'Portable Network Graphics', null, 'Portable Network Graphics (PNG) is a bitmapped image format that employs lossless data compression. PNG was created to improve upon and replace GIF (Graphics Interchange Format) as an image-file format not requiring a patent license.', 'http://en.wikipedia.org/wiki/.png');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1014', 'Hypertext Markup Language', null, 'HTML, which stands for HyperText Markup Language, is the predominant markup language for web pages. It provides a means to create structured documents by denoting structural semantics for text such as headings, paragraphs, lists etc as well as for links, quotes, and other items.', 'http://en.wikipedia.org/wiki/HTML');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1015', 'WordPerfect for MS-DOS/Windows Document', null, 'A file format used by WordPerfect: a proprietary word processing application. At the height of its popularity in the late 1980s and early 1990s, it was the de facto standard word processor.', 'http://en.wikipedia.org/wiki/WordPerfect');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1016', 'Microsoft Word', null, 'A file format used by Microsoft Word: a word processor designed by Microsoft. It was first released in 1983 under the name Multi-Tool Word for Xenix systems.', 'http://en.wikipedia.org/wiki/Microsoft_Word');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1017', 'Motorola Quark Express Document', null, 'A file format used by QuarkXPress ("Quark"): a computer application for creating and editing complex page layouts in a WYSIWYG (What You See Is What You Get) environment. It runs on Mac OS X and Windows. It was first released by Quark, Inc. in 1987 and is still owned and published by them.', 'http://en.wikipedia.org/wiki/QuarkXPress');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1018', 'ARJ archive data', null, 'A file format used by ARJ (Archived by Robert Jung): a software tool designed by Robert K. Jung for creating high-efficiency compressed file archives. ARJ is currently on version 2.85 for DOS and 3.15 for Windows and supports 16-bit and 32-bit Intel architectures.', 'http://en.wikipedia.org/wiki/ARJ');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1019', 'LHarc 1.x/ARX archive data [lh0]', null, 'A file format used by LHA: a freeware compression utility and associated file format. It was created in 1988 by Haruyasu Yoshizaki, and originally named LHarc.', 'http://en.wikipedia.org/wiki/Lharc');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1020', 'DOS/Windows executable', null, 'EXE is the common filename extension denoting an executable file (a program) in the DOS, OpenVMS, Microsoft Windows, Symbian, and OS/2 operating systems.', 'http://en.wikipedia.org/wiki/EXE');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1021', 'ISO 9660 CD-ROM', null, 'ISO 9660, also referred to as CDFS (Compact Disc File System) by some hardware and software providers, is a file system standard published by the International Organization for Standardization (ISO) for optical disc media.', 'http://en.wikipedia.org/wiki/ISO_9660');

-- Platform table
-- Contains the name and description of each known platform
-- Example: HPF-1004 (ID), x86 (name), Intel 8086 (description)
INSERT INTO softwarearchive.platforms (platform_id, name, description, creator, production_start, production_end, reference) VALUES('HPF-1000', 'Amiga','Commodore Amiga', 'Commodore-Amiga Inc.', '1980s', '1990s', 'http://en.wikipedia.org/wiki/Amiga');
INSERT INTO softwarearchive.platforms (platform_id, name, description, creator, production_start, production_end, reference) VALUES('HPF-1001', 'Amstrad','Amstrad', 'Alan Sugar', '1960s', '1990s', 'http://en.wikipedia.org/wiki/Amstrad');
INSERT INTO softwarearchive.platforms (platform_id, name, description, creator, production_start, production_end, reference) VALUES('HPF-1002', 'BBCMICRO','Acorn BBC Microcomputer System', 'Acorn Computers', '1981', '1994', 'http://en.wikipedia.org/wiki/BBC_Micro');
INSERT INTO softwarearchive.platforms (platform_id, name, description, creator, production_start, production_end, reference) VALUES('HPF-1003', 'C64','Commodore 64', 'Commodore International', '1982', '1994', 'http://en.wikipedia.org/wiki/Commodore_64');
INSERT INTO softwarearchive.platforms (platform_id, name, description, creator, production_start, production_end, reference) VALUES('HPF-1004', 'x86','Intel 8086 family', 'Intel, AMD', '1978', 'present', 'http://en.wikipedia.org/wiki/X86');

-- (Disk) image formats table
-- Contains the ID and name of the file system architecture a (disk) image may be in 
-- Example: IMG-1000 (ID), FAT16 (architecture)
INSERT INTO softwarearchive.imageformats (imageformat_id,name) VALUES('IFT-1000','FAT16');
INSERT INTO softwarearchive.imageformats (imageformat_id,name) VALUES('IFT-1001','FAT32');
INSERT INTO softwarearchive.imageformats (imageformat_id,name) VALUES('IFT-1002','ROM');
INSERT INTO softwarearchive.imageformats (imageformat_id,name) VALUES('IFT-1003','EXT3');

-- File format to platform table (fileformats_platform)
-- One to many link of a file format requiring a platform (but no rendering application or OS)
-- Example FFT-1005 (fileformat_id), HPF-1003 (platform_id): The 'Commodore C64 Disk Image' format can be rendered on the C64  
INSERT INTO softwarearchive.fileformats_platform (fileformat_id, platform_id) VALUES('FFT-1001','HPF-1001');
INSERT INTO softwarearchive.fileformats_platform (fileformat_id, platform_id) VALUES('FFT-1002','HPF-1001');
INSERT INTO softwarearchive.fileformats_platform (fileformat_id, platform_id) VALUES('FFT-1003','HPF-1002');
INSERT INTO softwarearchive.fileformats_platform (fileformat_id, platform_id) VALUES('FFT-1004','HPF-1003');
INSERT INTO softwarearchive.fileformats_platform (fileformat_id, platform_id) VALUES('FFT-1005','HPF-1003');