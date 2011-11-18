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

package eu.keep.kernel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import eu.keep.characteriser.Format;
import eu.keep.downloader.db.DBRegistry;
import eu.keep.emulatorarchive.emulatorpackage.EmuLanguageList;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;
import eu.keep.softwarearchive.pathway.SwLanguageList;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.softwarearchive.softwarepackage.SoftwarePackage;
import eu.keep.util.Language;


/**
 * API for the Core Emulation Framework.
 * The starting point for the Core EF is the digital object. 
 * This digital object is in the form of a computer file. The digital 
 * object may or may not include appropriate metadata describing it.
 * External technical registries provide the Core EF with a pathway describing the required
 * hardware, software and application to render the digital objects.
 * The Core EF renders the selected digital object using emulators. These emulators are
 * downloaded from the Emulator Archive, an external database containing (certified)
 * emulator packages. An emulator package contains executable files to run the emulator, and
 * associated metadata describing the emulator’s hardware, configuration, etc.
 * The pathway for rendering a digital object consists not only of a description of a hardware
 * emulator, but also a description of software dependencies including operating system,
 * application and drivers. This software stack is retrieved from an external software archive,
 * either as a complete stack or as separate components.
 * Given the emulator and software stack, the Core Emulation Framework then prepares, configures, and sets
 * up the complete environment required to render the digital file.
 * 
 * @author Bram Lohman
 * @author David Michel
 */
public interface CoreEngineModel {

    //////////////////////
    // General information
    //////////////////////
    /**
     * Get the Emulation Framework version from the jar manifest
     * @return String representing the version
     */
    public String getVersion();
    
    /**
     * Get the Emulation Framework title from the jar manifest
     * @return String representing the title
     */    
    public String getTitle();
    
    /**
     * Get the Emulation Framework vendor from the jar manifest
     * @return String representing the vendor
     */    
    public String getVendor();    

    //////////////////////
    // Core initialization
    //////////////////////
    /**
     * Get the Core Engine settings
     * @return Properties the Java Properties object
     */
    Properties getCoreSettings();

    /**
     * Register an observer
     * @param coreObs A Core Emulation Framework observer
     */
    void registerObserver(CoreObserver coreObs);

    /**
     * Remove an observer
     * @param coreObs A Core Emulation Framework observer
     */
    void removeObserver(CoreObserver coreObs);

    /**
     * Get the set of languages to filter out Emulators or Software. 
     * Emulators or Software using any of the languages in this set will not be selected/presented. 
     * @return Set of languages to filter out.
     */
	Set<Language> getAcceptedLanguages();

    /**
     * Define the set of languages to filter out Emulators or Software. 
     * Emulators or Software using any of the languages in this set will not be selected/presented. 
     * @param blockedLanguages Set of languages to filter out.
     */
    void setAcceptedLanguages(Set<Language> blockedLanguages);
    
    /**
     * Add a language to filter out Emulators or Software. 
     * Emulators or Software using this language will not be selected/presented. 
     * @param language the language to filter out.
     */
    void addAcceptedLanguage(Language language);
    
    /**
     * Remove a language to filter out Emulators or Software. 
     * Emulators or Software using this language will in future be selected/presented again. 
     * @param language the language to remove from the filter.
     */
    void removeAcceptedLanguage(Language language);
    
    /**
     * Stop the Core Emulator Framework engine
     * @return True if engine stopped without error, false otherwise
     * @throws IOException 
     */
    boolean stop() throws IOException;

    /**
     * Clean up any temporary files and directories that were created
     * by the Core Engine to unpack files, run emulators, etc.
     */
    void cleanUp();

    ///////////////////////////
    // Characterisation
    ///////////////////////////
    /**
     * Characterise a digital object and returns information on format names,
     * mime types and the reporting tools.
     * @param digObj File representing the digital object
     * @return List<Format> list of Format objects
     * @throws IOException If a FITS characterisation error occurs
     * @see eu.keep.characteriser.Format
     */
    List<Format> characterise(File digObj) throws IOException;

    /**
     * Characterise a digital object and returns technical metadata information
     * @param digObj File representing the digital object
     * @return Map<String, List<String>> a Map of item names (as keys) and an associated list of values 
     * @throws IOException If a FITS characterisation error occurs
     */
    Map<String, List<String>> getTechMetadata(File digObj) throws IOException;

    /**
     * Characterise a digital object and returns file information
     * @param digObj File representing the digital object
     * @return Map<String, List<String>> a Map of item names (as keys) and an associated list of values 
     * @throws IOException If a FITS characterisation error occurs
     */
    Map<String, List<String>> getFileInfo(File digObj) throws IOException;

    /**
     * Get pathways for a given file formatName. This contacts the active
     * technical registries and returns the pathways (digital object, 
     * rendering application, OS, hardware platform) found
     * for the given formatName.
     * Rendering applications and OSs will be filtered on the languages 
     * that the user has indicated as acceptable.
     * @param format Format of digital object
     * @return List<Pathway> List of available pathways objects
     * @throws IOException If a Software Archive connection error occurs
     */
    List<Pathway> getPathways(Format format) throws IOException;

    /**
     * Checks if a given pathway is satisfiable given the available emulators
     * and software images
     * @param pathway Pathway Configuration describing the environment
     * @return true is pathway is satisfiable, false otherwise
     * @throws IOException If an error occurs while connecting the Emulator/Software Archive
     */
    boolean isPathwaySatisfiable(Pathway pathway) throws IOException;

    //////////////////////////
    // Emulator configuration
    //////////////////////////

    /**
     * Get the configuration map of all available emulator parameters
     * Useful for manual configuration of the emulator, to be used with {@code
     * setEmuConfig()}
     * @param conf Integer representing an existing configuration
     * @return Map<String, List<Map<String, String>>> Map of emulator parameters ordered by component and (multiple) parameter-value pairs
     * @throws IOException If a template error occurs while configuring the parameters
     */
    Map<String, List<Map<String, String>>> getEmuConfig(Integer conf) throws IOException;

    /**
     * Set the emulator parameters
     * Useful for manual configuration of the emulator to be used with {@code
     * getEmuConfig()}
     * @param options Map<String, List<Map<String, String>>> map of emulator parameters ordered by component and (multiple) parameter-value pairs
     * @param conf Integer representing an existing configuration
     * @throws IOException If a template error occurs while configuring the parameters
     */
    void setEmuConfig(Map<String, List<Map<String, String>>> options, Integer conf) throws IOException;

    /**
     * Prepares the configuration settings for the selected emulation process
     * The resulting configuration (emulator options) can be edited using
     * {@code setEmuOptions()} and {@code getEmuOptions()}
     * @param digObj File representing the digital object to the passed to the
     *            emulator configurator
     * @param emuPack Emulator metadata package
     * @param swPack Software metadata package
     * @param pathway The Pathway that forms the basis for this configuration
     * @return Integer An identification of a newly generated configuration
     * @throws IOException If an error occurs while connecting the Emulator/Software Archive
     */
    Integer prepareConfiguration(File digObj, EmulatorPackage emuPack, SoftwarePackage swPack, Pathway pathway) throws IOException;

    /////////////////////
    // Emulation process
    /////////////////////

    /**
     * Match emulators with a list of associated software images from a given pathway and filter all by language
     * @param pathway Pathway object to analyse
     * @return Map<EmulatorPackage, List<SoftwarePackage>> A map of emulators with their
     *         associated list of compatible software images, all filtered on the languages 
     *         that the user has indicated as acceptable.
     * @throws IOException If an error occurs while connecting the Emulator/Software Archive
     */
    Map<EmulatorPackage, List<SoftwarePackage>> matchEmulatorWithSoftware(Pathway pathway) throws IOException;

    /**
     * Select an emulator automatically from a list of emulators
     * The selection process picks the first encountered emulator that can run
     * on the current host system
     * @param emuPacks list of emulator packages
     * @return EmulatorPackage The selected emulator package
     * @throws IOException If an error occurs while connecting the Emulator Archive
     */
    EmulatorPackage autoSelectEmulator(List<EmulatorPackage> emuPacks) throws IOException;

    /**
     * Select a software image automatically from a list of software images.
     * The selection process picks the first encountered software image from the
     * list
     * @param swPacks list of software packages
     * @return SoftwarePackage The selected software package
     * @throws IOException If an error occurs while connecting the Software Archive
     */
    SoftwarePackage autoSelectSoftwareImage(List<SoftwarePackage> swPacks) throws IOException;

    /**
     * Select a valid pathway automatically from a list of potential pathways
     * The selection process simply picks the first encountered satisfiable pathway
     * @param pathways List<Pathway> List of pathway objects
     * @return selected Pathway object
     * @throws IOException If an error occurs while analysing the given Pathways
     */
    Pathway autoSelectPathway(List<Pathway> pathways) throws IOException;

    /**
     * Select a format from a list of formats. This will simply pick the first
     * format of the list which should correspond to the format identified
     * by the highest number of tools within FITS
     * @param formats list of file Format objects
     * @return selected Format object
     * @throws IOException If a Format exception occurs
     * @see eu.keep.characteriser.Format
     */
    Format autoSelectFormat(List<Format> formats) throws IOException;

    /**
     * Run the chosen emulation process
     * An emulator must have already been selected and its configuration
     * settings properly prepared.
     * @param conf Integer representing an existing configuration
     * @throws IOException If the configuration environment is not set up properly
     */
    void runEmulationProcess(Integer conf) throws IOException;

    /**
     * Retrieve the technical environment, i.e. pathway from a metadata file (xml file)
     * which must validate the xsd schema {@code PathwaySchema.xsd}.
     * @param metadataFile File describing the pathway required to render a the digital object
     * @return Pathway The pathway described by the XML file
     * @throws IOException If a validation error occurs
     */
    Pathway extractPathwayFromFile(File metadataFile) throws IOException;

    /**
     * Launches the emulation process automatically (i.e. no human intervention)
     * given a digital object only. This method will characterise the given
     * digital object, retrieve a list of
     * pathways from the identified format and then call {@code start(File file,
     * List<Pathway> pathways)}.
     * @param file File representing the digital object to be rendered via
     *            emulation
     * @return True if emulation process is launched without error, false
     *         otherwise
     * @throws IOException If a characterisation error occurs
     */
    boolean start(File file) throws IOException;

    /**
     * Launches the emulation process automatically (i.e. no human intervention)
     * given a digital object and its metadata which should contain all the
     * necessary information
     * to prepare/configure the emulation environment. This method will extract
     * a the Pathway from the metadata using and then launch {@code start(File file, Pathway pw)}.
     * If no emulation pathway is defined in
     * the metadata file, then the automatic emulation process is launched by
     * calling {@code start(File file)}
     * @param file File representing the digital object to be rendered via
     *            emulation
     * @param metadata File representing the pathway required to render the digital object
     * @return True if emulation process is launched without error, false
     *         otherwise
     * @throws IOException If a validation error occurs
     */
    boolean start(File file, File metadata) throws IOException;

    /**
     * Launches the emulation process given a digital object and a list of
     * pathways to select from.
     * A suitable pathway is then selected using an automatic selection method
     * before calling {@code start(File file, Pathway pathway)}.
     * @param file File representing the digital object to be rendered via
     *            emulation
     * @param pathways List of pathways
     * @return True if emulation process is launched without error, false
     *         otherwise
     * @throws IOException If a pathway error occurs
     */
    boolean start(File file, List<Pathway> pathways) throws IOException;

    /**
     * Launches the emulation process given a digital object and a specific
     * pathway.
     * Returns false if the given pathway is not satisfiable (emulator/software
     * not supported/available).
     * @param file File representing the digital object to be rendered via
     *            emulation
     * @param pathway Pathway describing the environment required to render the digital object
     * @return True if emulation process is launched without error, false
     *         otherwise
     * @throws IOException If a pathway error occurs, or the emulation process cannot be launched successfully
     */
    boolean start(File file, Pathway pathway) throws IOException;

    ////////////////////
    // Emulator database
    ////////////////////

    /**
     * Get the list of all emulator packages available in the Emulator Archive
     * @return List<EmulatorPackage> the list of all emulator packages
     * @throws IOException If an Emulator Archive connection error occurs 
     */
    List<EmulatorPackage> getEmuListFromArchive() throws IOException;

    /**
     * Get the list of hardware supported by the Emulator Archive
     * @return Set<String> Hardware names supported by the emulator archive
     * @throws IOException If an Emulator Archive connection error occurs 
     */
    Set<String> getSupportedHardwareFromArchive() throws IOException;

    /**
     * Get the list of emulator packages that support a hardware type in the emulator archive
     * @param hardwareName String describing the hardware type
     * @return List<EmulatorPackage> Emulators that support the hardware type
     * @throws IOException If an Emulator Archive connection error occurs 
     */
    List<EmulatorPackage> getEmusByHWFromArchive(String hardwareName) throws IOException;

    /**
     * Returns a list of supported emulators that satisfy a given
     * pathway.
     * Retrieves a list of emulators packages that
     * satisfy (the hardware part) of the pathway
     * @param pathway Pathway object describing the environment required to render a digital object
     * @return list of emulator metadata packages
     * @throws IOException If an Emulator Archive connection error occurs 
     */
    List<EmulatorPackage> getEmulatorsByPathway(Pathway pathway) throws IOException;

    /**
     * Select the whitelisted emulator IDs from the local database
     * @return List<EmulatorPackage> a list of emulator metadata packages
     * @throws IOException If an database connection error occurs 
     */
    public List<EmulatorPackage> getWhitelistedEmus() throws IOException;

    /**
     * Adds an emulator ID to the whitelist in the local database
     * (list of emulators that will be used for rendering a digital object)
     * @param i Unique ID of emulator
     * @return True if ID successfully added to whitelist, false otherwise
     * @throws IOException If an database connection error occurs 
     */
    public boolean whiteListEmulator(Integer i) throws IOException;

    /**
     * Removes an emulator ID from the whitelist in the local database
     * (list of emulators that will be used for rendering a digital object)
     * @param i Unique ID of emulator
     * @return True if ID successfully removed from whitelist, false otherwise
     * @throws IOException If a database connection error occurs 
     */
    public boolean unListEmulator(Integer i) throws IOException;

    /**
     * Gets all emulator languages from the emulator archive
     * @return List of all languages used by emulators
     * @throws IOException If a database connection error occurs 
     */
    public List<Language> getEmulatorLanguages() throws IOException;

    ////////////////////
    // Software database
    ////////////////////
    /**
     * Returns a list of supported software packages that satisfy a given
     * pathway.
     * Retrieves a list of software metadat packages from the software database that
     * satisfy (the operating system and application part) of the pathway
     * @param pathway Pathway object
     * @return list of software packages
     * @throws IOException If an Software Archive connection error occurs 
     */
    List<SoftwarePackage> getSoftwareByPathway(Pathway pathway) throws IOException;


    /**
     * Get all software packages available in the software archive
     * @return List<SoftwarePackage> the list of software packages in the archive
     * @throws IOException If an Software Archive connection error occurs 
     */
   public List<SoftwarePackage> getSoftwareListFromArchive() throws IOException;

   /**
    * Gets all software languages from the software archive
    * @return List of all languages used by software
    * @throws IOException If a database connection error occurs 
    */
   public List<Language> getSoftwareLanguages() throws IOException;

   ////////////////////////
    // Technical registries
    ///////////////////////

    /**
     * Retrieve the list of technical registries
     * @return List<Registry> The list of technical registries in the local
     *         database
     * @throws IOException If a database connection error occurs 
     */
    List<DBRegistry> getRegistries() throws IOException;

    /**
     * Insert registry information from list into the local database 
     * This replaces all existing registry information with the contents of the list
     * @param listReg List of Registry
     * @throws IOException If a database connection error occurs 
     */
    List<DBRegistry> setRegistries(List<DBRegistry> listReg) throws IOException;

}
