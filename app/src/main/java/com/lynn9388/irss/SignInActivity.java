package com.lynn9388.irss;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignInActivity extends AppCompatActivity {
    private TextView logo;
    private TextInputLayout inputLayoutUsername;
    private TextInputLayout inputLayoutPassword;
    private EditText editUsername;
    private EditText editPassword;
    private Button buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        logo = (TextView) findViewById(R.id.logo);
        inputLayoutUsername = (TextInputLayout) findViewById(R.id.input_layout_sign_in_username);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_sign_in_password);
        editUsername = (EditText) findViewById(R.id.edit_sign_in_username);
        editPassword = (EditText) findViewById(R.id.edit_sign_in_password);
        buttonSignIn = (Button) findViewById(R.id.button_sign_in);

        Typeface alexBrush = Typeface.createFromAsset(getAssets(), "AlexBrush-Regular.ttf");
        logo.setTypeface(alexBrush);
    }

    public void signIn(View view) {
        final String username = editUsername.getText().toString();
        final String password = editPassword.getText().toString();
        saveAccount(username, password);

        final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this, R.style.SignInProgreeDialogTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.text_sign_in_progress));
        progressDialog.show();
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                finish();
            }
        }, 3000);
    }

    private void saveAccount(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.username), username);
        editor.putString(getString(R.string.password), password);
        editor.commit();
    }
}
