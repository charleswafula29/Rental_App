package com.fairmontsintenational.rentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fairmontsintenational.rentalapp.models.LeaseTermsModel;
import com.fairmontsintenational.rentalapp.models.PropertyModel;
import com.fairmontsintenational.rentalapp.utilities.BaseUrl;
import com.fairmontsintenational.rentalapp.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class LeaseDetails extends AppCompatActivity {

    private static String ACCESS_TOKEN;
    private Gson gson = new Gson();
    PropertyModel propertyModel;
    private AlertDialog dialog;
    private String TAG = "LEASE_DETAILS";
    private RecyclerView recyclerView;
    ConstraintLayout layout;
    List<LeaseTermsModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lease_details);

        Paper.init(this);
        ACCESS_TOKEN = Paper.book().read("ACCESS_TOKEN");
        Intent intent = getIntent();
        final String houseDetails = intent.getStringExtra("HouseDetails");
        propertyModel = gson.fromJson(houseDetails, PropertyModel.class);
        dialog = Utils.ShowLoader(this);

        layout = findViewById(R.id.Layout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            ((TextView) findViewById(R.id.Date)).setText(propertyModel.getLease_start()!=null ? Utils.convertDate(propertyModel.getLease_start()) : getString(R.string.not_set));
        } catch (ParseException e) {
            e.printStackTrace();
            ((TextView) findViewById(R.id.Date)).setText(propertyModel.getLease_start()!=null ? propertyModel.getLease_start() : getString(R.string.not_set));
        }

        findViewById(R.id.RequestTermination).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.ShowConfirmationPopup(LeaseDetails.this, "Are you sure you want to request for the termination of your lease for hse "+propertyModel.getHouse_unit()+"?", new Utils.PickerOptionListener() {
                    @Override
                    public void onProceedSelected() {
                        TerminateLease(propertyModel.getSlug());
                    }
                });
            }
        });

        getLeaseTerms();


    }

    private void getLeaseTerms() {
         list = new ArrayList<>();
        final Type listType = new TypeToken<List<LeaseTermsModel>>() {}.getType();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, BaseUrl.GetLeaseTerms(), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        list = gson.fromJson(response.toString(),listType);

                        innerAdapter innerAdapter = new innerAdapter(list);
                        recyclerView.setAdapter(innerAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Utils.ShowLongSnackBar(layout,"Failed to fetch lease terms, Kindly check your network connection.");
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
        RequestQueue queue = Volley.newRequestQueue(LeaseDetails.this);
        request.setRetryPolicy(
                new DefaultRetryPolicy(0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

    }

    private void TerminateLease(String slug) {
        dialog.show();
        String url = BaseUrl.TerminateLease(slug);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.e(TAG, response.toString());
                        Toast.makeText(LeaseDetails.this, "Lease termination sent successfully!", Toast.LENGTH_LONG).show();
                        finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                Utils.ShowLongSnackBar(layout,"Lease Request failed!");
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
        RequestQueue queue = Volley.newRequestQueue(LeaseDetails.this);
        request.setRetryPolicy(
                new DefaultRetryPolicy(0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    class innerAdapter extends RecyclerView.Adapter<innerAdapter.viewHolder>{
        List<LeaseTermsModel> list;

        public innerAdapter(List<LeaseTermsModel> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(LeaseDetails.this);
            return new viewHolder(inflater.inflate(R.layout.lease_terms_list,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, int position) {
            LeaseTermsModel model = list.get(position);
            holder.title.setText(model.getTitle());
            //holder.desc.setText(Html.fromHtml(model.getDescription()));
            holder.desc.setText(Utils.removeHtml(model.getDescription()));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class viewHolder extends RecyclerView.ViewHolder{

            TextView title,desc;
            public viewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.Title);
                desc = itemView.findViewById(R.id.Description);
            }
        }
    }
}
