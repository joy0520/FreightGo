package com.joy.freightgo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Joy on 2016/8/20.
 */
public class SettingActivity extends Activity {
    private static final String joytag = "joydebug.settingactivity.";
    private static final String LOGIN_FAIL = "login_fail";
    private static final String LOGIN_SUCCESS = "login_success";
    private static final String SIGNUP_FAIL = "signup_fail";
    private static final String SIGNUP_SUCCESS = "signup_success";

    private ConnectionHelper connection;
    private EditText editName, editCarName, editPassword;
    private Button btnLogin, btnSignup;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(joytag + "", "onCreate");
        setContentView(R.layout.activity_setting);
        editName = (EditText) findViewById(R.id.edit_name);
        editCarName = (EditText) findViewById(R.id.edit_car_name);
        editPassword = (EditText) findViewById(R.id.edit_password);
        btnLogin = (Button) findViewById(R.id.button_login);
        btnSignup = (Button) findViewById(R.id.button_signup);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLogin login = new DoLogin();
                login.execute(editName.getText().toString(),
                        editCarName.getText().toString(),
                        editPassword.getText().toString());
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoSignUp signup = new DoSignUp();
                signup.execute(editName.getText().toString(),
                        editCarName.getText().toString(),
                        editPassword.getText().toString());
            }
        });
    }

    private class DoLogin extends AsyncTask<String, Void, String> {
        String name, carName, password;
        boolean isSuccessful = false;

        @Override
        protected String doInBackground(String... params) {
            Log.i(joytag, "doInBackground " + params);
            name = params[0];
            carName = params[1];
            password = params[2];
            if (name == null || carName == null || password == null) {
                Log.i(joytag, "name=" + name + ", carName=" + carName + ", password=" + password);
                return LOGIN_FAIL;
            }
            try {
                Connection connection = ConnectionHelper.connect();
                if (connection == null) {
                    Log.i(joytag, "connection == null");
                    return LOGIN_FAIL;
                } else {
                    // log in
                    String query = "SELECT * from " + ConnectionHelper.dbSheetName
                            + " where ID='" + name + "' and carNumber='" + carName
                            + "'";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    Log.i(joytag, "resultSet=" + resultSet);
                    if (resultSet.next()) {
                        isSuccessful = true;
                        return LOGIN_SUCCESS;
                    }
                }
            } catch (Exception e) {
                Log.d(joytag, "try to connect exception", e);
            }
            return LOGIN_FAIL;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            if (isSuccessful) {
                Toast.makeText(SettingActivity.this,
                        s, Toast.LENGTH_SHORT).show();
                Intent result = new Intent();
                result.putExtra(MainActivity.RESULT_LOGIN_EXTRA_TAG, isSuccessful);
                setResult(RESULT_OK, result);
                finish();
            } else {
                Toast.makeText(SettingActivity.this,
                        s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private class DoSignUp extends AsyncTask<String, Void, String> {
        String name, carName, password;
        boolean isSuccessful = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            if (isSuccessful) {
                Toast.makeText(SettingActivity.this,
                        s, Toast.LENGTH_SHORT).show();
                Intent result = new Intent();
                result.putExtra(MainActivity.RESULT_LOGIN_EXTRA_TAG, isSuccessful);
                setResult(RESULT_OK, result);
                finish();
            } else {
                Toast.makeText(SettingActivity.this,
                        s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i(joytag, "doInBackground " + params);
            name = params[0];
            carName = params[1];
            password = params[2];
            if (name == null || carName == null || password == null) {
                Log.i(joytag, "name=" + name + ", carName=" + carName + ", password=" + password);
                return SIGNUP_FAIL;
            }
            try {
                Connection connection = ConnectionHelper.connect();
                if (connection == null) {
                    Log.i(joytag, "connection == null");
                    return SIGNUP_FAIL;
                } else {
                    // sign up
                    String query = "INSERT INTO "+ConnectionHelper.dbSheetName
                            +" (carNumber, ID) VALUES ('"
                            +carName+"', '"
                            +name+"')";
                    Statement statement = connection.createStatement();
                    int result = statement.executeUpdate(query);
                    Log.i(joytag, "after INSERT result="+result);
                    isSuccessful = true;
                    return SIGNUP_SUCCESS;
                }
            } catch (Exception e) {
                Log.d(joytag, "try to connect exception", e);
            }
            return SIGNUP_FAIL;
        }
    }
}
