package com.fairmontsintenational.rentalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fairmontsintenational.rentalapp.classes.Sessions;
import com.fairmontsintenational.rentalapp.models.UserModel;
import com.fairmontsintenational.rentalapp.utilities.BaseUrl;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class Splashscreen extends AppCompatActivity {
    Animation top, bottom;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        ImageView logo = findViewById(R.id.logo);
        TextView title = findViewById(R.id.title);

        top = AnimationUtils.loadAnimation(this, R.anim.top);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom);
        logo.setAnimation(top);
        title.setAnimation(bottom);

        prefs = getSharedPreferences("com.fairmontsintenational.rentalapp", MODE_PRIVATE);
        Paper.init(this);

        int SPLASH_SCREEN = 3000;
        if (prefs.getBoolean("firstrun", true)) {
            Paper.book().write("Session", Sessions.InActive.toString());
            Paper.book().write("ACCESS_TOKEN","none");
//            getRegistrationToken();

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    Intent intent = new Intent(Splashscreen.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_SCREEN);

            prefs.edit().putBoolean("firstrun", false).apply();
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkSession();
                }
            }, SPLASH_SCREEN);
        }

    }

    private void checkSession() {
        String session = Paper.book().read("Session").toString();
        String Token = Paper.book().read("ACCESS_TOKEN").toString();
        if(session.equals(Sessions.Active.toString())){
            if(Token.equals("none")){
                startActivity(new Intent(Splashscreen.this, Login.class));
                finish();
            }
            RefreshToken(Token);
        }
        else if(session.equals(Sessions.InActive.toString())){
            startActivity(new Intent(Splashscreen.this, Login.class));
            finish();
        }
    }

    private void RefreshToken(final String ACCESS_TOKEN) {
        String url = BaseUrl.GetLoggedInUser();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        UserModel userModel = gson.fromJson(response.toString(),UserModel.class);
                        String ActiveUser = gson.toJson(userModel);
                        Paper.book().write("ActiveUser",ActiveUser);

                        startActivity(new Intent(Splashscreen.this, Homepage.class));
                        finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("Homepage-Fetch user:",error.toString());
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
        RequestQueue queue = Volley.newRequestQueue(Splashscreen.this);
        jsonObjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
}
