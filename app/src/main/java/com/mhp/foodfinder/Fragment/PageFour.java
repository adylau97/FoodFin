package com.mhp.foodfinder.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.mhp.foodfinder.Activity.ListFoodPlaces;
import com.mhp.foodfinder.Activity.MainActivity;
import com.mhp.foodfinder.Activity.Snitch;
import com.mhp.foodfinder.Activity.UserSettings;
import com.mhp.foodfinder.R;

public class PageFour extends Fragment {

    private LinearLayout food_places,usersettings,logout;
    FirebaseAuth mAuth;

    public PageFour() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page_four, container, false);

        mAuth=FirebaseAuth.getInstance();


        food_places = view.findViewById(R.id.foodplaces);
        food_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ListFoodPlaces.class);
                startActivity(intent);
            }
        });

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        usersettings = view.findViewById(R.id.usersettings);
        usersettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),UserSettings.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
