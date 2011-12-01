package eu.keep.gui.explorer;

import java.awt.event.MouseAdapter;

public class FileTreeListener extends MouseAdapter {

    private FileTree fileTree;

    public FileTreeListener(FileTree fileTree) {
        if (fileTree == null) {
            throw new IllegalArgumentException("Null argument not allowed");
        }
        this.fileTree = fileTree;
    }
}