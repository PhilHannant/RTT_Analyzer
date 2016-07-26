package worm

import at.ofai.music.worm.AudioWorm


class Worm {

  def play = {

      audio = new AudioWorm


    audio.start
    this synchronized {
      notify
    }
  } // play()
  public void run() {
    // Code for play, executed as separate thread
    while (true) {
      // This is always running
      try {
        synchronized(this) {
          // wait until there is something to play
          wait();
        }
        Thread.sleep(200); // wait 0.2s
        try {
          while ((state == PLAY) && audio.nextBlock());
        } catch (ArrayIndexOutOfBoundsException e) {
          e.printStackTrace();
          audio.ti.showTime();
        }
        //System.out.println("loop ended " + (state != PLAY));
        if (state == PLAY) {
          // end of file; let audio drain
          //	audio.ti.saveHist();	//SD for dance test
          for (;
          wait > 0;
          wait --)
          {
            //	System.err.println("DEBUG: wait = " + wait);
            repaint();
            Thread.sleep((int) (AudioWorm.averageCount *
              AudioWorm.windowTime * 1000)); // wait 1.2s
          }
          //System.err.println("DEBUG: wait = " + wait);
          repaint();
          stop();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  } // run()
}
