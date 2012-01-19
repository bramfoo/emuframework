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

package eu.keep.downloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import eu.keep.downloader.db.EmulatorArchivePrototype;
import eu.keep.downloader.db.H2DataAccessObject;
import eu.keep.downloader.db.SoftwareArchivePrototype;
import eu.keep.emulatorarchive.emulatorpackage.EmuLanguageList;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;
import eu.keep.softwarearchive.SwLanguageList;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;
import eu.keep.util.FileUtilities;
import eu.keep.util.Language;

/**
 * The Downloader component handles all functionality related to downloading and 
 * storing external data. This includes emulator packages, software packages, and 
 * registry configurations.
 * Emulator packages (which contain specific metadata) are queried from an 
 * external database. The emulator metadata is used to determine which emulator
 * can fulfill the pathway requirements.
 * Software packages are retrieved based on pathway information from an external 
 * database. These are provided to the Controller for emulator configuration, and 
 * tend not to be stored locally.
 * Registry configuration is stored in the local database. This data 
 * can then be accessed and edited to configure the registry lookup in the 
 * Characteriser component.
 * 
 * @author Bram Lohman
 * @author David Michel
 */
public class Downloader {
    
    private static final Logger logger = Logger.getLogger(Downloader.class.getName());

    private EmulatorArchive             emulatorArchive;
    private SoftwareArchive             softwareArchive;
    private DataAccessObject			dao;

    public Downloader(Properties props, Connection conn) {
        // These initialisations have been extracted to support Mocking in the unit tests
    	softwareArchive = createSWA(props); 
        emulatorArchive = createEA(props);
        dao = createDAO(conn);
    }
   
	// Support for mocking
    protected SoftwareArchive createSWA(Properties props) {
    	return new SoftwareArchivePrototype(props);
    }
    protected EmulatorArchive createEA(Properties props) {
    	return new EmulatorArchivePrototype(props);
    }
    protected DataAccessObject createDAO(Connection conn) {
		return new H2DataAccessObject(conn);
	}



    ////////////////////////
    // Emulator Archive
    ////////////////////////

    /**
     * Returns the list of emulator packages in the archive database
     * @return List<EmulatorPackage> List of emulator packages 
     * @throws IOException If a connection error occurs when contacting the Emulator Archive
     */
    public List<EmulatorPackage> getEmulatorPackages() throws IOException {

        try {
            return emulatorArchive.getEmulatorPackages();
        }
    	catch (IOException e) {
    		processIOException(e, "emulator archive");
    	}
    	catch (WebServiceException wse) {
    		processWebServiceException(wse, "Emulator archive");
    	}
		return null;
    }
    
    /**
     * Get the list of supported hardware from the emulator archive
     * @return Set<String> A set of hardware names
     * @throws IOException If a connection error occurs when contacting the Emulator Archive
     */
    public Set<String> getSupportedHardware() throws IOException {

        try {
            return emulatorArchive.getSupportedHardware();      
        }
    	catch (IOException e) {
    		processIOException(e, "emulator archive");
    	}
    	catch (WebServiceException wse) {
    		processWebServiceException(wse, "Emulator archive");
    	}
		return null;
    }

    /**
     * Get the list of emulators that support a type of hardware from the Emulator Archive
     * @throws IOException If a connection error occurs when contacting the Emulator Archive
     */
    public List<EmulatorPackage> getEmulatorsByHardware(String hardwareName) throws IOException {

        try {
            return emulatorArchive.getEmulatorsByHardware(hardwareName);
        }
    	catch (IOException e) {
    		processIOException(e, "emulator archive");
    	}
    	catch (WebServiceException wse) {
    		processWebServiceException(wse, "Emulator archive");
    	}
		return null;
    }
    
    /**
     * Downloads an emulator from the Emulator Archive into a target directory, given an emulator ID
     * @param emuID emulator ID
     * @param targetDir target directory
     * @return File emulator executable
     * @throws IOException If a connection error occurs when contacting the Emulator Archive
     */
    public File getEmuExec(Integer emuID, File targetDir) throws IOException {

        InputStream is;
        EmulatorPackage emuPack;
        String emuPackName;
        String emuExecFileName;
        String emuExecDir;
		File emuFile;

        try {
            is = emulatorArchive.downloadEmulatorPackage(emuID);
            emuPack = emulatorArchive.getEmulatorPackage(emuID);
            emuExecFileName = emuPack.getEmulator().getExecutable().getName();
            emuExecDir = emuPack.getEmulator().getExecutable().getLocation();
            emuPackName = emuPack.getPackage().getName();
	        emuFile = new File(targetDir + File.separator + "tmp.zip");
        }
		catch(SOAPFaultException e) {
            logger.error("SOAP error: " + e.toString());
			throw e;
		}

        logger.info("Downloading emulator package " + emuPack.getEmulator().getName() + emuPack.getEmulator().getVersion() + " into "
                + targetDir.getPath());

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(emuFile));
            byte buf[]=new byte[1024];
            int len;
            while((len = is.read(buf)) > 0)
            {
                bos.write(buf,0,len);
            }
        }
        catch (FileNotFoundException e) {
            logger.error("Could not find file " + emuFile.getPath() + ": " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            logger.error("Error while accessing the file " + emuFile.getPath() + ": "
                    + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            try {
                bos.close();
                is.close();
            }
            catch (IOException e) {
                logger.error("Could not close file " + emuFile.getPath() + ": " + e.toString());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        // Unpack the emulator package
        List<File> emuPackFiles = new ArrayList<File>();
        if (targetDir.exists() && emuFile.canRead() && FileUtilities.getExtension(emuFile).equalsIgnoreCase("zip")) {
            logger.info("Unpacking emulator package: " + emuFile + " into "
                    + targetDir.getPath());
        	emuPackFiles = FileUtilities.unZip(emuFile, targetDir);
        }
        else {
            logger.error("Cannot unpack emulator. Cannot find file " + emuFile.getName() + "in "
                    + targetDir.getPath() + " or file is not a ZIP file");
            throw new IOException("Cannot unpack emulator. Cannot find file " + emuFile.getName() + "in "
                    + targetDir.getPath() + " or file is not a ZIP file");
        }

        // FIXME: This isn't required anymore??
        // unpack the emulator binaries
        File emuBin = new File(targetDir + File.separator + emuPackName);
        if (emuPackFiles.contains(emuBin)) {
            logger.info("Unpacking emulator binary: " + emuBin + " into "
                    + targetDir.getPath());
            FileUtilities.unZip(emuFile, targetDir);
        }

        // Get the exec path
        if (emuExecDir == null)
            emuExecDir = "";

        logger.debug("retrieve executable name: " + emuExecFileName);
        logger.debug("retrieve executable dir: " + emuExecDir);

        // Get exec file
        File emuExecFile = new File(targetDir + File.separator + emuExecDir, emuExecFileName);

        // set executable permission (useful for POSIX file system only)
        // method only exists for Java 6 !
        if (emuExecFile.exists() && !emuExecFile.setExecutable(true)) {
            logger.warn("Executable permission could not be set");
        }

        return emuExecFile;
    }

    /**
     * Select the whitelisted emulator IDs from the database
     * @return List<EmulatorPackage> A list of emulator packages
     * @throws IOException If a connection error occurs when contacting the Emulator Archive
     */
    public List<EmulatorPackage> getWhitelistedEmus() throws IOException {
    	
    	Map<Integer, String> emuIDs = new HashMap<Integer, String>();
    	List<EmulatorPackage> emuPacks = new ArrayList<EmulatorPackage>();
    	
    	logger.debug("Retrieving whitelisted emulators");
    	try {
			emuIDs = dao.getWhitelistedEmus();
		} catch (SQLException e) {
			throw new IOException("Error retrieving whitelisted emulator IDs: " + e, e);
		}
		logger.debug("Whitelisted emulators: " + emuIDs);
		
		logger.debug("Retrieving emulator package for IDs: " + emuIDs);
		for (Integer i : emuIDs.keySet()) {
			emuPacks.add(emulatorArchive.getEmulatorPackage(i));
		}

		logger.debug("Returning emulator package for IDs: " + emuIDs);
		return emuPacks;
    }

    /**
     * Adds an emulator ID to the whitelist
     * (list of emulators that will be used for rendering a digital object)
     * @param i Unique ID of emulator
     * @return True if ID successfully added to whitelist, false otherwise
     * @throws IOException If a connection error occurs when contacting the Emulator Archive
     */
    public boolean whiteListEmulator(Integer i) throws IOException {
    	
    	EmulatorPackage emuPack = emulatorArchive.getEmulatorPackage(i);
    	String descr = emuPack.getEmulator().getName() + " " + emuPack.getEmulator().getVersion() + ": " + emuPack.getEmulator().getDescription(); 
    	try {
			return dao.whiteListEmulator(i, descr);
		} catch (SQLException e) {
			throw new IOException("Error whitelisting emulator ID [" + i +"]: " + e, e);
		}
    }

    /**
     * Removes an emulator ID from the whitelist
     * (list of emulators that will be used for rendering a digital object)
     * @param i Unique ID of emulator
     * @return True if ID successfully removed from whitelist, false otherwise
     * @throws IOException If a connection error occurs when contacting the Emulator Archive
     */
    public boolean unListEmulator(Integer i) throws IOException {
    	try {
			return dao.unListEmulator(i);
		} catch (SQLException e) {
			throw new IOException("Error unlisting emulator ID [" + i +"]: " + e, e);
		}
    }
    
    /**
     * Gets all emulator languages from the emulator archive
     * @return List of all languages used by emulators
     * @throws IOException If a connection error occurs when contacting the Emulator Archive
     */
    public List<Language> getEmulatorLanguages() throws IOException {
        try {
        	List<Language> languages = new ArrayList<Language>();        	
        	EmuLanguageList emulatorLanguageIds = emulatorArchive.getEmulatorLanguages();
        	for (String languageId : emulatorLanguageIds.getLanguageIds()) {
        		languages.add(Language.valueOf(languageId));
        	}
            return languages;
        }
    	catch (IOException e) {
    		processIOException(e, "emulator archive");
    	}
    	catch (WebServiceException wse) {
    		processWebServiceException(wse, "Emulator archive");
    	}
		return null;
    }
    
    ////////////////////////
    // Software Archive
    ///////////////////////

    /**
     * Returns a list of pathways based on a file format
     * @param fileFormat String representing the file format of a digital object
     * @return List of pathways
     * @throws IOException If a connection error occurs when contacting the Software Archive
     */
    public List<Pathway> getPathwayByFileFormat(String fileFormat) throws IOException {
        try {
            return softwareArchive.getPathwayByFileFormat(fileFormat);
        }
    	catch (IOException e) {
    		processIOException(e, "software archive");
    	}
    	catch (WebServiceException wse) {
    		processWebServiceException(wse, "Software archive");
    	}
		return null;
    }

    /**
     * Returns a list of all available pathways
     * @return List of pathways
     * @throws IOException If a connection error occurs when contacting the Software Archive
     */
    public List<Pathway> getAllPathways() throws IOException {
        try {
            return softwareArchive.getAllPathways();
        }
    	catch (IOException e) {
    		processIOException(e, "software archive");
    	}
    	catch (WebServiceException wse) {
    		processWebServiceException(wse, "Software archive");
    	}
		return null;
    }

    /**
     * Returns the software package file format
     * @param imageID software package ID
     * @return String representing the format name
     * @throws IOException If a connection error occurs when contacting the Software Archive
     */
    public String getSoftwarePackageFormat(String imageID) throws IOException {
        try {
            return softwareArchive.getSoftwareFormat(imageID);
        }
    	catch (IOException e) {
    		processIOException(e, "software archive");
    	}
    	catch (WebServiceException wse) {
    		processWebServiceException(wse, "Software archive");
    	}
		return null;
    }

    /**
     * Returns a list of software packages based on a given pathway
     * @param pw Pathway representing the environment configuration
     * @return List of software packages
     * @throws IOException If a connection error occurs when contacting the Software Archive
     */
    public List<SoftwarePackage> getSoftWarePackageList(Pathway pw) throws IOException {

    	List<SoftwarePackage> swPacks = new ArrayList<SoftwarePackage>();

    	try {
    		swPacks = softwareArchive.getSoftwarePackageByPathway(pw);
    		return swPacks;
    	}
    	catch (IOException e) {
    		processIOException(e, "software archive");
    	}
    	catch (WebServiceException wse) {
    		processWebServiceException(wse, "Software archive");
    	}
    	return swPacks;
    }


    /**
     * Returns the list of software packages in the archive database
     * @return List<SoftwarePackage> List of software packages
     * @throws IOException If a connection error occurs when contacting the Software Archive
     */
    public List<SoftwarePackage> getSoftwarePackageList() throws IOException {
        try {
        	List<SoftwarePackage> swList = softwareArchive.getSoftwarePackageList();
        	return swList;
        }
    	catch (IOException e) {
    		processIOException(e, "software archive");
    	}
    	catch (WebServiceException wse) {
    		processWebServiceException(wse, "Software archive");
    	}
    	return null;
    }

    /**
     * Downloads a software image given an ID from the software archive into a target directory
     * @param softImageID software image ID
     * @param targetDir target directory
     * @return File software image file
     * @throws IOException If a connection error occurs when contacting the Software Archive
     */
    public File getSoftwareImage(String softImageID, File targetDir) throws IOException {

        InputStream is;
        String imageFileName;
        File softPackFile;

		try {
        	is = softwareArchive.downloadSoftware(softImageID);
		}
		catch(SOAPFaultException e) {
            logger.error("SOAP error: " + e.toString());
			throw e;
		}

    	imageFileName = softwareArchive.getSoftwarePackage(softImageID).getDescription().replace(" ", ""); // FIXME: More elegant
    	softPackFile = new File(targetDir + File.separator + imageFileName);
        logger.info("Downloading software image " + imageFileName + " (id=" + softImageID
                + ") into target directory: " + softPackFile.getAbsolutePath());

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(softPackFile));

            byte buf[]=new byte[1024*5];
            int len;
            while((len = is.read(buf)) > 0)
            {
                bos.write(buf,0,len);
            }
        }
        catch (FileNotFoundException e) {
            logger.error("Could not find file " + softPackFile.getPath() + ": " + e.toString());
        }
        catch (IOException e) {
            logger.error("Error while accessing the file " + softPackFile.getPath() + ": "
                    + e.toString());
        }
        finally {
            try {
                bos.close();
                is.close();
            }
            catch (IOException e) {
                logger.fatal("Could not close file " + softPackFile.getPath() + ": " + e.toString());
                e.printStackTrace();
            }
        }
        return softPackFile;
    }
    
    /**
     * Gets all software languages from the software archive
     * @return List of all languages used by software in the archive
     * @throws IOException If a connection error occurs when contacting the Software Archive
     */
    public List<Language> getSoftwareLanguages() throws IOException {
        try {
        	List<Language> languages = new ArrayList<Language>();        	
        	SwLanguageList softwareLanguageIds = softwareArchive.getSoftwareLanguages();
        	for (String languageId : softwareLanguageIds.getLanguageIds()) {
        		languages.add(Language.valueOf(languageId));
        	}
            return languages;
        }
    	catch (IOException e) {
    		processIOException(e, "software archive");
    	}
    	catch (WebServiceException wse) {
    		processWebServiceException(wse, "Software archive");
    	}
    	return null;
    }

    
    /**
     * Deal with IOExceptions thrown during Webservice calls
     * @param e the caught IOException
     * @param archive which archive was being contacted (software or emulator)
     * @throws IOException A new IOException, consisting of the original plus a informative message.
     */
	private void processIOException(IOException e, String archive) throws IOException {
		String message = "Connection to " + archive + " failed (unknown error): ";
		if (e instanceof ConnectException) {
			message = "Cannot connect to " + archive + ": ";
		}
		else if (e instanceof SocketTimeoutException) {
			message = "Connection to " + archive + " timed out: ";
		}
		logger.error(message + ExceptionUtils.getStackTrace(e));
		throw new IOException(message, e);
	}
	
	/**
     * Deal with WebServiceExceptions thrown during Webservice calls
	 * @param wse the caught IOException
     * @param archive which archive was being contacted (software or emulator)
	 * @throws IOException A new IOException, consisting of the original plus a informative message.
	 */
	private void processWebServiceException(WebServiceException wse, String archive)
			throws IOException {
		logger.error(archive + " failed (unknown error): "  + ExceptionUtils.getStackTrace(wse));
		throw new IOException(archive + " failed (unknown error): ", wse);
	}

}
