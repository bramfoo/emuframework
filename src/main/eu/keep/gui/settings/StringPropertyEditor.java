package eu.keep.gui.settings;

import javax.swing.JTextField;

/**
 * View to edit a String. A simple JTextField with an additional method to implement EFPropertyEditor.
 * @author nooe
 */
public class StringPropertyEditor extends JTextField implements EFPropertyEditor {

	public StringPropertyEditor(String initialValue) {
		super(initialValue);
	}
	
	@Override
	public String getValue() {
		return super.getText();
	}
		
}
