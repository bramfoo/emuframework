-- EA_CreateTables.sql: Creates tables for schema 'emulatorarchive'
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


-- Table for emulators
CREATE TABLE emulators
(
  emulator_id INT(4) NOT NULL PRIMARY KEY,
  name VARCHAR2(250) NOT NULL,
  version VARCHAR2(250),
  exec_type VARCHAR2(250),
  exec_name VARCHAR2(250),
  exec_dir VARCHAR2(250),
  description VARCHAR2(500),
  package_name VARCHAR2(250),
  package_type VARCHAR2(250),
  package_version VARCHAR2(250),
  package BLOB,
  user_instructions VARCHAR2(2000)
);

-- Table for hardware types
CREATE TABLE hardware
(
  hardware_id INT(4) NOT NULL PRIMARY KEY,
  name VARCHAR2(250) NOT NULL UNIQUE
);

-- Junction table for the emulators table and the hardware table
CREATE TABLE emus_hardware
(
  emulator_id INT(4) NOT NULL,
  hardware_id INT(4) NOT NULL,
  FOREIGN KEY (emulator_id) REFERENCES emulators (emulator_id),
  FOREIGN KEY (hardware_id) REFERENCES hardware (hardware_id),
  UNIQUE(emulator_id,hardware_id)
);


-- Table for the image formats
CREATE TABLE imageformats
(
    imageformat_id INT(4) NOT NULL PRIMARY KEY,
    name VARCHAR2(250) NOT NULL UNIQUE
);

-- Junction table for image formats table and the emulators table
CREATE TABLE emus_imageformats
(
  emulator_id INT(4) NOT NULL,
  imageformat_id INT(4) NOT NULL,
  FOREIGN KEY (emulator_id) REFERENCES emulators (emulator_id),
  UNIQUE(emulator_id,imageformat_id)
);