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
package eu.keep.gui.settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import java.awt.FlowLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;

import eu.keep.gui.util.RBLanguages;

/**
 * View to edit the hostName and Port of a URL Property.
 * @author nooe
 */
public class UrlPropertyEditor extends JPanel implements EFPropertyEditor {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String protocol = "http://";
	private static final String colon = ":";

	private JTextField hostName = new JTextField();
	private JTextField portNumber = new JTextField();
	private String path = "";

	private List<String> validationErrors = new ArrayList<String>();

	private final Border defaultBorder;

	/**
	 * Constructor
	 * @param initialValue: the current value of the URL
	 * @param path: 
	 */
	public UrlPropertyEditor(String initialValue, String path) {

		initEditor(initialValue, path);

		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setHgap(1);    	
		this.setLayout(layout);

		this.defaultBorder = hostName.getBorder();

		// String everything together
		this.add(new JLabel(protocol));
		this.add(hostName);
		this.add(new JLabel(":"));
		this.add(portNumber);
		this.add(new JLabel(path));        
	}

	/**
	 * Initialise editor elements
	 * @param initialValue
	 * @param path
	 */
	private void initEditor(String initialValue, String path) {
		try {
			URL initialURL = new URL(initialValue);
			hostName.setText(initialURL.getHost());
			hostName.setColumns(7);
			portNumber.setText(String.valueOf(initialURL.getPort()));
			portNumber.setColumns(4);
		}
		catch (MalformedURLException mfue) {
			// initial value is not a valid URL. Leave text fields empty
			logger.warn("Current property is not a valid URL: " + initialValue);
		}
		this.path = path;	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> validateInput() {

		resetValidation();

		// Hostname must not be null
		String hostNameString = hostName.getText();		
		if (hostNameString == null || hostNameString.equals("")) {
			hostName.setBorder(new ValidationFailedBorder());
			validationErrors.add(RBLanguages.get("error_empty_hostName"));
		}

		// Portnumber must a valid, positive number
		try {
			Long portNum = Long.valueOf(portNumber.getText());
			if (portNum < 0) {
				portNumber.setBorder(new ValidationFailedBorder());
				validationErrors.add(RBLanguages.get("error_invalid_portNumber"));
			}
		}
		catch (NumberFormatException nfe) {
			portNumber.setBorder(new ValidationFailedBorder());
			validationErrors.add(RBLanguages.get("error_invalid_portNumber"));
		}

		return validationErrors;
	}

	/**
	 * Reset the validation for this editor
	 */
	private void resetValidation() {
		validationErrors.clear();		
		hostName.setBorder(defaultBorder);
		portNumber.setBorder(defaultBorder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue() {
		String enteredHost = hostName.getText();
		String enteredPort = portNumber.getText();			
		return protocol + enteredHost + colon + enteredPort + path;
	}


	/**
	 * Border for textfield when it fails validation: thick red border
	 */
	private class ValidationFailedBorder extends LineBorder {

		public ValidationFailedBorder() {
			super(Color.red, 2);
		}

		public Insets getBorderInsets (Component c) {
			return defaultBorder.getBorderInsets(hostName);
		}

	}

}
