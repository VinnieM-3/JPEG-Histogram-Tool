import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

public class PrintPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1484829185506215976L;

	private final Dimension defaultPanelDimension = new Dimension(757,485);
	
	private final Color backGroundColor = new Color(20, 20, 20);
	private final Color imgBackGndColor = new Color(50,50,50);
	
	private final Cursor defaultCursor = getCursor();
	private final Cursor handCursor =  new Cursor(Cursor.HAND_CURSOR);
	
	private final Stroke stroke1 = new BasicStroke(1.0f);
	private final Stroke stroke2 = new BasicStroke(2.0f);	

	private final int margin = 10;
	private final int imgBoxWidth = 757 - 6 - (2*margin);
	private final int imgBoxHeight = 485 - (2*margin);	
	
	private double heightWidthRatio = 1.0f;
	
	private Rectangle rectScaledImage = new Rectangle();
	private Rectangle rectScaledCrop = new Rectangle();
	private Rectangle rectActualCrop = new Rectangle();
	private Rectangle smRect = new Rectangle();				
	private Rectangle proposed = new Rectangle();

	
	private BufferedImage actualImage = null;
	private BufferedImage scaledImage = null;
	
	ArrayList<ActiveRegion> activeRegions = null;
	private ActiveRegion curMouseRegion = null;
	
	private boolean cropDragged = false;
	private boolean cropRatioEvent = false;	
	
	private int curMouseX = 0;
	private int curMouseY = 0;
	private int prvMouseX = 0;
	private int prvMouseY = 0;		
	
	private int activeElement = 0;
	private final int arCornerNW = 1;
	private final int arCornerNE = 2;
	private final int arCornerSE = 3;
	private final int arCornerSW = 4;
	private final int arLineN = 5;
	private final int arLineS = 6;
	private final int arLineE = 7;
	private final int arLineW = 8;
	private final int arCenter = 9;
	
	private int newX = 0;
	private int newY = 0;
	private int newWidth = 0;
	private int newHeight = 0;
	
	public PrintPanel(BufferedImage srcImg) {
		activeRegions = new ArrayList<ActiveRegion>();
		
		actualImage = srcImg;
		
		heightWidthRatio = 1.0d;
		
		//calculate scaled image
	    scaledImage = ImageAnalysis.getScaledImage(srcImg, imgBoxWidth, imgBoxHeight);
	    int scaledImageWidth = scaledImage.getWidth();
		int imgX1 = margin;
		if ( scaledImageWidth < imgBoxWidth ) {
			imgX1 = imgX1 + (int)Math.round((float)((imgBoxWidth-scaledImageWidth))/2.0f);
		}
	    int scaledImageHeight = scaledImage.getHeight();
		int imgY1 = margin;
		if ( scaledImageHeight < imgBoxHeight ) {
			imgY1 = imgY1 + (int)Math.round((float)((imgBoxHeight-scaledImageHeight))/2.0f);
		}
		rectScaledImage.setRect(imgX1, imgY1, scaledImageWidth, scaledImageHeight);

	    setBackground(backGroundColor);
		setSize(defaultPanelDimension);
 		addMouseListener(this);
 	    addMouseMotionListener(this);
 	    
 	   setHeightWidthRatio(heightWidthRatio);
 	}
	
	
	public Dimension getPreferredSize() {
		return defaultPanelDimension;
	}

	
	public Rectangle getSetting() {
	    double rectActualCropX1 = (rectScaledCrop.x - rectScaledImage.x)*((double)actualImage.getWidth()/(double)scaledImage.getWidth());
	    double rectActualCropY1 = (rectScaledCrop.y - rectScaledImage.y)*((double)actualImage.getHeight()/(double)scaledImage.getHeight());
	    double rectActualCropWidth = rectScaledCrop.width*((double)actualImage.getWidth()/(double)scaledImage.getWidth());
	    double rectActualCropHeight = rectScaledCrop.height*((double)actualImage.getHeight()/(double)scaledImage.getHeight());
 	    rectActualCrop.setRect(rectActualCropX1, rectActualCropY1, rectActualCropWidth, rectActualCropHeight);
		return rectActualCrop;
	}
	
	public boolean setHeightWidthRatio(double ratio) {
		heightWidthRatio = ratio;
		cropRatioEvent = true;
		boolean ratioSetSuccessful = false;
		if (heightWidthRatio > 0 ) {
			newHeight = Math.round(0.9f*rectScaledImage.height);
			newWidth = (int)Math.round((double)newHeight*(1.0d/heightWidthRatio));
			if (newWidth > rectScaledImage.width) {
				newWidth = Math.round(0.9f*rectScaledImage.width);
				newHeight = (int)Math.round((double)newWidth*heightWidthRatio);
			}
			newX = Math.round((float)rectScaledImage.width/2.0f - (float)newWidth/2.0f) + rectScaledImage.x;
			newY = Math.round((float)rectScaledImage.height/2.0f - (float)newHeight/2.0f) + rectScaledImage.y;					
			smRect.setRect(newX, newY, 20, 20);				
			proposed.setRect(newX, newY, newWidth, newHeight);
			if ( rectScaledImage.contains(proposed) && proposed.contains(smRect) ) {
				ratioSetSuccessful = true;
				repaint();
			}
		}else {
			ratioSetSuccessful = true;
		}
		return ratioSetSuccessful;
	}
	
	public BufferedImage getOrigImage() {
		return actualImage;
	}
	
	public double getHeightWidthRatio() {
		return heightWidthRatio;
	}
	
	public void mouseEntered(MouseEvent e) {}	
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	
	public void mouseReleased(MouseEvent e) {
		activeElement=0;
	}
	
	public void mousePressed(MouseEvent e) {
		boolean inActiveRegion = false;
		curMouseX = e.getX();
		curMouseY = e.getY();
		for (ActiveRegion ar : activeRegions) {
			if ( ar.isInBounds(curMouseX, curMouseY) ) {
				inActiveRegion = true;
				if ( curMouseRegion != ar ) {
					curMouseRegion = ar;
				}
				inActiveRegion = true;
				if ( curMouseRegion != ar ) {
					curMouseRegion = ar;
				}
				if       ( ar.Name.equals("CornerNW") ) {
					activeElement = arCornerNW;
				}else if ( ar.Name.equals("CornerNE") ) {
					activeElement = arCornerNE;
				}else if ( ar.Name.equals("CornerSE") ) {
					activeElement = arCornerSE;
				}else if ( ar.Name.equals("CornerSW") ) {
					activeElement = arCornerSW;
				}else if ( ar.Name.equals("LineN") ) {
					activeElement = arLineN;
				}else if ( ar.Name.equals("LineS") ) {
					activeElement = arLineS;
				}else if ( ar.Name.equals("LineE") ) {
					activeElement = arLineE;
				}else if ( ar.Name.equals("LineW") ) {
					activeElement = arLineW;
				}else if ( ar.Name.equals("Center") ) {
					activeElement = arCenter;
				}
				setCursor(handCursor);
				repaint();
				break;
			}
		}
		if ( ( curMouseRegion != null ) && (inActiveRegion == false) ) {
			curMouseRegion = null;
			setCursor(defaultCursor);
			repaint();
		}
	}

	public void mouseMoved(MouseEvent e) {
		boolean inActiveRegion = false;
		curMouseX = e.getX();
		curMouseY = e.getY();
		for (ActiveRegion ar : activeRegions) {
			if ( ar.isInBounds(curMouseX, curMouseY) ) {
				inActiveRegion = true;
				if ( curMouseRegion != ar ) {
					curMouseRegion = ar;
				}
				inActiveRegion = true;
				if ( curMouseRegion != ar ) {
					curMouseRegion = ar;
				}
				setCursor(handCursor);
				repaint();
				break;
			}
		}
		if ( ( curMouseRegion != null ) && (inActiveRegion == false) ) {
			curMouseRegion = null;
			setCursor(defaultCursor);
			repaint();
		}
	}
	
	public void mouseDragged(MouseEvent e) { 
		prvMouseX = curMouseX;
		prvMouseY = curMouseY;		
		curMouseX = e.getX();
		curMouseY = e.getY();
		cropDragged = true;
		repaint();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		activeRegions.clear();
		
		Graphics2D g2 = (Graphics2D)g;
		
		//Border around scaled image
      	g2.setColor(imgBackGndColor);
      	g2.fill3DRect(margin, margin, imgBoxWidth, imgBoxHeight, true);
  
      	//draw scaled image
		g2.drawImage(scaledImage, rectScaledImage.x, rectScaledImage.y, rectScaledImage.width, rectScaledImage.height, null);

       	//adjust crop rectangle
		newX = rectScaledCrop.x;
		newY = rectScaledCrop.y;
		newWidth = rectScaledCrop.width;
		newHeight = rectScaledCrop.height;
		if ( ( activeElement > 0 ) && ( cropDragged == true ) ) {
			if ( activeElement == arCenter ) {
				newX = rectScaledCrop.x + (curMouseX-prvMouseX);
				newY = rectScaledCrop.y + (curMouseY-prvMouseY);
			}else if ( activeElement == arLineN ) {
				newY = rectScaledCrop.y + (curMouseY-prvMouseY);
				newHeight = rectScaledCrop.y+rectScaledCrop.height-newY;
				if ( heightWidthRatio > 0 ) {
					newWidth = (int)Math.round((double)newHeight*(1.0d/heightWidthRatio));
				}
			}else if ( activeElement == arLineS ) {
				newHeight = rectScaledCrop.height + (curMouseY-prvMouseY);
				if ( heightWidthRatio > 0  ) {
					newWidth = (int)Math.round((double)newHeight*(1.0d/heightWidthRatio));
				}
			}else if ( activeElement == arLineW ) {
				newX = rectScaledCrop.x + (curMouseX-prvMouseX);
				newWidth = rectScaledCrop.width - (newX-rectScaledCrop.x);
				if ( heightWidthRatio > 0  ) {
					newHeight = (int)Math.round((double)newWidth*heightWidthRatio);
				}
			}else if ( activeElement == arLineE ) {
				newWidth = rectScaledCrop.width + (curMouseX-prvMouseX);
				if ( heightWidthRatio > 0  ) {
					newHeight = (int)Math.round((double)newWidth*heightWidthRatio);
				}
			}else if ( activeElement == arCornerNW ) {
				if ( heightWidthRatio > 0  ) {
					if ( Math.abs(curMouseX-prvMouseX) >= Math.abs(curMouseY-prvMouseY) ) {
						newWidth = rectScaledCrop.width - (curMouseX-prvMouseX);
						newHeight = (int)Math.round((double)newWidth*heightWidthRatio);
					}else {
						newHeight = rectScaledCrop.height - (curMouseY-prvMouseY);
						newWidth = (int)Math.round((double)newHeight*(1.0d/heightWidthRatio));
					}
					newX = rectScaledCrop.x - (newWidth-rectScaledCrop.width);
					newY = rectScaledCrop.y - (newHeight-rectScaledCrop.height);
				}else {
					newX = rectScaledCrop.x + (curMouseX-prvMouseX);
					newY = rectScaledCrop.y + (curMouseY-prvMouseY);
					newWidth = rectScaledCrop.width - (newX-rectScaledCrop.x);
					newHeight = rectScaledCrop.height - (newY-rectScaledCrop.y);
				}
			}else if ( activeElement == arCornerNE ) {
				if ( heightWidthRatio > 0  ) {
					if ( Math.abs(curMouseX-prvMouseX) >= Math.abs(curMouseY-prvMouseY) ) {
						newWidth = rectScaledCrop.width + (curMouseX-prvMouseX);
						newHeight = (int)Math.round((double)newWidth*heightWidthRatio);
					}else {
						newHeight = rectScaledCrop.height - (curMouseY-prvMouseY);
						newWidth = (int)Math.round((double)newHeight*(1.0d/heightWidthRatio));
					}
					newY = rectScaledCrop.y - (newHeight-rectScaledCrop.height);
				}else {
					newY = rectScaledCrop.y + (curMouseY-prvMouseY);
					newWidth = rectScaledCrop.width + (curMouseX-prvMouseX);
					newHeight = rectScaledCrop.height - (newY-rectScaledCrop.y);
				}
			}else if ( activeElement == arCornerSW ) {
				if ( heightWidthRatio > 0  ) {
					if ( Math.abs(curMouseX-prvMouseX) >= Math.abs(curMouseY-prvMouseY) ) {
						newWidth = rectScaledCrop.width - (curMouseX-prvMouseX);
						newHeight = (int)Math.round((double)newWidth*heightWidthRatio);
					}else {
						newHeight = rectScaledCrop.height + (curMouseY-prvMouseY);
						newWidth = (int)Math.round((double)newHeight*(1.0d/heightWidthRatio));
					}
					newX = rectScaledCrop.x - (newWidth-rectScaledCrop.width);
				}else {
					newX = rectScaledCrop.x + (curMouseX-prvMouseX);
					newWidth = rectScaledCrop.width - (curMouseX-prvMouseX);
					newHeight = rectScaledCrop.height + (curMouseY-prvMouseY);
				}
			}else if ( activeElement == arCornerSE ) {
				if ( heightWidthRatio > 0  ) {
					if ( Math.abs(curMouseX-prvMouseX) >= Math.abs(curMouseY-prvMouseY) ) {
						newWidth = rectScaledCrop.width + (curMouseX-prvMouseX);
						newHeight = (int)Math.round((double)newWidth*heightWidthRatio);
					}else {
						newHeight = rectScaledCrop.height + (curMouseY-prvMouseY);
						newWidth = (int)Math.round((double)newHeight*(1.0d/heightWidthRatio));
					}
				}else {
					newWidth = rectScaledCrop.width + (curMouseX-prvMouseX);
					newHeight = rectScaledCrop.height + (curMouseY-prvMouseY);
				}
			}
		}else if ( cropRatioEvent ) {
			if ( heightWidthRatio > 0  ) {
				newHeight = Math.round(0.9f*rectScaledImage.height);
				newWidth = (int)Math.round((double)newHeight*(1.0d/heightWidthRatio));
				if (newWidth > rectScaledImage.width) {
					newWidth = Math.round(0.9f*rectScaledImage.width);
					newHeight = (int)Math.round((double)newWidth*heightWidthRatio);
				}
				newX = Math.round((float)rectScaledImage.width/2.0f - (float)newWidth/2.0f) + rectScaledImage.x;
				newY = Math.round((float)rectScaledImage.height/2.0f - (float)newHeight/2.0f) + rectScaledImage.y;					
			}
		}
		
		smRect.setRect(newX, newY, 20, 20);				
		proposed.setRect(newX, newY, newWidth, newHeight);
		if ( rectScaledImage.contains(proposed) && proposed.contains(smRect) ) {
			rectScaledCrop.setRect(newX, newY, newWidth, newHeight);
		}
		
		g2.setColor(Color.CYAN);
		g2.setStroke(stroke2);
		g2.drawRect(rectScaledCrop.x, rectScaledCrop.y, rectScaledCrop.width, rectScaledCrop.height);
		
      	g2.setColor(Color.LIGHT_GRAY);
		g2.setStroke(stroke1);
      	int Img3rdWidth = (int)((float)(rectScaledCrop.width)/3.0);
      	int Img3rdHeight = (int)((float)(rectScaledCrop.height)/3.0);			      	
		g2.drawLine(rectScaledCrop.x, rectScaledCrop.y+Img3rdHeight, rectScaledCrop.x + rectScaledCrop.width, rectScaledCrop.y+Img3rdHeight);
		g2.drawLine(rectScaledCrop.x, rectScaledCrop.y+(2*Img3rdHeight), rectScaledCrop.x + rectScaledCrop.width, rectScaledCrop.y+(2*Img3rdHeight));
		g2.drawLine(rectScaledCrop.x+Img3rdWidth, rectScaledCrop.y, rectScaledCrop.x+Img3rdWidth, rectScaledCrop.y + rectScaledCrop.height);
		g2.drawLine(rectScaledCrop.x+(2*Img3rdWidth), rectScaledCrop.y, rectScaledCrop.x+(2*Img3rdWidth), rectScaledCrop.y + rectScaledCrop.height);	  				
		
		//Crop Corners
		activeRegions.add(new ActiveRegion("CornerNW", ActiveRegion.CROP_CORNER, -1, rectScaledCrop.x-5, rectScaledCrop.y-5, 
												rectScaledCrop.x+5, rectScaledCrop.y+5 ));
		activeRegions.add(new ActiveRegion("CornerNE", ActiveRegion.CROP_CORNER, -1, rectScaledCrop.x+rectScaledCrop.width-5, rectScaledCrop.y-5,
												rectScaledCrop.x+rectScaledCrop.width+5, rectScaledCrop.y+5 ));
		activeRegions.add(new ActiveRegion("CornerSW", ActiveRegion.CROP_CORNER, -1, rectScaledCrop.x-5, rectScaledCrop.y+rectScaledCrop.height-5,
												rectScaledCrop.x+5, rectScaledCrop.y+rectScaledCrop.height+5 ));
		activeRegions.add(new ActiveRegion("CornerSE", ActiveRegion.CROP_CORNER, -1, rectScaledCrop.x+rectScaledCrop.width-5, rectScaledCrop.y+rectScaledCrop.height-5,
												rectScaledCrop.x+rectScaledCrop.width+5, rectScaledCrop.y+rectScaledCrop.height+5 ));
	
		//Crop Lines
		activeRegions.add(new ActiveRegion("LineN", ActiveRegion.CROP_LINE, -1, rectScaledCrop.x+5, rectScaledCrop.y-5, 
												rectScaledCrop.x+rectScaledCrop.width-5, rectScaledCrop.y+5 ));
		activeRegions.add(new ActiveRegion("LineS", ActiveRegion.CROP_LINE, -1, rectScaledCrop.x+5, rectScaledCrop.y+rectScaledCrop.height-5,
												rectScaledCrop.x+rectScaledCrop.width-5, rectScaledCrop.y+rectScaledCrop.height+5 ));
		activeRegions.add(new ActiveRegion("LineW", ActiveRegion.CROP_LINE, -1, rectScaledCrop.x-5, rectScaledCrop.y+5,
												rectScaledCrop.x+5, rectScaledCrop.y+rectScaledCrop.height-5 ));
		activeRegions.add(new ActiveRegion("LineE", ActiveRegion.CROP_LINE, -1, rectScaledCrop.x+rectScaledCrop.width-5, rectScaledCrop.y+5,
												rectScaledCrop.x+rectScaledCrop.width+5, rectScaledCrop.y+rectScaledCrop.height-5 ));
		
		//Crop Center
		activeRegions.add(new ActiveRegion("Center", ActiveRegion.CROP_CENTER, -1, rectScaledCrop.x+5, rectScaledCrop.y+5, 
				rectScaledCrop.x+rectScaledCrop.width-5, rectScaledCrop.y+rectScaledCrop.height-5 ));

		cropRatioEvent = false;
		cropDragged = false;
		g2.dispose();
	}
}
