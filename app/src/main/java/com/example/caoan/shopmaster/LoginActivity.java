package com.example.caoan.shopmaster;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caoan.shopmaster.Model.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tomer.fadingtextview.FadingTextView;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private Button btsignin, btsignup, btsignout;
    private EditText etemail, etpassword, etname, etaddress, etphone;
    private TextView tvuserid, tvsignup, tvsignin;
    private FirebaseAuth firebaseAuth;
    private Animation animation;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;
    private LinearLayout lospinner;
    private String[] tinh;
    private String[] huyen;
    private String[] xa;
    private Spinner spinnertinh, spinnerhuyen, spinnerxa;
    private FadingTextView fadingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btsignin = findViewById(R.id.btsignin);
        btsignup = findViewById(R.id.btsignup);
        btsignout = findViewById(R.id.btsignout);
        etemail = findViewById(R.id.etemail);
        etpassword = findViewById(R.id.etpassword);
        etname = findViewById(R.id.etname);
        etaddress = findViewById(R.id.etaddress);
        etphone = findViewById(R.id.etphone);
        tvuserid = findViewById(R.id.tvuserid);
        tvsignup = findViewById(R.id.tvsignup);
        tvsignin = findViewById(R.id.tvsignin);
        lospinner = findViewById(R.id.spinner);
        spinnertinh = findViewById(R.id.spinnertinh);
        spinnerhuyen = findViewById(R.id.spinnerhuyen);
        spinnerxa = findViewById(R.id.spinnerxa);
        fadingTextView = findViewById(R.id.tv);

        String[] arr = {"Tài khoản","Login","Hello"};
        fadingTextView.setTexts(arr);
        fadingTextView.setTimeout(500, TimeUnit.MILLISECONDS);

//        Slide slide = new Slide(); sdkmin = 21
//        slide.setDuration(1500);
//        getWindow().setExitTransition(slide);

        spinnerhuyen.setVisibility(View.INVISIBLE);
        spinnerxa.setVisibility(View.INVISIBLE);

        fillSpinner();

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Account");

        animation = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.fade_out);

        final ObjectAnimator objectAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.fade_out_animator);
        final ObjectAnimator objectAnimator1 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.fade_out_animator);
        final ObjectAnimator objectAnimator2 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.object_animator_ex);
        final ObjectAnimator objectAnimator3 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_left_animator);
        final ObjectAnimator objectAnimator4 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_right_animator);
        final ObjectAnimator objectAnimator5 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_left_animator);
        final ObjectAnimator objectAnimator6 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_up_animator);

        final ObjectAnimator objectAnimator9 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_up_animator);
        final ObjectAnimator objectAnimator10 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_up_animator);
        final ObjectAnimator objectAnimator11 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_up_animator);

        objectAnimator.setTarget(tvsignup);
        objectAnimator1.setTarget(btsignin);
        objectAnimator2.setTarget(tvsignup);
        objectAnimator3.setTarget(etname);
        objectAnimator11.setTarget(lospinner);
        objectAnimator4.setTarget(etaddress);
        objectAnimator5.setTarget(etphone);
        objectAnimator6.setTarget(btsignup);

        objectAnimator9.setTarget(etemail);
        objectAnimator10.setTarget(etpassword);

        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(tvsignup,"Đăng ký",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                btsignin.startAnimation(animation);
                btsignin.setEnabled(false);
                btsignin.setVisibility(View.GONE);
                etname.setVisibility(View.VISIBLE);
                lospinner.setVisibility(View.VISIBLE);
                etaddress.setVisibility(View.VISIBLE);
                etphone.setVisibility(View.VISIBLE);
                btsignup.setVisibility(View.VISIBLE);
                tvsignin.setVisibility(View.VISIBLE);
                tvsignin.setClickable(true);
                tvsignup.setVisibility(View.GONE);

                //etpassword.startAnimation(animation);
                objectAnimator.start();
                objectAnimator1.start();
                objectAnimator2.start();
                objectAnimator3.start();
                objectAnimator4.start();
                objectAnimator5.start();
                objectAnimator6.start();
                objectAnimator9.start();
                objectAnimator10.start();
                objectAnimator11.start();
            }
        });
        tvsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();

        btsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(etemail.getText());
                String password = String.valueOf(etpassword.getText());
                if (CheckOnline()) {
                    progressDialog.setMessage("Đang đăng nhập...");
                    if (CheckInput(etemail) && CheckInput(etpassword)) {
                        progressDialog.show();
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            updateUI(user);
                                            SaveAccountToSharedPreferences(user);
                                            progressDialog.dismiss();
                                            tvuserid.setText(user.getUid());
                                            startActivity(new Intent(LoginActivity.this,ShopActivity.class));
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Kiểm tra kết nối Internet",Toast.LENGTH_SHORT).show();

                }

            }
        });
        btsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(etemail.getText());
                String password = String.valueOf(etpassword.getText());

                if (CheckOnline()) {
                    progressDialog.setMessage("Đang đăng ký...");
                    if (CheckInput(etemail) && CheckInput(etpassword) && CheckInput(etname) && CheckInput(etaddress) && CheckInput(etphone) && checkSpinner()) {
                        progressDialog.show();
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String userID = user.getUid();
                                    String name = String.valueOf(etname.getText());
                                    String email = String.valueOf(etemail.getText());
                                    String address = String.valueOf(etaddress.getText());
                                    String tinh = String.valueOf(spinnertinh.getTag());
                                    String huyen = String.valueOf(spinnerhuyen.getTag());
                                    String xa = String.valueOf(spinnerxa.getTag());
                                    String phone = String.valueOf(etphone.getText());

                                    Account account = new Account(userID,name,email,address,tinh,huyen,xa,phone);
                                    databaseReference.child(user.getUid()).setValue(account);
                                    updateUI(user);
                                    SaveAccountToSharedPreferences(user);
                                    progressDialog.dismiss();
                                    startActivity(new Intent(LoginActivity.this,ShopActivity.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Sign up failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {

                }
            }
        });
        btsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                SharedPreferences sharedPreferences = getSharedPreferences("Account",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("userID");
                editor.commit();
                finish();
                startActivity(getIntent());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check if user is signed in
        FirebaseUser user = firebaseAuth.getCurrentUser();
        updateUI(user);
        if(user != null){
            startActivity(new Intent(this,ShopActivity.class));
        }
    }

    private void updateUI(FirebaseUser user) {
        if(user == null){
            final ObjectAnimator objectAnimator7 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_down_animator);
            final ObjectAnimator objectAnimator8 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_down_animator);

            objectAnimator7.setTarget(etemail);
            objectAnimator8.setTarget(etpassword);

            objectAnimator7.start();
            objectAnimator8.start();

            etname.setVisibility(View.INVISIBLE);
            lospinner.setVisibility(View.INVISIBLE);
            etaddress.setVisibility(View.INVISIBLE);
            etphone.setVisibility(View.INVISIBLE);
            etname.setClickable(false);
            lospinner.setClickable(false);
            etaddress.setClickable(false);
            etphone.setClickable(false);
            btsignup.setVisibility(View.GONE);
            btsignout.setVisibility(View.GONE);
            tvsignin.setVisibility(View.GONE);

            tvsignup.setClickable(true);
        }else {
            etemail.setVisibility(View.GONE);
            etpassword.setVisibility(View.GONE);
            etname.setVisibility(View.GONE);
            lospinner.setVisibility(View.GONE);
            etaddress.setVisibility(View.GONE);
            etphone.setVisibility(View.GONE);
            btsignin.setVisibility(View.GONE);
            btsignup.setVisibility(View.GONE);
            tvsignin.setVisibility(View.GONE);
            tvsignup.setVisibility(View.GONE);
            btsignup.setEnabled(false);
            btsignin.setEnabled(false);
            tvsignup.setClickable(false);
            tvsignin.setClickable(false);
            etemail.setEnabled(false);
            etpassword.setEnabled(false);
            etname.setClickable(false);
            lospinner.setClickable(false);
            etaddress.setClickable(false);
            etphone.setClickable(false);

            tvuserid.setVisibility(View.VISIBLE);
            tvuserid.setText(user.getUid());
            btsignout.setVisibility(View.VISIBLE);
        }
    }

    public boolean CheckOnline() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean CheckInput(EditText editText) {
        String text = String.valueOf(editText.getText());
        if (text.isEmpty() || text == null || text.equals("")) {
            editText.setError("Bạn phải điền thông tin này");
            return false;
        } else {
            return true;
        }
    }

    public void SaveAccountToSharedPreferences(FirebaseUser user){
        SharedPreferences sharedPreferences = getSharedPreferences("Account",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userID",user.getUid());
        editor.commit();
    }
    public void fillSpinner(){
        tinh  = getResources().getStringArray(R.array.tinh);
        final ArrayAdapter adapter = new ArrayAdapter(LoginActivity.this,android.R.layout.simple_spinner_item,tinh);
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
                    spinnerhuyen.setAdapter(new ArrayAdapter(LoginActivity.this,android.R.layout.simple_spinner_item,huyen));
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
                    spinnerxa.setAdapter(new ArrayAdapter(LoginActivity.this,android.R.layout.simple_spinner_item,xa));
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
}

