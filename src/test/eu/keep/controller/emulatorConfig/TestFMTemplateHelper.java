package eu.keep.controller.emulatorConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import eu.keep.controller.ConfigEnv;
import eu.keep.softwarearchive.pathway.HardwarePlatformType;
import eu.keep.softwarearchive.pathway.ObjectFormatType;
import eu.keep.softwarearchive.pathway.Pathway;

/**
 * A Junit Test class for {@link eu.keep.controller.emulatorConfig.FMTemplateHelper}
 * @author Bram Lohman
 */
public class TestFMTemplateHelper {

	FMTemplateHelper fm;
	File emuDir;
	File emuExec;
	Pathway mockedPW;
	List<File> digObjs;
	List<File> swImgs;
	
	@Before
    public void setup() {

		fm = new FMTemplateHelper();
		emuDir = new File("testData/");
		emuExec = new File("testData/digitalObjects/text.txt.xml");
		
		mockedPW = mock(Pathway.class);
		ObjectFormatType mockedObj = mock(ObjectFormatType.class);
		HardwarePlatformType mockedHW = mock(HardwarePlatformType.class);
		when(mockedPW.getObjectFormat()).thenReturn(mockedObj);
		when(mockedPW.getHardwarePlatform()).thenReturn(mockedHW);
		when(mockedObj.getId()).thenReturn("MOCK-0001");
		when(mockedHW.getName()).thenReturn("MOCK-0002");
		
		digObjs = new ArrayList<File>();
		digObjs.add(new File("testData/digitalObjects/x86/build.xml"));

		swImgs = new ArrayList<File>();
		swImgs.add(new File("testData/digitalObjects/HardDiskImage/FreeDOS09_blocek.img.zip"));
	}
	
	@Test
    public void testCreateConfigEnv() throws IOException {
	
		ConfigEnv env = fm.createConfigEnv(emuDir, emuExec, mockedPW);
		
		assertEquals("Incorrect directory", emuDir, env.getEmuDir());
		assertEquals("Incorrect executable", emuExec, env.getEmuExec());
		assertNotNull("Options not set", env.getOptions());
		assertTrue("Options not empty", env.getOptions().isEmpty());
		assertEquals("Incorrect pathway attribute", "MOCK-0001", env.getPathway().getObjectFormat().getId());
		assertNotNull("TemplateBuilder not set", env.getTemplateBuilder());
	}
	
	@Test
    public void testGenerateDefaults() throws IOException {

		Map<String, List<Map<String, String>>> opts;
		ConfigEnv env = fm.createConfigEnv(emuDir, emuExec, mockedPW);
		opts = fm.generateDefaults(env, digObjs, swImgs);
		
		assertTrue("Root node not found", opts.containsKey("root"));
		assertFalse("Root node should not be empty", opts.get("root").isEmpty());
		assertEquals("Root->digobj not set correctly", digObjs.get(0).getAbsolutePath(), opts.get("root").get(0).get("digobj"));
		assertEquals("Root->configFile not set correctly", "noConfFileDefined", opts.get("root").get(0).get("configFile"));
		assertEquals("Root->configDir not set correctly", emuDir.toString(), opts.get("root").get(0).get("configDir"));
		
		assertTrue("Floppy node not found", opts.containsKey("floppyDisks"));
		assertFalse("Floppy node should not be empty", opts.get("floppyDisks").isEmpty());
		assertEquals("Floppy->inserted not set correctly", "true", opts.get("floppyDisks").get(0).get("inserted"));
		assertEquals("Floppy->num not set correctly", "0", opts.get("floppyDisks").get(0).get("num"));
		assertEquals("Floppy->digobj not set correctly", digObjs.get(0).getAbsolutePath(), opts.get("floppyDisks").get(0).get("digobj"));
		assertEquals("Floppy->type not set correctly", "UNDEFINED", opts.get("floppyDisks").get(0).get("type"));

		assertTrue("Fixed node not found", opts.containsKey("fixedDisks"));
		assertFalse("Fixed node should not be empty", opts.get("fixedDisks").isEmpty());
		assertEquals("Fixed->enabled not set correctly", "true", opts.get("fixedDisks").get(0).get("enabled"));
		assertEquals("fixedDisks->master not set correctly", "true", opts.get("fixedDisks").get(0).get("master"));
		assertEquals("fixedDisks->index not set correctly", "0", opts.get("fixedDisks").get(0).get("index"));
		assertEquals("fixedDisks->swImg not set correctly", swImgs.get(0).getPath(), opts.get("fixedDisks").get(0).get("swImg"));
		assertEquals("fixedDisks->cylinders not set correctly", "0", opts.get("fixedDisks").get(0).get("cylinders"));
		assertEquals("fixedDisks->head not set correctly", "0", opts.get("fixedDisks").get(0).get("heads"));
		assertEquals("fixedDisks->sectors not set correctly", "0", opts.get("fixedDisks").get(0).get("sectorsPerTrack"));
	}

	@Test(expected=IOException.class)
    public void testCreateCLI() throws IOException  {

		ConfigEnv env = fm.createConfigEnv(emuDir, emuExec, mockedPW);
		fm.createCLI(env);
	}
	
	@Test
    public void testCreateCLI2() throws IOException  {

		Map<String, List<Map<String, String>>> opts;
		List<List<String>> cli;
		ConfigEnv env = fm.createConfigEnv(emuDir, emuExec, mockedPW);
		opts = fm.generateDefaults(env, digObjs, swImgs);
		env.setOptions(opts);
		cli = fm.createCLI(env);

		assertFalse("CLI should not be empty", cli.isEmpty());
		assertEquals("CLI should have 3 parts", 3, cli.size());
		assertTrue("Pre should be empty", cli.get(0).isEmpty());
		assertTrue("Post should be empty", cli.get(2).isEmpty());
		assertEquals("Body not set correctly", 6, cli.get(1).size());
		assertEquals("Body part not set correctly", "-L", cli.get(1).get(0));
		assertEquals("Body part not set correctly", "-fda", cli.get(1).get(2));
		assertEquals("Body part not set correctly", "\"" + swImgs.get(0).getPath() + "\"", cli.get(1).get(5));
	}

}
