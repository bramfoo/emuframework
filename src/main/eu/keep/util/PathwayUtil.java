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
package eu.keep.util;

import eu.keep.softwarearchive.pathway.Pathway;

/**
 * Util class to create a String representation of a Pathway object
 */
public class PathwayUtil {

	public static String pathwayToString(Pathway pw) {
		
    	StringBuilder stringBuilder = new StringBuilder();    			
    	stringBuilder.append(pw.getObjectFormat() == null ? "N/A" : pw.getObjectFormat().getId());
    	stringBuilder.append(" --> ");
    	stringBuilder.append(pw.getApplication() == null ? "N/A" : pw.getApplication().getId());
    	stringBuilder.append(" --> ");    	
    	stringBuilder.append(pw.getOperatingSystem() == null ? "N/A" : pw.getOperatingSystem().getId());
    	stringBuilder.append(" --> ");
    	stringBuilder.append(pw.getHardwarePlatform() == null ? "N/A" : pw.getHardwarePlatform().getId());

    	return stringBuilder.toString();
	}
	
}
