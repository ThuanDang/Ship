package com.example.mrt.ship.preferences;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.mrt.ship.R;
import com.example.mrt.ship.interfaces.ItemTouchHelperAdapter;

/**
 * Created by mrt on 25/11/2016.
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    private Paint p;
    private Context context;
    private boolean swipe;
    private boolean drag;


    public ItemTouchHelperCallback(ItemTouchHelperAdapter adapter,
                                   Context context,
                                   boolean swipe,
                                   boolean drag) {
        mAdapter = adapter;
        this.context = context;
        this.drag = drag;
        this.swipe = swipe;
        if(swipe){
            p = new Paint();
            p.setARGB(255, 244, 67, 54);
        }

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return drag;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return swipe;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition(), direction);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        mAdapter.onItemDrag(isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Get RecyclerView item from the ViewHolder

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            if (dX > 0) {
                // Draw Rect with varying right side, equal to displacement dX
                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(),
                        itemView.getLeft() + dX,
                        (float) itemView.getBottom(), p);

                Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                        com.example.mrt.ship.R.drawable.add);
                RectF icon_dest = new RectF((float) itemView.getLeft() + width ,
                        (float) itemView.getTop() + width,
                        (float) itemView.getLeft()+ 2*width,
                        (float)itemView.getBottom() - width);
                c.drawBitmap(icon, null,icon_dest,p);


            } else {
                /* Set your color for negative displacement */

                // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                        (float) itemView.getRight(), (float) itemView.getBottom(), p);


                Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.add);
                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width,
                        (float) itemView.getTop() + width,
                        (float) itemView.getRight() - width,
                        (float)itemView.getBottom() - width);
                c.drawBitmap(icon, null,icon_dest,p);
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

}
