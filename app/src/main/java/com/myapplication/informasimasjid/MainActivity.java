package com.myapplication.informasimasjid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.google.android.material.button.MaterialButton;
import com.myapplication.informasimasjid.library.Session;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   private OnboardingAdapter onboardingAdapter;
   private LinearLayout layoutOnboardingIndicators;
   private MaterialButton buttonOnboardingAction;

    Session sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefManager = new Session(this);

        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        buttonOnboardingAction = findViewById(R.id.buttonOnboardingAction);

        if(sharedPrefManager.getSes_boarding()){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        } else {
            setupOnboardingItems();
            ViewPager2 onboardingViewPager = findViewById(R.id.onboardingViewPager);
            onboardingViewPager.setAdapter(onboardingAdapter);

            setupOnboardingIndicators();
            setCurrentOnboardingIndicator(0);

            onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    setCurrentOnboardingIndicator(position);
                }
            });

            buttonOnboardingAction.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()){
                        onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem()+1);
                    } else {
                        sharedPrefManager.saveBoarding(Session.Ses_boarding, true);
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                }
            });

        }




    }

    private void setupOnboardingItems(){
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem screen1 = new OnboardingItem();
        screen1.setTitle("Assalamu'alaikum.");
        screen1.setDescription("Selamat datang di App Informasi Masjid Baitul Inayah");
        screen1.setImage(R.drawable.masjid1);

        OnboardingItem screen2 = new OnboardingItem();
        screen2.setTitle("Informasi Keuangan.");
        screen2.setDescription("Dapat juga melihat keuangan di masjid ini");
        screen2.setImage(R.drawable.masjid2);

        OnboardingItem screen3 = new OnboardingItem();
        screen3.setTitle("Informasi TPA dan Kajian.");
        screen3.setDescription("Informasi seputar TPA dan kajian dapat dilihat disini juga");
        screen3.setImage(R.drawable.masjid3);

        onboardingItems.add(screen1);
        onboardingItems.add(screen2);
        onboardingItems.add(screen3);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }

    private void setupOnboardingIndicators(){
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for(int i = 0; i < indicators.length; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int index){
        int childCount = layoutOnboardingIndicators.getChildCount();

        for(int i = 0; i < childCount; i++){
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if(i == index){
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive)
                );
            }
        }
        if( index == onboardingAdapter.getItemCount() - 1) {
            buttonOnboardingAction.setText("Start");
        } else {
            buttonOnboardingAction.setText("Next");
        }
    }
}