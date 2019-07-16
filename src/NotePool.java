import java.util.ArrayList;

/*
     Created by Olivier on 26/12/2015.
*/
public class NotePool extends MusicProp {

    public Note[] songNotes;
    public ArrayList<Note> onScreenNotes = new ArrayList<>();
    public ArrayList<BarLine> barLine;
    public ArrayList<BarLine> onScreenBarline = new ArrayList<>();
    private int onScreenNextNoteCounter = 0;
    private int onScreenNextBarLineCounter = 0;
    private int kaiCounter = 0;
    private Drum drum;
    public long startRenderTime;
    public long startSongTime = -1;
    public long nextFullBeatTimer;
    public long nextNoteRenderTimer;
    private boolean runChecker;
    private boolean mapEnded;
    private ArrayList<Long> kaiModeTimings;
    private boolean kaiMode;
    private int[] currentMesuremend;
    private int currentScrollModifier;
    private int combo;
    private int maxCombo;
    private long startGoodHitTimer;
    private boolean goodHitActive;
    private boolean musicStarted;

    private long addedStartSongTime = 1_425_000_000;//1580000000;//1_400_000_000;//1462000000;

    /* Debug stuff */
    private boolean debug;
    private boolean playerOn;
    private int alternateRedIndicator;
    private int alternateBlueIndicator;
    private int debugDrumCounter;


    public NotePool(String fineName) {

        super(fineName);
        this.songNotes = this.getNoteArray(super.readNotes());
        this.mapEnded = false;
        this.kaiMode = false;
        this.musicStarted = false;
        this.combo = 0;
        this.maxCombo = 0;
        this.goodHitActive = false;
        this.startGoodHitTimer = 0;
        this.alternateBlueIndicator = 0;
        this.alternateRedIndicator = 1;
        this.debugDrumCounter = 0;

        this.debug = false;
        this.playerOn = true;

        this.drum = new Drum();

    }

    public NotePool() {

    }


    public boolean checkNextNoteToRender(long thisTime) {

        if (thisTime >= this.nextNoteRenderTimer) {

            this.nextNoteRenderTimer = thisTime + (long) super.get16thInterval() + (long) this.getOffset();
            return true;

        } else {
            return false;
        }

    }

    public boolean checkBarlineToRender(long thisTime) {

        if (thisTime >= this.nextFullBeatTimer) {

            this.nextFullBeatTimer = thisTime + (long) super.getFullBeatInterval() + (long) this.getOffset();
            return true;

        } else {
            return false;
        }

    }

    public void renderChecker() {

        if (!runChecker) {
            this.startRenderTimer();
            runChecker = true;
        }


        if (((System.nanoTime() - this.startRenderTime)) >= this.addedStartSongTime) {
            if (startSongTime < 0) {

                //this.musicStarted = true;

                //if(((System.nanoTime() - this.startRenderTime)) >= (this.addedStartSongTime + this.getDemoStart()))
                this.startSongTimer();

                this.nextFullBeatTimer = (System.nanoTime() - this.startSongTime) + (long) super.getFullBeatInterval() + (long) this.getOffset();
                this.nextNoteRenderTimer = (System.nanoTime() - this.startSongTime) + (long) super.get16thInterval() + (long) this.getOffset();

            }
        }


        if (//this.musicStarted){
                this.startSongTime >= 0) {

            if (((System.nanoTime() - this.startSongTime) + this.getOffset()) >= 0) {
                if (!super.getSongStarted()) {
                    super.startSong();
                    super.setDemoStart(true);
                }
            }

            //if(((System.nanoTime() - this.startSongTime) + this.getOffset()) >= 0)
            this.checkNoteToRender(this.startSongTime, System.nanoTime());

        }

    }

    public void startRenderTimer() {

        this.startRenderTime = System.nanoTime();

    }

    public void startSongTimer() {

        this.startSongTime = System.nanoTime() + this.addedStartSongTime;

    }

    public void checkNoteToRender(long beginTime, long currentTime) {

        long thisTime = currentTime - beginTime;


        this.drum.checkImageEnding(System.nanoTime());

        if (this.kaiCounter < this.kaiModeTimings.size()) {

            //No "this.addedStartSongTime" added, because KAI mode doesn't have to start when the note (or place) where it starts just appears on screen.
            if (this.kaiModeTimings.get(this.kaiCounter) < thisTime) {

                this.kaiMode = !this.kaiMode;

                this.kaiCounter++;

            }

        }

        if (this.goodHitActive) {
            if ((this.startGoodHitTimer + 100000000) <= System.nanoTime())
                this.goodHitActive = false;
        }

        if (this.onScreenNextBarLineCounter < barLine.size()) {

            if (this.barLine.get(this.onScreenNextBarLineCounter).getPerfectHitTime() < thisTime + this.addedStartSongTime) {

                onScreenBarline.add(this.barLine.get(this.onScreenNextBarLineCounter));
                this.barLine.get(this.onScreenNextBarLineCounter).setBarRenderTimeCheck(currentTime);
                this.onScreenNextBarLineCounter++;

            }

        }

        if (onScreenNextNoteCounter < songNotes.length) {

            if (this.songNotes[this.onScreenNextNoteCounter].getPerfectHitTime() < thisTime + this.addedStartSongTime) {

                onScreenNotes.add(this.songNotes[this.onScreenNextNoteCounter]);
                this.songNotes[this.onScreenNextNoteCounter].setNoteRenderTimeCheck(currentTime);
                this.onScreenNextNoteCounter++;

            }
        }

        if (beginTime > 0) {
            if (!this.songNotes[this.songNotes.length - 1].isHit()) {

                if (this.onScreenBarline.size() > 0) {
                    for (int i = 0; i < this.onScreenBarline.size(); i++) {

                        if (thisTime - this.onScreenBarline.get(i).getHitPrecision()
                                > this.onScreenBarline.get(i).getPerfectHitTime() + 500000000) { //500000000

                            this.onScreenBarline.remove(i);
                            if (i > 0)
                                i--;
                        }
                    }
                }

                if (this.onScreenNotes.size() > 0) {

                    for (int i = 0; i < this.onScreenNotes.size(); i++) {

                        if (this.onScreenNotes.get(i).checkNoteTooLate(this.startSongTime, thisTime))
                            this.combo = 0;

                        if (thisTime - onScreenNotes.get(i).getHitPrecision()
                                > onScreenNotes.get(i).getPerfectHitTime() + 500000000) {

                            onScreenNotes.remove(i);
                            this.combo = 0;

                            if (i > 0)
                                i--;

                        } else {

                            if (this.debug) {
                                /*if (((this.onScreenNotes.get(i).getPerfectHitTime() + this.onScreenNotes.get(i).getHitPrecision()) >= thisTime)
                                        && ((this.onScreenNotes.get(i).getPerfectHitTime() - this.onScreenNotes.get(i).getHitPrecision()) <= thisTime)
                                        && !this.onScreenNotes.get(i).isHit) {*/
                                if ((Math.floor((this.onScreenNotes.get(i).getPerfectHitTime() - (this.onScreenNotes.get(i).getHitPrecision() / 2)) / 100000)
                                        <= Math.floor(thisTime / 100000)) && !this.onScreenNotes.get(i).isHit) {

                                    int[] keys = KeyboardListener.getKeyArrayKDDK();

                                    if (this.debugDrumCounter % 2 == 0) {

                                        this.alternateRedIndicator = 1;
                                        this.alternateBlueIndicator = 0;

                                    } else {

                                        this.alternateRedIndicator = 2;
                                        this.alternateBlueIndicator = 3;

                                    }

                                    if ((this.onScreenNotes.get(i).getNoteType() == 1 || this.onScreenNotes.get(i).getNoteType() == 3)) {
                                        drum.setHit(this.alternateRedIndicator, true, System.nanoTime());
                                        this.handleHit(keys[alternateRedIndicator], thisTime, 1, this.alternateRedIndicator);
                                    } else {
                                        drum.setHit(this.alternateBlueIndicator, true, System.nanoTime());
                                        this.handleHit(keys[alternateBlueIndicator], thisTime, 2, this.alternateBlueIndicator);
                                    }

                                    super.playSound(this.onScreenNotes.get(i).getNoteType());
                                    this.onScreenNotes.get(i).isHit = true;
                                    this.onScreenNotes.remove(i);

                                    this.debugDrumCounter++;

                                }
                            }

                        }

                    }
                }
            } else {
                if (!this.mapEnded) {
                    System.out.println("\nMap has ended!");
                    this.mapEnded = true;
                }
            }
        }
    }

    private void insertNewBarline(long time) {

        this.barLine.add(new BarLine(time));

    }


    private Note[] getNoteArray(String[] notes) {

        ArrayList<Note> tempNotes = new ArrayList<>();
        this.kaiModeTimings = new ArrayList<>();
        this.barLine = new ArrayList<>();
        long intervalCounter = 0;

        this.currentMesuremend = new int[]{4, 4};
        this.currentScrollModifier = 1;

        for (int i = 0; i < notes.length; i++) {

            if (notes[i].charAt(notes[i].length() - 1) == ',')
                notes[i] = notes[i].substring(0, notes[i].length() - 1);

            if (notes[i].contains("/") && !notes[i].contains("#"))
                notes[i] = notes[i].substring(notes[i].indexOf('/'), notes[i].length() - 1);

            if (notes[i].length() > 1) {
                if (notes[i].charAt(0) != '#') {
                    if ((notes[i].length() % this.currentMesuremend[1]) == 0) {
                        for (int j = 0; j < notes[i].length(); j++) {

                            if (notes[i].charAt(j) != '0')
                                tempNotes.add(this.getNoteType(notes[i].charAt(j), intervalCounter));

                            intervalCounter += (long) (super.get16thInterval()
                                    * ((double) (this.currentMesuremend[0] * this.currentMesuremend[1]) / (double) notes[i].length()));

                        }

                        this.insertNewBarline(intervalCounter);

                    } else {
                        System.out.println("Mesuremend not correct with the number of notes within the next full beat. Line: " + notes[i] + " Line number: " + i);
                        System.exit(1);
                    }
                } else {

                    this.handleExtraInfo(notes[i], tempNotes, intervalCounter);

                }
            } else {

                if (notes[i].length() == 1) {
                    if (notes[i].charAt(0) != '0')
                        tempNotes.add(this.getNoteType(notes[i].charAt(0), intervalCounter));

                    intervalCounter += (long) (super.get16thInterval()
                            * ((double) (this.currentMesuremend[0] * this.currentMesuremend[1]) / (double) notes[i].length()));

                    this.insertNewBarline(intervalCounter);

                } else {
                    intervalCounter += (long) (super.get16thInterval()
                            * ((double) (this.currentMesuremend[0] * this.currentMesuremend[1]) / (double) (notes[i].length() + 1)));

                    this.insertNewBarline(intervalCounter);

                }
            }

        }

        return tempNotes.toArray(new Note[tempNotes.size() - 1]);

    }

    private void handleExtraInfo(String info, ArrayList<Note> temp, long intervalCounter) {

        if (info.contains("MEASURE")) {
            this.currentMesuremend[0] = Integer.parseInt(info.substring(9, 10));
            this.currentMesuremend[1] = Integer.parseInt(info.substring(11, 12));
        }

        if (info.contains("GOGO"))
            this.kaiModeTimings.add(intervalCounter);

        if (info.contains("BPMCHANGE")) {
            super.getMapFlowChanges().add(new Timing(Double.parseDouble(info.substring(11, 14))));
            super.incrementTimingIndex();
        }

        if (info.contains("SCROLL"))
            this.currentScrollModifier = Integer.parseInt(info.substring(8, 11));

    }

    private Note getNoteType(char type, long interval) {


        if (type == '1') {

            return new Note(1, interval, this.currentScrollModifier);

        }

        if (type == '2') {

            return new Note(2, interval, this.currentScrollModifier);

        }

        if (type == '3') {

            return new Note(3, interval, this.currentScrollModifier);

        }

        if (type == '4') {

            return new Note(4, interval, this.currentScrollModifier);

        }

        return new Note(1, interval, this.currentScrollModifier);

    }

    public boolean isKaiMode() {

        return this.kaiMode;

    }

    public Drum getDrum() {

        return this.drum;

    }

    public boolean getPlayerActive(){

        return this.playerOn;

    }

    public void setDebug() {

        this.debug = !this.debug;
        this.playerOn = !this.playerOn;

    }

    public int getCombo() {

        return this.combo;

    }

    public boolean isGoodHit() {

        return this.goodHitActive;

    }

    public void handleNotePositions() {

        for (Note n : this.onScreenNotes) {


        }

    }

    public void handleHit(int key, long thisTime, int noteType, int drumSideIndex) {

        Note newestNote = null;
        int indexMemory = 0;
        super.playSound(noteType);

        for (int i = 0; i < this.onScreenNotes.size(); i++) {
            if ((this.onScreenNotes.get(i).getPerfectHitTime() > (thisTime - this.startSongTime))) {
                newestNote = this.onScreenNotes.get(i);
                indexMemory = i;
                i = this.onScreenNotes.size();
            }
        }

        if (newestNote != null) {

            int hitResult = newestNote.checkHit(key, this.startSongTime, thisTime);

            if(debug)
                System.out.println("Hit result " + hitResult);

            if (hitResult == 1) {
                this.combo++;
                this.startGoodHitTimer = System.nanoTime();
                this.goodHitActive = true;

                this.onScreenNotes.remove(indexMemory);
            }

            if (hitResult == 2) {

                if (this.combo > this.maxCombo)
                    this.maxCombo = this.combo;

                this.combo = 0;

                this.onScreenNotes.remove(indexMemory);
            }

        }

    }
}
