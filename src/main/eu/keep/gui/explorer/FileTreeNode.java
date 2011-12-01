package eu.keep.gui.explorer;

import java.io.File;

public class FileTreeNode {

    public File file;

    public FileTreeNode(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Null file not allowed");
        }
        this.file = file;
    }

    public String toString() {
        return file.getName().isEmpty() ? file.getAbsolutePath() : file.getName();
    }
}