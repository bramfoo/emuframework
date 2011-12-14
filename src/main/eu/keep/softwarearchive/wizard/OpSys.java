package eu.keep.softwarearchive.wizard;

public class OpSys {

    public final boolean newOpSys;
    public final String opsys_id, name, version, description, creator, release_date, license, language_id, reference, user_instructions;

    public OpSys() {
        this(false, null, null, null, null, null, null, null, null, null, null);
    }

    public OpSys(boolean newOpSys, String opsys_id, String name, String version, String description, String creator,
                 String release_date, String license, String language_id, String reference, String user_instructions) {
        this.newOpSys = newOpSys;
        this.opsys_id = opsys_id;
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
        return opsys_id == null;
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
