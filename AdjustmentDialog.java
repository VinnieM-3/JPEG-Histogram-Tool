import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AdjustmentDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -1598469217155146648L;
	private final Color backGroundColor = new Color(20, 20, 20);
    private JButton applyButton = null;
    private JButton cancelButton = null;
    private boolean butState = false;
    private int height = 0;
    private int width = 0;    
    
	public AdjustmentDialog(JFrame frame, String title, boolean modal, JPanel cap) {
		super(frame, title, modal);
		height = cap.getHeight() + 75;
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

		getContentPane().add(backPanel);
		pack();
		setLocationRelativeTo(frame);
		setVisible(true);
		setSize(new Dimension(width, height));
	}
	
	public boolean getState() { 
		return butState;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
    public void actionPerformed(ActionEvent e) {
        if(applyButton == e.getSource()) {
        	butState = true;
        	setVisible(false);
         	dispose();
        }
        else if(cancelButton == e.getSource()) {
        	butState = false;
            setVisible(false);
         	dispose();
        }
    }

}
