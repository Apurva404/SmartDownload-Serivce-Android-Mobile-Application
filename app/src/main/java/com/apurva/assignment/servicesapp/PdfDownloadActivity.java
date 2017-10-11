package com.apurva.assignment.servicesapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class PdfDownloadActivity extends Activity {

    BoundService mService;
    boolean mBound;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            BoundService.LocalBinder binder = (BoundService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Toast.makeText(getBaseContext(), "Bound to Bound Service", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            Toast.makeText(getBaseContext(), "Unbound from Bound Service", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfdownload);
    }

    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BoundService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Toast.makeText(this, "Trying to bind to Bound Service", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            Toast.makeText(this, "Unbinding from Bound Service", Toast.LENGTH_SHORT).show();
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        this.finish();
        super.onDestroy();
    }


    public void startDownload1(View v) {
        String url1 = ((TextView) findViewById(R.id.url1)).getText().toString();
        String url2 = ((TextView) findViewById(R.id.url2)).getText().toString();
        String url3 = ((TextView) findViewById(R.id.url3)).getText().toString();

        Intent startServiceIntent = new Intent(this, StartedService.class);
        startServiceIntent.putExtra(Constants.DOWNLOAD_URL_KEY, new String[] {url1, url2, url3});
        startService(startServiceIntent);
    }

    public void startDownload2(View v) {
        String url4 = ((TextView) findViewById(R.id.url4)).getText().toString();
        String url5 = ((TextView) findViewById(R.id.url5)).getText().toString();

        if(mBound)
            mService.downloadFiles(new String[] {url4, url5});
    }
}