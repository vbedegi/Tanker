package com.vbedegi.tanker;

import android.database.Cursor;
import android.os.Environment;
import com.dropbox.client.DropboxAPI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class Backup {
    private DatabaseHelper databaseHelper;

    public Backup(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public JSONObject createBackup() throws JSONException {
        JSONObject root = new JSONObject();

        writeHeader(root);

        writeHistory(root);

        String j = root.toString(4);


        File path = Environment.getExternalStorageDirectory();
        File file = new File(path, "tanker.json");
        try {
            FileOutputStream out = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.write(j);
            writer.flush();
            writer.close();
            dbox(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    private void writeHistory(JSONObject root) throws JSONException {
        JSONArray array = new JSONArray();

        Cursor cursor = databaseHelper.getHistory(false);

        while (cursor.moveToNext()) {
            JSONObject entry = createHistoryEntry(cursor);
            array.put(entry);
        }

        root.put("entries", array);
    }

    private JSONObject createHistoryEntry(Cursor cursor) {
        JSONObject entry = new JSONObject();

        try {
            entry.put("osszeg", cursor.getString(cursor.getColumnIndex("osszeg")));
            entry.put("datum", cursor.getString(cursor.getColumnIndex("datum")));
            entry.put("ar", cursor.getString(cursor.getColumnIndex("ar")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entry;
    }

    private void writeHeader(JSONObject root) throws JSONException {
        root.put("createdAt", DateUtils.toString(DateUtils.now()));
    }

    private void dbox(File file) {
        DropboxAPI api = new DropboxAPI();
//        api.putFile("/tanker","/tanker", file);
    }
}

