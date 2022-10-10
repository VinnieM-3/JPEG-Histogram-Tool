import java.awt.image.BufferedImage;


public class ContrastPanel extends AdjustmentPanel {
	private static final long serialVersionUID = -8932709872929030628L;

	public ContrastPanel(BufferedImage srcImg) {
		super(srcImg);
	}
	
	float calcMagSetting(int pos) {
		return (float)pos/200;
	}
		
	String heading() {
		return "Contrast Level";
	}
	
	BufferedImage applyAdj(BufferedImage srcImage, float magSetting) {
		return ImageAnalysis.Contrast(srcImage, magSetting, null);
	}

}
