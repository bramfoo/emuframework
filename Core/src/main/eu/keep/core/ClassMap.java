/*
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

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A class that holds all classes, and their methods, in the classpath.
 * Used for auto-completion.
 */
class ClassMap {

    /**
     * Maps all classes to their first character for fast retrieval:
     * ...
     * 'C' -> {String, StringBuilder, StringBuffer, ...}
     * ...
     * 'L' -> {List, ...}
     * ...
     */
    private SortedMap<Character, SortedSet<String>> classes;

    /**
     * Maps all names (short and full names) to the full name:
     * 'Integer -> java.lang.Integer' and 'java.lang.Integer ->
     * java.lang.Integer'
     */
    private Map<String, String>                     classNames;

    ClassMap() {
        classes = new TreeMap<Character, SortedSet<String>>();
        classNames = new HashMap<String, String>();
        init();
    }

    /**
     * Returns a sorted set of all class names of <code>className</code>.
     * @param className the class name to get all methods of.
     * @param instance whether to get the instance, or static methods of
     *            <code>className</code>.
     * @return a sorted set of all class names of <code>className</code>.
     */
    SortedSet<String> findMethods(String className, boolean instance) {
        SortedSet<String> set = new TreeSet<String>();
        try {
            String fullName = classNames.get(className);
            Class clazz = Class.forName(fullName);
            Method[] methodsArr = clazz.getMethods();
            for (Method m : methodsArr) {
                String mStr = m.toString();

                // only keep 'METHOD-NAME(PARAMS...)'
                mStr = mStr.replaceAll(".*\\.([^.]+\\([^)]*\\)).*", "$1");

                // remove package information: removes 'java.lang.' from
                // 'java.lang.Integer'
                mStr = mStr.replaceAll("(\\w+\\.)+", "");

                // compact the string by removing all spaces
                mStr = mStr.replace(" ", "");

                if (Modifier.isStatic(m.getModifiers()) != instance) {
                    set.add(mStr);
                }
            }
        }
        catch (Exception e) { /* no need to report this */
        }
        catch (Error e) { /* no need to report this */
        }
        return set;
    }

    /**
     * Initialize this class: read/index all classes in this CLASSPATH.
     */
    private void init() {
        String classPath = System.getProperty("java.class.path") + EFCliAutoComp.PS
                + System.getProperty("java.home") + EFCliAutoComp.FS + "lib";
        String[] classPaths = classPath.split(EFCliAutoComp.PS);
        for (String s : classPaths) {
            File f = new File(s);
            if (f.isFile() && f.getName().endsWith(".jar")) {
                initJAR(f);
            }
            else if (f.isDirectory()) {
                initRecursive(f, f);
            }
        }
    }

    /**
     * Recursively read/index all classes from <code>dir</code>.
     * @param dir the directory from which to read .class files.
     * @param root the root of the CLASSPATH.
     */
    private void initRecursive(File dir, final File root) {
        if (dir.getName().equals("test"))
            return;
        File[] contents = dir.listFiles();
        for (File f : contents) {
            if (f.isFile() && f.getName().endsWith(".jar")) {
                initJAR(f);
            }
            else if (f.isFile() && f.getName().endsWith(".class")) {
                String classFile = f.getAbsolutePath().replace(root.getAbsolutePath(), "")
                        .substring(1);
                String name = classFile.replace("\\", ".").replace("/", ".").replaceAll(
                        "\\.class$", "");
                String[] tokens = name.split("\\.");
                String shortName = tokens[tokens.length - 1];
                put(name);
                put(shortName);
                classNames.put(shortName, name);
                classNames.put(name, name);
            }
            else if (f.isDirectory()) {
                initRecursive(f, root);
            }
        }
    }

    /**
     * Read all entries from a JAR file. Ignores classes not in the public API
     * (sun.*, com.sun.*, etc.).
     * @param jar the JAR file whose .class files to index.
     */
    private void initJAR(File jar) {
        try {
            ZipInputStream zipStream = new ZipInputStream(new FileInputStream(jar));
            ZipEntry entry;

            while ((entry = zipStream.getNextEntry()) != null) {
                String name = entry.toString();

                if (name.endsWith("\\") || name.endsWith("/") || name.contains("$")
                        || !name.endsWith(".class") || name.startsWith("com")
                        || name.startsWith("sun") || name.startsWith("1.0")) {
                    continue;
                }

                // only index classes whose package starts with 'eu.keep.' or
                // 'java.'
                if (!(name.startsWith("eu/keep/") || name.startsWith("java/"))) {
                    continue;
                }

                name = name.replace("\\", ".").replace("/", ".").replaceAll("\\.class$", "");
                String[] tokens = name.split("\\.");
                String shortName = tokens[tokens.length - 1];

                put(name);
                put(shortName);

                classNames.put(shortName, name);
                classNames.put(name, name);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check is a class is present in the current CLASSPATH.
     * @param name the full, or short class name to check.
     * @return <code>true</code> iff a class is present in the current
     *         CLASSPATH.
     */
    boolean isClass(String name) {
        return classNames.containsKey(name);
    }

    /**
     * Add a class name to the Map&lt;Character, Set&gt;.
     * @param name the class name.
     */
    private void put(String name) {
        if (name == null || name.isEmpty())
            return;
        SortedSet<String> existing = classes.remove(name.charAt(0));
        if (existing == null)
            existing = new TreeSet<String>();
        existing.add(name);
        classes.put(name.charAt(0), existing);
    }

    /**
     * Returns a list of class names that start with a certain String:
     * <code>start</code>.
     * The String: <code>before</code> is pre-pended to it.
     * For example: <code>queryClasses("java.lang.", "Str")</code> might return
     * 
     * <code>["java.lang.String", "java.lang.StringBuffer", "java.lang.StringBuilder"]</code>
     * ,
     * and <code>queryClasses("", "Str")</code> might return
     * <code>["String", "StringBuffer", "StringBuilder"]</code>.
     * @param before the String to pre-pend to each entry.
     * @param start the start of the class- or method name.
     * @return a list of class names that start with a certain String:
     *         <code>start</code>.
     */
    List<String> queryClasses(String before, String start) {
        List<String> hits = new ArrayList<String>();
        if (!start.isEmpty()) {
            SortedSet<String> set = classes.get(start.charAt(0));
            if (set == null)
                return hits;
            for (String s : set) {
                if (s.startsWith(start)) {
                    hits.add(before + s);
                }
                else if (hits.size() > 0) {
                    // we've lexicographically passed the 'start' string: save
                    // to terminate the loop.
                    break;
                }
            }
        }
        return hits;
    }

    /**
     * Query for instance methods starting with the string <code>start</code>
     * from a given class.
     * @param typed the entire string already typed on the console by the user
     *            (this is pre-pended
     *            to each String in the list being returned).
     * @param className the name of the class to query.
     * @param start the start of the method name.
     * @return a list of method names starting with the string
     *         <code>typed</code>.
     */
    List<String> queryInstanceMethods(String typed, String className, String start) {
        return queryMethods(typed, className, start, true);
    }

    /**
     * Query for static methods starting with the string <code>start</code> from
     * a given class.
     * @param typed the entire string already typed on the console by the user
     *            (this is pre-pended
     *            to each String in the list being returned).
     * @param className the name of the class to query.
     * @param start the start of the method name.
     * @return a list of method names starting with the string
     *         <code>typed</code>.
     */
    List<String> queryStaticMethods(String typed, String className, String start) {
        return queryMethods(typed, className, start, false);
    }

    /**
     * Query for methods starting with the string <code>start</code> from a
     * given class.
     * @param typed the entire string already typed on the console by the user
     *            (this is pre-pended
     *            to each String in the list being returned).
     * @param className the name of the class to query.
     * @param start start the start of the method name.
     * @param instance if <code>true</code>, instance methods are returned, else
     *            static methods.
     * @return a list of method names starting with the string
     *         <code>typed</code>.
     */
    private List<String> queryMethods(String typed, String className, String start, boolean instance) {
        List<String> hits = new ArrayList<String>();
        SortedSet<String> methodsSet = findMethods(className, instance);
        if (methodsSet == null)
            return hits;

        for (String m : methodsSet) {
            if (m.startsWith(start)) {
                hits.add(typed + m);
            }
            else if (hits.size() > 0) {
                break;
            }
        }
        return hits;
    }

    /**
     * Returns a string representation of this class.
     * @return a string representation of this class.
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<Character, SortedSet<String>> entry : classes.entrySet()) {
            b.append(entry.getKey()).append(" -> ").append(entry.getValue()).append('\n');
        }
        return b.toString();
    }

    /**
     * Returns a list of fully qualified class names given a (short) class name.
     * For example,
     * given the (short) class name <code>"List"</code>, the list
     * <code>["java.util.List", "java.awt.List"]</code> might be returned.
     * @param className the (short) name of the class.
     * @return a list of fully qualified class names given a (short) class name.
     */
    List<String> whichClasses(String className) {
        List<String> hits = new ArrayList<String>();
        for (String shortName : classNames.keySet()) {
            String longName = classNames.get(shortName);
            if (longName.equalsIgnoreCase(className)) {
                hits.add(longName);
            }
        }
        if (hits.isEmpty()) {
            hits.add("no such class name: '" + className + "'");
        }
        return hits;
    }
}
