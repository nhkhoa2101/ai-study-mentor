package com.example.aistudymentor.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.aistudymentor.MainActivity;
import com.example.aistudymentor.R;

public class ReviewReminderReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "study_review";
    @Override public void onReceive(Context context, Intent intent) {
        NotificationManager manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID,"Study review reminders",NotificationManager.IMPORTANCE_DEFAULT));
        PendingIntent open=PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        manager.notify(1001,new NotificationCompat.Builder(context,CHANNEL_ID).setSmallIcon(R.drawable.ic_ai_text).setContentTitle("Time to review").setContentText("Review your bookmarked answers and repeated topics to keep your streak.").setContentIntent(open).setAutoCancel(true).build());
    }
}
