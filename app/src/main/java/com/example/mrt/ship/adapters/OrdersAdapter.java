package com.example.mrt.ship.adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mrt.ship.R;
import com.example.mrt.ship.interfaces.ItemTouchHelperAdapter;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.preferences.OrdersDiffCallback;
import com.example.mrt.ship.utils.FormatUtil;


import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by mrt on 15/10/2016.
 * RecyclerView Adapter for OrdersFragment
 */

// Create adapter extends RecyclerView.Adapter with specific ViewHolder
public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {
    private List<Order> data;
    private Context context;
    private SwipeRefreshLayout refresh;
    private boolean noRefresh = true;

    // Interface click item
    private static OnItemClickListener click_listener;
    private static OnItemSwipedListener swiped_listener;

    public interface OnItemClickListener{
        void onItemOrderClick(View itemView, int position);
    }
    
    public interface OnItemSwipedListener {
        void onSwiped(int position, int direction);
    }

    // Set listener
    public void setOnItemClickListener(OnItemClickListener listener){
        click_listener = listener;
    }
    public void setOnItemSwipedListener(OnItemSwipedListener listener){
        swiped_listener = listener;
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemDismiss(int position, int direction) {
        swiped_listener.onSwiped(position, direction);

    }

    @Override
    public void onItemDrag(boolean x) {
        if(noRefresh != x){
            noRefresh = x;
            refresh.setEnabled(!x);
        }
    }


    // constructor
    public OrdersAdapter(Context context, List<Order> data, SwipeRefreshLayout refresh){
        this.context = context;
        this.data = data;
        this.refresh = refresh;
    }

    private static class SmoothVh extends RecyclerView.ViewHolder{
        public SmoothVh(View itemView) {
            super(itemView);
        }
    }

    private static class ProgressVH extends RecyclerView.ViewHolder{
        ProgressVH(View itemView) {
            super(itemView);
        }
    }


    public static class OrderVH extends RecyclerView.ViewHolder{

        @BindView(R.id.icon_order) View image_order;
        @BindView(R.id.text_image_oder) TextView text_image_order;
        @BindView(R.id.name_order) TextView name_order;
        @BindView(R.id.place_receiver) TextView place_receiver;
        @BindView(R.id.place_delivery) TextView place_delivery;
        @BindView(R.id.ship_cost) TextView ship_cost;
        @BindView(R.id.deposit) TextView deposit;

        OrderVH(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (click_listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            click_listener.onItemOrderClick(itemView, position);
                    }
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        Order order = data.get(position);
        if(order == null){
            return 0;
        }else if(order.getId() == -1){
            return -1;
        }else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        if(viewType == 1){
            View view = inflater.inflate(R.layout.item_list_order, parent, false);
            return new OrderVH(view);
        }else if(viewType == 0){
            View view = inflater.inflate(R.layout.item_progress_load_more, parent, false);
            return new ProgressVH(view);
        }else {
            View view = inflater.inflate(R.layout.item_progress_receive, parent, false);
            return new SmoothVh(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof OrderVH){

            // Get the order model based on position
            Order order = data.get(position);
            OrderVH vh = (OrderVH)holder;

            // Set item views based on your views and order model
            TextView text_image_order = vh.text_image_order;
            View image_order = vh.image_order;
            TextView name_order = vh.name_order;
            TextView place_receiver = vh.place_receiver;
            TextView place_delivery = vh.place_delivery;
            TextView ship_cost = vh.ship_cost;
            TextView deposit = vh.deposit;

            text_image_order.setText(
                    order.getName().isEmpty()?"": FormatUtil.deAccent(order.getName().substring(0, 1)));
            name_order.setText(order.getName());
            place_receiver.setText(order.getWare_house().getAddress());
            place_delivery.setText(order.getRecipient().getAddress());
            ship_cost.setText(FormatUtil.formatCurrency(order.getShip_cost(), FormatUtil.VN));
            deposit.setText(FormatUtil.formatCurrency(order.getPrice(), FormatUtil.VN));

            // Set random color icon
            Drawable background = image_order.getBackground();
            int[] androidColors = context.getResources().getIntArray(R.array.colors);
            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            ((GradientDrawable)background).setColor(randomAndroidColor);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void swapItems(List<Order> orders) {
        final OrdersDiffCallback diffCallback = new OrdersDiffCallback(this.data, orders);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.data = orders;
        diffResult.dispatchUpdatesTo(this);
    }

    public List<Order> getData() {
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }
}
