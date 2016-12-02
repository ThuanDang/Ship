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
import com.example.mrt.ship.interfaces.ItemTouchHelperAdapter;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.preferences.OrdersDiffCallback;
import com.example.mrt.ship.utils.FormatUtils;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by mrt on 25/11/2016.
 */

// Create adapter extends RecyclerView.Adapter with specific ViewHolder
public class RcvReceivedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Order> data;
    private Context context;

    private static OnItemClickListener item_listener;


    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        item_listener = listener;
    }

    // constructor
    public RcvReceivedAdapter(Context context, List<Order> data){
        this.context = context;
        this.data = data;
    }

    private static class OrderVH extends RecyclerView.ViewHolder{

        //Member variable of itemView
        View image_order;
        TextView text_image_order, name_order, place_receiver, place_delivery,
                ship_cost, deposit;

        OrderVH(final View itemView) {
            super(itemView);
            text_image_order = (TextView)itemView.findViewById(R.id.text_image_oder);
            image_order = itemView.findViewById(R.id.icon_order);
            name_order = (TextView)itemView.findViewById(R.id.name_order);
            place_receiver = (TextView)itemView.findViewById(R.id.place_receiver);
            place_delivery = (TextView)itemView.findViewById(R.id.place_delivery);
            ship_cost = (TextView)itemView.findViewById(R.id.ship_cost);
            deposit = (TextView)itemView.findViewById(R.id.deposit);

            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item_listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            item_listener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_list_received, parent, false);
        return new OrderVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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

            text_image_order.setText(String.valueOf(position + 1));
            name_order.setText(order.getName());
            place_receiver.setText(order.getWare_house().getAddress());
            place_delivery.setText(order.getRecipient().getAddress());
            ship_cost.setText(FormatUtils.formatCurrency(order.getShip_cost(), FormatUtils.VN));
            deposit.setText(FormatUtils.formatCurrency(order.getPrice(), FormatUtils.VN));
            // Set random color icon
            Drawable background = image_order.getBackground();
            int[] androidColors = context.getResources().getIntArray(R.array.androidcolors);
            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            ((GradientDrawable)background).setColor(randomAndroidColor);
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
        this.data = orders;

        diffResult.dispatchUpdatesTo(this); // calls adapter's notify methods after diff is computed
    }

    public List<Order> getData() {
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }
}