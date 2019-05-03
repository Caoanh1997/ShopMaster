package com.example.caoan.shopmaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.caoan.shopmaster.Adapter.FragmentAdapter;
import com.example.caoan.shopmaster.EventBus.LoadEvent;
import com.example.caoan.shopmaster.FragmentComponent.DrinkFragment;
import com.example.caoan.shopmaster.FragmentComponent.FoodFragment;
import com.example.caoan.shopmaster.ViewPageTransformer.ZoomOutPageTransformer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    //private TabHost tabHost;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private FragmentAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tablayout);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //initTabhost();
        fillFragment();

        adapter = new FragmentAdapter(getSupportFragmentManager(),fragmentList);

        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true,new ZoomOutPageTransformer());
        tabLayout.setupWithViewPager(viewPager);
        /*viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //tabHost.setCurrentTab(viewPager.getCurrentItem());

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

        /*tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                viewPager.setCurrentItem(tabHost.getCurrentTab());
            }
        });*/
        Intent intent = getIntent();
        int tab = intent.getIntExtra("tab",0);
        viewPager.setCurrentItem(tab);
    }

    /*@Override
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
            case R.id.logout:
                firebaseAuth.signOut();
                Toast.makeText(getApplicationContext(),"Sign out ok",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProductActivity.this, LoginActivity.class));
                return true;
            case R.id.manage:
                startActivity(new Intent(this, OrderActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    private void fillFragment() {
        fragmentList = new ArrayList<Fragment>();

        String key = getKey_Store();

        FoodFragment foodFragment = new FoodFragment().newInstance(key);
        //FoodFragment foodFragment1 = new FoodFragment().newInstance(key);
        DrinkFragment drinkFragment = new DrinkFragment().newInstance(key);
        fragmentList.add(foodFragment);
        fragmentList.add(drinkFragment);
    }

    /*private void initTabhost() {
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
    }*/

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

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onCustomLoadEvent(LoadEvent loadEvent) {
        if (loadEvent.isLoad()) {
            if (loadEvent.getFragment() instanceof FoodFragment) {

            }
        }
    }
}
