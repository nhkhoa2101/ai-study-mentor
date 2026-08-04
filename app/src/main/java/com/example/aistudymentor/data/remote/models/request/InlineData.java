package com.example.aistudymentor.data.remote.models.request;

import com.google.gson.annotations.SerializedName;

public class InlineData {
    @SerializedName("mime_type")
    private String mimeType;
    private String data; // Base64 encoded string

    public InlineData(String mimeType, String data) {
        this.mimeType = mimeType;
        this.data = data;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
