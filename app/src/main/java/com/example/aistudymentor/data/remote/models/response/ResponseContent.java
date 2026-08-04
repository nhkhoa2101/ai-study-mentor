package com.example.aistudymentor.data.remote.models.response;

import java.util.List;

public class ResponseContent {
    private List<ResponsePart> parts;

    public List<ResponsePart> getParts() {
        return parts;
    }

    public void setParts(List<ResponsePart> parts) {
        this.parts = parts;
    }
}
