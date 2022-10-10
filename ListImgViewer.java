import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.JOptionPane;
import java.io.*;


public class ListImgViewer extends View {
	private static final long serialVersionUID = -9130956177434759178L;
	private Dimension defaultPanelDimension = new Dimension(800,600);
	private Color backGroundColor = new Color(20, 20, 20);
	private int topMargin = 10;
	private int bottomMargin = 10;
	private int leftMargin = 10;
	private int rightMargin = 10;
	private int header = 120;
	private int footer = 25;
	
	private int imgSpacer = 75; 
	
	private int imgBoxHeight = 180;
	private int imgBoxWidth = 270;
	
	private ArrayList<Integer> horzAlign = null;
	
	private ArrayList<ImageView> imageViewCol = null;

	private ImageLoader imgLoader;
	
	private int fileCollectionID = 0;
	
	private int layoutType = 1;
	
		
	public ListImgViewer() {
	    super(new BorderLayout());
	    horzAlign = new ArrayList<Integer>();		
		horzAlign.add(Integer.valueOf(0));
		layoutType = 1;

	    imageViewCol = new ArrayList<ImageView>();		
		setBackground(backGroundColor);
	   	setSize(defaultPanelDimension);
	}
	
	public void leaveView() {
    	if (imgLoader != null ) {
     		imgLoader.cancel(true);
     		while (!imgLoader.isDone()) {Thread.yield();}
     	}
	}
	
	public void displayView(int fileCollectionID) {
     	if (imgLoader != null ) {
     		imgLoader.cancel(true);
     		while (!imgLoader.isDone()) {Thread.yield();}
     	}
     	
    	if ( fileCollectionID != this.fileCollectionID  ) {  
     		imageViewCol.clear();
     		this.fileCollectionID = fileCollectionID;
     	}
      	
	   	this.setSize(getPreferredSize());
		this.getParent().validate();
    	
		imgLoader = new ImageLoader(imageViewCol);
 		imgLoader.execute();
	}
		
	public void setLayoutType(int type) {
		layoutType = type;
	    horzAlign = new ArrayList<Integer>();		
 		switch (layoutType) {
		case 1:
			leftMargin = 10;			
			horzAlign.add(Integer.valueOf(0));
			break;
		case 2: 
			leftMargin = 10;
			horzAlign.add(Integer.valueOf(0));
			horzAlign.add(Integer.valueOf(490));			
			break;
		case 3:
			leftMargin = 10;
			horzAlign.add(Integer.valueOf(0));
			horzAlign.add(Integer.valueOf(330));
			horzAlign.add(Integer.valueOf(660));	
			break;
		}

	   	setSize(getPreferredSize());
		getParent().validate();
		repaint();
 	}
	
	public void mouseClicked(MouseEvent e) {
		ActiveRegion ar = null;
		for (int x=0;x<activeRegions.size();x++) {
			ar = activeRegions.get(x);
			if ( ar.isInBounds(e.getX(), e.getY()) ) {
				break;
			}
			ar = null;
		}
		if (ar != null) {
			if (ar.Name.equals("CacheImg_Plain")) {
				imageViewCol.get(ar.ID).H_S_Flag = true;
			}else if (ar.Name.equals("Est_H_S_Image")) {
				imageViewCol.get(ar.ID).H_S_Flag = false;
			}else if (ar.Name.equals("Grid_3rds_On")) {
				imageViewCol.get(ar.ID).Grid_Flag = false;
			}else if (ar.Name.equals("Grid_3rds_Off")) {
				imageViewCol.get(ar.ID).Grid_Flag = true;
			}else if (ar.Name.equals("Selected")) {
				imageViewCol.get(ar.ID).imageAnalysis.Hidden = true;
				if ( settings.FilterState == true ) {
				   	setSize(getPreferredSize());
				}
			}else if (ar.Name.equals("Hidden")) {
				imageViewCol.get(ar.ID).imageAnalysis.Hidden = false;
			}else if (ar.Name.equals("Hist_Approx")) {
				setCursor(hourglassCursor);
				imageViewCol.get(ar.ID).ActHist_Flag = true;
			}else if (ar.Name.equals("Hist_Actual")) {
				imageViewCol.get(ar.ID).ActHist_Flag = false;
			}else if (ar.Name.equals("Rotate")) {
				setCursor(hourglassCursor);
				imageViewCol.get(ar.ID).incAngle();
			}else if (ar.Name.equals("Image")) {
				setCursor(hourglassCursor);
				Settings.overlay.drawLargeImage(ar.ID, imageViewCol.get(ar.ID).H_S_Flag, imageViewCol.get(ar.ID).Grid_Flag);
		        Settings.overlay.setVisible(true);
			}else if (ar.Name.equals("Rename")) {
                String newName = JOptionPane.showInputDialog(settings.mainFrame,"Rename File To:", imageViewCol.get(ar.ID).imageAnalysis.fileName);
                if ((newName != null) && (newName.length() > 0)) {
                	File oldFile = new File(imageViewCol.get(ar.ID).imageAnalysis.filePath + "/" + imageViewCol.get(ar.ID).imageAnalysis.fileName);
                	File newFile = new File(imageViewCol.get(ar.ID).imageAnalysis.filePath + "/" + newName);
                	if (!newFile.exists()) {
                		oldFile.renameTo(newFile);
                    	imageViewCol.get(ar.ID).imageAnalysis.fileName = newName;
                	}else {
                		JOptionPane.showMessageDialog(settings.mainFrame, "File Already Exists", "Error", JOptionPane.ERROR_MESSAGE);
                	}
                 }
			}
		}
		repaint();
	}
	
	@Override
	public void buttonClickEvent(JScrollPane s, int target) {
		if ( target == View.TOP ) {
			s.getVerticalScrollBar().setValue(0);
		}else if ( target == View.BOTTOM) {
			s.getVerticalScrollBar().setValue(this.getHeight());
		}else if ( target == View.ONE_DOWN ) {
			int cursor = topMargin + header;
			int curScrPnt = s.getVerticalScrollBar().getValue();
			while ( cursor <= (curScrPnt+15)) {
				cursor = cursor + (imgBoxHeight + imgSpacer);
			}
			s.getVerticalScrollBar().setValue(cursor-15);
		}else if ( target == View.ONE_UP ) {
			int cursor = this.getHeight() - footer - bottomMargin;
			int curScrPnt = s.getVerticalScrollBar().getValue();
			while ( cursor >= (curScrPnt+15)) {
				cursor = cursor - (imgBoxHeight + imgSpacer);
			}
			s.getVerticalScrollBar().setValue(cursor-15);
		}else if ( target >= 0 ) {
			int cursor = topMargin + header + (int)Math.floor((float)target/(float)(horzAlign.size()))*(imgBoxHeight + imgSpacer);
			s.getVerticalScrollBar().setValue(cursor-15);
		}
	}

	public Dimension getPreferredSize() {
		Dimension dim = null;
		int MinHeight = topMargin + bottomMargin + header + footer;
		int panelHeight = MinHeight;
		int panelWidth = leftMargin + imgBoxWidth + 190 + 256 + 10 + 256 + rightMargin;
		int numActive = settings.getNumImagesActive();
		if ( numActive > 0 ) {
			int numRows = (int)Math.ceil((float)(numActive /(float)(horzAlign.size())));
			panelHeight = topMargin + header + numRows*(imgBoxHeight + imgSpacer) + footer + bottomMargin;
			dim = new Dimension(panelWidth,panelHeight);
 		}else {
 			dim = defaultPanelDimension;
 		}
 		return dim;
	}
	
 	protected void paintComponent(Graphics g) {
    	super.paintComponent(g); 
    	
    	Graphics2D g2 = (Graphics2D)g;
    	
    	Rectangle visRect = getVisibleRect();

 		activeRegions.clear();
 		
     	//print directory selected
	   	g.setColor(TextColor150);
      	g.setFont(fontBold20);
      	g.drawString("Folder: " + settings.getFilePath(), leftMargin, 25);
    	g.drawString("Total JPEG Images Scanned = " + imageViewCol.size() + " of " + settings.ImageAnalysisCol.size(), leftMargin, 55);
		int numActive = settings.getNumImagesActive();
		if ( settings.FilterState == true ) {
			g.drawString("Image Filter is On: " + numActive + " images selected", leftMargin, 85);
		}else {
			g.drawString("Image Filter is Off", leftMargin, 85);
		}
	
     	int rowNum = 0;
     	int a = 0;
     	int alignX = horzAlign.get(a);
    	for (int x=0;x<imageViewCol.size();x++) {
    		ImageView IV = imageViewCol.get(x);
      		if ( !settings.FilterState || (settings.FilterState && !IV.imageAnalysis.Hidden ) ) {
      			int RegionY1 = topMargin + header + rowNum*(imgBoxHeight + imgSpacer) - 20;
      			int RegionY2 = topMargin + header + rowNum*(imgBoxHeight + imgSpacer) + imgBoxHeight + 20;	      			
      			
      			if ( (RegionY2 >= visRect.y) && (RegionY1 <= (visRect.y + visRect.height)) ) {

      				BufferedImage CacheImage = IV.getScaledImage();

		
			    	if ( curMouseRegion != null) {
						g.setColor(Color.CYAN);
						g.draw3DRect(curMouseRegion.x1, curMouseRegion.y1, (curMouseRegion.x2-curMouseRegion.x1), curMouseRegion.y2-curMouseRegion.y1, true);
			    	}
	      			
		  			int ImgBoxX1 = leftMargin + alignX;
		  			int ImgBoxX2 = ImgBoxX1 + imgBoxWidth;
	      			int ImgBoxY1 = topMargin + header + rowNum*(imgBoxHeight + imgSpacer);
		  			int ImgBoxY2 = ImgBoxY1 + imgBoxHeight;	  			
	 	  			
	      			int ImgX1 = ImgBoxX1;
	      			if ( CacheImage.getWidth() < imgBoxWidth ) {
	      				ImgX1 = ImgX1 + (int)Math.round((float)((imgBoxWidth-CacheImage.getWidth()))/2.0);
	      			}
	      			
	      			int ImgY1 = ImgBoxY1;
	      			if (CacheImage.getHeight() < imgBoxHeight) {
	      				ImgY1 = ImgY1 + (int)Math.round((float)((imgBoxHeight-CacheImage.getHeight()))/2.0);
	      			}
	
	      			//border around image
			      	g.setColor(ImgBackGndColor);
			      	g2.fill3DRect(ImgBoxX1-1, ImgBoxY1-1, imgBoxWidth+2, imgBoxHeight+2, true);
	      			
			      	g.setFont(fontBold11);
	  				g.setColor(Color.CYAN);	

		  			//Rotation
		            int Rotation_Loc = ImgBoxX1;
					int Rotation_Loc2 = Rotation_Loc + g.getFontMetrics().stringWidth("Rotate");	
		            g.drawString("Rotate", Rotation_Loc, ImgBoxY2+14);
		            activeRegions.add(new ActiveRegion("Rotate", ActiveRegion.TEXT_CMD, x, Rotation_Loc-1, ImgBoxY2+2, Rotation_Loc2, ImgBoxY2+16 ));
	  				
	  				
		            int H_S_Flag_Loc = Rotation_Loc2 + 15;
					int H_S_Flag_Loc2 = H_S_Flag_Loc + g.getFontMetrics().stringWidth("Clipping Off");			            
		  			if ( IV.H_S_Flag ) {
		  				g.drawImage(IV.getH_S_ScaledImage(), ImgX1, ImgY1, null);
			            g.drawString("Clipping On", H_S_Flag_Loc, ImgBoxY2+14);
			            activeRegions.add(new ActiveRegion("Est_H_S_Image", ActiveRegion.TEXT_CMD, x, H_S_Flag_Loc-1, ImgBoxY2+2, H_S_Flag_Loc2, ImgBoxY2+16 ));
		  			}else {
						g.drawImage(CacheImage, ImgX1, ImgY1, null);
			            g.drawString("Clipping Off", H_S_Flag_Loc, ImgBoxY2+14);
			            activeRegions.add(new ActiveRegion("CacheImg_Plain", ActiveRegion.TEXT_CMD, x, H_S_Flag_Loc-1, ImgBoxY2+2, H_S_Flag_Loc2, ImgBoxY2+16 ));
		  			}
		            activeRegions.add(new ActiveRegion("Image", ActiveRegion.IMAGE_VIEW, x, ImgBoxX1-2, ImgBoxY1-2, ImgBoxX2+2, ImgBoxY2+2 ));
		  			
		  			
		            int Grid_3rds_Loc = H_S_Flag_Loc2 + 15;
					int Grid_3rds_Loc2 = Grid_3rds_Loc + g.getFontMetrics().stringWidth("Grid Off");			            
		  			if ( IV.Grid_Flag) {
				      	g.setColor(Color.WHITE);
				      	int Img3rdWidth = (int)((float)(CacheImage.getWidth())/3.0);
				      	int Img3rdHeight = (int)((float)(CacheImage.getHeight())/3.0);			      	
		  				g.drawLine(ImgX1, ImgY1+Img3rdHeight, ImgX1 + CacheImage.getWidth(), ImgY1+Img3rdHeight);
		  				g.drawLine(ImgX1, ImgY1+(2*Img3rdHeight), ImgX1 + CacheImage.getWidth(), ImgY1+(2*Img3rdHeight));
		  				g.drawLine(ImgX1+Img3rdWidth, ImgY1, ImgX1+Img3rdWidth, ImgY1 + CacheImage.getHeight());
		  				g.drawLine(ImgX1+(2*Img3rdWidth), ImgY1, ImgX1+(2*Img3rdWidth), ImgY1 + CacheImage.getHeight());	  				
		  				g.setColor(Color.CYAN);
		  				g.drawString("Grid On", Grid_3rds_Loc, ImgBoxY2+14);
			            activeRegions.add(new ActiveRegion("Grid_3rds_On", ActiveRegion.TEXT_CMD, x, Grid_3rds_Loc-1, ImgBoxY2+2, Grid_3rds_Loc2, ImgBoxY2+16 ));
		  			}else {
			            g.drawString("Grid Off", Grid_3rds_Loc, ImgBoxY2+14);
			            activeRegions.add(new ActiveRegion("Grid_3rds_Off", ActiveRegion.TEXT_CMD, x, Grid_3rds_Loc-1, ImgBoxY2+2, Grid_3rds_Loc2, ImgBoxY2+16 ));
		  			}
		  			
		            int Selected_Loc = Grid_3rds_Loc2 + 15;
					int Selected_Loc2 = Selected_Loc + g.getFontMetrics().stringWidth("Selected");			            
		  			if ( IV.imageAnalysis.Hidden ) {
			            g.drawString("Filtered", Selected_Loc, ImgBoxY2+14);
			            activeRegions.add(new ActiveRegion("Hidden", ActiveRegion.TEXT_CMD, x, Selected_Loc-1, ImgBoxY2+2, Selected_Loc2, ImgBoxY2+16 ));
		  			}else {
			            g.drawString("Selected", Selected_Loc, ImgBoxY2+14);
			            activeRegions.add(new ActiveRegion("Selected", ActiveRegion.TEXT_CMD, x, Selected_Loc-1, ImgBoxY2+2, Selected_Loc2, ImgBoxY2+16 ));
		  			}
  			
		  			if (horzAlign.size() <= 2 ) {
			  			ImageEXIF exif = IV.imageAnalysis.getEXIF();
				    	g.setFont(fontBold11);
					   	g.setColor(Color.CYAN);
				    	g.drawString("Name: " + IV.imageAnalysis.fileName , ImgBoxX2 + 10, ImgBoxY1 + 15);
			            activeRegions.add(new ActiveRegion("Rename", ActiveRegion.TEXT_CMD, x, ImgBoxX2+10-1, ImgBoxY1+2, ImgBoxX2+190, ImgBoxY1+17 ));
				    	g.setColor(TextColor150);
				    	g.drawString("Orig Date: " + exif.imageDateTime , ImgBoxX2 + 10, ImgBoxY1 + 35);	    	
				    	g.drawString("ISO: " + exif.imageISO , ImgBoxX2 + 10, ImgBoxY1 + 55);	    	
			    		g.drawString("Speed: " + exif.imageTx, ImgBoxX2 + 10, ImgBoxY1 + 75);	
					    g.drawString("Aperture: " + exif.imageAp , ImgBoxX2 + 10, ImgBoxY1 + 95);	    	
				    	g.drawString("Focal Length: " + exif.imageFocalLengths, ImgBoxX2 + 10, ImgBoxY1 + 115);	    	
				    	g.drawString("ExpBias: " + exif.imageExpBias, ImgBoxX2 + 10, ImgBoxY1 + 135);	    	
				    	g.drawString("Flash: " + exif.imageFlashMode, ImgBoxX2 + 10, ImgBoxY1 + 155);
				    	g.drawString("Size: " + IV.imageAnalysis.imageHeight + " x " + IV.imageAnalysis.imageWidth, ImgBoxX2 + 10, ImgBoxY1 + 175);
				    	if (!exif.cameraModel.equals("?"))  {
				    		g.drawString(exif.cameraModel, ImgBoxX2 + 10, ImgBoxY1 + 195);	
				    	}
		  			}else if (horzAlign.size() == 3 ) {
				    	g.setFont(fontBold12);
					   	g.setColor(Color.CYAN);
				    	g.drawString("Name: " + IV.imageAnalysis.fileName , ImgBoxX1, ImgBoxY1-5);
			            activeRegions.add(new ActiveRegion("Rename", ActiveRegion.TEXT_CMD, x, ImgBoxX1-1, ImgBoxY1-18, ImgBoxX1+200, ImgBoxY1-3 ));
		  			}
		  			
		  			if (horzAlign.size() == 1 ) {
				    	double dblHistFact = 0;
				    	int[] aryLum;
				    	int[] aryRGB;
				    	int[] aryR;
				    	int[] aryG;
				    	int[] aryB;  
				    	if ( IV.ActHist_Flag ) {
			    			HistogramArray ha = IV.imageAnalysis.getHistArray();
				            dblHistFact = (((double)(imgBoxHeight)*0.1*256.0)/((float)(ha.numPixels)));
				            aryLum = ha.aryLum;
				            aryRGB = ha.aryRGB;
				            aryR = ha.aryR;
				            aryG = ha.aryG;
				            aryB = ha.aryB;
				    	}else {  //estimated
			    			HistogramArray ha = IV.imageAnalysis.getHist270_180();
				            dblHistFact = (((double)(imgBoxHeight)*0.1*256.0)/((float)(ha.numPixels)));
				            aryLum = ha.aryLum;
				            aryRGB = ha.aryRGB;
				            aryR = ha.aryR;
				            aryG = ha.aryG;
				            aryB = ha.aryB;
				    	}
				    	
			            int lineLen = 0;
			            int r = 0;
			            
				    	g.setFont(fontBold10);
				    	//Luminance Histogram
			            int LumHistX1 = ImgBoxX2 + 190;
			            int LumHistY2 = ImgBoxY1 + (int)Math.round(0.45*(float)imgBoxHeight);
			            g.setColor(TextColor150); 
			            g.drawString("Luminance", LumHistX1 + 200, LumHistY2+10);
			            g.setColor(new Color(255, 255, 255, 128));
				    	for (r=0;r<256;r++) {
			            	lineLen =  (int)(aryLum[r]*dblHistFact);
			            	if ( lineLen > imgBoxHeight*0.45 ) lineLen = (int)(imgBoxHeight*0.45);
			                g.drawLine(LumHistX1 + r, LumHistY2, LumHistX1 + r, (int)(LumHistY2 - lineLen));    
			            }
				    	
				    	//RGB Histogram
				    	int RGBHistX1 = ImgBoxX2 + 190;
			            int RGBHistY2 = ImgBoxY2;
			            g.setColor(TextColor150); 
			            g.drawString("RGB", RGBHistX1 + 230, RGBHistY2+10);
			            g.setColor(Color.ORANGE);
			            for (r=0;r<256;r++) {
			            	lineLen =  (int)(aryRGB[r]*dblHistFact/3.0); //3x as many points
			            	if ( lineLen > imgBoxHeight*0.45 ) lineLen = (int)(imgBoxHeight*0.45);
			                g.drawLine(RGBHistX1 + r, RGBHistY2, RGBHistX1 + r, (int)(RGBHistY2 - lineLen));           	
			            }
			            
			            //Red Histogram
			            g.setColor(Color.RED);
			            int RHistX1 = ImgBoxX2 + 190 + 256 + 10;
			            int RHistY2 = ImgBoxY1 + (int)((float)imgBoxHeight/3.0);   
			            for (r=0;r<256;r++) {
			            	lineLen =  (int)(aryR[r]*dblHistFact);
			            	if ( lineLen > imgBoxHeight*0.3 ) lineLen = (int)(imgBoxHeight*0.3);
			                g.drawLine(RHistX1 + r, RHistY2, RHistX1 + r, (int)(RHistY2 - lineLen));           	
			            }
			  	
			            //Green Histogram
			            g.setColor(Color.GREEN);
			            int GHistX1 = ImgBoxX2 + 190 + 256 + 10;
			            int GHistY2 = ImgBoxY1 + (int)((float)imgBoxHeight*0.6666);   
			            for (r=0;r<256;r++) {
			            	lineLen =  (int)(aryG[r]*dblHistFact);
			            	if ( lineLen > imgBoxHeight*0.3 ) lineLen = (int)(imgBoxHeight*0.3);
			                g.drawLine(GHistX1 + r, GHistY2, GHistX1 + r, (int)(GHistY2 - lineLen));           	
			            }
			  	
			            //Blue Histogram	            
			            g.setColor(Color.CYAN);
			            int BHistX1 = ImgBoxX2 + 190 + 256 + 10;
			            int BHistY2 = ImgBoxY2;
			            for (r=0;r<256;r++) {
			            	lineLen =  (int)(aryB[r]*dblHistFact);
			            	if ( lineLen > imgBoxHeight*0.3 ) lineLen = (int)(imgBoxHeight*0.3);
			                g.drawLine(BHistX1 + r, BHistY2, BHistX1 + r, (int)(BHistY2 - lineLen));           	
			            }
			
			            //Put clickable title over histograms
				      	g.setColor(Color.CYAN);
				    	g.setFont(fontBold12);
			    		int HistTitleX1 = ImgBoxX2 + 390;
			    		int HistTitleY1 = ImgBoxY1;
						int HistTitleX2 = HistTitleX1 + g.getFontMetrics().stringWidth("Histograms [Approx]");	
				    	if ( IV.ActHist_Flag ) {
					    	g.drawString("Histograms [Actual]", HistTitleX1, HistTitleY1);
					    	activeRegions.add(new ActiveRegion("Hist_Actual", ActiveRegion.TEXT_CMD, x, HistTitleX1-2, HistTitleY1-12, HistTitleX2, HistTitleY1+4 ));
				    	}else {
					    	g.drawString("Histograms [Approx]", HistTitleX1, HistTitleY1);
					    	activeRegions.add(new ActiveRegion("Hist_Approx", ActiveRegion.TEXT_CMD, x, HistTitleX1-2, HistTitleY1-12, HistTitleX2, HistTitleY1+4 ));
				    	}
		  			}
      			}
      			if (a == (horzAlign.size()-1)) {
      				a = 0;
		            rowNum++;	      				
      			}else {
      				a++;
      			}
  				alignX = horzAlign.get(a);
  	    	}
    	}
    	if (this.getCursor().equals(hourglassCursor)) {
    		setCursor(defaultCursor);   		
    	}
	}	
  	
	class ImageLoader extends SwingWorker<Void, ImageView> {
		private ArrayList<ImageView> imageViewCol = null;
		
		public ImageLoader(ArrayList<ImageView> imageViewCol) {
			this.imageViewCol = imageViewCol;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			int nxtID = imageViewCol.size();
		    for (int x=nxtID; x<settings.ImageAnalysisCol.size() && !isCancelled(); x++) {
		    	ImageView IV = new ImageView(settings.ImageAnalysisCol.get(x));
	    		IV.getScaledImage();
		    	publish(IV);
		    }
			return null;
		}
		
		@Override
		protected void done() {
	        setProgress(100);
			if (isCancelled()) {
				return;
			}
		}
		
		@Override
		protected void process(List<ImageView> ImgList) {
			for (ImageView IV: ImgList) {
				if (isCancelled()) {
					break;
				}
				imageViewCol.add(IV);
			}
			repaint();
		}
	};
	
	class ImageView {
		public ImageAnalysis imageAnalysis = null;
		public boolean H_S_Flag;
		public boolean Grid_Flag;
		public boolean ActHist_Flag;
		public double angle = 0;
		
		public ImageView(ImageAnalysis IA) {
			imageAnalysis = IA;
			H_S_Flag = false;
			Grid_Flag = false;
			ActHist_Flag = false;
		}
		
		public void incAngle() {
			imageAnalysis.incAngle();
			angle = imageAnalysis.rotationAngle;
		}
		
		public BufferedImage getScaledImage() {
			return imageAnalysis.getStdImg270_180();
		}
		
		public BufferedImage getH_S_ScaledImage() {
			return imageAnalysis.getHsImg270_180();
		}
		
		public HistogramArray getHist270_180() {
			return imageAnalysis.getHist270_180();
		}
	}
}
