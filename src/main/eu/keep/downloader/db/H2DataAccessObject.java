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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.keep.downloader.DataAccessObject;

/**
 * H2 (http://www.h2database.com) implementation of the DAO interface.
 * @author Bram Lohman
 */
public class H2DataAccessObject implements DataAccessObject {

    private Logger              logger                = Logger.getLogger(this.getClass());
    private Connection          conn;

    // Registry table
    private static final String REGISTRY_TABLE_NAME   = "REGISTRIES";
	private static final String EMUWHITELIST_TABLE_NAME = "EMULATOR_WHITELIST";
	private static final String EF_PCR_FF_VIEW = "EF_PCR_FORMATS";
	

    private static final String NEW_REGISTRY_ENTRY    = "INSERT INTO "
                                                              + REGISTRY_TABLE_NAME
                                                              + " (registry_id, name, url, class_name, translation_view, enabled, description, comment) "
                                                              + " VALUES (?,?,?,?,?,?,?,?)";
    private static final String GET_REGISTRY_ENTRIES  = "SELECT * FROM " + REGISTRY_TABLE_NAME;
    private static final String UPDATE_REGISTRY_ENTRY    = "UPDATE "
        + REGISTRY_TABLE_NAME
        + " SET name = ?, url = ?, class_name = ?, translation_view = ?, enabled = ?, description = ?, comment = ?"
        + " WHERE registry_id = ?";
    private static final String DELETE_REGISTRY_ENTRIES  = "DELETE FROM " + REGISTRY_TABLE_NAME;

    private static final String NEW_EMUWHITELIST_ENTRY    = "INSERT INTO "
        + EMUWHITELIST_TABLE_NAME
        + " (emulator_id, emulator_descr) "
        + " VALUES (?, ?)";
    private static final String GET_EMUWHITELIST_ENTRIES  = "SELECT * FROM " + EMUWHITELIST_TABLE_NAME;
    private static final String DELETE_EMUWHITELIST_ENTRY  = "DELETE FROM "
    	+ EMUWHITELIST_TABLE_NAME
    	+ " WHERE emulator_id = ?";

    private static final String GET_FF_ON_PCRID  = "SELECT ef_format_id, ef_format_name " + EF_PCR_FF_VIEW
    	+ "	WHERE pcr_format_id=?";
    
    /**
     * Constructor
     * @param conn a connection to an H2 database
     */
    public H2DataAccessObject(Connection conn) {
        this.conn = conn;
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public List<DBRegistry> getRegistries() throws SQLException {
        List<DBRegistry> regList = new ArrayList<DBRegistry>();

        // Retrieve the list of registries from the database
        PreparedStatement prepSt;
        ResultSet resultSet;

        try {
            prepSt = conn.prepareStatement(GET_REGISTRY_ENTRIES);
            resultSet = prepSt.executeQuery();

            // Create list of results
            resultSet.first();
            do {
                DBRegistry reg = new DBRegistry();
                reg.setRegistryID(resultSet.getInt(1));
                reg.setName(resultSet.getString(2));
                reg.setUrl(resultSet.getString(3));
                reg.setClassName(resultSet.getString(4));
                reg.setTranslationView(resultSet.getString(5));
                reg.setEnabled(resultSet.getBoolean(6));
                reg.setDescription(resultSet.getString(7));
                reg.setComment(resultSet.getString(8));
                regList.add(reg);
            }
            while (resultSet.next());
        }
        catch (SQLException e) {
            logger.error("Cannot get registry values from database: " + e.toString());
            throw e;
        }

        // Return the list
        return regList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setRegistries(List<DBRegistry> regList) throws SQLException {
        boolean result = true;

        // Loop over each registry in the catalog, inserting it into the
        // database
        PreparedStatement pSt;

        try {
            conn.setAutoCommit(false);
            
            // Delete the existing registries
            pSt = conn.prepareStatement(DELETE_REGISTRY_ENTRIES);
            pSt.execute();
                        
            for (DBRegistry reg : regList) {
                logger.debug("Attempting to insert registry: " + reg + " into database");
                pSt = conn.prepareStatement(NEW_REGISTRY_ENTRY);
                pSt.setLong(1, reg.getRegistryID());
                pSt.setString(2, reg.getName());
                pSt.setString(3, reg.getUrl());
                pSt.setString(4, reg.getClassName());
                pSt.setString(5, reg.getTranslationView());
                pSt.setBoolean(6, reg.isEnabled());
                pSt.setString(7, reg.getDescription());
                pSt.setString(8, reg.getComment());
                pSt.execute();
                result &= pSt.getUpdateCount() > 0 ? true : false;
            }
            conn.commit();
            logger.debug("Registries committed");
        }
        catch (SQLException e) {
            logger.warn("Cannot update registries: " + e.toString());
            try {
                conn.rollback();
            }
            catch (SQLException e2)
            {
                logger.fatal("Rollback failed: " + e2.toString());
                throw e2;
            }
            logger.error("Cannot get registry values from database: " + e.toString());
            throw e;
        }
        finally {
            try {
                conn.setAutoCommit(false);
            }
            catch (SQLException e)
            {
                logger.fatal("Cannot set autocommit: " + e.toString());
                throw e;
            }
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
	@Override
    public boolean updateRegistries(List<DBRegistry> regList) throws SQLException {
        boolean result = true;

        // Loop over each registry in the catalog, updating it in the
        // database
        PreparedStatement pSt;

        try {
            conn.setAutoCommit(false);
            for (DBRegistry reg : regList) {
                logger.debug("Attempting to update registry: " + reg + " into database");
                pSt = conn.prepareStatement(UPDATE_REGISTRY_ENTRY);
                pSt.setString(1, reg.getName());
                pSt.setString(2, reg.getUrl());
                pSt.setString(3, reg.getClassName());
                pSt.setString(4, reg.getTranslationView());
                pSt.setBoolean(5, reg.isEnabled());
                pSt.setString(6, reg.getDescription());
                pSt.setString(7, reg.getComment());
                pSt.setLong(8, reg.getRegistryID());
                pSt.execute();
                result &= pSt.getUpdateCount() > 0 ? true : false;
            }
            conn.commit();
            logger.debug("Registries committed");
        }
        catch (SQLException e) {
            logger.warn("Cannot update registries: " + e.toString());
            try {
                conn.rollback();
            }
            catch (SQLException e2)
            {
                logger.fatal("Rollback failed: " + e2.toString());
                throw e2;

            }
            logger.error("Cannot get registry values from database: " + e.toString());
            throw e;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean whiteListEmulator(Integer i, String descr) throws SQLException {
        boolean result = true;
        PreparedStatement pSt;

        try {
            conn.setAutoCommit(false);
            logger.debug("Attempting to insert emulator ID: " + i + " into database");
            pSt = conn.prepareStatement(NEW_EMUWHITELIST_ENTRY);
            pSt.setLong(1, i);
            pSt.setString(2, descr);
            pSt.execute();
            result &= pSt.getUpdateCount() > 0 ? true : false;
            conn.commit();
            logger.debug("Emulator committed");
        }
        catch (SQLException e) {
            logger.warn("Cannot insert emulator: " + e.toString());
            try {
                conn.rollback();
            }
            catch (SQLException e2)
            {
                logger.fatal("Rollback failed: " + e2.toString());
                throw e2;
            }
            logger.error("Cannot insert emulator: " + e.toString());
            result = false;
        }
        finally {
            try {
                conn.setAutoCommit(false);
            }
            catch (SQLException e)
            {
                logger.fatal("Cannot set autocommit: " + e.toString());
                throw e;
            }
        }
        return result;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean unListEmulator(Integer i) throws SQLException {
        boolean result = true;
        PreparedStatement pSt;

        try {
            conn.setAutoCommit(false);
            logger.debug("Attempting to remove emulator ID: " + i + " from database");
            pSt = conn.prepareStatement(DELETE_EMUWHITELIST_ENTRY);
            pSt.setLong(1, i);
            pSt.execute();
            result &= pSt.getUpdateCount() > 0 ? true : false;
            conn.commit();
            logger.debug("Emulator removed");
        }
        catch (SQLException e) {
            logger.warn("Cannot remove emulator: " + e.toString());
            try {
                conn.rollback();
            }
            catch (SQLException e2)
            {
                logger.fatal("Rollback failed: " + e2.toString());
                throw e2;
            }
            logger.error("Cannot remove emulator: " + e.toString());
            throw e;
        }
        finally {
            try {
                conn.setAutoCommit(false);
            }
            catch (SQLException e)
            {
                logger.fatal("Cannot set autocommit: " + e.toString());
                throw e;
            }
        }
        return result;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Map<Integer, String> getWhitelistedEmus() throws SQLException {
        Map<Integer, String> emuList = new HashMap<Integer, String>();

        // Retrieve the list of emulator IDs from the database
        PreparedStatement prepSt;
        ResultSet resultSet;

        try {
            prepSt = conn.prepareStatement(GET_EMUWHITELIST_ENTRIES);
            resultSet = prepSt.executeQuery();

            // Create list of results
            if (!resultSet.first())
            	{
            	logger.debug("No emulator IDs found in database");
            	return emuList;
            	}
            else
            {
	            do {
	            	emuList.put(resultSet.getInt(1), resultSet.getString(2));
	            }
	            while (resultSet.next());
            }
        }
        catch (SQLException e) {
            logger.error("Cannot get emulator ID values from database: " + e.toString());
            throw e;
        }

        // Return the list
        return emuList;
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public List<String> getFormatDataOnID(String id, String view) throws SQLException {
        List<String> data = new ArrayList<String>();

        // Retrieve the ID, name from the database
        PreparedStatement prepSt;
        ResultSet resultSet;

        try {
        	if (view.equalsIgnoreCase("EF_PCR_FORMATS"))
        		prepSt = conn.prepareStatement(GET_FF_ON_PCRID); // Only supported view
        	else
        		return data;
            prepSt.setString(1, id);
            resultSet = prepSt.executeQuery();

            // Create list of results
            if (!resultSet.first())
            	{
            	logger.debug("PCR ID not found in database");
            	return data;
            	}
            else
            {
	            do {
	            	data.add(resultSet.getString("EF_FORMAT_ID"));
	            	data.add(resultSet.getString("EF_FORMAT_NAME"));
	            }
	            while (resultSet.next());
            }
        }
        catch (SQLException e) {
            logger.error("Cannot get EF values from database: " + e.toString());
            throw e;
        }

        // Return the list
        return data;
	}
}