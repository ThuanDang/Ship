package com.example.mrt.ship.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mrt.ship.activities.DetailOrderActivity;
import com.example.mrt.ship.activities.ScheduleWayActivity;
import com.example.mrt.ship.adapters.ReceivedAdapter;
import com.example.mrt.ship.interfaces.HideViewScrollerListener;
import com.example.mrt.ship.interfaces.OnFragmentReceivedListener;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.networks.MyApi;
import com.example.mrt.ship.networks.Token;
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

    private List<Order> data;
    private ReceivedAdapter adapter;
    private OnFragmentReceivedListener listener;


    private Handler handler = new Handler();
    private boolean countable = false;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = preferences.getString("token", "");
        data = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.example.mrt.ship.R.layout.fragment_received_orders, container, false);

        receivedList = (RecyclerView)view.findViewById(com.example.mrt.ship.R.id.list);
        progressBar = (ProgressBar)view.findViewById(com.example.mrt.ship.R.id.progress);
        errorForm = view.findViewById(com.example.mrt.ship.R.id.error);
        task = (ProgressBar)view.findViewById(com.example.mrt.ship.R.id.progress_task);
        plan = (TextView)view.findViewById(com.example.mrt.ship.R.id.plan);

        setPlan();

        setList();

        setError();

       /* handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showProgress(true);
                fetchData();
            }
        }, 350);*/

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
        adapter = new ReceivedAdapter(getContext(), data);
        receivedList.setAdapter(adapter);
        receivedList.setLayoutManager(new LinearLayoutManager(getContext()));
        receivedList.setHasFixedSize(true);
        receivedList.addItemDecoration(new SpacesItemDecoration(10));
        receivedList.addOnScrollListener(new HideViewScrollerListener() {
            @Override
            public void onHide() {
                listener.hideTab();
            }

            @Override
            public void onShow() {
                listener.showTab();
            }
        });

        adapter.setOnItemClickListener(new ReceivedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent intent = new Intent(getActivity(), DetailOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("order", adapter.getData().get(position));
                intent.putExtras(bundle);
                startActivity(intent);
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
        Call<List<Order>> call = MyApi.getInstance().getReceivedOrders(Token.share(getContext()));
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                data = response.body();
                if(response.code() != 200){
                    showError(true);
                }else {
                    if(data != null){

                        if(countable){
                            listener.countOrders(data.size(), 1, countable);
                        }

                        task.setMax(data.size());
                        adapter.swapItems(data);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                            }
                        }, 300);

                    }
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                showError(true);
            }
        });
    }


    public void setPlan(){
        plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.size() != 0){
                    Intent intent = new Intent(getActivity(), ScheduleWayActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("data", (ArrayList<Order>)data);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            countable = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showProgress(true);
                    fetchData();
                }
            }, 350);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onStart() {
        super.onStart();
    }
}
