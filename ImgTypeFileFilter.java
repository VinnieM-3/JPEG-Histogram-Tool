import java.io.File;
import javax.swing.filechooser.*;

public class ImgTypeFileFilter extends FileFilter {

	public String getDescription() {
		return "JPEG Images";
	}

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 &&  i < s.length() - 1) {
			extension = s.substring(i+1).toLowerCase();
		}

		if (extension != null) {
			if ( (extension.equals("jpeg") 
					|| extension.equals("jpg")
					) ) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

}
