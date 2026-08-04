package com.example.aistudymentor.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.aistudymentor.data.database.SqliteDbHelper;
import com.example.aistudymentor.data.models.QuestionRecord;
import com.example.aistudymentor.security.LocalCrypto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.Normalizer;

public class StudyRepository {
    private final SqliteDbHelper helper;

    public StudyRepository(Context context) {
        helper = new SqliteDbHelper(context.getApplicationContext());
    }

    public long saveQuestion(String email, String question, String answer) {
        return saveQuestion(email, question, answer, detectSubject(question));
    }

    public long saveQuestion(String email, String question, String answer, String subject) {
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.EMAIL_QUESTION_USER, email);
        values.put(SqliteDbHelper.QUESTION_TEXT, LocalCrypto.encrypt(question));
        values.put(SqliteDbHelper.ANSWER_TEXT, LocalCrypto.encrypt(answer));
        values.put(SqliteDbHelper.SUBJECT_QUESTION, subject);
        values.put(SqliteDbHelper.DIFFICULTY_QUESTION, detectDifficulty(question));
        values.put(SqliteDbHelper.CREATED_AT_QUESTION, System.currentTimeMillis());
        return helper.getWritableDatabase().insert(SqliteDbHelper.TABLE_QUESTIONS, null, values);
    }

    public QuestionRecord findCachedAnswer(String email, String question) {
        String normalized = normalize(question);
        for (QuestionRecord record : search(email, question, false)) {
            if (normalize(record.question).equals(normalized)) return record;
        }
        return null;
    }

    public List<QuestionRecord> search(String email, String keyword, boolean bookmarksOnly) {
        List<QuestionRecord> result = new ArrayList<>();
        StringBuilder where = new StringBuilder(SqliteDbHelper.EMAIL_QUESTION_USER + " = ?");
        List<String> args = new ArrayList<>();
        args.add(email);
        if (bookmarksOnly) where.append(" AND ").append(SqliteDbHelper.BOOKMARKED_QUESTION).append(" = 1");
        Cursor cursor = helper.getReadableDatabase().query(SqliteDbHelper.TABLE_QUESTIONS, null,
                where.toString(), args.toArray(new String[0]), null, null,
                SqliteDbHelper.CREATED_AT_QUESTION + " DESC", "100");
        String filter = normalize(keyword);
        while (cursor.moveToNext()) {
            QuestionRecord record = fromCursor(cursor);
            if (filter.isEmpty() || normalize(record.question).contains(filter) || normalize(record.answer).contains(filter)
                    || normalize(record.subject).contains(filter)) result.add(record);
        }
        cursor.close();
        return result;
    }

    public void setBookmarked(long id, boolean bookmarked) {
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.BOOKMARKED_QUESTION, bookmarked ? 1 : 0);
        helper.getWritableDatabase().update(SqliteDbHelper.TABLE_QUESTIONS, values,
                SqliteDbHelper.ID_QUESTION + " = ?", new String[]{String.valueOf(id)});
    }

    public void markReviewed(long id) {
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.REVIEWED_QUESTION, 1);
        helper.getWritableDatabase().update(SqliteDbHelper.TABLE_QUESTIONS, values,
                SqliteDbHelper.ID_QUESTION + " = ?", new String[]{String.valueOf(id)});
    }

    public void saveQuizAttempt(String email, String question, String userAnswer,
                                String correctAnswer, String type, boolean correct) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.EMAIL_QUIZ_USER, email);
        values.put(SqliteDbHelper.QUESTION_QUIZ, LocalCrypto.encrypt(question));
        values.put(SqliteDbHelper.USER_ANSWER_QUIZ, LocalCrypto.encrypt(userAnswer));
        values.put(SqliteDbHelper.CORRECT_ANSWER_QUIZ, LocalCrypto.encrypt(correctAnswer));
        values.put(SqliteDbHelper.TYPE_QUIZ, type);
        values.put(SqliteDbHelper.CORRECT_QUIZ, correct ? 1 : 0);
        values.put(SqliteDbHelper.CREATED_AT_QUIZ, System.currentTimeMillis());
        db.insert(SqliteDbHelper.TABLE_QUIZ_ATTEMPTS, null, values);
        updateQuizAccuracy(db, email);
    }

    public String getMostFrequentSubject(String email) {
        Cursor cursor = helper.getReadableDatabase().rawQuery("SELECT " + SqliteDbHelper.SUBJECT_QUESTION
                + ", COUNT(*) total FROM " + SqliteDbHelper.TABLE_QUESTIONS + " WHERE "
                + SqliteDbHelper.EMAIL_QUESTION_USER + " = ? GROUP BY " + SqliteDbHelper.SUBJECT_QUESTION
                + " ORDER BY total DESC LIMIT 1", new String[]{email});
        String value = cursor.moveToFirst() ? cursor.getString(0) : "General";
        cursor.close();
        return value;
    }

    private void updateQuizAccuracy(SQLiteDatabase db, String email) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*), COALESCE(SUM(" + SqliteDbHelper.CORRECT_QUIZ
                + "),0) FROM " + SqliteDbHelper.TABLE_QUIZ_ATTEMPTS + " WHERE "
                + SqliteDbHelper.EMAIL_QUIZ_USER + " = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            int total = cursor.getInt(0);
            int accuracy = total == 0 ? 0 : Math.round(cursor.getInt(1) * 100f / total);
            ContentValues values = new ContentValues();
            values.put(SqliteDbHelper.ACCURACY_USER, accuracy);
            db.update(SqliteDbHelper.TABLE_USERS, values, SqliteDbHelper.EMAIL_USER + " = ?", new String[]{email});
        }
        cursor.close();
    }

    private QuestionRecord fromCursor(Cursor cursor) {
        QuestionRecord value = new QuestionRecord();
        value.id = cursor.getLong(cursor.getColumnIndexOrThrow(SqliteDbHelper.ID_QUESTION));
        value.question = LocalCrypto.decrypt(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.QUESTION_TEXT)));
        value.answer = LocalCrypto.decrypt(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.ANSWER_TEXT)));
        value.subject = cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.SUBJECT_QUESTION));
        value.difficulty = cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.DIFFICULTY_QUESTION));
        value.bookmarked = cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.BOOKMARKED_QUESTION)) == 1;
        value.reviewed = cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.REVIEWED_QUESTION)) == 1;
        value.createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(SqliteDbHelper.CREATED_AT_QUESTION));
        return value;
    }

    public static String detectSubject(String text) {
        String value = normalize(text);
        if (containsAny(value, "equation", "calculate", "math", "algebra", "geometry", "calculus",
                "toan", "phuong trinh", "dao ham", "tich phan", "hinh hoc", "xac suat")) return "Mathematics";
        if (containsAny(value, "physics", "force", "energy", "velocity", "newton", "vat ly",
                "van toc", "gia toc", "dien truong", "dong dien")) return "Physics";
        if (containsAny(value, "chemistry", "reaction", "molecule", "atom", "hoa hoc", "phan ung",
                "nguyen tu", "phan tu", "hoa tri")) return "Chemistry";
        if (containsAny(value, "biology", "cell", "dna", "photosynthesis", "sinh hoc", "te bao",
                "quang hop", "di truyen", "he sinh thai")) return "Biology";
        if (containsAny(value, "code", "java", "android", "algorithm", "programming", "database",
                "lap trinh", "thuat toan", "co so du lieu", "phan mem")) return "Computer Science";
        if (containsAny(value, "english", "grammar", "vocabulary", "tieng anh", "ngu phap", "tu vung")) return "English";
        if (containsAny(value, "literature", "poem", "novel", "van hoc", "bai tho", "tac pham", "nhan vat")) return "Literature";
        if (containsAny(value, "history", "historical", "lich su", "chien tranh", "trieu dai")) return "History";
        if (containsAny(value, "geography", "climate", "population", "dia ly", "khi hau", "dan so")) return "Geography";
        return "General";
    }

    public static String detectDifficulty(String text) {
        if (text == null) return "Medium";
        if (text.length() > 220 || containsAny(normalize(text), "prove", "derive", "analyze", "chung minh")) return "Advanced";
        if (text.length() < 70) return "Basic";
        return "Medium";
    }

    private static boolean containsAny(String value, String... terms) {
        for (String term : terms) if (value.contains(term)) return true;
        return false;
    }

    private static String normalize(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
