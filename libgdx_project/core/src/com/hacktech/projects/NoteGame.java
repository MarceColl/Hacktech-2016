package com.hacktech.projects;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;

public class NoteGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture staffBg;
	Texture quarter;
	Texture line_blue;
	Texture line_black;
	private final double BPM = 60;
	MusicFileProcessor mfp;
	
	long startTime = TimeUtils.millis();
	Queue<BeatTouch> beatInput = new Queue<BeatTouch>();
	IntMap<Array<Beat>> songHash = new IntMap<Array<Beat>>(); 
	@Override
	public void create () {
		mfp = new MusicFileProcessor();
		batch = new SpriteBatch();
		staffBg = new Texture("staffbg.png");
		quarter = new Texture("quarter.png");
		line_blue = new Texture("line_blue.png");
		line_black = new Texture("line_black.png");
		
		Array<Beat> beatSheet = new Array<Beat>();
		
		beatSheet.add(new Beat(1,1));
		beatSheet.add(new Beat(2,1));
		beatSheet.add(new Beat(3,1));
		beatSheet.add(new Beat(4,1));
		beatSheet.add(new Beat(4.5,1));
		beatSheet.add(new Beat(5,1));
		beatSheet.add(new Beat(6,1));
		beatSheet.add(new Beat(7,1));
		beatSheet.add(new Beat(8,1));
		beatSheet.add(new Beat(9,1));
		
		mfp.addMeasureLines(beatSheet);
		
		songHash = mfp.makeListIntoHashTable(beatSheet);
	}

	@Override
	public void render () {
		long timeElapsed = TimeUtils.millis() - startTime;
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		System.out.println(timeElapsed);
		
		batch.begin();
		
		/*
		 * DRAW THE STAFF / ANY BACKGROUND
		 */
		batch.draw(staffBg, 0, 60, 500, 100);
		
		/*
		 * RENDER THE BEATS
		 */
		
		double min = (double)timeElapsed / (60.0*1000.0);
		double beatsElapsed = BPM*min;
		
		Array<Beat> beatWindow = songHash.get((int)Math.floor(beatsElapsed/5.0));
		if(beatWindow != null){
			if((int)Math.ceil(beatsElapsed/5.0) != (int)Math.floor(beatsElapsed/5.0)){
				Array<Beat> b2 = songHash.get((int)Math.ceil(beatsElapsed/5.0));
				if(b2 != null){
					beatWindow.addAll(b2);
				}
			}
		
			double bWindow = 4;
			double bEnd = beatsElapsed+bWindow;
			for(Beat b : beatWindow){
				if(b.beatTime > beatsElapsed && b.beatTime < bEnd){
					double x = 50.0 + (400.0*(b.beatTime - beatsElapsed) / bWindow);
					if(b.type == 0){
						batch.draw(line_black, (float)x + 20, 82,3,70);
					}
					else if(b.type == 1){
						batch.draw(quarter, (float)x, 75, 40, 1.9f*40.0f);
					}
					
					
				}
			}
		}

		/*
		 * Render beat line
		 */
		batch.draw(line_blue, 68,82,3,70);
		
		
		batch.end();
	}
}

class BeatTouch{
	long timeStamp;
	long id;
}
