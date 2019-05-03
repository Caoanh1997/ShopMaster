package com.example.caoan.shopmaster.FragmentComponent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caoan.shopmaster.Adapter.DrinkAdapter;
import com.example.caoan.shopmaster.AddDrinkActivity;
import com.example.caoan.shopmaster.EditDrinkActivity;
import com.example.caoan.shopmaster.LoginActivity;
import com.example.caoan.shopmaster.Model.Drink;
import com.example.caoan.shopmaster.OrderActivity;
import com.example.caoan.shopmaster.R;
import com.example.caoan.shopmaster.ShopActivity;
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

public class DrinkFragment extends Fragment {

    private View view;
    private TextView tv;
    private GridView gridView;
    private Button button,btnadddrink;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private String key;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private List<Drink> drinkList;
    private DrinkAdapter adapter;
    private DatabaseReference reference;
    private SwipeRefreshLayout swipeRefreshLayout;

    public DrinkFragment() {
        // Required empty public constructor
    }

    public static DrinkFragment newInstance(String str) {
        DrinkFragment fragment = new DrinkFragment();
        Bundle args = new Bundle();
        args.putString("s",str);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_drink, container, false);
        view = inflater.inflate(R.layout.fragment_drink, container, false);
        tv = view.findViewById(R.id.tv);
        gridView = view.findViewById(R.id.gv);
        button = view.findViewById(R.id.btsize);
        btnadddrink = view.findViewById(R.id.btnadddrink);
        progressBar = view.findViewById(R.id.progress);
        fab = view.findViewById(R.id.fab);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorAccent);

        Bundle bundle = getArguments();
        key = bundle.getString("s");
        tv.setText(key);
        gridView.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Product").child(key)
                .child("Drink");
        Load();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                drinkList.clear();
                Load();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),isOnline()+" "+String.valueOf(drinkList.size()),Toast.LENGTH_SHORT).show();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Drink drink = (Drink) adapterView.getItemAtPosition(i);
                final String key_drink = ((Drink) adapterView.getItemAtPosition(i)).getKey();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose action");
                view = getLayoutInflater().inflate(R.layout.product_action_layout,null);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                FloatingActionButton btnedit, btndelete;
                btnedit = view.findViewById(R.id.btnedit);
                btndelete = view.findViewById(R.id.btndelete);

                btndelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        new AlertDialog.Builder(getContext())
                                .setTitle("Warning").setMessage("Are you sure delete this product?")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialogInterface, int i) {
                                        DatabaseReference reference = firebaseDatabase.getReference("Product").child(key)
                                                .child("Drink");

                                        reference.child(key_drink).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                System.out.println("Delete drink success");
                                            }
                                        });
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(drink.getUrlimage());
                                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                System.out.println("Delete Image Drink success");
                                                dialogInterface.dismiss();
                                                drinkList.clear();
                                                Load();
                                                /*startActivity(new Intent(getActivity(), ProductActivity.class)
                                                .putExtra("tab",1));*/
                                            }
                                        });
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
                    }
                });
                btnedit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        Intent intent = new Intent(getActivity(), EditDrinkActivity.class);
                        intent.putExtra("Drink",drink);
                        startActivity(intent);
                    }
                });
            }
        });
        btnadddrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getActivity(), AddDrinkActivity.class));
                PopupMenu popupMenu = new PopupMenu(getContext(),btnadddrink);
                popupMenu.getMenuInflater().inflate(R.menu.context_menu,popupMenu.getMenu());
                popupMenu.show();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddDrinkActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                firebaseAuth.signOut();
                Toast.makeText(getContext(), "Sign out ok", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return true;
            case R.id.about:
                return true;
            case R.id.shop:
                startActivity(new Intent(getActivity(), ShopActivity.class));
                return true;
            case R.id.manage:
                startActivity(new Intent(getActivity(), OrderActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void Load(){
        progressBar.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);

        drinkList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Drink drink = snapshot.getValue(Drink.class);
                    drinkList.add(drink);
                }
                adapter = new DrinkAdapter(getContext(),drinkList);
                gridView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                gridView.setAdapter(adapter);
                gridView.invalidateViews();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class ProgressBarProcess extends AsyncTask<Void,Integer,String> {
        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            if(drinkList == null || drinkList.size()==0){
                Toast.makeText(getContext(),"Chưa có sản phẩm",Toast.LENGTH_SHORT).show();
            }else {
                adapter = new DrinkAdapter(getContext(),drinkList);
                gridView.setAdapter(adapter);
            }
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
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Done";
        }
    }

    public String isOnline(){
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return "Online";
        }else {
            return "Offline";
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu,menu);
    }

}
