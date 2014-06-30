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

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a file format that contains information about its name,
 * MIME type and a list of the reporting tools.
 * The {@code Format} object is represented by the file format name {@code name}
 * ,the MIME type {@code mimeType} and the list of reporting tools {@code
 * reportingTools} that were used to identify the file format.
 * @author David Michel
 */
public class Format {

    public final String  name;
    public final String  mimeType;

    private List<String> reportingTools;

    /**
     * Constructor
     * @param name String representing the format name
     * @param mimeType String representing the mime type
     */
    public Format(String name, String mimeType) {
        this.name = name;
        this.mimeType = mimeType;

        // initialise the list of reporting tools
        reportingTools = new ArrayList<String>();
    }

    /**
     * Returns a {@code String} object representing this {@code Format}'s
     * content
     * @return a string representation of the content of this pathway
     */
    @Override
    public String toString() {

        return name + ":" + mimeType + ":" + reportingTools.toString();
    }

    /**
     * Returns a list of the reporting tools. List is empty oif no tools were
     * used.
     * @return a list of {@code String} representing the various reporting tools
     *         used to identify this format.
     */
    public List<String> getReportingTools() {
        return new ArrayList<String>(reportingTools);
    }

    /**
     * Set the list of reporting tools used to identity the format
     * @param reportingTools List of {@code String} representing the reporting
     *            tools.
     */
    public void setReportingTools(List<String> reportingTools) {
        this.reportingTools.addAll(reportingTools);
    }

    /**
     * Compare this format to the specified format. Returns @{code true} if
     * the content of both @{code Format} objects are identical
     * @param o {@code Object} the reference object with which to compare.
     * @return {@code true} if the two object are equal, {@code false} otherwise
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {

        // sanity checks
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        Format that = (Format) o;

        if (!this.getName().equals(that.getName())) {
            return false;
        }
        if (!this.getMimeType().equals(that.getMimeType())) {
            return false;
        }
        if (!this.getReportingTools().equals(that.getReportingTools())) {
            return false;
        }

        return true;
    }

    /**
     * Returns a hash code value for the {@code Format} object
     * @return the generated hashcode as a {@code int} depending on {@code name},
     *         {@code mimeType} and each element in {@code reportnigTools}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 1;

        hash ^= this.getName().hashCode();
        hash ^= this.getMimeType().hashCode();

        for (String s : this.getReportingTools()) {
            hash &= s.hashCode();
        }

        return hash;
    }

    /**
     * Returns the file format name
     * @return a string representation of the file format name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the mime type of the file format
     * @return a string representation of the file format's mime type.
     */
    public String getMimeType() {
        return mimeType;
    }

}
