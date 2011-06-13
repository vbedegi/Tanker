package com.vbedegi.tanker.dropbox;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

public class UploadAsyncTask extends AsyncTask<File, Void, Integer> {

    private static final String TAG = "UploadAsyncTask";
    private DropboxUploadListener listener;
    private Dropbox api;

    public UploadAsyncTask(DropboxUploadListener listener, Dropbox api) {
        this.listener = listener;
        this.api = api;
    }

    @Override
    protected Integer doInBackground(File... files) {
        try {
            if (!api.isAuthenticated()) {
                listener.uploadFailed("not authenticated");
                return -1;
            }

            api.putFile("dropbox", "/", files[0]);

            listener.uploadSuccessful();
        } catch (Exception e) {
            listener.uploadFailed(e.getMessage());

            Log.e(TAG, "Error while uploading", e);
            return -1;
        }
        return 0;
    }
}
