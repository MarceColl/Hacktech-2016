package com.hacktech.projects;

public class Beat{
	double beatTime;
	int type;
	int status = 0;
	boolean barsToNext = false;
	boolean barredByPrev = false;
	public Beat(double bt, int ty){
		this.beatTime = bt;
		this.type = ty;
	}
}
