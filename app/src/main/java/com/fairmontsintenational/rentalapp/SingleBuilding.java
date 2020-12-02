package com.fairmontsintenational.rentalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
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
import com.fairmontsintenational.rentalapp.adapters.BillsAdapter;
import com.fairmontsintenational.rentalapp.models.BillsModel;
import com.fairmontsintenational.rentalapp.models.PendingBillModel;
import com.fairmontsintenational.rentalapp.models.PropertyModel;
import com.fairmontsintenational.rentalapp.utilities.BaseUrl;
import com.fairmontsintenational.rentalapp.utilities.Utils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class SingleBuilding extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Gson gson = new Gson();
    PropertyModel propertyModel;
    private static String ACCESS_TOKEN;
    private ConstraintLayout layout;
    private LottieAnimationView animationView;
    private TextView notFound;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_building);

        Paper.init(this);
        ACCESS_TOKEN = Paper.book().read("ACCESS_TOKEN");
        Intent intent = getIntent();
        final String houseDetails = intent.getStringExtra("HouseDetails");
        propertyModel = gson.fromJson(houseDetails,PropertyModel.class);

        ((TextView) findViewById(R.id.HouseName)).setText(Utils.convertCapitalText(propertyModel.getProperty())+", "+propertyModel.getHouse_unit());
        ((TextView)findViewById(R.id.Balance)).setText(getString(R.string.ksh)+" "+Utils.formatNumber(propertyModel.getBalance()));

        swipeRefreshLayout = findViewById(R.id.Refresh);
        recyclerView = findViewById(R.id.Recycler);
        animationView = findViewById(R.id.imageView3);
        notFound = findViewById(R.id.NoText);
        layout = findViewById(R.id.Layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        getBills();

        findViewById(R.id.Pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AlertDialog dialog = Utils.Mpesa_Popup(RentActivity.this,5000,100);
//                dialog.show();
                startActivity(new Intent(SingleBuilding.this,BillPresentment.class));
            }
        });

        findViewById(R.id.RentPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SingleBuilding.this,RentActivity.class));
            }
        });

        findViewById(R.id.HouseDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SingleBuilding.this,RentalDetails.class);
                intent1.putExtra("HouseDetails",houseDetails);
                startActivity(intent1);
            }
        });

        findViewById(R.id.LeaseManagement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SingleBuilding.this,LeaseDetails.class);
                intent1.putExtra("HouseDetails",houseDetails);
                startActivity(intent1);
            }
        });

        findViewById(R.id.CapturePayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SingleBuilding.this,CapturePayment.class));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBills();
            }
        });
    }

    private void getBills() {
        animationView.setVisibility(View.GONE);
        notFound.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        final List<PendingBillModel> list = new ArrayList<>();

        String url = BaseUrl.PendingBills();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        swipeRefreshLayout.setRefreshing(false);
                        BillsModel billsModel = gson.fromJson(response.toString(),BillsModel.class);
                        list.addAll(billsModel.getData());

                        if(list.size()==0){
                            animationView.setVisibility(View.VISIBLE);
                            notFound.setVisibility(View.VISIBLE);
                        }

                        BillsAdapter adapter = new BillsAdapter(list,SingleBuilding.this);
                        recyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                animationView.setVisibility(View.VISIBLE);
                notFound.setVisibility(View.VISIBLE);


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
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer "+ACCESS_TOKEN);
                return params;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(SingleBuilding.this);
        jsonObjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);

    }
}
