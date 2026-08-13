package com.example.aistudymentor.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.models.User;
import com.example.aistudymentor.data.repositories.StudyRepository;
import com.example.aistudymentor.data.repositories.UserRepository;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;
import java.util.Locale;

public class ProgressFragment extends Fragment {
    private View rootView;
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        loadProgressData(view);
    }

    @Override public void onResume() {
        super.onResume();
        if (rootView != null) loadProgressData(rootView);
    }

    private void loadProgressData(View view) {
        String email = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getString("currentUserEmail", null);
        if (email == null) return;

        UserRepository users = new UserRepository(requireContext());
        StudyRepository study = new StudyRepository(requireContext());
        User user = users.getUserByEmail(email);

        if (user != null) {
            int hours = user.getStudyTime() / 60;
            int minutes = user.getStudyTime() % 60;
            ((TextView) view.findViewById(R.id.tvStudyTime))
                    .setText(hours + "h " + minutes + "m");
            ((TextView) view.findViewById(R.id.tvLessonsLearned))
                    .setText(user.getLessonsLearned() + " bài");
            ((TextView) view.findViewById(R.id.tvQuestionsSolved))
                    .setText(user.getQuestionsSolved() + " câu");
            ((TextView) view.findViewById(R.id.tvAccuracy))
                    .setText(user.getAccuracy() + "%");
            ((TextView) view.findViewById(R.id.tvDailyGoal))
                    .setText(user.getDailyGoalProgress() + "%");
            ((LinearProgressIndicator) view.findViewById(R.id.pbDailyGoal))
                    .setProgress(user.getDailyGoalProgress());
            ((TextView) view.findViewById(R.id.tvLearningInsight)).setText(
                    "Bạn học nhiều nhất: " + study.getMostFrequentSubject(email)
                            + ". Ôn câu đã đánh dấu và làm quiz để tăng độ chính xác.");
        }

        List<UserRepository.SubjectProgress> subjects = users.getSubjectProgress(email);
        LinearLayout container = view.findViewById(R.id.llSubjectsContainer);
        TextView empty = view.findViewById(R.id.tvEmptySubjects);
        container.removeAllViews();
        empty.setVisibility(subjects.isEmpty() ? View.VISIBLE : View.GONE);

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (UserRepository.SubjectProgress subject : subjects) {
            View card = inflater.inflate(R.layout.item_subject_progress, container, false);
            String initial = subject.name == null || subject.name.isEmpty()
                    ? "?" : subject.name.substring(0, 1).toUpperCase(Locale.getDefault());
            ((TextView) card.findViewById(R.id.tvSubjectIcon)).setText(initial);
            ((TextView) card.findViewById(R.id.tvSubjectName)).setText(subject.name);
            ((TextView) card.findViewById(R.id.tvSubjectStatus)).setText(subject.status);
            ((TextView) card.findViewById(R.id.tvSubjectProgress)).setText(subject.progress + "%");
            ((LinearProgressIndicator) card.findViewById(R.id.pbSubjectProgress))
                    .setProgress(subject.progress);
            container.addView(card);
        }
    }
}
