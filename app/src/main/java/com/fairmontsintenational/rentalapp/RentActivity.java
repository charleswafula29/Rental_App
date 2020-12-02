package com.fairmontsintenational.rentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fairmontsintenational.rentalapp.models.RespStatementModel;
import com.fairmontsintenational.rentalapp.models.StatementModel;
import com.fairmontsintenational.rentalapp.models.TransRespModel;
import com.fairmontsintenational.rentalapp.models.TransactionModel;
import com.fairmontsintenational.rentalapp.utilities.BaseUrl;
import com.fairmontsintenational.rentalapp.utilities.Utils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class RentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String ACCESS_TOKEN;
    Gson gson = new Gson();
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);

        Paper.init(this);
        ACCESS_TOKEN = Paper.book().read("ACCESS_TOKEN");

        findViewById(R.id.Title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        swipeRefreshLayout = findViewById(R.id.Refresh);
        recyclerView = findViewById(R.id.Recycler);
        layout = findViewById(R.id.Layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTransactions();
            }
        });

        getTransactions();
    }

    private void getTransactions() {
        swipeRefreshLayout.setRefreshing(true);
        final List<StatementModel> list = new ArrayList<>();

        String url = BaseUrl.Statement();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        swipeRefreshLayout.setRefreshing(false);

                        RespStatementModel respStatementModel = gson.fromJson(response.toString(),RespStatementModel.class);
                        list.addAll(respStatementModel.getData().getStatements());

                        InnerAdapter innerAdapter = new InnerAdapter(list);
                        recyclerView.setAdapter(innerAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
                Utils.ShowLongSnackBar(layout,"Failed to fetch statement, please check your network and try again.");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer "+ACCESS_TOKEN);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(RentActivity.this);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);

        swipeRefreshLayout.setRefreshing(false);
    }

    private class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.viewHolder> {
        private List<StatementModel> list;

        public InnerAdapter(List<StatementModel> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(RentActivity.this);
            return new viewHolder(inflater.inflate(R.layout.transactions_list,parent,false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
            final StatementModel model = list.get(position);
            try {
                holder.Date.setText(Utils.StatementConvertDate(model.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.Date.setText(model.getDate());
            }
            holder.InvoiceNo.setText(model.getCode());
            holder.Charged.setText(model.getCharges()==null?"Not set":Utils.formatNumber(model.getCharges()));
            holder.Payment.setText(model.getPayments()==null?"Not set":Utils.formatNumber(model.getPayments()));

            if (position%2==1){
                holder.layout.setBackgroundColor(ContextCompat.getColor(RentActivity.this,R.color.RowsColor));
            }else {
                holder.layout.setBackgroundColor(ContextCompat.getColor(RentActivity.this,R.color.white));
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class viewHolder extends RecyclerView.ViewHolder{

            TextView Date,InvoiceNo,Charged,Payment;
            LinearLayout layout;

            viewHolder(@NonNull View itemView) {
                super(itemView);

                Date = itemView.findViewById(R.id.Date);
                InvoiceNo = itemView.findViewById(R.id.InvoiceNo);
                Charged = itemView.findViewById(R.id.Charged);
                Payment = itemView.findViewById(R.id.Payment);
                layout = itemView.findViewById(R.id.Layout);
            }
        }
    }
}
