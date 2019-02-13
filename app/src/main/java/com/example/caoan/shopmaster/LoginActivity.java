package com.example.caoan.shopmaster;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button btsignin, btsignup;
    private EditText etemail, etpassword;
    private TextView tvuserid;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btsignin = findViewById(R.id.btsignin);
        btsignup = findViewById(R.id.btsignup);
        etemail = findViewById(R.id.etemail);
        etpassword = findViewById(R.id.etpassword);
        tvuserid = findViewById(R.id.tvuserid);

        firebaseAuth = FirebaseAuth.getInstance();

        btsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(etemail.getText());
                String password = String.valueOf(etpassword.getText());
                if(CheckOnline()){
                    if(email == null || password == null || email.equals("") || password.equals("")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Thông báo")
                                .setMessage("Chưa điền đầy đủ thông tin ?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }else {
                        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
//                                FirebaseUser user = firebaseAuth.getCurrentUser();
//                                tvuserid.setText(user.getUid());
//                                try {
//                                    Thread.sleep(3000);
//                                    startActivity(new Intent(LoginActivity.this,ShopActivity.class));
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
                                    new ProcessLogin(tvuserid).execute();
                                }else {
                                    Toast.makeText(getApplicationContext(),"Sign up failed",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else {
                    final Snackbar snackbar = Snackbar.make(btsignin,"Kiểm tra kêt nối Internet",Snackbar.LENGTH_SHORT);
                    snackbar.setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }

            }
        });
        btsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(etemail.getText());
                String password = String.valueOf(etpassword.getText());

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            tvuserid.setText(user.getUid());
                        }else {
                            Toast.makeText(getApplicationContext(),"Sign up failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    class ProcessLogin extends AsyncTask<Void, String, String>{

        private ProgressDialog progressDialog;
        private TextView textView;
        private FirebaseUser user;

        public ProcessLogin(TextView textView) {
            this.textView = textView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            user = firebaseAuth.getCurrentUser();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Đang đăng nhập...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            startActivity(new Intent(LoginActivity.this,ShopActivity.class));
        }

        @Override
        protected void onProgressUpdate(String... values) {
            textView.setText(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Void... voids) {
            for(int i=1;i<=100;i++){
                if(i==50){
                    publishProgress(user.getUid());
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check if user is signed in
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            tvuserid.setText(user.getUid()+",verity: "+user.isEmailVerified());
            try {
                Thread.sleep(3000);
                startActivity(new Intent(LoginActivity.this,ShopActivity.class));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean CheckOnline(){
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }else {
            return false;
        }
    }

}

