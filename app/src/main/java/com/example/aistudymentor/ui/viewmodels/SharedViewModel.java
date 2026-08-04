package com.example.aistudymentor.ui.viewmodels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Bitmap> capturedImage = new MutableLiveData<>();
    private final MutableLiveData<String> aiResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public void setCapturedImage(Bitmap bitmap) {
        capturedImage.setValue(bitmap);
    }

    public LiveData<Bitmap> getCapturedImage() {
        return capturedImage;
    }

    public void setAiResponse(String response) {
        aiResponse.setValue(response);
    }

    public LiveData<String> getAiResponse() {
        return aiResponse;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Mock processing for now, since we don't have API key
    public void processImageWithAI(Bitmap bitmap) {
        setLoading(true);
        // Simulate network delay
        new android.os.Handler().postDelayed(() -> {
            setAiResponse("This is a simulated AI response. Since no API key is provided yet, the system returns this mock explanation. \n\n1. Analyze the problem.\n2. Apply the correct formula.\n3. Solve it step-by-step.");
            setLoading(false);
        }, 2000);
    }
}
