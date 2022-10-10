import java.awt.image.BufferedImage;


public class SaturationPanel extends AdjustmentPanel {
	private static final long serialVersionUID = -2539624410708629497L;

	public SaturationPanel(BufferedImage srcImg) {
		super(srcImg);
	}
	
	float calcMagSetting(int pos) {
		return (float)pos/50;
	}
		
	String heading() {
		return "Saturation Level";
	}
	
	BufferedImage applyAdj(BufferedImage srcImage, float magSetting) {
		return ImageAnalysis.Saturation(srcImage, magSetting, null);
	}

}
