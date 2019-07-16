/**
 * Created by Olivier on 7/11/2016.
 */
public class Timing {

    private double BPM;
    private double beatInterval;
    private double fullBeatInterval;


    public Timing(double BPM){

        this.BPM = BPM;

        this.calcFullBeatInterval();
        this.calcBeatInterval();

    }


    /**
     * Returns the time between every position a note can appear
     */
    private void calcBeatInterval(){

        this.beatInterval = ((60/this.BPM)/4)*1000000000;

    }


    /**
     * Returns the time of a full beat interval. (interval*16)
     */
    private void calcFullBeatInterval(){

        this.fullBeatInterval = (((60/this.BPM))*1000000000)*16;

    }

    public double getFullBeatInterval() {

        return (Math.floor(this.fullBeatInterval));

    }

    public double get16thBeatInterval(){

        return Math.floor(this.beatInterval);

    }

}
