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

import javax.swing.JTextArea;

/**
 * Custom JTextArea displaying logging messages
 */
public class LogPanel extends JTextArea {

    private static final String NEW_LINE = System.getProperty("line.separator");
    
    private static final int MAXIMUM_TEXT_LENGTH = 512 * 1024;  // maximum length before truncating starts: 512 Kb.
    private static final int TRUNCATE_UNIT = 1024; 				// when the maximum length is reached, text will be truncated by units this large 
    
    
	/**
	 * Constructor
	 * @param initialMessage initial log message to be displayed
	 */
	public LogPanel(String initialMessage) {
		super(initialMessage);
		
        this.setEditable(false);
        this.setOpaque(false);
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
        this.setBorder(null);
        //this.setBackground(new Color(UIManager.getColor("background").getRGB()));

        // register this panel with the log4j appender
        Log4jAppender.addTextArea(this);
	}
	
	/**
	 * Append a message to this Panel and put the caret position at the end of the document
	 * @param message the message
	 */
	public void logMessage(String message) {
		this.append(NEW_LINE + message);
		
		// Truncate (from the beginning of the Document) if the document exceeds the maximum length
		while (this.getDocument().getLength() > MAXIMUM_TEXT_LENGTH) {
			this.replaceRange(null, 0, TRUNCATE_UNIT);
		}
		
		// Automatically scroll to the end of the document
		this.setCaretPosition(this.getDocument().getLength());
	}
	
}
