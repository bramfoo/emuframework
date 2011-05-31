/*
* $Revision: 758 $ $Date: 2011-05-17 16:27:52 +0200 (Tue, 17 May 2011) $
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

package eu.keep.characteriser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;

/**
 * A Junit Test class for {@link eu.keep.characteriser.FitsTool}
 * @author Bram Lohman
 */
public class TestFitsTool {

	FitsTool fits;
	final String fitsLoc = "eu/keep/resources/fits"; 
	File testObject;
	
	@Before
	public void setUp(){
		URL url = this.getClass().getClassLoader().getResource(fitsLoc);
		String fitsHomePath = "";
		try {
			fitsHomePath = url.toURI().getRawPath().replaceAll("%20"," ");
		} catch (URISyntaxException e) {
			fail("Failed to set up FITS:" + e.getMessage());
		}

		fits = new FitsTool(fitsHomePath);
		
		testObject = new File("./testData/digitalObjects/x86/build.xml");
	}

	@Test
    public void testGetFormats() {
		FitsOutput fitsOut = null;
		try {
			fitsOut = fits.examine(testObject);
		} catch (FitsException e) {
			fail("Unexpected error:" + e.getMessage());
		}
		
		Format expected = new Format("Extensible Markup Language", "text/xml");
		List<String> tools = new ArrayList<String>();
		tools.add("Jhove 1.5");
		tools.add("Droid 3.0");
		tools.add("file utility 5.03");
		tools.add("ffident 0.2");
		expected.setReportingTools(tools);
		List<Format> list = new ArrayList<Format>();
		list.add(expected);
		assertEquals("Wrong formats", list, fits.getFormats(fitsOut));
    }

	@Test
    public void testGetFileInfo() {
		FitsOutput fitsOut = null;
		try {
			fitsOut = fits.examine(testObject);
		} catch (FitsException e) {
			fail("Unexpected error:" + e.getMessage());
		}

		Map<String, List<String>> info = new HashMap<String, List<String>>();
		List<String> infoTrue = new ArrayList<String>();
		infoTrue.add("true");
		info.put("well-formed", infoTrue);
		info.put("valid", infoTrue);
		List<String> infoSize = new ArrayList<String>();
		infoSize.add("22241");
		info.put("size", infoSize);

		try {
			assertEquals("Wrong file info", info, fits.getFileInfo(fitsOut));
		} catch (FitsException e) {
			fail("Unexpected error:" + e.getMessage());
			}
	}
	
	@Test
    public void testGetFileMetadata() {
		FitsOutput fitsOut = null;
		try {
			fitsOut = fits.examine(testObject);
		} catch (FitsException e) {
			fail("Unexpected error:" + e.getMessage());
		}

		Map<String, List<String>> info = new HashMap<String, List<String>>();
		List<String> infoXML = new ArrayList<String>();
		infoXML.add("XML");
		info.put("markupBasis", infoXML);
		List<String> infoVersion = new ArrayList<String>();
		infoVersion.add("1.0");
		info.put("markupBasisVersion", infoVersion);
		List<String> infoChar = new ArrayList<String>();
		infoChar.add("UTF-8");
		info.put("charset", infoChar);

		try {
			assertEquals("Wrong file info", info, fits.getMetadata(fitsOut));
		} catch (FitsException e) {
			fail("Unexpected error:" + e.getMessage());
			}
	}
}