import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class GlassOverlay extends JComponent implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -2985575632059819691L;
	private final int MODE_OFF = 0;
	private final int MODE_DRAW_LARGE_IMAGE = 1;
	private final int MODE_DRAW_FULLSIZE_IMAGE = 2;	
	private int currentMode = MODE_OFF;
	
	private int curMouseX = 0;
	private int curMouseY = 0;
	private int prvMouseX = 0;
	private int prvMouseY = 0;		
	private int fullImgPosX = 0;
	private int fullImgPosY = 0;

	private int imgX1 = 0;
	private int imgY1 = 0;
	private int imgAdjWidth = 0;
	private int imgAdjHeight = 0;
	private int maxImgWidth = 0;
	private int maxImgHeight = 0;
	private int halfBoxWidth = 0;
	private int halfBoxHeight = 0;					
	
	private BufferedImage origFullImage = null;
	private BufferedImage imageScaled = null;
	private BufferedImage imgClip = null;
	private boolean gridFlag = false;
	private boolean highlightFlag = false;	
	private boolean zoomClick = false;
	private boolean zoomDrag = false;	
	
	public void drawLargeImage(int imageID, boolean highlightFlag, boolean gridFlag){
		this.gridFlag = gridFlag;
		this.highlightFlag = highlightFlag;
		Rectangle visRect = getVisibleRect();
		currentMode = MODE_DRAW_LARGE_IMAGE;
		origFullImage = Settings.getInstance().ImageAnalysisCol.get(imageID).getFullSizeImage();
		maxImgWidth = (int)visRect.getWidth();
		maxImgHeight = (int)visRect.getHeight();
		imageScaled = ImageAnalysis.getScaledImage(origFullImage, maxImgWidth, maxImgHeight);
		imgAdjWidth = imageScaled.getWidth();
		imgAdjHeight = imageScaled.getHeight();
		halfBoxWidth = (int)Math.floor((float)maxImgWidth/2.0f);
		halfBoxHeight = (int)Math.floor((float)maxImgHeight/2.0f);					
		if ( highlightFlag ) {
			imageScaled = ImageAnalysis.getH_S_Image(imageScaled);
		}
		setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	public void reset() {
		currentMode = MODE_OFF;
	}

	public GlassOverlay() {
		super();
  		addMouseListener(this);
	    addMouseMotionListener(this);
	}
	
	public void mouseClicked(MouseEvent e) {
		curMouseX = e.getX();
		curMouseY = e.getY();
		if ( (currentMode == MODE_DRAW_LARGE_IMAGE) && (origFullImage.getWidth() > imgAdjWidth) ){
			currentMode = MODE_DRAW_FULLSIZE_IMAGE;
			zoomClick = true;
		}else {
			currentMode = MODE_OFF;
		}
		repaint();		
	}
	
	public void mouseDragged(MouseEvent e) {
		prvMouseX = curMouseX;
		prvMouseY = curMouseY;		
		curMouseX = e.getX();
		curMouseY = e.getY();
		zoomDrag = true;
		repaint();
	}
	
	public void mouseMoved(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}	
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		prvMouseX = e.getX();
		prvMouseY = e.getY();		
		curMouseX = e.getX();
		curMouseY = e.getY();
	}
	
	public void mouseReleased(MouseEvent e) {}

	protected void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		
		g2.setColor(Color.GRAY);
		g2.fillRect(0, 0, maxImgWidth, maxImgHeight);

		if ( currentMode == MODE_DRAW_LARGE_IMAGE ) {

			//draw image
			imgX1 = 0;
			if ( imgAdjWidth < maxImgWidth ) {
				imgX1 = imgX1 + (int)Math.round((float)((maxImgWidth-imgAdjWidth))/2.0f);
			}
			imgY1 = 0;
			if ( imgAdjHeight < maxImgHeight ) {
				imgY1 = imgY1 + (int)Math.round((float)((maxImgHeight-imgAdjHeight))/2.0f);
			}
			g2.drawImage(imageScaled, imgX1, imgY1, null);
			
			//draw grid
  			if ( gridFlag ) {
		      	g.setColor(Color.WHITE);
		      	int img3rdWidth = (int)((float)(imgAdjWidth)/3.0);
		      	int img3rdHeight = (int)((float)(imgAdjHeight)/3.0);			      	
  				g.drawLine(imgX1, imgY1+img3rdHeight, imgX1 + imgAdjWidth, imgY1+img3rdHeight);
  				g.drawLine(imgX1, imgY1+(2*img3rdHeight), imgX1 + imgAdjWidth, imgY1+(2*img3rdHeight));
  				g.drawLine(imgX1+img3rdWidth, imgY1, imgX1+img3rdWidth, imgY1 + imgAdjHeight);
  				g.drawLine(imgX1+(2*img3rdWidth), imgY1, imgX1+(2*img3rdWidth), imgY1 + imgAdjHeight);	  				
  			}
		}else if ( currentMode == MODE_DRAW_FULLSIZE_IMAGE ) {
			if ( zoomDrag ) {
				fullImgPosX = fullImgPosX - (curMouseX-prvMouseX);
				int imgWidth = maxImgWidth;
				if (origFullImage.getWidth() < maxImgWidth ) {
					fullImgPosX = 0;
					imgWidth = origFullImage.getWidth();
				}else if ( fullImgPosX < 0 ) {
					fullImgPosX = 0;
				}else if ( fullImgPosX > (origFullImage.getWidth()-maxImgWidth) ) {
					fullImgPosX = origFullImage.getWidth()-maxImgWidth;
				}

				fullImgPosY = fullImgPosY - (curMouseY-prvMouseY);
				int imgHeight = maxImgHeight;
				if (origFullImage.getHeight() < maxImgHeight ) {
					fullImgPosY = 0;
					imgHeight = origFullImage.getHeight();
				}else if ( fullImgPosY < 0 ) {
					fullImgPosY = 0;
				}else if  ( fullImgPosY > (origFullImage.getHeight()-maxImgHeight) ) {
					fullImgPosY = origFullImage.getHeight()-maxImgHeight;
				}
				
				imgX1 = 0;
				if ( imgWidth < maxImgWidth ) {
					imgX1 = (int)Math.round((float)(maxImgWidth-imgWidth)/2.0f);
				}
				imgY1 = 0;
				if ( imgHeight < maxImgHeight ) {
					imgY1 = (int)Math.round((float)(maxImgHeight-imgHeight)/2.0f);
				}
				
				imgClip = ImageAnalysis.getImageClip(origFullImage, fullImgPosX, fullImgPosY, imgWidth, imgHeight);
				if ( highlightFlag ) {
					imgClip = ImageAnalysis.getH_S_Image(imgClip);
				}
				g2.drawImage(imgClip, imgX1, imgY1, null);
			}else if ( zoomClick ) {
				imgX1 = 0;
				if ( imgAdjWidth < maxImgWidth ) {
					imgX1 = (int)Math.round((float)(maxImgWidth-imgAdjWidth)/2.0f);
				}
				imgY1 = 0;
				if ( imgAdjHeight < maxImgHeight ) {
					imgY1 = (int)Math.round((float)(maxImgHeight-imgAdjHeight)/2.0f);
				}

				float scaledPosX = curMouseX - imgX1;
				fullImgPosX = Math.round(origFullImage.getWidth()*(scaledPosX/(float)imgAdjWidth));
				int rightSpace = origFullImage.getWidth() - fullImgPosX;
				int leftSpace = fullImgPosX;
				int imgWidth = maxImgWidth;
				if (origFullImage.getWidth() < maxImgWidth ) {
					fullImgPosX = 0;
					imgWidth = origFullImage.getWidth();
				}else if ( (rightSpace > halfBoxWidth) && (leftSpace > halfBoxWidth) ) {
					fullImgPosX = fullImgPosX - halfBoxWidth;
				}else if (rightSpace < halfBoxWidth) {
					fullImgPosX = origFullImage.getWidth() - maxImgWidth;
				}else if (leftSpace < halfBoxWidth) {
					fullImgPosX = 0;
				}
				
				float scaledPosY = curMouseY - imgY1;						
				fullImgPosY = Math.round(origFullImage.getHeight()*(scaledPosY/(float)imgAdjHeight));
				int bottomSpace = origFullImage.getHeight() - fullImgPosY;
				int topSpace = fullImgPosY;
				int imgHeight = maxImgHeight;
				if (origFullImage.getHeight() < maxImgHeight ) {
					fullImgPosY = 0;
					imgHeight = origFullImage.getHeight();
				}else if ( (bottomSpace > halfBoxHeight) && (topSpace > halfBoxHeight) ) {
					fullImgPosY = fullImgPosY - halfBoxHeight;
				}else if (bottomSpace < halfBoxHeight) {
					fullImgPosY = origFullImage.getHeight() - maxImgHeight;
				}else if (topSpace < halfBoxHeight) {
					fullImgPosY = 0;
				}
				
				imgX1 = 0;
				if ( imgWidth < maxImgWidth ) {
					imgX1 = (int)Math.round((float)(maxImgWidth-imgWidth)/2.0f);
				}
				imgY1 = 0;
				if ( imgHeight < maxImgHeight ) {
					imgY1 = (int)Math.round((float)(maxImgHeight-imgHeight)/2.0f);
				}
				
				imgClip = ImageAnalysis.getImageClip(origFullImage, fullImgPosX, fullImgPosY, imgWidth, imgHeight);
				if ( highlightFlag ) {
					imgClip = ImageAnalysis.getH_S_Image(imgClip);
				}
				g2.drawImage(imgClip, imgX1, imgY1, null);
			}else if ( imgClip != null ) { 
				g2.drawImage(imgClip, imgX1, imgY1, null);
			}
		}else if ( currentMode == MODE_OFF ) {
			imgClip = null;
			setVisible(false);
		}
		zoomDrag = false;
		zoomClick = false;
		g2.dispose();
	}
}
