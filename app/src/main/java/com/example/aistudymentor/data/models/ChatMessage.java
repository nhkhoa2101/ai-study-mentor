package com.example.aistudymentor.data.models;

import java.util.List;

public class ChatMessage {
    public static final int TYPE_AI_GREETING = 0;
    public static final int TYPE_USER_TEXT = 1;
    public static final int TYPE_USER_IMAGE = 2;
    public static final int TYPE_AI_LOADING = 3;
    public static final int TYPE_AI_SHORT = 4;
    public static final int TYPE_AI_DETAILED = 5;
    public static final int TYPE_AI_QUIZ = 6;

    private int type;
    private String text;
    private String time;
    private String imageUrl; // For TYPE_USER_IMAGE
    private android.graphics.Bitmap imageBitmap;
    private List<QuizQuestion> quizQuestions;

    public ChatMessage(int type, String text, String time) {
        this.type = type;
        this.text = text;
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public android.graphics.Bitmap getImageBitmap() {
        return imageBitmap;
    }
    
    public void setImageBitmap(android.graphics.Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public List<QuizQuestion> getQuizQuestions() { return quizQuestions; }
    public void setQuizQuestions(List<QuizQuestion> quizQuestions) { this.quizQuestions = quizQuestions; }
}
