package com.mhp.foodfinder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mhp.foodfinder.Activity.PlacesUpdate;
import com.mhp.foodfinder.Model.Firebase_Restaurant;
import com.mhp.foodfinder.Model.FoodList;
import com.mhp.foodfinder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ady on 5/11/2018.
 */

public class FoodPlaceAdapter extends RecyclerView.Adapter<FoodPlaceAdapter.FoodViewHolder> {

    private Context mCtx;
    private List<FoodList> foodlist;
    private DatabaseReference mDatabase;
    List<String> res = new ArrayList<>();
    List<String> menu =new ArrayList<>();
    FirebaseStorage mStorage = FirebaseStorage.getInstance();

    public FoodPlaceAdapter(Context mCtx, List<FoodList> foodlist) {
        this.mCtx = mCtx;
        this.foodlist = foodlist;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.swipe_layout, parent, false);

        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FoodViewHolder holder, final int position) {

        final FoodList f = foodlist.get(position);

        holder.name.setText(f.name);

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop();

        if (!f.photo.equals("")) {
            Glide.with(mCtx)
                    .load(f.photo).thumbnail(Glide.with(mCtx).load(R.drawable.loading_icon))
                    .apply(options)
                    .into(holder.imageView);
        }

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        //holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.swipeLayout.findViewById(R.id.bottom_wrapper1));
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wraper));

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.swipeLayout.close(true);
                Intent intent = new Intent(mCtx, PlacesUpdate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("key", f.key);
                mCtx.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.swipeLayout.close(true);
                mDatabase = FirebaseDatabase.getInstance().getReference("restaurant2").child(f.key);
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Firebase_Restaurant restaurant = dataSnapshot.getValue(Firebase_Restaurant.class);

                        if (restaurant.getImage() != null) {
                            res = restaurant.getImage();
                            for (int i = 0; i < res.size(); i++) {
                                StorageReference imageRef = mStorage.getReferenceFromUrl(res.get(i));
                                imageRef.delete();
                            }
                        }
                        if (restaurant.getMenu() != null) {
                            menu = restaurant.getMenu();
                            for (int i = 0; i < menu.size(); i++) {
                                StorageReference imageRef = mStorage.getReferenceFromUrl(menu.get(i));
                                imageRef.delete();
                            }
                        }

                        mDatabase.removeValue();
                        Toast.makeText(mCtx, "DELETED", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });






                //foodlist.remove(position);
                //notifyItemRemoved(position);
                //notifyItemRangeChanged(position,foodlist.size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodlist.size();
    }


    class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, delete, edit;
        ImageView imageView;
        SwipeLayout swipeLayout;

        public FoodViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.foodplaces_name);
            imageView = itemView.findViewById(R.id.imageView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            delete = (TextView) itemView.findViewById(R.id.Delete);
            edit = (TextView) itemView.findViewById(R.id.Edit);

            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            FoodList f = foodlist.get(getAdapterPosition());

            Toast.makeText(mCtx, f.key, Toast.LENGTH_SHORT).show();
        }
    }
}
