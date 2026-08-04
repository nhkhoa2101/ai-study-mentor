package com.example.aistudymentor.ui.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.repositories.UserRepository;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

public class OtpFragment extends Fragment {
    private UserRepository userRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        EditText etOtp = view.findViewById(R.id.etOtp);

        view.findViewById(R.id.btnVerifyOtp).setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();
            if (otp.length() != 6) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle args = getArguments();
            String expected = args == null ? null : args.getString("expectedOtp");
            long expiresAt = args == null ? 0L : args.getLong("otpExpiresAt", 0L);
            if (expected == null || !expected.equals(otp) || System.currentTimeMillis() > expiresAt) {
                Toast.makeText(getContext(), "Invalid or expired verification code", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mock OTP verification delay
            Toast.makeText(getContext(), "Đang xác thực...", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (getArguments() != null) {
                    if (getArguments().getBoolean("loginOnly", false)) {
                        String loginEmail = getArguments().getString("email");
                        requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit()
                                .putBoolean("isLoggedIn", true).putString("currentUserEmail", loginEmail).apply();
                        Navigation.findNavController(view).navigate(R.id.action_otp_to_authSuccess);
                        return;
                    }
                    String name = getArguments().getString("name", "Người dùng");
                    String email = getArguments().getString("email");
                    String password = getArguments().getString("password");
                    
                    if (email != null && password != null) {
                        boolean isRegistered = userRepository.registerUser(email, password, name);
                        if (isRegistered) {
                            SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                            prefs.edit().putString("currentUserEmail", email).apply();
                            userRepository.updateLearningPreferences(email,
                                    prefs.getString("education_level", "University"),
                                    prefs.getString("explanation_style", "Step by step"), true, false);
                            
                            // Retrieve onboarding subjects and insert them
                            Set<String> selectedSubjects = prefs.getStringSet("onboarding_subjects", null);
                            if (selectedSubjects != null && !selectedSubjects.isEmpty()) {
                                for (String subject : selectedSubjects) {
                                    userRepository.addSubjectProgress(email, subject, "Mới bắt đầu", 0);
                                }
                                // Optionally create a study plan
                                userRepository.addStudyPlan(email, "SUGGESTION", "Chào mừng bạn mới", "Hãy bắt đầu với môn học bạn đã chọn!", null);
                                // Clear them so they don't apply to another user
                                prefs.edit().remove("onboarding_subjects").apply();
                            }
                            
                            Navigation.findNavController(view).navigate(R.id.action_otp_to_authSuccess);
                        } else {
                            Toast.makeText(getContext(), "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, 1000);
        });
    }
}
