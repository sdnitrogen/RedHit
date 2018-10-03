package red.rednitrogen.hit.redhit;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.jraska.falcon.Falcon;

import java.io.File;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class PlayActivity extends AppCompatActivity implements RewardedVideoAdListener {

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
    private RewardedVideoAd vAd;

    private AlertDialog aDialog;
    private String imagePath;

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

        vAd = MobileAds.getRewardedVideoAdInstance(this);
        vAd.setRewardedVideoAdListener(this);
        vAd.loadAd(getString(R.string.reward_ad_unit_id), new AdRequest.Builder().build());

        showStartDialog();

        scoreview = findViewById(R.id.score);
        ctdwn_progressbar = findViewById(R.id.progress_bar_ctdwn);
        ctdwn_progressbar.setOnProgressListener(new RingProgressBar.OnProgressListener() {
            @Override
            public void progressToComplete() {
                startThread = false;
                findViewById(R.id.down).setClickable(false);
                findViewById(R.id.up).setClickable(false);
                if(score > shPrefs.getInt("high", 0)){
                    shEditor.putInt("high", score);
                    shEditor.commit();
                }
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
                        ((GradientDrawable)findViewById(R.id.center).getBackground()).setColor(Color.WHITE);

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
                    findViewById(R.id.down).setClickable(false);
                    findViewById(R.id.up).setClickable(false);
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
                        startThread = false;
                        findViewById(R.id.down).setClickable(false);
                        findViewById(R.id.up).setClickable(false);
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
                            ((GradientDrawable)findViewById(R.id.center).getBackground()).setColor(getResources().getColor(R.color.colorAccent));

                            progress = 0;
                            startThread = true;
                        }
                    }
                }
                else {
                    startThread = false;
                    findViewById(R.id.down).setClickable(false);
                    findViewById(R.id.up).setClickable(false);
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
                                Thread.sleep(10);
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

        TextView tp = view.findViewById(R.id.privacy_policy_text);
        tp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/AssassiNCrizR/RedHit/blob/master/privacy_policy.md")));
            }
        });

        Rect displayRectangle = new Rect();
        Window window = getWindow();

        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        alertDialog.getWindow().setLayout((int)(displayRectangle.width() *
                0.95f), (int)(displayRectangle.height() * 0.7f));
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
                if(vAd.isLoaded()){
                    vAd.show();
                }
                else{
                    loadInterstitialAd();
                }
            }
        });

        view.findViewById(R.id.btn_restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = 0;
                scoreview.setText("Score : "+score);
                findViewById(R.id.down).setClickable(true);
                findViewById(R.id.up).setClickable(true);
                aDialog.dismiss();

                progress = 0;
                startThread = true;
            }
        });

        view.findViewById(R.id.btn_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID);
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                }
            }
        });

        view.findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
                shareIt();
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
                findViewById(R.id.down).setClickable(true);
                findViewById(R.id.up).setClickable(true);
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

        view.findViewById(R.id.btn_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID);
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                }
            }
        });

        view.findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
                shareIt();
            }
        });
    }

    private void loadInterstitialAd() {
        mAd = new InterstitialAd(this);
        mAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
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
                findViewById(R.id.down).setClickable(true);
                findViewById(R.id.up).setClickable(true);
                aDialog.dismiss();
                progress = 0;
                startThread = true;
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                findViewById(R.id.down).setClickable(true);
                findViewById(R.id.up).setClickable(true);
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

    public void takeScreenshot() {
        File imageDir = new File(Environment.getExternalStorageDirectory().toString() + "/Redit/");
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        imagePath = Environment.getExternalStorageDirectory().toString() + "/Redit/Screenshot_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".png";
        File image = new File(imagePath);
        Falcon.takeScreenshot(PlayActivity.this, image);
    }

    private void shareIt() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "Can you beat me in Red up?\nGet it from https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath));

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        findViewById(R.id.down).setClickable(true);
        findViewById(R.id.up).setClickable(true);
        aDialog.dismiss();
        progress = 0;
        startThread = true;
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        findViewById(R.id.down).setClickable(true);
        findViewById(R.id.up).setClickable(true);
        aDialog.dismiss();
        progress = 0;
        startThread = true;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        vAd.loadAd(getString(R.string.reward_ad_unit_id), new AdRequest.Builder().build());
    }
}
