package com.example.aistudymentor.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.repositories.StudyRepository;
import com.example.aistudymentor.data.repositories.UserRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathFragment extends Fragment {
    private UserRepository repository;
    private String email;
    private LinearLayout container;
    private TextView empty;
    private TextView activeCount;
    private TextView completedCount;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_path, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        repository = new UserRepository(requireContext());
        email = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getString("currentUserEmail", null);
        container = view.findViewById(R.id.llPlanContainer);
        empty = view.findViewById(R.id.tvEmptyPlan);
        activeCount = view.findViewById(R.id.tvActivePlanCount);
        completedCount = view.findViewById(R.id.tvCompletedPlanCount);

        String topic = email == null ? "môn học bạn quan tâm"
                : new StudyRepository(requireContext()).getMostFrequentSubject(email);
        ((TextView) view.findViewById(R.id.tvSuggestionTitle))
                .setText("Ôn lại " + topic + " trong 20 phút");
        ((TextView) view.findViewById(R.id.tvSuggestionSubtitle))
                .setText("Chia nhỏ mục tiêu và hoàn thành từng phần mỗi ngày.");

        view.findViewById(R.id.btnAddPlan).setOnClickListener(v -> {
            if (email == null) {
                Toast.makeText(requireContext(), "Đăng nhập để lưu lộ trình", Toast.LENGTH_SHORT).show();
            } else {
                showAddPlanDialog();
            }
        });
        loadPlans();
    }

    private void loadPlans() {
        container.removeAllViews();
        if (email == null) {
            empty.setText("Đăng nhập để tạo và lưu lộ trình học tập của riêng bạn.");
            empty.setVisibility(View.VISIBLE);
            activeCount.setText("0");
            completedCount.setText("0");
            return;
        }

        List<UserRepository.StudyPlan> plans = repository.getStudyPlan(email, "PLAN_ITEM");
        List<UserRepository.StudyPlan> completed = repository.getStudyPlan(email, "COMPLETED");
        activeCount.setText(String.valueOf(plans.size()));
        completedCount.setText(String.valueOf(completed.size()));
        empty.setVisibility(plans.isEmpty() ? View.VISIBLE : View.GONE);

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (int i = 0; i < plans.size(); i++) {
            UserRepository.StudyPlan plan = plans.get(i);
            View item = inflater.inflate(R.layout.item_study_plan, container, false);
            ((TextView) item.findViewById(R.id.tvPlanNumber)).setText(String.valueOf(i + 1));
            ((TextView) item.findViewById(R.id.tvPlanTitle)).setText(plan.title);
            String subject = plan.subtitle == null || plan.subtitle.isEmpty() ? "Tự chọn" : plan.subtitle;
            String duration = plan.info == null || plan.info.isEmpty() ? "20 phút" : plan.info;
            ((TextView) item.findViewById(R.id.tvPlanMeta)).setText(subject + "  •  " + duration);
            item.findViewById(R.id.btnCompletePlan).setOnClickListener(v -> {
                repository.updateStudyPlanType(plan.id, "COMPLETED");
                repository.awardForReview(email, true);
                Toast.makeText(requireContext(), "Đã hoàn thành • +15 XP", Toast.LENGTH_SHORT).show();
                loadPlans();
            });
            item.findViewById(R.id.btnDeletePlan).setOnClickListener(v -> confirmDelete(plan));
            container.addView(item);
        }
    }

    private void showAddPlanDialog() {
        View form = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plan, null, false);
        EditText title = form.findViewById(R.id.etPlanTitle);
        Spinner subject = form.findViewById(R.id.spPlanSubject);
        Spinner duration = form.findViewById(R.id.spPlanDuration);

        List<String> subjects = new ArrayList<>();
        for (UserRepository.SubjectProgress value : repository.getSubjectProgress(email)) {
            if (!subjects.contains(value.name)) subjects.add(value.name);
        }
        if (subjects.isEmpty()) {
            subjects.addAll(Arrays.asList("Mathematics", "Physics", "Chemistry",
                    "Biology", "Computer Science", "English", "General"));
        }
        subject.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, subjects));
        duration.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"10 phút", "15 phút", "20 phút", "30 phút", "45 phút", "60 phút"}));

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Thêm mục học tập")
                .setView(form)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Thêm", null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String value = title.getText().toString().trim();
                    if (value.isEmpty()) {
                        title.setError("Nhập nội dung cần học");
                        return;
                    }
                    repository.addStudyPlan(email, "PLAN_ITEM", value,
                            String.valueOf(subject.getSelectedItem()),
                            String.valueOf(duration.getSelectedItem()));
                    Toast.makeText(requireContext(), "Đã thêm vào lộ trình", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadPlans();
                }));
        dialog.show();
    }

    private void confirmDelete(UserRepository.StudyPlan plan) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa mục học tập?")
                .setMessage(plan.title)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    repository.deleteStudyPlan(plan.id);
                    loadPlans();
                }).show();
    }
}
