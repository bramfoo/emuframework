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

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import eu.keep.softwarearchive.pathway.SwLanguageList;

/**
 * H2 database implementation of the EmuPackageDAO interface.
 * @note see H2 website: http://www.h2database.com
 * @author Bram Lohman
 * @author David Michel
 */
public class H2SoftwarePackageDAO implements SoftwarePackageDAO {

    private static final Logger LOGGER                          = Logger.getLogger(H2SoftwarePackageDAO.class.getName());
    private Connection          conn;

    // Main tables
    private static final String FILEFORMAT_NAME               = "fileformats";
    private static final String PLATFORM_NAME                 = "platforms";
    private static final String IMAGE_TABLE_NAME              = "images";
    private static final String BLOB_TABLE_NAME               = "imageblobs";
    private static final String IMG_PACK_VIEW                 = "image_package";
    private static final String OS_PACK_VIEW                  = "os_package";
    private static final String APP_PACK_VIEW                 = "app_package";
    private static final String PATHWAY_VIEW                  = "pathways";
    private static final String LANGUAGE_TABLE_NAME           = "languages";

    // Simple selection
    private static final String SELECT_ALL_FILEFORMATS_ON_FF   = "SELECT * FROM " + FILEFORMAT_NAME + " WHERE fileformat_id=?";
    private static final String SELECT_ALL_PLATFORMS_ON_PF     = "SELECT * FROM " + PLATFORM_NAME + " WHERE platform_id=?";
    private static final String SELECT_APP_PACK_VIEW_ON_IMG    = "SELECT * FROM " + APP_PACK_VIEW + " WHERE image_id=?";
    private static final String SELECT_APP_PACK_VIEW_ON_APP    = "SELECT * FROM " + APP_PACK_VIEW + " WHERE app_id=?";
    private static final String SELECT_OS_PACK_VIEW_ON_IMG     = "SELECT * FROM " + OS_PACK_VIEW + " WHERE image_id=?";
    private static final String SELECT_OS_PACK_VIEW_ON_OS      = "SELECT * FROM " + OS_PACK_VIEW + " WHERE os_id=?";
    private static final String SELECT_IMG_PACK_VIEW_ON_IMG    = "SELECT * FROM " + IMG_PACK_VIEW + " WHERE image_id=?";
    private static final String SELECT_PATHWAY_VIEW_ON_FF      = "SELECT * FROM " + PATHWAY_VIEW + " WHERE fileformat_name=?";
    private static final String SELECT_OS_VIEW_ON_OS           = "SELECT * FROM " + OS_PACK_VIEW + " WHERE os_name=?";

    // Joins
    private static final String SELECT_IMG_ON_APP_OS           = "SELECT " + APP_PACK_VIEW + ".IMAGE_ID FROM " + OS_PACK_VIEW + 
    																" INNER JOIN " + APP_PACK_VIEW + 
    																" ON " + OS_PACK_VIEW + ".image_id = " + APP_PACK_VIEW + ".image_id " +
    																"WHERE app_name=? AND os_name=?";
    
    private static final String SELECT_IMAGE_IDS                = "SELECT image_id FROM " + IMAGE_TABLE_NAME;

    private static final String SELECT_IMAGEBLOB_WHERE          = "SELECT image FROM " + BLOB_TABLE_NAME + " WHERE image_id=?";

    private static final String SELECT_LANGUAGES                = "SELECT * FROM " + LANGUAGE_TABLE_NAME;


    /**
     * Constructor
     * @param conn a connection to an H2 database
     */
    public H2SoftwarePackageDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getImageFile(String imageID) {

        InputStream is = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Get emulator package from database
            // logger.info("Retrieving software image file ID=" + imageID);
            pstmt = conn.prepareStatement(SELECT_IMAGEBLOB_WHERE);
            pstmt.setString(1, imageID);
            rs = pstmt.executeQuery();

            if (rs.first()) {
                // record has been found
                Blob blob = rs.getBlob("image");
                is = blob.getBinaryStream();
            }
        }
        catch (SQLException e) {
            LOGGER.info("Database: Error while retrieving binary " + imageID + ": " + e);
            e.printStackTrace();
            return is;
        }

        return is;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<List<String>> getViewData(String imageID, boolean primaryCol, String viewName, List<String> columnNames) {

        String query;
        if (viewName.equalsIgnoreCase("img"))
        {
        	// This view doesn't have a secondary column for selection
       		query = SELECT_IMG_PACK_VIEW_ON_IMG;
        }
        else if (viewName.equalsIgnoreCase("os"))
        {
        	if (primaryCol)
        		query = SELECT_OS_PACK_VIEW_ON_IMG;
        	else
        		query = SELECT_OS_PACK_VIEW_ON_OS;
        }
        else if (viewName.equalsIgnoreCase("app"))
        {
        	if (primaryCol)
            	query = SELECT_APP_PACK_VIEW_ON_IMG;
        	else
        		query = SELECT_APP_PACK_VIEW_ON_APP;
        }
        else
        	throw new RuntimeException("Unsupported view name specified: " + viewName);

        LOGGER.debug("Querying view: " + viewName + " for columns " + columnNames + " on ID: " + imageID);
        List<List<String>> viewInfo = new ArrayList<List<String>>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            try {
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, imageID);
                LOGGER.trace("Using query: " + pstmt.toString());
                
                rs = pstmt.executeQuery();

                while (rs.next()) {
                	List<String> row = new ArrayList<String>();
	                for (String name : columnNames)
	                {
	                	row.add(rs.getString(name));
	                }
	                LOGGER.debug("Added row: " + row);
	                viewInfo.add(row);
                }
            }
            catch (SQLException e) {
                LOGGER.info("Database: Error while retrieving data in '" + viewName + "' for image ID=" + imageID
                        + ": " + e);
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            finally {
                rs.close();
                pstmt.close();
            }
        }
        catch (SQLException e) {
            LOGGER.info("Database: Error while retrieving data in '" + viewName + "' for image ID=" + imageID
                    + ": " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return viewInfo;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<List<String>> getPathwaysView(String fileFormat, List<String> columnNames) {

        // sanity check
        if (fileFormat == null || fileFormat.length() == 0) {
            throw new IllegalArgumentException("Invalid file format");
        }

        LOGGER.debug("Retrieving pathways for file format '" + fileFormat + "'");
        List<List<String>> pathways = new ArrayList<List<String>>();
        try {
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
	            pstmt = conn.prepareStatement(SELECT_PATHWAY_VIEW_ON_FF);
	            pstmt.setString(1, fileFormat);
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	            	List<String> pathway = new ArrayList<String>();
	                for (String name : columnNames)
	                {
	                	pathway.add(rs.getString(name));
	                }
	                pathways.add(pathway);
	                LOGGER.debug("Added pathway for '" + fileFormat + "': " + pathway);
	            }
            }
            finally {
                rs.close();
                pstmt.close();
            }
        }
        catch (SQLException e) {
            LOGGER.info("Database: Error while retrieving row in 'pathways' for file format=" + fileFormat + ": " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        LOGGER.debug("Found " + pathways.size() + " pathways for file format '" + fileFormat + "'");
        return pathways;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getImageIDs() {

        ArrayList<String> ids = new ArrayList<String>();
        try {
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(SELECT_IMAGE_IDS);
                while (rs.next()) {
                    ids.add(rs.getString("image_id"));
                }
            }
            finally {
                rs.close();
                stmt.close();
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ids;
    }    

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getImageIDs(String osName) {
        List<String> imageID = new ArrayList<String>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(SELECT_OS_VIEW_ON_OS);
            pstmt.setString(1, osName);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                // record has been found
                String res = rs.getString("image_id");
                imageID.add(res);
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException e) {
            LOGGER.info("Database: Error while retrieving list of image IDs" + imageID + ": " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return imageID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getImageIDs(String appName, String osName) {
        List<String> imageID = new ArrayList<String>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        LOGGER.debug("Querying images for [" + appName +"|" + osName + "]");
        try {
            pstmt = conn.prepareStatement(SELECT_IMG_ON_APP_OS);
            pstmt.setString(1, appName);
            pstmt.setString(2, osName);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                // record has been found
                String res = rs.getString("image_id");
                imageID.add(res);
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException e) {
            LOGGER.info("Database: Error while retrieving list of image IDs" + imageID + ": " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        LOGGER.debug("Found images for [" + appName +"|" + osName + "]: " + imageID);
        return imageID;
    }

	@Override
	public List<String> getFileFormatInfo(String fileFormatID) {
        List<String> fileFormatInfo = new ArrayList<String>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        LOGGER.debug("Quering file format table for " + fileFormatID);
        try {
            pstmt = conn.prepareStatement(SELECT_ALL_FILEFORMATS_ON_FF);
            pstmt.setString(1, fileFormatID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                // record has been found
                fileFormatInfo.add(rs.getString(1));
                fileFormatInfo.add(rs.getString(2));
                fileFormatInfo.add(rs.getString(3));
                fileFormatInfo.add(rs.getString(4));
                fileFormatInfo.add(rs.getString(5));                
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException e) {
            LOGGER.info("Database: Error while retrieving data from file format table" + fileFormatInfo + ": " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        LOGGER.debug("Found information on fileformat [" + fileFormatID + "]: " + fileFormatInfo);
        return fileFormatInfo;
	}

	@Override
	public List<String> getHardwarePlatformInfo(String hardwarePlatformID) {
        List<String> platformInfo = new ArrayList<String>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        LOGGER.debug("Quering platform table for " + hardwarePlatformID);
        try {
            pstmt = conn.prepareStatement(SELECT_ALL_PLATFORMS_ON_PF);
            pstmt.setString(1, hardwarePlatformID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                // record has been found
                platformInfo.add(rs.getString(1));
                platformInfo.add(rs.getString(2));
                platformInfo.add(rs.getString(3));
                platformInfo.add(rs.getString(4));
                platformInfo.add(rs.getString(5));
                platformInfo.add(rs.getString(6));
                platformInfo.add(rs.getString(7));
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException e) {
            LOGGER.info("Database: Error while retrieving data from platform table" + hardwarePlatformID + ": " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        LOGGER.debug("Found information on platform [" + hardwarePlatformID + "]: " + platformInfo);
        return platformInfo;
	}

	@Override
	public SwLanguageList getLanguages() {
        LOGGER.debug("Querying available languages");

        SwLanguageList languages = new SwLanguageList();

		Statement stmt = null;
		ResultSet rs = null;
		try {
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(SELECT_LANGUAGES);
				while (rs.next()) {
					String languageId = rs.getString("language_id");
			        LOGGER.debug("Found language ID: " + languageId);
					languages.getLanguageIds().add(languageId);
				}
			}
			finally {
				rs.close();
				stmt.close();
			}
		}
		catch (SQLException e) {
            LOGGER.error("Database: Error while retrieving data from languages table: " + e);
            e.printStackTrace();
			throw new RuntimeException(e);
		}
		return languages;
	}    

}
