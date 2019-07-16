import java.applet.Applet;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Olivier on 26/12/2015.
 */
public class MusicProp extends Applet{


    /**
     * To Do List:
     * Make sure to recalculate 16th beat interval and full beat interval after BPM change.
     * **/


    private String songName;
    private double BPM;
    private String songFileName;
    private String musicFileName;
    private int difficulity;
    private double offset;
    private double demoStart;
    private long hitPrecision = 150000000;
    private double beatInterval;
    private double fullBeatInterval;
    private boolean intervalChanger;
    private boolean musicStart;
    private ArrayList<Sound> sfx;
    private Sound music;
    private ArrayList<Timing> mapFlowChanges;
    private int timingIndex;
    //private Clip musicClip;


    public MusicProp(String filePath){

        this.demoStart = 0;
        this.mapFlowChanges = new ArrayList<>();
        this.timingIndex = -1;
        this.songFileName = filePath;
        this.readFileInfo(filePath);
        this.initMusicClip();

    }

    public MusicProp() {



    }


    private void initMusicClip() {

        Sound.initContextDevice();

        this.sfx = new ArrayList<Sound>()
        {{
            add(new Sound(String.format("src/Files/%s", "don.wav")));
            add(new Sound(String.format("src/Files/%s", "katsu.wav")));
        }};

        this.music = new Sound(String.format("src/Music/%s", this.musicFileName));

    }

    private void readFileInfo(String filePath){

        ArrayList<String> temp = new ArrayList<>();

        try{

            File file = new File(this.songFileName);
            FileReader reader = new FileReader(file);
            BufferedReader lineReader = new BufferedReader(reader);
            String tempLine;

            while((tempLine = lineReader.readLine()) != null && !(tempLine.equals("#START"))){

                temp.add(tempLine);

            }

        }
        catch (IOException e){
            e.printStackTrace();
        }

        if(temp.size() != 0){

            for(int i = 0; i < temp.size(); i++){

                this.initialiseFileContent(temp.get(i));

            }

        }

    }

    private void initialiseFileContent(String text){

        if(text.contains("TITLE:")){

            this.songName = text.substring(6);

        }
        else if(text.contains("BPM:")){

            this.mapFlowChanges.add(new Timing(Double.parseDouble(text.substring(4))));
            this.timingIndex++;

        }
        else if(text.contains("WAVE:")){

            this.musicFileName= text.substring(5);

        }
        else if(text.contains("LEVEL:")){

            this.difficulity = Integer.parseInt(text.substring(6));

        }
        else if(text.contains("OFFSET:")){

            this.offset = Double.parseDouble(text.substring(7))*100000000;

        }
        else if(text.contains("DEMOSTART:")){

            this.demoStart = Double.parseDouble(text.substring(10))*100000000;

        }

    }

    public double getFullBeatInterval() {

        return this.mapFlowChanges.get(this.timingIndex).getFullBeatInterval();

    }

    public double get16thInterval(){

        return this.mapFlowChanges.get(this.timingIndex).get16thBeatInterval();

    }

    protected ArrayList<Timing> getMapFlowChanges(){

        return this.mapFlowChanges;

    }

    protected void incrementTimingIndex(){

        this.timingIndex++;

    }

    public double getOffset(){

        return Math.floor(this.offset);

    }

    public double getDemoStart(){

        return Math.floor(this.demoStart);

    }

    public long getHitPrecision(){

        return this.hitPrecision;

    }

    public String[] readNotes(){

        ArrayList<String> temp = new ArrayList<>();

        try{

            File file = new File(this.songFileName);
            FileReader reader = new FileReader(file);
            BufferedReader lineReader = new BufferedReader(reader);
            String tempLine;

            while((tempLine = lineReader.readLine()) != null){

                if(!tempLine.isEmpty()) {
                    if ((tempLine.charAt(0) == '0') || (tempLine.charAt(0) == '1')
                            || (tempLine.charAt(0) == '2') || (tempLine.charAt(0) == '3')
                            || (tempLine.charAt(0) == '4') || (tempLine.charAt(0) == ',')
                            || ((tempLine.charAt(0) == '#') && (!tempLine.contains("#START"))))
                        temp.add(tempLine);
                }
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }

        String[] tempArray = new String[temp.size()];
        return temp.toArray(tempArray);

    }

    public void setDemoStart(boolean lol){

        this.musicStart = lol;

    }

    public boolean getSongStarted(){

        return this.musicStart;

    }

    public String getSongFileName() {

        return this.musicFileName;

    }

    public void playSound(String sound){

        /*try {

                /*in = new FileInputStream(new File(String.format("src/Files/%s", this.musicFileName)));
                AudioStream audios = new AudioStream(in);
                AudioPlayer.player.start(audios);

            Clip clip = AudioSystem.getClip();
            AudioInputStream soundi = AudioSystem.getAudioInputStream(new File(String.format("src/Files/%s", sound)));
            clip.open(soundi);

            clip.setFramePosition(0);
            clip.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }*/

    }

    public void playSound(int type){

        /*try {

                /*in = new FileInputStream(new File(String.format("src/Files/%s", this.musicFileName)));
                AudioStream audios = new AudioStream(in);
                AudioPlayer.player.start(audios);

            Clip clip = AudioSystem.getClip();
            AudioInputStream soundi = AudioSystem.getAudioInputStream(new File(String.format("src/Files/%s", sound)));
            clip.open(soundi);

            clip.setFramePosition(0);
            clip.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }*/

        switch(type)
        {

            case 1: this.sfx.get(0).play(); break;
            case 2: this.sfx.get(1).play(); break;
            case 3: this.sfx.get(0).play(); break;
            case 4: this.sfx.get(1).play(); break;

        }

    }

    public void startSong(){

        try {
            this.music.play();
            //this.musicClip.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
