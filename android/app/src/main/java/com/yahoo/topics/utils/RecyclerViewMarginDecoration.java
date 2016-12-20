package com.yahoo.topics.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Handle mMargin for recycler view
 */
public class RecyclerViewMarginDecoration extends RecyclerView.ItemDecoration {
    private final int mMarginLeft;
    private final int mMarginTop;
    private final int mMarginRight;
    private final int mMarginBottom;

    public RecyclerViewMarginDecoration(int marginLeft, int marginTop, int marginRight,
            int marginBottom) {
        mMarginLeft = marginLeft;
        mMarginTop = marginTop;
        mMarginRight = marginRight;
        mMarginBottom = marginBottom;
    }

    public static RecyclerViewMarginDecoration fromDimenId(@NonNull Context context,
            @DimenRes int dimen) {
        int pixelSize = context.getResources().getDimensionPixelSize(dimen);
        return new RecyclerViewMarginDecoration(pixelSize, pixelSize, pixelSize, pixelSize);
    }

    public static RecyclerViewMarginDecoration fromDimenIds(
            @NonNull Context context,
            @DimenRes int dimenLeft,
            @DimenRes int dimenTop,
            @DimenRes int dimenRight,
            @DimenRes int dimenBottom) {

        Resources resources = context.getResources();
        int pixelLeftSize = dimenLeft == 0 ? 0 : resources.getDimensionPixelSize(dimenLeft);
        int pixelTopSize = dimenTop == 0 ? 0 : resources.getDimensionPixelSize(dimenTop);
        int pixelRightSize = dimenRight == 0 ? 0 : resources.getDimensionPixelSize(dimenRight);
        int pixelBottomSize = dimenBottom == 0 ? 0 : resources.getDimensionPixelSize(dimenBottom);

        return new RecyclerViewMarginDecoration(pixelLeftSize, pixelTopSize, pixelRightSize,
                pixelBottomSize);
    }

    public static RecyclerViewMarginDecoration fromDimenIdForBottom(@NonNull Context context,
            @DimenRes int dimenBottom) {
        return fromDimenIds(context, 0, 0, 0, dimenBottom);
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(mMarginLeft, mMarginTop, mMarginRight, mMarginBottom);
    }
}
