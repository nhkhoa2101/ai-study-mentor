package com.example.aistudymentor.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.aistudymentor.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OnboardingHostFragment extends Fragment {
    private ViewPager2 viewPager;
    private MaterialButton next;
    private TextView skip;
    private LinearLayout indicators;
    private List<Fragment> pages;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_host, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        next = view.findViewById(R.id.btnNext);
        skip = view.findViewById(R.id.btnSkip);
        indicators = view.findViewById(R.id.dotsIndicator);

        pages = new ArrayList<>();
        pages.add(OnboardingInfoFragment.newInstance("Học tập có định hướng",
                "Đặt câu hỏi và nhận lời giải phù hợp với trình độ của bạn.", R.drawable.ic_welcome));
        pages.add(OnboardingInfoFragment.newInstance("Gia sư AI luôn sẵn sàng",
                "Hỗ trợ câu hỏi văn bản và hình ảnh trên nhiều môn học.", R.drawable.ic_ai_text));
        pages.add(OnboardingInfoFragment.newInstance("Cá nhân hóa cách học",
                "Chọn môn học, trình độ và phong cách giải thích bạn mong muốn.", R.drawable.ic_path));
        pages.add(OnboardingInfoFragment.newInstance("Theo dõi tiến độ thật",
                "Câu hỏi, quiz, XP và tiến độ từng môn được cập nhật tự động.", R.drawable.ic_progress));
        pages.add(OnboardingInfoFragment.newInstance("Luyện tập hiệu quả",
                "Ôn lịch sử, đánh dấu câu trả lời và tạo quiz cá nhân hóa.", R.drawable.ic_game));
        pages.add(OnboardingInfoFragment.newInstance("Dữ liệu được bảo vệ",
                "Mật khẩu được băm và nội dung học tập được mã hóa cục bộ.", R.drawable.ic_security));
        pages.add(new OnboardingSetupFragment());
        pages.add(new OnboardingFinishFragment());

        viewPager.setAdapter(new OnboardingPagerAdapter(this, pages));
        createIndicators();
        selectIndicator(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                selectIndicator(position);
                boolean last = position == pages.size() - 1;
                next.setText(last ? "Bắt đầu ngay" : "Tiếp tục");
                skip.setVisibility(last ? View.INVISIBLE : View.VISIBLE);
            }
        });
        next.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < pages.size() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                        .edit().putBoolean("OnboardingCompleted", true).apply();
                Navigation.findNavController(view).navigate(R.id.action_onboardingHost_to_authWelcome);
            }
        });
        skip.setOnClickListener(v -> viewPager.setCurrentItem(6));
        return view;
    }

    private void createIndicators() {
        indicators.removeAllViews();
        for (int i = 0; i < pages.size(); i++) {
            View dot = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(8), dp(8));
            params.setMargins(dp(4), 0, dp(4), 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.bg_dot_inactive);
            indicators.addView(dot);
        }
    }

    private void selectIndicator(int selected) {
        for (int i = 0; i < indicators.getChildCount(); i++) {
            View dot = indicators.getChildAt(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dp(i == selected ? 22 : 8), dp(8));
            params.setMargins(dp(4), 0, dp(4), 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == selected
                    ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
