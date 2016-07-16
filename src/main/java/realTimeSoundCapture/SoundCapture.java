package realTimeSoundCapture;

import javax.sound.sampled.*;
import java.io.*;

/**
 * Created by philhannant on 20/06/2016.
 */
public class SoundCapture {

    private AudioFormat format;
    private float SAMPLE_RATE = 44100;
    private int BITS_PER_SAMPLE = 16;
    private int CHANNELS = 2;
    private boolean SIGNED = true;
    private boolean BIG_ENDIAN = true;
    private TargetDataLine input;
    private AudioInputStream inputStream;
    private SourceDataLine dataLine;


    public SoundCapture(){

    }
}
