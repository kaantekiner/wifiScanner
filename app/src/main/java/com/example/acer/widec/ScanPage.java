package com.example.acer.widec;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ScanPage extends AppCompatActivity
{
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 0;
    private static WifiManager wifiManager;

    private static ListView listView;
    private static List<ScanResult> results;
    private static  ArrayList<String> arrayList = new ArrayList<>();
    private static ArrayAdapter adapter;
    public static TextView nonetext;
    public static  TextView starttext;
    public static boolean stopscan = false;
    private  ScrollView mainscrollview;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_page);
        context = getApplicationContext();
        stopscan = false;
        RequesPermissions();
    }

    public void RequesPermissions()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
        else
        {
            setupWidgets();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            setupWidgets();
        }
    }

    public void setupWidgets()
    {
        mainscrollview = (ScrollView)findViewById(R.id.mainscrollview);
        nonetext = (TextView)findViewById(R.id.nonetext);
        starttext = (TextView)findViewById(R.id.starttext);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        listView = (ListView)findViewById(R.id.wifiList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.parseColor("#a1a1a1"));
                return view;
            }
        };
        listView.setAdapter(adapter);

        if (!wifiManager.isWifiEnabled())
        {
            starttext.setText("WiFi is disabled, please restart app");
        }
        else
        {
            scanWifi();
        }
    }

    public static void scanWifi()
    {
        ListenStarttext();
        if(stopscan == false && wifiManager.isWifiEnabled())
        {
            arrayList.clear();
            context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
        }
    }

    static BroadcastReceiver wifiReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(final Context context, Intent intent)
        {
            if(wifiManager.isWifiEnabled())
            {
                nonetext.setVisibility(View.GONE);
                results = wifiManager.getScanResults();
                context.unregisterReceiver(this);
                try
                {
                    arrayList.clear();
                    adapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren();
                    arrayList.add("");
                    arrayList.add("");
                    for (ScanResult scanResult : results)
                    {
                        String original = "";
                        char space = ' ';
                        int repeattimes = (60 - (scanResult.SSID.length() * 2)) - 2;
                        char[] repeat = new char[repeattimes];
                        Arrays.fill(repeat, space);
                        original += new String(repeat);
                        arrayList.add("  " + scanResult.SSID + original + "%" + WifiManager.calculateSignalLevel(scanResult.level, 100));
                    }
                    arrayList.add("");
                    adapter.notifyDataSetChanged();

                    int position1 = listView.getSelectedItemPosition();
                    listView.setSelection(position1);

                    setListViewHeightBasedOnChildren();
                    listView.setEnabled(false);
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
                            //Toast.makeText(context, "WiFi is disabled, please restart app", Toast.LENGTH_SHORT).show();
                            starttext.setText("WiFi is disabled, please restart app");
                            starttext.setClickable(false);
                            starttext.setEnabled(false);
                            arrayList.clear();
                            adapter.notifyDataSetChanged();
                            setListViewHeightBasedOnChildren();
                        }
                        else
                        {
                            scanWifi();
                        }
                    }
                }.start();
            }
            else
            {
                starttext.setText("WiFi is disabled, please restart app");
                starttext.setClickable(false);
                starttext.setEnabled(false);
                arrayList.clear();
                adapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren();
            }
        }
    };

    public static void setListViewHeightBasedOnChildren() // use with scroll view and just one listview?...
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private static void ListenStarttext()
    {
        starttext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!stopscan && wifiManager.isWifiEnabled())
                {
                    stopscan = true;
                    starttext.setText("Please wait");
                    new CountDownTimer(600, 100)
                    {
                        public void onTick(long millisUntilFinished)
                        {
                        }
                        public void onFinish()
                        {
                            listView.setEnabled(true);
                            starttext.setText("Scan stopped, select \n or restart app");
                            ListenListClick();
                        }
                    }.start();
                }
            }
        });
    }

    private static void ListenListClick()
    {
        if(stopscan)
        {
            listView.setEnabled(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    if(position == 0 || position == 1 || position == arrayList.size() -1)
                    {
                        return;
                    }
                    else
                    {
                        String selectedItem = (String) parent.getItemAtPosition(position);
                        int spaceindex = 0;
                        for(int i = 2; i < selectedItem.length(); i++)
                        {
                            if(selectedItem.charAt(i) == ' ' && spaceindex == 0)
                            {
                                spaceindex = i;
                            }
                        }
                        selectedItem = selectedItem.substring(2,spaceindex);
                        Intent intent = new Intent(context,Wifiinfo.class);
                        intent.putExtra("selectedwifiname", selectedItem);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
    @Override
    public void onBackPressed()
    {
        stopscan = true;
        finish();
    }
}
