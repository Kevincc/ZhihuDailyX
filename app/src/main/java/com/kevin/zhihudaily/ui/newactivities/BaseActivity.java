package com.kevin.zhihudaily.ui.newactivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by chenchao04 on 2015-06-01.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        // restore saved state
        if (savedInstanceState != null) {
            handleSavedInstanceState(savedInstanceState);
        }

        // handle intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            handleExtras(extras);
        }

        // init views
        initViews();
    }

    protected abstract int getLayoutResource();

    protected abstract void handleSavedInstanceState(Bundle savedInstanceState);

    protected abstract void handleExtras(Bundle extras);

    protected abstract void initViews();
}
