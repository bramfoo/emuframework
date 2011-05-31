/*
.* $Revision: 803 $ $Date: 2011-05-31 00:27:06 +0100 (Tue, 31 May 2011) $
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

package eu.keep.controller.emulatorRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import eu.keep.util.FileUtilities;

/**
 * Manager of emulation processes that keep tracks of currently running
 * processes
 * @author David Michel
 * @author Bram Lohman
 */
public class EmulatorProcessManager {

    private static final Logger           logger = Logger.getLogger(EmulatorProcessManager.class
                                                         .getName());

    // Map storing the process id and the instance of the EmulatorRunner class
    // which contains the actual Process
    private Map<Integer, NativeEmulatorRunner> emuProcMap;

    /**
     * Constructor
     */
    public EmulatorProcessManager() {
        emuProcMap = new HashMap<Integer, NativeEmulatorRunner>();
    }

    /**
     * Starts an emulation process
     * @param preamble list of arguments required before the executable
     * @param emuExec file representing the emulator executable to run
     * @param args list of arguments added after the executable
     * @param emuDir file representing the root directory of the running
     *            emulator
     */
    public void startEmulationProcess(List<String> preamble, File emuExec, List<String> args, File emuDir) {

    	NativeEmulatorRunner emuRunner = new NativeEmulatorRunner(emuDir);
        // get new process id
        int procId = emuProcMap.size() + 1;

        // update map track the process and its tmp directory
        emuProcMap.put(procId, emuRunner);
        
        // start process
        emuRunner.start(procId, preamble, emuExec, args, this);
    }

    /**
     * Prints the list of current emulation process
     * @return a map of the current emulation process
     */
    public Map<Integer, NativeEmulatorRunner> getEmulationProcessList() {
        return emuProcMap;
    }

    /**
     * Retrieve a process ID given a directory
     * @param runDir A directory a process is running in
     * @return the process ID of a process running in the directory
     */
    public Integer getProcessID(File runDir) {
        Integer id = -1;

        for (Entry<Integer, NativeEmulatorRunner> process : emuProcMap.entrySet()) {
            if (process.getValue().getWorkingDir().equals(runDir)) {
                id = process.getKey();
                break;
            }
        }

        return id;
    }

    /**
     * Kills all currently running emulation processes
     */
    public synchronized void killAll() {
        for (Iterator<Map.Entry<Integer, NativeEmulatorRunner>> it = emuProcMap.entrySet().iterator(); it
                .hasNext();) {
            Entry<Integer, NativeEmulatorRunner> entry = it.next();
            Integer process = entry.getKey();
            logger.warn("Stopping process id=" + process.toString());

            NativeEmulatorRunner emuRunner = entry.getValue();
            if (emuRunner != null) {
                // Stop the thread
                emuRunner.stop();
                // Delete the directory
                deleteDir(process);
                it.remove();
            }
            else {
                logger.warn("Error trying to kill process: " + process);
            }
        }
    }

    /**
     * Kills a sub-process given its id
     * @param id id of the process to be killed
     */
    public synchronized void kill(Integer id) {

        if (!emuProcMap.isEmpty()) {

            NativeEmulatorRunner emuRunner = emuProcMap.get(id);
            if (emuRunner != null) {
                // stop the thread
                emuRunner.stop();
                // remove the id from the track list and
                remove(id);
            }
            else {
                logger.warn("Error trying to kill process " + id);
            }

        }
        else {
            logger.debug("Nothing to kill");
        }
    }

    /**
     * Removes process id from track lists and remove its tmp directory
     * @param id id of the process to be killed
     */
    public synchronized void remove(int id) {
    	
    	// Ensure it still exists; it may have already been killed/removed
    	if (emuProcMap.containsKey(id))
    	{
	        deleteDir(id);
	        emuProcMap.remove(id);
    	}
    }

    private boolean deleteDir(Integer process) {
        boolean result = false;
        File dir = emuProcMap.get(process).getWorkingDir();

        if(!dir.exists()) {
            logger.warn("Directory does not exist: " + dir.getAbsolutePath());
            return result;
        }

        // Delete the directory
        logger.info("Deleting temporary directory: " + dir.getAbsolutePath());

        result = FileUtilities.deleteDir(dir);
        if (!result) {
            logger.error("Cannot delete temporary directory (still in use): " + dir.getAbsolutePath());
        }

        return result;
    }
}
