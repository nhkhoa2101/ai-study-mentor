package com.example.aistudymentor.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.aistudymentor.R;

public class OnboardingFinishFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_finish, container, false);
        Spinner level = view.findViewById(R.id.spEducationLevel);
        Spinner style = view.findViewById(R.id.spExplanationStyle);
        level.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Trung học cơ sở", "Trung học phổ thông", "Cao đẳng", "Đại học"}));
        style.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Từng bước", "Đơn giản và ngắn gọn", "Ví dụ trước", "Lý thuyết chi tiết"}));
        android.widget.AdapterView.OnItemSelectedListener listener = new android.widget.AdapterView.OnItemSelectedListener() {
            public void onItemSelected(android.widget.AdapterView<?> parent, View selected, int position, long id) {
                requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit()
                        .putString("education_level", String.valueOf(level.getSelectedItem()))
                        .putString("explanation_style", String.valueOf(style.getSelectedItem())).apply();
            }
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        };
        level.setOnItemSelectedListener(listener); style.setOnItemSelectedListener(listener);
        return view;
    }
}
