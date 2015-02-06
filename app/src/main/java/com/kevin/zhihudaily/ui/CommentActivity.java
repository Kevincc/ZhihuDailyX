package com.kevin.zhihudaily.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.halfbit.tinybus.Subscribe;
import com.kevin.zhihudaily.Constants;
import com.kevin.zhihudaily.DebugLog;
import com.kevin.zhihudaily.EventBus;
import com.kevin.zhihudaily.R;
import com.kevin.zhihudaily.Utils;
import com.kevin.zhihudaily.db.DataService;
import com.kevin.zhihudaily.model.CommentsModel;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CommentActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String NEWS_ID = "news_id";

    Toolbar mToolbar;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.comment_list)
    RecyclerView mListView;

    private CommentListAdapter mListAdpater;

    private int mNewsID;

    private boolean mLongCommentReady = false;
    private boolean mShortCommentReady = false;

    /**
     * A mark for reset all list data
     */
    private boolean mIsResetList = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//        // Hide title text and set home as up
//        getActionBar().setIcon(R.drawable.topbar_icon);
//        getActionBar().setDisplayShowTitleEnabled(true);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff33b5e5")));
//
//        // set action bar title text color to white
//        int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//        TextView title = (TextView) findViewById(titleId);
//        title.setTextColor(this.getResources().getColor(R.color.white));

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

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_comment;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getInstance().unregister(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NEWS_ID, mNewsID);
    }

    private void handleSavedInstanceState(Bundle savedInstanceState) {
        mNewsID = savedInstanceState.getInt(NEWS_ID);
    }

    private void handleExtras(Bundle extras) {
        mNewsID = extras.getInt(Constants.EXTRA_NEWS_ID, -1);
        if (mNewsID == -1) {
            Toast.makeText(this, getString(R.string.error_argument), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        ButterKnife.inject(this);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        setupToolbar();
        setupList();
    }

    private void setupToolbar() {
        mToolbar = getToolbar();
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    }

    private void setupList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(linearLayoutManager);
        mListView.setOnScrollListener(mOnScrollListener);

        mListAdpater = new CommentListAdapter(this);
        mListView.setAdapter(mListAdpater);

        // request for data
        if (!mLongCommentReady) {
            requestCommentsList(mNewsID, true);
        }
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        mIsResetList = false;
        if (!mLongCommentReady) {
            requestCommentsList(mNewsID, true);
        } else if (!mShortCommentReady) {
            requestCommentsList(mNewsID, false);
        }
    }

    private void requestCommentsList(int new_id, boolean isReqLongComment) {
        if (Utils.isNetworkConnected(this)) {
            DebugLog.d("==New ID==" + new_id);
            Intent intent = new Intent(this, DataService.class);
            String action;
            if (isReqLongComment) {
                action = Constants.Action.ACTION_GET_LONG_COMMENTS.toString();
            } else {
                action = Constants.Action.ACTION_GET_SHORT_COMMENTS.toString();
            }
            intent.setAction(action);
            intent.putExtra(Constants.EXTRA_NEWS_ID, new_id);
            startService(intent);
        } else {
            Toast.makeText(this, getString(R.string.network_disconnet), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCommentList(CommentsModel model, int comment_type) {
        // Set SwipeRefreshLayout to stop
        mSwipeRefreshLayout.setRefreshing(false);

        if (mIsResetList) {
            mListAdpater.updateListandReset(model, comment_type);
        } else {
            mListAdpater.updateList(model, comment_type);
        }
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
                if (lastPosition == (itemSize - 1)) {
                    mIsResetList = false;
                    if (!mLongCommentReady) {
                        requestCommentsList(mNewsID, true);
                    } else if (!mShortCommentReady) {
                        requestCommentsList(mNewsID, false);
                    }
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

    };

    @Subscribe
    public void onLongCommentsReadyEvent(CommentsModel model) {
        if (model == null) {
            return;
        }
        int comments_type = model.getComments_type();
        if (comments_type == Constants.COMMENT_TYPE_LONG) {
            updateCommentList(model, Constants.COMMENT_TYPE_LONG);
            mLongCommentReady = true;
        } else {
            updateCommentList(model, Constants.COMMENT_TYPE_SHORT);
            mShortCommentReady = true;
        }
    }

//    private class CommentReadyReceiver extends BroadcastReceiver {
//        // Prevents instantiation
//        private CommentReadyReceiver() {
//        }
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // TODO Auto-generated method stub
//            String action = intent.getAction();
//            if (action == null || !Constants.Action.ACTION_NOTIFY_COMMENTS_READY.toString().endsWith(action)) {
//                return;
//            }
//
//            int news_id = intent.getIntExtra(Constants.EXTRA_NEWS_ID, -1);
//            int comment_type = intent.getIntExtra(Constants.EXTRA_COMMENT_TYPE, Constants.COMMENT_TYPE_LONG);
//            CommentsModel model = DataCache.getInstance().getCommentsModel(news_id);
//
//            updateCommentList(model, comment_type);
//
//            if (comment_type == Constants.COMMENT_TYPE_LONG) {
//                mLongCommentReady = true;
//            } else if (comment_type == Constants.COMMENT_TYPE_SHORT) {
//                mShortCommentReady = true;
//            }
//
//            // remove cache
//            DataCache.getInstance().removeCommentCache(news_id);
//
//        }
//
//    }
}
