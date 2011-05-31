package eu.keep.controller.emulatorConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * A Junit Test class for {@link eu.keep.controller.emulatorConfig.SimpleTemplateBuilder}
 * @author Bram Lohman
 */
public class TestSimpleTemplateBuilder {

	SimpleTemplateBuilder sm;
	File emuDir;
	
	@Before
    public void setup() throws IOException {

		emuDir = new File("testData/");
		sm = new SimpleTemplateBuilder(emuDir);
	}
	
	@Test(expected=IOException.class)
    public void testLoadTemplate() throws IOException {
		sm.loadTemplate("foo");
	}

	@Test
    public void testLoadTemplate2() throws IOException {
		sm.loadTemplate("templateCLI.ftl");
	}

	@Test
    public void testGetTemplateArgs() throws IOException {
		
		// "configDir":"configDir", "configFile":"configFile", "digobj":"digobj", 
		// "fixedDisks":{"enabled":"enabled", "index":"index", "master":"master", "cylinders":"cylinders", "heads":"heads", "sectorsPerTrack":"sectorsPerTrack", "swImg":"swImg"},
		// "floppyDisks":{"type":"type", "num":"num", "digobj":"digobj", "inserted":"inserted"}
		
		sm.loadTemplate("templateCLI.ftl");
		Map<String, Map<String, String>> args;
		args = sm.getTemplateArgs();
//		System.out.println("Args: " + args);

		assertTrue("Root node not found", args.containsKey("root"));
		assertFalse("Root node should not be empty", args.get("root").isEmpty());
		assertEquals("Root->configFile not set correctly", "configDir", args.get("root").get("configDir"));

		assertTrue("Fixed node not found", args.containsKey("fixedDisks"));
		assertFalse("Fixed node should not be empty", args.get("fixedDisks").isEmpty());
		assertEquals("Fixed->index not set correctly", "index", args.get("fixedDisks").get("index"));

		assertTrue("Floppy node not found", args.containsKey("floppyDisks"));
		assertFalse("Floppy node should not be empty", args.get("floppyDisks").isEmpty());
		assertEquals("Floppy->index not set correctly", "digobj", args.get("floppyDisks").get("digobj"));
	}

	@Test(expected=IOException.class)
    public void testGenerateConfig() throws IOException {

		Map<String, List<Map<String, String>>> opts = new HashMap<String, List<Map<String, String>>>();
		List<Map<String, String>> opt = new ArrayList<Map<String, String>>();
		Map<String, String> vals = new HashMap<String, String>();
		vals.put("key1", "value1");
		opt.add(vals);
		opts.put("root", opt);

		// Not all arguments present
		sm.loadTemplate("templateCLI.ftl");
		sm.generateConfig(opts);
	}

	@Test(expected=IOException.class)
    public void testGenerateConfig2() throws IOException {

		Map<String, List<Map<String, String>>> opts = new HashMap<String, List<Map<String, String>>>();
		List<Map<String, String>> fixList = new ArrayList<Map<String, String>>();
		Map<String, String> fixed1 = new HashMap<String, String>();
		fixed1.put("swImg", "swImgVal");
		fixed1.put("index", "1");
		fixList.add(fixed1);
		Map<String, String> fixed2 = new HashMap<String, String>();
		fixed2.put("swImg", "swImgVal");
		// Should fail on missing fixed2->index
		fixList.add(fixed2);
		opts.put("fixedDisks", fixList);
		List<Map<String, String>> flopList = new ArrayList<Map<String, String>>();
		Map<String, String> flop = new HashMap<String, String>();
		flop.put("num", "1");
		flop.put("digobj", "digobjVal");
		flopList.add(flop);
		opts.put("floppyDisks", flopList);
		List<Map<String, String>> rootList = new ArrayList<Map<String, String>>();
		Map<String, String> root = new HashMap<String, String>();
		root.put("configDir", "dirVal");
		rootList.add(root);
		opts.put("root", rootList);

		sm.loadTemplate("templateCLI.ftl");
		sm.generateConfig(opts);
	}

	@Test
    public void testGenerateConfig3() throws IOException {

		Map<String, List<Map<String, String>>> opts = new HashMap<String, List<Map<String, String>>>();
		List<Map<String, String>> fixList = new ArrayList<Map<String, String>>();
		Map<String, String> fixed1 = new HashMap<String, String>();
		fixed1.put("swImg", "swImgVal");
		fixed1.put("index", "0");
		fixList.add(fixed1);
		Map<String, String> fixed2 = new HashMap<String, String>();
		fixed2.put("swImg", "swImgVal");
		fixed2.put("index", "1");
		fixed2.put("unused", "randomVal");	// This shouldn't stop it from working
		fixList.add(fixed2);
		opts.put("fixedDisks", fixList);
		List<Map<String, String>> flopList = new ArrayList<Map<String, String>>();
		Map<String, String> flop = new HashMap<String, String>();
		flop.put("num", "1");
		flop.put("digobj", "digobjVal");
		flopList.add(flop);
		opts.put("floppyDisks", flopList);
		List<Map<String, String>> rootList = new ArrayList<Map<String, String>>();
		Map<String, String> root = new HashMap<String, String>();
		root.put("configDir", "dirVal");
		rootList.add(root);
		opts.put("root", rootList);

		sm.loadTemplate("templateCLI.ftl");
		List<String> out = sm.generateConfig(opts);
//		System.out.println("Output: " + out);

		assertEquals("Incorrect number of arguments", 3, out.size());
		assertEquals("Pre should be empty", "", out.get(0));
		assertEquals("Body should not correct", "-L\ndirVal\n-fdb\n\"digobjVal\"\n-hda\n\"swImgVal\"\n-hdb\n\"swImgVal\"\n", out.get(1));
		assertEquals("Post should be empty", "", out.get(2));
		}

}
