package com.maradroid.brmbrm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


/**
 * Created by mara on 10/9/15.
 */
public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void onButtonClick(View v){

        int id = v.getId();

        if(id == R.id.trackpad_demo_btn){

            Intent intent = new Intent(this, Trackpad.class);
            startActivity(intent);

        }else if(id == R.id.acc_btn){

            Intent intent = new Intent(this, Accelerometer.class);
            startActivity(intent);

        }

    }

    public void Connect(View v){


        getDevices();

        if(isConnected()){
            TextView tv = (TextView) v;
            tv.setText("Odveži");
        }

    }
}
