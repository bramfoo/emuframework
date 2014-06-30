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

package eu.keep.core;

import bsh.*;
import eu.keep.kernel.CoreEngineModel;
import eu.keep.kernel.CoreObserver;
import eu.keep.kernel.Kernel;
import jline.*;

import java.io.*;
import java.util.List;

/**
 * <p>
 * A class to test the EF through a command line interface (CLI). The CLI
 * accepts valid Java code in a dynamic, scripted fashion by using BeanShell's
 * <code>Interpreter</code> class. The commands entered by the user are read
 * using the 3rd party library JLine.
 * </p>
 * <p>
 * When running this class, al classes in the current CLASSPATH are indexed and
 * can afterwards be used in this class' auto-complete feature. The
 * auto-complete can be used by typing part of the class' name, or method name,
 * and then pressing the TAB key. When the TAB is pressed and there are an
 * uneven number of (un-escaped) double quote characters present, a location
 * (file or directory) will be tried to complete. The '~' denotes the user's
 * home directory and the '.' the present working directory.
 * </p>
 * <p>
 * Note that the interpreter by default is already registered with one variable:
 * <code>m</code>, which points an instance of a
 * <code>eu.keep.kernel.Kernel</code> class.
 * </p>
 */
public class EFCliAutoComp implements CoreObserver {

    /** a short-cut for this system's file separator */
    static final String   FS               = System.getProperty("file.separator");

    /** a short-cut for this system's path separator */
    static final String   PS               = System.getProperty("path.separator");

    /** the location of the user defined BeanShell script loaded at start-up */
    static final String   SCRIPTS_LOCATION = "testData";

    /** the prompt printed by the cli */
    final String          prompt           = "\nEF-core$ ";

    /** the model */
    final CoreEngineModel model;

    /** the properties file name*/
    final String   PROP_FILE_NAME = "user.properties";

    /** */
    final ConsoleReader   console;

    /**
     * the stream to which to write the messages. Default set to
     * <code>System.out</code>
     */
    final PrintStream     out;

    /** the BeanShell interpreter evaluating the user commands */
    final Interpreter     interpreter;

    /**
     * a class holding all information about the classes (and their methods)
     * from the current CLASSPATH
     */
    final ClassMap        classMap;

    /**
     * Creates a new EFCliAutoComp piping all messages to the
     * <code>System.out</code>.
     * @throws EvalError if no interpreter can be instantiated.
     */
    public EFCliAutoComp() throws EvalError {
        this(System.out);
    }

    /**
     * Creates a new EFCliAutoComp.
     * @param stream the IO-stream to write all messages to.
     * @throws EvalError if no interpreter can be instantiated.
     */
    public EFCliAutoComp(PrintStream stream) throws EvalError {
        try {
			model = new Kernel(PROP_FILE_NAME);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error initialising Kernel: " + e.toString());
		}
        console = getConsole();
        out = stream;
        interpreter = getInterpreter();

        if (console == null) {
            echo("unable to create a console for your system");
            System.exit(666);
        }

        echo("* loading custom BeanShell scripts ......... ");
        loadScripts();
        echo("done\n");

        echo("* reading all classes from the classpath ... ");
        classMap = new ClassMap();
        echo("done\n");

        prompt();
    }

    /**
     * Prints a message to this class' output stream.
     * @param message the message to print.
     */
    void echo(String message) {
        out.print(message);
        out.flush();
    }

    /**
     * Evaluates a command entered by the user.
     * @param command the command entered by the user.
     * @param displayError if <code>true</code>, the error message thrown by
     *            BeanShell's
     *            interpreter is also printed, else nothing is printed.
     * @return the evaluated Object returned by BeanShell's interpreter.
     */
    Object evaluate(String command, boolean displayError) {
        try {
            return interpreter.eval(command);
        }
        catch (EvalError e) {
            if (displayError) {
                echo(e.getMessage() + "\n");
                echo(e.getScriptStackTrace());
            }
            return null;
        }
    }

    /**
     * Tries to create a JLine console.
     * @return a JLine console, or <code>null</code> if not possible for the
     *         underlying OS.
     */
    private ConsoleReader getConsole() {
        try {
            ConsoleReader c = new ConsoleReader();
            c.setBellEnabled(false);
            c.addCompletor(new ClassAndFileCompletor(this));
            c.setCompletionHandler(new CandidatesCompletionHandler());
            return c;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a interpreter with a EF-model registered in it.
     * @return a interpreter with a EF-model registered in it.
     */
    public Interpreter getInterpreter() {
        Interpreter i = new Interpreter();
        model.registerObserver(this);
        try {
            i.set("m", model);
        }
        catch (EvalError err) {
            err.printStackTrace();
        }
        return i;
    }

    /**
     * Loads all custom BeanShell scripts located in
     * <code>SCRIPTS_LOCATION</code>.
     */
    private void loadScripts() {
        File root = new File(SCRIPTS_LOCATION);
        if (!root.isDirectory())
            return;
        File[] scripts = root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && file.getName().endsWith(".bsh");
            }
        });
        for (File script : scripts) {
            try {
                interpreter.source(script.getAbsolutePath());
            }
            catch (Exception e) {
                echo(e.getMessage() + "\n");
            }
        }
    }

    /**
     * Prompt the user for a command.
     */
    private void prompt() {
        try {
            String line;
            while ((line = console.readLine(prompt)) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                    echo("successful shutdown? " + model.stop() + "\n\n");
                    return;
                }
                else if (line.equals("vars")) {
                    NameSpace ns = interpreter.getNameSpace();
                    Variable[] vars = ns.getDeclaredVariables();
                    for (Variable var : vars) {
                        if (var.getName().equals("bsh"))
                            continue;
                        Object value = null;
                        try {
                            value = interpreter.get(var.getName());
                        }
                        catch (EvalError err) {
                            err.printStackTrace();
                        }
                        echo(var.getName() + " = " + value
                                + (value != null ? " (" + value.getClass().getName() + ")" : "")
                                + "\n");
                    }
                }
                else if (line.matches("\\s*which\\s+\\w+\\s*")) {
                    String className = line.trim().split("\\s+")[1];
                    List<String> candidates = classMap.whichClasses(className);
                    for (String c : candidates) {
                        echo(c + "\n");
                    }
                }
                else {
                    Object evaluated = evaluate(line, true);
                    echo(evaluated + "\n");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void update(String updateNews) {
        echo(updateNews + "\n");
    }

    /**
     * The main entry point of this class.
     * @param args command line parameters, which are ignored.
     */
    public static void main(String[] args) {
        try {
            new EFCliAutoComp();
        }
        catch (EvalError err) {
            err.printStackTrace();
        }
    }
}
