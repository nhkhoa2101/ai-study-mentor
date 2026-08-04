package com.example.aistudymentor.data.remote.models.response;

import com.google.gson.annotations.SerializedName;

public class UsageMetadata {
    @SerializedName("promptTokenCount")
    private long promptTokenCount;

    @SerializedName("candidatesTokenCount")
    private long candidatesTokenCount;

    @SerializedName("totalTokenCount")
    private long totalTokenCount;

    public long getPromptTokenCount() {
        return promptTokenCount;
    }

    public long getCandidatesTokenCount() {
        return candidatesTokenCount;
    }

    public long getTotalTokenCount() {
        return totalTokenCount;
    }
}
