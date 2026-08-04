package com.example.aistudymentor.ui.fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.repositories.UserRepository;
import com.example.aistudymentor.notifications.ReviewReminderReceiver;

import java.util.Calendar;

public class SettingsFragment extends Fragment {
    private Spinner level,style; private SwitchCompat notifications,twoFactor; private UserRepository repository; private String email;
    private final ActivityResultLauncher<String> notificationPermission=registerForActivityResult(new ActivityResultContracts.RequestPermission(),granted->{if(!granted&&notifications!=null)notifications.setChecked(false);});
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle state){return inflater.inflate(R.layout.fragment_settings,container,false);}
    @Override public void onViewCreated(@NonNull View view,@Nullable Bundle state){
        repository=new UserRepository(requireContext()); email=requireActivity().getSharedPreferences("AppPrefs",Context.MODE_PRIVATE).getString("currentUserEmail",null);
        level=view.findViewById(R.id.spEducationLevel); style=view.findViewById(R.id.spExplanationStyle); notifications=view.findViewById(R.id.swNotifications); twoFactor=view.findViewById(R.id.swTwoFactor);
        String[] levels={"Secondary school","High school","College","University"}; String[] styles={"Step by step","Simple and concise","Examples first","Detailed theory"};
        level.setAdapter(new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_dropdown_item,levels)); style.setAdapter(new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_dropdown_item,styles));
        if(email!=null){String[] saved=repository.getLearningPreferences(email);select(level,saved[0]);select(style,saved[1]);notifications.setChecked("1".equals(saved[2]));twoFactor.setChecked("1".equals(saved[3]));}
        view.findViewById(R.id.btnBack).setOnClickListener(v->requireActivity().onBackPressed());
        view.findViewById(R.id.btnSaveSettings).setOnClickListener(v->save());
    }
    private void save(){if(email==null){Toast.makeText(requireContext(),"Sign in to save settings",Toast.LENGTH_SHORT).show();return;} boolean enabled=notifications.isChecked(); if(enabled&&Build.VERSION.SDK_INT>=33&&ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS); repository.updateLearningPreferences(email,String.valueOf(level.getSelectedItem()),String.valueOf(style.getSelectedItem()),enabled,twoFactor.isChecked());schedule(enabled);Toast.makeText(requireContext(),"Settings saved",Toast.LENGTH_SHORT).show();}
    private void schedule(boolean enabled){AlarmManager alarm=(AlarmManager)requireContext().getSystemService(Context.ALARM_SERVICE);PendingIntent pending=PendingIntent.getBroadcast(requireContext(),1001,new Intent(requireContext(),ReviewReminderReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);if(!enabled){alarm.cancel(pending);return;}Calendar time=Calendar.getInstance();time.set(Calendar.HOUR_OF_DAY,19);time.set(Calendar.MINUTE,0);time.set(Calendar.SECOND,0);if(time.getTimeInMillis()<=System.currentTimeMillis())time.add(Calendar.DAY_OF_YEAR,1);alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,time.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pending);}
    private void select(Spinner spinner,String value){for(int i=0;i<spinner.getCount();i++)if(String.valueOf(spinner.getItemAtPosition(i)).equals(value)){spinner.setSelection(i);break;}}
}
