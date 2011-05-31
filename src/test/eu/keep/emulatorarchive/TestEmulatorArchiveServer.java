/*
* $Revision: 601 $ $Date: 2011-05-31 00:13:40 +0100 (Tue, 31 May 2011) $
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
* Project Title: Emulator Archive (EA)$
 */

package eu.keep.emulatorarchive;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;

import static org.junit.Assert.*;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPBinding;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A Junit Test class for testing the webservices run by the server.
 * @author David Michel
 */
@Ignore("Integration tests of the server webservices requiring an instance of the server running")
public class TestEmulatorArchiveServer {

    private static final QName      SERVICE_NAME = new QName("http://emulatorarchive.keep.eu",
                                                         "EmulatorArchiveService");

    private final static String propertiesFile = "test.properties";
    private static Properties props;

    private EmulatorArchiveService  ss;
    private EmulatorArchivePortType port;

   
    @Before
    public void setUp() {

    	 //TODO Launch server in a separate thread in setup() and kill thread in tearDown()

        // read user.properties
        try {

            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("eu/keep/emulatorarchive/" + propertiesFile);
            props = getProperties(is);
//            props = getProperties(propertiesFile);

        }
        catch(IOException e){
            e.printStackTrace();
            fail(e.toString());
        }
        
        // Launch client
        try {
             String emulatorArchiveURL = props.getProperty("server.soap.address");
             ss = new EmulatorArchiveService(null, SERVICE_NAME);
             QName portName = new QName("http://emulatorarchive.keep.eu", "EmulatorArchivePort");
             ss.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, emulatorArchiveURL);
             port = ss.getPort(portName, EmulatorArchivePortType.class);

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
    public void testGetEmulatorPackage() {

        try {

           int id = 1;
            EmulatorPackage result = port.getEmulatorPackage(id);

        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmusByHardware() {

        try {

            String hw = "x86";
            EmulatorPackageList result = port.getEmusByHardware(hw);

        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetEmulatorPackageList() {

        try {

           int dummy = 0;   // dummy argument
            EmulatorPackageList result = port.getEmulatorPackageList(dummy);

        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testGetSupportedHardware() {

        try {

            int dummy = 0; //dummy element
            HardwareIDs result = port.getSupportedHardware(dummy);


        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testDownloadEmulatorPackage() {

        try {

            int id = 1;
            DataHandler result = port.downloadEmulator(id);


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
}
