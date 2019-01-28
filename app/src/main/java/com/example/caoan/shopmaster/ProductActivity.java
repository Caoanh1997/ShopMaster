package com.example.caoan.shopmaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import com.example.caoan.shopmaster.Adapter.FragmentAdapter;
import com.example.caoan.shopmaster.FragmentComponent.FoodFragment;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private TabHost tabHost;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private FragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        viewPager = findViewById(R.id.viewpager);

        initTabhost();
        fillFragment();

        adapter = new FragmentAdapter(getSupportFragmentManager(),fragmentList);

        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tabHost.setCurrentTab(viewPager.getCurrentItem());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                viewPager.setCurrentItem(tabHost.getCurrentTab());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.shop:
                startActivity(new Intent(ProductActivity.this,ShopActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fillFragment() {
        fragmentList = new ArrayList<Fragment>();

        String key = getKey_Store();

        FoodFragment foodFragment = new FoodFragment().newInstance(key);
        FoodFragment foodFragment1 = new FoodFragment().newInstance(key);
//        DrinkFragment drinkFragment = new DrinkFragment().newInstance();
        fragmentList.add(foodFragment);
        fragmentList.add(foodFragment1);
    }

    private void initTabhost() {
        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("tabfood").setIndicator("Food")
                .setContent(new TabHost.TabContentFactory() {
                    @Override
                    public View createTabContent(String s) {
                        View view = new View(ProductActivity.this);
                        return view;
                    }
                }));
        tabHost.addTab(tabHost.newTabSpec("tabdrink").setIndicator("Drink")
                .setContent(new TabHost.TabContentFactory() {
                    @Override
                    public View createTabContent(String s) {
                        View view = new View(ProductActivity.this);
                        return view;
                    }
                }));
    }

    @Override
    public void onBackPressed() {
        onBackPressed();
    }
    public String getKey_Store(){
        String key;
        SharedPreferences sharedPreferences = getSharedPreferences("key_store", Context.MODE_PRIVATE);
        key = sharedPreferences.getString("key","");

        return key;
    }
}
