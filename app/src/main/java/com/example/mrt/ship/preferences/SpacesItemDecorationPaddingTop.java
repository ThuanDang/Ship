package com.example.mrt.ship.preferences;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.mrt.ship.utils.FormatUtils;

/**
 * Created by mrt on 26/11/2016.
 */

public class SpacesItemDecorationPaddingTop extends RecyclerView.ItemDecoration{
    private int space;
    private int top;
    private Context context;
    public SpacesItemDecorationPaddingTop(Context context, int space, int top) {
        this.space = space;
        this.top = top;
        this.context = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = (int)FormatUtils.dipToPixels(context, top) + space;
        } else {
            outRect.top = 0;
        }
    }
}
