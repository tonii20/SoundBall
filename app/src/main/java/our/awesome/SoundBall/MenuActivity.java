package our.awesome.SoundBall;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import our.awesome.SoundBall.R;

public class MenuActivity extends Activity implements View.OnClickListener {
    protected ImageView lastWinner;
    private Button btnStart, btnLevel1, btnLevel2, btnLevel3, btnQuit, btnSinglePlayer, btnMultiPlayer;
    private int speed = 7;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private View rl;
    private TextView highscoreText;
    SharedPreferences mPrefs;
    int highscore =0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.Banner1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        requestAudioPermissions();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        mPrefs = getSharedPreferences("label", 0);
        highscore = mPrefs.getInt("tag1", 0);



        rl = findViewById(R.id.relativeLayout);

        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);

        btnLevel1 = findViewById(R.id.btnLevel1);
        btnLevel1.setOnClickListener(this);

        btnLevel2 = findViewById(R.id.btnLevel2);
        btnLevel2.setOnClickListener(this);

        btnLevel3 = findViewById(R.id.btnLevel3);
        btnLevel3.setOnClickListener(this);

        btnQuit = findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener(this);

        lastWinner = findViewById(R.id.lastWinner);
        highscoreText=findViewById(R.id.highscore);

        btnSinglePlayer = findViewById(R.id.btnSinglePlayer);
        btnSinglePlayer.setOnClickListener(this);

        btnMultiPlayer = findViewById(R.id.btnMultiPlayer);
        btnMultiPlayer.setOnClickListener(this);


        try {
            Bundle extras = getIntent().getExtras();
            int showbanner = extras.getInt("Banner");
            int sieger = extras.getInt("sieger");
            int high = extras.getInt("Highscore");

            if (showbanner != 0) {
                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        showInterstitial();
                    }
                });
            }

            if (high!= 0) {
                if(high>highscore);
                highscore=high;
                    }


            if (sieger != 0) {
                if (sieger == 1)
                    lastWinner.setImageResource(R.drawable.player1wins);
                else
                    lastWinner.setImageResource(R.drawable.player2wins);

            }

        } catch (Exception e) {

        }
        highscoreText.setText("Highscore: "+highscore);
        btnLevel1.setAlpha(1);


    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {

            case R.id.btnStart:

                if(btnSinglePlayer.getAlpha()==1)
                    singleplayerActivity(view);
                else
                    multiplayerActivity(view);
                break;
            case R.id.btnSinglePlayer:
                if (btnSinglePlayer.getAlpha() != 1) {
                    btnSinglePlayer.setAlpha(1);
                    btnMultiPlayer.setAlpha(0.5f);
                }
                break;
            case R.id.btnMultiPlayer:
                if (btnMultiPlayer.getAlpha() != 1) {
                    btnMultiPlayer.setAlpha(1);
                    btnSinglePlayer.setAlpha(0.5f);
                }
                break;
            case R.id.btnLevel1:
                speed = 7;
                if (btnLevel1.getAlpha() != 1) {
                    btnLevel1.setAlpha(1);
                    btnLevel2.setAlpha(0.5f);
                    btnLevel3.setAlpha(0.5f);
                }
                break;
            case R.id.btnLevel2:
                speed = 9;
                if (btnLevel2.getAlpha() != 1) {
                    btnLevel2.setAlpha(1);
                    btnLevel3.setAlpha(0.5f);
                    btnLevel1.setAlpha(0.5f);
                }
                break;
            case R.id.btnLevel3:
                speed = 11;
                if (btnLevel3.getAlpha() != 1) {
                    btnLevel3.setAlpha(1);
                    btnLevel2.setAlpha(0.5f);
                    btnLevel1.setAlpha(0.5f);
                }
                break;
            case R.id.btnQuit:
                quit(view);
                break;


            default:
                break;
        }

    }


    public void multiplayerActivity(View view) {
        Intent intentT = new Intent(this, MainActivity.class);

        intentT.putExtra("level", speed);
        startActivity(intentT);
        finish();
    }

    public void singleplayerActivity(View view) {
        Intent intentT = new Intent(this, SinglePlayer.class);
        startActivity(intentT);
        finish();
    }

    @Override
    public void onStop(){
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putInt("tag1" , highscore);
        super.onStop();

    }
    public void quit(View view) {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putInt("tag1" , highscore);
        finish();
        moveTaskToBack(true);
    }

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio, it will not be stored", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }

    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "The App will not work without Audio", Toast.LENGTH_LONG).show();

                }
                return;
            }
        }
    }

}
