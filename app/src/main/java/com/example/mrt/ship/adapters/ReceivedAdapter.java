package com.example.mrt.ship.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrt.ship.R;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.preferences.OrdersDiffCallback;
import com.example.mrt.ship.utils.FormatUtil;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mrt on 25/11/2016.
 */

// Create adapter extends RecyclerView.Adapter with specific ViewHolder
public class ReceivedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
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
    public ReceivedAdapter(Context context, List<Order> data){
        this.context = context;
        this.data = data;
    }

    public static class OrderVH extends RecyclerView.ViewHolder{

        @BindView(R.id.text_image_oder) TextView text_image_order;
        @BindView(R.id.icon_order) View image_order;
        @BindView(R.id.name_order) TextView name_order;
        @BindView(R.id.place_receiver) TextView place_receiver;
        @BindView(R.id.place_delivery) TextView place_delivery;
        @BindView(R.id.ship_cost) TextView ship_cost;
        @BindView(R.id.deposit) TextView deposit;
        @BindView(R.id.status) TextView status;
        @BindView(R.id.check_completed) ImageView check;


        OrderVH(final View itemView) {
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
    public int getItemViewType(int position) {
        Order order = data.get(position);
        if(order != null){
            return 1;
        }else {
            return 0;
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
            OrderVH vh = (OrderVH) holder;

            // Set item views based on your views and order model
            TextView text_image_order = vh.text_image_order;
            View image_order = vh.image_order;
            TextView name_order = vh.name_order;
            TextView place_receiver = vh.place_receiver;
            TextView place_delivery = vh.place_delivery;
            TextView ship_cost = vh.ship_cost;
            TextView deposit = vh.deposit;
            TextView status = vh.status;
            ImageView check = vh.check;

            text_image_order.setText(String.valueOf(position + 1));
            name_order.setText(order.getName());
            place_receiver.setText(order.getWare_house().getAddress());
            place_delivery.setText(order.getRecipient().getAddress());
            ship_cost.setText(FormatUtil.formatCurrency(order.getShip_cost(), FormatUtil.VN));
            deposit.setText(FormatUtil.formatCurrency(order.getPrice(), FormatUtil.VN));
            // Set random color icon
            Drawable background = image_order.getBackground();
            int[] androidColors = context.getResources().getIntArray(com.example.mrt.ship.R.array.colors);
            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            ((GradientDrawable) background).setColor(randomAndroidColor);

            catchStatus(status, check, order.getStatus());
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

    public void catchStatus(TextView view, ImageView check,  int status){
        switch (status){
            case 2: view.setText("Chưa lấy hàng");
                view.setTextColor(Color.argb(255, 244, 67, 54));
                break;
            case 3: view.setText("Đang đợi xác nhận lấy hàng...");
                    view.setTextColor(Color.argb(242, 66, 165, 245));
                break;
            case 4: view.setText("Đã lấy hàng, chưa giao hàng");
                    view.setTextColor(Color.argb(255, 76, 175, 80));
                break;
            case 5: view.setText("Đang đợi thanh toán...");
                view.setTextColor(Color.argb(242, 66, 165, 245));
                check.setVisibility(View.VISIBLE);
                break;
        }
    }
}