/*
 * 	Adrian Polimeni
 *  11/08/2018
 * 	
 *  FillUnknownsTest.java
 *  
 */
package com.adrianpolimeni.test;

import com.adrianpolimeni.image.TerrainImage;
import com.adrianpolimeni.main.Area;

public class FillUnknownsTest {

	public static void main(String args[]) {
		new FillUnknownsTest();
	}
	
	//	Read an area from /in/N51W116.hgt 	
	//	Output two images of the terrain with unknown values and another with estimated values
	public FillUnknownsTest() {
		System.out.println("Creating Area...");
		Area testA = new Area(51,116);	// Accesses file "N51W116.hgt"
		System.out.println("Drawing image...");
		TerrainImage drawWithUnknowns = new TerrainImage(testA,"N51W116_Unknowns");	
		drawWithUnknowns.writeToImage();	
		System.out.println("Estimating Unknowns...");
		testA.fillUnknowns();
		testA.writeToText();
		System.out.println("Drawing image...");
		TerrainImage drawWithEstimates = new TerrainImage(testA,"N51W116_Estimates");		
		drawWithEstimates.writeToImage();
		System.out.println("Process Complete!");	
	}
	
}
