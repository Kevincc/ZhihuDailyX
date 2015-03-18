package com.kevin.zhihudaily.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ExScrollView extends ScrollView {

    /**
     * @author Kevin
     */
    public interface OnScrollChangedListener {
        void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    //    private GestureDetector mGestureDetector;
    // private View.OnTouchListener mOnTouchListener;

    public ExScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        //		mGestureDetector = new GestureDetector(context, new YScrollDetector());
        //		setFadingEdgeLength(0);
    }

    public ExScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ExScrollView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    //    @Override
    //    public boolean onInterceptTouchEvent(MotionEvent ev) {
    //        // TODO Auto-generated method stub
    //        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    //    }
    //
    //    // Return false if we're scrolling in the x direction
    //    class YScrollDetector extends SimpleOnGestureListener {
    //
    //        @Override
    //        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    //            // TODO Auto-generated method stub
    //            // return super.onScroll(e1, e2, distanceX, distanceY);
    //            if (Math.abs(distanceY) > Math.abs(distanceX)) {
    //                return true;
    //            }
    //            return false;
    //        }
    //    }
}
