package com.kevin.zhihudaily.db;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import com.kevin.zhihudaily.Constants;
import com.kevin.zhihudaily.Constants.Action;
import com.kevin.zhihudaily.DebugLog;
import com.kevin.zhihudaily.EventBus;
import com.kevin.zhihudaily.http.ZhihuRequest;
import com.kevin.zhihudaily.model.CommentsModel;
import com.kevin.zhihudaily.model.DailyNewsModel;
import com.kevin.zhihudaily.model.NewsModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataService extends IntentService {

    private NewsDao dao;

    public DataService() {
        super(DataService.class.getSimpleName());
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        if (intent == null) {
            DebugLog.d("Intent is null!");
            return;
        }
        DebugLog.d(intent.toString());
        final Intent paraIntent = intent;
        DataBaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override public void run(SQLiteDatabase database) {
                dao = new NewsDao(database);
                executeAction(paraIntent);
            }
        });

    }

    private void executeAction(Intent intent) {
        String action = intent.getAction();
        Action actionType = Action.ACTION_NONE;
        try {
            actionType = Action.valueOf(action);
        } catch (Exception e) {
            // TODO: handle exception
            DebugLog.d("action type is null");
        }

        int id = intent.getIntExtra(Constants.EXTRA_NEWS_ID, -1);
        String date = intent.getStringExtra(Constants.EXTRA_NEWS_DATE);
        switch (actionType) {
        case ACTION_WRITE_DAILY_NEWS:
            String key = intent.getStringExtra(Constants.EXTRA_CACHE_ID);
            if (key == null) {
                break;
            }

            //            DailyNewsModel model = DataCache.getInstance().getDailyNewsModel(key);
            //            DataBaseManager.getInstance().writeDailyNewsToDB(model);

            break;
        case ACTION_WRITE_NEWS_DEATIL:
            String body = intent.getStringExtra(Constants.EXTRA_NEWS_BODY);
            if (id == -1 || body == null) {
                break;
            }

            dao.updateNewsBodyToDB(id, body, null);
            break;
        case ACTION_READ_DAILY_NEWS:
            if (date == null) {
                break;
            }
            DailyNewsModel dailyNewsModel = dao.readDaliyNewsList(date);
            if (dailyNewsModel != null) {
                DataCache.getInstance().addDailyCache(dailyNewsModel.getDate(), dailyNewsModel);

                // notify ui to update
                //                mBroadcastNotifier.notifyDailyNewsDataReady(date);
                EventBus.getInstance().post(dailyNewsModel);
            }
            break;
        case ACTION_READ_LASTEST_NEWS:
            DailyNewsModel lastestNewsModel = dao.readLastestNewsList();
            DebugLog.e("==Model size==" + lastestNewsModel.getNewsList().size());
            if (lastestNewsModel != null) {
                DataCache.getInstance().addDailyCache(lastestNewsModel.getDate(), lastestNewsModel);

                // notify ui to update
                //                mBroadcastNotifier.notifyDailyNewsDataReady(lastestNewsModel.getDate());
                EventBus.getInstance().post(lastestNewsModel);
            }
            break;
        case ACTION_READ_NEWS_DEATIL:
            if (date == null || id == -1) {
                break;
            }
            //            String news_body = DataBaseManager.getInstance().readNewsBody(news_id);
            NewsModel model = dao.readNewsBodyAndImageSource(id);
            if (model != null) {
                // Update to db
                DataCache.getInstance().updateNewsDetailByID(date, id, model.getBody(), model.getImage_source());

                // Notify ui to update
                //                mBroadcastNotifier.notifyNewsBodyDataReady(date, id);
                EventBus.getInstance().post(model);
            }
            break;
        case ACTION_GET_TODAY_NEWS:
            requestTodayNews();
            break;
        case ACTION_GET_DAILY_NEWS:
            requestDailyNewsByDate(date);
            break;
        case ACTION_GET_NEWS_DETAIL:
            requestNewsDetail(date, id);
            break;
        case ACTION_START_OFFLINE_DOWNLOAD:
            startOfflineDownload(date);
            break;
        case ACTION_GET_LONG_COMMENTS:
            requestLongComments(id);
            break;
        case ACTION_GET_SHORT_COMMENTS:
            requestShortComments(id);
            break;
        default:
            break;
        }
    }

    private void requestTodayNews() {
        //        Log.d(TAG, "==IN=" + SystemClock.currentThreadTimeMillis());
        try {
            DailyNewsModel model = ZhihuRequest.getRequestService().getDailyNewsToday();

            if (model != null) {
                int newTimeStamp = Integer.valueOf(model.getNewsList().get(0).getGa_prefix());

                int dataStatus = DataBaseManager.getInstance().checkDataExpire(newTimeStamp);
                if (dataStatus >= 0) {
                    if (dataStatus > 0) {
                        // update timestamp
                        DataBaseManager.getInstance().setDataTimeStamp(newTimeStamp);
                    }

                    // notify ui to update
                    EventBus.getInstance().post(model);
                }

            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        //        Log.d(TAG, "==Model=" + model.getDisplay_date());
        //        Log.d(TAG, "==OUT=" + SystemClock.currentThreadTimeMillis());

    }

    private void requestDailyNewsByDate(String date) {
        DailyNewsModel model = ZhihuRequest.getRequestService().getDailyNewsByDate(date);
        if (model != null) {
            // notify ui to update
            EventBus.getInstance().post(model);
        }
    }

    private void requestNewsDetail(String date, int id) {
        NewsModel model = ZhihuRequest.getRequestService().getNewsById(id);
        //        Log.d(TAG, "==ModelBody=" + model.getBody());
        if (model != null) {
            //            DataCache.getInstance().updateNewsDetailByID(date, id, model.getBody(), model.getImage_source());

            // Notify ui to update
            //            mBroadcastNotifier.notifyNewsBodyDataReady(date, id);
            EventBus.getInstance().post(model);
        }
    }

    private void startOfflineDownload(String date) {
        Calendar calendar = Calendar.getInstance();
        Date todayDate = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        String todayDateString = formatter.format(todayDate);

        DailyNewsModel model;
        if (todayDateString.equals(date)) {
            model = ZhihuRequest.getRequestService().getDailyNewsToday();
        } else {
            model = ZhihuRequest.getRequestService().getDailyNewsByDate(date);
        }

        if (model != null) {

            //            int newTimeStamp = Integer.valueOf(model.getNewsList().get(0).getGa_prefix());
            //            if (DataBaseManager.getInstance().checkDataExpire(newTimeStamp) >= 0) {
            //                return;
            //            }

            dao.writeDailyNewsToDB(model);

            ArrayList<Integer> lacklist = (ArrayList<Integer>) dao.getNewsDetailLackList(date);
            if (lacklist == null || lacklist.size() == 0) {
                // notify ui to update
                //                mBroadcastNotifier.notifyProgress(100);
                return;
            }

            ArrayList<NewsModel> list = (ArrayList<NewsModel>) model.getNewsList();
            int size = list.size();
            if (size > 0) {
                int incr = 100 / size + 1;
                int progress = 0;
                ArrayList<NewsModel> newslist = new ArrayList<NewsModel>();
                for (NewsModel news : list) {
                    news = ZhihuRequest.getRequestService().getNewsById(news.getId());
                    newslist.add(news);

                    // Update new body to db one by one
                    //                    if (news != null) {
                    //                        //                        Log.d(TAG, "==startOfflineDownload  image_source" + news.getImage_source());
                    //                        DataBaseManager.getInstance().updateNewsBodyToDB(news.getId(), news.getBody(),
                    //                                news.getImage_source());
                    //                    }

                    // notify ui to update
                    progress += incr;
                    //                    mBroadcastNotifier.notifyProgress(progress);

                }

                // Write to DB
                dao.updateNewsListToDB(newslist);

                // notify ui to update
                //                mBroadcastNotifier.notifyProgress(100);
            }
        }
    }

    private void requestLongComments(int id) {
        DebugLog.d("lid = " + id);
        CommentsModel model = ZhihuRequest.getRequestService().getLongCommentsById(id);

        DataCache.getInstance().addCommentsCache(id, model);

        // notify ui to update
        //        mBroadcastNotifier.notifyCommentDataReady(id, Constants.COMMENT_TYPE_LONG);
        EventBus.getInstance().post(model);
    }

    private void requestShortComments(int id) {
        DebugLog.d("sid = " + id);
        CommentsModel model = ZhihuRequest.getRequestService().getShortCommentsById(id);

        DataCache.getInstance().addCommentsCache(id, model);

        // notify ui to update
        //        mBroadcastNotifier.notifyCommentDataReady(id, Constants.COMMENT_TYPE_SHORT);
        EventBus.getInstance().post(model);
    }

}
