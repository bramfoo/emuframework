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

package eu.keep.controller.emulatorRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ProcessTesterQuit {

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length != 1)
            return;

        File processDir = new File(args[0]);
        File startFile = new File(processDir, "started.txt");
        int i = 0;

        // Started
        try {
            writeLine(startFile, i++);
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            System.out.println("Sleeping...");
            Thread.sleep(3000);
            writeLine(startFile, i++);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeLine(File startFile, int i) throws IOException {
        FileWriter fstream = new FileWriter(startFile);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("Line " + i + ": " + System.currentTimeMillis());
        out.close();
    }
}
