package com.maradroid.brmbrm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Trackpad extends AppCompatActivity {

    private int screenWidth,screenHeight, maxMotionRadius;
    private float screenDensity, deltaX, deltaY, trackpadRadius, trackpadStartX, trackpadStartY;
    private boolean isConnected;

    private View trackpad, trackpad_border;
    private TextView lijeviMotori, desniMotori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trackpad_demo_layout);

        isConnected = MainActivity.isConnected();
        getScreenParameters();
        /*Log.e("maradroid", "hight " + screenHeight);*/

        trackpad = (View) findViewById(R.id.trackpad);
        trackpad_border = (View) findViewById(R.id.trackpad_border);

        lijeviMotori = (TextView) findViewById(R.id.lijevi_motori);
        desniMotori = (TextView) findViewById(R.id.desni_motori);

        trackpad_border.post(new Runnable() {
            @Override
            public void run() {
                trackpadRadius = trackpad.getWidth() / 2;
                maxMotionRadius = trackpad_border.getWidth() / 2;
                trackpadStartX = trackpad.getX();
                trackpadStartY = trackpad.getY();
            }
        });

        setTrackpadMotion();

    }

    public void setTrackpadMotion(){

        trackpad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final float X = event.getRawX();
                final float Y = event.getRawY();

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        deltaX = X - trackpad.getX();
                        deltaY = Y - trackpad.getY();

                        break;

                    case MotionEvent.ACTION_MOVE:

                        if ((X - deltaX) < (trackpadStartX + maxMotionRadius) && (X - deltaX) > (trackpadStartX - maxMotionRadius) && (Y - deltaY) < (trackpadStartY + maxMotionRadius) && (Y - deltaY) > (trackpadStartY - maxMotionRadius)) {

                            //float tempRadius = (float) Math.sqrt(Math.pow(trackpad.getX() - trackpadStartX,2) + Math.pow(trackpad.getY() - trackpadStartY,2));

                            //if(tempRadius <= maxMotionRadius){
                            // Log.e("maradroid", "tempRadius " + tempRadius);
                            //Log.e("maradroid", "maxMotionRadius " + maxMotionRadius);
                            double multiplayerX = 1 - ((trackpadStartX + maxMotionRadius - trackpad.getX()) / (maxMotionRadius));
                            double multiplayerY = 1 - ((trackpad.getY() - trackpadStartY + maxMotionRadius) / (maxMotionRadius));

                            // predznaci multiplayera:
                            // naprijed: x+
                            // nazad: x-
                            // lijevo: y-
                            // desno: y+

                            //Log.e("maradroid", "multiplayerX " + multiplayerX);
                            //Log.e("maradroid", "multiplayerY " + multiplayerY);

                            trackpad.setX(X - deltaX);
                            trackpad.setY(Y - deltaY);
                            CalcAndSend((float) multiplayerX, (float) multiplayerY);

                            // Y pogon
                            // X skretanje

                            //}

                        }


                        break;


                    case MotionEvent.ACTION_UP:

                        trackpad.setY(trackpadStartY);
                        trackpad.setX(trackpadStartX);
                        CalcAndSend(0,0);

                        break;
                }
                return true;
            }
        });
    }

    public void CalcAndSend(float multiplayerX, float multiplayerY){

        // Y pogon
        // X skretanje

        String leftMotors = "0", rightMotors = "0";

        // naprijed lijevo
        if(multiplayerY > 0 && multiplayerX < 0){

            leftMotors = "LF" + Math.round(255 * multiplayerY * (1 + multiplayerX)) + "\n";
            rightMotors = "RF" + Math.round(255 * multiplayerY) + "\n";
        }

        // naprijed desno
        if(multiplayerY > 0 && multiplayerX > 0){

            leftMotors = "LF" + Math.round(255 * multiplayerY) + "\n";
            rightMotors = "RF" + Math.round(255 * multiplayerY * (1 -multiplayerX)) + "\n";
        }

        //nazad lijevo
        if(multiplayerY < 0 && multiplayerX < 0){

            leftMotors = "LB" + Math.round(255 * Math.abs(multiplayerY) * (1 + multiplayerX)) + "\n";
            rightMotors = "RB" + Math.round(255 * Math.abs(multiplayerY)) + "\n";
        }

        // nazad desno
        if(multiplayerY < 0 && multiplayerX > 0){

            leftMotors = "LB" + Math.round(255 * Math.abs(multiplayerY)) + "\n";
            rightMotors = "RB" + Math.round(255 * Math.abs(multiplayerY) * (1 - multiplayerX)) + "\n";
        }

        // stop
        if(multiplayerX == 0 && multiplayerY == 0){

            leftMotors = "0" + "\n";
            rightMotors = "0" + "\n";
        }

        lijeviMotori.setText(leftMotors);
        desniMotori.setText(rightMotors);

        if(isConnected){
            if(!MainActivity.sendMessage(leftMotors + rightMotors)){
                Toast.makeText(getApplicationContext(),
                        "Slanje neuspjelo!", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    public void getScreenParameters(){

        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        screenDensity = getApplicationContext().getResources().getDisplayMetrics().density;



    }


    public int dpToPx(int dp){
        return Math.round((float)dp * screenDensity);
    }
}
