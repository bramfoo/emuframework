/*
* $Revision: 272 $ $Date: 2010-03-31 10:23:48 +0200 (Wed, 31 Mar 2010) $
* $Author: BKiers $
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

package eu.keep.core;

import jline.Completor;

import javax.swing.*;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ClassAndFileCompletor implements Completor {

    private static String FILE_CHOOSER_POPUP    = "FILE";
    private static String WRAP_FILE_NAME_BEFORE = "new java.io.File(\"";
    private static String WRAP_FILE_NAME_AFTER  = "\")";

    private static final String  STR           = "\"(?:\\\\.|[^\\\\\"])*\"";
    private static final String  CHR           = "'(?:\\\\.|[^\\\\'])+'";
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "(?x)" + " (" + STR + "|" + CHR + "|[^=\\s])+ " + // match a String, char or
                                                              // anything other than spaces
            " |                                           " + // OR
            " =                                           " + // the '=' sign
            " |                                           " + // OR
            " \\s+                                        "   // match spaces
    );

    final EFCliAutoComp          cli;

    public ClassAndFileCompletor(EFCliAutoComp c) {
        cli = c;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public int complete(String buffer, int cursor, List candidates) {

        if (buffer.isEmpty())
            return 0;

        if(buffer.endsWith(FILE_CHOOSER_POPUP)) {
            // strip the FILE_CHOOSER_POPUP keyword from the buffer
            buffer = buffer.substring(0, buffer.length() - FILE_CHOOSER_POPUP.length());

            // create an OS file chooser GUI
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // append the file's absolute file name yo the buffer wrapped with quotes
                buffer = buffer + WRAP_FILE_NAME_BEFORE +
                        fileChooser.getSelectedFile().getAbsolutePath() +
                        WRAP_FILE_NAME_AFTER;
            }

            // replace all backslashes with forward slashes since BeanShell treats backslashes special
            buffer = buffer.replace('\\', '/');

            // put the buffer back as the sole candidate
            candidates.add(buffer);
        }
        else {
            buffer = buffer.substring(0, cursor);

            List<String> tokens = tokenize(buffer);
            String tokenToComplete = tokens.get(tokens.size() - 1);

            String before = join(tokens, 0, tokens.size() - 1);

            if (matchLocation(before, tokenToComplete, candidates))
                return 0;

            int indexLastDot = tokenToComplete.lastIndexOf('.');
            if (indexLastDot > -1) {
                String object = tokenToComplete.substring(0, indexLastDot);
                String startMethod = tokenToComplete.substring(indexLastDot + 1);
                Object evaluated = cli.evaluate(object, false);
                if (cli.classMap.isClass(object)) {
                    if (matchStaticMethods(before + object + ".", object, startMethod, candidates))
                        return 0;
                }
                else if (evaluated != null) {
                    String className = evaluated.getClass().getName();
                    if (matchInstanceMethods(before + object + ".", className, startMethod, candidates))
                        return 0;
                }
            }

            candidates.addAll(cli.classMap.queryClasses(before, tokenToComplete));
        }
        return 0;
    }

    private int countUnEscaped(char ch, String str) {
        int index = 0;
        int count = 0;
        while (index < str.length()) {
            char temp = str.charAt(index);
            if (temp == '\\') {
                index++;
            }
            else if (temp == ch) {
                count++;
            }
            index++;
        }
        return count;
    }

    private String join(List<String> tokens, int start, int end) {
        StringBuilder b = new StringBuilder();
        for (int i = start; i < end; i++) {
            b.append(tokens.get(i));
        }
        return b.toString();
    }

    private int lastUnEscaped(char ch, String str) {
        int index = 0;
        int last = -1;
        while (index < str.length()) {
            char temp = str.charAt(index);
            if (temp == '\\') {
                index++;
            }
            else if (temp == ch) {
                last = index;
            }
            index++;
        }
        return last;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean matchInstanceMethods(String typed, String className, String startMethod,
            List candidates) {
        List<String> possible = cli.classMap.queryInstanceMethods(typed, className, startMethod);
        candidates.addAll(possible);
        return !candidates.isEmpty();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean matchLocation(String typed, String possibleLocation, List candidates) {

        if (countUnEscaped('\"', possibleLocation) % 2 != 1)
            return false;

        possibleLocation = possibleLocation.replace('\\', '/');

        int indexLastQuote = lastUnEscaped('"', possibleLocation);
        typed += possibleLocation.substring(0, indexLastQuote);
        possibleLocation = possibleLocation.substring(indexLastQuote + 1);

        if (possibleLocation.startsWith("~")) {
            possibleLocation = System.getProperty("user.home") + "/"
                    + possibleLocation.substring(1);
        }
        else if (possibleLocation.startsWith(".")) {
            possibleLocation = System.getProperty("user.dir") + "/" + possibleLocation.substring(1);
        }

        int indexLastFS = possibleLocation.lastIndexOf("/");

        if (indexLastFS == -1)
            return false;

        File dir = new File(possibleLocation.substring(0, indexLastFS + 1));
        final String toComplete = possibleLocation.substring(indexLastFS + 1);

        if (!dir.isDirectory())
            return false;

        File[] contents = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().startsWith(toComplete);
            }
        });
        for (File f : contents) {
            candidates.add(typed + "\"" + f.getAbsolutePath().replace('\\', '/'));
        }
        return !candidates.isEmpty();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean matchStaticMethods(String typed, String className, String startMethod,
            List candidates) {
        List<String> possible = cli.classMap.queryStaticMethods(typed, className, startMethod);
        candidates.addAll(possible);
        return !candidates.isEmpty();
    }

    private List<String> tokenize(String buffer) {
        List<String> tokens = new ArrayList<String>();
        Matcher m = TOKEN_PATTERN.matcher(buffer);
        while (m.find()) {
            tokens.add(m.group());
        }
        return tokens;
    }
}
