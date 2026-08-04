package com.example.aistudymentor.security;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class QuestionSafety {
    private static final Pattern REPEATED_CHARACTER = Pattern.compile("(.)\\1{7,}");
    private static final Pattern REPEATED_WORD = Pattern.compile("(?i)(\\b[\\p{L}]{2,}\\b)(?:\\s+\\1){4,}");
    private static final Pattern URL = Pattern.compile("(?i)(https?://|www\\.)");

    private QuestionSafety() {}

    public static String validate(String text) {
        if (text == null || text.trim().isEmpty()) return "Hãy nhập câu hỏi trước.";
        if (text.length() > 1200) return "Câu hỏi quá dài. Giới hạn là 1.200 ký tự.";

        String value = normalize(text);
        if (containsAny(value, "ignore previous instructions", "reveal api key",
                "show system prompt", "bo qua huong dan truoc", "hien api key",
                "hien system prompt")) {
            return "Yêu cầu này không an toàn. Hãy nhập một câu hỏi học tập phù hợp.";
        }
        if (containsAny(value, "how to make a bomb", "cach che tao bom", "huong dan lam bom",
                "kill someone", "cach giet nguoi", "hack password", "danh cap mat khau",
                "steal account", "chiem doat tai khoan", "create ransomware")) {
            return "Nội dung có thể gây nguy hiểm hoặc vi phạm pháp luật nên không được xử lý.";
        }
        if (containsAny(value, "porn", "sex video", "anh khoa than", "noi dung khieu dam")) {
            return "Nội dung không phù hợp với mục đích học tập.";
        }
        if (containsAny(value, "how to commit suicide", "cach tu tu", "cach tu lam hai ban than")) {
            return "Nội dung tự gây hại không được xử lý. Hãy tìm sự hỗ trợ từ người tin cậy hoặc dịch vụ khẩn cấp tại nơi bạn sống.";
        }
        if (REPEATED_CHARACTER.matcher(value).find() || REPEATED_WORD.matcher(value).find()) {
            return "Câu hỏi có dấu hiệu spam hoặc lặp lại quá nhiều.";
        }
        Matcher urls = URL.matcher(value);
        int urlCount = 0;
        while (urls.find()) urlCount++;
        if (urlCount > 1 || containsAny(value, "buy now", "click here", "free money",
                "mua ngay", "bam vao day", "kiem tien nhanh")) {
            return "Nội dung quảng cáo hoặc spam không được chấp nhận.";
        }
        return null;
    }

    private static boolean containsAny(String value, String... terms) {
        for (String term : terms) if (value.contains(term)) return true;
        return false;
    }

    private static String normalize(String value) {
        String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return decomposed.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }
}
