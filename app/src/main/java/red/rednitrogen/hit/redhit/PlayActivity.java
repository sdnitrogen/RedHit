package red.rednitrogen.hit.redhit;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.security.SecureRandom;

public class PlayActivity extends AppCompatActivity {

    private TextView scoreview;

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
                    }
                    else {
                        randomNum--;
                    }
                }
                else {
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

                        }
                        else {
                            findViewById(R.id.down).setBackgroundColor(Color.WHITE);
                            findViewById(R.id.up).setBackgroundColor(Color.WHITE);
                            isWhite = true;
                        }
                    }
                }
                else {
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

        final AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlayActivity.this);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        ((TextView) view.findViewById(R.id.endgame_score)).setText(String.valueOf(score));
        ((TextView) view.findViewById(R.id.endgame_highscore)).setText("High Score : " + String.valueOf(shPrefs.getInt("high", 0)));
        view.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = 0;
                scoreview.setText("Score : "+score);
                alertDialog.dismiss();
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
}
