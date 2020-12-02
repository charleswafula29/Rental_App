package com.fairmontsintenational.rentalapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fairmontsintenational.rentalapp.models.PhotosModel;
import com.fairmontsintenational.rentalapp.models.PropertyModel;
import com.fairmontsintenational.rentalapp.models.UserModel;
import com.fairmontsintenational.rentalapp.utilities.Utils;
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener;
import com.github.chrisbanes.photoview.OnViewDragListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import pereira.agnaldo.previewimgcol.ImageCollectionView;

public class RentalDetails extends AppCompatActivity {
    private RecyclerView recyclerView;
    //private ImageCollectionView collectionView;
    //private PhotoView photoView;
    private PropertyModel propertyModel;
    private UserModel userModel;
    Gson gson = new Gson();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_details);

        //collectionView = findViewById(R.id.Images);
        //photoView = findViewById(R.id.photoView);
        //photoView.setZoomable(true);

        Paper.init(this);
        Intent intent = getIntent();
        final String houseDetails = intent.getStringExtra("HouseDetails");
        propertyModel = gson.fromJson(houseDetails, PropertyModel.class);

        String ActiveUser = Paper.book().read("ActiveUser");
        userModel = gson.fromJson(ActiveUser, UserModel.class);

        ((TextView) findViewById(R.id.HouseName)).setText(Utils.convertCapitalText(propertyModel.getProperty())+", "+propertyModel.getHouse_unit());
        ((TextView) findViewById(R.id.TenantName)).setText(Utils.convertCapitalText(userModel.getFirst_name()+" "+userModel.getLast_name()));
        ((TextView) findViewById(R.id.TenantPhone)).setText(userModel.getPhone()!=null? userModel.getPhone(): getString(R.string.not_set));
        ((TextView) findViewById(R.id.TenantID)).setText(userModel.getId_number()!=null? userModel.getId_number(): getString(R.string.not_set));
        ((TextView) findViewById(R.id.BuildingName)).setText(Utils.convertCapitalText(propertyModel.getProperty()));
        ((TextView) findViewById(R.id.HouseNo)).setText(propertyModel.getHouse_unit());
        ((TextView) findViewById(R.id.HouseID)).setText(propertyModel.getHousing_unit_id());




//        List<PhotosModel> list = new ArrayList<>();
//        list.add(new PhotosModel("https://images.pexels.com/photos/106399/pexels-photo-106399.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500","None"));
//        list.add(new PhotosModel("https://images.pexels.com/photos/1546166/pexels-photo-1546166.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500","None"));
//        list.add(new PhotosModel("https://images.pexels.com/photos/1438832/pexels-photo-1438832.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500","None"));
//        list.add(new PhotosModel("https://images.pexels.com/photos/347141/pexels-photo-347141.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500","None"));
//        list.add(new PhotosModel("https://images.pexels.com/photos/1475938/pexels-photo-1475938.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500","None"));
//        list.add(new PhotosModel("https://images.pexels.com/photos/772177/pexels-photo-772177.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500","None"));
//        list.add(new PhotosModel("https://images.pexels.com/photos/1643389/pexels-photo-1643389.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500","None"));
//        list.add(new PhotosModel("https://images.pexels.com/photos/1115804/pexels-photo-1115804.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500","None"));
//        list.add(new PhotosModel("https://images.pexels.com/photos/632522/pexels-photo-632522.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500","None"));
//        list.add(new PhotosModel("https://images.pexels.com/photos/1580329/pexels-photo-1580329.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500","None"));
//
//        LoadPhotos(list);


    }

//    private void LoadPhotos(List<PhotosModel> list) {
//        for(PhotosModel model: list){
//
//            Glide.with(RentalDetails.this)
//                    .asBitmap()
//                    .load(model.getUrl())
//                    .into(new CustomTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            collectionView.addImage(resource, new ImageCollectionView.OnImageClickListener() {
//                                @Override
//                                public void onClick(Bitmap bitmap, ImageView imageView) {
//                                    photoView.setImageBitmap(bitmap);
//                                    photoView.setVisibility(View.VISIBLE);
//                                }
//                            }, new ImageCollectionView.OnImageLongClickListener() {
//                                @Override
//                                public void onLongClick(Bitmap bitmap, ImageView imageView) {
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onLoadCleared(@Nullable Drawable placeholder) {
//                        }
//                    });
//        }
//    }

//    @Override
//    public void onBackPressed() {
//        if (photoView.getVisibility() == View.VISIBLE){
//            photoView.setVisibility(View.GONE);
//        }else {
//            super.onBackPressed();
//        }
//    }
}
