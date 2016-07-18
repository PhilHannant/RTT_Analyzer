package realTimeSoundCapture;

import java.io.IOException;

/**
 * Created by philhannant on 16/07/2016.
 */
public interface SoundCapture {

    int startCapture();

    Object getNext();

    void writeNext(Object data);

    void close() throws IOException;
}
