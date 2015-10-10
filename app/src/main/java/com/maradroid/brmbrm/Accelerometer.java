package com.maradroid.brmbrm;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by mara on 10/10/15.
 */
public class Accelerometer extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private TextView lijeviMotori, desniMotori;
    private boolean isConnected, start = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accelerometer_layout);

        isConnected = MainActivity.isConnected();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        lijeviMotori = (TextView) findViewById(R.id.lijevi_motori_tv);
        desniMotori = (TextView) findViewById(R.id.desni_motori_tv);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float posX = event.values[0];
        float posY = event.values[1];
        boolean minusNeeded = false;

        // accelerometer daje vrijednosti od -9.81 do 9.81
        // ovdje se koriste X i Y os
        // za X os uzimaju se vrijednosti od 0 (max naprijed) do 8 (max nazad)
        // za Y os uzimaju se vrijednosti od -4 (max lijevo) do 4 (max desno)

        if(posX > 8){

            posX = 8;
            minusNeeded = false;

        }else if(posX < 0){

            posX = 0;
            minusNeeded = false;

        }else if(posX > 4 && posX <= 8){

            minusNeeded = true;
            posX = 8 - posX;

        }else if(posX >= 0 && posX <= 4){

            minusNeeded = false;
        }

        if(posY < -4){

            posY = -4;

        }else if(posY > 4){

            posY = 4;
        }

        float multiplayerX;

        if(minusNeeded){
            multiplayerX = ((4 - posX)/(4)) * -1;
        }else{
            multiplayerX = ((4 - posX)/(4));
        }


        float multiplayerY = 1 - ((4 - posY)/(4));

        //Log.e("maradroid", "multiplayerX " + multiplayerX);
        //Log.e("maradroid", "multiplayerY " +multiplayerY);

        CalcAndSend(multiplayerY, multiplayerX);

        // X pogon
        // Y skretanje

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

        if(isConnected && start){
            if(!MainActivity.sendMessage(leftMotors + rightMotors)){
                Toast.makeText(getApplicationContext(),
                        "Slanje neuspjelo!", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    public void StartStopButton(View v){

        if(start){
            start = false;
            ((TextView) v).setText("Start");
        }else{
            start = true;
            ((TextView) v).setText("Stop");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
    }
}
