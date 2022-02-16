package de.coop.tgvertretung.adapter;

import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    private final int touchSlop;
    private float prevX;

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                MotionEvent e = MotionEvent.obtain(event);
                prevX = e.getX();
                e.recycle();
                break;

            case MotionEvent.ACTION_MOVE:
                float eventX = event.getX();
                float xDiff = Math.abs(eventX - prevX);
                if (xDiff > touchSlop) return false;
        }

        return super.onInterceptTouchEvent(event);
    }
}