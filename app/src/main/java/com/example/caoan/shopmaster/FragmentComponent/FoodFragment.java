package com.example.caoan.shopmaster.FragmentComponent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.example.caoan.shopmaster.Adapter.FoodAdapter;
import com.example.caoan.shopmaster.AddFoodActivity;
import com.example.caoan.shopmaster.EditFoodActivity;
import com.example.caoan.shopmaster.Model.Food;
import com.example.caoan.shopmaster.R;
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

public class FoodFragment extends Fragment {

    private View view;
    private TextView tv;
    private GridView gridView;
    private Button button, btnaddfood;
    private List<Food> foodList;
    private FoodAdapter adapter;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private String key;
    private FloatingActionButton fab;
    private DatabaseReference reference;
    private SwipeRefreshLayout swipeRefreshLayout;

    public FoodFragment() {
        // Required empty public constructor
    }


    public static FoodFragment newInstance(String str) {
        FoodFragment fragment = new FoodFragment();
        Bundle args = new Bundle();
        args.putString("s", str);
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_food, container, false);
        tv = view.findViewById(R.id.tv);
        gridView = view.findViewById(R.id.gv);
        button = view.findViewById(R.id.btsize);
        btnaddfood = view.findViewById(R.id.btnaddfood);
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
                .child("Food");
        Load();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                foodList.clear();
                Load();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), isOnline() + " " + String.valueOf(foodList.size()), Toast.LENGTH_SHORT).show();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Food food = (Food) adapterView.getItemAtPosition(i);
                final String key_food = ((Food) adapterView.getItemAtPosition(i)).getKey();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose action");
                view = getLayoutInflater().inflate(R.layout.product_action_layout, null);
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
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(food.getUrlimage());
                                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                System.out.println("Delete Image Food success");
                                                DatabaseReference reference = firebaseDatabase.getReference("Product").child(key)
                                                        .child("Food");

                                                reference.child(key_food).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        System.out.println("Delete food success");
                                                        dialogInterface.dismiss();
                                                        foodList.clear();
                                                        Load();
                                                        //startActivity(new Intent(getActivity(), ProductActivity.class));
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        System.out.println("Delete food failed, " + e.getMessage());
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("Delete Image Food failed, " + e.getMessage());
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
                        Intent intent = new Intent(getActivity(), EditFoodActivity.class);
                        intent.putExtra("Food", food);
                        startActivity(intent);
                    }
                });
            }
        });

        /*gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Food food = (Food) adapterView.getItemAtPosition(i);
                final String key_food = ((Food) adapterView.getItemAtPosition(i)).getKey();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose action");
                view = getLayoutInflater().inflate(R.layout.product_action_layout, null);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                FloatingActionButton btnedit, btndelete;
                btnedit = view.findViewById(R.id.btnedit);
                btndelete = view.findViewById(R.id.btndelete);

                btndelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Warning").setMessage("Are you sure delete this product?")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(food.getUrlimage());
                                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                System.out.println("Delete Image Food success");
                                                DatabaseReference reference = firebaseDatabase.getReference("Product").child(key)
                                                        .child("Food");

                                                reference.child(key_food).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        System.out.println("Delete food success");
                                                        alertDialog.dismiss();
                                                        startActivity(new Intent(getActivity(), ProductActivity.class));
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        System.out.println("Delete food failed, " + e.getMessage());
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("Delete Image Food failed, " + e.getMessage());
                                            }
                                        });
                                        foodList.clear();
                                        Load();
                                        dialogInterface.dismiss();
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
                        Intent intent = new Intent(getActivity(), EditFoodActivity.class);
                        intent.putExtra("Food", food);
                        startActivity(intent);
                    }
                });
                return false;
            }
        });*/
        btnaddfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getActivity(), AddFoodActivity.class));
                PopupMenu popupMenu = new PopupMenu(getContext(), btnaddfood);
                popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());
                popupMenu.show();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddFoodActivity.class));
            }
        });

        return view;
    }

    public void Load() {
        progressBar.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.INVISIBLE);

        foodList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Food food = snapshot.getValue(Food.class);
                    foodList.add(food);
                }
                adapter = new FoodAdapter(getContext(), foodList);
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

    public String isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return "Online";
        } else {
            return "Offline";
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }
}
