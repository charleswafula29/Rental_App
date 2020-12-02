package com.fairmontsintenational.rentalapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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

import io.blackbox_vision.datetimepickeredittext.view.DatePickerEditText;
import io.paperdb.Paper;

import static com.fairmontsintenational.rentalapp.utilities.Utils.AltConvertDate;
import static com.fairmontsintenational.rentalapp.utilities.Utils.getTodayDate;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionsFragment extends Fragment {

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private innerAdapter innerAdapter;
    private Gson gson = new Gson();
    private ConstraintLayout layout;
    private static String ACCESS_TOKEN;
    private LottieAnimationView animationView;
    private TextView notFound;
    private EditText FromDate,ToDate;

    public TransactionsFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_transactions, container, false);

        Paper.init(context);
        ACCESS_TOKEN = Paper.book().read("ACCESS_TOKEN");


        swipeRefreshLayout = root.findViewById(R.id.Refresh);
        recyclerView = root.findViewById(R.id.Recycler);
        layout = root.findViewById(R.id.Layout);
        animationView = root.findViewById(R.id.imageView3);
        notFound = root.findViewById(R.id.NoText);
        FromDate = root.findViewById(R.id.FromDate);
        ToDate = root.findViewById(R.id.ToDate);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        FromDate.setText("2020-01-01");
        ToDate.setText(getTodayDate());

        getTransactions();

        FromDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getTransactions();
            }
        });

        ToDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getTransactions();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTransactions();
            }
        });

        return root;
    }

    private void getTransactions() {
        animationView.setVisibility(View.GONE);
        notFound.setVisibility(View.GONE);
        String url = BaseUrl.Transactions(FromDate.getText().toString(),ToDate.getText().toString());
        swipeRefreshLayout.setRefreshing(true);
        final List<TransactionModel> list = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        swipeRefreshLayout.setRefreshing(false);

                        TransRespModel respModel = gson.fromJson(response.toString(),TransRespModel.class);
                        list.addAll(respModel.getData());

                        if(list.size()==0){
                            animationView.setVisibility(View.VISIBLE);
                            notFound.setVisibility(View.VISIBLE);
                        }else{
                            animationView.setVisibility(View.GONE);
                            notFound.setVisibility(View.GONE);
                        }

                        innerAdapter = new innerAdapter(list);
                        recyclerView.setAdapter(innerAdapter);
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

    private class innerAdapter extends RecyclerView.Adapter<innerAdapter.viewHolder>
    implements Filterable {
        private List<TransactionModel> list;
        private List<TransactionModel> All_list;

        public innerAdapter(List<TransactionModel> list) {
            this.list = list;
            All_list = new ArrayList<>(list);
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return new viewHolder(inflater.inflate(R.layout.all_transactions_list,parent,false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, int position) {
            TransactionModel model = list.get(position);
            holder.InvoiceNo.setText(context.getString(R.string.invoice_no)+" "+model.getCode());
            try {
                holder.Date.setText(AltConvertDate(model.getCreated_at()));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.Date.setText(model.getCreated_at());
            }
            holder.HouseName.setText(Utils.convertCapitalText(model.getProperty())+", "+model.getHouse_unit());
            holder.Amount.setText(getString(R.string.ksh)+" "+ Utils.formatNumber(String.valueOf(model.getAmount())));
            holder.PaymentMethod.setText(context.getString(R.string.paid_via)+" "+model.getPayment_mode());

            if (model.getStatus().equals("1")){
                holder.line.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.blue_circle,null));
            }else {
                holder.line.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.red_circle,null));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public Filter getFilter() {
            return filter;
        }

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<TransactionModel> filteredList = new ArrayList<>();
                if (constraint.toString().isEmpty()) {
                    filteredList.addAll(All_list);
                } else {
                    for (TransactionModel activity : All_list) {
                        if (activity.getProperty().toLowerCase().contains(constraint.toString().toLowerCase())
                        || activity.getHouse_unit().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredList.add(activity);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list.clear();
                list.addAll((Collection<? extends TransactionModel>) results.values);
                notifyDataSetChanged();
            }
        };

        class viewHolder extends RecyclerView.ViewHolder{

            TextView InvoiceNo, Amount, HouseName, Date, PaymentMethod;
            ImageView line;

            viewHolder(@NonNull View itemView) {
                super(itemView);

                InvoiceNo = itemView.findViewById(R.id.InvoiceNo);
                Amount = itemView.findViewById(R.id.Amount);
                HouseName = itemView.findViewById(R.id.Name);
                Date = itemView.findViewById(R.id.Date);
                line = itemView.findViewById(R.id.line);
                PaymentMethod = itemView.findViewById(R.id.PaymentMethod);
            }
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }
}
