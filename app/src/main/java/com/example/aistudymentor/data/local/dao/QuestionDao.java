package com.example.aistudymentor.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.aistudymentor.data.local.entity.Question;

import java.util.List;

@Dao
public interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY timestamp DESC")
    LiveData<List<Question>> getAllQuestions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestion(Question question);
}
