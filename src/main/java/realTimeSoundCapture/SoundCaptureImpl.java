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
    private int streamedBytes;
    private int sampleSize;
    private byte[] data;
    private long recordLength = 5000; //5 second length used for testing purposes


    public SoundCaptureImpl(){
        sampleSize = 1024;
        float sampleRate = 44100;
        int bitsPerSample = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        format = new AudioFormat(sampleRate, bitsPerSample, channels, signed, bigEndian);
    }

    public int startCapture(){
        try {
            input = AudioSystem.getTargetDataLine(format);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            input = (TargetDataLine) AudioSystem.getLine(info);
            input.open(format);
            outputStream = new ByteArrayOutputStream();
            data = new byte[input.getBufferSize()];
            input.start();
            long currentTime = System.currentTimeMillis();
            long finishTime = currentTime + recordLength;
            bytesRead = 0;
            while(System.currentTimeMillis() < finishTime){

                streamedBytes = input.read(data, 0, sampleSize);
                bytesRead += streamedBytes;

                outputStream.write(data, 0, streamedBytes);
            }
            return bytesRead;
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
