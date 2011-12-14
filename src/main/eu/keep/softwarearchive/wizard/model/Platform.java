package eu.keep.softwarearchive.wizard.model;

public class Platform {

    public final String platform_id, name, description, creator, production_start, production_end, reference;

    public Platform() {
        this(null, null, null, null, null, null, null);
    }

    public Platform(String platform_id, String name, String description, String creator, String production_start, String production_end, String reference) {
        this.platform_id = platform_id;
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.production_start = production_start;
        this.production_end = production_end;
        this.reference = reference;
    }

    public boolean isDummy() {
        return platform_id == null;
    }

    @Override
    public String toString() {
        if(name == null) {
            return "<select>";
        }
        return name;
    }
}
