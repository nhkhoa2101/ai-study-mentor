package com.example.aistudymentor.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.aistudymentor.R;
import com.example.aistudymentor.ui.viewmodels.SharedViewModel;

public class AnswerFragment extends Fragment {

    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_answer, container, false);
        
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        TextView tvAnswer = view.findViewById(R.id.tvAnswer);
        TextView tvQuestion = view.findViewById(R.id.tvQuestion);

        // Display the text
        tvQuestion.setText("Question attached."); // In a real app we'd display the parsed text or the image thumbnail

        sharedViewModel.getAiResponse().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                tvAnswer.setText(response);
            }
        });

        return view;
    }
}
