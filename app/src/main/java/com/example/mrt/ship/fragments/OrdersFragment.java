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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.mrt.ship.R;
import com.example.mrt.ship.adapters.RcvOrdersAdapter;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.preferences.EndlessRecyclerViewScrollerListener;
import com.example.mrt.ship.preferences.HideScrollListener;
import com.example.mrt.ship.preferences.OnFragmentOrdersListener;
import com.example.mrt.ship.preferences.SpacesItemDecoration;
import com.example.mrt.ship.sync.ApiInterface;
import com.example.mrt.ship.sync.GetJson;
import com.example.mrt.ship.utils.ApiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mrt on 14/10/2016.
 */

public class OrdersFragment extends Fragment {
    private OnFragmentOrdersListener mListener;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvOrderList;
    private RcvOrdersAdapter adapter;
    private ProgressBar progressBar;
    private View errorForm;
    private List<Order> data;
    private ApiInterface api;
    private GetJson getJson;
    private String token;

    private EndlessRecyclerViewScrollerListener listener;

    private Handler handler = new Handler();

    public static OrdersFragment newInstance(){
        return new OrdersFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new ArrayList<>();
        api = ApiUtils.getApi();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = preferences.getString("token", "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        // swipe
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_orders);
        // progress bar
        progressBar = (ProgressBar)view.findViewById(R.id.progress_orders);
        // error form
        errorForm = view.findViewById(R.id.error_form);
        errorForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                showError(false);
                fetchData();
            }
        });
        // recycler view
        rvOrderList = (RecyclerView)view.findViewById(R.id.list_order);
        // config RecyclerView
        setRecyclerView();
        // config Swipe
        setSwipeRefresh();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showProgress(true);
                fetchData();
            }
        },400);



        return view;
    }


    //---------------------------------------------------------------------------------------------
    private void showProgress(final boolean show) {
        if(progressBar.isShown() != show){
            int shortAnimTime = 300;
            swipeContainer.setEnabled(!show);
            rvOrderList.setVisibility(show ? View.GONE : View.VISIBLE);
            rvOrderList.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rvOrderList.setVisibility(show ? View.GONE : View.VISIBLE);
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

//-----------------------------------------------------------------------------------------------
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentOrdersListener) {
            mListener = (OnFragmentOrdersListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentOrdersListener");
        }
    }

//----------------------------------------------------------------------------------------------
    public void setRecyclerView(){
        adapter = new RcvOrdersAdapter(getContext(), data);
        rvOrderList.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvOrderList.setLayoutManager(linearLayoutManager);
        rvOrderList.setHasFixedSize(true);
        rvOrderList.addItemDecoration(new SpacesItemDecoration(10));

        // set hide tab
        rvOrderList.addOnScrollListener(new HideScrollListener() {
            @Override
            public void onHide() {
                mListener.hideViews();
            }

            @Override
            public void onShow() {
                mListener.showViews();
            }
        });

        // set load more
        listener = new EndlessRecyclerViewScrollerListener(
                linearLayoutManager, adapter) {
            @Override
            public void onLoadMore(final int position) {
                final List<Order> adapterData = adapter.getData();
                if(getJson.getNext() != null){
                    // show progress bar
                    adapterData.add(position, null);
                    adapter.notifyItemInserted(position);

                    Call<GetJson> call = api.getListOrderMore("Token " + token, getJson.getNext());
                    call.enqueue(new Callback<GetJson>() {
                        int status;
                        @Override
                        public void onResponse(Call<GetJson> call, Response<GetJson> response) {
                            status = response.code();
                            getJson = response.body();

                            if(status != 200){
                                // remove progress bar and add error
                                adapterData.remove(position);
                                adapter.notifyItemRemoved(position);
                                adapterData.add(new Order());
                                adapter.notifyItemInserted(position);
                            }

                            else if(getJson != null){
                                // remove progress bar
                                adapter.getData().remove(adapterData.size() - 1);
                                adapter.notifyItemRemoved(adapter.getData().size());
                                // notify data change
                                data.addAll(getJson.getResults());
                                adapter.swapItems(data);

                                mListener.countOrders(getJson.getCount());
                            }
                        }

                        @Override
                        public void onFailure(Call<GetJson> call, Throwable t) {
                            // remove progress bar and add error
                            adapterData.remove(adapterData.size() - 1);
                            adapter.notifyItemRemoved(adapter.getData().size());
                            adapterData.add(new Order());
                            adapter.notifyItemInserted(adapterData.size() - 1);
                        }
                    });
                }
            }
        };
        rvOrderList.addOnScrollListener(listener);

        // set click for item in recycler view
        adapter.setOnItemClickListener(new RcvOrdersAdapter.OnItemClickListener() {
            @Override
            public void onItemOrderClick(View itemView, int position) {

            }

            @Override
            public void onItemErrorClick(View itemView, int position) {
                adapter.getData().remove(position);
                adapter.notifyItemRemoved(position);
                listener.onLoadMore(position);
            }
        });
    }

//----------------------------------------------------------------------------------------------
    public void setSwipeRefresh(){
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                fetchData();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                    }
                }, 400);

            }

        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
//------------------------------------------------------------------------------

    public void fetchData(){

       Call<GetJson> call = api.getListOrder("Token " + token);

        call.enqueue(new Callback<GetJson>() {
            int status;
            @Override
            public void onResponse(Call<GetJson> call, Response<GetJson> response) {
                status = response.code();
                getJson = response.body();

                if(status != 200){
                    showError(true);
                }else {
                    if(getJson != null){
                        data = getJson.getResults();
                        adapter.swapItems(data);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                            }
                        }, 300);

                        mListener.countOrders(getJson.getCount());

                    }
                }
            }

            @Override
            public void onFailure(Call<GetJson> call, Throwable t) {
                showError(true);
            }
        });
    }
//----------------------------------------------------------------------------------------------
    public void showError(boolean show){
        errorForm.setVisibility(show?View.VISIBLE:View.GONE);
        errorForm.animate().alpha(show?1:0).setDuration(300);
    }
//----------------------------------------------------------------------------------------------

}
