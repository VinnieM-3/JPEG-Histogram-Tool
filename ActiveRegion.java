public class ActiveRegion {
	public static final int TEXT_CMD = 1;
	public static final int IMAGE_VIEW = 2;
	public static final int SLIDER = 3;	
	public static final int LUM_BOX = 4;		
	public static final int RGB_BOX = 5;
	public static final int R_BOX = 6;	
	public static final int G_BOX = 7;	
	public static final int B_BOX = 8;	
	public static final int CROP_CORNER = 9;
	public static final int CROP_LINE = 10;
	public static final int CROP_CENTER = 11;	
	
	public String Name = "";
	public int type;
	public int ID;
	public int x1;
	public int y1;
	public int x2;
	public int y2;
	
	public ActiveRegion(String Name, int type, int ID, int x1, int y1, int x2, int y2) {
		this.Name = Name;
		this.type = type;
		this.ID = ID;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public ActiveRegion(String Name, int type, int ID) {
		this.Name = Name;
		this.type = type;
		this.ID = ID;
	}
	
	public void setRegion(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public boolean isInBounds(int x, int y) {
		boolean ret = false;
		if ( (y >= y1) && (y <= y2) && (x >= x1) && (x <= x2) ) {
			ret = true;
		}
		return ret;
	}

}
