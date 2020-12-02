package com.fairmontsintenational.rentalapp.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.Env;
import com.androidstudy.daraja.util.Settings;
import com.androidstudy.daraja.util.TransactionType;
import com.fairmontsintenational.rentalapp.Login;
import com.fairmontsintenational.rentalapp.R;
import com.fairmontsintenational.rentalapp.classes.Sessions;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import io.paperdb.Paper;

public class Utils {

    public static String removeHtml(String html) {
        html = html.replaceAll("<(.*?)\\>","");
        html = html.replaceAll("<(.*?)\\\n","");
        html = html.replaceFirst("(.*?)\\>", "");
        html = html.replaceAll("&nbsp;","");
        html = html.replaceAll("&amp;","");
        return html;
    }

    public interface PickerOptionListener {
        void onProceedSelected();
    }

    public static String convertCapitalText(String text) {
        String str = text.toLowerCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String formatNumber(String amount) {
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);

        Double d = Double.valueOf(amount);
        return myFormat.format(d);
    }

    public static String pickPreviewText(String text) {
        int length = text.length();
        String preview;
        if (length > 20) {
            preview = text.substring(0, 20) + "...";
        } else {
            preview = text;
        }
        return preview;
    }

    public static String pickPreviewBody(String text) {
        int length = text.length();
        String preview;
        if (length > 70) {
            preview = text.substring(0, 70) + "....";
        } else {
            preview = text;
        }
        return preview;
    }

    public static String getTodayDate(){
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return dateFormat.format(today);
    }

    public static String convertDate(String parsedate) throws ParseException {
        //String new_date = parsedate.replace("T00:00:00", "");
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        Date date = sdf.parse(parsedate);
        assert date != null;
        return new SimpleDateFormat("EE MMM dd", java.util.Locale.getDefault()).format(date);
    }

    public static String AltConvertDate(String parsedate) throws ParseException {
        DateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd", java.util.Locale.getDefault());
        Date date = sdf.parse(parsedate);
        assert date != null;
        return new SimpleDateFormat("EE MMM dd", java.util.Locale.getDefault()).format(date);
    }

    public static String SecondAltConvertDate(String parsedate) throws ParseException {
        DateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm", java.util.Locale.getDefault());
        Date date = sdf.parse(parsedate);
        assert date != null;
        return new SimpleDateFormat("EE MMM dd", java.util.Locale.getDefault()).format(date);
    }

    public static String StatementConvertDate(String parsedate) throws ParseException {
        DateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSS", java.util.Locale.getDefault());
        Date date = sdf.parse(parsedate);
        assert date != null;
        return new SimpleDateFormat("EE MMM dd", java.util.Locale.getDefault()).format(date);
    }

    public static AlertDialog ShowLoader(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.loadingdialog, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        //dialog.show();
        return dialog;
    }

    @SuppressLint("SetTextI18n")
    public static AlertDialog Mpesa_Popup(final Context context,Integer amount, final Integer LeaseID){
//        final String timestamp = Settings.generateTimestamp();
//        final String password = Settings.generatePassword(BusinessShortCode, Passkey, timestamp);
//
//        final Daraja mydaraja = Daraja.with("DLBNaGlClN7Dl78MWv8JgNCtA2jWBVr1", "zY9E3xniVsLIpKee", Env.PRODUCTION, new DarajaListener<AccessToken>() {
//            @Override
//            public void onResult(@NonNull AccessToken accessToken) {
//                Log.e("Mpesa Token", accessToken.getAccess_token());
//            }
//
//            @Override
//            public void onError(String error) {
//                Log.e("Mpesa Token Error", error);
//            }
//        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.lipa_na_mpesa_popup, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog.setCancelable(false);
        dialog.show();

        ((ImageView) view.findViewById(R.id.Close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView Balance = view.findViewById(R.id.Balance);
        final EditText PhoneNo = view.findViewById(R.id.phone_number);
        final EditText Amount = view.findViewById(R.id.Amount);
        final ProgressBar progressBar = view.findViewById(R.id.ProgressBar);
        final Button Pay = view.findViewById(R.id.Pay);
        Balance.append(": KSH "+formatNumber(String.valueOf(amount)));

//        Pay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String phone = PhoneNo.getText().toString().trim();
//                String cash = Amount.getText().toString().trim();
//                if(phone.isEmpty()){
//                    PhoneNo.setError(context.getString(R.string.phone_empty));
//                    PhoneNo.requestFocus();
//                }else if(phone.length()!=10){
//                    PhoneNo.setError(context.getString(R.string.phone_format));
//                    PhoneNo.requestFocus();
//                }else if(cash.isEmpty()){
//                    Amount.setError(context.getString(R.string.amount_empty));
//                    Amount.requestFocus();
//                }else if(Integer.parseInt(cash)<=0){
//                    Amount.setError(context.getString(R.string.amount_format));
//                    Amount.requestFocus();
//                }
//                else {
//                    Pay.setEnabled(false);
//                    progressBar.setVisibility(View.VISIBLE);
//
//                    LNMExpress lnmExpress = new LNMExpress(
//                            BusinessShortCode,
//                            Passkey,
//                            TransactionType.CustomerPayBillOnline,
//                            cash,
//                            phone,
//                            BusinessShortCode,
//                            phone,
//                            callbackurl,
//                            "SID"+SUID,
//                            transctionDesc
//                    );
//
//                    mydaraja.requestMPESAExpress(lnmExpress,
//                            new DarajaListener<LNMResult>() {
//                                @Override
//                                public void onResult(@NonNull LNMResult lnmResult) {
//                                    progressBar.setVisibility(View.GONE);
//                                    //((Activity)context).finish();
//                                    Log.e("MPESA_RESP", lnmResult.ResponseDescription);
//                                    dialog.dismiss();
//                                }
//
//                                @Override
//                                public void onError(String error) {
//                                    dialog.dismiss();
//                                    //((Activity)context).finish();
//                                    Toast.makeText(context, "Failed to trigger payment!", Toast.LENGTH_LONG).show();
//                                    Log.e("MPESA_FAILED", error.toString());
//                                    Log.e("MPESA_FAILED_TIMESTAMP", timestamp);
//                                    Log.e("MPESA_FAILED_PASS", password);
//                                }
//                            });
//                }
//            }
//        });

        return dialog;
    }

    public static void ShowLongSnackBar(View view, String Message) {
        Snackbar.make(view, Message, Snackbar.LENGTH_LONG).show();
    }

    public static void ShowConfirmationPopup(Context context, final String alert, final PickerOptionListener listener){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.confirmation_popup, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Button proceed = view.findViewById(R.id.ConfirmationOk);

        ((TextView) view.findViewById(R.id.ConfirmationText)).setText(alert);

        (view.findViewById(R.id.ConfirmationCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProceedSelected();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void ShowSuccessPopup(final Context context, String Title, String Message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.success_layout, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ((TextView) view.findViewById(R.id.title)).setText(Title);
        ((TextView) view.findViewById(R.id.Message)).setText(Message);
        view.findViewById(R.id.Close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ((Activity) context).finish();
            }
        });
        dialog.show();
    }
}
