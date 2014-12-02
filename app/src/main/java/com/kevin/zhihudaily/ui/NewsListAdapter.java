package com.kevin.zhihudaily.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.kevin.zhihudaily.R;
import com.kevin.zhihudaily.Utils;
import com.kevin.zhihudaily.model.DailyNewsModel;
import com.kevin.zhihudaily.model.NewsModel;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchao04 on 2014-12-01.
 */
public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private WeakReference<Context> wrContext;
    private static final int ANIMATED_ITEMS_COUNT = 2;

    private int lastAnimatedPosition = -1;
    private List<ListItem> mItemList = null;

    //    private enum ItemViewType {
    //        Section,
    //        Item,
    //    }

    public NewsListAdapter(Context context) {
        this.wrContext = new WeakReference<Context>(context);
        mItemList = new ArrayList<ListItem>();
    }

    static class ListItem {
        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final String section;
        public final int sectionSize;
        public final NewsModel model;
        public final int indexOfDay;
        public final String date;

        public ListItem(int type, NewsModel model, String section, int size, int index, String date) {
            this.type = type;
            this.model = model;
            this.section = section;
            this.sectionSize = size;
            this.indexOfDay = index;
            this.date = date;
        }

        public NewsModel getModel() {
            return model;
        }

        public int getType() {
            return type;
        }

        public String getSection() {
            return section;
        }

        public int getSectionSize() {
            return sectionSize;
        }

        public int getIndexOfDay() {
            return indexOfDay;
        }

        public String getDate() {
            return date;
        }

    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(wrContext.get()).inflate(R.layout.news_list_item, parent, false);
        if (ListItem.SECTION == viewType) {
            view = LayoutInflater.from(wrContext.get()).inflate(R.layout.news_list_section, parent, false);
        } else if (ListItem.ITEM == viewType) {
            view = LayoutInflater.from(wrContext.get()).inflate(R.layout.news_list_item, parent, false);
        }
        return new NewsFeedViewHolder(view);
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        runEnterAnimation(viewHolder.itemView, position);
        final NewsFeedViewHolder holder = (NewsFeedViewHolder) viewHolder;
        setupItemView(holder, position);
    }

    private void setupItemView(NewsFeedViewHolder holder, int position) {
        //        if (position % 2 == 0) {
        //            holder.titleTextView.setText("TITLE " + position);
        //            if (holder.imageView != null) {
        //                holder.imageView.setImageResource(R.drawable.ic_launcher);
        //            }
        //        } else {
        //            holder.titleTextView.setText("2014.12." + position);
        //        }
        if (mItemList == null || mItemList.size() == 0) {
            return;
        }
        ListItem item = mItemList.get(position);

        if (item.getType() == ListItem.ITEM) {
            NewsModel model = item.getModel();
            holder.titleTextView.setText(model.getTitle());

            String url = model.getThumbnail();
            if (url != null && !url.isEmpty()) {
                Picasso.with(wrContext.get()).load(url).placeholder(R.drawable.spinner_76_inner_holo).fit()
                        .into(holder.imageView);
            }
        } else {
            holder.titleTextView.setText(item.getSection());
        }
    }

    public void updateList(DailyNewsModel dailyModel) {
        if (dailyModel != null) {
            List<NewsModel> newsList = dailyModel.getNewsList();
            String date = dailyModel.getDate();
            int len = newsList.size();

            ListItem section = new ListItem(ListItem.SECTION, null, dailyModel.getDisplay_date(), len, -1, date);
            mItemList.add(section);

            for (int j = 0; j < len; j++) {
                newsList.get(j).setDate(date);
                ListItem item = new ListItem(ListItem.ITEM, newsList.get(j), null, len, j, date);
                //                Log.e(TAG, "==item[" + j + "]" + "=title=" + newsList.get(j).getTitle());
                mItemList.add(item);
            }
        }

        notifyDataSetChanged();
    }

    public void updateAllList(List<DailyNewsModel> list) {
        if (list != null) {
            // clear all
            mItemList.clear();

            int size = list.size();
            for (int i = 0; i < size; i++) {
                DailyNewsModel dailyModel = list.get(i);
                List<NewsModel> newsList = dailyModel.getNewsList();
                String date = dailyModel.getDate();
                int len = newsList.size();

                ListItem section = new ListItem(ListItem.SECTION, null, dailyModel.getDisplay_date(), len, -1, date);
                mItemList.add(section);

                for (int j = 0; j < len; j++) {
                    ListItem item = new ListItem(ListItem.ITEM, newsList.get(j), null, len, j, date);
                    mItemList.add(item);
                }

            }
        }
        notifyDataSetChanged();
    }

    @Override public int getItemCount() {
        return mItemList.size();
    }

    @Override public int getItemViewType(int position) {
        //        return super.getItemViewType(position);
        ListItem item = mItemList.get(position);
        return item.getType();
    }

    @Override public void onClick(View v) {

    }

    private void runEnterAnimation(View view, int position) {
        if (position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(wrContext.get()));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    public static class NewsFeedViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTextView;

        public NewsFeedViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.tv_title);
            imageView = (ImageView) view.findViewById(R.id.iv_thumbnai);
        }
    }

}
