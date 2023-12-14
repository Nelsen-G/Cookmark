package com.example.cookmark_app.adapter.cookmark_app.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarginCardItemDecoration extends RecyclerView.ItemDecoration {

    private int margin;

    public MarginCardItemDecoration(int margin) {
        this.margin = margin;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();

        if (position == 0) {
            outRect.left = margin;
        } else if (position == itemCount - 1) {
            outRect.right = margin;
        } else {
            outRect.left = margin;
            outRect.right = margin;
        }
    }
}
