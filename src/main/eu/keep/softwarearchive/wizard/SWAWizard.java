package eu.keep.softwarearchive.wizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SWAWizard extends JFrame {

    public static final Color LIGHT_RED = new Color(255, 212, 222);

    protected static final String MANDATORY_MESSAGE = "<mandatory field>";

    protected Step1_App step1;
    protected Step2_OpSys step2;
    protected Step3_HardwareAndFormat step3;
    protected Step4_ZippedImage step4;
    protected Step5_FileFormat step5;
    protected ConfirmPanel confirm;

    private JLabel logLabel;

    public SWAWizard() {
        super("SWA wizard");

        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setResizable(false);
        super.setSize(500, 600);

        step1 = new Step1_App(this);
        step2 = new Step2_OpSys(this);
        step3 = new Step3_HardwareAndFormat(this);
        step4 = new Step4_ZippedImage(this);
        step5 = new Step5_FileFormat(this);
        confirm = new ConfirmPanel(this);

        logLabel = new JLabel("1/5");

        super.setLayout(new BorderLayout(5, 5));

        super.add(step1, BorderLayout.CENTER);
        super.add(logLabel, BorderLayout.SOUTH);

        super.setVisible(true);
    }

    public static JTextField createTxtField(Dimension d, boolean mandatory) {
        final JTextField txt = new JTextField();
        if(mandatory) {
            txt.setBackground(SWAWizard.LIGHT_RED);
            txt.setText(MANDATORY_MESSAGE);

            txt.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if(txt.getText().trim().equals(MANDATORY_MESSAGE)) {
                        txt.setText("");
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if(txt.getText().trim().isEmpty()) {
                        txt.setText(MANDATORY_MESSAGE);
                    }
                }
            });

            txt.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String s = txt.getText().trim();
                    if(s.isEmpty() || s.equals(MANDATORY_MESSAGE)) {
                        txt.setBackground(SWAWizard.LIGHT_RED);
                    }
                    else {
                        txt.setBackground(Color.WHITE);
                    }
                }
            });
        }
        txt.setPreferredSize(d);
        return txt;
    }

    protected void log(String message) {
        logLabel.setText(message);
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SWAWizard();
            }
        });
    }
}
