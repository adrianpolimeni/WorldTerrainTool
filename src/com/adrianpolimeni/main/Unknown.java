/*
 * 	Adrian Polimeni
 *  11/08/2018
 * 	
 *  Unknown.java
 *  Subclass of Point.java
 *  Represents an unknown point in the area
 */
package com.adrianpolimeni.main;
import java.util.LinkedList;
import java.util.ListIterator;

public class Unknown extends Point{

	private LinkedList<Estimation> estimates;	//	List of estimates
	private float totalConfidence = 0;			// 	Sum of all confidences in the linked list
	
	public Unknown(int x, int y) {
		super(x,y);
		estimates = new LinkedList<Estimation>();
	}
	
	// 	finds the weighted average of the estimate heights 
	// 	based on the confidence of the estimation
	public short getHeightEstimation() {
		ListIterator<Estimation> it = estimates.listIterator();
		int height = 0;
		while(it.hasNext()) {
			Estimation curr = it.next();
			float currConfidence = curr.getConfidence()/totalConfidence;
			height+= currConfidence*curr.getValue();
		}
		return (short)height;	
	}
	
	//	Simply adds an estimation to the list
	//	and updates the total confidence
	public void addEstimation(Estimation estimate) {
		if(estimate!=null) {
			this.estimates.add(estimate);
			totalConfidence+=estimate.getConfidence();
		}
	}
}