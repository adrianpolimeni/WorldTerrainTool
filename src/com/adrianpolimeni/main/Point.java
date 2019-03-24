/*
 * 	Adrian Polimeni
 *  11/08/2018
 * 	
 *  Point.java
 *  
 */
package com.adrianpolimeni.main;

public class Point {
	//	Contains coordinate values, and height values
	private int x,y;
	private short height;
	
	public Point(int x,int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public short getHeight() {
		return height;
	}
	public void setHeight(short height) {
		this.height = height;
	}
	
	public int get(int i) {
		if(i==0)
			return x;
		return y;
	}
	
	//	Picks the coordinate to set
	public void set(int i, int pick) {
		if(i==0)
			x=i;
		else if (i==1)
			y=i;
	}

}
