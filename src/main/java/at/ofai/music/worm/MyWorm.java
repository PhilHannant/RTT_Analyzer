package at.ofai.music.worm;

/**
 * Created by philhannant on 08/07/2016.
 */
public class MyWorm {

    public static void main (String[] args){
        Worm w = new Worm();
        WormControlPanel wcp = new WormControlPanel(w);
        wcp.actionPerformed("Play");
    }
}
