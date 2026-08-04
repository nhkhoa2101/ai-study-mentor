package com.example.aistudymentor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);

            SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);
            if (sharedPref.getBoolean("isGuest", false)) {
                sharedPref.edit().remove("isGuest").apply();
            }

            if (isLoggedIn) {
                navGraph.setStartDestination(R.id.mainHostFragment);
            } else {
                navGraph.setStartDestination(R.id.onboardingHostFragment);
            }

            navController.setGraph(navGraph);
        }
    }
}
