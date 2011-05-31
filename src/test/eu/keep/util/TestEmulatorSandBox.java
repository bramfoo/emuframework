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

import org.junit.Test;

import eu.keep.util.EmulatorSandBox;
import eu.keep.util.EmulatorSandBox.ExitException;

/**
 * A Junit Test class for {@link eu.keep.util.EmulatorSandBox}
 * @author David Michel
 */
public class TestEmulatorSandBox {

    private EmulatorSandBox emulatorSandBox;

    @Test(expected = ExitException.class)
    public void testSystemExitMethod() {
        // Open the sandbox environment
        emulatorSandBox = new EmulatorSandBox();
        emulatorSandBox.open();

        // Critical bit of code running potentially unsafe/sensitive operations
        System.exit(0);

        // Close the sandbox environment
        emulatorSandBox.close();
    }
}