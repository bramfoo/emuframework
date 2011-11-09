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
* Project Title: Emulator Archive (EA)$
*/

package eu.keep.emulatorarchive;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

import eu.keep.emulatorarchive.EmulatorPackageDAO;

/**
 * H2 database implementation of the EmuPackageDAO interface.
 * @note see H2 website: http://www.h2database.com
 * @author David Michel
 */
public class H2EmulatorPackageDAO implements EmulatorPackageDAO {

     private static final Logger logger = Logger.getLogger(H2EmulatorPackageDAO.class.getName());

    private Connection          conn;

    // Emulator table
    private static final String EMULATOR_TABLE_NAME                     = "emulators";

    private static final String SELECT_EMULATOR_PACKAGE_WHERE           = "SELECT package_name, package FROM "
                                                                               + EMULATOR_TABLE_NAME
                                                                               + " WHERE emulator_id=?";

    private static final String SELECT_EMULATOR_USER_INSTRUCTIONS_WHERE = "SELECT user_instructions FROM "
    																			+ EMULATOR_TABLE_NAME
    																			+ " WHERE emulator_id=?";

    private static final String SELECT_EMULATOR_IDS                     = "SELECT emulator_id FROM "
                                                                               + EMULATOR_TABLE_NAME;

    private static final String SELECT_EMULATOR_EXEC_TYPE_WHERE         = "SELECT exec_type FROM "
                                                                               + EMULATOR_TABLE_NAME
                                                                               + " WHERE emulator_id=?";

    private static final String SELECT_EMULATOR_PACKAGE_TYPE_WHERE      = "SELECT package_type FROM "
                                                                               + EMULATOR_TABLE_NAME
                                                                               + " WHERE emulator_id=?";

    private static final String SELECT_EMULATOR_PACKAGE_FILENAME_WHERE  = "SELECT package_name FROM "
                                                                               + EMULATOR_TABLE_NAME
                                                                               + " WHERE emulator_id=?";

    private static final String SELECT_EMULATOR_PACKAGE_VERSION_WHERE   = "SELECT package_version FROM "
                                                                               + EMULATOR_TABLE_NAME
                                                                               + " WHERE emulator_id=?";

    private static final String SELECT_EMULATOR_NAME_WHERE              = "SELECT name FROM "
                                                                               + EMULATOR_TABLE_NAME
                                                                               + " WHERE emulator_id=?";

    private static final String SELECT_EMULATOR_VERSION_WHERE           = "SELECT version FROM "
                                                                               + EMULATOR_TABLE_NAME
                                                                               + " WHERE emulator_id=?";

    private static final String SELECT_EMULATOR_DESCRIPTION_WHERE       = "SELECT description FROM "
                                                                               + EMULATOR_TABLE_NAME
                                                                               + " WHERE emulator_id=?";

    private static final String SELECT_EMULATOR_EXEC_NAME_WHERE         = "SELECT exec_name FROM "
                                                                               + EMULATOR_TABLE_NAME
                                                                               + " WHERE emulator_id=?";

    private static final String SELECT_EMULATOR_EXEC_DIR_WHERE          = "SELECT exec_dir FROM "
                                                                               + EMULATOR_TABLE_NAME
                                                                               + " WHERE emulator_id=?";

    private static final String COUNT_EMULATOR                          = "SELECT COUNT(*) FROM "
                                                                               + EMULATOR_TABLE_NAME;

    // Hardware table
    private static final String HARDWARE_TABLE_NAME                     = "hardware";

    private static final String SELECT_HARDWARE_IDS                     = "SELECT hardware_id FROM " + HARDWARE_TABLE_NAME;

    private static final String SELECT_HARDWARE_NAMES                   = "SELECT name FROM " + HARDWARE_TABLE_NAME;

    // Image format table
    private static final String IMAGEFORMAT_TABLE_NAME                  = "imageformats";

    // Languages table
    private static final String LANGUAGE_TABLE_NAME           			= "languages";
    private static final String SELECT_LANGUAGES                		= "SELECT * FROM " + LANGUAGE_TABLE_NAME;

    // Emulator-Hardware Junction Table
    private static final String EMULATOR_HARDWARE_JCT_TABLE_NAME        = "emus_hardware";

    // Emulator-imageFormat Junction Table
    private static final String EMULATOR_IMAGEFORMAT_JCT_TABLE_NAME     = "emus_imageformats";

    // Joins
    private static final String EMUID_FROM_HWNAME                       = "SELECT emulator_id FROM "
                                                                               + EMULATOR_HARDWARE_JCT_TABLE_NAME
                                                                               + " INNER JOIN "
                                                                               + HARDWARE_TABLE_NAME
                                                                               + " ON "
                                                                               + EMULATOR_HARDWARE_JCT_TABLE_NAME
                                                                               + ".hardware_id="
                                                                               + HARDWARE_TABLE_NAME
                                                                               + ".hardware_id "
                                                                               + " WHERE "
                                                                               + HARDWARE_TABLE_NAME
                                                                               + ".name=? ORDER BY emulator_id desc ";

    private static final String SELECT_EMULATOR_IMAGEFORMATS            = "SELECT e_if.name FROM "
                                                                               + IMAGEFORMAT_TABLE_NAME
                                                                               + " e_if "
                                                                               + " INNER JOIN "
                                                                               + EMULATOR_IMAGEFORMAT_JCT_TABLE_NAME
                                                                               + " e_eif "
                                                                               + " ON e_eif.imageformat_id=e_if.imageformat_id "
                                                                               + " WHERE e_eif.emulator_id=?";

    private static final String SELECT_EMULATOR_HARDWARE                = "SELECT e_hw.name FROM "
                                                                               + HARDWARE_TABLE_NAME
                                                                               + " e_hw "
                                                                               + " INNER JOIN "
                                                                               + EMULATOR_HARDWARE_JCT_TABLE_NAME
                                                                               + " e_ehw "
                                                                               + " ON e_ehw.hardware_id=e_hw.hardware_id "
                                                                               + " WHERE e_ehw.emulator_id=?";

    /**
     * Constructor
     * @param conn a connection to an H2 database
     */
    public H2EmulatorPackageDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * {@inheritDoc}
     */
    public String getPackageVersion(Integer emuID) {

        // sanity check
        if (emuID == null || emuID.compareTo(0) <= 0) {
            throw new IllegalArgumentException("Invalid emulator ID");
        }

        String packVersion = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            try {

                pstmt = conn.prepareStatement(SELECT_EMULATOR_PACKAGE_VERSION_WHERE);
                pstmt.setLong(1, emuID);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    packVersion = rs.getString("package_version");
                }

            }
            finally {
                rs.close();
                pstmt.close();
            }

        }
        catch (SQLException e) {
             logger.warn("Database: error while getting version for emulator ID= "
             + emuID + ": " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return packVersion;
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> getEmuID(String hardwareName) {

        List<Integer> emuID = new ArrayList<Integer>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // FIXME: Add proper exception handling
        try {
            pstmt = conn.prepareStatement(EMUID_FROM_HWNAME);
            pstmt.setString(1, hardwareName);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                // record has been found
                String res = rs.getString("emulator_id");
                emuID.add(Integer.parseInt(res));
            }
            rs.close();
            pstmt.close();

        }
        catch (SQLException e) {
             logger.warn("Database: Cannot get list of emulator IDs: " +
             e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return emuID;
    }

    // TODO add unit test
    /**
     * {@inheritDoc}
     */
    public int getEmulatorCount() {

        int count = 0;

        try {

            Statement stmt = null;
            ResultSet rs = null;

            try {

                stmt = conn.createStatement();
                rs = stmt.executeQuery(COUNT_EMULATOR);

                while (rs.next()) {
                    count = rs.getInt(1);
                }

            }
            finally {
                rs.close();
                stmt.close();
            }

        }
        catch (SQLException e) {
             logger.warn("Database: Cannot get emulator count: " +
             e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return count;
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> getEmulatorIDs() {

        ArrayList<Integer> ids = new ArrayList<Integer>();

        try {

            Statement stmt = null;
            ResultSet rs = null;

            try {

                stmt = conn.createStatement();
                rs = stmt.executeQuery(SELECT_EMULATOR_IDS);

                while (rs.next()) {
                    ids.add(rs.getInt("emulator_id"));
                }

            }
            finally {
                rs.close();
                stmt.close();
            }

        }
        catch (SQLException e) {
             logger.warn("Database: Cannot get emulator IDs: " +
             e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return ids;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getHardwareIDs() {

        HashSet<String> ids = new HashSet<String>();

        Statement stmt = null;
        ResultSet rs = null;

        try {

            try {

                stmt = conn.createStatement();
                rs = stmt.executeQuery(SELECT_HARDWARE_IDS);

                while (rs.next()) {
                    ids.add(rs.getString("hardware_id"));
                }

            }
            finally {
                rs.close();
                stmt.close();
            }

        }
        catch (SQLException e) {
             logger.warn("Database: Cannot get hardware IDs: " +
             e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return ids;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getHardwareNames() {

        HashSet<String> hwNames = new HashSet<String>();

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
                stmt = conn.prepareStatement(SELECT_HARDWARE_NAMES);
                rs = stmt.executeQuery();

                while (rs.next()) {
                    hwNames.add(rs.getString("name"));
                }
                stmt.close();
        }
        catch (SQLException e) {
             logger.warn("Database: Cannot get hardware names: " +
             e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return hwNames;
    }

    
    /**
     * {@inheritDoc}
     */
    public InputStream getEmulatorPackage(Integer emuID) {

        InputStream is = null;

        // Ensure we're using a positive value
        if (emuID.compareTo(0) <= 0) {
            throw new IllegalArgumentException("Argument 'emuID' is invalid (zero/negative)");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            // Get emulator package from database
            logger.info("Retrieving emulator executable: " + emuID);
            pstmt = conn.prepareStatement(SELECT_EMULATOR_PACKAGE_WHERE);
            pstmt.setLong(1, emuID);
            rs = pstmt.executeQuery();

            if (rs.first()) {
                // record has been found
                Blob blob = rs.getBlob("package");
                is = blob.getBinaryStream();
            }
        }
        catch (SQLException e) {
             logger.warn("Database: error while retrieving binary for emulator ID= "
             + emuID + ": " + e.toString());
            e.printStackTrace();
            return is;
        }

        logger.info("Returning streams for package: " + emuID);
        return is;
    }

    /**
     * {@inheritDoc}
     */
    public String getEmulatorPackageFileName(Integer emuID) {

        // sanity check
        if (emuID == null || emuID.compareTo(0) <= 0) {
            throw new IllegalArgumentException("Invalid emulator ID");
        }

        String emuPackFileName = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            try {

                pstmt = conn.prepareStatement(SELECT_EMULATOR_PACKAGE_FILENAME_WHERE);
                pstmt.setLong(1, emuID);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    emuPackFileName = rs.getString("package_name");
                }

            }
            finally {
                rs.close();
                pstmt.close();
            }

        }
        catch (SQLException e) {
             logger.warn("Database: error while retrieving package name for emulator ID= "
             + emuID + ": " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return emuPackFileName;

    }

    /**
     * {@inheritDoc}
     */
    public String getEmulatorName(Integer emuID) {

        // sanity check
        if (emuID == null || emuID.compareTo(0) <= 0) {
            throw new IllegalArgumentException("Invalid emulator ID");
        }

        String emuName = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            try {

                pstmt = conn.prepareStatement(SELECT_EMULATOR_NAME_WHERE);
                pstmt.setLong(1, emuID);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    emuName = rs.getString("name");
                }

            }
            finally {
                rs.close();
                pstmt.close();
            }

        }
        catch (SQLException e) {
             logger.warn("Database: error while retrieving name for emulator ID= "
             + emuID + ": " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return emuName;

    }

    /**
     * {@inheritDoc}
     */
    public String getEmulatorVersion(Integer emuID) {

        // sanity check
        if (emuID == null || emuID.compareTo(0) <= 0) {
            throw new IllegalArgumentException("Invalid emulator ID");
        }

        String emuVersion = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            try {

                pstmt = conn.prepareStatement(SELECT_EMULATOR_VERSION_WHERE);
                pstmt.setLong(1, emuID);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    emuVersion = rs.getString("version");
                }

            }
            finally {
                rs.close();
                pstmt.close();
            }

        }
        catch (SQLException e) {
             logger.warn("Database: error while retrieving version for emulator ID= "
             + emuID + ": " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return emuVersion;

    }

    /**
     * {@inheritDoc}
     */
    public String getEmulatorDescription(Integer emuID) {

        // sanity check
        if (emuID == null || emuID.compareTo(0) <= 0) {
            throw new IllegalArgumentException("Invalid emulator ID");
        }

        String emuDescription = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            try {

                pstmt = conn.prepareStatement(SELECT_EMULATOR_DESCRIPTION_WHERE);
                pstmt.setLong(1, emuID);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    emuDescription = rs.getString("description");
                }

            }
            finally {
                rs.close();
                pstmt.close();
            }

        }
        catch (SQLException e) {
             logger.warn("Database: error while retrieving description for emulator ID= "
             + emuID + ": " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return emuDescription;

    }

    /**
     * {@inheritDoc}
     */
    public String getEmulatorExecType(Integer emuID) {

        // sanity check
        if (emuID == null || emuID.compareTo(0) <= 0) {
            throw new IllegalArgumentException("Invalid emulator ID");
        }

        String emuType = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            try {

                pstmt = conn.prepareStatement(SELECT_EMULATOR_EXEC_TYPE_WHERE);
                pstmt.setLong(1, emuID);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    emuType = rs.getString("exec_type");
                }

            }
            finally {
                rs.close();
                pstmt.close();
            }

        }
        catch (SQLException e) {
             logger.warn("Database: error while retrieving executable type for emulator ID= "
             + emuID + ": " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return emuType;

    }

    /**
     * {@inheritDoc}
     */
    public String getEmulatorInstructions(Integer emuID) {

        // sanity check
        if (emuID == null || emuID.compareTo(0) <= 0) {
            throw new IllegalArgumentException("Invalid emulator ID");
        }

        String emuInstructions = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            try {

                pstmt = conn.prepareStatement(SELECT_EMULATOR_USER_INSTRUCTIONS_WHERE);
                pstmt.setLong(1, emuID);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    emuInstructions = rs.getString("user_instructions");
                }

            }
            finally {
                rs.close();
                pstmt.close();
            }

        }
        catch (SQLException e) {
             logger.warn("Database: error while retrieving user instructions for emulator ID= "
             + emuID + ": " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return emuInstructions;

    }

    /**
     * {@inheritDoc}
     */
    public String getEmulatorPackageType(Integer emuID) {

        // sanity check
        if (emuID == null || emuID.compareTo(0) <= 0) {
            throw new IllegalArgumentException("Invalid emulator ID");
        }

        String emuType = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            try {

                pstmt = conn.prepareStatement(SELECT_EMULATOR_PACKAGE_TYPE_WHERE);
                pstmt.setLong(1, emuID);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    emuType = rs.getString("package_type");
                }

            }
            finally {
                rs.close();
                pstmt.close();
            }

        }
        catch (Exception e) {
             logger.warn("Database: Cannot get package type for emulator " +
             emuID + ": " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return emuType;

    }

    /**
     * {@inheritDoc}
     */
    public String getEmulatorExecName(Integer emuID) {

        // sanity check
        if (emuID == null || emuID.compareTo(0) <= 0) {
            throw new IllegalArgumentException("Invalid emulator ID");
        }

        String emuExecName = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            try {

                pstmt = conn.prepareStatement(SELECT_EMULATOR_EXEC_NAME_WHERE);
                pstmt.setLong(1, emuID);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    emuExecName = rs.getString("exec_name");
                }

            }
            finally {
                rs.close();
                pstmt.close();
            }

        }
        catch (SQLException e) {
             logger.warn("Database: Cannot get executable name for emulator ID= "
             + emuID + ": " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return emuExecName;
    }

    /**
     * {@inheritDoc}
     */
    public String getEmulatorExecDir(Integer emuID) {

        // sanity check
        if (emuID == null || emuID.compareTo(0) <= 0) {
            throw new IllegalArgumentException("Invalid emulator ID");
        }

        String emuExecDir = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            try {

                pstmt = conn.prepareStatement(SELECT_EMULATOR_EXEC_DIR_WHERE);
                pstmt.setLong(1, emuID);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    emuExecDir = rs.getString("exec_dir");
                }

            }
            finally {
                rs.close();
                pstmt.close();
            }

        }
        catch (Exception e) {
             logger.warn("Database: Cannot get executable directory for emulator "
             + emuID + ": " + e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return emuExecDir;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getImageFormats(Integer emuID) {

        ArrayList<String> list = new ArrayList<String>();

        try {

            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {

                pstmt = conn.prepareStatement(SELECT_EMULATOR_IMAGEFORMATS);
                pstmt.setLong(1, emuID);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    list.add(rs.getString("name"));
                }

            }
            catch (SQLException sqle) {
                 logger.warn(sqle.toString());
                sqle.printStackTrace();
            }
            finally {
                rs.close();
                pstmt.close();
            }

        }
        catch (Exception e) {
             logger.warn("Database: Cannot get image format names: " +
             e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return list;

    }

    /**
     * {@inheritDoc}
     */
    public List<String> getHardware(Integer emuID) {

        ArrayList<String> list = new ArrayList<String>();

        try {

            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {

                pstmt = conn.prepareStatement(SELECT_EMULATOR_HARDWARE);
                pstmt.setLong(1, emuID);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    list.add(rs.getString("name"));
                }

            }
            catch (SQLException sqle) {
                 logger.warn(sqle.toString());
                sqle.printStackTrace();
            }
            finally {
                rs.close();
                pstmt.close();
            }

        }
        catch (Exception e) {
             logger.warn("Database: Cannot get hardware names: " +
             e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return list;

    }

	@Override
	public LanguageList getLanguages() {
		logger.debug("Querying available languages");

        LanguageList languages = new LanguageList();

		Statement stmt = null;
		ResultSet rs = null;
		try {
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(SELECT_LANGUAGES);
				while (rs.next()) {
					Language language = new Language();
					language.setLanguageId(rs.getString("language_id"));
					language.setLanguageName(rs.getString("language_name"));
					languages.getLanguages().add(language);
					logger.debug("Found language: " + language.getLanguageName());
				}
			}
			finally {
				rs.close();
				stmt.close();
			}
		}
		catch (SQLException e) {
			logger.error("Database: Error while retrieving data from languages table: " + e);
            e.printStackTrace();
			throw new RuntimeException(e);
		}
		return languages;
	}    


}
