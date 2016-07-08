package TempoTests;

import at.ofai.music.worm.WormControlPanel;
import at.ofai.music.worm.Worm;
import org.junit.Test;

import java.awt.event.ActionEvent;

/**
 * Created by philhannant on 08/07/2016.
 */
public class PerformanceWormTests {

    @Test
    public void RunBR(){
        Worm w = new Worm();
        WormControlPanel wcp = new WormControlPanel(w);
        wcp.actionPerformed("Play");



    }
}
