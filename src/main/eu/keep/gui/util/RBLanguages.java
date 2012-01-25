/*
 * $Revision: 191 $ $Date: 2012-01-11 15:21:43 +0100 (Wed, 11 Jan 2012) $
 * $Author: bkiers $
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

import eu.keep.util.FileUtilities;
import eu.keep.util.Language;
import org.apache.log4j.Logger;
import org.mockito.cglib.core.Local;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import java.io.*;
import java.util.*;

public final class RBLanguages {

    private static final Logger logger = Logger.getLogger(RBLanguages.class.getName());
    private static final String BUNDLE = "eu/keep/GUIBundle";

    private static Map<Object, String> components;
    private static Locale currentLocale;
    private static ResourceBundle messages;

    static {
        components = new HashMap<Object, String>();

        String fileName = "eu/keep/gui.properties";
        Properties props = new Properties();
        try {
            props = FileUtilities.getProperties(fileName);
        } catch (IOException e) {
            logger.error("Could not load properties file: " + fileName);
        }

        String lang = props.getProperty("language");

        if(lang == null) {
            currentLocale = new Locale("en");
        }
        else {
            currentLocale = new Locale(lang);
        }

        messages = ResourceBundle.getBundle(BUNDLE, currentLocale);
    }

    private RBLanguages() {}

    public static void change(Language language) {
        currentLocale = new Locale(language.getLanguageId());
        messages = ResourceBundle.getBundle(BUNDLE, currentLocale);

        for(Map.Entry<Object, String> entry : components.entrySet()) {
            setText(entry.getKey(), entry.getValue());
        }
    }

    public static String get(String key) {
        return messages.getString(key);
    }

    public static void set(Object component, String key) {
        components.put(component, key);
        setText(component, key);
    }

    private static void setText(Object component, String key) {
        String text = messages.getString(key);

        if(component instanceof JLabel) {
            ((JLabel)component).setText(text);
        }
        else if(component instanceof AbstractButton) {
            ((AbstractButton)component).setText(text);
        }
        else if(component instanceof JTextComponent) {
            ((JTextComponent)component).setText(text);
        }
        else if(component instanceof TitledBorder) {
            ((TitledBorder)component).setTitle(text);
        }
        else {
            logger.error("Could not set the text for component: " + component.getClass().getName());
        }
    }
}
