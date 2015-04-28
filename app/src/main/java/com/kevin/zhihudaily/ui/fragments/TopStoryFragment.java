package com.kevin.zhihudaily.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevin.zhihudaily.R;
import com.kevin.zhihudaily.model.NewsModel;

/**
 * Created by chenchao04 on 2015-03-20.
 */
public class TopStoryFragment extends Fragment {

    private View mRootView;

    public TopStoryFragment() {
        // Required empty public constructor
    }

    public static TopStoryFragment newInstance(NewsModel model) {
        TopStoryFragment fragment = new TopStoryFragment();
        Bundle args = new Bundle();
        //args.putParcelable(ARG_NEWW_MODEL, model);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
        return mRootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init views
        initViews();
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    public void initViews() {

    }
}
