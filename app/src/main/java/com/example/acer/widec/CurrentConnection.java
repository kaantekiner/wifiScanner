package com.example.acer.widec;

import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class CurrentConnection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_connection);
        //Toast.makeText(context, String.valueOf(wifiManager.getConnectionInfo()), Toast.LENGTH_SHORT).show();
        // Level of current connection
        //int rssi = wifiManager.getConnectionInfo().getRssi();
        //int level = WifiManager.calculateSignalLevel(rssi, 5);
        //System.out.println("Level is " + level + " out of 5");
        //sadece rssi DBM yi veriyor diğeri gücü galiba
    }
}
