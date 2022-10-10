import java.awt.image.BufferedImage;


public class HighlightsPanel extends AdjustmentPanel {
	private static final long serialVersionUID = -1936317634813614878L;

	public HighlightsPanel(BufferedImage srcImg) {
		super(srcImg);
	}
	
	float calcMagSetting(int pos) {
		return ((float)pos/100f)*0.5f;
	}
		
	String heading() {
		return "Hightlights";
	}
	
	BufferedImage applyAdj(BufferedImage srcImage, float magSetting) {
		return ImageAnalysis.Hightlights(srcImage, magSetting, null);
	}

}
