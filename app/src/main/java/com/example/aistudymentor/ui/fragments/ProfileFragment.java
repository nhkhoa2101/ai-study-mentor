package com.example.aistudymentor.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.models.User;
import com.example.aistudymentor.data.repositories.UserRepository;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadUserData(view);

        view.findViewById(R.id.rowNotifications).setOnClickListener(v -> openDevelopment("Thông báo"));
        view.findViewById(R.id.rowSecurity).setOnClickListener(v -> openDevelopment("Bảo mật"));
        view.findViewById(R.id.rowHelp).setOnClickListener(v -> openDevelopment("Trợ giúp"));

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity()
                    .getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("isLoggedIn", false).putBoolean("isGuest", false).apply();
            androidx.navigation.NavOptions options = new androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true).build();
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.onboardingHostFragment, null, options);
        });
    }

    private void openDevelopment(String featureName) {
        Bundle args = new Bundle();
        args.putString("featureName", featureName);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_mainHost_to_featureDevelopment, args);
    }

    private void loadUserData(View view) {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String email = prefs.getString("currentUserEmail", null);
        if (email == null) return;

        UserRepository repository = new UserRepository(requireContext());
        User user = repository.getUserByEmail(email);
        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);

        if (user != null) {
            ((TextView) view.findViewById(R.id.tvProfileName))
                    .setText(user.getName() != null ? user.getName() : "Khách");
            ((TextView) view.findViewById(R.id.tvProfileLevel))
                    .setText("★  Cấp " + user.getLevel());

            int levelXp = user.getXp() % 100;
            ((TextView) view.findViewById(R.id.tvProfileXpText))
                    .setText(format.format(levelXp) + " / 100 XP");
            ((LinearProgressIndicator) view.findViewById(R.id.pbProfileXp))
                    .setProgress(levelXp);

            ((TextView) view.findViewById(R.id.tvProfileQuestions))
                    .setText(format.format(user.getQuestionsSolved()));
            ((TextView) view.findViewById(R.id.tvProfileStreak))
                    .setText(user.getStreak() + " ngày");
            ((TextView) view.findViewById(R.id.tvProfileAccuracy))
                    .setText(user.getAccuracy() + "%");
        }

        List<User> leaders = repository.getTopUsers(5);
        StringBuilder board = new StringBuilder();
        for (int i = 0; i < leaders.size(); i++) {
            User leader = leaders.get(i);
            board.append(i + 1).append(". ")
                    .append(leader.getName() == null ? "Người học" : leader.getName())
                    .append("  —  ").append(format.format(leader.getXp())).append(" XP\n");
        }
        if (leaders.isEmpty()) {
            board.append("Hoàn thành hoạt động học để vào bảng xếp hạng.");
        }
        ((TextView) view.findViewById(R.id.tvLeaderboard))
                .setText(board.toString().trim());
    }
}
