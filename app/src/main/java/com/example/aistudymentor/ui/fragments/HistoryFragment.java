package com.example.aistudymentor.ui.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.models.QuestionRecord;
import com.example.aistudymentor.data.repositories.StudyRepository;
import com.example.aistudymentor.data.repositories.UserRepository;
import com.example.aistudymentor.ui.adapters.HistoryAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class HistoryFragment extends Fragment implements HistoryAdapter.Listener {
    private StudyRepository repository;
    private HistoryAdapter adapter;
    private TextView empty;
    private TextView count;
    private MaterialButton bookmarks;
    private String email;
    private String currentQuery = "";
    private boolean bookmarksOnly;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        repository = new StudyRepository(requireContext());
        email = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getString("currentUserEmail", "guest");

        RecyclerView list = view.findViewById(R.id.rvHistory);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HistoryAdapter(this);
        list.setAdapter(adapter);

        empty = view.findViewById(R.id.tvEmpty);
        count = view.findViewById(R.id.tvHistoryCount);
        bookmarks = view.findViewById(R.id.btnBookmarks);
        EditText search = view.findViewById(R.id.etSearch);

        TextView insight = view.findViewById(R.id.tvInsight);
        insight.setText("Chủ đề học nhiều nhất: " + repository.getMostFrequentSubject(email)
                + ". Hãy ôn lại các câu đã đánh dấu.");

        view.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());
        bookmarks.setOnClickListener(v -> {
            bookmarksOnly = !bookmarksOnly;
            updateBookmarkFilter();
            reload();
        });
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString();
                reload();
            }
            public void afterTextChanged(Editable editable) {}
        });

        updateBookmarkFilter();
        reload();
    }

    private void reload() {
        List<QuestionRecord> values = repository.search(email, currentQuery, bookmarksOnly);
        adapter.submit(values);
        empty.setVisibility(values.isEmpty() ? View.VISIBLE : View.GONE);
        count.setText(values.size() + (values.size() == 1 ? " câu hỏi" : " câu hỏi đã lưu"));
    }

    private void updateBookmarkFilter() {
        bookmarks.setText(bookmarksOnly ? "Đang xem câu đã đánh dấu" : "Chỉ xem câu đã đánh dấu");
        bookmarks.setIconResource(bookmarksOnly
                ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        bookmarks.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(
                bookmarksOnly ? "#EEF2FF" : "#FFFFFF")));
    }

    @Override public void onBookmark(QuestionRecord item) {
        repository.setBookmarked(item.id, !item.bookmarked);
        item.bookmarked = !item.bookmarked;
        reload();
    }

    @Override public void onReview(QuestionRecord item) {
        if (!item.reviewed) {
            repository.markReviewed(item.id);
            new UserRepository(requireContext()).awardForReview(email, false);
            item.reviewed = true;
        }
        reload();
    }
}
