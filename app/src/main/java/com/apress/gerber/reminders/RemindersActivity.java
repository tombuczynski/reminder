package com.apress.gerber.reminders;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class RemindersActivity extends AppCompatActivity {

    private static final String TAG = "RemindersActivity";

    private RemindersDB mRemindersDB;
    private RemindersCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mRemindersDB = new RemindersDB(this);
        mRemindersDB.open();


        if (savedInstanceState == null) {
            mRemindersDB.insertSomeReminders();
        }


        Cursor cur = mRemindersDB.fetchAll();

        mCursorAdapter = new RemindersCursorAdapter(this,
                R.layout.reminders_row, cur,
                new String[]{RemindersDB.COL_CONTENT, RemindersDB.COL_IMPORTANT},
                new int[]{R.id.row_text, R.id.row_tab});

        ListView lv = (ListView)findViewById(R.id.reminders_list_view);
        ListViewListener listener = new ListViewListener();
        lv.setOnItemClickListener(listener);
        lv.setAdapter(mCursorAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            multiChoice(lv);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_new:
                return true;

            case R.id.action_exit:
                mRemindersDB.close();
                finish();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private class ListViewListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
            //Toast.makeText(RemindersActivity.this, "Pos:" + position + ", Id:" + id, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialBuilder = new AlertDialog.Builder(RemindersActivity.this);
            dialBuilder.setItems(R.array.listitem_choice, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(RemindersActivity.this, "Pos:" + position + ", Id:" + id + ", Which:" + which, Toast.LENGTH_SHORT).show();

                }
            });

            dialBuilder.show();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void multiChoice(final AbsListView lv)
    {
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.list_action_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                if (item.getItemId() == R.id.menu_item_delete) {
                    long[] checkedIds = lv.getCheckedItemIds();

                    for (long id : checkedIds) {
                        mRemindersDB.delete(id);
                    }

                    mCursorAdapter.changeCursor(mRemindersDB.fetchAll());

                    mode.finish();
                    return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

    }
}

//tools:showIn="@layout/activity_reminders"