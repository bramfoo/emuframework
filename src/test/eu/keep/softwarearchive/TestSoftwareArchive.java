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
* Project Title: Software Archive (SWA)$
*/

package eu.keep.softwarearchive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;

/**
 * A Junit Test class for testing the webservices ran by the server.
 * @author David Michel
 */
@Ignore("Integration tests of the server webservices requiring an instance of the server running")
public class TestSoftwareArchive {

    private static final QName      SERVICE_NAME = new QName("http://softwarearchive.keep.eu",
                                                         "SoftwareArchiveService");
    private SoftwareArchiveService  ss;
    private SoftwareArchivePortType port;

    protected final static String propertiesFile = "test.properties";
    protected static Properties props;

    @Before
    public void setUp() {

   	 //TODO Launch server in a separate thread in setup() and kill thread in tearDown()

        // read user.properties
        try {

            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("eu/keep/" + propertiesFile);
            props = getProperties(is);
        }
        catch(IOException e){
            e.printStackTrace();
            fail(e.toString());
        }

        // Launch client
        try {
             String softwareArchiveURL = props.getProperty("server.soap.address");
             ss = new SoftwareArchiveService(null, SERVICE_NAME);
             QName portName = new QName("http://softwarearchive.keep.eu", "SoftwareArchivePort");
             ss.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, softwareArchiveURL);
             port = ss.getPort(portName, SoftwareArchivePortType.class);

             // Add time-outs; the default is 30000 (30 seconds) but these are
             // set shorter (10s) because at the moment the servers are local
             Client cl = ClientProxy.getClient(port);
             HTTPConduit conduit = (HTTPConduit) cl.getConduit();
             HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
             httpClientPolicy.setConnectionTimeout(100000);
             httpClientPolicy.setReceiveTimeout(100000);
             conduit.setClient(httpClientPolicy);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testPathwaysByFileFormat() {

        try {
            String ff = "Plain text";
            PathwayList result = port.getPathwaysByFileFormat(ff);
            assertEquals("Incorrect number of pathways returned. ", 2, result.getPathway().size());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetSoftwarePackagesByPathway() {

        try {
        	String ff = "Plain text";
        	PathwayList pwList = port.getPathwaysByFileFormat(ff);
            SoftwarePackageList result = port.getSoftwarePackagesByPathway(pwList.getPathway().get(0));
            assertEquals("Incorrect number of software packages returned. ", 1, result.getSoftwarePackage().size());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testDownloadSoftware() {

        try {
            String id = "IMG-1000";
            DataHandler result = port.downloadSoftware(id);
            assertNotNull("Empty software image returned", result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetSoftwarePackageInfo() {

        try {
            String id = "IMG-1";
            SoftwarePackage result = port.getSoftwarePackageInfo(id);
            assertNotNull("Empty software image returned", result);
            assertEquals("Empty software image returned", id, result.getId());
            assertNotNull("Empty software image returned", result.getApp());
            assertNotNull("Empty software image returned", result.getOs());
            assertNotNull("Empty software image returned", result.getDescription());
            assertNotNull("Empty software image returned", result.getFormat());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetAllSoftwarePackagesInfo() {

        try {
            String dummy = "0";
            SoftwarePackageList result = port.getAllSoftwarePackagesInfo(dummy);
            assertEquals("Incorrect number of software packages returned. ", 2, result.getSoftwarePackage().size());
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

    private Properties getProperties(String userPropertiesFileName) throws IOException {

        // Read the properties file
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
}