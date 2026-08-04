package com.example.aistudymentor.data.models;

import java.util.List;

public class QuizQuestion {
    private String question;
    private List<String> options;
    private int correctIndex;
    private String explanation;
    private int selectedIndex = -1;

    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
    public String getExplanation() { return explanation; }
    public int getSelectedIndex() { return selectedIndex; }
    public void setSelectedIndex(int selectedIndex) { this.selectedIndex = selectedIndex; }

    public boolean isValid() {
        return question != null && !question.trim().isEmpty()
                && options != null && options.size() >= 2 && options.size() <= 6
                && correctIndex >= 0 && correctIndex < options.size();
    }
}
