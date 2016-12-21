package com.example.mrt.ship.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.mrt.ship.R;
import com.example.mrt.ship.adapters.OrdersAdapter;
import com.example.mrt.ship.adapters.SearchAdapter;
import com.example.mrt.ship.models.Order;
import com.example.mrt.ship.networks.MyApi;
import com.example.mrt.ship.networks.Token;
import com.example.mrt.ship.preferences.SpacesItemDecorationPaddingTop;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_search) Toolbar toolbar;
    @BindView(R.id.search_input) EditText input;
    @BindView(R.id.list_search) RecyclerView orders;
    @BindView(R.id.progress_search) ProgressBar progress;
    @BindView(R.id.error) View error;

    private SearchAdapter adapter;
    private String textInput;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setList();

        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textInput = input.getText().toString();
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!textInput.isEmpty()){
                                fetchData();
                            }
                        }
                    }, 800);
            }
        });


        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showError(false);
                fetchData();
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void showProgress(final boolean show) {
        if(progress.isShown() != show){
            int shortAnimTime = 300;
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


    public void showError(boolean show){
        error.setVisibility(show?View.VISIBLE:View.GONE);
        error.animate().alpha(show?1:0).setDuration(300);
    }


    public void setList(){
        adapter = new SearchAdapter(this);
        orders.setAdapter(adapter);
        orders.setLayoutManager(new LinearLayoutManager(this));
        orders.setHasFixedSize(true);
        orders.addItemDecoration(new SpacesItemDecorationPaddingTop(this, 10, 48));

        // set click for item in recycler view
        adapter.setOnItemClickListener(new OrdersAdapter.OnItemClickListener() {
            @Override
            public void onItemOrderClick(View itemView, int position) {
                Intent intent = new Intent(SearchActivity.this, DetailOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("order", adapter.getData().get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    public void fetchData(){
        showProgress(true);
        MyApi.getInstance().search(Token.share(this), textInput)
                .enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if(response.code() != 200){
                    Log.d("test", "onResponse: " + response.code());
                    showError(true);
                }else {
                    if(response.body() != null){
                        adapter.swapItems(response.body());
                        showProgress(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.d("test", "onResponse: " + t.toString());
                showError(true);
            }
        });
    }
}
