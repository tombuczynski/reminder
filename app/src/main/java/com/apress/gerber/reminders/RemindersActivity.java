package com.apress.gerber.reminders;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;

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

         ActionBar bar = getSupportActionBar();
        if (bar != null) {
            //bar.setDisplayShowHomeEnabled(true);
            //bar.setHomeButtonEnabled(true);
            bar.setIcon(R.mipmap.ic_launcher);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                fireEditDialog(null);
            }
        });

        mRemindersDB = new RemindersDB(this);
        mRemindersDB.open();


/*
        if (savedInstanceState == null) {
            mRemindersDB.insertSomeReminders();
        }
*/


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
                fireEditDialog(null);
                return true;

            case R.id.action_about:
                fireAboutDialog();
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
                    switch (which) {
                        case 0:
                            fireEditDialog(mRemindersDB.fetchReminder((int)id));
                            break;

                        case 1:
                            mRemindersDB.delete(id);
                            mCursorAdapter.changeCursor(mRemindersDB.fetchAll());
                            break;

                        case 2:
                            TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    Reminder r = mRemindersDB.fetchReminder((int)id);
                                    scheduleAlarm(hourOfDay, minute, r.getContent());
                                }
                            };

                            Date now = new Date();
                            int hourOfDay = now.getHours();
                            int minute = now.getMinutes();

                            TimePickerDialog dialogTime = new TimePickerDialog(RemindersActivity.this,
                                    listener, hourOfDay, minute, true);
                            dialogTime.show();
                            break;

                        default:
                            Toast.makeText(RemindersActivity.this, "Pos:" + position + ", Id:" + id + ", Which:" + which, Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            });

            dialBuilder.show();
        }
    }

    private void fireAboutDialog() {
        AlertDialog.Builder dialBuilder = new AlertDialog.Builder(this);

        LayoutInflater li = getLayoutInflater();
        View v = li.inflate(R.layout.about_dialog_view, null);
        dialBuilder.setView(v);

        dialBuilder.show();
    }

    private void scheduleAlarm(int hourOfDay, int minute, String content) {
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.REMINDER_CONTENT, content);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Date now = new Date();
        now.setHours(hourOfDay);
        now.setMinutes(minute);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, now.getTime(), alarmIntent);
    }

    void fireEditDialog(final Reminder reminder) {
        AlertDialog.Builder dialBuilder = new AlertDialog.Builder(this);

        dialBuilder.setTitle(R.string.edit_dial_title_new);

        LayoutInflater li = getLayoutInflater();
        View v = li.inflate(R.layout.edit_dialog_view, null);
        dialBuilder.setView(v);

        final EditText editTextContent = (EditText)v.findViewById(R.id.editTextContent);
        final CheckBox checkBoxImportant = (CheckBox) v.findViewById(R.id.checkBoxImportant);

        if (reminder != null) {
            editTextContent.setText(reminder.getContent());
            checkBoxImportant.setChecked(reminder.getImportant() != 0);

            dialBuilder.setTitle(R.string.edit_dial_title_edit);
        }

        dialBuilder.setPositiveButton(R.string.edit_dial_pos_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = editTextContent.getText().toString();
                boolean important = checkBoxImportant.isChecked();
                if (reminder != null) {
                    mRemindersDB.update(reminder.getId(), content, important);
                } else {
                    mRemindersDB.insert(content, important);
                }
                mCursorAdapter.changeCursor(mRemindersDB.fetchAll());
            }
        });
        dialBuilder.setNegativeButton(R.string.edit_dial_neg_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialBuilder.show();
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