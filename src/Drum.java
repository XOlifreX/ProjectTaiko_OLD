import java.util.ArrayList;

/**
 * Created by Olivier on 3/11/2016.
 */
public class Drum {

    private boolean[] hitChecks;
    private boolean[] imageShowing;
    private long[] hitTime;
    private static final long DURATION_OF_IMAGE = 80000000;


    public Drum() {

        this.hitChecks = new boolean[]{false, false, false, false};
        this.imageShowing = new boolean[]{false, false, false, false};
        this.hitTime = new long[]{0, 0, 0, 0};

    }


    public void setHit(int index, boolean state, long time) {

        this.hitChecks[index] = state;

        if (state) {
            this.imageShowing[index] = true;
            this.hitTime[index] = time;
        } else {
            this.imageShowing[index] = false;
            this.hitTime[index] = 0;
        }

    }

    public int getNumberOfActiveHits() {

        int counter = 0;

        for (int i = 0; i < 4; i++) {

            if (this.hitChecks[i])
                counter++;

        }

        return counter;

    }

    public int getNumberOfActiveImages() {

        int counter = 0;

        for (int i = 0; i < 4; i++) {

            if (this.imageShowing[i])
                counter++;

        }

        return counter;


    }

    public void checkImageEnding(long time) {

        for (int i = 0; i < 4; i++) {
            if (this.imageShowing[i]) {
                if ((this.hitTime[i] + DURATION_OF_IMAGE) <= time)
                    this.imageShowing[i] = false;
            }
        }
    }

    public boolean checkImageToDraw(int index) {

        return this.imageShowing[index];

    }

    public boolean checkSideActivity(int index) {

        return this.hitChecks[index];

    }

}
