package com.example.mrt.ship.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mrt.ship.R;
import com.example.mrt.ship.sync.ApiInterface;
import com.example.mrt.ship.sync.GetJson;
import com.example.mrt.ship.utils.ApiUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mrt on 11/10/2016.
 */

public class ReceivedOrdersFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private ApiInterface api;
    private int mPage;
    private String token;
    private SharedPreferences preferences;

    public static ReceivedOrdersFragment newInstance(int page){
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ReceivedOrdersFragment fragment = new ReceivedOrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        api = ApiUtils.getApi();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = preferences.getString("token", "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_received_orders, container, false);
        return view;
    }
}
