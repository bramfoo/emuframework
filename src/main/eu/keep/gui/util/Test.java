package eu.keep.gui.util;

import javax.swing.*;

public class Test {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Ext-ASCII test");
        String lang = "Fran\u00E7ais";
        frame.add(new JLabel(lang));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
    }
}
