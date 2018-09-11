package red.rednitrogen.hit.redhit;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.security.SecureRandom;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class PlayActivity extends AppCompatActivity {

    RingProgressBar ctdwn_progressbar;
    private boolean startThread = false;
    int progress = 0;
    Thread t;

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(startThread){
                if(msg.what == 0){
                    if(progress < 100){
                        progress++;
                        ctdwn_progressbar.setProgress(progress);
                    }
                }
            }
        }
    };

    private TextView scoreview;
    private InterstitialAd mAd;

    private AlertDialog aDialog;

    private SharedPreferences shPrefs;

    private boolean isWhite = true;
    private int score = 0;
    private int randomNum = 0;
    SecureRandom rand = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        shPrefs = getSharedPreferences("HighScore", MODE_PRIVATE);
        final SharedPreferences.Editor shEditor = shPrefs.edit();

        showStartDialog();

        scoreview = findViewById(R.id.score);
        ctdwn_progressbar = findViewById(R.id.progress_bar_ctdwn);
        ctdwn_progressbar.setOnProgressListener(new RingProgressBar.OnProgressListener() {
            @Override
            public void progressToComplete() {
                startThread = false;
                showEndDialog();
            }
        });

        scoreview.setText("Score : "+score);

        randomNum = rand.nextInt(9 - 0 + 1);

        findViewById(R.id.down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWhite){
                    if (randomNum == 0){
                        findViewById(R.id.down).setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        findViewById(R.id.up).setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        isWhite = false;

                        ctdwn_progressbar.setRingColor(Color.WHITE);
                        ctdwn_progressbar.setRingProgressColor(Color.WHITE);
                        ctdwn_progressbar.setTextColor(Color.WHITE);

                        progress = 0;
                        startThread = true;
                    }
                    else {
                        randomNum--;

                        progress = 0;
                        startThread = true;
                    }
                }
                else {
                    startThread = false;
                    if(score > shPrefs.getInt("high", 0)){
                        shEditor.putInt("high", score);
                        shEditor.commit();
                    }
                    showEndDialog();
                }
            }
        });

        findViewById(R.id.up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isWhite){
                    score++;
                    ((TextView)findViewById(R.id.score)).setText("Score : "+score);
                    if(score == 999){
                        showWinDialog();
                    }
                    else {
                        randomNum = rand.nextInt(9 - 0 + 1);
                        if (randomNum == 0){
                            progress = 0;
                            startThread = true;
                        }
                        else {
                            findViewById(R.id.down).setBackgroundColor(Color.WHITE);
                            findViewById(R.id.up).setBackgroundColor(Color.WHITE);
                            isWhite = true;

                            ctdwn_progressbar.setRingColor(getResources().getColor(R.color.colorAccent));
                            ctdwn_progressbar.setRingProgressColor(getResources().getColor(R.color.colorAccent));
                            ctdwn_progressbar.setTextColor(getResources().getColor(R.color.colorAccent));

                            progress = 0;
                            startThread = true;
                        }
                    }
                }
                else {
                    startThread = false;
                    if(score > shPrefs.getInt("high", 0)){
                        shEditor.putInt("high", score);
                        shEditor.commit();
                    }
                    showEndDialog();
                }
            }
        });
    }

    private void showStartDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.start_dialog, null);

        final AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlayActivity.this);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("START", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                progress = 0;
                startThread = true;
                t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true){
                            try {
                                Thread.sleep(50);
                                myHandler.sendEmptyMessage(0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                t.start();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        Rect displayRectangle = new Rect();
        Window window = getWindow();

        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        alertDialog.getWindow().setLayout((int)(displayRectangle.width() *
                0.95f), (int)(displayRectangle.height() * 0.6f));
    }

    private void showEndDialog(){
        LayoutInflater layoutInflaterAndroid = PlayActivity.this.getLayoutInflater();
        View view = layoutInflaterAndroid.inflate(R.layout.end_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlayActivity.this);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);

        aDialog = alertDialogBuilder.create();
        aDialog.setCancelable(false);
        aDialog.setCanceledOnTouchOutside(false);
        aDialog.show();
        ((TextView) view.findViewById(R.id.endgame_score)).setText(String.valueOf(score));
        ((TextView) view.findViewById(R.id.endgame_highscore)).setText("High Score : " + String.valueOf(shPrefs.getInt("high", 0)));
        view.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInterstitialAd();
            }
        });

        view.findViewById(R.id.btn_restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = 0;
                scoreview.setText("Score : "+score);
                aDialog.dismiss();

                progress = 0;
                startThread = true;
            }
        });
    }

    private void showWinDialog(){
        LayoutInflater layoutInflaterAndroid = PlayActivity.this.getLayoutInflater();
        View view = layoutInflaterAndroid.inflate(R.layout.win_dialog, null);

        final AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlayActivity.this);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        ((TextView) view.findViewById(R.id.wingame_score)).setText(String.valueOf(score));
        view.findViewById(R.id.btn_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = 0;
                scoreview.setText("Score : "+score);
                alertDialog.dismiss();

                progress = 0;
                startThread = true;
            }
        });

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });
    }

    private void loadInterstitialAd() {
        mAd = new InterstitialAd(this);
        mAd.setAdUnitId(getString(R.string.reward_ad_unit_id));
        mAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(mAd.isLoaded()) {
                    mAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                aDialog.dismiss();
                progress = 0;
                startThread = true;
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                aDialog.dismiss();
                progress = 0;
                startThread = true;
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mAd.loadAd(adRequest);
    }

    @Override
    protected void onDestroy() {
        if(t != null){
            t.interrupt();
            return;
        }
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
    }
}
