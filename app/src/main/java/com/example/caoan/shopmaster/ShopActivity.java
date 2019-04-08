package com.example.caoan.shopmaster;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caoan.shopmaster.Adapter.StoreRecyclerViewAdapter;
import com.example.caoan.shopmaster.Model.Drink;
import com.example.caoan.shopmaster.Model.Food;
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
    //private ListView lvstore;
    private List<Store> storeList;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private ProgressBar progressBar;
    //private StoreAdapter adapter;
    private RecyclerView recyclerView;
    private StoreRecyclerViewAdapter storeRecyclerViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.rcvstore);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorAccent);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                storeList.clear();
                Load();
            }
        });

        // sdkmin = 21
        //Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.fade);
        //getWindow().setEnterTransition(fade);

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
        //lvstore = findViewById(R.id.lvstore);
        progressBar = findViewById(R.id.progressbar);
        //lvstore.setVisibility(View.INVISIBLE);
        //registerForContextMenu(lvstore);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        Load();
        ItemTouchHelper.Callback callback = new ItemTouchListenerCallBack(new ItemTouchListener() {
            @Override
            public void Swipe(final int position, final int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
                builder.setTitle("Warning").setMessage("Are you sure delete this shop?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                DeleteStore(storeList.get(position).getKey(), storeList.get(position).getUrlImage());
                                storeRecyclerViewAdapter.Swipe(position, direction);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                storeList.clear();
                                Load();
                            }
                        }).create().show();

            }

            @Override
            public void Move(int oldPosition, int newPosition) {
                storeRecyclerViewAdapter.Move(oldPosition, newPosition);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        /*lvstore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                storeRecyclerViewAdapter.getFilter().filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                firebaseAuth.signOut();
                Toast.makeText(getApplicationContext(), "Sign out ok", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ShopActivity.this, LoginActivity.class));
                return true;
            case R.id.about:
                Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.manage:
                startActivity(new Intent(this, OrderActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(ShopActivity.this, LoginActivity.class));
        }
        super.onStart();
    }

    public void Load() {
        storeList = new ArrayList<>();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            tvuserid.setText(user.getUid());
            firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference reference = firebaseDatabase.getReference("Store");

            reference.child(getSharedPreferences("Account", Context.MODE_PRIVATE)
                    .getString("userID", "")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Store store = snapshot.getValue(Store.class);
                        storeList.add(store);

                    }
                    progressBar.setVisibility(View.GONE);
                    storeRecyclerViewAdapter = new StoreRecyclerViewAdapter(storeList, ShopActivity.this);
                    recyclerView.setAdapter(storeRecyclerViewAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ShopActivity.this));
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                //Store store = storeList.get((Integer) lvstore.getTag());
                Intent intent = new Intent(ShopActivity.this, EditShopActivity.class);
                //intent.putExtra("store", store);
                startActivity(intent);
                return true;
            case R.id.delete:
                //int i = (int) lvstore.getTag();
                //String key = storeList.get(i).getKey();
                //String urlimage = storeList.get(i).getUrlImage();
                //System.out.println(key);
                //DeleteStore(key,urlimage);
                finish();
                startActivity(getIntent());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void DeleteStore(final String key, final String urlimage) {

        //System.out.println("Key store: "+key+"\nUrl image: "+urlimage);
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference reference = firebaseDatabase.getReference("Store");
        reference.child(getSharedPreferences("Account", Context.MODE_PRIVATE)
                .getString("userID", "")).child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference("Shopmaster").child(key)
                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Delete store infor success");
                        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(urlimage);
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                System.out.println("Delete image store success");
                                storeList.clear();
                                Load();
                            }
                        });
                    }
                });
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //get food and delete image food
        firebaseDatabase.getReference("Product").child(key).child("Food").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Food food = snapshot.getValue(Food.class);

                    firebaseStorage.getReferenceFromUrl(food.getUrlimage()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("Delete image food success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Delete image food failed: "+e.getMessage());
                        }
                    });
                }

                //get drink and delete image drink
                firebaseDatabase.getReference("Product").child(key).child("Drink")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Drink drink = snapshot.getValue(Drink.class);

                                    firebaseStorage.getReferenceFromUrl(drink.getUrlimage()).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    System.out.println("Delete image drink success");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            System.out.println("Delete image drink failed: " + e.getMessage());
                                        }
                                    });
                                }

                                //delete product infor of store
                                firebaseDatabase.getReference("Product").child(key).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                System.out.println("Delete infor product success");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("Delete infor product failed: " + e.getMessage());
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
