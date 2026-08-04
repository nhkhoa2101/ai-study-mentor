package com.example.aistudymentor.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.models.QuestionRecord;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {
    public interface Listener {
        void onBookmark(QuestionRecord item);
        void onReview(QuestionRecord item);
    }

    private final List<QuestionRecord> items = new ArrayList<>();
    private final Listener listener;

    public HistoryAdapter(Listener listener) { this.listener = listener; }

    public void submit(List<QuestionRecord> values) {
        items.clear();
        items.addAll(values);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        QuestionRecord item = items.get(position);
        String date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(new Date(item.createdAt));
        holder.meta.setText(item.subject + "  •  " + item.difficulty + "  •  " + date);
        holder.question.setText(item.question);
        holder.answer.setText(item.answer);
        holder.bookmark.setText(item.bookmarked ? "Đã đánh dấu" : "Đánh dấu");
        holder.bookmark.setIconResource(item.bookmarked
                ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        holder.review.setText(item.reviewed ? "Đã ôn ✓" : "Đánh dấu đã ôn");
        holder.review.setEnabled(!item.reviewed);
        holder.bookmark.setOnClickListener(v -> listener.onBookmark(item));
        holder.review.setOnClickListener(v -> listener.onReview(item));
    }

    @Override public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView meta;
        final TextView question;
        final TextView answer;
        final MaterialButton bookmark;
        final MaterialButton review;

        Holder(View view) {
            super(view);
            meta = view.findViewById(R.id.tvMeta);
            question = view.findViewById(R.id.tvQuestion);
            answer = view.findViewById(R.id.tvAnswer);
            bookmark = view.findViewById(R.id.btnBookmark);
            review = view.findViewById(R.id.btnReview);
        }
    }
}
