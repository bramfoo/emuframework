-- SWA_CreateTables.sql: Creates tables for schema 'softwarearchive'
--                       This script needs to be run as system/root/administrator
--                       For H2 this is 'sa'
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

-- Table for file formats
CREATE TABLE fileformats
(
  fileformat_id VARCHAR2(16) NOT NULL PRIMARY KEY,
  name VARCHAR2(250) NOT NULL,
  version VARCHAR2(250),
  description VARCHAR2(1000),
  reference VARCHAR2(500)
);

-- Table for applications
CREATE TABLE apps
(
  app_id VARCHAR2(16) NOT NULL PRIMARY KEY,
  name VARCHAR2(250) NOT NULL,
  version VARCHAR2(250),
  description VARCHAR2(500),
  creator VARCHAR2(500),
  release_date VARCHAR2(500),
  license VARCHAR2(500),
  language VARCHAR2(500),
  reference VARCHAR2(500),
  user_instructions VARCHAR2(2000)
);

-- Table for operating systems
CREATE TABLE opsys
(
  opsys_id VARCHAR2(16) NOT NULL PRIMARY KEY,
  name VARCHAR2(250) NOT NULL,
  version VARCHAR2(250),
  description VARCHAR2(500),
  creator VARCHAR2(500),
  release_date VARCHAR2(500),
  license VARCHAR2(500),
  language VARCHAR2(500),
  reference VARCHAR2(500)
);

-- Table for platforms
CREATE TABLE platforms
(
  platform_id VARCHAR2(16) NOT NULL PRIMARY KEY,
  name VARCHAR2(250) NOT NULL,
  description VARCHAR2(500),
  creator VARCHAR2(500),
  production_start VARCHAR2(500),
  production_end VARCHAR2(500),
  reference VARCHAR2(500)
);

-- Table for imageformats
CREATE TABLE imageformats
(
  imageformat_id VARCHAR2(16) NOT NULL PRIMARY KEY,
  name VARCHAR2(250) NOT NULL
);

-- Table for images
CREATE TABLE images
(
  image_id VARCHAR2(16) NOT NULL PRIMARY KEY,
  description VARCHAR2(1000) NOT NULL,
  imageformat_id VARCHAR2(16) NOT NULL,
  platform_id VARCHAR2(16) NOT NULL,
  FOREIGN KEY (imageformat_id) REFERENCES imageformats (imageformat_id),
  FOREIGN KEY (platform_id) REFERENCES platforms (platform_id)
);

-- Table for image BLOBs
CREATE TABLE imageblobs
(
  image_id VARCHAR2(16) NOT NULL PRIMARY KEY,
  image BLOB NOT NULL,
  FOREIGN KEY (image_id) REFERENCES images (image_id),
);

-- Junction table for fileformat to application link
CREATE TABLE fileformats_apps
(
  fileformat_id VARCHAR2(16) NOT NULL,
  app_id VARCHAR2(16) NOT NULL,
  FOREIGN KEY (fileformat_id) REFERENCES fileformats (fileformat_id),
  FOREIGN KEY (app_id) REFERENCES apps (app_id)
);

-- Junction table for fileformat to operating system link
CREATE TABLE fileformats_opsys
(
  fileformat_id VARCHAR2(16) NOT NULL,
  opsys_id VARCHAR2(16) NOT NULL,
  FOREIGN KEY (fileformat_id) REFERENCES fileformats (fileformat_id),
  FOREIGN KEY (opsys_id) REFERENCES opsys (opsys_id)
);

-- Junction table for fileformat to platform link
CREATE TABLE fileformats_platform
(
  fileformat_id VARCHAR2(16) NOT NULL,
  platform_id VARCHAR2(16) NOT NULL,
  FOREIGN KEY (fileformat_id) REFERENCES fileformats (fileformat_id),
  FOREIGN KEY (platform_id) REFERENCES platforms (platform_id)
);

-- Junction table for the operating systems to platform link
CREATE TABLE opsys_platform
(
  opsys_id VARCHAR2(16) NOT NULL,
  platform_id VARCHAR2(16) NOT NULL,
  FOREIGN KEY (opsys_id) REFERENCES opsys (opsys_id),
  FOREIGN KEY (platform_id) REFERENCES platforms (platform_id)
);

-- Junction table for the applications to operating systems link
CREATE TABLE apps_opsys
(
  app_id VARCHAR2(16) NOT NULL,
  opsys_id VARCHAR2(16) NOT NULL,
  FOREIGN KEY (app_id) REFERENCES apps (app_id),
  FOREIGN KEY (opsys_id) REFERENCES opsys (opsys_id)
);

-- Junction table for the applications to images link
CREATE TABLE apps_images
(
  app_id VARCHAR2(16) NOT NULL,
  image_id VARCHAR2(16) NOT NULL,
  FOREIGN KEY (app_id) REFERENCES apps (app_id),
  FOREIGN KEY (image_id) REFERENCES images (image_id)
);

-- Junction table for the operating system to images link
CREATE TABLE opsys_images
(
  opsys_id VARCHAR2(16) NOT NULL,
  image_id VARCHAR2(16) NOT NULL,
  FOREIGN KEY (opsys_id) REFERENCES opsys (opsys_id),
  FOREIGN KEY (image_id) REFERENCES images (image_id)
);

-- Views
CREATE VIEW image_package AS
SELECT imgs.image_id,
       imgs.description as "IMAGE_DESCRIPTION",
       imgform.name as "IMAGE_FORMAT_NAME"
FROM images imgs, imageformats imgform
WHERE imgs.imageformat_id = imgform.imageformat_id;

CREATE VIEW pf_package AS
SELECT imgs.image_id,
       pf.platform_id as "PF_ID",
       pf.name as "PF_NAME",
       pf.description as "PF_DESCRIPTION",
       pf.creator as "PF_CREATOR",
       pf.production_start as "PF_PRODUCTION_START",
       pf.production_end as "PF_PRODUCTION_END",
       pf.reference as "PF_REFERENCE"
FROM images imgs
JOIN platforms pf
ON imgs.platform_id = pf.platform_id;

CREATE VIEW os_package AS
SELECT imgs.image_id,
       ops.opsys_id as "OS_ID",
       ops.name as "OS_NAME",
       ops.version as "OS_VERSION",
       ops.description as "OS_DESCRIPTION",
       ops.creator as "OS_CREATOR",
       ops.release_date as "OS_RELEASE_DATE",
       ops.license as "OS_LICENSE",
       ops.language as "OS_LANGUAGE",
       ops.reference as "OS_REFERENCE"
FROM images imgs
INNER JOIN opsys_images ops_img
ON imgs.image_id = ops_img.image_id JOIN opsys ops
ON ops_img.opsys_id = ops.opsys_id;

CREATE VIEW app_package AS
SELECT imgs.image_id,
       apps.app_id as "APP_ID",
       apps.name as "APP_NAME",
       apps.version as "APP_VERSION",
       apps.description as "APP_DESCRIPTION",
       apps.creator as "APP_CREATOR",
       apps.release_date as "APP_RELEASE_DATE",
       apps.license as "APP_LICENSE",
       apps.language as "APP_LANGUAGE",
       apps.reference as "APP_REFERENCE",
       apps.user_instructions as "APP_USER_INSTRUCTIONS"
FROM images imgs
INNER JOIN apps_images app_img
ON imgs.image_id = app_img.image_id JOIN apps
ON app_img.app_id = apps.app_id
ORDER BY imgs.image_id ASC;

CREATE VIEW ff_pf_pathway AS
SELECT ff.fileformat_id as "FILEFORMAT_ID",
       ff.name as "FILEFORMAT_NAME",
       pf.platform_id as "PLATFORM_ID",
       pf.name as "PLATFORM_NAME"
FROM fileformats ff
INNER JOIN fileformats_platform ff_pf
ON ff.fileformat_id = ff_pf.fileformat_id JOIN platforms pf
ON ff_pf.platform_id = pf.platform_id;

CREATE VIEW ff_os_pf_pathway AS
SELECT ff.fileformat_id as "FILEFORMAT_ID",
       ff.name as "FILEFORMAT_NAME",
       os.opsys_id as "OS_ID",
       os.name as "OS_NAME",
       pf.platform_id as "PLATFORM_ID",
       pf.name as "PLATFORM_NAME"
FROM fileformats ff
INNER JOIN fileformats_opsys ff_os
ON ff.fileformat_id = ff_os.fileformat_id JOIN opsys os
ON ff_os.opsys_id = os.opsys_id JOIN opsys_platform os_pf
ON ff_os.opsys_id = os_pf.opsys_id JOIN platforms pf
ON os_pf.platform_id = pf.platform_id;

CREATE VIEW ff_app_os_pf_pathway AS
SELECT ff.fileformat_id as "FILEFORMAT_ID", 
       ff.name as "FILEFORMAT_NAME",
       app.app_id as "APP_ID",
       app.name as "APP_NAME",
       os.opsys_id as "OS_ID",
       os.name as "OS_NAME",
       pf.platform_id as "PLATFORM_ID",
       pf.name as "PLATFORM_NAME"
FROM fileformats ff
INNER JOIN fileformats_apps ff_apps
ON ff.fileformat_id = ff_apps.fileformat_id JOIN apps app
ON ff_apps.app_id = app.app_id JOIN apps_opsys app_os
ON ff_apps.app_id = app_os.app_id JOIN opsys os
ON app_os.opsys_id = os.opsys_id JOIN opsys_platform os_pf
ON os.opsys_id = os_pf.opsys_id JOIN platforms pf
ON os_pf.platform_id = pf.platform_id;

CREATE VIEW pathways AS
SELECT ff.fileformat_id as "FILEFORMAT_ID",
       ff.name as "FILEFORMAT_NAME",
       app.app_id as "APP_ID",
       app.name as "APP_NAME",
       os.opsys_id as "OS_ID",
       os.name as "OS_NAME",
       pf.platform_id as "PLATFORM_ID",
       pf.name as "PLATFORM_NAME"
FROM fileformats ff
LEFT OUTER JOIN fileformats_apps ff_apps
ON ff.fileformat_id = ff_apps.fileformat_id LEFT OUTER JOIN fileformats_opsys ff_os
ON ff.fileformat_id = ff_os.fileformat_id LEFT OUTER JOIN fileformats_platform ff_pf
ON ff.fileformat_id = ff_pf.fileformat_id LEFT OUTER JOIN apps app
ON ff_apps.app_id = app.app_id LEFT OUTER JOIN apps_opsys app_os
ON ff_apps.app_id = app_os.app_id LEFT OUTER JOIN opsys os
ON (app_os.opsys_id = os.opsys_id OR ff_os.opsys_id = os.opsys_id) LEFT OUTER JOIN opsys_platform os_pf
ON (os.opsys_id = os_pf.opsys_id OR ff_os.opsys_id = os_pf.opsys_id) LEFT OUTER JOIN platforms pf
ON (os_pf.platform_id = pf.platform_id OR ff_pf.platform_id = pf.platform_id);