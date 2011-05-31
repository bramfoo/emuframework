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

package eu.keep.characteriser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.harvard.hul.ois.fits.tools.ToolInfo;

import org.apache.log4j.Logger;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.consolidation.FitsIdentitySection;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.exceptions.FitsException;

/**
 * This is a wrapper class for the FITS tool available at
 * <a href="URL#http://code.google.com/p/fits/">http://code.google.com/p/fits/</a> It permits the
 * identification/characterisation of file formats using different tools,
 * reports conflicts, etc...
 * @author Bram Lohman
 * @author David Michel
 */
public class FitsTool {

    private static final Logger logger = Logger.getLogger(FitsTool.class.getName());
    private Fits                fits;

    /**
     * Constructor
     * @param home String representing the location of FITS_HOME
     */
    public FitsTool(String home) {
        try {
            fits = new Fits(home);
            logger.debug("FITS home is set to: " + home);
        }
        catch (FitsConfigurationException e) {
            logger.error("Cannot initialise FITS for characterisation: " + e.toString());
            throw new RuntimeException(e);
        }
    }

    /**
     * Examine a file and returns the FitsOutput
     * @param file representing the digital object to characterise
     * @return {@code FitsOutput} FITS specific XML output
     * @throws FitsException If an error occurs during examination
     */
    public FitsOutput examine(File file) throws FitsException {
    	FitsOutput fitsOut = fits.examine(file);
    	return fitsOut;
    }
	
    /**
     * Parse the FitsOutput of a file and returns a list of all formats identified by the
     * various tools available
     * @param fitsOut FITS output of the digital object
     * @return a list of {@code Format} objects
     */
    public List<Format> getFormats(FitsOutput fitsOut) {

        List<Format> fis = new ArrayList<Format>();

            List<FitsIdentitySection> fitsIdentities = fitsOut.getIdentities();
            for (FitsIdentitySection fitsIdentity : fitsIdentities) {

                String format = fitsIdentity.getFormat();
                String mime = fitsIdentity.getMimetype();

                List<String> tools = new ArrayList<String>();
                List<ToolInfo> toolsInfo = fitsIdentity.getReportingTools();
                for (ToolInfo ti : toolsInfo) {
                    String s = ti.getName() + " " + ti.getVersion();
                    tools.add(s);
                }

                // Some hacks for 'file utility', which tends to be far too verbose:
                if (format.contains("ISO 9660"))
                {
                	logger.debug("Renaming ISO format: " + format);
                	format = "ISO 9660 CD-ROM";
                }
                if (format.contains("Quark Express"))
                {
                	logger.debug("Renaming Quark format: " + format);
                	format = "Motorola Quark Express Document";
                }
                if (format.contains("ARJ archive"))
                {
                	logger.debug("Renaming ARJ format: " + format);
                	format = "ARJ archive data";
                }
                
                Format f = new Format(format, mime);
                f.setReportingTools(tools);

                fis.add(f);
            }
        return fis;
    }

    /**
     * Parse the FitsOutput of a file and list the file info identified by the
     * various tools available
     * @param fitsOut FITS output of the digital object
     * @return a Map of item names (as keys) and an associated list of values
     * @throws FitsException If an error occurs during examination
     */
    public Map<String, List<String>> getFileInfo(FitsOutput fitsOut) throws FitsException {
    	List<FitsMetadataElement> fitsFileInfo = fitsOut.getFileInfoElements();
    	List<FitsMetadataElement> fitsFileStatus = fitsOut.getFileStatusElements();
        Map<String, List<String>> fileInfo = new HashMap<String, List<String>>();

        if (fitsFileInfo == null && fitsFileStatus == null )
        {
        	logger.info("No file info available");
        	return fileInfo;
        }
    	for (FitsMetadataElement fitsMetadata : fitsFileInfo) {
    		logger.info("Adding file info: " + fitsMetadata.getName() + ": " + fitsMetadata.getValue() + "[" + fitsMetadata.getReportingToolName() + fitsMetadata.getReportingToolVersion() +"]");
    		if (fileInfo.containsKey(fitsMetadata.getName()))
    		{
    			fileInfo.get(fitsMetadata.getName()).add(fitsMetadata.getValue());
    		}
    		else
    		{
    			List<String> vals = new ArrayList<String>();
    			vals.add(fitsMetadata.getValue());
    			fileInfo.put(fitsMetadata.getName(), vals);
    		}
    	}
    	for (FitsMetadataElement fitsMetadata : fitsFileStatus) {
    		logger.info("Adding file info: " + fitsMetadata.getName() + ": " + fitsMetadata.getValue() + "[" + fitsMetadata.getReportingToolName() + fitsMetadata.getReportingToolVersion() +"]");
    		if (fileInfo.containsKey(fitsMetadata.getName()))
    		{
    			fileInfo.get(fitsMetadata.getName()).add(fitsMetadata.getValue());
    		}
    		else
    		{
    			List<String> vals = new ArrayList<String>();
    			vals.add(fitsMetadata.getValue());
    			fileInfo.put(fitsMetadata.getName(), vals);
    		}
    	}
    	
    	return fileInfo;
    }
    
    /**
     * Parse the FitsOutput of a file and list the metadata identified by the
     * various tools available
     * @param fitsOut FITS output of the digital object
     * @return a Map of metadata item names (as keys) and an associated list of values
     * @throws FitsException If an error occurs during examination
     */
    public Map<String, List<String>> getMetadata(FitsOutput fitsOut) throws FitsException {

        List<FitsMetadataElement> fitsMetadatas = fitsOut.getTechMetadataElements();
        Map<String, List<String>> techMD = new HashMap<String, List<String>>();

        if (fitsMetadatas == null)
        {
        	logger.info("No technical metadata available");
        	return techMD;
        }
    	for (FitsMetadataElement fitsMetadata : fitsMetadatas) {
    		logger.info("Adding tech. metadata: " + fitsMetadata.getName() + ": " + fitsMetadata.getValue() + "[" + fitsMetadata.getReportingToolName() + fitsMetadata.getReportingToolVersion() +"]");
    		if (techMD.containsKey(fitsMetadata.getName()))
    		{
    			techMD.get(fitsMetadata.getName()).add(fitsMetadata.getValue());
    		}
    		else
    		{
    			List<String> vals = new ArrayList<String>();
    			vals.add(fitsMetadata.getValue());
    			techMD.put(fitsMetadata.getName(), vals);
    		}
    	}
    	return techMD;
    }
}
