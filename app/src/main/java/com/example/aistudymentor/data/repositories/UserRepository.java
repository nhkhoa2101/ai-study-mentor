package com.example.aistudymentor.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.aistudymentor.data.database.SqliteDbHelper;
import com.example.aistudymentor.data.models.User;
import com.example.aistudymentor.security.LocalCrypto;
import com.example.aistudymentor.security.PasswordHasher;

public class UserRepository {
    private SqliteDbHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = new SqliteDbHelper(context);
    }

    public boolean registerUser(String email, String password, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.EMAIL_USER, email);
        values.put(SqliteDbHelper.PASSWORD_USER, PasswordHasher.hash(password));
        values.put(SqliteDbHelper.NAME_USER, name);
        
        long result = db.insert(SqliteDbHelper.TABLE_USERS, null, values);
        return result != -1;
    }

    private void insertSubjectProgress(SQLiteDatabase db, String email, String name, String status, int progress) {
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.EMAIL_SUBJECT_USER, email);
        values.put(SqliteDbHelper.NAME_SUBJECT, name);
        values.put(SqliteDbHelper.STATUS_SUBJECT, status);
        values.put(SqliteDbHelper.PROGRESS_SUBJECT, progress);
        db.insert(SqliteDbHelper.TABLE_SUBJECT_PROGRESS, null, values);
    }
    
    public void addSubjectProgress(String email, String name, String status, int progress) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        insertSubjectProgress(db, email, name, status, progress);
    }

    private long insertStudyPlan(SQLiteDatabase db, String email, String type, String title, String subtitle, String info) {
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.EMAIL_PLAN_USER, email);
        values.put(SqliteDbHelper.TYPE_PLAN, type);
        values.put(SqliteDbHelper.TITLE_PLAN, title);
        values.put(SqliteDbHelper.SUBTITLE_PLAN, subtitle);
        values.put(SqliteDbHelper.INFO_PLAN, info);
        return db.insert(SqliteDbHelper.TABLE_STUDY_PLAN, null, values);
    }
    
    public long addStudyPlan(String email, String type, String title, String subtitle, String info) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return insertStudyPlan(db, email, type, title, subtitle, info);
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = SqliteDbHelper.EMAIL_USER + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(SqliteDbHelper.TABLE_USERS, null, selection, selectionArgs, null, null, null);
        
        User user = null;
        if (cursor != null && cursor.moveToFirst()
                && PasswordHasher.verify(password, cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.PASSWORD_USER)))) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.ID_USER)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.EMAIL_USER)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.PASSWORD_USER)));
            int nameIndex = cursor.getColumnIndex(SqliteDbHelper.NAME_USER);
            if(nameIndex != -1 && !cursor.isNull(nameIndex)) {
                user.setName(cursor.getString(nameIndex));
            }
            
            int levelIndex = cursor.getColumnIndex(SqliteDbHelper.LEVEL_USER);
            if(levelIndex != -1 && !cursor.isNull(levelIndex)) user.setLevel(cursor.getInt(levelIndex));
            
            int xpIndex = cursor.getColumnIndex(SqliteDbHelper.XP_USER);
            if(xpIndex != -1 && !cursor.isNull(xpIndex)) user.setXp(cursor.getInt(xpIndex));
            
            int streakIndex = cursor.getColumnIndex(SqliteDbHelper.STREAK_USER);
            if(streakIndex != -1 && !cursor.isNull(streakIndex)) user.setStreak(cursor.getInt(streakIndex));
            
            int dailyGoalIndex = cursor.getColumnIndex(SqliteDbHelper.DAILY_GOAL_PROGRESS_USER);
            if(dailyGoalIndex != -1 && !cursor.isNull(dailyGoalIndex)) user.setDailyGoalProgress(cursor.getInt(dailyGoalIndex));
            
            int lessonsLearnedIndex = cursor.getColumnIndex(SqliteDbHelper.LESSONS_LEARNED_USER);
            if(lessonsLearnedIndex != -1 && !cursor.isNull(lessonsLearnedIndex)) user.setLessonsLearned(cursor.getInt(lessonsLearnedIndex));
            
            int studyTimeIndex = cursor.getColumnIndex(SqliteDbHelper.STUDY_TIME_USER);
            if(studyTimeIndex != -1 && !cursor.isNull(studyTimeIndex)) user.setStudyTime(cursor.getInt(studyTimeIndex));
            
            int questionsSolvedIndex = cursor.getColumnIndex(SqliteDbHelper.QUESTIONS_SOLVED_USER);
            if(questionsSolvedIndex != -1 && !cursor.isNull(questionsSolvedIndex)) user.setQuestionsSolved(cursor.getInt(questionsSolvedIndex));
            
            int accuracyIndex = cursor.getColumnIndex(SqliteDbHelper.ACCURACY_USER);
            if(accuracyIndex != -1 && !cursor.isNull(accuracyIndex)) user.setAccuracy(cursor.getInt(accuracyIndex));
            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.PASSWORD_USER));
            if (!storedPassword.startsWith("pbkdf2$")) {
                ContentValues upgraded = new ContentValues();
                upgraded.put(SqliteDbHelper.PASSWORD_USER, PasswordHasher.hash(password));
                db.update(SqliteDbHelper.TABLE_USERS, upgraded, SqliteDbHelper.EMAIL_USER + " = ?", new String[]{email});
            }
            cursor.close();
        }
        if (cursor != null && !cursor.isClosed()) cursor.close();
        return user;
    }

    public void updateLearningPreferences(String email, String educationLevel, String explanationStyle,
                                          boolean notifications, boolean twoFactor) {
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.EDUCATION_LEVEL_USER, educationLevel);
        values.put(SqliteDbHelper.EXPLANATION_STYLE_USER, explanationStyle);
        values.put(SqliteDbHelper.NOTIFICATIONS_USER, notifications ? 1 : 0);
        values.put(SqliteDbHelper.TWO_FACTOR_USER, twoFactor ? 1 : 0);
        dbHelper.getWritableDatabase().update(SqliteDbHelper.TABLE_USERS, values,
                SqliteDbHelper.EMAIL_USER + " = ?", new String[]{email});
    }

    public String[] getLearningPreferences(String email) {
        Cursor cursor = dbHelper.getReadableDatabase().query(SqliteDbHelper.TABLE_USERS,
                new String[]{SqliteDbHelper.EDUCATION_LEVEL_USER, SqliteDbHelper.EXPLANATION_STYLE_USER,
                        SqliteDbHelper.NOTIFICATIONS_USER, SqliteDbHelper.TWO_FACTOR_USER},
                SqliteDbHelper.EMAIL_USER + " = ?", new String[]{email}, null, null, null);
        String[] result = new String[]{"University", "Step by step", "1", "0"};
        if (cursor.moveToFirst()) {
            for (int i = 0; i < 4; i++) if (!cursor.isNull(i)) result[i] = cursor.getString(i);
        }
        cursor.close();
        return result;
    }

    public void awardForQuestion(String email, String subject) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + SqliteDbHelper.TABLE_USERS + " SET "
                        + SqliteDbHelper.XP_USER + " = " + SqliteDbHelper.XP_USER + " + 10, "
                        + SqliteDbHelper.QUESTIONS_SOLVED_USER + " = " + SqliteDbHelper.QUESTIONS_SOLVED_USER + " + 1, "
                        + SqliteDbHelper.STUDY_TIME_USER + " = " + SqliteDbHelper.STUDY_TIME_USER + " + 3, "
                        + SqliteDbHelper.DAILY_GOAL_PROGRESS_USER + " = MIN(100, " + SqliteDbHelper.DAILY_GOAL_PROGRESS_USER + " + 5), "
                        + SqliteDbHelper.LEVEL_USER + " = 1 + ((" + SqliteDbHelper.XP_USER + " + 10) / 100) WHERE "
                        + SqliteDbHelper.EMAIL_USER + " = ?", new Object[]{email});
        updateDetectedSubjectProgress(db, email, subject);
    }

    private void updateDetectedSubjectProgress(SQLiteDatabase db, String email, String detectedSubject) {
        updateDetectedSubjectProgress(db, email, detectedSubject, 3);
    }

    private void updateDetectedSubjectProgress(SQLiteDatabase db, String email,
                                               String detectedSubject, int increment) {
        String[] aliases = subjectAliases(detectedSubject);
        int matchingId = -1;
        Cursor cursor = db.query(SqliteDbHelper.TABLE_SUBJECT_PROGRESS,
                new String[]{SqliteDbHelper.ID_SUBJECT, SqliteDbHelper.NAME_SUBJECT},
                SqliteDbHelper.EMAIL_SUBJECT_USER + " = ?", new String[]{email},
                null, null, null);
        while (cursor.moveToNext()) {
            String existing = normalizeSubject(cursor.getString(1));
            for (String alias : aliases) {
                if (existing.equals(normalizeSubject(alias))) {
                    matchingId = cursor.getInt(0);
                    break;
                }
            }
            if (matchingId != -1) break;
        }
        cursor.close();

        if (matchingId == -1) {
            int initial = Math.min(100, Math.max(0, increment));
            insertSubjectProgress(db, email, displaySubject(detectedSubject),
                    initial >= 100 ? "Hoàn thành" : "Đang học", initial);
        } else {
            db.execSQL("UPDATE " + SqliteDbHelper.TABLE_SUBJECT_PROGRESS + " SET "
                            + SqliteDbHelper.PROGRESS_SUBJECT + " = MIN(100, "
                            + SqliteDbHelper.PROGRESS_SUBJECT + " + ?), "
                            + SqliteDbHelper.STATUS_SUBJECT + " = CASE WHEN "
                            + SqliteDbHelper.PROGRESS_SUBJECT + " + ? >= 100 THEN 'Hoàn thành' "
                            + "ELSE 'Đang học' END WHERE "
                            + SqliteDbHelper.ID_SUBJECT + " = ?",
                    new Object[]{increment, increment, matchingId});
        }
    }

    private String[] subjectAliases(String subject) {
        switch (subject) {
            case "Mathematics": return new String[]{"Mathematics", "Math", "Toán"};
            case "Physics": return new String[]{"Physics", "Vật lý", "Lý"};
            case "Chemistry": return new String[]{"Chemistry", "Hóa học", "Hóa"};
            case "Biology": return new String[]{"Biology", "Sinh học", "Sinh"};
            case "Computer Science": return new String[]{"Computer Science", "Tin học", "Lập trình"};
            case "English": return new String[]{"English", "Tiếng Anh", "Anh"};
            case "Literature": return new String[]{"Literature", "Ngữ văn", "Văn"};
            case "History": return new String[]{"History", "Lịch sử", "Sử"};
            case "Geography": return new String[]{"Geography", "Địa lý", "Địa"};
            default: return new String[]{"General", "Khác", "Other"};
        }
    }

    private String displaySubject(String subject) {
        switch (subject) {
            case "Mathematics": return "Toán";
            case "Physics": return "Lý";
            case "Chemistry": return "Hóa";
            case "Biology": return "Sinh";
            case "Computer Science": return "Tin học";
            case "English": return "Anh";
            case "Literature": return "Văn";
            case "History": return "Sử";
            case "Geography": return "Địa";
            default: return "Khác";
        }
    }

    private String normalizeSubject(String value) {
        if (value == null) return "";
        return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(java.util.Locale.ROOT).trim();
    }

    public void awardForReview(String email, boolean correctQuiz) {
        int xp = correctQuiz ? 15 : 5;
        dbHelper.getWritableDatabase().execSQL("UPDATE " + SqliteDbHelper.TABLE_USERS + " SET "
                        + SqliteDbHelper.XP_USER + " = " + SqliteDbHelper.XP_USER + " + ?, "
                        + SqliteDbHelper.LESSONS_LEARNED_USER + " = " + SqliteDbHelper.LESSONS_LEARNED_USER + " + 1, "
                        + SqliteDbHelper.STUDY_TIME_USER + " = " + SqliteDbHelper.STUDY_TIME_USER + " + 2, "
                        + SqliteDbHelper.LEVEL_USER + " = 1 + ((" + SqliteDbHelper.XP_USER + " + ?) / 100) WHERE "
                        + SqliteDbHelper.EMAIL_USER + " = ?", new Object[]{xp, xp, email});
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = SqliteDbHelper.EMAIL_USER + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(SqliteDbHelper.TABLE_USERS, null, selection, selectionArgs, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        
        return exists;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = SqliteDbHelper.EMAIL_USER + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(SqliteDbHelper.TABLE_USERS, null, selection, selectionArgs, null, null, null);
        
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.ID_USER)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.EMAIL_USER)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.PASSWORD_USER)));
            
            int nameIndex = cursor.getColumnIndex(SqliteDbHelper.NAME_USER);
            if(nameIndex != -1 && !cursor.isNull(nameIndex)) user.setName(cursor.getString(nameIndex));
            
            int levelIndex = cursor.getColumnIndex(SqliteDbHelper.LEVEL_USER);
            if(levelIndex != -1 && !cursor.isNull(levelIndex)) user.setLevel(cursor.getInt(levelIndex));
            
            int xpIndex = cursor.getColumnIndex(SqliteDbHelper.XP_USER);
            if(xpIndex != -1 && !cursor.isNull(xpIndex)) user.setXp(cursor.getInt(xpIndex));
            
            int streakIndex = cursor.getColumnIndex(SqliteDbHelper.STREAK_USER);
            if(streakIndex != -1 && !cursor.isNull(streakIndex)) user.setStreak(cursor.getInt(streakIndex));
            
            int dailyGoalIndex = cursor.getColumnIndex(SqliteDbHelper.DAILY_GOAL_PROGRESS_USER);
            if(dailyGoalIndex != -1 && !cursor.isNull(dailyGoalIndex)) user.setDailyGoalProgress(cursor.getInt(dailyGoalIndex));
            
            int lessonsLearnedIndex = cursor.getColumnIndex(SqliteDbHelper.LESSONS_LEARNED_USER);
            if(lessonsLearnedIndex != -1 && !cursor.isNull(lessonsLearnedIndex)) user.setLessonsLearned(cursor.getInt(lessonsLearnedIndex));
            
            int studyTimeIndex = cursor.getColumnIndex(SqliteDbHelper.STUDY_TIME_USER);
            if(studyTimeIndex != -1 && !cursor.isNull(studyTimeIndex)) user.setStudyTime(cursor.getInt(studyTimeIndex));
            
            int questionsSolvedIndex = cursor.getColumnIndex(SqliteDbHelper.QUESTIONS_SOLVED_USER);
            if(questionsSolvedIndex != -1 && !cursor.isNull(questionsSolvedIndex)) user.setQuestionsSolved(cursor.getInt(questionsSolvedIndex));
            
            int accuracyIndex = cursor.getColumnIndex(SqliteDbHelper.ACCURACY_USER);
            if(accuracyIndex != -1 && !cursor.isNull(accuracyIndex)) user.setAccuracy(cursor.getInt(accuracyIndex));
            
            
            cursor.close();
        }
        return user;
    }

    public java.util.List<User> getTopUsers(int limit) {
        java.util.List<User> users = new java.util.ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String orderBy = SqliteDbHelper.XP_USER + " DESC";
        Cursor cursor = db.query(SqliteDbHelper.TABLE_USERS, null, null, null, null, null, orderBy, String.valueOf(limit));
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.ID_USER)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.EMAIL_USER)));
                
                int nameIndex = cursor.getColumnIndex(SqliteDbHelper.NAME_USER);
                if(nameIndex != -1 && !cursor.isNull(nameIndex)) user.setName(cursor.getString(nameIndex));
                
                int levelIndex = cursor.getColumnIndex(SqliteDbHelper.LEVEL_USER);
                if(levelIndex != -1 && !cursor.isNull(levelIndex)) user.setLevel(cursor.getInt(levelIndex));
                
                int xpIndex = cursor.getColumnIndex(SqliteDbHelper.XP_USER);
                if(xpIndex != -1 && !cursor.isNull(xpIndex)) user.setXp(cursor.getInt(xpIndex));
                
                users.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return users;
    }

    public static class SubjectProgress {
        public String name;
        public String status;
        public int progress;
    }

    public java.util.List<SubjectProgress> getSubjectProgress(String email) {
        java.util.List<SubjectProgress> list = new java.util.ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = SqliteDbHelper.EMAIL_SUBJECT_USER + " = ?";
        String[] args = {email};
        Cursor cursor = db.query(SqliteDbHelper.TABLE_SUBJECT_PROGRESS, null, selection, args, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                SubjectProgress sp = new SubjectProgress();
                sp.name = cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.NAME_SUBJECT));
                sp.status = cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.STATUS_SUBJECT));
                sp.progress = cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.PROGRESS_SUBJECT));
                list.add(sp);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public static class StudyPlan {
        public int id;
        public String type;
        public String title;
        public String subtitle;
        public String info;
        public int quizTotal;
        public int quizAnswered;

        @Override public String toString() {
            return title + (info == null || info.isEmpty() ? "" : " • " + info);
        }
    }

    public static class PlanQuizQuestion {
        public long id;
        public int planId;
        public String question;
        public String correctAnswer;
        public String userAnswer;
        public int position;
        public boolean answered;
        public boolean correct;

        public boolean isValid() {
            return question != null && !question.trim().isEmpty()
                    && correctAnswer != null && !correctAnswer.trim().isEmpty();
        }
    }

    public java.util.List<StudyPlan> getStudyPlan(String email, String type) {
        java.util.List<StudyPlan> list = new java.util.ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = SqliteDbHelper.EMAIL_PLAN_USER + " = ? AND " + SqliteDbHelper.TYPE_PLAN + " = ?";
        String[] args = {email, type};
        Cursor cursor = db.query(SqliteDbHelper.TABLE_STUDY_PLAN, null, selection, args, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                StudyPlan sp = new StudyPlan();
                sp.id = cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.ID_PLAN));
                sp.type = cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.TYPE_PLAN));
                sp.title = cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.TITLE_PLAN));
                
                int subIndex = cursor.getColumnIndex(SqliteDbHelper.SUBTITLE_PLAN);
                if (subIndex != -1 && !cursor.isNull(subIndex)) sp.subtitle = cursor.getString(subIndex);
                
                int infoIndex = cursor.getColumnIndex(SqliteDbHelper.INFO_PLAN);
                if (infoIndex != -1 && !cursor.isNull(infoIndex)) sp.info = cursor.getString(infoIndex);

                int[] quizProgress = getPlanQuizProgress(sp.id, email);
                sp.quizTotal = quizProgress[0];
                sp.quizAnswered = quizProgress[1];
                
                list.add(sp);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public void updateStudyPlanType(int id, String type) {
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.TYPE_PLAN, type);
        dbHelper.getWritableDatabase().update(SqliteDbHelper.TABLE_STUDY_PLAN, values,
                SqliteDbHelper.ID_PLAN + " = ?", new String[]{String.valueOf(id)});
    }

    public boolean completeStudyPlanIfQuizFinished(int id, String email) {
        int[] progress = getPlanQuizProgress(id, email);
        if (progress[0] == 0 || progress[1] < progress[0]) return false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Cursor cursor = db.query(SqliteDbHelper.TABLE_STUDY_PLAN,
                    new String[]{SqliteDbHelper.SUBTITLE_PLAN, SqliteDbHelper.INFO_PLAN},
                    SqliteDbHelper.ID_PLAN + " = ? AND " + SqliteDbHelper.EMAIL_PLAN_USER
                            + " = ? AND " + SqliteDbHelper.TYPE_PLAN + " = 'PLAN_ITEM'",
                    new String[]{String.valueOf(id), email}, null, null, null);
            if (!cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
            String subject = cursor.isNull(0) ? "General" : cursor.getString(0);
            String duration = cursor.isNull(1) ? "20 phút" : cursor.getString(1);
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(SqliteDbHelper.TYPE_PLAN, "COMPLETED");
            int rows = db.update(SqliteDbHelper.TABLE_STUDY_PLAN, values,
                    SqliteDbHelper.ID_PLAN + " = ? AND " + SqliteDbHelper.EMAIL_PLAN_USER
                            + " = ? AND " + SqliteDbHelper.TYPE_PLAN + " = 'PLAN_ITEM'",
                    new String[]{String.valueOf(id), email});
            if (rows == 0) return false;

            int minutes = durationMinutes(duration);
            int percentIncrease = Math.min(100, Math.max(1, Math.round(minutes * 100f / 60f)));
            db.execSQL("UPDATE " + SqliteDbHelper.TABLE_USERS + " SET "
                            + SqliteDbHelper.XP_USER + " = " + SqliteDbHelper.XP_USER + " + 15, "
                            + SqliteDbHelper.LESSONS_LEARNED_USER + " = "
                            + SqliteDbHelper.LESSONS_LEARNED_USER + " + 1, "
                            + SqliteDbHelper.STUDY_TIME_USER + " = "
                            + SqliteDbHelper.STUDY_TIME_USER + " + ?, "
                            + SqliteDbHelper.DAILY_GOAL_PROGRESS_USER + " = MIN(100, "
                            + SqliteDbHelper.DAILY_GOAL_PROGRESS_USER + " + ?), "
                            + SqliteDbHelper.LEVEL_USER + " = 1 + (("
                            + SqliteDbHelper.XP_USER + " + 15) / 100) WHERE "
                            + SqliteDbHelper.EMAIL_USER + " = ?",
                    new Object[]{minutes, percentIncrease, email});
            updateDetectedSubjectProgress(db, email, subject, percentIncrease);
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    private int durationMinutes(String value) {
        if (value == null) return 20;
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)").matcher(value);
        return matcher.find() ? Math.max(1, Integer.parseInt(matcher.group(1))) : 20;
    }

    public void replacePlanQuizQuestions(int planId, String email,
                                         java.util.List<PlanQuizQuestion> questions) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(SqliteDbHelper.TABLE_PLAN_QUIZ,
                    SqliteDbHelper.PLAN_ID_QUIZ + " = ? AND " + SqliteDbHelper.EMAIL_PLAN_QUIZ_USER + " = ?",
                    new String[]{String.valueOf(planId), email});
            int position = 0;
            for (PlanQuizQuestion question : questions) {
                if (question == null || !question.isValid()) continue;
                ContentValues values = new ContentValues();
                values.put(SqliteDbHelper.PLAN_ID_QUIZ, planId);
                values.put(SqliteDbHelper.EMAIL_PLAN_QUIZ_USER, email);
                values.put(SqliteDbHelper.PLAN_QUIZ_QUESTION, LocalCrypto.encrypt(question.question));
                values.put(SqliteDbHelper.PLAN_QUIZ_ANSWER, LocalCrypto.encrypt(question.correctAnswer));
                values.put(SqliteDbHelper.PLAN_QUIZ_POSITION, position++);
                db.insertOrThrow(SqliteDbHelper.TABLE_PLAN_QUIZ, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public java.util.List<PlanQuizQuestion> getPlanQuizQuestions(int planId, String email) {
        java.util.List<PlanQuizQuestion> result = new java.util.ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().query(SqliteDbHelper.TABLE_PLAN_QUIZ, null,
                SqliteDbHelper.PLAN_ID_QUIZ + " = ? AND " + SqliteDbHelper.EMAIL_PLAN_QUIZ_USER + " = ?",
                new String[]{String.valueOf(planId), email}, null, null,
                SqliteDbHelper.PLAN_QUIZ_POSITION + " ASC");
        while (cursor.moveToNext()) {
            PlanQuizQuestion item = new PlanQuizQuestion();
            item.id = cursor.getLong(cursor.getColumnIndexOrThrow(SqliteDbHelper.ID_PLAN_QUIZ));
            item.planId = planId;
            item.question = LocalCrypto.decrypt(cursor.getString(
                    cursor.getColumnIndexOrThrow(SqliteDbHelper.PLAN_QUIZ_QUESTION)));
            item.correctAnswer = LocalCrypto.decrypt(cursor.getString(
                    cursor.getColumnIndexOrThrow(SqliteDbHelper.PLAN_QUIZ_ANSWER)));
            int userAnswerIndex = cursor.getColumnIndex(SqliteDbHelper.PLAN_QUIZ_USER_ANSWER);
            if (userAnswerIndex >= 0 && !cursor.isNull(userAnswerIndex)) {
                item.userAnswer = LocalCrypto.decrypt(cursor.getString(userAnswerIndex));
            }
            item.position = cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.PLAN_QUIZ_POSITION));
            item.answered = cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.PLAN_QUIZ_ANSWERED)) == 1;
            item.correct = cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.PLAN_QUIZ_CORRECT)) == 1;
            result.add(item);
        }
        cursor.close();
        return result;
    }

    public void savePlanQuizAnswer(long questionId, String email, String userAnswer, boolean correct) {
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.PLAN_QUIZ_USER_ANSWER, LocalCrypto.encrypt(userAnswer));
        values.put(SqliteDbHelper.PLAN_QUIZ_ANSWERED, 1);
        values.put(SqliteDbHelper.PLAN_QUIZ_CORRECT, correct ? 1 : 0);
        dbHelper.getWritableDatabase().update(SqliteDbHelper.TABLE_PLAN_QUIZ, values,
                SqliteDbHelper.ID_PLAN_QUIZ + " = ? AND " + SqliteDbHelper.EMAIL_PLAN_QUIZ_USER + " = ?",
                new String[]{String.valueOf(questionId), email});
    }

    public int[] getPlanQuizProgress(int planId, String email) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT COUNT(*), COALESCE(SUM("
                        + SqliteDbHelper.PLAN_QUIZ_ANSWERED + "),0) FROM " + SqliteDbHelper.TABLE_PLAN_QUIZ
                        + " WHERE " + SqliteDbHelper.PLAN_ID_QUIZ + " = ? AND "
                        + SqliteDbHelper.EMAIL_PLAN_QUIZ_USER + " = ?",
                new String[]{String.valueOf(planId), email});
        int total = 0;
        int answered = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
            answered = cursor.getInt(1);
        }
        cursor.close();
        return new int[]{total, answered};
    }

    public void deleteStudyPlan(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(SqliteDbHelper.TABLE_PLAN_QUIZ,
                SqliteDbHelper.PLAN_ID_QUIZ + " = ?", new String[]{String.valueOf(id)});
        db.delete(SqliteDbHelper.TABLE_STUDY_PLAN,
                SqliteDbHelper.ID_PLAN + " = ?", new String[]{String.valueOf(id)});
    }
}
