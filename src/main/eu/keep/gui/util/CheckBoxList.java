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
package eu.keep.gui.util;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.*;

/**
 * Class to display a list of checkboxes
 * @author nooe
 */
public class CheckBoxList extends JList {
	
    private static final Logger logger = Logger.getLogger(CheckBoxList.class.getName());

	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public CheckBoxList() {
		setCellRenderer(new CheckBoxCellRenderer());
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				if (index != -1) {
					JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
					checkbox.doClick();
					repaint();
				}
			}
		});
	}

	private class CheckBoxCellRenderer implements ListCellRenderer {
		
		public Component getListCellRendererComponent(JList list, 
				Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JCheckBox checkbox = (JCheckBox) value;
			checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
			checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
//			checkbox.setEnabled(isEnabled());
//			checkbox.setFont(getFont());
//			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
//			checkbox.setBorder(isSelected ?
//					UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			return checkbox;
		}

	}
	
	/**
	 * Change the selection state of all checkboxes in the list
	 * @param newState true to select all checkboxes, false to deselect
	 */
	public void changeAllSelections(boolean newState) {
		for (int i=0; i<getModel().getSize(); i++) {
			JCheckBox checkbox = (JCheckBox) getModel().getElementAt(i);
			checkbox.setSelected(newState);
		}
		repaint();	
	}
	
	/**
	 * Check if all checkBoxes in this list are selected
	 * @return true if <b>all</b> checkBoxes are selected, false otherwise
	 */
	public boolean isAllSelected() {
		boolean isAllSelected = true;
		for (int i=0; i<getModel().getSize(); i++) {
			JCheckBox checkbox = (JCheckBox) getModel().getElementAt(i);
			if (!checkbox.isSelected()) {
				isAllSelected = false;
			}
		}
		return isAllSelected;
	}

	/**
	 * Check if no checkBoxes in this list are selected
	 * @return true if <b>all</b> checkBoxes are <b>deselected</b>, false otherwise
	 */
	public boolean isNoneSelected() {
		boolean isNoneSelected = true;
		for (int i=0; i<getModel().getSize(); i++) {
			JCheckBox checkbox = (JCheckBox) getModel().getElementAt(i);
			if (checkbox.isSelected()) {
				isNoneSelected = false;
			}
		}
		return isNoneSelected;
	}

}