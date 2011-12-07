package eu.keep.softwarearchive.wizard.tabs;

import eu.keep.util.Language;

public class LanguageOption {
	
	final String id;
	final String name;
	
	public LanguageOption(Language language) {
		id = language.getLanguageId();
		name = language.getLanguageName();    			
	}
	
	@Override
    public String toString() {
        return name == null ? "" : name;
    }    	
}

