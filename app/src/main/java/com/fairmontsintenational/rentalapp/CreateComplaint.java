package com.fairmontsintenational.rentalapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.bumptech.glide.Glide;
import com.fairmontsintenational.rentalapp.fragments.MyRentalsFragment;
import com.fairmontsintenational.rentalapp.models.PropertyModel;
import com.fairmontsintenational.rentalapp.models.TenantModel;
import com.fairmontsintenational.rentalapp.models.UserModel;
import com.fairmontsintenational.rentalapp.utilities.BaseUrl;
import com.fairmontsintenational.rentalapp.utilities.BottomPopupActivity;
import com.fairmontsintenational.rentalapp.utilities.Utils;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class CreateComplaint extends AppCompatActivity {

    private static String ACCESS_TOKEN;
    private Gson gson = new Gson();
    private TenantModel tenantModel;
    private List<PropertyModel> list;
    private ProgressBar progressBar;
    private MaterialSpinner HouseSpinner;
    private ImageView previewPhoto;
    private static final int REQUEST_IMAGE = 100;
    private AlertDialog loader;
    private boolean isUploaded = false;
    private EditText Title,Description;
    private ConstraintLayout layout;
    private String imageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_complaint);

        loader = Utils.ShowLoader(this);
        //loader.setCancelable(true);
        Paper.init(this);
        ACCESS_TOKEN = Paper.book().read("ACCESS_TOKEN");
        String ActiveUser = Paper.book().read("ActiveUser");
        UserModel userModel = gson.fromJson(ActiveUser, UserModel.class);

        progressBar = findViewById(R.id.ProgressBar);
        HouseSpinner = findViewById(R.id.SelectHouse);
        previewPhoto = findViewById(R.id.AttachmentPhoto);
        Title = findViewById(R.id.ComplaintTitle);
        Description = findViewById(R.id.Description);
        layout = findViewById(R.id.Layout);

        findViewById(R.id.Title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getProperty();

        findViewById(R.id.AttachmentUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerSelect();
            }
        });

        findViewById(R.id.Submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HouseSpinner.getText().toString().isEmpty()){
                    Utils.ShowLongSnackBar(layout,"No house has been selected.");
                    getProperty();
                }else if(Title.getText().toString().isEmpty()){
                    Utils.ShowLongSnackBar(layout,"Kindly set complaint's title.");
                    Title.requestFocus();
                }else if(Description.getText().toString().isEmpty()){
                    Utils.ShowLongSnackBar(layout,"Kindly provide a description of the complaint.");
                    Description.requestFocus();
                }else{
                    SubmitComplaint(Title.getText().toString(),Description.getText().toString(),HouseSpinner.getText().toString());
                }
            }
        });
    }

    private void SubmitComplaint(String title, String desc, String hseUnit) {
        loader.show();
        String housing_unit_id = null;
        for (PropertyModel model: list){
            if (model.getHouse_unit().equals(hseUnit)){
                housing_unit_id = model.getHousing_unit_id();
            }
        }

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("name",title);
        hashMap.put("description",desc);
        //hashMap.put("tenant", String.valueOf(userModel.getId()));
        hashMap.put("housing_unit_id", housing_unit_id);
        if (isUploaded){
            hashMap.put("image", imageBase64);
        }else{
            hashMap.put("image", null);
        }

        //String param = gson.toJson(hashMap);
        Log.e("param",hashMap.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BaseUrl.GetComplaints(), new JSONObject(hashMap),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loader.dismiss();
                        Log.e("Complaint",response.toString());
                        Utils.ShowSuccessPopup(CreateComplaint.this,getString(R.string.complaint_success_title),getString(R.string.complaint_success));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                loader.dismiss();
                Utils.ShowLongSnackBar(layout,"Failed to create complaint!");
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
        RequestQueue queue = Volley.newRequestQueue(CreateComplaint.this);
        jsonObjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);

    }

    private void triggerSelect() {
        Dexter.withActivity(CreateComplaint.this)
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
        BottomPopupActivity.showImagePickerOptions(CreateComplaint.this, new BottomPopupActivity.PickerOptionListener() {
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
        Intent intent = new Intent(CreateComplaint.this, BottomPopupActivity.class);
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
        Intent intent = new Intent(CreateComplaint.this, BottomPopupActivity.class);
        intent.putExtra(BottomPopupActivity.INTENT_IMAGE_PICKER_OPTION, BottomPopupActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(BottomPopupActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(BottomPopupActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(BottomPopupActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                loader.show();

                assert data != null;
                Uri uri = data.getParcelableExtra("path");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    // loading profile image from local cache
                    assert uri != null;
                    loadProfile(uri.toString(), bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    loader.dismiss();
                    Utils.ShowLongSnackBar(layout,getString(R.string.failed_picture));
                }
            }
        }
    }

    private void loadProfile(String url, Bitmap bitmap) {
        Log.d("IMAGE PATH", "Image cache path: " + url);
        Glide.with(this).load(url)
                .into(previewPhoto);
        previewPhoto.setColorFilter(ContextCompat.getColor(CreateComplaint.this, android.R.color.transparent));
        isUploaded = true;
        imageBase64 = convertBitmap(bitmap);

        loader.dismiss();
        //UploadPic(bitmap);
    }

    private String convertBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    private void getProperty() {
        String url = BaseUrl.TenantDetails();
        list = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            tenantModel = gson.fromJson(response.getJSONObject("data").toString(), TenantModel.class);
                            list.addAll(tenantModel.getLeases());

                            List<String> houses = new ArrayList<>();
                            for (PropertyModel model: list){
                                houses.add(model.getHouse_unit());
                            }

                            HouseSpinner.setItems(houses);

                            //Log.e("List",list.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                error.printStackTrace();
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
        RequestQueue queue = Volley.newRequestQueue(CreateComplaint.this);
        jsonObjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);

    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateComplaint.this);
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
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


}
