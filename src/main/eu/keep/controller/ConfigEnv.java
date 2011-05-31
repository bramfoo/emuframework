/*
* $Revision: 463 $ $Date: 2010-08-16 14:30:28 +0200 (Mon, 16 Aug 2010) $
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.keep.controller.emulatorConfig.TemplateBuilder;
import eu.keep.softwarearchive.pathway.Pathway;

/**
 * Holds information relevant for the emulator configuration environment
 * @author Bram Lohman
 *
 */
public class ConfigEnv {

    private File emuDir;
    private File emuExec;
    private TemplateBuilder templateBuilder;
    private boolean cliTemplate;
    private boolean xmlTemplate;
    private boolean propsTemplate;
    private Pathway pathway;
    
	private Map<String, List<Map<String, String>>> options;

    public ConfigEnv(File emuDir, File emuExec, TemplateBuilder tb, boolean cliTemplate, boolean xmlTemplate, boolean propsTemplate, Pathway pathway) {
        this.emuDir = emuDir;
        this.emuExec = emuExec;
        this.templateBuilder = tb;
        this.cliTemplate = cliTemplate;
        this.xmlTemplate = xmlTemplate;
        this.propsTemplate = propsTemplate;
        this.pathway = pathway;
        options = new HashMap<String, List<Map<String, String>>>();
    }

    public File getEmuExec() {
        return emuExec;
    }

    public TemplateBuilder getTemplateBuilder() {
        return templateBuilder;
    }

    public File getEmuDir() {
        return emuDir;
    }

    public void setOptions(Map<String, List<Map<String, String>>> options) {
        this.options = options;
    }

    public Map<String, List<Map<String, String>>> getOptions() {
        return options;
    }
    
    public boolean hasCliTemplate() {
        return cliTemplate;
    }

    public boolean hasXmlTemplate() {
        return xmlTemplate;
    }

    public boolean hasPropsTemplate() {
        return propsTemplate;
    }

    public Pathway getPathway() {
		return pathway;
	}
}
