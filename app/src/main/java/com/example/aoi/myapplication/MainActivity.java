package com.example.aoi.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import java.io.FileInputStream;
import android.support.v7.app.AppCompatActivity;
import java.io.ObjectInputStream;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import java.io.File;
import android.app.Activity;
import android.widget.AbsListView;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import java.util.Collections;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Map;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.lang.Comparable;
import java.util.Comparator;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

/**
 * Created by JT on 12/3/16.
 */

public class MainActivity extends AppCompatActivity {
    private String defaultLink = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q=bing&Size=Wallpaper";
    private String result,tUrl,cUrl,rUrl;
    public static List<String> urls1 = new ArrayList<String>();
    public static String full="";
    private Context c;
    GridView gridView;
    TextView text;
    FloatingActionButton fab;
    static HashMap<String, Integer> hm;
    HashMap<String, Integer> map;
    HashMap<String, Integer> userPref;
    static String key;
    public int liked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("CHECK THIS OUTTTTTT" + FullSizeImageActivity.liked);
        //get saved user preferences
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Your_Shared_Prefs", Context.MODE_PRIVATE);
        map = (HashMap<String, Integer>) pref.getAll();
        for (String s : map.keySet()) {
            Integer value=map.get(s);
        }

        hm = map;

        SharedPreferences ss = getSharedPreferences("your_prefs", FullSizeImageActivity.MODE_PRIVATE);
        liked = ss.getInt("liked", 0);

        /*
        Iterator iterator = hm.keySet().iterator();
        while (iterator.hasNext()) {
            key = iterator.next().toString();
            Integer value = hm.get(key);

            System.out.println(key + " " + value);
        }
        */
        text = (TextView) findViewById(R.id.editText);
        gridView = (GridView) findViewById(R.id.grid_view);

        gridView.setOnScrollListener(new scrollListener());

        text.setOnKeyListener(new myKeyListener());

        //expand the image when touched
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(MainActivity.this, "" + urls1.get(position), Toast.LENGTH_SHORT).show();
                full = urls1.get(position);
                Intent i = new Intent(MainActivity.this, FullSizeImageActivity.class);
                startActivity(i);
            }
        });
        //start screen
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //check if user liked any wallpaper, if liked load wallpapers tailored for user
            if (liked>=1) {
                System.out.println("CHECK THIS OUTTTTTT" + FullSizeImageActivity.liked);
                new getUserPrefJson().execute();
            }
            else
                new getBingJson().execute(defaultLink);
        } else {
            Toast.makeText(getApplicationContext(), "No network connection available", Toast.LENGTH_SHORT);
        }
        c=this;
    }

    //hide text box during scroll
    public class scrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
        int visibleItemCount, int totalItemCount) {

        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // TODO Auto-generated method stub
            switch(scrollState) {
                case 2: // SCROLL_STATE_FLING
                    //hide button here
                    text.setText("");
                    break;

                case 1: // SCROLL_STATE_TOUCH_SCROLL
                    //hide button here
                    //text.setVisibility(View.GONE);
                    text.setText("");
                    break;

                case 0: // SCROLL_STATE_IDLE
                    //show button here
                    //text.setVisibility(View.INVISIBLE);
                    text.setText("");
                    break;

                default:
                    //show button here
                    fab.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    //handle search request
    public class myKeyListener implements OnKeyListener {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                String input = text.getText().toString().replace(" ", "%20");
                Toast.makeText(getApplicationContext(), input, Toast.LENGTH_SHORT).show();
                rUrl = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q="+input+"&Size=Wallpaper";
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new getBingJson().execute(rUrl);
                } else {
                    Toast.makeText(getApplicationContext(), "No network connection available", Toast.LENGTH_SHORT);
                }
                return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //download and store bing search json and store image url in the array
    private class getBingJson extends AsyncTask<String,Void,String> {
        protected void onPreExecute(){
            urls1.clear();
        }
        protected String doInBackground(String...url) {
            try {
            JSONObject jsonObject = null;
            MyUtils.down.downloadJson(url[0]);
            result = MyUtils.down.result;
            Log.e(". ", result + " . ");

                if (JSONValue.isValidJson(result)) {
                    try {
                        //jsonObject = (JSONObject) JSONValue.parse(result);
                        jsonObject = new JSONObject(result);
                        Log.e("Result. ", jsonObject.toString() + " . ");
                    }
                    catch (Exception e) {
                    }
                }
                JSONArray JAPhotos = jsonObject.getJSONArray("value");
                for (int i = 0; i < 100; i++) {
                    //jsonObject = (JSONObject) ((JSONArray) jsonObject.get("value")).get(i);
                    JSONObject JOPhotos = JAPhotos.getJSONObject(i);
                    Log.e("1. ", JOPhotos.toString() + " . ");

                    tUrl = (String) JOPhotos.get("thumbnailUrl");
                    Log.e("turl ", tUrl + " . ");
                    cUrl = (String) JOPhotos.get("contentUrl");
                    Log.e(". ", cUrl + " . ");
                    urls1.add(cUrl);
                    Collections.shuffle(urls1);
                }
            }
            catch (Exception e) {
            }
            return null;
        }
        protected void onPostExecute(String result){
            gridView.setAdapter(new ImgAdapter(c));
        }
    }

    private class getUserPrefJson extends AsyncTask<Void,Void,String> {
        //ProgressDialog pd;
        protected void onPreExecute(){
            //pd = ProgressDialog.show(MainActivity.this,"","Please Wait", false);
            urls1.clear();
        }
        protected String doInBackground(Void...url) {
            try {
                Iterator iterator = hm.keySet().iterator();
                while (iterator.hasNext()) {
                    key = iterator.next().toString();
                    Integer value = hm.get(key);
                    String userLiked = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q=" + key + "&Size=Wallpaper";
                    System.out.println(key + " " + value);

                    JSONObject jsonObject = null;
                    MyUtils.down.downloadJson(userLiked);
                    result = MyUtils.down.result;
                    Log.e(". ", result + " . ");

                    if (JSONValue.isValidJson(result)) {
                        try {
                            //jsonObject = (JSONObject) JSONValue.parse(result);
                            jsonObject = new JSONObject(result);
                            Log.e("Result. ", jsonObject.toString() + " . ");
                        } catch (Exception e) {
                        }
                    }
                    JSONArray JAPhotos = jsonObject.getJSONArray("value");
                    for (int i = 0; i <= hm.get(key); i++) {
                        //jsonObject = (JSONObject) ((JSONArray) jsonObject.get("value")).get(i);
                        JSONObject JOPhotos = JAPhotos.getJSONObject(i);
                        Log.e("1. ", JOPhotos.toString() + " . ");

                        tUrl = (String) JOPhotos.get("thumbnailUrl");
                        Log.e("turl ", tUrl + " . ");
                        cUrl = (String) JOPhotos.get("contentUrl");
                        Log.e(". ", cUrl + " . ");
                        urls1.add(cUrl);
                    }
                }
            }
            catch (Exception e) {
            }
            return null;
        }
        protected void onPostExecute(String result){
            //pd.dismiss();
            Collections.shuffle(urls1);
            gridView.setAdapter(new ImgAdapter(c));
        }
    }

    public static List<String> getList() {
        return urls1;
    }

    public void onPause(){
        super.onPause();
        entriesSortedByValues(hm);
        //save the user preferences
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Your_Shared_Prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        for (String s : hm.keySet()) {
            editor.putInt(s, hm.get(s));
        }
        editor.commit();
    }

    public Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet()); Collections.sort(list, new Comparator() {
            public int compare(Object o2, Object o1) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()) .compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next(); result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    static <K,V extends Comparable<? super V>>
    List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

        List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Entry<K,V>>() {
                    @Override
                    public int compare(Entry<K,V> e1, Entry<K,V> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );

        return sortedEntries;
    }

}
