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
* Project Title: Software Archive (SWA)$
*/

package eu.keep.softwarearchive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * A Junit Test class for {@link eu.keep.softwarearchive.H2SoftwarePackageDAO}
 * @author David Michel
 */
public class TestH2SoftwarePackageDAO {

    private Connection           conn;
    private H2SoftwarePackageDAO dao;
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
            dao = new H2SoftwarePackageDAO(conn);
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
    public void testGetImageCount() {
        try {
            int result = dao.getImageIDs().size();
            assertEquals("software image count is incorrect", 2, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetImageFile() {
        try {
           InputStream dbData = dao.getImageFile("IMG-1");
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Error retrieving image file id=1: " + e.toString());
        }
    }

    @Test
    public void testGetImageIDs() {
        try {
            List<String> listIds = dao.getImageIDs();

            assertEquals("Incorrect size of image ID list", 2, listIds.size());
            assertEquals("Incorrect Image ID number", "IMG-1000", listIds.get(0));
            assertEquals("Incorrect Image ID number", "IMG-1001", listIds.get(1));
            
//	These are proprietary software.
//            assertEquals("Incorrect Image ID number", "IMG-2000", listIds.get(2));
//            assertEquals("Incorrect Image ID number", "IMG-2001", listIds.get(3));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetImageIDs2() {
        try {
            List<String> listIds1 = dao.getImageIDs("FreeDOS");

            assertEquals("Incorrect size of image ID list", 1, listIds1.size());
            assertEquals("Incorrect Image ID number", "IMG-1000", listIds1.get(0));

            List<String> listIds2 = dao.getImageIDs("MSDOS");
            assertEquals("Incorrect size of image ID list",0, listIds2.size());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
    
    @Test
    public void testGetImageIDs3() {
        try {
            List<String> listIds1 = dao.getImageIDs("FreeDOS Edit", "FreeDOS");

            assertEquals("Incorrect size of image ID list", 1, listIds1.size());
            assertEquals("Incorrect Image ID number", "IMG-1000", listIds1.get(0));

            List<String> listIds2 = dao.getImageIDs("MSDOS", "Edit");
            assertEquals("Incorrect size of image ID list",0, listIds2.size());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetImageItems() {
    	List<String> col = new ArrayList<String>();
    	col.add("IMAGE_DESCRIPTION");
    	col.add("IMAGE_FORMAT_NAME");
    	
        try {
            List<List<String>> ImageDesc1 = dao.getViewData("IMG-1000", true, "img", col);
            assertEquals("Incorrect Image description for image id=1", "FreeDOS version 0.9", ImageDesc1.get(0).get(0));
            assertEquals("Incorrect Image format for image id=1", "FAT16", ImageDesc1.get(0).get(1));

            List<List<String>> ImageDesc2 = dao.getViewData("IMG-1001", true, "img", col);
            assertEquals("Incorrect Image description for image id=1", "Damn Small Linux version 4.4.10", ImageDesc2.get(0).get(0));
            assertEquals("Incorrect Image description for image id=1", "EXT3", ImageDesc2.get(0).get(1));

//        	This is proprietary software.
//            List<List<String>> ImageDesc3 = dao.getViewData("IMG-2002", true, "img", col);
//            assertEquals("Incorrect Image description for image id=1", "Amiga KickStart ROM", ImageDesc3.get(0).get(0));
//            assertEquals("Incorrect Image description for image id=1", "ROM", ImageDesc3.get(0).get(1));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
    
    @Test
    public void testGetOsName() {
    	List<String> col = new ArrayList<String>();
    	col.add("OS_NAME");
    	col.add("OS_DESCRIPTION");
    	col.add("OS_VERSION");
        try {
            List<List<String>> OsName1 = dao.getViewData("IMG-1000", true, "os", col);
            assertEquals("Incorrect number of OS's returned. ", 1, OsName1.size());
            assertEquals("Incorrect OS name for image id=IMG-1000", "FreeDOS", OsName1.get(0).get(0));
            assertEquals("Incorrect OS description for image id=IMG-1000", 
            		"Open source DOS for x86", OsName1.get(0).get(1));
            assertEquals("Incorrect OS version for image id=IMG-1000", "0.9", OsName1.get(0).get(2));

            List<List<String>> OsName2 = dao.getViewData("IMG-1001", true, "os", col);
            assertEquals("Incorrect number of OS's returned. ", 1, OsName2.size());
            assertEquals("Incorrect OS name for image id=IMG-1001", "Damn Small Linux", OsName2.get(0).get(0));
            assertEquals("Incorrect OS description for image id=IMG-1001", 
            		"Versatile mini desktop oriented Linux distribution", OsName2.get(0).get(1));
            assertEquals("Incorrect OS version for image id=IMG-1001", "4.4.10", OsName2.get(0).get(2));

//        	This is proprietary software.
//            List<List<String>> OsName3 = dao.getViewData("IMG-2001", true, "os", col);
//            assertEquals("Incorrect number of OS's returned. ", 1, OsName3.size());
//            assertEquals("Incorrect OS name for image id=3", "Windows 98", OsName3.get(0).get(0));
//            assertEquals("Incorrect OS description for image id=3", "Microsoft Windows for x86", OsName3.get(0).get(1));
//            assertEquals("Incorrect OS version for image id=3", "1.0", OsName3.get(0).get(2));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetAppName() {
    	List<String> col = new ArrayList<String>();
    	col.add("APP_NAME");
    	col.add("APP_VERSION");
    	col.add("APP_DESCRIPTION");
        try {
            List<List<String>> appNames1 = dao.getViewData("IMG-1000", true, "app", col);
            assertEquals("Incorrect size of app name list for image id=1", 2, appNames1.size());
            assertEquals("Incorrect app name", "FreeDOS Edit", appNames1.get(0).get(0));
            assertEquals("Incorrect app name", "Blocek", appNames1.get(1).get(0));
            assertEquals("Incorrect app version", "1.0", appNames1.get(0).get(1));
            assertEquals("Incorrect app verion", "1.33b", appNames1.get(1).get(1));
            assertEquals("Incorrect app description", "FreeDOS improved clone of MS-DOS Edit", appNames1.get(0).get(2));
            assertEquals("Incorrect app description", "Image viewer for DOS", appNames1.get(1).get(2));

            List<List<String>> appNames2 = dao.getViewData("IMG-1001", true, "app", col);
            assertEquals("Incorrect size of app name list for image id=2", 5, appNames2.size());
            assertEquals("Incorrect app name", "Xzgv", appNames2.get(0).get(0));
            assertEquals("Incorrect app name", "Xpdf", appNames2.get(1).get(0));
            assertEquals("Incorrect app name", "Beaver", appNames2.get(2).get(0));
            assertEquals("Incorrect app name", "Firefox", appNames2.get(3).get(0));
            assertEquals("Incorrect app name", "MS Office Viewer", appNames2.get(4).get(0));
            assertNull("Incorrect app version", appNames2.get(0).get(1));
            assertNull("Incorrect app version", appNames2.get(1).get(1));
            assertNull("Incorrect app version", appNames2.get(2).get(1));
            assertNull("Incorrect app version", appNames2.get(3).get(1));
            assertNull("Incorrect app version", appNames2.get(4).get(1));
            assertTrue("Incorrect app description", 
            		appNames2.get(0).get(2).startsWith("Xzgv is a picture viewer for X, "));
            assertTrue("Incorrect app description", 
            		appNames2.get(1).get(2).startsWith("Xpdf is an open source viewer for Portable Document Format (PDF) files. "));
            assertEquals("Incorrect app description", "Beaver is an Early AdVanced (text) EditoR. ", appNames2.get(2).get(2));
            assertTrue("Incorrect app description", 
            		appNames2.get(3).get(2).startsWith("Mozilla Firefox is a free and open source web browser descended from "));
            assertNull("Incorrect app description", appNames2.get(4).get(2));

//        	This is proprietary software.
//            List<List<String>> appNames3 = dao.getViewData("IMG-2001", true, "app", col);
//            System.out.println(appNames3.toString());
//            assertEquals("Incorrect size of app name list for image id=3", 6, appNames3.size());
//            assertEquals("Incorrect app name", "Acrobat Reader", appNames3.get(0).get(0));
//            assertEquals("Incorrect app name", "Internet Explorer", appNames3.get(1).get(0));
//            assertEquals("Incorrect app name", "Microsoft Word", appNames3.get(2).get(0));
//            assertEquals("Incorrect app name", "Paint", appNames3.get(3).get(0));      
//            assertEquals("Incorrect app name", "Quark Express", appNames3.get(4).get(0));      
//            assertEquals("Incorrect app name", "Wordpad", appNames3.get(5).get(0));      
//            assertEquals("Incorrect size of app version list for image id=3", 6, appNames3.size());
//            assertEquals("Incorrect app version", "2.0", appNames3.get(0).get(1));
//            assertEquals("Incorrect app version", "1.0", appNames3.get(1).get(1));
//            assertEquals("Incorrect app version", "1.0", appNames3.get(2).get(1));
//            assertEquals("Incorrect app version", "1.0", appNames3.get(3).get(1));
//            assertEquals("Incorrect app version", "3.1", appNames3.get(4).get(1));
//            assertEquals("Incorrect app version", "1.0", appNames3.get(5).get(1));
//            assertEquals("Incorrect size of app description list for image id=3", 6, appNames3.size());
//            assertEquals("Incorrect app description", "Page layout software", appNames3.get(4).get(2));
//            assertEquals("Incorrect app description", "Text editor for Windows", appNames3.get(5).get(2));
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetPathwayApp() {
    	List<String> col = new ArrayList<String>();
    	col.add("APP_NAME");
    	col.add("OS_NAME");
    	col.add("PLATFORM_NAME");

        try {
        	List<List<String>> appName = dao.getPathwaysView("BBC Micro Image", col);
            assertEquals("Incorrect number of pathways", 1, appName.size());
            assertNull("Incorrect app name image format BBC Micro Image", appName.get(0).get(0));
            assertNull("Incorrect app name image format BBC Micro Image", appName.get(0).get(1));
            assertEquals("Incorrect platform name image format BBC Micro Image", "BBCMICRO", appName.get(0).get(2));

            List<List<String>> appName2 = dao.getPathwaysView("Portable Document Format", col);
            assertEquals("Incorrect number of pathways", 1, appName2.size());
            assertEquals("Incorrect app name image format Portable Document Format", "Xpdf", appName2.get(0).get(0));
            assertEquals("Incorrect app name image format Portable Document Format", "Damn Small Linux", appName2.get(0).get(1));
            assertEquals("Incorrect platform name image format Portable Document Format", "x86", appName2.get(0).get(2));

        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
    
    @Test
    public void testGetFileFormatInfo() {
    	List<String> result = new ArrayList<String>();
    	result = dao.getFileFormatInfo("FFT-1000");
    	Iterator<String> it = result.iterator();
    	assertEquals("Incorrect fileformat ID", "FFT-1000", it.next());
    	assertEquals("Incorrect fileformat name", "Amiga Disk Image", it.next());
    	assertNull("Incorrect fileformat version", it.next());
    	assertNotNull("Incorrect fileformat description", it.next());
    	assertNotNull("Incorrect fileformat reference", it.next());
    }

    @Test
    public void testGetHardwarePlatformInfo() {
    	List<String> result = new ArrayList<String>();
    	result = dao.getHardwarePlatformInfo("HPF-1000");
    	Iterator<String> it = result.iterator();
    	assertEquals("Incorrect platform ID", "HPF-1000", it.next());
    	assertEquals("Incorrect platform name", "Amiga", it.next());
    	assertEquals("Incorrect platform description", "Commodore Amiga", it.next());
    	assertNotNull("Incorrect platform creator", it.next());
    	assertNotNull("Incorrect platform production start", it.next());
    	assertNotNull("Incorrect platform production end", it.next());
    	assertNotNull("Incorrect platform reference", it.next());
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

    private Properties getProperties(String userPropertiesFileName) throws IOException {

        // Read the properties file
        Properties props = new Properties();
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(userPropertiesFileName);
            return getProperties(fis);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find properties file [" + userPropertiesFileName + "]: "
                    + e.toString());
            throw new IOException("Could not find properties file [" + userPropertiesFileName + "]: ", e);
        } catch (IOException e) {
            System.out.println("Failed to read properties file [" + userPropertiesFileName + "]: "
                    + e.toString());
            throw e;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                // Hmm... hoping not to get this far into catching exceptions;
                // we'll just log it and proceed...
                System.out.println("Failed to close open file: [" + userPropertiesFileName + "]: "
                        + e.toString());
            }
        }
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