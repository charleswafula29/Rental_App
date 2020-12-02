package com.fairmontsintenational.rentalapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fairmontsintenational.rentalapp.models.ComplaintsModel;
import com.fairmontsintenational.rentalapp.models.RespSingleComplaint;
import com.fairmontsintenational.rentalapp.utilities.BaseUrl;
import com.fairmontsintenational.rentalapp.utilities.Utils;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.fairmontsintenational.rentalapp.fragments.MyAccountFragment.ConvertBase64;

public class ViewComplaint extends AppCompatActivity {
    private Gson gson = new Gson();
    private AlertDialog loader;
    private static String ACCESS_TOKEN;
    private ComplaintsModel model;
    private TextView HouseName,Status,Title,Desc;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_complaint);

        loader = Utils.ShowLoader(this);
        Paper.init(this);
        ACCESS_TOKEN = Paper.book().read("ACCESS_TOKEN");

        Intent intent = getIntent();
        String ComplaintSlug = intent.getStringExtra("ComplaintSlug");
        //final ComplaintsModel model = gson.fromJson(ComplaintDetails,ComplaintsModel.class);

        getComplaintDetails(ComplaintSlug);

        HouseName = findViewById(R.id.HouseName);
        Status = findViewById(R.id.Status);
        Title = findViewById(R.id.ComplaintTitle);
        Desc = findViewById(R.id.Description);

        findViewById(R.id.Title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.Delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.ShowConfirmationPopup(ViewComplaint.this, getString(R.string.confirm_delete_complaint), new Utils.PickerOptionListener() {
                    @Override
                    public void onProceedSelected() {
                        deleteComplain(model.getSlug());
                    }
                });
            }
        });
    }

    private void getComplaintDetails(String complaintSlug) {
        loader.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BaseUrl.DeleteComplaint(complaintSlug), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        RespSingleComplaint singleComplaint = gson.fromJson(response.toString(),RespSingleComplaint.class);
                        model = singleComplaint.getData();
                        HouseName.append(" "+model.getHouseUnit().getName());
                        Status.setText(model.getStatusName());
                        Title.setText(model.getName());
                        Desc.setText(model.getDescription());

                        if(model.getImage()!=null){
                            Bitmap bitmap = ConvertBase64(model.getImage());
                            ((ImageView) findViewById(R.id.Photo)).setImageBitmap(bitmap);
                        }

                        loader.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.dismiss();
                error.printStackTrace();
                finish();
                Toast.makeText(ViewComplaint.this, "Failed to fetch complaint details.", Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(ViewComplaint.this);
        jsonObjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    private void deleteComplain(String slug) {
        loader.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, BaseUrl.DeleteComplaint(slug), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loader.dismiss();
                        finish();
                        Toast.makeText(ViewComplaint.this, "Complaint delete.", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                loader.dismiss();
                Toast.makeText(ViewComplaint.this, "Failed to delete complaint.", Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(ViewComplaint.this);
        jsonObjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
}
