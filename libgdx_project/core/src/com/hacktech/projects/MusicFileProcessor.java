package com.hacktech.projects;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class MusicFileProcessor {
	private final int BUCKET_SIZE = 5;
	
	public MusicFileProcessor(){
		
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
