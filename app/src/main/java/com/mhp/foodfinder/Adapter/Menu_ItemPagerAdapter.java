package com.mhp.foodfinder.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.mhp.foodfinder.R;

/**
 * Created by Ady on 12/11/2018.
 */

public class Menu_ItemPagerAdapter extends android.support.v4.view.PagerAdapter {

    private Context mCtx;
    private String[] mItems;

    PhotoView photoView;

    public Menu_ItemPagerAdapter(Context mCtx, String[] items) {
        this.mCtx = mCtx;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        photoView = new PhotoView(mCtx);

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(mCtx).load(Uri.parse(mItems[position])).thumbnail(Glide.with(mCtx).load(R.drawable.loading_icon)).apply(options)
                .into(photoView);

        container.addView(photoView,0);

        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //container.removeView((PhotoView) object);
    }

}
