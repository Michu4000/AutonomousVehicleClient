// Autonomous Vehicle Client for Android
// version 0.5 17.05.2018
// Michal K
//
package com.michu.AutonomousVehicleClient;

import android.Manifest;
import android.app.ActivityGroup;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActivityGroup {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // lock vertical orientation

        // tabs setup
        TabHost tab = findViewById(android.R.id.tabhost);
        tab.setup(this.getLocalActivityManager());

        TabHost.TabSpec spec1 = tab.newTabSpec("Scan Mode");
        spec1.setIndicator("Scan Mode");
        Intent intent1 = new Intent().setClass(this, Scan.class);
        spec1.setContent(intent1);
        tab.addTab(spec1);
        TextView tv1= tab.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tv1.setTextColor(Color.WHITE);
        tv1.setAllCaps(false);
        tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TabHost.TabSpec spec2 = tab.newTabSpec("Manual Control Mode");
        spec2.setIndicator("Manual Control Mode");
        Intent intent2 = new Intent().setClass(this, ManualControl.class);
        spec2.setContent(intent2);
        tab.addTab(spec2);
        TextView tv2 = tab.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        tv2.setTextColor(Color.WHITE);
        tv2.setAllCaps(false);
        tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check application permissions and request them if it's necessary
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )

            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE} , 0);
        else {
            WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if(wifiMgr.isWifiEnabled()) { // wifi is on
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

                if(wifiInfo.getNetworkId() == -1){ //  not connected to an access point
                    Toast.makeText(this, "Not connected to AP!", Toast.LENGTH_LONG).show();
                    finish(); // terminate application
                }
                if(!wifiInfo.getSSID().equals("\"autonomous\"")) { // connected to bad access point
                    Toast.makeText(this, "Connected to bad AP!", Toast.LENGTH_LONG).show();
                    finish(); // terminate application
                }
            }
            else { // wifi is off
                Toast.makeText(this, "WiFi is disabled!", Toast.LENGTH_LONG).show();
                finish(); // terminate application
            }

            // connected to good access point
            int connStatus = Connectivity.connect(); // connect to vehicle
            if(connStatus == 0) // if connect status == 0 -> connect OK
                Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show();
            else {
                Connectivity.showErrors(connStatus, this); // show connection error
                finish(); // terminate application
            }
        }
    }

    // after permissions dialog
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {

            // restart application if permissions were granted
            Intent mStartActivity = new Intent(MainActivity.this, MainActivity.class);
            int mPendingIntentId = 130794;
            PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis(), mPendingIntent);
        }
        // or only exit if they weren't
        Toast.makeText(this, "No permissions!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onPause() {
        Connectivity.disconnect(); // disconnect from vehicle
        super.onPause();
    }

    @Override
    protected  void onDestroy() {
        Connectivity.disconnect(); // disconnect from vehicle
        super.onDestroy();
    }
}