/*
* $Revision: 803 $ $Date: 2011-05-31 00:27:06 +0100 (Tue, 31 May 2011) $
* $Author: BLohman $
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

/**
 * A Junit Test class for {@link eu.keep.util.FileUtilities }
 * @author Bram Lohman
 * @author David Michel
 */
public class TestFileUtilities {

    @Test
    public void testCopy() throws IOException {
        File source = new File("./testData/digitalObjects/text.txt.xml");
        File target = new File(System.getProperty("java.io.tmpdir") + "/text.txt_copy.xml");

        FileUtilities.copy(source,target);

        int a, b;
        InputStream is_source = new FileInputStream(source);
        InputStream is_target = new FileInputStream(target);

            while ((a = is_source.read()) != -1 && (b = is_target.read()) != -1) {
                assertEquals("Bytes from source and target differ", a, b);
            }

        if(target.exists()) {
            target.delete();
        }
    }
	
    @Test
    public void testGenerateUniqueDir() throws FileNotFoundException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        File tmpDirFile = new File(tmpDir);
        File genDir = FileUtilities.GenerateUniqueDir(tmpDirFile);
        assertTrue("Random directory not created", genDir.exists());
        assertTrue("Random directory not a directory", genDir.isDirectory());
    }

    @Test
    public void testDeleteDir() throws IOException {

        String tmpDir = System.getProperty("java.io.tmpdir");
        File tmpDirFile = new File(tmpDir);
        File genDir = FileUtilities.GenerateUniqueDir(tmpDirFile);

        // Add content
        File source = new File("./testData/digitalObjects/text.txt.xml");
        File target = new File(tmpDir + "/text.txt_copy.xml");
        FileUtilities.copy(source,target);

        // test the delete method
        boolean result = FileUtilities.deleteDir(genDir);
        assertTrue("Directory not succesfully deleted", result);
        assertFalse("Directory not succesfully deleted", genDir.exists());
    }

    @Test
    public void testGetExtension() {
        File source = new File("./testData/digitalObjects/text.txt.xml");
        String ext = FileUtilities.getExtension(source);

        assertEquals("Extension incorrect", "xml", ext);
    }

    @Test
    public void testGetProperties() throws IOException {
        Properties props = FileUtilities.getProperties("./test.properties");

        assertEquals("Incorrect property", ";AUTO_SERVER=TRUE", props.getProperty("test.h2.db.server"));
    }
    
    @Test
    public void testUnzip() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        File tmpDirFile = new File(tmpDir);
        File genDir = FileUtilities.GenerateUniqueDir(tmpDirFile);
        File source = new File("./testData/LinUAE_0829Package.zip");
        
        List<File> unzips = FileUtilities.unZip(source, genDir);

        File zipFile = new File(genDir + "/templateCLI.ftl");
        assertTrue("File not part of list", unzips.contains(zipFile));
        zipFile = new File(genDir + "/templateProps.ftl");
        assertTrue("File not part of list", unzips.contains(zipFile));
        assertTrue("Directory not created", genDir.exists());
        assertEquals("Incorrect number files", 3, unzips.size());        
        assertTrue("File not created", unzips.get(0).exists());        
    }
}