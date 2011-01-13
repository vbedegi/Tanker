package com.vbedegi;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setTitle("Tanker");

        setupUI();
        initializeUIForNewEntry();
    }

    private void setupUI() {
        android.widget.Button button = (android.widget.Button) findViewById(R.id.kesz);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                store();
            }
        });
    }

    private void initializeUIForNewEntry() {
        EditText editText = (EditText) findViewById(R.id.datum);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String value = format.format(calendar.getTime());
        editText.setText(value);

        editText = (EditText) findViewById(R.id.osszeg);
        editText.setText("10000");
        editText.requestFocus();
    }

    private void fillLocation(ContentValues contentValues) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return;

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) return;
        contentValues.put("lat", location.getLatitude());
        contentValues.put("long", location.getLongitude());
    }

    private ContentValues buildContentValuesToStore() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("datum", getControlValue(R.id.datum));
        contentValues.put("osszeg", getControlValue(R.id.osszeg));
        contentValues.put("ar", getControlValue(R.id.ar));
        contentValues.put("km", getControlValue(R.id.km));

        contentValues.put("uploaded", false);

        fillLocation(contentValues);

        return contentValues;
    }

    private void store() {
        ContentValues content = buildContentValuesToStore();
        insertIntoDb(content);
        Toast.makeText(this, "Tankolás elmentve", Toast.LENGTH_SHORT).show();
    }

    private void insertIntoDb(ContentValues content) {
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.insert("tanker", DatabaseHelper.DATUM, content);
        db.close();
    }

    private String getControlValue(int id) {
        EditText editText = (EditText) findViewById(id);
        return editText.getText().toString();
    }
}

