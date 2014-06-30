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

package eu.keep.characteriser;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import eu.keep.characteriser.registry.Registry;
import eu.keep.downloader.SoftwareArchive;
import eu.keep.downloader.db.DBRegistry;
import eu.keep.downloader.db.SoftwareArchivePrototype;
import eu.keep.softwarearchive.pathway.ApplicationType;
import eu.keep.softwarearchive.pathway.EfFormat;
import eu.keep.softwarearchive.pathway.HardwarePlatformType;
import eu.keep.softwarearchive.pathway.ObjectFormatType;
import eu.keep.softwarearchive.pathway.OperatingSystemType;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.util.XMLUtilities;

/**
 * The {@code Characteriser} class holds registry functionality (such as
 * importing registries
 * into the database, enabling registries for lookup) and characterisation
 * functionality
 * (such as characterising files, looking up pathways).
 * @author David Michel
 * @author Bram Lohman
 */
public class Characteriser {

    private static final Logger logger = Logger.getLogger(Characteriser.class.getName());
    private Map<File, FitsOutput> fitsCache;		// Cache of FITS results (optimisation)
    private FitsTool fitsTool;
    private SoftwareArchive softwareArchive;
    
    private static final String fitsHome = "eu/keep/resources/fits";

    /**
     * Constructor
     * @param props Properties (from user.properties) specifying the URL for the SWA webservice server.
     * @throws IOException If FITS connection cannot be set up properly
     */
    public Characteriser(Properties props) throws IOException {
    	fitsCache = new HashMap<File, FitsOutput>();

    	// Get FITShome url
    	URL url = null;
    	logger.info("Attempting to read FITShome from file...");
    	try {
    		File fitsLoc = new File(fitsHome);
    		logger.debug("Using FITShome file location: " + fitsLoc);
    		if (fitsLoc.exists()) {
    			url = fitsLoc.toURI().toURL();
    		}
    	}
    	catch (MalformedURLException me) {
    		logger.debug("Invalid URL created for FITShome (file): " + me);
    	}    	
    	// If it's not found, try as resource
    	if (url == null) {
    		logger.info("FITShome not found in file, attempting to read as resource...");
    		url = this.getClass().getClassLoader().getResource(fitsHome);
    	}    	
    	// If still not found, throw exception
    	if (url == null) {
    		logger.warn("No valid FITS home file found (as resource or file)");
    		throw new IOException("No valid FITS home file found (as resource or file)");
    	}
    	
    	// get the FITShome path, and start FITS
    	String fitsHomePath = "";
    	try {
    		logger.info("FITShome found as url: " + url.toString());
    		fitsHomePath = url.toURI().getRawPath().replaceAll("%20"," ");
    	} catch (URISyntaxException e) {
    		throw new IOException("Cannot read resources directory: " + fitsHomePath);
    	}
    	fitsTool = new FitsTool(fitsHomePath);
    	logger.info("FITS home succesfully set");
    	
    	// Create a webservice client to communicate with the SWA
    	softwareArchive = createSWA(props); 
    }

    protected SoftwareArchive createSWA(Properties props) {
    	return new SoftwareArchivePrototype(props);
    }

    /**
     * Queries the registry for the pathway of a file format
     * @param fileFormat The format of the digital object
     * @param registry The registry to query for a pathway
     * @return The pathway for the given file format, according to the given
     *         registry
     * @throws IOException On any registry error
     */
    private List<Pathway> getPathwayFromReg(String fileFormat,
           DBRegistry registry) throws IOException {

        List<Pathway> foreignPathways;

        // Create a class based on the registry className
        try {
            Class<?> regClass = Class.forName(registry.getClassName());
            Registry regInt = (Registry) regClass.newInstance();
            foreignPathways = regInt.getEmulationPathWays(fileFormat);
        }
        catch (ClassNotFoundException e) {
            logger.error("Cannot find class for technical registry " + registry.toString()
                    + ": " + e.toString());
            throw new IOException("Cannot find class for technical registry " + registry.toString()
                    + ": " + e.toString());
        }
        catch (InstantiationException e) {
            logger.error("Cannot instantiate technical registry " + registry.toString()
                    + ": " + e.toString());
            throw new IOException("Cannot instantiate technical registry " + registry.toString()
                    + ": " + e.toString());
            }
        catch (IllegalAccessException e) {
            logger.error("Cannot access class for technical registry " + registry.toString()
                    + ": " + e.toString());
            throw new IOException("Cannot access class for technical registry " + registry.toString()
                    + ": " + e.toString());
        }
        
        // Translate pathways to EF identifiers
        List<Pathway> pathways = translatePathways(foreignPathways, registry.getTranslationView());

        return pathways;
    }

    /**
     * Translates a list of pathways from an external registry to a pathway using EF names and IDs
     * NOTE: Only translation of Digital Object ID is currently done; Application, OS and Platform is unsupported
     * @param foreignPathways A list of pathways from an external registry 
     * @param databaseView name of view to use to translate the IDs 
     * @return List of Pathways using EF names and IDs
     */
    private List<Pathway> translatePathways(List<Pathway> foreignPathways, String databaseView) 
    		throws IOException {
    	
    	List<Pathway> localPathways = new ArrayList<Pathway>();
    	
    	logger.info("Translating " + foreignPathways.size() + " pathways to EF IDs/names...");
    	for (Pathway pathway : foreignPathways) {
    		Pathway pw = new Pathway();
    		ObjectFormatType objF = new ObjectFormatType();
    		
			try {
	    		List<EfFormat> formatData;
				formatData = softwareArchive.getFormatDataOnID(pathway.getObjectFormat().getId(), databaseView);
				if (!formatData.isEmpty()) {
					EfFormat format = formatData.get(0); // Take the first returned format					
					objF.setId(format.getId());
		    		objF.setName(format.getName());
		    		objF.setDescription(""); // Not returned by webservice
				}
			} 
			catch (Exception e) {
				processSWAExceptions(e, "retrieve EF fileformat information", false);
			}

			// Only file formats are currently supported 
    		ApplicationType app = new ApplicationType();
        	OperatingSystemType opsys = new OperatingSystemType();
        	HardwarePlatformType hpf = new HardwarePlatformType();

        	pw.setObjectFormat(objF);
        	pw.setApplication(app);
        	pw.setOperatingSystem(opsys);
        	pw.setHardwarePlatform(hpf);
        	
        	logger.info("Translated pathway file format: " + pathway.getObjectFormat().getId() + " to: " + pw.getObjectFormat().getId());
        	localPathways.add(pw);
    	}
		
		return localPathways;
	}

	/**
     * Characterises a file using the FITS tool
     * @param file input File to be characterised
     * @return List<Format> list of formats for the characterised object
     * @throws IOException If a characterisation error occurs
     */
    public List<Format> characterise(File file) throws IOException {

        List<Format> formats;
        FitsOutput fitsOut = examineFile(file);
        formats = fitsTool.getFormats(fitsOut);
        logger.debug("Formats returned: " + formats);

        return formats;
    }

    /**
     * Returns the file information from a file
     * @param file The input file to be characterised
     * @return Map<String, List<String>> a Map of item names (as keys) and an associated list of values 
     * @throws IOException If a characterisation error occurs
     */
    public Map<String, List<String>> getFileInfo(File file) throws IOException 
    {    
        Map<String, List<String>> techMD;

        FitsOutput fitsOut = examineFile(file);
	    try {
			techMD = fitsTool.getFileInfo(fitsOut);
		} catch (FitsException e) {
            logger.error("Error occurred during characterisation: " + e.toString());
            throw new IOException("Error occurred during FITS characterisation: " + e.toString());
		}
	    logger.debug("File info: " + techMD.toString());
	    return techMD;
    }

    /**
     * Returns the technical metadata from a file
     * @param file The input file to be characterised
     * @return Map<String, List<String>> a Map of item names (as keys) and an associated list of values
     * @throws IOException If a characterisation error occurs
     */
    public Map<String, List<String>> getTechMetadata(File file) throws IOException 
    {    
        Map<String, List<String>> techMD;

        FitsOutput fitsOut = examineFile(file);
	    try {
			techMD = fitsTool.getMetadata(fitsOut);
		} catch (FitsException e) {
            logger.error("Error occurred during characterisation: " + e.toString());
            throw new IOException("Error occurred during FITS characterisation: " + e.toString());
		}
	    logger.debug("Technical metadata: " + techMD.toString());
	    return techMD;
    }
    
    /**
     * Optimised wrapper for FITS file examination. Looks in a cache to see
     * if a file has been examined previously; if so, it will return the previously 
     * created results. For uncached files, the fits.examine() method is called;
     * @param file Input file to examine
     * @return FitsOutput FITS output XML
     * @throws IOException If a characterisation error occurs
     */
    private FitsOutput examineFile(File file) throws IOException {
        // sanity checks
        if (!file.exists()) {
            logger.error("Cannot find file: " + file.getPath());
            throw new IOException("Cannot find file: " + file.getAbsolutePath());
        }

        // Call FITS to examine the given file
    	// As the examination is a slow operation, check the cache for previous results 
        try {
        	if (fitsCache.containsKey(file))
        	{
        		logger.debug("File " + file + " found in cache; returning cached results...");
        		return fitsCache.get(file);
        	}
        	else
        	{
	        	FitsOutput fitsOut = fitsTool.examine(file);
	            // Cache the results for fast retrieval if operation is repeated
	            // Ensure hashmap size doesn't get out of hand
	            if (fitsCache.size() > 100)	// Randomly selected number
	            {
	            	logger.debug("Limiting FITS cache size (currently " + fitsCache.size() + "); clearing cache");
	            	fitsCache.clear();
	            }

        		logger.debug("File " + file + " not found in cache, adding...");
	            fitsCache.put(file, fitsOut);
	            return fitsOut;
        	}
        }
        catch (FitsException e)
        {
            logger.error("Error occurred during characterisation: " + e.toString());
            throw new IOException("Error occurred during FITS characterisation: " + e.toString());
        }
    }
    
    /**
     * Generate a list of {@code Pathway} objects for a given file format
     * by contacting technical registries. The Pathways will use EF IDs and names, even
     * for pathways generated by external registries.
     * @param format A File Format object
     * @return a list of the generated pathways
     * @throws IOException 
     */
    public List<Pathway> generatePathway(Format format) throws IOException {

        // sanity checks
        if(softwareArchive == null) {
            throw new IllegalStateException("SoftwareArchive not set");
        }

        List<Pathway> pathList = new ArrayList<Pathway>();

        // Get pathways from all enabled supported registries
        // Returns dummy (stubbed) pathways from getEmulationPathWays()
        // implementation of Registry
        List<DBRegistry> regList = new ArrayList<DBRegistry>();
        try {
            regList.addAll(softwareArchive.getRegistries());
            logger.info("Retrieved " + regList.size() + "registries from SWA");
		} 
        catch (Exception e) {
			processSWAExceptions(e, "retrieve the list of Registries", true);
		}

        for (DBRegistry reg : regList) {
            if (reg.isEnabled()) {
                List<Pathway> pw = getPathwayFromReg(format.getName(), reg);
                if (!pw.isEmpty()) {
                    pathList.addAll(pw);
                }
            }
        }
        return pathList;
    }

    /**
     * Returns a list of registries registered in the local database
     * @return a {@code java.util.List} of {@code RegistryCatalogue.Registry} object
     * @throws IOException
     */
    public List<DBRegistry> getRegistries() throws IOException  {
        try {
            return softwareArchive.getRegistries();
		} 
        catch (Exception e) {
			processSWAExceptions(e, "retrieve the list of Registries", true);
		}
		return null;
    }

    /**
     * Sets a new list of registries in the local database
     * @param regList new {@code java.util.List} of {@code RegistryCatalogue.Registry}
     * @return {@code true} if successful, {@code false} otherwise
     * @throws IOException
     */
    public boolean setRegistries(List<DBRegistry> regList) throws IOException {
        try {
            return softwareArchive.setRegistries(regList);
        }
        catch (Exception e) {
			processSWAExceptions(e, "insert a new list of Registries", true);
		}
		return false;
    }
    
    /**
     * Returns a {@code Pathway} object from a given metadata (xml)
     * file, validated by a given schema
     * @param pathwayFile Input metadata xml file
     * @param validationSchema schema used for validation
     * @return a {@code Pathway} object the pathway found in the
     *         metadata file
     * @throws IOException when validation or data transfer from file to object
     *             fails
     */
    public Pathway getPathwayFromFile(File pathwayFile, Source validationSchema)
            throws IOException {

        try {
            XMLUtilities.validateXML(validationSchema, pathwayFile);
        }
        catch (SAXException se) {
            logger.error("Pathway xml file did not validate: " + pathwayFile
                    + ": " + se.getMessage());
            throw new IOException("Pathway xml file did not validate: " + pathwayFile
                    + ": " + se.getMessage());
        }
        logger.debug("Pathway xml file validated: " + pathwayFile);

        Pathway pathway;
        try {
            pathway = (Pathway) XMLUtilities.loadFromXML(pathwayFile);
        }
        catch (JAXBException e) {
            logger.error("Error parsing XML: " + e.getMessage());
            throw new IOException("Error parsing XML: " + e.getMessage());
        }
        logger.debug("Pathway file successfully parsed");

        return pathway;
    }

    /**
     * Process exceptions that can be thrown during a call to the SWA webservice
     * @param e the exception that was thrown 
     * @param rethrow true to rethrow the exception as a IOException, false to only log an error message.
     * @throws IOException
     */
	private void processSWAExceptions(Exception e, String detailedMessage, boolean rethrow) throws IOException {
		String message = "";
		if (e instanceof ConnectException) {
			message = "Cannot connect to software archive to " + detailedMessage + ": ";					
		}
		else if (e instanceof SocketTimeoutException) {
			message = "Connection to software archive to " + detailedMessage + " timed out: ";					
		}
		else {
			message = "Connection to software archive to " + detailedMessage + " failed (unknown error): ";					
		}
		logger.error(message + ExceptionUtils.getStackTrace(e));
		
		if (rethrow) {
			throw new IOException(message, e);
		}
	}
	
}
