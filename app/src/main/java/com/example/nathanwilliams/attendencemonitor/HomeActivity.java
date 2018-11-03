package com.example.nathanwilliams.attendencemonitor;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity
{
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private SliderAdapter sliderAdapter;
    private TextView[] mDots;

    private Button signOutBtn;
    private Button nextBtn,prevBtn;

    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dot_layout);

        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        nextBtn = findViewById(R.id.nextBtn);
        prevBtn = findViewById(R.id.prevBtn);
        /*signOutBtn = findViewById(R.id.home_signout);

        signOutBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(HomeActivity.this, SignInActivity.class);
                startActivity(logoutIntent);
                finish();
            }
        });*/
    }

    public void addDotsIndicator(int pos)
    {
        mDots = new TextView[sliderAdapter.slide_headings.length];
        mDotLayout.removeAllViews();
        //mDots = new TextView[3];

        for(int i = 0; i <mDots.length; i++)
        {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            mDotLayout.addView(mDots[i]);

        }

        if(mDots.length > 0)
        {
            mDots[pos].setTextColor(getResources().getColor(R.color.colorWhite));
        }

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i)
        {

            addDotsIndicator(i);
            currentPage = i;

            if(i == 0)
            {
                nextBtn.setEnabled(true);

                prevBtn.setEnabled(false);
                prevBtn.setVisibility(View.INVISIBLE);
                nextBtn.setVisibility(View.VISIBLE);

                nextBtn.setText("next");
                prevBtn.setText("");

            }
            else if(i == mDots.length - 1)
            {
                nextBtn.setEnabled(false);

                prevBtn.setEnabled(true);
                nextBtn.setVisibility(View.INVISIBLE);
                prevBtn.setVisibility(View.VISIBLE);

                nextBtn.setText("");
                prevBtn.setText("back");
            }
            else
            {
                nextBtn.setEnabled(true);
                prevBtn.setEnabled(true);

                nextBtn.setVisibility(View.VISIBLE);
                prevBtn.setVisibility(View.VISIBLE);

                nextBtn.setText("next");
                prevBtn.setText("back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}
