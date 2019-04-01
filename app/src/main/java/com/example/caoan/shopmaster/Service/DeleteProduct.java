package com.example.caoan.shopmaster.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.example.caoan.shopmaster.EventBus.DeleteProductEvent;
import com.example.caoan.shopmaster.Model.Drink;
import com.example.caoan.shopmaster.Model.Food;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class DeleteProduct extends Service {
    private List<Food> foodList;
    private List<Drink> drinkList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    public DeleteProduct() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }*/
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String key = intent.getStringExtra("key");

        foodList = new ArrayList<>();
        drinkList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //get food and delete image food
        databaseReference = firebaseDatabase.getReference("Product");
        databaseReference.child(key).child("Food").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Food food = snapshot.getValue(Food.class);
                    foodList.add(food);
                }
                if(foodList.size()>0){
                    for (Food food : foodList){
                        storageReference = firebaseStorage.getReferenceFromUrl(food.getUrlimage());
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get drink and delete image drink
        databaseReference = firebaseDatabase.getReference("Product");
        databaseReference.child(key).child("Drink").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Drink drink = snapshot.getValue(Drink.class);
                    drinkList.add(drink);
                }
                if(drinkList.size()>0)
                {
                    for(Drink drink : drinkList){
                        storageReference = firebaseStorage.getReferenceFromUrl(drink.getUrlimage());
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                System.out.println("Delete image drink success");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("Delete image drink failed: "+e.getMessage());
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //delete product infor of store
        databaseReference = firebaseDatabase.getReference("Product");
        databaseReference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Delete infor product success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Delete infor product failed: "+e.getMessage());
            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //EventBus.getDefault().unregister(this);
    }

    /*@Subscribe
    public void DeleteProduct(DeleteProductEvent productEvent){
        String key = productEvent.getKey_store();

        foodList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //get food and delete image food
        databaseReference = firebaseDatabase.getReference("Product");
        databaseReference.child(key).child("Food").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Food food = snapshot.getValue(Food.class);
                    storageReference = firebaseStorage.getReferenceFromUrl(food.getUrlimage());
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get drink and delete image drink
        databaseReference = firebaseDatabase.getReference("Product");
        databaseReference.child(key).child("Drink").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Drink drink = snapshot.getValue(Drink.class);
                    storageReference = firebaseStorage.getReferenceFromUrl(drink.getUrlimage());
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //delete product infor of store
        databaseReference = firebaseDatabase.getReference("Product");
        databaseReference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }*/
}
