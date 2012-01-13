/*
 * $Revision$ $Date$
 * $Author$
 * $header:
 * Copyright (c) 2009-2011 Tessella plc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information about this project, visit
 *   http://www.keep-project.eu/
 *   http://emuframework.sourceforge.net/
 * or contact us via email:
 *   blohman at users.sourceforge.net
 *   dav_m at users.sourceforge.net
 *   bkiers at users.sourceforge.net
 * Developed by:
 *   Tessella plc <www.tessella.com>
 *   Koninklijke Bibliotheek <www.kb.nl>
 *   KEEP <www.keep-project.eu>
 * Project Title: Core Emulation Framework (Core EF)$
 */
package eu.keep.gui.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;

import eu.keep.gui.util.InfoTableCell;


public class LineWrapCellRenderer extends JTextArea implements
		TableCellRenderer {

 
    @Override
    public Component getTableCellRendererComponent(
    		JTable table,
    		Object value,
    		boolean isSelected,
    		boolean hasFocus,
    		int row,
    		int column) {

		InfoTableCell cellValue;
		try {
			cellValue = (InfoTableCell)value;
    	} catch (ClassCastException cce) {
    		throw new RuntimeException("Invalid table entry type: " + value.getClass());    		
    	}

		// Change style of cell if it's a section header
    	String defaultFontName = UIManager.getDefaults().getFont("TextField.font").getName();
    	int defaultFontSize = UIManager.getDefaults().getFont("TextField.font").getSize();		
		Border emptyBorder = new EmptyBorder(2,4,0,0);
    	if (cellValue.isSectionTitle()) {
        	this.setBorder(new CompoundBorder(new MatteBorder(0, 0, 2, 0, Color.black), emptyBorder));
        	this.setFont(new Font(defaultFontName, Font.BOLD, defaultFontSize));
    	} else {
        	this.setBorder(emptyBorder);
        	this.setFont(new Font(defaultFontName, Font.PLAIN, defaultFontSize));     	
    	}
		
    	// Defaults for all cells
        this.setBackground(new Color(UIManager.getColor("background").getRGB())); 
    	this.setText(cellValue.getText());
    	this.setWrapStyleWord(true);                    
    	this.setLineWrap(true);
    	this.setFocusable(false);

    	return this;            
    }
}
