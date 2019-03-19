package com.mhp.foodfinder.Activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mhp.foodfinder.Adapter.FoodPlaceAdapter;
import com.mhp.foodfinder.Model.Firebase_Restaurant;
import com.mhp.foodfinder.Model.FoodList;
import com.mhp.foodfinder.R;

import java.util.ArrayList;
import java.util.List;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class ListFoodPlaces extends AppCompatActivity {

    private LinearLayout insert_btn;
    private RecyclerView recyclerView;
    private FoodPlaceAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private List<FoodList> foodlist = new ArrayList<>();
    private List<String> image = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_food_places);


        insert_btn = (LinearLayout) findViewById(R.id.btn_insert);
        insert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PlacesInsert.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        /*DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider));
        recyclerView.addItemDecoration(itemDecoration);*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Later edit
        mDatabase.child("restaurant2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                foodlist.clear();

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Firebase_Restaurant restaurant = data.getValue(Firebase_Restaurant.class);

                    if(restaurant.getAddByUser().equals(user.getUid())){
                        //Later edit
                        image = restaurant.getImage();
                        FoodList f = new FoodList(restaurant.getName(),image.get(0),data.getKey());
                        foodlist.add(f);
                    }
                }

                adapter = new FoodPlaceAdapter(getApplicationContext(),foodlist);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
