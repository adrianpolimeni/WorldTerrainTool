/*
 * 	Adrian Polimeni
 *  11/08/2018
 * 	
 *  TerrainImage.java
 *  
 */
package com.adrianpolimeni.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.adrianpolimeni.main.Area;

public class TerrainImage {

	public final int EARTH_MIN = -420;	// Lowest on-land point on earth (shore of Black Sea)
	public final int EARTH_MAX = 8900;	// Highest point on earth (Summit of Mount. Everest)
	
	private short[][] data;
	private int maxHeight,minHeight;
	private String name;
	public Color unknownDataColor = Color.RED;
	private BufferedImage image;
	
	public TerrainImage(Area a) {
		 data = a.getHeightData();
		 minHeight = a.getLowestPoint();
		 maxHeight = a.getHighestPoint();
		 name = a.toString();
	}
	
	public TerrainImage(Area a, String name) {
		 data = a.getHeightData();
		 minHeight = a.getLowestPoint();
		 maxHeight = a.getHighestPoint();
		 this.name = name;
	}
	
	// Height to rgb color integer
	private int heightToColor(short height) {
		if(height==Short.MIN_VALUE) 
			return unknownDataColor.getRGB();
		if(height==0)
			return Color.BLUE.getRGB();
		if(height<minHeight)
			height = (short) minHeight;
		if(height>maxHeight)
			height = (short) maxHeight;
		float ratio = ((float)height-minHeight)/((float)maxHeight-minHeight);
		int value = (int)(255f*ratio);
		Color tempColor = new Color(0,value,0);
		return tempColor.getRGB();
	}
	
	//	Output the data as an image
	public boolean writeToImage() {
		image = new BufferedImage ( data.length, data[0].length, BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<data.length;i++) 
        		for(int j=0;j<data[i].length;j++) 
        			image.setRGB(i, j, heightToColor(data[j][i]));	
        try {
			ImageIO.write ( image, "png", new File ( "out/images/"+name+".png") );
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	
	
	
	

}
