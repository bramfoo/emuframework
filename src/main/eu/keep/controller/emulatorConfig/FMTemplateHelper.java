/*
* $Revision: $ $Date: $
* $Author: $
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
package eu.keep.controller.emulatorConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.keep.controller.ConfigEnv;
import eu.keep.controller.emulatorConfig.TemplateBuilder;
import eu.keep.softwarearchive.pathway.Pathway;
import eu.keep.util.DiskUtilities;
import eu.keep.util.FloppyDiskImage;
import eu.keep.util.FloppyDiskType;
import eu.keep.util.VariableFixedDiskImage;

/**
 * Template helper class for Freemarker implementation.
 * Contains general template functionality
 * @author Bram Lohman
 *
 */
public class FMTemplateHelper {

    private static final Logger logger = Logger.getLogger(FMTemplateHelper.class.getName());
    
    public final static String CLI_TEMPLATE_FILE = "templateCLI.ftl"; 
    public final static String XML_TEMPLATE_FILE = "templateXML.ftl";
    public final static String PROPS_TEMPLATE_FILE = "templateProps.ftl";
    
    public final static String XML_CONFIG_FILE = "config.xml";
    public final static String PROPS_CONFIG_FILE = "config.props";


    /**
     * Create the ConfigEnv object for the emulator
     * @param emuDir The emulator's temporary executable directory
     * @param emuExec The emulator executable
     * @param pathway The pathway that is the basis for this configuration
     * @return An initialised ConfigEnv object
     * @throws IOException 
     */
    public ConfigEnv createConfigEnv(File emuDir, File emuExec, Pathway pathway) throws IOException {
        
        ConfigEnv conf;
        
        // Check for template files
        boolean cliTempl = true;
        boolean xmlTempl = true;
        boolean propsTempl = true;
        File cliTemplate = new File(emuDir, CLI_TEMPLATE_FILE);
        if (!cliTemplate.exists())
        {
            logger.warn("No CLI template config file found!");
            cliTempl = false;
        }
        File xmlTemplate = new File(emuDir, XML_TEMPLATE_FILE);
        if (!xmlTemplate.exists())
        {
            logger.info("Emulator has no XML template config file");
            xmlTempl = false;
        }
        File propsTemplate = new File(emuDir, PROPS_TEMPLATE_FILE);
        if (!propsTemplate.exists())
        {
            logger.info("Emulator has no properties template config file");
            propsTempl = false;
        }

        // TODO: Choose between simple/complex builders
        TemplateBuilder tb;
        try {
            tb = new SimpleTemplateBuilder(emuDir);
        }
        catch (IOException ioe) {
            logger.error("Error creating simple template builder: " + ioe.toString());
            throw ioe;
        }

        logger.debug("Creating configuration -- dir: " + emuDir + "; exec: " + emuExec + "; templates: " + (cliTempl ? cliTemplate : "No CLI template") + "; " + (xmlTempl ? xmlTemplate : "No XML template") + "; " + (propsTempl ? propsTemplate : "No props template"));
        conf = new ConfigEnv(emuDir, emuExec, tb, cliTempl, xmlTempl, propsTempl, pathway);
        
        return conf;
    }
    
    /**
     * Generate a default configuration for an emulator
     * @param env The ConfigEnv environment containing the environment information
     * @param digObjs A list of digital objects to use in the configuration
     * @param softImg A list of software images to use in the configuration
     * @return A Map containing a default configuration, based on the provided environment and files
     * @throws IOException If an error occurs while loading a template
     */
    public Map<String, List<Map<String, String>>> generateDefaults(ConfigEnv env, List<File> digObjs, List<File> softImg) throws IOException
    {
        Map<String, List<Map<String, String>>> defaultOpts = new HashMap<String, List<Map<String, String>>>(); 
        
        TemplateBuilder tb = env.getTemplateBuilder();
        File emuDir = env.getEmuDir();
        Pathway pw = env.getPathway();
        // Create a list of images that need wrapping
        List<File> unwrappedSoftImg = new ArrayList<File>();

        // Generate default options
        // Load a template to access the attributes (initialise for getTemplateArgs)
        try {
            tb.loadTemplate(CLI_TEMPLATE_FILE);
        }
        catch (IOException ioe) {
            logger.error("Error loading template: " + ioe.toString());
            throw ioe;
        }
        
        // Note that all generic options get wrapped in a list to fit in the template setup (supporting multiple floppy/fixed disks)
            logger.info("Creating default emulator configuration");

            List<Map<String, String>> r = new ArrayList<Map<String, String>>();
            Map<String, String> root = createRoot(tb, digObjs, env, emuDir);
            r.add(root);
            defaultOpts.put("root", r);
            logger.debug("Root: " + r);
            
            // TODO: Split into separate methods; only assign fields defined in getTemplateArgs()
            // Floppy disks (type, num, digobj, inserted)
            logger.debug("Generating floppy disks: " + digObjs);
            List<Map<String, String>> f = new ArrayList<Map<String, String>>();
            int numDisk = 0;
            for (File digObj : digObjs)
            {
                Map<String, String> floppy = tb.getTemplateArgs().get("floppyDisks");
                floppy.put("inserted", "true");
                floppy.put("num", (new Integer(numDisk)).toString());

                // Determine floppy image needed
                if (pw.getHardwarePlatform().getName().contains("x86"))
                {
                    logger.debug("Customizing floppy disk for x86");
                    // x86, so wrap the object in a disk if it fits
                    if (digObj.length() < FloppyDiskImage.MAX_FILE_SIZE)
                    {
                        FloppyDiskImage fdi = new FloppyDiskImage();
                        File diskfile = new File(emuDir, "floppy_" + numDisk + ".img");
                        if (pw.getObjectFormat().getId().equalsIgnoreCase("FFT-1015"))
                        {
                        	// Special case for autostarting WP5.1 with a given filename
                        	fdi.injectDigitalObject(diskfile, digObj, "DOC.WPS");
                        	logger.debug("Adding specific floppy disk customisation for file format FFT-1015");
                        }
                        else {
                        	fdi.injectDigitalObject(diskfile, digObj);
                        }

                        floppy.put("digobj", diskfile.toString());
                        floppy.put("type", FloppyDiskType.getDiskType(diskfile).name());
                        f.add(floppy);
                    }
                    else
                    {
                        // Add it to the fixed-disk list
                        logger.debug("Marking digital object as requiring fixed-disk wrapping: " + digObj);
                        unwrappedSoftImg.add(digObj);
                    }
                }
                else
                {
                    floppy.put("digobj", digObj.getAbsolutePath());
                    floppy.put("type", FloppyDiskType.getDiskType(digObj).name());
                    f.add(floppy);
                }

                numDisk++;
            }
            logger.debug("floppyDisks: " + f);
            defaultOpts.put("floppyDisks", f);

            
            // Fixed disks (master, index, enabled, swImg, cylinders, heads, sectorsPerTrack)
            logger.debug("Generating fixed disks for: " + softImg + " and " + unwrappedSoftImg);
            numDisk = 0;
            List<Map<String, String>> h = new ArrayList<Map<String, String>>();
            for (File swImg : softImg)
            {
                logger.debug("Generating fixed disk config for: " + swImg);
                Map<String, String> fixed = tb.getTemplateArgs().get("fixedDisks");
                fixed.put("enabled", "true");
                fixed.put("master", numDisk == 0 ? "true" : "false" );
                fixed.put("index", (new Integer(numDisk)).toString());
                fixed.put("swImg", swImg.getPath());
                if (pw.getHardwarePlatform().getName().contains("x86"))
                {
	                List<Integer> chs = DiskUtilities.determineCHS(swImg);
	                logger.debug("Fixed disk config: " + chs.toString());
	                fixed.put("cylinders", chs.get(0).toString());
	                fixed.put("heads", chs.get(1).toString());
	                fixed.put("sectorsPerTrack", chs.get(2).toString());
                }
                else
                {
	                fixed.put("cylinders", "0");
	                fixed.put("heads", "0");
	                fixed.put("sectorsPerTrack", "0");
                }
                logger.debug("Adding fixed disk config: " + fixed);
                h.add(fixed);
                numDisk++;
            }
            // Now loop over the unwrapped digital objects
            for (File unwrappedImg : unwrappedSoftImg)
            {
                if (!DiskUtilities.isISO9660(unwrappedImg))
                {
                    logger.debug("Generating fixed disk image for: " + unwrappedImg);
                    Map<String, String> unwrapped = new HashMap<String, String>();
                    unwrapped.put("enabled", "true");
                    unwrapped.put("master", numDisk == 0 ? "true" : "false" );
                    unwrapped.put("index", (new Integer(numDisk)).toString());

                    // Wrap it in a image
                    File diskfile = new File(emuDir, "fixed_" + numDisk + ".img");
                    VariableFixedDiskImage vfdi = new VariableFixedDiskImage();
                    int vfdiSize = ((int) Math.ceil(unwrappedImg.length() / ((double) 1024*1000*10))) * 10; // Round to nearest 10MB
                    vfdiSize = Math.max(vfdiSize, 20); // Fixed disk should be at least 16MB, rounded up to 20MB
                    logger.debug("Creating fixed disk of " + vfdiSize + "MB to hold d.o. of size " + unwrappedImg.length() + " (~ " + unwrappedImg.length() / ((double)1024*1000) +"MB)");
                    vfdi.injectDigitalObject(diskfile, unwrappedImg, vfdiSize);
                    
                    unwrapped.put("swImg", diskfile.toString());
                    List<Integer> chs = DiskUtilities.determineCHS(diskfile);
                    logger.debug("Fixed disk config: " + chs.toString());
                    unwrapped.put("cylinders", chs.get(0).toString());
                    unwrapped.put("heads", chs.get(1).toString());
                    unwrapped.put("sectorsPerTrack", chs.get(2).toString());
                    logger.debug("Adding fixed disk config: " + unwrapped);
                    h.add(unwrapped);
                }
                else
                {
                    // Attach it as an optical disk
                    logger.debug("Attaching digital object as (unwrapped) ISO9660 optical disk: " + unwrappedImg);
                    Map<String, String> optical = new HashMap<String, String>();
                    optical.put("enabled", "true");
                    optical.put("master", "false" );
                    optical.put("index", "2");
                    optical.put("swImg", unwrappedImg.toString());
                    optical.put("cylinders", "0");
                    optical.put("heads", "255");
                    optical.put("sectorsPerTrack", "63");
                    logger.debug("Adding fixed disk config: " + optical);
                    h.add(optical);
                }
            }
            logger.debug("fixedDisks: " + h);
            defaultOpts.put("fixedDisks", h);
            
            logger.debug("Default config: " + defaultOpts);
        
        return defaultOpts;
    }
    
    private Map<String, String> createRoot(TemplateBuilder tb, List<File> digObjs, ConfigEnv env, File emuDir) throws IOException {
        // Root (digobj, configDir, configFile)
        Map<String, String> root = tb.getTemplateArgs().get("root");
        // Only put in those that are necessary
        if (root.containsKey("digobj"))
        {
        	root.put("digobj", digObjs.get(0).getAbsolutePath());
        }
        if (root.containsKey("configFile"))
        {
            if (env.hasXmlTemplate())
                root.put("configFile", new File(XML_CONFIG_FILE).toString());
            else if (env.hasPropsTemplate())
                root.put("configFile", new File(PROPS_CONFIG_FILE).toString());
            else
                root.put("configFile", new File("noConfFileDefined").toString());
        }
        if (root.containsKey("configDir"))
        {
        	root.put("configDir", emuDir.toString());
        }
        
        return root;
	}

	/**
     * Generates the command line string for the emulator
     * @param env The emulator configuration environment
     * @return A list of arguments consisting of the preamble, body, and postscript arguments. Intended
     * to be used as >'preamble' executable 'body' 'postscript' when generating the final command line
     * @throws IOException 
     */
    public List<List<String>> createCLI(ConfigEnv env) throws IOException
    {
        String newLine = "\n"; // Note that this uses a linebreak for Strings, not File(s) -- latter being OS dependant!
        
        logger.debug("Generating CLI config");
        List<String> config = new ArrayList<String>();
        List<List<String>> configParts = new ArrayList<List<String>>();
 
        TemplateBuilder tb = env.getTemplateBuilder();

        tb.loadTemplate(CLI_TEMPLATE_FILE);
        config = tb.generateConfig(env.getOptions());
        logger.debug("CLI args [pre, body, post]: " + config);

        // Split the config into the three sections (preamble, body, postscript)
        List<String> preamble = new ArrayList<String>();
        for (String str : config.get(0).split(newLine))
        { 
            logger.debug("Adding CLI preamble args: " + str);
            if (!str.isEmpty())
                preamble.add(str);
        }
        configParts.add(preamble);
        
        List<String> body = new ArrayList<String>();
        for (String str : config.get(1).split(newLine))
        { 
            logger.debug("Adding CLI body args: " + str);
            if (!str.isEmpty())
                body.add(str);
        }
        configParts.add(body);
        
        List<String> postscript = new ArrayList<String>();
        for (String str : config.get(2).split(newLine))
        { 
            logger.debug("Adding CLI postscript args: " + str);
            if (!str.isEmpty())
                postscript.add(str);
        }
        configParts.add(postscript);
        
        return configParts;
    }
}
