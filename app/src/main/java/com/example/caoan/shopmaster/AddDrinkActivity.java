package com.example.caoan.shopmaster;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.caoan.shopmaster.Model.Drink;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class AddDrinkActivity extends AppCompatActivity {

    private Button btnchooseImage, btnsave, btncancel;
    private EditText etnamedrink, etdescription, etprice;
    private ImageView imagedrink;
    private static int RESULT_CODE=71;
    private Uri filePath;
    private ProgressDialog progressUploadImage, progressUploadDrink;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String namefile;
    private String urlImage;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink);

        btnchooseImage = findViewById(R.id.btnchooseimage);
        btnsave = findViewById(R.id.btnsave);
        btncancel = findViewById(R.id.btncancel);
        imagedrink = findViewById(R.id.imagedrink);
        etnamedrink = findViewById(R.id.etnamedrink);
        etdescription = findViewById(R.id.etdescription);
        etprice = findViewById(R.id.etprice);
        progressUploadImage = new ProgressDialog(this);
        progressUploadImage.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressUploadImage.setMax(100);
        progressUploadImage.setProgress(0);
        progressUploadImage.setMessage("Uploading image drink");

        progressUploadDrink = new ProgressDialog(this);
        progressUploadDrink.setMessage("Uploading drink");
        progressUploadDrink.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressUploadDrink.setMax(100);
        progressUploadDrink.setProgress(0);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Product").child(getKey_Store()).child("Drink");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Product");

        btnchooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInput(etnamedrink) && checkInput(etdescription) && checkInput(etprice) && checkImage(filePath)){
                    uploadImageDrink();
                }
            }
        });
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void uploadImageDrink() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Drink uploading....");
        progressDialog.show();

        if(filePath != null){
            namefile = UUID.randomUUID().toString();

            storageReference.child(getKey_Store()).child("Drink").child(namefile).putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload image drink success");

                    //get link image drink
                    storageReference.child(getKey_Store()).child("Drink").child(namefile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urlImage = uri.toString();
                            System.out.println("Get link image success");

                            //upload new drink
                            String namedrink = String.valueOf(etnamedrink.getText());
                            String description = String.valueOf(etdescription.getText());
                            String price_str = String.valueOf(etprice.getText());
                            int price = Integer.parseInt(price_str);
                            String key_drink = databaseReference.push().getKey();
                            databaseReference.child(key_drink).setValue(new Drink(key_drink,namedrink,description,getUrlImage(),price)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("Upload drink success");
                                    startActivity(new Intent(AddDrinkActivity.this,ProductActivity.class).putExtra("tab",1));
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Upload drink failed, "+e.getMessage());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Get link image failed");
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Upload image drink failed");
                }
            });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imagedrink.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean checkInput(EditText editText){
        String text = String.valueOf(editText.getText());
        if(text.isEmpty() || text == null || text.equals("")){
            editText.setError("Bạn phải điền thông tin này");
            return false;
        }else {
            return true;
        }
    }
    public String getKey_Store(){
        String key;
        SharedPreferences sharedPreferences = getSharedPreferences("key_store", Context.MODE_PRIVATE);
        key = sharedPreferences.getString("key","");

        return key;
    }

    public String getUrlImage() {
        storageReference.child(getKey_Store()).child("Drink").child(namefile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                urlImage = uri.toString();
                System.out.println("Get link image success");
                System.out.println(urlImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Get link image failed");
            }
        });
        return urlImage;
    }
    public boolean checkImage(Uri filePath){
        if (filePath == null || filePath.toString().equals("")){
            Snackbar.make(imagedrink,"Bạn phải chọn ảnh",Snackbar.LENGTH_LONG).setAction("Action",null).show();
            return false;
        }else {
            return true;
        }
    }

}
