package com.codecomputerlove.fastscrollrecyclerviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by flaviusmester on 23/02/15.
 */
@Accessors(prefix = "m")
public class FastScrollRecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
    private static final int DEFAULT_TEXT_SIZE = 100;

    @Getter
    @Setter
    private float mTextSizePx;

    public FastScrollRecyclerViewItemDecoration(Context context) {
        mTextSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TEXT_SIZE,
                context.getResources().getDisplayMetrics());
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        // We draw the letter in the middle
        if (((FastScrollRecyclerView) parent).isFsShouldBeShown() &&
                !((FastScrollRecyclerView) parent).getFsCurrentSection().equals("")) {

            //overlay everything when displaying selected index Letter in the middle
            Paint overlayDark = new Paint();
            overlayDark.setColor(Color.BLACK);
            overlayDark.setAlpha(100);

            canvas.drawRect(0, 0, parent.getWidth(), parent.getHeight(), overlayDark);

            Paint middleLetter = new Paint();
            middleLetter.setColor(Color.WHITE);
            middleLetter.setTextSize(getTextSizePx());
            middleLetter.setAntiAlias(true);
            middleLetter.setFakeBoldText(true);
            middleLetter.setStyle(Paint.Style.FILL);

            int xPos = (canvas.getWidth() - (int) getTextSizePx()) / 2;
            int yPos = (int) ((canvas.getHeight() / 2) - ((middleLetter.descent() + middleLetter.ascent()) / 2));


            canvas.drawText(((FastScrollRecyclerView) parent).getFsCurrentSection(), xPos, yPos, middleLetter);
        }

        // draw indez A-Z
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < ((FastScrollRecyclerView) parent).getFsSections().length; i++) {
            if (((FastScrollRecyclerView) parent).isFsShouldBeShown() &&
                    !((FastScrollRecyclerView) parent).getFsCurrentSection().equals("") &&
                    ((FastScrollRecyclerView) parent).getFsSections()[i].equals(
                            ((FastScrollRecyclerView) parent).getFsCurrentSection())) {

                textPaint.setColor(Color.WHITE);
                textPaint.setAlpha(255);
                textPaint.setFakeBoldText(true);
                textPaint.setTextSize((((FastScrollRecyclerView) parent).getFsItemWidthPx() / 2));

                canvas.drawText(((FastScrollRecyclerView) parent).getFsSections()[i],
                        ((FastScrollRecyclerView) parent).getFsStartingX() + textPaint.getTextSize() / 2,
                        ((FastScrollRecyclerView) parent).getFsStartingY() + parent.getPaddingTop() +
                                ((FastScrollRecyclerView) parent).getFsItemHeightPx() * (i + 1), textPaint);

                textPaint.setTextSize((((FastScrollRecyclerView) parent).getFsItemWidthPx()));
                canvas.drawText("•",
                        ((FastScrollRecyclerView) parent).getFsStartingX() - textPaint.getTextSize() / 3,
                        ((FastScrollRecyclerView) parent).getFsStartingY() + parent.getPaddingTop() +
                                ((FastScrollRecyclerView) parent).getFsItemHeightPx() * (i + 1) +
                                ((FastScrollRecyclerView) parent).getFsItemHeightPx() / 3, textPaint);

            } else {
                textPaint.setColor(Color.LTGRAY);
                textPaint.setAlpha(200);
                textPaint.setFakeBoldText(false);
                textPaint.setTextSize(((FastScrollRecyclerView) parent).getFsItemWidthPx() / 2);
                canvas.drawText(((FastScrollRecyclerView) parent).getFsSections()[i],
                        ((FastScrollRecyclerView) parent).getFsStartingX() + textPaint.getTextSize() / 2,
                        ((FastScrollRecyclerView) parent).getFsStartingY() + parent.getPaddingTop() +
                                ((FastScrollRecyclerView) parent).getFsItemHeightPx() * (i + 1), textPaint);
            }
        }
    }
}
