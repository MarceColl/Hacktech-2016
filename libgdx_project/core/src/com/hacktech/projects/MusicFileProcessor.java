package com.hacktech.projects;

import java.io.IOException;
import java.util.ArrayList;

import music.Note;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.mparser.musicXMLparserDH;

public class MusicFileProcessor {
	private final int BUCKET_SIZE = 5;
	
	public MusicFileProcessor(){
		
	}
	
	public Array<Beat> getBeatsFromFile(String fName){
		Array<Beat> beats = new Array<Beat>();
		musicXMLparserDH parser = null;
		try {
			parser = new musicXMLparserDH(fName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

		parser.parseMusicXML();
        ArrayList<Note> songSequenceOfNoteObjects = parser.getNotesOfSong();
        System.out.println("ho");
		for(Note n : songSequenceOfNoteObjects){
			double st = (double)n.getStartTime();
			
			int duration = n.getDuration();
			
			int type = 0;
			boolean dotted = false;
			
			switch(duration){
				case 1:
					type = 2;
					break;
				case 2: 
					type = 1;
					break;
				case 3:
					type = 1;
					dotted = true;
					break;
				case 4:
					type = 3;
					break;
				case 6:
					type = 3;
					dotted = true;
					break;
				case 8:
					type = 4;
					break;
					
			}
			
			String p = n.getPitch();
			int pitch = 0;
			if(p.equals("Z")){
				type = -type;
			}
			
			if(p.equals("F")){
				pitch = 0;
			}
			if(p.equals("G")){
				pitch = 1;
			}
			if(p.equals("A")){
				pitch = 2;
			}
			
			beats.add(new Beat(9 + st/2,type,pitch,dotted));
		}
        
        return beats;
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
		double ls;
		if (last.beatTime % finalBeat == 0){
			ls = last.beatTime + finalBeat - 4;
		}
		else
		{
			ls = last.beatTime + finalBeat - (last.beatTime % finalBeat);
		}
		
		blist.addAll(measureLines);
		blist.add(new Beat(ls+1,-5));
		
	}
	
	public IntMap<Array<Beat>> makeListIntoHashTable(Array<Beat> bList){
		IntMap<Array<Beat>> bMap = new IntMap<Array<Beat>>();
		for(int i = 0; i < bList.size; i++){
			Beat b = bList.get(i);
			int k = (int) Math.floor(b.beatTime / (double)this.BUCKET_SIZE);
			Array<Beat> bs = bMap.get(k, new Array<Beat>());
			bs.add(b);
			bMap.put(k, bs);
		}
		return bMap;
	}
	
}
