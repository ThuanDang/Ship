package com.example.mrt.ship.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mrt.ship.R;

import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.preferences.OrdersDiffCallback;
import com.example.mrt.ship.utils.FormatUtils;


import java.util.List;
import java.util.Random;


/**
 * Created by mrt on 15/10/2016.
 * RecyclerView Adapter for OrdersFragment
 */

// Create adapter extends RecyclerView.Adapter with specific ViewHolder
public class RcvOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Order> data;
    private Context context;
    // Interface click item
    private static OnItemClickListener item_listener;
    public interface OnItemClickListener{
        void onItemOrderClick(View itemView, int position);
        void onItemErrorClick(View itemView, int position);
    }
    // Set listener method
    public void setOnItemClickListener(OnItemClickListener listener){
        this.item_listener = listener;
    }


    // constructor
    public RcvOrdersAdapter(Context context, List<Order> data){
        this.context = context;
        this.data = data;
    }

    private static class ProgressVH extends RecyclerView.ViewHolder{

        ProgressVH(View itemView) {
            super(itemView);
        }
    }

    private static class ErrorVH extends RecyclerView.ViewHolder{

        ErrorVH(final View itemView) {
            super(itemView);
            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item_listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            item_listener.onItemErrorClick(itemView, position);
                    }
                }
            });
        }
    }

    private static class OrderVH extends RecyclerView.ViewHolder{

        //Member variable of itemView
        View image_order;
        TextView text_image_order, name_order, place_receiver, place_delivery,
        ship_cost;

        OrderVH(final View itemView) {
            super(itemView);
            text_image_order = (TextView)itemView.findViewById(R.id.text_image_oder);
            image_order = itemView.findViewById(R.id.icon_order);
            name_order = (TextView)itemView.findViewById(R.id.name_order);
            place_receiver = (TextView)itemView.findViewById(R.id.place_receiver);
            place_delivery = (TextView)itemView.findViewById(R.id.place_delivery);
            ship_cost = (TextView)itemView.findViewById(R.id.ship_cost);
            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item_listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            item_listener.onItemOrderClick(itemView, position);
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
        }else if (order.getName() != null){
            return 1;
        }else {
            return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        if(viewType == 1){
            View view = inflater.inflate(R.layout.item_list_order, parent, false);
            return new OrderVH(view);
        }else if (viewType == 0){
            View view = inflater.inflate(R.layout.item_progress, parent, false);
            return new ProgressVH(view);
        }else {
            View view = inflater.inflate(R.layout.error_form, parent, false);
            view.setVisibility(View.VISIBLE);
            return new ErrorVH(view);
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

            text_image_order.setText(order.getName().substring(0, 1));
            name_order.setText(order.getName());
            place_receiver.setText(order.getFrom_address().getName());
            place_delivery.setText(order.getTo_address().getName());
            ship_cost.setText(FormatUtils.formatCurrency(order.getPrice(), FormatUtils.US));
            // Set random color icon
            Drawable background = image_order.getBackground();
            int[] androidColors = context.getResources().getIntArray(R.array.androidcolors);
            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            ((GradientDrawable)background).setColor(randomAndroidColor);
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void swapItems(List<Order> orders) {
        // compute diffs
        final OrdersDiffCallback diffCallback = new OrdersDiffCallback(this.data, orders);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        // clear and add
        this.data.clear();
        this.data.addAll(orders);

        diffResult.dispatchUpdatesTo(this); // calls adapter's notify methods after diff is computed
    }

    public List<Order> getData() {
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }
}
