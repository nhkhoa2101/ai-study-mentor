package com.example.aistudymentor.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.aistudymentor.data.database.SqliteDbHelper;
import com.example.aistudymentor.data.models.RecentActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityRepository {
    private SqliteDbHelper dbHelper;

    public ActivityRepository(Context context) {
        dbHelper = new SqliteDbHelper(context);
    }

    public long addActivity(String email, String title, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.EMAIL_ACTIVITY_USER, email);
        values.put(SqliteDbHelper.TITLE_ACTIVITY, title);
        values.put(SqliteDbHelper.DESCRIPTION_ACTIVITY, description);
        
        long result = db.insert(SqliteDbHelper.TABLE_RECENT_ACTIVITIES, null, values);
        db.close();
        return result;
    }

    public List<RecentActivity> getRecentActivitiesByEmail(String email) {
        List<RecentActivity> activities = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String selection = SqliteDbHelper.EMAIL_ACTIVITY_USER + " = ?";
        String[] selectionArgs = { email };
        
        // Get the 5 most recent activities by ordering by ID descending
        Cursor cursor = db.query(
                SqliteDbHelper.TABLE_RECENT_ACTIVITIES,
                null,
                selection,
                selectionArgs,
                null,
                null,
                SqliteDbHelper.ID_ACTIVITY + " DESC",
                "5"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                RecentActivity activity = new RecentActivity();
                int indexId = cursor.getColumnIndex(SqliteDbHelper.ID_ACTIVITY);
                int indexEmail = cursor.getColumnIndex(SqliteDbHelper.EMAIL_ACTIVITY_USER);
                int indexTitle = cursor.getColumnIndex(SqliteDbHelper.TITLE_ACTIVITY);
                int indexDescription = cursor.getColumnIndex(SqliteDbHelper.DESCRIPTION_ACTIVITY);
                int indexTimestamp = cursor.getColumnIndex(SqliteDbHelper.TIMESTAMP_ACTIVITY);

                if(indexId != -1) activity.setId(cursor.getInt(indexId));
                if(indexEmail != -1) activity.setUserEmail(cursor.getString(indexEmail));
                if(indexTitle != -1) activity.setTitle(cursor.getString(indexTitle));
                if(indexDescription != -1) activity.setDescription(cursor.getString(indexDescription));
                if(indexTimestamp != -1) activity.setTimestamp(cursor.getString(indexTimestamp));

                activities.add(activity);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return activities;
    }
}
