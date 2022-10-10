import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.LayoutManager;

public abstract class View extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -4773404593362280177L;
	public final static int TOP = -1;
	public final static int BOTTOM = -2;
	
	public final static int BEGINNING = -3;
	public final static int END = -4;
	
	public final static int ONE_UP = -5;
	public final static int ONE_DOWN = -6;
	
	public final static int ONE_RIGHT = -7;
	public final static int ONE_LEFT = -8;
	
	public final static int JUMP_RIGHT = -9;
	public final static int JUMP_LEFT = -10;
	
	final static int MaxCacheImageViews = 100;
	
	final static Color ImgBackGndColor = new Color(50,50,50);
	final static Font fontBold10 = new Font(Font.SANS_SERIF, Font.BOLD, 10);
	final static Font fontBold11 = new Font(Font.SANS_SERIF, Font.BOLD, 11);	
	final static Font fontBold12 = new Font(Font.SANS_SERIF, Font.BOLD, 12);	
	final static Font fontBold20 = new Font(Font.SANS_SERIF, Font.BOLD, 20);
	final static Color TextColor150 = new Color(150,150,150);
	final static Color TextColor175 = new Color(175,175,175);
	final static Color TextColor240 = new Color(240,240,240);
	final static Stroke stroke1 = new BasicStroke(1.0f);
	final static Stroke stroke2 = new BasicStroke(2.0f);	
	final static Stroke stroke3 = new BasicStroke(3.0f);
	
	ActiveRegion curMouseRegion = null;
	ArrayList<ActiveRegion> activeRegions = null;
	
	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
	Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
	
	Cursor defaultCursor = null;

	Settings settings;
	
	public void setWaitCursor() {
		this.setCursor(hourglassCursor);
	}
	
	public void setDefaultCursor() {
		this.setCursor(defaultCursor);
	}

	public View(LayoutManager layout) {
		super(layout);
		settings = Settings.getInstance();
		activeRegions = new ArrayList<ActiveRegion>();
	    defaultCursor = getCursor();
  		addMouseListener(this);
 	    addMouseMotionListener(this);
	}
	
	public void buttonClickEvent(JScrollPane s, int target) {}

	public View() {
		super();
		settings = Settings.getInstance();
		activeRegions = new ArrayList<ActiveRegion>();
	    defaultCursor = getCursor();
	    addMouseListener(this);
 	    addMouseMotionListener(this);
	}

	protected void paintComponent(Graphics g) {
    	super.paintComponent(g); 
	}

	abstract public Dimension getPreferredSize();

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}	
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
	 
	public void mouseMoved(MouseEvent e) {
		boolean inActiveRegion = false;
		int x = e.getX();
		int y = e.getY();
		for (ActiveRegion ar : activeRegions) {
			if ( ar.isInBounds(x, y) ) {
				if ( curMouseRegion != ar ) {
					curMouseRegion = ar;
					this.setCursor(handCursor);
					repaint();
				}
				inActiveRegion = true;
				break;
			}
		}
		if ( ( curMouseRegion != null ) && (inActiveRegion == false) ) {
			curMouseRegion = null;
			this.setCursor(defaultCursor);
			repaint();
		}
	}
	
	public void FilterStateChange() {
	   	setSize(getPreferredSize());
		getParent().validate();
 		repaint();
	}
	
	public void FrameSizeChange() {
		Settings.overlay.reset();
		repaint();
	}
	
	abstract public void leaveView();
	
	abstract public void displayView(int fileCollectionID);
	
}
