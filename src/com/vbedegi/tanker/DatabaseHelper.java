package com.vbedegi.tanker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public Date getLastDate() throws ParseException {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select max(datum) as maxdatum from tanker", null);
        if (!cursor.moveToFirst()) return null;

        String value = cursor.getString(cursor.getColumnIndex("maxdatum"));
        DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        return formatter.parse(value);
    }

    public Cursor getHistory(boolean sortDescending) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "select * from tanker order by _id";
        if (sortDescending) sql += " desc";
        return db.rawQuery(sql, null);
    }

    public void insertEntry(ContentValues content) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert("tanker", DatabaseHelper.DATUM, content);
        db.close();
    }

    public void clearAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from tanker");
    }
}
