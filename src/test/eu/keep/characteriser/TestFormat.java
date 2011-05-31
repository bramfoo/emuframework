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

package eu.keep.characteriser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * A Junit Test class for {@link eu.keep.characteriser.Format}
 * @author Bram Lohman
 */
public class TestFormat {

	ArrayList<String> tools;
	Format form;
	
	@Before
	public void setUp(){
    	tools = new ArrayList<String>();
    	tools.add("Test tool 1");
    	tools.add("Test tool 2");
    	form = new Format("txt", "plain");
    	form.setReportingTools(tools);
	}

	@Test
    public void testGetters() {
    	assertEquals("Wrong name", "txt", form.getName());
    	assertEquals("Wrong mimeType", "plain", form.getMimeType());
    	assertEquals("Wrong tools", "Test tool 1", form.getReportingTools().get(0));
    }

	@Test
    public void testToString() {
    	assertEquals("Wrong toString(0 output", "txt:plain:[Test tool 1, Test tool 2]", form.toString());
    }

    @Test
    public void testEquals() {

        Format a = new Format("txt", "plain");
        Format b = new Format("txt", "plain");
        Format c = new Format("xml", "plain");
        Format d = new Format("exe", "binary stream");

        assertTrue("a.equals(b) method should return true when testing"
                + " two Format objects with the same content", a.equals(b));

        assertTrue("b.equals(a) method should return true when testing"
                + " two Format objects with the same content", b.equals(a));

        assertFalse("c.equals(a) method should return false when testing"
                + " two Format objects with the same content", c.equals(a));

        assertFalse("d.equals(a) method should return false when testing"
                + " two Format objects with the same content", d.equals(a));

        assertFalse("d.equals(c) method should return false when testing"
                + " two Format objects with the same content", d.equals(c));

        // Checking java.util.List.contains() method does use the Object's
        // equals() method
        List<Format> list = Arrays.asList(a, b, a, b);
        assertTrue("java.util.List.contains(a) method should return true", list.contains(a));
        assertTrue("java.util.List.contains(b) method should return true", list.contains(b));
        assertFalse("java.util.List.contains(c) method should return false", list.contains(c));
        assertFalse("java.util.List.contains(d) method should return false", list.contains(d));

    }

    @Test
    public void testHashCode() {

        Format a = new Format("txt", "plain");
        Format b = new Format("txt", "plain");
        Format c = new Format("xml", "plain");

        Integer ia = new Integer(a.hashCode());
        Integer ib = new Integer(b.hashCode());
        Integer ic = new Integer(c.hashCode());

        assertTrue("a and b have the same content and should return the same hashcode", ia
                .equals(ib));
        assertFalse("a and c do not have the same content and should return a different hashcode",
                ia.equals(ic));
        assertFalse("a and b do not have the same content and should return a different hashcode",
                ib.equals(ic));

    }

}