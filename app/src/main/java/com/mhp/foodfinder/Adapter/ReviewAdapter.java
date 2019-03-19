package com.mhp.foodfinder.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mhp.foodfinder.Model.CurrentReview;
import com.mhp.foodfinder.R;

import java.util.List;

/**
 * Created by Ady on 1/10/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context mCtx;
    private List<CurrentReview> currentReviewList;

    public ReviewAdapter(Context mCtx, List<CurrentReview> currentReviewList) {
        this.mCtx = mCtx;
        this.currentReviewList = currentReviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.review_list, parent,false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        CurrentReview cr = currentReviewList.get(position);
        holder.review_text.setText(cr.text);

    }

    @Override
    public int getItemCount() {
        return currentReviewList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{

        TextView review_text;

        public ReviewViewHolder (View itemView){
            super(itemView);

            review_text = itemView.findViewById(R.id.review_text);
        }
    }

}
