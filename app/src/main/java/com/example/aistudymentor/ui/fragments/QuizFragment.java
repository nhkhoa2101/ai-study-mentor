package com.example.aistudymentor.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.models.QuestionRecord;
import com.example.aistudymentor.data.repositories.StudyRepository;
import com.example.aistudymentor.data.repositories.UserRepository;
import com.example.aistudymentor.ui.formatting.RichTextFormatter;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class QuizFragment extends Fragment {
    private static final int MAX_SESSION_QUESTIONS = 10;

    private StudyRepository repository;
    private String email;
    private List<QuestionRecord> source;
    private QuestionRecord current;
    private TextView question;
    private TextView feedback;
    private TextView progress;
    private EditText answer;
    private Spinner type;
    private View submit;
    private View next;
    private int shownCount;
    private final Random random = new Random();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        repository = new StudyRepository(requireContext());
        email = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getString("currentUserEmail", "guest");
        question = view.findViewById(R.id.tvQuizQuestion);
        feedback = view.findViewById(R.id.tvFeedback);
        progress = view.findViewById(R.id.tvQuizProgress);
        answer = view.findViewById(R.id.etQuizAnswer);
        type = view.findViewById(R.id.spQuizType);
        submit = view.findViewById(R.id.btnSubmitQuiz);
        next = view.findViewById(R.id.btnNextQuiz);
        type.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Trả lời ngắn", "Điền ý chính", "Chọn môn học"}));
        source = repository.search(email, "", false);
        view.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());
        next.setOnClickListener(v -> showNext());
        submit.setOnClickListener(v -> check());
        showNext();
    }

    private void showNext() {
        answer.setText("");
        feedback.setText("");
        feedback.setVisibility(View.GONE);
        if (source.isEmpty()) {
            current = null;
            progress.setText("CHƯA CÓ DỮ LIỆU");
            question.setText("Hãy hỏi AI và lưu ít nhất một câu trả lời trước khi luyện tập.");
            answer.setEnabled(false);
            submit.setEnabled(false);
            next.setEnabled(false);
            return;
        }
        if (shownCount >= MAX_SESSION_QUESTIONS) {
            current = null;
            progress.setText("HOÀN THÀNH 10 / 10");
            question.setText("Bạn đã hoàn thành 10 câu trong phiên luyện tập này.");
            answer.setEnabled(false);
            submit.setEnabled(false);
            next.setEnabled(false);
            return;
        }
        answer.setEnabled(true);
        submit.setEnabled(true);
        shownCount++;
        progress.setText("CÂU " + shownCount + " / " + MAX_SESSION_QUESTIONS);
        current = source.get(random.nextInt(source.size()));
        int selectedType = type.getSelectedItemPosition();
        String prompt;
        if (selectedType == 2) {
            prompt = "**Câu hỏi thuộc môn học nào?**\n\n" + current.question
                    + "\n\nMathematics • Physics • Chemistry • Biology • Computer Science • English • General";
        } else if (selectedType == 1) {
            prompt = "**Hoàn thành ý chính:**\n\n" + current.question
                    + "\n\nViết một thuật ngữ hoặc kết quả quan trọng trong câu trả lời.";
        } else {
            prompt = "**Giải thích bằng lời của bạn:**\n\n" + current.question;
        }
        question.setText(RichTextFormatter.format(prompt));
    }

    private void check() {
        if (current == null) return;
        String user = answer.getText().toString().trim();
        feedback.setVisibility(View.VISIBLE);
        if (user.isEmpty()) {
            feedback.setText("Hãy nhập câu trả lời trước khi kiểm tra.");
            return;
        }
        int selectedType = type.getSelectedItemPosition();
        String expected = selectedType == 2 ? current.subject : current.answer;
        boolean correct = selectedType == 2
                ? normalize(user).equals(normalize(expected))
                : hasKeywordOverlap(user, expected);
        String result = correct
                ? "**Chính xác ✓**  +15 XP\n\n" + expected
                : "**Chưa chính xác.** +5 XP luyện tập\n\nĐáp án gợi ý: " + expected;
        feedback.setText(RichTextFormatter.format(result));
        String typeName = String.valueOf(type.getSelectedItem());
        repository.saveQuizAttempt(email, current.question, user, expected, typeName, correct);
        new UserRepository(requireContext()).awardForReview(email, correct);
    }

    private boolean hasKeywordOverlap(String user, String expected) {
        String[] words = normalize(user).split(" ");
        String target = normalize(expected);
        int hits = 0;
        for (String word : words) if (word.length() > 3 && target.contains(word)) hits++;
        return hits >= Math.min(2, Math.max(1, words.length / 3));
    }

    private String normalize(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9\\p{L}]+", " ").trim();
    }
}
