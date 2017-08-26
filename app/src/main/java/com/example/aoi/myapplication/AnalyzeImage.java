package com.example.aoi.myapplication;

import android.util.Log;

import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Category;
import com.microsoft.projectoxford.vision.contract.Face;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by JT on 12/10/16.
 */

public class AnalyzeImage {
    static String des="";
    private static String url ="";
    static AnalysisResult v;
    static String result;

    public static String AnalyzeImgFromURL(String desUrl) {
        Gson gson = new Gson();
        url = MainActivity.full;
        String Key = "3afee4807ffb4bcb9c0a49049def0d41";
        VisionServiceClient vsc = new VisionServiceRestClient(Key);
        String imgURL = desUrl;
        String features[] = {"ImageType","Color","Faces","Adult","Categories"};
        String[] details = {};
        //Here is the result of analyze for the image
        try {
        v = vsc.describe(imgURL,1);
        } catch (Exception e) {
            ;    // Store error
        }
        result = gson.toJson(v);
        Log.d("result", result);

        JSONObject jsonObject = null;
        JSONObject JAPhotos = null;
        JSONArray Tags = null;

        if (JSONValue.isValidJson(result)) {
            try {
                //jsonObject = (JSONObject) JSONValue.parse(result);
                jsonObject = new JSONObject(result);
                Log.e("Result. ", jsonObject.toString() + " . ");
                JAPhotos = jsonObject.getJSONObject("description");
                Tags = JAPhotos.getJSONArray("tags");
                //get the first description tag
                des = Tags.getString(0);
            }
            catch (Exception e) {
            }
        }
        Log.d("fwfafsafsaf. ", des);

        //if the description is already in the map, increase the value by 1
        //else add the new description as new key and assign the value as 1
        if (MainActivity.hm.containsKey(des)) {
            MainActivity.hm.put(des,MainActivity.hm.get(des)+1);
            System.out.println("lollol.lll"+MainActivity.hm.get(des));
        } else {
            MainActivity.hm.put(des,1);
            System.out.println("lollol.lll"+MainActivity.hm.get(des));
        }
        return null;
    }
}
