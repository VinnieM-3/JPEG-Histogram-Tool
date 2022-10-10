import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;


public class DynoHistogram extends View {
	private static final long serialVersionUID = -4014631039782292358L;
	private final Dimension defaultPanelDimension = new Dimension(900,600);
	private final Color backGroundColor = new Color(20, 20, 20);
	private final int topMargin = 10;
	private final int bottomMargin = 10;
	private final int leftMargin = 10;
	private final int rightMargin = 10;
	private final int header = 120;
	private final int footer = 0;

	private final int smImgBoxHeight = 60;
	private final int smImgBoxWidth = 90;
	private final int leftImgBoxHeight = 180;
	private final int leftImgBoxWidth = 270;
	
	//space between small images
	private final int imgSpacer = 20; 

	private final int minRightBoxWidth = 350;
	private final int leftBoxWidth = 290;
	private final int interBoxSpace = 10; //space between left and right boxes.
	private final int minBoxesHeight = 550;

	//array of small images at top of view
	private ArrayList<SmImageView> imageViewCol = null;

	//small image loader
	private ImageLoader imgLoader;

	private int fileCollectionID = 0;

	//used to determine if user is clicking or dragging rectangle in left image.
	private ActiveRegion leftImgActRegion = new ActiveRegion("Left_Image", ActiveRegion.IMAGE_VIEW, -1);

	//used to determine if user is dragging small image slider
	private ActiveRegion sliderActRegion = new ActiveRegion("Slider", ActiveRegion.SLIDER, -1);

	//variables used to control dragging and sizing the rectangle around the left image.
	private int selectRectWidth = 0;
	private int selectRectHeight = 0;	
	private int selectMouseX = 50;
	private int selectMouseY = 50;

	private SelectedImageView selectedImgView = new SelectedImageView();

	//variables needed to track slider and nav button actions.
	private float smImgSliderLoc = 0;
	private int smImgPosition = 1;
	private boolean goButtonClicked = false;	
	private boolean navButtonClicked = false;
	private int lastNavButtonClicked = -999;
	private boolean sliderMoved = false;
	private int curMouseX = 0;
	
	private int rightImageTrueWidth = 0;
	
	private BufferedImage origFullImage = null;
	
	private View panel = null;
	private ProgressFrame pf = new ProgressFrame();

	
	public DynoHistogram() {
		super(new GridLayout());
		panel = this;
		imageViewCol = new ArrayList<SmImageView>();	
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
		smImgPosition = 1;

		if (imgLoader != null ) {
			imgLoader.cancel(true);
			while (!imgLoader.isDone()) {Thread.yield();}
		}

		if ( fileCollectionID != this.fileCollectionID  ) {  	
			imageViewCol.clear();
			this.fileCollectionID = fileCollectionID;
			selectedImgView.clearImageAnalysis();
		}else if ( (selectedImgView.imageAnalysis != null) && ( selectedImgView.imageAnalysis.Hidden ) && (settings.FilterState) ) {
			selectedImgView.clearImageAnalysis();
		}

		this.setSize(getPreferredSize());
		this.getParent().validate();

		imgLoader = new ImageLoader(imageViewCol);
		imgLoader.execute();
	}

	@Override
	public void buttonClickEvent(JScrollPane s, int target) {
		if ( target < 0 ) {
			lastNavButtonClicked = target;
			navButtonClicked = true;
		}else {
			smImgPosition = target+1;
			goButtonClicked = true;
		}
		int x1 = leftMargin + leftBoxWidth + interBoxSpace + 225;
		int x2 = this.getWidth() - x1;
		repaint(x1, 10, x2, 130 );
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		ActiveRegion actReg = null;
		int x = e.getX();
		int y = e.getY();
		for (ActiveRegion ar : activeRegions) {
			if ( ar.isInBounds(x, y) ) {
				actReg = ar;
				break;
			}
		}
		if ( actReg != null ) {
			if (actReg.Name.equals("Img_Normal")) {
				selectedImgView.setImgH_S(true);
			}else if (actReg.Name.equals("Img_H_S")) {
				selectedImgView.setImgH_S(false);
			}else if (actReg.Name.equals("Grid_On")) {
				selectedImgView.setGrid(false);
			}else if (actReg.Name.equals("Grid_Off")) {
				selectedImgView.setGrid(true);
			}else if (actReg.Name.equals("Zoom +")) {
				selectedImgView.setZoom(1);
			}else if (actReg.Name.equals("Zoom -")) {
				selectedImgView.setZoom(-1);
			}else if (actReg.Name.equals("Left_Image")) {
				selectMouseX = e.getX();
				selectMouseY = e.getY();
				selectedImgView.rightImage = null;
			}else if (actReg.Name.equals("Top_Image")) {
				setCursor(hourglassCursor);
				ImageAnalysis IA = imageViewCol.get(actReg.ID).imageAnalysis;
				origFullImage = IA.getFullSizeImage();
				selectedImgView.setImageAnalysis(IA);
			}else if (actReg.Name.equals("Hist_Full")) {
				selectedImgView.setFullHist(false);
			}else if (actReg.Name.equals("Hist_Zoom")) {
				selectedImgView.setFullHist(true);
			}else if (actReg.Name.equals("Rotate")) {
				setCursor(hourglassCursor);
				selectedImgView.incAngle();
			}else if (actReg.Name.equals("Lum-1")) {
				setCursor(hourglassCursor);
				selectedImgView.setLum(1);
			}else if (actReg.Name.equals("Lum-2")) {
				setCursor(hourglassCursor);
				selectedImgView.setLum(2);
			}else if (actReg.Name.equals("Lum-3")) {
				setCursor(hourglassCursor);
				selectedImgView.setLum(3);
			}else if (actReg.Name.equals("Lum-4")) {
				setCursor(hourglassCursor);
				selectedImgView.setLum(4);
			}else if (actReg.Name.equals("Lum-5")) {
				setCursor(hourglassCursor);
				selectedImgView.setLum(5);
			}else if (actReg.Name.equals("RGB-1")) {
				setCursor(hourglassCursor);
				selectedImgView.setRGB(1);		
			}else if (actReg.Name.equals("RGB-2")) {
				setCursor(hourglassCursor);
				selectedImgView.setRGB(2);		
			}else if (actReg.Name.equals("RGB-3")) {
				setCursor(hourglassCursor);
				selectedImgView.setRGB(3);		
			}else if (actReg.Name.equals("RGB-4")) {
				setCursor(hourglassCursor);
				selectedImgView.setRGB(4);		
			}else if (actReg.Name.equals("RGB-5")) {
				setCursor(hourglassCursor);
				selectedImgView.setRGB(5);		
			}else if (actReg.Name.equals("R-1")) {
				setCursor(hourglassCursor);
				selectedImgView.setR(1);					
			}else if (actReg.Name.equals("G-1")) {
				setCursor(hourglassCursor);
				selectedImgView.setG(1);
			}else if (actReg.Name.equals("B-1")) {
				selectedImgView.setB(1);
			}else if (actReg.Name.equals("Brightness")) {
				selectedImgView.Brightness();
			}else if (actReg.Name.equals("Saturation")) {
				selectedImgView.Saturation();
			}else if (actReg.Name.equals("Contrast")) {
				selectedImgView.Contrast();
			}else if (actReg.Name.equals("Highlights")) {
				selectedImgView.Highlights();
			}else if (actReg.Name.equals("Shadows")) {
				selectedImgView.Shadows();
			}else if (actReg.Name.equals("Sharpen/Blur")) {
				selectedImgView.SharpenBlur();
			}else if (actReg.Name.equals("Compare")) {
				selectedImgView.Compare();
			}else if (actReg.Name.equals("Crop")) {
				selectedImgView.Crop();
			}
		}
		repaint();
	}
	
	public void UndoImage() {
		selectedImgView.UndoImage();
		repaint();
	}
	
	public void newImageSaved(ImageAnalysis IA) {
		selectedImgView.setImageAnalysis(IA);
	}
	
	public ImageAnalysis getCurrentImageAnalysis() {
		return selectedImgView.imageAnalysis;
	}
	

	public void FrameSizeChange() {
		selectedImgView.rightImage = null;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//did the user drag the rectangle in the left image?
		if ( leftImgActRegion.isInBounds(e.getX(), e.getY()) ) {
			selectMouseX = e.getX();
			selectMouseY = e.getY();
			selectedImgView.rightImage = null;
			repaint();
			//did the user drag the small image slider?
		}else if (sliderActRegion.isInBounds(e.getX(), e.getY())) {
			curMouseX = e.getX();
			sliderMoved = true;
			int x1 = leftMargin + leftBoxWidth + interBoxSpace + 225;
			int x2 = this.getWidth() - x1;
			repaint(x1, 10, x2, 130 );
		}
	}

	public Dimension getPreferredSize() {
		int panelWidth = leftMargin + leftBoxWidth + interBoxSpace + minRightBoxWidth + rightMargin;
		int panelHeight = topMargin + header + minBoxesHeight + footer + bottomMargin;
		return new Dimension(panelWidth,panelHeight);
	}
	
	public void showPrintDialog() {
		if ( selectedImgView.getFullImage() != null ) {
			PrintPanel sap = new PrintPanel(selectedImgView.getFullImage());
			new PrintDialog(settings.mainFrame, "Print Image", true, sap);
		}
	}


	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;

		Rectangle visRect = getVisibleRect();

		activeRegions.clear();
		
		//highlight Active Region
		if ( curMouseRegion != null) {
			g.setColor(Color.CYAN);
			if ( (curMouseRegion.type == ActiveRegion.LUM_BOX)
					|| (curMouseRegion.type == ActiveRegion.RGB_BOX) 
					|| (curMouseRegion.type == ActiveRegion.R_BOX)
					|| (curMouseRegion.type == ActiveRegion.G_BOX)
					|| (curMouseRegion.type == ActiveRegion.B_BOX) ) {
				g2.setStroke(stroke3);
				g2.setColor(Color.CYAN);
				g2.draw3DRect(curMouseRegion.x1-1, curMouseRegion.y1-1, (curMouseRegion.x2-curMouseRegion.x1)+2, curMouseRegion.y2-curMouseRegion.y1+2, true);
				g2.setStroke(stroke1);
			}else {
				g.draw3DRect(curMouseRegion.x1, curMouseRegion.y1, (curMouseRegion.x2-curMouseRegion.x1), curMouseRegion.y2-curMouseRegion.y1, true);
			
			}
		}

		//calculate right box dimensions based on frame size
		int adjRightBoxWidth = visRect.width - leftMargin - leftBoxWidth - interBoxSpace - rightMargin;
		if (adjRightBoxWidth < minRightBoxWidth) adjRightBoxWidth = minRightBoxWidth;
		int AdjBoxesHeight = visRect.height - topMargin - header - footer - bottomMargin;
		if (AdjBoxesHeight < minBoxesHeight) AdjBoxesHeight = minBoxesHeight;

		//draw the two orange rectangles to frame the left and right areas.
		g.setColor(Color.ORANGE);
		g.drawRect(leftMargin, topMargin, leftBoxWidth, AdjBoxesHeight + header);
		g.drawRect(leftMargin + leftBoxWidth + interBoxSpace, topMargin + header, adjRightBoxWidth, AdjBoxesHeight);

		//display directory, files scanned, filter setting
		g.setColor(TextColor175);
		g.setFont(fontBold10);
		g.drawString("Folder: " + settings.getFilePath(), leftMargin + leftBoxWidth + interBoxSpace, 20);
		g.drawString("Total JPEG Images Scanned = " + imageViewCol.size() + " of " + settings.ImageAnalysisCol.size(), leftMargin + leftBoxWidth + interBoxSpace, 50);
		int numActive = settings.getNumImagesActive();
		if ( settings.FilterState == true ) {
			g.drawString("Image Filter is On: " + numActive + " images selected", leftMargin + leftBoxWidth + interBoxSpace, 80);
		}else {
			g.drawString("Image Filter is Off", leftMargin + leftBoxWidth + interBoxSpace, 80);
		}

		//draw the small-image slider bar
		g2.setColor(TextColor240);
		int physicalSliderWidth = adjRightBoxWidth - 225;
		int sliderBarX1 = leftMargin + leftBoxWidth + interBoxSpace + 225;
		int sliderBarY1 = topMargin + header - 20;
		g2.fill3DRect(sliderBarX1, sliderBarY1, physicalSliderWidth, 15,false);
		sliderActRegion.setRegion(sliderBarX1-1, sliderBarY1-1, sliderBarX1 + physicalSliderWidth + 1, sliderBarY1 + 15 + 1);
		activeRegions.add(sliderActRegion);

		//draw slider drag box
		//how many small images fit on the view at one time?
		int imgsPerPhySliderWidth = (int)Math.floor(((float)physicalSliderWidth / (float)(smImgBoxWidth + imgSpacer)));
		int TotalImgPositions = numActive;

		if (sliderMoved) {
			sliderMoved = false;
			//where is slider box now as a percentage of the total slider width?
			smImgSliderLoc = ((float)(curMouseX-(leftMargin + leftBoxWidth + interBoxSpace + 225 + 8))/(physicalSliderWidth-16));
			if ( smImgSliderLoc > 1f ) {
				smImgSliderLoc = 1f;
			}else if ( smImgSliderLoc < 0f ) {
				smImgSliderLoc = 0f;
			}
			//what left most image position should we be on now?
			smImgPosition = Math.round((float)(TotalImgPositions-1)*smImgSliderLoc)+1;
		}else if (navButtonClicked) {
			navButtonClicked = false;
			if ( lastNavButtonClicked == View.ONE_LEFT ) {
				if ( smImgPosition > 1 ) {
					smImgPosition--;
				}
			}else if ( lastNavButtonClicked == View.ONE_RIGHT ) {
				if ( smImgPosition < TotalImgPositions ) {
					smImgPosition++;
				}
			}else if ( lastNavButtonClicked == View.JUMP_LEFT ) {
				if ( smImgPosition > imgsPerPhySliderWidth ) {
					smImgPosition = smImgPosition - imgsPerPhySliderWidth;
				}else {
					smImgPosition = 1;
				}
			}else if ( lastNavButtonClicked == View.JUMP_RIGHT ) {
				if ( smImgPosition < (TotalImgPositions-imgsPerPhySliderWidth) ) {
					smImgPosition = smImgPosition + imgsPerPhySliderWidth;
				}else if ( smImgPosition < TotalImgPositions ) {
					smImgPosition++;
				}
			}else if ( lastNavButtonClicked == View.BEGINNING ) {
				smImgPosition = 1;
			}else if ( lastNavButtonClicked == View.END ) {
				smImgPosition = TotalImgPositions;
			}
			smImgSliderLoc = (float)(smImgPosition-1)/(float)(TotalImgPositions-1);
		}else if (goButtonClicked) {
			goButtonClicked = false;
			smImgSliderLoc = (float)(smImgPosition-1)/(float)(TotalImgPositions-1);
		}

		g.setColor(Color.GRAY);
		int sliderX1 = sliderBarX1 + Math.round((smImgSliderLoc*(float)(physicalSliderWidth-16))) ;
		int sliderY1 = sliderBarY1;
		int sliderX2 = sliderX1 + 16;
		int sliderY2 = sliderY1 + 15;
		g2.fill3DRect(sliderX1, sliderY1, sliderX2 - sliderX1, sliderY2 - sliderY1, true);
		activeRegions.add(new ActiveRegion("Slider-Box", ActiveRegion.SLIDER, -1, sliderX1-1, sliderY1-1, sliderX2+1, sliderY2+1));

		int imgSelected = 0;
		int imgDisplayed = 0;
		int imgBoxX1 = 0;
		int imgBoxX2 = 0;
		int imgBoxY1 = 0;
		int imgBoxY2 = 0;	  			
		int imgX1 = 0;
		int imgY1 = 0;
		g.setFont(fontBold10);
		g.setColor(TextColor175);
		for (int x=0;x<imageViewCol.size();x++) {
			SmImageView IV = imageViewCol.get(x);
			if ( !settings.FilterState || (settings.FilterState && !IV.imageAnalysis.Hidden ) )  {
				imgSelected ++;
				if (( imgSelected >= smImgPosition ) && ( imgDisplayed < imgsPerPhySliderWidth ) ) {
					//draw small image box
					imgBoxX1 = leftMargin + leftBoxWidth + interBoxSpace + 225 + imgDisplayed*(smImgBoxWidth + imgSpacer);
					imgBoxX2 = imgBoxX1 + smImgBoxWidth;
					imgBoxY1 = topMargin + 25;
					imgBoxY2 = imgBoxY1 + smImgBoxHeight;	  			

					g.setColor(Color.DARK_GRAY);
			      	g2.fill3DRect(imgBoxX1-1, imgBoxY1-1, smImgBoxWidth+2, smImgBoxHeight+2, true);

					//get small image.
					BufferedImage SmCacheImage = IV.getScaledImage();

					//center small image in box.
					imgX1 = imgBoxX1;
					if ( SmCacheImage.getWidth() < smImgBoxWidth ) {
						imgX1 = imgX1 + (int)Math.round((float)((smImgBoxWidth-SmCacheImage.getWidth()))/2.0);
					}
					imgY1 = imgBoxY1;
					if (SmCacheImage.getHeight() < smImgBoxHeight) {
						imgY1 = imgY1 + (int)Math.round((float)((smImgBoxHeight-SmCacheImage.getHeight()))/2.0);
					}

					//draw small image
					g.drawImage(SmCacheImage, imgX1, imgY1, SmCacheImage.getWidth(), SmCacheImage.getHeight(), null);
					activeRegions.add(new ActiveRegion("Top_Image", ActiveRegion.IMAGE_VIEW, x, imgBoxX1-2, imgBoxY1-2, imgBoxX2+2, imgBoxY2+2));
					g.setColor(TextColor175);
					g.drawString(IV.imageAnalysis.fileNameLessExt(), imgX1, imgBoxY1-5);
					imgDisplayed++;
				}else if (imgDisplayed == imgsPerPhySliderWidth) {
					break;
				}
			}
		}


		if ( selectedImgView.imageAnalysis != null ) {

			//Left Image
			BufferedImage cacheImage = selectedImgView.getScaledImage();

			//print image name;
			int NameY1 = topMargin + 15;
			g.setColor(TextColor175);
			g.setFont(fontBold12);
			g.drawString("Name: " + selectedImgView.imageAnalysis.fileName, leftMargin + 10, NameY1);

			//locate left image box within upper left area.
			int leftImageBoxX1 = leftMargin + 10;
			int leftImageBoxY1 = NameY1 + 5;
			int leftImageBoxY2 = leftImageBoxY1 + leftImgBoxHeight;	  			

			//center left image within left image box.
			int leftImageX1 = leftImageBoxX1;
			if ( cacheImage.getWidth() < leftImgBoxWidth ) {
				leftImageX1 = leftImageX1 + (int)Math.round((float)(leftImgBoxWidth-cacheImage.getWidth())/2.0);
			}
			int leftImageX2 = leftImageX1 + cacheImage.getWidth();

			int leftImageY1 = leftImageBoxY1;
			if (cacheImage.getHeight() < leftImgBoxHeight) {
				leftImageY1 = leftImageY1 + (int)Math.round((float)(leftImgBoxHeight-cacheImage.getHeight())/2.0);
			}
			int leftImageY2 = leftImageY1 + cacheImage.getHeight();

			leftImgActRegion.setRegion(leftImageX1,leftImageY1,leftImageX2,leftImageY2);

	      	g.setColor(Color.DARK_GRAY);
	      	g2.fill3DRect(leftImageBoxX1-1, leftImageBoxY1-1, leftImgBoxWidth+2, leftImgBoxHeight+2, true);

	      	g.setFont(fontBold11);
			g.setColor(Color.CYAN);	

			//Rotation
			int Rotation_Loc = leftImageBoxX1;
			int Rotation_Loc2 = Rotation_Loc + g.getFontMetrics().stringWidth("Rotate");
			g.drawString("Rotate", Rotation_Loc, leftImageBoxY2+14);
			activeRegions.add(new ActiveRegion("Rotate", ActiveRegion.TEXT_CMD, -1, Rotation_Loc-1, leftImageBoxY2+2, Rotation_Loc2, leftImageBoxY2+16 ));


			int H_S_Flag_Loc = Rotation_Loc2 + 10;
			int H_S_Flag_Loc2 = H_S_Flag_Loc + g.getFontMetrics().stringWidth("Clipping Off");
			if ( selectedImgView.H_S_Flag) {
				g.drawImage(selectedImgView.getH_S_ScaledImage(), 
						leftImageX1, leftImageY1, cacheImage.getWidth(), cacheImage.getHeight(), null);
				g.drawString("Clipping On", H_S_Flag_Loc, leftImageBoxY2+14);
				activeRegions.add(new ActiveRegion("Img_H_S", ActiveRegion.TEXT_CMD, -1, H_S_Flag_Loc, leftImageBoxY2+2, H_S_Flag_Loc2, leftImageBoxY2+16 ));
			}else {
				g.drawImage(cacheImage, leftImageX1, leftImageY1, cacheImage.getWidth(), cacheImage.getHeight(), null);
				g.drawString("Clipping Off", H_S_Flag_Loc, leftImageBoxY2+14);
				activeRegions.add(new ActiveRegion("Img_Normal", ActiveRegion.TEXT_CMD, -1, H_S_Flag_Loc, leftImageBoxY2+2, H_S_Flag_Loc2, leftImageBoxY2+16 ));
			}
			activeRegions.add(new ActiveRegion("Left_Image", ActiveRegion.IMAGE_VIEW, -1, leftImageX1+2, leftImageY1+2, leftImageX1 + cacheImage.getWidth()-2, leftImageY1+cacheImage.getHeight()-2 ));

			int Grid_3rds_Loc = H_S_Flag_Loc2 + 10;
			int Grid_3rds_Loc2 = Grid_3rds_Loc + g.getFontMetrics().stringWidth("Grid_Off");
			if ( selectedImgView.Grid_Flag ) {
				g.setColor(Color.WHITE);
				int Img3rdWidth = (int)((float)(cacheImage.getWidth())/3.0);
				int Img3rdHeight = (int)((float)(cacheImage.getHeight())/3.0);			      	
				g.drawLine(leftImageX1, leftImageY1+Img3rdHeight, leftImageX1 + cacheImage.getWidth(), leftImageY1+Img3rdHeight);
				g.drawLine(leftImageX1, leftImageY1+(2*Img3rdHeight), leftImageX1 + cacheImage.getWidth(), leftImageY1+(2*Img3rdHeight));
				g.drawLine(leftImageX1+Img3rdWidth, leftImageY1, leftImageX1+Img3rdWidth, leftImageY1 + cacheImage.getHeight());
				g.drawLine(leftImageX1+(2*Img3rdWidth), leftImageY1, leftImageX1+(2*Img3rdWidth), leftImageY1 + cacheImage.getHeight());	  				
				g.setColor(Color.CYAN);
				g.drawString("Grid On", Grid_3rds_Loc, leftImageBoxY2+14);
				activeRegions.add(new ActiveRegion("Grid_On", ActiveRegion.TEXT_CMD, -1, Grid_3rds_Loc, leftImageBoxY2+2, Grid_3rds_Loc2, leftImageBoxY2+16 ));
			}else {
				g.setColor(Color.CYAN);
				g.drawString("Grid Off", Grid_3rds_Loc, leftImageBoxY2+14);
				activeRegions.add(new ActiveRegion("Grid_Off", ActiveRegion.TEXT_CMD, -1, Grid_3rds_Loc, leftImageBoxY2+2, Grid_3rds_Loc2, leftImageBoxY2+16 ));
			}

			int Zoom_Loc = Grid_3rds_Loc2 + 10;
			int Zoom_Loc2 = Zoom_Loc + g.getFontMetrics().stringWidth("Zoom");			
			g.setColor(Color.CYAN);
			g.drawString("Zoom", Zoom_Loc, leftImageBoxY2+14);
			
			int Zoom_in_Loc = Zoom_Loc2 + 2;
			int Zoom_in_Loc2 = Zoom_in_Loc + g.getFontMetrics().stringWidth(" - ");			
			g.setColor(Color.CYAN);
			g.drawString(" - ", Zoom_in_Loc, leftImageBoxY2+14);				
			activeRegions.add(new ActiveRegion("Zoom -", ActiveRegion.TEXT_CMD, -1, Zoom_in_Loc, leftImageBoxY2+2, Zoom_in_Loc2, leftImageBoxY2+17 ));

			int Zoom_out_Loc = Zoom_in_Loc2 + 2;
			int Zoom_out_Loc2 = Zoom_out_Loc + g.getFontMetrics().stringWidth(" + ");			
			g.setColor(Color.CYAN);
			g.drawString(" + ", Zoom_out_Loc, leftImageBoxY2+14);					
			activeRegions.add(new ActiveRegion("Zoom +", ActiveRegion.TEXT_CMD, -1, Zoom_out_Loc, leftImageBoxY2+2, Zoom_out_Loc2, leftImageBoxY2+17 ));

			int rightImageBoxX1 = leftMargin + leftBoxWidth + interBoxSpace + 10;
			int rightImageBoxX2 = rightImageBoxX1 + adjRightBoxWidth - 20;
			int rightImageBoxY1 = topMargin + header + 10;
			int rightImageBoxY2 = rightImageBoxY1 + AdjBoxesHeight - 20;
			int rightImageBoxWidth = rightImageBoxX2 - rightImageBoxX1;
			int rightImageBoxHeight = rightImageBoxY2 - rightImageBoxY1;
			

			selectRectWidth = Math.round(((float)cacheImage.getWidth()/selectedImgView.curZoomFact));
			selectRectHeight = Math.round(((float)cacheImage.getHeight()/selectedImgView.curZoomFact));	  			
			int adjselectMouseX1 = selectMouseX - Math.round((float)selectRectWidth/2.0f);
			int adjselectMouseY1 = selectMouseY - Math.round((float)selectRectHeight/2.0f);
			int adjselectMouseX2 = selectMouseX + Math.round((float)selectRectWidth/2.0f);
			int adjselectMouseY2 = selectMouseY + Math.round((float)selectRectHeight/2.0f);
			if (adjselectMouseX1 < leftImageX1) {
				adjselectMouseX1 = leftImageX1;
				selectMouseX = adjselectMouseX1 + Math.round((float)selectRectWidth/2.0f);
			}
			if (adjselectMouseY1 < leftImageY1) {
				adjselectMouseY1 = leftImageY1;
				selectMouseY = adjselectMouseY1 + Math.round((float)selectRectHeight/2.0f);
			}
			if (adjselectMouseX2 > leftImageX2 ) {
				adjselectMouseX1 = leftImageX2 - selectRectWidth;
			}
			if (adjselectMouseY2 > leftImageY2) {
				adjselectMouseY1 = leftImageY2 - selectRectHeight;
			}

			int rightImageRelX1 = Math.round(((adjselectMouseX1 - leftImageX1)*((float)selectedImgView.imageAnalysis.imageWidth/(float)cacheImage.getWidth())));
			int rightImageRelY1 = Math.round(((adjselectMouseY1 - leftImageY1)*((float)selectedImgView.imageAnalysis.imageHeight/(float)cacheImage.getHeight())));   	  			
			int rightImageWidth = Math.round(((float)selectedImgView.imageAnalysis.imageWidth/selectedImgView.curZoomFact));
			int rightImageHeight = Math.round(((float)selectedImgView.imageAnalysis.imageHeight/selectedImgView.curZoomFact));


			if ( ( rightImageRelX1 + rightImageWidth ) > selectedImgView.imageAnalysis.imageWidth )  {
				rightImageRelX1 = selectedImgView.imageAnalysis.imageWidth - rightImageWidth;
			}
			if ( ( rightImageRelY1 + rightImageHeight ) > selectedImgView.imageAnalysis.imageHeight )  {
				rightImageRelY1 = selectedImgView.imageAnalysis.imageHeight - rightImageHeight;
			}

			if ( selectedImgView.rightImage == null ) {
				selectedImgView.rightImage = ImageAnalysis.getImageClip(selectedImgView.getFullImage(), rightImageRelX1, rightImageRelY1,rightImageWidth, rightImageHeight);
				rightImageTrueWidth = selectedImgView.rightImage.getWidth();
				selectedImgView.rightImage = ImageAnalysis.getScaledImage(selectedImgView.rightImage, rightImageBoxWidth, rightImageBoxHeight);
				if ( selectedImgView.H_S_Flag) {
					selectedImgView.rightImage = ImageAnalysis.getH_S_Image(selectedImgView.rightImage);
				}else if ( selectedImgView.lumNum > 0 ) {
					selectedImgView.rightImage = ImageAnalysis.getLumImage(selectedImgView.rightImage, selectedImgView.lumNum);
				}else if ( selectedImgView.rgbNum > 0 ) {
					selectedImgView.rightImage = ImageAnalysis.getRGBImage(selectedImgView.rightImage, selectedImgView.rgbNum);
				}else if ( selectedImgView.rNum > 0 ) {
					selectedImgView.rightImage = ImageAnalysis.getR_ChannelImage(selectedImgView.rightImage);
				}else if ( selectedImgView.gNum > 0 ) {
					selectedImgView.rightImage = ImageAnalysis.getG_ChannelImage(selectedImgView.rightImage);
				}else if ( selectedImgView.bNum > 0 ) {
					selectedImgView.rightImage = ImageAnalysis.getB_ChannelImage(selectedImgView.rightImage);
				}

			}

			imgX1 = rightImageBoxX1;
			if ( selectedImgView.rightImage.getWidth() < rightImageBoxWidth ) {
				imgX1 = imgX1 + (int)Math.round((float)((rightImageBoxWidth-selectedImgView.rightImage.getWidth()))/2.0);
			}
			imgY1 = rightImageBoxY1;
			if ( selectedImgView.rightImage.getHeight() < rightImageBoxHeight ) {
				imgY1 = imgY1 + (int)Math.round((float)((rightImageBoxHeight-selectedImgView.rightImage.getHeight()))/2.0);
			}
			g.drawImage(selectedImgView.rightImage, imgX1, imgY1, null);
			
			g.setColor(TextColor240);
			g.setFont(fontBold12);
			g.drawString("Zoom = " + Math.round(((float)selectedImgView.rightImage.getWidth()/(float)rightImageTrueWidth)*100f), leftMargin + leftBoxWidth + interBoxSpace, 125);

			g2.setStroke(stroke1);
			if ( selectedImgView.Grid_Flag ) {
				g.setColor(Color.WHITE);
				int Img3rdWidth = (int)((float)(selectedImgView.rightImage.getWidth())/3.0);
				int Img3rdHeight = (int)((float)(selectedImgView.rightImage.getHeight())/3.0);			      	
				g.drawLine(imgX1, imgY1+Img3rdHeight, imgX1 + selectedImgView.rightImage.getWidth(), imgY1+Img3rdHeight);
				g.drawLine(imgX1, imgY1+(2*Img3rdHeight), imgX1 + selectedImgView.rightImage.getWidth(), imgY1+(2*Img3rdHeight));
				g.drawLine(imgX1+Img3rdWidth, imgY1, imgX1+Img3rdWidth, imgY1 + selectedImgView.rightImage.getHeight());
				g.drawLine(imgX1+(2*Img3rdWidth), imgY1, imgX1+(2*Img3rdWidth), imgY1 + selectedImgView.rightImage.getHeight());	  				
			}


			//move left image rectangle
			g2.setStroke(stroke2);
			g2.setColor(Color.CYAN);
			g2.drawRect(adjselectMouseX1, adjselectMouseY1, selectRectWidth, selectRectHeight);


			double dblFactor = 0.25;
			int totImgPix = 0;
			double dblHistFact = 0;
			int[] aryLum;
			int[] aryRGB;
			int[] aryR;
			int[] aryG;
			int[] aryB;  
			if ( selectedImgView.ActHist_Flag ) {
				totImgPix = selectedImgView.imageAnalysis.imageHeight*selectedImgView.imageAnalysis.imageWidth;
				HistogramArray ha = selectedImgView.imageAnalysis.getHistArray();
				aryLum = ha.aryLum;
				aryRGB = ha.aryRGB;
				aryR = ha.aryR;
				aryG = ha.aryG;
				aryB = ha.aryB;
			}else {  //Clip
				totImgPix = selectedImgView.rightImage.getHeight()*selectedImgView.rightImage.getWidth();
				HistogramArray ha;
				if (selectedImgView.H_S_Flag) {
					BufferedImage clipImg = ImageAnalysis.getImageClip(selectedImgView.getFullImage(), rightImageRelX1, rightImageRelY1,rightImageWidth, rightImageHeight);
					BufferedImage scaledImg = ImageAnalysis.getScaledImage(clipImg, rightImageBoxWidth, rightImageBoxHeight);
					ha = ImageAnalysis.getHistArray(scaledImg);
				}else {
					ha = ImageAnalysis.getHistArray(selectedImgView.rightImage);
				}
				aryLum = ha.aryLum;
				aryRGB = ha.aryRGB;
				aryR = ha.aryR;
				aryG = ha.aryG;
				aryB = ha.aryB;
			}

			dblHistFact = ((70*256.0*dblFactor)/((float)(totImgPix)));


			//Set Brightness
			g.setFont(fontBold11);
			g.setColor(Color.CYAN);	
			int brightnessX1 = leftImageBoxX1 + 0;
			int brightnessY2 = leftImageBoxY2 + 30;
			int brightnessX2 = brightnessX1 + g.getFontMetrics().stringWidth("Brightness");		
			g.drawString("Brightness", brightnessX1, brightnessY2+14);
			activeRegions.add(new ActiveRegion("Brightness", ActiveRegion.TEXT_CMD, -1, brightnessX1, brightnessY2+2, brightnessX2, brightnessY2+16 ));


			//Set Hightlights
			g.setFont(fontBold11);
			g.setColor(Color.CYAN);	
			int hightlightX1 = brightnessX2 + 10;
			int hightlightY2 = leftImageBoxY2 + 30;
			int hightlightX2 = hightlightX1 + g.getFontMetrics().stringWidth("Highlights");					
			g.drawString("Highlights", hightlightX1, hightlightY2+14);
			activeRegions.add(new ActiveRegion("Highlights", ActiveRegion.TEXT_CMD, -1, hightlightX1, hightlightY2+2, hightlightX2, hightlightY2+16 ));
			

			//Set Saturation
			g.setFont(fontBold11);
			g.setColor(Color.CYAN);	
			int saturationX1 = hightlightX2 + 10;
			int saturationY2 = leftImageBoxY2 + 30;
			int saturationX2 = saturationX1 + g.getFontMetrics().stringWidth("Saturation");					
			g.drawString("Saturation", saturationX1, saturationY2+14);
			activeRegions.add(new ActiveRegion("Saturation", ActiveRegion.TEXT_CMD, -1, saturationX1, saturationY2+2, saturationX2, saturationY2+16 ));
			

			//Set Crop
			g.setFont(fontBold11);
			g2.setColor(Color.CYAN);
			int cropX1 = saturationX2 + 10;
			int cropY2 = leftImageBoxY2 + 30;
			int cropX2 = cropX1 + g.getFontMetrics().stringWidth("Crop");					
			g.drawString("Crop", cropX1, cropY2+14);
			activeRegions.add(new ActiveRegion("Crop", ActiveRegion.TEXT_CMD, -1, cropX1, cropY2+2, cropX2, cropY2+16));
			
			
			//Set Contrast
			g.setFont(fontBold11);
			g.setColor(Color.CYAN);	
			int contrastX1 = leftImageBoxX1 + 0;
			int contrastY2 = leftImageBoxY2 + 50;
			int contrastX2 = contrastX1 + g.getFontMetrics().stringWidth("Contrast");	
			g.drawString("Contrast", contrastX1, contrastY2+14);
			activeRegions.add(new ActiveRegion("Contrast", ActiveRegion.TEXT_CMD, -1, contrastX1, contrastY2+2, contrastX2, contrastY2+16 ));
			

			//Set Shadows
			g.setFont(fontBold11);
			g.setColor(Color.CYAN);	
			int shadowsX1 = contrastX2 + 10;
			int shadowsY2 = leftImageBoxY2 + 50;
			int shadowsX2 = shadowsX1 + g.getFontMetrics().stringWidth("Shadows");				
			g.drawString("Shadows", shadowsX1, shadowsY2+14);
			activeRegions.add(new ActiveRegion("Shadows", ActiveRegion.TEXT_CMD, -1, shadowsX1, shadowsY2+2, shadowsX2, shadowsY2+16 ));
			
	
			//Set SharpenBlur
			g.setFont(fontBold11);
			g.setColor(Color.CYAN);	
			int sharpenX1 = shadowsX2 + 10;
			int sharpenY2 = leftImageBoxY2 + 50;
			int sharpenX2 = sharpenX1 + g.getFontMetrics().stringWidth("Sharp/Blur");				
			g.drawString("Sharp/Blur", sharpenX1, sharpenY2+14);
			activeRegions.add(new ActiveRegion("Sharpen/Blur", ActiveRegion.TEXT_CMD, -1, sharpenX1, sharpenY2+2, sharpenX2, sharpenY2+16 ));

			//Set Compare
			g.setFont(fontBold11);
			g2.setColor(Color.CYAN);
			int compareX1 = sharpenX2 + 10;
			int compareY2 = leftImageBoxY2 + 50;
			int compareX2 = compareX1 + g.getFontMetrics().stringWidth("Compare");				
			g.drawString("Compare", compareX1, compareY2+14);
			activeRegions.add(new ActiveRegion("Compare", ActiveRegion.TEXT_CMD, -1, compareX1, compareY2+2, compareX2, compareY2+16));
			
			
			//Put clickable title over histograms
			g.setColor(Color.CYAN);
			g.setFont(fontBold11);
			int HistTitleX1 = leftImageBoxX1 + 75;
			int HistTitleY1 = leftImageBoxY2 + 95;
			int HistTitleX2 = HistTitleX1 + g.getFontMetrics().stringWidth("Histograms [Actual]");	
			if ( selectedImgView.ActHist_Flag ) {
				g.drawString("Histograms [Actual]", HistTitleX1, HistTitleY1);
				activeRegions.add(new ActiveRegion("Hist_Full", ActiveRegion.TEXT_CMD, -1, HistTitleX1-2, HistTitleY1-12, HistTitleX2, HistTitleY1+4 ));
			}else {
				g.drawString("Histograms [Zoom]", HistTitleX1, HistTitleY1);
				activeRegions.add(new ActiveRegion("Hist_Zoom", ActiveRegion.TEXT_CMD, -1, HistTitleX1-2, HistTitleY1-12, HistTitleX2, HistTitleY1+4 ));
			}
			
			//Luminance Histogram
			int LumHistX1 = leftImageBoxX1 + 7;
			int LumHistY2 = HistTitleY1 + 70;
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
			
			//draw interactive rectangles
			if ( selectedImgView.ActHist_Flag == true ) { 
				g2.setColor(Color.CYAN);
				if (selectedImgView.lumNum == 1 ) g2.setStroke(stroke3); else g2.setStroke(stroke1);
				g2.drawRect(LumHistX1-1, LumHistY2-60, 52, 60);
				activeRegions.add(new ActiveRegion("Lum-1", ActiveRegion.LUM_BOX, -1, LumHistX1-1, LumHistY2-60, LumHistX1+51, LumHistY2 ));
				for ( int x=1;x<4;x++ ) {
					if (selectedImgView.lumNum == (x+1) ) g2.setStroke(stroke3); else g2.setStroke(stroke1);
					g2.drawRect(LumHistX1+(x*51), LumHistY2-60, 51, 60);
					activeRegions.add(new ActiveRegion("Lum-" + (x+1), ActiveRegion.LUM_BOX, -1, LumHistX1+(x*51), LumHistY2-60, LumHistX1+(x*51)+51, LumHistY2 ));
				}
				if (selectedImgView.lumNum == 5 ) g2.setStroke(stroke3); else g2.setStroke(stroke1);
				g2.drawRect(LumHistX1+(4*51), LumHistY2-60, 52, 60);
				activeRegions.add(new ActiveRegion("Lum-5", ActiveRegion.LUM_BOX, -1, LumHistX1+(4*51), LumHistY2-60, LumHistX1+(4*51)+52, LumHistY2 ));
			}

			//RGB Histogram
			int RGBHistX1 = leftImageBoxX1 + 7;
			int RGBHistY2 = LumHistY2 + 75;
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
			
			//draw interactive rectangles
			if ( selectedImgView.ActHist_Flag == true ) { 
				g2.setColor(Color.CYAN);
				if (selectedImgView.rgbNum == 1 ) g2.setStroke(stroke3); else g2.setStroke(stroke1);
				g2.drawRect(RGBHistX1-1, RGBHistY2-60, 52, 60);
				activeRegions.add(new ActiveRegion("RGB-1", ActiveRegion.RGB_BOX, -1, RGBHistX1-1, RGBHistY2-60, RGBHistX1+51, RGBHistY2 ));
				for ( int x=1;x<4;x++ ) {
					if (selectedImgView.rgbNum == (x+1) ) g2.setStroke(stroke3); else g2.setStroke(stroke1);
					g2.drawRect(RGBHistX1+(x*51), RGBHistY2-60, 51, 60);
					activeRegions.add(new ActiveRegion("RGB-" + (x+1), ActiveRegion.RGB_BOX, -1, RGBHistX1+(x*51), RGBHistY2-60, RGBHistX1+(x*51)+51, RGBHistY2 ));
				}
				if (selectedImgView.rgbNum == 5 ) g2.setStroke(stroke3); else g2.setStroke(stroke1);
				g2.drawRect(RGBHistX1+(4*51), RGBHistY2-60, 52, 60);
				activeRegions.add(new ActiveRegion("RGB-5", ActiveRegion.RGB_BOX, -1, RGBHistX1+(4*51), RGBHistY2-60, RGBHistX1+(4*51)+52, RGBHistY2 ));
			}
			
			//Red Histogram
			g.setColor(Color.RED);
			g2.setStroke(stroke1);
			int RHistX1 = leftImageBoxX1 + 7;
			int RHistY2 = RGBHistY2 + 75;   
			for (int r=0;r<256;r++) {
				int lineLen =  (int)(aryR[r]*dblHistFact);
				if ( lineLen > 60 ) lineLen = 60;
				g.drawLine(RHistX1 + r, RHistY2, RHistX1 + r, (int)(RHistY2 - lineLen));           	
			}
			
			//draw interactive rectangles
			if ( selectedImgView.ActHist_Flag == true ) { 
				g2.setColor(Color.CYAN);
				if (selectedImgView.rNum == 1 ) g2.setStroke(stroke3); else g2.setStroke(stroke1);
				g2.drawRect(RHistX1-1, RHistY2-60, 257, 60);
				activeRegions.add(new ActiveRegion("R-1", ActiveRegion.R_BOX, -1, RHistX1-1, RHistY2-60, RHistX1+256, RHistY2 ));
			}
			

			//Green Histogram
			g.setColor(Color.GREEN);
			g2.setStroke(stroke1);
			int GHistX1 = leftImageBoxX1 + 7;
			int GHistY2 = RHistY2 + 70;   
			for (int r=0;r<256;r++) {
				int lineLen =  (int)(aryG[r]*dblHistFact);
				if ( lineLen > 60 ) lineLen = 60;
				g.drawLine(GHistX1 + r, GHistY2, GHistX1 + r, (int)(GHistY2 - lineLen));           	
			}
			
			//draw interactive rectangles
			if ( selectedImgView.ActHist_Flag == true ) { 
				g2.setColor(Color.CYAN);
				if (selectedImgView.gNum == 1 ) g2.setStroke(stroke3); else g2.setStroke(stroke1);
				g2.drawRect(GHistX1-1, GHistY2-60, 257, 60);
				activeRegions.add(new ActiveRegion("G-1", ActiveRegion.G_BOX, -1, GHistX1-1, GHistY2-60, GHistX1+256, GHistY2 ));
			}

			//Blue Histogram	            
			g.setColor(Color.CYAN);
			g2.setStroke(stroke1);
			int BHistX1 = leftImageBoxX1 + 7;
			int BHistY2 = GHistY2 + 70;
			for (int r=0;r<256;r++) {
				int lineLen =  (int)(aryB[r]*dblHistFact);
				if ( lineLen > 60 ) lineLen = 60;
				g.drawLine(BHistX1 + r, BHistY2, BHistX1 + r, (int)(BHistY2 - lineLen));           	
			}
			
			//draw interactive rectangles
			if ( selectedImgView.ActHist_Flag == true ) { 
				g2.setColor(Color.CYAN);
				if (selectedImgView.bNum == 1 ) g2.setStroke(stroke3); else g2.setStroke(stroke1);
				g2.drawRect(BHistX1-1, BHistY2-60, 257, 60);
				activeRegions.add(new ActiveRegion("B-1", ActiveRegion.B_BOX, -1, BHistX1-1, BHistY2-60, BHistX1+256, BHistY2 ));
			}

			pf.dispose();
			g2.dispose();
			g.dispose();
		}
    	if (this.getCursor().equals(hourglassCursor)) {
    		setCursor(defaultCursor);   		
    	}
	}
	
	class ImageLoader extends SwingWorker<Void, SmImageView> {
		private ArrayList<SmImageView> imageViewCol = null;

		public ImageLoader(ArrayList<SmImageView> imageViewCol) {
			this.imageViewCol = imageViewCol;
		}

		@Override
		protected Void doInBackground() throws Exception {
			int nxtID = imageViewCol.size();
			for (int x=nxtID; x<settings.ImageAnalysisCol.size() && !isCancelled(); x++) {
				ImageAnalysis IA = settings.ImageAnalysisCol.get(x);
				SmImageView IV = new SmImageView(IA);
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
		protected void process(List<SmImageView> ImgList) {
			for (SmImageView IV: ImgList) {
				if (isCancelled()) {
					break;
				}
				imageViewCol.add(IV);
			}
			repaint();
		}
	};

	class SelectedImageView {
		private BufferedImage fullImage;
		public ImageAnalysis undoImageAnalysis = null;
		public ImageAnalysis imageAnalysis = null;
		public boolean H_S_Flag;
		public boolean Grid_Flag;
		public boolean ActHist_Flag;
		public int lumNum = 0;
		public int rgbNum = 0;
		public int rNum = 0;
		public int gNum = 0;
		public int bNum = 0;
		public BufferedImage rightImage = null;
		public int zoomValue = 1;
		public float curZoomFact = 1.0f;
		private AdjustmentDialog myDialog = null;
		
		
		public SelectedImageView() {
			H_S_Flag = false;
			Grid_Flag = false;
			ActHist_Flag = false;
		}
		
		public void UndoImage() {
			if ( undoImageAnalysis != null ) {
				imageAnalysis = undoImageAnalysis;
				fullImage = null;
				rightImage = null;
			}
		}
		
		public void clearImageAnalysis() {
			fullImage = null;
			rightImage = null;
			undoImageAnalysis = null;
			imageAnalysis = null;
			curZoomFact = 1.0f;
		}
		
		public void setLum(int segment) {
			fullImage = null;
			rightImage = null;
			if (lumNum != segment) { 
				lumNum = segment;
				rgbNum = 0;	
				rNum = 0;
				gNum = 0;	
				bNum = 0;
				H_S_Flag = false;
				Grid_Flag = false;
				ActHist_Flag = true;
			}else lumNum = 0;
		}
		
		public void setRGB(int segment) {
			fullImage = null;
			rightImage = null;
			if (rgbNum != segment) { 
				lumNum = 0;
				rgbNum = segment;
				rNum = 0;
				gNum = 0;	
				bNum = 0;
				H_S_Flag = false;
				Grid_Flag = false;
				ActHist_Flag = true;
			}else rgbNum = 0;
		}
		
		public void setR(int segment) {
			fullImage = null;
			rightImage = null;
			if (rNum != segment) { 
				rgbNum = 0;
				lumNum = 0;
				rNum = segment;
				gNum = 0;	
				bNum = 0;
				H_S_Flag = false;
				Grid_Flag = false;
				ActHist_Flag = true;
			}else rNum = 0;
		}
		
		public void setG(int segment) {
			fullImage = null;
			rightImage = null;
			if (gNum != segment) { 
				rgbNum = 0;
				lumNum = 0;
				rNum = 0;
				gNum = segment;	
				bNum = 0;
				H_S_Flag = false;
				Grid_Flag = false;
				ActHist_Flag = true;
			}else gNum = 0;
		}
		
		public void setB(int segment) {
			fullImage = null;
			rightImage = null;
			if (bNum != segment) { 
				rgbNum = 0;
				lumNum = 0;
				rNum = 0;
				gNum = 0;	
				bNum = segment;
				H_S_Flag = false;
				Grid_Flag = false;
				ActHist_Flag = true;
			}else bNum = 0;
		}
		
		public void setImageAnalysis(ImageAnalysis IA) {
			imageAnalysis = IA;
			fullImage = null;
			rightImage = null;
			undoImageAnalysis = null;
			lumNum = 0;
			rgbNum = 0;	
			rNum = 0;
			gNum = 0;
			bNum = 0;
			curZoomFact = 1.0f;
			Grid_Flag = false;
			H_S_Flag = false;
			ActHist_Flag = false;
		}
		
		public void incAngle() {
			imageAnalysis.incAngle();
			fullImage = null;
			rightImage = null;
		}
		
		public void setImgH_S(boolean turnON) {
			if ( turnON ) {
				H_S_Flag = true;
			}else {
				H_S_Flag = false;
			}
			rightImage = null;
			lumNum = 0;
			rgbNum = 0;
			rNum = 0;
			gNum = 0;	
			bNum = 0;
		}
		
		public void setGrid(boolean turnON) {
			if ( turnON ) {
				Grid_Flag = true;
			}else {
				Grid_Flag = false;
			}
			rightImage = null;
			lumNum = 0;
			rgbNum = 0;
			rNum = 0;
			gNum = 0;	
			bNum = 0;
		}
		
		public void setFullHist(boolean turnON) {
			ActHist_Flag = turnON;
			lumNum = 0;
			rgbNum = 0;
			rNum = 0;
			gNum = 0;	
			bNum = 0;
			rightImage = null;
		}
		
		public void setZoom(int direction) {
			if ( direction > 0 ) {
				curZoomFact = curZoomFact + curZoomFact*0.15f;
			}else if ( direction < 0 ) {
				curZoomFact = curZoomFact - curZoomFact*0.15f;
			}
			if (curZoomFact < 1.0f ) curZoomFact = 1.0f;
			
			int minRightImageSize = 25; //pixels
			int imgRightWidth = Math.round(((float)getFullImage().getWidth()/selectedImgView.curZoomFact));
			int imgRightHeight = Math.round(((float)getFullImage().getHeight()/selectedImgView.curZoomFact));
			if ( imgRightWidth <= imgRightHeight ) {
				if ( imgRightWidth < minRightImageSize) {
					curZoomFact = Math.round(((float)getFullImage().getWidth()/minRightImageSize));
				}
			}else {
				if ( imgRightHeight < minRightImageSize) {
					curZoomFact = Math.round(((float)getFullImage().getHeight()/minRightImageSize));
				}
				
			}
			rightImage = null;
		}
		

		public void Brightness() {
			BrightnessPanel bap = new BrightnessPanel(getFullImage());
			BrightnessDialog myDialog = new BrightnessDialog(settings.mainFrame, "Brightness Adjustment", true, bap);
			if( ( myDialog.getState() ) && ( bap.getSetting() != 0.0f ) ){
				setCursor(hourglassCursor);
				Point pnt = panel.getLocationOnScreen();
				pnt.x = pnt.x + Math.round((float)panel.getWidth()/2f);
				pnt.y = pnt.y + Math.round((float)panel.getHeight()/2f);
				pf.showProgress(pnt);
				BufferedImage img = null;
				if (bap.getMode() == 1 ) {
					img = ImageAnalysis.BrightnessMode1(getFullImage(), bap.getSetting(), pf);
				}else if (bap.getMode() == 2 ) {
					img = ImageAnalysis.BrightnessMode2(getFullImage(), bap.getSetting(), pf);
				}
				undoImageAnalysis = imageAnalysis;
				imageAnalysis = new ImageAnalysis(img, this.imageAnalysis.filePath, this.imageAnalysis.fileName);
				fullImage = null;
				rightImage = null;
			}
		}
		
		public void Highlights() {		
			HighlightsPanel hap = new HighlightsPanel(getFullImage());
			myDialog = new AdjustmentDialog(settings.mainFrame, "Highlight Adjustment", true, hap);
			if ( myDialog.getState() ) {
				setCursor(hourglassCursor);
				Point pnt = panel.getLocationOnScreen();
				pnt.x = pnt.x + Math.round((float)panel.getWidth()/2f);
				pnt.y = pnt.y + Math.round((float)panel.getHeight()/2f);
				pf.showProgress(pnt);
				undoImageAnalysis = imageAnalysis;
				BufferedImage img = ImageAnalysis.Hightlights(getFullImage(), hap.getSetting(), pf);
				imageAnalysis = new ImageAnalysis(img, this.imageAnalysis.filePath, this.imageAnalysis.fileName);
				fullImage = null;
				getFullImage();
				rightImage = null;
			}
		}

		public void Shadows() {
			ShadowsPanel sap = new ShadowsPanel(getFullImage());
			myDialog = new AdjustmentDialog(settings.mainFrame, "Shadow Adjustment", true, sap);
			if ( myDialog.getState() ) {
				setCursor(hourglassCursor);
				Point pnt = panel.getLocationOnScreen();
				pnt.x = pnt.x + Math.round((float)panel.getWidth()/2f);
				pnt.y = pnt.y + Math.round((float)panel.getHeight()/2f);
				pf.showProgress(pnt);
				undoImageAnalysis = imageAnalysis;
				BufferedImage img = ImageAnalysis.Shadows(getFullImage(), sap.getSetting(),pf);
				imageAnalysis = new ImageAnalysis(img, this.imageAnalysis.filePath, this.imageAnalysis.fileName);
				fullImage = null;
				rightImage = null;
			}
		}

		public void Contrast() {
			ContrastPanel cap = new ContrastPanel(getFullImage());
			myDialog = new AdjustmentDialog(settings.mainFrame, "Contrast Adjustment", true, cap);
			if( ( myDialog.getState() ) && ( cap.getSetting() != 0.0f ) ){
				setCursor(hourglassCursor);
				Point pnt = panel.getLocationOnScreen();
				pnt.x = pnt.x + Math.round((float)panel.getWidth()/2f);
				pnt.y = pnt.y + Math.round((float)panel.getHeight()/2f);
				pf.showProgress(pnt);
				undoImageAnalysis = imageAnalysis;
				BufferedImage img = ImageAnalysis.Contrast(getFullImage(), cap.getSetting(),pf);
				imageAnalysis = new ImageAnalysis(img, this.imageAnalysis.filePath, this.imageAnalysis.fileName);
				fullImage = null;
				rightImage = null;
			}
		}

		public void Saturation() {
			SaturationPanel sap = new SaturationPanel(getFullImage());
			myDialog = new AdjustmentDialog(settings.mainFrame, "Saturation Adjustment", true, sap);
			if( ( myDialog.getState() ) && ( sap.getSetting() != 0.0f ) ){
				setCursor(hourglassCursor);
				Point pnt = panel.getLocationOnScreen();
				pnt.x = pnt.x + Math.round((float)panel.getWidth()/2f);
				pnt.y = pnt.y + Math.round((float)panel.getHeight()/2f);
				pf.showProgress(pnt);
				undoImageAnalysis = imageAnalysis;
				BufferedImage img = ImageAnalysis.Saturation(getFullImage(), sap.getSetting(),pf);
				imageAnalysis = new ImageAnalysis(img, this.imageAnalysis.filePath, this.imageAnalysis.fileName);
				fullImage = null;
				rightImage = null;
			}
		}
		
		public void SharpenBlur() {
			SharpenBlurPanel sap = new SharpenBlurPanel(getFullImage());
			myDialog = new AdjustmentDialog(settings.mainFrame, "Sharpen/Blur Adjustment", true, sap);
			if( ( myDialog.getState() ) && ( sap.getSetting() != 0.0f ) ){
				setCursor(hourglassCursor);
				Point pnt = panel.getLocationOnScreen();
				pnt.x = pnt.x + Math.round((float)panel.getWidth()/2f);
				pnt.y = pnt.y + Math.round((float)panel.getHeight()/2f);
				pf.showProgress(pnt);
				undoImageAnalysis = imageAnalysis;
				BufferedImage img = ImageAnalysis.SharpenBlur(getFullImage(), sap.getSetting(),pf);
				imageAnalysis = new ImageAnalysis(img, this.imageAnalysis.filePath, this.imageAnalysis.fileName);
				fullImage = null;
				rightImage = null;
			}
		}

		public void Crop() {
			if ( (getFullImage().getWidth() > 50) && (getFullImage().getHeight() > 50) ) {
				CropPanel sap = new CropPanel(getFullImage());
				CropDialog myDialog = new CropDialog(settings.mainFrame, "Crop Image", true, sap);
				if( ( myDialog.getState() )) {
					setCursor(hourglassCursor);
					Rectangle rec = sap.getSetting();
					undoImageAnalysis = imageAnalysis;
					BufferedImage img = ImageAnalysis.getImageClip(getFullImage(), rec.x, rec.y, rec.width, rec.height);
					imageAnalysis = new ImageAnalysis(img, this.imageAnalysis.filePath, this.imageAnalysis.fileName);
					fullImage = null;
					rightImage = null;
				}
			}
		}

		public void Compare() {
			new CompareDialog(settings.mainFrame, "Compare Orginal vs. Modified", true, 
					new ComparePanel(origFullImage, getFullImage()));
		}
		
		public BufferedImage getScaledImage() {
			return imageAnalysis.getStdImg270_180();
		}

		public BufferedImage getH_S_ScaledImage() {
			return imageAnalysis.getHsImg270_180();
		}

		public BufferedImage getFullImage() {
			if ( fullImage == null ) {
				setCursor(hourglassCursor);
				if ( imageAnalysis != null ) {
					fullImage = imageAnalysis.getFullSizeImage();					
				}
			}
			return fullImage;
		}
	}
	
	class SmImageView {
		public ImageAnalysis imageAnalysis = null;

		public SmImageView(ImageAnalysis IA) {
			imageAnalysis = IA;
		}
	
		public BufferedImage getScaledImage() {
			return imageAnalysis.getStdImg90_60();
		}
	}
}
