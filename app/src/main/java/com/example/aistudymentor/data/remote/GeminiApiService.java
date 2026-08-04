package com.example.aistudymentor.data.remote;

import com.example.aistudymentor.data.remote.models.request.GeminiRequest;
import com.example.aistudymentor.data.remote.models.response.GeminiResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GeminiApiService {
    @POST("v1beta/models/gemini-3.1-flash-lite:generateContent")
    Call<GeminiResponse> generateContent(
            @Header("x-goog-api-key") String apiKey,
            @Body GeminiRequest request
    );
}
