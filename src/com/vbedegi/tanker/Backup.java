package com.vbedegi.tanker;

import android.database.Cursor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Backup {
    private DatabaseHelper databaseHelper;

    public Backup(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public JSONObject createBackup() throws JSONException {
        JSONObject root = new JSONObject();

        writeHeader(root);

        writeHistory(root);

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
}
