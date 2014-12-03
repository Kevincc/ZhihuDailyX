package com.kevin.zhihudaily.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.halfbit.tinybus.Subscribe;
import com.kevin.zhihudaily.Constants;
import com.kevin.zhihudaily.DebugLog;
import com.kevin.zhihudaily.R;
import com.kevin.zhihudaily.Utils;
import com.kevin.zhihudaily.ZhihuDailyApplication;
import com.kevin.zhihudaily.db.DataService;
import com.kevin.zhihudaily.model.DailyNewsModel;
import com.kevin.zhihudaily.model.NewsModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.rv_list)
    RecyclerView rvList;

    @InjectView(R.id.drawer)
    DrawerLayout drawer;

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

        // init views
        initViews();
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override protected void onResume() {
        super.onResume();
        ZhihuDailyApplication.getInstance().getBus().register(this);
    }

    @Override protected void onStop() {
        ZhihuDailyApplication.getInstance().getBus().unregister(this);
        //        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataReadyReceiver);
        super.onStop();
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
            drawer.openDrawer(Gravity.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        ButterKnife.inject(this);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        setupToolbar();
        setupList();
    }

    private void setupToolbar() {
        setToolbarIcon(R.drawable.ic_ab_drawer);
    }

    private void setupList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //        rvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        rvList.setLayoutManager(linearLayoutManager);

        newsListAdapter = new NewsListAdapter(this);
        rvList.setAdapter(newsListAdapter);

        //        mDataReadyReceiver = new DataReadyReceiver();
        //        IntentFilter dataIntentFilter = new IntentFilter(Constants.Action.ACTION_NOTIFY_NEWS_LIST_UI.toString());
        //        dataIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        //        LocalBroadcastManager.getInstance(this).registerReceiver(mDataReadyReceiver, dataIntentFilter);

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

    @Subscribe public void onNewsReadyEvent(DailyNewsModel model) {
        if (model == null) {
            return;
        }

        // update index date
        mIndexDate = model.getDate();
        DebugLog.d("==Index date==" + mIndexDate);
        updateNewsList(model);
    }

    private void updateNewsList(DailyNewsModel model) {

        // Set SwipeRefreshLayout to stop
        //        mSwipeRefreshLayout.setRefreshing(false);

        // Hide Loading footer view
        //        mFooterView.setVisibility(View.GONE);

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
            }
        }, 3000);
    }
}
