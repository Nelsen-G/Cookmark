package com.example.cookmark_app.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MarginSnapHelper extends LinearSnapHelper {

    private int marginStart = 20;

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        out[0] = layoutManager.getPosition(targetView) == 0 ? 0 : marginStart;
        out[1] = 0;
        return out;
    }

    private int distanceToStart(View targetView, OrientationHelper helper) {
        return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
    }

    private OrientationHelper getHorizontalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollHorizontally()) {
            return OrientationHelper.createHorizontalHelper(layoutManager);
        } else {
            return null;
        }
    }

    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return OrientationHelper.createVerticalHelper(layoutManager);
        } else {
            return null;
        }
    }
}
