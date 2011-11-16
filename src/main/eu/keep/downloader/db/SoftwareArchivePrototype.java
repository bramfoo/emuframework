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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

import eu.keep.softwarearchive.SoftwarePackageList;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;

import eu.keep.softwarearchive.PathwayList;
import eu.keep.softwarearchive.SoftwareArchivePortType;
import eu.keep.softwarearchive.SoftwareArchiveService;
import eu.keep.softwarearchive.SwLanguageList;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;

import eu.keep.downloader.SoftwareArchive;
import eu.keep.util.FileUtilities;

/**
 * Implementation of the Software Archive interface.
 * Connects to the prototype written for KEEP, softwarearchive.keep.eu.
 * DAOs for software packages acting as client for Software Archive webservices
 * @author David Michel
 * @author Bram Lohman
 */
public class SoftwareArchivePrototype implements SoftwareArchive {

    private static final Logger logger       = Logger.getLogger(SoftwareArchivePrototype.class.getName());

    private static final QName  SERVICE_NAME = new QName("http://softwarearchive.keep.eu",
                                                     "SoftwareArchiveService");

    SoftwareArchiveService      ss;
    SoftwareArchivePortType     port;
    final int timeOut = 10000;

    /**
     * Constructor that passes the {@code Properties} (to get information about webservice SOAP address)
     * @param props {@code Properties} object
     */
    public SoftwareArchivePrototype(Properties props) {

        // Using a local copy of the WSDL (only used to read the soap:address) but makes deployment difficult
        // as the WSDL then needs to be stored in the classpath which creates trouble with it refers to other files
    	// Instead, the soap:address will be overwritten using the method below.
    	// ss = new SoftwareArchiveService(wsdlUrl, SERVICE_NAME);
        // port = ss.getSoftwareArchivePort();

        // Overwrite the soap:address by creating a new port to the service.
        String softwareArchiveURL = props.getProperty("software.archive.url");
        ss = getSoftwareArchiveService(null, SERVICE_NAME);
        QName portName = new QName("http://softwarearchive.keep.eu", "SoftwareArchivePort");
        ss.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, softwareArchiveURL);
        port = getSoftwareArchivePortType(portName, SoftwareArchivePortType.class);

        // Add time-outs; the default is 30000 (30 seconds) but these are
        // set shorter (10s) because at the moment the servers are local
        Client cl = ClientProxy.getClient(port);
        HTTPConduit conduit = (HTTPConduit) cl.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(timeOut);
        httpClientPolicy.setReceiveTimeout(timeOut);
        conduit.setClient(httpClientPolicy);

        // enabling MTOM
        SoftwareArchivePortType client = ss.getSoftwareArchivePort();
        BindingProvider bp = (BindingProvider)client;
        SOAPBinding binding = (SOAPBinding) bp.getBinding();
        binding.setMTOMEnabled(true);
    }

    // Extracted for mocking in unit tests
    protected SoftwareArchiveService getSoftwareArchiveService(URL url, QName qname) {
    	return new SoftwareArchiveService(url, qname);
    }
    protected SoftwareArchivePortType getSoftwareArchivePortType(QName qname, Class<SoftwareArchivePortType> clazz){
    	return ss.getPort(qname, clazz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pathway> getPathwayByFileFormat(String fileFormat) throws ConnectException, SocketTimeoutException, WebServiceException {
        logger.info("Invoking getPathwayByFF with format: " + fileFormat + "...");

        PathwayList pwl = new PathwayList();
        
        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, "...");

        try {
             pwl = port.getPathwaysByFileFormat(fileFormat);
             logger.debug("Retrieved list of size: " + pwl.getPathway().size());
        }
        catch (WebServiceException e) {
            if (e.getCause() instanceof SocketTimeoutException)
            {
                logger.warn("Error retrieving software package list by OS/app: " + e.toString());
                SocketTimeoutException se = (SocketTimeoutException) e.getCause();
                throw se;
            }
            else if (e.getCause() instanceof ConnectException)
            {
            	logger.warn("Error retrieving software package list by OS/app: " + e.toString());
                ConnectException ce = (ConnectException) e.getCause();
                throw ce;
            }
            else
            {
            	logger.warn("Error retrieving software package list by OS/app: " + e.toString());
                throw e;
            }
        }
        finally
        {
            // Stop the timer
            t.cancel();
        }

        logger.info("Retrieved Pathways using format: " + fileFormat + " (" + pwl.getPathway().size() + " pathways returned)");
        return pwl.getPathway();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<SoftwarePackage> getSoftwarePackageByPathway(Pathway pw) throws ConnectException, SocketTimeoutException, WebServiceException {
        logger.info("Invoking getSWpackage with pathway: " + pw + "...");

        SoftwarePackageList swl = new SoftwarePackageList();
        
        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, "...");

        try {
             swl = port.getSoftwarePackagesByPathway(pw);
             logger.debug("Retrieved list of size: " + swl.getSoftwarePackage().size());
        }
        catch (WebServiceException e) {
            if (e.getCause() instanceof SocketTimeoutException)
            {
                logger.warn("Error retrieving software package list by OS/app: " + e.toString());
                SocketTimeoutException se = (SocketTimeoutException) e.getCause();
                throw se;
            }
            else if (e.getCause() instanceof ConnectException)
            {
            	logger.warn("Error retrieving software package list by OS/app: " + e.toString());
                ConnectException ce = (ConnectException) e.getCause();
                throw ce;
            }
            else
            {
            	logger.warn("Error retrieving software package list by OS/app: " + e.toString());
                throw e;
            }
        }
        finally
        {
            // Stop the timer
            t.cancel();
        }

        logger.info("Retrieved SWpackage using pathway: " + pw + " (" + swl.getSoftwarePackage().size() + " packages returned)");
        return swl.getSoftwarePackage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream downloadSoftware(String imageID) throws IOException, WebServiceException {
        logger.info("Downloading software file for ID: " + imageID + "...");
        DataHandler swHandler;
        
        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, ":::");

        try {
            swHandler = port.downloadSoftware(imageID);
            logger.debug("Got handler to software img ID: " + imageID);
        }
        catch (WebServiceException e) {
            if (e.getCause() instanceof SocketTimeoutException)
            {
                logger.warn("Error retrieving software package file: " + e.toString());
                SocketTimeoutException se = (SocketTimeoutException) e.getCause();
                throw se;
            }
            else if (e.getCause() instanceof ConnectException)
            {
                logger.warn("Error retrieving software package file: " + e.toString());
                ConnectException ce = (ConnectException) e.getCause();
                throw ce;
            }
            else
            {
                logger.warn("Error retrieving software package file: " + e.toString());
                throw e;
            }
        }
        finally
        {
            // Stop the timer
            t.cancel();
        }
        
        logger.info("Returning InputStream for sw package " + imageID);
        InputStream is = swHandler.getInputStream();
        return is;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSoftwareFormat(String imageID) throws ConnectException, SocketTimeoutException, WebServiceException {
        logger.info("Retrieving format for sw package " + imageID + "...");
        String format;
        
        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, "...");

        try {
        	SoftwarePackage swPack = getSoftwarePackage(imageID);
            format = swPack.getFormat();
            logger.debug("Format for sw package " + imageID + " is: " + format);
        }
        catch (WebServiceException e) {
            if (e.getCause() instanceof SocketTimeoutException)
            {
                logger.warn("Error retrieving software format: " + e.toString());
                SocketTimeoutException se = (SocketTimeoutException) e.getCause();
                throw se;
            }
            else if (e.getCause() instanceof ConnectException)
            {
                logger.warn("Error retrieving software format: " + e.toString());
                ConnectException ce = (ConnectException) e.getCause();
                throw ce;
            }
            else
            {
            	logger.warn("Error retrieving software format: " + e.toString());
                throw e;
            }
        }
        finally
        {
            // Stop the timer
            t.cancel();
        }

        logger.info("Retrieved format for sw package " + imageID + ": " + format);
        return format;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SoftwarePackage> getSoftwarePackageList() throws ConnectException, SocketTimeoutException, WebServiceException {
        logger.info("Retrieving sw package list...");
        String dummy = "0"; // dummy input argument
        SoftwarePackageList swPackList;

        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, "...");

        try {
            swPackList = port.getAllSoftwarePackagesInfo(dummy);
            logger.debug("Retrieved sw package list of size " + swPackList.getSoftwarePackage().size());
        }
        catch (WebServiceException e) {
            if (e.getCause() instanceof SocketTimeoutException)
            {
                logger.warn("Error retrieving software package list: " + e.toString());
                SocketTimeoutException se = (SocketTimeoutException) e.getCause();
                throw se;
            }
            else if (e.getCause() instanceof ConnectException)
            {
                logger.warn("Error retrieving software package list: " + e.toString());
                ConnectException ce = (ConnectException) e.getCause();
                throw ce;
            }
            else
            {
                logger.warn("Error retrieving software package list: " + e.toString());
                throw e;
            }
        }
        finally
        {
            // Stop the timer
            t.cancel();
        }

        logger.info("Retrieved sw package list of size " + swPackList.getSoftwarePackage().size());
        return swPackList.getSoftwarePackage();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SoftwarePackage getSoftwarePackage(String imageID) throws ConnectException, SocketTimeoutException, WebServiceException {
        logger.info("Retrieving sw package for ID " + imageID + "...");
        SoftwarePackage swPack;

        // Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, "...");

        try {
            swPack = port.getSoftwarePackageInfo(imageID);
            logger.debug("Retrieved sw package: " + swPack.getDescription());
        }
        catch (WebServiceException e) {
            if (e.getCause() instanceof SocketTimeoutException)
            {
                logger.warn("Error retrieving software package: " + e.toString());
                SocketTimeoutException se = (SocketTimeoutException) e.getCause();
                throw se;
            }
            else if (e.getCause() instanceof ConnectException)
            {
                logger.warn("Error retrieving software package: " + e.toString());
                ConnectException ce = (ConnectException) e.getCause();
                throw ce;
            }
            else
            {
                logger.warn("Error retrieving software package: " + e.toString());
                throw e;
            }
        }
        finally
        {
            // Stop the timer
            t.cancel();
        }

        logger.info("Retrieved sw package for ID " + imageID + ": " + swPack.getDescription());
        return swPack;
    }

    /**
     * {@inheritDoc}
     */
	public SwLanguageList getSoftwareLanguages() throws ConnectException, SocketTimeoutException, WebServiceException {

		SwLanguageList languageList = null;
		
		// Request a timer to display progress
        Timer t = FileUtilities.getFixedRateTimer(500, 500, "...");

        String dummy = "dummy";
        try {
        	languageList = port.getLanguageList(dummy);
            logger.debug("Downloaded " + languageList.getLanguages().size() + " languages referenced in Software Archive.");
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

        return languageList;
	}
}