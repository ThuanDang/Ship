package com.example.mrt.ship.adapters;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mrt.ship.R;
import com.example.mrt.ship.preferences.NotesDiffCallback;
import com.example.mrt.ship.utils.FontUtils;
import com.example.mrt.ship.models.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrt on 16/10/2016.
 */

public class RcvNotesAdapter extends RecyclerView.Adapter<RcvNotesAdapter.ViewHolder>{
    private Context context;
    private List<Note> data = new ArrayList<>();


    // Interface click options menu
    private static OnOptionMenuClickListener menu_listener;
    public interface OnOptionMenuClickListener{
        void onDeleteClick(int position);
        void onUpdateClick(int position);
    }

    public void setOnOptionMenuListener(OnOptionMenuClickListener listener){
        this.menu_listener = listener;
    }

    // Constructor
    public RcvNotesAdapter(Context context){
        this.context = context;
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder{
        // View element
        TextView header_note, content_note, date_note, options;
        public ViewHolder(final View itemView) {
            super(itemView);
            options = (TextView)itemView.findViewById(R.id.textViewOptions);
            header_note = (TextView)itemView.findViewById(R.id.header_note);
            content_note = (TextView)itemView.findViewById(R.id.content_note);
            date_note = (TextView)itemView.findViewById(R.id.date_note);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_list_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final TextView header_note, content_note, date_note, options;

        options = holder.options;
        header_note = holder.header_note;
        content_note = holder.content_note;
        date_note = holder.date_note;
        // Set typeface
        FontUtils.from(context).applyFontToTextView(header_note, "Roboto-Light.ttf");
        FontUtils.from(context).applyFontToTextView(content_note, "Roboto-Light.ttf");
        // Set text
        header_note.setText(data.get(position).getHeader());
        content_note.setText(data.get(position).getContent());
        date_note.setText(data.get(position).getDate());

        // Setup the options click listener
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, options);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu_note);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.delete_note:
                                // handle delete
                                if(menu_listener != null){
                                    menu_listener.onDeleteClick(holder.getAdapterPosition());
                                }

                                break;

                            case R.id.edit_note:
                                //handle update
                                if(menu_listener != null){
                                    menu_listener.onUpdateClick(holder.getAdapterPosition());
                                }

                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void swapItems(List<Note> notes) {
        // compute diffs
        final NotesDiffCallback diffCallback = new NotesDiffCallback(this.data, notes);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        // clear and add
        this.data = notes;

        diffResult.dispatchUpdatesTo(this); // calls adapter's notify methods after diff is computed
    }

    public List<Note> getData() {
        return data;
    }

    public void setData(List<Note> data) {
        this.data = data;
    }
}
