package eu.keep.softwarearchive.wizard;

import eu.keep.softwarearchive.util.DBUtil;
import eu.keep.softwarearchive.wizard.model.ImageFormat;
import eu.keep.softwarearchive.wizard.model.Platform;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class Step3_HardwareAndFormat extends JPanel {

    protected Platform platform;
    protected ImageFormat imageFormat;

    Step3_HardwareAndFormat(final SWAWizard parent) {
        super.setLayout(new BorderLayout(5, 5));

        final Dimension d = new Dimension(320, 25);

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final String explanation = "On what hardware platform, and file system, does " +
                "the operating system selected in the previous step run?";

        final Vector<Vector<String>> platformData = DBUtil.query(DBUtil.DB.SWA,
                "SELECT platform_id, name, description, creator, production_start, production_end, reference FROM softwarearchive.platforms");
        final Vector<Platform> existingPlatforms = new Vector<Platform>();
        platform = new Platform();
        existingPlatforms.add(platform);
        for(Vector<String> row : platformData) {
            existingPlatforms.add(new Platform(row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5), row.get(6)));
        }

        final Vector<Vector<String>> fileSystemData = DBUtil.query(DBUtil.DB.SWA,
                "SELECT imageformat_id, name FROM softwarearchive.imageformats");
        final Vector<ImageFormat> existingFileSystems = new Vector<ImageFormat>();
        imageFormat = new ImageFormat();
        existingFileSystems.add(imageFormat);
        for(Vector<String> row : fileSystemData) {
            existingFileSystems.add(new ImageFormat(row.get(0), row.get(1)));
        }

        final JComboBox platformCombo = new JComboBox(existingPlatforms);
        platformCombo.setPreferredSize(d);

        final JComboBox imageFormatCombo = new JComboBox(existingFileSystems);
        imageFormatCombo.setPreferredSize(d);

        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(new JLabel("<html>" + explanation + "</html>"),              "span 2 1 wrap" );
        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(new JLabel("hardware platform: ")                                            );
        center.add(platformCombo,                                               "wrap"          );
        center.add(new JLabel(" "),                                             "wrap"          ); // empty line
        center.add(new JLabel("file system: ")                                                  );
        center.add(imageFormatCombo,                                            "wrap"          );

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
                parent.remove(parent.step3);
                parent.add(parent.step2, BorderLayout.CENTER);
                parent.log("2/5, select an operating system");
                parent.validate();
                parent.repaint();
            }
        });

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step3);
                parent.add(parent.step4, BorderLayout.CENTER);
                parent.log("4/5, select the ZIP file");
                parent.validate();
                parent.repaint();
            }
        });

        platformCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                platform = (Platform)platformCombo.getSelectedItem();
                imageFormat = (ImageFormat)imageFormatCombo.getSelectedItem();
                next.setEnabled(!platform.isDummy() && !imageFormat.isDummy());
            }
        });

        imageFormatCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                platform = (Platform)platformCombo.getSelectedItem();
                imageFormat = (ImageFormat)imageFormatCombo.getSelectedItem();
                next.setEnabled(!platform.isDummy() && !imageFormat.isDummy());
            }
        });
    }
}
