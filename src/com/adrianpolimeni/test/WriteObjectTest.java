package com.adrianpolimeni.test;

import com.adrianpolimeni.main.Area;
import com.adrianpolimeni.model.TerrainModel;

public class WriteObjectTest {

	public static void main(String args[]) {
		new WriteObjectTest();
	}
	
	public WriteObjectTest() {
		long start = System.currentTimeMillis();
		System.out.println("Creating Area...");
		Area testA = new Area(51,116);	// Accesses file "N51W116.hgt"
		System.out.println("Estimating Unknowns...");
		testA.fillUnknowns();
		System.out.println("Creating .obj");
		TerrainModel testTM = new TerrainModel(testA);
		testTM.writeObj(0,0.3f,0,0.05f);
		System.out.println("Process Complete!");
		float timeOut = ((float)(System.currentTimeMillis() - start))/1000f;
		System.out.println("Process Time: "+timeOut);	
	}
}
