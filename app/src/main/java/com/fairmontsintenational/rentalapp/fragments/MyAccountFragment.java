package com.fairmontsintenational.rentalapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.fairmontsintenational.rentalapp.CreateComplaint;
import com.fairmontsintenational.rentalapp.Homepage;
import com.fairmontsintenational.rentalapp.Login;
import com.fairmontsintenational.rentalapp.R;
import com.fairmontsintenational.rentalapp.classes.Sessions;
import com.fairmontsintenational.rentalapp.models.UserModel;
import com.fairmontsintenational.rentalapp.utilities.BaseUrl;
import com.fairmontsintenational.rentalapp.utilities.BottomPopupActivity;
import com.fairmontsintenational.rentalapp.utilities.Utils;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    private Context context;
    private Gson gson = new Gson();
    private CircleImageView AccountProfile_image;
    private ProgressBar progressBar;
    private AlertDialog dialog;
    private static final int REQUEST_IMAGE = 100;
    private static String ACCESS_TOKEN;

    public MyAccountFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_account, container, false);

        ACCESS_TOKEN = Paper.book().read("ACCESS_TOKEN");
        String ActiveUser = Paper.book().read("ActiveUser");
        UserModel userModel = gson.fromJson(ActiveUser, UserModel.class);

        TextView Names = root.findViewById(R.id.Account_names);
        TextView IdNo = root.findViewById(R.id.Email);
        AccountProfile_image = root.findViewById(R.id.AccountProfile_image);
        progressBar = root.findViewById(R.id.ProgressBar);
        dialog = Utils.ShowLoader(context);

        Names.setText(Utils.convertCapitalText(userModel.getFirst_name()+" "+userModel.getMiddle_name()+" "+userModel.getLast_name()));
        IdNo.setText(getString(R.string.id_no)+" "+ userModel.getId_number());

        if(userModel.getProfile_image()!=null){
            progressBar.setVisibility(View.VISIBLE);
            Bitmap bitmap = ConvertBase64(userModel.getProfile_image());
            AccountProfile_image.setImageBitmap(bitmap);
            progressBar.setVisibility(View.GONE);
        }

        root.findViewById(R.id.Logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });

        root.findViewById(R.id.ChoosePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerSelect();
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri uri = data.getParcelableExtra("path");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                    // loading profile image from local cache
                    assert uri != null;
                    dialog.show();
                    loadProfile(uri.toString(), bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, getString(R.string.failed_picture), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadProfile(String url, Bitmap bitmap) {
        Log.d("IMAGE PATH", "Image cache path: " + url);
        Glide.with(this).load(url)
                .into(AccountProfile_image);
        AccountProfile_image.setColorFilter(ContextCompat.getColor(context, android.R.color.transparent));
        UploadPic(bitmap);
    }

    private void UploadPic(Bitmap bitmap) {
        String url = BaseUrl.UploadTenantProfilePic();
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("profile_image",convertBitmap(bitmap));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(hashMap),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        UpdateUserDetails();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                Toast.makeText(context, getString(R.string.failed_picture), Toast.LENGTH_SHORT).show();
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

    private void UpdateUserDetails() {
        String url = BaseUrl.GetLoggedInUser();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        UserModel userModel = gson.fromJson(response.toString(),UserModel.class);
                        String ActiveUser = gson.toJson(userModel);
                        Paper.book().write("ActiveUser",ActiveUser);

                        dialog.dismiss();
                        Toast.makeText(context, "Picture uploaded successfully!", Toast.LENGTH_LONG).show();

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
        RequestQueue queue = Volley.newRequestQueue(context);
        jsonObjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    private String convertBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    private void triggerSelect() {
        Dexter.withActivity((Activity) context)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        BottomPopupActivity.showImagePickerOptions(context, new BottomPopupActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(context, BottomPopupActivity.class);
        intent.putExtra(BottomPopupActivity.INTENT_IMAGE_PICKER_OPTION, BottomPopupActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(BottomPopupActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(BottomPopupActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(BottomPopupActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(BottomPopupActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(BottomPopupActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(BottomPopupActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(context, BottomPopupActivity.class);
        intent.putExtra(BottomPopupActivity.INTENT_IMAGE_PICKER_OPTION, BottomPopupActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(BottomPopupActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(BottomPopupActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(BottomPopupActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void LogOut() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.confirmation_popup, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Button proceed = view.findViewById(R.id.ConfirmationOk);

        (view.findViewById(R.id.ConfirmationCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().write("Session", Sessions.InActive.toString());
                Paper.book().write("ACCESS_TOKEN", "none");
                dialog.dismiss();
                startActivity(new Intent(context, Login.class));
                ((Activity)context).finish();
            }
        });

        dialog.show();
    }

    public static Bitmap ConvertBase64(String base64String){
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
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
