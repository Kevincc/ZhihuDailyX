package com.kevin.zhihudaily.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.baidu.mobstat.StatService;
import com.halfbit.tinybus.Subscribe;
import com.kevin.zhihudaily.Constants;
import com.kevin.zhihudaily.DebugLog;
import com.kevin.zhihudaily.EventBus;
import com.kevin.zhihudaily.R;
import com.kevin.zhihudaily.Utils;
import com.kevin.zhihudaily.ZhihuDailyApplication;
import com.kevin.zhihudaily.db.DataService;
import com.kevin.zhihudaily.model.NewsModel;
import com.kevin.zhihudaily.view.BackdropImageView;
import com.kevin.zhihudaily.view.CollapsingTitleLayout;
import com.kevin.zhihudaily.view.ExScrollView;
import com.kevin.zhihudaily.view.ExWebView;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.kevin.zhihudaily.ui.DetailFragmentNew.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.kevin.zhihudaily.ui.DetailFragmentNew#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class DetailFragmentNew extends Fragment {
    // parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NEWW_MODEL = "arg_news_model";
    private static final String ARG_PARAM2 = "param2";

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = true;

    //  types of parameters
    private NewsModel mNewsModel;

    private View mRootView;

    @InjectView(R.id.backdrop_toolbar)
    CollapsingTitleLayout mTitleLayout;

    @InjectView(R.id.tb_toolbar)
    View mToolbar;

    @InjectView(R.id.scroll_view)
    ExScrollView mScrollView;

    @InjectView(R.id.iv_thumbnail)
    BackdropImageView mImageView;

    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;

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
    public static DetailFragmentNew newInstance(NewsModel model) {
        DetailFragmentNew fragment = new DetailFragmentNew();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NEWW_MODEL, model);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragmentNew() {
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
        mRootView = inflater.inflate(R.layout.fragment_detail_new, container, false);
        ButterKnife.inject(this, mRootView);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init views
        initViews();
    }

    @Override public void onStart() {
        super.onStart();
        EventBus.getInstance().register(this);
    }

    @Override public void onStop() {
        EventBus.getInstance().unregister(this);
        super.onStop();
    }

    @Override public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override public void onDestroyView() {
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

        //        if (!TOOLBAR_IS_STICKY) {
        //            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        //        }
        //        mToolbar.setBackgroundColor(Color.TRANSPARENT);

        getActivity().setTitle(null);

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

        mTitleLayout.setTitle(mNewsModel.getTitle());

        mScrollView.setOnScrollChangedListener(mOnScrollChangedListener);

        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
        updateNewsDetail();

    }

    private int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[] { R.attr.actionBarSize };
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

    private ExScrollView.OnScrollChangedListener mOnScrollChangedListener = new ExScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            DebugLog.d(" l:" + l + "  t:" + t + "  ol:" + oldl + "  ot:" + oldt);
            final int toolbarHeight = mToolbar.getHeight();
            final int y = -t;
            final float percent = y / (float) (t - toolbarHeight);

            if (oldt > toolbarHeight) {
                setBackdropOffset(percent);
            } else {
                setBackdropOffset(1f);
            }
        }

    };

    private void setBackdropOffset(float offset) {
        if (mTitleLayout != null) {
            mTitleLayout.setScrollOffset(offset);
        }
        if (mImageView != null) {
            mImageView.setScrollOffset(
                    (int) ((mToolbar.getHeight() - mImageView.getHeight()) * offset));
        }
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
            //            String imageSource = mNewsModel.getImage_source();
            //            if (imageSource != null) {
            //                mSourceTextView.setText(imageSource);
            //            }
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

    @Subscribe
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
                //                if (imageSource != null) {
                //                    mSourceTextView.setText(imageSource);
                //                }
            }

        }
    }

    private class ExWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
        }
    }
}
