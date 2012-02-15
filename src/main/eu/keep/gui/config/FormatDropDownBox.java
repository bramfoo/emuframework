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
package eu.keep.gui.config;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;

import org.apache.log4j.Logger;

import eu.keep.gui.config.ConfigPanel.FormatWrapper;

public class FormatDropDownBox extends JComboBox {

	//private final ListCellRenderer defaultRenderer;
	private Object currentItem;	
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	public FormatDropDownBox() {
		
		// Add a custom Renderer that displays the section headers properly
		final ListCellRenderer defaultRenderer = this.getRenderer();
		this.setRenderer(new ListCellRenderer() {			
			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				Component c;
				if (value == null || value instanceof String) {
					// This cell represents a 'header' row: always display as non-selected and non-focused.
					c = defaultRenderer.getListCellRendererComponent(list,value,index,false,false);
					Font defaultFont = list.getFont();          
					c.setFont(new Font(defaultFont.getFontName(), Font.BOLD, defaultFont.getSize()));
				}
				else if (value instanceof ConfigPanel.FormatWrapper){
					// This cell represents a 'normal' row: display with the default rendering
					c = defaultRenderer.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
					c.setEnabled(true);
				}
				else {
					throw new RuntimeException("Invalid object for formats dropdown");
				}
				return c;
			}
		});
		
		
		// Add an ActionListener used to listen to selection events in the 
		// file formats dropdown. If a user tries to select one of the section 
		// header rows, the previously selected item will be kept.
		currentItem = this.getSelectedItem();
		this.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selectedItem = FormatDropDownBox.this.getSelectedItem();
				if (selectedItem instanceof String) {
					FormatDropDownBox.this.setSelectedItem(currentItem);
				} else {
					currentItem = selectedItem;
				}
			}
			
		});
		
		// Add custom Actions, to make sure the up- and down-keys can be used
		// to navigate through the entire list. Section headers will be skipped.
        ActionMap actionMap = this.getActionMap();
        Action up = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
              int si = getSelectedIndex();
              for(int i = si-1;i >= 0;i--) {
                if(FormatDropDownBox.this.getItemAt(i) instanceof FormatWrapper) {
                  setSelectedIndex(i);
                  break;
                }
              }
            }
          };
          Action down = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
              int si = getSelectedIndex();
              for(int i = si+1;i < getModel().getSize();i++) {
                  if(FormatDropDownBox.this.getItemAt(i) instanceof FormatWrapper) {
                  setSelectedIndex(i);
                  break;
                }
              }
            }
          };
          actionMap.put("UP", up);
          actionMap.put("DOWN", down);
          
          InputMap im = getInputMap();
          im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),      "UP");
          im.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0),   "UP");
          im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),    "DOWN");
          im.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0), "DOWN");
	}
	
}
