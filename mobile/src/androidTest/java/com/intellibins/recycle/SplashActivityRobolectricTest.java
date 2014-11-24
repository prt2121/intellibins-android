package com.intellibins.recycle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by prt2121 on 11/23/14.
 */
//@Config(emulateSdk = 18, manifest = "mobile/src/main/AndroidManifest.xml")
//@RunWith(RobolectricGradleTestRunner.class)
@RunWith(RobolectricGradleTestRunner.class)
public class SplashActivityRobolectricTest {

    private Activity mActivity;

    @Before
    public void setup() {
        mActivity = Robolectric.setupActivity(SplashActivity.class);
    }

    @Test
    public void testActivityFound() {
        assertNotNull(mActivity);
    }

    @Test
    public void testSplashScreenBackgroundColor() throws Exception {
        int color = ((ColorDrawable) mActivity.findViewById(R.id.layout_splash).getBackground())
                .getColor();
        int primary = mActivity.getResources().getColor(R.color.primary);
        assertThat(color, equalTo(primary));
    }
}
