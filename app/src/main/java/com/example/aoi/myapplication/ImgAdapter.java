package com.example.aoi.myapplication;

/**
 * Created by JT 12/3/16.
 */

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.widget.ImageView;
import android.util.AttributeSet;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

final class ImgAdapter extends BaseAdapter{
    private final Context context;
    private  List<String> urls = new ArrayList<String>();

    public ImgAdapter(Context context) {
        this.context = context;

        // Ensure we get a different ordering of images on each run.
        //Collections.addAll(urls, Data.URLS);
        //Collections.shuffle(urls);

        urls.addAll(MainActivity.getList());
        Log.e("whahtttttt ", urls + " . ");
        //urls.add("https://tse2.mm.bing.net/th?id=OIP.M8a189b48274466a7007f00f64ba9a692o0&pid=Api");
        //urls.add("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg");
    }
    public class SquaredImageView extends ImageView {

        public SquaredImageView(Context context) {
            super(context);
        }

        public SquaredImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        }
    }
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = getItem(position);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(url) //
                .placeholder(R.drawable.placeholder) //
                //.error(R.drawable.error) //
                .fit() //
                //.centerCrop()
                .centerInside()
                .tag(context) //
                .into(view);
        view.invalidate();
        return view;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public String getItem(int position) {
        return urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
}
