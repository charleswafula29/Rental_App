package com.fairmontsintenational.rentalapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

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
import com.fairmontsintenational.rentalapp.CreateComplaint;
import com.fairmontsintenational.rentalapp.R;
import com.fairmontsintenational.rentalapp.ViewComplaint;
import com.fairmontsintenational.rentalapp.models.ComplaintsModel;
import com.fairmontsintenational.rentalapp.models.RespComplaintsModel;
import com.fairmontsintenational.rentalapp.models.TransactionModel;
import com.fairmontsintenational.rentalapp.utilities.BaseUrl;
import com.fairmontsintenational.rentalapp.utilities.Utils;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

import static com.fairmontsintenational.rentalapp.utilities.Utils.AltConvertDate;
import static com.fairmontsintenational.rentalapp.utilities.Utils.SecondAltConvertDate;
import static com.fairmontsintenational.rentalapp.utilities.Utils.pickPreviewBody;
import static com.fairmontsintenational.rentalapp.utilities.Utils.pickPreviewText;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComplaintsFragment extends Fragment {
    private Context context;
    private innerAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout layout,notFound;
    private static String ACCESS_TOKEN;
    private Gson gson = new Gson();


    public ComplaintsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_complaints, container, false);

        EditText searchField = root.findViewById(R.id.SearchField);
        swipeRefreshLayout = root.findViewById(R.id.Refresh);
        recyclerView = root.findViewById(R.id.Recycler);
        layout = root.findViewById(R.id.Layout);
        notFound = root.findViewById(R.id.NotFound);

        Paper.init(context);
        ACCESS_TOKEN = Paper.book().read("ACCESS_TOKEN");

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        //getComplaints();

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getComplaints();
            }
        });

        root.findViewById(R.id.NewComplaint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, CreateComplaint.class));
            }
        });

        return root;
    }

    private void getComplaints() {
        notFound.setVisibility(View.GONE);
        String url = BaseUrl.GetComplaints();
        swipeRefreshLayout.setRefreshing(true);
        final List<ComplaintsModel> list = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        swipeRefreshLayout.setRefreshing(false);
                        RespComplaintsModel model = gson.fromJson(response.toString(),RespComplaintsModel.class);
                        list.addAll(model.getData());

                        if (list.size()==0){
                            notFound.setVisibility(View.VISIBLE);
                        }else{
                            notFound.setVisibility(View.GONE);
                        }

                        //Log.e("List",list.toString());

                        adapter = new innerAdapter(list);
                        recyclerView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onResume() {
        super.onResume();
        getComplaints();
    }

    class innerAdapter extends RecyclerView.Adapter<innerAdapter.viewHolder>
    implements Filterable {
        List<ComplaintsModel> list;
        List<ComplaintsModel> All_list;

        public innerAdapter(List<ComplaintsModel> list) {
            this.list = list;
            All_list = new ArrayList<>(list);
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return new viewHolder(inflater.inflate(R.layout.complaints_list,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, int position) {
            final ComplaintsModel model = list.get(position);
            holder.HouseName.setText(model.getHouseUnit()!=null?"House unit: "+model.getHouseUnit().getName():"House unit: not set");
            try {
                holder.Date.setText(SecondAltConvertDate(model.getCreated_at()));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.Date.setText(model.getCreated_at());
            }
            holder.Status.setText(model.getStatusName());
            holder.Title.setText(pickPreviewText(model.getName()));
            holder.Description.setText(pickPreviewBody(model.getDescription()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //String details = gson.toJson(model);
                    Intent intent = new Intent(context, ViewComplaint.class);
                    intent.putExtra("ComplaintSlug",model.getSlug());
                    context.startActivity(intent);
                }
            });
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
                List<ComplaintsModel> filteredList = new ArrayList<>();
                if (constraint.toString().isEmpty()) {
                    filteredList.addAll(All_list);
                } else {
                    for (ComplaintsModel activity : All_list) {
                        if (activity.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
                list.addAll((Collection<? extends ComplaintsModel>) results.values);
                notifyDataSetChanged();
            }
        };

        class viewHolder extends RecyclerView.ViewHolder{
            TextView Title,Date,HouseName,Description,Status;

            public viewHolder(@NonNull View itemView) {
                super(itemView);

                Title = itemView.findViewById(R.id.Title);
                Date = itemView.findViewById(R.id.Date);
                HouseName = itemView.findViewById(R.id.HouseName);
                Description = itemView.findViewById(R.id.Description);
                Status = itemView.findViewById(R.id.Status);
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
