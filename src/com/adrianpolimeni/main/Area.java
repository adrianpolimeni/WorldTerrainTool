/*
 * 	Adrian Polimeni
 *  11/08/2018
 * 	
 *  Area.java
 *  Represents the height data of a .hgt file
 */
package com.adrianpolimeni.main;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import Jama.Matrix;

public class Area {
	//	Use the following constants to pick which 
	//	axis we are applying spline interpolation to
	static final int HORIZONTALLY = 0;
	static final int VERTICALLY = 1;
	
	private ArrayList<Unknown> unknowns;
	private short[][] heightData;
	private short lowestPoint, highestPoint;
	private int length;
	private InputStream is;
	private String name;
	public boolean hasUnknowns = true;
	
	//TODO 
	// Change area parameter just array of shorts
	public Area(float N, float W) {			
		getData(new File("in/N"+((int)N)+"W"+(int)W+".hgt"));
	}
	public Area(String fileName) {
		getData(new File(fileName));
	}
	public Area(File file) {
		getData(file);
	}
	
	// Decodes the 2 byte values from the file into an array of shorts 
	private boolean getData(File hgt) {
		lowestPoint = Short.MAX_VALUE;
		highestPoint = Short.MIN_VALUE;
		name = hgt.getName();
		int size=0;
		try {
			is = new FileInputStream(hgt);
			size = is.available();
			length = (int)Math.sqrt(size/2);		// size is number of bytes, divided by 2 to get number of values
			heightData = new short[length][length];
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			return false;
		} catch (IOException e) {
			System.out.println("File input error!");
			return false;
		}
		byte[] value = new byte[2];
		int y = 0;
		int x=0;
		for(int i=0;i<size;i+=2) {
			try {
				is.read(value, 0, 2);
			} catch (IOException e) {
				System.out.println("Read error");
				return false;
			}
			ByteBuffer wrapped = ByteBuffer.wrap(value); // Big-Endian by default
			short num = wrapped.getShort(); // 1
			heightData[x][y] = num;
			if(num<lowestPoint && num!=Short.MIN_VALUE)	// Short.MIN_VALUE is unknown data
				lowestPoint = num;
			if(num>highestPoint)
				highestPoint = num;
			y++;
			if(y==1201) {
				y=0;
				x++;
			}
		}	
		return true;
	}
	
	
	//	Replaces all unknown values with an estimation
	public short[][] fillUnknowns(){
		unknowns = new ArrayList<Unknown>();
		if (heightData==null) {return null;}
		
		//	First, populate a list of unknown points
		for (int i=0;i<length; i++) 
			for(int j=0; j<length;j++) 
				if(isUnknown(i,j)) 
					unknowns.add(new Unknown(i,j));
		
		//	Iterate through the list of unknown points to then calculate a estimation
		for (int i=0;i<unknowns.size(); i++) {
			Unknown temp = unknowns.get(i);
			Point[] adjacentIndices;
			//System.out.println("\tCalculating unknown at ("+temp.getX()+", "+temp.getY()+")");
			if(temp.getY()!=0 && temp.getY()!=length-1) {	// can do a X estimation
				adjacentIndices = findAdjacent(temp.getX(),temp.getY(),HORIZONTALLY);
				splineInterpolation(adjacentIndices,HORIZONTALLY);
			}
			if(temp.getX()!=0 && temp.getX()!=length-1) {	// can do a Y estimation
				adjacentIndices = findAdjacent(temp.getX(),temp.getY(),VERTICALLY);
				splineInterpolation(adjacentIndices,VERTICALLY);
			}
			short tempHeight = temp.getHeightEstimation();	
			heightData[temp.getX()][temp.getY()] = tempHeight;
		}
		hasUnknowns = false;
		return heightData;
	}
	
	//	Search by x & y coordinate (index)
	private Unknown search(int x, int y) {
		for(int i=0;i<unknowns.size();i++) {
			Unknown temp = unknowns.get(i);
			if(temp.getX()==x && temp.getY()==y)
				return temp;
		}
		return null;
	}
	
	
	private boolean splineInterpolation(Point[] indices, int pick) {
		// Do the first formula to calculate a and b value (k1, k2)
		// Then have another method for calculating each
		int[] x = new int[3];
		int[] y = new int[3];
		double[][] a = new double[3][3];
		double[] b = new double[3];
		try {
			for(int i=0;i<3;i++) {
				x[i] = indices[i].get(pick);	
				y[i] = indices[i].getHeight();
			}
		}catch(NullPointerException e) {
			return false;
		}
		a[0][0] = 2d/(x[1]-x[0]);
		a[0][1] = 1d/(x[1]-x[0]);
		
		a[1][0] = a[0][1];
		a[1][2] = 1d/(x[2]-x[1]);
		a[1][1] = 2d * (a[1][0]+a[1][2]);
		
		a[2][1] = a[1][2]; 
		a[2][2] = 2d/(x[2]-x[1]);

		b[0] = 3d * (y[1]-y[0])/Math.pow((x[1]-x[0]), 2d);
	    b[2] = 3d * (y[2]-y[1])/Math.pow((x[2]-x[1]), 2d);
		b[1] = b[0] + b[2];
		
		Matrix matrixA = new Matrix(a);
		Matrix matrixB = new Matrix(b,3);
		Matrix matrixK = matrixA.solve(matrixB);
		
		double[][] k = matrixK.getArray();
		double a2 = (k[1][0]) * (x[2] - x[1]) - (y[2] - y[1]);
		double b2 = (-k[2][0]) * (x[2] - x[1]) + (y[2] - y[1]);
		int nonPick = indices[0].get(-1*pick + 1);
		
		for(int i = x[0]+1;i<x[2];i++) {
			if(i!=x[1]) {
				Unknown temp;
				if(pick==HORIZONTALLY)
					temp = search(i,nonPick);
				else
					temp = search(nonPick,i);
				temp.addEstimation(new Estimation((float)getValue(i,x,y,a2,b2),calcConfidence(i,x)));
			}	
		}
		return true;
	}
	
	//	Helper method for calculating confidence of an estimation
	//	The closer an unknown point is to a known point, 
	// 	the higher the confidence is in the estimation   
	private float calcConfidence(int index,int[] x) {
		int v1 = index - x[0];
		int v2 = x[2] - index;
		int v3 = x[1] - index;
		int large = x[2]-x[0];
		float avg = Math.abs(v3);
		if (v3>0)
			avg+=v2;
		else 
			avg+=v1;
		avg/=2;
		return (large-avg)/large;	// 0.5 at worst. ~0.99 at best
	}
	
	//	Helper method for spline interpolation method
	//	This gets the estimated height value at the given index
	public double getValue(int index,int[] x, int y[], double a2, double b2) {
		try {
			double t = (index - x[1])/(x[2]-x[1]);
			double out = (1-t)*y[1] + t*y[2] + t*(1-t)*(a2*(1-t)) + b2*t;
			return out;
		} catch (ArrayIndexOutOfBoundsException e) {
			return Double.MIN_VALUE;
		}
	}
	
	//	Finds 3 points that are adjacent to unknown values
	private Point[] findAdjacent(int x, int y, int pick) { 	// Pick: 0 uses x, 1 uses y
		int[] indices  = {x,y};
		Point[] adjacentKnowns = new Point[4];
		Point[] out = new Point[3];
		int adjacentKnownSize = 0;
		int centre = indices[pick];
		
		indices[pick]--;
		while(indices[pick] >= 0 && adjacentKnownSize<2) {
			if(!isUnknown(indices[0],indices[1])) {
				adjacentKnowns[adjacentKnownSize] = new Point(indices[0],indices[1]);
				adjacentKnowns[adjacentKnownSize].setHeight(heightData[indices[0]][indices[1]]);
				adjacentKnownSize++;
			}
			indices[pick]--;
		}
		
		indices[pick] = centre+1;
		
		while(indices[pick] < length && adjacentKnownSize<4) {
			if(!isUnknown(indices[0],indices[1])) {
				adjacentKnowns[adjacentKnownSize] = new Point(indices[0],indices[1]);
				adjacentKnowns[adjacentKnownSize].setHeight(heightData[indices[0]][indices[1]]);
				adjacentKnownSize++;
			}
			indices[pick]++;
		}
		
		if(adjacentKnownSize<3)
			return null;
		
		boolean closest = false;
		if(adjacentKnownSize>3)
			closest = !(Math.abs(adjacentKnowns[3].get(pick) - centre) < (centre - adjacentKnowns[1].get(pick)));
		int counter = 0;
		if(closest) 
			out[counter++] = adjacentKnowns[1];
		out[counter++] = adjacentKnowns[0];
		out[counter++] = adjacentKnowns[2];
		if(!closest) 
			out[counter] = adjacentKnowns[3];
		return out;
	}
	
	//	Returns if the height value is unknown
	//	An unknown value will be equal to Short.MIN_VALUE
	public boolean isUnknown(int x, int y) {
		try {
			return heightData[x][y] == Short.MIN_VALUE;	
		}catch(ArrayIndexOutOfBoundsException e) {
			return true;
		}
	}
	
	// Getters
	public short[][] getHeightData() {
		return heightData;
	}
	
	public short getLowestPoint() {
		return lowestPoint;
	}
	
	public short getHighestPoint() {
		return highestPoint;
	}
	
	// Writes the data to a text file
	public boolean writeToText() {
		BufferedWriter output = null;
		try {
            File file = new File("out/text/"+this.toString()+".txt");
            output = new BufferedWriter(new FileWriter(file));
            for(int i = 0;i<heightData.length;i++){
            		for(int j = 0;j<heightData[i].length;j++) {
            			if(j!=0)
            				output.write(" ");
            			output.write(""+heightData[i][j]);
            		}
            		output.write("\n");
            }
        } catch ( IOException e ) {
            e.printStackTrace();
            return false;
        } finally {
            if ( output != null )
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;		
				}
        }	
		return true;
	}
	
	
	@Override
	public String toString(){
		return name.substring(0, name.length()-4);
	}
}
