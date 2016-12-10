package com.example.mrt.ship.adapters;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrt.ship.R;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.preferences.OrdersDiffCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mrt on 10/12/2016.
 */

public class OrdersSuggestAdapter extends RecyclerView.Adapter<OrdersSuggestAdapter.ViewHolder>{
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
    public OrdersSuggestAdapter(Context context, List<Order> data){
        this.context = context;
        this.data = data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.icon) ImageView icon;
        @BindView(R.id.name_order) TextView name_order;
        @BindView(R.id.place_delivery) TextView place_delivery;
        @BindView(R.id.place_receiver) TextView place_receiver;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_suggest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the order model based on position
        Order order = data.get(position);

        // Set item views based on your views and order model
        holder.name_order.setText(order.getName());
        holder.place_receiver.setText(order.getWare_house().getAddress());
        holder.place_delivery.setText(order.getRecipient().getAddress());
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
