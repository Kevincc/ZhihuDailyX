package com.kevin.zhihudaily.db;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.SparseArray;

import com.kevin.zhihudaily.model.CommentsModel;
import com.kevin.zhihudaily.model.DailyNewsModel;
import com.kevin.zhihudaily.model.NewsModel;

public class DataCache {

    private static DataCache mDataCache;
    private SparseArray<DailyNewsModel> mDailyMap;
    private SparseArray<NewsModel> mNewsMap;
    private SparseArray<CommentsModel> mCommentsMap;
    private static final int CACHE_MAX_SIZE = 10;
    private SparseArray<String> mBodyMap;

    private DataCache() {
        mDailyMap = new SparseArray<DailyNewsModel>();
        mNewsMap = new SparseArray<NewsModel>();
        mBodyMap = new SparseArray<String>();
        mCommentsMap = new SparseArray<CommentsModel>();
    }

    public static DataCache getInstance() {
        if (mDataCache == null) {
            mDataCache = new DataCache();
        }
        return mDataCache;
    }

    public void clearAllCache() {
        clearDailyCache();
        clearNewsCache();
        clearNewsBodyCache();
        clearCommentsCache();
    }

    @SuppressLint("NewApi")
    public void addDailyCache(String key, DailyNewsModel model) {
        if (mDailyMap.size() >= CACHE_MAX_SIZE) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                for (int i = 0; i < CACHE_MAX_SIZE / 4; i++) {
                    mDailyMap.remove(mDailyMap.keyAt(i));
                }
            } else {
                mDailyMap.removeAtRange(0, CACHE_MAX_SIZE / 4);
            }
        }
        mDailyMap.put(key.hashCode(), model);

    }

    public void deleteDailyCache(int key) {
        mDailyMap.remove(key);
    }

    public void clearDailyCache() {
        mDailyMap.clear();
    }

    public DailyNewsModel getDailyNewsModel(String key) {
        if (key == null) {
            return null;
        }
        return mDailyMap.get(key.hashCode());
    }

    public void addNewsCache(int id, NewsModel model) {
        mNewsMap.put(id, model);
    }

    public NewsModel getNewsCache(int id) {
        return mNewsMap.get(id);
    }

    public void clearNewsCache() {
        mNewsMap.clear();
    }

    public boolean updateNewsDetailByID(String key, int id, String body, String imageSource) {
        DailyNewsModel dailyModel = mDailyMap.get(key.hashCode());
        ArrayList<NewsModel> list = (ArrayList<NewsModel>) dailyModel.getNewsList();
        for (NewsModel model : list) {
            if (model.getId() == id) {
                model.setBody(body);
                model.setImage_source(imageSource);
                return true;
            }
        }
        return false;
    }

    public NewsModel getNewsModelByDateAndID(String key, int id) {
        if (key == null || id == -1) {
            return null;
        }
        DailyNewsModel dailyModel = mDailyMap.get(key.hashCode());
        ArrayList<NewsModel> list = (ArrayList<NewsModel>) dailyModel.getNewsList();
        for (NewsModel model : list) {
            if (model.getId() == id) {
                return model;
            }
        }
        return null;
    }

    public String getNewsBodyByDateAndID(String key, int id) {
        if (key == null || id == -1) {
            return null;
        }
        DailyNewsModel dailyModel = mDailyMap.get(key.hashCode());
        ArrayList<NewsModel> list = (ArrayList<NewsModel>) dailyModel.getNewsList();
        for (NewsModel model : list) {
            if (model.getId() == id) {
                return model.getBody();
            }
        }
        return null;
    }

    public void addNewsBodyCache(int id, String body) {
        mBodyMap.put(id, body);
    }

    public String getNewsBodyCache(int id) {
        return mBodyMap.get(id);
    }

    public void removeNewsBodyCahce(int id) {
        mBodyMap.remove(id);
    }

    public void clearNewsBodyCache() {
        mBodyMap.clear();
    }

    public void addCommentsCache(int id, CommentsModel model) {
        mCommentsMap.put(id, model);
    }

    public CommentsModel getCommentsModel(int id) {
        return mCommentsMap.get(id);
    }

    public void removeCommentCache(int id) {
        mCommentsMap.remove(id);
    }

    private void clearCommentsCache() {
        mCommentsMap.clear();
    }
}
