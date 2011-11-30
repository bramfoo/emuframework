package eu.keep.gui.settings;

import java.awt.FlowLayout;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

/**
 * View to edit the hostName and Port of a URL Property.
 * @author nooe
 */
public class UrlPropertyEditor extends JPanel implements EFPropertyEditor {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String protocol = "http://";
	private static final String colon = ":";
	
    private JTextField hostName = new JTextField();
    private JTextField portNumber = new JTextField();
    private String path = "";

    public UrlPropertyEditor(String initialValue, String path) {
    	
    	initEditor(initialValue, path);
		
    	FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
    	layout.setHgap(1);    	
    	this.setLayout(layout);
    	
        // String everything together
        this.add(new JLabel(protocol));
        this.add(hostName);
        this.add(new JLabel(":"));
        this.add(portNumber);
        this.add(new JLabel(path));        
	}

	/**
	 * Initialise editor elements
	 * @param initialValue
	 * @param path
	 */
    private void initEditor(String initialValue, String path) {
		try {
       		URL initialURL = new URL(initialValue);
       		hostName.setText(initialURL.getHost());
       		hostName.setColumns(7);
       		portNumber.setText(String.valueOf(initialURL.getPort()));
       		portNumber.setColumns(4);
		}
       	catch (MalformedURLException mfue) {
       		// initial value is not a valid URL. Leave text fields empty
       		logger.warn("Current property is not a valid URL: " + initialValue);
       	}
		this.path = path;	
   }
    
	@Override
	public String getValue() {

		String enteredHost = hostName.getText();
		String enteredPort = portNumber.getText();
		
		return protocol + enteredHost + colon + enteredPort + path;
	}
		
}
