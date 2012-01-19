/*
$Revison$ $Date$
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import eu.keep.softwarearchive.pathway.ApplicationType;
import eu.keep.softwarearchive.pathway.EfFormat;
import eu.keep.softwarearchive.pathway.HardwarePlatformType;
import eu.keep.softwarearchive.pathway.ObjectFormatType;
import eu.keep.softwarearchive.pathway.OperatingSystemType;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.softwarearchive.SwLanguageList;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;


/**
 * Implementation of the SoftwareArchivePortType interface
 * The SoftwareArchivePortType interface is auto-generated by Apache CXF wsdl2java
 * Caution: this class can also be auto-generated by CXF (using the '-impl' flag),
 * but will contain empty methods. Do not overwrite! 
 *  
 * @author David Michel
 * @author Bram Lohman
 */
public class SoftwareArchivePortTypeImpl implements SoftwareArchivePortType {

    private static final Logger LOG = Logger.getLogger(SoftwareArchivePortTypeImpl.class.getName());
    private SoftwarePackageDAO  spDAO;

    /**
     * Constructor
     * @param spDAO Data Access Object
     */
    public SoftwareArchivePortTypeImpl(SoftwarePackageDAO spDAO) {
        this.spDAO = spDAO;
    }

    /**
     * Generate an software package for a specific software image
     * @param id Software ID
     * @return SoftwarePackage Software packages 
     */
    @Override
    public SoftwarePackage getSoftwarePackageInfo(String id) {
        LOG.info("Generating package for software ID " + id + "...");
        
    	// Ensure ID exists
    	List<String> ids = spDAO.getImageIDs();
    	if (!ids.contains(id)) {
    		// Return an empty sw package (set the items that were returned null)
    		SoftwarePackage swPack = createSoftwarePack(id);
    		swPack.setDescription("N/A");
    		swPack.setFormat("N/A");
    		return swPack;
    	}

        try {
           	SoftwarePackage swPack = createSoftwarePack(id);
            LOG.info("Returning software package " + id);
            return swPack;
        }
        catch (Exception ex) {
        	LOG.fatal("Error generating software package: " + ex);
            ex.printStackTrace();
            throw new RuntimeException("Error generating software package: " + ex);
        }
    }

    
    /**
     * Generate an software package list of all software images in the database
     * @param dummy Dummy parameter (unused) required by CXF
     * @return SoftwarePackageList List of software packages 
     */
    @Override
    public SoftwarePackageList getAllSoftwarePackagesInfo(String dummy) {
        LOG.info("Generating list of Software Packages...");
        try {

            SoftwarePackageList swPackList = new SoftwarePackageList();

            List<String> ids = spDAO.getImageIDs();
            LOG.info("Found software IDs: " + ids);

            for (String i : ids) {
            	SoftwarePackage swPack = createSoftwarePack(i);
            	LOG.debug("Adding software package ID " + i);
            	swPackList.getSoftwarePackage().add(swPack);
            }
            LOG.info("Returning list of Software Packages");
            return swPackList;
        }
        catch (Exception ex) {
        	LOG.fatal("Error generating software package list: " + ex);
            ex.printStackTrace();
            throw new RuntimeException("Error generating software package list: " + ex);
        }
    }

    /**
     * Return a list of Software Packages based on a pathway
     * @param pathway The pathway containing application, operating system and hardware information
     * @return SoftwarePackageList List of software packages that matches both OS and application
     */
    @Override
    public PathwayList getPathwaysByFileFormat(String fileFormat) {
    	LOG.info("Retrieving pathways for file format: " + fileFormat + "...");

    	// The following combinations are valid pathways:
    	// Format->Platform
    	// Format->OS->Platform
        // Format->App->OS->Platform
        List<String> columnNames = new ArrayList<String>();
        // The following columns will be retrieved, so add them to the List
        columnNames.add("FILEFORMAT_ID");
        columnNames.add("APP_ID");
        columnNames.add("OS_ID");
        columnNames.add("PLATFORM_ID");
        List<List<String>> pathways = spDAO.getPathwaysView(fileFormat, columnNames);
        LOG.debug("Pathways found: " + pathways);
        
        if (pathways.size() == 0)
        	return new PathwayList();

        PathwayList pwList = createPathwayList(pathways);

        LOG.info("Returning " + pwList.getPathway().size() + " pathway(s) for file format: " + fileFormat);
        return pwList;
    }

	@Override
	public PathwayList getAllPathways() {
    	LOG.info("Retrieving all available pathways...");

    	// The following combinations are valid pathways:
    	// Format->Platform
    	// Format->OS->Platform
        // Format->App->OS->Platform
        List<String> columnNames = new ArrayList<String>();
        // The following columns will be retrieved, so add them to the List
        columnNames.add("FILEFORMAT_ID");
        columnNames.add("APP_ID");
        columnNames.add("OS_ID");
        columnNames.add("PLATFORM_ID");
        List<List<String>> pathways = spDAO.getPathwaysView(null, columnNames);
        LOG.debug(pathways.size() + " Pathways found");
        
        if (pathways.size() == 0)
        	return new PathwayList();

        PathwayList pwList = createPathwayList(pathways);

        LOG.info("Returning " + pwList.getPathway().size() + " pathway(s)");
        return pwList;
	}

    /**
     * Convert the result of a query on the PathwaysView to a PathwayList object
     * @param pathways the result of a query on the PathwaysView
     * @return a PathwayList object based on the input pathways.
     */
	private PathwayList createPathwayList(List<List<String>> pathways) {

		// Loop over results
        PathwayList pwList = new PathwayList();
        for(List<String> pathway : pathways) {
        	Pathway pw = createPathway();
        	
        	// Note: the order of these assignments must match the columnNames order!
        	Iterator<String> it = pathway.iterator();
        	pw.setObjectFormat(createObjectFormatType(it.next()));

        	// Either of App/OS could be null 
        	String item = it.next();
        	if (item == null) {
        		pw.getApplication().setId("-1");
        		pw.getApplication().setName("N/A");
        	}
        	else
        		pw.setApplication(createAppType(item, false).get(0));
        	item = it.next();
        	if (item == null) {
        		pw.getOperatingSystem().setId("-1");
        		pw.getOperatingSystem().setName("N/A");
        	}
        	else
        		pw.setOperatingSystem(createOSType(item == null ? "-1" : item, false).get(0));
        	
        	pw.setHardwarePlatform(createHardwarePlatformType(it.next()));

            LOG.debug("Pathway added: [" + pathwayToString(pw) + "]");
            pwList.getPathway().add(pw);
        }
		return pwList;
	}
	

	/**
     * Return a list of Software Packages based on a pathway
     * @param pathway The pathway containing application, operating system and hardware information
     * @return SoftwarePackageList List of software packages that matches both OS and application
     */
    @Override
    public SoftwarePackageList getSoftwarePackagesByPathway(Pathway pathway) {
        LOG.info("Retrieving software packages that match pathway: [" + pathwayToString(pathway) + "]...");
        SoftwarePackageList spl = new SoftwarePackageList();
        List<String> ids = new ArrayList<String>();

    	// The following combinations are pathways for which images are available:
    	// Format->OS->Platform
        // Format->App->OS->Platform
        try {
            if(pathway.getApplication().getName().equalsIgnoreCase("N/A") && pathway.getOperatingSystem().getName().equalsIgnoreCase("N/A")) {
            	LOG.info("No app/OS defined so no packages needed");
            	return spl;
            }
            else if (pathway.getApplication().getName().equalsIgnoreCase("N/A"))
            {
            	LOG.info("No app defined so retrieving OS/Platform pathways");
            	ids = spDAO.getImageIDs(pathway.getOperatingSystem().getName());
            }
            else
            {
            	LOG.info("Full stack defined so retrieving App/OS/Platform pathways");
            	ids = spDAO.getImageIDs(pathway.getApplication().getName(), pathway.getOperatingSystem().getName());
            }

            for (String i : ids)
            {
            	LOG.debug("Adding package ID " + i + " to list");
            	spl.getSoftwarePackage().add(createSoftwarePack(i));
            }
            LOG.info("Returning software packages matching pathway " + pathwayToString(pathway) + ": " + ids);
            return spl;
        }
        catch (Exception ex) {
        	LOG.fatal("Error retrieving software packages that match pathway: " + pathwayToString(pathway) + ": " + ex);
            ex.printStackTrace();
            throw new RuntimeException("Error retrieving software packages that match pathway: " + pathwayToString(pathway) + ": " + ex);
        }
    }

    /**
     * Provide a DataHandler for a software binary in the database
     * @param Software ID
     * @return DataHandler for binary software file
     */
    @Override
    public DataHandler downloadSoftware(String id) {
        LOG.info("Getting DataHandler for software ID " + id + "...");
        
    	// Ensure ID exists
    	List<String> ids = spDAO.getImageIDs();
    	if (!ids.contains(id))
    	{
    		throw new RuntimeException("Invalid ID provided: " + id);
    	}

        try {
            InputStream is = spDAO.getImageFile(id);

            LOG.debug("Converting InputStream to DataHandler");
            DataSource ds = new InputStreamDataSource(is);
            DataHandler dh = new DataHandler(ds);

            LOG.info("Returning DataHandler for software ID " + id);
            return dh;
            
        } catch (Exception ex) {
        	LOG.fatal("Error getting DataHandler for binary: " + ex);
            ex.printStackTrace();
            throw new RuntimeException("Error getting DataHandler for binary: " + ex);
        }
    }

    /**
     * Generate a list of all software languages in the database
     * @param dummy Dummy parameter (unused) required by CXF
     * @return LanguageList List of languages
     */
    @Override
    public SwLanguageList getLanguageList(String dummy) {
    	LOG.info("Generating list of Languages...");
    	SwLanguageList languages = spDAO.getLanguages();
    	return languages;
    }

    /**
     * Get all registries from the database
     * @return RegistryList a list of the available registries
     */
	@Override
	public RegistryList getRegistries(int dummy) {
    	LOG.info("Retrieving all registries from database...");
    	
    	RegistryList registryList = new RegistryList();

    	try {
	    	registryList.getRegistries().addAll(spDAO.getRegistries());
        }
        catch (SQLException e) {
        	LOG.error("Cannot get registry values from database: " + ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("Error retrieving registries from database: " + e);
        }
    	
    	return registryList;
	}

    /**
     * Inserts registries in the database
     * @param registries The list of registries to insert into the database
     * @return True if successful, false otherwise
     */
	@Override
	public boolean setRegistries(RegistryList registries) {
    	LOG.info("Inserting registries in the database...");
    	
    	boolean success = false;
    	
    	try {
	    	spDAO.setRegistries(registries.getRegistries());
	    	success = true;
        }
        catch (SQLException e) {
        	// No logging needed here. Already done in spDAO.
            throw new RuntimeException("Error inserting registries in the database: " + e);
        }
    	
    	return success;
	}

    /**
     * Updates registries in the database. These registries must already exist in the database.
     * @param registries The list of registries to update
     * @return True if successful, false otherwise
     */
	@Override
	public boolean updateRegistries(RegistryList registries) {
    	LOG.info("Updating registries in the database...");
    	
    	boolean success = false;
    	
    	try {
	    	spDAO.updateRegistries(registries.getRegistries());
	    	success = true;
        }
        catch (SQLException e) {
        	// No logging needed here. Already done in spDAO.
            throw new RuntimeException("Error updating registries in the database: " + e);
        }
    	
    	return success;
	}

    /**
     * Retrieves the EF fileformat ID and fileformat name from the database given a PCR ID
     * @param id Unique PCR Identifier
     * @param view View in database containing translations
     * @return List of Strings (ID, name) of the corresponding EF fileformat ID and name
     */
	@Override
	public EFFormatData getFormatDataOnId(String pcrFormatId, String viewName) {
    	LOG.info("Retrieving EF fileformat ID and name from the database for PCR Format ID " + 
    			pcrFormatId + ", using view " + viewName);
    	
    	EFFormatData efFormatData = new EFFormatData();
    	
    	try {
    		Map<String, String> formatData = spDAO.getFormatDataOnID(pcrFormatId, viewName);
    		for (Map.Entry<String, String> formatDataEntry : formatData.entrySet()) {
    			EfFormat efFormat = new EfFormat();
    			efFormat.setId(formatDataEntry.getKey());
    			efFormat.setName(formatDataEntry.getValue());
    			efFormatData.getEfFormat().add(efFormat);
    		}
        }
        catch (SQLException e) {
        	LOG.error("Cannot retrieve EF fileformat ID and name from the database for PCR Format ID " + 
    			pcrFormatId + ", using view " + viewName + ": " + ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("Error retrieving EF fileformat ID and name from the database for PCR Format ID " + 
    			pcrFormatId + ", using view " + viewName + ": " + e);
        }
    	
    	return efFormatData;
	}

    /**
     * Create an empty Pathway object (@code PathwaySchema.xsd) with all
     * items initialised
     * @return Pathway empty pathway object
     */
    private Pathway createPathway()
    {
    	Pathway pw = new Pathway();
    	ObjectFormatType objF = new ObjectFormatType();
    	ApplicationType app = new ApplicationType();
    	OperatingSystemType opsys = new OperatingSystemType();
    	HardwarePlatformType hpf = new HardwarePlatformType();
    	
    	pw.setObjectFormat(objF);
    	pw.setApplication(app);
    	pw.setOperatingSystem(opsys);
    	pw.setHardwarePlatform(hpf);
    	
    	return pw;
    }

    /**
     * Custom toString method for Pathway objects
     * @return String representing pathway
     */
    private String pathwayToString(Pathway pw)
    {
    	String result = "";
    	String separator = "|";
    	result += "[" + pw.getObjectFormat().getName() + separator;
    	result += pw.getApplication().getName() + separator;
    	result += pw.getOperatingSystem().getName() + separator;
    	result += pw.getHardwarePlatform().getName() +"]";
    	
    	result += " (" + pw.getObjectFormat().getId() + separator;
    	result += pw.getApplication().getId() + separator;
    	result += pw.getOperatingSystem().getId() + separator;
    	result += pw.getHardwarePlatform().getId() +")";

    	return result;
    }    
    
    /**
     * Create an Software Package (@code SoftwarePackageSchema.xsd) for a software image in the database
     * @param id ID of the software image
     * @return Software Package for the requested software image
     */
    private SoftwarePackage createSoftwarePack(String id) {
    	LOG.trace("Creating Software Package for software ID: " + id);
    	
    	List<List<String>> results;
        List<String> columnNames = new ArrayList<String>();
        columnNames.add("IMAGE_DESCRIPTION");
        columnNames.add("IMAGE_FORMAT_NAME");
        results = spDAO.getViewData(id, true, "img", columnNames);
        
        SoftwarePackage softPack = new SoftwarePackage();
        softPack.setId(id);
        // Although this is a loop, it should always only contain 1 result (unique IMG ID)
        for (List<String> row : results)
        {
        	// Note: the order of these assignments must match the columnNames order!
        	Iterator<String> it = row.iterator();
		    softPack.setDescription(it.next());
		    softPack.setFormat(it.next());
        }

        // Add OSes and Apps
       	softPack.getOs().addAll(createOSType(id, true));
        softPack.getApp().addAll(createAppType(id, true));
        
        LOG.trace("Created Software Package for emulator ID: " + id + " (" + softPack.toString() + ")");
        return softPack;
    }
    
    /**
     * Return the information from the OS_PACKAGE view as an OperatingSystemType object
     * @param id Id of the image
     * @return List of complete OperatingSystemType objects
     */
    private List<OperatingSystemType> createOSType(String id, boolean byImgID) {
    	
    	List<OperatingSystemType> osList = new ArrayList<OperatingSystemType>();
    	List<String> columnNames = new ArrayList<String>();
    	List<List<String>> results;
        columnNames.add("OS_ID");
        columnNames.add("OS_NAME");
        columnNames.add("OS_VERSION");
        columnNames.add("OS_DESCRIPTION");
        columnNames.add("OS_CREATOR");
        columnNames.add("OS_RELEASE_DATE");
        columnNames.add("OS_LICENSE");
        columnNames.add("OS_LANGUAGE_ID");
        columnNames.add("OS_REFERENCE");
        results = spDAO.getViewData(id, byImgID, "os", columnNames);

        for (List<String> row : results)
        {
            OperatingSystemType os = new OperatingSystemType();
        	// Note: the order of these assignments must match the columnNames order!
        	Iterator<String> it = row.iterator();
       		os.setId(it.next());
	        os.setName(it.next());
	        
        	// Any of the following items can be null 
        	String item = it.next();
        	os.setVersion(item == null ? "N/A" : item);
        	item = it.next();
	        os.setDescription(item == null ? "N/A" : item);
        	item = it.next();
	        os.setCreator(item == null ? "N/A" : item);
        	item = it.next();
	        os.setReleaseDate(item == null ? "N/A" : item);
        	item = it.next();
	        os.setLicense(item == null ? "N/A" : item);
	        // The following item cannot be null
        	String languageId = it.next();
	        os.setLanguageId(languageId);
	        // The folllowing item can be null
        	item = it.next();
	        os.setReference(item == null ? "N/A" : item);
	        
            osList.add(os);
        }
        return osList;
    }
    
	/**
	 * Return the information from the APP_PACKAGE view as an ApplicationType object
	 * @param id Id of the image
	 * @return List of complete ApplicationType objects
	 */
	private List<ApplicationType> createAppType(String id, boolean byImgID) {

		List<ApplicationType> apps = new ArrayList<ApplicationType>();
		List<String> columnNames = new ArrayList<String>();
		List<List<String>> results;
		columnNames.add("APP_ID");
		columnNames.add("APP_NAME");
		columnNames.add("APP_VERSION");
		columnNames.add("APP_DESCRIPTION");
		columnNames.add("APP_CREATOR");
		columnNames.add("APP_RELEASE_DATE");
		columnNames.add("APP_LICENSE");
		columnNames.add("APP_LANGUAGE_ID");
		columnNames.add("APP_REFERENCE");
		columnNames.add("APP_USER_INSTRUCTIONS");
		results = spDAO.getViewData(id, byImgID, "app", columnNames);
		for (List<String> row : results) {
			ApplicationType app = new ApplicationType();
			// Note: the order of these assignments must match the columnNames
			// order!
			Iterator<String> it = row.iterator();
			app.setId(it.next());
			app.setName(it.next());

			// Any of the following items can be null
			String item = it.next();
			app.setVersion(item == null ? "N/A" : item);
			item = it.next();
			app.setDescription(item == null ? "N/A" : item);
			item = it.next();
			app.setCreator(item == null ? "N/A" : item);
			item = it.next();
			app.setReleaseDate(item == null ? "N/A" : item);
			item = it.next();
			app.setLicense(item == null ? "N/A" : item);
	        // The following item cannot be null
        	String languageId = it.next();
        	app.setLanguageId(languageId);
	        // The folllowing items can be null
			item = it.next();
			app.setReference(item == null ? "N/A" : item);
			item = it.next();
			app.setUserInstructions(item == null ? "N/A" : item);
			apps.add(app);
		}
		return apps;
	}

	/**
	 * Return the information from the FILEFORMAT table as an ObjectFormatType object
	 * @param id Id of the object format
	 * @return Complete ObjectFormatType object
	 */
	private ObjectFormatType createObjectFormatType(String id) {

		ObjectFormatType obj = new ObjectFormatType();
		List<String> results = new ArrayList<String>();
		results = spDAO.getFileFormatInfo(id);
		Iterator<String> it = results.iterator();
		obj.setId(it.next());
		obj.setName(it.next());

		// Any of the following items can be null
		String item = it.next();
		obj.setVersion(item == null ? "N/A" : item);
		item = it.next();
		obj.setDescription(item == null ? "N/A" : item);
		item = it.next();
		obj.setReference(item == null ? "N/A" : item);

		return obj;
	}
	
	/**
	 * Return the information from the FILEFORMAT table as an ObjectFormatType object
	 * @param id Id of the object format
	 * @return Complete ObjectFormatType object
	 */
	private HardwarePlatformType createHardwarePlatformType(String id) {

		HardwarePlatformType hwp = new HardwarePlatformType();
		List<String> results = new ArrayList<String>();
		results = spDAO.getHardwarePlatformInfo(id);
		Iterator<String> it = results.iterator();
		hwp.setId(it.next());
		hwp.setName(it.next());

		// Any of the following items can be null
		String item = it.next();
		hwp.setDescription(item == null ? "N/A" : item);
		item = it.next();
		hwp.setCreator(item == null ? "N/A" : item);
		item = it.next();
		hwp.setProductionStart(item == null ? "N/A" : item);
		item = it.next();
		hwp.setProductionEnd(item == null ? "N/A" : item);
		item = it.next();
		hwp.setReference(item == null ? "N/A" : item);

		return hwp;
	}
	
    private class InputStreamDataSource implements DataSource {
        private InputStream is;
        public InputStreamDataSource(InputStream is) { this.is = is;}

        @Override
        public OutputStream getOutputStream() throws IOException {return null;}

        @Override
        public String getName() {return null;}

        @Override
        public InputStream getInputStream() throws IOException {return is;}

        @Override
        public String getContentType() {return null;}
    }

}
