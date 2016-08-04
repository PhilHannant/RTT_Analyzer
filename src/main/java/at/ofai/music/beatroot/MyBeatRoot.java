package at.ofai.music.beatroot;

/**
 * Created by philhannant on 04/08/2016.
 */
public class MyBeatRoot {

    public static void main(String[] args){
        BeatRoot b = new BeatRoot(args);
        b.gui.loadAudioData("/Users/philhannant/Desktop/Wavs/D128.wav");
        b.gui.displayPanel.beatTrack();

    }
}
