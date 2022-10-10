import java.awt.*;

public class HomePage extends View {
	private static final long serialVersionUID = -5622673383276039209L;
	private Dimension defaultPanelDimension = new Dimension(1000,600);
	private Color backGroundColor = new Color(0, 0, 0);
	
	public HomePage(int cols) {
	    super(new BorderLayout());
	    settings = Settings.getInstance();
		setBackground(backGroundColor);
	   	setSize(defaultPanelDimension);
	}
	
	public void leaveView() {}
	
	public void displayView(int fileCollectionID) {
	   	this.setSize(getPreferredSize());
		this.getParent().validate();
		repaint();
	}
		
	public Dimension getPreferredSize() {
 		return defaultPanelDimension;
	}
	
 	protected void paintComponent(Graphics g) {
    	super.paintComponent(g); 
    	
 		g.setColor(TextColor150);
        g.setFont(fontBold20);
    	
        String licAppTitle = "JPEG Histogram Tool";
      	int halfPageWidth = Math.round((float)this.getWidth()/2.0f);
      	int halfPageHeight = Math.round((float)this.getHeight()/2.0f);
      	int strLen = g.getFontMetrics().stringWidth(licAppTitle);
      	int halfStrLen = Math.round((float)strLen/2.0f);
      	
        if ( halfStrLen > halfPageWidth ) halfStrLen = halfPageWidth;
        g.drawString(licAppTitle, (halfPageWidth - halfStrLen), halfPageHeight);
	}	

}
