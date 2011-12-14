package eu.keep.softwarearchive.wizard;

public class ImageFormat {

    public final String imageformat_id, name;

    public ImageFormat() {
        this(null, null);
    }

    public ImageFormat(String imageformat_id, String name) {
        this.imageformat_id = imageformat_id;
        this.name = name;
    }

    public boolean isDummy() {
        return imageformat_id == null;
    }

    @Override
    public String toString() {
        if(name == null) {
            return "<select>";
        }
        return name;
    }
}
