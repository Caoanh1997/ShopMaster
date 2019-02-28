package com.example.caoan.shopmaster;

import android.app.TabActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

public class OrderActivity extends TabActivity {

    private TabHost tabHost;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Xác nhận")
                .setContent(new Intent(this,OrderManagementActivity.class)));
        view = getLayoutInflater().inflate(R.layout.transport_tabwidget_layout,null);
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(view)
                .setContent(new Intent(this,TransportActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("Đã giao")
                .setContent(new Intent(this,DeliveredActivity.class)));
//        tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("Đã giao")
//                .setContent(new Intent(this,DeliveredActivity.class)));

    }
}
