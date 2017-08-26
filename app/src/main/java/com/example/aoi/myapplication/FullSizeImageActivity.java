package com.example.aoi.myapplication;

import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import java.io.IOException;
import android.view.MenuItem;

import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.View;
import com.squareup.picasso.Picasso.LoadedFrom;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.graphics.Bitmap;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by JT on 12/9/16.
 */

public class FullSizeImageActivity extends AppCompatActivity implements View.OnClickListener{
    private String link;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    static int liked;
    static SharedPreferences mPreferences;
    static SharedPreferences.Editor editor;
    static int likes;
    Context c;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_size);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mPreferences = getSharedPreferences("CurrentUser",
                MODE_PRIVATE);
        editor = mPreferences.edit();

        System.out.println(liked);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        Intent i = getIntent();
        link = MainActivity.full;

        ImageView iv = (ImageView) findViewById(R.id.image);
        Picasso.with(getApplicationContext())
                .load(link)
                .fit()
                .centerCrop()
                .into(iv);
        c=this;
    }
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1:
                Toast.makeText(getApplicationContext(), "Setting Wallpaper", Toast.LENGTH_SHORT).show();
                new setWallpaperTask().execute();
                break;
            case R.id.fab2:
                Toast.makeText(getApplicationContext(), "Liked", Toast.LENGTH_SHORT).show();
                liked ++;
                editor.putInt("liked", 1);
                editor.commit();
                new getAnalysisTask().execute();
                break;
        }
    }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }
    public class setWallpaperTask extends AsyncTask <String, Void, Bitmap> {
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap result= null;
            try {
                result = Picasso.with(getApplicationContext())
                        .load(link)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                wallpaperManager.setBitmap(result);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute (Bitmap result) {
            super.onPostExecute(result);

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getBaseContext());
            try {
                wallpaperManager.setBitmap(result);
                Toast.makeText(getApplicationContext(), "Wallpaper set successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }
    public class getAnalysisTask extends AsyncTask<String, Void, String>{
        protected void onPreExecute(){
        }
        protected String doInBackground(String...urls) {
            AnalyzeImage.AnalyzeImgFromURL(link);
            return null;
        }
        protected void onPostExecute(String feed) {
        }
    }
    public void onPause(){
        super.onPause();
        if(liked>=1) {
            SharedPreferences sp = getSharedPreferences("your_prefs", FullSizeImageActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("liked", 1);
            editor.commit();
        }
    }
}
