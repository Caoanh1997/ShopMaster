package com.example.caoan.shopmaster.FragmentComponent;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
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
import com.example.caoan.shopmaster.Adapter.FoodAdapter;
import com.example.caoan.shopmaster.AddDrinkActivity;
import com.example.caoan.shopmaster.EditDrinkActivity;
import com.example.caoan.shopmaster.Model.Drink;
import com.example.caoan.shopmaster.Model.Drink;
import com.example.caoan.shopmaster.Model.Food;
import com.example.caoan.shopmaster.ProductActivity;
import com.example.caoan.shopmaster.R;
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),isOnline()+" "+String.valueOf(drinkList.size()),Toast.LENGTH_SHORT).show();
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
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
                                alertDialog.dismiss();
                                startActivity(new Intent(getActivity(), ProductActivity.class));
                            }
                        });
                    }
                });
                btnedit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), EditDrinkActivity.class);
                        intent.putExtra("Drink",drink);
                        startActivity(intent);
                    }
                });
                return false;
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
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Drink drink = (Drink) adapterView.getItemAtPosition(i);
                //Toast.makeText(getContext(),drink.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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
