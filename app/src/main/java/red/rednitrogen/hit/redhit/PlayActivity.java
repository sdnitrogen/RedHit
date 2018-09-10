package red.rednitrogen.hit.redhit;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.security.SecureRandom;

public class PlayActivity extends AppCompatActivity {

    private boolean isWhite = true;
    private int score = 0;
    private int randomNum = 0;
    SecureRandom rand = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ((TextView)findViewById(R.id.score)).setText("Score : "+score);

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
                    finish();
                }
            }
        });

        findViewById(R.id.up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isWhite){
                    score++;
                    ((TextView)findViewById(R.id.score)).setText("Score : "+score);
                    randomNum = rand.nextInt(9 - 0 + 1);
                    if (randomNum == 0){

                    }
                    else {
                        findViewById(R.id.down).setBackgroundColor(Color.WHITE);
                        findViewById(R.id.up).setBackgroundColor(Color.WHITE);
                        isWhite = true;
                    }
                }
                else {
                    finish();
                }
            }
        });
    }
}
