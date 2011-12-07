package eu.keep.softwarearchive.wizard.tabs;

import eu.keep.gui.util.DBUtil;
import eu.keep.softwarearchive.wizard.SWAGUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;

public class DiskImage extends JPanel {

    private final String explanation =
            "Either select an existing image which contains the selected operating " +
            "system and application, or create a new image that contains this " +
            "operating system and application.<br><br>" +
            "If the 'disk image format' or 'platform' are not available, you'll need " +
            "to add them through the wizard to add a new emulator.";

    private final String getImages = "SELECT image_id, description FROM softwarearchive.images";
    private final String getImageFormats = "SELECT imageformat_id, name FROM softwarearchive.imageformats";
    private final String getPlatforms = "SELECT platform_id, name, description FROM softwarearchive.platforms";

    private final String insertImage = "INSERT INTO softwarearchive.images (image_id, description, imageformat_id, platform_id) VALUES(?, ?, ?, ?)";
    private final String insertImageBlob = "INSERT INTO softwarearchive.imageblobs (image_id, image) VALUES(?, FILE_READ(?))";
    private final String insertAppImage = "INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES(?, ?)";
    private final String insertOSImage = "INSERT INTO softwarearchive.opsys_images (opsys_id, image_id) VALUES(?, ?)";

    // INSERT INTO softwarearchive.images (image_id, description, imageformat_id, platform_id) VALUES('IMG-1000','FreeDOS version 0.9', 'IFT-1000', 'HPF-1004');
    // INSERT INTO softwarearchive.imageformats (imageformat_id,name) VALUES('IFT-1000','FAT16')
    // INSERT INTO softwarearchive.imageblobs (image_id, image) VALUES('IMG-1000', FILE_READ('./packages/FreeDOS09_blocek.img.zip'));
    // INSERT INTO softwarearchive.apps_images (app_id, image_id) VALUES('APP-1000','IMG-1000')
    // INSERT INTO softwarearchive.opsys_images (opsys_id, image_id) VALUES('OPS-1000','IMG-1000')

    private final SWAGUI parent;

    public DiskImage(SWAGUI p) {
        parent = p;
        initGUI();
    }

    private void initGUI() {

        super.setLayout(new MigLayout());

        final Vector<Vector<String>> imgData = DBUtil.query(DBUtil.DB.SWA, getImages);
        Vector<ImageIDDescription> tempImgs = new Vector<ImageIDDescription>();
        final ImageIDDescription firstImg = new ImageIDDescription(null, null);
        tempImgs.add(firstImg);
        for(Vector<String> row : imgData) {
            tempImgs.add(new ImageIDDescription(row.get(0), row.get(1)));
        }

        final Vector<Vector<String>> imgFrmtData = DBUtil.query(DBUtil.DB.SWA, getImageFormats);
        Vector<FormatIDName> tempImgFrmts = new Vector<FormatIDName>();
        final FormatIDName firstImgFrmt = new FormatIDName(null, null);
        tempImgFrmts.add(firstImgFrmt);
        for(Vector<String> row : imgFrmtData) {
            tempImgFrmts.add(new FormatIDName(row.get(0), row.get(1)));
        }

        final Vector<Vector<String>> platformsData = DBUtil.query(DBUtil.DB.SWA, getPlatforms);
        Vector<PlatformIDNameDescription> tempPlatforms = new Vector<PlatformIDNameDescription>();
        final PlatformIDNameDescription firstPlatform = new PlatformIDNameDescription(null, null, null);
        tempPlatforms.add(firstPlatform);
        for(Vector<String> row : platformsData) {
            tempPlatforms.add(new PlatformIDNameDescription(row.get(0), row.get(1), row.get(2)));
        }

        Dimension d = new Dimension(320, 25);

        final JComboBox images = new JComboBox(tempImgs);
        images.setPreferredSize(d);

        final JTextField description = new JTextField();
        description.setPreferredSize(d);

        final JComboBox imageformat_id = new JComboBox(tempImgFrmts);
        imageformat_id.setPreferredSize(d);

        final JComboBox platform_id = new JComboBox(tempPlatforms);
        platform_id.setPreferredSize(d);

        final JButton zip = new JButton("...");
        zip.setPreferredSize(d);
        zip.setToolTipText("");

        final JButton finished = new JButton("finished");

        super.add(new JLabel("<html>" + explanation + "</html>"),               "span 2 1 wrap");
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel("select an existing disk image: ")                                );
        super.add(images,                                                       "wrap"         );
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel("Create a new disk image"),                        "wrap"         );
        super.add(new JLabel("description:")                                                   );
        super.add(description,                                                  "wrap"         );
        super.add(new JLabel("file system:")                                                   );
        super.add(imageformat_id,                                               "wrap"         );
        super.add(new JLabel("platform:")                                                      );
        super.add(platform_id,                                                  "wrap"         );
        super.add(new JLabel("zip file containing the image:")                                 );
        super.add(zip,                                                          "wrap"         );
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel()                                                                 ); // empty cell
        super.add(finished,                                                     "align right"  );

        final JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || (f.isFile() && f.getName().toLowerCase().endsWith(".zip"));
            }
            @Override
            public String getDescription() {
                return "Select a ZIP archive";
            }
        });

        images.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ImageIDDescription image = (ImageIDDescription)images.getSelectedItem();
                FormatIDName format = (FormatIDName)imageformat_id.getSelectedItem();
                PlatformIDNameDescription platform = (PlatformIDNameDescription)platform_id.getSelectedItem();
                String desc = description.getText().trim();
                File zipFile = new File(zip.getToolTipText());

                if(image.id != null) {
                    description.setText("");
                    imageformat_id.setSelectedItem(firstImgFrmt);
                    platform_id.setSelectedItem(firstPlatform);
                    zip.setText("...");
                    zip.setToolTipText("");
                    finished.setEnabled(true);
                }
                else {
                    finished.setEnabled(!desc.isEmpty() && format.id != null && platform.id != null && zipFile.exists());
                }
            }
        });

        description.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                ImageIDDescription image = (ImageIDDescription)images.getSelectedItem();
                FormatIDName format = (FormatIDName)imageformat_id.getSelectedItem();
                PlatformIDNameDescription platform = (PlatformIDNameDescription)platform_id.getSelectedItem();
                String desc = description.getText().trim();
                File zipFile = new File(zip.getToolTipText());

                if(!desc.isEmpty()) {
                    finished.setEnabled(format.id != null && platform.id != null && zipFile.exists());
                    images.setSelectedItem(firstImg);
                }
                else {
                    finished.setEnabled(image.id != null);
                }
            }
        });

        imageformat_id.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ImageIDDescription image = (ImageIDDescription)images.getSelectedItem();
                FormatIDName format = (FormatIDName)imageformat_id.getSelectedItem();
                PlatformIDNameDescription platform = (PlatformIDNameDescription)platform_id.getSelectedItem();
                String desc = description.getText().trim();
                File zipFile = new File(zip.getToolTipText());

                if(format.id != null) {
                    finished.setEnabled(!desc.isEmpty() && platform.id != null && zipFile.exists());
                    images.setSelectedItem(firstImg);
                }
                else {
                    finished.setEnabled(image.id != null);
                }
            }
        });

        platform_id.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ImageIDDescription image = (ImageIDDescription)images.getSelectedItem();
                FormatIDName format = (FormatIDName)imageformat_id.getSelectedItem();
                PlatformIDNameDescription platform = (PlatformIDNameDescription)platform_id.getSelectedItem();
                String desc = description.getText().trim();
                File zipFile = new File(zip.getToolTipText());

                if(platform.id != null) {
                    finished.setEnabled(!desc.isEmpty() && format.id != null && zipFile.exists());
                    images.setSelectedItem(firstImg);
                }
                else {
                    finished.setEnabled(image.id != null);
                }
            }
        });

        zip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                FormatIDName format = (FormatIDName)imageformat_id.getSelectedItem();
                PlatformIDNameDescription platform = (PlatformIDNameDescription)platform_id.getSelectedItem();
                String desc = description.getText().trim();

                int returnVal = chooser.showOpenDialog(parent);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    String path = file.getAbsolutePath();
                    final int maxChars = 45;
                    zip.setText(path.length() <= maxChars ? path : "..." + path.substring(path.length() - maxChars));
                    zip.setToolTipText(path);

                    finished.setEnabled(!desc.isEmpty() && format.id != null && platform.id != null);
                    images.setSelectedItem(firstImg);
                }
            }
        });

        finished.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO
            }
        });
    }

    private static class ImageIDDescription {

        final String id;
        final String description;

        ImageIDDescription(String i, String d) {
            id = i;
            description = d;
        }

        @Override
        public String toString() {
            return description == null ? "" : id + ", " + description;
        }
    }

    private static class FormatIDName {

        final String id;
        final String name;

        FormatIDName(String i, String n) {
            id = i;
            name = n;
        }

        @Override
        public String toString() {
            return name == null ? "" : name;
        }
    }

    private static class PlatformIDNameDescription {

        final String id;
        final String name;
        final String description;

        PlatformIDNameDescription(String i, String nm, String des) {
            id = i;
            name = nm;
            description = des;
        }

        @Override
        public String toString() {
            return name == null ? "" : name + ", " + description;
        }
    }
}
