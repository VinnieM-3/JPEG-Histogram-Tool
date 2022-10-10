import java.awt.image.BufferedImage;


public class ShadowsPanel extends AdjustmentPanel {
	private static final long serialVersionUID = -3695575603595647930L;

	public ShadowsPanel(BufferedImage srcImg) {
		super(srcImg);
	}
	
	float calcMagSetting(int pos) {
		return ((float)pos/100f)*0.5f;
	}
		
	String heading() {
		return "Shadows";
	}
	
	BufferedImage applyAdj(BufferedImage srcImage, float magSetting) {
		return ImageAnalysis.Shadows(srcImage, magSetting, null);
	}

}
