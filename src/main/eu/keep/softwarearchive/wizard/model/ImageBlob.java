package eu.keep.softwarearchive.wizard.model;

import java.io.File;

public class ImageBlob {

    public final String id;
    public final File zipFile;

    public ImageBlob(String id, File zipFile) {
        this.id = id;
        this.zipFile = zipFile;
    }
}
