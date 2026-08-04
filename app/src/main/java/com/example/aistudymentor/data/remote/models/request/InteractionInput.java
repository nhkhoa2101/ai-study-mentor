package com.example.aistudymentor.data.remote.models.request;

import com.google.gson.annotations.SerializedName;

public class InteractionInput {
    private String type;
    
    // For text
    private String text;

    // For image
    @SerializedName("mime_type")
    private String mimeType;
    private String data;

    // Constructor for text
    public InteractionInput(String text) {
        this.type = "text";
        this.text = text;
    }

    // Constructor for image
    public InteractionInput(String mimeType, String data) {
        this.type = "image";
        this.mimeType = mimeType;
        this.data = data;
    }

    public String getType() { return type; }
    public String getText() { return text; }
    public String getMimeType() { return mimeType; }
    public String getData() { return data; }
}
