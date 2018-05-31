package com.apress.gerber.reminders;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class RemindersActivity extends AppCompatActivity {

    private RemindersDB mRemindersDB;

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

        Cursor cur = mRemindersDB.fetchAll();

        RemindersCursorAdapter a = new RemindersCursorAdapter(this,
                R.layout.reminders_row, cur,
                new String[]{RemindersDB.COL_CONTENT, RemindersDB.COL_IMPORTANT},
                new int[]{R.id.row_text, R.id.row_tab});

        ListView lv = (ListView)findViewById(R.id.reminders_list_view);
        lv.setAdapter(a);
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
}

//tools:showIn="@layout/activity_reminders"