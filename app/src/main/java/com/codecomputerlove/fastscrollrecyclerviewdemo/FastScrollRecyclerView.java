package com.codecomputerlove.fastscrollrecyclerviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.codecomputerlove.fastscrollrecyclerviewdemo.interfaces.FastScrollRecyclerViewSupport;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created by flaviusmester on 23/02/15.
 */
@Accessors(prefix = "m")
public class FastScrollRecyclerView extends RecyclerView {
    private boolean mIsConfigured;

    @Getter
    private String mFsCurrentSection;
    @Getter
    private float mFsStartingX;
    @Getter
    private float mFsStartingY;
    @Getter
    private boolean mFsShouldBeShown;
    @Getter
    private String[] mFsSections;
    @Getter
    private int mFsItemWidth;
    @Getter
    private int mFsItemHeight;
    @Getter
    private float mFsItemWidthPx;
    @Getter
    private float mFsItemHeightPx;

    public FastScrollRecyclerView(Context context) {
        super(context);

        setupDefaults();
    }

    public FastScrollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setupDefaults();
    }

    public FastScrollRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setupDefaults();
    }

    @Override
    public void onDraw(Canvas c) {
        if (!mIsConfigured) {
            configure();
        }

        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (isCoordinateOutsideRange(x, y))
                    return super.onTouchEvent(event);
                else {
                    mFsCurrentSection = getFsSections()[getCurrentPosition(y - this.getPaddingTop() - getPaddingBottom() - getFsStartingY())]
                            .toUpperCase();

                    scrollToPosition();
                }

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!mFsShouldBeShown && isCoordinateOutsideRange(x, y))
                    return super.onTouchEvent(event);
                else {
                    mFsCurrentSection = getFsSections()[getCurrentPosition(y - getFsStartingY())]
                            .toUpperCase();

                    scrollToPosition();
                }

                break;
            }
            case MotionEvent.ACTION_UP: {
                new Handler().postDelayed(new NotifierRunnable(this), 100);


                return !isCoordinateOutsideRange(x, y) || super.onTouchEvent(event);
            }
        }

        return true;
    }

    private void setupDefaults() {
        mFsItemWidth = 24;
        mFsItemHeight = 24;
        mFsCurrentSection = "";
    }

    private void configure() {
        Set<String> sectionSet = ((FastScrollRecyclerViewSupport) getAdapter()).getMapIndex().keySet();
        ArrayList<String> listSection = new ArrayList<>(sectionSet);
        Collections.sort(listSection);

        mFsSections = new String[listSection.size()];

        for (int i = 0; i < mFsSections.length; i++) {
            mFsSections[i] = listSection.get(i).toUpperCase();
        }

        mFsItemWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getFsItemWidth(),
                getContext().getResources().getDisplayMetrics());
        mFsItemHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getFsItemHeight(),
                getContext().getResources().getDisplayMetrics());

        mFsStartingX = this.getWidth() - this.getPaddingRight() - getFsItemWidthPx();

        mFsStartingY = (float) ((this.getHeight() - (getFsItemHeightPx() * getFsSections().length)) / 2.0);

        if (mFsStartingY < 0) {
            mFsStartingY = 0;
        }

        mIsConfigured = true;
    }

    private int getCurrentPosition(float yy) {
        int currentPosition = (int) Math.floor(yy / getFsItemHeightPx());

        if (currentPosition < 0) {
            currentPosition = 0;
        } else if (currentPosition >= getFsSections().length) {
            currentPosition = getFsSections().length - 1;
        }

        return currentPosition;
    }

    private void scrollToPosition() {
        mFsShouldBeShown = true;

        int positionInData = 0;
        if (((FastScrollRecyclerViewSupport) getAdapter()).getMapIndex().containsKey(mFsCurrentSection.toUpperCase())) {
            positionInData = ((FastScrollRecyclerViewSupport) getAdapter()).getMapIndex().get(mFsCurrentSection.toUpperCase());
        }

        //influenced by https://github.com/appukrb
        getLayoutManager().scrollToPosition(positionInData);

        invalidate();
    }

    private boolean isCoordinateOutsideRange(float x, float y) {
        return x < getFsStartingX() - getFsItemWidthPx() ||
                y < getFsStartingY() ||
                y > (getFsStartingY() + getFsItemHeightPx() * getFsSections().length);
    }

    private static class NotifierRunnable implements Runnable {
        private WeakReference<FastScrollRecyclerView> rvReference;

        public NotifierRunnable(FastScrollRecyclerView value) {
            rvReference = new WeakReference<>(value);
        }

        @Override
        public void run() {
            FastScrollRecyclerView rv = rvReference.get();

            if (rv != null) {
                rv.mFsShouldBeShown = false;
                rv.invalidate();
            }
        }
    }
}
