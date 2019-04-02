package com.example.caoan.shopmaster;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.caoan.shopmaster.EventBus.BillEvent;
import com.example.caoan.shopmaster.EventBus.DeleteEvent;
import com.example.caoan.shopmaster.EventBus.FailedEvent;
import com.example.caoan.shopmaster.EventBus.LoadEvent;
import com.example.caoan.shopmaster.EventBus.SucessEvent;
import com.example.caoan.shopmaster.FragmentComponent.ConfirmOrderFragment;
import com.example.caoan.shopmaster.FragmentComponent.DeleteOrderFragment;
import com.example.caoan.shopmaster.FragmentComponent.DeliveredOrderFragment;
import com.example.caoan.shopmaster.FragmentComponent.TransportOrderFragment;
import com.example.caoan.shopmaster.Model.Bill;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OrderActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private String userID;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Confirm");

        bottomNavigationView = findViewById(R.id.navigation);
        LoadFragment(new ConfirmOrderFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()){
                    case R.id.confirm:
                        fragment = new ConfirmOrderFragment();
                        LoadFragment(fragment);
                        actionBar.setTitle("Confirm");
                        return true;
                    case R.id.transport:
                        fragment = new TransportOrderFragment();
                        LoadFragment(fragment);
                        actionBar.setTitle("Transport");
                        return true;
                    case R.id.delivered:
                        fragment = new DeliveredOrderFragment();
                        LoadFragment(fragment);
                        actionBar.setTitle("Delivered");
                        return true;
                    case R.id.delete:
                        fragment = new DeleteOrderFragment();
                        LoadFragment(fragment);
                        actionBar.setTitle("Deleted");
                        return true;
                }
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.confirm);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        final int i = bottomNavigationView.getSelectedItemId();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(i==R.id.delivered){
                    DeliveredOrderFragment.Search(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void LoadFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Subscribe
    public void Load(LoadEvent loadEvent){
        if(loadEvent.isLoad()){
            if(loadEvent.getFragment() instanceof ConfirmOrderFragment){
                bottomNavigationView.setSelectedItemId(R.id.confirm);
            }else {
                if(loadEvent.getFragment() instanceof TransportOrderFragment){
                    bottomNavigationView.setSelectedItemId(R.id.transport);
                }
            }
        }
    }

    @Subscribe
    public void OnCustomDeleteEvent(DeleteEvent deleteEvent){
        getUserID();
        final Bill bill = deleteEvent.getBill();
        final Bill b = new Bill(bill.getKey_cart(),bill.getName_user(),bill.getAddress(),
                bill.getPhone(),bill.getUserID(), bill.getCartList(), bill
                .getTotal_price(), "Đơn hàng đã hủy từ chủ cửa hàng",
                bill.getKey_store(), bill.getDatetime(), bill.getDatetime_delivered());

        FirebaseDatabase.getInstance().getReference("DeleteOrder")
                .child(userID).child(bill.getKey_cart()).setValue(b).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference("Order").child(bill.getUserID())
                        .child("Delete").child(bill.getKey_cart()).setValue(b).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });
    }

    @Subscribe
    public void OnCustomFailedEvent(FailedEvent failedEvent){
        getUserID();
        final Bill bill = failedEvent.getBill();
        final Bill b = new Bill(bill.getKey_cart(),bill.getName_user(),bill.getAddress(),
                bill.getPhone(),bill.getUserID(), bill.getCartList(), bill
                .getTotal_price(), "Giao hàng thất bại",
                bill.getKey_store(), bill.getDatetime(), bill.getDatetime_delivered());

        FirebaseDatabase.getInstance().getReference("DeleteOrder")
                .child(userID).child(bill.getKey_cart()).setValue(b).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference("Order").child(bill.getUserID())
                        .child("Delete").child(bill.getKey_cart()).setValue(b).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });
    }

    @Subscribe
    public void OnCustomSuccessEvent(SucessEvent sucessEvent){
        getUserID();
        final Bill bill = sucessEvent.getBill();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format_date = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format_time = new SimpleDateFormat("HH:mm:ss");
        String day_delivered = format_date.format(calendar.getTime())+" "+format_time.format(calendar.getTime());

        final Bill b = new Bill(bill.getKey_cart(),bill.getName_user(),bill.getAddress(),
                bill.getPhone(),bill.getUserID(), bill.getCartList(), bill
                .getTotal_price(), "Giao hàng thành công",
                bill.getKey_store(), bill.getDatetime(),day_delivered);

        FirebaseDatabase.getInstance().getReference("DeliveredOrder")
                .child(userID).child(bill.getKey_cart()).setValue(b).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference("Order").child(bill.getUserID())
                        .child("Delivered").child(bill.getKey_cart()).setValue(b).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });
    }

    @Subscribe
    public void OnCustomBillEvent(BillEvent billEvent){
        getUserID();
        final Bill bill = billEvent.getBill();
        final Bill b = new Bill(bill.getKey_cart(),bill.getName_user(),bill.getAddress(),
                bill.getPhone(),bill.getUserID(), bill.getCartList(), bill
                .getTotal_price(), "Đang giao",
                bill.getKey_store(), bill.getDatetime(), bill.getDatetime_delivered());

        FirebaseDatabase.getInstance().getReference("TransportOrder")
                .child(userID).child(bill.getKey_cart()).setValue(b).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference("Order").child(bill.getUserID())
                        .child("Transport").child(bill.getKey_cart()).setValue(b).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });
    }

    public void getUserID(){
        userID = getSharedPreferences("Account", Context.MODE_PRIVATE).getString("userID","");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
