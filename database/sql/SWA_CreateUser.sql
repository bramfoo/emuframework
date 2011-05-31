-- SWA_CreateUser.sql: Creates a Software Archive admin user.
--                     This script needs to be run as system/root/administrator
--                     For H2 this is 'sa'
--
-- $Revision: 685 $ $Date: 2011-05-31 00:17:31 +0100 (Tue, 31 May 2011) $ $Author: BLohman $
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
-- Author: Bram Lohman
-- Author: David Michel
--
-- Arguments:
--             none (H2 doesn't accept any)
--


-- create user
create user swa password 'swa';
alter user swa admin true;