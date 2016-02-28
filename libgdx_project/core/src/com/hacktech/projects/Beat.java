package com.hacktech.projects;

public class Beat{
	double beatTime;
	int type;
	int status = 0;
	boolean barsToNext = false;
	boolean barredByPrev = false;
	boolean dotted = false;
	public Beat(double bt, int ty){
		this.beatTime = bt;
		this.type = ty;
		}
	public Beat(double bt, int ty, boolean dot){
		this.beatTime = bt;
		this.type = ty;
		this.dotted = dot;
	}
}
