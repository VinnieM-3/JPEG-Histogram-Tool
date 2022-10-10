import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

public abstract class AdjustmentPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -517192845260142222L;
	private final Dimension defaultPanelDimension = new Dimension(757,505);
	private final Color backGroundColor = new Color(20, 20, 20);
	private final Color ImgBackGndColor = new Color(50,50,50);
	private final Font fontBold10 = new Font(Font.SANS_SERIF, Font.BOLD, 10);
	private final Font fontBold12 = new Font(Font.SANS_SERIF, Font.BOLD, 12);	
	private final Color TextColor175 = new Color(175,175,175);
	private final Color TextColor240 = new Color(240,240,240);
	private final Stroke stroke1 = new BasicStroke(1.0f);
	private final Cursor defaultCursor = getCursor();
	private final Cursor handCursor =  new Cursor(Cursor.HAND_CURSOR);

	private final int margin = 10;
	private final int imgBoxWidth = 360;
	private final int imgBoxHeight = 240;	
	
	private BufferedImage origFullImage = null;
	private BufferedImage srcScaledImage = null;
	private BufferedImage dstScaledImage = null;
	private BufferedImage srcScaledImageZoom = null;
	private BufferedImage dstScaledImageZoom = null;	
	private BufferedImage dstScaledImageZoomAdj = null;		
	
	ArrayList<ActiveRegion> activeRegions = null;

	private ActiveRegion curMouseRegion = null;
	private int curMouseX = 0;
	private int curMouseY = 0;
	private int prvMouseX = 0;
	private int prvMouseY = 0;		

	private boolean sliderMoved = false;
	private int curSliderX = 0;

	private boolean zoomed = false;
	private boolean zoomDrag = false;
	private boolean zoomClick = false;
	private int fullPosX = 0;
	private int fullPosY = 0;

	private float magSetting = 0;
	
	abstract float calcMagSetting(int pos);
	abstract String heading();	
	abstract BufferedImage applyAdj(BufferedImage srcScaledImage, float magSetting); 
	
	
	public AdjustmentPanel(BufferedImage srcImg) {
		activeRegions = new ArrayList<ActiveRegion>();
		origFullImage = srcImg;
	    srcScaledImage = ImageAnalysis.getScaledImage(srcImg, imgBoxWidth, imgBoxHeight);
 	    dstScaledImage = ImageAnalysis.getScaledImage(srcScaledImage, imgBoxWidth, imgBoxHeight);    
	
		magSetting = 0;
		int sliderBaseX = margin*2 + imgBoxWidth + (int)Math.round((imgBoxWidth-201)/2);
		curSliderX = sliderBaseX + 100;
		
 	    setBackground(backGroundColor);
		setSize(defaultPanelDimension);
 		addMouseListener(this);
 	    addMouseMotionListener(this);
 	}
	
	public Dimension getPreferredSize() {
		return defaultPanelDimension;
	}
	
	public float getSetting() {
		return magSetting;
	}
	
	public void mouseEntered(MouseEvent e) {}	
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
	public void mouseMoved(MouseEvent e) { 
		boolean inActiveRegion = false;
		curMouseX = e.getX();
		curMouseY = e.getY();
		for (ActiveRegion ar : activeRegions) {
			if ( ar.isInBounds(curMouseX, curMouseY) ) {
				inActiveRegion = true;
				if ( curMouseRegion != ar ) {
					curMouseRegion = ar;
					this.setCursor(handCursor);
				}
				repaint();
				break;
			}
		}
		if ( ( curMouseRegion != null ) && (inActiveRegion == false) ) {
			curMouseRegion = null;
			this.setCursor(defaultCursor);
			repaint();
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		boolean inActiveRegion = false;
		curMouseX = e.getX();
		curMouseY = e.getY();
		for (ActiveRegion ar : activeRegions) {
			if ( ar.isInBounds(curMouseX, curMouseY) ) {
				inActiveRegion = true;
				curMouseRegion = ar;
				if ( ar.Name.equals("Slider") ) {
					sliderMoved = true;
					dstScaledImageZoomAdj = null;
				}else if ( ar.Name.equals("Zoom") ) {
					if (zoomed) {
						zoomed = false;
					}else {
						zoomed = true;
						zoomClick = true;
						dstScaledImageZoomAdj = null;
					}
				}
				repaint();
				break;
			}
		}
		if ( ( curMouseRegion != null ) && (inActiveRegion == false) ) {
			curMouseRegion = null;
			this.setCursor(defaultCursor);
			repaint();
		}
	}
	
	
	public void mouseDragged(MouseEvent e) { 
		boolean inActiveRegion = false;
		prvMouseX = curMouseX;
		prvMouseY = curMouseY;		
		curMouseX = e.getX();
		curMouseY = e.getY();
		for (ActiveRegion ar : activeRegions) {
			if ( ar.isInBounds(curMouseX, curMouseY) ) {
				inActiveRegion = true;
				if ( curMouseRegion != ar ) {
					curMouseRegion = ar;
				}
				if ( ar.Name.equals("Slider") ) {
					sliderMoved = true;
					dstScaledImageZoomAdj = null;
				}else if ( (ar.Name.equals("Zoom")) && (zoomed) ) {
					zoomDrag = true;
					dstScaledImageZoomAdj = null;
				}
				repaint();
				break;
			}
		}
		if ( ( curMouseRegion != null ) && (inActiveRegion == false) ) {
			curMouseRegion = null;
			this.setCursor(defaultCursor);
			repaint();
		}
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		activeRegions.clear();
		Graphics2D g2 = (Graphics2D)g;
		
		//highlight Active Region
		if ( curMouseRegion != null) {
			g.setColor(Color.CYAN);
			g.draw3DRect(curMouseRegion.x1, curMouseRegion.y1, (curMouseRegion.x2-curMouseRegion.x1), curMouseRegion.y2-curMouseRegion.y1, true);
		}

		//Titles over images
		int yTitlePoint = margin + 10;
		g2.setColor(TextColor240); 
		g2.setFont(fontBold12);
		int cntrLeftBox = (int)((float)imgBoxWidth/2.0f) + margin;
		int cntrRightBox = (int)((float)imgBoxWidth*1.5f) + 2*margin;		
		g2.drawString("<<< BEFORE >>>", cntrLeftBox - 45, yTitlePoint);
		g2.drawString("<<< AFTER >>>", cntrRightBox - 45, yTitlePoint);
		
		//Border around images
		int yImgPoint = yTitlePoint + margin;
      	g.setColor(ImgBackGndColor);
      	g2.fill3DRect(10, yImgPoint, imgBoxWidth-1, imgBoxHeight, true);
      	g2.fill3DRect(20 + imgBoxWidth, yImgPoint, imgBoxWidth-1, imgBoxHeight, true);
		activeRegions.add(new ActiveRegion("Zoom", ActiveRegion.IMAGE_VIEW, -1, 10-1, yImgPoint-1, 10 + imgBoxWidth-1+2, yImgPoint + imgBoxHeight+2 ));
		activeRegions.add(new ActiveRegion("Zoom", ActiveRegion.IMAGE_VIEW, -1, 20 + imgBoxWidth-1, yImgPoint-1, 20 + imgBoxWidth-1 + imgBoxWidth-1+2, yImgPoint + imgBoxHeight+2 ));

		int sliderBaseX = margin*2 + imgBoxWidth + (int)Math.round((imgBoxWidth-201)/2);
		if (sliderMoved) {
			curSliderX = curMouseX;
			int pos = curSliderX - sliderBaseX - 100;
			if ( pos > 100 ) curSliderX = sliderBaseX + 200;
			if ( pos < -100 ) curSliderX = sliderBaseX;
			magSetting = calcMagSetting(curSliderX - sliderBaseX - 100);
			dstScaledImage = applyAdj(srcScaledImage, magSetting);
		}

		int sliderBaseY = yImgPoint + imgBoxHeight + 25;
		g.setColor(TextColor240);
		int charLen = Math.round(heading().length()*3f);
		g2.drawString(heading(), cntrRightBox - charLen, sliderBaseY);
		g2.drawString(String.valueOf((curSliderX - sliderBaseX - 100)), cntrRightBox - 5, sliderBaseY+15);

		sliderBaseY = sliderBaseY + 35;
		g.setColor(Color.CYAN);
		g2.fill3DRect(sliderBaseX, sliderBaseY, 201, 5,false);
		ActiveRegion ar = new ActiveRegion("Slider", ActiveRegion.SLIDER, -1, sliderBaseX-20, sliderBaseY-10, sliderBaseX + 221, sliderBaseY + 15);
		activeRegions.add(ar);

		g.setColor(Color.CYAN);
		g2.fill3DRect(curSliderX-2, sliderBaseY-5, 5, 15, true);

		int imgOffsetX, imgOffsetY = 0;
		int leftScaledImgX, rightScaledImgX, leftScaledImgY, rightScaledImgY = 0;
		if ( srcScaledImage.getWidth() < imgBoxWidth ) {
			imgOffsetX = (int)Math.round((imgBoxWidth - srcScaledImage.getWidth()) / 2);
		}else {
			imgOffsetX = 0;
		}
		if ( srcScaledImage.getHeight() < imgBoxHeight ) {
			imgOffsetY = (int)Math.round((imgBoxHeight - srcScaledImage.getHeight()) / 2);
		}else {
			imgOffsetY = 0;
		}
		leftScaledImgX = margin + imgOffsetX;
		rightScaledImgX = margin + imgBoxWidth + margin + imgOffsetX;
		leftScaledImgY = yImgPoint + imgOffsetY;
		rightScaledImgY = yImgPoint + imgOffsetY;		
		
		int halfBoxWidth = (int)Math.floor((float)imgBoxWidth/2.0f);
		int halfBoxHeight = (int)Math.floor((float)imgBoxHeight/2.0f);					
	
      	//draw images
		if ( (zoomed) ) {
			if ( zoomDrag ) {
				fullPosX = fullPosX - (curMouseX-prvMouseX);
				int imgWidth = imgBoxWidth;
				if (origFullImage.getWidth() < imgBoxWidth ) {
					fullPosX = 0;
					imgWidth = origFullImage.getWidth();
				}else if ( fullPosX < 0 ) {
					fullPosX = 0;
					imgWidth = imgBoxWidth;
				}else if ( fullPosX > (origFullImage.getWidth()-imgBoxWidth) ) {
					fullPosX = origFullImage.getWidth()-imgBoxWidth;
					imgWidth = imgBoxWidth;
				}
	
				fullPosY = fullPosY - (curMouseY-prvMouseY);
				int imgHeight = imgBoxHeight;
				if (origFullImage.getHeight() < imgBoxHeight ) {
					fullPosY = 0;
					imgHeight = origFullImage.getHeight();
				}else if ( fullPosY < 0 ) {
					fullPosY = 0;
					imgHeight = imgBoxHeight;
				}else if  ( fullPosY > (origFullImage.getHeight()-imgBoxHeight) ) {
					fullPosY = origFullImage.getHeight()-imgBoxHeight;
					imgHeight = imgBoxHeight;
				}
				srcScaledImageZoom = ImageAnalysis.getImageClip(origFullImage, fullPosX, fullPosY, imgWidth, imgHeight);
				dstScaledImageZoom = ImageAnalysis.getImageClip(origFullImage, fullPosX, fullPosY, imgWidth, imgHeight);
			}else if (zoomClick) {
				float scaledPosX = 0f;
				if (curMouseX < (margin + imgBoxWidth) ) {
					scaledPosX = curMouseX - leftScaledImgX;
				}else {
					scaledPosX = curMouseX - rightScaledImgX;
				}
				fullPosX = Math.round(origFullImage.getWidth()*(scaledPosX/(float)srcScaledImage.getWidth()));
				int rightSpace = origFullImage.getWidth() - fullPosX;
				int leftSpace = fullPosX;
				int imgWidth = 0;
				if (origFullImage.getWidth() < imgBoxWidth ) {
					fullPosX = 0;
					imgWidth = origFullImage.getWidth();
				}else if ( (rightSpace > halfBoxWidth) && (leftSpace > halfBoxWidth) ) {
					fullPosX = fullPosX - halfBoxWidth;
					imgWidth = imgBoxWidth;
				}else if (rightSpace < halfBoxWidth) {
					fullPosX = origFullImage.getWidth() - imgBoxWidth;
					imgWidth = imgBoxWidth;
				}else if (leftSpace < halfBoxWidth) {
					fullPosX = 0;
					imgWidth = imgBoxWidth;
				}
				
				float scaledPosY = curMouseY - leftScaledImgY;						
				fullPosY = Math.round(origFullImage.getHeight()*(scaledPosY/(float)srcScaledImage.getHeight()));
				int bottomSpace = origFullImage.getHeight() - fullPosY;
				int topSpace = fullPosY;
				int imgHeight = 0;
				if (origFullImage.getHeight() < imgBoxHeight ) {
					fullPosY = 0;
					imgHeight = origFullImage.getHeight();
				}else if ( (bottomSpace > halfBoxHeight) && (topSpace > halfBoxHeight) ) {
					fullPosY = fullPosY - halfBoxHeight;
					imgHeight = imgBoxHeight;
				}else if (bottomSpace < halfBoxHeight) {
					fullPosY = origFullImage.getHeight() - imgBoxHeight;
					imgHeight = imgBoxHeight;
				}else if (topSpace < halfBoxHeight) {
					fullPosY = 0;
					imgHeight = imgBoxHeight;
				}
				srcScaledImageZoom = ImageAnalysis.getImageClip(origFullImage, fullPosX, fullPosY, imgWidth, imgHeight);
				dstScaledImageZoom = ImageAnalysis.getImageClip(origFullImage, fullPosX, fullPosY, imgWidth, imgHeight);
			}else if (sliderMoved) {
				dstScaledImageZoomAdj = null;
			}
			if (dstScaledImageZoomAdj == null) {
				dstScaledImageZoomAdj = applyAdj(dstScaledImageZoom, magSetting);
			}
			if ( srcScaledImageZoom.getWidth() < imgBoxWidth ) {
				imgOffsetX = (int)Math.round((imgBoxWidth - srcScaledImageZoom.getWidth()) / 2);
			}else {
				imgOffsetX = 0;
			}
			if ( srcScaledImageZoom.getHeight() < imgBoxHeight ) {
				imgOffsetY = (int)Math.round((imgBoxHeight - srcScaledImageZoom.getHeight()) / 2);
			}else {
				imgOffsetY = 0;
			}
			int leftZoomImgX, rightZoomImgX, leftZoomImgY, rightZoomImgY = 0;
			leftZoomImgX = margin + imgOffsetX;
			rightZoomImgX = margin + imgBoxWidth + margin + imgOffsetX;
			leftZoomImgY = yImgPoint + imgOffsetY;
			rightZoomImgY = yImgPoint + imgOffsetY;		

			g2.drawImage(srcScaledImageZoom, leftZoomImgX, leftZoomImgY, srcScaledImageZoom.getWidth(), srcScaledImageZoom.getHeight(), null);
			g2.drawImage(dstScaledImageZoomAdj, rightZoomImgX, rightZoomImgY, dstScaledImageZoomAdj.getWidth(), dstScaledImageZoomAdj.getHeight(), null);
		}else {
			g2.drawImage(srcScaledImage, leftScaledImgX, leftScaledImgY, srcScaledImage.getWidth(), srcScaledImage.getHeight(), null);
			g2.drawImage(dstScaledImage, rightScaledImgX, rightScaledImgY, dstScaledImage.getWidth(), dstScaledImage.getHeight(), null);
		}
		
		//Histograms
		double dblFactor = 0.25;
		int[] aryLum;
		int[] aryRGB;
		int	totImgPix = 0;
		HistogramArray ha = null;
		double dblHistFact = 0;
		int LumHistX1 = 0;
		int LumHistY2 = 0;
		int RGBHistX1 = 0;
		int RGBHistY2 = 0;
		

		//Draw srcScaledImage Histograms
		totImgPix = srcScaledImage.getWidth()*srcScaledImage.getHeight();
		dblHistFact = ((70*256.0*dblFactor)/((float)(totImgPix)));
		ha = new HistogramArray(srcScaledImage);
		aryLum = ha.aryLum;
		aryRGB = ha.aryRGB;


		//Luminance srcScaledImage Histogram
		LumHistX1 = cntrLeftBox - (int)(255f/2f);
		LumHistY2 = sliderBaseY + 80;
		g.setColor(TextColor175); 
		g.setFont(fontBold10);
		g.drawString("Luminance", LumHistX1 + 200, LumHistY2+10);
		g.setColor(Color.WHITE);
		g2.setStroke(stroke1);
		for (int r=0;r<256;r++) {
			int lineLen =  (int)(aryLum[r]*dblHistFact);
			if ( lineLen > 60 ) lineLen = 60;
			g.drawLine(LumHistX1 + r, LumHistY2, LumHistX1 + r, (int)(LumHistY2 - lineLen));   
		}

		//RGB srcScaledImage Histogram
		RGBHistX1 = cntrLeftBox - (int)(255f/2f);
		RGBHistY2 = LumHistY2 + 75;
		g.setColor(TextColor175); 
		g.setFont(fontBold10);
		g.drawString("RGB", RGBHistX1 + 230, RGBHistY2+10);
		g.setColor(Color.ORANGE);
		g2.setStroke(stroke1);
		for (int r=0;r<256;r++) {
			int lineLen =  (int)(aryRGB[r]*dblHistFact/3.0); //3x as many points
			if ( lineLen > 60 ) lineLen = 60;
			g.drawLine(RGBHistX1 + r, RGBHistY2, RGBHistX1 + r, (int)(RGBHistY2 - lineLen));           	
		}


		//Draw dstScaledImage Histograms
		totImgPix = dstScaledImage.getHeight()*dstScaledImage.getWidth();
		dblHistFact = ((70*256.0*dblFactor)/((float)(totImgPix)));
		ha = new HistogramArray(dstScaledImage);
		aryLum = ha.aryLum;
		aryRGB = ha.aryRGB;
		
		//Luminance dstScaledImage Histogram
		LumHistX1 = cntrRightBox - (int)(255f/2f);
		LumHistY2 = sliderBaseY + 80;
		g.setColor(TextColor175); 
		g.setFont(fontBold10);
		g.drawString("Luminance", LumHistX1 + 200, LumHistY2+10);
		g.setColor(Color.WHITE);
		g2.setStroke(stroke1);
		for (int r=0;r<256;r++) {
			int lineLen =  (int)(aryLum[r]*dblHistFact);
			if ( lineLen > 60 ) lineLen = 60;
			g.drawLine(LumHistX1 + r, LumHistY2, LumHistX1 + r, (int)(LumHistY2 - lineLen));   
		}

		//RGB dstScaledImage Histogram
		RGBHistX1 = cntrRightBox - (int)(255f/2f);
		RGBHistY2 = LumHistY2 + 75;
		g.setColor(TextColor175); 
		g.setFont(fontBold10);
		g.drawString("RGB", RGBHistX1 + 230, RGBHistY2+10);
		g.setColor(Color.ORANGE);
		g2.setStroke(stroke1);
		for (int r=0;r<256;r++) {
			int lineLen =  (int)(aryRGB[r]*dblHistFact/3.0); //3x as many points
			if ( lineLen > 60 ) lineLen = 60;
			g.drawLine(RGBHistX1 + r, RGBHistY2, RGBHistX1 + r, (int)(RGBHistY2 - lineLen));           	
		}
		
		sliderMoved = false;
		zoomDrag = false;
		zoomClick = false;
		g2.dispose();
	}
}
