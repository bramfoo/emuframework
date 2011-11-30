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
