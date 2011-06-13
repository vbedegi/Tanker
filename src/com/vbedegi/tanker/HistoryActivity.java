package com.vbedegi.tanker;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
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
import org.json.JSONException;

import java.io.IOException;

public class HistoryActivity extends ListActivity {

    private ListView listView;
    private CursorAdapter adapter;
    private ProgressDialog progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.history);

        adapter = createListAdapter();
        setListAdapter(adapter);

        listView = getListView();
        registerForContextMenu(listView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("relax!");
    }

    private CursorAdapter createListAdapter() {
        Cursor cursor = openDatabase();
        return new HistoryCursorAdapter(this, R.layout.history_list_item, cursor);
    }

    private Cursor openDatabase() {
        return new DatabaseHelper(this).getHistory(true);
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

    private static final int MENU_1 = Menu.FIRST + 1;
    private static final int MENU_2 = Menu.FIRST + 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        populateMenu(menu);
        return (super.onCreateOptionsMenu(menu));
    }

    private void populateMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_1, Menu.NONE, "Backup");
        menu.add(Menu.NONE, MENU_2, Menu.NONE, "Restore");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
    }

    private boolean applyMenuChoice(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_1:
                backup();
                return true;
            case MENU_2:
                //restore();
                return true;
        }
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    private void backup() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Biztos?")
                .setMessage("Indulhat a backup a Dropbox-ra ?")
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startBackup();
                    }
                })
                .setNegativeButton("Nem", null)
                .show();
    }

    private void startBackup() {
        final Context ctx = this;

        progressDialog.setMessage("backing up...");
        progressDialog.show();

        AsyncListener<Void, Void> listener = new AsyncListener<Void, Void>() {
            @Override
            public void completed(Void... result) {
                progressDialog.dismiss();
                Toast.makeText(ctx, "backup kész", 2000);
            }

            @Override
            public void failed(Void... result) {
                progressDialog.dismiss();
                Toast.makeText(ctx, "hiba a backup közben", 2000);
            }
        };
        BackupAsyncTask task = new BackupAsyncTask(this, listener);
        task.execute();
    }

    private void restore() {
        Restore restore = new Restore(this);
        try {
            restore.restore();
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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

