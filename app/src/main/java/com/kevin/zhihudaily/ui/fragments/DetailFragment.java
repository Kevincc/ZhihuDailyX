package com.kevin.zhihudaily.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.baidu.mobstat.StatService;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.kevin.zhihudaily.R;
import com.kevin.zhihudaily.ZhihuDailyApplication;
import com.kevin.zhihudaily.common.Constants;
import com.kevin.zhihudaily.model.NewsModel;
import com.kevin.zhihudaily.provider.DataService;
import com.kevin.zhihudaily.ui.activities.CommentActivity;
import com.kevin.zhihudaily.ui.activities.DetailActivity;
import com.kevin.zhihudaily.ui.views.ExWebView;
import com.kevin.zhihudaily.utils.DebugLog;
import com.kevin.zhihudaily.utils.Utils;
import com.squareup.picasso.Picasso;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements ObservableScrollViewCallbacks {
    // parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NEWW_MODEL = "arg_news_model";
    private static final String ARG_PARAM2 = "param2";

    private static final float MAX_TEXT_SCALE_DELTA = 0.1f;
    private static final boolean TOOLBAR_IS_STICKY = true;

    //  types of parameters
    private NewsModel mNewsModel;

    @InjectView(R.id.tb_toolbar)
    Toolbar mToolbar;

    private View mRootView;

    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;

    @InjectView(R.id.scroll)
    ObservableScrollView mScrollView;

    @InjectView(R.id.iv_image)
    ImageView mImageView;

    @InjectView(R.id.rl_header)
    RelativeLayout mHeaderView;

    @InjectView(R.id.title)
    TextView mTitleView;

    @InjectView(R.id.overlay)
    View mOverlayView;

    @InjectView(R.id.tv_news_image_source)
    TextView mSourceTextView;

    @InjectView(R.id.wv_webview)
    ExWebView mWebView;

    private int mActionBarSize;
    private int mFlexibleSpaceImageHeight;
    private int mToolbarColor;
    private int mTitleViewHeight;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param model Parameter 1.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(NewsModel model) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NEWW_MODEL, model);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNewsModel = getArguments().getParcelable(ARG_NEWW_MODEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.inject(this, mRootView);
        ViewTreeObserver vto = mTitleView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mTitleView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mTitleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                if (mTitleView != null) {
                    mTitleViewHeight = mTitleView.getHeight();
                    DebugLog.d("==mTitleViewHeight=" + mTitleViewHeight);
                    mTitleView.setTranslationY(mFlexibleSpaceImageHeight - (mActionBarSize * 13 / 10));
                }
            }
        });
        return mRootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init views
        initViews();
    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getInstance().register(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
//        EventBus.getInstance().unregister(this);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
    }

    private void initViews() {
        if (mRootView == null) {
            return;
        }
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getActionBarSize();
        mToolbarColor = getResources().getColor(R.color.colorPrimary);

        setHasOptionsMenu(true);
        ((DetailActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(null);
            }
        });

        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        mOverlayView.setAlpha(0);

        mScrollView.setScrollViewCallbacks(this);
        mTitleView.setText(getActivity().getTitle());
        getActivity().setTitle(null);

//        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                    mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                } else {
//                    mScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//                mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);
//            }
//
//        });
        mProgressBar.setVisibility(View.VISIBLE);

        // setup webview
        setupWebView();

        // set up views
        setupViews();
    }

    private void setupWebView() {
        mWebView.setBackgroundColor(0);
        mWebView.getSettings().setAppCacheEnabled(true);
        String str = ZhihuDailyApplication.getInstance().getApplicationContext().getCacheDir().getAbsolutePath();
        mWebView.getSettings().setAppCachePath(str);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(1);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);

        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setVisibility(View.GONE);
        mWebView.setWebViewClient(new ExWebViewClient());
    }

    private void setupViews() {
        String url = mNewsModel.getImage();
        if (url != null && !url.isEmpty()) {
            Picasso.with(getActivity()).load(mNewsModel.getImage()).placeholder(R.drawable.image_top_default).fit()
                    .centerCrop().into(mImageView);
        }

        mTitleView.setText(mNewsModel.getTitle());
        // Scale title text
        float scale = 1 + Math.max(0, MAX_TEXT_SCALE_DELTA);
        mTitleView.setPivotX(0);
        mTitleView.setPivotY(0);
        mTitleView.setScaleX(scale);
        mTitleView.setScaleY(scale);

        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleViewHeight * scale);
        DebugLog.d("  maxTitleY = " + maxTitleTranslationY + "  scale = " + scale + " title-H = " + mTitleView
                .getHeight());
        mTitleView.setTranslationY(maxTitleTranslationY);
        mSourceTextView.setText(mNewsModel.getImage_source());

        updateNewsDetail();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_comment:
                gotoComments();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoComments() {
        Intent intent = new Intent(getActivity(), CommentActivity.class);
        intent.putExtra(Constants.EXTRA_NEWS_ID, mNewsModel.getId());
        startActivity(intent);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        mOverlayView.setTranslationY(Math.max(minOverlayTransitionY, Math.min(0, -scrollY)));
//        mImageView.setTranslationY(Math.max(minOverlayTransitionY, Math.min(0, -scrollY / 2)));
        mHeaderView.setTranslationY(Math.max(minOverlayTransitionY, Math.min(0, -scrollY / 2)));

        // Change alpha of overlay
        mOverlayView.setAlpha(Math.max(0, Math.min(1, (float) scrollY / flexibleRange)));

        // Scale title text
        float scale = 1 + Math.max(0, Math.min(MAX_TEXT_SCALE_DELTA, (flexibleRange - scrollY) / flexibleRange));
        mTitleView.setPivotX(0);
        mTitleView.setPivotY(0);
        mTitleView.setScaleX(scale);
        mTitleView.setScaleY(scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        DebugLog.d(" scrollY = " + scrollY + "  titleY = " + titleTranslationY + "  maxTitleY = " + maxTitleTranslationY
                + "  scale = " + scale + " title-H = " + mTitleView.getHeight());
        if (TOOLBAR_IS_STICKY) {
            titleTranslationY = Math.max(0, titleTranslationY);
        }

        mTitleView.setTranslationY(titleTranslationY);

        if (TOOLBAR_IS_STICKY) {
            // Change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
                setBackgroundAlpha(mToolbar, 1, mToolbarColor);
                mTitleView.setAlpha(0);
                mTitleView.setVisibility(View.GONE);
                mToolbar.setTitle(mTitleView.getText());
            } else {
                setBackgroundAlpha(mToolbar, 0, mToolbarColor);
                mTitleView.setAlpha(1);
                mTitleView.setVisibility(View.VISIBLE);
                mToolbar.setTitle("");
            }
        } else {
            // Translate Toolbar
            if (scrollY < mFlexibleSpaceImageHeight) {
                mToolbar.setTranslationY(0);
            } else {
                mToolbar.setTranslationY(-scrollY);
            }
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    private int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = getActivity().obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    private void setBackgroundAlpha(View view, float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        view.setBackgroundColor(a + rgb);
    }

    private String optimizeHtml(String body) {
        String html = "";
        String tag = "";
        tag += "large";
        html = String
                .format("<!doctype html><html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,user-scalable=no\"><link href=\"news_qa.min.css\" rel=\"stylesheet\"><style>.headline .img-place-holder{height:0}</style><script src=\"img_replace.js\"></script></head><body className=\"%s\">",
                        tag);
        html += body;

        html += "</body></html>";

        return html;
    }

    private void updateNewsDetail() {
        String body = mNewsModel.getBody();
        if (body != null) {
            updateWebView(body);
            String imageSource = mNewsModel.getImage_source();
            if (imageSource != null) {
                mSourceTextView.setText(imageSource);
            }
        } else {
            if (Utils.isNetworkConnected(getActivity())) {
                //                requestNewsDetail();
                Intent intent = new Intent(getActivity(), DataService.class);
                intent.setAction(Constants.Action.ACTION_GET_NEWS_DETAIL.toString());
                intent.putExtra(Constants.EXTRA_NEWS_DATE, mNewsModel.getDate());
                intent.putExtra(Constants.EXTRA_NEWS_ID, mNewsModel.getId());
                getActivity().startService(intent);
            } else {
                //                readNewsDetailFromDB();
            }
        }
    }

    //    private void readNewsDetailFromDB() {
    //        // Read db data
    //        Intent intent = new Intent(getActivity(), DataService.class);
    //        intent.setAction(Constants.Action.ACTION_READ_NEWS_DEATIL.toString());
    //        intent.putExtra(Constants.EXTRA_NEWS_DATE, mNewsModel.getDate());
    //        intent.putExtra(Constants.EXTRA_NEWS_ID, mNewsModel.getId());
    //        getActivity().startService(intent);
    //    }

    private void updateWebView(String body) {
        String htmldata = optimizeHtml(body);
        mWebView.loadDataWithBaseURL("file:///android_asset/", htmldata, "text/html", "UTF-8", null);
    }

    public void onNewsDetailReadyEvent(NewsModel model) {
        if (model != null && model.getId() == mNewsModel.getId()) {
            if (model != null) {
                // Update ui
                String body = model.getBody();
                mNewsModel.setBody(model.getBody());
                updateWebView(body);

                // update image source
                String imageSource = model.getImage_source();
                mNewsModel.setImage_source(imageSource);
                if (imageSource != null) {
                    mSourceTextView.setText(imageSource);
                }
            }

        }
    }

    public void onEventMainThread(NewsModel model) {
        onNewsDetailReadyEvent(model);
    }

    private class ExWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
//            return super.shouldOverrideUrlLoading(view, url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }
    }
}
