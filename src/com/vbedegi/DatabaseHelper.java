package com.vbedegi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Tanker";
    public static final String DATUM = "datum";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tanker (id INTEGER PRIMARY KEY AUTOINCREMENT, datum DATETIME, osszeg REAL, ar REAL, km REAL, lat REAL, lon REAL, uploaded BOOL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE tanker");
        //onCreate(db);
    }
}