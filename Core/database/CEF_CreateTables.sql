-- CEF_CreateTables.sql: Creates tables for schema 'engine'
--
-- $Revision$ $Date$ $Author$
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

-- Table for whitelisted emulators
CREATE TABLE emulator_whitelist
(
  emulator_id INT(4) NOT NULL PRIMARY KEY,
  emulator_descr VARCHAR2(500)
);

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
INSERT INTO emulator_whitelist (emulator_id, emulator_descr) values (15, 'Thomson (Windows)');
