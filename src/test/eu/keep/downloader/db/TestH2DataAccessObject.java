/*
* $Revision: 803 $ $Date: 2011-05-31 00:27:06 +0100 (Tue, 31 May 2011) $
* $Author: BLohman $
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
import java.util.ArrayList;
import java.util.List;
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
    public void testGetRegistries() {
        try {
            DBRegistry testReg1 = new DBRegistry();
            testReg1.setRegistryID(2);
            testReg1.setName("PRONOM/PCR");
            testReg1.setUrl("http://www.nationalarchives.gov.uk/pronom/");
            testReg1.setClassName("eu.keep.characteriser.registry.PronomRegistry");
            testReg1.setTranslationView("EF_PCR_FORMATS");
            testReg1.setEnabled(true);
            testReg1.setDescription("PRONOM/PCR, the one and only registry");
            testReg1.setComment("Does not support pathways yet");

            List<DBRegistry> regListdb;
            regListdb = regDAO.getRegistries();

            assertFalse("List shouldn't be empty", regListdb.isEmpty());
            assertEquals("Wrong size", 2, regListdb.size());
            assertEquals("Wrong registry ID", testReg1.getRegistryID(), regListdb.get(0).getRegistryID());
            assertEquals("Wrong registry name", testReg1.getName(), regListdb.get(0).getName());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

//    @Test
    public void testSetRegistries() {

        try {

            List<DBRegistry> regList = new ArrayList<DBRegistry>();
            DBRegistry testReg3 = new DBRegistry();
            testReg3.setRegistryID(1);
            testReg3.setName("Test Registry 3");
            testReg3.setUrl("http://www.test3.com");
            testReg3.setClassName("eu.keep.registry.TestRegistryThree");
            testReg3.setEnabled(false);
            testReg3.setDescription("Test Registry 3 dummy data");
            testReg3.setComment("Test Registry 3 dummy comment");
            regList.add(testReg3);

            DBRegistry testReg4 = new DBRegistry();
            testReg4.setRegistryID(2);
            testReg4.setName("Test Registry 4");
            testReg4.setUrl("http://www.testFour.eu");
            testReg4.setClassName("eu.keep.registry.TestRegistryFour");
            testReg4.setEnabled(true);
            testReg4.setDescription("Test Registry 4 dummy data");
            testReg4.setComment("Test Registry 4 dummy comment");
            regList.add(testReg4);

            // This will erase the current contents and replace them with the above
            regDAO.updateRegistries(regList);
            //regDAO.setRegistries(regList);

            List<DBRegistry> regListdb;
            regListdb = regDAO.getRegistries();

            assertEquals(testReg3.getRegistryID(), regListdb.get(0).getRegistryID());
            assertEquals(testReg3.getName(), regListdb.get(0).getName());
            assertEquals(testReg3.getUrl(), regListdb.get(0).getUrl());
            assertEquals(testReg3.getClassName(), regListdb.get(0).getClassName());
            assertFalse(regListdb.get(0).isEnabled());
            assertEquals(testReg3.getDescription(), regListdb.get(0).getDescription());
            assertEquals(testReg3.getComment(), regListdb.get(0).getComment());

            assertEquals(testReg4.getRegistryID(), regListdb.get(1).getRegistryID());
            assertEquals(testReg4.getName(), regListdb.get(1).getName());
            assertEquals(testReg4.getUrl(), regListdb.get(1).getUrl());
            assertEquals(testReg4.getClassName(), regListdb.get(1).getClassName());
            assertTrue(regListdb.get(1).isEnabled());
            assertEquals(testReg4.getDescription(), regListdb.get(1).getDescription());
            assertEquals(testReg4.getComment(), regListdb.get(1).getComment());

        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
}
