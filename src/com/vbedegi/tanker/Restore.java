package com.vbedegi.tanker;

import android.R;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class Restore {
    private Context context;
    private DatabaseHelper databaseHelper;
    private boolean quietMode = false;

    public Restore(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public void restore() throws JSONException, IOException {
        String jsonString = loadJsonString();
        JSONObject json = new JSONObject(jsonString);

        if (!quietMode) {
            queryForConfirmation(json);
        } else {
            executeRestore(json);
        }
    }

    private void executeRestore(JSONObject json) throws JSONException {
        databaseHelper.clearAll();

        JSONArray entries = json.getJSONArray("entries");
        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.getJSONObject(i);
            databaseHelper.insertEntry(buildContentValuesToStore(entry));
        }
    }

    private void queryForConfirmation(final JSONObject json) {
        String createdAt = json.optString("createdAt", null);

        if (createdAt == null) {
            Toast.makeText(context, "ez meg mi?", 2000);
            return;
        }

        new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_dialog_alert)
                .setTitle("Biztos?")
                .setMessage("Biztosan visszaállítod (" + createdAt + ") ?")
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            executeRestore(json);
                        } catch (JSONException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                })
                .setNegativeButton("Nem", null)
                .show();
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

    public void setQuietMode(boolean quietMode) {
        this.quietMode = quietMode;
    }
}
