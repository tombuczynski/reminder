package com.apress.gerber.reminders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Tom Buczynski on 20.04.2018.
 */
public class RemindersDB {
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANT = "important";
    private static final String DB_NAME = "reminders.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "reminders";
    private static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                    " ( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CONTENT + " TEXT, " +
                    COL_IMPORTANT + " INTEGER " + ");";

    private static final String TAG = "RemindersDB";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private Context mContext;

    public RemindersDB(Context context) {
        mContext = context;
    }

    public void open() throws SQLiteException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
    }

    public Cursor fetchAll() {
        Cursor cur = mDb.query(TABLE_NAME, new String[]{COL_ID, COL_CONTENT, COL_IMPORTANT},
                null, null, null, null, null);
        if (cur != null) {
            cur.moveToFirst();
        }

        return cur;
    }

    public Reminder fetchReminder(int id) {
        Cursor cur = mDb.query(TABLE_NAME, new String[]{COL_ID, COL_CONTENT, COL_IMPORTANT},
                 COL_ID + "=" + id, null, null, null, null);
        if (cur != null) {
            cur.moveToFirst();
            Reminder r = new Reminder(id, cur.getString(cur.getColumnIndex(COL_CONTENT)),
                                        cur.getInt(cur.getColumnIndex(COL_IMPORTANT)));
            cur.close();

            return r;
        }

        return null;
    }

    public void deleteAll() {
        mDb.delete(TABLE_NAME, null, null);
    }

    public int insert(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, reminder.getContent());
        values.put(COL_IMPORTANT, reminder.getImportant());

        return (int)mDb.insert(TABLE_NAME, null, values);
    }

    public int insert(String content, boolean important){
        return insert(new Reminder(0, content, important ? 1 : 0));
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public int update(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, reminder.getContent());
        values.put(COL_IMPORTANT, reminder.getImportant());

        return mDb.update(TABLE_NAME, values,COL_ID + "=" + reminder.getId(), null);
    }

    public int update(int id, String content, boolean important) {
        return update(new Reminder(id, content, important ? 1 : 0));
    }

    public void insertSomeReminders() {
        deleteAll();
        insert("Zwykłe, nudne przypomnienie", false);
        insert("Superważne przypomnienie", true);
    }

    public int delete(long id) {
        return mDb.delete(TABLE_NAME, COL_ID + "=" +id, null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CREATE);

            Log.d(TAG, "DB " + DB_NAME + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.d(TAG, "DB " + DB_NAME +
                    "upgrade from " + oldVersion +
                    " to " + newVersion);
        }
    }
}
