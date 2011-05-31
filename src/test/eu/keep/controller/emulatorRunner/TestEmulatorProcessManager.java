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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.keep.controller.emulatorRunner.EmulatorProcessManager;

/**
 * A Junit Test class for
 * {@link eu.keep.controller.emulatorRunner.EmulatorProcessManager }
 * @author Bram Lohman
 * @author David Michel
 */
public class TestEmulatorProcessManager {

    private Logger         logger = Logger.getLogger(this.getClass());

    File                   processExeNeverQuit;
    File                   processExeQuit;
    EmulatorProcessManager emuProc;
    int                    numDirs;
    List<String>           args;
    List<String>           javaPreamble;

    @Before
    public void setup() {
        numDirs = 3;
        args = new ArrayList<String>();
        processExeNeverQuit = new File("testData/processTesterNeverQuit.jar");
        processExeQuit = new File("testData/processTesterQuit.jar");
        emuProc = new EmulatorProcessManager();

        javaPreamble = new ArrayList<String>(2);
        javaPreamble.add("java");
        javaPreamble.add("-jar");

    }

    @After
    public void cleanup() {
        // Clean up
    	logger.info("Attempting clean-up...");
        emuProc.killAll();

        processExeNeverQuit = null;
        processExeQuit = null;
    }

    @Test
    public void testStartEmulationProcess() {
        // Create temporary dir for process
        File tmpDir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        tmpDir.mkdirs();
        logger.info("Using tmp dir: " + tmpDir.toString());

        args = Arrays.asList(tmpDir.toString());
        
        // Start process
        emuProc.startEmulationProcess(javaPreamble, processExeNeverQuit, args, tmpDir);

        // Give the process a chance to create the file
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            fail("Thread.sleep exception thrown");
        }
        assertTrue("File " + tmpDir + "/started.txt exists", (new File(tmpDir, "started.txt"))
                .exists());
    }

    @Test
    public void testKillAll() {

        File[] tmpDirs = new File[numDirs];

        // Create several temporary dirs for processes
        for (int i = 0; i < numDirs; i++) {
            tmpDirs[i] = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID()
                    .toString());
            tmpDirs[i].mkdirs();
        }

        logger.info("Using tmp dirs: " + Arrays.toString(tmpDirs));

        // Start processes
        for (int i = 0; i < numDirs; i++) {
            args = Arrays.asList(tmpDirs[i].toString());
            emuProc.startEmulationProcess(javaPreamble, processExeNeverQuit, args, tmpDirs[i]);
        }

        // Give the processes a chance to create the file
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            fail("Thread.sleep exception thrown");
        }
        for (int i = 0; i < numDirs; i++) {
            assertTrue("File " + tmpDirs[i] + "/started.txt does not exist", (new File(tmpDirs[i],
                    "started.txt")).exists());
        }

        // Kill all
        emuProc.killAll();
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            fail("Thread.sleep exception thrown");
        }
        for (int i = 0; i < numDirs; i++) {
            assertFalse("File " + tmpDirs[i] + " not removed", tmpDirs[i].exists());
        }
    }

    @Test
    public void testKill() {
        File[] tmpDirs = new File[numDirs];

        // Create several temporary dirs for processes
        for (int i = 0; i < numDirs; i++) {
            tmpDirs[i] = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID()
                    .toString());
            tmpDirs[i].mkdirs();
        }

        logger.info("Using tmp dirs: " + Arrays.toString(tmpDirs));

        // Start processes
        for (int i = 0; i < numDirs; i++) {
            args = Arrays.asList(tmpDirs[i].toString());
            emuProc.startEmulationProcess(javaPreamble, processExeNeverQuit, args, tmpDirs[i]);
        }

        // Give the processes a chance to create the file
        try {
            Thread.sleep(3000);
        }
        catch (InterruptedException e) {
            fail("Thread.sleep exception thrown");
        }
        for (int i = 0; i < numDirs; i++) {
            assertTrue("File " + tmpDirs[i] + "/started.txt exists", (new File(tmpDirs[i],
                    "started.txt")).exists());
        }

        // Kill
        emuProc.kill(emuProc.getProcessID(tmpDirs[0]));
        assertFalse("File " + tmpDirs[0] + "not removed", tmpDirs[0].exists());

        for (int i = 1; i < numDirs; i++) {
            assertTrue("File " + tmpDirs[i] + "/started.txt exists", (new File(tmpDirs[i],
                    "started.txt")).exists());
        }

        emuProc.kill(emuProc.getProcessID(tmpDirs[2]));
        assertFalse("File " + tmpDirs[2] + "not removed", tmpDirs[2].exists());
        assertTrue("File " + tmpDirs[1] + "/started.txt does not exist", (new File(tmpDirs[1],
                "started.txt")).exists());
    }

    @Test
    public void testRemove() {
        File[] tmpDirs = new File[numDirs];

        // Create several temporary dirs for processes
        for (int i = 0; i < numDirs; i++) {
            tmpDirs[i] = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID()
                    .toString());
            tmpDirs[i].mkdirs();
        }
        logger.info("Using tmp dirs: " + Arrays.toString(tmpDirs));

        // Start processes
        for (int i = 0; i < numDirs; i++) {
            args = Arrays.asList(tmpDirs[i].toString());
            emuProc.startEmulationProcess(javaPreamble, processExeQuit, args, tmpDirs[i]);
        }

        // Give the processes a chance to create the file
        try {
            // NOTE: Keep this shorter than 3 seconds, otherwise the process
            // will have quit itself
            Thread.sleep(3000);
        }
        catch (InterruptedException e) {
            fail("Thread.sleep exception thrown");
        }
        for (int i = 0; i < numDirs; i++) {
            assertTrue("File " + tmpDirs[i] + "/started.txt does not exist", (new File(tmpDirs[i],
                    "started.txt")).exists());
        }

        // Remove() is called by the process when it exits, after 3 seconds

        // Give the processes a chance to quit
        emuProc.killAll();

        for (int i = 0; i < numDirs; i++) {
            assertFalse("File " + tmpDirs[i] + " not removed", tmpDirs[i].exists());
        }
    }

    @Test
    public void testGetEmulationProcessList() {
        File[] tmpDirs = new File[3];

        // Create several temporary dirs for processes
        for (int i = 0; i < numDirs; i++) {
            tmpDirs[i] = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID()
                    .toString());
            tmpDirs[i].mkdirs();
        }

        logger.info("Using tmp dirs: " + Arrays.toString(tmpDirs));

        // Start processes
        for (int i = 0; i < numDirs; i++) {
            args = Arrays.asList(tmpDirs[i].toString());
            emuProc.startEmulationProcess(javaPreamble, processExeNeverQuit, args, tmpDirs[i]);
        }

        // Give the processes a chance to create the file
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            fail("Thread.sleep exception thrown");
        }
        for (int i = 0; i < numDirs; i++) {
            assertTrue("File " + tmpDirs[i] + "/started.txt does not exist", (new File(tmpDirs[i],
                    "started.txt")).exists());
        }

        Map<Integer, NativeEmulatorRunner> procList = emuProc.getEmulationProcessList();
        assertEquals("Incorrect process list size", numDirs, procList.size());
    }

    @Test
    public void testGetProcessID() {
        File[] tmpDirs = new File[3];

        // Create several temporary dirs for processes
        for (int i = 0; i < numDirs; i++) {
            tmpDirs[i] = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID()
                    .toString());
            tmpDirs[i].mkdirs();
        }

        logger.info("Using tmp dirs: " + Arrays.toString(tmpDirs));

        // Start processes
        for (int i = 0; i < numDirs; i++) {
            args = Arrays.asList(tmpDirs[i].toString());
            emuProc.startEmulationProcess(javaPreamble, processExeNeverQuit, args, tmpDirs[i]);

        }

        // Give the processes a chance to create the file
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            fail("Thread.sleep exception thrown");
        }

        for (int i = 0; i < numDirs; i++) {
            assertEquals("wrong process id", i + 1, (int) emuProc.getProcessID(tmpDirs[i]));
        }
    }
}
