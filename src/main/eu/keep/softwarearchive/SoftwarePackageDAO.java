/*
* $Revision: 685 $ $Date: 2011-05-31 00:17:31 +0100 (Tue, 31 May 2011) $
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
* Project Title: Software Archive (SWA)$
*/

package eu.keep.softwarearchive;

import java.io.InputStream;
import java.util.List;

/**
 * Interface definition for DAOs for software packages.
 * @author David Michel
 */
public interface SoftwarePackageDAO {

    /**
     * Returns a list of all image IDs
     * @return List of String
     */
    public List<String> getImageIDs();

    /**
     * Returns a list of image IDs based on a given target OS name
     * @param osName String representing the name of the OS
     * @return List of String
     */
    public List<String> getImageIDs(String osName);
    
    /**
     * Returns a list of image IDs based on a given target App and OS names
     * @param appName String representing the name of the app
     * @param osName String representing the name of the OS
     * @return List of String
     */
    public List<String> getImageIDs(String appName, String osName);

    /**
     * Returns the image file associated given its ID
     * @param imageID image ID
     * @return InputStream A handle to the image file
     */
    public InputStream getImageFile(String imageID);

    /**
     * Returns the database rows of a query on a view based on an image ID
     * @param id Column ID
     * @param primaryColumn The column to be used as clause. If true, uses img_id; if false uses any of "os_id", "app_id", depending on view queried
     * @param viewName The view to be queried. Currently supports 'img', 'os' and 'app'
     * @param List<String> Columns names to be returned. The column data must all be of type VARCHAR2! 
     * @return Map of lists, with the column name as key and a list for the values
     */
    public List<List<String>> getViewData(String id, boolean primaryCol, String viewName, List<String> columnNames);

    /**
     * Returns the values the 'pathways' view
     * @param String File Format name
     * @param List<String> Columns names to be returned. The column data must all be of type VARCHAR2! 
     * @return Map of lists, with the column name as key and a list for the values
     */
    public List<List<String>> getPathwaysView(String fileFormat, List<String> columnNames);
    
    /**
     * Returns the values of the FileFormat table
     * @param fileFormatID File Format ID
     * @return List of values from the table
     */
    public List<String> getFileFormatInfo(String fileFormatID);
    
    /**
     * Returns the values of the Platforms table
     * @param hardwarePlatformID Platform ID
     * @return List of values from the table
     */
    public List<String> getHardwarePlatformInfo(String hardwarePlatformID);

}
