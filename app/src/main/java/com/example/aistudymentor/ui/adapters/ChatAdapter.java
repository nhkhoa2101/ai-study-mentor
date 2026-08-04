package com.example.aistudymentor.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aistudymentor.R;
import com.example.aistudymentor.data.models.ChatMessage;
import com.example.aistudymentor.data.models.QuizQuestion;
import com.example.aistudymentor.ui.formatting.RichTextFormatter;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> messageList;
    private OnChatActionClickListener listener;

    public interface OnChatActionClickListener {
        void onSuggestionClicked(String text);
        void onStepByStepClicked();
        void onActionClicked(String action);
        void onCameraClicked();
        void onTypeClicked();
    }

    public ChatAdapter(List<ChatMessage> messageList, OnChatActionClickListener listener) {
        this.messageList = messageList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ChatMessage.TYPE_USER_TEXT:
                return new UserTextViewHolder(inflater.inflate(R.layout.item_chat_user, parent, false));
            case ChatMessage.TYPE_USER_IMAGE:
                return new UserImageViewHolder(inflater.inflate(R.layout.item_chat_user_image, parent, false));
            case ChatMessage.TYPE_AI_LOADING:
                return new AiLoadingViewHolder(inflater.inflate(R.layout.item_chat_ai_loading, parent, false));
            case ChatMessage.TYPE_AI_SHORT:
                return new AiShortViewHolder(inflater.inflate(R.layout.item_chat_ai_short, parent, false));
            case ChatMessage.TYPE_AI_DETAILED:
                return new AiDetailedViewHolder(inflater.inflate(R.layout.item_chat_ai_detailed, parent, false));
            case ChatMessage.TYPE_AI_QUIZ:
                return new AiQuizViewHolder(inflater.inflate(R.layout.item_chat_ai_quiz, parent, false));
            case ChatMessage.TYPE_AI_GREETING:
            default:
                return new AiGreetingViewHolder(inflater.inflate(R.layout.item_chat_ai_greeting, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        if (holder instanceof UserTextViewHolder) {
            ((UserTextViewHolder) holder).tvMessage.setText(message.getText());
            ((UserTextViewHolder) holder).tvTime.setText(message.getTime());
        } 
        else if (holder instanceof AiShortViewHolder) {
            ((AiShortViewHolder) holder).tvAnswer.setText(RichTextFormatter.format(message.getText()));
            ((AiShortViewHolder) holder).btnStepByStep.setOnClickListener(v -> {
                if (listener != null) listener.onStepByStepClicked();
            });
            ((AiShortViewHolder) holder).btnQuiz.setOnClickListener(v -> {
                if (listener != null) listener.onActionClicked("quiz");
            });
        }
        else if (holder instanceof AiDetailedViewHolder) {
            AiDetailedViewHolder detailed = (AiDetailedViewHolder) holder;
            detailed.tvDetailedAnswer.setText(RichTextFormatter.format(message.getText()));
            detailed.simplify.setOnClickListener(v -> listener.onActionClicked("simplify"));
            detailed.alternative.setOnClickListener(v -> listener.onActionClicked("alternative"));
            detailed.summary.setOnClickListener(v -> listener.onActionClicked("summary"));
            detailed.mistakes.setOnClickListener(v -> listener.onActionClicked("mistakes"));
            detailed.quiz.setOnClickListener(v -> listener.onActionClicked("quiz"));
        }
        else if (holder instanceof AiQuizViewHolder) {
            bindQuiz((AiQuizViewHolder) holder, message);
        }
        else if (holder instanceof UserImageViewHolder) {
            if (message.getImageBitmap() != null) {
                ((UserImageViewHolder) holder).ivImage.setImageBitmap(message.getImageBitmap());
            }
        }
        else if (holder instanceof AiGreetingViewHolder) {
            AiGreetingViewHolder greetingHolder = (AiGreetingViewHolder) holder;
            
            greetingHolder.btnCamera.setOnClickListener(v -> {
                if (listener != null) listener.onCameraClicked();
            });
            
            greetingHolder.btnType.setOnClickListener(v -> {
                if (listener != null) listener.onTypeClicked();
            });
            
            greetingHolder.btnSuggest.setOnClickListener(v -> {
                if (listener != null) listener.onSuggestionClicked("Cho mình xin gợi ý bài tập hôm nay nhé");
            });

            greetingHolder.chip1.setOnClickListener(v -> {
                if (listener != null) listener.onSuggestionClicked("Giải phương trình");
            });
            
            greetingHolder.chip2.setOnClickListener(v -> {
                if (listener != null) listener.onSuggestionClicked("Giải thích định luật Newton");
            });
            
            greetingHolder.chip3.setOnClickListener(v -> {
                if (listener != null) listener.onSuggestionClicked("Tóm tắt bài Sinh");
            });
        }
    }

    private void bindQuiz(AiQuizViewHolder holder, ChatMessage message) {
        List<QuizQuestion> questions = message.getQuizQuestions();
        holder.container.removeAllViews();
        holder.result.setVisibility(View.GONE);
        if (questions == null || questions.isEmpty()) {
            holder.title.setText("Không thể tạo quiz");
            holder.check.setEnabled(false);
            return;
        }
        holder.check.setEnabled(true);
        holder.title.setText("Kiểm tra nhanh • " + questions.size() + " câu");
        Context context = holder.itemView.getContext();
        int teal = context.getColor(R.color.brand_teal);
        int navy = context.getColor(R.color.brand_text);
        int muted = context.getColor(R.color.brand_text_muted);

        for (int questionIndex = 0; questionIndex < questions.size(); questionIndex++) {
            QuizQuestion quiz = questions.get(questionIndex);
            LinearLayout block = new LinearLayout(context);
            block.setOrientation(LinearLayout.VERTICAL);
            block.setPadding(dp(context, 10), dp(context, 10), dp(context, 10), dp(context, 8));
            android.graphics.drawable.GradientDrawable background = new android.graphics.drawable.GradientDrawable();
            background.setColor(context.getColor(R.color.brand_surface_teal));
            background.setCornerRadius(dp(context, 13));
            block.setBackground(background);
            LinearLayout.LayoutParams blockParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            blockParams.topMargin = dp(context, 7);
            holder.container.addView(block, blockParams);

            TextView question = new TextView(context);
            question.setText(RichTextFormatter.format((questionIndex + 1) + ". " + quiz.getQuestion()));
            question.setTextColor(navy);
            question.setTextSize(14);
            question.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            block.addView(question);

            List<CheckBox> boxes = new java.util.ArrayList<>();
            for (int optionIndex = 0; optionIndex < quiz.getOptions().size(); optionIndex++) {
                CheckBox option = new CheckBox(context);
                String prefix = String.valueOf((char) ('A' + optionIndex));
                option.setText(RichTextFormatter.format(prefix + ". " + quiz.getOptions().get(optionIndex)));
                option.setTextColor(muted);
                option.setTextSize(13);
                option.setPadding(0, dp(context, 2), 0, dp(context, 2));
                option.setButtonTintList(new ColorStateList(
                        new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                        new int[]{teal, muted}));
                option.setChecked(quiz.getSelectedIndex() == optionIndex);
                boxes.add(option);
                block.addView(option, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            for (int optionIndex = 0; optionIndex < boxes.size(); optionIndex++) {
                final int selected = optionIndex;
                boxes.get(optionIndex).setOnClickListener(v -> {
                    CheckBox clicked = (CheckBox) v;
                    if (clicked.isChecked()) {
                        quiz.setSelectedIndex(selected);
                        for (int index = 0; index < boxes.size(); index++) {
                            if (index != selected) boxes.get(index).setChecked(false);
                        }
                    } else if (quiz.getSelectedIndex() == selected) {
                        quiz.setSelectedIndex(-1);
                    }
                });
            }
        }

        holder.check.setOnClickListener(v -> {
            int correct = 0;
            int unanswered = 0;
            StringBuilder details = new StringBuilder();
            for (int index = 0; index < questions.size(); index++) {
                QuizQuestion quiz = questions.get(index);
                if (quiz.getSelectedIndex() < 0) unanswered++;
                else if (quiz.getSelectedIndex() == quiz.getCorrectIndex()) correct++;
                else {
                    details.append("\n\n**Câu ").append(index + 1).append(":** ")
                            .append(quiz.getExplanation() == null ? "Đáp án đúng là "
                                    + (char) ('A' + quiz.getCorrectIndex()) + "." : quiz.getExplanation());
                }
            }
            String summary = "**Kết quả: " + correct + "/" + questions.size() + " câu đúng**";
            if (unanswered > 0) summary += "\nChưa chọn: " + unanswered + " câu.";
            holder.result.setText(RichTextFormatter.format(summary + details));
            holder.result.setVisibility(View.VISIBLE);
        });
    }

    private static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolders
    static class UserTextViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        UserTextViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvUserMessage);
            tvTime = itemView.findViewById(R.id.tvUserTime);
        }
    }

    static class UserImageViewHolder extends RecyclerView.ViewHolder {
        android.widget.ImageView ivImage;
        UserImageViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivUserImage);
        }
    }

    static class AiLoadingViewHolder extends RecyclerView.ViewHolder {
        AiLoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class AiShortViewHolder extends RecyclerView.ViewHolder {
        TextView tvAnswer, btnStepByStep, btnQuiz;
        AiShortViewHolder(View itemView) {
            super(itemView);
            tvAnswer = itemView.findViewById(R.id.tvAiShortAnswer);
            btnStepByStep = itemView.findViewById(R.id.btnStepByStep);
            btnQuiz = itemView.findViewById(R.id.btnShortQuiz);
        }
    }

    static class AiDetailedViewHolder extends RecyclerView.ViewHolder {
        TextView tvDetailedAnswer;
        View simplify, alternative, summary, mistakes, quiz;
        AiDetailedViewHolder(View itemView) {
            super(itemView);
            tvDetailedAnswer = itemView.findViewById(R.id.tvAiDetailedAnswer);
            simplify=itemView.findViewById(R.id.btnSimplify); alternative=itemView.findViewById(R.id.btnAlternative);
            summary=itemView.findViewById(R.id.btnSummary); mistakes=itemView.findViewById(R.id.btnMistakes); quiz=itemView.findViewById(R.id.btnQuiz);
        }
    }

    static class AiQuizViewHolder extends RecyclerView.ViewHolder {
        TextView title, result;
        LinearLayout container;
        View check;
        AiQuizViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvChatQuizTitle);
            result = itemView.findViewById(R.id.tvChatQuizResult);
            container = itemView.findViewById(R.id.quizQuestionsContainer);
            check = itemView.findViewById(R.id.btnCheckChatQuiz);
        }
    }

    static class AiGreetingViewHolder extends RecyclerView.ViewHolder {
        View btnCamera, btnType, btnSuggest;
        TextView chip1, chip2, chip3;
        
        AiGreetingViewHolder(View itemView) {
            super(itemView);
            btnCamera = itemView.findViewById(R.id.btnGreetingCamera);
            btnType = itemView.findViewById(R.id.btnGreetingType);
            btnSuggest = itemView.findViewById(R.id.btnGreetingSuggest);
            
            chip1 = itemView.findViewById(R.id.chipSuggest1);
            chip2 = itemView.findViewById(R.id.chipSuggest2);
            chip3 = itemView.findViewById(R.id.chipSuggest3);
        }
    }
}
