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
package eu.keep.gui.settings;

import java.util.Properties;

public class EFProperty {

	private final String key;
	private String value = "";
	private String description;
	private final PROPERTY_TYPE type;
	
    public static enum PROPERTY_TYPE {STRING, SOFTWARE_ARCHIVE_URL, EMULATOR_ARCHIVE_URL};
    
    // Default Properties
    public static EFProperty softwareArchiveURL = 
    		new EFProperty("software.archive.url", PROPERTY_TYPE.SOFTWARE_ARCHIVE_URL, "Software archive address:");
    public static EFProperty emulatorArchiveURL = 
    		new EFProperty("emulator.archive.url", PROPERTY_TYPE.EMULATOR_ARCHIVE_URL, "Emulator archive address:");
    	
    /**
     * Constructor
     * @param key Property key (as given in user.properties)
     * @param type Type of property
     * @param description User-friendly description
     */
    public EFProperty(String key, PROPERTY_TYPE type, String description) {
    	this.key = key;
    	this.type = type;
    	this.description = description;
    }

    
	/**
	 * Extract the value for this property from a Properties collection
	 * @param properties
	 */
	public void setValue(Properties properties) {
		this.value = properties.getProperty(this.key);
	}

	public String getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}

	public String getDescription() {
		return description;
	}

	public PROPERTY_TYPE getType() {
		return type;
	}
       
}
