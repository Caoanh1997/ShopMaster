package com.example.caoan.shopmaster;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private EditText etemail, etpassword, etname, etaddress, etphone;
    private TextView tvuserid, tvsignup, tvsignin;
    private FirebaseAuth firebaseAuth;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btsignin = findViewById(R.id.btsignin);
        btsignup = findViewById(R.id.btsignup);
        etemail = findViewById(R.id.etemail);
        etpassword = findViewById(R.id.etpassword);
        etname = findViewById(R.id.etname);
        etaddress = findViewById(R.id.etaddress);
        etphone = findViewById(R.id.etphone);
        tvuserid = findViewById(R.id.tvuserid);
        tvsignup = findViewById(R.id.tvsignup);
        tvsignin = findViewById(R.id.tvsignin);

        etname.setVisibility(View.INVISIBLE);
        etaddress.setVisibility(View.INVISIBLE);
        etphone.setVisibility(View.INVISIBLE);
        etname.setClickable(false);
        etaddress.setClickable(false);
        etphone.setClickable(false);
        btsignup.setVisibility(View.GONE);
        tvsignin.setVisibility(View.GONE);

        tvsignup.setClickable(true);
        animation = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.fade_out);

        final ObjectAnimator objectAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.fade_out_animator);
        final ObjectAnimator objectAnimator1 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.fade_out_animator);
        final ObjectAnimator objectAnimator2 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.object_animator_ex);
        final ObjectAnimator objectAnimator3 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_left_animator);
        final ObjectAnimator objectAnimator4 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_right_animator);
        final ObjectAnimator objectAnimator5 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_left_animator);
        final ObjectAnimator objectAnimator6 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_up_animator);
        final ObjectAnimator objectAnimator7 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_down_animator);
        final ObjectAnimator objectAnimator8 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_down_animator);
        final ObjectAnimator objectAnimator9 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_up_animator);
        final ObjectAnimator objectAnimator10 = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.move_up_animator);

        objectAnimator.setTarget(tvsignup);
        objectAnimator1.setTarget(btsignin);
        objectAnimator2.setTarget(tvsignup);
        objectAnimator3.setTarget(etname);
        objectAnimator4.setTarget(etaddress);
        objectAnimator5.setTarget(etphone);
        objectAnimator6.setTarget(btsignup);
        objectAnimator7.setTarget(etemail);
        objectAnimator8.setTarget(etpassword);
        objectAnimator9.setTarget(etemail);
        objectAnimator10.setTarget(etpassword);

        objectAnimator7.start();
        objectAnimator8.start();

        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(tvsignup,"Đăng ký",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                btsignin.startAnimation(animation);
                btsignin.setEnabled(false);
                btsignin.setVisibility(View.GONE);
                etname.setVisibility(View.VISIBLE);
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
            }
        });
        tvsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*etname.startAnimation(animation);
                etaddress.startAnimation(animation);
                etphone.startAnimation(animation);
                etname.setVisibility(View.INVISIBLE);
                etaddress.setVisibility(View.INVISIBLE);
                etphone.setVisibility(View.INVISIBLE);
                etname.setEnabled(false);
                etaddress.setEnabled(false);
                etphone.setEnabled(false);

                btsignin.setVisibility(View.VISIBLE);
                btsignin.setEnabled(true);

                tvsignin.setClickable(false);
                tvsignin.setVisibility(View.GONE);

                tvsignup.setClickable(true);
                tvsignup.setVisibility(View.VISIBLE);

                objectAnimator7.start();
                objectAnimator8.start();*/
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
                    if (CheckInput(etemail) && CheckInput(etpassword)) {
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            new ProcessLogin(tvuserid).execute();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Sign up failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else {
                    final Snackbar snackbar = Snackbar.make(btsignin, "Kiểm tra kêt nối Internet", Snackbar.LENGTH_SHORT);
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

                if (CheckOnline()) {
                    if (CheckInput(etemail) && CheckInput(etpassword) && CheckInput(etname) && CheckInput(etaddress) && CheckInput(etphone)) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    tvuserid.setText(user.getUid());
                                } else {
                                    Toast.makeText(getApplicationContext(), "Sign up failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    final Snackbar snackbar = Snackbar.make(btsignin, "Kiểm tra kêt nối Internet", Snackbar.LENGTH_SHORT);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check if user is signed in
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            tvuserid.setText(user.getUid() + ",verity: " + user.isEmailVerified());
            try {
                Thread.sleep(3000);
                startActivity(new Intent(LoginActivity.this, ShopActivity.class));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    class ProcessLogin extends AsyncTask<Void, String, String> {

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
            startActivity(new Intent(LoginActivity.this, ShopActivity.class));
        }

        @Override
        protected void onProgressUpdate(String... values) {
            textView.setText(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Void... voids) {
            for (int i = 1; i <= 100; i++) {
                if (i == 50) {
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

}

