-- EA_PopulateTables.sql:  Populates tables with test data for schema 'emulatorarchive'
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
-- * Project Title: Emulator Archive (EA)$
-- */
--
-- Author: David Michel
--
-- Arguments:
--             none (H2 doesn't accept any)
--

-- populate tables
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(3, 'Dioscuri', '0.5.0', 'jar', 'Dioscuri-0.5.0.jar', 'Dioscuri, the modular emulator', 'dioscuri_050Package.zip', 'zip', '1', FILE_READ('./packages/dioscuri_050Package.zip'));
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(4, 'Vice', '2.2', 'ELF', 'x64', 'VICE, the VersatIle Commodore Emulator (Linux)', 'LinVICE_22Package.zip', 'zip', '1', FILE_READ('./packages/LinVICE_22Package.zip'));
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(5, 'Vice', '2.2', 'exe', 'x64.exe', 'VICE, the VersatIle Commodore Emulator (Windows)', 'WinVICE_22Package.zip', 'zip', '1', FILE_READ('./packages/WinVICE_22Package.zip'));
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(6, 'Qemu', '0.13.0', 'ELF', 'qemu', 'Qemu, the generic and open source machine emulator and virtualiser (Linux)', 'LinQemu_0130Package.zip', 'zip', '1', FILE_READ('./packages/LinQemu_0130Package.zip'));
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(7, 'Qemu', '0.9.0', 'exe', 'qemu.exe', 'Qemu, the generic and open source machine emulator and virtualiser (Windows)', 'WinQemu_090Package.zip', 'zip', '1', FILE_READ('./packages/WinQemu_090Package.zip'));
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(8, 'UAE', '0.8.29', 'ELF', 'uae', 'UAE, Amiga emulator for Linux', 'LinUAE_0829Package.zip', 'zip', '1', FILE_READ('./packages/LinUAE_0829Package.zip'));
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(9, 'UAE', '1.6', 'exe', 'winuae.exe', 'UAE, Amiga emulator for Windows', 'WinUAE_16Package.zip', 'zip', '1', FILE_READ('./packages/WinUAE_16Package.zip'));
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(10, 'JavaCPC', '6.7', 'exe', 'JavaCPC.exe', 'JavaCPC, Java Amstrad emulator (Windows)', 'WinJavaCPC_67.zip', 'zip', '1', FILE_READ('./packages/WinJavaCPC_67Package.zip'));
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(11, 'Dioscuri', '0.6.0', 'jar', 'Dioscuri-0.6.0.jar', 'Dioscuri, the modular emulator', 'dioscuri_060Package.zip', 'zip', '1', FILE_READ('./packages/dioscuri_060Package.zip'));
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(12, 'Dioscuri', '0.5.0', 'jar', 'Dioscuri-0.5.0.jar', 'Dioscuri, the modular emulator', 'dioscuri_050Packagev2.zip', 'zip', '2', FILE_READ('./packages/dioscuri_050Packagev2.zip'));
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(13, 'BeebEm', '0.0.13', 'ELF', 'beebem', 'BeebEm, BBC Micro and Master 128 Emulator (Linux)', 'LinBeebEm_0013Package.zip', 'zip', '1', FILE_READ('./packages/LinBeebEm_0013Package.zip'));
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(14, 'BeebEm', '4.13', 'exe', 'beebem.exe', 'BeebEm, BBC Micro and Master 128 Emulator (Windows)', 'WinBeebEm_413Package.zip', 'zip', '1', FILE_READ('./packages/WinBeebEm_413Package.zip'));
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,package_name,package_type,package_version,package) VALUES(15, 'Thomson', '1.0', 'exe', 'bnftowin.exe', 'Thomson Emulator (Windows)', 'WinThomson_10Package.zip', 'zip', '1', FILE_READ('./packages/WinThomson_10Package.zip'));

INSERT INTO emulatorarchive.hardware (hardware_id,name) VALUES(1,'x86');
INSERT INTO emulatorarchive.hardware (hardware_id,name) VALUES(2,'C64');
INSERT INTO emulatorarchive.hardware (hardware_id,name) VALUES(3,'Amiga');
INSERT INTO emulatorarchive.hardware (hardware_id,name) VALUES(4,'Amstrad');
INSERT INTO emulatorarchive.hardware (hardware_id,name) VALUES(5,'BBCMICRO');
INSERT INTO emulatorarchive.hardware (hardware_id,name) VALUES(6,'Thomson');

INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(1,'FAT12');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(2,'FAT16');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(3,'FAT32');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(4,'D64');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(5,'T64');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(6,'X64');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(7,'ROM');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(8,'ADF');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(9,'DSK');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(10,'CDT');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(11,'SNA');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(12,'IMG');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(13,'SSD');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(14,'EXT3');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(15,'D7');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(16,'DD7');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(17,'M7');
INSERT INTO emulatorarchive.imageformats (imageformat_id,name) VALUES(18,'K7');

-- populate junction tables
--
-- Defines what emulators run what hardware
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(3,1);
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(4,2);
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(5,2);
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(6,1);
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(7,1);
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(8,3);
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(9,3);
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(10,4);
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(11,1);
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(12,1);
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(13,5);
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(14,5);
INSERT INTO emulatorarchive.emus_hardware (emulator_id,hardware_id) VALUES(15,6);

-- Defines what emulators run what imageformats
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(3,1);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(3,2);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(4,4);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(4,5);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(4,6);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(5,4);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(5,5);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(5,6);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(6,1);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(6,2);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(6,3);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(6,14);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(7,1);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(7,2);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(7,3);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(7,14);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(8,7);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(8,8);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(9,7);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(9,8);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(10,9);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(10,10);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(10,11);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(11,1);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(11,2);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(12,1);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(12,2);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(13,12);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(13,13);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(14,12);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(14,13);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(15,15);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(15,16);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(15,17);
INSERT INTO emulatorarchive.emus_imageformats (emulator_id,imageformat_id) VALUES(15,18);