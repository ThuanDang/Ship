package com.example.mrt.ship.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.mrt.ship.R;
import com.example.mrt.ship.activities.DetailOrderActivity;
import com.example.mrt.ship.activities.MainActivity;
import com.example.mrt.ship.adapters.OrdersAdapter;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.interfaces.EndlessRecyclerViewScrollerListener;
import com.example.mrt.ship.interfaces.HideViewScrollerListener;
import com.example.mrt.ship.interfaces.OnFragmentOrdersListener;
import com.example.mrt.ship.networks.MyApi;
import com.example.mrt.ship.networks.Token;
import com.example.mrt.ship.preferences.ItemTouchHelperCallback;
import com.example.mrt.ship.networks.Result;
import com.example.mrt.ship.preferences.SpacesItemDecorationPaddingTop;
import com.example.mrt.ship.utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mrt on 14/10/2016.
 */

public class OrdersFragment extends Fragment {

    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.list) RecyclerView orders;
    @BindView(R.id.progress) ProgressBar progress;
    @BindView(R.id.error) View error;


    private Result result;
    private OrdersAdapter adapter;
    private List<Order> data;
    private OnFragmentOrdersListener fragmentListener;
    private EndlessRecyclerViewScrollerListener endlessListener;

    private Handler handler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new ArrayList<>();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        ButterKnife.bind(this, view);

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
        adapter = new OrdersAdapter(getContext(), data, refresh);
        orders.setAdapter(adapter);
        orders.setLayoutManager(new LinearLayoutManager(getContext()));
        orders.setHasFixedSize(true);
        orders.addItemDecoration(new SpacesItemDecorationPaddingTop(getContext(), 10, 48));

        ItemTouchHelper touchHelper = new ItemTouchHelper(
                new ItemTouchHelperCallback(adapter, getContext(), true, false));
        touchHelper.attachToRecyclerView(orders);

        // set hide tab
        orders.addOnScrollListener(new HideViewScrollerListener() {
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
                (LinearLayoutManager)orders.getLayoutManager(), adapter) {
            @Override
            public void onLoadMore(final int position) {
                final List<Order> adapterData = adapter.getData();
                if(result.getNext_page_url() != null){
                    // show progress bar
                    adapterData.add(position, null);
                    adapter.notifyItemInserted(position);

                    Call<Result> call = MyApi.getInstance()
                            .loadMoreOrder(Token.share(getContext()), result.getNext_page_url());
                    call.enqueue(new Callback<Result>() {
                        int status;
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            status = response.code();
                            result = response.body();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // remove progress bar
                                    adapterData.remove(position);
                                    adapter.notifyItemRemoved(position);

                                    if(result != null){
                                        // notify data change
                                        data.addAll(result.getData());
                                        adapter.swapItems(data);
                                    }
                                }
                            }, 250);
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            showError(true);
                        }
                    });
                }
            }
        };
        orders.addOnScrollListener(endlessListener);

        // set click for item in recycler view
        adapter.setOnItemClickListener(new OrdersAdapter.OnItemClickListener() {
            @Override
            public void onItemOrderClick(View itemView, int position) {
                Intent intent = new Intent(getActivity(), DetailOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("order", adapter.getData().get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        adapter.setOnItemSwipedListener(new OrdersAdapter.OnItemSwipedListener() {
            @Override
            public void onSwiped(final int position, int direction) {
                Order order = adapter.getData().get(position);
                if(order != null){
                    Call<Void> call = MyApi.getInstance().receiveOrder(Token.share(getContext()),
                            adapter.getData().get(position).getId());

                    // Show smooth
                    Order smooth = new Order();
                    smooth.setId(-1);
                    final Order temp = adapter.getData().get(position);
                    adapter.getData().remove(position);
                    adapter.getData().add(position, smooth);
                    adapter.notifyItemChanged(position);

                    call.enqueue(new Callback<Void>() {
                        int status;
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            status = response.code();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Hide smooth
                                    adapter.getData().remove(position);
                                    adapter.notifyItemRemoved(position);

                                    if(status == 200){
                                        DialogUtil.receiveSuccess(getContext());
                                        fetchData();
                                    }else{
                                        adapter.getData().add(position, temp);
                                        adapter.notifyItemInserted(position);

                                        if(status == 404){
                                            DialogUtil.conflictReceiveOrder(getContext(), new DialogUtil.Callback() {
                                                @Override
                                                public void onPositiveButtonClick() {
                                                    fetchData();
                                                }
                                            });

                                        }else if(status == 402){
                                            DialogUtil.notEnoughMoney(getContext());
                                            adapter.notifyItemChanged(position);
                                        }else {
                                            DialogUtil.connectError(getContext());
                                            adapter.notifyItemChanged(position);
                                        }
                                    }
                                }
                            }, 400);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            adapter.getData().add(position, temp);
                            adapter.notifyItemInserted(position);
                            DialogUtil.connectError(getContext());
                        }
                    });
                }
            }
        });
    }

//---------------------------------------------------------------------------------------------

    public void setError(){
        error.setOnClickListener(new View.OnClickListener() {
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
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh.setRefreshing(false);
                    }
                }, 500);

            }
        });
        // Configure the refreshing colors
        refresh.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // Set margin to top
        refresh.setProgressViewOffset(false, 0, 120);
    }

//----------------------------------------------------------------------------------------------

    private void showProgress(final boolean show) {
        if(progress.isShown() != show){
            int shortAnimTime = 300;
            refresh.setEnabled(!show);
            orders.setVisibility(show ? View.GONE : View.VISIBLE);
            orders.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    orders.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progress.setVisibility(show ? View.VISIBLE : View.GONE);
            progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

//-------------------------------------------------------------------------------------------------

    public void showError(boolean show){
        error.setVisibility(show?View.VISIBLE:View.GONE);
        error.animate().alpha(show?1:0).setDuration(300);
    }

//-------------------------------------------------------------------------------------------------

    public void fetchData(){

        Call<Result> call = MyApi.getInstance().getListOrderWaiting(Token.share(getContext()));

        call.enqueue(new Callback<Result>() {
            int status;
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                status = response.code();
                result = response.body();

                if(status != 200){
                    showError(true);
                }else {
                    if(result != null){
                        data = result.getData();
                        adapter.swapItems(data);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                            }
                        }, 300);

                        fragmentListener.countOrders(result.getTotal(), 0, true);

                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                showError(true);
                Log.d("test", "onFailure: " + t.toString());
            }
        });
    }


}
