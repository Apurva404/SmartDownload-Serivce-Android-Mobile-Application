package com.apurva.assignment.servicesapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void download(View v) {
        Intent intent = new Intent(MainActivity.this, PdfDownloadActivity.class);
        startActivity(intent);
    }

    public void closeApp(View v) {
        Intent stopServiceIntent = new Intent(this, StartedService.class);
        stopService(stopServiceIntent);
        this.finish();
    }

}
