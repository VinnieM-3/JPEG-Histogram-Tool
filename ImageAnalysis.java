import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ShortLookupTable;
import java.awt.image.LookupOp;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;


public class ImageAnalysis {
	private final static double angle0 = 0;
	private final static double angleCW = Math.PI/2.0;
	private final static double angleCCW = -Math.PI/2.0;
	
	private BufferedImage stdImg270_180;
	private BufferedImage stdImg90_60;
	private BufferedImage hsImg270_180;
	
	public String filePath = null;
	public String fileName = null;	

	private boolean first = true;
	public int imageWidth = 0;
	public int imageHeight = 0;
	
	public boolean Hidden = true;
	
	public double rotationAngle = angle0;
	
	private ImageEXIF imageEXIF = null;
	
	private HistogramArray Hist_Actual = null;
	private HistogramArray Hist270_180 = null;
	
	private BufferedImage seedImage = null;
	private Component sComponent = new Component() {
		private static final long serialVersionUID = -702856035097921567L;
	};

	
	public ImageAnalysis(String filePath, String fileName) {
		this.filePath = filePath;
		this.fileName = fileName;
 	}
	
	public ImageAnalysis(BufferedImage seedImage, String filePath, String fileName) {
		this.seedImage = seedImage;
		this.filePath = filePath;
		this.fileName = fileName;
	}
	
	public void loadNewImage(BufferedImage seedImage, String filePath, String fileName) {
		this.seedImage = seedImage;
		this.filePath = filePath;
		this.fileName = fileName;
		stdImg270_180 = null;
		stdImg90_60 = null;
		hsImg270_180 = null;
		imageEXIF = null;
		Hist_Actual = null;
		Hist270_180 = null;
		imageWidth = 0;
		imageHeight = 0;
		first = true;
		rotationAngle = angle0;
	}
	
	//returns just the file name without the .jpg or .jpeg extension
	public String fileNameLessExt() {
		String ret = fileName;
		int i = fileName.lastIndexOf('.');
		if (i > 0 &&  i < fileName.length() - 1) {
			ret =  fileName.substring(0,i);
		}
		return ret;
	}
	
	//increment the angle.  +90, -90, and 0.  No 180 necessary.
	public double incAngle() {
		if ( rotationAngle == angle0 ) {
			rotationAngle = angleCW;
			int tmpWidth = imageWidth; //swap dimensions
			imageWidth = imageHeight;
			imageHeight = tmpWidth;
		}else if ( rotationAngle == angleCW ) {
			rotationAngle = angleCCW;  //dimensions still swapped
		}else if ( rotationAngle == angleCCW ) {
			rotationAngle = angle0;
			int tmpWidth = imageWidth;
			imageWidth = imageHeight; //dimensions return to normal
			imageHeight = tmpWidth;
		}
		stdImg270_180 = null;
		stdImg90_60 = null;
		hsImg270_180 = null;
		return rotationAngle;
	}
	
	public BufferedImage getStdImg270_180() {
		if ( stdImg270_180 == null ) {
			loadStdImages();
		}
		return stdImg270_180;
	}
	
	public BufferedImage getStdImg90_60() {
		if ( stdImg90_60 == null ) {
			loadStdImages();
		}
		return stdImg90_60;
	}
	
	private void loadStdImages() {
		BufferedImage fullImg = getFullSizeImage();
		if ( stdImg270_180 == null ) {
			stdImg270_180 = getScaledImage(fullImg, 270, 180);
		}
		if ( stdImg90_60 == null ) {
			stdImg90_60 = getScaledImage(fullImg, 90, 60);
		}
	}
	
	public BufferedImage getHsImg270_180() {
		if ( hsImg270_180 == null ) {
			hsImg270_180 = getH_S_Image(getStdImg270_180());
		}
		return hsImg270_180;
	}
	
	public BufferedImage getLumImage(int lumZone) {
		return getLumImage(getFullSizeImage(), lumZone);
	}
	
	
	public static BufferedImage getLumImage(BufferedImage srcImage, int lumZone) {
		BufferedImage LumImageBack = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = LumImageBack.createGraphics();
		GradientPaint gp = new GradientPaint(0, 0, Color.DARK_GRAY, 1, 1, Color.black, true);
		g2.setPaint(gp);
		g2.fillRect(0, 0, srcImage.getWidth(), srcImage.getHeight());
		g2.dispose();
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		int lowLimit = 0;
		int highLimit = 0;
		switch (lumZone) {
		case 1: 
			lowLimit = -1;
			highLimit = 51;
			break;
		case 2:
			lowLimit = 51;
			highLimit = 102;
			break;
		case 3:
			lowLimit = 102;
			highLimit = 153;
			break;
		case 4:
			lowLimit = 153;
			highLimit = 204;
			break;
		case 5:
			lowLimit = 204;
			highLimit = 255;
			break;
		}
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Color c = new Color(srcImage.getRGB(x,y));
				int lumVal = (int)(0.59f*(float)c.getGreen() + 0.30f*(float)c.getRed() + 0.11f*(float)c.getBlue());
				if ( ( lumVal > lowLimit ) && (lumVal <= highLimit ) ) {
					LumImageBack.setRGB(x, y, c.getRGB());
				}
			}
		}
		return LumImageBack;
	}
	
	public static BufferedImage getRGBImage(BufferedImage srcImage, int rgbZone) {
		BufferedImage RGBImageBack = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = RGBImageBack.createGraphics();
		GradientPaint gp = new GradientPaint(0, 0, Color.DARK_GRAY, 1, 1, Color.black, true);
		g2.setPaint(gp);
		g2.fillRect(0, 0, srcImage.getWidth(), srcImage.getHeight());
		g2.dispose();
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		int lowLimit = 0;
		int highLimit = 0;
		switch (rgbZone) {
		case 1: 
			lowLimit = -1;
			highLimit = 51;
			break;
		case 2:
			lowLimit = 51;
			highLimit = 102;
			break;
		case 3:
			lowLimit = 102;
			highLimit = 153;
			break;
		case 4:
			lowLimit = 153;
			highLimit = 204;
			break;
		case 5:
			lowLimit = 204;
			highLimit = 255;
			break;
		}
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Color c = new Color(srcImage.getRGB(x,y));
				int rgbVal = (int)(((float)c.getGreen() + (float)c.getRed() + (float)c.getBlue())/3.0f);
				if ( ( rgbVal > lowLimit ) && (rgbVal <= highLimit ) ) {
					RGBImageBack.setRGB(x, y, c.getRGB());
				}
			}
		}
		return RGBImageBack;
	}
	
	public static BufferedImage WhiteBalance(BufferedImage srcImage, Color pix, ProgressFrame pf) {
		BufferedImage GImageBack = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Color c = new Color(srcImage.getRGB(x,y));
				float[] f = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(), null);
				int newCol = Color.HSBtoRGB(f[0], f[1], f[2]);
				GImageBack.setRGB(x, y, newCol);
			}
			if ( pf != null ) {
				pf.setProgress(Math.round(((float)x*100f)/(float)width));
			}
		}
		return GImageBack;
	}

	
	public static BufferedImage BrightnessMode1(BufferedImage srcImage, float adj, ProgressFrame pf) {
		short[] bright = new short[256];
		for (int j=0; j<256; j++) {
			short pixelValue = (short) (j + adj*255);
			if (pixelValue > 255)
				pixelValue = 255;
			else if (pixelValue < 0)
				pixelValue = 0;
			bright[j] = pixelValue;
		}
		ShortLookupTable blut = new ShortLookupTable(0, bright);
		BufferedImageOp lop = new LookupOp(blut, null);
		BufferedImage GImageBack = lop.filter(srcImage, null);
		Graphics2D g2 = (Graphics2D)GImageBack.createGraphics();
		g2.drawImage(GImageBack, 0, 0, null);
		g2.dispose();
		return GImageBack;
	}

	
	public static BufferedImage BrightnessMode2(BufferedImage srcImage, float adj, ProgressFrame pf) {
		BufferedImage GImageBack = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		float maxB = 0;
		float minB = 255;
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Color c = new Color(srcImage.getRGB(x,y));
				float[] f = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(), null);
				if ( f[2] > maxB ) maxB = f[2];
				if ( f[2] < minB ) minB = f[2];
			}
			if ( pf != null ) {
				pf.setProgress(Math.round(((float)x*10f)/(float)width));
			}
		}
		if ( adj > 0 ) {
			if ( adj < (1f-maxB) ) {
				for (int x=0;x<width;x++) {
					for (int y=0;y<height;y++) {
						Color c = new Color(srcImage.getRGB(x,y));
						float[] f = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(), null);
						f[2] = f[2] + adj;
						int newCol = Color.HSBtoRGB(f[0], f[1], f[2]);
						GImageBack.setRGB(x, y, newCol);
					}
					if ( pf != null ) {
						pf.setProgress(Math.round(((float)x*90f)/(float)width)+10);
					}
				}
			}else {
				for (int x=0;x<width;x++) {
					for (int y=0;y<height;y++) {
						Color c = new Color(srcImage.getRGB(x,y));
						float[] f = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(), null);
						f[2] = f[2] + (1f-maxB);
						int newCol = Color.HSBtoRGB(f[0], f[1], f[2]);
						GImageBack.setRGB(x, y, newCol);
					}
					if ( pf != null ) {
						pf.setProgress(Math.round(((float)x*45f)/(float)width)+10);
					}
				}
				adj = adj - (1f-maxB);
				for (int x=0;x<width;x++) {
					for (int y=0;y<height;y++) {
						Color c = new Color(GImageBack.getRGB(x,y));
						float[] f = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(), null);
						f[2] = f[2]*(1-adj) + adj;
						int newCol = Color.HSBtoRGB(f[0], f[1], f[2]);
						GImageBack.setRGB(x, y, newCol);
					}
					if ( pf != null ) {
						pf.setProgress(Math.round(((float)x*45f)/(float)width)+55);
					}
				}
			}
		}else if ( adj < 0 ) {
			adj = adj*-1f;
			if ( adj < minB ) {
				for (int x=0;x<width;x++) {
					for (int y=0;y<height;y++) {
						Color c = new Color(srcImage.getRGB(x,y));
						float[] f = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(), null);
						f[2] = f[2] - adj;
						int newCol = Color.HSBtoRGB(f[0], f[1], f[2]);
						GImageBack.setRGB(x, y, newCol);
					}
					if ( pf != null ) {
						pf.setProgress(Math.round(((float)x*90f)/(float)width)+10);
					}
				}
			}else {
				for (int x=0;x<width;x++) {
					for (int y=0;y<height;y++) {
						Color c = new Color(srcImage.getRGB(x,y));
						float[] f = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(), null);
						f[2] = f[2] - minB;
						int newCol = Color.HSBtoRGB(f[0], f[1], f[2]);
						GImageBack.setRGB(x, y, newCol);
					}
					if ( pf != null ) {
						pf.setProgress(Math.round(((float)x*45f)/(float)width)+10);
					}
				}
				adj = adj - minB;
				for (int x=0;x<width;x++) {
					for (int y=0;y<height;y++) {
						Color c = new Color(GImageBack.getRGB(x,y));
						float[] f = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(), null);
						f[2] = f[2]*(1-adj);
						int newCol = Color.HSBtoRGB(f[0], f[1], f[2]);
						GImageBack.setRGB(x, y, newCol);
					}
					if ( pf != null ) {
						pf.setProgress(Math.round(((float)x*45f)/(float)width)+55);
					}
				}
			}
		}else {
			for (int x=0;x<width;x++) {
				for (int y=0;y<height;y++) {
					Color c = new Color(srcImage.getRGB(x,y));
					float[] f = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(), null);
					int newCol = Color.HSBtoRGB(f[0], f[1], f[2]);
					GImageBack.setRGB(x, y, newCol);
				}
				if ( pf != null ) {
					pf.setProgress(Math.round(((float)x*90f)/(float)width)+10);
				}
			}
		}
		return GImageBack;
	}
	
	
	public static BufferedImage Hightlights(BufferedImage srcImage, float mag, ProgressFrame pf) {
		BufferedImage GImageBack = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		float pivPnt = 0.8f;
		float beginPnt = 0.5f;
		float sqrt2 = (float)Math.sqrt(2.0f);
		float aSeg = sqrt2*(1f-pivPnt);
		float xPnt = pivPnt - ((mag*aSeg)/sqrt2);
		float yPnt = pivPnt + ((mag*aSeg)/sqrt2);	
		float mSlp1 = (yPnt-beginPnt)/(xPnt-beginPnt);
		float mSlp2 = (1.1f-yPnt)/(1.1f-xPnt);
		float con1 = yPnt - (mSlp1*xPnt);
		float con2 = yPnt - (mSlp2*xPnt);	
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Color c = new Color(srcImage.getRGB(x,y));
				float[] f = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(), null);
				if ( f[2] > beginPnt ) {
					if ( f[2] <= xPnt ) {
						f[2] = mSlp1*f[2] + con1;	
					}else {
						f[2] = mSlp2*f[2] + con2;	
						if ( f[2] > 1.0 ) f[2] = 1.0f;
					}
				}
				int newCol = Color.HSBtoRGB(f[0], f[1], f[2]);
				GImageBack.setRGB(x, y, newCol);
			}
			if ( pf != null ) {
				pf.setProgress(Math.round(((float)x*100f)/(float)width));
			}
		}
		return GImageBack;
	}
	
	public static BufferedImage Shadows(BufferedImage srcImage, float mag, ProgressFrame pf) {
		BufferedImage GImageBack = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		float pivPnt = 0.2f;
		float endPnt = 0.6f;
		float pivPntX2 = pivPnt*2.0f;		
		float sqrt2 = (float)Math.sqrt(2.0f);
		float xPnt = pivPntX2 - ( ( (pivPntX2*sqrt2/2.0f) + (mag*pivPntX2*sqrt2/2.0f) )/sqrt2 );
		float yPnt = ( (pivPntX2*sqrt2/2.0f) + (mag*pivPntX2*sqrt2/2.0f) )/sqrt2;	
		float mSlp1 = yPnt/xPnt;
		float mSlp2 = (endPnt-yPnt)/(endPnt-xPnt);
		float con1 = 0f;		
		float con2 = endPnt*(1f-mSlp2);
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Color c = new Color(srcImage.getRGB(x,y));
				float[] f = Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(), null);
				if ( f[2] < endPnt ) {
					if ( f[2] <= xPnt ) {
						f[2] = mSlp1*f[2] + con1;
					}else {
						f[2] = mSlp2*f[2] + con2;						
					}
				}
				int newCol = Color.HSBtoRGB(f[0], f[1], f[2]);
				GImageBack.setRGB(x, y, newCol);
			}
			if ( pf != null ) {
				pf.setProgress(Math.round(((float)x*100f)/(float)width));
			}
		}
		return GImageBack;
	}

	
	public static BufferedImage Contrast(BufferedImage srcImage, float adj, ProgressFrame pf) {
		int len,lum = 0;
		int[] pixels = srcImage.getRGB(0, 0, srcImage.getWidth(), srcImage.getHeight(), null, 0, srcImage.getWidth());
		len = pixels.length;
		for (int x = 0; x < len; x++) {
			Color pixelColor = new Color(pixels[x]);
			lum = lum + (int) Math.round(pixelColor.getGreen()*0.59 + pixelColor.getRed()*0.30 + pixelColor.getBlue()*0.11);
			if ( pf != null ) {
				pf.setProgress(Math.round(((float)x*40f)/(float)len));
			}
		}
		short meanLum = (short)Math.round(lum/len);
		short[] contrast = new short[256];
		short pixelValue = 0;
		for (int j=0; j<256; j++) {
			if ( adj >= 0 ) {
				pixelValue = (short) ((j - (meanLum*adj))/(1-adj));
			}else {
				pixelValue = (short) ((-adj*(meanLum - j)) + j);
			}
			if (pixelValue > 255)
				pixelValue = 255;
			else if (pixelValue < 0)
				pixelValue = 0;
			contrast[j] = pixelValue;
		}
		if ( pf != null ) {
			pf.setProgress(50);
		}
		ShortLookupTable blut = new ShortLookupTable(0, contrast);
		BufferedImageOp lop = new LookupOp(blut, null);
		BufferedImage GImageBack = lop.filter(srcImage, null);
		Graphics2D g2 = (Graphics2D)GImageBack.createGraphics();
		g2.drawImage(GImageBack, 0, 0, null);	
		g2.dispose();
		if ( pf != null ) {
			pf.setProgress(95);
		}
		return GImageBack;
	}
	
	
	static class colValue {
		public float value = 0;
		public colValue() {};
		public colValue(float f) {
			value = f;
		}
	}

	public static BufferedImage Saturation(BufferedImage srcImage, float adj, ProgressFrame pf) {
		BufferedImage GImageBack = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		colValue r = new colValue();
		colValue g = new colValue();
		colValue b = new colValue();
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Color c = new Color(srcImage.getRGB(x,y));
				r.value = (float)c.getRed()/255f;
				g.value = (float)c.getGreen()/255f;
				b.value = (float)c.getBlue()/255f;
				
				//determine max
				colValue max = null;
				if ( ( r.value >= g.value ) && ( r.value >= b.value ) ) {
					max = r;
				}else if ( ( g.value >= r.value ) && ( g.value >= b.value ) ) {
					max = g;
				}else if ( ( b.value >= g.value ) && ( b.value >= r.value ) ) {
					max = b;
				}
				
				//determine min
				colValue min = null;
				if ( (r != max) && ( r.value <= g.value ) && ( r.value <= b.value ) ) {
					min = r;
				}else if ( (g != max) && ( g.value <= r.value ) && ( g.value <= b.value ) ) {
					min = g;
				}else if ( (b != max) && ( b.value <= g.value ) && ( b.value <= r.value ) ) {
					min = b;
				}
				
				//determine other
				colValue other = null;
				if ( ( r != max ) && ( r != min ) ) {
					other = r;
				}else if ( ( g != max ) && ( g != min ) ) {
					other = g;
				}else if ( ( b != max ) && ( b != min ) ) {
					other = b;
				}

				float maxMinusMin = max.value - min.value;

				//calculate percentage change
				float chg = maxMinusMin*adj*0.5f;

				//check if we are exceeding limits
				if ( chg > 0 ) {
					if ( (1f - max.value) < chg ) {
						chg = 1f - max.value;
					}
					if ( chg > min.value) {
						chg = min.value;
					}
				}else if ( chg < 0 ) {
					float center = (max.value + min.value)/2.0f - max.value;
					if ( chg < center) {
						chg = center;
					}
				}
				
				//calculate new values for r,g,b based on max, min, other.
				if ( maxMinusMin > 0f ) {
					float v1 = min.value - chg;
					if ( ( r == max ) && ( b == min ) ) {
						float v2 = ((maxMinusMin + 2f*chg)*(g.value - b.value))/(maxMinusMin);
						other.value = v1 + v2;
					}else if ( ( r == max ) && ( g == min ) ) {
						float v2 = ((maxMinusMin + 2f*chg)*(g.value - b.value))/(maxMinusMin);
						other.value = v1 - v2;					
					}else if ( ( g == max ) && ( r == min ) ) {
						float v2 = ((maxMinusMin + 2f*chg)*(b.value - r.value))/(maxMinusMin);
						other.value = v1 + v2;
					}else if ( ( g == max ) && ( b == min ) ) {
						float v2 = ((maxMinusMin + 2f*chg)*(b.value - r.value))/(maxMinusMin);
						other.value = v1 - v2;					
					}else if ( ( b == max ) && ( g == min ) ) {
						float v2 = ((maxMinusMin + 2f*chg)*(r.value - g.value))/(maxMinusMin);
						other.value = v1 + v2;
					}else if ( ( b == max ) && ( r == min ) ) {
						float v2 = ((maxMinusMin + 2f*chg)*(r.value - g.value))/(maxMinusMin);
						other.value = v1 - v2;					
					}
					max.value = max.value + chg;
					min.value = min.value - chg;
				}
				Color c2 = new Color((int)Math.round(r.value*255), (int)Math.round(g.value*255), (int)Math.round(b.value*255));
				GImageBack.setRGB(x, y, c2.getRGB());	
			}
			if ( pf != null ) {
				pf.setProgress(Math.round(((float)x*100f)/(float)width));
			}
		}
		return GImageBack;
	}
	
	
	public static BufferedImage SharpenBlur(BufferedImage srcImage, float mag, ProgressFrame pf) {
		BufferedImage imgPadded = new BufferedImage(srcImage.getWidth()+2, srcImage.getHeight()+2, BufferedImage.TYPE_INT_RGB);
        Graphics2D gPadded = imgPadded.createGraphics();
        gPadded.drawImage(srcImage, 1, 1, null);
        gPadded.dispose();
        
		Color c = new Color(srcImage.getRGB(0,0));
		imgPadded.setRGB(0, 0, c.getRGB());
		
		c = new Color(srcImage.getRGB(0,srcImage.getHeight()-1));
		imgPadded.setRGB(0, srcImage.getHeight()+1, c.getRGB());

		c = new Color(srcImage.getRGB(srcImage.getWidth()-1,0));
		imgPadded.setRGB(srcImage.getWidth()+1, 0, c.getRGB());
		
		c = new Color(srcImage.getRGB(srcImage.getWidth()-1,srcImage.getHeight()-1));
		imgPadded.setRGB(srcImage.getWidth()+1, srcImage.getHeight()+1, c.getRGB());
        
        for (int x=0;x<srcImage.getWidth();x++) {
			c = new Color(srcImage.getRGB(x,0));
			imgPadded.setRGB(x+1, 0, c.getRGB());
			c = new Color(srcImage.getRGB(x,srcImage.getHeight()-1));			
			imgPadded.setRGB(x+1, srcImage.getHeight()+1, c.getRGB());			
        }
        
        for (int y=0;y<srcImage.getHeight();y++) {
			c = new Color(srcImage.getRGB(0,y));
			imgPadded.setRGB(0, y+1, c.getRGB());
			c = new Color(srcImage.getRGB(srcImage.getWidth()-1,y));
			imgPadded.setRGB(srcImage.getWidth()+1, y+1, c.getRGB());			
        }
        
        ConvolveOp cop = null;
        BufferedImage GImageBack = null;
		float[] kern = {0f,0f,0f,0f,1f,0f,0f,0f,0f};
       if ( mag > 0 ) {
         	//create mask
    	   	float maskKernVal = (1.0f/9.0f);
			kern[0]=maskKernVal;
			kern[1]=maskKernVal;
			kern[2]=maskKernVal;
			kern[3]=maskKernVal;
			kern[4]=maskKernVal;
			kern[5]=maskKernVal;
			kern[6]=maskKernVal;
			kern[7]=maskKernVal;
			kern[8]=maskKernVal;
			cop = new ConvolveOp(new Kernel(3, 3, kern), ConvolveOp.EDGE_NO_OP, null);
			BufferedImage maskImg = new BufferedImage(srcImage.getWidth()+2, srcImage.getHeight()+2, BufferedImage.TYPE_INT_RGB);
			Graphics2D gMask = maskImg.createGraphics();
			gMask.drawImage(imgPadded, cop, 0, 0);
			gMask.dispose();
       	
			//create edges
			mag = -1f*mag*4;  //scaling factor.
			kern[0]=0f;
			kern[1]=mag;
			kern[2]=0f;
			kern[3]=mag;
			kern[4]=mag*4f*-1f;
			kern[5]=mag;
			kern[6]=0f;
			kern[7]=mag;
			kern[8]=0f;
			cop = new ConvolveOp(new Kernel(3, 3, kern), ConvolveOp.EDGE_NO_OP, null);
			BufferedImage EdgesImg = new BufferedImage(srcImage.getWidth()+2, srcImage.getHeight()+2, BufferedImage.TYPE_INT_RGB);
			Graphics2D gEdges = EdgesImg.createGraphics();
			gEdges.drawImage(maskImg, cop, 0, 0);
			gEdges.dispose();
  
			//add edges to padded to create sharpened image.
			GImageBack = new BufferedImage(imgPadded.getWidth(), imgPadded.getHeight(), BufferedImage.TYPE_INT_RGB);
			int width = imgPadded.getWidth();
			int height = imgPadded.getHeight();
			for (int x=0;x<width;x++) {
				for (int y=0;y<height;y++) {
					Color cPadded = new Color(imgPadded.getRGB(x,y));
					Color cEdges = new Color(EdgesImg.getRGB(x,y));
					int newRed = cPadded.getRed() + cEdges.getRed();
					if ( newRed > 255 ) newRed = 255;
					int newGreen = cPadded.getGreen() + cEdges.getGreen();
					if ( newGreen > 255 ) newGreen = 255;					
					int newBlue = cPadded.getBlue() + cEdges.getBlue();
					if ( newBlue > 255 ) newBlue = 255;							
					Color newColor = new Color(newRed, newGreen, newBlue);
					GImageBack.setRGB(x, y, newColor.getRGB());
				}
				if ( pf != null ) {
					pf.setProgress(Math.round(((float)x*100f)/(float)width));
				}
			}
        }else if ( mag < 0 ) {
	  		for (int x=0;x<kern.length;x++) {
	 			kern[x] = (1.0f/9.0f);
	 		}
			cop = new ConvolveOp(new Kernel(3, 3, kern), ConvolveOp.EDGE_NO_OP, null);
			GImageBack = new BufferedImage(srcImage.getWidth()+2, srcImage.getHeight()+2, BufferedImage.TYPE_INT_RGB);
			Graphics2D g1 = GImageBack.createGraphics();
	        g1.drawImage(imgPadded, cop, 0, 0);
	        mag = mag*0.25f; //scaling factor.
			int blurTimes = Math.round(Math.abs(mag)*100f/2f);
			if ( pf != null ) {
				pf.setProgress(Math.round(((float)1f*100f)/(float)blurTimes));
			}
	        for (int x=0;x<blurTimes;x++) {
	        	g1.drawImage(GImageBack, cop, 0, 0);
				if ( pf != null ) {
					pf.setProgress(Math.round(((float)x*100f)/(float)blurTimes+((float)1f*100f)/(float)blurTimes));
				}
	        }
	        g1.dispose();
	    }else {
	    	return srcImage;
	    }
        BufferedImage GImageBack2 = ImageAnalysis.getImageClip(GImageBack, 1, 1, srcImage.getWidth(), srcImage.getHeight());
 		return GImageBack2;
	}
	
	public static BufferedImage getR_ChannelImage(BufferedImage srcImage) {
		BufferedImage RImageBack = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Color c = new Color(srcImage.getRGB(x,y));
				RImageBack.setRGB(x, y, new Color(c.getRed(),0,0).getRGB());
			}
		}
		return RImageBack;
	}
	
	public static BufferedImage getG_ChannelImage(BufferedImage srcImage) {
		BufferedImage GImageBack = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Color c = new Color(srcImage.getRGB(x,y));
				GImageBack.setRGB(x, y, new Color(0,c.getGreen(),0).getRGB());
			}
		}
		return GImageBack;
	}

	public static BufferedImage getB_ChannelImage(BufferedImage srcImage) {
		BufferedImage BImageBack = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Color c = new Color(srcImage.getRGB(x,y));
				BImageBack.setRGB(x, y, new Color(0,0,c.getBlue()).getRGB());
			}
		}
		return BImageBack;
	}

	public BufferedImage getScaledImage(int BoxWidth, int BoxHeight) {
		return getScaledImage(getFullSizeImage(), BoxWidth, BoxHeight);
	}
	
	public static BufferedImage getScaledImage(BufferedImage srcImage, int BoxWidth, int BoxHeight) {
 	  	Dimension PropDim = getPropImgDim(srcImage, BoxWidth, BoxHeight);
  		return ScaleImage(srcImage, PropDim.width, PropDim.height);
	}
	
	public BufferedImage getH_S_Image(int BoxWidth, int BoxHeight) {
		return getH_S_Image(getScaledImage(BoxWidth, BoxHeight));
	}
	
	public static BufferedImage getH_S_Image(BufferedImage srcImage) {
		BufferedImage Scaled_H_S_Image = null;
		Scaled_H_S_Image = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = Scaled_H_S_Image.createGraphics();
		g2.drawImage(srcImage, 0, 0, srcImage.getWidth(), srcImage.getHeight(), null);
		g2.dispose();
		int r;
		int g;
		int b;
		int width = Scaled_H_S_Image.getWidth();
		int height = Scaled_H_S_Image.getHeight();
		int highlightColor = Settings.Highlight_Color.getRGB();
		int shadowColor = Settings.Shadow_Color.getRGB();
		int highlightThreshold = Settings.HighlightThreshold;
		int shadowThreshold = Settings.ShadowThreshold;
		
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Color pixelColor = new Color(Scaled_H_S_Image.getRGB(x, y));
				r = pixelColor.getRed();
				g = pixelColor.getGreen();
				b = pixelColor.getBlue();
				if ( (r >= highlightThreshold) && (g >= highlightThreshold) && (b >= highlightThreshold) ) {
					Scaled_H_S_Image.setRGB(x, y, highlightColor);
				}else if ( (r <= shadowThreshold) && (g <= shadowThreshold) && (b <= shadowThreshold) ) {
					Scaled_H_S_Image.setRGB(x, y, shadowColor);
				}
			}
		}
		return Scaled_H_S_Image;
	}
	
	public static HistogramArray getHistArray(BufferedImage img) {
		return new HistogramArray(img);
	}
 	
	public HistogramArray getHistArray() {
		if ( Hist_Actual == null ) {
			Hist_Actual = new HistogramArray(readFile());
		}
		return Hist_Actual;
	}
	
	public HistogramArray getHist270_180() {
		if ( Hist270_180 == null ) {
			Hist270_180 = new HistogramArray(getStdImg270_180());
		}
		return Hist270_180;
	}

	public BufferedImage getImageClip(int srcX1, int srcY1, int srcW, int srcH) {
 		return getImageClip(getFullSizeImage(), srcX1, srcY1, srcW, srcH);
	}

	public static BufferedImage getImageClip(BufferedImage srcImage, int srcX1, int srcY1, int srcW, int srcH) {
		BufferedImage clippedImg = new BufferedImage(srcW, srcH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = clippedImg.createGraphics();
		g2.drawImage(srcImage, 0, 0, srcW, srcH, srcX1, srcY1, srcX1+srcW, srcY1+srcH, null);
  		g2.dispose();
 		return clippedImg;
	}
  	
 	public BufferedImage getFullSizeImage() {
		BufferedImage FullSizeImage = readFile();
		if ( rotationAngle != angle0 ) {
			BufferedImage rotImage = new BufferedImage(FullSizeImage.getHeight(), FullSizeImage.getWidth(), BufferedImage.TYPE_INT_RGB);
		    Graphics2D g = (Graphics2D) rotImage.getGraphics();
		    g.rotate(rotationAngle);
		    if ( rotationAngle == angleCW ) {
		    	g.drawImage(FullSizeImage, 0, -FullSizeImage.getHeight(), null);
		    }else if ( rotationAngle == angleCCW ) {
		    	g.drawImage(FullSizeImage, -FullSizeImage.getWidth(), 0, null);
		    }
		    g.dispose();
		  FullSizeImage = rotImage;
   		}
		return FullSizeImage;
	}
 	
 	private BufferedImage readFile() {
 		BufferedImage img = null;
 		if ( seedImage != null ) {
 			img = seedImage;
 		}else {
	 		try {
				Image imgNonBuf = null;
				
				int maxTrys = 3;
				boolean success = false;
				boolean trackerException = false;
				int numTry = 1;
				while ( (numTry <= maxTrys) && (success == false)) {
		 			imgNonBuf = java.awt.Toolkit.getDefaultToolkit().createImage(filePath + "/" + fileName);
					MediaTracker sTracker = new MediaTracker(sComponent);
		   			sTracker.addImage(imgNonBuf, 1);
		   			try {
		   				sTracker.waitForAll(60000);
		   			}catch (Exception e) {
		   				trackerException = true;
		   			}
		  			if (sTracker.isErrorAny() || trackerException ) {
		  				numTry++;
		  				trackerException = false;
		 			}else {
		 				success = true;
		 			}
				}
				
				if ( success == true ) {
			 		BufferedImage imgbuf = new BufferedImage(imgNonBuf.getWidth(null), imgNonBuf.getHeight(null), BufferedImage.TYPE_INT_RGB);
					Graphics g = imgbuf.getGraphics();
					g.drawImage(imgNonBuf, 0, 0, null);
					img = imgbuf;
					g.dispose();
				}else {
					throw new Exception("Image Load Failed");
				}
	
	 		}catch (Exception e) {
	 			e.printStackTrace();
				img = new BufferedImage(200, 50, BufferedImage.TYPE_INT_RGB);
			    Graphics2D g = (Graphics2D) img.getGraphics();
			    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
			    g.drawString("Unable to read file", 50, 30);
			    g.dispose();
	 		}
 		}
  		if ( first ) {
			imageWidth = img.getWidth();
			imageHeight = img.getHeight();
			first = false;
 		}
	  	return img;
 	}
 	
 	public static Dimension getPropImgDim(BufferedImage img, int BoxWidth, int BoxHeight) {
		int AdjImgHeight = img.getHeight();
  		int AdjImgWidth = img.getWidth();
  		
		float ImgBoxWidthRatio = (float)img.getWidth()/(float)BoxWidth;
		float ImgBoxHeightRatio = (float)img.getHeight()/(float)BoxHeight;

		if ( ( ImgBoxWidthRatio >= ImgBoxHeightRatio ) && ( ImgBoxWidthRatio > 1 ) ){
			AdjImgWidth = BoxWidth;
  			AdjImgHeight = Math.round(((float)img.getHeight()*((float)AdjImgWidth/(float)img.getWidth())));
		}else if ( ( ImgBoxHeightRatio >= ImgBoxWidthRatio ) && ( ImgBoxHeightRatio > 1 ) ){
			AdjImgHeight = BoxHeight;
			AdjImgWidth = Math.round(((float)img.getWidth()*((float)AdjImgHeight/(float)img.getHeight())));
		}
		return new Dimension(AdjImgWidth, AdjImgHeight);
	}

 	private static BufferedImage ScaleImage(Image srcImg, int w, int h) {
		BufferedImage scaledImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = scaledImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
  		g2.dispose();
		return scaledImg;
	}
 	
	public ImageEXIF getEXIF() {
 		if ( imageEXIF == null ) {
 			imageEXIF = new ImageEXIF(filePath, fileName);
 		}
 		return imageEXIF;
 	}
}
