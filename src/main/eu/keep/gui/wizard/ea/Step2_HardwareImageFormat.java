/*
 * $Revision: 114 $ $Date: 2011-12-01 12:25:35 +0100 (Thu, 01 Dec 2011) $
 * $Author: bkiers $
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
package eu.keep.gui.wizard.ea;

import eu.keep.gui.util.DBUtil;
import eu.keep.gui.util.RBLanguages;
import eu.keep.gui.wizard.ea.model.Hardware;
import eu.keep.gui.wizard.ea.model.ImageFormat;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;

public class Step2_HardwareImageFormat extends JPanel {

    protected Hardware[] hardwares = null;
    protected ImageFormat[] formats = null;
    
    private JList hardwareList;
    private JList imageFormatList;
    
    private JButton next;
    private JButton previous;

    public Step2_HardwareImageFormat(final EAWizardAdd parent) {

        super.setLayout(new BorderLayout(5, 5));

        final Dimension d = new Dimension(320, 150);

        final JPanel center = new JPanel(new MigLayout());
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final String explanation = RBLanguages.get("ea_hw_explanation");

        Vector<Vector<String>> hardwareData = DBUtil.query(DBUtil.DB.EA, "select hardware_id, name from emulatorarchive.hardware");
        Vector<Vector<String>> imageFormatData = DBUtil.query(DBUtil.DB.EA, "select imageformat_id, name from emulatorarchive.imageformats");

        Vector<Hardware> hardwareVector = new Vector<Hardware>();
        for(Vector<String> row : hardwareData) {
            hardwareVector.add(new Hardware(row.get(0), row.get(1)));
        }

        Vector<ImageFormat> imageFormatVector = new Vector<ImageFormat>();
        for(Vector<String> row : imageFormatData) {
            imageFormatVector.add(new ImageFormat(row.get(0), row.get(1)));
        }

        hardwareList = new JList(hardwareVector);
        hardwareList.setVisibleRowCount(5);        
        JScrollPane hardwareListScroller = new JScrollPane(hardwareList);
        hardwareListScroller.setPreferredSize(d);
                
        imageFormatList = new JList(imageFormatVector);
        imageFormatList.setVisibleRowCount(5);
        JScrollPane imageFormatListScroller = new JScrollPane(imageFormatList);
        imageFormatListScroller.setPreferredSize(d);
        
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel("<html>" + explanation + "</html>"),      "span 2 1 wrap" );
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(" "),                                     "wrap"          ); // empty line
        center.add(new JLabel(RBLanguages.get("hardware_platform") + ": ")              );
        center.add(hardwareListScroller,                                "wrap"          );
        center.add(new JLabel(RBLanguages.get("image_format") + ": ")                   );
        center.add(imageFormatListScroller,                             "wrap"          );

        previous = new JButton("<html>&larr;</html>");
        next = new JButton("<html>&rarr;</html>");
        next.setEnabled(false);

        buttons.add(previous);
        buttons.add(next);

        super.add(center, BorderLayout.CENTER);
        super.add(buttons, BorderLayout.SOUTH);

        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step2);
                parent.add(parent.step1, BorderLayout.CENTER);
                parent.log("1/2, " + RBLanguages.get("select_emulator"));
                parent.validate();
                parent.repaint();
            }
        });

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.remove(parent.step2);
                parent.add(parent.confirm, BorderLayout.CENTER);
                parent.log(RBLanguages.get("please_confirm"));
                parent.confirm.init();
                parent.validate();
                parent.repaint();
            }
        });

        hardwareList.addListSelectionListener(new ListSelectionListener() {
        	@Override
        	public void valueChanged(ListSelectionEvent e) {
        		validateInput();
        	}
        });

        imageFormatList.addListSelectionListener(new ListSelectionListener() {
        	@Override
        	public void valueChanged(ListSelectionEvent e) {
        		validateInput();
        	}
        });

    }

	private void validateInput() {
		// Selected hardwares
		Object[] selectedHardwares = hardwareList.getSelectedValues();

		// Selected imageformats
		Object[] selectedImageFormats = imageFormatList.getSelectedValues();

		// Only enable 'next' button if at least one hardware and one imageFormat is selected
		if ((selectedHardwares.length > 0 && !((Hardware)selectedHardwares[0]).isDummy()) &&
			(selectedImageFormats.length > 0 && !((ImageFormat)selectedImageFormats[0]).isDummy())) {
			hardwares = Arrays.copyOf(selectedHardwares, selectedHardwares.length, Hardware[].class);
			formats = Arrays.copyOf(selectedImageFormats, selectedImageFormats.length, ImageFormat[].class);
			next.setEnabled(true);
		}
		else {
			hardwares = null;
			formats = null;
			next.setEnabled(false);
		}
	}
}
