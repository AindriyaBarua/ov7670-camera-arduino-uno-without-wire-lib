

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BMP {
	byte [] bytes;
	
	public int[][] readBMP(String fileName) {
		byte[]buf = new byte[54];		
		int[][]rgb = null; 
		
		try {
			FileInputStream fos = new FileInputStream(new File(fileName));
			fos.read(buf, 0, buf.length);
			
			int width = ((buf[21]&0xFF) << 24) + ((buf[20]&0xFF) << 16) + ((buf[19]&0xFF) << 8) + (buf[18]&0xFF);
			int height = ((buf[25]&0xFF) << 24) + ((buf[24]&0xFF) << 16) + ((buf[23]&0xFF) << 8) + (buf[22]&0xFF);
			
			rgb = new int[height][width];
			
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					fos.read(buf, 0, 3);
					rgb[y][x] = ((buf[2]&0xFF) << 16) + ((buf[1]&0xFF) << 8) + (buf[0]&0xFF);
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		
		return rgb;
	}
	
	public void saveBMP(String filename, int [][] rgbValues){
		try {
			FileOutputStream fos = new FileOutputStream(new File(filename));
			
			bytes = new byte[54 + 3*rgbValues.length*rgbValues[0].length];

			saveFileHeader();
			saveInfoHeader(rgbValues.length, rgbValues[0].length);
			saveBitmapData(rgbValues);

			fos.write(bytes);
			
			fos.close();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private void saveFileHeader() {
		bytes[0]='B';
		bytes[1]='M';
		
		bytes[5]=(byte) bytes.length;
		bytes[4]=(byte) (bytes.length>>8);
		bytes[3]=(byte) (bytes.length>>16);
		bytes[2]=(byte) (bytes.length>>24);
		
		//data offset
		bytes[10]=54;
	}
	
	private void saveInfoHeader(int height, int width) {
		bytes[14]=40;

		bytes[18]=(byte) width;
		bytes[19]=(byte) (width>>8);
		bytes[20]=(byte) (width>>16);
		bytes[21]=(byte) (width>>24);

		bytes[22]=(byte) height;
		bytes[23]=(byte) (height>>8);
		bytes[24]=(byte) (height>>16);
		bytes[25]=(byte) (height>>24);

		bytes[26]=1;
		
		bytes[28]=24;
	}
	
	private void saveBitmapData(int[][]rgbValues) {
		for(int i=0;i<rgbValues.length;i++){
			writeLine(i, rgbValues);
		}
	}
	
	private void writeLine(int row, int [][] rgbValues) {
		final int offset=54;
		final int rowLength=rgbValues[row].length;
		for(int i=0;i<rowLength;i++){
			int rgb=rgbValues[row][i];
			int temp=offset + 3*(i+rowLength*row);
			
			bytes[temp + 2]    = (byte) (rgb>>16);
			bytes[temp +1] = (byte) (rgb>>8);
			bytes[temp] = (byte) rgb;
		}
	}
}

