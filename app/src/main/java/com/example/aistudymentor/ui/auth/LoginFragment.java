package com.example.aistudymentor.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import java.security.SecureRandom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.models.User;
import com.example.aistudymentor.data.repositories.UserRepository;

public class LoginFragment extends Fragment {
    private UserRepository userRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);

        view.findViewById(R.id.btnSubmitLogin).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = userRepository.loginUser(email, password);
            if (user != null) {
                String[] preferences = userRepository.getLearningPreferences(email);
                if ("1".equals(preferences[3])) {
                    String otp = String.format(java.util.Locale.US, "%06d", new SecureRandom().nextInt(1_000_000));
                    Bundle args = new Bundle();
                    args.putBoolean("loginOnly", true); args.putString("email", email); args.putString("expectedOtp", otp);
                    args.putLong("otpExpiresAt", System.currentTimeMillis() + 5 * 60 * 1000L);
                    Toast.makeText(requireContext(), "Prototype verification code: " + otp, Toast.LENGTH_LONG).show();
                    Navigation.findNavController(view).navigate(R.id.action_login_to_otp, args);
                    return;
                }
                SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                prefs.edit().putBoolean("isLoggedIn", true)
                     .putString("currentUserEmail", email).apply();
                
                Navigation.findNavController(view).navigate(R.id.action_login_to_dashboard);
            } else {
                Toast.makeText(getContext(), "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.tvGoToRegister).setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_login_to_register);
        });
    }
}
