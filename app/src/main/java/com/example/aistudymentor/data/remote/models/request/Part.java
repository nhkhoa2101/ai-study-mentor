package com.example.aistudymentor.data.remote.models.request;

import com.google.gson.annotations.SerializedName;

public class Part {
    private String text;
    
    @SerializedName("inline_data")
    private InlineData inlineData;

    // For text parts
    public Part(String text) {
        this.text = text;
    }

    // For image parts
    public Part(InlineData inlineData) {
        this.inlineData = inlineData;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public InlineData getInlineData() {
        return inlineData;
    }

    public void setInlineData(InlineData inlineData) {
        this.inlineData = inlineData;
    }
}
