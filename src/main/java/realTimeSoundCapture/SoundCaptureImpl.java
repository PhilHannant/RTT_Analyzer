package realTimeSoundCapture;

import javax.sound.sampled.*;
import java.io.*;

/**
 * Created by philhannant on 20/06/2016.
 */
public class SoundCaptureImpl implements SoundCapture{

    private AudioFormat format;
    private TargetDataLine input;
    private AudioInputStream inputStream;
    private SourceDataLine sourceDataLine;
    private DataLine dataLine;
    private ByteArrayOutputStream outputStream;
    private int bytesRead;
    private long recordLength = 30000; //30 second length used for testing purposes


    public SoundCaptureImpl(){
        float sampleRate = 44100;
        int bitsPerSample = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        format = new AudioFormat(sampleRate, bitsPerSample, channels, signed, bigEndian);
    }

    public void startCapture(){


    }
}
