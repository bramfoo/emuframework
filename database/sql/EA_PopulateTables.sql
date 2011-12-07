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

-- Languages table
-- Contains the language id (locale code) and language name of known languages
INSERT INTO emulatorarchive.languages (language_id) VALUES('aa');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ab');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ae');
INSERT INTO emulatorarchive.languages (language_id) VALUES('af');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ak');
INSERT INTO emulatorarchive.languages (language_id) VALUES('am');
INSERT INTO emulatorarchive.languages (language_id) VALUES('an');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ar');
INSERT INTO emulatorarchive.languages (language_id) VALUES('as');
INSERT INTO emulatorarchive.languages (language_id) VALUES('av');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ay');
INSERT INTO emulatorarchive.languages (language_id) VALUES('az');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ba');
INSERT INTO emulatorarchive.languages (language_id) VALUES('be');
INSERT INTO emulatorarchive.languages (language_id) VALUES('bg');
INSERT INTO emulatorarchive.languages (language_id) VALUES('bh');
INSERT INTO emulatorarchive.languages (language_id) VALUES('bi');
INSERT INTO emulatorarchive.languages (language_id) VALUES('bm');
INSERT INTO emulatorarchive.languages (language_id) VALUES('bn');
INSERT INTO emulatorarchive.languages (language_id) VALUES('bo');
INSERT INTO emulatorarchive.languages (language_id) VALUES('br');
INSERT INTO emulatorarchive.languages (language_id) VALUES('bs');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ca');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ce');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ch');
INSERT INTO emulatorarchive.languages (language_id) VALUES('co');
INSERT INTO emulatorarchive.languages (language_id) VALUES('cr');
INSERT INTO emulatorarchive.languages (language_id) VALUES('cs');
INSERT INTO emulatorarchive.languages (language_id) VALUES('cu');
INSERT INTO emulatorarchive.languages (language_id) VALUES('cv');
INSERT INTO emulatorarchive.languages (language_id) VALUES('cy');
INSERT INTO emulatorarchive.languages (language_id) VALUES('da');
INSERT INTO emulatorarchive.languages (language_id) VALUES('de');
INSERT INTO emulatorarchive.languages (language_id) VALUES('dv');
INSERT INTO emulatorarchive.languages (language_id) VALUES('dz');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ee');
INSERT INTO emulatorarchive.languages (language_id) VALUES('el');
INSERT INTO emulatorarchive.languages (language_id) VALUES('en');
INSERT INTO emulatorarchive.languages (language_id) VALUES('eo');
INSERT INTO emulatorarchive.languages (language_id) VALUES('es');
INSERT INTO emulatorarchive.languages (language_id) VALUES('et');
INSERT INTO emulatorarchive.languages (language_id) VALUES('eu');
INSERT INTO emulatorarchive.languages (language_id) VALUES('fa');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ff');
INSERT INTO emulatorarchive.languages (language_id) VALUES('fi');
INSERT INTO emulatorarchive.languages (language_id) VALUES('fj');
INSERT INTO emulatorarchive.languages (language_id) VALUES('fo');
INSERT INTO emulatorarchive.languages (language_id) VALUES('fr');
INSERT INTO emulatorarchive.languages (language_id) VALUES('fy');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ga');
INSERT INTO emulatorarchive.languages (language_id) VALUES('gd');
INSERT INTO emulatorarchive.languages (language_id) VALUES('gl');
INSERT INTO emulatorarchive.languages (language_id) VALUES('gn');
INSERT INTO emulatorarchive.languages (language_id) VALUES('gu');
INSERT INTO emulatorarchive.languages (language_id) VALUES('gv');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ha');
INSERT INTO emulatorarchive.languages (language_id) VALUES('he');
INSERT INTO emulatorarchive.languages (language_id) VALUES('hi');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ho');
INSERT INTO emulatorarchive.languages (language_id) VALUES('hr');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ht');
INSERT INTO emulatorarchive.languages (language_id) VALUES('hu');
INSERT INTO emulatorarchive.languages (language_id) VALUES('hy');
INSERT INTO emulatorarchive.languages (language_id) VALUES('hz');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ia');
INSERT INTO emulatorarchive.languages (language_id) VALUES('id');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ie');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ig');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ii');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ik');
INSERT INTO emulatorarchive.languages (language_id) VALUES('io');
INSERT INTO emulatorarchive.languages (language_id) VALUES('is');
INSERT INTO emulatorarchive.languages (language_id) VALUES('it');
INSERT INTO emulatorarchive.languages (language_id) VALUES('iu');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ja');
INSERT INTO emulatorarchive.languages (language_id) VALUES('jv');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ka');
INSERT INTO emulatorarchive.languages (language_id) VALUES('kg');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ki');
INSERT INTO emulatorarchive.languages (language_id) VALUES('kj');
INSERT INTO emulatorarchive.languages (language_id) VALUES('kk');
INSERT INTO emulatorarchive.languages (language_id) VALUES('kl');
INSERT INTO emulatorarchive.languages (language_id) VALUES('km');
INSERT INTO emulatorarchive.languages (language_id) VALUES('kn');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ko');
INSERT INTO emulatorarchive.languages (language_id) VALUES('kr');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ks');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ku');
INSERT INTO emulatorarchive.languages (language_id) VALUES('kv');
INSERT INTO emulatorarchive.languages (language_id) VALUES('kw');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ky');
INSERT INTO emulatorarchive.languages (language_id) VALUES('la');
INSERT INTO emulatorarchive.languages (language_id) VALUES('lb');
INSERT INTO emulatorarchive.languages (language_id) VALUES('lg');
INSERT INTO emulatorarchive.languages (language_id) VALUES('li');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ln');
INSERT INTO emulatorarchive.languages (language_id) VALUES('lo');
INSERT INTO emulatorarchive.languages (language_id) VALUES('lt');
INSERT INTO emulatorarchive.languages (language_id) VALUES('lu');
INSERT INTO emulatorarchive.languages (language_id) VALUES('lv');
INSERT INTO emulatorarchive.languages (language_id) VALUES('mg');
INSERT INTO emulatorarchive.languages (language_id) VALUES('mh');
INSERT INTO emulatorarchive.languages (language_id) VALUES('mi');
INSERT INTO emulatorarchive.languages (language_id) VALUES('mk');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ml');
INSERT INTO emulatorarchive.languages (language_id) VALUES('mn');
INSERT INTO emulatorarchive.languages (language_id) VALUES('mr');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ms');
INSERT INTO emulatorarchive.languages (language_id) VALUES('mt');
INSERT INTO emulatorarchive.languages (language_id) VALUES('my');
INSERT INTO emulatorarchive.languages (language_id) VALUES('na');
INSERT INTO emulatorarchive.languages (language_id) VALUES('nb');
INSERT INTO emulatorarchive.languages (language_id) VALUES('nd');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ne');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ng');
INSERT INTO emulatorarchive.languages (language_id) VALUES('nl');
INSERT INTO emulatorarchive.languages (language_id) VALUES('nn');
INSERT INTO emulatorarchive.languages (language_id) VALUES('no');
INSERT INTO emulatorarchive.languages (language_id) VALUES('nr');
INSERT INTO emulatorarchive.languages (language_id) VALUES('nv');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ny');
INSERT INTO emulatorarchive.languages (language_id) VALUES('oc');
INSERT INTO emulatorarchive.languages (language_id) VALUES('oj');
INSERT INTO emulatorarchive.languages (language_id) VALUES('om');
INSERT INTO emulatorarchive.languages (language_id) VALUES('or');
INSERT INTO emulatorarchive.languages (language_id) VALUES('os');
INSERT INTO emulatorarchive.languages (language_id) VALUES('pa');
INSERT INTO emulatorarchive.languages (language_id) VALUES('pi');
INSERT INTO emulatorarchive.languages (language_id) VALUES('pl');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ps');
INSERT INTO emulatorarchive.languages (language_id) VALUES('pt');
INSERT INTO emulatorarchive.languages (language_id) VALUES('qu');
INSERT INTO emulatorarchive.languages (language_id) VALUES('rm');
INSERT INTO emulatorarchive.languages (language_id) VALUES('rn');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ro');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ru');
INSERT INTO emulatorarchive.languages (language_id) VALUES('rw');
INSERT INTO emulatorarchive.languages (language_id) VALUES('sa');
INSERT INTO emulatorarchive.languages (language_id) VALUES('sc');
INSERT INTO emulatorarchive.languages (language_id) VALUES('sd');
INSERT INTO emulatorarchive.languages (language_id) VALUES('se');
INSERT INTO emulatorarchive.languages (language_id) VALUES('sg');
INSERT INTO emulatorarchive.languages (language_id) VALUES('si');
INSERT INTO emulatorarchive.languages (language_id) VALUES('sk');
INSERT INTO emulatorarchive.languages (language_id) VALUES('sl');
INSERT INTO emulatorarchive.languages (language_id) VALUES('sm');
INSERT INTO emulatorarchive.languages (language_id) VALUES('sn');
INSERT INTO emulatorarchive.languages (language_id) VALUES('so');
INSERT INTO emulatorarchive.languages (language_id) VALUES('sq');
INSERT INTO emulatorarchive.languages (language_id) VALUES('sr');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ss');
INSERT INTO emulatorarchive.languages (language_id) VALUES('st');
INSERT INTO emulatorarchive.languages (language_id) VALUES('su');
INSERT INTO emulatorarchive.languages (language_id) VALUES('sv');
INSERT INTO emulatorarchive.languages (language_id) VALUES('sw');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ta');
INSERT INTO emulatorarchive.languages (language_id) VALUES('te');
INSERT INTO emulatorarchive.languages (language_id) VALUES('tg');
INSERT INTO emulatorarchive.languages (language_id) VALUES('th');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ti');
INSERT INTO emulatorarchive.languages (language_id) VALUES('tk');
INSERT INTO emulatorarchive.languages (language_id) VALUES('tl');
INSERT INTO emulatorarchive.languages (language_id) VALUES('tn');
INSERT INTO emulatorarchive.languages (language_id) VALUES('to');
INSERT INTO emulatorarchive.languages (language_id) VALUES('tr');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ts');
INSERT INTO emulatorarchive.languages (language_id) VALUES('tt');
INSERT INTO emulatorarchive.languages (language_id) VALUES('tw');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ty');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ug');
INSERT INTO emulatorarchive.languages (language_id) VALUES('uk');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ur');
INSERT INTO emulatorarchive.languages (language_id) VALUES('uz');
INSERT INTO emulatorarchive.languages (language_id) VALUES('ve');
INSERT INTO emulatorarchive.languages (language_id) VALUES('vi');
INSERT INTO emulatorarchive.languages (language_id) VALUES('vo');
INSERT INTO emulatorarchive.languages (language_id) VALUES('wa');
INSERT INTO emulatorarchive.languages (language_id) VALUES('wo');
INSERT INTO emulatorarchive.languages (language_id) VALUES('xh');
INSERT INTO emulatorarchive.languages (language_id) VALUES('yi');
INSERT INTO emulatorarchive.languages (language_id) VALUES('yo');
INSERT INTO emulatorarchive.languages (language_id) VALUES('za');
INSERT INTO emulatorarchive.languages (language_id) VALUES('zh');
INSERT INTO emulatorarchive.languages (language_id) VALUES('zu');

-- Emulators table
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(3, 'Dioscuri', '0.5.0', 'jar', 'Dioscuri-0.5.0.jar', 'Dioscuri, the modular emulator', 'en', 'dioscuri_050Package.zip', 'zip', '1', FILE_READ('./packages/dioscuri_050Package.zip'),'Set at least the following parameters from the "Configure->Edit Config" menu: BIOS The System BIOS file and Video BIOS file need to be set for the emulator to boot properly. Distributed with the system are two open source BIOSes, the Bochs System BIOS (BIOS-Bochs-latest)and the LGPL''d VGABios, VGABIOS-lgpl-latest. These files are located in the images/bios folder and should be sufficient to boot the system. The BIOS start locations should be left at the default values, 983040 and 786432 respectively. Boot The boot menu indicates which device will be used to boot an operating system after the BIOS has finished booting, and can be either floppy or hard disk. A bootable disk will need to be provided in either the floppy or hard disk menu for this to be successful. ATA/FDC A boot disk needs to be provided for either the hard disk or floppy disk, to successfully boot once the BIOS has finished. The image file can be selected by choosing either ATA or FDC (Floppy Disk Controller)and clicking ‘Image File’, and then selecting the appropriate disk image. The geometry for the disk image must be selected as well; in case of floppy this is the disk size, and for ATA this needs to be specified in cylinders/heads/sectors per track. Start the emulator via Emulator -> "Start process". The system and Video BIOS will boot, followed by the boot device set in the configuration file.');
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(4, 'Vice', '2.2', 'ELF', 'x64', 'VICE, the VersatIle Commodore Emulator (Linux)', 'en', 'LinVICE_22Package.zip', 'zip', '1', FILE_READ('./packages/LinVICE_22Package.zip'),'When the emulator is run, the screen of the emulated machine is displayed in a standard X Window which we will call the emulation window. This window will be updated in real time, displaying the same contents that a real monitor or TV set would. Below the emulation window there is an area which is used to display information about the state of the emulator; we will call this area the status bar. On the extreme left of the status bar, there is a performance meter. This displays the current relative speed of the emulator (as a percentage)and the update frequency (in frames per second). All the machines emulated are PAL, so the update frequency will be 50 frames per second if your system is fast enough to allow emulation at the speed of the real machine. On the extreme right of the status bar, there is a drive status indicator. This is only visible if the hardware-level ("True")1541 emulation is turned on. In that case, the drive status indicator will contain a rectangle emulating the drive LED and will display the current track position of the drive''s read/write head.');
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(5, 'Vice', '2.2', 'exe', 'x64.exe', 'VICE, the VersatIle Commodore Emulator (Windows)', 'en', 'WinVICE_22Package.zip', 'zip', '1', FILE_READ('./packages/WinVICE_22Package.zip'), 'When the emulator is run, the screen of the emulated machine is displayed in a standard X Window which we will call the emulation window. This window will be updated in real time, displaying the same contents that a real monitor or TV set would. Below the emulation window there is an area which is used to display information about the state of the emulator; we will call this area the status bar. On the extreme left of the status bar, there is a performance meter. This displays the current relative speed of the emulator (as a percentage)and the update frequency (in frames per second). All the machines emulated are PAL, so the update frequency will be 50 frames per second if your system is fast enough to allow emulation at the speed of the real machine. On the extreme right of the status bar, there is a drive status indicator. This is only visible if the hardware-level ("True")1541 emulation is turned on. In that case, the drive status indicator will contain a rectangle emulating the drive LED and will display the current track position of the drive''s read/write head.');
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(6, 'Qemu', '0.13.0', 'ELF', 'qemu', 'Qemu, the generic and open source machine emulator and virtualiser (Linux)', 'en', 'LinQemu_0130Package.zip', 'zip', '1', FILE_READ('./packages/LinQemu_0130Package.zip'), 'When QEMU is running, it provides a monitor console for interacting with QEMU. Through various commands, the monitor allows you to inspect the running guest OS, change removable media and USB devices, take screenshots and audio grabs, and control various aspects of the virtual machine. The monitor is accessed from within QEMU by holding down the Control and Alt keys, and pressing CTRL-ALT-2. Once in the monitor, CTRL-ALT-1 switches back to the guest OS. Typing help or ? in the monitor brings up a list of all commands. Alternatively the monitor can be redirected to using the -monitor <dev> command line option Using -monitor stdio will send the monitor to the standard output, this is most useful when using qemu on the command line. See: http://en.wikibooks.org/wiki/QEMU/Monitor');
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(7, 'Qemu', '0.9.0', 'exe', 'qemu.exe', 'Qemu, the generic and open source machine emulator and virtualiser (Windows)', 'en', 'WinQemu_090Package.zip', 'zip', '1', FILE_READ('./packages/WinQemu_090Package.zip'), 'When QEMU is running, it provides a monitor console for interacting with QEMU. Through various commands, the monitor allows you to inspect the running guest OS, change removable media and USB devices, take screenshots and audio grabs, and control various aspects of the virtual machine. The monitor is accessed from within QEMU by holding down the Control and Alt keys, and pressing CTRL-ALT-2. Once in the monitor, CTRL-ALT-1 switches back to the guest OS. Typing help or ? in the monitor brings up a list of all commands. Alternatively the monitor can be redirected to using the -monitor <dev> command line option Using -monitor stdio will send the monitor to the standard output, this is most useful when using qemu on the command line. See: http://en.wikibooks.org/wiki/QEMU/Monitor');
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(8, 'UAE', '0.8.29', 'ELF', 'uae', 'UAE, Amiga emulator for Linux', 'en', 'LinUAE_0829Package.zip', 'zip', '1', FILE_READ('./packages/LinUAE_0829Package.zip'), 'Some short cut commands: f12: winuae settings, Ctrl+F12: toggles fullscreen/windowed modes, pause: pause emulation, pause+end: toggles, rap/normal emulation speed, print Screen+End: takes screenshot, Ctrl+F11: quit emulation, End+F1: Swap disk in DFO.');
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(9, 'UAE', '1.6', 'exe', 'winuae.exe', 'UAE, Amiga emulator for Windows', 'en', 'WinUAE_16Package.zip', 'zip', '1', FILE_READ('./packages/WinUAE_16Package.zip'), 'Some short cut commands: f12: winuae settings, Ctrl+F12: toggles fullscreen/windowed modes, pause: pause emulation, pause+end: toggles, rap/normal emulation speed, print Screen+End: takes screenshot, Ctrl+F11: quit emulation, End+F1: Swap disk in DFO.');
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(10, 'JavaCPC', '6.7', 'exe', 'JavaCPC.exe', 'JavaCPC, Java Amstrad emulator (Windows)', 'en', 'WinJavaCPC_67.zip', 'zip', '1', FILE_READ('./packages/WinJavaCPC_67Package.zip'), 'N/A');
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(11, 'Dioscuri', '0.6.0', 'jar', 'Dioscuri-0.6.0.jar', 'Dioscuri, the modular emulator', 'en', 'dioscuri_060Package.zip', 'zip', '1', FILE_READ('./packages/dioscuri_060Package.zip'), 'Set at least the following parameters from the "Configure->Edit Config" menu: BIOS The System BIOS file and Video BIOS file need to be set for the emulator to boot properly. Distributed with the system are two open source BIOSes, the Bochs System BIOS (BIOS-Bochs-latest)and the LGPL''d VGABios, VGABIOS-lgpl-latest. These files are located in the images/bios folder and should be sufficient to boot the system. The BIOS start locations should be left at the default values, 983040 and 786432 respectively. Boot The boot menu indicates which device will be used to boot an operating system after the BIOS has finished booting, and can be either floppy or hard disk. A bootable disk will need to be provided in either the floppy or hard disk menu for this to be successful. ATA/FDC A boot disk needs to be provided for either the hard disk or floppy disk, to successfully boot once the BIOS has finished. The image file can be selected by choosing either ATA or FDC (Floppy Disk Controller)and clicking ‘Image File’, and then selecting the appropriate disk image. The geometry for the disk image must be selected as well; in case of floppy this is the disk size, and for ATA this needs to be specified in cylinders/heads/sectors per track. Start the emulator via Emulator -> "Start process". The system and Video BIOS will boot, followed by the boot device set in the configuration file.');
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(12, 'Dioscuri', '0.5.0', 'jar', 'Dioscuri-0.5.0.jar', 'Dioscuri, the modular emulator', 'en', 'dioscuri_050Packagev2.zip', 'zip', '2', FILE_READ('./packages/dioscuri_050Packagev2.zip'), 'Set at least the following parameters from the "Configure->Edit Config" menu: BIOS The System BIOS file and Video BIOS file need to be set for the emulator to boot properly. Distributed with the system are two open source BIOSes, the Bochs System BIOS (BIOS-Bochs-latest)and the LGPL''d VGABios, VGABIOS-lgpl-latest. These files are located in the images/bios folder and should be sufficient to boot the system. The BIOS start locations should be left at the default values, 983040 and 786432 respectively. Boot The boot menu indicates which device will be used to boot an operating system after the BIOS has finished booting, and can be either floppy or hard disk. A bootable disk will need to be provided in either the floppy or hard disk menu for this to be successful. ATA/FDC A boot disk needs to be provided for either the hard disk or floppy disk, to successfully boot once the BIOS has finished. The image file can be selected by choosing either ATA or FDC (Floppy Disk Controller)and clicking ‘Image File’, and then selecting the appropriate disk image. The geometry for the disk image must be selected as well; in case of floppy this is the disk size, and for ATA this needs to be specified in cylinders/heads/sectors per track. Start the emulator via Emulator -> "Start process". The system and Video BIOS will boot, followed by the boot device set in the configuration file.');
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(13, 'BeebEm', '0.0.13', 'ELF', 'beebem', 'BeebEm, BBC Micro and Master 128 Emulator (Linux)', 'en', 'LinBeebEm_0013Package.zip', 'zip', '1', FILE_READ('./packages/LinBeebEm_0013Package.zip'), 'N/A');
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(14, 'BeebEm', '4.13', 'exe', 'beebem.exe', 'BeebEm, BBC Micro and Master 128 Emulator (Windows)', 'en', 'WinBeebEm_413Package.zip', 'zip', '1', FILE_READ('./packages/WinBeebEm_413Package.zip'), 'N/A');
INSERT INTO emulatorarchive.emulators (emulator_id,name,version,exec_type,exec_name,description,language_id,package_name,package_type,package_version,package,user_instructions)VALUES(15, 'Thomson', '1.0', 'exe', 'bnftowin.exe', 'Thomson Emulator (Windows)', 'fr', 'WinThomson_10Package.zip', 'zip', '1', FILE_READ('./packages/WinThomson_10Package.zip'), 'N/A');

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