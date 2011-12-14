package eu.keep.softwarearchive.wizard;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Step5_FileFormat extends JPanel {

    Step5_FileFormat(final SWAWizard parent) {
        super.setLayout(new BorderLayout(5, 5));

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final String explanation = "File format explanation...";

        center.add(new JLabel(" "),                                              "wrap"         ); // empty line
        center.add(new JLabel("<html>" + explanation + "</html>"),               "span 2 1 wrap");
        center.add(new JLabel(" "),                                              "wrap"         ); // empty line
        center.add(new JLabel(" "),                                              "wrap"         ); // empty line

        JButton previous = new JButton("<html>&larr;</html>");
        JButton next = new JButton("<html>&rarr;</html>");

        buttons.add(previous);
        buttons.add(next);

        super.add(center, BorderLayout.CENTER);
        super.add(buttons, BorderLayout.SOUTH);

        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step5);
                parent.add(parent.step4, BorderLayout.CENTER);
                parent.log("4/5");
                parent.validate();
                parent.repaint();
            }
        });

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step5);
                parent.add(parent.confirm, BorderLayout.CENTER);
                parent.log("confirm...");
                parent.validate();
                parent.repaint();
            }
        });
    }
}
