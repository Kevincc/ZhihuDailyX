package com.kevin.zhihudaily.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.kevin.zhihudaily.Constants;
import com.kevin.zhihudaily.DebugLog;
import com.kevin.zhihudaily.EventBus;
import com.kevin.zhihudaily.R;
import com.kevin.zhihudaily.model.DailyNewsModel;
import com.kevin.zhihudaily.model.NewsModel;
import com.kevin.zhihudaily.view.DepthPageTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchao04 on 2014-12-04.
 */
public class DetailActivity extends BaseActivity implements DetailFragment.OnFragmentInteractionListener {
    private static final String DATE_KEY = "date_key";

    @InjectView(R.id.pager)
    ViewPager mPager;

    private DetailPagerAdapter mAdapter;
    private DailyNewsModel mDailyNewsModel;
    private NewsModel mSelectModel = new NewsModel();

    private String mDateKey;
    private int mNewsNum = 1;
    private int mCurPosition = 0;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    @Override protected int getLayoutResource() {
        return R.layout.activity_detail;
    }

    @Override protected void onStart() {
        super.onStart();
        EventBus.getInstance().register(this);
    }

    @Override protected void onStop() {
        EventBus.getInstance().unregister(this);
        super.onStop();
    }

    @Override protected void onResume() {
        super.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DATE_KEY, mDateKey);
    }

    private void initViews() {
        ButterKnife.inject(this);

        setupViewPager();
    }

    private void handleSavedInstanceState(Bundle savedInstanceState) {
        mDateKey = savedInstanceState.getString(DATE_KEY);
    }

    private void handleExtras(Bundle extras) {
        mDateKey = extras.getString(Constants.EXTRA_NEWS_DATE);
        mNewsNum = extras.getInt(Constants.EXTRA_NEWS_NUM, 1);
        mSelectModel.setId(extras.getInt(Constants.EXTRA_NEWS_ID, -1));
        mSelectModel.setTitle(extras.getString(Constants.EXTRA_NEWS_TITLE));
        mSelectModel.setUrl(extras.getString(Constants.EXTRA_NEWS_URL));
        mSelectModel.setImage_source(extras.getString(Constants.EXTRA_NEWS_IMAGE_SOURCE));
        mSelectModel.setImage(extras.getString(Constants.EXTRA_NEWS_IMAGE_URL));
        mDailyNewsModel = extras.getParcelable(Constants.EXTRA_DAILY_NEWS_MODEL);
        // Read from cache for viewpager data
        if (mDailyNewsModel != null) {
            mNewsNum = mDailyNewsModel.getNewsList().size();
        }
    }

    private void setupViewPager() {
        // Set up ViewPager and backing adapter
        mAdapter = new DetailPagerAdapter(getSupportFragmentManager(), mNewsNum, mDailyNewsModel);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setPageMargin((int) getResources().getDimension(R.dimen.detail_pager_margin));
        mPager.setOffscreenPageLimit(2);
        mPager.setOnPageChangeListener(mPageChangeListener);
        //        mPager.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Set animation
        //        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setPageTransformer(true, new DepthPageTransformer());

        // Set the current item based on the extra passed in to this activity
        int extraCurrentItem = getIntent().getIntExtra(Constants.EXTRA_NEWS_INDEX, -1);

        mCurPosition = extraCurrentItem;
        DebugLog.d("==pageindex==" + mCurPosition);
        if (extraCurrentItem != -1) {
            mPager.setCurrentItem(extraCurrentItem);
        } else {
            ArrayList<NewsModel> list = (ArrayList<NewsModel>) mDailyNewsModel.getNewsList();
            extraCurrentItem = getIndexById(list, mSelectModel.getId());
            //            Log.e(TAG, "==pageindex_new==" + extraCurrentItem);
            if (extraCurrentItem != -1) {
                mPager.setCurrentItem(extraCurrentItem);
            }
        }
    }

    private int getIndexById(List<NewsModel> list, int id) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                index = i;
                return index;
            }
        }
        return index;
    }

    @Override public void onFragmentInteraction(Uri uri) {

    }

    private class DetailPagerAdapter extends FragmentStatePagerAdapter {

        private final int mSize;
        private final DailyNewsModel mDailyNewsModel;

        public DetailPagerAdapter(FragmentManager fm, int size, DailyNewsModel model) {
            super(fm);
            // TODO Auto-generated constructor stub
            mSize = size;
            mDailyNewsModel = model;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mSize;
        }

        @Override
        public Fragment getItem(int position) {
            // TODO Auto-generated method stub
            return DetailFragment.newInstance(mDailyNewsModel.getNewsList().get(position));
        }
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            NewsModel model = mDailyNewsModel.getNewsList().get(position);
            //            String curTitle = getToolbar().getTitle().toString();
            //            if (!curTitle.equals("")) {
            //                getToolbar().setTitle(model.getTitle());
            //                mCurPosition = position;
            //            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // TODO Auto-generated method stub

        }

    };
}
