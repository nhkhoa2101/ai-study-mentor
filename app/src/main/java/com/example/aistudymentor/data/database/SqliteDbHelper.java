package com.example.aistudymentor.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqliteDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "studyMentorDB";
    private static final int DB_VERSION = 5;

    public static final String TABLE_USERS = "users";
    public static final String ID_USER = "id";
    public static final String EMAIL_USER = "email";
    public static final String PASSWORD_USER = "password";
    public static final String NAME_USER = "name";
    public static final String LEVEL_USER = "level";
    public static final String XP_USER = "xp";
    public static final String STREAK_USER = "streak";
    public static final String DAILY_GOAL_PROGRESS_USER = "daily_goal_progress";
    public static final String LESSONS_LEARNED_USER = "lessons_learned";
    public static final String STUDY_TIME_USER = "study_time";
    public static final String QUESTIONS_SOLVED_USER = "questions_solved";
    public static final String ACCURACY_USER = "accuracy";
    public static final String EDUCATION_LEVEL_USER = "education_level";
    public static final String EXPLANATION_STYLE_USER = "explanation_style";
    public static final String NOTIFICATIONS_USER = "notifications_enabled";
    public static final String TWO_FACTOR_USER = "two_factor_enabled";

    public static final String TABLE_QUESTIONS = "questions";
    public static final String ID_QUESTION = "id";
    public static final String EMAIL_QUESTION_USER = "user_email";
    public static final String QUESTION_TEXT = "question_text";
    public static final String ANSWER_TEXT = "answer_text";
    public static final String SUBJECT_QUESTION = "subject";
    public static final String DIFFICULTY_QUESTION = "difficulty";
    public static final String BOOKMARKED_QUESTION = "bookmarked";
    public static final String REVIEWED_QUESTION = "reviewed";
    public static final String CREATED_AT_QUESTION = "created_at";

    public static final String TABLE_QUIZ_ATTEMPTS = "quiz_attempts";
    public static final String ID_QUIZ = "id";
    public static final String EMAIL_QUIZ_USER = "user_email";
    public static final String QUESTION_QUIZ = "question";
    public static final String USER_ANSWER_QUIZ = "user_answer";
    public static final String CORRECT_ANSWER_QUIZ = "correct_answer";
    public static final String TYPE_QUIZ = "question_type";
    public static final String CORRECT_QUIZ = "is_correct";
    public static final String CREATED_AT_QUIZ = "created_at";

    // Recent Activities Table
    public static final String TABLE_RECENT_ACTIVITIES = "recent_activities";
    public static final String ID_ACTIVITY = "id";
    public static final String EMAIL_ACTIVITY_USER = "user_email";
    public static final String TITLE_ACTIVITY = "title";
    public static final String DESCRIPTION_ACTIVITY = "description";
    public static final String TIMESTAMP_ACTIVITY = "timestamp";

    // Subject Progress Table
    public static final String TABLE_SUBJECT_PROGRESS = "subject_progress";
    public static final String ID_SUBJECT = "id";
    public static final String EMAIL_SUBJECT_USER = "user_email";
    public static final String NAME_SUBJECT = "subject_name";
    public static final String STATUS_SUBJECT = "status";
    public static final String PROGRESS_SUBJECT = "progress_percent";

    // Study Plan Table
    public static final String TABLE_STUDY_PLAN = "study_plan";
    public static final String ID_PLAN = "id";
    public static final String EMAIL_PLAN_USER = "user_email";
    public static final String TYPE_PLAN = "plan_type"; // SUGGESTION, PLAN_ITEM, CONTINUE
    public static final String TITLE_PLAN = "title";
    public static final String SUBTITLE_PLAN = "subtitle";
    public static final String INFO_PLAN = "info"; // Can be duration or progress
    
    public SqliteDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String usersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EMAIL_USER + " VARCHAR(100) UNIQUE NOT NULL, "
                + PASSWORD_USER + " VARCHAR(200) NOT NULL, "
                + NAME_USER + " VARCHAR(100), "
                + LEVEL_USER + " INTEGER DEFAULT 1, "
                + XP_USER + " INTEGER DEFAULT 0, "
                + STREAK_USER + " INTEGER DEFAULT 0, "
                + DAILY_GOAL_PROGRESS_USER + " INTEGER DEFAULT 0, "
                + LESSONS_LEARNED_USER + " INTEGER DEFAULT 0, "
                + STUDY_TIME_USER + " INTEGER DEFAULT 0, "
                + QUESTIONS_SOLVED_USER + " INTEGER DEFAULT 0, "
                + ACCURACY_USER + " INTEGER DEFAULT 0, "
                + EDUCATION_LEVEL_USER + " TEXT DEFAULT 'University', "
                + EXPLANATION_STYLE_USER + " TEXT DEFAULT 'Step by step', "
                + NOTIFICATIONS_USER + " INTEGER DEFAULT 1, "
                + TWO_FACTOR_USER + " INTEGER DEFAULT 0 )";
        
        db.execSQL(usersTable);

        String activitiesTable = "CREATE TABLE " + TABLE_RECENT_ACTIVITIES + " ("
                + ID_ACTIVITY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EMAIL_ACTIVITY_USER + " VARCHAR(100) NOT NULL, "
                + TITLE_ACTIVITY + " TEXT NOT NULL, "
                + DESCRIPTION_ACTIVITY + " TEXT, "
                + TIMESTAMP_ACTIVITY + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + EMAIL_ACTIVITY_USER + ") REFERENCES " + TABLE_USERS + "(" + EMAIL_USER + ") ON DELETE CASCADE)";
        db.execSQL(activitiesTable);

        String subjectTable = "CREATE TABLE " + TABLE_SUBJECT_PROGRESS + " ("
                + ID_SUBJECT + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EMAIL_SUBJECT_USER + " VARCHAR(100) NOT NULL, "
                + NAME_SUBJECT + " VARCHAR(50) NOT NULL, "
                + STATUS_SUBJECT + " VARCHAR(50), "
                + PROGRESS_SUBJECT + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + EMAIL_SUBJECT_USER + ") REFERENCES " + TABLE_USERS + "(" + EMAIL_USER + ") ON DELETE CASCADE)";
        db.execSQL(subjectTable);

        String planTable = "CREATE TABLE " + TABLE_STUDY_PLAN + " ("
                + ID_PLAN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EMAIL_PLAN_USER + " VARCHAR(100) NOT NULL, "
                + TYPE_PLAN + " VARCHAR(50) NOT NULL, "
                + TITLE_PLAN + " TEXT NOT NULL, "
                + SUBTITLE_PLAN + " TEXT, "
                + INFO_PLAN + " TEXT, "
                + "FOREIGN KEY(" + EMAIL_PLAN_USER + ") REFERENCES " + TABLE_USERS + "(" + EMAIL_USER + ") ON DELETE CASCADE)";
        db.execSQL(planTable);
        createLearningTables(db);
    }

    private void createLearningTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_QUESTIONS + " ("
                + ID_QUESTION + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EMAIL_QUESTION_USER + " TEXT NOT NULL, "
                + QUESTION_TEXT + " TEXT NOT NULL, "
                + ANSWER_TEXT + " TEXT NOT NULL, "
                + SUBJECT_QUESTION + " TEXT DEFAULT 'General', "
                + DIFFICULTY_QUESTION + " TEXT DEFAULT 'Medium', "
                + BOOKMARKED_QUESTION + " INTEGER DEFAULT 0, "
                + REVIEWED_QUESTION + " INTEGER DEFAULT 0, "
                + CREATED_AT_QUESTION + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + EMAIL_QUESTION_USER + ") REFERENCES " + TABLE_USERS + "(" + EMAIL_USER + ") ON DELETE CASCADE)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_questions_user_time ON " + TABLE_QUESTIONS
                + "(" + EMAIL_QUESTION_USER + ", " + CREATED_AT_QUESTION + " DESC)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_QUIZ_ATTEMPTS + " ("
                + ID_QUIZ + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EMAIL_QUIZ_USER + " TEXT NOT NULL, "
                + QUESTION_QUIZ + " TEXT NOT NULL, "
                + USER_ANSWER_QUIZ + " TEXT, "
                + CORRECT_ANSWER_QUIZ + " TEXT NOT NULL, "
                + TYPE_QUIZ + " TEXT NOT NULL, "
                + CORRECT_QUIZ + " INTEGER DEFAULT 0, "
                + CREATED_AT_QUIZ + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + EMAIL_QUIZ_USER + ") REFERENCES " + TABLE_USERS + "(" + EMAIL_USER + ") ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            addColumnIfMissing(db, TABLE_USERS, EDUCATION_LEVEL_USER, "TEXT DEFAULT 'University'");
            addColumnIfMissing(db, TABLE_USERS, EXPLANATION_STYLE_USER, "TEXT DEFAULT 'Step by step'");
            addColumnIfMissing(db, TABLE_USERS, NOTIFICATIONS_USER, "INTEGER DEFAULT 1");
            addColumnIfMissing(db, TABLE_USERS, TWO_FACTOR_USER, "INTEGER DEFAULT 0");
            createLearningTables(db);
        }
    }

    private void addColumnIfMissing(SQLiteDatabase db, String table, String column, String definition) {
        android.database.Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
        boolean exists = false;
        while (cursor.moveToNext()) {
            if (column.equals(cursor.getString(cursor.getColumnIndexOrThrow("name")))) {
                exists = true;
                break;
            }
        }
        cursor.close();
        if (!exists) db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
    }
}
