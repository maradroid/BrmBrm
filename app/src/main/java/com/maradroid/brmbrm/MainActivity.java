package com.maradroid.brmbrm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by mara on 10/10/15.
 */
public class MainActivity extends BaseActivity implements SensorEventListener, Connection {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private TextView leftMotorsTV;
    private TextView rightMotorsTV;
    private TextView connectedTV;

    private LinearLayout forwardCircleLL;
    private LinearLayout reverseCircleLL;
    private LinearLayout textPlaceHolderLL;
    private LinearLayout connectLL;
    private LinearLayout selfDrivingLL;

    private boolean messageDisplayed;
    private boolean movingForward;
    private boolean movingReverse;
    private boolean inSelfDrivingMode;

    private float screenDensity;

    private AnimatorSet scaleDown;

    private String lastSpeed = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accelerometer_layout);

        setConnectionInterface(this);

        getScreenParameters();
        initSensor();
        initViews();

    }

    private void initViews() {

        leftMotorsTV = (TextView) findViewById(R.id.lijevi_motori_tv);
        rightMotorsTV = (TextView) findViewById(R.id.desni_motori_tv);
        connectedTV = (TextView) findViewById(R.id.povezi_tv);

        forwardCircleLL = (LinearLayout) findViewById(R.id.forward_circle_ll);
        reverseCircleLL = (LinearLayout)  findViewById(R.id.reverse_circle_ll);
        textPlaceHolderLL = (LinearLayout) findViewById(R.id.text_placeholder_ll);
        connectLL = (LinearLayout) findViewById(R.id.connect_circle_ll);
        selfDrivingLL = (LinearLayout) findViewById(R.id.self_driving_circle_ll);

        hidePlaceholderElements(false, connectLL);
        setupForwardLLTouchListener();
        setupReverseLLTouchListener();
    }

    private void hidePlaceholderElements(boolean animated, LinearLayout animatedObject) {

        int movingDistance = dpToPx(150);

        if (animated) {

            textPlaceHolderLL.clearAnimation();
            textPlaceHolderLL.animate().translationY(-movingDistance).setDuration(2000);
            textPlaceHolderLL.animate().alpha(0).setDuration(2000);

            forwardCircleLL.clearAnimation();
            forwardCircleLL.animate().translationX(movingDistance).setDuration(2000);
            forwardCircleLL.animate().alpha(0).setDuration(2000);

            reverseCircleLL.clearAnimation();
            reverseCircleLL.animate().translationX(-movingDistance).setDuration(2000);
            reverseCircleLL.animate().alpha(0).setDuration(2000);

            if (animatedObject.equals(connectLL)) {
                connectLL.clearAnimation();
                connectLL.animate().translationY(0).setDuration(2000);
                connectLL.animate().scaleX(1).scaleY(1).setDuration(2000);

                selfDrivingLL.clearAnimation();
                selfDrivingLL.animate().scaleX(0).scaleY(0).setDuration(2000);
            }

            setBlinkAnimation(animatedObject);

        } else {

            textPlaceHolderLL.clearAnimation();
            textPlaceHolderLL.setY(-movingDistance);
            textPlaceHolderLL.setAlpha(0);

            forwardCircleLL.clearAnimation();
            forwardCircleLL.setX(movingDistance);
            forwardCircleLL.setAlpha(0);

            reverseCircleLL.clearAnimation();
            reverseCircleLL.setX(-movingDistance);
            reverseCircleLL.setAlpha(0);

            selfDrivingLL.clearAnimation();
            selfDrivingLL.setScaleX(0);
            selfDrivingLL.setScaleY(0);

            setBlinkAnimation(animatedObject);
        }
    }

    private void showPlaceholderElements(boolean workWithConnectLL) {

        textPlaceHolderLL.clearAnimation();
        textPlaceHolderLL.animate().translationY(0).setDuration(2000);
        textPlaceHolderLL.animate().alpha(1).setDuration(2000);

        forwardCircleLL.clearAnimation();
        forwardCircleLL.animate().translationX(0).setDuration(2000);
        forwardCircleLL.animate().alpha(1).setDuration(2000);

        reverseCircleLL.clearAnimation();
        reverseCircleLL.animate().translationX(0).setDuration(2000);
        reverseCircleLL.animate().alpha(1).setDuration(2000);

        if (workWithConnectLL) {

            scaleDown.cancel();
            connectLL.clearAnimation();
            connectLL.animate().translationY(dpToPx(120)).setDuration(2000);
            connectLL.animate().scaleX(0.8f).scaleY(0.8f).setDuration(2000);

            selfDrivingLL.clearAnimation();
            selfDrivingLL.animate().scaleX(0.8f).scaleY(0.8f).setDuration(2000);
        }

    }

    private void setBlinkAnimation(LinearLayout animatedObject) {

        if (scaleDown != null) {
            scaleDown.cancel();
        }

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(animatedObject, "scaleX", 0.9f);
        scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
        scaleDownX.setDuration(3000);

        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(animatedObject, "scaleY", 0.9f);
        scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
        scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
        scaleDownY.setDuration(3000);

        scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
    }

    private void initSensor() {

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void setupForwardLLTouchListener() {

        forwardCircleLL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        movingForward = true;
                        forwardCircleLL.animate().alpha(0.8f).scaleY(0.9f).scaleX(0.9f).setDuration(0);
                        break;

                    case MotionEvent.ACTION_UP:
                        forwardCircleLL.animate().alpha(1).scaleY(1).scaleX(1).setDuration(0);
                        movingForward = false;
                        messageDisplayed = false;
                        break;
                }

                return true;
            }
        });
    }

    private void setupReverseLLTouchListener() {

        reverseCircleLL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        movingReverse = true;
                        reverseCircleLL.animate().alpha(0.8f).scaleY(0.9f).scaleX(0.9f).setDuration(0);
                        break;

                    case MotionEvent.ACTION_UP:
                        reverseCircleLL.animate().alpha(1).scaleY(1).scaleX(1).setDuration(0);
                        movingReverse = false;
                        messageDisplayed = false;
                        break;
                }

                return true;
            }
        });
    }

    private void sendData(String motorsSpeed) {

        if(isConnected() && !motorsSpeed.equals(lastSpeed)){
            lastSpeed = motorsSpeed;

            if(!sendMessage(motorsSpeed) && !messageDisplayed){
                Toast.makeText(getApplicationContext(),
                        "Slanje neuspjelo!", Toast.LENGTH_SHORT)
                        .show();

                messageDisplayed = true;
            }
        }
    }

    public void getScreenParameters(){

        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        screenDensity = getApplicationContext().getResources().getDisplayMetrics().density;
    }

    public int dpToPx(int dp){
        return Math.round((float)dp * screenDensity);
    }

    public void connectButton(View v) {

        if (inSelfDrivingMode) {
            inSelfDrivingMode = false;
        }

        getDevices();
    }

    public void selfDrivingButton(View v) {

        if (!inSelfDrivingMode) { // pokreni self driving mod

            sendData("#");
            inSelfDrivingMode = true;
            selfDrivingLL.clearAnimation();
            hidePlaceholderElements(true, selfDrivingLL);
            mSensorManager.unregisterListener(this);

        } else {

            sendData("$");
            inSelfDrivingMode = false;
            scaleDown.cancel();
            showPlaceholderElements(false);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }


    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float posY = event.values[1];

        // accelerometer daje vrijednosti od -9.81 do 9.81
        // ovdje se koriste X i Y os
        // za X os uzimaju se vrijednosti od 0 (max naprijed) do 8 (max nazad)
        // za Y os uzimaju se vrijednosti od -4 (max lijevo) do 4 (max desno)

        if (movingForward != movingReverse && !inSelfDrivingMode) {

            if (movingForward) {

                if (posY <= 2 && posY >= -2) {
                    sendData("255,255\n");
                    leftMotorsTV.setText("Left motor\nmax");
                    rightMotorsTV.setText("Right motor\nmax");

                }else if (posY > 2) {
                    sendData("255,0\n");
                    leftMotorsTV.setText("Left motor\nmax");
                    rightMotorsTV.setText("Right motor\n0");

                } else if (posY < -2) {
                    sendData("0,255\n");
                    leftMotorsTV.setText("Left motor\n0");
                    rightMotorsTV.setText("Right motor\nmax");
                }

            } else if (movingReverse) {

                if (posY <= 2 && posY >= -2) {
                    sendData("-255,-255\n");
                    leftMotorsTV.setText("Left motor\nmax");
                    rightMotorsTV.setText("Right motor\nmax");

                }else if (posY > 2) {
                    sendData("-255,0\n");
                    leftMotorsTV.setText("Left motor\nmax");
                    rightMotorsTV.setText("Right motor\n0");

                } else if (posY < -2) {
                    sendData("0,-255\n");
                    leftMotorsTV.setText("Left motor\n0");
                    rightMotorsTV.setText("Right motor\nmax");
                }
            }

        } else {
            sendData("0/0\n");
            leftMotorsTV.setText("Left motor\n0");
            rightMotorsTV.setText("Right motor\n0");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        hideStatusBar();

        if (isConnected()) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isConnected()) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void connectionCompleted() {

        if (isConnected()) {
            hideStatusBar();
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            showPlaceholderElements(true);
            connectedTV.setText("Disconnect");

        }else {
            mSensorManager.unregisterListener(this);
            hidePlaceholderElements(true, connectLL);
            connectedTV.setText("BrmBrm");
        }

    }
}