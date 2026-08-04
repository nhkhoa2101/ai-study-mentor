package com.example.aistudymentor.data.network;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeminiRepository {
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";
    private final GeminiApiService apiService;
    private final String apiKey;

    public GeminiRepository(String apiKey) {
        this.apiKey = apiKey;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(GeminiApiService.class);
    }

    public interface OnExplanationReadyListener {
        void onSuccess(String explanation);
        void onError(String error);
    }

    public void getExplanation(String questionText, OnExplanationReadyListener listener) {
        String prompt = "You are an expert tutor. Please explain step-by-step how to solve this problem: " + questionText;
        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content(Collections.singletonList(part));
        GeminiRequest request = new GeminiRequest(Collections.singletonList(content));

        apiService.generateContent(apiKey, request).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String text = response.body().getCandidates().get(0).getContent().getParts().get(0).getText();
                        listener.onSuccess(text);
                    } catch (Exception e) {
                        listener.onError("Failed to parse response.");
                    }
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                listener.onError("Network failure: " + t.getMessage());
            }
        });
    }
}
