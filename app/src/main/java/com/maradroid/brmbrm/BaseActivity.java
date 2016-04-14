package com.maradroid.brmbrm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by mara on 10/9/15.
 */
public class BaseActivity extends AppCompatActivity {

    private static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;

    private BluetoothSocket mSocket = null;
    private OutputStream mOutStream = null;

    private boolean spojeno = false;
    private String[] devices;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideStatusBar();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public boolean sendMessage(String message){

        try {

            if(mOutStream != null){
                mOutStream.write(message.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void getDevices(){

        if(spojeno){
            if(mSocket!=null){
                try {
                    mSocket.close();
                    spojeno = false;
                    connection.connectionCompleted();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),
                            "Veza nije prekinuta!\nPokušajte ponovno kasnije.", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }else{

            if (mBluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(),
                        "Bluetooth trenutno nedostupan!", Toast.LENGTH_SHORT)
                        .show();

            }else if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            }else{
                pairedDevices = mBluetoothAdapter.getBondedDevices();
                devices = new String[pairedDevices.size()];

                if(pairedDevices.size() > 0)
                {
                    int i = 0;
                    for(BluetoothDevice device : pairedDevices)
                    {
                        devices[i] = device.getName();
                        i++;
                    }
                    showMyDialog("odabir");

                }else{
                    showMyDialog("upozorenje");
                }
            }
        }
    }

    public void connectToDevice(int position){

        Object[] o = new Object[pairedDevices.size()];
        o = pairedDevices.toArray();
        final BluetoothDevice bluetoothDevice = (BluetoothDevice) o[position];
        Toast.makeText(getApplicationContext(),"Uspostava veze...", Toast.LENGTH_SHORT).show();


        new Thread(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.cancelDiscovery();
                try {
                    mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(mUUID);
                    mSocket.connect();
                    mOutStream = mSocket.getOutputStream();

                    BaseActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Veza uspostavljena!", Toast.LENGTH_SHORT)
                                    .show();

                            spojeno = true;
                            connection.connectionCompleted();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        mSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    BaseActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Veza nije uspostavljena!", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            }
        }).start();
    }

    public void showMyDialog(String string){

        if(string.equals("odabir")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Odaberi uređaj")
                    .setItems(devices, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            connectToDevice(which);
                        }
                    });
            builder.create().show();
        }else if(string.equals("upozorenje")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Upozorenje")
                    .setMessage("Prije spajanja na željeni uređaj potrebno je upariti se s njim u Bluetooth postavkama telefona!");
            builder.create().show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1 && resultCode == -1){
            Toast.makeText(getApplicationContext(), "Bluetooth uključen!\nPonovno pritisnite gumb Poveži!", Toast.LENGTH_LONG).show();
        }else if (requestCode == 1 && resultCode == 0) {
            Toast.makeText(getApplicationContext(), "Bluetooth nije uključen!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isConnected(){
        return spojeno;
    }

    public void setConnectionInterface(Connection connection) {
        this.connection = connection;
    }

    public void hideStatusBar() {

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSocket!=null){
            try {
                mSocket.close();
            } catch (IOException e) { }
        }

    }
}