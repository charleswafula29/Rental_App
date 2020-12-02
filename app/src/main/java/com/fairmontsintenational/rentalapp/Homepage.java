package com.fairmontsintenational.rentalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fairmontsintenational.rentalapp.fragments.ComplaintsFragment;
import com.fairmontsintenational.rentalapp.fragments.MyAccountFragment;
import com.fairmontsintenational.rentalapp.fragments.MyRentalsFragment;
import com.fairmontsintenational.rentalapp.fragments.TransactionsFragment;
import com.fairmontsintenational.rentalapp.models.TenantModel;
import com.fairmontsintenational.rentalapp.models.UserModel;
import com.fairmontsintenational.rentalapp.utilities.BaseUrl;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class Homepage extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Paper.init(this);
        String ACCESS_TOKEN = Paper.book().read("ACCESS_TOKEN");

        //BottomNavigationView navView = findViewById(R.id.nav_view);

        ChipNavigationBar chipNavigationBar = findViewById(R.id.ChipNavigation);

        chipNavigationBar.setItemSelected(R.id.navigation_home,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new MyRentalsFragment()).commit();

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i){
                    case R.id.navigation_home:
                        fragment = new MyRentalsFragment();
                        break;
                    case R.id.navigation_transactions:
                        fragment = new TransactionsFragment();
                        break;
                    case R.id.navigation_complaints:
                        fragment = new ComplaintsFragment();
                        break;
                    case R.id.navigation_my_account:
                        fragment = new MyAccountFragment();
                        break;
                }
                if(fragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
                }
            }
        });

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,R.id.navigation_transactions, R.id.navigation_my_account)
                .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.click_back_again), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
