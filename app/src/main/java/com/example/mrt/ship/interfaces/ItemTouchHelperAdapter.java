package com.example.mrt.ship.interfaces;

/**
 * Created by mrt on 25/11/2016.
 */
public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position, int direction);

    void onItemDrag(boolean x);
}
