import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by Olivier on 3/11/2016.
 */
public class KeyboardListener extends KeyAdapter {

    private Drum drum;
    private NotePool pool;


    public KeyboardListener(NotePool p) {

        this.drum = p.getDrum();
        this.pool = p;

    }



    public static int[] getKeyArrayKDDK(){

        return new int[]
                {
                        KeyEvent.VK_D,
                        KeyEvent.VK_F,
                        KeyEvent.VK_J,
                        KeyEvent.VK_K
                };

    }


    @Override
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();
        long thisTime = System.nanoTime();

        //70
        if (key == KeyEvent.VK_F && pool.getPlayerActive()) {
            if(!drum.checkSideActivity(1)) {
                drum.setHit(1, true, thisTime);
                pool.handleHit(key, thisTime, 1, 1);
            }
        }

        //74
        if (key == KeyEvent.VK_J && pool.getPlayerActive()) {
            if(!drum.checkSideActivity(2)) {
                drum.setHit(2, true, thisTime);
                pool.handleHit(key, thisTime, 1, 2);
            }
        }

        //68
        if (key == KeyEvent.VK_D && pool.getPlayerActive()) {
            if(!drum.checkSideActivity(0)) {
                drum.setHit(0, true, thisTime);
                pool.handleHit(key, thisTime, 2, 0);
            }
        }

        //75
        if (key == KeyEvent.VK_K && pool.getPlayerActive()) {
            if(!drum.checkSideActivity(3)) {
                drum.setHit(3, true, thisTime);
                pool.handleHit(key, thisTime, 2, 3);
            }
        }

        if(key == KeyEvent.VK_SPACE){
            pool.setDebug();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_F && pool.getPlayerActive()) {
            drum.setHit(1,false,0);
        }

        if (key == KeyEvent.VK_J && pool.getPlayerActive()) {
            drum.setHit(2,false,0);
        }

        if (key == KeyEvent.VK_D && pool.getPlayerActive()) {
            drum.setHit(0,false,0);
        }

        if (key == KeyEvent.VK_K && pool.getPlayerActive()) {
            drum.setHit(3,false,0);
        }
    }

}