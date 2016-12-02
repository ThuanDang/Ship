package com.example.mrt.ship.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mrt.ship.R;
import com.example.mrt.ship.adapters.RcvReceivedAdapter;
import com.example.mrt.ship.interfaces.HideScrollListener;
import com.example.mrt.ship.interfaces.OnFragmentReceivedListener;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.networks.ApiInterface;
import com.example.mrt.ship.networks.GetJson;
import com.example.mrt.ship.networks.RESTfulApi;
import com.example.mrt.ship.preferences.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mrt on 11/10/2016.
 */

public class ReceivedOrdersFragment extends Fragment {

    private RecyclerView receivedList;
    private ProgressBar progressBar;
    private ProgressBar task;
    private View errorForm;
    private String token;
    private TextView plan;

    private ApiInterface api;
    private List<Order> data;
    private RcvReceivedAdapter adapter;
    private OnFragmentReceivedListener listener;


    private Handler handler = new Handler();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = RESTfulApi.getApi();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = preferences.getString("token", "");
        data = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_received_orders, container, false);

        receivedList = (RecyclerView)view.findViewById(R.id.list_order);
        progressBar = (ProgressBar)view.findViewById(R.id.progress);
        errorForm = view.findViewById(R.id.error_form);
        task = (ProgressBar)view.findViewById(R.id.progress_task);
        plan = (TextView)view.findViewById(R.id.plan);

        setList();

        setError();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showProgress(true);
                fetchData();
            }
        }, 350);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentReceivedListener){
            listener = (OnFragmentReceivedListener)context;
        }else {
            throw new RuntimeException(context.toString() +
                    " must implement OnFragmentReceivedListener");
        }
    }


    public void setList(){
        adapter = new RcvReceivedAdapter(getContext(), data);
        receivedList.setAdapter(adapter);
        receivedList.setLayoutManager(new LinearLayoutManager(getContext()));
        receivedList.setHasFixedSize(true);
        receivedList.addItemDecoration(new SpacesItemDecoration(10));
        receivedList.addOnScrollListener(new HideScrollListener() {
            @Override
            public void onHide() {
                listener.hideTab();
            }

            @Override
            public void onShow() {
                listener.showTab();
            }
        });

        adapter.setOnItemClickListener(new RcvReceivedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

            }
        });
    }


    public void setError(){
        errorForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                showError(false);
                fetchData();
            }
        });
    }


    private void showProgress(final boolean show) {
        if(progressBar.isShown() != show){
            int shortAnimTime = 300;

            receivedList.setVisibility(show ? View.GONE : View.VISIBLE);
            receivedList.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    receivedList.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
    }


    public void showError(boolean show){
        errorForm.setVisibility(show?View.VISIBLE:View.GONE);
        errorForm.animate().alpha(show?1:0).setDuration(300);
    }


    public void fetchData(){

        Call<List<Order>> call = api.getReceivedOrders("Bearer " + token);

        call.enqueue(new Callback<List<Order>>() {
            int status;
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                status = response.code();
                data = response.body();

                if(status != 200){
                    showError(true);
                }else {
                    if(data != null){
                        adapter.swapItems(data);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                            }
                        }, 300);

                        listener.countOrders(data.size(), 1);
                        task.setMax(data.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                showError(true);
            }
        });
    }

}
