package com.dazone.crewemail.Service;

import android.os.AsyncTask;
import android.util.Log;

import com.dazone.crewemail.utils.Util;

/**
 * Created by THANHTUNG on 30/12/2015.
 */
public class UploadFileToServer  extends AsyncTask<String, Integer, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
    }

    @Override
    protected String doInBackground(String... params) {
        return handleBackground(params);
    }

    protected String handleBackground(String... params) {
        String filePath = params[0];
        String fileName = params[1];
        return fileUploadDownload.uploadFileToServer(filePath, fileName);

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e("Server trả về:", result);
        Util.printLogs("Upload "+result);
    }
}
