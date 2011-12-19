package eu.keep.gui.wizard.ea.model;

public class Hardware {

    public final String hardware_id, name;

    public Hardware() {
        this(null, null);
    }

    public Hardware(String imageformat_id, String name) {
        this.hardware_id = imageformat_id;
        this.name = name;
    }

    public boolean isDummy() {
        return hardware_id == null;
    }

    @Override
    public String toString() {
        return name == null ? " " : name;
    }
}
