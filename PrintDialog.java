import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import java.text.NumberFormat;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.print.*;
import java.awt.image.BufferedImage;


public class PrintDialog extends JDialog implements ActionListener, DocumentListener, PropertyChangeListener, Printable {
	private static final long serialVersionUID = -5949655941175497655L;
	private final Color backGroundColor = new Color(20, 20, 20);
 	private PrintPanel panel = null;
    private JButton closeButton = null;
    private JButton printButton = null;    
    private boolean butState = false;
    private JCheckBox printScaleBox = null;
    private int height = 0;
    private int width = 0;
    
    private JComboBox<String> ratioList = null;
    private JFormattedTextField ratioField1 = null;
    private JFormattedTextField ratioField2 = null;   
    private JComboBox<String> orientationList = null;

    private NumberFormat ratioFormat;

    public final String RATIO_4_X_6 = "4\" x 6\"";
    public final String RATIO_5_X_7 = "5\" x 7\"";
    public final String RATIO_8_X_10 = "8\" x 10\"";
    public final String RATIO_8_X_12 = "8\" x 12\"";   
    public final String RATIO_10_X_13 = "10\" x 13\"";
    public final String RATIO_10_X_20 = "10\" x 20\"";
    public final String RATIO_11_X_14 = "11\" x 14\"";
    public final String RATIO_12_X_18 = "12\" x 18\"";   
    public final String RATIO_16_X_20 = "16\" x 20\"";
    public final String RATIO_20_X_24 = "20\" x 24\"";
    public final String RATIO_20_X_30 = "20\" x 30\""; 
    public final String CustomRatio = "Custom Ratio"; 
  
    private final String[] ratioSizes = {
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
    
    public final String LANDSCAPE = "Landscape";
    public final String PORTRAIT = "Portrait";
  
    private final String[] orientations = {
    		LANDSCAPE,
    		PORTRAIT,
    };
    
	PageFormat p = new PageFormat();
	
  
	public PrintDialog(JFrame frame, String title, boolean modal, PrintPanel cap) {
		super(frame, title, modal);
		panel = cap;
	    JToolBar toolBar = new JToolBar();
	    toolBar.setFloatable(false);
	    
	    ratioFormat = NumberFormat.getNumberInstance();
	    ratioFormat.setMinimumFractionDigits(0);
	    ratioFormat.setMaximumFractionDigits(2);
	    ratioFormat.setMinimumIntegerDigits(0);
	    ratioFormat.setMaximumIntegerDigits(5);  
	    
	    toolBar.addSeparator(new Dimension(20,16));

	    JLabel label0 = new JLabel("Print Size: ");
	    toolBar.add(label0);	    
	    
	    ratioList = new JComboBox<String>(ratioSizes);
	    ratioList.setSelectedIndex(0);
	    ratioList.addActionListener(this);
	    toolBar.add(ratioList);

	    toolBar.addSeparator(new Dimension(20,16));
	    
	    printScaleBox = new JCheckBox("Scale to fit Page");
	    printScaleBox.setSelected(true);
	    toolBar.add(printScaleBox);
	    
	    toolBar.addSeparator(new Dimension(20,16));

	 
	    ratioField1 = new JFormattedTextField(ratioFormat);
	    ratioField1.setColumns(8);
	    ratioField1.setText("1");
	    ratioField1.getDocument().addDocumentListener(this);
	    toolBar.add(ratioField1);
	    
	    JLabel label1 = new JLabel(" in");
	    toolBar.add(label1);	    
	    
	    toolBar.addSeparator(new Dimension(10,16));
	    
	    JLabel label2 = new JLabel(" X ");
	    toolBar.add(label2);
	    
	    toolBar.addSeparator(new Dimension(10,16));

	    ratioField2 = new JFormattedTextField(ratioFormat);
	    ratioField2.setColumns(8);
	    ratioField2.setText("1");	    
	    ratioField2.getDocument().addDocumentListener(this);
	    toolBar.add(ratioField2);
	    
	    JLabel label3 = new JLabel(" in");
	    toolBar.add(label3);
   
	    
	    toolBar.addSeparator(new Dimension(20,16));
	    
	    orientationList = new JComboBox<String>(orientations);
	    orientationList.setSelectedIndex(0);
	    orientationList.addActionListener(this);
	    toolBar.add(orientationList);
	    
	    toolBar.addSeparator(new Dimension(50,16));
	    
		printButton = new JButton("   PRINT   ");
		printButton.addActionListener(this);
		toolBar.add(printButton);
		
	    toolBar.addSeparator(new Dimension(20,16));

	 	height = cap.getHeight() + 95;
		width = cap.getWidth();
		
		this.setAlwaysOnTop(false);
		setResizable(false);
		
		JPanel backPanel = new JPanel(new BorderLayout());
		JPanel butPanel = new JPanel(new FlowLayout());

		backPanel.setBackground(backGroundColor);
		butPanel.setBackground(backGroundColor);

		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		butPanel.add(closeButton);

		backPanel.add((JPanel)cap, BorderLayout.NORTH);
		backPanel.add(butPanel, BorderLayout.SOUTH);

		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(backPanel);
	       
		pack();
		setLocationRelativeTo(frame);
		setVisible(true);
		setSize(new Dimension(width, height));
		panel.setHeightWidthRatio(1.0d);
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
			if ( orientationList.getSelectedItem() == LANDSCAPE ) {
				if ( num1 >= num2 ) {
					ratioValue = 1.0f/ratioValue;
				}
			}else if ( orientationList.getSelectedItem() == PORTRAIT ) {
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
			ratioField1.setBackground(Color.RED);
			ratioField2.setBackground(Color.RED);	
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
	
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		if ( page > 0 ) return NO_SUCH_PAGE;
		Graphics2D g2d = (Graphics2D)g;
		Rectangle rec = panel.getSetting();
		BufferedImage bufImgPrint = ImageAnalysis.getImageClip(panel.getOrigImage(), rec.x, rec.y, rec.width, rec.height);
		
 		try {
			ratioField1.commitEdit();
			ratioField2.commitEdit();	
		}catch (Exception err) {
			err.printStackTrace();
		}

		double num1 = ((Number)ratioField1.getValue()).doubleValue();
		double num2 = ((Number)ratioField2.getValue()).doubleValue();
		double imgWidth72 = 0;
		double imgHeight72 = 0;
		if ( bufImgPrint.getWidth() <= bufImgPrint.getHeight() ) {
			if ( num1 <= num2 ) {
				imgWidth72 = num1*72;
				imgHeight72 = num2*72;
			}else {
				imgWidth72 = num2*72;
				imgHeight72 = num1*72;
			}
		}else {
			if ( num1 <= num2 ) {
				imgWidth72 = num2*72;
				imgHeight72 = num1*72;
			}else {
				imgWidth72 = num1*72;
				imgHeight72 = num2*72;
			}
		}
		
		double widthRatio = imgWidth72/p.getImageableWidth();
		double heightRatio = imgHeight72/p.getImageableHeight();
		
		if ( ( widthRatio <= 1 ) && ( heightRatio <= 1 ) ) {
			  g2d.drawImage(bufImgPrint, (int)Math.round(p.getImageableX()), (int)Math.round(p.getImageableY()), (int)Math.round(p.getImageableX()+imgWidth72), (int)Math.round(p.getImageableY()+imgHeight72), 0, 0, bufImgPrint.getWidth(), bufImgPrint.getHeight(), null);
		}else if ( printScaleBox.isSelected() ) {
			if ( widthRatio >= heightRatio ) {
				double newHeight = p.getImageableWidth()*bufImgPrint.getHeight()/bufImgPrint.getWidth();
				g2d.drawImage(bufImgPrint, (int)Math.round(p.getImageableX()), (int)Math.round(p.getImageableY()), (int)Math.round(p.getImageableX()+p.getImageableWidth()), (int)Math.round(p.getImageableY()+newHeight), 0, 0, bufImgPrint.getWidth(), bufImgPrint.getHeight(), null);
			}else {
				double newWidth = p.getImageableHeight()*bufImgPrint.getWidth()/bufImgPrint.getHeight();
				g2d.drawImage(bufImgPrint, (int)Math.round(p.getImageableX()), (int)Math.round(p.getImageableY()), (int)Math.round(p.getImageableX()+newWidth), (int)Math.round(p.getImageableY()+p.getImageableHeight()), 0, 0, bufImgPrint.getWidth(), bufImgPrint.getHeight(), null);
			}
		}else {
			g2d.drawImage(bufImgPrint, (int)Math.round(p.getImageableX()), (int)Math.round(p.getImageableY()), (int)Math.round(p.getImageableX()+imgWidth72), (int)Math.round(p.getImageableY()+imgHeight72), 0, 0, bufImgPrint.getWidth(), bufImgPrint.getHeight(), null);
		}
		g2d.dispose();
		return PAGE_EXISTS;
	}
	
	private void printImage() {
         PrinterJob job = PrinterJob.getPrinterJob();
         job.setPrintable(this);
         if ( job.printDialog() ) {
             try {
            	  p = job.pageDialog(job.getPageFormat(null));
                  job.print();           		  
             } catch (PrinterException ex) { 
            	 System.out.println(ex);
             }
         }
	}


	public void actionPerformed(ActionEvent e) {
		if(closeButton == e.getSource()) {
			butState = true;
			setVisible(false);
         	dispose();
		}else if (e.getSource() == printButton ) {
			printImage();
		}else if (ratioList == e.getSource()) {
			if (ratioList.getSelectedItem() != CustomRatio) {
				ratioField1.setEnabled(false);
				ratioField2.setEnabled(false);	
			}
			if (ratioList.getSelectedItem() == CustomRatio) {
				ratioField1.setEnabled(true);
				ratioField2.setEnabled(true);	
				ratioField1.setText("1");
				ratioField2.setText("1");
			}else if (ratioList.getSelectedItem() == RATIO_4_X_6) {
				ratioField1.setText("4");
				ratioField2.setText("6");
			}else if (ratioList.getSelectedItem() == RATIO_5_X_7) {
				ratioField1.setText("5");
				ratioField2.setText("7");
			}else if (ratioList.getSelectedItem() == RATIO_8_X_10) {
				ratioField1.setText("8");
				ratioField2.setText("10");
			}else if (ratioList.getSelectedItem() == RATIO_8_X_12) {
				ratioField1.setText("8");
				ratioField2.setText("12");
			}else if (ratioList.getSelectedItem() == RATIO_10_X_13) {
				ratioField1.setText("10");
				ratioField2.setText("13");
			}else if (ratioList.getSelectedItem() == RATIO_10_X_20) {
				ratioField1.setText("10");
				ratioField2.setText("20");
			}else if (ratioList.getSelectedItem() == RATIO_11_X_14) {
				ratioField1.setText("11");
				ratioField2.setText("14");
			}else if (ratioList.getSelectedItem() == RATIO_12_X_18) {
				ratioField1.setText("12");
				ratioField2.setText("18");
			}else if (ratioList.getSelectedItem() == RATIO_16_X_20) {
				ratioField1.setText("16");
				ratioField2.setText("20");
			}else if (ratioList.getSelectedItem() == RATIO_20_X_24) {
				ratioField1.setText("20");
				ratioField2.setText("24");
			}else if (ratioList.getSelectedItem() == RATIO_20_X_30) {
				ratioField1.setText("20");
				ratioField2.setText("30");
			}	
			setRatio();
		}else if (orientationList == e.getSource()) {
			setRatio();
		}
	}

}
