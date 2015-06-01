package com.kevin.zhihudaily.ui.newactivities;

import java.util.ArrayList;
import java.util.Date;

import com.baidu.mobstat.StatService;
import com.kevin.zhihudaily.R;
import com.kevin.zhihudaily.ZhihuDailyApplication;
import com.kevin.zhihudaily.common.Constants;
import com.kevin.zhihudaily.common.EventBus;
import com.kevin.zhihudaily.model.DailyNewsModel;
import com.kevin.zhihudaily.model.NewsModel;
import com.kevin.zhihudaily.provider.DataBaseManager;
import com.kevin.zhihudaily.provider.DataService;
import com.kevin.zhihudaily.ui.activities.DetailActivity;
import com.kevin.zhihudaily.ui.adapters.NewsListAdapter;
import com.kevin.zhihudaily.utils.DebugLog;
import com.kevin.zhihudaily.utils.Utils;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.halfbit.tinybus.Subscribe;

public class MainActivity2 extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener, NewsListAdapter.OnItemClickListener {

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.rv_list)
    RecyclerView rvList;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeLayout;

    private NewsListAdapter newsListAdapter;

    /**
     * A mark for reset all list data
     */
    private boolean mIsResetList = false;
    private Date mTodayDate;
    private String mTodayDateString;
    private String mIndexDate;
    private int preDays = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main_activity2;
    }

    @Override
    protected void handleSavedInstanceState(Bundle savedInstanceState) {

    }

    @Override
    protected void handleExtras(Bundle extras) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        DebugLog.e("==onStart");
        EventBus.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        DebugLog.e("==onStop");
        EventBus.getInstance().unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugLog.e("==onResume");
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DebugLog.e("==onPause");
        StatService.onPause(this);
    }

    @Override
    protected void initViews() {
        ButterKnife.inject(this);

        setupToolbar();
        setupNavigationDrawer();
        setupSwipeLayout();
        setupListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //        //noinspection SimplifiableIfStatement
        //        if (id == R.id.action_settings) {
        //            return true;
        //        }
        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_ab_drawer);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                            return true;
                        }
                    });
        }
    }

    private void setupSwipeLayout() {
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupListView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(linearLayoutManager);

        newsListAdapter = new NewsListAdapter(this);
        newsListAdapter.setOnItemClickListener(this);
        rvList.setAdapter(newsListAdapter);
        rvList.setOnScrollListener(mOnScrollListener);

    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
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
                //                DebugLog.d("childcount = " + childCount + "  lastposition = " + lastPosition + "
                // itemSize = " + itemSize);
                if (lastPosition == (itemSize - 1)) {
                    requestNextDayNews();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            NewsListAdapter.ListItem item = (NewsListAdapter.ListItem) ((NewsListAdapter) recyclerView.getAdapter())
                    .getItemByPosition(position);
            if (item != null) {
                DebugLog.d("== item = " + item.getSection() + "  type=" + item.getType() + "  position = " + position);
                String dateTitle = item.getSection();
                //mToolbar.setTitle(dateTitle + "");
            }
        }
    };

    @Override
    public void onItemClick(View view, NewsListAdapter.ListItem item) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Constants.EXTRA_NEWS_NUM, item.getSectionSize());
        intent.putExtra(Constants.EXTRA_NEWS_INDEX, item.getIndexOfDay());
        intent.putExtra(Constants.EXTRA_NEWS_DATE, item.getDate());
        intent.putExtra(Constants.EXTRA_DAILY_NEWS_MODEL, newsListAdapter.getDailyNewsModelByDate(item.getDate()));
        DebugLog.d("==index=" + item.getIndexOfDay() + "==pos=" + item.getIndexOfDay());

        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onRefresh() {
        mIsResetList = true;
        requestNewsList(mTodayDateString, true);
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
            if (isToday) {
                readLastestNewsFromDB();
            } else {
                date = Utils.getPreDateString(date, "yyyyMMdd");
                DebugLog.d("==PreDate==" + date);
                readNewsFromDBByDate(date);
            }
        }
    }

    private void requestNextDayNews() {
        String date = mIndexDate;

        DebugLog.d("==preDate==" + date);
        requestNewsList(date, false);
        preDays++;
    }

    private void readLastestNewsFromDB() {
        // Read db data
        Intent intent = new Intent(this, DataService.class);
        intent.setAction(Constants.Action.ACTION_READ_LASTEST_NEWS.toString());
        // intent.putExtra(Constants.INTENT_NEWS_DATE, date);
        startService(intent);
    }

    private void readNewsFromDBByDate(String date) {
        // Read db data
        Intent intent = new Intent(this, DataService.class);
        intent.setAction(Constants.Action.ACTION_READ_DAILY_NEWS.toString());
        intent.putExtra(Constants.EXTRA_NEWS_DATE, date);
        startService(intent);
    }

    private void writeDailyModetoDB(String date, DailyNewsModel model) {
        Intent intent = new Intent(ZhihuDailyApplication.getInstance().getApplicationContext(), DataService.class);
        intent.setAction(Constants.Action.ACTION_START_OFFLINE_DOWNLOAD.toString());
        intent.putExtra(Constants.EXTRA_NEWS_DATE, date);
        intent.putExtra(Constants.EXTRA_DAILY_NEWS_MODEL, model);
        ZhihuDailyApplication.getInstance().getApplicationContext().startService(intent);

        // Show notification
        //showNotification();
    }

    @Subscribe
    public void onDailyNewsModel(DailyNewsModel model) {
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

        if (DataBaseManager.getInstance().checkDataExpire(newTimeStamp) >= 0) {
            // Write to db
            if (ZhihuDailyApplication.sNetworkType == ConnectivityManager.TYPE_WIFI) {
                writeDailyModetoDB(date, model);
            }

            // Auto start data cache
            //            new Handler().postDelayed(new Runnable() {
            //                @Override
            //                public void run() {
            //                    if (ZhihuDailyApplication.sNetworkType == ConnectivityManager.TYPE_WIFI) {
            //                        startDataCache(mTodayDateString);
            //                    }
            //                }
            //            }, 2000);
        }
    }
}
