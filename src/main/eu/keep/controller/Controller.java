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

package eu.keep.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.keep.controller.emulatorConfig.FMTemplateHelper;
import eu.keep.controller.emulatorConfig.TemplateBuilder;
import eu.keep.controller.emulatorRunner.EmulatorProcessManager;
import eu.keep.softwarearchive.pathway.Pathway;

/**
 * Entry point for the {@code Controller} package. This class contains
 * methods to configure and run emulators
 * 
 * @author Bram Lohman
 * @author David Michel
 */
public class Controller {

    private static final Logger logger = Logger.getLogger(Controller.class.getName());
    private Map<Integer, ConfigEnv> envs;
    private EmulatorProcessManager      emulatorProcessManager;

    
    public Controller(){
        emulatorProcessManager = new EmulatorProcessManager();
        envs = new HashMap<Integer, ConfigEnv>();
    }

    /**
     * Check if configuration is complete/valid or not
     * @return true if configuration is complete/valid, false otherwise
     */
    private boolean isConfigurationComplete(Integer conf) {

        boolean result = true;

        // Sanity check
        if (!envs.containsKey(conf))
        {
            logger.error("Emulator configuration is invalid/incomplete. Cannot check configuration.");
            return false;
        }
        ConfigEnv env = envs.get(conf);

            if (env.getEmuDir() == null | !env.getEmuDir().exists() || !env.getEmuDir().isDirectory()) {
                logger.error("Emulator temporary directory " + env.getEmuDir().getPath()
                        + " does not exists/is not a directory");
                result = false;
            }

            if (env.getEmuExec() == null | !env.getEmuExec().exists() ) {
                logger.error("Emulator executable " + env.getEmuExec().getPath() + " does not exist/cannot be executed");
                result = false;
            }

        	if (env.getTemplateBuilder() == null) {
                logger.error("Emulator configuration builder director undefined");
                result = false;
            }

        	if (env.getPathway() == null) {
                logger.error("Emulator pathway undefined");
                result = false;
            }
        	
        	if (env.getOptions() == null) {
                logger.error("Emulator options undefined");
                result = false;
            }
        return result;
    }

	/**
	 * Return the hardware configuration options for an emulator 
	 * @param conf Configuration environment set up for an emulator
	 * @return Hardware configuration options, according to the Template model
	 * @throws IOException If a configuration environment cannot be found or is not implemented properly
	 */
    public Map<String, List<Map<String, String>>> getEmuConfig(Integer conf) throws IOException {

        Map<String, List<Map<String, String>>> opts;

        // Sanity check
        if (!envs.containsKey(conf))
        {
            logger.error("Emulator configuration is invalid/incomplete. Cannot get emulation options.");
            throw new IOException("Emulator configuration is invalid/incomplete. Cannot get emulation options.");
        }

        ConfigEnv env = envs.get(conf);
        opts = env.getOptions();
        
        return opts;
    }

	/**
	 * Set the hardware configuration options for an emulator 
	 * @param options Hardware configuration options, according to the Template model
	 * @param conf Configuration environment set up for an emulator
	 * @throws IOException If a configuration environment cannot be found or is not implemented properly
	 */
    public void setEmuConfig(Map<String, List<Map<String, String>>> options, Integer conf) throws IOException{

        // Sanity check
        if (!envs.containsKey(conf))
        {
            logger.error("Emulator configuration is invalid/incomplete. Cannot set emulation options.");
            throw new IOException("Emulator configuration is invalid/incomplete. Cannot set emulation options.");
        }
        ConfigEnv env = envs.get(conf);

        // set the options back in the configuration
        env.setOptions(options);
    }

    /**
	 * Prepare the emulator configuration environment 
     * @param emuDir Directory where emulation process will be run
     * @param digObjs Digital objects for which the environment is created
     * @param swImgs Software images which will be run in the environment
     * @param emuExec Emulator executable
     * @param pathway Pathway describing software/hardware stack for the environment 
     * @return Configuration identifier
     * @throws IOException If an error occurs during the creation of the Configuration Environment 
     */
    public Integer prepareConfiguration(File emuDir, List<File> digObjs, List<File> swImgs, File emuExec, Pathway pathway) throws IOException {
        
        // Create a new configuration in the map
        Integer envNum = envs.size() + 1;
        logger.info("Creating new configuration [" + envNum + "]");

        FMTemplateHelper templHelp = new FMTemplateHelper();
       	ConfigEnv env = templHelp.createConfigEnv(emuDir, emuExec, pathway);

        Map<String, List<Map<String, String>>> defaultOpts = new HashMap<String, List<Map<String, String>>>();
        
        defaultOpts = templHelp.generateDefaults(env, digObjs, swImgs);
        env.setOptions(defaultOpts);

        // Add the environment to the map
        envs.put(envNum, env);

        return envNum;
        }
    
    /**
     * Run the chosen emulation process
     * An emulator must have already been selected and its configuration
     * settings worked out.
     * @param conf Integer representing an existing configuration
     * @throws IOException If the configuration environment is not set up properly 
     */
    public void runEmulationProcess(Integer conf) throws IOException {

        // Sanity check
        if (!envs.containsKey(conf))
        {
            logger.error("Emulator configuration is invalid/incomplete. Cannot set emulation options.");
            throw new IOException("Emulator configuration is invalid/incomplete. Cannot set emulation options.");
        }
        logger.debug("Retrieving config " + conf);
        ConfigEnv env = envs.get(conf);
        FMTemplateHelper templHelp = new FMTemplateHelper();

        logger.info("Finalising emulator configuration [" + conf + "]");
        // Write XML file to emulator directory
        logger.debug("Checking for XML template: " + env.hasXmlTemplate());
        if (env.hasXmlTemplate())
        {
            writeConfigFile(env.getEmuDir(), FMTemplateHelper.XML_CONFIG_FILE, FMTemplateHelper.XML_TEMPLATE_FILE, env);
        }

        // Write properties file to emulator directory
        logger.debug("Checking for properties template: " + env.hasPropsTemplate());
        if (env.hasPropsTemplate())
        {
            writeConfigFile(env.getEmuDir(), FMTemplateHelper.PROPS_CONFIG_FILE, FMTemplateHelper.PROPS_TEMPLATE_FILE, env);
        }
    	
        // Sanity check
        if (!isConfigurationComplete(conf))
        {
            logger.error("Emulator configuration is invalid/incomplete. Cannot run emulation process.");
            throw new IOException("Emulator configuration is invalid/incomplete. Cannot run emulation process.");
        }

        List<List<String>> config = templHelp.createCLI(env);

        // Start the emulation process using the process manager
        emulatorProcessManager.startEmulationProcess(config.get(0), env.getEmuExec(), config.get(1), env.getEmuDir());

        logger.debug("Emulation process terminated");
    }

    /**
     * Helper function to write a configuration file to disk
     * @param targetDir Target directory for configuration file
     * @param fileName Name of configuration file
     * @param templateName Template used to generate configuration
     * @param env The emulator configuration environment
     */
    private void writeConfigFile(File targetDir, String fileName, String templateName, ConfigEnv env)
    {
        TemplateBuilder tb = env.getTemplateBuilder();
        
        File config = new File(targetDir, fileName);
        logger.debug("Generating config file: " + config);

        try {
            tb.loadTemplate(templateName);
            if (!config.exists())
            {
                config.createNewFile();
            }
            
            logger.debug("Writing config file to disk: " + config);
            FileWriter out = new FileWriter(config);
            String conf = new String();
            for (String s : tb.generateConfig(env.getOptions()))
            {
                conf += s;
            }

            logger.debug("Generated config string: " + conf);
            out.write(conf);
            out.close();
        }
        catch(IOException e)
        {
            logger.fatal("Error writing config: " + e);
        }
    }

    
    /**
     * Stop all running emulator processes
     */
    public void stopProcesses() {
        // Kill all currently running emulator instances
        emulatorProcessManager.killAll();
    }
}
