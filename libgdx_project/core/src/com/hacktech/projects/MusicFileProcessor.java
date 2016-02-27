package com.hacktech.projects;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class MusicFileProcessor {
	private final int BUCKET_SIZE = 5;
	
	public MusicFileProcessor(){
		
	}
	
	public void addMeasureLines(Array<Beat> blist){
		double beatSum = 0;
		
		double prevBeat = 1;
		
		double finalBeat = 4;
		
		Array<Beat> measureLines = new Array<Beat>();
		for(Beat b : blist){
			double delta = (b.beatTime - prevBeat);
			beatSum += delta;
			prevBeat = b.beatTime;
			
			if(Math.abs(beatSum - finalBeat) < 0.001){
				measureLines.add(new Beat(b.beatTime - delta/2.0,0));
				beatSum = 0;
			}
		}
		blist.addAll(measureLines);
		
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
