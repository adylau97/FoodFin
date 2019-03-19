package com.mhp.foodfinder.Fragment;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.places.Place;
import com.mhp.foodfinder.Activity.PlacesInsert;
import com.mhp.foodfinder.Activity.ViewDirection;
import com.mhp.foodfinder.Adapter.FirebaseReviewAdapter;
import com.mhp.foodfinder.Adapter.ItemPagerAdapter;
import com.mhp.foodfinder.Adapter.ReviewAdapter;
import com.mhp.foodfinder.Model.CurrentReview;
import com.mhp.foodfinder.Model.CurrentReview_Firebase;
import com.mhp.foodfinder.Model.MyPlaces;
import com.mhp.foodfinder.Model.PlaceDetail;
import com.mhp.foodfinder.Model.Results;
import com.mhp.foodfinder.R;
import com.mhp.foodfinder.Remote.Common;
import com.mhp.foodfinder.Remote.IGoogleAPIService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class PageThree extends Fragment {

    private RadioGroup radioGroup;
    IGoogleAPIService mService;
    double lat,lng=0;
    String price_selection="";
    private Button ramdom_btn,create_review_btn;
    BottomSheetBehavior bottomSheetBehavior;
    //ImageView photo;
    RatingBar ratingBar;
    TextView opening,place_address,place_name,time_2, contact, food_style, website;
    ImageButton btnViewOnMap,btnViewDirection,btnViewWebsite, btnCall, btnShare;
    RecyclerView recyclerView;
    FirebaseReviewAdapter firebaseReviewAdapter;
    PlaceDetail mPlace;
    private ViewPager res_viewPager,menu_viewPager;
    private ItemPagerAdapter viewpager_adapter;

    LinearLayout menu, desc,time1,time2,averageprice_linear;

    public PageThree() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_page_three, container, false);

        mService = Common.getGoogleAPIService();

        res_viewPager = (ViewPager) view.findViewById(R.id.res_viewPager);
        menu_viewPager = (ViewPager) view.findViewById(R.id.menu_viewPager);
        //photo = (ImageView) view.findViewById(R.id.photo);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        opening = (TextView) view.findViewById(R.id.place_open);
        place_address = (TextView) view.findViewById(R.id.place_address);
        place_name = (TextView) view.findViewById(R.id.place_name);
        btnViewOnMap = (ImageButton) view.findViewById(R.id.btn_show_map);
        btnViewDirection = (ImageButton) view.findViewById(R.id.btn_view_direction);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        /*DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        recyclerView.addItemDecoration(itemDecoration);*/

        btnCall = (ImageButton) view.findViewById(R.id.btn_call);
        btnShare = (ImageButton) view.findViewById(R.id.btn_share);
        btnViewWebsite = (ImageButton) view.findViewById(R.id.btn_show_website);
        menu = (LinearLayout) view.findViewById(R.id.menu_linearlayout);
        desc = (LinearLayout) view.findViewById(R.id.desc_linearlayout);
        time1 = (LinearLayout) view.findViewById(R.id.time1);
        time2 = (LinearLayout) view.findViewById(R.id.time2);
        averageprice_linear = (LinearLayout) view.findViewById(R.id.averagePrice_linearlayout);
        time_2= (TextView) view.findViewById(R.id.open_close_time2);
        contact = (TextView) view.findViewById(R.id.contact);
        food_style = (TextView) view.findViewById(R.id.food_style);
        website = (TextView) view.findViewById(R.id.website);

        place_name.setText("");
        place_address.setText("");
        opening.setText("");

        create_review_btn = (Button)view.findViewById(R.id.create_review_btn);
        create_review_btn.setVisibility(View.GONE);

        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior.setPeekHeight(0);

        ramdom_btn=view.findViewById(R.id.btn_random);
        ramdom_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lat==0 && lng ==0){
                    Toast.makeText(getContext(),"Unable to detect current location please try again!",Toast.LENGTH_SHORT).show();
                }
                else if(price_selection.equals("")){
                    Toast.makeText(getContext(),"Please select a price level!",Toast.LENGTH_SHORT).show();
                }else{
                    randomPlace();
                }
            }
        });

        radioGroup = view.findViewById(R.id.price_selector);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.price1:
                        price_selection="1";
                        break;
                    case R.id.price2:
                        price_selection="2";
                        break;
                    case R.id.price3:
                        price_selection="3";
                        break;
                    case R.id.price4:
                        price_selection="4";
                        break;

                }
            }
        });


        return view;
    }

    public void setCoordinate(Double lat, Double lng){
        this.lat = lat;
        this.lng = lng;
    }

    private void randomPlace(){

        mService.getNearByPlaces(getUrl(lat,lng)).enqueue(new Callback<MyPlaces>() {
            @Override
            public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                if(response.isSuccessful()){

                    Random random = new Random();
                    //Toast.makeText(getContext(),String.valueOf(response.body().getResults().length),Toast.LENGTH_SHORT).show();

                    if(response.body().getResults().length>0) {
                        int i = random.nextInt(response.body().getResults().length)+1;
                        Results googlePlace = response.body().getResults()[i-1];

                        mService.getDetailPlace(getPlaceDetailUrl(googlePlace.getPlace_id())).enqueue(new Callback<PlaceDetail>() {
                            @Override
                            public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                                if (response.isSuccessful()) {
                                    mPlace = response.body();
                                    //Toast.makeText(getContext(), mPlace.getResult().getName(), Toast.LENGTH_SHORT).show();

                                    //Photo
                                    if (mPlace.getResult().getPhotos() != null && mPlace.getResult().getPhotos().length > 0) {

                                        String [] image= new String[1];
                                        image[0] = getPhotoOfPlace(mPlace.getResult().getPhotos()[0].getPhoto_reference(), 1000);
                                        viewpager_adapter = new ItemPagerAdapter(getContext(),image);
                                        res_viewPager.setAdapter(viewpager_adapter);
                                        res_viewPager.setVisibility(View.VISIBLE);

                                    }else{
                                        res_viewPager.setVisibility(View.GONE);
                                    }

                                    menu.setVisibility(View.GONE);
                                    desc.setVisibility(View.GONE);
                                    averageprice_linear.setVisibility(View.GONE);

                                    //Rating
                                    if (mPlace.getResult().getRating() != null && !TextUtils.isEmpty(mPlace.getResult().getRating())) {
                                        ratingBar.setRating(Float.parseFloat(mPlace.getResult().getRating()));
                                    } else {
                                        ratingBar.setRating(0);
                                    }

                                    //Open
                                    if (mPlace.getResult().getOpening_hours() != null) {
                                        if (mPlace.getResult().getOpening_hours().getOpen_now().equals("true")) {
                                            opening.setText("Open");
                                        } else {
                                            opening.setText("Close");
                                        }
                                    } else {
                                        opening.setText("Unidentified");
                                    }

                                    //Address
                                    if(mPlace.getResult().getFormatted_address()!=null) {
                                        place_address.setText(mPlace.getResult().getFormatted_address());
                                    }else{
                                        place_address.setText("");
                                    }

                                    //Name
                                    if(mPlace.getResult().getName()!=null) {
                                        place_name.setText(mPlace.getResult().getName());
                                    }else{
                                        place_name.setText("");
                                    }

                                    //Review
                                    List<CurrentReview_Firebase> currentReviews = new ArrayList<>();

                                    if (mPlace.getResult().getReviews() != null) {
                                        for (int i = 0; i < mPlace.getResult().getReviews().length; i++) {

                                            String review = mPlace.getResult().getReviews()[i].getText();
                                            String rating = mPlace.getResult().getReviews()[i].getRating();
                                            String user = mPlace.getResult().getReviews()[i].getAuthor_name();
                                            CurrentReview_Firebase cr = new CurrentReview_Firebase(rating, review, user);
                                            currentReviews.add(cr);
                                        }
                                        firebaseReviewAdapter = new FirebaseReviewAdapter(getActivity(), currentReviews);
                                        recyclerView.setAdapter(firebaseReviewAdapter);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        firebaseReviewAdapter.notifyDataSetChanged();
                                    } else {
                                        recyclerView.setVisibility(View.GONE);
                                    }

                                    if(mPlace.getResult().getOpening_hours()!=null && mPlace.getResult().getOpening_hours().getWeekday_text()!=null){
                                        String [] time_text = mPlace.getResult().getOpening_hours().getWeekday_text();
                                        time_2.setText(time_text[0]+"\n"+time_text[1]+"\n"+time_text[2]+"\n"+time_text[3]+"\n"+time_text[4]+"\n"+time_text[5]+"\n"+time_text[6]);
                                        time1.setVisibility(View.GONE);
                                        time2.setVisibility(View.VISIBLE);
                                    }else{
                                        time1.setVisibility(View.GONE);
                                        time_2.setText("");
                                        time2.setVisibility(View.VISIBLE);
                                    }

                                    if(mPlace.getResult().getTypes()!=null){
                                        String [] foodstyle_text =  mPlace.getResult().getTypes();
                                        food_style.setText(foodstyle_text[0]);
                                    }else{
                                        food_style.setText("");
                                    }

                                    if(mPlace.getResult().getWebsite()!=null){
                                        website.setText(mPlace.getResult().getWebsite());
                                    }else{
                                        website.setText("");
                                    }

                                    if(mPlace.getResult().getInternational_phone_number()!=null){
                                        contact.setText(mPlace.getResult().getInternational_phone_number());
                                    }else{
                                        contact.setText("");
                                    }

                                }
                            }

                            @Override
                            public void onFailure(Call<PlaceDetail> call, Throwable t) {

                            }
                        });

                        //Button
                        btnViewOnMap.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(mPlace.getResult().getUrl()!=null) {
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getUrl()));
                                    startActivity(mapIntent);
                                }else{
                                    Toast.makeText(getContext(), "Coordinates of the food place is not provided!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        btnViewWebsite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(mPlace.getResult().getWebsite()!=null) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getWebsite()));
                                    startActivity(browserIntent);
                                }else{
                                    Toast.makeText(getContext(), "This food place does not have a website!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        btnCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                                    if(mPlace.getResult().getInternational_phone_number()!=null) {
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                        String phno = "tel:" + mPlace.getResult().getInternational_phone_number();
                                        callIntent.setData(Uri.parse(phno));
                                        startActivity(callIntent);
                                    }else{
                                        Toast.makeText(getContext(), "This food place does not have a contact number!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "This device is unable to perform call operation.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        btnShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(mPlace.getResult().getUrl()!=null) {
                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, mPlace.getResult().getUrl());
                                    shareIntent.setType("text/plain");
                                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                                }else{
                                    Toast.makeText(getContext(), "This food place does not have a link to share!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        bottomSheetBehavior.setPeekHeight(190);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        bottomSheetBehavior.setHideable(true);

                    }else{
                        Toast.makeText(getContext(),"There are no food places available according to the price level you have selected around your area.",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyPlaces> call, Throwable t) {

            }
        });
    }

    private String getUrl(double lat, double lon) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + lat + "," + lon);
        googlePlacesUrl.append("&radius=" + 2000);
        googlePlacesUrl.append("&type=restaurant|food");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&maxprice="+ price_selection);
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.browser_key));
        Log.d("getUrl: ", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();
    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
        url.append("?placeid=" + place_id);
        url.append("&key=" + getResources().getString(R.string.browser_key));
        return url.toString();
    }

    private String getPhotoOfPlace(String photo_reference, int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth=" + maxWidth);
        url.append("&photoreference=" + photo_reference);
        url.append("&key=" + getResources().getString(R.string.browser_key));
        Log.d("getPhotoOfPlace: ", url.toString());
        return url.toString();
    }
}
