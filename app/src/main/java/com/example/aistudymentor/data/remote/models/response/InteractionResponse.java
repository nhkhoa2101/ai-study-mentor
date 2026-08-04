package com.example.aistudymentor.data.remote.models.response;

import java.util.List;

public class InteractionResponse {
    private String id;
    private String status;
    private List<InteractionStep> steps;
    private InteractionUsage usage;

    public String getId() { return id; }
    public String getStatus() { return status; }
    public List<InteractionStep> getSteps() { return steps; }
    public InteractionUsage getUsage() { return usage; }
}
