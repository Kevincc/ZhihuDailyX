package com.kevin.zhihudaily.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.SparseBooleanArray;

import com.kevin.zhihudaily.DebugLog;
import com.kevin.zhihudaily.model.DailyNewsModel;
import com.kevin.zhihudaily.model.NewsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenchao04 on 2014-12-02.
 */
public class NewsDao {
    private SQLiteDatabase db;

    public NewsDao(SQLiteDatabase database) {
        db = database;
    }

    public int writeDailyNewsToDB(DailyNewsModel dailyNewsModel) {
        DebugLog.d("==writeDailyNewsToDB==START");
        int count = 0;
        if (dailyNewsModel == null) {
            return 0;
        }

        db.beginTransaction();
        try {
            String date = dailyNewsModel.getDate();

            List<NewsModel> topList = dailyNewsModel.getTopStories();
            SparseBooleanArray topIDMap = new SparseBooleanArray();
            for (NewsModel topModel : topList) {
                topIDMap.put(topModel.getId(), true);
            }

            List<NewsModel> newList = dailyNewsModel.getNewsList();
            for (NewsModel model : newList) {
                ContentValues values = new ContentValues();
                values.put(DataBaseConstants.ID, model.getId());
                values.put(DataBaseConstants.DATE, date);
                if (topIDMap.get(model.getId())) {
                    values.put(DataBaseConstants.IS_TOP_STORY, 1);
                } else {
                    values.put(DataBaseConstants.IS_TOP_STORY, 0);
                }

                values.put(DataBaseConstants.GA_PREFIX, model.getGa_prefix());
                values.put(DataBaseConstants.TITLE, model.getTitle());
                values.put(DataBaseConstants.URL, model.getUrl());
                //                Log.e(TAG, "==Image_source" + model.getImage_source());
                values.put(DataBaseConstants.IMAGE_SOURCE, model.getImage_source());
                values.put(DataBaseConstants.IMAGE_URL, model.getImage());
                values.put(DataBaseConstants.IMAGE_THUMBNAIL, model.getThumbnail());
                values.put(DataBaseConstants.SHARE_URL, model.getShare_url());
                values.put(DataBaseConstants.BODY, model.getBody());

                db.insertWithOnConflict(DataBaseConstants.NEWS_TABLE_NAME, null, values,
                        SQLiteDatabase.CONFLICT_REPLACE);

                count++;
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            DebugLog.d(e.getMessage());
        } finally {
            db.endTransaction();
        }
        DebugLog.d("==writeDailyNewsToDB==END");
        return count;
    }

    public int writeNewsToDB(NewsModel model) {
        DebugLog.d("==writeNewsToDB");
        int count = 0;
        if (model == null) {
            return 0;
        }

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DataBaseConstants.BODY, model.getBody());
            values.put(DataBaseConstants.IMAGE_SOURCE, model.getImage_source());

            String[] whereArgs = {String.valueOf(model.getId())};
            db.updateWithOnConflict(DataBaseConstants.NEWS_TABLE_NAME, values, DataBaseConstants.ID + "=?", whereArgs,
                    SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            DebugLog.d(e.getMessage());
        } finally {
            db.endTransaction();
        }
        return count;
    }

    public int updateNewsBodyToDB(int id, String body, String imageSource) {
        //        Log.d(TAG, "==updateNewsBodyToDB");
        int count = 0;
        if (id == -1 || body == null) {
            return 0;
        }

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DataBaseConstants.BODY, body);
            if (imageSource != null) {
                values.put(DataBaseConstants.IMAGE_SOURCE, imageSource);
            }
            String[] whereArgs = {String.valueOf(id)};
            //            Log.d(TAG, "==imageSource=" + imageSource);
            count = db.updateWithOnConflict(DataBaseConstants.NEWS_TABLE_NAME, values, DataBaseConstants.ID + "=?",
                    whereArgs, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            DebugLog.e("==Exception==" + e.toString());
        } finally {
            db.endTransaction();
        }
        return count;
    }

    public int updateNewsListToDB(List<NewsModel> list) {
        DebugLog.d("==updateNewsListToDB==START");
        int count = 0;
        if (list == null || list.size() <= 0) {
            return 0;
        }

        db.beginTransaction();
        try {
            for (NewsModel newsModel : list) {
                ContentValues values = new ContentValues();
                values.put(DataBaseConstants.BODY, newsModel.getBody());
                String imageSource = newsModel.getImage_source();
                if (imageSource != null) {
                    values.put(DataBaseConstants.IMAGE_SOURCE, imageSource);
                }
                String[] whereArgs = {String.valueOf(newsModel.getId())};
                count = db.updateWithOnConflict(DataBaseConstants.NEWS_TABLE_NAME, values, DataBaseConstants.ID + "=?",
                        whereArgs, SQLiteDatabase.CONFLICT_REPLACE);
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            DebugLog.e("==Exception==" + e.toString());
        } finally {
            db.endTransaction();
        }
        DebugLog.d("==updateNewsListToDB==END");
        return count;
    }

    public DailyNewsModel readDaliyNewsList(String date) {
        DailyNewsModel dailyModel = null;

        String[] columns = {DataBaseConstants.ID, DataBaseConstants.DATE, DataBaseConstants.GA_PREFIX,
                DataBaseConstants.IS_TOP_STORY, DataBaseConstants.TITLE, DataBaseConstants.URL,
                DataBaseConstants.IMAGE_SOURCE, DataBaseConstants.IMAGE_URL, DataBaseConstants.IMAGE_THUMBNAIL,
                DataBaseConstants.SHARE_URL, DataBaseConstants.BODY};
        String selection = "date=?";
        String[] selectionArgs = {date};
        String orderBy = DataBaseConstants.GA_PREFIX + " DESC" + ", " + DataBaseConstants.ID + " DESC";
        Cursor cursor = db.query(DataBaseConstants.NEWS_TABLE_NAME, columns, selection, selectionArgs, null, null,
                orderBy);
        if (cursor != null && cursor.getCount() > 0) {
            dailyModel = new DailyNewsModel();
            ArrayList<NewsModel> newsList = new ArrayList<NewsModel>();
            ArrayList<NewsModel> topStories = new ArrayList<NewsModel>();
            while (cursor.moveToNext()) {
                NewsModel model = new NewsModel();
                model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseConstants.ID)));
                // Log.d(TAG, "==id=" + model.getId());
                model.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.DATE)));
                model.setGa_prefix(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.GA_PREFIX)));
                model.setIs_top_story(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseConstants.IS_TOP_STORY)));
                model.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.TITLE)));
                DebugLog.d("==id=" + model.getTitle());
                model.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.URL)));
                model.setImage_source(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.IMAGE_SOURCE)));
                model.setImage(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.IMAGE_URL)));
                model.setThumbnail(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.IMAGE_THUMBNAIL)));
                model.setShare_url(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.SHARE_URL)));

                // read body
                model.setBody(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.BODY)));

                newsList.add(model);
                if (model.isIs_top_story() == 1) {
                    topStories.add(model);
                }
            }
            dailyModel.setNewsList(newsList);
            dailyModel.setTopStories(topStories);
            String dateString = newsList.get(0).getDate();
            dailyModel.setDate(dateString);
            // convert string to date
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            try {
                Date dateTime = formatter.parse(dateString);
                SimpleDateFormat diaplayFormat = new SimpleDateFormat("yyyy.M.d cccc");
                String displayDate = diaplayFormat.format(dateTime);
                dailyModel.setDisplay_date(displayDate);
            } catch (ParseException e) {
                DebugLog.d(e.getMessage());
            }
            cursor.close();
        }
        return dailyModel;
    }

    public DailyNewsModel readLastestNewsList() {
        DailyNewsModel dailyModel = null;

        String date = getLastestNewsDate();
        if (date == null) {
            return null;
        }

        String[] columns = {DataBaseConstants.ID, DataBaseConstants.DATE, DataBaseConstants.GA_PREFIX,
                DataBaseConstants.IS_TOP_STORY, DataBaseConstants.TITLE, DataBaseConstants.URL,
                DataBaseConstants.IMAGE_SOURCE, DataBaseConstants.IMAGE_URL, DataBaseConstants.IMAGE_THUMBNAIL,
                DataBaseConstants.SHARE_URL, DataBaseConstants.BODY};
        String selection = "date=?";
        String[] selectionArgs = {date};
        String orderBy = DataBaseConstants.GA_PREFIX + " DESC" + ", " + DataBaseConstants.ID + " DESC";
        Cursor cursor = db.query(DataBaseConstants.NEWS_TABLE_NAME, columns, selection, selectionArgs, null, null,
                orderBy);
        if (cursor != null && cursor.getCount() > 0) {
            dailyModel = new DailyNewsModel();
            ArrayList<NewsModel> newsList = new ArrayList<NewsModel>();
            ArrayList<NewsModel> topStories = new ArrayList<NewsModel>();
            while (cursor.moveToNext()) {
                NewsModel model = new NewsModel();
                model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseConstants.ID)));
                // Log.d(TAG, "==id=" + model.getId());
                model.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.DATE)));
                model.setGa_prefix(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.GA_PREFIX)));
                model.setIs_top_story(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseConstants.IS_TOP_STORY)));
                model.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.TITLE)));
                DebugLog.d("==id=" + model.getTitle());
                model.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.URL)));
                model.setImage_source(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.IMAGE_SOURCE)));
                model.setImage(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.IMAGE_URL)));
                model.setThumbnail(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.IMAGE_THUMBNAIL)));
                model.setShare_url(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.SHARE_URL)));

                // read body
                model.setBody(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.BODY)));

                newsList.add(model);
                if (model.isIs_top_story() == 1) {
                    topStories.add(model);
                }
            }
            dailyModel.setNewsList(newsList);
            dailyModel.setTopStories(topStories);
            String dateString = newsList.get(0).getDate();
            dailyModel.setDate(dateString);
            // convert string to date
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            try {
                Date dateTime = formatter.parse(dateString);
                SimpleDateFormat diaplayFormat = new SimpleDateFormat("yyyy.M.d cccc");
                String displayDate = diaplayFormat.format(dateTime);
                dailyModel.setDisplay_date(displayDate);
            } catch (ParseException e) {
                DebugLog.d(e.getMessage());
            }
            cursor.close();
        }
        return dailyModel;
    }

    public String readNewsBody(int id) {
        String body = null;

        String[] columns = {DataBaseConstants.ID, DataBaseConstants.BODY};
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db
                .query(DataBaseConstants.NEWS_TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            // read body
            body = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.BODY));
            DebugLog.d("==body=" + body);
            cursor.close();
        }
        return body;
    }

    public NewsModel readNewsBodyAndImageSource(int id) {
        NewsModel model = null;

        String[] columns = {DataBaseConstants.ID, DataBaseConstants.BODY};
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db
                .query(DataBaseConstants.NEWS_TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            // init model
            model = new NewsModel();
            // read image source
            model.setImage_source(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.IMAGE_SOURCE)));

            // read body
            model.setBody(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.BODY)));

            cursor.close();
        }
        return model;
    }

    public String getLastestNewsDate() {
        String date = null;

        String[] columns = {DataBaseConstants.DATE};
        String orderBy = "date";
        String groupBy = "date";
        Cursor cursor = db.query(true, DataBaseConstants.NEWS_TABLE_NAME, columns, null, null, groupBy, null, orderBy,
                null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            // read lastest date
            date = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.DATE));
            DebugLog.d("==last date=" + date);
            cursor.close();
        }
        return date;
    }

    public List<Integer> getNewsDetailLackList(String date) {
        ArrayList<Integer> lacklist = null;

        if (date == null) {
            return null;
        }

        String[] columns = {DataBaseConstants.ID, DataBaseConstants.DATE, DataBaseConstants.BODY};
        //        String selection = "body=?";
        //        String[] selectionArgs = { "null" };
        Cursor cursor = db.query(DataBaseConstants.NEWS_TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            lacklist = new ArrayList<Integer>();
            while (cursor.moveToNext()) {
                String body = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstants.BODY));
                //                DebugLog.d("body=" + body);
                if (TextUtils.isEmpty(body)) {
                    NewsModel model = new NewsModel();
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseConstants.ID));
                    DebugLog.d("==id=" + model.getId());
                    lacklist.add(id);
                }
            }
            cursor.close();
        }
        return lacklist;
    }
}
