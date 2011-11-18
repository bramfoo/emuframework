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
*   edonoordermeer at users.sourceforge.net
*   dav_m at users.sourceforge.net
*   bkiers at users.sourceforge.net
* Developed by:
*   Tessella plc <www.tessella.com>
*   Koninklijke Bibliotheek <www.kb.nl>
*   KEEP <www.keep-project.eu>
* Project Title: Core Emulation Framework (Core EF)$ 
*/
package eu.keep.util;

//import eu.keep.emulatorarchive.emulatorpackage.EmuLanguage;
//import eu.keep.softwarearchive.pathway.SwLanguage;

/**
 * Utility class to hold language details.
 * Wraps both EmuLanguage and SwLanguage objects (which come
 * (from EmulatorArchive and SoftwareArchive respectively)
 * @author nooe
 */
public enum Language {

	en("en", "English"),
	nl("nl", "Nederlands"),
	fr("fr", "Français"),
	de("de", "Deutsch");
	
    private final String languageId;
	private final String languageName;

	/**
	 * Construct a basic Language object with a languageId only
	 * @param languageId the languageId
	 */
	private Language(String languageId, String languageName) {
		this.languageId = languageId;
		this.languageName = languageName;
	}
	
//	/**
//	 * Construct a basic Language object with a languageId only
//	 * @param languageId the languageId
//	 */
//	public Language(String languageId) {
//		this.languageId = languageId;
//		this.languageName = null;
//	}
//	
//	/**
//	 * Construct a Language object from an EmuLanguage object
//	 * @param emuLanguage
//	 */
//	public Language(EmuLanguage emuLanguage) {
//		this.languageId = emuLanguage.getLanguageId();
//		this.languageName = emuLanguage.getLanguageName();
//	}
//	
//	/**
//	 * Construct a Language object from an SwLanguage object
//	 * @param emuLanguage
//	 */
//	public Language(SwLanguage swLanguage) {
//		this.languageId = swLanguage.getLanguageId();
//		this.languageName = swLanguage.getLanguageName();
//	}

	public String getLanguageId() {
		return languageId;
	}

	public String getLanguageName() {
		return languageName;
	}

//	/**
//	 * equals method to enable comparing languages. Only the language ID is used for comparison.
//	 * @param otherLanguage
//	 * @return true if both languages have the same languageId, false otherwise
//	 */
//	@Override
//	public boolean equals(Object otherLanguage) {
//		if (otherLanguage instanceof Language) {
//			if (((Language)otherLanguage).getLanguageId().equals(this.languageId) 
//					// Currently test for equality on languageId only. Uncomment following line to include languageName in test.
//					// && otherLanguage.getLanguageName().equalsIgnoreCase(this.languageName)
//				) {
//				return true;
//			} 
//			else {
//				return false;
//			}
//		}
//		else {
//			return false;
//		}
//	}

//	/**
//	 * hashCode method to enable comparing languages. Only the language ID is used for calculating the Hash.
//	 * @param otherLanguage
//	 * @return hashCode based on language Id
//	 */
//	@Override
//	public int hashCode() {
//        final int prime = 31;
//        int hashCode = 1;
//        hashCode = prime * hashCode + ((this.languageId == null) ? 0 : this.languageId.hashCode());
//		// Currently test for equality on languageId only. Uncomment following line to include languageName in test.
//        // hashCode = prime * hashCode + ((this.languageName == null) ? 0 : this.languageName.hashCode());
//        return hashCode;
//	}
	
}
