package com.example.acer.widec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class Wifiinfo extends AppCompatActivity
{
    String selectedwifiname;
    TextView APnametext;
    private WifiManager wifiManager;
    boolean stopscan = false;
    private List<ScanResult> results;
    TextView signallost;
    TextView wifidisabled;
    TextView bssidbig;
    TextView dbm;
    TextView levelpercent;
    TextView bssid;
    TextView centerFreq0;
    TextView centerFreq1;
    TextView frequency;
    TextView channelWidth;
    TextView operatorFriendlyName;
    TextView timestamp;
    TextView venueName;
    TextView is80211mcResponder;
    TextView isPasspointNetwork;
    TextView advancedtext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiinfo);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        selectedwifiname = extras.getString("selectedwifiname");
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled())
        {
            setupWidgets();
        }
        else
        {
            stopscan = true;
            ScanPage.stopscan = false;
            Toast.makeText(this, "WiFi is disabled, please restart app", Toast.LENGTH_SHORT).show();
            ScanPage.scanWifi();
            finish();
            Intent intent2 = new Intent(Wifiinfo.this,WifiScan.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent2);
        }
    }

    private void setupWidgets()
    {
        APnametext = (TextView)findViewById(R.id.APname);
        APnametext.setText(selectedwifiname);
        signallost = (TextView)findViewById(R.id.signallost);
        signallost.setVisibility(View.GONE);
        wifidisabled = (TextView)findViewById(R.id.wifidisabled);
        wifidisabled.setVisibility(View.GONE);
        bssidbig = (TextView)findViewById(R.id.bssidbig);
        dbm = (TextView)findViewById(R.id.dbm);
        levelpercent = (TextView)findViewById(R.id.levelpercent);
        bssid = (TextView)findViewById(R.id.bssid);
        centerFreq0 = (TextView)findViewById(R.id.centerFreq0);
        centerFreq1 = (TextView)findViewById(R.id.centerFreq1);
        frequency = (TextView)findViewById(R.id.frequency);
        channelWidth = (TextView)findViewById(R.id.channelWidth);
        operatorFriendlyName = (TextView)findViewById(R.id.operatorFriendlyName);
        timestamp = (TextView)findViewById(R.id.timestamp);
        venueName = (TextView)findViewById(R.id.venueName);
        is80211mcResponder  = (TextView)findViewById(R.id.is80211mcResponder);
        isPasspointNetwork = (TextView)findViewById(R.id.isPasspointNetwork);
        advancedtext = (TextView)findViewById(R.id.advancedtext);
        scanWifi();
    }

    private void scanWifi()
    {
        if(stopscan == false && wifiManager.isWifiEnabled())
        {
            registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
            //Toast.makeText(Wifiinfo.this, "tekrar başladı", Toast.LENGTH_SHORT).show();
        }
        if(!wifiManager.isWifiEnabled())
        {
            stopscan = true;
            ScanPage.stopscan = false;
            Toast.makeText(this, "WiFi is disabled, please restart app", Toast.LENGTH_SHORT).show();
            ScanPage.scanWifi();
            finish();

            Intent intent2 = new Intent(Wifiinfo.this,WifiScan.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent2);
        }
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(final Context context, Intent intent)
        {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);
            try
            {
                boolean iswifistillhere = false;
                for(int i = 0; i < results.size();  i++)
                {
                    if(results.get(i).toString().contains("SSID: " + selectedwifiname + ","))
                    {
                        iswifistillhere = true;
                    }
                }
                if(iswifistillhere)
                {
                    wifidisabled.setVisibility(View.GONE);
                    signallost.setVisibility(View.GONE);
                    dbm.setVisibility(View.VISIBLE);
                    levelpercent.setVisibility(View.VISIBLE);
                    bssidbig.setVisibility(View.VISIBLE);
                    bssid.setVisibility(View.VISIBLE);
                    centerFreq0.setVisibility(View.VISIBLE);
                    centerFreq1.setVisibility(View.VISIBLE);
                    frequency.setVisibility(View.VISIBLE);
                    channelWidth.setVisibility(View.VISIBLE);
                    operatorFriendlyName.setVisibility(View.VISIBLE);
                    timestamp.setVisibility(View.VISIBLE);
                    venueName.setVisibility(View.VISIBLE);
                    is80211mcResponder.setVisibility(View.VISIBLE);
                    isPasspointNetwork.setVisibility(View.VISIBLE);
                    advancedtext.setVisibility(View.VISIBLE);
                    //Toast.makeText(context, "currentscanResult burda", Toast.LENGTH_SHORT).show();
                    signallost.setVisibility(View.GONE);
                    for (ScanResult currentscanResult : results)
                    {
                        if(currentscanResult.SSID.equals(selectedwifiname))
                        {
                            APnametext.setText(currentscanResult.SSID);
                            dbm.setText(String.valueOf(currentscanResult.level) + " dBm");
                            levelpercent.setText( "% " + String.valueOf(WifiManager.calculateSignalLevel(currentscanResult.level, 100)));
                            bssidbig.setText(String.valueOf(currentscanResult.capabilities));
                            bssid.setText("BSSID : " + String.valueOf(currentscanResult.BSSID));
                            frequency.setText("frequency : " + String.valueOf(currentscanResult.frequency));
                            timestamp.setText("timestamp : " + String.valueOf(currentscanResult.timestamp));
                            is80211mcResponder.setText("is80211mcResponder : " + String.valueOf(currentscanResult.is80211mcResponder()));
                            isPasspointNetwork.setText("isPasspointNetwork : " + String.valueOf(currentscanResult.isPasspointNetwork()));
                            channelWidth.setText("channelWidth : " + String.valueOf(currentscanResult.channelWidth));
                            centerFreq0.setText("centerFreq0 : " + String.valueOf(currentscanResult.centerFreq0));
                            centerFreq1.setText("centerFreq1 : " + String.valueOf(currentscanResult.centerFreq1));

                            String operatorfriendlynamestring = String.valueOf(currentscanResult.operatorFriendlyName);
                            String venuenamestring = String.valueOf(currentscanResult.venueName);
                            if(operatorfriendlynamestring == null ||operatorfriendlynamestring.equals("") || operatorfriendlynamestring.equals(" "))
                            {
                                operatorfriendlynamestring = "?";
                            }
                            if(venuenamestring == null ||venuenamestring.equals("") || venuenamestring.equals(" "))
                            {
                                venuenamestring = "?";
                            }
                            operatorFriendlyName.setText("operatorFriendlyName : " + operatorfriendlynamestring);
                            venueName.setText("venueName : " + venuenamestring);
                        }
                    }
                    iswifistillhere = false;
                }
                else
                {
                    signallost.setVisibility(View.VISIBLE);
                    wifidisabled.setVisibility(View.GONE);
                    dbm.setVisibility(View.GONE);
                    levelpercent.setVisibility(View.GONE);
                    bssidbig.setVisibility(View.GONE);
                    bssid.setVisibility(View.GONE);
                    centerFreq0.setVisibility(View.GONE);
                    centerFreq1.setVisibility(View.GONE);
                    frequency.setVisibility(View.GONE);
                    channelWidth.setVisibility(View.GONE);
                    operatorFriendlyName.setVisibility(View.GONE);
                    timestamp.setVisibility(View.GONE);
                    venueName.setVisibility(View.GONE);
                    is80211mcResponder.setVisibility(View.GONE);
                    isPasspointNetwork.setVisibility(View.GONE);
                    advancedtext.setVisibility(View.GONE);
                    iswifistillhere = false;
                }
                iswifistillhere = false;
            }
            catch (Exception e)
            {
                Log.w("WifScanner", "Exception: "+e);
            }
            //Repeat it again
            new CountDownTimer(200, 100)
            {
                public void onTick(long millisUntilFinished)
                {
                }
                public void onFinish()
                {
                    if (!wifiManager.isWifiEnabled())
                    {
                        Toast.makeText(context, "WiFi is disabled, please restart app", Toast.LENGTH_SHORT).show();
                        wifidisabled.setVisibility(View.VISIBLE);
                        dbm.setVisibility(View.GONE);
                        levelpercent.setVisibility(View.GONE);
                        bssidbig.setVisibility(View.GONE);
                        bssid.setVisibility(View.GONE);
                        centerFreq0.setVisibility(View.GONE);
                        centerFreq1.setVisibility(View.GONE);
                        frequency.setVisibility(View.GONE);
                        channelWidth.setVisibility(View.GONE);
                        operatorFriendlyName.setVisibility(View.GONE);
                        timestamp.setVisibility(View.GONE);
                        venueName.setVisibility(View.GONE);
                        is80211mcResponder.setVisibility(View.GONE);
                        isPasspointNetwork.setVisibility(View.GONE);
                        advancedtext.setVisibility(View.GONE);
                    }
                    else
                    {
                        scanWifi();
                    }
                }
            }.start();
        }
    };
    @Override
    public void onBackPressed()
    {
        if(!wifiManager.isWifiEnabled())
        {
            stopscan = true;
            ScanPage.stopscan = false;
            Toast.makeText(this, "WiFi is disabled, please restart app", Toast.LENGTH_SHORT).show();
            ScanPage.scanWifi();
            finish();
            Intent intent2 = new Intent(Wifiinfo.this,WifiScan.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent2);
        }
        else
        {
            stopscan = true;
            ScanPage.stopscan = false;
            ScanPage.starttext.setText("Avaible wireless networks" + "\n" + "tap to stop and select");
            ScanPage.scanWifi();
            finish();
        }
    }
}
