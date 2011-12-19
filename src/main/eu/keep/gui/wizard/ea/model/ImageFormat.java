package eu.keep.gui.wizard.ea.model;

public class ImageFormat {

    public final String imageformat_id, name;

    public ImageFormat() {
        this(null, null);
    }

    public ImageFormat(String hardware_id, String name) {
        this.imageformat_id = hardware_id;
        this.name = name;
    }

     public boolean isDummy() {
        return imageformat_id == null;
    }

    @Override
    public String toString() {
        return name == null ? " " : name;
    }
}
