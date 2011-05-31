/*
* $Revision: 803 $ $Date: 2011-05-31 00:27:06 +0100 (Tue, 31 May 2011) $
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Functionality for starting executables as external processes in new threads.
 * @author David Michel
 */
public class NativeRunner extends Thread {

    private static final Logger          logger = Logger.getLogger(NativeRunner.class.getName());

    private Process                      proc;
    private final int                    id;
    private final EmulatorProcessManager owner;
    private ProcessBuilder pb;

    /**
     * Constructor
     * @param id process id
     * @param exeFile file representing the executable
     * @param args list of string representing the list of arguments to be used
     *            when calling that executable
     * @param owner instance of the Kernel that 'owns' the process
     */
    public NativeRunner(int id, File exeFile, List<String> args, EmulatorProcessManager owner, File workingDir) {
        this(id, new ArrayList<String>(), exeFile, args, owner, workingDir);
    }

    /**
     * Constructor
     * @param id process id
     * @param preamble a list of string representation of any preamble to be used before
     *            the executable name, e.g. "java -jar"
     * @param exeFile file representing the executable
     * @param args list of string representing the list of arguments to be used
     *            when calling that executable
     * @param owner instance of the Kernel that 'owns' the process
     */
    public NativeRunner(int id, List<String> preamble, File exeFile, List<String> args,
            EmulatorProcessManager owner, File workingDir) {

        this.id = id;
        this.owner = owner;

		    List<String> cmds = new ArrayList<String>();
		    // add the prepend if any
		    if(!preamble.isEmpty())
		    {
		        cmds.addAll(preamble);
		        }
		    cmds.add(exeFile.getAbsolutePath());
		    cmds.addAll(args);

		    logger.info("Process commands: " + cmds.toString());

	    	pb = new ProcessBuilder(cmds);
//	    	pb.directory(workingDir);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void run() {

        // Launch sub-process
            logger.info("Running command '" + pb.command() + "' in directory '" + pb.directory() + "'");

            try {
            	proc = pb.start();
            }
            catch(IOException ioe) {
            	logger.error("Error starting process: " + ioe);
            }

            // Get the processes' stdout and stderr, start them as separate threads
            InputStream is = proc.getInputStream();
            InputStream err = proc.getErrorStream();
            new Thread(new ChildReader(err, logger, id)).start();
            new Thread(new ChildReader(is, logger, id)).start();
            
        // Wait for it for be killed
        int exitVal = 0;
        try {
            exitVal = proc.waitFor();
            logger.info("Process terminated with exit value of  " + exitVal);
             owner.remove(id);
        }
        catch (InterruptedException e) {
            logger.info("Caught exception while waiting for process to end: " + e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * kills the current process
     */
    public void kill() {
        proc.destroy();
        logger.warn("Process (id=" + id + ") killed successfully (args: " + pb.command() + ")");
    }
}

/**
 * Reads and buffers output from a child process 
 *
 */
class ChildReader implements Runnable
    {

    private final InputStream is;
    private final Logger logger;
    private final int id;

    public void run()
        {
        try
            {
            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String s;
            while(( s = br.readLine()) != null ) {
            	logger.info("[" + id + "]:" + s);
                }
            br.close();
            }
        catch(IOException ioe)
            {
            	throw new IllegalArgumentException("Error reading data from child: " + ioe);
            }
        }

    ChildReader(InputStream is, Logger logger, int id) {
        this.is = is;
        this.logger = logger;
        this.id = id;
        }
    }