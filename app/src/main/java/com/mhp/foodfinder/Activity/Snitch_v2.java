package com.mhp.foodfinder.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.mhp.foodfinder.Fragment.PageFour;
import com.mhp.foodfinder.Fragment.PageOne;
import com.mhp.foodfinder.Fragment.PageThree;
import com.mhp.foodfinder.Fragment.PageTwo;
import com.mhp.foodfinder.Model.Nearby;
import com.mhp.foodfinder.R;

import java.util.List;

public class Snitch_v2 extends AppCompatActivity implements PageTwo.PageTwoListener, PageOne.PageOneListener {


    final PageOne fragment1 = new PageOne();
    final PageTwo fragment2 = new PageTwo();
    final PageThree fragment3 = new PageThree();
    final PageFour fragment4 = new PageFour();
    Fragment active = fragment1;
    final FragmentManager fm = getSupportFragmentManager();
    BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snitch_v2);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_home);

        fm.beginTransaction().add(R.id.content, fragment4, "FragmentFour").commit();
        fm.beginTransaction().add(R.id.content, fragment3, "FragmentThree").commit();
        fm.beginTransaction().add(R.id.content, fragment2, "FragmentTwo").commit();
        fm.beginTransaction().add(R.id.content,fragment1, "FragmentOne").commit();
        fm.beginTransaction().hide(fragment2).commit();
        fm.beginTransaction().hide(fragment3).commit();
        fm.beginTransaction().hide(fragment4).commit();

       //fm.beginTransaction().replace(R.id.content,fragment1).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active=fragment1;
                    //fm.beginTransaction().replace(R.id.content,fragment1).commit();
                    return true;

                case R.id.navigation_nearby:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    //fm.beginTransaction().replace(R.id.content,fragment2).commit();
                    return true;

                case R.id.navigation_recommendation:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    //fm.beginTransaction().replace(R.id.content,fragment3).commit();
                    return true;

                case R.id.navigation_settings:
                    fm.beginTransaction().hide(active).show(fragment4).commit();
                    active = fragment4;
                    //fm.beginTransaction().replace(R.id.content,fragment3).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    public void moveMap(LatLng latLng) {
        fragment1.recyclerViewClick(latLng);
        fm.beginTransaction().hide(active).show(fragment1).commit();
        active=fragment1;
        navigation.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       finishAffinity();
    }

    @Override
    public void passList(List<Nearby> nearbyList) {
        fragment2.setNearbyList(nearbyList);
    }

    @Override
    public void passCoordinate(Double lat, Double lng) {
        fragment3.setCoordinate(lat,lng);
    }

}
