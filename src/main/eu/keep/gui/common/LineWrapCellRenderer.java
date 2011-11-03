package eu.keep.gui.common;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;


public class LineWrapCellRenderer extends JTextArea implements
		TableCellRenderer {

    private static Logger logger = Logger.getLogger(LineWrapCellRenderer.class.getName());

 
    @Override
    public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {
    	
            this.setText((String)value);
            this.setWrapStyleWord(true);                    
            this.setLineWrap(true);
            //this.setEditable(false);
            this.setFocusable(false);
                        
            return this;            
    }
}
