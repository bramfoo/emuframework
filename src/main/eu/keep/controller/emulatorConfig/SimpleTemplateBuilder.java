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
package eu.keep.controller.emulatorConfig;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Class to build an emulator's configuration using a simple template
 * Freemarker template implementation that creates the following template sections:
 * - preamble
 * - body
 * - postscript
 * It is based on the following data model:
 * (root)
 *   |
 *   +- digobj     = "/path/to/file/file.ext"
 *   +- configDir  = "/path/to/file/"
 *   +- configFile = "file.ext"
 *   |
 *   +- floppy (sequence)
 *   |   |
 *   |   +- type = "3.5_144" (key into driveTypes hash, defined in template) 
 *   |   +- num = "0" (index into driveLetter hash, defined in template)
 *   |   +- digobj = "file://path/to/file/obj.ext" (may be the same as the root digobj)
 *   |   +- inserted = "true/false"
 *   |
 *   +- fixeddisk (sequence)
 *       |
 *       +- master = "true/false" 
 *       +- index = "0" (index into driveLetter hash, defined in template)
 *       +- enabled = "true/false"
 *       +- swImg = "/path/to/file/file.ext"
 *       +- cylinders = "0"
 *       +- heads = "4"
 *       +- sectorsPerTrack = "17"
 *
 * These variables should be used to generate a EOL-line separated configuration for
 * a specific emulator, e.g.:
 * 
 * --Section: preamble--
 * --Section: body--
 * -autostart
 * IKPlus.d64
 * -drive8type
 * 1541
 * -8
 * IKPlus.d64
 * +truedrive
 * -drive9type
 * 1571
 * -9
 * IKPlus.d64.2
 * -truedrive
 * --Section: postscript--
 * 
 * The different sections in the configuration file are used to guarantee order for specific
 * configuration options (such as XML headers/footers, command line arguments, etc.)
 * @author Bram Lohman
 *
 */
public class SimpleTemplateBuilder implements TemplateBuilder {

    private static final Logger logger = Logger.getLogger(SimpleTemplateBuilder.class.getName());

    private final String SECTION_SEPARATOR = "##Section: ";
    private final String SEPARATOR_END = "##" + "\n"; // Note that this uses a linebreak for Strings, not file(s) -- latter being OS dependant! 

    
    Configuration cfg;
    Template templ;
    
    public SimpleTemplateBuilder(File emulatorDir) throws IOException
    {
        // Template configuration
        cfg = new Configuration();
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        logger.debug("Templates loaded from: " + emulatorDir);
        cfg.setDirectoryForTemplateLoading(emulatorDir);
    }

    @Override
    public void loadTemplate(String templateName) throws IOException {
        templ = cfg.getTemplate(templateName);
    }

    @Override
    public Map<String, Map<String, String>> getTemplateArgs() throws IOException {
        // Return the data model, defined in the ftl tag
        Map<String, Map<String, String>> dataModel = new HashMap<String, Map<String, String>>();

        // Ensure a template is loaded
        if (templ == null)
        	throw new IOException("No template loaded");
        
        logger.debug("Generating template arguments");
        // Root node
        Map<String, String> root = new HashMap<String, String>();
        String[] names = templ.getCustomAttributeNames();
        logger.debug("Custom attributes in template: " + Arrays.toString(names));
        // Loop over the attributes, checking types and adding to the datamodel
        for (String attribName: names)
        {
            Object attrib = templ.getCustomAttribute(attribName);
            if (attrib instanceof String)
            {
                // Add it to the root node
                logger.debug("Adding " + attribName + " to 'root' node");
                root.put(attribName, attrib.toString());
            }
            else if (attrib instanceof HashMap<?, ?>)
            {
                // Create a separate Map for it
                HashMap<?, ?> attribMap = (HashMap<?, ?>) attrib;
                Map<String, String> dmMap = new HashMap<String, String>();
                for (Object key : attribMap.keySet())
                {
                        dmMap.put((String)key, (String) attribMap.get(key));
                }
                dataModel.put(attribName, dmMap);
                logger.debug("Adding attribute " + attribName + " (map: " + dmMap + ") to 'root' node");
            }
        }
        dataModel.put("root", root);
        return dataModel;
    }

    @Override
    public List<String> generateConfig(Map<String, List<Map<String, String>>> params)
            throws IOException {

        logger.debug("Generating config using: " + params);
        List<String> config = new ArrayList<String>();
        
        // Ensure a template is loaded
        if (templ == null)
        	throw new IOException("No template loaded");

        // Sanity test: check if all the template arguments are present
        List<String> missingArgs = allArgumentsPresent(params); 
        if (!missingArgs.isEmpty())
        	throw new IOException("Required argument(s) not present: " + missingArgs);
        
        // Create the data model based on the input
        Map<String, Object> root = new HashMap<String, Object>();
        
        // For the root there should only be one item in each list
        for (Map<String, String> rootVars : params.get("root"))
        {
            for(String var : rootVars.keySet())
            {
                logger.debug("Adding [" + var + ":" + rootVars.get(var) + "] to 'root' node");
                root.put(var, rootVars.get(var));
            }
        }
        
        // The remaining parameters can be passed through to the datamodel as lists
        for (String key : params.keySet())
        {
            if (key.equals("root"))
            {
                // Skip the root, as we've already handled it
                continue;
            }
            logger.debug("Adding " + params.get(key) + " to '" + key + "' node");
            root.put(key, params.get(key));
        }
        
        // Write the complete template
        Writer out = new StringWriter();
        try {
            templ.process(root, out);
        }
        catch (TemplateException e) {
            logger.error("Error generating configuration: " + e.toString());
            return config;
        }
        out.flush();
        
        // Parse the output into a list, based on the [preamble, body, postscript] separators
        String output = out.toString();
        
        logger.debug("Configuration: " + output);
        int preambleStart = output.indexOf(SECTION_SEPARATOR);
        int preambleSepEnd = output.indexOf(SEPARATOR_END, preambleStart + 1) + SEPARATOR_END.length();
        logger.debug("Preamble separator found: " + preambleStart + ";" + preambleSepEnd);
        int bodyStart = output.indexOf(SECTION_SEPARATOR, preambleStart + 1);
        int bodySepEnd = output.indexOf(SEPARATOR_END, bodyStart + 1);
        bodySepEnd = output.indexOf(SEPARATOR_END, bodyStart + 1) + SEPARATOR_END.length();
        logger.debug("Body separator found: " + bodyStart + ";" + bodySepEnd);
        int postscriptStart = output.indexOf(SECTION_SEPARATOR, bodyStart + 1);
        int postscriptSepEnd = output.indexOf(SEPARATOR_END, postscriptStart + 1) + SEPARATOR_END.length();
        postscriptSepEnd = output.indexOf(SEPARATOR_END, postscriptStart + 1) + SEPARATOR_END.length();
        logger.debug("Postscript separator found: " + postscriptStart + ";" + postscriptSepEnd);
        
        // Remove section separators
        config.add(output.substring(preambleSepEnd, bodyStart));
        config.add(output.substring(bodySepEnd, postscriptStart));
        config.add(output.substring(postscriptSepEnd, output.length()));
        
        return config;
    }

    /** 
     * Checks if all required arguments are present
     * @param params Custom parameters for the template file
     * @return List<String> List of missing parameters
     */
	private List<String> allArgumentsPresent(Map<String, List<Map<String, String>>> params) throws IOException {
		List<String> missingArgs = new ArrayList<String>();
		
		// Required arguments
		Map<String, Map<String, String>> reqArgs = getTemplateArgs();
		
		// Loop over the required arguments and check for missing ones
		for (String key : reqArgs.keySet())
		{
			if (params.containsKey(key))
			{
				// Loop over inner map
				for (String innerKey : reqArgs.get(key).keySet())
				{
					// Loop over each of the lists in the params
					for (Map<String, String> paramMap : params.get(key))
					{
						if (!paramMap.containsKey(innerKey))
						{
							missingArgs.add(key.toString() + "->" + innerKey.toString());
						}
					}
				}
			}
			else
			{
				// Add all the inner keys to the missing list
				for (String innerKey : reqArgs.get(key).keySet())
				{
					missingArgs.add(key.toString() + "->" + innerKey.toString());
				}
			}
		}
		
		return missingArgs;
	}
}
