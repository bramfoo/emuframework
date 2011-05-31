package eu.keep.downloader.db;

/**
 * Class representing the Registry table in the local database. Used for queries relating to 
 * registries in the local database
 * @author Bram Lohman
 *
 */
public class DBRegistry {

    private String name;
    private String url;
    private String className;
    private String translationView;
    private boolean enabled;
    private String description;
    private String comment;
    private int registryID;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getTranslationView() {
		return translationView;
	}
	public void setTranslationView(String translationView) {
		this.translationView = translationView;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getRegistryID() {
		return registryID;
	}
	public void setRegistryID(int registryID) {
		this.registryID = registryID;
	}

    public String toString() {
    	return "[" + registryID  + "]" + ":" + name;
    }
}
