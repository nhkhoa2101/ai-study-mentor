package com.example.aistudymentor.data.remote.models.response;

import com.google.gson.annotations.SerializedName;

public class InteractionUsage {
    @SerializedName("total_input_tokens")
    private long totalInputTokens;

    @SerializedName("total_output_tokens")
    private long totalOutputTokens;

    @SerializedName("total_tokens")
    private long totalTokens;

    public long getTotalInputTokens() { return totalInputTokens; }
    public long getTotalOutputTokens() { return totalOutputTokens; }
    public long getTotalTokens() { return totalTokens; }
}
