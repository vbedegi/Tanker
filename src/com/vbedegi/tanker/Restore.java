package com.vbedegi.tanker;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class Restore {
    private Context context;
    private DatabaseHelper databaseHelper;

    public Restore(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public void restore() throws JSONException, IOException {
        String jsonString = loadJsonString();
        JSONObject json = new JSONObject(jsonString);

        JSONArray entries = json.getJSONArray("entries");
        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.getJSONObject(i);
            databaseHelper.insertEntry(buildContentValuesToStore(entry));
        }
    }

    private ContentValues buildContentValuesToStore(JSONObject entry) throws JSONException {
        ContentValues contentValues = new ContentValues();

        contentValues.put("datum", entry.getString("datum"));
        contentValues.put("osszeg", entry.getString("osszeg"));
        contentValues.put("ar", entry.getString("ar"));
        contentValues.put("km", entry.optString("km", null));
        contentValues.put("uploaded", false);

        return contentValues;
    }

    private String loadJsonString() throws IOException {
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path, "tanker.json");

        StringBuilder builder = new StringBuilder();
        FileInputStream in = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();
    }
}
