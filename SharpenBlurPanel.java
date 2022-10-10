import java.awt.image.BufferedImage;


public class SharpenBlurPanel extends AdjustmentPanel {
	private static final long serialVersionUID = 1279577109160876122L;

	public SharpenBlurPanel(BufferedImage srcImg) {
		super(srcImg);
	}
	
	float calcMagSetting(int pos) {
		return (float)pos/100f;
	}
		
	String heading() {
		return "Sharpening/Blur Level";
	}
	
	BufferedImage applyAdj(BufferedImage srcImage, float magSetting) {
		return ImageAnalysis.SharpenBlur(srcImage, magSetting, null);
	}

}
