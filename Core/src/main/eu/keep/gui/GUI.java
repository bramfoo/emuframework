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
package eu.keep.gui;

import eu.keep.characteriser.Format;
import eu.keep.gui.common.GlassPane;
import eu.keep.gui.common.LogPanel;
import eu.keep.gui.config.ConfigPanel;
import eu.keep.gui.settings.LanguageSettingsFrame;
import eu.keep.gui.settings.SettingsFrame;
import eu.keep.gui.settings.WhitelistFrame;
import eu.keep.gui.util.RBLanguages;
import eu.keep.gui.wizard.ea.EAWizardAdd;
import eu.keep.gui.wizard.ea.EAWizardRemove;
import eu.keep.gui.wizard.swa.SWAWizardAdd;
import eu.keep.gui.wizard.swa.SWAWizardRemove;
import eu.keep.kernel.CoreEngineModel;
import eu.keep.kernel.CoreObserver;
import eu.keep.kernel.Kernel;
import eu.keep.softwarearchive.pathway.ObjectFormatType;
import eu.keep.util.ArchiveException;
import eu.keep.util.FileUtilities;
import eu.keep.gui.settings.EFProperty;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

/**
 * The main GUI of the emulation framework.
 */
public class GUI extends JFrame implements CoreObserver {

    public static final int WIDTH_UNIT = 10;
    public static final int TOTAL_WIDTH_UNITS = 105;
    public static final int WIDTH = WIDTH_UNIT * TOTAL_WIDTH_UNITS;
    public static final int HEIGHT = 700;
    public static final String PROP_FILE_NAME = "gui.properties";
    public static final String PROP_FILE_NAME_KERNEL = "user.properties";
    private static final Logger logger = Logger.getLogger(GUI.class.getName());

    public CoreEngineModel model;
    public final Properties guiProps;

    private MainPanel tabPanel;
    private LogPanel logPanel;

    private static boolean eaAdmin = false;
    private static boolean swaAdmin = false;

    /**
     * Private constructor: instantiation is done through the main method.
     *
     * @param model
     * @param eaAdmin
     * @param swaAdmin
     * @throws IOException if the gui.properties file can not be found.
     */
    private GUI(CoreEngineModel model, boolean eaAdmin, boolean swaAdmin) throws IOException {
        super("KEEP ~ Emulation Framework ~ comprehensive user interface");
		super.setIconImage(Toolkit.getDefaultToolkit().getImage("images/EF_icon.png"));
        super.setSize(WIDTH, HEIGHT);
        super.setResizable(false);

        this.model = model;

        String fileName = "eu/keep/" + PROP_FILE_NAME;
        if (!new File(fileName).exists()) {
            throw new FileNotFoundException("file not found: " + fileName);
        }

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);

        if (is == null) {
            is = new FileInputStream(fileName);
        }

        guiProps = FileUtilities.getProperties(is);

        // some custom actions are needed when exiting the GUI
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GUI.this.gracefulExit();
            }
        });

        super.setGlassPane(new GlassPane());

        // initialize all GUI components
        initGUI(eaAdmin, swaAdmin);
        initActionListeners();
        
        // Register this GUI as an observer on the Kernel
        model.registerObserver(this);
    }

    public ConfigPanel getConfigPanel() {
        return tabPanel.configPanel;
    }

    /**
     * Tries to stop the application gracefully and cleaning up the mess it left behind :)
     */
    private void gracefulExit() {
        logger.info(RBLanguages.get("log_exit_gui"));
        try {
            model.cleanUp();
            model.stop();
            GUI.this.dispose();
            System.exit(0);
        } catch (Exception e) {
            logger.warn(RBLanguages.get("log_error_exit_model"));
            e.printStackTrace();
            System.exit(1);
        }
    }

	@Override
	public void update(String updateNews) {
		logPanel.logMessage(updateNews);
	}

	/*
     * Initialize action listeners.
     */
    private void initActionListeners() {

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (x > 5 || y > 23 || e.getClickCount() < 3) {
                    return;
                }

                GUI.this.showGlassPane();

                final JDialog dialog = new JDialog();
                dialog.setLayout(new BorderLayout(5, 5));
                JLabel img = new JLabel("", new ImageIcon(GUI.EE_IMG), JLabel.CENTER);
                dialog.add(img, BorderLayout.CENTER);

                dialog.setLocation(GUI.this.getLocation());
                dialog.setSize(GUI.this.getWidth(), GUI.this.getHeight());
                dialog.setUndecorated(true);

                dialog.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        GUI.this.hideGlassPane();
                        dialog.dispose();
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    /*
     * Initialize the GUI components.
     */
    private void initGUI(boolean eaAdmin, boolean swaAdmin) {
        super.setLayout(new BorderLayout(5, 5));

        // init main panel
        tabPanel = new MainPanel(this, eaAdmin, swaAdmin);
        super.add(tabPanel, BorderLayout.CENTER);

        // log/message label
        logPanel = new LogPanel(RBLanguages.get("gui_started"));        
        JScrollPane logLabelScroller = new JScrollPane(logPanel);
        logLabelScroller.setPreferredSize(new Dimension(WIDTH, 87));
        super.add(logLabelScroller, BorderLayout.SOUTH);

        // init menu bar
        JMenuBar menuBar = initMenuBar(eaAdmin, swaAdmin);
        this.setJMenuBar(menuBar);        
    }

    /**
     * Init menu bar
     *
     * @return
     */
    private JMenuBar initMenuBar(boolean eaAdmin, boolean swaAdmin) {
        JMenuBar menuBar = new JMenuBar();

        // File menu option
        JMenu file = initFileMenu();

        // Settings menu option
        JMenu settings = initSettingsMenu();

        // Tools menu option
        JMenu tools = initToolsMenu(eaAdmin, swaAdmin);

        // Help menu option
        JMenu help = initHelpMenu();

        // Add everything together
        menuBar.add(file);
        menuBar.add(settings);
        if (eaAdmin || swaAdmin) { // Do not add Tools menu if in 'client mode'
            menuBar.add(tools);
        }
        menuBar.add(help);
        return menuBar;
    }

    /**
     * Initialise the File menu
     *
     * @return the File menu
     */
    private JMenu initFileMenu() {
        JMenu fileMenu = new JMenu();
        RBLanguages.set(fileMenu, "file");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem exit = new JMenuItem();
        RBLanguages.set(exit, "exit");

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.this.gracefulExit();
            }
        });
        fileMenu.add(exit);
        return fileMenu;
    }

    /**
     * Initialise the Settings menu
     *
     * @return the Settings menu
     */
    private JMenu initSettingsMenu() {
        JMenu settingsMenu = new JMenu();
        RBLanguages.set(settingsMenu, "settings");
        settingsMenu.setMnemonic(KeyEvent.VK_S);

        // Language settings option
        JMenuItem languages = new JMenuItem();
        RBLanguages.set(languages, "languages");
        languages.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LanguageSettingsFrame(GUI.this, "eu/keep/" + PROP_FILE_NAME_KERNEL, "eu/keep/" + PROP_FILE_NAME);
            }
        });
        settingsMenu.add(languages);

        // Web-service addresses settings option
        JMenuItem addresses = new JMenuItem();
        RBLanguages.set(addresses, "addresses");
        addresses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<EFProperty> editableProperties = new ArrayList<EFProperty>();
                editableProperties.add(EFProperty.softwareArchiveURL);
                editableProperties.add(EFProperty.emulatorArchiveURL);
                new SettingsFrame(GUI.this,
                        "eu/keep/" + PROP_FILE_NAME_KERNEL,
                        editableProperties);
            }
        });
        settingsMenu.add(addresses);

        // Emulator whitelist option
        JMenuItem whitelist = new JMenuItem();
        RBLanguages.set(whitelist, "whitelist");
        whitelist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new WhitelistFrame(GUI.this, "eu/keep/" + PROP_FILE_NAME_KERNEL);
            }
        });
        settingsMenu.add(whitelist);

        return settingsMenu;
    }

    /**
     * Initialise the Tools menu
     *
     * @return the Tools menu
     */
    private JMenu initToolsMenu(boolean eaAdmin, boolean swaAdmin) {
        JMenu toolsMenu = new JMenu();
        RBLanguages.set(toolsMenu, "toolsMenu");
        toolsMenu.setMnemonic(KeyEvent.VK_T);

        // Option to start Add Emulator wizard
        if (eaAdmin) {
            JMenuItem addEmulator = new JMenuItem();
            RBLanguages.set(addEmulator, "addEmulator");
            addEmulator.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new EAWizardAdd();
                        }
                    });
                }
            });
            toolsMenu.add(addEmulator);

            JMenuItem removeEmulator = new JMenuItem();
            RBLanguages.set(removeEmulator, "removeEmulator");
            removeEmulator.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new EAWizardRemove();
                        }
                    });
                }
            });
            toolsMenu.add(removeEmulator);
        }

        // Option to start Add Software wizard
        if (swaAdmin) {
            JMenuItem addSoftware = new JMenuItem();
            RBLanguages.set(addSoftware, "addSoftware");
            addSoftware.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new SWAWizardAdd();
                        }
                    });
                }
            });
            toolsMenu.add(addSoftware);

            JMenuItem removeSoftware = new JMenuItem();
            RBLanguages.set(removeSoftware, "removeSoftware");
            removeSoftware.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new SWAWizardRemove();
                        }
                    });
                }
            });
            toolsMenu.add(removeSoftware);
        }

        return toolsMenu;
    }

    /**
     * Initialise the Help menu
     *
     * @return the help menu
     */
    private JMenu initHelpMenu() {
        JMenu helpMenu = new JMenu();
        RBLanguages.set(helpMenu, "help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem onlineDocItem = new JMenuItem();
        RBLanguages.set(onlineDocItem, "onlineDocItem");
        onlineDocItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String uri = "http://emuframework.sourceforge.net/documentation.html";
                try {
                    Desktop.getDesktop().browse(new URI(uri));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GUI.this,
                            RBLanguages.get("errorOpenURI") + uri +
                                    "\n\n" + ex.getMessage(),
                            RBLanguages.get("error"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JMenuItem onlineForum = new JMenuItem();
        RBLanguages.set(onlineForum, "onlineForum");
        onlineForum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String uri = "http://sourceforge.net/projects/emuframework/forums";
                try {
                    Desktop.getDesktop().browse(new URI(uri));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GUI.this,
                            RBLanguages.get("log_error_open_uri") + " " + uri +
                                    "\n\n" + RBLanguages.get("more_info") + " " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JMenuItem aboutItem = new JMenuItem();
        RBLanguages.set(aboutItem, "about");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(GUI.this,
                        model.getVendor() + "\n\n" +
                                model.getTitle() + " " + model.getVersion() + " - Copyright (c) 2009-2012 Tessella plc.\n" +
                                "GUI - Copyright (c) 2012 National Library of the Netherlands\n" +
                                "\n" +
                                "Software Archive version 2.1.0\n" + // TODO get version from SWA
                                "Emulator Archive version 2.1.0\n" + // TODO get version from EA
                                "\n" +
                                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                                "you may not use this software except in compliance with the License.\n" +
                                "You may obtain a copy of the License at:\n" +
                                "\n" +
                                "  http://www.apache.org/licenses/LICENSE-2.0\n" +
                                "\n" +
                                "Unless required by applicable law or agreed to in writing, software\n" +
                                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either\n" +
                                "express or implied.\n" +
                                "See the License for the specific language governing permissions and\n" +
                                "\n" +
                                "For more information about this project, visit:\n" +
                                "  http://www.keep-project.eu\n" +
                                "limitations under the License.\n" +
                                "  http://emuframework.sourceforge.net\n" +
                                "\n" +
                                "Credits:\n" +
                                "  - Bram Lohman (Tessella plc., Emulation Framework)\n" +
                                "  - David Michel (Tessella plc., Emulation Framework)\n" +
                                "  - Edo Noordermeer (Tessella plc., Emulation Framework, GUI)\n" +
                                "  - Bart Kiers (National Library of the Netherlands, GUI)\n" +
                                "  - Jeffrey van der Hoeven (National Library of the Netherlands, Coordination)\n" +
                                "\n" +
                                "Support:\n" +
                                "  https://sourceforge.net/projects/emuframework/support"
                );
            }
        });

        helpMenu.add(onlineDocItem);
        helpMenu.add(onlineForum);
        helpMenu.add(aboutItem);

        return helpMenu;
    }

    public void loadFormats(List<Format> fitsFormats) {
        tabPanel.loadFormats(fitsFormats, new ArrayList<ObjectFormatType>());
    }
    public void loadFormats(List<Format> fitsFormats, List<ObjectFormatType> allFormats) {
        tabPanel.loadFormats(fitsFormats, allFormats);
    }
    
    /**
     * Display an error or warning message. The message will be displayed both in the status bar 
     * (at the bottom of the window) and in a popup window.
     * @param parent the parent Frame  in which the dialog is displayed; if null, or if the 
     * 		parentComponent has no Frame, a default Frame is used
     * @param message an error message to be shown in a popup-window
     * @param statusBarMessage an error message to be shown in the status bar at the bottom of the window
     * @param messageType the type of message to be displayed: JOptionPanel.ERROR_MESSAGE, .INFORMATION_MESSAGE, 
     * 		.WARNING_MESSAGE, .QUESTION_MESSAGE, or .PLAIN_MESSAGE
     * @throws HeadlessException
     */
	public void displayMessage(Component parent, String message, String statusBarMessage, int messageType) throws HeadlessException {
		unlock(statusBarMessage);
		JOptionPane.showMessageDialog(parent, message, "", messageType);
	}

    /**
     * Disable the components on this frame and display the message on the "log-label".
     *
     * @param message the message to be displayed on the "log-label".
     */
    public void lock(String message) {
    	logPanel.logMessage(message);    	
        showGlassPane();
        super.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }

    /**
     * Display the GlassPane for this frame
     */
	public void showGlassPane() {
		this.getGlassPane().setVisible(true);
        this.setEnabled(false);
	}

    /**
     * Enable the components on this frame and display the message on the "log-label".
     *
     * @param message the message to be displayed on the "log-label".
     */
    public void unlock(String message) {
    	logPanel.logMessage(message);    	
        hideGlassPane();
        super.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Hide the GlassPane for this frame
     */
	public void hideGlassPane() {
		this.getGlassPane().setVisible(false);
        this.setEnabled(true);
	}
    
    /**
     * Reload the Kernel. This method can be used to reload the Kernel with updated properties.
     */
    public void reloadModel() {
        try {
            model = new Kernel(PROP_FILE_NAME_KERNEL);
            if (!this.tabPanel.isEnabled()) {
            	// This covers the situation where on a previous reload, the catch-block below
            	// was executed and the tabPanel was disabled.
                this.remove(tabPanel);
                this.tabPanel = new MainPanel(this, eaAdmin, swaAdmin);
                this.tabPanel.setEnabled(true);
                this.add(tabPanel, BorderLayout.CENTER);
            }
            unlock(RBLanguages.get("log_restart_emu"));
        } catch (IOException e) {
            model = null;
            logger.fatal(RBLanguages.get("log_error_restart_kernel") + " " + ExceptionUtils.getStackTrace(e));
            enableComponentWithChildren(this.tabPanel, false);

            String message = RBLanguages.get("log_error_restart_kernel") + " " + e.getMessage();
            if (e.getCause() instanceof ArchiveException) {
            	message = RBLanguages.get("failure_EF_restart") + " " + RBLanguages.get("failure_archive_connect");
            }
            displayMessage(this, message, RBLanguages.get("log_error_reload_kernel"), JOptionPane.ERROR_MESSAGE);            
        }
    }

    /**
     * Recursively enable/disable a component and all of its children
     *
     * @param component the component to be enabled/disabled
     * @param enabled   true to enable, false to disable
     */
    public static void enableComponentWithChildren(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof Container) {
            for (int i = 0; i < ((Container) component).getComponentCount(); i++) {
                GUI.enableComponentWithChildren(((Container) component).getComponent(i), enabled);
            }
        }
    }

    /**
     * The entry point for this class.
     *
     * @param args String array of command line parameters, which are ignored.
     */
    public static void main(final String[] args) {
        // try to set Nimbus as look-and-feel, if possible
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        // create the core engine model
        CoreEngineModel model;
        try {
            model = new Kernel(PROP_FILE_NAME_KERNEL);
        } catch (IOException e) {
            model = null;
            logger.fatal(RBLanguages.get("log_error_init") + " " + ExceptionUtils.getStackTrace(e));
            String message = RBLanguages.get("log_error_init") + " " + e.getMessage();
            if (e.getCause() instanceof ArchiveException) {
            	message = RBLanguages.get("failure_EF_start") + " " + RBLanguages.get("failure_archive_connect");
            }
            
    		JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // The anonymous inner class in `invokeLater(...)` needs a final variable.
        final CoreEngineModel m = model;

        // create the gui in it's own thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {

                    // Check if the EA admin mode should be started
                    File ea = new File("ea");
                    if (ea.exists() && ea.isDirectory() && ea.list().length > 0) {
                        logger.info(RBLanguages.get("log_found_local_ea") + " " + ea.getAbsolutePath() + ". " +
                                RBLanguages.get("log_start_ea_admin"));
                        eaAdmin = true;
                    } else {
                        logger.info(RBLanguages.get("log_no_local_ea"));
                    }

                    // Check if the SWA admin mode should be started
                    File swa = new File("swa");
                    if (swa.exists() && swa.isDirectory() && swa.list().length > 0) {
                        logger.info(RBLanguages.get("log_found_local_swa") + " " + swa.getAbsolutePath() + ". " +
                                RBLanguages.get("log_start_swa_admin"));
                        swaAdmin = true;
                    } else {
                        logger.info(RBLanguages.get("log_no_local_swa"));
                    }

                    if (!eaAdmin && !swaAdmin) {
                        logger.info(RBLanguages.get("log_start_emu_client"));
                    }

                    logger.info(RBLanguages.get("log_init_gui"));
                    GUI gui = new GUI(m, eaAdmin, swaAdmin);
                    gui.setVisible(true);
                    logger.debug(RBLanguages.get("log_started_gui"));
                } catch (IOException e) {
                    logger.fatal(e.getMessage());
                }
            }
        });
    }

    private static final byte[] EE_IMG = {71, 73, 70, 56, 57, 97, 82, 3, -118, 2, -128, 1, 0, 77, 77, 77, 80, 80, 80, 33, -2, 17, 67, 114, 101, 97, 116, 101, 100, 32, 119, 105, 116, 104, 32, 71, 73, 77, 80, 0, 33, -7, 4, 1, 10, 0, 1, 0, 44, 0, 0, 0, 0, 82, 3, -118, 2, 0, 2, -2, -116, -113, -87, -53, -19, 15, -93, -100, -76, -38, -117, -77, -34, -68, -5, 15, -122, -30, 72, -106, -26, 7, -92, -22, -54, -74, -18, 11, -57, -14, 76, -41, -10, -115, -25, -20, -55, -9, -2, 15, 12, 10, -121, -60, -94, -15, -120, 76, 14, 117, -52, -90, -13, 9, -115, 62, -107, -44, -86, -11, -118, -51, 106, -73, -36, -82, 119, 33, 13, -117, -57, -28, 114, -21, -117, 78, -85, -41, -20, -74, -5, 13, 103, -104, -25, -12, -70, -3, 21, -49, -21, -9, -4, -66, -1, -65, 118, 39, 56, 72, 40, 5, 120, -120, -104, -88, -72, -56, -40, 104, 80, 8, 25, 41, 25, -29, 88, 105, 121, -119, -103, -87, 73, 50, -39, -23, 41, -71, 25, 42, 58, 74, 90, 122, -8, -119, -102, 122, 103, -54, -38, -22, -6, 10, 75, -92, 58, 75, 91, 22, 123, -117, -101, -85, -69, -85, 80, -21, -5, -21, -60, 43, 60, 76, 92, -52, 8, -116, -100, 108, 99, -52, -36, -20, -4, -36, -91, 44, 61, -19, 2, 109, 125, -115, -99, 125, 66, -51, 77, -83, -3, 13, 30, 46, -34, -48, 93, -82, 60, -114, -98, -82, -50, 108, -34, -114, -68, 14, 31, 47, -33, -22, 94, 95, 59, -113, -97, -81, 127, 108, -33, -81, -70, 15, 48, -96, 64, 54, -2, 10, -90, 26, -120, 48, -95, 66, 42, 6, 27, 118, 90, 8, 49, -94, -60, 109, 14, 43, 22, -102, -120, 49, -93, 70, -2, 11, 22, 59, 14, -38, 8, 50, -92, -56, 71, 30, 75, -46, 25, -119, 50, 37, 68, -109, 44, 109, -87, 124, 9, 51, 95, -53, -103, 98, 98, -38, -68, 57, -114, -90, 78, 67, 56, 123, -6, -124, -74, 51, 104, -80, -97, 68, -117, 10, 19, -118, 52, -121, -47, -91, 76, 95, 37, 125, -70, -84, -87, -44, -87, -101, -96, 90, -107, 65, 53, -85, -42, 70, 87, -69, 86, -37, 42, 17, 10, 88, 126, 94, -53, -86, 24, 43, -48, -50, -125, 21, 104, -45, -104, 125, 123, -74, 109, 58, 80, 1, 110, -56, -27, 2, 23, -18, 93, 116, 116, -19, -18, -59, -110, 87, -17, -33, 111, -26, 6, 51, 12, 108, -42, -80, 54, -102, -118, 67, 32, 78, -36, -8, 26, -29, -56, 30, 30, -105, -91, 12, -82, 36, 102, 13, -106, -33, 110, -106, 108, -14, 51, -122, -50, -112, 69, 59, 99, 105, -102, 35, 105, -81, -87, -101, -59, -83, 59, -125, 36, 14, -40, 116, 91, 67, 88, 93, -38, -74, -45, -41, 76, 16, -108, -45, 77, 14, 119, 110, -32, -92, -56, -48, -114, 125, -32, 23, 113, -31, -61, -119, -117, 50, 30, 85, 54, -83, -27, -52, 89, 59, 47, 94, 59, 57, 48, -35, -43, 47, 95, -81, 58, 9, -52, -69, -42, -35, -83, 127, -49, -12, -80, -41, -8, -44, -27, -69, -98, 71, -97, 93, -69, 52, -10, -19, -83, -66, -81, 84, -45, 119, 13, -11, -34, -2, 68, -41, 103, 123, -100, 78, -9, 45, -110, 95, 2, 45, -23, -9, -38, 94, -1, -59, -123, -44, -128, -118, 20, -120, -96, 102, -14, -91, 48, -40, -126, 20, 70, 56, -109, -125, -89, 64, 40, 94, 104, 103, 40, 88, -97, 28, -56, 77, -24, -114, -122, 127, -124, 113, 91, 82, 32, -74, -41, 33, 37, 6, 22, 100, 34, 31, 40, -82, -91, -30, 93, -1, -75, 8, 3, 127, -3, -60, -88, 7, -121, 24, 54, 104, 35, -117, 56, -30, 65, -30, -114, 60, -66, 97, -36, -117, -37, 5, -88, 86, 91, -27, 57, -80, 31, -109, -2, 28, -23, 70, -110, 63, 78, 39, -27, 73, 78, 118, 7, 37, 13, -46, 25, 68, 37, 65, 99, 40, -23, 25, 90, 92, 6, 7, 85, -104, 110, 101, -8, -27, 39, 99, 49, -105, 98, -102, 106, 70, 35, 96, -101, -98, -68, -119, 91, 4, -26, -51, 9, 88, -99, 89, -122, 7, -106, 112, 122, -70, -57, -89, 21, 66, 93, 121, -89, 118, 82, -83, 54, -127, 119, -123, 30, -15, 20, -94, -23, -7, -42, 20, 105, 20, 124, 101, 39, 106, -113, 22, 17, 105, -111, 110, 26, 88, 105, 103, -105, 126, -104, 105, 104, -101, 2, 113, 78, -87, 81, 12, 57, 105, -128, 75, -119, 58, -22, -123, 58, 78, 118, 42, 15, -21, 121, 58, 35, -82, -83, -38, -39, 19, -93, 18, -112, 42, -30, 78, -75, 114, -110, -52, -84, 86, -2, -103, -2, 104, -90, 56, -7, 58, 40, -128, 104, -6, 57, 44, 8, -87, 74, -54, -109, -86, -83, 18, 105, 19, -84, -65, 58, -5, -84, -80, -47, 74, 59, 109, -109, -42, 38, -117, 108, 76, -106, 53, -70, -125, 28, -70, -46, -6, 109, 6, -71, -35, 58, 34, 74, -25, 54, -53, -19, -72, -99, -74, -85, -38, -98, -16, 98, -91, -46, -68, 113, -42, -117, -84, 125, -8, -26, 75, -24, -76, 1, 39, 24, -110, -65, 52, -90, 123, 48, -95, 3, 111, 91, -80, -90, 26, 5, 86, 1, -111, 121, 62, -4, -81, -64, 7, 110, 68, 113, -84, -23, 94, -116, 113, -105, 87, 53, -36, 16, 72, 101, -94, 107, 97, -56, -63, -70, 7, -28, 68, -114, -46, 107, 33, -62, 42, -57, 12, 99, 68, 123, 46, 76, 51, -61, 42, -109, -100, -13, -83, 10, -35, 44, 114, -49, 50, 99, 44, -76, -111, 11, 57, -36, 109, -47, -78, -50, -84, -76, 61, 43, -115, 28, 116, -45, 0, -29, 43, 117, 61, 79, 11, -100, 116, -43, 59, 87, 109, -11, -49, 114, -82, -52, -11, -44, -47, -122, 109, 116, 90, 95, 27, -37, 4, -50, -51, -115, 77, -74, -45, 3, 97, -115, 118, 111, 81, -65, -36, 110, -37, 110, -101, 125, 47, -75, 81, 86, -68, 118, -83, 118, -33, 29, 16, -46, 83, 112, -128, -12, -73, 127, 119, -115, 119, -115, -21, -58, -117, 66, -108, 14, 61, 124, 120, -119, 111, -97, -2, -51, 51, -90, -32, 122, 89, -7, 124, 84, 71, -34, 14, 66, 88, -89, -19, -125, -27, -117, 115, 51, 48, -25, -99, 79, -2, -92, 16, 75, -109, 9, 56, -37, -90, 119, -29, -71, -112, 65, -84, -82, 55, -23, -101, -65, 110, 123, -32, 13, 82, 56, -44, 21, 53, -69, -114, 123, -18, 0, 5, 53, 55, -65, 106, -25, 40, -62, -17, -61, 6, -1, 91, -30, 27, 31, 31, 91, -75, 29, 40, -17, 55, -13, -62, -17, -29, 45, -12, 88, 73, 79, -8, -108, -122, 91, 63, 77, -20, -20, -10, 50, -6, 28, -115, -105, 93, 61, -8, 6, -21, 115, 104, -15, 23, 85, -26, 61, -16, -22, 47, 57, 60, -15, -18, 19, 114, 126, -21, -89, -50, -81, 57, -10, -118, -77, 30, -119, -4, 33, 110, 121, -4, 43, -106, -1, 34, 53, 11, -57, -60, -113, -128, 5, 52, 32, 62, -20, -109, -64, -53, -23, 111, 83, 13, 92, 95, 60, -62, 38, 11, -22, 81, -80, -126, 62, 91, 7, -41, 32, -91, -63, 71, 113, -48, -127, -16, -112, 88, -61, 96, -125, -62, -14, 101, 1, 76, -14, 27, 33, -106, 46, 104, 42, 123, -95, 112, 105, -104, -13, 93, 8, 11, -27, 66, -6, 121, -48, -124, -54, -126, -103, -50, 108, -120, -66, -3, -27, -48, 23, -14, 120, 30, 24, 46, -128, 45, 45, 4, 81, -124, -41, -53, -100, -48, -118, -56, 67, 80, 17, 76, 108, 85, 88, 34, -2, 14, -101, -120, 59, 40, -58, 80, -126, 67, 51, -44, 0, -45, 103, -63, -5, -75, 12, -128, -67, 83, -121, 17, -71, -8, -123, -45, -43, -83, 127, 124, 43, 89, -42, 0, -112, 49, -48, -19, 112, -117, -32, -127, 93, -23, -40, -24, -79, 27, -10, 80, -116, 126, -103, -93, -124, -98, -45, -68, -17, -31, 17, -119, 90, -86, 93, 116, 32, 70, 69, 50, 42, -91, -124, 81, -124, -113, 29, 5, -39, 65, 1, -42, -48, 93, 108, -127, -93, 2, 45, -103, 71, 57, -50, -123, -114, -102, 120, -28, 26, 73, 120, -55, 67, -114, -90, -110, -55, -93, 93, 28, -27, 102, 70, 78, 58, 50, 124, 119, 4, -27, 8, -110, 72, -96, 85, -7, -79, 35, -40, -23, -49, -19, 116, 40, 14, -79, -64, -112, -106, -93, -16, 36, 36, -107, -77, -55, -63, -51, -14, 113, -67, 108, 34, 3, 113, 25, 14, 89, 50, -46, 34, -75, 100, -27, 45, -127, 25, 76, 77, 46, -13, 35, 113, 43, 99, 29, -99, -7, 73, 34, 78, 19, -107, -69, 20, 4, -40, -108, 89, -52, 65, 30, -13, 30, -37, -44, -63, 3, -15, -57, 42, 97, -122, 51, -116, 96, 36, 103, 55, 23, 57, 15, 116, 86, 83, -102, -95, -80, 101, 54, 95, -8, -50, -39, -56, -60, -101, -122, -28, 38, 32, -59, -39, -50, 8, 106, -47, -100, -5, 92, 69, 63, 9, -38, 76, 87, -114, 83, -96, -7, -20, 99, 65, -2, -83, -40, 73, -128, 10, 81, -101, -15, -12, -25, 57, 41, 74, 45, 122, -80, 115, -94, 12, -83, 40, 66, -21, -9, 15, 68, 98, -46, 20, 10, 13, 40, 42, 14, 8, 79, -112, 30, 68, -92, -82, 40, 41, 71, 67, -6, 80, 125, 58, 47, -128, -124, 52, -27, 58, -15, -7, 75, -104, 94, 52, -91, 51, -91, 102, 77, -69, 120, 77, -100, -74, 112, -91, 49, 117, 104, 79, -7, -7, 83, -101, 6, -75, -93, 67, 61, 41, 74, 69, -119, 58, -97, -114, 50, -111, -85, 20, -22, 66, -119, 90, 84, -29, 93, -51, -96, -100, -7, -31, 77, -83, -6, 82, -99, -18, 116, -110, 71, -109, -22, 84, -127, 26, 81, 104, -26, 20, -85, 99, 101, -100, -51, -22, 48, 61, -81, -2, 19, -93, 77, -3, 20, -5, -96, 42, -110, 92, 117, -107, -86, -104, -40, 40, -97, -44, -38, 86, 23, -103, -117, -98, -108, -28, -21, 37, 36, 58, 39, -64, 122, -108, -84, -3, 34, -20, 89, -107, -102, -42, 72, 94, -47, -99, 79, 69, 94, -74, 44, 90, 88, -61, -30, 7, -101, 76, -92, -20, 93, -35, -6, -110, -113, 118, 79, -82, 115, 69, -26, 95, -23, -102, 85, -47, -63, 68, -76, 27, 80, -19, 87, 81, 123, 90, -49, -90, 22, 88, 55, -111, 105, 92, 73, 91, 79, -60, 30, 73, -79, -127, 117, -19, 106, -9, 38, -55, -111, -110, -44, -81, -69, -123, -19, 98, 5, -2, -37, 43, -58, -114, 86, -77, -121, 37, 46, -113, 120, 123, 92, -53, 38, 23, -76, -83, -91, 109, 41, 116, -5, 92, -39, -50, 22, -73, -95, 85, 110, 117, -71, -69, 84, -29, -122, 73, -68, 3, 69, -18, -78, -68, -69, 87, -26, 90, 2, -69, 49, 34, 111, 67, 125, -5, 91, -22, -90, 23, -83, -31, -43, -82, -102, -36, -5, 94, -16, -90, 4, -72, -16, -45, 111, 85, -95, 59, 94, -5, -10, 86, -67, 9, -61, 107, 102, -23, 27, 89, -45, -34, 87, -64, -47, -75, 110, 109, 13, -4, 88, -56, -42, 23, -84, 1, -90, 112, -125, -3, 43, 47, 8, 39, 85, -62, 9, -58, 111, 118, 45, 92, 94, -8, 118, 87, -61, 83, -28, 112, 95, -99, -5, 97, -79, -86, -44, -63, 15, 38, 113, 27, 9, -52, 21, -55, -58, 86, -59, -107, -123, -79, -55, -116, -6, 93, 27, -57, 82, -63, 51, 102, -21, -118, 49, -100, 87, -42, -50, 23, 22, -22, 116, -94, 124, 19, 75, -29, 31, -21, 24, 35, -42, 60, -16, 110, 48, 107, 47, -2, -122, 117, 87, 71, 53, -15, 72, -102, -4, -40, 39, 11, 57, -54, -24, -19, 49, -71, -94, -54, -30, -8, 110, 121, 52, -79, 112, 108, -7, -52, 91, 87, 72, 108, 21, -56, 65, 54, 115, -84, -54, 12, 101, 69, 106, -11, -103, -15, 73, -120, 116, 127, -30, 102, -111, 10, -9, -75, 82, -42, -34, -99, -17, 89, -2, 103, -15, -123, -7, -78, 99, 70, 25, -126, -101, 91, -24, 116, -2, 25, -48, 106, 126, -85, -120, 9, 109, -37, 13, 91, 57, -58, 113, -26, -29, -95, 55, 8, 40, 71, 15, 26, -46, -111, 126, -15, -93, 41, -99, 104, -19, -19, -20, -52, -26, 11, 11, 44, -15, 92, -23, 83, 46, -71, 15, 88, 54, -12, -86, -67, -52, 85, 77, 95, -102, -45, 125, 118, -11, -89, 119, 28, -22, 81, -33, 22, -87, -78, -98, 52, -83, -113, -84, -22, 91, 67, 44, -123, 120, -55, -77, -82, -5, 11, -41, -116, 8, -69, -59, 46, -74, -12, -84, -29, 68, -20, 45, -76, -6, -40, 92, 44, 53, -109, 55, -19, -109, 84, -65, 81, -37, 17, -14, -62, -76, -87, 29, -36, 49, 77, 12, -37, -39, -26, 105, 38, -115, -83, 31, 111, 23, 25, -36, -91, 44, -92, 17, 58, -67, -39, 87, 55, 22, -57, -74, -26, 118, 114, -46, -72, 110, 118, 87, -5, 88, -100, -94, -73, 35, -56, 77, 20, 115, -41, 59, -41, -9, 86, -9, -73, -11, -83, 54, 69, -125, -77, 4, 52, -35, -112, -68, -25, -19, 111, 31, -62, -5, -31, 75, -56, 55, -62, -125, 45, -25, -123, -65, -110, -63, -8, 126, -74, 81, 4, 46, 113, -58, 46, 59, -125, -13, 108, -10, -59, -11, 118, 80, -112, 35, -101, -57, -13, 53, 57, -122, 86, -28, -14, -116, -93, 121, -28, 21, 47, 121, -83, 79, -2, 77, -2, -99, 89, 123, -29, 46, 109, -7, -51, -69, 101, 24, 120, -5, 25, -51, -68, 90, -71, -81, -11, 108, 111, -100, -57, -53, 12, 20, -79, -25, -71, -35, -19, -86, -58, -60, -100, -44, 54, -91, -7, 27, -37, 109, 113, -100, -37, 60, -27, 17, 95, 110, 32, -121, -98, 108, 38, 109, -26, -25, 57, 95, -76, -40, -67, -50, -26, -112, 11, 93, -21, 10, -89, -95, 75, 120, 94, 24, -75, -117, 11, -32, 120, 2, 118, -37, 73, 75, 119, 49, -122, 18, -35, 108, 47, -105, -52, -109, -2, -12, -72, -105, -35, -82, -108, -23, -14, -32, -71, -101, -9, 109, -17, 57, -57, 124, -41, -6, -100, -115, 108, -9, 18, 47, -111, -27, 127, -103, 58, -39, -9, 125, 116, -80, 119, -67, -17, -42, -78, 57, -63, 3, 47, -71, -65, 103, 26, 51, -106, 55, 60, -26, 23, 127, -32, -125, 115, -98, -24, -4, 70, -5, -33, -24, -13, -8, 96, -97, 26, -18, -109, 78, -92, 94, 57, -81, -16, -69, -85, 92, -46, -81, -121, 61, -47, 59, -108, 57, 19, -52, -2, -101, -121, 95, 59, -18, -3, 46, 122, -29, 123, -6, 112, -36, -119, 60, 109, -126, -49, -16, 101, -77, -8, -10, -57, -49, -88, -25, -107, 15, 122, 12, 2, 39, -14, 89, 103, 60, -22, -77, 70, -11, -87, 95, -100, -11, -30, -34, 117, -28, -100, 19, -5, -21, -101, -35, -5, -33, 119, 54, -9, -86, -65, 46, -35, -2, -117, 95, -15, 100, -69, 78, -6, -109, -65, -2, -44, -73, -65, -8, -27, -121, 127, -58, -27, 127, 121, 3, 103, 55, -10, -9, 123, -8, 55, 124, 62, 39, 121, 59, -25, 127, -15, -89, 126, -90, 39, -128, 109, -13, 29, 5, 24, 126, 105, -25, 106, 62, -9, 118, 11, -120, 43, -18, -25, -128, 114, 7, -127, 4, 120, -128, -112, -25, 113, 121, 100, -127, 62, -126, -127, 89, -62, -127, -50, 119, -126, -38, -121, 126, -7, -41, 125, -116, 55, -126, -44, 87, -126, 31, 56, -127, 20, 72, 127, -11, -25, -127, 102, -89, 122, 17, -58, 126, -3, 87, -126, 69, 39, 123, 27, -88, 121, 29, -72, -126, -65, -105, -125, 27, -74, -125, 48, 24, -125, -28, -90, 75, 71, -24, 70, 6, -56, 107, 67, -72, 104, 45, -104, 101, -82, 71, -126, 73, -24, 95, 75, -8, -126, 44, -28, -124, -79, 6, -123, -77, 39, -123, 73, 101, 126, -83, -41, -125, 81, -57, 127, 1, -120, 116, -60, 84, -122, 79, 24, -127, -28, 7, 120, -93, 18, 110, -17, 55, -122, 103, -40, -122, 53, 24, 118, 32, 104, 119, -124, 119, 31, -79, -121, -123, -23, -75, 119, 72, 24, -121, 41, 56, 127, 0, -56, 116, 51, 120, 121, 120, -8, 30, 122, -8, -123, -12, -46, -121, 112, -8, -121, 63, -120, 125, -39, 87, -121, 23, -88, -127, 10, -104, -121, -120, 104, 73, 115, -24, -125, 111, -40, 120, -115, -56, -2, 101, -127, -104, -122, -83, 39, -119, 116, 88, -121, 26, -62, 77, 121, 6, -124, 15, -72, 123, -100, -8, -119, -98, 40, -120, 62, 34, -122, -85, 8, 92, 102, -72, 125, 31, 101, -118, 40, -120, -118, -113, -88, -118, 92, 119, -118, -114, 56, -120, -70, -120, -126, -57, 34, -117, -51, -105, 82, 105, 83, -124, -53, 87, -116, -71, -8, -118, 76, -40, -117, -124, 40, -127, -83, -88, 85, -63, 40, -116, 14, 21, 104, 97, -120, -119, -115, -104, -116, -44, 120, 18, -108, 24, -124, -121, 4, -115, -77, 40, 83, 13, -73, -120, -97, -105, -117, 118, 40, -125, -54, 8, 29, -53, 8, -120, -79, -72, -117, -34, -40, 71, -32, 120, 122, -72, 56, -114, 78, -92, 43, -95, -13, 69, -23, -120, 57, -84, -24, 123, 34, 39, 79, -17, -120, -113, 113, 104, 91, -7, 71, 123, 78, 103, -113, 85, -40, 94, 74, 49, -115, -26, 104, -117, -15, 8, 85, 0, 25, 125, 2, 41, -121, -38, -104, 98, -53, 112, -112, 89, -72, -114, -56, 40, 101, 12, 73, 44, 14, 57, -112, 26, 87, 97, -20, 117, -117, 9, -87, -112, 23, 89, -114, 25, -55, 89, 31, -55, -120, 72, -42, 115, 17, -106, -118, -15, 120, 125, -118, -42, 111, 37, -7, -112, -101, 56, 89, 0, -106, -128, 43, -55, -110, -55, -25, -110, 25, 4, -109, 27, 89, -111, 11, 70, 121, -112, -40, -115, 33, 9, 90, 24, -39, 116, 52, -2, -55, -117, -3, -40, -111, 32, 102, -116, -16, 120, -109, -122, -108, 123, 32, 100, -108, 71, -39, -116, 33, -61, 113, -6, 23, -108, 77, -87, 92, 83, 105, 43, 63, -71, -115, 90, 9, 57, 74, -87, -110, 87, -39, -108, 1, -13, -108, 73, 80, -107, 82, 73, -108, 68, -29, 99, -40, -120, -108, 66, -23, 90, 94, -119, 42, -123, 38, -109, -80, -104, 120, -83, -12, -115, 39, 73, -111, 105, 57, -106, -94, 86, 77, 116, 66, -125, 76, -55, -109, 33, 104, -105, 100, 85, 67, -105, -40, -109, -52, -72, 127, 123, 121, -122, 57, 41, 109, 117, 9, 125, -102, 8, -110, -125, -55, 56, -123, 25, 101, 8, -87, -105, -118, 9, 126, 125, -119, 107, -121, -39, -107, -105, -71, 86, 119, 54, -108, -100, 9, -126, -104, 41, 64, 101, -119, 8, -101, 71, -123, -111, 73, 103, 94, 40, 88, -128, -23, -117, 20, 71, -102, 114, 5, -105, 72, -46, -106, -94, -24, -104, -85, -7, -106, -83, -119, -102, -74, 57, -110, -92, 57, 108, -90, -103, 8, -94, 89, -109, -73, -119, -101, -120, -25, -121, 104, 105, 117, -66, -55, 123, -103, 87, 37, -77, -87, -100, 49, 41, 97, 120, 57, -119, -55, -7, -100, 75, -55, -100, -51, 73, -99, -43, 73, -121, -99, -23, -100, -120, -103, -104, -38, -7, -126, -1, -42, -101, -32, -103, -114, -36, 41, 98, 67, -109, -120, -28, 57, -100, -22, 105, 13, 63, -73, -121, -2, 47, -41, -128, -80, -55, -98, -1, 55, -97, -49, -48, 101, 11, 103, 60, 115, 89, -97, -78, -73, -97, -89, 113, 100, -8, -87, -101, -30, -40, -97, -4, 57, -96, -58, -32, -98, 59, 89, -96, -53, -7, -99, 9, 74, 100, 89, -87, -111, 12, 106, -99, 11, 10, -95, 26, 21, -102, 8, 58, -95, 15, 121, -95, -60, 96, 23, -122, -119, 98, 25, -86, 119, 30, 122, 20, -68, 4, -94, 10, -6, 32, 52, 56, -94, -81, -39, -95, 39, 58, 79, 37, 42, -97, 19, 42, -94, 42, 10, 122, -128, 112, 127, 48, -118, -100, 41, 73, -93, -121, -57, 106, -86, 121, -93, 90, 120, -93, 49, 42, 35, 58, 74, -93, 60, -38, -93, 24, -102, 7, -69, 57, -92, -15, 25, -107, 71, 74, -97, 112, 32, -106, 71, 58, 65, 46, 122, -99, -86, 66, -101, -62, 57, -92, 79, 10, -95, -126, -55, -128, 109, 80, -101, 61, -86, 70, 32, 106, -94, 51, 10, 68, 54, -39, 3, 45, -118, 123, -58, -28, -91, 20, 8, -90, -17, 38, -99, 113, 105, -94, 61, -24, -111, 12, 90, -105, 105, -6, -110, -17, 57, -89, 88, -22, -90, 41, -102, -96, 105, -39, -99, 99, -54, -125, 106, 58, -98, 127, 40, 99, 39, 42, -127, 84, 10, -103, -57, 88, -108, -98, -119, -127, 73, -22, -95, -125, 10, -92, 88, -41, -89, 117, -6, -89, 119, 122, -106, 35, 26, -118, 58, 9, -111, -86, 67, -2, -88, -85, -105, 100, 85, 122, -115, 108, 57, -111, -12, -40, -92, -43, -73, -106, 74, 74, -114, -39, 25, -104, 22, 58, -99, -66, 57, 122, -92, -70, -123, -115, 106, -98, 102, -118, -100, -86, -6, -87, 78, 90, -104, 114, 106, -107, 104, 8, -99, -90, -102, -88, -5, -56, -86, 109, -121, 54, -62, -105, 61, -45, 73, -100, -110, -118, -114, -67, 90, 124, -65, -38, -112, -29, 67, -105, 109, 74, -84, -107, 106, -84, 50, -89, 35, 124, 74, 51, -38, 121, -87, -49, -70, 56, -64, 7, -84, 79, 68, -83, -57, 105, -83, 40, -57, 31, -39, -86, -83, -43, 73, -112, -35, -22, -83, 74, 2, -82, 61, -93, -98, -23, 73, -82, -74, 26, -112, 57, 83, -97, -20, 74, -82, -66, 106, -87, 16, -12, -88, -32, 57, -84, -15, -38, 121, -13, -102, 38, -99, -86, -100, -9, -118, -81, -16, 26, -114, -10, -45, -119, -95, -118, -81, 87, -118, -88, -123, 122, 70, 3, 11, -80, 5, 123, -90, -111, 26, -80, -48, -30, -99, -52, -54, -80, 112, 122, -80, -98, -102, -80, 40, -86, -85, 19, 91, -96, 123, 106, 116, -54, -118, -92, -82, -86, -79, -17, 90, -79, 21, 88, -86, -126, -105, -85, 70, 26, -78, 23, -54, -79, 34, 88, -78, 95, 119, -86, 14, -101, -78, 27, 59, -78, 62, -44, -78, -66, -12, -78, 48, 27, -77, -5, -71, -78, 39, -37, 72, 55, 43, -79, 57, 75, -98, 59, -2, -21, -77, 122, -12, -86, 51, 11, -76, -10, 106, -76, 69, 43, 57, -14, -58, -83, 71, 75, -79, 56, 107, -110, 68, -101, -105, 32, -21, -76, -30, 10, -75, 60, 107, -91, 8, 43, -76, 85, -69, -83, 25, -85, -76, 54, -21, -88, 98, -54, -75, -3, -103, -76, -62, 90, -113, -4, 72, -75, 99, 27, -101, 87, 59, -76, -80, 106, -79, 11, -85, -74, 65, -53, -74, 53, -102, -75, -87, -103, -87, 113, -117, -103, -113, 38, -95, -50, -104, 55, -53, 106, -89, 120, 107, -75, -32, -123, -91, 103, -46, -86, 100, 10, -72, 55, -87, -73, -41, 73, -72, 24, -69, -73, -121, 43, -73, -126, 43, -104, 89, -28, -72, -108, 10, -71, 71, -57, 60, -109, 75, -71, 84, 69, 96, -63, -125, -71, -103, 75, 95, -101, 43, -71, -99, -101, -95, -119, -69, -73, -98, -76, -95, -1, 41, 46, -94, -85, -78, -107, -37, -74, -17, -29, 125, 81, 43, -96, -86, -5, -72, -97, 107, 98, 38, -101, 122, -80, 43, -74, -78, -101, -82, 87, -72, 58, 11, -92, -81, 28, -87, -69, 121, -86, -124, -52, 4, -107, 107, 26, -68, 58, 59, -68, 21, -127, 4, 77, 123, -68, 100, -101, -68, -72, -6, -69, -43, -40, -68, 72, 123, 104, 30, 81, -68, -6, 57, -67, -69, -53, -69, -42, -117, -87, -84, -7, -77, 39, -30, 84, -39, -53, 49, -49, 75, -68, -77, 19, -123, -2, 10, -66, -122, 40, -66, -2, -90, -74, -67, 108, -110, -84, -24, 123, 98, 76, -75, -66, 101, -123, 97, 99, -44, -79, 127, -101, 91, -14, 59, -65, 118, 6, 112, -10, -21, -87, -68, -128, -86, -5, -101, 90, -83, 27, 122, 64, 25, -91, 29, -90, -88, 2, 28, 98, -33, 41, 24, -83, -72, 11, 16, -91, -64, -27, 68, -69, 121, -31, -124, -72, 16, -84, 17, 76, 24, 50, -120, 24, 21, 124, 11, 23, -116, -63, -39, 0, -112, 27, -36, 79, 29, 12, 53, 31, -36, -98, -27, 72, -63, 127, -105, 11, -123, 99, -62, -82, -127, -62, 13, 60, 58, 22, 124, 50, 45, -52, 14, -92, 91, -62, -94, -73, -62, 51, 76, -61, -59, -96, -63, -42, -31, 118, 101, -53, -94, 34, -68, -61, -61, -16, -62, -36, 91, -77, -8, 43, -98, -113, 49, -60, 26, -6, 105, 49, -60, -81, -91, -91, 48, 75, 44, -61, -40, -10, 71, 31, -5, -67, -101, 25, -59, 82, 12, 103, 31, -88, 74, 13, 56, -59, -117, -85, -59, 90, 54, 104, 93, 108, -128, 95, 92, 29, 97, 108, -58, 98, 115, -79, 25, 124, 35, 104, -36, -96, -34, -21, -79, 64, 49, -83, 110, 76, -95, -71, 9, -79, -96, 17, 51, 116, 44, -58, 29, -29, 105, 9, 53, -57, 122, 60, 92, 112, -62, -104, -7, -117, -82, -128, 92, -57, -31, -118, -64, 69, 99, -56, 126, -20, -82, -124, 44, 53, -117, -52, -56, 41, 51, 97, 77, 3, -2, -55, -111, 44, 59, -1, -11, 65, -107, 12, -59, 109, 28, -65, -52, -89, -55, -114, 28, 34, -119, -84, -126, -97, 60, -55, 32, -77, 94, -100, 75, -54, -91, 108, 41, -89, 60, 63, -87, -116, 104, -105, 12, 106, -32, -29, -54, -99, 124, -58, -15, 86, 64, -77, -52, -54, -75, -100, -60, -73, -116, -53, -69, 44, -56, 100, 49, 66, -67, 28, -53, -90, 28, -60, 28, 36, -52, -66, -100, -59, -89, 57, 68, 72, 124, -52, 107, -94, -60, -56, -20, 66, -51, 60, -52, 48, 28, -100, -53, 108, -72, -46, -68, 66, 124, 12, -52, -42, -116, -51, -74, -116, 85, -77, -6, -93, -42, 124, -59, -35, 108, 112, 58, 5, -50, 123, 32, -50, -16, 75, -50, -27, 28, -66, 10, 43, -101, 112, -85, -91, -23, 60, -73, -21, -116, 6, 107, -103, -69, -81, 44, -49, -13, 76, -49, -59, -90, -66, -9, -100, -53, -7, -20, -75, -5, -20, -105, -85, -54, 101, 105, 12, -48, -29, 44, -48, -115, 73, 101, 7, -93, 11, 7, -67, -75, 9, -51, -50, -15, -111, -74, -59, -20, -48, -22, 12, -47, 125, -14, 101, 19, 93, -51, 21, -83, -49, 23, -83, 68, 0, 0, -46, 33, 45, -46, 35, 77, -46, 37, 109, -46, 39, 13, -46, 43, 74, -62, 40, -51, -46, 45, -19, -46, 47, 13, -45, 49, 45, -45, 51, 77, -45, 53, 29, -45, 30, -19, 112, 54, -83, -45, 34, 29, 55, 43, -2, -67, -45, 63, 13, -44, 65, 45, -44, 67, 45, -44, 56, 45, -93, 68, 93, -45, 61, -67, -57, 72, -51, -44, 77, -19, -44, 79, 109, -45, 70, -99, -66, 80, -3, -45, 111, 76, -43, 87, -115, -43, 89, -3, -44, 82, 61, -43, 90, 61, -45, 75, -19, -43, 97, 45, -42, 99, -35, -46, 92, -99, -93, 100, -3, -46, 96, -115, -42, 107, -51, -42, 90, 109, -42, -31, -36, -42, 39, 125, -56, 113, 77, -41, 117, -19, -44, 111, 125, -42, 118, 77, -46, -103, 56, -51, 122, -19, -41, 127, -99, -44, 120, -99, -41, -128, -115, -46, -48, 76, -40, -121, -115, -40, 38, 45, -40, -125, -99, -40, 35, 109, -40, -115, 13, -39, -119, -67, -40, -24, 28, -39, 37, -67, -51, -107, -115, -39, -124, 61, -39, 112, -99, -39, 41, -83, -52, -99, 13, -38, 126, -67, -39, -108, 29, -38, -115, 27, 8, -91, -115, -38, 113, 61, -38, -92, -99, -38, 123, 61, -91, -83, 13, -37, 104, -67, -82, 101, 125, 109, -79, 109, -38, 94, 100, -37, -71, -19, -43, -77, -51, -46, -29, -106, -37, -40, -87, -37, -63, 77, -43, 74, 74, -44, 94, -13, -37, -15, 44, -36, -55, 125, -41, 42, -70, -37, 85, -122, -39, 76, -86, -36, -47, -35, -44, -93, 75, -41, 53, 22, -39, 113, 32, -35, -39, 93, -36, -85, -85, -38, -97, -43, -39, -40, -83, -35, -31, 93, -43, 50, -85, -39, -34, -35, -40, -2, 61, 34, -34, -23, 29, -40, -20, -7, -35, -42, 109, -41, -84, -83, -34, -15, -19, -46, 34, -5, -36, -58, -51, -42, 126, 32, -33, -7, 13, -45, 93, -117, -38, -126, -74, -42, -8, -83, -33, 1, 94, -40, -78, 122, -36, -20, -69, -43, 57, 45, -32, 9, -18, -40, 123, 41, -35, 87, -90, -45, -105, -83, -32, 10, -82, -104, 13, -34, 102, -21, -67, -47, 17, -82, -34, -4, -99, -35, -51, -118, -31, -31, -3, -100, -7, -67, -85, 29, -98, -34, 31, 46, -33, 33, 46, -30, 30, -98, -73, 9, 46, -86, 39, -98, -31, 93, -30, -113, 34, -82, -87, 1, -2, -75, -1, -83, -44, 41, -14, -30, 29, 30, -29, -6, 61, -29, -78, 93, -29, 52, 50, -122, 44, 126, -37, -110, 25, -38, 124, 109, -103, 52, -66, 113, 28, 14, -29, 74, 7, -37, 68, 62, -75, 70, 94, 74, 38, 14, -28, -98, 61, 126, 78, -82, -103, -53, -85, -41, 20, 1, -27, 81, -82, -28, 60, 62, 120, 74, 32, -38, -64, -70, -128, 81, 110, -39, 8, 119, -33, 69, 58, -44, 124, -71, -107, -16, 39, -26, 3, 14, 110, 101, 14, -34, 69, -19, 62, -12, -88, -26, 107, -82, -40, -20, -58, -27, -24, 61, -34, 31, -67, -30, 116, 94, -25, -44, 118, -25, 102, -98, -25, -39, 92, -90, 124, 78, -37, -70, 54, -42, 23, 126, -45, 116, -110, -29, -124, 46, -41, -57, 118, -24, -97, 45, -2, -45, -22, -74, -24, -116, 62, -26, -122, 46, -42, -76, -68, -32, -118, -34, 119, -108, 94, -24, 91, 115, -23, 38, 51, -24, -100, -34, -23, 106, 25, -42, 9, 19, -22, -94, -50, -26, 84, -7, -23, 28, -77, -23, -88, -98, -42, 76, 83, -22, -96, -50, 118, -82, -66, -33, -86, 30, -21, -54, 38, -27, 91, 78, -21, -67, 109, -21, -51, -115, -21, 65, 94, -100, 0, 32, -20, -61, 78, -20, -59, 110, -20, -57, -114, -20, -55, -82, -20, -53, -50, -20, -51, -18, -20, -49, 110, -20, -92, 14, -19, -45, -114, -20, -29, 11, -20, -97, 73, -19, -39, -82, -19, -37, -50, -19, -35, 30, -19, 66, -18, -19, -51, 110, -19, -77, 30, -18, -27, -82, -20, 86, 108, -18, -34, 30, -20, -23, -2, -19, -65, 78, -18, -20, 14, -17, 123, 22, -17, -13, 94, -19, -116, 70, -17, -60, 62, -18, 39, 119, -17, -27, -114, -75, -5, 14, -19, -21, -66, -17, -72, -18, 120, -2, -98, -19, 90, 75, -16, -11, -98, 102, 7, -17, 50, -41, 126, 85, 7, -65, -20, 6, -17, -16, -59, 110, -17, 17, -65, -16, -70, 78, -15, -26, 27, -15, -1, -98, -16, 10, -1, 86, 22, 31, -16, -112, -102, -15, 15, -65, -15, 4, 111, 106, -6, 78, -14, -72, 29, -14, -25, -34, -16, 33, -1, 52, 38, -17, -17, 97, -102, -14, 8, 63, 101, 41, -33, -14, 83, 126, -17, 31, 29, -13, -55, -2, -66, -14, 52, -49, -65, 100, 126, -14, -39, -100, -13, 58, -113, 105, 65, 63, -20, 94, -29, -13, 47, -113, -13, 68, -33, -18, 51, -87, -12, -62, 78, -49, 63, -81, -48, 77, -17, -12, 67, 47, -11, -21, -52, -15, 3, 93, -11, -99, 37, -11, 12, 15, 26, 54, -1, -15, 88, -33, -12, 84, 31, -10, -70, -93, 111, 14, -81, 6, 91, -49, -11, -74, -127, -10, -8, -18, 63, 118, 110, -10, -50, -68, -11, 76, 31, -9, 109, -33, -26, 80, 15, -9, 89, -17, -109, 107, 95, 63, 110, -113, -12, -89, -115, -10, -80, 54, -10, 59, -27, -25, 87, 47, 38, 115, -97, -9, 127, -1, 80, -114, 78, -17, 0, -114, -9, 84, -78, -10, 69, -97, -8, -106, 62, -17, -116, 31, -8, -114, -1, -8, 105, 95, -61, 83, 63, 106, 55, 127, -44, 68, 127, -8, -113, 47, -8, -110, 15, -17, -112, -98, -13, -97, -81, -9, -117, 37, -6, -93, 79, -6, 60, 31, -111, -105, -113, -7, 6, -6, -6, -92, -72, -8, -85, -49, -14, -59, -27, -6, -112, 127, 78, -87, -49, -18, 88, 124, -15, 14, 114, -5, 50, 15, 69, -98, 62, -7, 125, 109, -9, -121, -8, -5, 75, 31, -4, -80, 30, -17, -113, -35, -9, 3, 114, -4, -56, 15, 67, -62, -65, -4, -1, 92, -4, -25, -15, -4, 18, -65, 79, -46, -65, -5, -104, -2, -11, -50, 127, -3, -72, 95, 81, -38, -97, -18, -2, -104, -52, -7, -66, -1, -3, 123, -81, -4, -37, 79, -2, -45, 111, -2, -41, -113, -2, -46, -82, -6, -94, -84, -2, -34, -17, -2, -39, 47, -2, -4, -50, 103, -13, 95, -119, -11, -97, -5, -9, 31, -18, -106, 76, 0, -16, 49, 117, -93, -3, 97, -108, -109, 86, 123, 113, -42, -101, 119, -1, -77, 69, 28, -55, -46, 84, -64, 84, 93, -39, -42, 49, -36, 88, -98, -23, -38, -66, -33, 83, 71, -15, 94, -34, -127, 60, -33, -112, 88, 52, -78, -126, 73, -27, -24, -40, 116, 106, 96, 79, -23, -108, 90, -75, 44, 117, 86, 109, 0, -69, -37, 126, -63, -31, 73, -105, 12, 20, -97, -47, 105, -11, -38, 89, 38, -79, -117, -18, 18, -100, 94, -97, -55, -15, 34, -5, -98, -33, -9, -121, -13, -124, -2, 104, 2, 17, 6, 15, 17, 115, 10, 3, 19, 27, 29, 31, 33, 41, 22, 13, 35, 91, 22, 43, 49, -45, 38, -15, 50, 59, 61, 63, -47, 54, -93, 64, 63, 24, 73, 79, -37, 68, -53, 80, 89, 91, 93, -17, 68, 95, -95, -14, 100, 107, 9, 85, -55, 108, 117, 119, 121, 27, 84, 123, -57, -28, -128, -121, 61, 112, 115, -119, -111, -109, 31, -115, -107, 31, -36, -102, -95, 37, -116, -69, -94, -85, -83, -41, -104, -93, -97, -81, -75, -89, -105, -72, -63, -61, -85, -78, -95, -73, -59, -111, -67, -87, -49, -41, -39, 111, -56, -101, -51, -37, -2, 121, -45, -79, -28, -19, -17, -111, 54, -83, -29, -15, 101, -23, -65, -5, 5, 20, 120, 33, 86, 53, 126, 3, 81, -3, 83, -126, -112, 97, 67, 8, -123, -82, -83, 114, -40, 74, 97, -110, -119, 23, 7, -46, -38, 119, 12, 35, -87, -118, 65, 58, -122, -76, -89, -47, 32, 71, -111, -97, 62, -102, 57, -71, 18, 28, -89, -115, -22, 88, -94, 76, -23, 101, -30, -88, -104, 37, 37, -66, -84, 119, -45, -45, 76, -102, 14, 109, -14, -124, 119, 112, 40, 76, -95, -107, 124, -86, 100, 24, -12, 104, 50, -94, -54, 76, 54, -123, -108, -12, 39, 66, -90, 82, -121, 69, -59, 9, 16, 107, 36, -110, 92, 32, 54, -68, -38, 117, -98, 81, -99, 11, -55, 78, 117, -23, 43, -20, 82, 0, 105, -47, -103, -35, -118, 22, 110, 35, 97, -46, -38, 10, -92, 84, -73, 23, 87, 110, 90, -7, -6, -71, 27, -31, -110, -43, 3, -127, -5, -46, -3, 43, 23, 49, -97, -63, 120, 77, 29, 57, -127, 104, 111, -29, 87, 126, 35, 50, -74, 92, -25, 49, -31, -68, 113, 76, 80, 62, -68, -7, 50, -56, 115, -128, 73, 115, 30, 109, -79, -62, 36, 28, -99, -31, 84, 78, 125, -54, -12, 105, -51, -77, -39, 80, 98, 45, -87, -80, 13, -40, -71, 71, -29, -90, -83, 84, 28, 106, -31, 106, -104, 18, -25, -3, 57, 5, 46, 58, -126, -114, 103, -86, 93, -4, 118, -2, 116, 59, -109, 67, 20, 92, 78, -11, -19, -113, -86, 87, 18, 88, -105, -98, -123, -99, 113, -15, -86, -25, 100, -41, -41, -102, -5, -40, -26, -33, -39, -53, 62, 111, -105, -4, 58, -13, -13, -81, -21, -23, -48, -98, -65, -73, 89, -18, -15, 19, -84, 62, -37, 118, 10, 112, 16, 38, -10, -21, 79, 65, -19, 8, 10, -50, 64, -47, 66, 107, -89, -64, 7, 15, -111, -49, -123, 5, 49, -84, -18, 33, 11, 41, 68, 47, -67, -14, 48, -21, -80, 15, 14, -13, -55, -48, 68, -27, -30, 3, 80, 68, -28, -80, 3, 81, -79, 21, -53, 57, 81, 70, 4, 49, 96, 0, 70, 15, 105, 116, 113, -73, 27, -117, -102, -47, 71, 7, -63, 11, -113, -57, -40, 34, -108, -16, -59, 33, -95, -6, -15, 71, 14, 108, 68, 50, -108, 55, -16, 57, -46, 73, 98, -108, 92, 114, -125, 38, -89, 4, 4, -54, 123, -92, -52, 18, -104, 42, 103, 76, -112, 68, 47, -89, -8, 112, 5, 21, 101, -126, -113, -52, -72, -64, -60, -80, -108, -18, -42, 4, -61, 76, 121, -90, -117, -109, -51, 54, 21, 124, -45, 78, 57, -73, 28, 9, -59, 61, -77, -62, -77, 63, 64, 33, -60, -46, -49, 1, 9, -91, 82, 80, -2, 18, 61, 48, 71, 31, 12, 45, -76, -56, 70, -109, 92, 116, -90, -9, 40, -107, -94, -49, 33, 30, -3, 3, -47, 76, -17, -76, -76, 34, 76, 65, -2, 109, 98, 83, 72, -11, -109, -76, -45, 82, -83, 64, -109, 83, 81, -1, -79, 4, 78, 86, -119, 56, -43, -73, 73, 61, 109, -111, 86, 45, 92, 69, 21, -42, 116, 100, -35, 21, -76, 84, 95, -5, -13, -71, 79, -123, -43, 20, 72, 101, 127, 101, -112, -44, 100, 95, 37, -42, 29, 99, -119, -44, 21, 90, 102, 103, 45, -77, 89, 103, 65, -24, -11, -38, 51, 87, -67, 112, -62, -4, -84, -3, -10, -119, 49, 77, 93, 22, -84, 109, -111, 53, -105, 69, 105, -59, -3, -48, 86, 108, -38, 117, 119, -40, 108, -87, -80, -112, 93, 53, -19, 125, 18, 94, 89, -27, 13, -9, 93, 92, -5, 77, 23, 95, 122, -9, -123, -82, 96, 127, 5, 126, 111, 78, 69, -44, -43, -124, -38, -123, 107, -51, -43, -89, 13, 115, -94, 120, -32, 127, -29, -115, -12, 33, -57, -8, -43, -72, 98, 1, 47, 118, -26, 41, -111, -65, 32, -40, 59, -113, 29, 13, 25, -27, 110, 70, 77, 113, -30, -105, 121, 125, 120, 101, -123, 43, -100, -103, 102, -89, 62, 10, -78, -50, -99, -7, -100, -9, -26, -125, 45, -42, 25, -24, 80, -89, -87, -15, -25, -93, 83, -74, 25, -26, -91, -103, 126, -38, -65, 6, -115, -114, 58, 21, -95, 123, -84, -38, 106, -92, 123, -69, -46, -23, -83, -57, -7, 90, 81, 13, -63, -26, -7, 23, 49, 27, 46, -5, 106, -84, 41, 90, 79, -19, 87, -2, -67, 98, -50, -21, -73, 37, 22, -69, 39, 96, -23, 70, 53, -18, -56, -118, 33, 58, -17, -80, -19, -18, -124, -98, -65, 123, -16, 86, -83, 114, 9, 79, 4, -15, -69, -15, 78, 124, -38, -46, 22, 119, -68, -24, -76, 15, 31, 92, -14, 91, 107, 9, 92, 50, -65, 47, 87, -113, -19, -54, 27, -17, -4, 102, -56, 89, 62, -105, 115, -47, -107, -42, -36, -111, 16, -95, 70, 93, 5, 116, -57, -29, -40, 116, -41, -69, 85, -103, 113, -69, -75, -90, -35, -13, -52, -123, -84, -39, 112, -35, 33, -90, 124, 111, -36, 93, 6, -98, -55, -120, 61, -22, -67, 85, -28, -115, 15, 70, 117, -48, 75, 15, -2, -7, -26, 83, 108, 91, -10, 84, -88, -9, -38, 118, 80, 60, 6, -78, -8, -20, 83, 63, 29, 41, -31, 65, 3, -97, -22, -56, -109, -81, 30, -29, -49, -51, 87, 127, 56, -14, 125, 21, 31, -4, -36, -57, 119, 95, -6, -24, -37, -9, -103, -107, -23, -117, -3, -67, 115, -78, 109, 33, 31, -2, -27, 111, 125, -36, -85, -41, -26, 8, 104, -78, 16, 5, -22, 42, -20, 75, 32, 100, -28, -89, 42, -8, 21, 46, -126, -70, 3, 32, -17, 26, 56, -63, 7, 66, -20, 118, 3, 12, 95, -126, 54, -72, -82, 46, 125, -119, 104, 26, 12, -95, 8, -57, -9, -67, -38, -31, 12, 47, 39, 20, 33, -3, -72, -26, 66, -19, 85, -112, 92, 7, -2, 124, 29, -2, 114, -32, -62, 5, -74, -124, -123, 50, -124, -32, 50, 70, 8, -82, 30, 42, 80, -121, 65, 60, -117, -1, 66, 8, -69, 26, -62, -16, 63, -54, 91, -114, 12, -115, -72, 7, 26, 118, 108, -118, 80, 100, -34, 13, -107, -120, -74, 11, -82, 112, -120, 108, -87, 34, -19, -94, 120, -84, 43, -62, 98, -116, 62, 84, -32, 23, 61, -121, -60, -13, 109, -15, 77, 56, 44, 35, -11, -62, 88, 45, 53, 114, 17, -115, 73, 124, 99, -33, -70, -40, -60, -109, 1, -116, 99, 78, 124, 96, 28, -127, -109, 69, 33, 10, -46, -116, -124, 60, 94, 30, -45, -104, -79, 91, -124, -53, -113, 9, 4, 36, -62, -18, -120, 69, 67, 90, 113, -114, 107, -100, -28, -3, 126, 19, 3, 93, -31, 48, 123, -113, -36, 88, 29, 15, 121, -55, 19, -118, 50, 124, -115, 76, -28, 90, 106, -80, 73, 68, -62, -111, -119, 12, -117, -92, -61, 94, -23, 67, 82, 90, 50, -106, -104, -36, 99, -119, 60, 105, -68, -42, -127, -52, -108, 67, -85, -28, 6, 103, -23, -77, 94, -46, 50, -109, 84, -36, 101, -2, -114, -71, -60, 95, -90, -47, -116, 104, -69, 21, 39, 61, 115, -74, 69, -26, 18, 120, -55, -60, -47, 50, 75, -39, -52, 80, 62, 115, -107, -74, -100, 26, 44, -83, 105, -66, 112, -54, -79, -106, 90, -44, -26, 12, -71, 57, 76, 8, -58, 12, -100, -83, -76, -2, -96, 59, -125, -42, 77, 112, -98, -77, -119, -3, 19, 100, -62, 84, 40, 78, 120, 54, 13, -102, -110, 4, -27, 40, -53, -87, 71, 111, -31, -45, -122, -114, 44, 40, 57, -125, 105, 73, 122, 30, -81, 86, 52, 36, 40, -6, -2, 120, -48, 64, -86, 19, -105, -1, -108, -91, 69, 103, 49, -61, -121, 94, 47, -94, 16, 21, 35, 69, 7, -119, -47, 34, 106, 105, -93, 38, -100, -97, 68, 33, -103, -48, 53, 46, -108, -95, -15, 44, -87, 60, -37, -105, -49, -108, 6, 52, -111, 44, 5, -31, 22, 94, 106, -46, 78, -94, -12, -109, -10, 84, -23, 69, -7, -103, 83, -112, 26, -44, -93, 8, -11, 41, 77, -75, -119, 77, 77, 10, 21, -90, 49, 45, -22, 76, 69, -70, 29, -91, 22, 50, -86, 124, 100, -22, 84, 93, -57, 83, 87, 98, 117, -99, 54, 125, 86, -66, -82, -54, -43, -84, 62, -11, -109, 85, -19, -86, 87, -65, 106, -70, -80, 122, 85, -85, 103, 104, -22, 54, -47, 122, 67, -64, 93, 53, -82, 30, 20, -61, 91, -75, 23, 87, -85, -22, 84, -96, 57, -83, -85, 3, 103, -86, 87, -124, 114, 52, -92, 66, -3, 43, 95, 93, 106, 86, -63, 78, 75, -90, 125, -11, -21, 97, -5, -39, -45, -59, 66, 21, -80, -114, 125, -87, 94, 43, 91, 55, -92, 58, 111, -78, -66, -76, -85, -13, -104, -70, -40, -52, 110, 117, -111, -99, 93, 106, -2, 99, -119, -71, -47, -55, 18, -10, -93, 92, -59, -85, 105, -93, -71, -49, 23, -122, 86, -76, -111, -43, -20, 102, -49, 40, -42, -38, -114, 83, -86, -76, 21, 44, 107, 39, 106, 86, -37, -62, 86, -104, 109, -51, -19, 99, 87, 59, 92, -73, -2, -12, 126, -60, -11, -84, 7, -61, -118, -37, -114, 74, 119, 121, -118, 85, -82, 115, 123, -69, 61, -103, -7, -74, -77, -52, -59, -60, 117, -79, 91, 92, -19, -82, 19, -71, -80, -11, 110, 10, -49, 27, -34, -39, -114, 55, -74, -122, -59, 46, 117, -47, -101, 94, -11, -82, 87, 115, -47, -43, 45, 50, -17, 75, -97, -95, -50, 55, -108, 7, -75, -81, 98, 9, 8, -33, -31, 9, -104, -65, 94, -12, -17, 90, 11, 108, -74, -41, 38, 24, -76, 69, -115, 46, -125, 99, 8, -31, -45, 82, -53, -66, 18, 102, -32, 126, 45, -100, 90, -95, 85, 56, -61, 101, 89, 112, -121, -41, -89, 38, 14, -125, 56, -128, 31, 38, -79, 55, 87, 53, -30, 19, 91, 15, -61, 43, 22, -17, 127, -115, -21, 98, -3, -74, 88, -58, -37, -123, -15, 104, 107, -68, 58, 26, -25, 56, -69, 55, 6, 46, -113, -127, -72, 99, 32, 55, -40, -57, 66, 30, 114, 113, -63, -126, 83, 35, 31, 57, -60, 69, -106, -17, -112, 81, -112, 95, 60, 18, 24, -56, 78, 126, 50, -109, -23, 123, -27, 82, 74, 89, -58, 86, -90, 50, -106, -2, -77, 108, -30, -2, 114, -103, -57, 94, 6, -13, 52, 63, -37, 70, 45, 87, -39, -54, 103, 78, -27, -114, -48, 76, -26, 50, -5, -40, -51, 70, -128, 115, 59, 1, 12, -26, 34, -41, -7, 94, 49, 38, 111, -98, -49, 124, 99, 62, -37, -7, 62, -19, -107, 51, -109, 5, 61, -24, -51, 61, 18, -68, -118, 126, 113, 73, 29, -115, 45, -78, -2, 56, -46, 99, -18, 26, -118, -99, 83, 105, -125, -95, -108, -46, -102, 62, -91, -36, 46, -27, -23, 77, -41, -73, -45, -94, -2, -12, 45, 13, -4, 77, 83, 55, -108, -67, 97, 94, -11, 115, 81, 29, 102, -73, -67, 58, 126, -20, -61, 49, -83, 109, -52, -58, 38, 95, 26, -41, 113, -90, -36, -83, 123, -35, -29, 113, 105, -44, 53, -63, -10, 53, 107, -127, 109, 108, 34, -21, 122, -41, 124, 83, -74, 63, 91, -115, -40, 103, -1, 121, -40, -60, -106, -37, -76, -75, 24, -19, 82, 99, -5, -47, -44, 108, -74, 34, -71, 109, 109, 91, 75, 59, -36, -33, -10, -10, 113, -117, 89, -18, 92, -69, 49, -51, -22, -90, -10, -99, -43, 12, 110, 119, 111, -7, -41, -19, -98, -73, -95, -103, -115, -17, 115, -33, 27, -49, 107, 14, -9, 87, -30, 93, 109, 126, -89, -77, -47, 3, -17, 54, 53, -27, 109, 112, -126, -117, 89, -31, -6, 22, -8, -63, -55, -38, -16, 126, 127, 89, -30, -78, -42, 80, -62, 43, -32, 62, -31, -126, 103, 92, -40, 24, 79, -11, -66, 57, -82, -31, -112, 51, -10, 68, 35, 39, 57, -61, 77, -18, -16, 65, -91, -4, -40, 44, 63, -7, -126, 92, -82, 113, 127, -89, 92, 70, 49, 55, 38, -59, 109, -114, 105, -53, -27, -4, -26, 60, -89, 32, 119, 124, 110, -52, -96, -41, -70, 103, 67, -57, -91, -47, -5, 28, 58, -92, -61, 114, -23, -47, -118, 85, -45, -79, 8, 117, 86, 39, 77, -22, 85, 31, 44, -73, -84, -98, -11, -69, 102, 90, -21, 93, -33, 42, -81, -67, 30, 118, -79, -113, -99, -20, 101, 55, -5, -39, -47, -98, 118, -75, -81, -99, -19, 109, 119, -5, -37, -31, 30, 119, -71, -49, -99, -18, 117, -73, -5, -35, -15, -98, 119, -67, -17, -99, -17, 125, -9, -5, -33, 1, 31, 120, -63, 15, -98, -16, -123, 55, -4, -31, 17, -97, 120, -59, 47, -98, -15, -115, 119, -4, -29, 33, 31, 121, -55, 79, -98, -14, -107, -73, -4, -27, 49, -97, 121, -51, 111, -98, -13, -99, -9, -4, -25, 65, 31, 122, -47, -113, -98, -12, -91, 55, -3, -23, 81, -97, 122, -43, -81, -98, -11, -83, 119, -3, -21, 97, 31, 123, -39, -49, -98, -10, -75, -73, -3, -19, 113, -33, -117, 2, 0, 0, 59,};

}