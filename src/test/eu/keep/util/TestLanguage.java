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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import eu.keep.emulatorarchive.emulatorpackage.EmuLanguage;
import eu.keep.softwarearchive.SwLanguage;

/**
 * A Junit Test class for {@link eu.keep.util.Language }
 * @author Bram Lohman
 * @author David Michel
 */
public class TestLanguage {

	private EmuLanguage emuLanguage1;	
	private SwLanguage swLanguage2;
	private SwLanguage swLanguage3;
	private SwLanguage swLanguage4;
	
	private Language language1;
	private Language language2;
	private Language language3;
	private Language language4;
	
	@Before
	public void setUp() {
    	emuLanguage1 = new EmuLanguage();
    	emuLanguage1.setLanguageId("nl");
    	emuLanguage1.setLanguageName("Nederlands");   	
    	language1 = new Language(emuLanguage1);  	
    	
    	swLanguage2 = new SwLanguage();
    	swLanguage2.setLanguageId("nl");
    	swLanguage2.setLanguageName("Nederlands");
    	language2 = new Language(swLanguage2);
    	
    	swLanguage3 = new SwLanguage();
    	swLanguage3.setLanguageId("nl");
    	swLanguage3.setLanguageName("Dutch");
    	language3 = new Language(swLanguage3);
    	
    	swLanguage4 = new SwLanguage();
    	swLanguage4.setLanguageId("du");
    	swLanguage4.setLanguageName("Nederlands");
    	language4 = new Language(swLanguage4);		
	}
	
    @Test
    public void testConstructFromEmuLanguage()  {
    	assertEquals("Language should have same ID as EmuLanguage is was constructed from", 
    			emuLanguage1.getLanguageId(), language1.getLanguageId());
    	assertEquals("Language should have same Name as EmuLanguage is was constructed from", 
    			emuLanguage1.getLanguageName(), language1.getLanguageName());    	    	
    }
	
    @Test
    public void testConstructFromSwLanguage()  {
    	assertEquals("Language should have same ID as SwLanguage is was constructed from", 
    			swLanguage2.getLanguageId(), language2.getLanguageId());
    	assertEquals("Language should have same Name as SwLanguage is was constructed from", 
    			swLanguage2.getLanguageName(), language2.getLanguageName());    	    	
    }
	
    @Test
    public void testEqualsBasedOnLanguageId() {
    	assertEquals("Languages with equal Id are considered equal", language1, language2);
    	assertEquals("Languages with equal Id are considered equal, even if Name is different", language1, language3);
    	assertFalse("Languages with unequal Id are considered unequal, even if Name is equal", language1.equals(language4));
    	assertFalse("Languages with unequal Id are considered unequal", language3.equals(language4));
    }
    
    @Test
    public void testHashCodeBasedOnLanguageId() {
    	assertEquals("Languages with equal Id are considered equal", language1.hashCode(), language2.hashCode());
    	assertEquals("Languages with equal Id are considered equal, even if Name is different", language1.hashCode(), language3.hashCode());
    	assertFalse("Languages with unequal Id are considered unequal, even if Name is equal", language1.hashCode() == language4.hashCode());
    	assertFalse("Languages with unequal Id are considered unequal", language3.hashCode() == language4.hashCode());    	
    }
    
    @Test
    public void testSetOperationsUseLanguageIdForEquality() {
    	Set<Language> languageSet = new HashSet<Language>();
    	
    	// Try to add all four languages
    	languageSet.add(language1);
    	languageSet.add(language2);
    	languageSet.add(language3);
    	languageSet.add(language4);
    	
    	// HashSet will not add element if it thinks it is already present, and
    	// it uses the .equals() and .hashCode() methods to determine this.
    	// Since those two methods are based on languageId only, we expect only 
    	// language1 and language4 to be present.
    	assertEquals("Only two languages with different Id expected in Set", 2, languageSet.size());

    	// The contains() method will again use the .equals() and .hashCode() methods 
    	// to determine whether or not a given element is present in the Set.
    	// The Set now contains language1 and language4, i.e. languages with Id="nl" and Id="du".
    	// Therefore, the contains() method should return true for all 4 input languages.
    	assertTrue("Set should contain language with same Id as language1", languageSet.contains(language1));
    	assertTrue("Set should contain language with same Id as language2", languageSet.contains(language2));
    	assertTrue("Set should contain language with same Id as language3", languageSet.contains(language3));
    	assertTrue("Set should contain language with same Id as language4", languageSet.contains(language4));
    	
    	EmuLanguage emuLanguage5 = new EmuLanguage();
    	emuLanguage5.setLanguageId("en");
    	emuLanguage5.setLanguageName("english");
    	Language language5 = new Language(emuLanguage5);
    	assertFalse("Set should not contain language with same Id as language5", languageSet.contains(language5));
    	
    	// Test that really only language1 and language4 were actually added
    	for (Language language : languageSet) {
    		if (language.getLanguageId().equals(language1.getLanguageId())) {
        		assertTrue("Set should contain input language1", language.getLanguageName().equals(language1.getLanguageName()));   			
    		} 
    		else if (language.getLanguageId().equals(language4.getLanguageId())) {
            	assertTrue("Set should contain input language4", language.getLanguageName().equals(language4.getLanguageName()));   			
    		}	
    		else {
    			fail("Set should not contain other elements than input language1 and language4");
    		}
    	}
    }
}