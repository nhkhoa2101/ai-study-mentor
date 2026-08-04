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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainHostFragment extends Fragment {

    private DashboardFragment dashboardFragment = new DashboardFragment();
    // Use simple fragments for others right now
    private Fragment pathFragment = new PathFragment(); 
    private Fragment progressFragment = new ProgressFragment();
    private Fragment profileFragment = new ProfileFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_host, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottomNavigationView);
        
        // Initial fragment
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                .replace(R.id.main_nav_host, dashboardFragment)
                .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = dashboardFragment;
            } else if (itemId == R.id.nav_path) {
                selectedFragment = pathFragment;
            } else if (itemId == R.id.nav_progress) {
                selectedFragment = progressFragment;
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = profileFragment;
            }

            if (selectedFragment != null) {
                getChildFragmentManager().beginTransaction()
                    .replace(R.id.main_nav_host, selectedFragment)
                    .commit();
                return true;
            }
            return false;
        });

        view.findViewById(R.id.fab_ai).setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_mainHost_to_ask);
        });
    }
}
