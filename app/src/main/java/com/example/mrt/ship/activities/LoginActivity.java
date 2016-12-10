package com.example.mrt.ship.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.mrt.ship.R;
import com.example.mrt.ship.networks.MyApi;
import com.example.mrt.ship.networks.Token;
import com.example.mrt.ship.utils.FontUtil;
import com.example.mrt.ship.utils.ValidationUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.app_name) TextView mAppName;
    @BindView(R.id.slogan) TextView mSlogan;
    @BindView(R.id.username) EditText mUsernameView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.sign_in_button) Button signInButton;
    @BindView(R.id.sign_up_button) Button signUpButton;
    @BindView(R.id.login_form) View mLoginFormView;
    @BindView(R.id.login_progress) View mProgressView;
    @BindView(R.id.icon_login_form) View mIconLoginFormView;

    private SharedPreferences preferences;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.mrt.ship.R.layout.activity_login);
        ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set no no micro input
        mUsernameView.setPrivateImeOptions("nm");
        // Get save username
        mUsernameView.setText(preferences.getString("username", ""));

        // Set font
        FontUtil.from(this).applyFontToTextView(mAppName, "Roboto-Light.ttf");
        FontUtil.from(this).applyFontToTextView(mSlogan, "Roboto-Light.ttf");
        FontUtil.from(this).applyFontToTextView(mPasswordView, "Roboto-Light.ttf");
        FontUtil.from(this).applyFontToTextView(mUsernameView, "Roboto-Light.ttf");
        FontUtil.from(this).applyFontToTextView(signInButton, "Roboto-Light.ttf");
        FontUtil.from(this).applyFontToTextView(signUpButton, "Roboto-Light.ttf");

        // Set check empty fields
        TextView[] fields = new TextView[2];
        fields[0] = mUsernameView;
        fields[1] = mPasswordView;
        ValidationUtil.setListenFieldsEmpty(signInButton, fields,
                R.drawable.button_disable, R.drawable.ripple);

        // Set on of KeyBoard
        setListenerToRootView();

        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                View focus = LoginActivity.this.getCurrentFocus();
                if (focus != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                attemptLogin();
            }
        });

    }


    public void setListenerToRootView() {
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean show = false;
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                    if(!show){
                        show = true;
                        mIconLoginFormView.animate().setDuration(500).alpha(0);
                        mIconLoginFormView.setVisibility(View.GONE);
                    }


                } else if (show){
                    show = false;
                    mIconLoginFormView.animate().setDuration(500).alpha(1);
                    mIconLoginFormView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void attemptLogin() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Đăng nhập không thành công");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        showProgress(true);

        Call<Token> call = MyApi.getInstance().login(username, password);
        call.enqueue(new Callback<Token>() {
            int statusCode= -1;
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                statusCode = response.code();
                Token token = response.body();
                if(statusCode == 401){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            showProgress(false);

                            builder.setMessage("Mật khẩu hoặc tài khoản bạn đã nhập không chính xác." +
                                    " Xin vui lòng thử lại.");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                                    getResources().getColor(R.color.colorPrimary));

                        }
                    },250);
                    return;
                }else if(statusCode != 200){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            showProgress(false);

                            builder.setMessage("Không thể kết nối tới máy chủ." +
                                    " Xin vui lòng thử lại.");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                                    getResources().getColor(com.example.mrt.ship.R.color.colorPrimary));
                        }
                    }, 250);

                    return;
                }

                if(token != null){
                    preferences.edit().putString("token", "Bearer " + token.getToken()).apply();
                    preferences.edit().putString("username", username).apply();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }
                    }, 250);

                }

            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                showProgress(false);
                builder.setMessage("Rất tiếc, không thể đăng nhập. Vui lòng kiểm tra kết nối internet của bạn.");
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                        getResources().getColor(com.example.mrt.ship.R.color.colorPrimary));
            }
        });

    }
    
    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
            int shortAnimTime = 400;

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
    }

    @OnClick(R.id.sign_up_button)
    public void signUp(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

}


