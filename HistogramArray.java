import java.awt.Color;
import java.awt.image.BufferedImage;

public class HistogramArray {
	public int[] aryLum = new int[256];
	public int[] aryRGB = new int[256];
	public int[] aryR = new int[256];
	public int[] aryG = new int[256];
	public int[] aryB = new int[256];
	public int numPixels = 0;
	private int len,r,g,b,lum = 0;

	public HistogramArray(BufferedImage img) {
		int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
		len = pixels.length;
		numPixels = len;
		for (int x = 0; x < len; x++) {
			Color pixelColor = new Color(pixels[x]);
			r = pixelColor.getRed();
			g = pixelColor.getGreen();
			b = pixelColor.getBlue();
			lum = (int) Math.round(g * 0.59 + r * 0.30 + b * 0.11);
			aryLum[lum]++;
			aryRGB[r]++;
			aryRGB[g]++;
			aryRGB[b]++;
			aryR[r]++;
			aryG[g]++;
			aryB[b]++;
		}
	}
}
