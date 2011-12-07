package eu.keep.softwarearchive.wizard.tabs;

import eu.keep.gui.util.DBUtil;
import eu.keep.softwarearchive.wizard.SWAGUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

public class PronomFileFormat extends JPanel {

    private final String explanation =
            "The format added in step 1 will need to be linked to at least one " +
            "PRONOM file format. Below is a table of PRONOM formats currently available " +
            "in the emulation framework. <br><br>" +
            "Check all PRONOM formats that should be associated with your file format and " +
            "optionally add new PRONOM formats to the database. <br><br>" +
            "If you're not sure what formats to add, search the following page: <br>" +
            "&nbsp;&nbsp;&nbsp;&nbsp; <u>http://www.nationalarchives.gov.uk/PRONOM</u>";

    private final String getPronomFormatsQuery = "SELECT fileformat_id, name FROM engine.PCR_fileformats";
    private final String insertPronomFormatQuery = "INSERT INTO engine.PCR_fileformats (fileformat_id, name) VALUES(?, ?)";
    private final String insertEFPronomFormatQuery = "INSERT INTO engine.EF_PCR_fileformats (EF_ff_id, PCR_ff_id) VALUES(?, ?)";

    private final SWAGUI parent;

    public PronomFileFormat(SWAGUI p) {
        parent = p;
        initGUI();
    }

    private void initGUI() {
        super.setLayout(new MigLayout());

        final Vector<Vector<String>> data = DBUtil.query(DBUtil.DB.CEF, getPronomFormatsQuery);
        final CustomTableModel model = new CustomTableModel();

        for(Vector<String> row : data) {
            model.addRow(false, row.get(0), row.get(1));
        }

        final JTable table = new JTable(model);
        final JScrollPane scroll = new JScrollPane(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(20);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(parent.getWidth() - 171);

        Dimension d = new Dimension(320, 25);

        final JTextField id = new JTextField();
        id.setPreferredSize(d);

        final JTextField name = new JTextField();
        name.setPreferredSize(d);

        final JButton add = new JButton("add");
        add.setEnabled(false);

        final JButton done = new JButton("done");
        done.setEnabled(false);

        super.add(new JLabel("<html>" + explanation + "</html>"),  "span 2 1 wrap");
        super.add(new JLabel(" "),                                 "wrap"         ); // empty line
        super.add(new JLabel(" "),                                 "wrap"         ); // empty line
        super.add(scroll,                                          "span 3 1 wrap");
        super.add(new JLabel(" "),                                 "wrap"         ); // empty line
        super.add(new JLabel(" "),                                 "wrap"         ); // empty line
        super.add(new JLabel("Add a new PRONOM format"),           "wrap"         );
        super.add(new JLabel("id: ")                                              );
        super.add(id,                                              "wrap"         );
        super.add(new JLabel("name: ")                                            );
        super.add(name                                                            );
        super.add(add,                                             "wrap"         );
        super.add(new JLabel(" "),                                 "wrap"         ); // empty line
        super.add(new JLabel()                                                    ); // empty cell
        super.add(new JLabel()                                                    ); // empty cell
        super.add(done                                                            ); // empty cell

        id.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String i = id.getText().trim();
                String nm = name.getText().trim();
                add.setEnabled(!i.isEmpty() && !nm.isEmpty());
            }
        });

        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String i = id.getText().trim();
                String nm = name.getText().trim();
                add.setEnabled(!i.isEmpty() && !nm.isEmpty());
            }
        });

        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String i = id.getText().trim();
                String nm = name.getText().trim();
                int records = DBUtil.insert(DBUtil.DB.CEF, insertPronomFormatQuery, i, nm);

                if(records != 1) {
                    // TODO warn
                }
                else {
                    model.addRow(true, i, nm);
                    model.fireTableDataChanged();
                }

                id.setText("");
                name.setText("");
                add.setEnabled(false);
            }
        });

        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                Vector<String> frmts = model.getSelectedFormats();
                if(frmts.size() > 0) {
                    done.setEnabled(true);
                }
            }
        });

        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Vector<String> frmts = model.getSelectedFormats();
                String fileFormat = parent.getFormatIDCef();
                int records = 0;

                for(String f : frmts) {
                    records += DBUtil.insert(DBUtil.DB.CEF, insertEFPronomFormatQuery, fileFormat, f);
                }

                table.setEnabled(false);
                scroll.setEnabled(false);
                scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                id.setEnabled(false);
                name.setEnabled(false);
                add.setEnabled(false);
                done.setEnabled(false);

                if(records != frmts.size()) {
                    // TODO warn
                }
                else {
                    // TODO log
                    parent.enableTabIndex(2);
                }
            }
        });
    }

    private static class CustomTableModel extends AbstractTableModel {

        private String[] columnNames = {"", "id", "name"};
        private Vector<Vector<Object>> data = new Vector<Vector<Object>>();

        public void addRow(boolean selected, String id, String name) {
            Vector<Object> newRow = new Vector<Object>();
            newRow.add(selected);
            newRow.add(id);
            newRow.add(name);
            data.add(newRow);
        }

        public Vector<String> getSelectedFormats() {
            Vector<String> selected = new Vector<String>();
            for(Vector<Object> row : data) {
                if((Boolean)row.get(0)) {
                    selected.add((String)row.get(1));
                }
            }
            return selected;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data.get(row).get(col);
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return col == 0;
        }

        public void setValueAt(Object value, int row, int col) {
            data.get(row).set(col, value);
            fireTableCellUpdated(row, col);
        }


    }
}