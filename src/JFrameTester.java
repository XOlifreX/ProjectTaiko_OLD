import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
    Created by Olivier on 23/11/2015.
*/
public class JFrameTester extends JFrame implements Runnable{

    public int x_loc;
    public int y_loc;
    private DrawPane dr = new DrawPane();
    private NotePool p;
    public boolean gameRunning;
    public boolean lineRenderer = false;
    public int x = 0, y = 0; //DEBUG
    private long timeNoteRenderCheck = 0L;

    public JFrameTester(){

        super("My Frame");

        setTitle("Project Taiko - by XOlifréX");
        setContentPane(dr);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(512, 120);
        setResizable(false);

        this.x_loc = 450;
        this.y_loc = 38;

        p = new NotePool("src/Music/Sweet_Sweet_Magic.txt");

        dr.painterino();

        setVisible(true);

        this.initListeners();

    }

    public void runGame(){

        this.gameRunning = true;

        //Thread renderer = new Thread(this);
        //renderer.start();

        long lastTime = System.nanoTime();
        double nanoPerTick = 1000000000d/60d;

        int frames = 0; //FPS
        int ticks = 0; //UPS

        long lastTimer = System.currentTimeMillis();
        double delta = 0; //TIME BEFORE UPS

        while(gameRunning){

            long current = System.nanoTime();
            //.startRenderTimer();
            delta += (current - lastTime)/nanoPerTick;
            lastTime = current;

            boolean renderMax = true;

            while (delta >= 1) {

                ticks++;
                p.renderChecker();
                delta -=1;
                renderMax = true;

            }

            /*try {

                Thread.sleep(3);

            }
            catch (InterruptedException e)
            {

                e.printStackTrace();

            }*/

            if(renderMax) {

                frames++;
                dr.painterino();

            }

            if(System.currentTimeMillis() - lastTimer >= 1000){

                lastTimer += 1000;
                System.out.println("Ticks: " + ticks + " Frames:" + frames);
                frames = 0;
                ticks = 0;

            }

        }

    }

    @Override
    public void run(){

        /*boolean runPaint = true;

        while(runPaint) {

            dr.painterino();

            try{
                Thread.sleep(3);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

        }*/

    }

    private void initListeners()
    {

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                Sound.destroyContextDevice();
            }
        });

        this.addKeyListener(new KeyboardListener(this.p));

    }

    //create a component that you can actually draw on.
    class DrawPane extends JPanel{

        private Graphics2D g2;

        public void painterino(){

            this.repaint();

        }

        @Override
        public void paintComponent(Graphics g){

            g2 = (Graphics2D) g;
            g2.setBackground(Color.BLACK);
            Image bg = new ImageIcon("src/Files/taikoplayfieldbg.png").getImage();
            Image field = new ImageIcon("src/Files/taikoplayfield.png").getImage();
            Image hitspot = new ImageIcon("src/Files/taikohitspot.png").getImage();
            Image kaiGlow = new ImageIcon("src/Files/tempkaiglow.png").getImage();
            Image kaiFire = new ImageIcon("src/Files/tempkaifire.png").getImage();
            g2.drawImage(bg,0,0,this);
            g2.drawImage(hitspot,95,11,this);

            if(p.isKaiMode()) {
                g2.drawImage(kaiGlow, 63, 8, this);
                g2.drawImage(kaiFire,92,-28,this);
            }


            if(p.onScreenBarline.size() > 0){

                BarLine lawl;

                for(int i = 0; i < p.onScreenBarline.size(); i++){

                    lawl = p.onScreenBarline.get(i);

                    g2.drawImage(lawl.getBarLineImage(), lawl.getX_loc() , lawl.getY_loc(), this);

                    lawl.updateBarLineLocation();

                }

            }

            if(p.onScreenNotes.size() > 0){

                Note using;

                for(int i = 0; i < p.onScreenNotes.size(); i++){

                    using = p.onScreenNotes.get(i);

                    g2.drawImage(using.getNoteImage(), using.getX_loc() , using.getY_loc(), this);

                    using.updateNoteLocation();

                }

            }

            g2.drawImage(field,0,0,this);

            if(p.getDrum().getNumberOfActiveImages() > 0){

                for(int i = 0; i < 4; i++){

                    if(p.getDrum().checkImageToDraw(i)) {
                        switch (i) {
                            case 0:
                                g2.drawImage(new ImageIcon("src/Files/taikoblue_left.png").getImage(), -5, -14, this);
                                break;
                            case 1:
                                g2.drawImage(new ImageIcon("src/Files/taikored_left.png").getImage(), -5, -14, this);
                                break;
                            case 2:
                                g2.drawImage(new ImageIcon("src/Files/taikored_right.png").getImage(), 46, -14, this);
                                break;
                            case 3:
                                g2.drawImage(new ImageIcon("src/Files/taikoblue_right.png").getImage(), 46, -14, this);
                                break;
                        }
                    }

                }

            }

            if(p.isGoodHit())
                g2.drawImage(new ImageIcon("src/Files/temphit(better).png").getImage(),72,-11,this);


            if(p.getCombo() >= 10)
                g2.drawString(p.getCombo() + "", 20,40);

            g2.dispose();

        }

    }


}
