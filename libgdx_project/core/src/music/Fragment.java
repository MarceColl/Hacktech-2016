package music;


import java.util.*;
import java.lang.String;
import java.lang.Iterable;


import music.*;

/**
 * Created by dorien on 10/11/14.
 */
public class Fragment {


    private Integer length;
    private Map<Integer, Note> eventsInFragment = new HashMap();


    private Type type;
//    private double score;

    private ArrayList<Note> songList;

    public Type getType() {
        return type;
    }

    public Fragment(Fragment original) {
        //constructor to duplicate a fragemnt
        this.length = original.length;
        this.eventsInFragment = original.eventsInFragment;

        this.type = original.type;
//        this.score = original.score;

    }





    public void setTemplate(ArrayList<Note> allNotes){

        eventsInFragment.clear();

        if (length != allNotes.size()){
            System.out.println("allnotes not the right length"+length +" " + allNotes.size());
        }

        //for each length
        for(int i = 0 ; i < length; i++) {

            //create event

            //no need to create event when using allnotes

                //store in hashmap, with id
                eventsInFragment.put(allNotes.get(i).getId(), allNotes.get(i));
        }

//        updateScore();

        //fill songlist

//        songList.clear();
        this.songList = new ArrayList<Note>();
        this.songList = (ArrayList<Note>) allNotes.clone();
    }



    public void setRandom(Type type){

        this.type = type;

        eventsInFragment.clear();

        //for each length
        for(int i = 0 ; i < length; i++) {

            //create event

            //set random properties base on type
            //TODO

            if (type == Type.Note){

                Note e = new Note(i, "nonparsed");

                e.setRandom();

                //store in hashmap, with id
                eventsInFragment.put(e.getId(), e);

            }
        }

//        updateScore();

        //fill songlist

//        songList.clear();
        songList = new ArrayList<Note>();

        for (int i = 0; i < eventsInFragment.size(); i++){
            songList.add(i, eventsInFragment.get(i));
        }
    }


    public ArrayList<Note> getSongList() {
        return songList;
    }

    public void print(){
        for (Map.Entry<Integer,Note> entry : eventsInFragment.entrySet()) {
            Integer key = entry.getKey();
            Note value = entry.getValue();
            // do stuff
           // if (type == Type.Note){
                System.out.println(value.getId() + " " + ((Note) value).getDuration() + " ");// + (((Note)value).getPitch() + " ");
            //}

        }
//        System.out.println("score: " + getScore());



    }




    public Map<Integer, Note> getEventsInFragment() {
        return eventsInFragment;
    }
}
