package com.mhp.foodfinder.Adapter;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mhp.foodfinder.Fragment.PageOne;
import com.mhp.foodfinder.Fragment.PageTwo;
import com.mhp.foodfinder.Model.Nearby;
import com.mhp.foodfinder.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Nearbyadapter extends RecyclerView.Adapter<Nearbyadapter.NearbyViewHolder>{

    private Context mCtx;
    private List<Nearby> nearbyList;
    private PageTwo pageTwo;

    public Nearbyadapter(Context mCtx, List<Nearby> nearbyList, PageTwo pageTwo) {
        this.mCtx = mCtx;
        this.nearbyList = nearbyList;
        this.pageTwo = pageTwo;
    }

    @Override
    public NearbyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.nearbyplaces_list, parent,false);

        return new NearbyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NearbyViewHolder holder, int position) {
        Nearby n = nearbyList.get(position);
        holder.name.setText(n.name);
        holder.isOpening.setText(n.isOpening);

        if(!n.review.equals("")){
            holder.ratingBar.setRating(Float.parseFloat(n.review));
        }else{
            holder.ratingBar.setRating(0);
        }

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop();

        if(!n.photo_reference.equals("")) {
            Glide.with(mCtx)
                    .load(n.photo_reference).thumbnail(Glide.with(mCtx).load(R.drawable.loading_icon))
                    .apply(options)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return nearbyList.size();
    }

    class NearbyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name,isOpening;
        ImageView imageView;
        RatingBar ratingBar;

            public NearbyViewHolder(View itemView) {
                super(itemView);
                name=itemView.findViewById(R.id.nearbyplaces_name);
                isOpening=itemView.findViewById(R.id.isOpening);
                imageView=itemView.findViewById(R.id.imageView);
                ratingBar=itemView.findViewById(R.id.ratingBar);

                itemView.setOnClickListener(this);
            }

        @Override
        public void onClick(View view) {

            try {
                int p = getAdapterPosition();
                Nearby n = nearbyList.get(p);

                pageTwo.recyclerViewClick(n.latLng);

            }catch(Exception e){
                Log.d("ERROR", e.toString());
            }
        }
    }
}


