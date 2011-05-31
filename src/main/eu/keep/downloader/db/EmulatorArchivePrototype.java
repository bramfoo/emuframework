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

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;

import eu.keep.downloader.EmulatorArchive;
import eu.keep.emulatorarchive.EmulatorArchivePortType;
import eu.keep.emulatorarchive.EmulatorArchiveService;
import eu.keep.emulatorarchive.EmulatorPackageList;
import eu.keep.emulatorarchive.HardwareIDs;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;
import eu.keep.util.FileUtilities;


/**
 * Implementation of the Emulator Archive interface.
 * Connects to the prototype written for KEEP, emulatorarchive.keep.eu.
 * Provides DAOs for Emulator packages acting as client for Emulator Archive webservices
 * @author David Michel
 */
public class EmulatorArchivePrototype implements EmulatorArchive {

    private static final Logger logger       = Logger.getLogger(EmulatorArchivePrototype.class.getName());

    private static final QName  SERVICE_NAME = new QName("http://emulatorarchive.keep.eu",
                                                     "EmulatorArchiveService");
    EmulatorArchiveService      ss;
    EmulatorArchivePortType     port;
    final int timeOut = 10000;

    public EmulatorArchivePrototype(Properties props) {


        // Using a local copy of the WSDL (only used to read the soap:address) but makes deployment difficult
        // as the WSDL then needs to be stored in the classpath which creates trouble with it refers to other files
    	// Instead, the soap:address will be overwritten using the method below.
    	// ss = new EmulatorArchiveService(wsdlURL, SERVICE_NAME);
        // port = ss.getEmulatorArchivePort();
    	
        // Overwrite the soap:address by creating a new port to the service.
        String emulatorArchiveURL = props.getProperty("emulator.archive.url");
        ss = getEmulatorArchiveService(null, SERVICE_NAME);
        QName portName = new QName("http://emulatorarchive.keep.eu", "EmulatorArchivePort");
        ss.addPort(portName, javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING, emulatorArchiveURL);
        port = getEmulatorArchivePortType(portName, EmulatorArchivePortType.class);


        // Add time-outs; the default is 30000 (30 seconds) but these are
        // set shorter (10s) because at the moment the servers are local
        Client cl = ClientProxy.getClient(port);
        HTTPConduit conduit = (HTTPConduit) cl.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(timeOut);
        httpClientPolicy.setReceiveTimeout(timeOut);
        conduit.setClient(httpClientPolicy);

        // enabling MTOM
        EmulatorArchivePortType client = ss.getEmulatorArchivePort();
        BindingProvider bp = (BindingProvider)client;
        javax.xml.ws.soap.SOAPBinding binding = (javax.xml.ws.soap.SOAPBinding)bp.getBinding();
        binding.setMTOMEnabled(true);
    }
    
    // Extracted for mocking in unit tests
    protected EmulatorArchiveService getEmulatorArchiveService(URL url, QName qname) {
    	return new EmulatorArchiveService(url, qname);
    }
    protected EmulatorArchivePortType getEmulatorArchivePortType(QName qname, Class<EmulatorArchivePortType> clazz){
    	return ss.getPort(qname, clazz);
    }

    /**
     * {@inheritDoc}
     */
    public EmulatorPackage getEmulatorPackage(Integer emuID) throws ConnectException, SocketTimeoutException, WebServiceException {
        logger.debug("Retrieving emulator package for ID " + emuID);
        
        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, "...");

        EmulatorPackage emuPack;
        try {
             emuPack = port.getEmulatorPackage(emuID);
        }
        catch (WebServiceException e) {
            if (e.getCause() instanceof SocketTimeoutException)
            {
                SocketTimeoutException se = (SocketTimeoutException) e.getCause();
                throw se;
            }
            else if (e.getCause() instanceof ConnectException)
            {
                ConnectException ce = (ConnectException) e.getCause();
                throw ce;
            }
            else
                throw e;
        }
        finally
        {
            // Stop the timer
            t.cancel();
        }

        logger.debug("Package retrieved: " + emuPack.getEmulator().getName() + emuPack.getEmulator().getVersion());
        return emuPack;
    }

    
    /**
     * {@inheritDoc}
     */
    public List<EmulatorPackage> getEmulatorPackages() throws ConnectException, SocketTimeoutException, WebServiceException {
        logger.debug("Invoking getEmulatorPackages...");
        // redundant parameter, needed as notification service (i.e. no input
        // argument) is not possible with Apache CXF
        int _getEmulatorPackageList_parameters = 0;
        
        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, "...");

        EmulatorPackageList _getEmulatorPackageList__return;
        try {
             _getEmulatorPackageList__return = port
                    .getEmulatorPackageList(_getEmulatorPackageList_parameters);
            logger.debug("getEmulatorPackages.result=" + _getEmulatorPackageList__return.toString());
        }
        catch (WebServiceException e) {
            if (e.getCause() instanceof SocketTimeoutException)
            {
                SocketTimeoutException se = (SocketTimeoutException) e.getCause();
                throw se;
            }
            else if (e.getCause() instanceof ConnectException)
            {
                ConnectException ce = (ConnectException) e.getCause();
                throw ce;
            }
            else
                throw e;
        }
        finally
        {
            // Stop the timer
            t.cancel();
        }

        return _getEmulatorPackageList__return.getEmulatorPackage();
    }

    /**
     * {@inheritDoc}
     */
    public InputStream downloadEmulatorPackage(Integer emuID) throws ConnectException,
    IOException, SocketTimeoutException, WebServiceException {
        logger.debug("Downloading emulator binary for ID " + emuID);
        DataHandler emuBinary;

        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, ":::");
        try {
            emuBinary = port.downloadEmulator(emuID);
        }
        catch (WebServiceException e) {
            if (e.getCause() instanceof SocketTimeoutException)
            {
                SocketTimeoutException se = (SocketTimeoutException) e.getCause();
                throw se;
            }
            else if (e.getCause() instanceof ConnectException)
            {
                ConnectException ce = (ConnectException) e.getCause();
                throw ce;
            }
            else
                throw e;
        }
        finally
        {
            // Stop the timer
            t.cancel();
        }
        
        logger.debug("Binary retrieved for ID " + emuID);
        InputStream is = emuBinary.getInputStream();
        return is;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getSupportedHardware() throws ConnectException, SocketTimeoutException, WebServiceException {

        Set<String> hw = new HashSet<String>();
        HardwareIDs hwIDs;
        int dummy = 0;

        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, "...");

        try {
             hwIDs = port.getSupportedHardware(dummy);
            logger.debug("Emulator archive supported hardware types: " + hwIDs.getId());
            hw.addAll(hwIDs.getId());
        }
        catch (WebServiceException e) {
            if (e.getCause() instanceof SocketTimeoutException)
            {
                SocketTimeoutException se = (SocketTimeoutException) e.getCause();
                throw se;
            }
            else if (e.getCause() instanceof ConnectException)
            {
                ConnectException ce = (ConnectException) e.getCause();
                throw ce;
            }
            else
                throw e;
        }
        finally
        {
            // Stop the timer
            t.cancel();
        }

        return hw;
    }

    @Override
    public List<EmulatorPackage> getEmulatorsByHardware(String hardwareName) throws ConnectException,
            SocketTimeoutException, WebServiceException {

        List<EmulatorPackage> emuPacks = new ArrayList<EmulatorPackage>();

        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, "...");

        try {
            EmulatorPackageList emuList = port.getEmusByHardware(hardwareName);
            emuPacks.addAll(emuList.getEmulatorPackage());
        }
        catch (WebServiceException e) {
            if (e.getCause() instanceof SocketTimeoutException)
            {
                SocketTimeoutException se = (SocketTimeoutException) e.getCause();
                throw se;
            }
            else if (e.getCause() instanceof ConnectException)
            {
                ConnectException ce = (ConnectException) e.getCause();
                throw ce;
            }
            else
                throw e;
        }
        finally
        {
            // Stop the timer
            t.cancel();
        }

        logger.debug("Found " + emuPacks.size() + " emulators supporting hardware '" + hardwareName +"'");
        return emuPacks;
        }
}