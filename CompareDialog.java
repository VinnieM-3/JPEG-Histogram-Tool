import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CompareDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -6683105872991508837L;
	private final Color backGroundColor = new Color(20, 20, 20);
    private JButton okButton = null;
    private int height = 0;
    private int width = 0;    
    
	public CompareDialog(JFrame frame, String title, boolean modal, JPanel cap) {
		super(frame, title, modal);
		height = cap.getHeight() + 75;
		width = cap.getWidth();
		
		this.setAlwaysOnTop(true);
		setResizable(false);
		
		JPanel backPanel = new JPanel(new BorderLayout());
		JPanel butPanel = new JPanel(new FlowLayout());

		backPanel.setBackground(backGroundColor);
		butPanel.setBackground(backGroundColor);
		
		okButton = new JButton("Close");
		okButton.addActionListener(this);
		butPanel.add(okButton);

		backPanel.add((JPanel)cap, BorderLayout.NORTH);
		backPanel.add(butPanel, BorderLayout.SOUTH);

		getContentPane().add(backPanel);
		pack();
		setLocationRelativeTo(frame);
		setVisible(true);
		setSize(new Dimension(width, height));
	}
	
	
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
    public void actionPerformed(ActionEvent e) {
        if(okButton == e.getSource()) {
         	setVisible(false);
         	dispose();
        }
    }


}
