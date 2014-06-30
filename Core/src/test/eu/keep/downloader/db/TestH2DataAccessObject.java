/*
* $Revision$ $Date$
* $Author$
* $header:
* Copyright (c) 2009-2011 Tessella plc.
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* For more information about this project, visit
*   http://www.keep-project.eu/
*   http://emuframework.sourceforge.net/
* or contact us via email:
*   blohman at users.sourceforge.net
*   dav_m at users.sourceforge.net
*   bkiers at users.sourceforge.net
* Developed by:
*   Tessella plc <www.tessella.com>
*   Koninklijke Bibliotheek <www.kb.nl>
*   KEEP <www.keep-project.eu>
* Project Title: Core Emulation Framework (Core EF)$
*/

package eu.keep.downloader.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import eu.keep.util.FileUtilities;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.keep.downloader.DataAccessObject;
import eu.keep.downloader.db.H2DataAccessObject;

import static org.junit.Assert.*;

/**
 * A Junit Test class for {@link eu.keep.downloader.db.TestH2DataAccessObject}
 * @author Bram Lohman
 */
public class TestH2DataAccessObject {

    DataAccessObject regDAO;

    protected Logger              logger         = Logger.getLogger(this.getClass());

    protected final static String propertiesFile = "test.properties";
    protected static Properties   props;
    Connection                    conn;

    @Before
    public void setupRegistryDAO() {
        try {
            props = FileUtilities.getProperties(propertiesFile);
            String driverName = props.getProperty("test.h2.db.driver");
            String dbUrl = props.getProperty("test.h2.jdbc.prefix")
            + props.getProperty("test.h2.db.url") + props.getProperty("test.h2.db.exists");
            String dbUrlEngine = dbUrl + props.getProperty("test.h2.db.schema");
            String dbUser = props.getProperty("test.h2.db.user");
            String dbPasswd = props.getProperty("test.h2.db.userpassw");

            int connectionAttempts = 5;

            conn = DBUtil.establishConnection(driverName, dbUrlEngine, dbUser,
                    dbPasswd, connectionAttempts);
            
            regDAO = new H2DataAccessObject(conn);
            logger.info("Successfully setup registry DAO");
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @After
    public void closeDB() {

        try {
            conn.close();
        }
        catch (SQLException e) {
            logger.error("Cannot close connection to database: " + e.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    
    @Test
    public void testGetWhitelistedEmus() {
        try {
        	Map<Integer, String> whitelist = regDAO.getWhitelistedEmus();       	
        	assertEquals("Incorrect number of whitelisted emulators. ", 11, whitelist.size());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
    
    @Test
    public void testEditWhitelist() {
        try {
        	int initialSize = regDAO.getWhitelistedEmus().size();
        	
        	Integer emuID = 10;
        	String emuName = "JavaCPC (Win)";
        	
        	// Remove emulator JavaCPC (Win)
        	boolean success = regDAO.unListEmulator(emuID);       	
        	assertTrue("Negative response when unlisting emulator. ", success);
        	Map<Integer, String> whitelist = regDAO.getWhitelistedEmus();
        	assertEquals("Emulator not removed from whitelist. ", initialSize-1, whitelist.size());
        	assertFalse("Emulator not removed from whitelist. ", whitelist.containsKey(emuID));
        	assertFalse("Emulator not removed from whitelist. ", whitelist.containsValue(emuName));
        	
        	// Add new emulator JavaCPC (Windows)
        	String newName = "JavaCPC (Windows)";
        	boolean success2 = regDAO.whiteListEmulator(emuID, "JavaCPC (Windows)");       	
        	assertTrue("Negative response when adding emulator to whitelist. ", success2);
        	Map<Integer, String> newWhitelist = regDAO.getWhitelistedEmus();
        	assertEquals("Emulator not added to whitelist. ", initialSize, newWhitelist.size());
        	assertTrue("Emulator not added to whitelist. ", newWhitelist.containsKey(emuID));
        	assertTrue("Emulator not added to whitelist. ", newWhitelist.containsValue(newName));        	
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
    
}
