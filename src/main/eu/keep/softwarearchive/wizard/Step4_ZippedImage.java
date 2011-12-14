package eu.keep.softwarearchive.wizard;

import eu.keep.softwarearchive.util.DBUtil;
import eu.keep.softwarearchive.wizard.model.ImageBlob;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;

public class Step4_ZippedImage extends JPanel {

    protected ImageBlob imageBlob = null;

    Step4_ZippedImage(final SWAWizard parent) {
        super.setLayout(new BorderLayout(5, 5));

        final Dimension d = new Dimension(320, 25);

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final String explanation = "Please point where the zipped image containing " +
                "the operating system and application is located on your local disk.";

        final Vector<Vector<String>> blobIDs = DBUtil.query(DBUtil.DB.SWA, "SELECT image_id FROM softwarearchive.imageblobs");
        final String blobID = DBUtil.createUniqueStringID(blobIDs,  0);

        final JTextField txtPath = SWAWizard.createTxtField(d, true);
        final JButton browse = new JButton("browse...");

        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(new JLabel("<html>" + explanation + "</html>"),              "span 2 1 wrap" );
        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(txtPath,                                                     "span 2 1 wrap" );
        center.add(browse,                                                      "wrap"          );

        final JButton previous = new JButton("<html>&larr;</html>");
        final JButton next = new JButton("<html>&rarr;</html>");

        next.setEnabled(false);

        buttons.add(previous);
        buttons.add(next);

        super.add(center, BorderLayout.CENTER);
        super.add(buttons, BorderLayout.SOUTH);

        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step4);
                parent.add(parent.step3, BorderLayout.CENTER);
                parent.log("3/5, select a hardware platform and file system");
                parent.validate();
                parent.repaint();
            }
        });

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step4);
                parent.add(parent.step5, BorderLayout.CENTER);
                parent.log("5/5, select a file format");
                parent.validate();
                parent.repaint();
            }
        });

        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("ZIP files", "zip");
                fileChooser.setFileFilter(filter);

                int returnValue = fileChooser.showOpenDialog(parent);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    txtPath.setText(f.toString());
                    txtPath.setBackground(Color.WHITE);
                    next.setEnabled(true);
                    imageBlob = new ImageBlob(blobID, f);
                }
            }
        });

        txtPath.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                File f = new File(txtPath.getText().trim());
                if(f.exists() && f.isFile() && f.getName().toLowerCase().endsWith(".zip")) {
                    txtPath.setBackground(Color.WHITE);
                    imageBlob = new ImageBlob(blobID, f);
                    next.setEnabled(true);
                }
                else {
                    txtPath.setBackground(SWAWizard.LIGHT_RED);
                    imageBlob = null;
                    next.setEnabled(false);
                }
            }
        });
    }
}
