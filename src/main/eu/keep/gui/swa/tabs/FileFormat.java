package eu.keep.gui.swa.tabs;

import edu.harvard.hul.ois.fits.FitsOutput;
import eu.keep.characteriser.FitsTool;
import eu.keep.characteriser.Format;
import eu.keep.gui.swa.SWAGUI;
import eu.keep.gui.util.DBUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileFormat extends JPanel {

    private static Logger logger = Logger.getLogger(FileFormat.class.getName());

    private final String explanation =
            "Select an existing file format from the drop-down box, or enter a new " +
            "format, which should be associated with the software you're about to " +
            "add. <br><br>" +
            "Be careful when pressing the 'ok' button: once you press it, the file " +
            "format is added to the database. Contact your system administrator if " +
            "you have accidentally entered a wrong value. He or she can remove the " +
            "data. <br><br>" +
            "Note that you can also safely terminate this window and restart this " +
            "wizard to start over. The incorrectly added data can be removed at a " +
            "later stage. <br><br>";

    private final String getFormatQueryCEF = "SELECT fileformat_id, name FROM engine.fileformats";
    private final String insertFormatQueryCEF = "INSERT INTO engine.fileformats (fileformat_id, name) VALUES(?, ?)";

    private final String getFormatQuerySWA = "SELECT fileformat_id FROM softwarearchive.fileformats";
    private final String insertFormatQuerySWA = "INSERT INTO softwarearchive.fileformats (fileformat_id, name, version, description, reference) VALUES(?, ?, ?, ?, ?)";

    private final String matchFileFormat = "SELECT fileformat_id FROM softwarearchive.fileformats WHERE name = ?";

    private final SWAGUI parent;
    private JComboBox formats;
    private JComboBox fitsFormats;

    public String formatIDCef;
    public String formatName;
    public String formatIDSwa;

    public FileFormat(SWAGUI p) {
        parent = p;
        formatIDCef = null;
        formatName = null;
        initGUI();
    }

    private void initGUI() {
        super.setLayout(new MigLayout());

        final Vector<Vector<String>> data = DBUtil.query(DBUtil.DB.CEF, getFormatQueryCEF);
        final Vector<FormatIDName> existingFormats = new Vector<FormatIDName>();
        final FormatIDName first = new FormatIDName(null, null);
        existingFormats.add(first);
        for(Vector<String> row : data) {
            existingFormats.add(new FormatIDName(row.get(0), row.get(1)));
        }

        Dimension d = new Dimension(320, 25);

        formats = new JComboBox(existingFormats);
        formats.setPreferredSize(d);
        fitsFormats = new JComboBox();
        fitsFormats.setPreferredSize(d);
        fitsFormats.setEnabled(false);

        //final JTextField name = new JTextField();
        //name.setPreferredSize(d);
        final JTextField version = new JTextField();
        version.setPreferredSize(d);
        final JTextField description = new JTextField();
        description.setPreferredSize(d);
        final JTextField reference = new JTextField();
        reference.setPreferredSize(d);

        final JButton select = new JButton("or select a file");
        final JButton ok = new JButton("ok");
        ok.setEnabled(false);

        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel("<html>" + explanation + "</html>"),               "span 2 1 wrap");
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel("select an existing format:    ")                                 );
        super.add(formats,                                                      "wrap"         );
        super.add(select                                                                       );
        super.add(fitsFormats,                                                  "wrap"         );
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel(" "),                                              "wrap"         ); // empty line
        super.add(new JLabel("version: ")                                                      );
        super.add(version,                                                      "wrap"         );
        super.add(new JLabel("description: ")                                                  );
        super.add(description,                                                  "wrap"         );
        super.add(new JLabel("reference: ")                                                    );
        super.add(reference,                                                    "wrap"         );
        super.add(new JLabel()                                                                 ); // empty cell
        super.add(ok, "align right");

        version.setEditable(false);
        description.setEditable(false);
        reference.setEditable(false);
        version.setBackground(Color.LIGHT_GRAY);
        description.setBackground(Color.LIGHT_GRAY);
        reference.setBackground(Color.LIGHT_GRAY);

        select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnVal = chooser.showOpenDialog(parent);

                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    formats.setSelectedIndex(0);
                    fitsFormats.setEnabled(true);
                    version.setEditable(true);
                    description.setEditable(true);
                    reference.setEditable(true);
                    version.setBackground(Color.WHITE);
                    description.setBackground(Color.WHITE);
                    reference.setBackground(Color.WHITE);

                    File file = chooser.getSelectedFile();

                    try {
                        // TODO create a separate thread since it might take a little time...
                        FitsTool fits = getFitsTool();
                        FitsOutput fitsOut = fits.examine(file);
                        java.util.List<Format> formats = fits.getFormats(fitsOut);
                        int addToID = 0;
                        for(Format ff : formats) {
                            String id = DBUtil.createUniqueStringID(data, 0, addToID);
                            fitsFormats.addItem(new FormatIDName(id, ff.getName().split(":")[0]));
                            addToID++;
                        }

                        if(!formats.isEmpty()) {
                            ok.setEnabled(true);
                            version.setText("");
                            description.setText("");
                            reference.setText("");
                        }
                    } catch(Exception ex) {
                        JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        formats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = ((FormatIDName) formats.getSelectedItem()).name;
                String name = getFormatIDName().name;

                if (selected != null) {
                    ok.setEnabled(true);
                    version.setText("");
                    description.setText("");
                    reference.setText("");
                    version.setEditable(false);
                    description.setEditable(false);
                    reference.setEditable(false);
                    version.setBackground(Color.LIGHT_GRAY);
                    description.setBackground(Color.LIGHT_GRAY);
                    reference.setBackground(Color.LIGHT_GRAY);
                    fitsFormats.setEnabled(false);
                    fitsFormats.removeAllItems();
                }
                else if (name == null || name.isEmpty()) {
                    ok.setEnabled(false);
                }
            }
        });

        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                FormatIDName temp = getFormatIDName();

                if(fitsFormats.getSelectedItem() != null && existingFormats.contains(temp)) {
                    // file format already present in the DB
                    version.setText("");
                    description.setText("");
                    reference.setText("");
                    version.setEditable(false);
                    description.setEditable(false);
                    reference.setEditable(false);
                    temp = existingFormats.get(existingFormats.indexOf(temp));
                }

                if(!temp.name.isEmpty()) {
                    formatIDCef = DBUtil.createUniqueStringID(data, 0);
                    formatName = temp.name;

                    int records = 0;

                    records += DBUtil.insert(DBUtil.DB.CEF, insertFormatQueryCEF, formatIDCef, formatName);

                    Vector<Vector<String>> ids = DBUtil.query(DBUtil.DB.SWA, getFormatQuerySWA);
                    formatIDSwa = DBUtil.createUniqueStringID(ids, 0);

                    records += DBUtil.insert(DBUtil.DB.SWA, insertFormatQuerySWA,
                            formatIDSwa,
                            temp.name,
                            version.getText().trim(),
                            description.getText().trim(),
                            reference.getText().trim()
                    );

                    if(records != 2) {
                        // TODO log error
                    }
                    else {
                        // TODO log
                    }
                }
                else if (temp.id != null) {
                    formatIDCef = temp.id;
                    formatName = temp.name;

                    final Vector<Vector<String>> swaID = DBUtil.query(DBUtil.DB.SWA,
                            matchFileFormat.replace("?", "'" + formatName + "'") // TODO use prepared statement!
                    );

                    formatIDSwa = swaID.isEmpty() ? null : swaID.get(0).isEmpty() ? null : swaID.get(0).get(0);
                }

                if(formatIDCef != null && formatName != null && formatIDSwa != null) {
                    formats.setEnabled(false);
                    version.setEnabled(false);
                    description.setEnabled(false);
                    reference.setEnabled(false);
                    ok.setEnabled(false);
                    fitsFormats.setEnabled(false);
                    select.setEnabled(false);
                    parent.enableTabIndex(1);
                }
                else {
                    logger.severe(String.format("formatIDCef=%s, formatName=%s, formatIDSwa=%s",
                            formatIDCef, formatName, formatIDSwa));
                }
            }
        });
    }

    private FormatIDName getFormatIDName() {
        Object format = fitsFormats.getSelectedItem();
        if(format != null) {
            return (FormatIDName)format;
        }
        else {
            return (FormatIDName)formats.getSelectedItem();
        }
    }

    private FitsTool getFitsTool() throws IOException {
        String fitsHome = "eu/keep/resources/fits";
        String fitsHomePath = "";
        URL url = null;
        logger.log(Level.WARNING, "Attempting to read FITShome from file...");
		try {
			File fitsLoc = new File(fitsHome);
			logger.log(Level.FINE, "Using FITShome file location: " + fitsLoc);
			if (fitsLoc.exists())
				url = fitsLoc.toURI().toURL();
		}
		catch (MalformedURLException me)
		{
			logger.log(Level.FINE, "Invalid URL created for FITShome (file): " + me);
		}
        // If it's not found, try as resource
        if (url == null) {
            logger.info("FITShome not found in file, attempting to read as resource...");
            url = this.getClass().getClassLoader().getResource(fitsHome);
        }
        if (url == null)
        {
        	logger.log(Level.WARNING, "No valid FITS home file found (as resource or file)");
        	throw new IOException("No valid FITS home file found (as resource or file)");
        }
        try {
        	logger.info("FITShome found as url: " + url.toString());
            fitsHomePath = url.toURI().getRawPath().replaceAll("%20", " ");
        } catch (URISyntaxException e) {
            throw new IOException("Cannot read resources directory: " + fitsHomePath);
        }
        return new FitsTool(fitsHomePath);
    }

    private static class FormatIDName {

        final String id;
        final String name;

        FormatIDName(String i, String nm) {
            id = i;
            name = nm;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FormatIDName that = (FormatIDName) o;

            return !(name != null ? !name.equals(that.name) : that.name != null);

        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return name == null ? "" : name;
        }
    }
}
