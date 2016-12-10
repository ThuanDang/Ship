package com.example.mrt.ship.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mrt.ship.R;
import com.example.mrt.ship.networks.MyApi;
import com.example.mrt.ship.networks.Token;
import com.example.mrt.ship.utils.DialogUtil;
import com.example.mrt.ship.utils.FontUtil;
import com.example.mrt.ship.utils.ValidationUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.app_name) TextView appName;
    @BindView(R.id.slogan) TextView slogan;
    @BindView(R.id.username) EditText username;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.submit_button) Button submitButton;
    @BindView(R.id.confirm_password) EditText confirmPassword;
    @BindView(R.id.sign_up_form) View signUpFormView;
    @BindView(R.id.login_progress) View progressView;
    @BindView(R.id.icon_login_form) View titleFormView;
    @BindView(R.id.name) EditText name;
    @BindView(R.id.phone) EditText phone;
    @BindView(R.id.address) EditText address;
    @BindView(R.id.account) EditText account;

    private SharedPreferences preferences;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set no no micro input
        username.setPrivateImeOptions("nm");
        // Get save username
        username.setText(preferences.getString("username", ""));

        // Set font
        FontUtil.from(this).applyFontToTextView(appName, "Roboto-Light.ttf");
        FontUtil.from(this).applyFontToTextView(slogan, "Roboto-Light.ttf");
        FontUtil.from(this).applyFontToTextView(password, "Roboto-Light.ttf");
        FontUtil.from(this).applyFontToTextView(username, "Roboto-Light.ttf");
        FontUtil.from(this).applyFontToTextView(submitButton, "Roboto-Light.ttf");

        // Set check empty fields
        TextView[] fields = {username, password, confirmPassword, name, phone, address, account};
        ValidationUtil.setListenFieldsEmpty(submitButton, fields,
                R.drawable.button_disable, R.drawable.ripple);
        
        // Set on of KeyBoard
        setListenerToRootView();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // hide keyboard
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (ValidationUtil.isTheSame(password, confirmPassword)) {
                    attemptSignUp();
                }else {
                    DialogUtil.confirmPasswordFail(SignUpActivity.this);
                }
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
                if (heightDiff > 100) {
                    if(!show){
                        show = true;
                        titleFormView.animate().setDuration(500).alpha(0);
                        titleFormView.setVisibility(View.GONE);
                    }

                } else if (show){
                    show = false;
                    titleFormView.animate().setDuration(500).alpha(1);
                    titleFormView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void attemptSignUp() {

        showProgress(true);

        MyApi.getInstance().signUp(username.getText().toString(), password.getText().toString(),
                name.getText().toString(), phone.getText().toString(), account.getText().toString(),
                address.getText().toString()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                final int status = response.code();
                if(status == 200){
                    DialogUtil.signUpSuccess(SignUpActivity.this);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            SignUpActivity.this.finish();
                        }
                    }, 500);
                }else if(status == 403){
                    DialogUtil.conflictSignUp(SignUpActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                DialogUtil.connectError(SignUpActivity.this);
            }
        });

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = 400;

        signUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        signUpFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                signUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

}
