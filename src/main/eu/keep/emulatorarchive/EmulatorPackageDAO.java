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
import java.util.List;
import java.util.Set;

/**
 * Interface definition for DAOs for emulator packages.
 * @author David Michel
 */
public interface EmulatorPackageDAO {

    /**
     * Returns the package file name associated with an emulator
     * @param emuID emulator ID
     * @return String the package version
     */
    public String getPackageVersion(Integer emuID);

    /**
     * Count the number of emulator available
     * @return List of Integers
     */
    public int getEmulatorCount();

    /**
     * Returns the IDs of all available emulator packages
     * @return List of Integers
     */
    public List<Integer> getEmulatorIDs();

    /**
     * Returns a list of hardware supported by the available emulators
     * @return Set of hardware
     */
    public Set<String> getHardwareIDs();

    /**
     * Returns a list of hardware names supported by the available emulators
     * @return Set of hardware names
     */
    public Set<String> getHardwareNames();

    /**
     * Returns the executable package associated with an emulator
     * @param emuID emulator ID
     * @return InputStream A handle to the binary
     */
    public InputStream getEmulatorPackage(Integer emuID);

    /**
     * Returns the package file name associated with an emulator
     * @param emuID emulator ID
     * @return String the emulator package file name
     */
    public String getEmulatorPackageFileName(Integer emuID);

    /**
     * Returns the emulator name
     * @param emuID emulator ID
     * @return String the executable type
     */
    public String getEmulatorName(Integer emuID);

    /**
     * Returns the emulator version
     * @param emuID emulator ID
     * @return String the emulator version
     */
    public String getEmulatorVersion(Integer emuID);

    /**
     * Returns the emulator language name
     * @param emuID emulator ID
     * @return String the name of the language associated with this emulator
     */
    public String getEmulatorLanguage(Integer emuID);

    /**
     * Returns the emulator description
     * @param emuID emulator ID
     * @return String the emulator description
     */
    public String getEmulatorDescription(Integer emuID);

    /**
     * Returns the executable type associated with an emulator
     * @param emuID emulator ID
     * @return String the executable type
     */
    public String getEmulatorExecType(Integer emuID);

    /**
     * Returns the emulator executable name
     * @param emuID emulator ID
     * @return String representing the executable name
     */
    public String getEmulatorExecName(Integer emuID);

    /**
     * Returns the name of the directory in which the emulator executable is
     * located
     * @param emuID emulator ID
     * @return String representing the directory
     */
    public String getEmulatorExecDir(Integer emuID);

    /**
     * Returns the list of formats supported by an emulator
     * @param emuID emulator ID
     * @return list of image format name
     */
    public List<String> getImageFormats(Integer emuID);

    /**
     * Returns the list of formats supported by an emulator
     * @param emuID emulator ID
     * @return list of hardware name
     */
    public List<String> getHardware(Integer emuID);
    
    /**
     * Returns the user instructions for an emulator
     * @param emuID emulator ID
     * @return list of hardware name
     */
    public String getEmulatorInstructions(Integer emuID);
    
    /**
     * Returns the package type associated with an emulator
     * @param emuID emulator ID
     * @return String the executable type
     */
    public String getEmulatorPackageType(Integer emuID);

    /**
     * Returns the emulator ID based on a given target hardware type
     * @param hardwareName The type to search on
     * @return List<Integer> List of matching emulator IDs
     */
    public List<Integer> getEmuID(String hardwareName);

    /**
     * Returns the languages from the Languages table
     * @return List of values from the table
     */
    public EmuLanguageList getLanguages();
    
}
