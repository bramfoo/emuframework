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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
	private static final String EMUWHITELIST_TABLE_NAME   = "EMULATOR_WHITELIST";
	
    private static final String NEW_EMUWHITELIST_ENTRY    = "INSERT INTO " + EMUWHITELIST_TABLE_NAME + 
    													    " (emulator_id, emulator_descr) " + 
    													    " VALUES (?, ?)";
    private static final String GET_EMUWHITELIST_ENTRIES  = "SELECT * FROM " + EMUWHITELIST_TABLE_NAME;
    private static final String DELETE_EMUWHITELIST_ENTRY = "DELETE FROM " + EMUWHITELIST_TABLE_NAME + 
    														" WHERE emulator_id = ?";

    
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
            if (!resultSet.first()) {
            	logger.debug("No emulator IDs found in database");
            	return emuList;
            }
            else {
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
	
}