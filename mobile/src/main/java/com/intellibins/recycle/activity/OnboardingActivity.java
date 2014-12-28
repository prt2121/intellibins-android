/*
 * Copyright (c) 2014 Intellibins authors
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *   * Neither the name of The Intern nor the names of its contributors may
 * be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE LISTED COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intellibins.recycle.activity;

import com.intellibins.recycle.R;
import com.intellibins.recycle.view.CirclePageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

public class OnboardingActivity extends ActionBarActivity
        implements ViewPager.OnPageChangeListener {

    private static final int NUM_PAGES = 3;

    private ViewPager mPager;

    private Button mSkipButton;

    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        mPager = (ViewPager) findViewById(R.id.onboardingViewPager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);

        CirclePageIndicator circlePageIndicator =
                (CirclePageIndicator) findViewById(R.id.indicator);
        circlePageIndicator.setViewPager(mPager);
        circlePageIndicator.setOnPageChangeListener(this);

        mSkipButton = (Button) findViewById(R.id.skipButton);
        mSkipButton.setOnClickListener(skipButtonOnClickListener);

        mNextButton = (Button) findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(nextButtonOnClickListener);
    }

    View.OnClickListener skipButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(OnboardingActivity.this, MapsActivity.class);
            OnboardingActivity.this.startActivity(intent);
            OnboardingActivity.this.finish();
        }
    };

    View.OnClickListener nextButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPager.getCurrentItem() + 1 < NUM_PAGES) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                // TODO smoothScroll doesn't work
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        onPageSelected(position);
    }

    @Override
    public void onPageSelected(int position) {
        if (position >= NUM_PAGES - 1) {
            mSkipButton.setVisibility(View.GONE);
            mNextButton.setText("DONE");
            mNextButton.setOnClickListener(skipButtonOnClickListener);
        } else {
            mSkipButton.setVisibility(View.VISIBLE);
            mNextButton.setText("NEXT");
            mNextButton.setOnClickListener(nextButtonOnClickListener);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return OnboardingFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
