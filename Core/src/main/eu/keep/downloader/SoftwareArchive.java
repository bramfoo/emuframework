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

import eu.keep.characteriser.Format;
import eu.keep.downloader.db.DBRegistry;
import eu.keep.softwarearchive.SwLanguageList;
import eu.keep.softwarearchive.pathway.EfFormat;
import eu.keep.softwarearchive.pathway.ObjectFormatType;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.List;

import javax.xml.ws.WebServiceException;

/**
 * Interface to the Software Archive, the webservice providing software package metadata and binary files
 * @author Bram Lohman
 */
public interface SoftwareArchive {

	/**
	 * Test whether the SoftwareArchive is up and running and reachable
	 * @return true if the SoftwareArchive could be contacted and gave a positive response
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
	 */
	public abstract boolean ping()
            throws ConnectException, SocketTimeoutException, WebServiceException;	
	
	/**
     * Returns a list of pathways based on a file format
     * @param fileFormat String representing the file format of a digital object
     * @return List of pathways
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public abstract List<Pathway> getPathwayByFileFormat(String fileFormat)
            throws ConnectException, SocketTimeoutException, WebServiceException;

    /**
     * Returns a list of all available file formats
     * @return List of file formats
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
	public abstract List<ObjectFormatType> getAllFileFormats()
			throws ConnectException, SocketTimeoutException, WebServiceException;

	
    /**
     * Returns a list of all available pathways
     * @return List of pathways
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
	public abstract List<Pathway> getAllPathways()
			throws ConnectException, SocketTimeoutException, WebServiceException;

	
	/**
     * Returns a list of software packages based on a given pathway
     * @param pw Pathway object describing the environment configuration
     * @return List of software packages
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public abstract List<SoftwarePackage> getSoftwarePackageByPathway(Pathway pw)
            throws ConnectException, SocketTimeoutException, WebServiceException;

    /**
     * Returns the software package file associated with the given ID
     * @param softpackID software package ID
     * @return InputStream A handle to the image file
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public abstract InputStream downloadSoftware(String softpackID) throws IOException,
            SocketTimeoutException, WebServiceException;

    /**
     * Returns the software package file format
     * @param softpackID software package ID
     * @return String representing the format name
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public abstract String getSoftwareFormat(String softpackID) throws ConnectException,
            SocketTimeoutException, WebServiceException;


    /**
     * Returns the list of all software packages in the archive database
     * @return List<SoftwarePackage> List of software packages
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)
     * @throws SocketTimeoutException when a response takes longer than the allocated time
     * @throws WebServiceException for any other webservice error
     */
    public abstract List<SoftwarePackage> getSoftwarePackageList() throws ConnectException,
            SocketTimeoutException, WebServiceException;

    /**
     * Returns the software package metadata from the archive database associated with the ID
     * @param softpackID software package ID
     * @return Software package Software package metadata for the given ID
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)
     * @throws SocketTimeoutException when a response takes longer than the allocated time
     * @throws WebServiceException for any other webservice error
     */
    public abstract SoftwarePackage getSoftwarePackage(String softpackID) throws ConnectException,
            SocketTimeoutException, WebServiceException;

    /**
     * Get the list of software languages
     * @return List of all languages used by software
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public abstract SwLanguageList getSoftwareLanguages() throws ConnectException, SocketTimeoutException, WebServiceException;
    
    /**
     * Get all registries from the database
     * @return List<DBRegistry> a list of registries
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public List<DBRegistry> getRegistries() throws ConnectException, SocketTimeoutException, WebServiceException;

    /**
     * Updates registries in the database. These registries must already exist in the database.
     * @param registries The list of registries to update
     * @return True if successful, false otherwise
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public boolean updateRegistries(List<DBRegistry> registries) throws ConnectException, SocketTimeoutException, WebServiceException;

    /**
     * Inserts registries in the database
     * @param registries The list of registries to insert into the database
     * @return True if successful, false otherwise
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public boolean setRegistries(List<DBRegistry> registries) throws ConnectException, SocketTimeoutException, WebServiceException;

    /**
     * Retrieves the EF fileformat ID and fileformat name from the database given a PCR ID
     * @param id Unique Identifier
     * @param view View in database containing translations
     * @return List of EfFormat objects, containing the corresponding EF fileformat IDs and names
     * @throws SQLException
     */
	public List<EfFormat> getFormatDataOnID(String id, String view) throws ConnectException, SocketTimeoutException, WebServiceException;

}
