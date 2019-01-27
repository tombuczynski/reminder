package com.apress.gerber.reminders;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String REMINDER_CONTENT = "REMINDER_CONTENT";

    @Override
    public void onReceive(Context context, Intent intent) {
        String reminderContent = intent.getStringExtra(REMINDER_CONTENT);

        Intent actionIntent = new Intent(context, RemindersActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, actionIntent, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker("Reminder !")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(reminderContent)
                .setWhen(new Date().getTime())
                .setContentIntent(pi)
                .build();

        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);
    }
}
