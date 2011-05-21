package com.vbedegi.tanker;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class HistoryActivity extends ListActivity {

    private ListView listView;
    private CursorAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.history);

        adapter = createListAdapter();
        setListAdapter(adapter);

        listView = getListView();
        registerForContextMenu(listView);
    }

    private CursorAdapter createListAdapter() {
        Cursor cursor = openDatabase();
        return new HistoryCursorAdapter(this, R.layout.history_list_item, cursor);
    }

    private Cursor openDatabase() {
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.rawQuery("select * from tanker order by _id desc", null);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (view == listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Menü");

            menu.add(Menu.NONE, 0, 0, "Töröl");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final int itemId = (int) adapter.getItemId(info.position);

        int menuItemIndex = item.getItemId();

        if (menuItemIndex == 0) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Biztos?")
                    .setMessage("Biztosan törlöd?")
                    .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteRecord(itemId);
                            adapter.getCursor().requery();
                        }
                    })
                    .setNegativeButton("Nem", null)
                    .show();
        }

        return true;
    }

    private void deleteRecord(int id) {
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from tanker where _id=" + Integer.toString(id));
    }

    class HistoryCursorAdapter extends ResourceCursorAdapter {

        public HistoryCursorAdapter(Context context, int layout, Cursor c) {
            super(context, layout, c);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String datum = cursor.getString(cursor.getColumnIndex("datum"));
            String osszeg = cursor.getString(cursor.getColumnIndex("osszeg"));
            String ar = cursor.getString(cursor.getColumnIndex("ar")).trim();

            if (ar.length() == 0 || ar == null) {
                ar = "?";
            }

            setText(view, android.R.id.text1, datum);
            setText(view, android.R.id.text2, osszeg);
            setText(view, R.id.history_item_ar, ar);
        }

        private void setText(View view, int id, String value) {
            TextView textView = (TextView) view.findViewById(id);
            if (textView != null) textView.setText(value);
        }
    }
}

