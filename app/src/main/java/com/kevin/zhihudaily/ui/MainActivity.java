package com.kevin.zhihudaily.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.baidu.mobstat.StatService;
import com.halfbit.tinybus.Subscribe;
import com.kevin.zhihudaily.Constants;
import com.kevin.zhihudaily.DebugLog;
import com.kevin.zhihudaily.EventBus;
import com.kevin.zhihudaily.R;
import com.kevin.zhihudaily.Utils;
import com.kevin.zhihudaily.db.DataService;
import com.kevin.zhihudaily.model.DailyNewsModel;
import com.kevin.zhihudaily.model.NewsModel;
import com.kevin.zhihudaily.view.DrawerArrowDrawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener, NewsListAdapter.OnItemClickListener {
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    @InjectView(R.id.rv_list)
    RecyclerView rvList;

    @InjectView(R.id.drawer)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeLayout;

    Toolbar mToolbar;
    ActionBarDrawerToggle mDrawerToggle;
    MenuItem mMenuItem;

    private NewsListAdapter newsListAdapter;

    /**
     * A mark for reset all list data
     */
    private boolean mIsResetList = false;
    private Date mTodayDate;
    private String mTodayDateString;
    private String mIndexDate;
    private int preDays = 0;

    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init views
        initViews();
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_main;
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
        StatService.onResume(this);
    }

    @Override protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                mDrawerLayout.closeDrawers();
            } else {
                mDrawerLayout.openDrawer(Gravity.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        ButterKnife.inject(this);

        mToolbar = getToolbar();
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        setupToolbar();
        setupDrawer();
        setupList();
    }

    private void setupToolbar() {
        setToolbarIcon(R.drawable.ic_ab_drawer);
    }

    private void setupDrawer() {
        final Resources resources = getResources();
        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.white));
        setToolbarIcon(drawerArrowDrawable);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

//        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
        //            @Override public void onDrawerSlide(View drawerView, float slideOffset) {
        //                offset = slideOffset;
        //                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
        //                if (slideOffset >= .995) {
        //                    flipped = true;
        //                    drawerArrowDrawable.setFlip(flipped);
        //                } else if (slideOffset <= .005) {
        //                    flipped = false;
        //                    drawerArrowDrawable.setFlip(flipped);
        //                }
        //                drawerArrowDrawable.setParameter(offset);
        //            }
        //        });
    }

    private void setupList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //        rvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        rvList.setLayoutManager(linearLayoutManager);

        newsListAdapter = new NewsListAdapter(this);
        newsListAdapter.setOnItemClickListener(this);
        rvList.setAdapter(newsListAdapter);
        rvList.setOnScrollListener(mOnScrollListener);

        startIntroAnimation();
        //        requestLastestNews();
    }

    private void requestLastestNews() {
        Calendar calendar = Calendar.getInstance();
        mTodayDate = calendar.getTime();

        // request latest news
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        String todayDate = formatter.format(mTodayDate);
        mIndexDate = todayDate;
        mTodayDateString = todayDate;
        requestNewsList(todayDate, true);
    }

    private void requestNewsList(String date, boolean isToday) {
        if (Utils.isNetworkConnected(this)) {
            DebugLog.d("==NET-Mode==" + date);
            if (isToday) {
                Intent intent = new Intent(this, DataService.class);
                intent.setAction(Constants.Action.ACTION_GET_TODAY_NEWS.toString());
                startService(intent);
            } else {
                Intent intent = new Intent(this, DataService.class);
                intent.setAction(Constants.Action.ACTION_GET_DAILY_NEWS.toString());
                intent.putExtra(Constants.EXTRA_NEWS_DATE, date);
                startService(intent);
            }

        } else {
            DebugLog.d("==DB-Mode==" + date);
            //            if (isToday) {
            //                readLastestNewsFromDB();
            //            } else {
            //                date = getPreDateString(date, "yyyyMMdd");
            //                DebugLog.d("==PreDate==" + date);
            //                readNewsFromDBByDate(date);
            //            }
        }
    }

    private void requestNextDayNews() {
        String date = mIndexDate;

        DebugLog.d("==preDate==" + date);
        requestNewsList(date, false);
        preDays++;
    }

    @Subscribe public void onNewsReadyEvent(DailyNewsModel model) {
        // Set SwipeRefreshLayout to stop
        mSwipeLayout.setRefreshing(false);

        if (model == null) {
            return;
        }

        // update index date
        mIndexDate = model.getDate();
        DebugLog.d("==Index date==" + mIndexDate);
        updateNewsList(model);
    }

    private void updateNewsList(DailyNewsModel model) {
        // Add date to each news model
        String date = model.getDate();
        for (NewsModel news : model.getNewsList()) {
            news.setDate(date);
        }
        ArrayList<NewsModel> hotList = (ArrayList<NewsModel>) model.getTopStories();
        if (hotList != null) {
            for (NewsModel news : hotList) {
                news.setDate(date);
            }
        }

        if (mIsResetList) {
            ArrayList<DailyNewsModel> list = new ArrayList<DailyNewsModel>();
            list.add(model);
            newsListAdapter.updateAllList(list);

            ArrayList<NewsModel> newslist = new ArrayList<NewsModel>();
            newslist.addAll(model.getTopStories());
            //            mFlowAdapter.updateAllList(newslist);

            mIsResetList = false;
        } else {
            newsListAdapter.updateList(model);

            //            mFlowAdapter.updateList(model.getTopStories());
        }

        int newTimeStamp = Integer.valueOf(model.getNewsList().get(0).getGa_prefix());

        //        if (DataBaseManager.getInstance().checkDataExpire(newTimeStamp) >= 0) {
        //            // Write to db
        //            // Add to cache and write to db
        //            // DataCache.getInstance().addDailyCache(model.getDate(), model);
        //            // Intent intent = new Intent(getActivity(), DataService.class);
        //            // intent.putExtra(Constants.EXTRA_CACHE_ID, model.getDate());
        //            // intent.setAction(Constants.Action.ACTION_WRITE_DAILY_NEWS.toString());
        //            // getActivity().startService(intent);
        //
        //            // Auto start data cache
        //            new Handler().postDelayed(new Runnable() {
        //                @Override
        //                public void run() {
        //                    if (ZhihuDailyApplication.sNetworkType == ConnectivityManager.TYPE_WIFI) {
        //                        startDataCache(mTodayDateString);
        //                    }
        //                }
        //            }, 2000);
        //        }
    }

    @Override public void onRefresh() {
        //        new Handler().postDelayed(new Runnable() {
        //            @Override
        //            public void run() {
        //                mSwipeLayout.setRefreshing(false);
        //            }
        //        }, 3000);
        mIsResetList = true;
        requestNewsList(mTodayDateString, true);
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                int lastPosition;
                final int childCount = recyclerView.getChildCount();
                final int itemSize = recyclerView.getAdapter().getItemCount();
                if (childCount == 0) {
                    lastPosition = 0;
                } else {
                    lastPosition = recyclerView.getChildPosition(recyclerView.getChildAt(childCount - 1));
                }
                //                DebugLog.d(
                //                        "childcount = " + childCount + "  lastposition = " + lastPosition + "  itemSize = " + itemSize);
                if (lastPosition == (itemSize - 1)) {
                    requestNextDayNews();
                }
            }
        }

        @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

        }
    };

    @Override public void onItemClick(View view, NewsListAdapter.ListItem item) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Constants.EXTRA_NEWS_NUM, item.getSectionSize());
        intent.putExtra(Constants.EXTRA_NEWS_INDEX, item.getIndexOfDay());
        intent.putExtra(Constants.EXTRA_NEWS_DATE, item.getDate());
        intent.putExtra(Constants.EXTRA_DAILY_NEWS_MODEL, newsListAdapter.getDailyNewsModelByDate(item.getDate()));
        DebugLog.d("==index=" + item.getIndexOfDay() + "==pos=" + item.getIndexOfDay());

        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void startIntroAnimation() {
        //        btnCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionbarSize = Utils.dpToPx(56);
        mToolbar.setTranslationY(-actionbarSize);
        //        ivLogo.setTranslationY(-actionbarSize);
        //        inboxMenuItem.getActionView().setTranslationY(-actionbarSize);

        mToolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
        //        ivLogo.animate()
        //                .translationY(0)
        //                .setDuration(ANIM_DURATION_TOOLBAR)
        //                .setStartDelay(400);
        //        inboxMenuItem.getActionView().animate()
        //                .translationY(0)
        //                .setDuration(ANIM_DURATION_TOOLBAR)
        //                .setStartDelay(500)
        //                .setListener(new AnimatorListenerAdapter() {
        //                    @Override
        //                    public void onAnimationEnd(Animator animation) {
        //                        startContentAnimation();
        //                    }
        //                })
        //                .start();
    }

    private void startContentAnimation() {
        //        btnCreate.animate()
        //                .translationY(0)
        //                .setInterpolator(new OvershootInterpolator(1.f))
        //                .setStartDelay(300)
        //                .setDuration(ANIM_DURATION_FAB)
        //                .start();
        requestLastestNews();
    }
}
