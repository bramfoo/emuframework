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

package eu.keep.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Common shared XML utilities, such as validate, marshall/unmarshall, etc.
 * @author Bram Lohman
 */
public class XMLUtilities {

    private static final Logger logger       = Logger.getLogger(XMLUtilities.class.getName());

    private static JAXBContext  jc           = null;
    public static String        JAXB_PATHS = "eu.keep.softwarearchive.pathway";

    // Expensive operation, so use static
    static {
        try {
            jc = JAXBContext.newInstance(JAXB_PATHS);
        }
        catch (JAXBException e) {
            logger.error("Cannot initialise JAXBContext for binding migration xml files: "
                    + e.toString());
        }
    }

    /**
     * Get an unmarshaller that can unmarshal the given types.
     * @return A new unmarshaller
     * @throws JAXBException if an error was encountered while creating the Unmarshaller object
     */
    public static Unmarshaller getCoreUnmarshaller() throws JAXBException {
        return jc.createUnmarshaller();
    }

    /**
     * Get a marshaller that can marshal the given types
     * @return A new marshaller
     * @throws JAXBException if an error was encountered while creating the Marshaller object
     */
    public static Marshaller getCoreMarshaller() throws JAXBException {
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        return m;
    }

    /**
     * Save JAXB XML object to disk as an XML file
     * @param xmlObject The XML object
     * @param outputXMLFile The xml output file
     * @throws IOException If a file I/O error occurs while saving XML
     * @throws JAXBException If a JAXB error was encountered while saving to XML
     */
    public static void saveToXML(Object xmlObject, File outputXMLFile) throws JAXBException, IOException {
        FileOutputStream fos = new FileOutputStream(outputXMLFile);
        try {
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(xmlObject, fos);
        }
        finally {
            fos.close();
        }
    }

    /**
     * Load a whole XML object from an XML file on disk
     * @param inputFile An XML config file to load into memory
     * @return An object representing the whole XML file
     * @throws IOException If a file I/O error occurs while loading XML
     * @throws JAXBException If a JAXB error was encountered while loading XML
     */
    public static Object loadFromXML(File inputFile) throws JAXBException, IOException {
        FileInputStream fis = new FileInputStream(inputFile);
        try {
            return jc.createUnmarshaller().unmarshal(fis);
        }
        finally {
            fis.close();
        }
    }

  /**
     * Validates an XML file against the associated schema
     * @param SchemaFile A File representing the schema
     * @param sourceFile File representing the XML file to validate
     * @return {@code true} if XML conforms to schema, {@code false} otherwise
     * @throws IOException If a file I/O error occurs while validating XML
     * @throws SAXException If a SAX error is encountered while validating to XML
     */
    public static boolean validateXML(File SchemaFile, File sourceFile) throws SAXException, IOException {
        // sanity checks
        if (!SchemaFile.exists()) {
            logger.error("Error: File '" + SchemaFile.getPath() + "' doesn't exists");
            return false;
        }

        // convert File to Source before validating
        Source schemaSource = new StreamSource(SchemaFile);
        // validate!
        return validateXML(schemaSource,sourceFile);
    }

  /**
     * Validates an XML file against the associated schema
     * @param schemaSource A StreamSource representing the schema
     * @param sourceFile File representing the XML file to validate
     * @return {@code true} if XML conforms to schema, {@code false} otherwise
     * @throws IOException If a file I/O error occurs while validating XML
     * @throws SAXException If a SAX error is encountered while validating to XML
     */
    public static boolean validateXML(Source schemaSource, File sourceFile) throws SAXException,
            IOException {

        // sanity checks
        if (!sourceFile.exists()) {
            logger.error("Error: File '" + sourceFile.getPath() + "' doesn't exists");
            return false;
        }

        // Get factory for W3C XML Schema language
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        // Compile the schema
        Schema schema = factory.newSchema(schemaSource);

        // Assign a validator for the schema.
        Validator validator = schema.newValidator();

        // Load the source file
        Source source = new StreamSource(sourceFile);

        // Validate document; throws exceptions if invalid
        validator.validate(source);

        return true;
    }
}