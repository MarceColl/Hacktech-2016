package com.hacktech.projects;

public class Beat{
	double beatTime;
	boolean matched = false;
	/*
	 * Type: 
	 * 		0 = Measure
	 * 		1 = Quarter Note
	 * 		2 = Eighth Note
	 * 		3 = Half Note
	 * 		4 = Whole Note
	 * 	   -1 = Quarter Rest
	 * 	   -2 = Eighth Rest
	 * 	   -3 = Half Rest
	 *     -4 = Whole Rest
	 * 	   -5 = End of notes
	 */
	int type;
	int status = 0;
	/*
	 * Pitch Number:
	 * 		0 = Concert F
	 * 		1 = Concert G
	 * 		2 = Concert A
	 */
	int pitchNumber = 0;
	int barNote = 2;
	boolean barsToNext = false;
	boolean barredByPrev = false;
	boolean dotted = false;
	public Beat(double bt, int ty){
		this.beatTime = bt;
		this.type = ty;
	}
	public Beat(double bt, int ty, int pi){
		this.beatTime = bt;
		this.type = ty;
		this.pitchNumber = pi;
	}
	public Beat(double bt, int ty, boolean dot){
		this.beatTime = bt;
		this.type = ty;
		this.dotted = dot;
	}
	public Beat(double bt, int ty, int pi, boolean dot){
		this.beatTime = bt;
		this.type = ty;
		this.pitchNumber = pi;
		this.dotted = dot;
	}
}
