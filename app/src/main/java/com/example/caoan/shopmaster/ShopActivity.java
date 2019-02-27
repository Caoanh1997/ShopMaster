package com.example.caoan.shopmaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caoan.shopmaster.Adapter.StoreAdapter;
import com.example.caoan.shopmaster.Model.Store;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView tvuserid;
    private ListView lvstore;
    private List<Store> storeList;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private ProgressBar progressBar;
    private StoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(ShopActivity.this, AddShopActivity.class));
            }
        });

        tvuserid = findViewById(R.id.tvuserid);
        lvstore = findViewById(R.id.lvstore);
        progressBar = findViewById(R.id.progressbar);
        lvstore.setVisibility(View.INVISIBLE);
        registerForContextMenu(lvstore);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storeList = new ArrayList<>();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            tvuserid.setText(user.getUid());
            firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference reference = firebaseDatabase.getReference("Store");

            new ProgressBarProcess().execute();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Store store = snapshot.getValue(Store.class);
                        if(store.getUserkey().equals(user.getUid())){
                            storeList.add(store);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        lvstore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String key_store = ((Store) adapterView.getItemAtPosition(i)).getKey();
                Toast.makeText(getApplicationContext(),key_store,Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("key_store", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("key",key_store);
                editor.commit();
                startActivity(new Intent(ShopActivity.this,ProductActivity.class));
            }
        });

        lvstore.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                lvstore.setTag(i);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                Toast.makeText(getApplicationContext(),"Add",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ShopActivity.this, AddShopActivity.class));
                return true;
            case R.id.logout:
                firebaseAuth.signOut();
                Toast.makeText(getApplicationContext(),"Sign out ok",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ShopActivity.this, LoginActivity.class));
                return true;
            case R.id.about:
                Toast.makeText(getApplicationContext(),"About",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.refresh:
                finish();
                startActivity(getIntent());
                return true;
            case R.id.manage:
                startActivity(new Intent(this,OrderManagementActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(ShopActivity.this, LoginActivity.class));
        }
        super.onStart();
    }

    class ProgressBarProcess extends AsyncTask<Void,Integer,String> {
        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            lvstore.setVisibility(View.VISIBLE);
            adapter = new StoreAdapter(ShopActivity.this,storeList);
            lvstore.setAdapter(adapter);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(Void... voids) {
            for (int i =0;i<100;i++){
                publishProgress(i);
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Done";
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit:
                Store store = storeList.get((Integer) lvstore.getTag());
                Intent intent = new Intent(ShopActivity.this, EditShopActivity.class);
                intent.putExtra("store", store);
                startActivity(intent);
                return true;
            case R.id.delete:
                int i = (int) lvstore.getTag();
                String key = storeList.get(i).getKey();
                String urlimage = storeList.get(i).getUrlImage();
                System.out.println(key);
                DeleteStore(key,urlimage);
                finish();
                startActivity(getIntent());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    public void DeleteStore(String key, String urlimage){
        DatabaseReference reference = firebaseDatabase.getReference("Store");
        reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Delete store success");
            }
        });

        DatabaseReference reference_product = firebaseDatabase.getReference("Product");
        reference_product.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Delete product success");
            }
        });
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(urlimage);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Delete image success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Delete image failed, "+e.getMessage());
            }
        });

    }
}
