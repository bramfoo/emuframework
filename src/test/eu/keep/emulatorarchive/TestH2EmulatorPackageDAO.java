/*
* $Revision $ $Date$
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
* Project Title: Emulator Archive (EA)$
 */

package eu.keep.emulatorarchive;

import java.io.IOException;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.keep.emulatorarchive.H2EmulatorPackageDAO;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * A Junit Test class for {@link eu.keep.emulatorarchive.H2EmulatorPackageDAO}
 * @author David Michel
 */
public class TestH2EmulatorPackageDAO {
   
    private Connection           conn;
    private H2EmulatorPackageDAO dao;

    protected final static String propertiesFile = "test.properties";
    protected static Properties   props;

    @Before
    public void setUp() {

        // read user.properties
        try {

            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("eu/keep/" + propertiesFile);
            props = getProperties(is);

        }
        catch(IOException e){
            e.printStackTrace();
            fail(e.toString());
        }

        // Set up the db connection
        try {

           // open db connection
            setupDBConnection(props);

            // set up DAO
            dao = new H2EmulatorPackageDAO(conn);

        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @After
    public void tearDown() {
        try {
            conn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetPackageVersion() {

        try {
            assertEquals("wrong package version", "1", dao.getPackageVersion(1));
            assertEquals("wrong package version", "2", dao.getPackageVersion(2));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmulatorCount() {

        try {
            assertEquals("wrong emulator count", 2, dao.getEmulatorCount());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmulatorIDs() {

        try {
            List<Integer> list = dao.getEmulatorIDs();
            assertEquals("wrong emulator ID", new Integer(1), list.get(0));
            assertEquals("wrong emulator ID", new Integer(2), list.get(1));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetHardwareIDs() {

        try {
            Set<String> list = dao.getHardwareIDs();
            assertTrue("hardware id missing", list.contains("1"));
            assertTrue("hardware id missing", list.contains("2"));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmulatorPackage() {

//        int a, b;

        try {
             /*
            InputStream testData0 = new FileInputStream(".packages/dioscuri_050Package.zip");
            InputStream dbData0 = dao.getEmulatorPackage(1);
            while ((a = testData0.read()) != -1 && (b = dbData0.read()) != -1) {
                assertEquals("Bytes from testData and dbData differ", a, b);
            }

            InputStream testData1 = new FileInputStream(".packages/LinVICE_22Package.zip");
            InputStream dbData1 = dao.getEmulatorPackage(2);
            while ((a = testData1.read()) != -1 && (b = dbData1.read()) != -1) {
                assertEquals("Bytes from testData and dbData differ", a, b);
            }
               */
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmulatorPackageFileName() {

        try {
            assertEquals("wrong emulator package file name", "dioscuri_043Package.zip", dao
                    .getEmulatorPackageFileName(1));
            assertEquals("wrong emulator package file name", "LinVICE_22Package.zip", dao
                    .getEmulatorPackageFileName(2));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmulatorName() {

        try {
            assertEquals("wrong emulator name", "Dioscuri", dao.getEmulatorName(1));
            assertEquals("wrong emulator name", "Vice", dao.getEmulatorName(2));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmulatorVersion() {

        try {
            assertEquals("wrong emulator version", "0.4.3", dao.getEmulatorVersion(1));
            assertEquals("wrong emulator version", "2.2", dao.getEmulatorVersion(2));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmulatorDescription() {

        try {
            assertEquals("wrong emulator description", "Dioscuri, the modular emulator", dao
                    .getEmulatorDescription(1));
            assertEquals("wrong emulator description",
                    "VICE, the VersatIle Commodore Emulator (Linux)", dao.getEmulatorDescription(2));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmulatorExecType() {

        try {
            assertEquals("wrong emulator executable type", "jar", dao.getEmulatorExecType(1));
            assertEquals("wrong emulator executable type", "ELF", dao.getEmulatorExecType(2));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmulatorExecName() {

        try {
            assertEquals("wrong emulator executable name", "Dioscuri-0.4.3.jar", dao
                    .getEmulatorExecName(1));
            assertEquals("wrong emulator executable name", "x64", dao.getEmulatorExecName(2));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmulatorExecDir() {

        try {
            assertNull("wrong emulator executable directory", dao.getEmulatorExecDir(1));
            assertNull("wrong emulator executable directory", dao.getEmulatorExecDir(2));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetImageFormats() {

        try {
            List<String> list0 = dao.getImageFormats(1);
            assertEquals("wrong emulator ID", "FAT12", list0.get(0));
            assertEquals("wrong emulator ID", "FAT16", list0.get(1));

            List<String> list1 = dao.getImageFormats(2);
            assertEquals("wrong emulator ID", "D64", list1.get(0));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetHardware() {

        try {
            List<String> list0 = dao.getHardware(1);
            assertEquals("wrong emulator ID", "x86", list0.get(0));

            List<String> list1 = dao.getHardware(2);
            assertEquals("wrong emulator ID", "c64", list1.get(0));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmulatorPackageType() {

        try {
            assertEquals("wrong emulator package type", "zip", dao.getEmulatorPackageType(1));
            assertEquals("wrong emulator package type", "zip", dao.getEmulatorPackageType(2));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmuID() {

        try {
            String hw0 = "c64";
            List<Integer> list0 = dao.getEmuID(hw0);
            assertEquals("wrong list of emulator id for hardware " + hw0, new Integer(2), list0
                    .get(0));

            String hw1 = "x86";
            List<Integer> list1 = dao.getEmuID(hw1);
            assertEquals("wrong list of emulator id for hardware " + hw1, new Integer(1), list1
                    .get(0));

        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }


    private Properties getProperties(InputStream inputStream) throws IOException {
        // Read the properties file
        Properties props = new Properties();

        try {
            props.load(inputStream);
            System.out.println("Correctly read properties file: " + inputStream);
        } catch (IOException e) {
            System.out.println("Failed to read properties file [" + inputStream + "]: "
                    + e.toString());
            throw e;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                // Hmm... hoping not to get this far into catching exceptions;
                // we'll just log it and proceed...
                System.out.println("Failed to close open file: [" + inputStream + "]: "
                        + e.toString());
            }
        }
        return props;
    }

    private void setupDBConnection(Properties props) {

        // Set up the db connection
        // Register the JDBC driver
        try {
            String driver = props.getProperty("h2.db.driver");
            Class.forName(driver);
        }
        catch (ClassNotFoundException e) {
            System.out.println("Cannot set up database driver");
            throw new RuntimeException(e);
        }
        System.out.println("Successfully set up database driver");

        int attempt = 0;
        String dbUrl = props.getProperty("h2.jdbc.prefix")
                + props.getProperty("h2.db.url") + props.getProperty("h2.db.exists")
                + props.getProperty("h2.db.schema");
        String dbUser = props.getProperty("h2.db.user");
        String dbPasswd = props.getProperty("h2.db.userpassw");
        while (attempt < 5) {

            try {
                conn = DriverManager.getConnection(dbUrl, dbUser, dbPasswd);
                break;
            }
            catch (SQLException e) {
                System.out.println("Failed database connection attempt: " + attempt);
            }

            attempt++;
        }

        if (attempt >= 5) {
            System.out.println("Cannot connect to database: " + dbUrl);
            throw new RuntimeException();
        }
        else {
            System.out.println("Successfully connected to database" + dbUrl);
        }
    }

}