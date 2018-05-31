package com.apress.gerber.reminders;

/**
 * Created by Tom Buczynski on 20.04.2018.
 */
public class Reminder {
    private int mId;
    private String mContent;
    private int mImportant;

    public Reminder(int id, String content, int important) {
        mId = id;
        mContent = content;
        mImportant = important;
    }

    public int getId() {
        return mId;
    }

    public String getContent() {
        return mContent;
    }

    public int getImportant() {
        return mImportant;
    }
}
