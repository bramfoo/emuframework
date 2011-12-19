package eu.keep.gui.wizard.ea;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: bki010
 * Date: 12/15/11
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Step0_Introduction extends JPanel {

    public Step0_Introduction(final EAWizard parent) {

        super.setLayout(new BorderLayout(5, 5));

        final Dimension d = new Dimension(320, 25);

        final String explanation = "<html>" +
                "<h3>Introduction</h3>\n" +
                "\n" +
                "<p>\n" +
                "This wizard adds an emulators to the Emulator Archive. Beware that \n" +
                "some work in advance is needed: this wizard expects the emulator to \n" +
                "contain a FreeMarker<sup>1</sup> template in its root directory \n" +
                "(note that the emulator needs to be locally installed!).\n" +
                "</p>\n" +
                "\n" +
                "<p>\n" +
                "For more information on how to create a proper FreeMarker template, \n" +
                "see their website<sup>1</sup> or look at the documentation of the \n" +
                "Emulation Framework<sup>2</sup> located in the <b>packages</b> folder " +
                "of the <b>trunk</b> source tree. You can also look at the SVN \n" +
                "repository containing the existing emulator packages to see how \n" +
                "they're supposed to be created<sup>3</sup>.\n" +
                "</p>\n" +
                "\n" +
                "<ol>\n" +
                "  <li>http://freemarker.sourceforge.net</li>\n" +
                "  <li>http://emuframework.sourceforge.net/documentation.html</li>\n" +
                "  <li>http://emuframework.svn.sourceforge.net/viewvc/emuframework</li>\n" +
                "</ol>" +
                "<html>";

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final JButton next = new JButton("<html>&rarr;</html>");

        center.add(new JLabel(explanation));
        buttons.add(next);

        super.add(center);
        super.add(buttons, BorderLayout.SOUTH);

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step0);
                parent.add(parent.step1, BorderLayout.CENTER);
                parent.log("1/2, select an emulator");
                parent.validate();
                parent.repaint();
            }
        });
    }
}
