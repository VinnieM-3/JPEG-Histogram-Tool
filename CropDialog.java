import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.NumberFormat;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class CropDialog extends JDialog implements ActionListener, DocumentListener, PropertyChangeListener {
	private static final long serialVersionUID = 1199118169668015818L;
	private final Color backGroundColor = new Color(20, 20, 20);
 	private CropPanel panel = null;
    private JButton applyButton = null;
    private JButton cancelButton = null;
    private boolean butState = false;
    private int height = 0;
    private int width = 0;
    
    private JComboBox<String> ratioList = null;
    private JFormattedTextField ratioField1 = null;
    private JFormattedTextField ratioField2 = null;   
    private JComboBox<String> orientationList = null;

    private NumberFormat ratioFormat;
    
    private final String RATIO_4_X_6 = "4 x 6";
    private final String RATIO_5_X_7 = "5 x 7";
    private final String RATIO_8_X_10 = "8 x 10";
    private final String RATIO_8_X_12 = "8 x 12";   
    private final String RATIO_10_X_13 = "10 x 13";
    private final String RATIO_10_X_20 = "10 x 20";
    private final String RATIO_11_X_14 = "11 x 14";
    private final String RATIO_12_X_18 = "12 x 18";   
    private final String RATIO_16_X_20 = "16 x 20";
    private final String RATIO_20_X_24 = "20 x 24";
    private final String RATIO_20_X_30 = "20 x 30"; 
    private final String Unrestricted = "Unrestricted";     
    private final String ImageRatio = "Image Ratio"; 
    private final String CustomRatio = "Custom Ratio"; 
    
    private final String[] ratioSizes = {
    		Unrestricted,
    		ImageRatio,
    		CustomRatio,
    		RATIO_4_X_6,
    		RATIO_5_X_7,
    		RATIO_8_X_10,
    		RATIO_8_X_12,
    		RATIO_10_X_13,
    		RATIO_10_X_20,
    		RATIO_11_X_14,
    		RATIO_12_X_18,
    		RATIO_16_X_20,
    		RATIO_20_X_24,
    		RATIO_20_X_30
    };
    
	public CropDialog(JFrame frame, String title, boolean modal, CropPanel cap) {
		super(frame, title, modal);
		panel = cap;
	    JToolBar toolBar = new JToolBar();
	    toolBar.setFloatable(false);
	    
	    ratioFormat = NumberFormat.getNumberInstance();
	    ratioFormat.setMinimumFractionDigits(0);
	    ratioFormat.setMaximumFractionDigits(2);
	    ratioFormat.setMinimumIntegerDigits(0);
	    ratioFormat.setMaximumIntegerDigits(5);  
	    
	    toolBar.addSeparator(new Dimension(100,16));

	    JLabel label0 = new JLabel("Ratio: ");
	    toolBar.add(label0);	    
	    
	    ratioList = new JComboBox<String>(ratioSizes);
	    ratioList.setSelectedIndex(0);
	    ratioList.addActionListener(this);
	    toolBar.add(ratioList);

	    toolBar.addSeparator(new Dimension(50,16));
	 
	    ratioField1 = new JFormattedTextField(ratioFormat);
	    ratioField1.setColumns(8);
	    ratioField1.getDocument().addDocumentListener(this);
	    ratioField1.setEnabled(false);
	    toolBar.add(ratioField1);
	    
	    toolBar.addSeparator(new Dimension(10,16));
	    
	    JLabel label3 = new JLabel(" X ");
	    toolBar.add(label3);
	    
	    toolBar.addSeparator(new Dimension(10,16));

	    ratioField2 = new JFormattedTextField(ratioFormat);
	    ratioField2.setColumns(8);
	    ratioField2.getDocument().addDocumentListener(this);
	    ratioField2.setEnabled(false);
	    toolBar.add(ratioField2);
	    
	    toolBar.addSeparator(new Dimension(50,16));
	    
	    String[] orientationStrings = { "Landscape", "Portrait" };
	    orientationList = new JComboBox<String>(orientationStrings);
	    orientationList.setSelectedIndex(0);
	    orientationList.addActionListener(this);
	    orientationList.setEnabled(false);
	    toolBar.add(orientationList);
	    
	    toolBar.addSeparator(new Dimension(100,16));

	 	height = cap.getHeight() + 95;
		width = cap.getWidth();
		
		this.setAlwaysOnTop(true);
		setResizable(false);
		
		JPanel backPanel = new JPanel(new BorderLayout());
		JPanel butPanel = new JPanel(new FlowLayout());

		backPanel.setBackground(backGroundColor);
		butPanel.setBackground(backGroundColor);
		
		applyButton = new JButton("Apply");
		applyButton.addActionListener(this);
		butPanel.add(applyButton);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		butPanel.add(cancelButton);

		backPanel.add((JPanel)cap, BorderLayout.NORTH);
		backPanel.add(butPanel, BorderLayout.SOUTH);

		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(backPanel);
	       
		pack();
		setLocationRelativeTo(frame);
		setVisible(true);
		setSize(new Dimension(width, height));
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
	public boolean getState() { 
		return butState;
	}

	private void setRatio() {
		try {
			ratioField1.commitEdit();
			ratioField2.commitEdit();			
			double num1 = ((Number)ratioField1.getValue()).doubleValue();
			double num2 = ((Number)ratioField2.getValue()).doubleValue();
			double ratioValue = num1/num2;
			if ( orientationList.getSelectedIndex() == 0 ) { //landscape
				if ( num1 >= num2 ) {
					ratioValue = 1.0f/ratioValue;
				}
			}else {
				if ( num1 <= num2 ) {
					ratioValue = 1.0f/ratioValue;
				}
			}
			boolean result = panel.setHeightWidthRatio(ratioValue);

			if ( result ) {
				ratioField1.setBackground(Color.WHITE);
				ratioField2.setBackground(Color.WHITE);				
			}else {
				ratioField1.setBackground(Color.RED);
				ratioField2.setBackground(Color.RED);				
			}
		}catch(Exception err) {
			if (ratioList.getSelectedItem() != Unrestricted) {
				ratioField1.setBackground(Color.RED);
				ratioField2.setBackground(Color.RED);	
			}
		}
		repaint();
	}

	public void insertUpdate(DocumentEvent e) {
		setRatio();
	}   

	public void removeUpdate(DocumentEvent e) {
		setRatio();
	} 

	public void changedUpdate(DocumentEvent e) {
		setRatio();
	}
	
	public void propertyChange(PropertyChangeEvent e) {
	    Object source = e.getSource();
	    if ( (source == ratioField1) || (source == ratioField2) ) {
			setRatio();
		}
	}


	public void actionPerformed(ActionEvent e) {
		if(applyButton == e.getSource()) {
			butState = true;
			setVisible(false);
         	dispose();
		}else if(cancelButton == e.getSource()) {
			butState = false;
			setVisible(false);
         	dispose();
		}else if (ratioList == e.getSource()) {
			if (ratioList.getSelectedItem() != CustomRatio) {
				ratioField1.setEnabled(false);
				ratioField2.setEnabled(false);	
			}
			if (ratioList.getSelectedItem() != Unrestricted) {
				orientationList.setEnabled(true);
				panel.enableRatio(true);				
			}
			if (ratioList.getSelectedItem() == Unrestricted) {
				ratioField1.setText("");
				ratioField2.setText("");
				panel.enableRatio(false);
				orientationList.setEnabled(false);
			}else if (ratioList.getSelectedItem() == ImageRatio) {
				ratioField1.setText(String.valueOf(panel.getOrigImage().getHeight()));
				ratioField2.setText(String.valueOf(panel.getOrigImage().getWidth()));
				if ( panel.getOrigImage().getHeight() >= panel.getOrigImage().getWidth() ) {
					orientationList.setSelectedIndex(1); //portrait
				}
			}else if (ratioList.getSelectedItem() == CustomRatio) {
				ratioField1.setEnabled(true);
				ratioField2.setEnabled(true);	
				ratioField1.setText("1.0");
				ratioField2.setText("1.0");
			}else if (ratioList.getSelectedItem() == RATIO_4_X_6) {
				ratioField1.setText("4.0");
				ratioField2.setText("6.0");
			}else if (ratioList.getSelectedItem() == RATIO_5_X_7) {
				ratioField1.setText("5.0");
				ratioField2.setText("7.0");
			}else if (ratioList.getSelectedItem() == RATIO_8_X_10) {
				ratioField1.setText("8.0");
				ratioField2.setText("10.0");
			}else if (ratioList.getSelectedItem() == RATIO_8_X_12) {
				ratioField1.setText("8.0");
				ratioField2.setText("12.0");
			}else if (ratioList.getSelectedItem() == RATIO_10_X_13) {
				ratioField1.setText("10.0");
				ratioField2.setText("13.0");
			}else if (ratioList.getSelectedItem() == RATIO_10_X_20) {
				ratioField1.setText("10.0");
				ratioField2.setText("20.0");
			}else if (ratioList.getSelectedItem() == RATIO_11_X_14) {
				ratioField1.setText("11.0");
				ratioField2.setText("14.0");
			}else if (ratioList.getSelectedItem() == RATIO_12_X_18) {
				ratioField1.setText("12.0");
				ratioField2.setText("18.0");
			}else if (ratioList.getSelectedItem() == RATIO_16_X_20) {
				ratioField1.setText("16.0");
				ratioField2.setText("20.0");
			}else if (ratioList.getSelectedItem() == RATIO_20_X_24) {
				ratioField1.setText("20.0");
				ratioField2.setText("24.0");
			}else if (ratioList.getSelectedItem() == RATIO_20_X_30) {
				ratioField1.setText("20.0");
				ratioField2.setText("30.0");
			}	
			setRatio();
		}else if (orientationList == e.getSource()) {
			setRatio();
		}
	}

}
