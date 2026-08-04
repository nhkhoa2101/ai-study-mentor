package com.example.aistudymentor.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.models.RecentActivity;
import com.example.aistudymentor.data.models.User;
import com.example.aistudymentor.data.repositories.ActivityRepository;
import com.example.aistudymentor.data.repositories.UserRepository;
import com.example.aistudymentor.ui.adapters.RecentActivityAdapter;
import com.google.android.material.card.MaterialCardView;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        MaterialCardView btnQuickSnap = view.findViewById(R.id.btnQuickSnap);
        MaterialCardView btnQuickAsk = view.findViewById(R.id.btnQuickAsk);
        MaterialCardView btnQuickQuiz = view.findViewById(R.id.btnQuickQuiz);

        // Navigate to AskFragment
        View.OnClickListener goToAsk = v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_mainHost_to_ask);
        };

        btnQuickSnap.setOnClickListener(goToAsk);
        btnQuickAsk.setOnClickListener(goToAsk);
        
        btnQuickQuiz.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_mainHost_to_quiz));
        view.findViewById(R.id.btnHistory).setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_mainHost_to_history));

        loadUserData(view);
    }

    private void loadUserData(View view) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("currentUserEmail", null);

        if (userEmail != null) {
            UserRepository userRepository = new UserRepository(requireContext());
            User user = userRepository.getUserByEmail(userEmail);

            if (user != null) {
                TextView tvUserName = view.findViewById(R.id.tvUserName);
                TextView tvLevel = view.findViewById(R.id.tvLevel);
                TextView tvXp = view.findViewById(R.id.tvXp);
                TextView tvStreak = view.findViewById(R.id.tvStreak);
                TextView tvProgressDailyPercent = view.findViewById(R.id.tvProgressDailyPercent);
                TextView tvProgressSub = view.findViewById(R.id.tvProgressSub);
                TextView tvLessons = view.findViewById(R.id.tvLessons);
                TextView tvStudyTime = view.findViewById(R.id.tvStudyTime);
                TextView tvQuestions = view.findViewById(R.id.tvQuestions);
                TextView tvAccuracy = view.findViewById(R.id.tvAccuracy);
                CircularProgressIndicator progressDaily = view.findViewById(R.id.progressDaily);

                if (user.getName() != null) {
                    tvUserName.setText(user.getName() + " 👋");
                }
                
                tvLevel.setText("🌟 Level " + user.getLevel());
                tvXp.setText(user.getXp() + " XP");
                tvStreak.setText(user.getStreak() + " ngày");
                
                int progress = user.getDailyGoalProgress();
                progressDaily.setProgress(progress);
                tvProgressDailyPercent.setText(progress + "%");
                int currentMins = (progress * 60) / 100;
                tvProgressSub.setText(currentMins + " / 60 phút");
                
                tvLessons.setText(user.getLessonsLearned() + " bài");
                tvStudyTime.setText(user.getStudyTime() + " phút");
                tvQuestions.setText(user.getQuestionsSolved() + " câu");
                tvAccuracy.setText(user.getAccuracy() + "%");

                // Load Recent Activities
                ActivityRepository activityRepository = new ActivityRepository(requireContext());
                List<RecentActivity> recentActivities = activityRepository.getRecentActivitiesByEmail(userEmail);

                // For testing purposes, if it's empty, we could insert some mock data, but we'll just show empty
                RecyclerView rvRecentActivities = view.findViewById(R.id.rvRecentActivities);
                rvRecentActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
                
                if (recentActivities.isEmpty()) {
                    // Temporarily add a mock activity for visual verification if empty
                    activityRepository.addActivity(userEmail, "Chào mừng bạn mới", "Bạn đã tạo tài khoản thành công!");
                    recentActivities = activityRepository.getRecentActivitiesByEmail(userEmail);
                }

                RecentActivityAdapter adapter = new RecentActivityAdapter(recentActivities);
                rvRecentActivities.setAdapter(adapter);
            }
        }
    }
}
