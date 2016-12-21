package com.example.mrt.ship.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.mrt.ship.R;
import com.example.mrt.ship.activities.AboutActivity;
import com.example.mrt.ship.activities.EventActivity;
import com.example.mrt.ship.activities.LoginActivity;
import com.example.mrt.ship.activities.MainActivity;
import com.example.mrt.ship.activities.NoteActivity;
import com.example.mrt.ship.adapters.OptionsAdapter;
import com.example.mrt.ship.interfaces.HideViewScrollerListener;
import com.example.mrt.ship.interfaces.OnFragmentOptionsListener;
import com.example.mrt.ship.interfaces.OnFragmentOrdersListener;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.models.Shipper;
import com.example.mrt.ship.networks.MyApi;
import com.example.mrt.ship.networks.Token;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mrt on 22/11/2016.
 */

public class OptionsFragment extends Fragment{

    @BindView(R.id.name) TextView name;
    @BindView(R.id.email) TextView email;
    @BindView(R.id.avatar) ImageView avatar;
    @BindView(R.id.toggle_get_notifications) ToggleButton toggle_notifications;
    @BindView(R.id.list_options) RecyclerView listOptions;
    @BindView(R.id.rating) RatingBar rating;

    private OnFragmentOptionsListener mListener;
    private OptionsAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options, container, false);
        ButterKnife.bind(this, view);
        toggle_notifications.setChecked(true);
        setListOptions();
        getInfo();

        toggle_notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    MainActivity.nativePusher.subscribe("kittens");
                }else {
                    MainActivity.nativePusher.unsubscribe("kittens");
                }

            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentOrdersListener) {
            mListener = (OnFragmentOptionsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentOrdersListener");
        }
    }

    public void setListOptions(){
        adapter = new OptionsAdapter(getActivity());
        listOptions.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOptions.setAdapter(adapter);

        // set hide tab
        listOptions.addOnScrollListener(new HideViewScrollerListener() {
            @Override
            public void onHide() {
                mListener.hideTab();
            }

            @Override
            public void onShow() {
                mListener.showTab();
            }
        });

        adapter.setOnItemClickListener(new OptionsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                switch (position){
                    case 1: note(); break;
                    case 7: logout(); break;
                    case 2: support(); break;
                    case 4: share(); break;
                    case 6: about(); break;
                    case 0: event(); break;
                }
            }
        });
    }

    public void logout(){
        Token.remove(getContext());
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_to_left_1,
                R.anim.right_to_left_2);
        getActivity().finish();
    }

    public void note(){
        Intent intent = new Intent(getActivity(), NoteActivity.class);
        startActivity(intent);
    }

    public void support(){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:123456789"));
        startActivity(callIntent);
    }

    public void share(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "hust.edu.vn");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void about(){
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
    }


    public void getInfo(){
        MyApi.getInstance().getInfo(Token.share(getContext()))
                .enqueue(new Callback<Shipper>() {
                    @Override
                    public void onResponse(Call<Shipper> call, Response<Shipper> response) {
                        Shipper shipper = response.body();
                        if(shipper != null){
                            name.setText(shipper.getName());
                            email.setText(shipper.getEmail());
                            rating.setRating(shipper.getLevel());

                        }
                    }

                    @Override
                    public void onFailure(Call<Shipper> call, Throwable t) {

                    }
                });
    }

    public void event(){
        Intent intent = new Intent(getActivity(), EventActivity.class);
        startActivity(intent);
    }
}
