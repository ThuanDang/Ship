package com.example.mrt.ship.preferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.mrt.ship.adapters.RcvOrdersAdapter;


/**
 * Created by mrt on 30/10/2016.
 */

public abstract class EndlessRecyclerViewScrollerListener extends RecyclerView.OnScrollListener {
    private int visibleThreshold = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private RcvOrdersAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public EndlessRecyclerViewScrollerListener(LinearLayoutManager layoutManager,
                                               RcvOrdersAdapter adapter) {
        this.mLayoutManager = layoutManager;
        this.adapter = adapter;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int totalItemCount = adapter.getData().size();
        int lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        // lastVisibleItemPosition is index of element in data array
        if (loading && (totalItemCount > previousTotalItemCount + 1)) {
            // previousTotalItemCount + 1 = totalItemCount + a null element(progress bar)
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        if (!loading && (lastVisibleItemPosition + visibleThreshold + 1) >= totalItemCount) {
                onLoadMore(lastVisibleItemPosition + 1);
                loading = true;
        }
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int position);

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

}
