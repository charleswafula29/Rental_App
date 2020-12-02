package com.fairmontsintenational.rentalapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fairmontsintenational.rentalapp.classes.Sessions;
import com.fairmontsintenational.rentalapp.models.UserModel;
import com.fairmontsintenational.rentalapp.utilities.BaseUrl;
import com.fairmontsintenational.rentalapp.utilities.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    private EditText IDNumber,Password;
    private ImageView ShowPass;
    private AlertDialog loader;
    private int setType = 1;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loader = Utils.ShowLoader(this);
        IDNumber = findViewById(R.id.PhoneNo);
        Password = findViewById(R.id.Password);
        ShowPass = findViewById(R.id.ShowPass);
        layout = findViewById(R.id.Layout);
        TextView forgotPass = findViewById(R.id.ForgotPass);

        Paper.init(this);
        
        ShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(setType==1) {
                    setType = 0;
                    Password.setTransformationMethod(null);
                    if (Password.getText().length() > 0){
                        Password.setSelection(Password.getText().length());
                        ShowPass.setImageDrawable(getDrawable(R.drawable.ic_hide_pass));
                    }
                }
                else{
                    setType=1;
                    Password.setTransformationMethod(new PasswordTransformationMethod());
                    if(Password.getText().length() > 0){
                        Password.setSelection(Password.getText().length());
                        ShowPass.setImageDrawable(getDrawable(R.drawable.ic_show_pass));
                    }
                }
            }
        });

        (findViewById(R.id.Login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    private void validate() {
        String IDNo  = IDNumber.getText().toString().trim();
        String Pass = Password.getText().toString().trim();

        if (IDNo.isEmpty()){
            Utils.ShowLongSnackBar(layout,"National ID has not been provided!");
            IDNumber.requestFocus();
        }else if(Pass.isEmpty()){
            Utils.ShowLongSnackBar(layout,"Password has not been provided!");
            Password.requestFocus();
        }else {
            LoginFunction(IDNo,Pass);
        }

    }

    private void LoginFunction(final String idNo, final String pass) {
        loader.show();
        String url = BaseUrl.PostLogin();

        LinkedHashMap<String,String> param = new LinkedHashMap<>();
        param.put("username",idNo);
        param.put("password",pass);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //loader.dismiss();
                        try {
                            Paper.book().write("ACCESS_TOKEN", response.getString("data"));
                            Paper.book().write("USERNAME", idNo);
                            Paper.book().write("PASS", pass);
                            Paper.book().write("Session", Sessions.Active.toString());

                            getLoggedInUserDetails(response.getString("data"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loader.dismiss();
                            Utils.ShowLongSnackBar(layout, getString(R.string.auth_error));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.dismiss();
                String message = null;
                if (error instanceof NetworkError) {
                    message = getString(R.string.network_error);
                } else if (error instanceof ServerError) {
                    message = getString(R.string.server_error);
                } else if (error instanceof AuthFailureError) {
                    message = getString(R.string.auth_error);
                } else if (error instanceof ParseError) {
                    message = getString(R.string.parse_error);
                } else if (error instanceof TimeoutError) {
                    message = getString(R.string.timeout_error);
                } else {
                    Utils.ShowLongSnackBar(layout, error.toString());
                }

                Utils.ShowLongSnackBar(layout, message);
            }
        });
        RequestQueue queue = Volley.newRequestQueue(Login.this);
        jsonObjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    private void getLoggedInUserDetails(final String ACCESS_TOKEN) {
        String url = BaseUrl.GetLoggedInUser();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        UserModel userModel = gson.fromJson(response.toString(),UserModel.class);

                        String ActiveUser = gson.toJson(userModel);
                        Paper.book().write("ActiveUser",ActiveUser);

                        startActivity(new Intent(Login.this, Homepage.class));
                        loader.dismiss();
                        finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("Homepage-Fetch user:",error.toString());
                loader.dismiss();
                Utils.ShowLongSnackBar(layout, "User details not found.");

            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer "+ACCESS_TOKEN);
                return params;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(Login.this);
        jsonObjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
}
