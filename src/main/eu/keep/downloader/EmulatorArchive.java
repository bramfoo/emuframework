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

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Set;

import javax.xml.ws.WebServiceException;

import eu.keep.emulatorarchive.emulatorpackage.EmuLanguageList;
import eu.keep.emulatorarchive.emulatorpackage.EmulatorPackage;

/**
 * Interface to the Emulator Archive, the webservice providing emulator package metadata and binary files
 * @author Bram Lohman
 */
public interface EmulatorArchive {

    /**
     * Returns an InputStream of the emulator executable
     * @param emuID Unique ID of an emulator package
     * @return InputStream Binary stream of emulator executable 
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public abstract InputStream downloadEmulatorPackage(Integer emuID) throws ConnectException,
            IOException, SocketTimeoutException, WebServiceException;

    /**
     * Returns the specified emulator package metadata from the archive database
     * @param emuID Unique ID of an emulator package
     * @return EmulatorPackage Emulator package metadata
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public abstract EmulatorPackage getEmulatorPackage(Integer emuID) throws ConnectException,
            SocketTimeoutException, WebServiceException;

    /**
     * Returns the list of all emulator packages in the archive database
     * @return List<EmulatorPackage> List of emulator packages 
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public abstract List<EmulatorPackage> getEmulatorPackages() throws ConnectException,
            SocketTimeoutException, WebServiceException;

    /**
     * Get the list of hardware supported by the Emulator Archive
     * @return Set<String> A set of hardware names
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public abstract Set<String> getSupportedHardware()
            throws ConnectException, SocketTimeoutException, WebServiceException;

    /**
     * Get the list of emulators that support a particular type of hardware
     * @param hardwareName String representing the emulator hardware
     * @return List<EmulatorPackage> A list of emulator packages that support the given hardware 
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public abstract List<EmulatorPackage> getEmulatorsByHardware(String hardwareName) throws ConnectException, SocketTimeoutException, WebServiceException;

    /**
     * Get the list of emulator languages
     * @return List of all languages used by emulators
     * @throws ConnectException when setting up a connection to the server fails (e.g. not within allocated time)  
     * @throws SocketTimeoutException when a response takes longer than the allocated time 
     * @throws WebServiceException for any other webservice error
     */
    public abstract EmuLanguageList getEmulatorLanguages() throws ConnectException, SocketTimeoutException, WebServiceException;
    
}
