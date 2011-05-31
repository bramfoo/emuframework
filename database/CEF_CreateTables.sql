-- CEF_CreateTables.sql: Creates tables for schema 'engine'
--
-- $Revision: 803 $ $Date: 2011-05-31 00:27:06 +0100 (Tue, 31 May 2011) $ $Author: BLohman $
--
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
-- * Project Title: Core Emulation Framework (Core EF)$
-- */
--
-- Author: Bram Lohman
--
-- Arguments:
--		none
--

-- Table for external registries
CREATE TABLE registries
(
  registry_id INT(4) NOT NULL PRIMARY KEY,
  name VARCHAR2(250) NOT NULL,
  url VARCHAR2(250),
  class_name VARCHAR2(500),
  translation_view VARCHAR2(500),
  enabled BOOLEAN DEFAULT FALSE,
  description VARCHAR2(500),
  comment VARCHAR2(500)
);

-- Table for whitelisted emulators
CREATE TABLE emulator_whitelist
(
  emulator_id INT(4) NOT NULL PRIMARY KEY,
  emulator_descr VARCHAR2(500)
);

-- Table for EF file formats
-- NOTE: This is a direct copy from the Sofware Archive database
CREATE TABLE fileformats
(
  fileformat_id VARCHAR2(16) NOT NULL PRIMARY KEY,
  name VARCHAR2(250) NOT NULL
);

-- Table for PCR file formats
CREATE TABLE PCR_fileformats
(
  fileformat_id VARCHAR2(16) NOT NULL PRIMARY KEY,
  name VARCHAR2(250) NOT NULL
);

-- Junction table for EF fileformats to PCR fileformats
CREATE TABLE EF_PCR_fileformats
(
  PCR_ff_id VARCHAR2(16) NOT NULL,
  EF_ff_id VARCHAR2(16) NOT NULL,
  FOREIGN KEY (PCR_ff_id) REFERENCES PCR_fileformats (fileformat_id),
  FOREIGN KEY (EF_ff_id) REFERENCES fileformats (fileformat_id)
);

-- Views
-- Show all combinations (including nulls) between EF and PCR file formats
CREATE VIEW EF_PCR_FORMATS AS
SELECT ff.fileformat_id as "EF_FORMAT_ID", ff.name as "EF_FORMAT_NAME", pcr_ff.fileformat_id as "PCR_FORMAT_ID", pcr_ff.name as "PCR_FORMAT_NAME"
FROM fileformats ff 
LEFT OUTER JOIN EF_PCR_fileformats ep_ff
ON ff.fileformat_id = ep_ff.EF_ff_id LEFT OUTER JOIN PCR_fileformats pcr_ff
ON ep_ff.pcr_ff_id = pcr_ff.fileformat_id
UNION
SELECT ff.fileformat_id as "EF_FORMAT_ID", ff.name as "EF_FORMAT_NAME", pcr_ff.fileformat_id as "PCR_FORMAT_ID", pcr_ff.name as "PCR_FORMAT_NAME"
FROM fileformats ff 
RIGHT OUTER JOIN EF_PCR_fileformats ep_ff
ON ff.fileformat_id = ep_ff.EF_ff_id RIGHT OUTER JOIN PCR_fileformats pcr_ff
ON ep_ff.pcr_ff_id = pcr_ff.fileformat_id;

-- Whitelisted emulators
INSERT INTO emulator_whitelist (emulator_id, emulator_descr) values (4, 'Vice (Linux)');
INSERT INTO emulator_whitelist (emulator_id, emulator_descr) values (5, 'Vice (Windows)');
INSERT INTO emulator_whitelist (emulator_id, emulator_descr) values (6, 'QEMU (Linux)');
INSERT INTO emulator_whitelist (emulator_id, emulator_descr) values (7, 'QEMU (Windows)');
INSERT INTO emulator_whitelist (emulator_id, emulator_descr) values (8, 'UAE (Linux)');
INSERT INTO emulator_whitelist (emulator_id, emulator_descr) values (9, 'UAE (Windows)');
INSERT INTO emulator_whitelist (emulator_id, emulator_descr) values (10, 'JavaCPC (Win)');
INSERT INTO emulator_whitelist (emulator_id, emulator_descr) values (11, 'Dioscuri (0.6)');
INSERT INTO emulator_whitelist (emulator_id, emulator_descr) values (13, 'BeebEm (Linux)');
INSERT INTO emulator_whitelist (emulator_id, emulator_descr) values (14, 'BeebEm (Windows)');

-- File format table
-- Contains the name of known file formats 
-- NOTE: This is a direct copy from the Sofware Archive database
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-1', 'Amiga Disk Image');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-2', 'Amstrad Tape Image');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-3', 'Amstrad Disk Image');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-4', 'BBC Micro Image');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-5', 'Commodore C64 Tape Image');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-6', 'Commodore C64 Disk Image');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-7', 'Portable Document Format');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-8', 'Extensible Markup Language');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-9', 'Plain text');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-10', 'JPEG File Interchange Format');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-11', 'Windows Bitmap');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-12', 'Graphics Interchange Format');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-13', 'Tagged Image File Format');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-14', 'Portable Network Graphics');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-15', 'Hypertext Markup Language');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-16', 'WordPerfect for MS-DOS/Windows Document');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-17', 'Microsoft Word');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-18', 'Motorola Quark Express Document');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-19', 'ARJ archive data');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-20', 'LHarc 1.x/ARX archive data');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-21', 'DOS/Windows executable');
INSERT INTO fileformats (fileformat_id, name) VALUES('FFT-22', 'ISO 9660 CD-ROM');

-- Registries table
-- Contains external registries 
INSERT INTO registries (registry_id, name, url, class_name, translation_view, enabled, description, comment) VALUES(2, 'PRONOM/PCR', 'http://www.nationalarchives.gov.uk/pronom/', 'eu.keep.characteriser.registry.PronomRegistry', 'EF_PCR_FORMATS', TRUE, 'PRONOM/PCR, the one and only registry', 'Does not support pathways yet');
INSERT INTO registries (registry_id, name, url, class_name, translation_view, enabled, description, comment) VALUES(3, 'UDFR', 'http://www.gdfr.info/udfr.html', 'eu.keep.characteriser.registry.UDFRRegistry', 'EF_PCR_FORMATS', TRUE, 'UDFR, the non-existing registry', 'Will this ever get implemented?');

-- PCR File format table
-- Contains the name of PCR file formats and IDs corresponding to EF file formats
-- NOTE: This is taken from PRONOM (http://www.nationalarchives.gov.uk/PRONOM/), 26-04-2011
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/3', 'Graphics Interchange Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/4', 'Graphics Interchange Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/7', 'Tagged Image File Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/8', 'Tagged Image File Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/9', 'Tagged Image File Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/10', 'Tagged Image File Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/11', 'Portable Network Graphics');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/12', 'Portable Network Graphics');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/13', 'Portable Network Graphics');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/14', 'Acrobat PDF 1.0 - Portable Document Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/15', 'Acrobat PDF 1.1 - Portable Document Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/16', 'Acrobat PDF 1.2 - Portable Document Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/17', 'Acrobat PDF 1.3 - Portable Document Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/18', 'Acrobat PDF 1.4 - Portable Document Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/19', 'Acrobat PDF 1.5 - Portable Document Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/20', 'Acrobat PDF 1.6 - Portable Document Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/37', 'Microsoft Word for Windows Document');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/38', 'Microsoft Word for Windows Document');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/39', 'Microsoft Word for Windows Document');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/40', 'Microsoft Word for Windows Document');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/41', 'Raw JPEG Stream');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/42', 'JPEG File Interchange Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/43', 'JPEG File Interchange Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/44', 'JPEG File Interchange Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/55', 'Microsoft Excel 2.1 Worksheet (xls)');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/56', 'Microsoft Excel 3.0 Worlsheet (xls)');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/57', 'Microsoft Excel 4.0 Worksheet (xls)');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/58', 'Microsoft Excel 4.0 Workbook (xls)');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/59', 'Microsoft Excel 5.0 Workbook (xls)');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/60', 'Microsoft Excel 95 Workbook (xls)');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/61', 'Microsoft Excel 97 Workbook (xls)');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/62', 'Microsoft Excel 2000-2003 Workbook (xls)');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/95', 'Acrobat PDF/A - Portable Document Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/96', 'Hypertext Markup Language');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/97', 'Hypertext Markup Language');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/98', 'Hypertext Markup Language');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/99', 'Hypertext Markup Language');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/100', 'Hypertext Markup Language');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/101', 'Extensible Markup Language');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/114', 'Windows Bitmap');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/115', 'Windows Bitmap');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/116', 'Windows Bitmap');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/117', 'Windows Bitmap');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/118', 'Windows Bitmap');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/119', 'Windows Bitmap');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('fmt/276', 'Acrobat PDF 1.7 - Portable Document Format');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('x-fmt/44', 'WordPerfect for MS-DOS/Windows Document');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('x-fmt/111', 'Plain Text File');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('x-fmt/182', 'Quark Xpress Data File');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('x-fmt/203', 'WordPerfect for Windows Document');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('x-fmt/393', 'WordPerfect for MS-DOS Document');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('x-fmt/394', 'WordPerfect for MS-DOS/Windows Document');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('x-fmt/409', 'MS-DOS Executable');
INSERT INTO PCR_fileformats (fileformat_id, name) VALUES('x-fmt/410', 'Windows New Executable');

-- EF to PCR file format conversion
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-7', 'fmt/14');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-7', 'fmt/15');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-7', 'fmt/16');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-7', 'fmt/17');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-7', 'fmt/18');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-7', 'fmt/19');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-7', 'fmt/20');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-7', 'fmt/95');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-7', 'fmt/276');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-8', 'fmt/101');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-9', 'x-fmt/111');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-10', 'fmt/41');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-10', 'fmt/42');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-10', 'fmt/43');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-10', 'fmt/44');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-11', 'fmt/114');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-11', 'fmt/115');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-11', 'fmt/116');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-11', 'fmt/117');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-11', 'fmt/118');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-11', 'fmt/119');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-12', 'fmt/3');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-12', 'fmt/4');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-13', 'fmt/7');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-13', 'fmt/8');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-13', 'fmt/9');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-13', 'fmt/10');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-14', 'fmt/11');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-14', 'fmt/12');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-14', 'fmt/13');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-15', 'fmt/96');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-15', 'fmt/97');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-15', 'fmt/98');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-15', 'fmt/99');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-15', 'fmt/100');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-16', 'x-fmt/44');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-16', 'x-fmt/203');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-16', 'x-fmt/393');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-16', 'x-fmt/394');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-17', 'fmt/37');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-17', 'fmt/38');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-17', 'fmt/39');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-17', 'fmt/40');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-18', 'x-fmt/182');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-21', 'x-fmt/409');
INSERT INTO EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES('FFT-21', 'x-fmt/410');