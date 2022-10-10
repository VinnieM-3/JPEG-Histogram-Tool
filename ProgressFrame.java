import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JProgressBar;
import java.awt.Point;

public class ProgressFrame {
	private final Dimension size = new Dimension(200,25);
	private int curProgress = -1;
	private JFrame frame = null;
	private JPanel newContentPane = null;
	private JProgressBar progressBar = null;
	
	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);

	public ProgressFrame() {}
	
	public void setProgress(int x) {
		if ( ( frame != null ) && ( x != curProgress ) ) {
			curProgress = x;
			progressBar.setValue(curProgress);
			newContentPane.paintImmediately(0, 0, newContentPane.getWidth(), newContentPane.getHeight());
		}
	}
	
	public void showProgress(Point pnt) {
		frame = new JFrame();
		frame.setAlwaysOnTop(true);
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		progressBar = new JProgressBar();
		progressBar.setPreferredSize(size);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		curProgress = -1;
		progressBar.setValue(0);
		progressBar.setString("Updating Image...");
		progressBar.setStringPainted(true);
	
		newContentPane = new JPanel();
		newContentPane.setBackground(new Color(20,20,20));
		newContentPane.setOpaque(true);
		newContentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		newContentPane.add(progressBar, BorderLayout.CENTER);
		
		frame.setContentPane(newContentPane);
		frame.pack();
		pnt.x = pnt.x - Math.round((float)size.getWidth()/2f);
		pnt.y = pnt.y - Math.round((float)size.getHeight()/2f);
		frame.setLocation(pnt);
		frame.setVisible(true);
		
		frame.setCursor(hourglassCursor);
	}
	
	public void dispose() {
		if ( frame != null ) {
			frame.dispose();
		}
	}

}
