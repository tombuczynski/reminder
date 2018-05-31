package com.apress.gerber.reminders;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Tom Buczynski on 22.05.2018.
 */
public class RemindersCursorAdapter extends SimpleCursorAdapter {

    private int mColorImportant, mColorNotImportant;

    public RemindersCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to, 0);

        mColorImportant = context.getResources().getColor(R.color.col_orange, null);
        mColorNotImportant = context.getResources().getColor(R.color.col_green, null);

        SimpleCursorAdapter.ViewBinder vb = new ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                if (!(view instanceof TextView)) {
                    int important = cursor.getInt(columnIndex);
                    view.setBackgroundColor(important != 0 ? mColorImportant : mColorNotImportant);
                    return true;
                }

                return false;
            }
        };

        setViewBinder(vb);
    }
}
