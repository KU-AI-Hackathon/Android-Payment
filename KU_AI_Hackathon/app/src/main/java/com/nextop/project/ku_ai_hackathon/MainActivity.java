package com.nextop.project.ku_ai_hackathon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BillingManager bm;
    BluetoothManager blem;
    private String GP_LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjGjZqpS72P+GJQ7Y0pDDILKZ8H/ITCLS+8NT58RXNS5Yd78JwEnJ3mzgYeyBEukZyMCeoJ6QnepRvYo9qm1gzEHeGtp2RsA8LV0G22wi+IM65Wlfo7736ml8okLNBCUGjlBp9B7fxf6LDFeN7Vu0SVH9DOqMTnJwJX02shY8ms5w0Dy8K/CMvNEAJopxGxATuic631qLzhUUZvHuIDby8tN9DgvznmI/0o20hTs9+IowiMkLyTQF/7bG23dtNcPqBQoJnh8IHLK7QR5L4xMIGSMDc7ENA0Ho885L5GYFvGB9ZiR6V4Qdi/bxyWApwUvfSr401w7CyHOs4LDufXMnRQIDAQAB";

    //

    ImageButton ib;
    TextView tv;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textView2);
        ib = findViewById(R.id.imageButton2);

        bm = new BillingManager(this, this);
        blem = new BluetoothManager(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        blem.destroy();
    }

    //

    public void pairing(View view){

        //success
        Toast.makeText(this, "자판기와 연결되었습니다.", Toast.LENGTH_SHORT).show();
        ib.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchase(0);
            }
        });
        ib.setImageResource(R.drawable.mask_1);
        tv.setText("마스크를 구매하시려면 화면을 터치하세요.");
    }

    //

    public void purchase(int index){
        //bm.get_Sku_Detail_List();
        bm.purchase(index);
    }

    public void purchaseSuccess(){
        Toast.makeText(this, "마스크 구매에 성공했습니다.", Toast.LENGTH_SHORT).show();
        ib.setEnabled(false);
        ib.setImageResource(R.drawable.mask_2);
        tv.setText("마스크를 자판기로부터 가져가주세요.");
    }
}