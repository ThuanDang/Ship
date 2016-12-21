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
import com.example.mrt.ship.utils.FormatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mrt on 15/12/2016.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<Order> data = new ArrayList<>();
    private Context context;
    private static OrdersAdapter.OnItemClickListener click_listener;

    public SearchAdapter(Context context){
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemOrderClick(View itemView, int position);
    }

    public void setOnItemClickListener(OrdersAdapter.OnItemClickListener listener){
        click_listener = listener;
    }

    public List<Order> getData() {
        return data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }
    
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_list_order, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Order order = data.get(position);

        // Set item views based on your views and order model
        TextView text_image_order = holder.text_image_order;
        View image_order = holder.image_order;
        TextView name_order = holder.name_order;
        TextView place_receiver = holder.place_receiver;
        TextView place_delivery = holder.place_delivery;
        TextView ship_cost = holder.ship_cost;
        TextView deposit = holder.deposit;

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

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon_order) View image_order;
        @BindView(R.id.text_image_oder) TextView text_image_order;
        @BindView(R.id.name_order) TextView name_order;
        @BindView(R.id.place_receiver) TextView place_receiver;
        @BindView(R.id.place_delivery) TextView place_delivery;
        @BindView(R.id.ship_cost) TextView ship_cost;
        @BindView(R.id.deposit) TextView deposit;
        
        public ViewHolder(final View itemView) {
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


    public void swapItems(List<Order> orders) {
        final OrdersDiffCallback diffCallback = new OrdersDiffCallback(this.data, orders);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.data = orders;
        diffResult.dispatchUpdatesTo(this);
    }
}
