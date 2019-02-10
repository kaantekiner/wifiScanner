package com.example.acer.widec;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class WifiScan extends AppCompatActivity
{
    private WifiManager wifiManager;
    ImageView wifiicon;
    TextView starttext;
    TextView infotext1;
    TextView infotext0;
    TextView infotext01;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan);
        SetupWidgets();
        ListenClicks();
    }

    void SetupWidgets()
    {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiicon = (ImageView)findViewById(R.id.wifiicon);
        starttext = (TextView)findViewById(R.id.starttext);
        infotext1 = (TextView)findViewById(R.id.infotext1);
        infotext0 = (TextView)findViewById(R.id.infotext0);
        infotext01 = (TextView)findViewById(R.id.infotext01);
    }

    public void ListenClicks()
    {
        starttext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                starttext.setVisibility(View.GONE);
                wifiicon.setVisibility(View.VISIBLE);
                infotext1.setVisibility(View.VISIBLE);
                infotext0.setVisibility(View.GONE);
                infotext01.setVisibility(View.GONE);
                new CountDownTimer(1000, 100)
                {
                    public void onTick(long millisUntilFinished)
                    {
                    }
                    public void onFinish()
                    {
                        CheckWifiState();
                    }
                }.start();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void CheckWifiState()
    {
        if (!wifiManager.isWifiEnabled())
        {
            infotext1.setText("WiFi is disabled, "+ "enabling it again");
            wifiManager.setWifiEnabled(true);
            new CountDownTimer(1200, 100)
            {
                public void onTick(long millisUntilFinished)
                {
                }
                public void onFinish()
                {
                    infotext1.setText("WiFi enabled");
                    new CountDownTimer(700, 100)
                    {

                        public void onTick(long millisUntilFinished)
                        {
                        }
                        public void onFinish()
                        {
                            CheckWifiState();
                        }

                    }.start();
                }
            }.start();
        }
        else
        {
            infotext1.setText("WiFi is enable, starting scan");
            new CountDownTimer(700, 100)
            {
                public void onTick(long millisUntilFinished)
                {
                }
                public void onFinish()
                {
                    ChangeToStartScanPage();
                }
            }.start();
        }
    }

    private void ChangeToStartScanPage()
    {
        //Toast.makeText(this, "scan start", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(WifiScan.this,ScanPage.class);
        startActivity(intent);
        new CountDownTimer(400, 100)
        {
            public void onTick(long millisUntilFinished)
            {
            }
            public void onFinish()
            {
                infotext1.setText("Checking wifi state");
                starttext.setVisibility(View.VISIBLE);
                wifiicon.setVisibility(View.GONE);
                infotext1.setVisibility(View.GONE);
                infotext0.setVisibility(View.VISIBLE);
                infotext01.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}
