package com.example.aistudymentor.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.EditText;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

import com.example.aistudymentor.R;

public class OnboardingSetupFragment extends Fragment {
    
    private Set<String> selectedSubjects = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_setup, container, false);
        
        int[] checkboxIds = {R.id.cbMath, R.id.cbPhysics, R.id.cbChemistry, R.id.cbBiology, R.id.cbEnglish, R.id.cbLiterature, R.id.cbOther};
        
        for (int id : checkboxIds) {
            CheckBox cb = view.findViewById(id);
            if (cb != null) {
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    String subject = cb.getText().toString();
                    if (isChecked) {
                        selectedSubjects.add(subject);
                    } else {
                        selectedSubjects.remove(subject);
                    }
                    saveSubjects();
                });
            }
        }
        RadioGroup grades = view.findViewById(R.id.rgGrade);
        grades.setOnCheckedChangeListener((group, checkedId) -> {
            String level = checkedId == R.id.rbMid ? "Secondary school"
                    : checkedId == R.id.rbHigh ? "High school" : "University";
            requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    .edit().putString("education_level", level).apply();
        });
        ((EditText) view.findViewById(R.id.etGoal)).setOnFocusChangeListener((field, focused) -> {
            if (!focused) requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    .edit().putString("learning_goal", ((EditText) field).getText().toString().trim()).apply();
        });
        
        return view;
    }
    
    private void saveSubjects() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putStringSet("onboarding_subjects", selectedSubjects).apply();
    }
}
