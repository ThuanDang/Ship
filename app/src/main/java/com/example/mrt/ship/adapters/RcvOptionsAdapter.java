package com.example.mrt.ship.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrt.ship.R;

import java.util.Random;

/**
 * Created by mrt on 22/11/2016.
 */

public class RcvOptionsAdapter extends RecyclerView.Adapter<RcvOptionsAdapter.ViewHolder> {

    private Context context;
    private int[] icons = {
            R.drawable.ic_options_events, R.drawable.ic_options_note, R.drawable.ic_options_support,
            R.drawable.ic_options_history, R.drawable.ic_options_share, R.drawable.ic_options_feedback,
            R.drawable.ic_options_about, R.drawable.ic_options_logout,


    };

    private String[] names = {
            "Sự kiện", "Ghi chú", "Trợ giúp", "Lịch sử các đơn hàng", "Chia sẻ", "Góp ý",
            "Về chúng tôi", "Đăng xuất"
    };

    public RcvOptionsAdapter(Context context){
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.item_list_options, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView icon_option;
        TextView name_option;

        icon_option = holder.icon_option;
        name_option = holder.name_option;

        //set values
        icon_option.setImageResource(icons[position]);
        name_option.setText(names[position]);


        // Set random color icon
        Drawable background = icon_option.getBackground();
        int[] androidColors = context.getResources().getIntArray(R.array.androidcolors);
        int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
        ((GradientDrawable)background).setColor(randomAndroidColor);
    }

    @Override
    public int getItemCount() {
        return icons.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon_option;
        private TextView name_option;

        public ViewHolder(final View itemView) {
            super(itemView);
            icon_option = (ImageView)itemView.findViewById(R.id.icon_option);
            name_option = (TextView)itemView.findViewById(R.id.name_option);

            // set click item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(item_listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            item_listener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }

    // Interface click item
    private static OnItemClickListener item_listener;
    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.item_listener = listener;
    }
}
