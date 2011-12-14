package eu.keep.softwarearchive.wizard.model;

public class App {

    public final boolean newApp;
    public final String app_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions;

    public App() {
        this(false, null, null, null, null, null, null, null, null, null, null);
    }

    public App(boolean newApp, String app_id, String name, String version, String description, String creator,
               String release_date, String license, String language_id, String reference, String user_instructions) {
        this.newApp = newApp;
        this.app_id = app_id;
        this.name = name;
        this.version = version;
        this.description = description;
        this.creator = creator;
        this.release_date = release_date;
        this.license = license;
        this.language_id = language_id;
        this.reference = reference;
        this.user_instructions = user_instructions;
    }

    public boolean isDummy() {
        return app_id == null;
    }

    @Override
    public String toString() {
        if(name == null) {
            return "<select>";
        }
        else if(version == null) {
            return name;
        }
        return name + " " + version;
    }
}
