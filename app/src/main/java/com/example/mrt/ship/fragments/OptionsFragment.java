package com.example.mrt.ship.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.mrt.ship.R;
import com.example.mrt.ship.activities.LoginActivity;
import com.example.mrt.ship.activities.NoteActivity;
import com.example.mrt.ship.adapters.RcvOptionsAdapter;
import com.example.mrt.ship.preferences.HideScrollListener;
import com.example.mrt.ship.preferences.OnFragmentOptionsScrollListener;
import com.example.mrt.ship.preferences.OnFragmentOrdersListener;

/**
 * Created by mrt on 22/11/2016.
 */

public class OptionsFragment extends Fragment{
    private OnFragmentOptionsScrollListener mListener;
    private RcvOptionsAdapter adapter;
    private RecyclerView listOptions;
    private ImageView avatar;
    private TextView name;
    private TextView email;
    private ToggleButton get_notifications;
    private SharedPreferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_options, container, false);

        listOptions = (RecyclerView)view.findViewById(R.id.list_options);
        setListOptions();


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentOrdersListener) {
            mListener = (OnFragmentOptionsScrollListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentOrdersListener");
        }
    }

    public void setListOptions(){
        adapter = new RcvOptionsAdapter(getActivity());
        listOptions.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOptions.setAdapter(adapter);

        // set hide tab
        listOptions.addOnScrollListener(new HideScrollListener() {
            @Override
            public void onHide() {
                mListener.hideTab();
            }

            @Override
            public void onShow() {
                mListener.showTab();
            }
        });

        adapter.setOnItemClickListener(new RcvOptionsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                switch (position){
                    case 1: note(); break;
                    case 7: logout(); break;
                }
            }
        });
    }

    public void logout(){
        preferences.edit().remove("token").apply();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_to_left_1, R.anim.right_to_left_2);
        getActivity().finish();
    }

    public void note(){
        Intent intent = new Intent(getActivity(), NoteActivity.class);
        startActivity(intent);
    }
}
