package com.fairmontsintenational.rentalapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fairmontsintenational.rentalapp.BillPresentment;
import com.fairmontsintenational.rentalapp.R;
import com.fairmontsintenational.rentalapp.RentActivity;
import com.fairmontsintenational.rentalapp.models.BillsModel;
import com.fairmontsintenational.rentalapp.models.PendingBillModel;
import com.fairmontsintenational.rentalapp.utilities.Utils;

import java.text.ParseException;
import java.util.List;

import static com.fairmontsintenational.rentalapp.utilities.Utils.Mpesa_Popup;
import static com.fairmontsintenational.rentalapp.utilities.Utils.formatNumber;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.ViewHolder>{
    private List<PendingBillModel> list;
    private Context context;

    public BillsAdapter(List<PendingBillModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.bills_list, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PendingBillModel model = list.get(position);
        holder.BillName.setText(model.getBilling_type());
        try {
            holder.DueDate.setText(context.getString(R.string.bill_created)+" "+ Utils.convertDate(model.getCreated_at()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //holder.Status.setText(model.getStatus());
        holder.Amount.setText(context.getString(R.string.ksh)+" "+formatNumber(model.getAmount()));
        holder.Pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AlertDialog dialog = Mpesa_Popup(context,Integer.parseInt(model.getAmount()),100);
//                dialog.show();

                context.startActivity(new Intent(context, BillPresentment.class));
            }
        });

        if(list.size()==position+1){
            holder.layout.setBackgroundResource(0);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView BillName, Amount,DueDate,Status,Pay;
        ConstraintLayout layout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            BillName = itemView.findViewById(R.id.BillName);
            Amount = itemView.findViewById(R.id.Amount);
            DueDate = itemView.findViewById(R.id.DueDate);
            //Status = itemView.findViewById(R.id.Status);
            Pay = itemView.findViewById(R.id.Pay);
            layout = itemView.findViewById(R.id.ParentLayout);


        }
    }
}
