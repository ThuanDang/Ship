package com.example.mrt.ship.interfaces;

import android.support.v7.widget.RecyclerView;

/**
 * Created by mrt on 27/10/2016.
 */

public abstract class HideScrollListener extends RecyclerView.OnScrollListener {
    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if(scrolledDistance > HIDE_THRESHOLD && controlsVisible){
            onHide();
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible){
            onShow();
            controlsVisible = true;
            scrolledDistance = 0;
        }
        if((controlsVisible && dy>0)||(!controlsVisible && dy<0)){
            scrolledDistance+=dy;
        }

    }

    public abstract void onHide();
    public abstract void onShow();
}
