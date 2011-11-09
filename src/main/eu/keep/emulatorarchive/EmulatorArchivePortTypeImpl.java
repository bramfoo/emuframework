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


/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package eu.keep.emulatorarchive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;

import javax.activation.DataHandler;
import javax.activation.DataSource;

/**
 * Implementation of the EmulatorArchivePortType interface
 * The EmulatorArchivePortType interface is auto-generated by Apache CXF wsdl2java
 * Caution: this class can also be auto-generated by CXF (using the '-impl' flag),
 * but will contain empty methods. Do not overwrite! 
 *  
 * @author David Michel
 * @author Bram Lohman
 */
public class EmulatorArchivePortTypeImpl implements EmulatorArchivePortType {

    private static final Logger LOGGER = Logger.getLogger(EmulatorArchivePortTypeImpl.class.getName());

    private EmulatorPackageDAO  epDAO;

    public EmulatorArchivePortTypeImpl(EmulatorPackageDAO epDAO) {
        this.epDAO = epDAO;
    }

    /**
     * Generate an emulator package for a specific emulator
     * @param id Emulator ID
     * @return EmulatorPackage Emulator packages 
     */
    public EmulatorPackage getEmulatorPackage(int id) {
        LOGGER.info("Generating package for emulator ID " + id + "...");
        try {

           	EmulatorPackage emuPack = createEmuPack(id);
            LOGGER.info("Returning emulator package " + id);
            return emuPack;
        }
        catch (Exception ex) {
        	LOGGER.fatal("Error generating emulator package: " + ex);
            ex.printStackTrace();
            throw new RuntimeException("Error generating emulator package: " + ex);
        }
    }

    
    /**
     * Generate an emulator package list of all emulators in the database
     * @param dummy Dummy parameter (unused) required by CXF
     * @return EmulatorPackageList List of emulator packages 
     */
    public EmulatorPackageList getEmulatorPackageList(int dummy) {
        LOGGER.info("Generating list of Emulator Packages...");
        try {

            EmulatorPackageList emuPackList = new EmulatorPackageList();

            List<Integer> ids = epDAO.getEmulatorIDs();
            LOGGER.info("Found emulator IDs: " + ids);

            for (Integer i : ids) {

            	EmulatorPackage emuPack = createEmuPack(i);
            	LOGGER.debug("Adding emulator package ID " + i);
            	emuPackList.getEmulatorPackage().add(emuPack);
            }
            LOGGER.info("Returning list of Emulator Packages");
            return emuPackList;
        }
        catch (Exception ex) {
        	LOGGER.fatal("Error generating emulator package list: " + ex);
            ex.printStackTrace();
            throw new RuntimeException("Error generating emulator package list: " + ex);
        }
    }

    /**
     * Provide a DataHandler for a emulator binary in the database
     * @param Emulator ID
     * @return DataHandler for binary emulator executable
     */
    public DataHandler downloadEmulator(int id) {
        LOGGER.info("Getting DataHandler for emulator ID " + id + "...");
        try {

            InputStream is = epDAO.getEmulatorPackage(id);

            LOGGER.debug("Converting InputStream to DataHandler");
            DataSource ds = new InputStreamDataSource(is);
            DataHandler dh = new DataHandler(ds);

            LOGGER.info("Returning DataHandler for emulator ID " + id);
            return dh;
            
        } catch (Exception ex) {
        	LOGGER.fatal("Error getting DataHandler for binary: " + ex);
            ex.printStackTrace();
            throw new RuntimeException("Error getting DataHandler for binary: " + ex);
        }
    }

    /**
     * Return the list of hardware supported by the emulators in the database
     * @param dummy Dummy parameter (unused) required by CXF
     * @return HardwareIDs List of hardware names (strings)  
     */
    public HardwareIDs getSupportedHardware(int dummy) {
        LOGGER.info("Retrieving list of supported hardware from database...");
        try {
            Set<String> hwNames = epDAO.getHardwareNames();
            LOGGER.debug("Supported hardware: " + hwNames.toString());
            
            HardwareIDs hwids = new HardwareIDs();
            hwids.getId().clear();
            hwids.getId().addAll(hwNames);
            LOGGER.info("Returning list of supported hardware from database");
            return hwids;
        }
        catch (Exception ex) {
        	LOGGER.fatal("Error getting hardware list from database: " + ex);
            ex.printStackTrace();
            throw new RuntimeException("Error getting hardware list from database: " + ex);
        }
    }

    /**
     * Return the list of emulators that can render the given hardware
     * @param hw Hardware identification string (see {@code getSupportedHardware()} 
     * @return EmulatorPackageList List of emulator packages  
     */
    public EmulatorPackageList getEmusByHardware(String hw) {
        LOGGER.info("Retrieving list of emulators supporting hardware " + hw + "...");
        
        EmulatorPackageList emuPackList = new EmulatorPackageList();

        try {
            List<Integer> emus = epDAO.getEmuID(hw);
            LOGGER.debug("Supported emulator IDs: " + emus.toString());

            for (Integer i : emus) {
            	EmulatorPackage emuPack = createEmuPack(i);
                emuPackList.getEmulatorPackage().add(emuPack);
            	LOGGER.debug("Adding emulator package ID: " + i);
            }

            LOGGER.info("Returning list of emulators supporting hardware " + hw);
            return emuPackList;
        }
        catch (Exception ex) {
        	LOGGER.fatal("Error getting list of emulators supporting hardware " + hw + ": " + ex);
            ex.printStackTrace();
            throw new RuntimeException("Error getting list of emulators supporting hardware " + hw + ": " + ex);
        }
    }
    
    /**
     * Generate a list of all software languages in the database
     * @param dummy Dummy parameter (unused) required by CXF
     * @return LanguageList List of languages
     */
    @Override
    public LanguageList getLanguageList(int dummy) {
    	LOGGER.info("Generating list of Languages...");
    	LanguageList languages = epDAO.getLanguages();
    	return languages;
    }

    /**
     * Create an Emulator Package (@code EmulatorPackageSchema.xsd) for an emulator in the database
     * @param id ID of the emulator
     * @return Emulator Package for the requested emulator
     */
    private EmulatorPackage createEmuPack(int id)
    {
    	LOGGER.trace("Creating Emulator Package for emulator ID: " + id);
        EmulatorPackage emuPack = new EmulatorPackage();
        EmulatorPackage.Emulator emu = new EmulatorPackage.Emulator();
        EmulatorPackage.Emulator.Executable exec = new EmulatorPackage.Emulator.Executable();
        EmulatorPackage.Package pack = new EmulatorPackage.Package();

        pack.setId(id);
        pack.setVersion(epDAO.getPackageVersion(id));
        pack.setType(epDAO.getEmulatorPackageType(id));
        pack.setName(epDAO.getEmulatorPackageFileName(id));

        emu.setName(epDAO.getEmulatorName(id));
        emu.setUserInstructions(epDAO.getEmulatorInstructions(id));
        emu.setVersion(epDAO.getEmulatorVersion(id));
        emu.setDescription(epDAO.getEmulatorDescription(id));
        List<String> hw = emu.getHardware();
        hw.clear();
        hw.addAll(epDAO.getHardware(id));
        List<String> imform = emu.getImageFormat();
        imform.clear();
        imform.addAll(epDAO.getImageFormats(id));

        exec.setType(epDAO.getEmulatorExecType(id));
        exec.setName(epDAO.getEmulatorExecName(id));
        exec.setLocation(epDAO.getEmulatorExecDir(id));
        emu.setExecutable(exec);

        emuPack.setEmulator(emu);
        emuPack.setPackage(pack);
        LOGGER.trace("Created Emulator Package for emulator ID: " + id + " (" + emuPack.getEmulator().getName() + " ver " + emuPack.getEmulator().getVersion());
        return emuPack;

    }

    private class InputStreamDataSource implements DataSource {

        private InputStream is;

        public InputStreamDataSource(InputStream is) {
            this.is = is;
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return is;
        }

        @Override
        public String getContentType() {
            return null;
        }
    }
}
