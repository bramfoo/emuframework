package eu.keep.softwarearchive.wizard;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfirmPanel extends JPanel {

    ConfirmPanel(final SWAWizard parent) {
        super.setLayout(new BorderLayout(5, 5));

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final String explanation = "Confirm...";

        center.add(new JLabel(" "),                                              "wrap"         ); // empty line
        center.add(new JLabel("<html>" + explanation + "</html>"),               "span 2 1 wrap");
        center.add(new JLabel(" "),                                              "wrap"         ); // empty line
        center.add(new JLabel(" "),                                              "wrap"         ); // empty line

        final JButton previous = new JButton("<html>&larr;</html>");
        final JButton cancel = new JButton("cancel");
        final JButton confirm = new JButton("confirm");

        buttons.add(previous);
        buttons.add(cancel);
        buttons.add(confirm);

        super.add(center, BorderLayout.CENTER);
        super.add(buttons, BorderLayout.SOUTH);

        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.confirm);
                parent.add(parent.step5, BorderLayout.CENTER);
                parent.log("5/5");
                parent.validate();
                parent.repaint();
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.dispose();
            }
        });

        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previous.setEnabled(false);
                cancel.setEnabled(false);
                confirm.setEnabled(false);
                parent.log("writing to DB...");
            }
        });
    }
}
