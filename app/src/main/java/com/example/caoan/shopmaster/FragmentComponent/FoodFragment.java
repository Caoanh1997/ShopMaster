package com.example.caoan.shopmaster.FragmentComponent;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caoan.shopmaster.Adapter.FoodAdapter;
import com.example.caoan.shopmaster.Model.Food;
import com.example.caoan.shopmaster.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {

    private View view;
    private TextView tv;
    private GridView gridView;
    private Button button;
    private List<Food> foodList;
    private FoodAdapter adapter;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    public FoodFragment() {
        // Required empty public constructor
    }


    public static FoodFragment newInstance(String str) {
        FoodFragment fragment = new FoodFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_food, container, false);
        tv = view.findViewById(R.id.tv);
        gridView = view.findViewById(R.id.gv);
        button = view.findViewById(R.id.btsize);
        progressBar = view.findViewById(R.id.progress);

        Bundle bundle = getArguments();
        String key = bundle.getString("s");
        tv.setText(key);
        gridView.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("Product").child(key)
                .child("Food");
        /*for(int i=0;i<5;i++){
            String foodID = reference.push().getKey();
            Food food = new Food("Chuoi "+i, "This is banana", "https://cdn1.woolworths.media/content/wowproductimages/medium/306510.jpg", 10000);

            reference.child(foodID).setValue(food);
        }*/
        new ProgressBarProcess().execute();
        foodList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Food food = snapshot.getValue(Food.class);
                    //System.out.println(food.getName());
                    foodList.add(food);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),isOnline()+" "+String.valueOf(foodList.size()),Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    class ProgressBarProcess extends AsyncTask<Void,Integer,String> {
        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            adapter = new FoodAdapter(getContext(),foodList);
            gridView.setAdapter(adapter);
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
                    Thread.sleep(100);
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
}
