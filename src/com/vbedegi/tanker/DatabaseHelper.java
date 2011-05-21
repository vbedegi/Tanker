package com.vbedegi.tanker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tanker";
    public static final String DATUM = "datum";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tanker (_id INTEGER PRIMARY KEY AUTOINCREMENT, datum DATETIME, osszeg REAL, ar REAL, km REAL, lat REAL, lon REAL, uploaded BOOL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE tanker");
        //onCreate(db);
    }

    public Cursor getLastEntry() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from tanker where _id=(select max(_id) from tanker)", null);
        if (!cursor.moveToFirst()) return null;
        return cursor;
    }
}
