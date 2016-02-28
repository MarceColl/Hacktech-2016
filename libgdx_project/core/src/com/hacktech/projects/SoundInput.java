package com.hacktech.projects;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.sound.sampled.*;
import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marce Coll on 27/02/16.
 */

public class SoundInput implements OnsetHandler, PitchDetectionHandler {
    // Audio helper classes
    Mixer soundMixer;
    com.hacktech.projects.BetterAudioDispatcher dispatcher;

    PitchProcessor.PitchEstimationAlgorithm algo;

    // Parameters
    double sensitivity = 35;
    double threshold = 5;

    final int sampleRate = 16000;
    final int bufferSize = 128;
    final int overlap = 0;

    int beat_counter = 0;
    long id_counter = 0;

    Note last_note;
    int last_note_counter = 0;
    int break_counter = 0;
    boolean note_sent = false;

    final int BREAK_THRESHOLD = 2;
    final int NOTE_THRESHOLD = 2;

    // NOTE PARAMETERS
    float noteOffset = 0.0f;
    Map<Note, Float> baseFreq = new EnumMap<Note, Float>(Note.class);
    float bpm;
    Note[] noteBuffer;
    int noteBufferCounter;


    com.hacktech.projects.PitchHandler handler;

    public SoundInput(float bpm, com.hacktech.projects.PitchHandler h) {
        // Setup the sound dispatcher
        try {
            setupDispatcher(bpm, h);
        } catch (LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("Shit is nuts yo!");
            e.printStackTrace();
        }
    }

    public void setupDispatcher(float bpm, com.hacktech.projects.PitchHandler h)
            throws LineUnavailableException,
            UnsupportedAudioFileException
    {
        // float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian
        final AudioFormat audioFormat = new AudioFormat(sampleRate, 16, 1, true, true);
        // Info about the sound data line
        final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

        // Pitch Recognition algorithm
        algo = PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET;

        // Base frequencies
        baseFreq.put(Note.F, 349.23f);
        baseFreq.put(Note.G, 392.00f);
        baseFreq.put(Note.A, 440.00f);

        handler = h;
        this.bpm = bpm;
        threshold = (int)Math.ceil(getTimeBetweenEighthNotes()*sampleRate/8/bufferSize)*0.4;

        noteBuffer = new Note[(int)Math.ceil(getTimeBetweenEighthNotes()*sampleRate/8/bufferSize)];


        // Setup music device and data line
        soundMixer = getMixerByName("Logitech Camera");
        if(soundMixer == null) {
            throw new LineUnavailableException();
        }

        TargetDataLine line;
        line = (TargetDataLine) soundMixer.getLine(dataLineInfo);
        line.open(audioFormat, sampleRate);
        line.start();
        final AudioInputStream stream = new AudioInputStream(line);

        JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);

        // The dispatcher is the manager of the device, it routes the data to audio processors
        dispatcher = new com.hacktech.projects.BetterAudioDispatcher(audioStream, bufferSize, overlap);

        dispatcher.addAudioProcessor(
                new PitchProcessor(algo, (float)sampleRate, bufferSize, this));

        // run the dispatcher (on a new thread).
        new Thread(dispatcher, "Audio dispatching").start();
    }

    public static void main(String... strings)
            throws InterruptedException,
            InvocationTargetException
    {
        SwingUtilities.invokeAndWait(() -> {
            System.out.println("Initializing");
            SoundInput si = new SoundInput(120, null);
        });
    }

    @Override
    public void handleOnset(double v, double v1) {
        System.out.println("Percussion at " + v);
    }

    // Callback that handles a pitch detection object
    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        if(handler.hasStarted()) {
            if (pitchDetectionResult.getPitch() != -1) {
                double timeStamp = audioEvent.getTimeStamp();
                float pitch = pitchDetectionResult.getPitch();

                noteBufferCounter++;
                if (noteBufferCounter == (int) Math.ceil(getTimeBetweenEighthNotes() * sampleRate / 8 / bufferSize)) {
                    noteBufferCounter = 0;
                    Note result = getAveragedNote();
                    noteBuffer = new Note[(int) Math.ceil(getTimeBetweenEighthNotes() * sampleRate / 8 / bufferSize)];

                    double timestamp = audioEvent.getTimeStamp();

                    if (result == last_note && result != Note.None) {
                        last_note_counter++;
                    } else {
                        break_counter++;
                    }

                    if (break_counter > BREAK_THRESHOLD) {
                        last_note_counter = 0;
                        break_counter = 0;
                        last_note = Note.None;
                        note_sent = false;
                    } else if (last_note_counter > NOTE_THRESHOLD && !note_sent) {
                        if (result != Note.None) {
                            BeatTouch bt = null;
                            if (result == Note.F) {
                                bt = new BeatTouch((timestamp - handler.getTimeOffset()) / 60 * 120, id_counter, 0);
                            } else if (result == Note.G) {
                                bt = new BeatTouch((timestamp - handler.getTimeOffset()) / 60 * 120, id_counter, 1);
                            } else if (result == Note.A) {
                                bt = new BeatTouch((timestamp - handler.getTimeOffset()) / 60 * 120, id_counter, 2);
                            }

                            System.out.println(result.name());
                            id_counter++;
                            handler.insertIntoQueue(bt);
                            note_sent = true;
                        }
                    } else {
                        last_note = result;
                    }
                } else {
                    Note note = getNoteByFrequency(pitch);
                    noteBuffer[noteBufferCounter - 1] = note;
                }
            }
        }
    }

    //////////////
    // PRIVATES //
    //////////////

    // Helper method to get a list of mixers
    private List<Mixer.Info> getMixers() {
        List<Mixer.Info> ret = new ArrayList<>();

        for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
            ret.add(mi);
        }
        return ret;
    }

    // Helper method to get a method by system name
    private Mixer getMixerByName(String name) {
        for(Mixer.Info mi : getMixers()) {
            if(mi.getName().equals(name)) {
                return AudioSystem.getMixer(mi);
            }
        }
        return null;
    }

    // Helper method to get a note by frequency, it takes into account
    // error range and offset
    private Note getNoteByFrequency(float freq) {
        float freqF = getFrequencyByNote(Note.F);
        float freqG = getFrequencyByNote(Note.G);
        float freqA = getFrequencyByNote(Note.A);
        float diffFG = freqG - freqF;
        float diffGA = freqA - freqG;
        if (freq < freqF - diffFG/2.0f || freq > freqA + diffGA/2.0f) {
            return Note.None;
        } else if (freq < freqG - diffFG/2) {
            return Note.F;
        } else if (freq < freqA - diffGA/2) {
            return Note.G;
        } else {
            return Note.A;
        }
    }

    // Helper method to get the base frequency of a note applying
    // the note offset if set
    private float getFrequencyByNote(Note n) {
        if(baseFreq.containsKey(n)) {
            return baseFreq.get(n) + noteOffset;
        }
        return -1.0f;
    }

    // Helper method to get number of eighth notes per second using bpm
    private float getEighthNotesPerSecond() {
        return 1.0f/getTimeBetweenEighthNotes();
    }

    // Helper method to get time between to eighth notes
    private float getTimeBetweenEighthNotes() {
        return 30.0f/bpm;
    }

    // Helper method that returns the best fitting note in
    // eigthth notes intervals
    private Note getAveragedNote() {
        int[] counter = new int[3];
        int max, max_index = -1;
        max = 0;
        for(Note n : noteBuffer) {
            if(n == Note.F) {
                counter[0]++;
                if (counter[0] > max){
                    max = counter[0];
                    max_index = 0;
                }
            }
            else if(n == Note.G) {
                counter[1]++;
                if (counter[1] > max){
                    max = counter[1];
                    max_index = 1;
                }
            }
            else if(n == Note.A) {
                counter[2]++;
                if (counter[2] > max){
                    max = counter[2];
                    max_index = 2;
                }
            }
        }

        if(max > threshold) {
            if(max_index == 0) {
                return Note.F;
            } else if (max_index == 1) {
                return Note.G;
            } else if (max_index == 2) {
                return Note.A;
            }
        }

        return Note.None;
    }
}
