package com.kevin.zhihudaily.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevin.zhihudaily.Constants;
import com.kevin.zhihudaily.R;
import com.kevin.zhihudaily.model.Comment;
import com.kevin.zhihudaily.model.CommentsModel;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private WeakReference<Context> mContextWR;
    private List<ListItem> mItemList = null;
    private static final String TIME_FORMAT = "MM-dd hh:mm";

    public CommentListAdapter(Context context) {
        this.mContextWR = new WeakReference<Context>(context);
        mItemList = new ArrayList<ListItem>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == ListItem.SECTION) {
            view = LayoutInflater.from(mContextWR.get()).inflate(R.layout.comment_list_section, parent, false);
        } else if (viewType == ListItem.ITEM) {
            view = LayoutInflater.from(mContextWR.get()).inflate(R.layout.comment_list_item, parent, false);
        }
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final CommentViewHolder holder = (CommentViewHolder) viewHolder;
        setupItemView(holder, position);
    }

    @Override
    public void onClick(View v) {

    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatarView;
        public TextView authorTextView;
        public TextView timeTextView;
        public TextView contentTextView;
        public TextView likesTextView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            avatarView = (ImageView) itemView.findViewById(R.id.comment_item_avatar);
            authorTextView = (TextView) itemView.findViewById(R.id.comment_item_author);
            timeTextView = (TextView) itemView.findViewById(R.id.comment_item_time);
            contentTextView = (TextView) itemView.findViewById(R.id.comment_item_content);
            likesTextView = (TextView) itemView.findViewById(R.id.comment_item_likes);
        }
    }

    static class ListItem {
        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final String section;
        public final int sectionSize;
        public final Comment model;

        public ListItem(int type, Comment model, String section, int size) {
            this.type = type;
            this.model = model;
            this.section = section;
            this.sectionSize = size;
        }

        public Comment getModel() {
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

    }

    public void addListItem(CommentsModel commentsModel, int comment_type) {
        if (commentsModel != null) {
            List<Comment> commentList = commentsModel.getComments();
            int len = commentList.size();

            for (int j = 0; j < len; j++) {
                ListItem item = new ListItem(ListItem.ITEM, commentList.get(j), null, len);
                mItemList.add(item);
            }
        }

    }

    public void addListSection(CommentsModel commentsModel, int comment_type) {
        if (commentsModel != null) {
            List<Comment> commentList = commentsModel.getComments();
            int len = commentList.size();

            String sectionTitle = mContextWR.get().getString(R.string.long_comment);

            if (comment_type == Constants.COMMENT_TYPE_LONG) {
                sectionTitle = mContextWR.get().getString(R.string.long_comment);
            } else {
                sectionTitle = mContextWR.get().getString(R.string.short_comment);
            }

            ListItem section = new ListItem(ListItem.SECTION, null, sectionTitle, len);
            mItemList.add(section);
        }

    }

    public void updateList(CommentsModel commentsModel, int comment_type) {
        if (commentsModel != null) {

            addListSection(commentsModel, comment_type);

            addListItem(commentsModel, comment_type);
        }

        notifyDataSetChanged();
    }

    public void updateListandReset(CommentsModel commentsModel, int comment_type) {
        if (mItemList != null) {
            // clear all
            mItemList.clear();

            if (commentsModel != null) {
                addListSection(commentsModel, comment_type);

                addListItem(commentsModel, comment_type);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return mItemList.size();
    }

    public Object getItemByPosition(int position) {
        // TODO Auto-generated method stub
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        //        return super.getItemViewType(position);
        ListItem item = mItemList.get(position);
        return item.getType();
    }

    private void setupItemView(CommentViewHolder holder, int position) {
        if (mItemList == null || mItemList.size() == 0) {
            return;
        }
        ListItem item = mItemList.get(position);

        Comment model = item.getModel();
        if (item.getType() == ListItem.ITEM) {
            holder.authorTextView.setText(model.getAuthor());
            // format unix second to date
            holder.timeTextView.setText(formatTimeToString(model.getTime(), TIME_FORMAT));
            holder.contentTextView.setText(model.getContent());
            holder.likesTextView.setText(model.getLikes() + "");

            String url = model.getAvatar();
            if (url != null && !url.isEmpty()) {
                Picasso.with(mContextWR.get()).load(url).placeholder(R.drawable.spinner_76_inner_holo).fit()
                        .into(holder.avatarView);
            }
        } else {
            holder.contentTextView.setText(item.getSection() + "");
            holder.likesTextView.setText(item.getSectionSize() + "");
        }

    }

    private String formatTimeToString(int second, String format) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        // 防止精度丢失
        long timestamp = Long.valueOf(second) * 1000;
        timeString = dateFormat.format(new Date(timestamp));
        //        DebugLog.d("UNIX=" + second + "  TIME=" + timestamp + "  DATE=" + timeString);
        return timeString;
    }
}
