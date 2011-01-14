package com.vbedegi;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setTitle("Tanker");

        setupUI();
        initializeUIForNewEntry();
        initializeUIWithLastEntry();
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
        initializeUIWithLastEntry();
    }

    private void insertIntoDb(ContentValues content) {
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.insert("tanker", DatabaseHelper.DATUM, content);
        db.close();
    }

    private void initializeUIWithLastEntry() {
        SQLiteDatabase db = new DatabaseHelper(this).getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from tanker where id=(select max(id) from tanker)", null);

        if (!cursor.moveToFirst()) {
            hideView(R.id.utolsoContainer);
            return;
        }

        showView(R.id.utolsoContainer);

        int index;
        String value;

        index = cursor.getColumnIndex("osszeg");
        value = cursor.getString(index);
        setControlValue(R.id.utolsoosszeg, value);

        index = cursor.getColumnIndex("datum");
        value = cursor.getString(index);
        setControlValue(R.id.utolsodatum, value);

        cursor.close();
    }

    private String getControlValue(int id) {
        EditText editText = (EditText) findViewById(id);
        return editText.getText().toString();
    }

    public void setControlValue(int id, String value) {
        TextView textView = (TextView) findViewById(id);
        textView.setText(value);
    }

    public void showView(int id) {
        View view = findViewById(id);
        if (view == null) return;
        view.setVisibility(View.VISIBLE);
    }

    public void hideView(int id) {
        View view = findViewById(id);
        if (view == null) return;
        view.setVisibility(View.INVISIBLE);
    }

    private static final int MENU_1 = Menu.FIRST + 1;
    private static final int MENU_2 = Menu.FIRST + 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        populateMenu(menu);
        return (super.onCreateOptionsMenu(menu));
    }

    private void populateMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_1, Menu.NONE, "History");
        menu.add(Menu.NONE, MENU_2, Menu.NONE, "Summary");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
    }

    private boolean applyMenuChoice(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_1:
                return true;
            case MENU_2:
                return true;
        }
        return false;  //To change body of created methods use File | Settings | File Templates.
    }
}

