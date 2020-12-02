package com.fairmontsintenational.rentalapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.fairmontsintenational.rentalapp.R;
import com.fairmontsintenational.rentalapp.SingleBuilding;
import com.fairmontsintenational.rentalapp.models.PropertyModel;
import com.fairmontsintenational.rentalapp.models.TenantModel;
import com.fairmontsintenational.rentalapp.models.UserModel;
import com.fairmontsintenational.rentalapp.utilities.BaseUrl;
import com.fairmontsintenational.rentalapp.utilities.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static com.fairmontsintenational.rentalapp.fragments.MyAccountFragment.ConvertBase64;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRentalsFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private static String ACCESS_TOKEN;
    private Gson gson = new Gson();
    private TenantModel tenantModel;
    private LottieAnimationView animationView;
    private TextView notFound;
    private ConstraintLayout layout;
    private AlertDialog dialog;
    private CircleImageView profilePic;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    public MyRentalsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_rentals, container, false);
        recyclerView = root.findViewById(R.id.Recycler);
        swipeRefreshLayout = root.findViewById(R.id.Refresh);
        animationView = root.findViewById(R.id.imageView3);
        notFound = root.findViewById(R.id.NoText);
        layout = root.findViewById(R.id.Layout);
        profilePic = root.findViewById(R.id.profilePic);
        ProgressBar progressBar = root.findViewById(R.id.ProgressBar);

        Paper.init(context);
        ACCESS_TOKEN = Paper.book().read("ACCESS_TOKEN");

        dialog = Utils.ShowLoader(context);

        String ActiveUser = Paper.book().read("ActiveUser");
        UserModel userModel = gson.fromJson(ActiveUser, UserModel.class);
        //Log.e("Model",userModel.toString());

        ((TextView) root.findViewById(R.id.Name)).append(" "+Utils.convertCapitalText((userModel.getFirst_name()!=null)?userModel.getFirst_name():""));

        progressBar.setVisibility(View.VISIBLE);
        Bitmap bitmap = ConvertBase64(userModel.getProfile_image());
        profilePic.setImageBitmap(bitmap);
        progressBar.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        getProperty();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProperty();
            }
        });

        return root;
    }

    private void getProperty() {
        animationView.setVisibility(View.GONE);
        notFound.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        String url = BaseUrl.TenantDetails();
        final List<PropertyModel> list = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            tenantModel = gson.fromJson(response.getJSONObject("data").toString(),TenantModel.class);
                            list.addAll(tenantModel.getLeases());

                            if(list.size()==0){
                                animationView.setVisibility(View.VISIBLE);
                                notFound.setVisibility(View.VISIBLE);
                            }
                            InnerAdapter innerAdapter = new InnerAdapter(list);
                            recyclerView.setAdapter(innerAdapter);
                            //Log.e("List",list.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            animationView.setVisibility(View.VISIBLE);
                            notFound.setVisibility(View.VISIBLE);
                        }

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
        RequestQueue queue = Volley.newRequestQueue(context);
        jsonObjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);

    }

    private class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.ViewHolder>{
        private List<PropertyModel> list;

        InnerAdapter(List<PropertyModel> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return new ViewHolder(inflater.inflate(R.layout.property_list,parent,false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final PropertyModel model = list.get(position);
            holder.Building.setText(context.getString(R.string.building_name)+" "+Utils.convertCapitalText(model.getProperty()));
            holder.HouseNo.setText(context.getString(R.string.house_unit)+" "+model.getHouse_unit());
            float balance = Float.parseFloat(model.getBalance());
            if(balance>0){
                holder.Status.setText(context.getString(R.string.has_balance));
                holder.Amount.setVisibility(View.VISIBLE);
                holder.Amount.setText(context.getString(R.string.ksh)+" "+Utils.formatNumber(model.getBalance()));
            }else {
                holder.Status.setText(context.getString(R.string.no_balance));
                holder.Amount.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String houseDetails = gson.toJson(model);
                    Intent intent = new Intent(context, SingleBuilding.class);
                    intent.putExtra("HouseDetails",houseDetails);
                    context.startActivity(intent);
                }
            });

            if(list.size() == position+1){
                holder.layout.setBackgroundResource(0);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        private class ViewHolder extends RecyclerView.ViewHolder{

            TextView Building,HouseNo,Status,Amount;
            ConstraintLayout layout;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                Building = itemView.findViewById(R.id.Building);
                HouseNo = itemView.findViewById(R.id.HouseNo);
                Status = itemView.findViewById(R.id.Status);
                layout = itemView.findViewById(R.id.constraintLayout);
                Amount = itemView.findViewById(R.id.Amount);
            }
        }
    }
}
