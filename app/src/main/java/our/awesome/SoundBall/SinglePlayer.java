package our.awesome.SoundBall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;
import java.util.TimerTask;


import java.util.Timer;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import static android.content.ContentValues.TAG;


public class SinglePlayer extends AppCompatActivity {

    //PauseButton
    Button pauseButton;
    Button quitButton;
    //Image
    ImageView player1, fussball;
    //timer
    TextView sekAnzeige;
    //Richtung
    float randomx;
    float randomy;
    float randomxminus;
    float randomyminus;
    //Threadscheise
    Thread barBewegen = new Thread();
    Timer timer;
    Task1 task1;
    Handler handler1 = new Handler();
    AudioDispatcher dispatcher;
    PitchDetectionHandler pdh;
    AudioProcessor pitchProcessor;
    Thread audioThread;
    int maxpunkte = 3;
    boolean pausetrue = false;
    //Ball
    private float[] direction; //direction modifier (-1,1)
    //Layout
    private int speed;
    private int size;
    //Score
    private FrameLayout gameFrame;
    private int frameHeight, frameWidth;
    private ImageView goal;
    private boolean startKeinGoal = true;
    private float player1X;
    private int initialPosY;
    private int initialPosX;
    private TextView score, spielerFeld1, tonHoch, tonTief, erklearung, tabToStart;
    private int scoreCounter;
    private int sieger;
    private int speedIncrease;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        gameFrame = findViewById(R.id.gameFrame);
        player1 = findViewById(R.id.player1);
        fussball = findViewById(R.id.fussball);
        score = findViewById(R.id.Score);
        goal = findViewById(R.id.goal);
        sekAnzeige = findViewById(R.id.sekAnzeige);
        pauseButton = findViewById(R.id.btnPauseResume);
        quitButton = findViewById(R.id.btnQuit);
        spielerFeld1=findViewById(R.id.spielerFeld1);
        tonHoch=findViewById(R.id.TonHoch);
        tonTief=findViewById(R.id.TonTief);
        erklearung=findViewById(R.id.erklärung);
        tabToStart=findViewById(R.id.TabToStart);



        gameFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tonHoch.setVisibility(View.INVISIBLE);
                tonTief.setVisibility(View.INVISIBLE);
                erklearung.setVisibility(View.INVISIBLE);
                tabToStart.setVisibility(View.INVISIBLE);
                startgame();
            }
        });

    }

    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (frameHeight == 0) {
                fussball =  findViewById(R.id.fussball);

                Log.d(TAG, "width : " + fussball.getWidth());

                initialPosY = (int) fussball.getY();
                initialPosX = (int) fussball.getX();
                frameWidth = gameFrame.getWidth();
                frameHeight = gameFrame.getHeight();

                size = frameWidth / 15;


                fussball.getLayoutParams().width = size;
                fussball.getLayoutParams().height = size;


                player1.getLayoutParams().height = (frameWidth / 5) / 4;
                player1.getLayoutParams().width = frameWidth / 5;


            }

        }


    }


    protected void startgame() {

        speed=7;
        scoreCounter = 0;
        score.setText("Score: "+ scoreCounter);
        speedIncrease = 0;
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        setzteRichtung();
        task1 = new Task1();
        timer = new Timer();


        pdh = new PitchDetectionHandler() {

            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e) {
                final float pitchInHz = res.getPitch();
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                processPitch(pitchInHz);

                            }
                        });
            }

        };

        pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);


        audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();


        timer.scheduleAtFixedRate(task1, 5000, 12);
        countdowntimer();


    }

    public void processPitch(float pitchInHz) {

        final float laenge = frameWidth / 5;
        Runnable r = new Runnable() {
            @Override
            public void run() {

            }
        };
        if (pitchInHz >= 80 && pitchInHz < 800) {


            if (pitchInHz >= 80 && pitchInHz < 120) {

                r = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            if (player1X > 0) {
                                player1X = player1X - 2;
                            }
                            if (player1X < 0) {
                                player1X = player1X + 2;
                            }
                            player1.setX(player1X);

                        }
                    }
                };


            } else if (pitchInHz >= 120 && pitchInHz < 210) {


                r = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            if (player1X > laenge) {
                                player1X = player1X - 2;
                            }
                            if (player1X < laenge) {
                                player1X = player1X + 2;
                            }
                            player1.setX(player1X);
                        }
                    }
                };


            } else if (pitchInHz >= 210 && pitchInHz < 390) {
                r = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            if (player1X > 2 * laenge) {
                                player1X = player1X - 2;
                            }
                            if (player1X < 2 * laenge) {
                                player1X = player1X + 2;
                            }
                            player1.setX(player1X);
                        }
                    }
                };
            } else if (pitchInHz >= 390 && pitchInHz < 530) {
                r = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            if (player1X > 3 * laenge) {
                                player1X = player1X - 2;
                            }
                            if (player1X < 3 * laenge) {
                                player1X = player1X + 2;
                            }
                            player1.setX(player1X);
                        }
                    }
                };
            } else if (pitchInHz >= 530 && pitchInHz <= 800) {

                r = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 15; i++) {
                            if (player1X > 4 * laenge) {
                                player1X = player1X - 2;
                            }
                            if (player1X < 4 * laenge) {
                                player1X = player1X + 2;
                            }
                            player1.setX(player1X);
                        }
                    }
                };
            }

            barBewegen = new Thread(r);
            barBewegen.start();

        }


    }

    public void setzteRichtung() {
        //Direction
        randomx = (float) new Random().nextFloat();

        randomy = (float) new Random().nextFloat();
        randomxminus = (float) new Random().nextFloat();
        randomyminus = (float) new Random().nextFloat();
        if (randomxminus > 0.5f) randomx = -randomx;

        if (randomx < 0.5) {
            direction = new float[]{randomx, randomy};
        } else {
            direction = new float[]{randomx , randomy};
        }

    }

    public void move() {

        if (randomxminus > 0.5f)
            fussball.setX(fussball.getX() + speed * (int) direction[0] - 1.0f);
        else
            fussball.setX(fussball.getX() + speed * (int) direction[0] + 1.0f);

        if (randomyminus > 0.5f)
            fussball.setY(fussball.getY() + speed * (int) (direction[1] - 1.0f));
        else
            fussball.setY(fussball.getY() + speed * (int) (direction[1] + 1.0f));

        //Check if ball touches the Player
        if (fussball.getY() >= (player1.getY() - player1.getHeight())) {
            if (player1.getX() - size / 2 <= fussball.getX() && player1.getX() + size * 1 / 5 >= fussball.getX()) {
                direction[1] = direction[1] * -1.0f;
                if (direction[0] > 0) {
                    direction[0] = direction[0] * -1.0f;
                    randomxminus = 1 - randomxminus;
                }
                randomyminus = 1 - randomyminus;

            } else if (player1.getX() + player1.getWidth() + size / 2 >= fussball.getX() &&
                    player1.getX() + player1.getWidth() - size * 1 / 5 <= fussball.getX()) {
                direction[1] = direction[1] * -1.0f;
                if (direction[0] < 0) {
                    direction[0] = direction[0] * -1.0f;
                    randomxminus = 1 - randomxminus;
                }
                randomyminus = 1 - randomyminus;

            } else if ((player1.getX() - size / 2 <= fussball.getX()) &&
                    (player1.getX() + size / 2 + player1.getWidth() >= fussball.getX())) {

                direction[1] = direction[1] * -1.0f;
                randomyminus = 1 - randomyminus;
            }
            scoreCounter++;
            speedIncrease++;
            score.setText("Score: "+ scoreCounter);
            if(speedIncrease==3){
                speed++;
                speedIncrease=0;
            }
        }

        if (fussball.getX() < 0 || fussball.getX() + size > gameFrame.getWidth()) {
            direction[0] = direction[0] * -1;
            randomxminus = 1 - randomxminus;
        }
        //Check if ball touches the Opposite Wall
        if (fussball.getY() < 0) {
            direction[1] = direction[1] * -1.0f;
            randomyminus = 1 - randomyminus;
        }

        if (fussball.getY() + size > gameFrame.getHeight()) {

            gameOver();

        }
    }

    public void gameOver() {
        Intent intentT = new Intent(this, MenuActivity.class);
        intentT.putExtra("sieger", sieger);
        intentT.putExtra("Banner", 1);
        intentT.putExtra("Highscore", this.scoreCounter);
        startActivity(intentT);
        handler1.removeCallbacks(task1);
        timer.cancel();
        task1 = null;
        audioThread.interrupt();
        dispatcher.stop();
        finish();
    }


    public void countdowntimer() {
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished > 4000&&!startKeinGoal) {
                    handler1.post(new Runnable() {
                        @Override
                        public void run() {

                            goal.getLayoutParams().width = frameWidth / 2;
                            goal.getLayoutParams().height = frameWidth / 3;
                            goal.setScaleType(ImageView.ScaleType.FIT_XY);
                            goal.setVisibility(View.VISIBLE);


                        }
                    });
                }
                if (millisUntilFinished < 3000) {

                    handler1.post(new Runnable() {
                        @Override
                        public void run() {

                            goal.setVisibility(View.INVISIBLE);
                            sekAnzeige.setVisibility(View.VISIBLE);

                        }
                    });

                }

                if (millisUntilFinished < 1000) {
                    if(startKeinGoal)
                        startKeinGoal=false;
                    spielerFeld1.setVisibility(View.INVISIBLE);
                    sekAnzeige.setText("GOOO");
                } else {
                    sekAnzeige.setText("" + millisUntilFinished / 1000);
                }
            }

            public void onFinish() {

                sekAnzeige.setVisibility(View.INVISIBLE);
            }
        }.start();

    }

    public void pausePushed(View view) {

        if (!pausetrue) {
            pausetrue = true;
            timer.cancel();
            timer = null;
            task1 = null;
            pauseButton.setText("Resume");
            audioThread.interrupt();
            dispatcher.stop();

        } else {
            pausetrue = false;
            task1 = new Task1();
            timer = new Timer();
            timer.scheduleAtFixedRate(task1, 1000, 12);
            pauseButton.setText("Pause");
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
            pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
            dispatcher.addAudioProcessor(pitchProcessor);

            audioThread = new Thread(dispatcher, "Audio Thread");
            audioThread.start();
        }
    }

    public void quit(View view) {
        sieger = 0;
        Intent intentT = new Intent(this, MenuActivity.class);
        intentT.putExtra("sieger", sieger);
        intentT.putExtra("Banner", 1);
        intentT.putExtra("Highscore", this.scoreCounter);
        startActivity(intentT);
        handler1.removeCallbacks(task1);
        try {
            timer.cancel();
            task1 = null;
            audioThread.interrupt();
            dispatcher.stop();

        }catch(Exception e){

        }
        finish();

    }




    private class Task1 extends TimerTask {

        public void run() {

            move();

        }
    }
}


