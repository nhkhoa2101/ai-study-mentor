package com.example.aistudymentor.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.navigation.Navigation;

import com.example.aistudymentor.BuildConfig;
import com.example.aistudymentor.R;
import com.example.aistudymentor.data.remote.ApiClient;
import com.example.aistudymentor.data.remote.GeminiApiService;
import com.example.aistudymentor.data.remote.models.request.Content;
import com.example.aistudymentor.data.remote.models.request.GeminiRequest;
import com.example.aistudymentor.data.remote.models.request.Part;
import com.example.aistudymentor.data.remote.models.response.Candidate;
import com.example.aistudymentor.data.remote.models.response.GeminiResponse;
import com.example.aistudymentor.data.remote.models.response.ResponsePart;
import com.example.aistudymentor.data.remote.models.response.UsageMetadata;
import com.example.aistudymentor.data.repositories.StudyRepository;
import com.example.aistudymentor.data.repositories.UserRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PathFragment extends Fragment {
    private UserRepository repository;
    private String email;
    private LinearLayout container;
    private TextView empty;
    private TextView activeCount;
    private TextView completedCount;
    private Context appContext;
    private final Set<Integer> generatingPlanIds = new HashSet<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_path, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        appContext = requireContext().getApplicationContext();
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
                .setText("AI tạo 1 câu quiz cho mỗi phút học trong lộ trình.");

        view.findViewById(R.id.btnAddPlan).setOnClickListener(v -> {
            if (email == null) {
                Toast.makeText(requireContext(), "Đăng nhập để lưu lộ trình", Toast.LENGTH_SHORT).show();
            } else {
                showAddPlanDialog();
            }
        });
        loadPlans();
    }

    @Override public void onResume() {
        super.onResume();
        if (container != null) loadPlans();
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
            String quizStatus = plan.quizTotal == 0
                    ? (generatingPlanIds.contains(plan.id) ? "AI đang tạo quiz…" : "Chưa có quiz")
                    : "Quiz " + plan.quizAnswered + "/" + plan.quizTotal;
            ((TextView) item.findViewById(R.id.tvPlanMeta))
                    .setText(subject + "  •  " + duration + "  •  " + quizStatus);

            TextView action = item.findViewById(R.id.btnCompletePlan);
            if (generatingPlanIds.contains(plan.id)) {
                action.setText("Đang tạo quiz");
                action.setEnabled(false);
            } else if (plan.quizTotal == 0) {
                action.setText("Tạo lại quiz AI");
                action.setEnabled(true);
                action.setOnClickListener(v -> generatePlanQuiz(plan));
            } else if (plan.quizAnswered < plan.quizTotal) {
                action.setText("Làm quiz " + plan.quizAnswered + "/" + plan.quizTotal);
                action.setEnabled(true);
                action.setOnClickListener(v -> openPlanQuiz(plan.id));
            } else {
                action.setText("Hoàn thành");
                action.setEnabled(true);
                action.setOnClickListener(v -> completePlan(plan));
            }
            item.findViewById(R.id.btnDeletePlan).setOnClickListener(v -> confirmDelete(plan));
            container.addView(item);
        }
    }

    private void openPlanQuiz(int planId) {
        Bundle args = new Bundle();
        args.putInt("planId", planId);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_mainHost_to_quiz, args);
    }

    private void completePlan(UserRepository.StudyPlan plan) {
        if (!repository.completeStudyPlanIfQuizFinished(plan.id, email)) {
            Toast.makeText(requireContext(),
                    "Bạn phải trả lời hết quiz trước khi hoàn thành lộ trình.", Toast.LENGTH_LONG).show();
            loadPlans();
            return;
        }
        Toast.makeText(requireContext(), "Đã hoàn thành • +15 XP", Toast.LENGTH_SHORT).show();
        loadPlans();
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
                .setTitle("Thêm lộ trình học tập")
                .setView(form)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Thêm và tạo quiz", null)
                .create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String value = title.getText().toString().trim();
                    if (value.isEmpty()) {
                        title.setError("Nhập nội dung cần học");
                        return;
                    }
                    String selectedSubject = String.valueOf(subject.getSelectedItem());
                    String selectedDuration = String.valueOf(duration.getSelectedItem());
                    long id = repository.addStudyPlan(email, "PLAN_ITEM", value,
                            selectedSubject, selectedDuration);
                    if (id < 0 || id > Integer.MAX_VALUE) {
                        Toast.makeText(requireContext(), "Không thể tạo lộ trình.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    UserRepository.StudyPlan plan = new UserRepository.StudyPlan();
                    plan.id = (int) id;
                    plan.title = value;
                    plan.subtitle = selectedSubject;
                    plan.info = selectedDuration;
                    dialog.dismiss();
                    loadPlans();
                    generatePlanQuiz(plan);
                }));
        dialog.show();
    }

    private void generatePlanQuiz(UserRepository.StudyPlan plan) {
        if (generatingPlanIds.contains(plan.id)) return;
        if (BuildConfig.GEMINI_API_KEY == null || BuildConfig.GEMINI_API_KEY.trim().isEmpty()) {
            Toast.makeText(requireContext(),
                    "Chưa cấu hình Gemini API để tạo quiz.", Toast.LENGTH_LONG).show();
            return;
        }
        int target = questionCountForDuration(plan.info);
        String prompt = "Create exactly " + target + " short-answer revision questions for this learning plan. "
                + "Use the same language as the plan title. Questions must be directly related to the title and subject, "
                + "clear for a student, and answerable in one or two sentences. Return JSON only with this schema: "
                + "{\"questions\":[{\"question\":\"...\",\"correctAnswer\":\"...\"}]}. "
                + "Plan title: " + plan.title + ". Subject: " + plan.subtitle
                + ". Duration: " + plan.info + ".";
        GeminiRequest request = new GeminiRequest(Collections.singletonList(
                new Content(Collections.singletonList(new Part(prompt)))));
        generatingPlanIds.add(plan.id);
        loadPlans();
        executePlanQuizRequest(plan, request, target, true);
    }

    private void executePlanQuizRequest(UserRepository.StudyPlan plan, GeminiRequest request,
                                        int target, boolean mayRetry) {
        ApiClient.getClient().create(GeminiApiService.class)
                .generateContent(BuildConfig.GEMINI_API_KEY, request)
                .enqueue(new retrofit2.Callback<GeminiResponse>() {
                    @Override public void onResponse(@NonNull retrofit2.Call<GeminiResponse> call,
                                                     @NonNull retrofit2.Response<GeminiResponse> response) {
                        if (mayRetry && isRetryable(response.code())) {
                            executePlanQuizRequest(plan, request, target, false);
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            finishGeneration(plan.id, "Không thể tạo quiz AI. Hãy chọn ‘Tạo lại quiz AI’.");
                            return;
                        }
                        recordTokenUsage(response.body());
                        List<UserRepository.PlanQuizQuestion> questions = parseGeneratedQuiz(response.body());
                        if (questions.size() < target && mayRetry) {
                            executePlanQuizRequest(plan, request, target, false);
                            return;
                        }
                        if (questions.size() < target) {
                            finishGeneration(plan.id, "AI trả về chưa đủ câu hỏi. Hãy tạo lại quiz.");
                            return;
                        }
                        repository.replacePlanQuizQuestions(plan.id, email,
                                new ArrayList<>(questions.subList(0, target)));
                        finishGeneration(plan.id, "AI đã tạo " + target + " câu hỏi ôn tập.");
                    }

                    @Override public void onFailure(@NonNull retrofit2.Call<GeminiResponse> call,
                                                    @NonNull Throwable throwable) {
                        if (mayRetry) {
                            executePlanQuizRequest(plan, request, target, false);
                        } else {
                            finishGeneration(plan.id,
                                    "Không thể kết nối AI. Hãy chọn ‘Tạo lại quiz AI’.");
                        }
                    }
                });
    }

    private List<UserRepository.PlanQuizQuestion> parseGeneratedQuiz(GeminiResponse response) {
        StringBuilder raw = new StringBuilder();
        if (response.getCandidates() != null) {
            for (Candidate candidate : response.getCandidates()) {
                if (candidate == null || candidate.getContent() == null
                        || candidate.getContent().getParts() == null) continue;
                for (ResponsePart part : candidate.getContent().getParts()) {
                    if (part != null && part.getText() != null) raw.append(part.getText());
                }
            }
        }
        int start = raw.indexOf("{");
        int end = raw.lastIndexOf("}");
        if (start < 0 || end <= start) return new ArrayList<>();
        try {
            GeneratedQuizPayload payload = new Gson().fromJson(raw.substring(start, end + 1),
                    GeneratedQuizPayload.class);
            List<UserRepository.PlanQuizQuestion> valid = new ArrayList<>();
            if (payload != null && payload.questions != null) {
                for (UserRepository.PlanQuizQuestion item : payload.questions) {
                    if (item != null && item.isValid()) valid.add(item);
                }
            }
            return valid;
        } catch (RuntimeException ignored) {
            return new ArrayList<>();
        }
    }

    private void recordTokenUsage(GeminiResponse response) {
        UsageMetadata usage = response.getUsageMetadata();
        if (usage == null || usage.getTotalTokenCount() <= 0 || email == null) return;
        if (appContext == null) return;
        SharedPreferences prefs = appContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String account = email.trim().toLowerCase(Locale.ROOT);
        String prefix = "account_usage." + account + ".";
        String day = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        long used = day.equals(prefs.getString(prefix + "daily_usage_day", ""))
                ? prefs.getLong(prefix + "daily_token_used", 0L) : 0L;
        prefs.edit().putString(prefix + "daily_usage_day", day)
                .putLong(prefix + "daily_token_used", used + usage.getTotalTokenCount()).apply();
    }

    private void finishGeneration(int planId, String message) {
        generatingPlanIds.remove(planId);
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            loadPlans();
        });
    }

    private int questionCountForDuration(String duration) {
        if (duration == null) return 20;
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)").matcher(duration);
        int minutes = matcher.find() ? Integer.parseInt(matcher.group(1)) : 20;
        return Math.max(1, minutes);
    }

    private boolean isRetryable(int code) {
        return code == 500 || code == 502 || code == 503 || code == 504;
    }

    private void confirmDelete(UserRepository.StudyPlan plan) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa lộ trình học tập?")
                .setMessage(plan.title + " và toàn bộ quiz liên quan sẽ bị xóa.")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    repository.deleteStudyPlan(plan.id);
                    loadPlans();
                }).show();
    }

    private static class GeneratedQuizPayload {
        List<UserRepository.PlanQuizQuestion> questions;
    }
}
