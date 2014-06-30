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

-- Apps table
-- Contains the name, version and description of each application in the Software Archive
-- Example: APP-2001 (ID), Acrobat Reader (name), 1.0 (version), PDF reader for DOS (description)
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1000', 'FreeDOS Edit','1.0','FreeDOS improved clone of MS-DOS Edit', 'FreeDOS', '2008', 'GNU General Public License, version 2', 'en', 'http://www.freedos.org/software/?prog=edit', FILE_READ('./database/UserInstructions/FreeDOS_Edit_manual.txt', NULL));
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1001', 'Blocek','1.33b','Image viewer for DOS', 'Laaca', '3 November 2007', 'GNU General Public License, version 2', 'en', 'http://www.laaca-mirror.ic.cz/', FILE_READ('./database/UserInstructions/Blocek_manual.txt', NULL));
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1002', 'Xzgv', null, 'Xzgv is a picture viewer for X, with a thumbnail-based file selector. It uses GTK+ and Imlib. Most file formats are supported, and the thumbnails used are compatible with xv, zgv, and the Gimp.', null, null, 'GNU General Public License', 'en', 'http://www.archive.org/details/tucows_51418_xzgv', FILE_READ('./database/UserInstructions/Xzgv_manual.txt', NULL));
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1003', 'Xpdf', null, 'Xpdf is an open source viewer for Portable Document Format (PDF) files. Xpdf is designed to be small and efficient.', 'Derek Noonburg', null,'GNU General Public License (GPL), version 2', 'en', 'http://www.foolabs.com/xpdf/index.html', FILE_READ('./database/UserInstructions/Xpdf_manual.txt', NULL));
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1004', 'Beaver', null, 'Beaver is an Early AdVanced (text) EditoR. ', 'Marc Bevand, Damien Terrier and Emmanuel Turquin, 2008 Tobias Heinzen, Double 12', null, 'GNU General Public License', 'en', 'http://beaver-editor.sourceforge.net/', FILE_READ('./database/UserInstructions/Beaver_manual.txt', NULL));
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1005', 'Firefox', null, 'Mozilla Firefox is a free and open source web browser descended from the Mozilla Application Suite and managed by Mozilla Corporation.', 'Mozilla Corporation', null, 'GNU GPL, GNU LGPL, or Mozilla Public License', 'en', 'http://www.mozilla.com/en-US/firefox', FILE_READ('./database/UserInstructions/Firefox_manual.txt', NULL));
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1006', 'MS Office Viewer', null, null, null, null, 'GNU General Public License (GPL)', 'en', 'http://www.damnsmalllinux.org/applications.html', FILE_READ('./database/UserInstructions/MS_Office_Viewer_manual.txt', NULL));
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1007', 'gnumeric', null, null, null, null, 'GNU General Public License (GPL)', 'en', 'http://projects.gnome.org/gnumeric/', '');
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1008', 'mPlayer', null, null, null, null, 'GNU General Public License (GPL)', 'en', 'http://www.mplayerhq.hu', '');
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1009', 'Viewnior', null, '', null, null, 'GNU General Public License (GPL)', 'en', 'http://xsisqox.github.com/Viewnior/', '');
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1010', 'FreeDOS ZIP', '2.32', 'A file archiver tool, like PKZIP', 'info-zip -at- wkuvx1.wku.edu', '2011-08-18', 'Source code available (open)', 'en', 'http://www.info-zip.org/', '');
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1011', 'Open-source ARJ','2.78','Compression utility for DOS', 'Andrew Belov', '23 June 2005', 'GNU General Public License, version 2', 'en', 'http://arj.sourceforge.net/', '');
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1012', 'LHarc','1.0','Compression utility for DOS', 'Haruyasu Yoshizaki', '1988', 'FREEWARE', 'en', 'http://en.wikipedia.org/wiki/LHarc', '');
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1013', 'Inklite','0.36','A cut down version of Inkscape', 'null', '2011', 'GNU General Public License (GPL)', 'en', 'http://puppylinux.org/wikka/InkLite', '');
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1014', 'Info-zip','3.00','Compressor-archiver utilities', 'null', '2008', 'GNU General Public License (GPL)', 'en', 'http://www.info-zip.org/', '');
INSERT INTO softwarearchive.apps (app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('APP-1015', 'ePDFView', '0.1.7', 'ePDFView is an open source viewer for Portable Document Format (PDF) files. ePDFView is a lightweight PDF viewer.', 'Emmas software', '2009', 'GNU General Public License (GPL), version 2', 'en', 'http://www.emma-soft.com/projects/epdfview', '');

-- OpSys table
-- Contains the name, version and description of each Operating System in the Software Archive
-- Example: OPS-1000 (ID), FreeDOS (name), 0.9 (version), Open source DOS for x86 (description)
INSERT INTO softwarearchive.opsys (opsys_id, name,version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('OPS-1000', 'FreeDOS','0.9','Open source DOS for x86', 'Jim Hall & The FreeDOS team', '3 September 2006', 'GNU GPL with some freeware and shareware licensed utils', 'en', 'http://en.wikipedia.org/wiki/FreeDOS http://www.freedos.org/', FILE_READ('./database/UserInstructions/Freedos_manual.txt', NULL));
INSERT INTO softwarearchive.opsys (opsys_id, name,version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('OPS-1001', 'Damn Small Linux','4.4.10','Versatile mini desktop oriented Linux distribution', 'John Andrews, et al.', 'November 18, 2008', 'GNU General Public License (GPL)', 'en', 'http://www.damnsmalllinux.org', FILE_READ('./database/UserInstructions/Damn_Small_Linux_manual.txt', NULL));
INSERT INTO softwarearchive.opsys (opsys_id, name,version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('OPS-1002', 'Puppy Linux','5.2.2','Linux distribution', 'Barry Kauler and the Puppy community', '2011', 'Mainly the GNU GPL and various other free software licenses', 'en', 'http://www.puppylinux.org', FILE_READ('./database/UserInstructions/Ubuntu_manual.txt', NULL));
INSERT INTO softwarearchive.opsys (opsys_id, name,version, description, creator, release_date, license, language_id, reference, user_instructions) VALUES('OPS-1003', 'FreeDOS','1.1','Open source DOS for x86', 'Jim Hall & The FreeDOS team', '2 January 2012', 'GNU GPL with some freeware and shareware licensed utils', 'en', 'http://en.wikipedia.org/wiki/FreeDOS http://www.freedos.org/', FILE_READ('./database/UserInstructions/Freedos_manual.txt', NULL));

-- (Disk) Images table
-- Contains the ID, description, image format and platform of the disk images in the database 
-- Example: IMG-1000 (ID), FreeDOS version 0.9 (description), 1 (image format), 5 (platform)
INSERT INTO softwarearchive.images (image_id,description, imageformat_id, platform_id) VALUES('IMG-1000','FreeDOS version 0.9', 'IFT-1000', 'HPF-1004');
INSERT INTO softwarearchive.images (image_id,description, imageformat_id, platform_id) VALUES('IMG-1001','Damn Small Linux version 4.4.10', 'IFT-1003', 'HPF-1004');
INSERT INTO softwarearchive.images (image_id,description, imageformat_id, platform_id) VALUES('IMG-1002','Puppy Linux version 5.2.2', 'IFT-1003', 'HPF-1004');
INSERT INTO softwarearchive.images (image_id,description, imageformat_id, platform_id) VALUES('IMG-1003','FreeDOS version 1.1', 'IFT-1000', 'HPF-1004');

-- (Disk) Images BLOBs table
-- Contains file location (for upload into database) of the disk images in the database 
-- Example: IMG-1000 (ID), ./packages/FreeDOS09_arj_blocek_lhz.img.zip (location)
INSERT INTO softwarearchive.imageblobs (image_id, image) VALUES('IMG-1000', FILE_READ('./packages/FreeDOS09_blocek.img.zip'));
INSERT INTO softwarearchive.imageblobs (image_id, image) VALUES('IMG-1001', FILE_READ('./packages/DamnSmallLinux250MB.img.zip'));
INSERT INTO softwarearchive.imageblobs (image_id, image) VALUES('IMG-1002', FILE_READ('./packages/Puppy522_512MB.img.zip'));
INSERT INTO softwarearchive.imageblobs (image_id, image) VALUES('IMG-1003', FILE_READ('./packages/FreeDOS11_complete.img.zip'));

-- Junction tables
-- These tables form the relational links between the above tables

-- File format to application table (fileformats_apps)
-- One to many link of a file format requiring a rendering application
-- Example FFT-1008 (fileformat_id), APP-1000 (app_id): The 'Plain text' format needs to be rendered in the 'Edit' application  
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1006','APP-1003');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1006','APP-1015');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1007','APP-1000');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1007','APP-1004');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1008','APP-1000');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1008','APP-1004');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1009','APP-1001');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1009','APP-1009');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1009','APP-1002');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1010','APP-1001');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1010','APP-1002');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1010','APP-1009');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1011','APP-1001');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1011','APP-1002');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1011','APP-1009');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1012','APP-1001');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1012','APP-1002');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1012','APP-1009');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1013','APP-1001');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1013','APP-1002');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1013','APP-1009');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1014','APP-1005');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1016','APP-1006');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1018','APP-1011');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1019','APP-1012');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1022','APP-1007');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1023','APP-1008');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1024','APP-1013');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1025','APP-1010');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1025','APP-1014');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1026','APP-1008');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1030','APP-1003');
INSERT INTO softwarearchive.fileformats_apps (fileformat_id,app_id) VALUES('FFT-1030','APP-1015');

-- File format to operating system table (fileformats_opsys)
-- One to many link of a file format requiring an operating system (but no rendering application)
-- Example FFT-1020 (fileformat_id), OPS-1001 (opsys_id): The 'ISO 9660 CD-ROM' format can be rendered in Damn Small Linux  
INSERT INTO softwarearchive.fileformats_opsys (fileformat_id,opsys_id) VALUES('FFT-1020','OPS-1000');
INSERT INTO softwarearchive.fileformats_opsys (fileformat_id,opsys_id) VALUES('FFT-1021','OPS-1000');
INSERT INTO softwarearchive.fileformats_opsys (fileformat_id,opsys_id) VALUES('FFT-1021','OPS-1001');
INSERT INTO softwarearchive.fileformats_opsys (fileformat_id,opsys_id) VALUES('FFT-1021','OPS-1002');
INSERT INTO softwarearchive.fileformats_opsys (fileformat_id,opsys_id) VALUES('FFT-1021','OPS-1003');

-- Operating System to Platform (opsys_platform) table
-- Defines which OS (defined in the opsys table) runs on what platform (defined in the platform table)
-- Example: OPS-1000 (opsys_id), HPF-1000 (platform_id): FreeDOS runs on x86 
INSERT INTO softwarearchive.opsys_platform (opsys_id, platform_id) VALUES('OPS-1000','HPF-1004');
INSERT INTO softwarearchive.opsys_platform (opsys_id, platform_id) VALUES('OPS-1001','HPF-1004');
INSERT INTO softwarearchive.opsys_platform (opsys_id, platform_id) VALUES('OPS-1002','HPF-1004');
INSERT INTO softwarearchive.opsys_platform (opsys_id, platform_id) VALUES('OPS-1003','HPF-1004');

-- Application to Operating System (apps_opsys) table
-- Defines which application (defined in the apps table) runs on what OS (defined in the opsys table)
-- Example: APP-1000 (app_id), OPS-1000 (opsys_id): The Edit application runs on FreeDOS 
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1000','OPS-1000');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1000','OPS-1003');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1001','OPS-1000');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1001','OPS-1003');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1002','OPS-1001');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1003','OPS-1001');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1004','OPS-1001');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1005','OPS-1001');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1006','OPS-1001');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1007','OPS-1002');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1008','OPS-1002');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1009','OPS-1002');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1010','OPS-1003');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1011','OPS-1003');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1012','OPS-1003');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1013','OPS-1002');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1014','OPS-1001');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1014','OPS-1002');
INSERT INTO softwarearchive.apps_opsys (app_id, opsys_id) VALUES('APP-1015','OPS-1002');

-- Application to Image (apps_images) table
-- Defines which application (defined in the apps table) is contained in which image (defined in the images table)
-- Example: APP-1006 (app_id), IMG-1001 (image_id): The MS Office Viewer application is contained in Damn Small Linux 
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1000','IMG-1000');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1001','IMG-1000');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1002','IMG-1001');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1003','IMG-1001');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1004','IMG-1001');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1005','IMG-1001');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1006','IMG-1001');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1007','IMG-1002');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1008','IMG-1002');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1009','IMG-1002');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1000','IMG-1003');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1001','IMG-1003');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1010','IMG-1003');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1011','IMG-1003');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1012','IMG-1003');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1013','IMG-1002');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1014','IMG-1002');
INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1015','IMG-1002');

-- Operating System to Image (opsys_images) table
-- Defines which operating system (defined in the opsys table) is contained in which image (defined in the images table)
-- Example: OPS-1000 (opsys_id), IMG-1000 (image_id): The FreeDOS OS is contained in the FreeDOS image 
INSERT INTO softwarearchive.opsys_images (opsys_id, image_id) VALUES('OPS-1000','IMG-1000');
INSERT INTO softwarearchive.opsys_images (opsys_id, image_id) VALUES('OPS-1001','IMG-1001');
INSERT INTO softwarearchive.opsys_images (opsys_id, image_id) VALUES('OPS-1002','IMG-1002');
INSERT INTO softwarearchive.opsys_images (opsys_id, image_id) VALUES('OPS-1003','IMG-1003');
