package com.example.caoan.shopmaster;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

public class EditFoodActivity extends AppCompatActivity {

    private Button btnchooseImage, btnsave, btncancel;
    private EditText etnamefood, etdescription, etprice;
    private ImageView imagefood;
    private static int RESULT_CODE=71;
    private Uri filePath;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String namefile;
    private String urlImage;
    private Food food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        btnchooseImage = findViewById(R.id.btnchooseimage);
        btnsave = findViewById(R.id.btnsave);
        btncancel = findViewById(R.id.btncancel);
        imagefood = findViewById(R.id.imagefood);
        etnamefood = findViewById(R.id.etnamefood);
        etdescription = findViewById(R.id.etdescription);
        etprice = findViewById(R.id.etprice);

        btnsave.setEnabled(true);
        btncancel.setEnabled(true);

        Intent intent = getIntent();
        food = (Food) intent.getSerializableExtra("Food");
        etnamefood.setText(food.getName());
        etdescription.setText(food.getDescription());
        etprice.setText(String.valueOf(food.getPrice()));
        Picasso.get().load(food.getUrlimage()).into(imagefood);

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
                    if(filePath != null && filePath.toString() != food.getUrlimage()){
                        System.out.println("image mới");
                        deleteOldImageFood();
                    }else {
                        System.out.println("image cũ");
                        uploadFood(food.getUrlimage());
                    }
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

    private void deleteOldImageFood() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Food Updating....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();

        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(food.getUrlimage());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Delete old image success");

                //upload new image food
                namefile = UUID.randomUUID().toString();
                firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = firebaseStorage.getReference("Product");
                if(filePath != null){
                    storageReference.child(getKey_Store()).child("Food").child(namefile).putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Upload image success");

                            //Get link new image food
                            firebaseStorage = FirebaseStorage.getInstance();
                            StorageReference storageReference = firebaseStorage.getReference("Product");
                            storageReference.child(getKey_Store()).child("Food").child(namefile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlImage = uri.toString();
                                    System.out.println("Get link success");

                                    //Update food
                                    firebaseDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = firebaseDatabase.getReference("Product").child(getKey_Store()).child("Food");
                                    String name = String.valueOf(etnamefood.getText());
                                    String description = String.valueOf(etdescription.getText());
                                    int price = Integer.parseInt(String.valueOf(etprice.getText()));
                                    String key = food.getKey();
                                    Food food = new Food(key,name,description,urlImage,price);
                                    databaseReference.child(key).setValue(food).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            System.out.println("Upload food success");
                                            progressDialog.dismiss();
                                            finish();
                                            startActivity(new Intent(EditFoodActivity.this,ProductActivity.class ));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            System.out.println("Upload image failed, "+e.getMessage());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Get link failed, "+e.getMessage());
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Upload image failed, "+e.getMessage());
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Delete old image failed, "+e.getMessage());
            }
        });
    }

    class ProcessDeleteOldImage extends AsyncTask<Void, Integer, Void>{
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EditFoodActivity.this);
            progressDialog.setMessage("Image Deleting...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            uploadImage();
            new ProcessUploadImage().execute();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for(int i=1;i<=100;i++){
                publishProgress(i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private void uploadImage() {
        namefile = UUID.randomUUID().toString();
        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("Product");
        if(filePath != null){
            storageReference.child(getKey_Store()).child("Food").child(namefile).putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload image success");
                }
            });
        }
    }

    private String getKey_Store(){
        String key;
        SharedPreferences sharedPreferences = getSharedPreferences("key_store", Context.MODE_PRIVATE);
        key = sharedPreferences.getString("key","");
        return key;
    }

    class ProcessUploadImage extends AsyncTask<Void, Integer, Void>{
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EditFoodActivity.this);
            progressDialog.setMessage("Image Uploading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            String url = getUrlImage();
            new ProcessGetLink().execute();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for(int i=1;i<=100;i++){
                publishProgress(i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    class ProcessGetLink extends AsyncTask<Void ,Integer, Void>{
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EditFoodActivity.this);
            progressDialog.setMessage("Link getting...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            System.out.println(getUrlImage());
            uploadFood(getUrlImage());
            new ProcessUploadFood().execute();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for(int i=1;i<=100;i++){
                publishProgress(i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private void uploadFood(String urlImage) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Food Updating....");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Product").child(getKey_Store()).child("Food");
        String name = String.valueOf(etnamefood.getText());
        String description = String.valueOf(etdescription.getText());
        int price = Integer.parseInt(String.valueOf(etprice.getText()));
        String key = food.getKey();
        Food food = new Food(key,name,description,urlImage,price);
        databaseReference.child(key).setValue(food).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Upload food success");
                progressDialog.dismiss();
                startActivity(new Intent(EditFoodActivity.this,ProductActivity.class ));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Upload food failed, "+e.getMessage());
            }
        });
    }
    class ProcessUploadFood extends AsyncTask<Void, Integer, Void>{
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EditFoodActivity.this);
            progressDialog.setMessage("Food Uploading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            startActivity(new Intent(EditFoodActivity.this, ProductActivity.class));
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for(int i=1;i<=100;i++){
                publishProgress(i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private String getUrlImage() {
        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("Product");
        storageReference.child(getKey_Store()).child("Food").child(namefile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                urlImage = uri.toString();
                System.out.println("Get link success");
            }
        });
        return urlImage;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Chọn ảnh"),RESULT_CODE);
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

    class ProcessImage extends AsyncTask<String, Integer,Bitmap>{

        private ImageView imageView;
        private ProgressDialog progressDialog;

        public ProcessImage(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EditFoodActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressDialog.dismiss();
            imageView.setImageBitmap(bitmap);
            btnsave.setEnabled(true);
            btncancel.setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap=null;
            String urlImage = strings[0];
            try {
                InputStream inputStream = new URL(urlImage).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}
