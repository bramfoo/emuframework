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

package eu.keep.controller.emulatorRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Implementation of the methods used for starting/stopping an emulation process.
 * It simply uses the {@code NativeRunner} class to launch a native executable
 * and uses the owner (parent class)
 * to start and kill the newly created process.
 * @author David Michel
 */
public class NativeEmulatorRunner {

    private NativeRunner nr;
    private static final Logger logger = Logger.getLogger(NativeEmulatorRunner.class.getName());
    private File workingDir;

    public NativeEmulatorRunner(File workingDir) {
    	this.workingDir = workingDir;
    }

    public File getWorkingDir() {
		return workingDir;
	}

	/**
     * Start the emulation process given without command line arguments
     * @param id process id
     * @param preamble list of arguments required before the executable
     * @param exec emulator executable File
     * @param owner Parent class as an instance of {@code
     *            EmulatorProcessManager}
     */
    public void start(int id, List<String> preamble, File exec, EmulatorProcessManager owner) {
        start(id, preamble, exec, new ArrayList<String>(), owner);
    }

    /**
     * Start the emulation process given with command line arguments
     * @param id process id
     * @param preamble list of arguments required before the executable
     * @param exec emulator executable File
     * @param args array of arguments for that executable
     * @param owner Parent class as an instance of {@code
     *            EmulatorProcessManager}
     */
    public void start(int id, List<String> preamble, File exec, List<String> args, EmulatorProcessManager owner) {

        logger.debug("Launching emulator: " + exec);
        logger.debug("Preamble: " + preamble +"; Arguments: " + args);

        try {
            startProcess(id, preamble, exec, args, owner);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Launch an emulation process given an executable, command line arguments,
     * process id and parent class (owner)
     * @param id process id
     * @param preamble array of arguments required before the executable
     * @param exec emulator executable File
     * @param args array of arguments added after the executable
     * @param owner Parent class as an instance of {@code
     *            EmulatorProcessManager}
     */
    public void startProcess(int id, List<String> preamble, File exec, List<String> args, EmulatorProcessManager owner) {

        logger.info("Starting process " + id);
        try {
            if (preamble == null || preamble.isEmpty())
            {
                logger.debug("Process " + id + " has no preamble");
                nr = new NativeRunner(id, exec, args, owner, workingDir);
            }
            else
            {
                logger.debug("Process " + id + " has has preamble: " + preamble);
                nr = new NativeRunner(id, preamble, exec, args, owner, workingDir);
            }
            nr.start();
        }
        catch (Exception e) {
            logger.error("Error launching native executable on host");
            throw new RuntimeException(e);
        }
    }

    /**
     * Stop the emulation process
     */
    public void stop() {
        logger.info("Stopping process");
        nr.kill();
    }
}
