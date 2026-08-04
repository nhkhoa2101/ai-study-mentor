package com.example.aistudymentor.data.models;

public class User {
    private int id;
    private String email;
    private String password;
    private String name;

    private int level = 1;
    private int xp = 0;
    private int streak = 0;
    private int dailyGoalProgress = 0;
    private int lessonsLearned = 0;
    private int studyTime = 0;
    private int questionsSolved = 0;
    private int accuracy = 0;

    public User() {
    }

    public User(int id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }

    public int getDailyGoalProgress() { return dailyGoalProgress; }
    public void setDailyGoalProgress(int dailyGoalProgress) { this.dailyGoalProgress = dailyGoalProgress; }

    public int getLessonsLearned() { return lessonsLearned; }
    public void setLessonsLearned(int lessonsLearned) { this.lessonsLearned = lessonsLearned; }

    public int getStudyTime() { return studyTime; }
    public void setStudyTime(int studyTime) { this.studyTime = studyTime; }

    public int getQuestionsSolved() { return questionsSolved; }
    public void setQuestionsSolved(int questionsSolved) { this.questionsSolved = questionsSolved; }

    public int getAccuracy() { return accuracy; }
    public void setAccuracy(int accuracy) { this.accuracy = accuracy; }
}
