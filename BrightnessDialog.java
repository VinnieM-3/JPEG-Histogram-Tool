import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class BrightnessDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -4504903782717388582L;
	private final Color backGroundColor = new Color(20, 20, 20);
 	private BrightnessPanel panel = null;
    private JButton applyButton = null;
    private JButton cancelButton = null;
    private boolean butState = false;
    private int height = 0;
    private int width = 0;
    
    private JComboBox<String> modeList = null;
 
    private final String MODE_1 = "MODE 1";
    private final String MODE_2 = "MODE 2";
    
    private final String[] modeStrings = {
    		MODE_1,
    		MODE_2
     };
    
	public BrightnessDialog(JFrame frame, String title, boolean modal, BrightnessPanel cap) {
		super(frame, title, modal);
		panel = cap;
	    JToolBar toolBar = new JToolBar();
	    toolBar.setFloatable(false);
	    
	    toolBar.addSeparator(new Dimension(275,16));

	    JLabel label0 = new JLabel("Brightness Algothim:  ");
	    toolBar.add(label0);	    
	    
	    modeList = new JComboBox<String>(modeStrings);
	    modeList.setSelectedIndex(0);
	    modeList.addActionListener(this);
	    toolBar.add(modeList);

	    toolBar.addSeparator(new Dimension(275,16));
	 
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

	public void actionPerformed(ActionEvent e) {
		if(applyButton == e.getSource()) {
			butState = true;
			setVisible(false);
         	dispose();
		}else if(cancelButton == e.getSource()) {
			butState = false;
			setVisible(false);
         	dispose();
		}else if (modeList == e.getSource()) {
			if (modeList.getSelectedItem() == MODE_1) {
				panel.setMode(1);
			}else if (modeList.getSelectedItem() == MODE_2) {
				panel.setMode(2);
			}
		}
	}

}
