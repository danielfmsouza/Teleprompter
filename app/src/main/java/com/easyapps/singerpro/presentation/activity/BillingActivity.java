package com.easyapps.singerpro.presentation.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.easyapps.singerpro.R;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;

import java.util.List;

public class BillingActivity extends BaseActivity implements PurchasesUpdatedListener {
    private static final String TAG = "InAppBilling";

    static final String ITEM_SKU_DONATION = "????????????????????????";

    private Button mBuyButton;
    private String mAdRemovalPrice;
    private SharedPreferences mSharedPreferences;

    private BillingClient mBillingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        hideActionBar();

        mBillingClient = BillingClient.newBuilder(BillingActivity.this).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

            }

            @Override
            public void onBillingServiceDisconnected() {
                //TODO implement your own retry policy
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });

        SkuDetails skuDetails = new SkuDetails(ITEM_SKU_DONATION);

        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(ITEM_SKU_DONATION)
                        .setType(BillingClient.SkuType.INAPP)
                        .build();
                int responseCode = mBillingClient
                        .launchBillingFlow(BillingActivity.this, flowParams)
                        .getResponseCode();
            }
        });
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.backToMain(this);
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

    }
}
