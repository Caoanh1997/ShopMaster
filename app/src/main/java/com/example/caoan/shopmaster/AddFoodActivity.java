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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.caoan.shopmaster.Model.Food;
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

public class AddFoodActivity extends AppCompatActivity {

    private Button btnchooseImage, btnsave, btncancel;
    private EditText etnamefood, etdescription, etprice;
    private ImageView imagefood;
    private static int RESULT_CODE=71;
    private Uri filePath;
    private ProgressDialog progressUploadImage, progressUploadFood;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String namefile;
    private String urlImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        btnchooseImage = findViewById(R.id.btnchooseimage);
        btnsave = findViewById(R.id.btnsave);
        imagefood = findViewById(R.id.imagefood);
        etnamefood = findViewById(R.id.etnamefood);
        etdescription = findViewById(R.id.etdescription);
        etprice = findViewById(R.id.etprice);
        progressUploadImage = new ProgressDialog(this);
        progressUploadImage.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressUploadImage.setMax(100);
        progressUploadImage.setProgress(0);
        progressUploadImage.setMessage("Uploading image food");

        progressUploadFood = new ProgressDialog(this);
        progressUploadFood.setMessage("Uploading food");
        progressUploadFood.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressUploadFood.setMax(100);
        progressUploadFood.setProgress(0);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Product").child(getKey_Store()).child("Food");

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
                if(checkInput(etnamefood) && checkInput(etdescription) && checkInput(etprice)){
                    uploadImageFood();
                    progressUploadImage.show();
                    new ProgressUploadImage().execute();
                }
            }
        });
    }

    private void uploadImageFood() {
        if(filePath != null){
            namefile = UUID.randomUUID().toString();

            storageReference.child(namefile).putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload image food success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Upload image food failed");
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
                imagefood.setImageBitmap(bitmap);
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
    class ProgressUploadImage extends AsyncTask<Void, Integer, Void>{


        @Override
        protected Void doInBackground(Void... voids) {
            for (int i=0;i<100;i++){
                publishProgress(i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressUploadImage.dismiss();
            String str = getUrlImage();
            progressUploadFood.show();
            new ProgressUploadFood().execute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressUploadImage.setProgress(values[0]);
        }
    }

    class ProgressUploadFood extends AsyncTask<Void, Integer, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i=0;i<100;i++){
                publishProgress(i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            uploadFood();
            progressUploadFood.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressUploadFood.setProgress(values[0]);
        }
    }

    private void uploadFood() {
        String namefood = String.valueOf(etnamefood.getText());
        String description = String.valueOf(etdescription.getText());
        String price_str = String.valueOf(etprice.getText());
        int price = Integer.parseInt(price_str);
        String key_food = databaseReference.push().getKey();
        databaseReference.child(key_food).setValue(new Food(key_food,namefood,description,getUrlImage(),price));
    }

    public String getUrlImage() {
        storageReference.child(namefile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
}
