package com.kevin.zhihudaily.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import com.kevin.zhihudaily.R;

/**
 * Created by chenchao04 on 2014-12-01.
 */
public abstract class BaseActivity extends ActionBarActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(Color.WHITE);
        }
    }

    protected abstract int getLayoutResource();

    protected Toolbar getToolbar() {
        return toolbar;
    }

    protected void setToolbarIcon(int resId) {
        if (toolbar != null) {
            toolbar.setNavigationIcon(resId);
        }
    }



    protected CharSequence getToolbarTitle() {
        if (toolbar != null) {
            return toolbar.getTitle();
        }
        return null;
    }

    protected void setToolbarBackgroundColor(int color) {
        if (toolbar != null) {
            toolbar.setBackgroundColor(color);
        }
    }
}
