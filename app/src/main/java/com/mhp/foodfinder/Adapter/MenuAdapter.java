package com.mhp.foodfinder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mhp.foodfinder.Activity.Menu_ViewPager;
import com.mhp.foodfinder.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ady on 12/11/2018.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder>{

    private Context mCtx;
    private List<String> image;

    public MenuAdapter(Context mCtx, List<String> image) {
        this.mCtx = mCtx;
        this.image = image;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.menu_list, parent,false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {

        String img = image.get(position);

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop();

        Glide.with(mCtx)
                .load(img).thumbnail(Glide.with(mCtx).load(R.drawable.loading_icon))
                .apply(options)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return image.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;

        public MenuViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.menu_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int p = getAdapterPosition();
            Intent intent = new Intent(mCtx, Menu_ViewPager.class);
            intent.putExtra("LIST", (Serializable) image);
            intent.putExtra("POSITION",String.valueOf(p));
            mCtx.startActivity(intent);
        }
    }
}
