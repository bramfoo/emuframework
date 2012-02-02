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
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1022', 'XLS', null, 'Microsoft Excel is a proprietary commercial spreadsheet application written and distributed by Microsoft for Microsoft Windows and Mac OS X.', 'http://en.wikipedia.org/wiki/Microsoft_Excel');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1023', 'MPEG', null, 'MPEG encoding is the process of capturing (digitizing) or converting (re-encoding) video and/or audio to one of several MPEG video and/or audio standards for distribution (Internet, LAN) or for archiving to optical disc (CD, DVD).', 'http://en.wikipedia.org/wiki/MPEG_encoding');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1024', 'Scalable Vector Graphics', null, 'Scalable Vector Graphics (SVG) is a family of specifications of an XML-based file format for describing two-dimensional vector graphics, both static and dynamic (i.e. interactive or animated).', 'http://en.wikipedia.org/wiki/Scalable_Vector_Graphics');
INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES('FFT-1025', 'ZIP Format', null, 'Zip is a file format used for data compression and archiving. A zip file contains one or more files that have been compressed, to reduce file size, or stored as is.', 'http://en.wikipedia.org/wiki/Zip_%28file_format%29');

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

-- Languages table
-- Contains the language id (locale code) and language name of known languages
INSERT INTO softwarearchive.languages (language_id) VALUES('aa');
INSERT INTO softwarearchive.languages (language_id) VALUES('ab');
INSERT INTO softwarearchive.languages (language_id) VALUES('ae');
INSERT INTO softwarearchive.languages (language_id) VALUES('af');
INSERT INTO softwarearchive.languages (language_id) VALUES('ak');
INSERT INTO softwarearchive.languages (language_id) VALUES('am');
INSERT INTO softwarearchive.languages (language_id) VALUES('an');
INSERT INTO softwarearchive.languages (language_id) VALUES('ar');
INSERT INTO softwarearchive.languages (language_id) VALUES('as');
INSERT INTO softwarearchive.languages (language_id) VALUES('av');
INSERT INTO softwarearchive.languages (language_id) VALUES('ay');
INSERT INTO softwarearchive.languages (language_id) VALUES('az');
INSERT INTO softwarearchive.languages (language_id) VALUES('ba');
INSERT INTO softwarearchive.languages (language_id) VALUES('be');
INSERT INTO softwarearchive.languages (language_id) VALUES('bg');
INSERT INTO softwarearchive.languages (language_id) VALUES('bh');
INSERT INTO softwarearchive.languages (language_id) VALUES('bi');
INSERT INTO softwarearchive.languages (language_id) VALUES('bm');
INSERT INTO softwarearchive.languages (language_id) VALUES('bn');
INSERT INTO softwarearchive.languages (language_id) VALUES('bo');
INSERT INTO softwarearchive.languages (language_id) VALUES('br');
INSERT INTO softwarearchive.languages (language_id) VALUES('bs');
INSERT INTO softwarearchive.languages (language_id) VALUES('ca');
INSERT INTO softwarearchive.languages (language_id) VALUES('ce');
INSERT INTO softwarearchive.languages (language_id) VALUES('ch');
INSERT INTO softwarearchive.languages (language_id) VALUES('co');
INSERT INTO softwarearchive.languages (language_id) VALUES('cr');
INSERT INTO softwarearchive.languages (language_id) VALUES('cs');
INSERT INTO softwarearchive.languages (language_id) VALUES('cu');
INSERT INTO softwarearchive.languages (language_id) VALUES('cv');
INSERT INTO softwarearchive.languages (language_id) VALUES('cy');
INSERT INTO softwarearchive.languages (language_id) VALUES('da');
INSERT INTO softwarearchive.languages (language_id) VALUES('de');
INSERT INTO softwarearchive.languages (language_id) VALUES('dv');
INSERT INTO softwarearchive.languages (language_id) VALUES('dz');
INSERT INTO softwarearchive.languages (language_id) VALUES('ee');
INSERT INTO softwarearchive.languages (language_id) VALUES('el');
INSERT INTO softwarearchive.languages (language_id) VALUES('en');
INSERT INTO softwarearchive.languages (language_id) VALUES('eo');
INSERT INTO softwarearchive.languages (language_id) VALUES('es');
INSERT INTO softwarearchive.languages (language_id) VALUES('et');
INSERT INTO softwarearchive.languages (language_id) VALUES('eu');
INSERT INTO softwarearchive.languages (language_id) VALUES('fa');
INSERT INTO softwarearchive.languages (language_id) VALUES('ff');
INSERT INTO softwarearchive.languages (language_id) VALUES('fi');
INSERT INTO softwarearchive.languages (language_id) VALUES('fj');
INSERT INTO softwarearchive.languages (language_id) VALUES('fo');
INSERT INTO softwarearchive.languages (language_id) VALUES('fr');
INSERT INTO softwarearchive.languages (language_id) VALUES('fy');
INSERT INTO softwarearchive.languages (language_id) VALUES('ga');
INSERT INTO softwarearchive.languages (language_id) VALUES('gd');
INSERT INTO softwarearchive.languages (language_id) VALUES('gl');
INSERT INTO softwarearchive.languages (language_id) VALUES('gn');
INSERT INTO softwarearchive.languages (language_id) VALUES('gu');
INSERT INTO softwarearchive.languages (language_id) VALUES('gv');
INSERT INTO softwarearchive.languages (language_id) VALUES('ha');
INSERT INTO softwarearchive.languages (language_id) VALUES('he');
INSERT INTO softwarearchive.languages (language_id) VALUES('hi');
INSERT INTO softwarearchive.languages (language_id) VALUES('ho');
INSERT INTO softwarearchive.languages (language_id) VALUES('hr');
INSERT INTO softwarearchive.languages (language_id) VALUES('ht');
INSERT INTO softwarearchive.languages (language_id) VALUES('hu');
INSERT INTO softwarearchive.languages (language_id) VALUES('hy');
INSERT INTO softwarearchive.languages (language_id) VALUES('hz');
INSERT INTO softwarearchive.languages (language_id) VALUES('ia');
INSERT INTO softwarearchive.languages (language_id) VALUES('id');
INSERT INTO softwarearchive.languages (language_id) VALUES('ie');
INSERT INTO softwarearchive.languages (language_id) VALUES('ig');
INSERT INTO softwarearchive.languages (language_id) VALUES('ii');
INSERT INTO softwarearchive.languages (language_id) VALUES('ik');
INSERT INTO softwarearchive.languages (language_id) VALUES('io');
INSERT INTO softwarearchive.languages (language_id) VALUES('is');
INSERT INTO softwarearchive.languages (language_id) VALUES('it');
INSERT INTO softwarearchive.languages (language_id) VALUES('iu');
INSERT INTO softwarearchive.languages (language_id) VALUES('ja');
INSERT INTO softwarearchive.languages (language_id) VALUES('jv');
INSERT INTO softwarearchive.languages (language_id) VALUES('ka');
INSERT INTO softwarearchive.languages (language_id) VALUES('kg');
INSERT INTO softwarearchive.languages (language_id) VALUES('ki');
INSERT INTO softwarearchive.languages (language_id) VALUES('kj');
INSERT INTO softwarearchive.languages (language_id) VALUES('kk');
INSERT INTO softwarearchive.languages (language_id) VALUES('kl');
INSERT INTO softwarearchive.languages (language_id) VALUES('km');
INSERT INTO softwarearchive.languages (language_id) VALUES('kn');
INSERT INTO softwarearchive.languages (language_id) VALUES('ko');
INSERT INTO softwarearchive.languages (language_id) VALUES('kr');
INSERT INTO softwarearchive.languages (language_id) VALUES('ks');
INSERT INTO softwarearchive.languages (language_id) VALUES('ku');
INSERT INTO softwarearchive.languages (language_id) VALUES('kv');
INSERT INTO softwarearchive.languages (language_id) VALUES('kw');
INSERT INTO softwarearchive.languages (language_id) VALUES('ky');
INSERT INTO softwarearchive.languages (language_id) VALUES('la');
INSERT INTO softwarearchive.languages (language_id) VALUES('lb');
INSERT INTO softwarearchive.languages (language_id) VALUES('lg');
INSERT INTO softwarearchive.languages (language_id) VALUES('li');
INSERT INTO softwarearchive.languages (language_id) VALUES('ln');
INSERT INTO softwarearchive.languages (language_id) VALUES('lo');
INSERT INTO softwarearchive.languages (language_id) VALUES('lt');
INSERT INTO softwarearchive.languages (language_id) VALUES('lu');
INSERT INTO softwarearchive.languages (language_id) VALUES('lv');
INSERT INTO softwarearchive.languages (language_id) VALUES('mg');
INSERT INTO softwarearchive.languages (language_id) VALUES('mh');
INSERT INTO softwarearchive.languages (language_id) VALUES('mi');
INSERT INTO softwarearchive.languages (language_id) VALUES('mk');
INSERT INTO softwarearchive.languages (language_id) VALUES('ml');
INSERT INTO softwarearchive.languages (language_id) VALUES('mn');
INSERT INTO softwarearchive.languages (language_id) VALUES('mr');
INSERT INTO softwarearchive.languages (language_id) VALUES('ms');
INSERT INTO softwarearchive.languages (language_id) VALUES('mt');
INSERT INTO softwarearchive.languages (language_id) VALUES('my');
INSERT INTO softwarearchive.languages (language_id) VALUES('na');
INSERT INTO softwarearchive.languages (language_id) VALUES('nb');
INSERT INTO softwarearchive.languages (language_id) VALUES('nd');
INSERT INTO softwarearchive.languages (language_id) VALUES('ne');
INSERT INTO softwarearchive.languages (language_id) VALUES('ng');
INSERT INTO softwarearchive.languages (language_id) VALUES('nl');
INSERT INTO softwarearchive.languages (language_id) VALUES('nn');
INSERT INTO softwarearchive.languages (language_id) VALUES('no');
INSERT INTO softwarearchive.languages (language_id) VALUES('nr');
INSERT INTO softwarearchive.languages (language_id) VALUES('nv');
INSERT INTO softwarearchive.languages (language_id) VALUES('ny');
INSERT INTO softwarearchive.languages (language_id) VALUES('oc');
INSERT INTO softwarearchive.languages (language_id) VALUES('oj');
INSERT INTO softwarearchive.languages (language_id) VALUES('om');
INSERT INTO softwarearchive.languages (language_id) VALUES('or');
INSERT INTO softwarearchive.languages (language_id) VALUES('os');
INSERT INTO softwarearchive.languages (language_id) VALUES('pa');
INSERT INTO softwarearchive.languages (language_id) VALUES('pi');
INSERT INTO softwarearchive.languages (language_id) VALUES('pl');
INSERT INTO softwarearchive.languages (language_id) VALUES('ps');
INSERT INTO softwarearchive.languages (language_id) VALUES('pt');
INSERT INTO softwarearchive.languages (language_id) VALUES('qu');
INSERT INTO softwarearchive.languages (language_id) VALUES('rm');
INSERT INTO softwarearchive.languages (language_id) VALUES('rn');
INSERT INTO softwarearchive.languages (language_id) VALUES('ro');
INSERT INTO softwarearchive.languages (language_id) VALUES('ru');
INSERT INTO softwarearchive.languages (language_id) VALUES('rw');
INSERT INTO softwarearchive.languages (language_id) VALUES('sa');
INSERT INTO softwarearchive.languages (language_id) VALUES('sc');
INSERT INTO softwarearchive.languages (language_id) VALUES('sd');
INSERT INTO softwarearchive.languages (language_id) VALUES('se');
INSERT INTO softwarearchive.languages (language_id) VALUES('sg');
INSERT INTO softwarearchive.languages (language_id) VALUES('si');
INSERT INTO softwarearchive.languages (language_id) VALUES('sk');
INSERT INTO softwarearchive.languages (language_id) VALUES('sl');
INSERT INTO softwarearchive.languages (language_id) VALUES('sm');
INSERT INTO softwarearchive.languages (language_id) VALUES('sn');
INSERT INTO softwarearchive.languages (language_id) VALUES('so');
INSERT INTO softwarearchive.languages (language_id) VALUES('sq');
INSERT INTO softwarearchive.languages (language_id) VALUES('sr');
INSERT INTO softwarearchive.languages (language_id) VALUES('ss');
INSERT INTO softwarearchive.languages (language_id) VALUES('st');
INSERT INTO softwarearchive.languages (language_id) VALUES('su');
INSERT INTO softwarearchive.languages (language_id) VALUES('sv');
INSERT INTO softwarearchive.languages (language_id) VALUES('sw');
INSERT INTO softwarearchive.languages (language_id) VALUES('ta');
INSERT INTO softwarearchive.languages (language_id) VALUES('te');
INSERT INTO softwarearchive.languages (language_id) VALUES('tg');
INSERT INTO softwarearchive.languages (language_id) VALUES('th');
INSERT INTO softwarearchive.languages (language_id) VALUES('ti');
INSERT INTO softwarearchive.languages (language_id) VALUES('tk');
INSERT INTO softwarearchive.languages (language_id) VALUES('tl');
INSERT INTO softwarearchive.languages (language_id) VALUES('tn');
INSERT INTO softwarearchive.languages (language_id) VALUES('to');
INSERT INTO softwarearchive.languages (language_id) VALUES('tr');
INSERT INTO softwarearchive.languages (language_id) VALUES('ts');
INSERT INTO softwarearchive.languages (language_id) VALUES('tt');
INSERT INTO softwarearchive.languages (language_id) VALUES('tw');
INSERT INTO softwarearchive.languages (language_id) VALUES('ty');
INSERT INTO softwarearchive.languages (language_id) VALUES('ug');
INSERT INTO softwarearchive.languages (language_id) VALUES('uk');
INSERT INTO softwarearchive.languages (language_id) VALUES('ur');
INSERT INTO softwarearchive.languages (language_id) VALUES('uz');
INSERT INTO softwarearchive.languages (language_id) VALUES('ve');
INSERT INTO softwarearchive.languages (language_id) VALUES('vi');
INSERT INTO softwarearchive.languages (language_id) VALUES('vo');
INSERT INTO softwarearchive.languages (language_id) VALUES('wa');
INSERT INTO softwarearchive.languages (language_id) VALUES('wo');
INSERT INTO softwarearchive.languages (language_id) VALUES('xh');
INSERT INTO softwarearchive.languages (language_id) VALUES('yi');
INSERT INTO softwarearchive.languages (language_id) VALUES('yo');
INSERT INTO softwarearchive.languages (language_id) VALUES('za');
INSERT INTO softwarearchive.languages (language_id) VALUES('zh');
INSERT INTO softwarearchive.languages (language_id) VALUES('zu');

-- Registries table
-- Contains external registries 
INSERT INTO softwarearchive.registries (registry_id, name, url, class_name, translation_view, enabled, description, comment) VALUES(2, 'PRONOM/PCR', 'http://www.nationalarchives.gov.uk/pronom/', 'eu.keep.characteriser.registry.PronomRegistry', 'EF_PCR_FORMATS', TRUE, 'PRONOM/PCR, the one and only registry', 'Does not support pathways yet');
INSERT INTO softwarearchive.registries (registry_id, name, url, class_name, translation_view, enabled, description, comment) VALUES(3, 'UDFR', 'http://www.gdfr.info/udfr.html', 'eu.keep.characteriser.registry.UDFRRegistry', 'EF_PCR_FORMATS', TRUE, 'UDFR, the non-existing registry', 'Will this ever get implemented?');

-- PCR File format table
-- Contains the name of PCR file formats and IDs corresponding to EF file formats
-- NOTE: This is taken from PRONOM (http://www.nationalarchives.gov.uk/PRONOM/), 26-04-2011
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/3', 'Graphics Interchange Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/4', 'Graphics Interchange Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/7', 'Tagged Image File Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/8', 'Tagged Image File Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/9', 'Tagged Image File Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/10', 'Tagged Image File Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/11', 'Portable Network Graphics');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/12', 'Portable Network Graphics');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/13', 'Portable Network Graphics');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/14', 'Acrobat PDF 1.0 - Portable Document Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/15', 'Acrobat PDF 1.1 - Portable Document Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/16', 'Acrobat PDF 1.2 - Portable Document Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/17', 'Acrobat PDF 1.3 - Portable Document Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/18', 'Acrobat PDF 1.4 - Portable Document Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/19', 'Acrobat PDF 1.5 - Portable Document Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/20', 'Acrobat PDF 1.6 - Portable Document Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/37', 'Microsoft Word for Windows Document');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/38', 'Microsoft Word for Windows Document');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/39', 'Microsoft Word for Windows Document');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/40', 'Microsoft Word for Windows Document');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/41', 'Raw JPEG Stream');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/42', 'JPEG File Interchange Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/43', 'JPEG File Interchange Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/44', 'JPEG File Interchange Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/55', 'Microsoft Excel 2.1 Worksheet (xls)');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/56', 'Microsoft Excel 3.0 Worlsheet (xls)');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/57', 'Microsoft Excel 4.0 Worksheet (xls)');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/58', 'Microsoft Excel 4.0 Workbook (xls)');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/59', 'Microsoft Excel 5.0 Workbook (xls)');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/60', 'Microsoft Excel 95 Workbook (xls)');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/61', 'Microsoft Excel 97 Workbook (xls)');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/62', 'Microsoft Excel 2000-2003 Workbook (xls)');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/95', 'Acrobat PDF/A - Portable Document Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/96', 'Hypertext Markup Language');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/97', 'Hypertext Markup Language');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/98', 'Hypertext Markup Language');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/99', 'Hypertext Markup Language');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/100', 'Hypertext Markup Language');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/101', 'Extensible Markup Language');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/114', 'Windows Bitmap');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/115', 'Windows Bitmap');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/116', 'Windows Bitmap');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/117', 'Windows Bitmap');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/118', 'Windows Bitmap');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/119', 'Windows Bitmap');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/276', 'Acrobat PDF 1.7 - Portable Document Format');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/44', 'WordPerfect for MS-DOS/Windows Document');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/111', 'Plain Text File');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/182', 'Quark Xpress Data File');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/203', 'WordPerfect for Windows Document');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/393', 'WordPerfect for MS-DOS Document');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/394', 'WordPerfect for MS-DOS/Windows Document');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/409', 'MS-DOS Executable');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/410', 'Windows New Executable');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/88', 'Microsoft Powerpoint Presentation');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/125', 'Microsoft Powerpoint Presentation 95');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/126', 'Microsoft Powerpoint Presentation 97-2002');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('fmt/215', 'Microsoft Powerpoint for Windows 2007');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/66', 'Microsoft Access Database 2.0');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/240', 'Microsoft Access Database 2000');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/241', 'Microsoft Access Database 2002');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/238', 'Microsoft Access Database 95');
INSERT INTO softwarearchive.PCR_fileformats (fileformat_id, name) VALUES('x-fmt/239', 'Microsoft Access Database 97');

-- EF to PCR file format conversion
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1006', 'fmt/14');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1006', 'fmt/15');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1006', 'fmt/16');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1006', 'fmt/17');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1006', 'fmt/18');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1006', 'fmt/19');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1006', 'fmt/20');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1006', 'fmt/95');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1006', 'fmt/276');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1007', 'fmt/101');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1008', 'x-fmt/111');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1009', 'fmt/41');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1009', 'fmt/42');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1009', 'fmt/43');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1009', 'fmt/44');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1010', 'fmt/114');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1010', 'fmt/115');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1010', 'fmt/116');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1010', 'fmt/117');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1010', 'fmt/118');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1010', 'fmt/119');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1011', 'fmt/3');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1011', 'fmt/4');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1012', 'fmt/7');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1012', 'fmt/8');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1012', 'fmt/9');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1012', 'fmt/10');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1013', 'fmt/11');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1013', 'fmt/12');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1013', 'fmt/13');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1014', 'fmt/96');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1014', 'fmt/97');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1014', 'fmt/98');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1014', 'fmt/99');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1014', 'fmt/100');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1015', 'x-fmt/44');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1015', 'x-fmt/203');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1015', 'x-fmt/393');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1015', 'x-fmt/394');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1016', 'fmt/37');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1016', 'fmt/38');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1016', 'fmt/39');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1016', 'fmt/40');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1017', 'x-fmt/182');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1020', 'x-fmt/409');
INSERT INTO softwarearchive.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-1020', 'x-fmt/410');

