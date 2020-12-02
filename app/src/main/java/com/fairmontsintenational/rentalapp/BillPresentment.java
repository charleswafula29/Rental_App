package com.fairmontsintenational.rentalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BillPresentment extends AppCompatActivity {
    private TextView Bank;
    private TextView Money;
    private ConstraintLayout MobileMoneyLayout, BankLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_presentment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolBar);
        toolbar.setNavigationIcon(R.drawable.ic_white_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bank = findViewById(R.id.Banks2);
        Money = findViewById(R.id.Card1);
        MobileMoneyLayout = findViewById(R.id.MobileMoneyLayout);
        BankLayout = findViewById(R.id.BankLayout);

        Bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BankLayout.setVisibility(View.VISIBLE);
                MobileMoneyLayout.setVisibility(View.GONE);
                Bank.setBackground(getDrawable(R.drawable.rounded_border_button));
                Money.setBackground(getDrawable(R.drawable.material_login));
            }
        });

        Money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BankLayout.setVisibility(View.GONE);
                MobileMoneyLayout.setVisibility(View.VISIBLE);
                Money.setBackground(getDrawable(R.drawable.rounded_border_button));
                Bank.setBackground(getDrawable(R.drawable.material_login));
            }
        });
    }
}
