package realTimeSoundCapture;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by philhannant on 20/06/2016.
 */
public class SoundCaptureImplTest {

    private SoundCaptureImpl sc;

    @Before
    public void setUp(){
        sc = new SoundCaptureImpl();
    }

    @Test
    public void testStartCapture(){
        int bytesread = sc.startCapture();
        assertTrue(bytesread > 0);
    }

}