package com.example.caoan.shopmaster;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.caoan.shopmaster.Model.Store;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class AddShopActivity extends AppCompatActivity {

    private ImageView imagestore;
    private Button btnchooseimage, btnsave, btncancel;
    private EditText etnamestore, etphone, etaddress;
    private Spinner spinnertinh, spinnerhuyen, spinnerxa;
    private String[] tinh;
    private String[] huyen;
    private String[] xa;
    private static int PICK_IMAGE_REQUEST=71;
    private Uri filePath;
    private String namefileimage;
    private String url;
    private String urlImage;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Store store;
    private ProgressDialog progressDialog,progress;
    private int i=0;

    private String key_store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);

        imagestore = findViewById(R.id.imagestore);
        btnchooseimage = findViewById(R.id.btnchooseimage);
        btnsave = findViewById(R.id.btnsave);
        btncancel = findViewById(R.id.btncancel);
        etnamestore = findViewById(R.id.etnamestore);
        etphone = findViewById(R.id.etphone);
        etaddress = findViewById(R.id.etaddress);
        spinnertinh = findViewById(R.id.spinnertinh);
        spinnerhuyen = findViewById(R.id.spinnerhuyen);
        spinnerxa = findViewById(R.id.spinnerxa);

        spinnerhuyen.setVisibility(View.INVISIBLE);
        spinnerxa.setVisibility(View.INVISIBLE);
        tinh  = getResources().getStringArray(R.array.tinh);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Store");
        key_store = databaseReference.push().getKey();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Store");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser =firebaseAuth.getCurrentUser();

        final ArrayAdapter adapter = new ArrayAdapter(AddShopActivity.this,android.R.layout.simple_spinner_item,tinh);
        spinnertinh.setAdapter(adapter);
        spinnertinh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tinh = (String) adapterView.getItemAtPosition(i);
                if(!tinh.equals("Tỉnh/thành phố")){
                    spinnerhuyen.setVisibility(View.VISIBLE);
                    if(tinh.equals("Đà Nẵng")){
                        spinnertinh.setTag(tinh);
                        huyen = getResources().getStringArray(R.array.huyenDN);
                    }else {
                        spinnertinh.setTag("Quảng Nam");
                        huyen = getResources().getStringArray(R.array.huyenQN);
                    }
                    spinnerhuyen.setAdapter(new ArrayAdapter(AddShopActivity.this,android.R.layout.simple_spinner_item,huyen));
                }else {
                    spinnerhuyen.setVisibility(View.INVISIBLE);
                    spinnerxa.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerhuyen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String huyen = (String) adapterView.getItemAtPosition(i);
                if(!huyen.equals("Quận/huyện")){
                    spinnerxa.setVisibility(View.VISIBLE);
                    if (huyen.equals("Thanh Khê")) {
                        xa = getResources().getStringArray(R.array.xaDN1);
                    }
                    if (huyen.equals("Liên Chiểu")) {
                        xa = getResources().getStringArray(R.array.xaDN3);
                    }
                    if (huyen.equals("Hải Châu")) {
                        xa = getResources().getStringArray(R.array.xaDN2);
                    }
                    if (huyen.equals("Điện Bàn")) {
                        xa = getResources().getStringArray(R.array.xaQN1);
                    }
                    if (huyen.equals("Đại Lộc")) {
                        xa = getResources().getStringArray(R.array.xaQN2);
                    }
                    if (huyen.equals("Duy Xuyên")) {
                        xa = getResources().getStringArray(R.array.xaQN3);
                    }
                    spinnerhuyen.setTag(huyen);
                    spinnerxa.setAdapter(new ArrayAdapter(AddShopActivity.this,android.R.layout.simple_spinner_item,xa));
                }else {
                    spinnerxa.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerxa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String xa = String.valueOf(adapterView.getItemAtPosition(i));
                spinnerxa.setTag(xa);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnchooseimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInput(etnamestore) && checkInput(etphone) && checkImage(filePath) && checkSpinner()){
                    uploadImageStore();
                    progressDialog = new ProgressDialog(AddShopActivity.this);
                    progressDialog.setMessage("Image is Uploading....");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setMax(100);
                    progressDialog.setProgress(0);
                    progressDialog.show();
                    new ProgressProcess().execute();
                }else {
                    return;
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

    public boolean checkSpinner(){
        if(spinnertinh.getSelectedItem().toString().equals("Tỉnh/thành phố") || spinnerhuyen.getSelectedItem().toString().equals("Quận/huyện")
                || spinnerxa.getSelectedItem().toString().equals("Xã/phường")){
            Snackbar.make(spinnerxa,"Điền đầy đủ thông tin địa chỉ",Snackbar.LENGTH_LONG).setAction("Action",null)
                    .show();
            return false;
        }else {
            return true;
        }
    }

    public String getlink(){
        return urlImage;
    }

    private void UploadStore(String urlImage) {
        //String key_store = databaseReference.push().getKey();
        String name = String.valueOf(etnamestore.getText());
        String duong = String.valueOf(etaddress.getText());
        String xa = String.valueOf(spinnerxa.getTag());
        String huyen = String.valueOf(spinnerhuyen.getTag());
        String tinh = String.valueOf(spinnertinh.getTag());
        String userkey = firebaseUser.getUid();
        String phone = String.valueOf(etphone.getText());

        store = new Store(key_store,name,duong,xa,huyen,tinh,userkey,urlImage,phone);
        System.out.println(store.toString());
        databaseReference.child(key_store).setValue(store);
    }

    private String getUrlImage() {
        storageReference.child(key_store).child(namefileimage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url = uri.toString();
                System.out.println(url);
            }
        });
        return url;
    }

    private void uploadImageStore() {
        if(filePath != null){
            namefileimage = UUID.randomUUID().toString();
            storageReference.child(key_store).child(namefileimage).putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Uploaded Image success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Uploaded Image failed, "+e.getMessage());
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imagestore.setImageBitmap(bitmap);
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
    public boolean checkImage(Uri filePath){
        if (filePath == null || filePath.toString().equals("")){
            Snackbar.make(imagestore,"Bạn phải chọn ảnh",Snackbar.LENGTH_LONG).setAction("Action",null).show();
            return false;
        }else {
            return true;
        }
    }

    class ProgressProcess extends AsyncTask<Void,Integer,String>{

        @Override
        protected String doInBackground(Void... voids) {
            for (int i=1;i<=100;i++){
                publishProgress(i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
            urlImage = getUrlImage();
            progressDialog.dismiss();

            progress = new ProgressDialog(AddShopActivity.this);
            progress.setMax(100);
            progress.setProgress(0);
            progress.setMessage("Store Uploading...");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.show();
            Runnable runnable = new ProcessUploadStore();
            new Thread(runnable).start();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }
    }
    class ProcessUploadStore implements Runnable{

        @Override
        public void run() {
            while (i<100){
                i++;
                progress.setProgress(i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            UploadStore(getUrlImage());
            startActivity(new Intent(AddShopActivity.this, ShopActivity.class));
            progress.dismiss();
        }
    }


}
