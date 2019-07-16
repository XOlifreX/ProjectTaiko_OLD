import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by Olivier on 23/11/2015.
 */

/*Website voor key inputs te vinden: http://docs.oracle.com/javase/6/docs/api/constant-values.html#java.awt.event.KeyEvent.VK_0*/

public class Note extends NotePool{

    private int noteType;
    private double approachSpeed;
    private String noteImage;
    private String noteImageHappy;
    private String noteHitSound;
    private int[] correctKeys = new int[2];
    private long  hitTimePerfect;
    private double x_loc, y_loc;
    public boolean isHit;
    private int isHitCheck = 0; //0 = not hit yet   // 1 = hit     //2 = missed/wrong collour
    private int noteNumber;
    private boolean isLinePartner;
    private long startHitShower;
    private long coolDownTime; //Time before a new position update may happen (is used for Approach Rate)
    private double scrollManipulator; //A number (> 1 when it's going faster, < 1 when it's going slower) that says how fast a note is moving
    private long noteRenderTimeCheck = 0L;

    private final int hitShowDuration = 100;
    private static final double FINAL_SPEED = 1;
    private static final long FIXED_NOTE_COOLDOWN_TIME = 2900000; //3ms
    private static int noteCount = 0;


    /*-----------------------------------------------------------------------------------------*/


    {

        noteCount++;

    }

    public Note(int noteType, long  hitTimePerfect){

        this.noteType = noteType;
        this.setNoteImage();
        this.setKeys();
        this.hitTimePerfect = hitTimePerfect;
        this.x_loc = 530;
        this.y_loc = this.calcX_loc(this.noteType);
        this.approachSpeed = FINAL_SPEED;
        this.noteNumber = this.noteCount;

    }

    public Note(int noteType, long  hitTimePerfect, int scrollManipulator){

        this.noteType = noteType;
        this.scrollManipulator = scrollManipulator;
        this.setNoteImage();
        this.setKeys();
        this.hitTimePerfect = hitTimePerfect;
        this.x_loc = 530;
        this.y_loc = this.calcX_loc(this.noteType);
        this.approachSpeed = FINAL_SPEED;
        this.noteNumber = this.noteCount;

    }

    public Note(int noteType, long  hitTimePerfect, int approachSpeed, int key1, int key2){

        this.noteType = noteType;
        this.approachSpeed = approachSpeed;
        this.setNoteImage();
        this.setKeys(key1, key2);
        this.x_loc = 530;
        this.y_loc = this.calcX_loc(this.noteType);

    }

    public Note(int noteType, long hitTimePerfect, int key1, int key2){

        this.noteType = noteType;
        this.approachSpeed = FINAL_SPEED;
        this.setNoteImage();
        this.setKeys(key1, key2);
        this.x_loc = 530;
        this.y_loc = this.calcX_loc(this.noteType);

    }


    /*-----------------------------------------------------------------------------------------*/

    //TODO: think of something to make this happen.
    private void calculateNoteCooldownTime(){

        /**
         * This formula uses the fixed note cooldown time to calculate the time that's needed
         * before the x-position of this note can to be incremented.
         * It is calculated with the BPM and an extra scroll speed modifier if specified.
         */



    }

    public static int getNoteCount(){

        return noteCount;

    }

    private int calcX_loc(int noteType){

        return 11;

    }

    public boolean checkInterval(long startTime, long currentTime){

        if(((this.getPerfectHitTime() + this.getHitPrecision()) >= (currentTime - startTime))
                && ((this.getPerfectHitTime() - this.getHitPrecision()) <= (currentTime - startTime))){

            return true;

        }

        return false;

    }

    public boolean checkNoteTooLate(long startTime, long currentTime){

        return (this.getPerfectHitTime() + this.getHitPrecision()) < (currentTime - startTime);

    }

    public int checkHit(int keyInput, long startTime, long currentTime){

        if(this.checkInterval(startTime,currentTime)) {
            for(int keys : correctKeys) {

                if (keyInput == keys) {
                    this.isHitCheck = 1;

                    return 1;
                }

            }

            return 2;

        }

        return 0;

    }

    public void checkHit(int situation){

        this.isHitCheck = 2;
        this.isHit = true;

    }

    public boolean isHit() {

        return this.isHit;

    }

    public long getPerfectHitTime(){

        return this.hitTimePerfect;

    }

    public void updateNoteLocation(){

        if(System.nanoTime()-this.noteRenderTimeCheck >= FIXED_NOTE_COOLDOWN_TIME) {
            this.x_loc -= approachSpeed;
            this.noteRenderTimeCheck = System.nanoTime();
        }

    }

    public double getApproachSpeed(){

        return this.approachSpeed;

    }

    private void setKeys(){

        if(this.noteType == 1 || this.noteType == 3)
        {

            this.correctKeys[0] = 70;
            this.correctKeys[1] = 74;

        }
        else if(this.noteType == 2 || this.noteType == 4)
        {

            this.correctKeys[0] = 68;
            this.correctKeys[1] = 75;

        }

    }

    private void setKeys(int key1, int key2){

        if(this.noteType == 1)
        {

            this.correctKeys[0] = key1;
            this.correctKeys[1] = key2;

        }
        else if(this.noteType == 2)
        {

            this.correctKeys[0] = key1;
            this.correctKeys[1] = key2;

        }

    }

    private void setNoteImage(){

        if(this.noteType == 1)
        {

            this.noteImage = "src/Files/note-red1.png";
            this.setNoteHitSound("don.wav");

        }
        else if (this.noteType == 2)
        {

            this.noteImage = "src/Files/note-blue1.png";
            this.setNoteHitSound("katsu.wav");

        }
        else if(this.noteType == 3){

            this.noteImage = "src/Files/note-red2.png";
            this.setNoteHitSound("don.wav");

        }
        else if(this.noteType == 4){

            this.noteImage = "src/Files/note-blue2.png";
            this.setNoteHitSound("katsu.wav");

        }

    }

    private void setNoteHitSound(String sound){

        this.noteHitSound = sound;

    }

    public Image getNoteImage(){

        return new ImageIcon(this.noteImage).getImage();

    }

    public String getNoteHitSound(){

        return this.noteHitSound;

    }

    public Image getNoteLineImage(){

        return new ImageIcon("src/Files/bpmline.png").getImage();

    }

    public void checkLinePartner(int checker){

        if(checker == 0){
            this.isLinePartner = true;
        }
        else
        {
            this.isLinePartner = false;
        }

    }

    public boolean isLinePartner(){

        return this.isLinePartner;

    }

    public int getX_loc(){

        return (int)Math.ceil(this.x_loc);

    }

    public int getY_loc(){

        return (int)Math.ceil(this.y_loc);

    }

    public int getNoteType(){

        return this.noteType;

    }

    public int getNoteNumber(){

        return this.noteNumber;

    }

    private void setStartHitShower(long time){

        this.startHitShower = time;

    }

    public boolean checkHitShown(){

        return false;

    }

    public void setNoteRenderTimeCheck(long t){

        this.noteRenderTimeCheck = t;

    }

}
