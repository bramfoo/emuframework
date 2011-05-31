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

import java.lang.SecurityManager;
import java.lang.SecurityException;
import java.security.Permission;

/**
 * Modified {@code java.lang.SecurityManager} for running emulator in a sandbox
 * environment where certain actions are disabled.
 * For now, only {@code system.exit()} is disabled but other actions can be
 * added to this 'black list'.
 * Usage is very readily achieved:
 * <blockquote> {@code
 * EmulatorSandBox emulatorSandBox= new EmulatorSandBox();
 * emulatorSandBox.open();
 * // perform potential unsafe code
 * emulatorSandBox.close(); * } </blockquote>
 * @author David Michel
 */
public class EmulatorSandBox {

    private SecurityManager defaultSecurityManager;

    /**
     * Constructor that initialise a new sandbox environment. backups the
     * previous {@code SecurityManager} in order to be able to restore it later
     * if required.
     */
    public EmulatorSandBox() {
        // Get the {@code SecurityManager} currently in use.
        defaultSecurityManager = System.getSecurityManager();
    }

    /**
     * Starts the sandbox environment, i.e. set a new {@code SecurityManager}
     * with modified permission(s)
     */
    public void open() {
        System.setSecurityManager(new NoExitSecurityManager());
    }

    /**
     * Stops the sandbox environment, i.e. reset the default (backed-up) {@code
     * SecurityManager}
     */
    public void close() {
        System.setSecurityManager(defaultSecurityManager);
    }

    /**
     * Class defining an new 'exit' exception used whenever {@code
     * System.exit()} is called
     */
    @SuppressWarnings("serial")
	public static class ExitException extends SecurityException {
        public final int status;

        public ExitException(int status) {
            super("Security violation: System.exit() disabled");
            this.status = status;
        }
    }

    /**
     * Class representing the new security manager with modified permission(s)
     * It overrides the checkExit method and throw a ExitException allowing the
     * client code
     * to catch any System.exit() calls.
     */
    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
            // allow anything.
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
            // allow anything.
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }
}