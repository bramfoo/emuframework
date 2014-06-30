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

import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Common database utilities, to be shared among classes
 * @author Bram Lohman
 *
 */
public class DBUtil {

    private static final Logger logger = Logger.getLogger(DBUtil.class.getName());

    /**
     * Establish a connection to a database
     * @param driver Class used to establish a connection to the database
     * @param dbUrl URL of the database
     * @param dbUser Username
     * @param dbPasswd Password
     * @param nAttempt Number of attempts to try making a connection before giving up
     * @return Connection A connection to the database with given username and password
     * @throws IOException If an database connection error occurs
     */
    public static Connection establishConnection(String driver, String dbUrl, String dbUser,
            String dbPasswd, int nAttempt) throws IOException {

        Connection conn = null;
        String error = "";
        int delay = 3;

        // Set up the db connection
        // Register the JDBC driver
        try {
            Class.forName(driver);
        }
        catch (ClassNotFoundException e) {
            logger.warn("Database driver not found: " + driver);
            error = e.toString();
            throw new IOException("Database driver not found: " + error);
        }

        int iAttempt = 1;
        while (iAttempt <= nAttempt) {

            try {
                conn = DriverManager.getConnection(dbUrl, dbUser, dbPasswd);
                break;
            }
            catch (SQLException e) {
                error = e.toString();
                logger.warn("Failed to connect to the database (attempt " + iAttempt + "), retrying in " + delay + " seconds...");
            }

            iAttempt++;
            try {
                Thread.sleep(delay*1000);
            }
            catch (InterruptedException e) {
                logger.warn("Thread.sleep() interruption occurred while waiting for database connection...");
            }

        }

        if (iAttempt >= nAttempt) {
            logger.error("Multiple connection attempts to database failed: " + error);
            throw new IOException("Multiple connection attempts to database failed: " + error);
        }

        return conn;
    }
}