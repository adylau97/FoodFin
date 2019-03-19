package com.mhp.foodfinder.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mhp.foodfinder.Model.CurrentReview_Firebase;
import com.mhp.foodfinder.R;

import java.util.List;

/**
 * Created by Ady on 7/11/2018.
 */

public class FirebaseReviewAdapter extends RecyclerView.Adapter<FirebaseReviewAdapter.FirebaseReviewViewHoler> {

    private Context mCtx;
    private List<CurrentReview_Firebase> currentReviewList;

    public FirebaseReviewAdapter(Context mCtx, List<CurrentReview_Firebase> currentReviewList) {
        this.mCtx = mCtx;
        this.currentReviewList = currentReviewList;
    }

    @Override
    public FirebaseReviewViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.review_list, parent,false);
        return new FirebaseReviewViewHoler(view);
    }

    @Override
    public void onBindViewHolder(FirebaseReviewViewHoler holder, int position) {

        CurrentReview_Firebase cr = currentReviewList.get(position);
        holder.review_text.setText(cr.review);
        holder.ratingBar.setRating(Float.parseFloat(cr.rating));
        holder.user_text.setText(cr.user);
    }

    @Override
    public int getItemCount() {
        return currentReviewList.size();
    }

    class FirebaseReviewViewHoler extends RecyclerView.ViewHolder{

        RatingBar ratingBar;
        TextView review_text,user_text;

        public FirebaseReviewViewHoler(View itemView){
            super(itemView);
            review_text = itemView.findViewById(R.id.review_text);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            user_text = itemView.findViewById(R.id.user);
        }
    }
}
