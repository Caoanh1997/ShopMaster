package com.example.caoan.shopmaster;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;
    private boolean pause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        imageView = findViewById(R.id.imagewelcome);
        progressBar = findViewById(R.id.progressbar);

        imageView.setImageResource(R.drawable.welcome);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();

        imageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.setAdjustViewBounds(false);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setClickable(false);
        new Process().execute();
    }

    class Process extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.VISIBLE);
            new ProcessLoad().execute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i=0;i<3;i++){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    class ProcessLoad extends AsyncTask<Void, Integer, String>{
        @Override
        protected String doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for( int i =0;i<=100;i++){
                publishProgress(i);//gởi lại i cho main thread (UI thread)
                if(pause){
                    cancel(true);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;// gởi "Loading completed" khi đã hoàn thành
        }

        @Override
        protected void onProgressUpdate(final Integer... values) {
            progressBar.setProgress(values[0]);// update UI (progressbar) trong main thread
        }

        @Override
        protected void onPostExecute(String s) {
            startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        pause = true;
    }
}
