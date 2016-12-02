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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.mrt.ship.R;
import com.example.mrt.ship.adapters.RcvOrdersAdapter;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.interfaces.EndlessRecyclerViewScrollerListener;
import com.example.mrt.ship.interfaces.HideScrollListener;
import com.example.mrt.ship.interfaces.OnFragmentOrdersListener;
import com.example.mrt.ship.preferences.ItemTouchHelperCallback;
import com.example.mrt.ship.preferences.SpacesItemDecoration;
import com.example.mrt.ship.networks.ApiInterface;
import com.example.mrt.ship.networks.GetJson;
import com.example.mrt.ship.networks.RESTfulApi;
import com.example.mrt.ship.preferences.SpacesItemDecorationPaddingTop;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mrt on 14/10/2016.
 */

public class OrdersFragment extends Fragment {
    
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvOrderList;
    private ProgressBar progressBar;
    private View errorForm;
    
    private ApiInterface api;
    private GetJson getJson;
    private String token;
    private RcvOrdersAdapter adapter;
    private List<Order> data;
    private OnFragmentOrdersListener fragmentListener;
    private EndlessRecyclerViewScrollerListener endlessListener;

    private Handler handler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new ArrayList<>();
        api = RESTfulApi.getApi();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = preferences.getString("token", "");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_orders);
        progressBar = (ProgressBar)view.findViewById(R.id.progress);
        errorForm = view.findViewById(R.id.error_form);
        rvOrderList = (RecyclerView)view.findViewById(R.id.list_order);
        
        setList();
        setError();
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentOrdersListener) {
            fragmentListener = (OnFragmentOrdersListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentOrdersListener");
        }
    }

//---------------------------------------------------------------------------------------------

    public void setList(){
        adapter = new RcvOrdersAdapter(getContext(), data, swipeRefresh);
        rvOrderList.setAdapter(adapter);
        rvOrderList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrderList.setHasFixedSize(true);
        rvOrderList.addItemDecoration(new SpacesItemDecorationPaddingTop(getContext(), 10, 48));

        ItemTouchHelper touchHelper = new ItemTouchHelper(
                new ItemTouchHelperCallback(adapter, getContext(), true, false));
        touchHelper.attachToRecyclerView(rvOrderList);

        // set hide tab
        rvOrderList.addOnScrollListener(new HideScrollListener() {
            @Override
            public void onHide() {
                fragmentListener.hideTab();
                fragmentListener.hideSearch();
            }

            @Override
            public void onShow() {
                fragmentListener.showTab();
                fragmentListener.showSearch();
            }
        });

        // set load more
        endlessListener = new EndlessRecyclerViewScrollerListener(
                (LinearLayoutManager)rvOrderList.getLayoutManager(), adapter) {
            @Override
            public void onLoadMore(final int position) {
                final List<Order> adapterData = adapter.getData();
                if(getJson.getNext_page_url() != null){
                    // show progress bar
                    adapterData.add(position, null);
                    adapter.notifyItemInserted(position);

                    Call<GetJson> call = api.getListOrderMore("Bearer " + token, getJson.getNext_page_url());
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
                                data.addAll(getJson.getData());
                                adapter.swapItems(data);

                                fragmentListener.countOrders(getJson.getTotal(), 0);
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
        rvOrderList.addOnScrollListener(endlessListener);

        // set click for item in recycler view
        adapter.setOnItemClickListener(new RcvOrdersAdapter.OnItemClickListener() {
            @Override
            public void onItemOrderClick(View itemView, int position) {

            }

            @Override
            public void onItemErrorClick(View itemView, int position) {
                adapter.getData().remove(position);
                adapter.notifyItemRemoved(position);
                endlessListener.onLoadMore(position);
            }
        });
    }

//---------------------------------------------------------------------------------------------

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

//---------------------------------------------------------------------------------------------

    public void setSwipeRefresh(){
        // Setup refresh endlessListener which triggers new data loading
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                    }
                }, 400);

            }
        });
        // Configure the refreshing colors
        swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // Set margin to top
        swipeRefresh.setProgressViewOffset(false, 0, 120);
    }

//----------------------------------------------------------------------------------------------

    private void showProgress(final boolean show) {
        if(progressBar.isShown() != show){
            int shortAnimTime = 300;
            swipeRefresh.setEnabled(!show);
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

//-------------------------------------------------------------------------------------------------

    public void showError(boolean show){
        errorForm.setVisibility(show?View.VISIBLE:View.GONE);
        errorForm.animate().alpha(show?1:0).setDuration(300);
    }

//-------------------------------------------------------------------------------------------------

    public void fetchData(){

        Call<GetJson> call = api.getListOrder("Bearer " + token);

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
                        data = getJson.getData();
                        adapter.swapItems(data);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                            }
                        }, 300);

                        fragmentListener.countOrders(getJson.getTotal(), 0);

                    }
                }
            }

            @Override
            public void onFailure(Call<GetJson> call, Throwable t) {
                showError(true);
            }
        });
    }

}
