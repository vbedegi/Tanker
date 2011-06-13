package com.vbedegi.tanker;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import com.vbedegi.tanker.dropbox.Dropbox;
import com.vbedegi.tanker.dropbox.DropboxAPIFactory;
import com.vbedegi.tanker.dropbox.DropboxLoginListener;
import com.vbedegi.tanker.dropbox.DropboxUploadListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class BackupAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private AsyncListener<Void, Void> listener;
    private boolean completed = false;

    public BackupAsyncTask(Context context, AsyncListener<Void, Void> listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Backup backup = new Backup(context);
        try {
            JSONObject json = backup.createBackup();
            File file = saveToFile(json);
            updateToDropbox(file);
        } catch (Exception e) {
        }
        completed = true;

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (completed) {
            listener.completed();
        } else {
            listener.failed();
        }
    }

    private void updateToDropbox(final File file) {
        final Dropbox api = DropboxAPIFactory.create(context);

        final DropboxUploadListener uploadListener = new DropboxUploadListener() {
            @Override
            public void uploadSuccessful() {
                //listener.completed();
            }

            @Override
            public void uploadFailed(String message) {
                //listener.failed();
            }
        };

        DropboxLoginListener loginListener = new DropboxLoginListener() {
            @Override
            public void loginSuccessfull() {
                api.upload(uploadListener, file);
            }

            @Override
            public void loginFailed(String message) {
                //listener.failed();
            }
        };

        api.login(loginListener, "vbedegi.1@gmail.com", "tanker");
    }

    private File saveToFile(JSONObject json) throws JSONException, IOException {
        String serialized = json.toString(4);

        File path = Environment.getExternalStorageDirectory();
        File file = new File(path, "tanker.json");

        FileOutputStream out = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(out);
        writer.write(serialized);
        writer.flush();
        writer.close();

        return file;
    }
}

