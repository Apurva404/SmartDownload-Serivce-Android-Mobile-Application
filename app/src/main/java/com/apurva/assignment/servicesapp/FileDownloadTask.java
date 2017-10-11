package com.apurva.assignment.servicesapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


class FileDownloadTask extends AsyncTask<String, Integer, Boolean> {
    private String mDownloadURL;
    private Context mContext;

    FileDownloadTask(Context contextIn) {
        mContext = contextIn;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if(params.length > 0) {
            mDownloadURL = params[0];


            DataInputStream inStream = null;
            DataOutputStream outStream = null;
            try {
                URL url = new URL(mDownloadURL);
                URLConnection conn = url.openConnection();
                int contentLength = conn.getContentLength();
                inStream = new DataInputStream(url.openStream());

                String fileName = Util.getFileNameFromURL(mDownloadURL);
                File outFile = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), fileName);
                if(!outFile.exists())
                    outFile.createNewFile();
                outStream = new DataOutputStream(new FileOutputStream(outFile));

                byte[] buffer = new byte[4096];
                long totalBytesRead = 0;
                int lastPublishedPercentage = 0;
                while(totalBytesRead < contentLength) {
                    int bytesRead = inStream.read(buffer);
                    totalBytesRead += (long)bytesRead;
                    outStream.write(buffer, 0, bytesRead);
                    outStream.flush();
                    int percentageComplete = (int)(((double) totalBytesRead/contentLength) * 100);
                    if((percentageComplete - lastPublishedPercentage) >= 1) {
                        publishProgress(percentageComplete);
                        lastPublishedPercentage = percentageComplete;
                    }
                }
            } catch(FileNotFoundException e) {
                return false;
            } catch (IOException e) {
                return false;
            } finally {
                try {
                    if(inStream != null)
                        inStream.close();
                    if(outStream != null)
                        outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return true;
        } else
            return false;
    }

    @Override
    protected void onProgressUpdate(Integer... update) {
        Intent intent = new Intent(Constants.DOWNLOAD_PROGRESS_UPDATE_BROADCAST_INTENT);
        intent.putExtra(Constants.DOWNLOAD_URL_KEY, mDownloadURL);
        if(update.length > 0)
            intent.putExtra(Constants.DOWNLOAD_PROGRESS_UPDATE_KEY, update[0]);
        if(mContext != null)
            mContext.sendBroadcast(intent);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Intent intent;
        if(result) {
            intent = new Intent(Constants.DOWNLOAD_COMPLETE_BROADCAST_INTENT);
            intent.putExtra(Constants.DOWNLOAD_URL_KEY, mDownloadURL);
        } else {
            intent = new Intent(Constants.DOWNLOAD_FAILED_BROADCAST_INTENT);
            intent.putExtra(Constants.DOWNLOAD_URL_KEY, mDownloadURL);
        }
        if(mContext != null)
            mContext.sendBroadcast(intent);
    }
}
