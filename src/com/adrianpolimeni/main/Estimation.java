/*
 * 	Adrian Polimeni
 *  11/08/2018
 * 	
 *  Estimation.java
 *  Calculated estimation with height value and confidence
 *  
 */
package com.adrianpolimeni.main;

public class Estimation {
	private float value;
	private float confidence = 1f;
	
	public Estimation(float value) {
		this.value = value;
	}
	public Estimation(float value, float confidence) {
		this(value);
		this.confidence = confidence;
	}
	
	// Value is an calculated height value
	public double getValue() {
		return value;
	}
	
	// Confidence is a percent confidence in the estimated value
	public float getConfidence() {
		return confidence;
	}

	
	
}
