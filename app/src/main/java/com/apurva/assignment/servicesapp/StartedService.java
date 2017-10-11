package com.apurva.assignment.servicesapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

public class StartedService extends Service {

    private int mCurrentDownloadCount;
    private String[] mDownloadURLs;
    private Toast mToast;

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(Constants.DOWNLOAD_PROGRESS_UPDATE_KEY, 0);
            String url = intent.getStringExtra(Constants.DOWNLOAD_URL_KEY);
            if (url != null) {
                String filname = Util.getFileNameFromURL(url);
                mToast.setText("Started Service - Download of " + filname + " " + progress + "%");
                mToast.show();
            }
        }
    };

    private BroadcastReceiver completionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String url = intent.getStringExtra(Constants.DOWNLOAD_URL_KEY);
            if(url != null) {
                String filname = Util.getFileNameFromURL(url);
                mToast.setText("Started Service - Download of " + filname + " complete!");
                mToast.show();
            }
            mCurrentDownloadCount++;
            if(mCurrentDownloadCount == mDownloadURLs.length)
                finish();
            else
                triggerNextDownload();
        }
    };

    private BroadcastReceiver failureReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String url = intent.getStringExtra(Constants.DOWNLOAD_URL_KEY);
            if(url != null) {
                String filname = Util.getFileNameFromURL(url);
                mToast.setText("Started Service - Download of " + filname + " failed!");
                mToast.show();
            }
            mCurrentDownloadCount++;
            if(mCurrentDownloadCount == mDownloadURLs.length)
                finish();
            else
                triggerNextDownload();
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Started Service - starting..", Toast.LENGTH_SHORT).show();
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDownloadURLs = intent.getStringArrayExtra(Constants.DOWNLOAD_URL_KEY);
        mCurrentDownloadCount = 0;

        registerReceiver(progressReceiver, new IntentFilter(Constants.DOWNLOAD_PROGRESS_UPDATE_BROADCAST_INTENT));
        registerReceiver(completionReceiver, new IntentFilter(Constants.DOWNLOAD_COMPLETE_BROADCAST_INTENT));
        registerReceiver(failureReceiver, new IntentFilter(Constants.DOWNLOAD_FAILED_BROADCAST_INTENT));

        triggerNextDownload();

        return START_STICKY;
    }

    private void triggerNextDownload() {
        mToast.setText("Started Service - Starting to download "
                + Util.getFileNameFromURL(mDownloadURLs[mCurrentDownloadCount]));
        mToast.show();
        new FileDownloadTask(getBaseContext()).execute(mDownloadURLs[mCurrentDownloadCount]);
    }

    private void finish() {
        unregisterReceiver(progressReceiver);
        unregisterReceiver(completionReceiver);
        unregisterReceiver(failureReceiver);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Started Service - stopping..", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

}
