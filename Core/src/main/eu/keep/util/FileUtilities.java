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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

/**
 * Common shared file utilities, such as copy, unzip, etc.
 * @author Bram Lohman
 * @author David Michel
 */
public abstract class FileUtilities {

    protected static Logger  logger;
    private static final int COPY_BUFFER_SIZE = 8192;

    static {
        logger = Logger.getLogger(FileUtilities.class.getName());
    }

    /**
     * Copy an InputStream into an OutputStream
     * @param in instance of {@code InputStream} object
     * @param out instance of {@code OutputStream} object
     * @throws IOException If a file error occurs
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[COPY_BUFFER_SIZE];

        int count;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
    }

    /**
     * Copy a source file to a new target file
     * @param source name of the source file
     * @param target name of the target file
     * @throws IOException If a file error occurs
     */
    public static void copy(File source, File target) throws IOException {
        // Open input and output
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(target);

        try {
            copy(in, out);
        }
        finally {
            // Close input and output
            in.close();
            out.close();
        }
    }

    /**
     * Copy a source file to an OutputStream
     * @param file source file
     * @param out instance of a {@code OutputStream} object
     * @throws java.io.IOException
     */
    public static void copy(File file, OutputStream out) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            copy(in, out);
        }
        finally {
            in.close();
        }
    }

    /**
     * Copy InputStream to an target file
     * @param in instance of the {@code InputStream} object to be copied
     * @param file target name of the target file
     * @throws java.io.IOException
     */
    public static void copy(InputStream in, File file) throws IOException {
        OutputStream out = new FileOutputStream(file);
        try {
            copy(in, out);
        }
        finally {
            out.close();
        }
    }

    /**
     * Generate a unique folder name in a specific location
     * @param dir File representing the location where the unique folder should
     *            be created
     * @return File unique directory created if successful, null otherwise
     */
    public static File GenerateUniqueDir(File dir) throws FileNotFoundException {
        File uniqueDir;
        UUID id;

        try {
            // If exec location doesn't exist, create it
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // generate a new unique ID
            id = UUID.randomUUID();

            // create directory with the generated ID for name in the dir
            // location
            uniqueDir = new File(dir, id.toString());

                uniqueDir.mkdirs();
        }
            catch (SecurityException e) {
                logger.error("Cannot create unique directory in : " + dir);
                throw new FileNotFoundException(e.getMessage());
            }
        // Ensure dir exists
            if (!uniqueDir.exists())
            {
                throw new FileNotFoundException("Could not succesfully create " + uniqueDir);
            }
        return uniqueDir;
    }

    /**
     * Delete a folder and its content recursively
     * @param dir File representing the directory to delete
     * @return {@code true} when successful, {@code false} otherwise
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
//                logger.debug("Recursing into: " + dir);        
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        logger.debug("Deleting: " + dir);
        return dir.delete();
    }

    /**
     * Returns the file extension as the substring starting from the last '.'
     * @param file file to get the extension of
     * @return a string representation of the found file extension
     */
    public static String getExtension(File file) {
        String fileName = file.getName();
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
        return ext;
    }

    /**
     * Reads a properties file from a location specified as a String in its argument
     * @param userPropertiesFileName String representing the properties file path
     * @return a {@code java.util.Properties} object
     * @throws IOException
     */
    public static Properties getProperties(String userPropertiesFileName) throws IOException {
        // Read the properties file
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(userPropertiesFileName);
            return getProperties(fis);
        } catch (FileNotFoundException e) {
            logger.warn("Could not find properties file [" + userPropertiesFileName + "]: "
                    + e.toString());
            throw new IOException("Could not find properties file [" + userPropertiesFileName + "]: ", e);
        } catch (IOException e) {
            logger.warn("Failed to read properties file [" + userPropertiesFileName + "]: "
                    + e.toString());
            throw e;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                // Hmm... hoping not to get this far into catching exceptions;
                // we'll just log it and proceed...
                logger.fatal("Failed to close open file: [" + userPropertiesFileName + "]: "
                        + e.toString());
            }
        }
    }

    /**
     * Reads a properties file from an {@code InputStream} object
     * @param inputStream {@code java.io.InputStream} representing the properties file path
     * @return a {@code java.util.Properties} object
     * @throws IOException
     */
    public static Properties getProperties(InputStream inputStream) throws IOException {
        // Read the properties file
        Properties props = new Properties();
        try {
            props.load(inputStream);
            logger.debug("Correctly read properties file: " + inputStream);
        } catch (IOException e) {
            logger.warn("Failed to read properties file [" + inputStream + "]: "
                    + e.toString());
            throw e;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                // Hmm... hoping not to get this far into catching exceptions;
                // we'll just log it and proceed...
                logger.fatal("Failed to close open file: [" + inputStream + "]: "
                        + e.toString());
            }
        }
        return props;
    }

    /**
     * Unpack a ZIP file into a target location and returns the list of
     * extracted files
     * @param zipFile file archive to be unpacked
     * @param directory target directory of contents
     * @return List of files extracted from archive
     */
    public static List<File> unZip(File zipFile, File directory) throws IOException {
        List<File> unzippedFiles = new ArrayList<File>();

        ZipFile zfile = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> entries = zfile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            // logger.debug("Extracting '" + entry + "' from package '" +
            // zfile.getName() + "'.");

            File destination = new File(directory, entry.getName());
            if (entry.isDirectory()) {
                destination.mkdirs();
            }
            else {
                destination.getParentFile().mkdirs();
                InputStream in = zfile.getInputStream(entry);
                try {
                    copy(in, destination);
                }
                finally {
                    in.close();
                }
            }

            // Add the unzipped file to the list
            unzippedFiles.add(destination);
        }
        return unzippedFiles;
        }
    
    /**
     * Get a fixed rate timer that has the given delay, period and prints the given message
     * @param delay Delay before the timer starts (in msec)
     * @param period Frequency of performing the action
     * @param message The message to print to System.out
     * @return Initialized Timer object
     */
    public static Timer getFixedRateTimer(int delay, int period, final String message){
        logger.debug("Starting (delayed + [" + delay + "msec]) timer, [" + period + "msec]) period");
    	Timer timer = new Timer();
        
        TimerTask timerTask = new TimerTask() 
        { 
            public void run() { 
            	// FIXME: Find better way of printing message
                System.out.print(message);
            }
        };
        
        timer.scheduleAtFixedRate(timerTask, delay, period);
        
        return timer;
    }
}