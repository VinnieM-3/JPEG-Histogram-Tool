import javax.swing.*;
import java.beans.*;
import java.awt.*;
import java.io.File;

public class FileDialogSmallImage extends JComponent implements PropertyChangeListener {
	private static final long serialVersionUID = -6303271430283175146L;
	ImageIcon smImage = null;
    JFileChooser fileChooser = null;
    File file = null;
    int imgWidth = 120;
    int imgHeight = 120;
    
	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
	Cursor defaultCursor = null;

    public FileDialogSmallImage(JFileChooser fileChooser) {
    	this.fileChooser = fileChooser;
        setPreferredSize(new Dimension(imgWidth, imgHeight));
        fileChooser.addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(e.getPropertyName())) {
            file = null;
        	smImage = null;
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(e.getPropertyName())) {
            file = (File) e.getNewValue();
        	smImage = null;
        }
        repaint();
    }

    protected void paintComponent(Graphics g) {
    	defaultCursor = fileChooser.getCursor();
    	fileChooser.setCursor(hourglassCursor);
     	if ( (file != null) && (smImage == null) ){
 	        ImageIcon icon = new ImageIcon(file.getPath());
	        if (icon != null) {
	    		int adjImgHeight = icon.getIconHeight();
	      		int adjImgWidth = icon.getIconWidth();
	    		float imgBoxWidthRatio = (float)icon.getIconWidth()/(float)imgWidth;
	    		float imgBoxHeightRatio = (float)icon.getIconHeight()/(float)imgHeight;
	    		if ( ( imgBoxWidthRatio >= imgBoxHeightRatio ) && ( imgBoxWidthRatio > 1 ) ){
	    			adjImgWidth = imgWidth;
	      			adjImgHeight = Math.round(((float)icon.getIconHeight()*((float)adjImgWidth/(float)icon.getIconWidth())));
	    		}else if ( ( imgBoxHeightRatio >= imgBoxWidthRatio ) && ( imgBoxHeightRatio > 1 ) ){
	    			adjImgHeight = imgHeight;
	    			adjImgWidth = Math.round(((float)icon.getIconWidth()*((float)adjImgHeight/(float)icon.getIconHeight())));
	    		}
             	smImage = new ImageIcon(icon.getImage().getScaledInstance(adjImgWidth, adjImgHeight, Image.SCALE_FAST));
	        }
        }
        if (smImage != null) {
            int x = Math.round(getWidth()/2f - smImage.getIconWidth()/2f);
            int y = Math.round(getHeight()/2f - smImage.getIconHeight()/2f);
            smImage.paintIcon(this, g, x, y);
        }
        fileChooser.setCursor(defaultCursor);
    }
}
