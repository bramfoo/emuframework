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

package eu.keep.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import eu.keep.characteriser.Characteriser;
import eu.keep.characteriser.Format;
import eu.keep.controller.Controller;
import eu.keep.downloader.Downloader;
import eu.keep.downloader.db.DBRegistry;
import eu.keep.downloader.db.DBUtil;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;
import eu.keep.util.FileUtilities;
import eu.keep.util.Language;

/**
 * Main class of the Core Emulation Framework. Implements the API.
 * Delegates work to other components; acts as a 'model' as given by the MVC pattern
 * @author Bram Lohman
 * @author David Michel
 */
public class Kernel implements CoreEngineModel {

    private static final Logger         logger         = Logger.getLogger(Kernel.class.getName());
    private static Properties           props;

    private final String		        PROP_FILE_NAME;
    private final static String         PATHWAY_SCHEMA = "PathwaySchema.xsd";

    private Connection                  localDBConnection;

    private final List<CoreObserver>    coreObs;
    private final Characteriser         characteriser;
    private final Controller            controller;
    private final Downloader		    downloader;

    private Set<Language> 				acceptedLanguages = new HashSet<Language>(); 
    
    /**
     * Constructor
     * @param propFileName The properties file name
     * @throws IOException 
     */
    public Kernel(String propFileName) throws IOException {
    	PROP_FILE_NAME = propFileName;
        coreObs = new ArrayList<CoreObserver>();
        props = new Properties();
        
        boolean result = init();
        if (!result)
        	throw new IOException("Failed to initialise Kernel properly");

        // Both the properties and db connection are working so no errors expected here
        characteriser = new Characteriser(localDBConnection);
        controller = new Controller();
        downloader = createDownloader(props, localDBConnection);
        
        // Now that downloader is set up, can initialise the accepted Languages
        initAcceptedLanguages();
    }
    
	// Support for mocking
    protected Downloader createDownloader(Properties props, Connection conn) {
    	return new Downloader(props, conn);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Properties getCoreSettings() {
        return props;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerObserver(CoreObserver o) {
        if (o == null)
            throw new NullPointerException();
        if (!coreObs.contains(o)) {
            coreObs.add(o);
            logger.debug("Registered CoreObserver " + o.toString());
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void removeObserver(CoreObserver o) {
        coreObs.remove(o);
        logger.debug("Removed CoreObserver " + o.toString());
    }

    /**
     * Notify the observer with a notification string
     * @param source String representing the actual notification text
     */
    private void notifyObservers(String source) {
        // Send "news" to all observers
        for (CoreObserver o : coreObs) {
            o.update(source);
        }
    }

    /**
     * @inheritDoc
     */
    public String getVersion() {
        return Kernel.class.getPackage().getSpecificationVersion();
    }

    /**
     * @inheritDoc
     */
    public String getTitle() {
        return Kernel.class.getPackage().getSpecificationTitle();
    }

    /**
     * @inheritDoc
     */
    public String getVendor() {
        return Kernel.class.getPackage().getSpecificationVendor();
    }

    /**
     * Initialize. Calls the methods required to set up and configure
     * the model, such as loading of parameters, connecting to databases, etc.
     * This is a required call to successfully run the Core Engine
     * @return True if initialization successful, false otherwise
     * @throws IOException On initialization problems, such as database connection problems
     */
    private boolean init() throws IOException {

        // display project info (only runs from a jar as it reads the jar manifest file)
        String title = getTitle();
        String version = getVersion();
        if(version!=null && title!=null) {
            logger.info(title + " version " + version);
        }

        boolean result = true;

        InputStream is = null;
    	try {
    		logger.info("Attempting to read " + PROP_FILE_NAME + " from file...");
            is = new FileInputStream(PROP_FILE_NAME);
    	}
    	catch (FileNotFoundException fe)
    	{
    		logger.debug("user.property not found as file: " + fe);
    	}
        // If it's not found, try as resource
        if(is == null) {
            // Read the properties file, location independent
            logger.info("user.property not found as file, attempting to read as resource...");
            is = this.getClass().getClassLoader().getResourceAsStream("eu/keep/" + PROP_FILE_NAME);
        }
        if(is == null)
        {
        	logger.warn("No valid user.properties file found (as resource or file)");
        	throw new IOException("No valid user.properties file found (as resource or file)");
        }
                
        logger.info(PROP_FILE_NAME + " succesfully read");
        props = FileUtilities.getProperties(is);
        result &= props != null;

        // set up connection to databases
        String dbLocation = props.getProperty("h2.db.url");
        String driverName = props.getProperty("h2.db.driver");
        String dbUrl = props.getProperty("h2.jdbc.prefix") + dbLocation + props.getProperty("h2.db.server") + props.getProperty("h2.db.exists");
        String dbUrlEngine = dbUrl + props.getProperty("h2.db.schema");
        String dbUser = props.getProperty("h2.db.user");
        String dbPasswd = props.getProperty("h2.db.userpassw");
        
        int connectionAttempts = 5;
        localDBConnection = DBUtil.establishConnection(driverName, dbUrlEngine, dbUser,
                dbPasswd, connectionAttempts);
        result &=  localDBConnection != null;

        // Tell Apache CXF to use log4j so we can use the same logging properties file
        try {
            System.setProperty("org.apache.cxf.Logger", "org.apache.cxf.common.logging.Log4jLogger");
            logger.debug("Apache CXF logging system property set to 'log4j'");
        }
        catch (Exception e) {
            logger.warn("Cannot change Apache CXF logging to log4j; CXF will use default Java logger");
        }

        String message = "Kernel successfully initialised !";
        notifyObservers(message);
        logger.info(message);
        
        return result;
    }

    /**
     * Initialize the set of accepted languages. Reads in property "accepted.languages".
     * @throws IOException On initialization problems, such as database connection problems
     */
    private void initAcceptedLanguages() throws IOException {
    	// Get the accepted Languages from properties
    	logger.debug("Reading current acceptable languages from property accepted.languages");
    	String acceptedLanguagesProp = props.getProperty("accepted.languages");

    	if (acceptedLanguagesProp.equalsIgnoreCase("all")) {
    		logger.debug("Marking all available languages as acceptable.");
    		// Get all available languages, from both EmulatorArchive and SoftwareArchive
    		// The Set interface of acceptedLanguages will ensure that no duplicate Languages are added.
			try {
	    		acceptedLanguages.addAll(getEmulatorLanguages());
	    		acceptedLanguages.addAll(getSoftwareLanguages());	
			} catch (IllegalArgumentException iae) {
				logger.fatal("Cannot initialise Set of accepted Languages: EmulatorArchive or SoftwareArchive contains invalid language ID.");
				throw iae;
			} catch (IOException ioe) {
				logger.fatal("Cannot initialise Set of accepted Languages: EmulatorArchive or SoftwareArchive could not be contacted.");
				throw ioe;				
			}
    		logger.info("Total of " + acceptedLanguages.size() + " languages available in Emulator and Software Archives. All accepted.");
    	} else {
    		String[] acceptedLangIds = acceptedLanguagesProp.split(",");
    		for (String acceptedLangId : acceptedLangIds) {
    			try {
	    			Language acceptedLanguage = Language.valueOf(acceptedLangId);
	    			logger.info("Marking language as acceptable: " + acceptedLanguage.getLanguageId());
	    			acceptedLanguages.add(acceptedLanguage);
    			} catch (IllegalArgumentException iae) {
    				logger.warn("'accepted.languages' property in property-file contains unknown language ID: " + 
    						acceptedLangId + ". Ignoring...");
    			}
    		}
    	}
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Format> characterise(File digObj) throws IOException {

        List<Format> formats = null;

            formats = characteriser.characterise(digObj);
            if (!formats.isEmpty()) {
                logger.info("Digital object '" + digObj.getPath() + "' successfully identified");
                for (Format format : formats) {
                    logger.info("Format found: " + format.toString());
                }
            }
            else {
                logger.error("Digital object '" + digObj + "' could not be identified");
                return formats;
            }

        return new ArrayList<Format>(formats);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Map<String, List<String>> getTechMetadata(File digObj) throws IOException
    {
    	return characteriser.getTechMetadata(digObj);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Map<String, List<String>> getFileInfo(File digObj) throws IOException
    {
    	return characteriser.getFileInfo(digObj);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Format autoSelectFormat(List<Format> formats) throws IOException {

        // Sanity check
        if (formats == null || formats.isEmpty()) {
            throw new IOException("Cannot autoselect format from empty/nonexisting list");
        }

        // Simply pick the first one of the list which corresponds to
        // the format identified by the highest number of  tools within FITS
        return formats.get(0);
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Pathway> getPathways(Format format) throws IOException {

    	// Query technical registry and EF database for pathways 
    	List<Pathway> pathways;
    	pathways = characteriser.generatePathway(format);
    	pathways.addAll(downloader.getPathwayByFileFormat(format.getName()));

    	// Filter by application and OS language
    	List<Pathway> filteredPathways = new ArrayList<Pathway>();
    	for (Pathway pathway : pathways) {
    		try {
    			if ((pathway.getOperatingSystem().getId().equals("-1") || 
    				 acceptedLanguages.contains(Language.valueOf(pathway.getOperatingSystem().getLanguageId()))) &&
    				(pathway.getApplication().getId().equals("-1") ||
    				 acceptedLanguages.contains(Language.valueOf(pathway.getApplication().getLanguageId())))
    				) {
    				filteredPathways.add(pathway);
    			} 
    			else {
    				logger.info("Pathway has OS or Application with language that is not selected in the EF settings. " + 
    						"OS ID = " + pathway.getOperatingSystem().getId() + "; language ID = " + pathway.getOperatingSystem().getLanguageId() + "; " + 
    						"Application ID = " + pathway.getApplication().getId() + "; language ID = " + pathway.getApplication().getLanguageId());
    			}
    		} catch (IllegalArgumentException iae) {
    			logger.error("Pathway has OS or Application with invalid language ID. " + 
						"OS ID = " + pathway.getOperatingSystem().getId() + "; language ID = " + pathway.getOperatingSystem().getLanguageId() + "; " + 
						"Application ID = " + pathway.getApplication().getId() + "; language ID = " + pathway.getApplication().getLanguageId());
    		}
    	}

    	logger.info("Pathways found for the format '" + format + "': " + filteredPathways.toString());

    	return filteredPathways;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Pathway autoSelectPathway(List<Pathway> pathways) throws IOException {
        
        // Sanity check
        if (pathways == null || pathways.isEmpty())
        {
            throw new IOException("Cannot autoselect pathway from empty/nonexisting list");
        }

        for (Pathway pw : pathways) {
            logger.info("Checking satisfiability of pathway: " + pw.toString());

            boolean res = isPathwaySatisfiable(pw);
            logger.debug("pathway " + pw.toString() + " is satisfiable: " + res);

            if (res) {
                return pw;
            }
        }
        throw new IOException("No satisfiable pathway found");
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isPathwaySatisfiable(Pathway pathway) throws IOException {

		// If the pathway object is not valid, return false
		if(pathway.getObjectFormat().getId().equalsIgnoreCase("-1") || pathway.getHardwarePlatform().getId().equalsIgnoreCase("-1")) {
			return false;
		}

		// check if there are emulators in the internal db satisfying the hardware part of the pathway
		List<EmulatorPackage> emuPacks = getEmulatorsByPathway(pathway);
		if(emuPacks.isEmpty() || emuPacks == null) {
			logger.info("No emulators found, matching required hardware");
			return false;
		}

		// check if software image is required
		if(pathway.getApplication().getId().equalsIgnoreCase("-1") && pathway.getOperatingSystem().getId().equalsIgnoreCase("-1")) {
			logger.info("App/OS not defined, no software image necessary");
			return true;
		}
		else {
			// Perform matching between emulators and software images
			// as some images may not be compatible with certain emulators
			logger.info("Attempting to match emulators and software images...");
			Map<EmulatorPackage, List<SoftwarePackage>> emuSwPacks = matchEmulatorWithSoftware(pathway);
			if (emuSwPacks.isEmpty()) {
				return false;
			}
		}
		return true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<EmulatorPackage> getEmulatorsByPathway(Pathway pathway) throws IOException {

        List<EmulatorPackage> emuPacks = new ArrayList<EmulatorPackage>();
        List<EmulatorPackage> whiteListedPacks = new ArrayList<EmulatorPackage>();
        List<EmulatorPackage> finalPacks = new ArrayList<EmulatorPackage>();
        
        // get single component from pathway
        String hw = pathway.getHardwarePlatform().getName();

                emuPacks = downloader.getEmulatorsByHardware(hw);
                if (emuPacks.isEmpty()) {
                    logger.warn("No emulators found matching " + hw + " hardware" + ": ");
                    return emuPacks;
                }

        logger.info("Found " + emuPacks.size() + " emulators matching hardware '" + hw + "': " + emuPacks);
        
        
        // Detect current host operating system
        String osName;
        try {
            osName = System.getProperty("os.name");
            // Set default if null is returned
            osName = osName == null ? "Windows.default" : osName;
            logger.info("Host operating system detected as '" + osName +"'. If this is incorrect, successful emulation is not guaranteed!");
        }
        catch (Exception e) {
            logger.error("Could not detect the OS name. Defaulting to 'Windows'. Successful emulation not guaranteed!");
            osName = "Windows.default";
        }
        
        // Ensure that only explicitly selected emulators, running on the current host operating system, are used
        String emuType;
        whiteListedPacks = getWhitelistedEmus();
        for (EmulatorPackage emuPack : emuPacks)
        {        		
        	emuType = emuPack.getEmulator().getExecutable().getType();
        	logger.debug("Found emulator executable type: " + emuType);
        	if (emuType.equals("exe") && osName.matches("Windows.*") || 
        			emuType.equals("ELF") && osName.matches("Linux") || 
        			emuType.equals("jar")) {
        		logger.info("Executable type of emulator " + emuPack.getEmulator().getName() + emuPack.getEmulator().getVersion() + 
        				" matches host operating system, so can be used");
        		
        		for (EmulatorPackage whitePack: whiteListedPacks)
        		{
        			if (emuPack.getPackage().getId() == whitePack.getPackage().getId())
        			{
        				logger.info("Emulator " + emuPack.getEmulator().getName() + emuPack.getEmulator().getVersion() + " is on whitelist, can be used");
        				finalPacks.add(emuPack);
        				break;
        			}
        		}           
        	}
        	else {
        		logger.info("Executable type of emulator " + emuPack.getEmulator().getName() + emuPack.getEmulator().getVersion() + 
        				" does not match host operating system, so cannot be used");
        	}
        }

        logger.info("Final list of " + finalPacks.size() + " emulators matching hardware '" + hw + "' after removing non-whitelised emulators: " + finalPacks);
        return new ArrayList<EmulatorPackage>(finalPacks);
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<SoftwarePackage> getSoftwareByPathway(Pathway pathway) throws IOException {

        List<SoftwarePackage> swPacks = new ArrayList<SoftwarePackage>();

        // get APP and OS name from pathway
        String os = pathway.getOperatingSystem().getId();
        String app = pathway.getApplication().getId();

        // If app and os are empty, the pathway does not require any software images
        if(app.equalsIgnoreCase("-1") && os.equalsIgnoreCase("-1")) {
        	logger.info("App/OS not defined, no software image necessary (returning null package)");
        	SoftwarePackage sw = new SoftwarePackage();
        	sw.setId("0");
        	sw.setDescription("N/A");
        	sw.setFormat("0");
        	swPacks.add(sw);
        	return new ArrayList<SoftwarePackage>(swPacks);
        }

        // Get a list of software images that satisfies the pathway (OS and/or app)
        logger.debug("Retrieving list of software images for pathway " + pathway);
        swPacks = downloader.getSoftWarePackageList(pathway);

        // If software archive is empty, return empty list
        if (swPacks.isEmpty()) {
            logger.warn("No software images found matching os='" + os + "' and app='" + app +"'");
            return swPacks;
        }
        else {
            logger.info("Found " + swPacks.size() + " software images matching os='" + os + "' and app='" + app +"': "
                    + swPacks.toString());
        }

        // return
        return new ArrayList<SoftwarePackage>(swPacks);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Map<EmulatorPackage, List<SoftwarePackage>> matchEmulatorWithSoftware(Pathway pathway) throws IOException {
    	List<EmulatorPackage> emuPacks;
    	List<SoftwarePackage> swPacks;

    	// Get list of potential emulator and software images that satisfy the pathway
    	try {
    		emuPacks = getEmulatorsByPathway(pathway);
    		swPacks = getSoftwareByPathway(pathway);
    	}
    	catch (IOException e) {
    		logger.error("Error while retrieving list of potential " +
    				"emulators and software images from pathway: " + e.toString());
    		throw e;
    	}

    	Map<EmulatorPackage, List<SoftwarePackage>> emuSwMap = new HashMap<EmulatorPackage, List<SoftwarePackage>>();
    	List<String> formats_emu;

    	try {
    		logger.info("Starting emulator hardware/software image matching " +
    				"(" + emuPacks.size() +"/" + swPacks.size() +") and language filtering");

    		// Get a Map of emulators (as keys) and a list of image IDs (as value)
    		for (EmulatorPackage emuPack : emuPacks) {

    			// Check if the user accepts the language of this emulator
    			if (this.acceptedLanguages.contains(Language.valueOf(emuPack.getEmulator().getLanguageId()))) {
    				logger.debug("Emulator " + emuPack.getEmulator().getName() + emuPack.getEmulator().getVersion() + 
    						" has acceptable language: " + 
    						Language.valueOf(emuPack.getEmulator().getLanguageId()).getLanguageName());

    				// Get the list of formats supported by that emulator
    				formats_emu = emuPack.getEmulator().getImageFormat();
    				logger.debug("Emulator " + emuPack.getEmulator().getName() + emuPack.getEmulator().getVersion() + " supports formats " + formats_emu);

    				if (!formats_emu.isEmpty()) {
    					// loop over format supported by this emulator
    					Set<SoftwarePackage> swPackSet = new HashSet<SoftwarePackage>();
    					for (String format_emu : formats_emu) {
    						// get all images whose format is compatible
    						for (SoftwarePackage swPack : swPacks) {
    							String format = downloader.getSoftwarePackageFormat(swPack.getId());
    							if (format.equals(format_emu) || format.equalsIgnoreCase("N/A")) {
    								swPackSet.add(swPack);
    							}        								
    						}
    					}
    					// Store the emulator - list of compatible software image IDs
    					if(!swPackSet.isEmpty()) {
    						emuSwMap.put(emuPack, new ArrayList<SoftwarePackage>(swPackSet));
    					}
    				}        			
    				else {
    					logger.warn("Emulator " + emuPack.getEmulator().getName() + " " + emuPack.getEmulator().getVersion() + 
    							" does not support any image formats");
    				}	
    			}      		
    			else {
    				logger.warn("Emulator " + emuPack.getEmulator().getName() + " " + emuPack.getEmulator().getVersion() + 
    						" has a language that is not selected in the EF settings: " + emuPack.getEmulator().getLanguageId() + 
    						" - " + Language.valueOf(emuPack.getEmulator().getLanguageId()).getLanguageName());
    			}

    		}
    	}
    	catch (IllegalArgumentException e) {
    		logger.error("Inappropriate ID requested: " + e.getMessage());
    		throw new IOException("Inappropriate ID requested: " + e.getMessage());
    	}

    	logger.info("Found compatible emulator-images pairs: " + emuSwMap);
    	return new HashMap<EmulatorPackage, List<SoftwarePackage>>(emuSwMap);
    }

    /**
     * @inheritDoc
     */
    @Override
    public SoftwarePackage autoSelectSoftwareImage(List<SoftwarePackage> swPackList) throws IOException {

        // Sanity check
        if (swPackList == null || swPackList.isEmpty())
        {
            throw new IOException("Cannot autoselect image from empty/nonexisting list");
        }

        return swPackList.get(0);
    }

    /**
     * @inheritDoc
     */
    @Override
    public EmulatorPackage autoSelectEmulator(List<EmulatorPackage> emuPacks) throws IOException {

        // Sanity check
        if (emuPacks == null || emuPacks.isEmpty())
        {
            throw new IOException("Cannot autoselect emulator from empty/nonexisting list");
        }

        // Simply pick the last (most recent) emulator of 
        // the list that has a compatible executable 
        // type with the current host operating system.
        String osName;
        try {
            osName = System.getProperty("os.name");
            // Set default if null is returned
            osName = osName == null ? "Windows.default" : osName;
            logger.info("Host operating system detected as '" + osName +"'. If this is incorrect, successful emulation is not guaranteed!");
        }
        catch (Exception e) {
            logger.error("Could not detect the OS name. Defaulting to 'Windows'. Successful emulation not guaranteed!");
            osName = "Windows.default";
        }

        for (EmulatorPackage emuPack : emuPacks) {
            String emuType;
            try {
                    emuType = emuPack.getEmulator().getExecutable().getType();
                    logger.info("Found emulator executable type: " + emuType);
            }
            catch (IllegalArgumentException e) {
                logger.error("Inappropriate ID requested: " + e.getMessage());
                continue;
            }

            if (emuType.equals("exe") && osName.matches("Windows.*") || emuType.equals("ELF")
                    && osName.matches("Linux") || emuType.equals("jar")) {
            	logger.info("Auto-selected emulator " + emuPack.getEmulator().getName() + emuPack.getEmulator().getVersion());
                return emuPack;
            }
        }

        // No result
        logger.error("No emulator autoselected");
        throw new IOException("No emulator autoselected");
    }

    /**
     * @inheritDoc
     */
    @Override
    public Map<String, List<Map<String, String>>> getEmuConfig(Integer conf) throws IOException {

        Map<String, List<Map<String, String>>> config = new HashMap<String, List<Map<String, String>>>();
        config = controller.getEmuConfig(conf);

        return config;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setEmuConfig(Map<String, List<Map<String, String>>> options, Integer conf) throws IOException {
        controller.setEmuConfig(options, conf);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Integer prepareConfiguration(File digObj, EmulatorPackage emuPack, SoftwarePackage swPack, Pathway pathway) throws IOException {

        File emuDir;
        File softImg = null;

        // Generate a unique directory
        try {
            emuDir = FileUtilities.GenerateUniqueDir(new File(props.getProperty("exec.dir")));
        }
        catch(FileNotFoundException e)
        {
            throw new IOException("Failed to find file required for preparation: " + e.toString());
        }

        // Download the emulator into the dir
        File emuExec = downloader.getEmuExec(emuPack.getPackage().getId(), emuDir);

        ArrayList<File> swImgs = new ArrayList<File>();
         	try {
                if (!(pathway.getApplication().getId().equalsIgnoreCase("-1") && pathway.getOperatingSystem().getId().equalsIgnoreCase("-1"))) {
                	logger.info("Preparing software image: " + swPack.getId());
                    softImg = installSoftwareImage(swPack, emuDir);
                    swImgs.add(softImg);
                }
            }
            catch(FileNotFoundException e)
            {
                throw new IOException("Failed to find file required for preparation: " + e.toString());
            }

        ArrayList<File> digObjs = new ArrayList<File>();
        digObjs.add(digObj);
        return controller.prepareConfiguration(emuDir, digObjs, swImgs, emuExec, pathway);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void runEmulationProcess(Integer conf) throws IOException {
        controller.runEmulationProcess(conf);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Pathway extractPathwayFromFile(File pathwayFile) throws IOException {

        // sanity check
        if (!pathwayFile.exists()) {
            logger.error("Error: File '" + pathwayFile.getAbsolutePath() + "' doesn't exists");
            throw new IOException("Cannot autoselect emulator from empty/nonexisting list");
        }

        InputStream is = null;
    	try {
    		logger.info("Attempting to read Pathway schema validation from file...");
            is = new FileInputStream(PATHWAY_SCHEMA);
    	}
    	catch (FileNotFoundException fe)
    	{
    		logger.debug("Pathway schema validation not found as file: " + fe);
    	}
        // If it's not found, try as resource
        if(is == null) {
            // Read the properties file, location independent
            logger.info("Pathway schema validation not found as file, attempting to read as resource...");
            is = this.getClass().getClassLoader().getResourceAsStream("eu/keep/resources/external/" + PATHWAY_SCHEMA);
        }
        if(is == null)
        {
        	logger.warn("No valid Pathway schema validation file found (as resource or file)");
        	throw new IOException("No valid Pathway schema validation file found (as resource or file)");
        }
        // get pathways from metadata file
        StreamSource validationSchema = new StreamSource(is);
        Pathway pathway = characteriser.getPathwayFromFile(pathwayFile, validationSchema);

        return pathway;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean start(File file, File metadata) throws IOException {

        // sanity check
        if (!file.exists()) {
            logger.error("Error: File '" + file.getAbsolutePath() + "' doesn't exists");
            return false;
        }
        if (!metadata.exists()) {
            logger.error("Error: File '" + metadata.getAbsolutePath() + "' doesn't exists");
            return false;
        }

        // get pathways from metadata file
        logger.info("Parsing metadata information...");
        Pathway pathway = extractPathwayFromFile(metadata);
        if (pathway == null) {
            logger.warn("No pathway defined in metadata file. Switching to automatic mode.");
            return start(file);
        }

        logger.info("Found pathway: " + pathway.toString());
        return start(file, pathway);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean start(File file) throws IOException {

        // sanity check
        if (!file.exists()) {
            logger.error("Error: File '" + file.getAbsolutePath() + "' doesn't exists");
            return false;
        }

        // Characterise object, i.e. identify the file format
        logger.info("Characterising the digital object...");
        List<Format> formats = characterise(file);
        if (formats.isEmpty()) {
            return false;
        }

        logger.info("Automatic selection of a format...");
        Format format = autoSelectFormat(formats);
        if (format == null) {
            logger.error("no format could be selected");
            return false;
        }
        logger.info("Format selected: " + format.toString());

        // Retrieve emulation pathways from the file format
        logger.info("Retrieving emulation pathways...");
        List<Pathway> pathways = getPathways(format);
        if (pathways == null || pathways.size() == 0) {
            logger.error("No pathways could be retrieved from format " + format);
            return false;
        }

        return start(file, pathways);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean start(File file, List<Pathway> pathways) throws IOException {

        Pathway pathway;
        logger.info("Automatic selection of a pathway from " + pathways);
        try {
            pathway = autoSelectPathway(pathways);
        }
        catch (IOException e) {
            logger.error("No satisfiable pathway could be autoselected from " + pathways.toString());
            throw new IOException("No satisfiable pathway could be autoselected from " + pathways.toString());
        }
        logger.info("Pathway selected: " + pathway.toString());

        return start(file, pathway);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean start(File file, Pathway pathway) throws IOException {

		List<EmulatorPackage> emuPacks = null;
		EmulatorPackage emuPack = null;
		SoftwarePackage swPack = null;

        // sanity check
        if (!file.exists()) {
            logger.error("Error: File '" + file.getAbsolutePath() + "' doesn't exists");
            return false;
        }
        if (pathway == null) {
            return false;
        }

		// check if pathway requires a software image or not
		if( pathway.getApplication().getId().equalsIgnoreCase("-1") && pathway.getOperatingSystem().getId().equalsIgnoreCase("-1")) {
			logger.info("No software image needed...");
			
		    // Select an emulator automatically from the list of potential emulators
		    logger.info("Automatic selection of an emulator...");
			emuPacks = getEmulatorsByPathway(pathway);
		    emuPack = autoSelectEmulator(emuPacks);
		    if (emuPack == null) {
		        logger.error("No emulator could be selected");
		        return false;
		    }
		    logger.info("Emulator " + emuPack.getEmulator().getName() + emuPack.getEmulator().getVersion() + " has been selected");
		}
		else {
		    // perform matching between emulators and software images
		    // as some images may not be compatible with certain emulators
		    Map<EmulatorPackage, List<SoftwarePackage>> emuSwPacks = matchEmulatorWithSoftware(pathway);
		    if (emuSwPacks.isEmpty()) {
		        return false;
		    }

		    // get list of viable emulators from the map of compatible emulator-image pairs
		    // (i.e. only those with a list of sw packages
		    emuPacks = new ArrayList<EmulatorPackage>();
		    for (Map.Entry<EmulatorPackage, List<SoftwarePackage>> entry : emuSwPacks.entrySet()) {
		    	if (!entry.getValue().isEmpty())
		    	{
		    		logger.debug("Possible emulator for pathway: " + entry.getKey());
		    		emuPacks.add(entry.getKey());
		    	}
		    }

		    // Select an emulator automatically from the list of potential emulators
		    logger.info("Automatic selection of an emulator...");
		    try {
		    	emuPack = autoSelectEmulator(emuPacks);
		    }
		    catch(IOException ioe) {
		        logger.error("No emulator could be selected: " + ioe);
		        return false;
		    }
		    logger.info("Emulator " + emuPack.getEmulator().getName() + "(ver. " + emuPack.getEmulator().getVersion() + ") has been selected");

		    // get the list of compatible software image IDs for the selected emulator
		    List<SoftwarePackage> swPackList = emuSwPacks.get(emuPack);
		    if (!swPackList.isEmpty()) {
		        // Select a software image automatically from the list of
		        // compatible images for the selected emulator
		        logger.info("Automatic selection of a compatible software image...");
		        swPack = autoSelectSoftwareImage(swPackList);
		        if (swPack.getId().equalsIgnoreCase("-1")) {
		            logger.error("No compatible software image could be selected");
		            return false;
		        }
		        logger.info("software image id=" + swPack + " has been selected");
		    }
		    else {
		        logger.warn("No compatible software image could be found for the selected emulator");
		    }
		}

        // Set up the emulator, i.e. download from db + perform automatic
        // configuration
        // Note: manual configuration can be done using getEmuOptions()
        // together with setEmuOptions() instead
        logger.info("Preparing configuration...");
        Integer conf = prepareConfiguration(file, emuPack, swPack, pathway);

        // Run the configured emulation process
        logger.info("Launching the emulation process...");
        runEmulationProcess(conf);

        notifyObservers("Starting emulation process: " + emuPack.getEmulator().getName() + ":" + emuPack.getEmulator().getVersion());
        return true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean stop() throws IOException {

        controller.stopProcesses();
        
        // Clean up the exec dir byr removing any remaining emulation process
        // tmp directory
        cleanUp();

        // Close JDBC connection to H2 database
        try {
            localDBConnection.close();
        }
        catch (SQLException e) {
            logger.error("Error closing database");
            throw new IOException("Database SQL error " + e.getErrorCode() + " " + e.getSQLState());
            }
        logger.info("Closed database");

        notifyObservers("'Stop' was called");
        return true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void cleanUp() {
        List<File> listDir = new ArrayList<File>();

        File execDirFile = new File(props.getProperty("exec.dir"));
        listDir.add(execDirFile);

        for (File dir : listDir) {

            logger.info("Deleting temporary folder: " + dir.getAbsolutePath());
            boolean result = FileUtilities.deleteDir(dir);

            if (!result) {
                logger.error("Cannot delete temporary folder (still in use) "
                        + dir.getAbsolutePath());
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<EmulatorPackage> getEmuListFromArchive() throws IOException {
            return downloader.getEmulatorPackages();
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<SoftwarePackage> getSoftwareListFromArchive() throws IOException {
        return downloader.getSoftwarePackageList();
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Language> getSoftwareLanguages() throws IOException {
        return downloader.getSoftwareLanguages();
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<DBRegistry> setRegistries(List<DBRegistry> listReg) throws IOException {

        boolean result = true;
        List<DBRegistry> regList = new ArrayList<DBRegistry>();

        result &= characteriser.setRegistries(listReg);
        regList = characteriser.getRegistries();

        return new ArrayList<DBRegistry>(regList);
    }

    /**
     * @throws IOException 
     * @inheritDoc
     */
    @Override
    public List<DBRegistry> getRegistries() throws IOException {
        List<DBRegistry> regList;
        regList = characteriser.getRegistries();
        return new ArrayList<DBRegistry>(regList);
    }
    
    /**
     * Download a software image from the software archive and install it in the
     * emulator root directory
     * @param swPack Integer representing the software image ID
     * @param emuDir File representing the emulator root directory
     * @throws IOException 
     */
    private File installSoftwareImage(SoftwarePackage swPack, File emuDir) throws IOException {
        File softImg;

        // Temporarily store the zip file
        File tmpDir = FileUtilities.GenerateUniqueDir(new File(props.getProperty("system.tmpdir")));

        softImg = downloader.getSoftwareImage(swPack.getId(), tmpDir);
        // Unpack the zip into the emulation directory
        List<File> unzips = FileUtilities.unZip(softImg, emuDir);

        return unzips.get(0);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Set<String> getSupportedHardwareFromArchive() throws IOException {
        return downloader.getSupportedHardware();
    }
    
    /**
     * @inheritDoc
     */
    @Override
    public List<EmulatorPackage> getEmusByHWFromArchive(String hardwareName) throws IOException {
            return downloader.getEmulatorsByHardware(hardwareName);
    }

    /**
     * @inheritDoc
     */
	@Override
	public boolean whiteListEmulator(Integer i) throws IOException {
		return downloader.whiteListEmulator(i);
	}

    /**
     * @inheritDoc
     */
	@Override
	public List<EmulatorPackage> getWhitelistedEmus() throws IOException {
		return downloader.getWhitelistedEmus();
		}

    /**
     * @inheritDoc
     */
	@Override
	public boolean unListEmulator(Integer i) throws IOException {
		return downloader.unListEmulator(i);
		}

    /**
     * @inheritDoc
     */
	@Override
    public List<Language> getEmulatorLanguages() throws IOException {
        return downloader.getEmulatorLanguages();
    }

    /**
     * @inheritDoc
     */
	@Override
	public void setAcceptedLanguages(Set<Language> acceptedLanguages) {
		for (Language language : acceptedLanguages) {
			logger.info("adding language to list of accepted languages: id = " + language.getLanguageId() + 
					"; name = " + language.getLanguageName());			
		}
		this.acceptedLanguages = acceptedLanguages;
		logger.debug("number of accepted languages now: " + this.acceptedLanguages.size());
	}

    /**
     * @inheritDoc
     */
	@Override
	public void addAcceptedLanguage(Language language) {
		// The Java.util.Set interface guarantees that if the existing Set already contains
		// the same Language, it will not be added as duplicate. 
		// (see the Language.equals() method on how Language objects are compared for equality).
		logger.info("adding language to list of accepted languages: id = " + language.getLanguageId() + 
				"; name = " + language.getLanguageName());
		this.acceptedLanguages.add(language);
		logger.debug("number of accepted languages now: " + this.acceptedLanguages.size());
	}

    /**
     * @inheritDoc
     */
	@Override
	public void removeAcceptedLanguage(Language language) {
		// The Java.util.Set interface guarantees that if the Set does not contain
		// the Language, it will be left intact. 
		// (see the Language.equals() method on how Language objects are compared for equality).
		logger.info("removing language from list of accepted languages: id = " + language.getLanguageId() + 
				"; name = " + language.getLanguageName());
		this.acceptedLanguages.remove(language);
		logger.debug("number of accepted languages now: " + this.acceptedLanguages.size());
	}

    /**
     * @inheritDoc
     */
	@Override
	public Set<Language> getAcceptedLanguages() {
		return acceptedLanguages;
	}
}