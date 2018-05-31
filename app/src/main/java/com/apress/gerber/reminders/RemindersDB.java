package com.apress.gerber.reminders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Tom Buczynski on 20.04.2018.
 */
public class RemindersDB {
    private static final String DB_NAME = "reminders.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "reminders";
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANT = "important";
    public static final int COLIDX_ID = 0;
    public static final int COLIDX_CONTENT = 1;
    public static final int COLIDX_IMPORTANT = 2;

    private static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            " ( " +  COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
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

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CREATE);

            ContentValues values1 = new ContentValues();
            values1.put(COL_CONTENT, "Zwykłe przypomnienie");
            values1.put(COL_IMPORTANT, 0);
            db.insert(TABLE_NAME, null, values1);

            ContentValues values2 = new ContentValues();
            values2.put(COL_CONTENT, "Ważne przypomnienie");
            values2.put(COL_IMPORTANT, 1);
            db.insert(TABLE_NAME, null, values2);

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
