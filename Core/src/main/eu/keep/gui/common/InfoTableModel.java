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

import javax.swing.table.AbstractTableModel;

public class InfoTableModel extends AbstractTableModel {

	private Object[] colNames;
	private Object[][] data;
	
	public InfoTableModel(Object[] colNames, Object[][] data) {
		this.colNames = colNames;
		this.data = data;
	}
	
	@Override
    public String getColumnName(int col) {
        return this.colNames[col].toString();
    }
    
	@Override
    public int getRowCount() { 
    	return this.data.length; 
    }
    
	@Override
    public int getColumnCount() { 
		return this.colNames.length; 
	}
	
	@Override
    public Object getValueAt(int row, int col) {
        return this.data[row][col];
    }
	
}
