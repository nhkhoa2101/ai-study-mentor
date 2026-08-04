package com.example.aistudymentor.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Patterns;
import java.security.SecureRandom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.repositories.UserRepository;

public class RegisterFragment extends Fragment {
    private UserRepository userRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        EditText etName = view.findViewById(R.id.etName);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);

        view.findViewById(R.id.btnSubmitRegister).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter a valid email address");
                return;
            }
            if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                etPassword.setError("Use 8+ characters with upper-case, lower-case and a number");
                return;
            }

            if (userRepository.checkEmailExists(email)) {
                Toast.makeText(getContext(), "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass name, email and password to OTP fragment via Bundle
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putString("email", email);
            bundle.putString("password", password);
            String otp = String.format(java.util.Locale.US, "%06d", new SecureRandom().nextInt(1_000_000));
            bundle.putString("expectedOtp", otp);
            bundle.putLong("otpExpiresAt", System.currentTimeMillis() + 5 * 60 * 1000L);
            Toast.makeText(requireContext(), "Prototype verification code: " + otp, Toast.LENGTH_LONG).show();
            Navigation.findNavController(view).navigate(R.id.action_register_to_otp, bundle);
        });

        view.findViewById(R.id.tvGoToLogin).setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_register_to_login);
        });
    }
}
