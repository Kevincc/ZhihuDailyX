package com.kevin.zhihudaily.db;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.kevin.zhihudaily.http.ZhihuRequest;
import com.kevin.zhihudaily.model.CommentsModel;
import com.kevin.zhihudaily.model.DailyNewsModel;
import com.kevin.zhihudaily.model.NewsModel;
import com.kevin.zhihudaily.utils.Constants;
import com.kevin.zhihudaily.utils.Constants.Action;
import com.kevin.zhihudaily.utils.DebugLog;
import com.kevin.zhihudaily.utils.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.halfbit.tinybus.Produce;

public class DataService extends IntentService {

    private NewsDao dao;

    private DailyNewsModel mDailyNewsModel;

    public DataService() {
        super(DataService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            DebugLog.d("Intent is null!");
            return;
        }
        DebugLog.d(intent.toString());
        final Intent paraIntent = intent;
        DataBaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
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
            DebugLog.d("action type is null");
        }

        int id = intent.getIntExtra(Constants.EXTRA_NEWS_ID, -1);
        String date = intent.getStringExtra(Constants.EXTRA_NEWS_DATE);
        switch (actionType) {
            case ACTION_WRITE_DAILY_NEWS:
                writeDailyNewsToDB(intent);
                break;
            case ACTION_WRITE_NEWS_DEATIL:
                writeNewsDetailToDB(intent);
                break;
            case ACTION_READ_DAILY_NEWS:
                readDailyNewsByDate(date);
                break;
            case ACTION_READ_LASTEST_NEWS:
                readLastDailyNews();
                break;
            case ACTION_READ_NEWS_DEATIL:
                readNewsDetail(id);
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

    private void writeDailyNewsToDB(Intent intent) {
        if (intent == null) {
            return;
        }

        DailyNewsModel model = intent.getParcelableExtra(Constants.EXTRA_DAILY_NEWS_MODEL);
        dao.writeDailyNewsToDB(model);
    }

    private void writeNewsDetailToDB(Intent intent) {
        int id = intent.getIntExtra(Constants.EXTRA_NEWS_ID, -1);
        String body = intent.getStringExtra(Constants.EXTRA_NEWS_BODY);
        if (id == -1 || body == null) {
            return;
        }
        dao.updateNewsBodyToDB(id, body, null);
    }

    private void readDailyNewsByDate(String date) {
        if (date == null) {
            return;
        }
        DailyNewsModel dailyNewsModel = dao.readDaliyNewsList(date);
        if (dailyNewsModel != null) {
            // notify ui to update
            EventBus.getInstance().post(dailyNewsModel);
        }
    }

    private void readLastDailyNews() {
        DailyNewsModel lastestNewsModel = dao.readLastestNewsList();
        DebugLog.e("==Model size==" + lastestNewsModel.getNewsList().size());
        if (lastestNewsModel != null) {
            // notify ui to update
            EventBus.getInstance().post(lastestNewsModel);
        }
    }

    private void readNewsDetail(int id) {
        if (id == -1) {
            return;
        }
        //            String news_body = DataBaseManager.getInstance().readNewsBody(news_id);
        NewsModel model = dao.readNewsBodyAndImageSource(id);
        if (model != null) {
            // Update to db

            // Notify ui to update
            EventBus.getInstance().post(model);
        }
    }

    private void requestTodayNews() {
        DebugLog.d("==IN=" + SystemClock.currentThreadTimeMillis());
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
                    DebugLog.d("==Model=" + model.getDisplay_date());
                    mDailyNewsModel = model;
                    // notify ui to update
                    EventBus.getInstance().post(model);
                }

            }
        } catch (Exception e) {
            DebugLog.d(e.getMessage());
        }
        DebugLog.d("==OUT=" + SystemClock.currentThreadTimeMillis());
    }

    @Produce
    public DailyNewsModel getLastDailyNewsModel() {
        return mDailyNewsModel;
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
        model.setComments_type(Constants.COMMENT_TYPE_LONG);

        EventBus.getInstance().post(model);
    }

    private void requestShortComments(int id) {
        DebugLog.d("sid = " + id);
        CommentsModel model = ZhihuRequest.getRequestService().getShortCommentsById(id);
        model.setComments_type(Constants.COMMENT_TYPE_SHORT);

        EventBus.getInstance().post(model);
    }

}
