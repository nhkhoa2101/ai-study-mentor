package com.example.aistudymentor.ui.formatting;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RichTextFormatter {
    private static final int MAX_REPLACEMENTS = 200;

    private RichTextFormatter() {}

    public static CharSequence format(String source) {
        String fallback = source == null ? "" : source;
        try {
            String safe = normalizeMath(fallback);
            SpannableStringBuilder value = new SpannableStringBuilder(safe);
            applyDelimited(value, Pattern.compile("\\*\\*(.+?)\\*\\*", Pattern.DOTALL), 1);
            applyDelimited(value, Pattern.compile("__(.+?)__", Pattern.DOTALL), 1);
            applyItalic(value, Pattern.compile("(?<!\\*)\\*([^*\\n]+?)\\*(?!\\*)"));
            applyItalic(value, Pattern.compile("(?<!_)_([^_\\n]+?)_(?!_)"));
            applyCode(value);
            applyScript(value, Pattern.compile("([A-Za-z0-9)])\\^\\{?([+\\-]?[A-Za-z0-9]+)\\}?"), true);
            applyScript(value, Pattern.compile("([A-Za-z0-9)])_\\{?([+\\-]?[A-Za-z0-9]+)\\}?"), false);
            applyHeadings(value);
            return value;
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    static String normalizeMath(String value) {
        return value.replace("\\times", "×").replace("\\cdot", "·")
                .replace("\\div", "÷").replace("\\pm", "±")
                .replace("\\leq", "≤").replace("\\le", "≤")
                .replace("\\geq", "≥").replace("\\ge", "≥")
                .replace("\\neq", "≠").replace("\\approx", "≈")
                .replace("\\infty", "∞").replace("\\pi", "π")
                .replace("\\theta", "θ").replace("\\alpha", "α")
                .replace("\\beta", "β").replace("\\Delta", "Δ")
                .replace("\\sum", "∑").replace("\\int", "∫")
                .replaceAll("\\\\sqrt\\{([^{}]+)\\}", "√($1)")
                .replaceAll("\\\\frac\\{([^{}]+)\\}\\{([^{}]+)\\}", "($1)/($2)")
                .replace("\\(", "").replace("\\)", "")
                .replace("\\[", "").replace("\\]", "")
                .replace("$", "");
    }

    private static void applyDelimited(SpannableStringBuilder value, Pattern pattern, int group) {
        for (int count = 0; count < MAX_REPLACEMENTS; count++) {
            Matcher matcher = pattern.matcher(value.toString());
            if (!matcher.find()) return;
            String inner = matcher.group(group);
            int start = matcher.start();
            value.replace(start, matcher.end(), inner);
            value.setSpan(new StyleSpan(Typeface.BOLD), start, start + inner.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static void applyItalic(SpannableStringBuilder value, Pattern pattern) {
        for (int count = 0; count < MAX_REPLACEMENTS; count++) {
            Matcher matcher = pattern.matcher(value.toString());
            if (!matcher.find()) return;
            String inner = matcher.group(1);
            int start = matcher.start();
            value.replace(start, matcher.end(), inner);
            value.setSpan(new StyleSpan(Typeface.ITALIC), start, start + inner.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static void applyCode(SpannableStringBuilder value) {
        Pattern pattern = Pattern.compile("`([^`\\n]+?)`");
        for (int count = 0; count < MAX_REPLACEMENTS; count++) {
            Matcher matcher = pattern.matcher(value.toString());
            if (!matcher.find()) return;
            String inner = matcher.group(1);
            int start = matcher.start();
            value.replace(start, matcher.end(), inner);
            value.setSpan(new TypefaceSpan("monospace"), start, start + inner.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            value.setSpan(new BackgroundColorSpan(Color.rgb(232, 244, 242)), start, start + inner.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static void applyScript(SpannableStringBuilder value, Pattern pattern, boolean superscript) {
        for (int count = 0; count < MAX_REPLACEMENTS; count++) {
            Matcher matcher = pattern.matcher(value.toString());
            if (!matcher.find()) return;
            String replacement = matcher.group(1) + matcher.group(2);
            int start = matcher.start();
            int scriptStart = start + matcher.group(1).length();
            value.replace(start, matcher.end(), replacement);
            Object span = superscript ? new SuperscriptSpan() : new SubscriptSpan();
            value.setSpan(span, scriptStart, start + replacement.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static void applyHeadings(SpannableStringBuilder value) {
        Pattern pattern = Pattern.compile("(?m)^#{1,3}\\s+(.+)$");
        for (int count = 0; count < MAX_REPLACEMENTS; count++) {
            Matcher matcher = pattern.matcher(value.toString());
            if (!matcher.find()) return;
            String heading = matcher.group(1);
            int start = matcher.start();
            value.replace(start, matcher.end(), heading);
            value.setSpan(new StyleSpan(Typeface.BOLD), start, start + heading.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
