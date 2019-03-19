package com.mhp.foodfinder.Activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mhp.foodfinder.Adapter.ItemPagerAdapter;
import com.mhp.foodfinder.Adapter.Menu_ItemPagerAdapter;
import com.mhp.foodfinder.R;

import java.util.List;

public class Menu_ViewPager extends AppCompatActivity {

    List<String> image;
    String [] imageList;
    Menu_ItemPagerAdapter adapter;
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__view_pager);

        image = getIntent().getStringArrayListExtra("LIST");
        imageList = new String[image.size()];
        String position = getIntent().getStringExtra("POSITION");
        int pos = Integer.parseInt(position);

        for(int i=0;i<image.size();i++){
            imageList[i]=image.get(i);
        }

        adapter = new Menu_ItemPagerAdapter(this,imageList);
        viewPager = (ViewPager) findViewById(R.id.full_view);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos);

    }
}
