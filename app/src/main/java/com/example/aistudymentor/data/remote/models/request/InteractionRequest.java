package com.example.aistudymentor.data.remote.models.request;

import java.util.List;

public class InteractionRequest {
    private String model;
    private List<InteractionInput> input;

    public InteractionRequest(String model, List<InteractionInput> input) {
        this.model = model;
        this.input = input;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<InteractionInput> getInput() {
        return input;
    }

    public void setInput(List<InteractionInput> input) {
        this.input = input;
    }
}
