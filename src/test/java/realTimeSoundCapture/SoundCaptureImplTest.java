package realTimeSoundCapture;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by philhannant on 20/06/2016.
 */
public class SoundCaptureImplTest {

    private SoundCaptureImpl sc;

    @Before
    public void setUp(){
        sc = new SoundCaptureImpl();
        sc.startCapture();
    }

//    Implementation changed so test no longer relevant
//    @Test
//    public void testStartCapture(){
//        int bytesread = sc.startCapture();
//        System.out.println(bytesread);
//        assertTrue(bytesread > 0);
//    }

    @Test
    public void cbTest(){
        Object obj = sc.getNext();
        assertTrue(obj != null);
    }

    @Test
    public void circularitytest(){
        for(int i = 0; i < 11; i++){
            sc.getNext();
        }
        int bufferNumber = sc.getReadPos();
        assertEquals(bufferNumber, 1);
    }

    @After
    public void finish() throws IOException {
        sc.close();
    }
}