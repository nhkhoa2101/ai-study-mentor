package com.example.aistudymentor.data.remote.models.response;

import java.util.List;

public class InteractionStep {
    private String type;
    private List<InteractionOutputContent> content;

    public String getType() { return type; }
    public List<InteractionOutputContent> getContent() { return content; }
}
