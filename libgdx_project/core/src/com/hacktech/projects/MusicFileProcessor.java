package com.hacktech.projects;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class MusicFileProcessor {
	private final int BUCKET_SIZE = 5;
	
	public MusicFileProcessor(){
		
	}
	
	public void addMeasureLines(Array<Beat> blist){
		double beatSum = 0;
		Beat prevBeat = new Beat(1.0,0);
		double finalBeat = 4;
		
		Array<Beat> measureLines = new Array<Beat>();
		for(int i = 0; i < blist.size; i++){
			Beat b = blist.get(i);
			double delta = (b.beatTime - prevBeat.beatTime);
			beatSum += delta;
			
			if(delta == 0.5 && (int)Math.floor(prevBeat.beatTime) == (int)Math.ceil(prevBeat.beatTime)){
				if(prevBeat.type == 2 && b.type == 2){
					prevBeat.barsToNext = true;
					b.barredByPrev = true;
				}
			}
			
			prevBeat = b;
			
			if(Math.abs(beatSum - finalBeat) < 0.001){
				measureLines.add(new Beat(b.beatTime - Math.min(delta/2.0,.5),0));
				beatSum = 0;
			}
		}
		
		Beat prev = new Beat(0.0,0);
		for(int i = 0; i < blist.size; i++){
			Beat b = blist.get(i);
			if(b.barsToNext && prev.barredByPrev){
				//Check if beatTime falls midway through the measure
				 if((b.beatTime%finalBeat)%2 == 0){
					 prev.barsToNext = true;
					 b.barredByPrev = true;
				 }
			}
			
			prev = b;
		}
		
		Beat last = blist.get(blist.size - 1);
		double ls = last.beatTime + finalBeat - (last.beatTime % finalBeat);
		
		blist.addAll(measureLines);
		blist.add(new Beat(ls+1,-5));
		
	}
	
	public IntMap<Array<Beat>> makeListIntoHashTable(Array<Beat> bList){
		IntMap<Array<Beat>> bMap = new IntMap<Array<Beat>>();
		for(Beat b : bList){
			int k = (int) Math.floor(b.beatTime / (double)this.BUCKET_SIZE);
			Array<Beat> bs = bMap.get(k, new Array<Beat>());
			bs.add(b);
			bMap.put(k, bs);
		}
		return bMap;
	}
	
}
