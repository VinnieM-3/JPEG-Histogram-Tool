import java.io.*;
import java.text.*;


public class ImageEXIF {
	public String cameraModel = "?";
	public String imageDateTime = "?";
	public String imageISO = "?";
	public String imageTx = "?";
	public String imageAp = "?";
	public String imageExpBias = "?";
	public String imageFocalLength = "?";
	public String image35FocalLength = "?";
	public String imageFocalLengths = "?";	
	public String imageFlashMode = "?";

	private static final int BYTE = 1;
	private static final int ASCII = 2;
	private static final int SHORT = 3;
	private static final int LONG = 4;
	private static final int RATIONAL = 5;
	private static final int SBYTE = 6;
	private static final int UNDEFINED = 7;
	private static final int SSHORT = 8;
	private static final int SLONG = 9;
	private static final int SRATIONAL = 10;
	private static final int FLOAT = 11;
	private static final int DOUBLE = 12;
	
	private static final int LittleEndian = 13;
	private static final int BigEndian = 14;
	private static final int Unknown = 15;	
	
	private int exifOffset = 0;
	private int Endianness = 0;
	private int[] rawFileData;
	
	private boolean isNumeric(String str) {  
		try {
			Double.parseDouble(str);  
		} catch(NumberFormatException e) {  
			return false;  
		}  
		return true;  
	}  

	private void getModel() {
		int[] modelCode = {0x01,0x10};
		FieldEntry fe = new FieldEntry(modelCode);
		cameraModel = fe.getFieldValue();
	}
	
	private void getExposureTime() {
		String exposureTime = null;
		int[] exposureTimeCode = {0x82,0x9A};
		FieldEntry fe = new FieldEntry(exposureTimeCode);
		exposureTime = fe.getFieldValue();
		if (isNumeric(exposureTime) ) {
			NumberFormat  formatter = new DecimalFormat("##.######");
			exposureTime = formatter.format(Double.valueOf(exposureTime)); 
 			double rem = Math.IEEEremainder(1d,Double.valueOf(exposureTime));
			if ( Math.abs(rem) < 0.1f ) {
				String fracSpd = String.valueOf(Math.round(1d/Double.valueOf(exposureTime)));
				exposureTime = "1/" + fracSpd + " or " + exposureTime + "sec";
			}
 		}
		imageTx = exposureTime;
	}
	
	private void getFNumber() {
		int[] FNumberCode = {0x82,0x9D};
		FieldEntry fe = new FieldEntry(FNumberCode);
		imageAp = fe.getFieldValue();
	}
	
	private void getISOSpeedRatings() {
		int[] ISOSpeedRatingsCode = {0x88,0x27};
		FieldEntry fe = new FieldEntry(ISOSpeedRatingsCode);
		imageISO = fe.getFieldValue();
	}
	
	private void getDateTimeOriginal() {
		int[] dateTimeOriginalCode = {0x90,0x03};
		FieldEntry fe = new FieldEntry(dateTimeOriginalCode);
		imageDateTime = fe.getFieldValue();
	}
	
	private void getExposureBiasValue() {
		String exposureBiasValue = null;
		int[] exposureBiasValueCode= {0x92,0x04};
		FieldEntry fe = new FieldEntry(exposureBiasValueCode);
		exposureBiasValue = fe.getFieldValue();
		if (!exposureBiasValue.equals("?")) {
			NumberFormat  formatter = new DecimalFormat("0.0#");
			imageExpBias = formatter.format(Double.valueOf(exposureBiasValue)); 
		}else {
			imageExpBias = exposureBiasValue;
		}
	}
	
	private void getFlash() {
		String flash = null;
		int[] flashCode = {0x92,0x09};
		FieldEntry fe = new FieldEntry(flashCode);
		flash = fe.getFieldValue();
		if ( !flash.equals("?") ) {
			int intFlash = Integer.valueOf(flash);
		  	int bitmask = 0x0001;
		  	intFlash = (intFlash & bitmask);
	    	if (intFlash == 0 ) {
	    		flash = "Flash did not fire";
	    	}else {
	    		flash = "Flash fired";
	    	}
		}
		imageFlashMode = flash;
	}
	
	private void getFocalLength() {
		int[] focalLengthCode = {0x92,0x0A};
		FieldEntry fe = new FieldEntry(focalLengthCode);
		imageFocalLength = fe.getFieldValue();
	}
	
	private void getFocalLengths() {
		String combined = imageFocalLength;
		if (!image35FocalLength.equals("?")) {
			combined = combined + "/"  + image35FocalLength + "mm";
		}else {
			combined = combined + "mm";
		}
		imageFocalLengths = combined;
	}
	
	private void getFocalLengthIn35mmFilm() {
		int[] focalLengthIn35mmFilmCode = {0xA4,0x05};
		FieldEntry fe = new FieldEntry(focalLengthIn35mmFilmCode);
		image35FocalLength = fe.getFieldValue();
	}
	
	public ImageEXIF(String filePath, String fileName) {
		rawFileData = loadFile(filePath, fileName);
		Endianness = Unknown;
		if ( rawFileData.length > 0 ) {
			exifOffset = search(new int[] {0x49,0x49,42,0}, rawFileData); //little endian
			if ( exifOffset >= 0 ) {
				Endianness = LittleEndian;
			}else {
				exifOffset = search(new int[] {0x4D,0x4D,0,42}, rawFileData); //big endian
				if ( exifOffset >= 0 ) {
					Endianness = BigEndian;
				}
			}
			if ( Endianness != Unknown ){
				getModel();
				getExposureTime();
				getFNumber();
				getISOSpeedRatings();
				getDateTimeOriginal();
				getExposureBiasValue();
				getFocalLength();
				getFocalLengthIn35mmFilm();
				getFlash();	
				getFocalLengths();
			}
		}
	}
		
	private int[] big2LittleEndian(int[] big) {
		int[] little = new int[big.length];
		for(int x=0;x<big.length;x++) {
			little[x] = big[big.length-x-1];
		}
		return little;
	}
	
	private int[] little2BigEndian(int[] little) {
		int[] big = new int[little.length];
		for(int x=0;x<little.length;x++) {
			big[x] = little[little.length-x-1];
		}
		return big;
	}
	
	private int[] loadFile(String filePath, String fileName) {
		int[] rawData = new int[65536];
		FileInputStream in = null;
		try {
			in = new FileInputStream(filePath + "/" + fileName);
			for(int x=0;x<65000;x++) {
				rawData[x] = Integer.valueOf(in.read());
			}
		}catch (Exception e) {
			//do nothing
		}finally {
			if (rawData != null) {
				try {
					in.close();
				}catch (Exception e) {}
			}
		}
		return rawData;
	}

	private int search(int[] pattern, int[] data) {
		int M = pattern.length;
		int N = data.length;
		int result = -1;

		// preprocess
		int[] next = new int[M];
		int i = 0, j = -1;
		next[0] = -1;
		while(i < M - 1) {
			if ( (j == -1) || (pattern[i] == pattern[j] ) ) {
				i++;
				j++;
				if ( pattern[i] != pattern[j] ) {
					next[i] = j;
				}else {
					next[i] = next[j];
				}
			}else {
				j = next[j];
			}
		}
		i = j = 0;
		while (i < N && j < M) {
			if (( j == -1 ) || ( pattern[j] == data[i] ) ) {
				i++;
				j++;
			}else {
				j = next[j];
			}
		}
		if ( j == M ) {
			result = i - j; //match found at offset i - M
		}
		return result;
	}
	
	class FieldEntry {
		private String fieldValue = "?";
		private boolean validData = true;
		private int numValues = 0;
		
		public FieldEntry(int[] code) {
			try {
				if ( Endianness == LittleEndian ) {
					code = big2LittleEndian(code);
				}
					
				int[] fieldEntry = getFieldEntry(code);
	
				if ( fieldEntry != null ) {
					int fieldType = getFieldType(fieldEntry);
					if ( ( fieldType > 12 ) || ( fieldType < 1 ) ) {
						validData = false;
					}
					int byteCount = 0;
					int[] rawValues = null;
					int[] numerator = null;
					int[] denominator = null;
					int intValue = 0;
					long lngValue = 0;
					long lngNumerator = 0;
					long lngDenominator = 0;
					if (validData) {
						switch (fieldType) {
						case BYTE: 
							numValues = getNumValues(fieldEntry);
							if (numValues < 2000) {
								byteCount = numValues;
								if ( numValues <= 4 ) {
									if (byteCount > 0) fieldValue = String.valueOf( (char)fieldEntry[8] );
									for (int x=1;x<byteCount;x++) {
										fieldValue = fieldValue + " " + String.valueOf( (char)fieldEntry[8+x] );
									}
								}else {
									int[] values = getValues(getAddress(fieldEntry), byteCount);
									if (byteCount > 0) fieldValue = String.valueOf( (char)values[0] );
									for (int x=1;x<byteCount;x++) {
										fieldValue = fieldValue + " " + String.valueOf( (char)values[x] );
									}
								}
							}else {
								fieldValue = "?";
							}
							break;
						case ASCII:
							numValues = getNumValues(fieldEntry);
							if (numValues < 2000) {
								fieldValue = "";
								byteCount = numValues;
								if ( byteCount <= 4 ) {
									for (int x=0;x<(byteCount-1);x++) {
										fieldValue = fieldValue + String.valueOf( (char)fieldEntry[8+x] );
									}
								}else {
									int[] values = getValues(getAddress(fieldEntry), byteCount);
									for (int x=0;x<(byteCount-1);x++) {
										fieldValue = fieldValue + String.valueOf( (char)values[x] );
									}
								}
							}else {
								fieldValue = "?";
							}
							break;
						case SHORT:
							rawValues = new int[2];
							rawValues[0] = fieldEntry[8];
							rawValues[1] = fieldEntry[9];					
							if (Endianness == LittleEndian) {
								rawValues = little2BigEndian(rawValues);
							}
							intValue = Integer.valueOf(
								String.format("%02X",rawValues[0], 16)
								+ String.format("%02X",rawValues[1], 16), 16 ).intValue();	
							fieldValue = String.valueOf(intValue);
							break;
						case LONG:
							rawValues = new int[4];
							for (int x=0;x<4;x++) {
								rawValues[x] = fieldEntry[8+x];
							}
							if (Endianness == LittleEndian) {
								rawValues = little2BigEndian(rawValues);
							}
							lngValue = Long.valueOf(
								String.format("%02X",rawValues[0], 16)
								+ String.format("%02X",rawValues[1], 16)
								+ String.format("%02X",rawValues[2], 16)
								+ String.format("%02X",rawValues[3], 16), 16 ).longValue();	
							fieldValue = String.valueOf(lngValue);
							break;
						case RATIONAL:
							byteCount = 8;
							numerator  = new int[4];
							rawValues = getValues(getAddress(fieldEntry), byteCount);
							for (int x=0;x<4;x++) {
								numerator[x] = rawValues[x];
							}
							denominator  = new int[4];
							for (int x=4;x<8;x++) {
								denominator[x-4] = rawValues[x];
							}
							if (Endianness == LittleEndian) {
								numerator = little2BigEndian(numerator);
								denominator = little2BigEndian(denominator);
							}
							lngNumerator = Long.valueOf(
									String.format("%02X",numerator[0], 16)
									+ String.format("%02X",numerator[1], 16)
									+ String.format("%02X",numerator[2], 16)
									+ String.format("%02X",numerator[3], 16), 16 ).longValue();	
							lngDenominator = Long.valueOf(
									String.format("%02X",denominator[0], 16)
									+ String.format("%02X",denominator[1], 16)
									+ String.format("%02X",denominator[2], 16)
									+ String.format("%02X",denominator[3], 16), 16 ).longValue();	
							if ( lngDenominator != 0 ) {
								fieldValue = String.valueOf((double)lngNumerator/(double)lngDenominator);
							}else {
								fieldValue = "?";
							}
							break;
						case SBYTE:
							numValues = getNumValues(fieldEntry);
							if (numValues < 2000) {
								byteCount = numValues;
								if ( numValues <= 4 ) {
									if (byteCount > 0) fieldValue = String.valueOf( (char)fieldEntry[8] );
									for (int x=1;x<byteCount;x++) {
										fieldValue = fieldValue + " " + String.valueOf( (char)fieldEntry[8+x] );
									}
								}else {
									int[] values = getValues(getAddress(fieldEntry), byteCount);
									if (byteCount > 0) fieldValue = String.valueOf( (char)values[0] );
									for (int x=1;x<byteCount;x++) {
										fieldValue = fieldValue + " " + String.valueOf( (char)values[x] );
									}
								}
							}else {
								fieldValue = "?";
							}
							break;
						case UNDEFINED:
							numValues = getNumValues(fieldEntry);
							if (numValues < 2000) {
								byteCount = numValues;
								if ( numValues <= 4 ) {
									if (byteCount > 0) fieldValue = String.valueOf( (char)fieldEntry[8] );
									for (int x=1;x<byteCount;x++) {
										fieldValue = fieldValue + " " + String.valueOf( (char)fieldEntry[8+x] );
									}
								}else {
									int[] values = getValues(getAddress(fieldEntry), byteCount);
									if (byteCount > 0) fieldValue = String.valueOf( (char)values[0] );
									for (int x=1;x<byteCount;x++) {
										fieldValue = fieldValue + " " + String.valueOf( (char)values[x] );
									}
								}
							}else {
								fieldValue = "?";
							}
							break;
						case SSHORT:
							rawValues = new int[2];
							rawValues[0] = fieldEntry[8];
							rawValues[1] = fieldEntry[9];					
							if (Endianness == LittleEndian) {
								rawValues = little2BigEndian(rawValues);
							}
							intValue = Integer.valueOf(
								String.format("%02X",rawValues[0], 16)
								+ String.format("%02X",rawValues[1], 16), 16).intValue();	
							if (intValue > 32767) {
		                       	  intValue = (65536 - intValue)*-1;
							}
							fieldValue = String.valueOf(intValue);
							break;
						case SLONG:
							rawValues = new int[4];
							for (int x=0;x<4;x++) {
								rawValues[x] = fieldEntry[8+x];
							}
							if (Endianness == LittleEndian) {
								rawValues = little2BigEndian(rawValues);
							}
							lngValue = Long.valueOf(
								String.format("%02X",rawValues[0], 16)
								+ String.format("%02X",rawValues[1], 16)
								+ String.format("%02X",rawValues[2], 16)
								+ String.format("%02X",rawValues[3], 16), 16 ).longValue();	
							if (lngValue > 2147483647) {
								lngValue = (4294967296L - lngValue)*-1;
							}
							fieldValue = String.valueOf(lngValue);
							break;
						case SRATIONAL:
							byteCount = 8;
							numerator  = new int[4];
							rawValues = getValues(getAddress(fieldEntry), byteCount);
							for (int x=0;x<4;x++) {
								numerator[x] = rawValues[x];
							}
							denominator  = new int[4];
							for (int x=4;x<8;x++) {
								denominator[x-4] = rawValues[x];
							}
							if (Endianness == LittleEndian) {
								numerator = little2BigEndian(numerator);
								denominator = little2BigEndian(denominator);
							}
							lngNumerator = Long.valueOf(
									String.format("%02X",numerator[0], 16)
									+ String.format("%02X",numerator[1], 16)
									+ String.format("%02X",numerator[2], 16)
									+ String.format("%02X",numerator[3], 16), 16 ).longValue();	
							if (lngNumerator > 2147483647) {
								lngNumerator = (4294967296L - lngNumerator)*-1;
							}
	
							lngDenominator = Long.valueOf(
									String.format("%02X",denominator[0], 16)
									+ String.format("%02X",denominator[1], 16)
									+ String.format("%02X",denominator[2], 16)
									+ String.format("%02X",denominator[3], 16), 16 ).longValue();	
							if (lngDenominator > 2147483647) {
								lngDenominator = (4294967296L - lngDenominator)*-1;
							}
	
							if ( lngDenominator != 0 ) {
								fieldValue = String.valueOf((double)lngNumerator/(double)lngDenominator);
							}else {
								fieldValue = "?";
							}
							break;
						case FLOAT:
							rawValues = new int[4];
							for (int x=0;x<4;x++) {
								rawValues[x] = fieldEntry[8+x];
							}
							if (Endianness == LittleEndian) {
								rawValues = little2BigEndian(rawValues);
							}
							float fltValue = Float.valueOf(
								String.format("%02X",rawValues[0], 16)
								+ String.format("%02X",rawValues[1], 16)
								+ String.format("%02X",rawValues[2], 16)
								+ String.format("%02X",rawValues[3], 16)).floatValue();	
							fieldValue = String.valueOf(fltValue);
							break;
						case DOUBLE:
							rawValues = getValues(getAddress(fieldEntry), 8);
							if (Endianness == LittleEndian) {
								rawValues = little2BigEndian(rawValues);
							}
							if ( rawValues.length > 0 ) {
								double dblValue = Double.valueOf(
									String.format("%02X",rawValues[0], 16)
									+ String.format("%02X",rawValues[1], 16)
									+ String.format("%02X",rawValues[2], 16)
									+ String.format("%02X",rawValues[3], 16)
									+ String.format("%02X",rawValues[5], 16)
									+ String.format("%02X",rawValues[6], 16)
									+ String.format("%02X",rawValues[7], 16)).doubleValue();	
								fieldValue = String.valueOf(dblValue);
							}
							break;
						}
					}else {
						fieldValue = "?";
					}
				}
			}catch(Exception e) {
				fieldValue = "?";
			}
		}
		
		public String getFieldValue() {
			return fieldValue;
		}

		private int[] getValues(int address, int count) {
			int[] values = new int[count];
			for (int x=address;x<(address+count);x++) {
				values[x-address] = rawFileData[x];
			}
			return values;
		}
		
		private int getAddress(int[] fieldEntry) {
			int addr = 0;
			int[] rawAddr = new int[4];
			for (int x=8;x<=11;x++) {
				rawAddr[x-8] = fieldEntry[x];
			}
			if (Endianness == LittleEndian) {
				rawAddr = little2BigEndian(rawAddr);
			}
			addr = Integer.valueOf(String.format("%02X",rawAddr[0]) 
					+ String.format("%02X",rawAddr[1])
					+ String.format("%02X",rawAddr[2])
					+ String.format("%02X",rawAddr[3]), 16);
			return addr + exifOffset;
		}
		
		private int getNumValues(int[] fieldEntry) {
			int count = 0;
			int[] rawCount = new int[4];
			for (int x=4;x<=7;x++) {
				rawCount[x-4] = fieldEntry[x];
			}
			if (Endianness == LittleEndian) {
				rawCount = little2BigEndian(rawCount);
			}
			count = Integer.valueOf(String.format("%02X",rawCount[0]) 
					+ String.format("%02X",rawCount[1])
					+ String.format("%02X",rawCount[2])
					+ String.format("%02X",rawCount[3]), 16);
			return count;
		}
		
		private int getFieldType(int[] fieldEntry) {
			int type = 0;
			int[] rawType = new int[2];
			for (int x=2;x<=3;x++) {
				rawType[x-2] = fieldEntry[x];
			}
			if (Endianness == LittleEndian) {
				rawType = little2BigEndian(rawType);
			}
			type = Integer.valueOf(String.format("%02X",rawType[0]) + String.format("%02X",rawType[1]), 16);
			return type;
		}
		
		private int[] getFieldEntry(int[] Code) {
			int[] fieldEntry = new int[12];
			int loc = search(Code, rawFileData);
			if (loc >= 0 ) {
				for (int x=loc;x<loc+12;x++) {
					fieldEntry[x-loc] = rawFileData[x];
				}
				return fieldEntry;
			}else {
				return null;
			}
		}
	}

	
//    public static void main(String[] args) {
//     	ImageEXIF e = new ImageEXIF("c:/", "test.jpg");
//		System.out.println("Model=" + e.cameraModel);
//		System.out.println("ExposureTime=" + e.imageTx);
//		System.out.println("FNumber=" + e.imageAp);
//		System.out.println("ISOSpeedRatings=" + e.imageISO);
//		System.out.println("DateTimeOriginal=" + e.imageDateTime);
//		System.out.println("ExposureBiasValue=" + e.imageExpBias);
//		System.out.println("FocalLength=" + e.imageFocalLength);
//		System.out.println("FocalLengthIn35mmFilm=" + e.image35FocalLength);
//		System.out.println("Flash=" + e.imageFlashMode);				
//    }

}
