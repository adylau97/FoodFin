package com.mhp.foodfinder.Fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.media.Rating;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mhp.foodfinder.Activity.PlacesInsert;
import com.mhp.foodfinder.Activity.ViewDirection;
import com.mhp.foodfinder.Adapter.FirebaseReviewAdapter;
import com.mhp.foodfinder.Adapter.ItemPagerAdapter;
import com.mhp.foodfinder.Adapter.MenuAdapter;
import com.mhp.foodfinder.Adapter.PlaceAutocompleteAdapter;
import com.mhp.foodfinder.Adapter.ReviewAdapter;
import com.mhp.foodfinder.Model.CurrentReview;
import com.mhp.foodfinder.Model.CurrentReview_Firebase;
import com.mhp.foodfinder.Model.Firebase_Restaurant;
import com.mhp.foodfinder.Model.MyPlaces;
import com.mhp.foodfinder.Model.Nearby;
import com.mhp.foodfinder.Model.Photos;
import com.mhp.foodfinder.Model.PlaceDetail;
import com.mhp.foodfinder.Model.Results;
import com.mhp.foodfinder.Model.Reviews;
import com.mhp.foodfinder.Model.User;
import com.mhp.foodfinder.R;
import com.mhp.foodfinder.Remote.Common;
import com.mhp.foodfinder.Remote.IGoogleAPIService;
import com.squareup.picasso.Picasso;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class PageOne extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnCameraMoveStartedListener
        , GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener {

    GoogleMap mMap;
    SupportMapFragment mapFragment;
    GoogleApiClient googleApiClient;

    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mPageLeft, mPageRight;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private GeoDataClient mGeoDataClient;
    private ViewPager res_viewPager, menu_viewPager;
    private ItemPagerAdapter viewpager_adapter;

    BottomSheetBehavior bottomSheetBehavior;

    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=30.3280983,-81.4855&radius=10000&type=market&sensor=true&key=AIzaSyB9elh2EcYY2uuejoYNZGz2hvdneuS_Ex0
    private double latitude, longitude, point_lat, point_lng = 0;
    IGoogleAPIService mService;
    MyPlaces currentPlace;
    //ImageView photo;
    RatingBar ratingBar;
    TextView opening, place_address, place_name, description, time_1, time_2, contact, averageprice, food_style, website;
    LinearLayout menu, desc, time1, time2, averageprice_linear;
    ImageButton btnViewOnMap, btnViewDirection, btnViewWebsite, btnCall, btnShare;
    Button create_review_btn;
    FloatingTextButton insert_review_btn;
    PlaceDetail mPlace;

    String[] next_token;
    int counter;
    int animcounter = 0;
    int firebaseCounter = 0;
    int firebaseRecorder = 0;
    String pageToken;

    private RecyclerView recyclerView,menu_recyclerView;
    private MenuAdapter menuAdapter;
    private FirebaseReviewAdapter firebaseReviewAdapter;

    private PageOneListener listener;

    private RadioGroup radioGroup;
    private EditText reviewText;
    private String review_selection;
    private String review;
    private List<CurrentReview_Firebase> currentReview = new ArrayList<>();

    int ratings;

    String user_text;
    boolean mark = true;


    public PageOne() {
        // Required empty public constructor
    }

    public interface PageOneListener {
        void passList(List<Nearby> nearbyList);

        void passCoordinate(Double lat, Double lng);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_page_one, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mapFragment.getMapAsync(this);
        mSearchText = (AutoCompleteTextView) view.findViewById(R.id.input_search);
        mGps = (ImageView) view.findViewById(R.id.mylocationbutton);
        mPageLeft = (ImageView) view.findViewById(R.id.page_left);
        mPageRight = (ImageView) view.findViewById(R.id.page_right);

        res_viewPager = (ViewPager) view.findViewById(R.id.res_viewPager);
        menu_viewPager = (ViewPager) view.findViewById(R.id.menu_viewPager);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        //Code that can use
        //bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet));
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior.setPeekHeight(0);

        //viewPager = (ViewPager)view.findViewById(R.id.pager);


        mService = Common.getGoogleAPIService();

        //photo = (ImageView) view.findViewById(R.id.photo);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        opening = (TextView) view.findViewById(R.id.place_open);
        place_address = (TextView) view.findViewById(R.id.place_address);
        place_name = (TextView) view.findViewById(R.id.place_name);
        btnViewOnMap = (ImageButton) view.findViewById(R.id.btn_show_map);
        btnViewDirection = (ImageButton) view.findViewById(R.id.btn_view_direction);
        btnCall = (ImageButton) view.findViewById(R.id.btn_call);
        btnShare = (ImageButton) view.findViewById(R.id.btn_share);
        btnViewWebsite = (ImageButton) view.findViewById(R.id.btn_show_website);
        create_review_btn = (Button) view.findViewById(R.id.create_review_btn);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        menu_recyclerView= (RecyclerView) view.findViewById(R.id.menu_recycler_view);
        menu_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        /*DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        recyclerView.addItemDecoration(itemDecoration);*/

        description = (TextView) view.findViewById(R.id.description);
        time_1 = (TextView) view.findViewById(R.id.open_close_time1);
        time_2 = (TextView) view.findViewById(R.id.open_close_time2);
        contact = (TextView) view.findViewById(R.id.contact);
        averageprice = (TextView) view.findViewById(R.id.average_price);
        food_style = (TextView) view.findViewById(R.id.food_style);
        website = (TextView) view.findViewById(R.id.website);
        menu = (LinearLayout) view.findViewById(R.id.menu_linearlayout);
        desc = (LinearLayout) view.findViewById(R.id.desc_linearlayout);
        time1 = (LinearLayout) view.findViewById(R.id.time1);
        time2 = (LinearLayout) view.findViewById(R.id.time2);
        averageprice_linear = (LinearLayout) view.findViewById(R.id.averagePrice_linearlayout);

        place_name.setText("");
        place_address.setText("");
        opening.setText("");
        description.setText("");
        time_1.setText("");
        time_2.setText("");
        contact.setText("");
        averageprice.setText("");
        food_style.setText("");
        website.setText("");

        mPageRight.setVisibility(View.INVISIBLE);
        mPageLeft.setVisibility(View.INVISIBLE);

        return view;

    }

    public void recyclerViewClick(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 25f));
        //Toast.makeText(getActivity(),latLng.toString(),Toast.LENGTH_SHORT).show();
    }

    private void nearByPlace(String placeType) {
        mMap.clear();

        next_token = new String[10];
        pageToken = "";
        next_token[0] = "";
        counter = 0;

        String url = getUrl(latitude, longitude, placeType);

        mPageLeft.setVisibility(View.INVISIBLE);

        mService.getNearByPlaces(url).enqueue(new Callback<MyPlaces>() {
            @Override
            public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                currentPlace = response.body();
                final List<Nearby> nearbyList = new ArrayList<>();

                if (response.isSuccessful()) {

                    if (response.body().getNext_page_token() != null) {
                        next_token[counter + 1] = response.body().getNext_page_token();
                        mPageRight.setVisibility(View.VISIBLE);

                    } else {
                        mPageRight.setVisibility(View.INVISIBLE);
                    }

                    firebaseRecorder = response.body().getResults().length;
                    firebaseCounter = response.body().getResults().length;
                    ;

                    for (int i = 0; i < response.body().getResults().length; i++) {

                        Results googlePlace = response.body().getResults()[i];

                        MarkerOptions markerOptions = new MarkerOptions();
                        double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                        double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());

                        String placeName = googlePlace.getName();
                        String review = "";
                        if (googlePlace.getRating() != null && !TextUtils.isEmpty(googlePlace.getRating())) {
                            review = googlePlace.getRating();
                        }
                        String photo_reference = "";
                        if (googlePlace.getPhotos() != null && googlePlace.getPhotos().length > 0) {
                            photo_reference = getPhotoOfPlace(googlePlace.getPhotos()[0].getPhoto_reference(), 1000);
                        }
                        //String vicinity = googlePlace.getVicinity();
                        LatLng latLng = new LatLng(lat, lng);

                        markerOptions.position(latLng);
                        //markerOptions.title(placeName);
                        markerOptions.snippet(String.valueOf(i));

                        String isOpening = "";

                        if (googlePlace.getOpening_hours() != null) {
                            if (googlePlace.getOpening_hours().getOpen_now().equals("true")) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_open));
                                isOpening = "Open";
                            } else {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_close));
                                isOpening = "Close";
                            }
                        } else {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_undefined));
                            isOpening = "Unidentified";
                        }


                        mMap.addMarker(markerOptions);
                        Nearby nearby = new Nearby(placeName, review, photo_reference, isOpening, latLng);

                       /* if(counter==1){
                            nearbyList.clear();
                            counter=0;
                            Log.d("getPass: ","pass");
                        }*/


                        nearbyList.add(nearby);

                    }

                    //counter++;
                    //Log.d("getCount: ",String.valueOf(counter));
                    //Log.d("getList: ",String.valueOf(nearbyList.size()));

                    /*if(!nearbyList.isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("nearbylist", (Serializable) nearbyList);
                        PageTwo pageTwo = new PageTwo();
                        pageTwo.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.fragment2,pageTwo).commit();
                        //getFragmentManager().beginTransaction().hide(pageTwo).commit();
                        //Toast.makeText(getActivity(),pageTwo.getArguments().toString(),Toast.LENGTH_SHORT).show();
                    }*/

                    mDatabase.child("restaurant2").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (nearbyList.size() > firebaseRecorder) {
                                nearbyList.subList(firebaseRecorder, nearbyList.size()).clear();
                            }

                            for (DataSnapshot data : dataSnapshot.getChildren()) {

                                Firebase_Restaurant restaurant = data.getValue(Firebase_Restaurant.class);

                                LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.snippet(String.valueOf(firebaseCounter));
                                markerOptions.title(data.getKey());
                                firebaseCounter++;

                                Location locA = new Location("Point A");
                                locA.setLongitude(longitude);
                                locA.setLatitude(latitude);

                                Location locB = new Location("Point B");
                                locB.setLongitude(restaurant.getLongitude());
                                locB.setLatitude(restaurant.getLatitude());

                                if (locB.distanceTo(locA) <= 500) {

                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                                    String isOpening = "";
                                    try {

                                        Date d = new Date();
                                        String curr = sdf.format(d);
                                        Date d0 = sdf.parse(curr);
                                        Date d1 = sdf.parse(restaurant.getOpentime());
                                        Date d2 = sdf.parse(restaurant.getClosetime());

                                        if (d0.after(d1) && d0.before(d2)) {
                                            isOpening = "Open";
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_open));
                                        } else {
                                            isOpening = "Close";
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_close));
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    int rating = 0;

                                    if (restaurant.getRatings() != 0) {
                                        rating = restaurant.getRatings();
                                    }

                                    mMap.addMarker(markerOptions);

                                    List<String> image = restaurant.getImage();

                                    //Later edit
                                    Nearby nearby = new Nearby(restaurant.getName(), String.valueOf(rating), image.get(0), isOpening, latLng);
                                    nearbyList.add(nearby);
                                }


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    listener.passList(nearbyList);
                }
            }

            @Override
            public void onFailure(Call<MyPlaces> call, Throwable t) {

            }
        });

    }


    private String getUrl(double lat, double lon, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + lat + "," + lon);
        googlePlacesUrl.append("&radius=" + 500);
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&sensor=true");
        //googlePlacesUrl.append("&maxprice=3");
        googlePlacesUrl.append("&pagetoken=" + pageToken);
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.browser_key));
        Log.d("getUrl: ", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(),R.raw.style_json));
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                init();


                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            init();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Toast.makeText(getActivity(), "Map Connected", Toast.LENGTH_SHORT).show();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                //Place current location marker
                LatLng latLng = new LatLng(latitude, longitude);

                //move map camera
                if (animcounter == 0) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.5f));
                    animcounter++;
                }


            }
        }

    };

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity().getApplicationContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

   /* private void setLocation() {

        final LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        mDatabase.child("restaurant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    Firebase_Restaurant restaurant = data.getValue(Firebase_Restaurant.class);

                    LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    markerOptions.title(data.getKey());


                    if (bounds.contains(latLng)) {
                        mMap.addMarker(markerOptions);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }*/


    @Override
    public void onMapClick(LatLng latLng) {

       /* MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        markerOptions.title("Pointer");


        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(markerOptions);


        Toast.makeText(getActivity(), latLng.toString(),
                Toast.LENGTH_SHORT).show();*/


    }

    @Override
    public void onMapLongClick(LatLng latLng) {

       /* MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        markerOptions.title("Pointer");


        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(markerOptions);

        //writeNewRestaurant(latLng.latitude, latLng.longitude);

        Toast.makeText(getActivity(), latLng.toString(),
                Toast.LENGTH_SHORT).show();
*/

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onCameraIdle() {
        //setLocation();
        //nearByPlace("cafe|restaurant");
        //nearByPlace("cafe|food");

        Location locA = new Location("Point A");
        locA.setLongitude(longitude);
        locA.setLatitude(latitude);

        Location locB = new Location("Point B");
        locB.setLongitude(point_lng);
        locB.setLatitude(point_lat);

        if (locB.distanceTo(locA) >= 450) {
            nearByPlace("restaurant|food");
            point_lat = latitude;
            point_lng = longitude;
            listener.passCoordinate(point_lat, point_lng);
        }
    }

    @Override
    public void onCameraMove() {
        //setLocation();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        if (Integer.parseInt(marker.getSnippet()) >= firebaseRecorder) {

            mDatabase.child("restaurant2").child(marker.getTitle()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final Firebase_Restaurant restaurant = dataSnapshot.getValue(Firebase_Restaurant.class);

                    //Toast.makeText(getContext(),restaurant.getName(),Toast.LENGTH_SHORT).show();

                    if (restaurant == null) {
                        Toast.makeText(getContext(), "This place is currently not available!", Toast.LENGTH_SHORT).show();
                        marker.remove();
                        mark = false;
                        return;
                    }else{
                        mark = true;
                    }

                    //Name
                    if (restaurant.getName() != null) {
                        place_name.setText(restaurant.getName());
                    } else {
                        place_name.setText("");
                    }

                    if (restaurant.getOpentime() != null && restaurant.getClosetime() != null) {
                        time_1.setText("Open Time: " + restaurant.getOpentime() + "\n" + "Close Time: " + restaurant.getClosetime());
                        time1.setVisibility(View.VISIBLE);
                        time2.setVisibility(View.GONE);
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                        try {

                            Date d = new Date();
                            String curr = sdf.format(d);
                            Date d0 = sdf.parse(curr);
                            Date d1 = sdf.parse(restaurant.getOpentime());
                            Date d2 = sdf.parse(restaurant.getClosetime());

                            if (d0.after(d1) && d0.before(d2)) {
                                opening.setText("Open");
                            } else {
                                opening.setText("Close");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        time_1.setText("");
                        time1.setVisibility(View.VISIBLE);
                        time2.setVisibility(View.GONE);
                    }

                    if (restaurant.getDescription() != null) {
                        description.setText(restaurant.getDescription());
                        desc.setVisibility(View.VISIBLE);
                    } else {
                        description.setText("");
                    }

                    if (restaurant.getAveragePrice() != null) {
                        averageprice.setText(restaurant.getAveragePrice());
                        averageprice_linear.setVisibility(View.VISIBLE);
                    } else {
                        averageprice.setText("");
                    }

                    if (restaurant.getContact() != null) {
                        contact.setText(restaurant.getContact());
                    } else {
                        contact.setText("");
                    }

                    if (restaurant.getFoodStyle() != null) {
                        food_style.setText(restaurant.getFoodStyle());
                    } else {
                        food_style.setText("");
                    }

                    if (restaurant.getWebsite() != null) {
                        website.setText(restaurant.getWebsite());
                    } else {
                        website.setText("");
                    }

                    if (restaurant.getAddress() != null) {
                        place_address.setText(restaurant.getAddress());
                    } else {
                        place_address.setText("");
                    }


                    //Photo
                    if (restaurant.getImage() != null) {

                        List<String> image = restaurant.getImage();
                        String[] img_transfer = new String[image.size()];
                        for (int i = 0; i < image.size(); i++) {
                            img_transfer[i] = image.get(i);
                        }

                        viewpager_adapter = new ItemPagerAdapter(getContext(), img_transfer);
                        res_viewPager.setAdapter(viewpager_adapter);
                        res_viewPager.setVisibility(View.VISIBLE);

                    } else {
                        res_viewPager.setVisibility(View.GONE);
                    }

                    //Menu
                    if (restaurant.getMenu() != null) {

                        List<String> image = restaurant.getMenu();
                        /*String[] img_transfer = new String[image.size()];
                        for (int i = 0; i < image.size(); i++) {
                            img_transfer[i] = image.get(i);
                        }

                        viewpager_adapter = new ItemPagerAdapter(getContext(), img_transfer);
                        menu_viewPager.setAdapter(viewpager_adapter);
                        menu.setVisibility(View.VISIBLE);*/


                        menuAdapter = new MenuAdapter(getActivity(),image);
                        menu_recyclerView.setAdapter(menuAdapter);
                        menu.setVisibility(View.VISIBLE);
                        menuAdapter.notifyDataSetChanged();

                    } else {
                        menu.setVisibility(View.GONE);
                        //menu_recyclerView.setVisibility(View.GONE);
                    }

                    btnViewWebsite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!restaurant.getWebsite().equals("")) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.getWebsite()));
                                startActivity(browserIntent);
                            } else {
                                Toast.makeText(getContext(), "This food place does not have a website!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    btnViewOnMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/maps/place/" + restaurant.getLatitude() + "," + restaurant.getLongitude()));
                            startActivity(mapIntent);
                        }
                    });

                    btnCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                                String phno = "tel:" + restaurant.getContact();
                                callIntent.setData(Uri.parse(phno));
                                startActivity(callIntent);
                            } else {
                                Toast.makeText(getContext(), "This device is unable to perform call operation.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    btnShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.google.com/maps/place/" + restaurant.getLatitude() + "," + restaurant.getLongitude());
                            shareIntent.setType("text/plain");
                            startActivity(Intent.createChooser(shareIntent, "Share via"));
                        }
                    });


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            mDatabase.child("restaurant2").child(marker.getTitle()).child("reviews").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentReview.clear();
                    ratings = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        CurrentReview_Firebase cr = data.getValue(CurrentReview_Firebase.class);
                        ratings = ratings + Integer.parseInt(cr.rating);
                        currentReview.add(cr);
                    }

                    firebaseReviewAdapter = new FirebaseReviewAdapter(getActivity(), currentReview);
                    recyclerView.setAdapter(firebaseReviewAdapter);

                    if (currentReview.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    firebaseReviewAdapter.notifyDataSetChanged();

                    if (ratings != 0) {
                        int final_rating = ratings / currentReview.size();
                        ratingBar.setRating(final_rating);
                        mDatabase.child("restaurant2").child(marker.getTitle()).child("ratings").setValue(final_rating);
                    } else {
                        ratingBar.setRating(0);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            create_review_btn.setVisibility(View.VISIBLE);

            create_review_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
                    View mView = getLayoutInflater().inflate(R.layout.insert_review, null);

                    review_selection = "";

                    radioGroup = (RadioGroup) mView.findViewById(R.id.review_selector);
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            switch (i) {
                                case R.id.review1:
                                    review_selection = "1";
                                    break;
                                case R.id.review2:
                                    review_selection = "2";
                                    break;
                                case R.id.review3:
                                    review_selection = "3";
                                    break;
                                case R.id.review4:
                                    review_selection = "4";
                                    break;
                                case R.id.review5:
                                    review_selection = "5";
                                    break;

                            }
                        }
                    });
                    reviewText = (EditText) mView.findViewById(R.id.reviewText);
                    insert_review_btn = (FloatingTextButton) mView.findViewById(R.id.insert_review_btn);
                    mBuilder.setView(mView);
                    final android.support.v7.app.AlertDialog dialog = mBuilder.create();
                    dialog.setCancelable(true);
                    dialog.show();

                    insert_review_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (review_selection.equals("")) {
                                Toast.makeText(getContext(), "Please select a rating!", Toast.LENGTH_SHORT).show();
                            } else {
                                review = reviewText.getText().toString().trim();
                                /*if(review.equals("")){
                                    review = "null";
                                }*/
                                final FirebaseUser user = mAuth.getCurrentUser();


                                mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user_d = dataSnapshot.getValue(User.class);

                                        if(!user_d.getName().equals("")){
                                            user_text = user_d.getName();
                                        }else{
                                            user_text = user.getEmail();
                                        }

                                        CurrentReview_Firebase cr = new CurrentReview_Firebase(review_selection, review, user_text);
                                        currentReview.add(cr);
                                        mDatabase.child("restaurant2").child(marker.getTitle()).child("reviews").setValue(currentReview);
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }
                        }
                    });

                }
            });

        } else {

            try {

                mark = true;

                Common.currentResult = currentPlace.getResults()[Integer.parseInt(marker.getSnippet())];

                //Photo
                if (Common.currentResult.getPhotos() != null && Common.currentResult.getPhotos().length > 0) {

                    String[] image = new String[1];
                    image[0] = getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(), 1000);
                    viewpager_adapter = new ItemPagerAdapter(getContext(), image);
                    res_viewPager.setAdapter(viewpager_adapter);
                    res_viewPager.setVisibility(View.VISIBLE);

                } else {
                    res_viewPager.setVisibility(View.GONE);
                }

                menu.setVisibility(View.GONE);
                desc.setVisibility(View.GONE);
                averageprice_linear.setVisibility(View.GONE);


                //Rating
                if (Common.currentResult.getRating() != null && !TextUtils.isEmpty(Common.currentResult.getRating())) {
                    ratingBar.setRating(Float.parseFloat(Common.currentResult.getRating()));
                } else {
                    ratingBar.setRating(0);
                }

                //Open
                if (Common.currentResult.getOpening_hours() != null) {
                    if (Common.currentResult.getOpening_hours().getOpen_now().equals("true")) {
                        opening.setText("Open");
                    } else {
                        opening.setText("Close");
                    }
                } else {
                    opening.setText("Unidentified");
                }


                //User servicee to fetch Address and Name
                mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult.getPlace_id()))
                        .enqueue(new Callback<PlaceDetail>() {
                            @Override
                            public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                                if (response.isSuccessful()) {
                                    mPlace = response.body();

                                    if (mPlace.getResult().getFormatted_address() != null) {
                                        place_address.setText(mPlace.getResult().getFormatted_address());
                                    } else {
                                        place_address.setText("");
                                    }

                                    if (mPlace.getResult().getName() != null) {
                                        place_name.setText(mPlace.getResult().getName());
                                    } else {
                                        place_name.setText("");
                                    }
                               /* //Opening hours
                                if (mPlace.getResult().getOpening_hours().getWeekday_text() != null) {
                                    opening_hours.setText("Open Now: " + mPlace.getResult().getOpening_hours().getWeekday_text()[6]);
                                } else {
                                    opening_hours.setVisibility(View.GONE);
                                }
*/
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

                                    if (mPlace.getResult().getOpening_hours() != null && mPlace.getResult().getOpening_hours().getWeekday_text() != null) {
                                        String[] time_text = mPlace.getResult().getOpening_hours().getWeekday_text();
                                        time_2.setText(time_text[0] + "\n" + time_text[1] + "\n" + time_text[2] + "\n" + time_text[3] + "\n" + time_text[4] + "\n" + time_text[5] + "\n" + time_text[6]);
                                        time1.setVisibility(View.GONE);
                                        time2.setVisibility(View.VISIBLE);
                                    } else {
                                        time1.setVisibility(View.GONE);
                                        time_2.setText("");
                                        time2.setVisibility(View.VISIBLE);
                                    }

                                    if (mPlace.getResult().getTypes() != null) {
                                        String[] foodstyle_text = mPlace.getResult().getTypes();
                                        food_style.setText(foodstyle_text[0]);
                                    } else {
                                        food_style.setText("");
                                    }

                                    if (mPlace.getResult().getWebsite() != null) {
                                        website.setText(mPlace.getResult().getWebsite());
                                    } else {
                                        website.setText("");
                                    }

                                    if (mPlace.getResult().getInternational_phone_number() != null) {
                                        contact.setText(mPlace.getResult().getInternational_phone_number());
                                    } else {
                                        contact.setText("");
                                    }


                                }
                            }

                            @Override
                            public void onFailure(Call<PlaceDetail> call, Throwable t) {

                            }
                        });


                create_review_btn.setVisibility(View.GONE);

                btnViewOnMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPlace.getResult().getUrl() != null) {
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getUrl()));
                            startActivity(mapIntent);
                        } else {
                            Toast.makeText(getContext(), "Coordinates of the food place is not provided!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                /*btnViewDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mapIntent = new Intent(getContext(), ViewDirection.class);
                        startActivity(mapIntent);
                    }
                });*/

                btnViewWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPlace.getResult().getWebsite() != null) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getWebsite()));
                            startActivity(browserIntent);
                        } else {
                            Toast.makeText(getContext(), "This food place does not have a website!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                            if (mPlace.getResult().getInternational_phone_number() != null) {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                String phno = "tel:" + mPlace.getResult().getInternational_phone_number();
                                callIntent.setData(Uri.parse(phno));
                                startActivity(callIntent);
                            } else {
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
                        if (mPlace.getResult().getUrl() != null) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, mPlace.getResult().getUrl());
                            shareIntent.setType("text/plain");
                            startActivity(Intent.createChooser(shareIntent, "Share via"));
                        } else {
                            Toast.makeText(getContext(), "This food place does not have a link to share!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } catch (Exception e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
            }

        }

        if(mark) {
            //Code that can use
            bottomSheetBehavior.setPeekHeight(190);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setHideable(true);
        }

        return true;
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

    private void getPrevPage() {
        mMap.clear();

        counter--;
        pageToken = next_token[counter];
        //Toast.makeText(getContext(),String.valueOf(counter),Toast.LENGTH_SHORT).show();
        String url = getUrl(latitude, longitude, "restaurant|food");
        mService.getNearByPlaces(url).enqueue(new Callback<MyPlaces>() {
            @Override
            public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                currentPlace = response.body();
                final List<Nearby> nearbyList = new ArrayList<>();

                if (response.isSuccessful()) {

                    if (response.body().getNext_page_token() != null) {
                        mPageRight.setVisibility(View.VISIBLE);
                    }

                    firebaseCounter = response.body().getResults().length;
                    firebaseRecorder = response.body().getResults().length;

                    for (int i = 0; i < response.body().getResults().length; i++) {

                        Results googlePlace = response.body().getResults()[i];

                        MarkerOptions markerOptions = new MarkerOptions();
                        double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                        double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());

                        String placeName = googlePlace.getName();
                        String review = "";
                        if (googlePlace.getRating() != null && !TextUtils.isEmpty(googlePlace.getRating())) {
                            review = googlePlace.getRating();
                        }
                        String photo_reference = "";
                        if (googlePlace.getPhotos() != null && googlePlace.getPhotos().length > 0) {
                            photo_reference = getPhotoOfPlace(googlePlace.getPhotos()[0].getPhoto_reference(), 1000);
                        }
                        LatLng latLng = new LatLng(lat, lng);

                        markerOptions.position(latLng);
                        markerOptions.snippet(String.valueOf(i));

                        String isOpening = "";

                        if (googlePlace.getOpening_hours() != null) {
                            if (googlePlace.getOpening_hours().getOpen_now().equals("true")) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_open));
                                isOpening = "Open";
                            } else {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_close));
                                isOpening = "Close";
                            }
                        } else {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_undefined));
                            isOpening = "Unidentified";
                        }


                        mMap.addMarker(markerOptions);
                        Nearby nearby = new Nearby(placeName, review, photo_reference, isOpening, latLng);


                        nearbyList.add(nearby);

                    }

                    mDatabase.child("restaurant2").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (nearbyList.size() > firebaseRecorder) {
                                nearbyList.subList(firebaseRecorder, nearbyList.size()).clear();
                            }

                            for (DataSnapshot data : dataSnapshot.getChildren()) {

                                Firebase_Restaurant restaurant = data.getValue(Firebase_Restaurant.class);

                                LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.snippet(String.valueOf(firebaseCounter));
                                markerOptions.title(data.getKey());
                                firebaseCounter++;

                                Location locA = new Location("Point A");
                                locA.setLongitude(longitude);
                                locA.setLatitude(latitude);

                                Location locB = new Location("Point B");
                                locB.setLongitude(restaurant.getLongitude());
                                locB.setLatitude(restaurant.getLatitude());

                                if (locB.distanceTo(locA) <= 500) {

                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                                    String isOpening = "";
                                    try {

                                        Date d = new Date();
                                        String curr = sdf.format(d);
                                        Date d0 = sdf.parse(curr);
                                        Date d1 = sdf.parse(restaurant.getOpentime());
                                        Date d2 = sdf.parse(restaurant.getClosetime());

                                        if (d0.after(d1) && d0.before(d2)) {
                                            isOpening = "Open";
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_open));
                                        } else {
                                            isOpening = "Close";
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_close));
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    int rating = 0;

                                    if (restaurant.getRatings() != 0) {
                                        rating = restaurant.getRatings();
                                    }

                                    mMap.addMarker(markerOptions);

                                    List<String> image = restaurant.getImage();
                                    //Later edit
                                    Nearby nearby = new Nearby(restaurant.getName(), String.valueOf(rating), image.get(0), isOpening, latLng);
                                    nearbyList.add(nearby);
                                }


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    listener.passList(nearbyList);
                }
            }

            @Override
            public void onFailure(Call<MyPlaces> call, Throwable t) {

            }
        });

        if (counter == 0) {
            mPageLeft.setVisibility(View.INVISIBLE);
        } else {
            mPageLeft.setVisibility(View.VISIBLE);
        }

    }

    private void getNextPage() {
        mMap.clear();
        counter++;
        pageToken = next_token[counter];
        String url = getUrl(latitude, longitude, "restaurant|food");
        mService.getNearByPlaces(url).enqueue(new Callback<MyPlaces>() {
            @Override
            public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                currentPlace = response.body();
                final List<Nearby> nearbyList = new ArrayList<>();

                if (response.isSuccessful()) {

                    if (response.body().getNext_page_token() != null) {
                        next_token[counter + 1] = response.body().getNext_page_token();
                        mPageRight.setVisibility(View.VISIBLE);
                    } else {
                        mPageRight.setVisibility(View.INVISIBLE);
                    }

                    firebaseCounter = response.body().getResults().length;
                    firebaseRecorder = response.body().getResults().length;

                    for (int i = 0; i < response.body().getResults().length; i++) {

                        Results googlePlace = response.body().getResults()[i];

                        MarkerOptions markerOptions = new MarkerOptions();
                        double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                        double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());

                        String placeName = googlePlace.getName();
                        String review = "";
                        if (googlePlace.getRating() != null && !TextUtils.isEmpty(googlePlace.getRating())) {
                            review = googlePlace.getRating();
                        }
                        String photo_reference = "";
                        if (googlePlace.getPhotos() != null && googlePlace.getPhotos().length > 0) {
                            photo_reference = getPhotoOfPlace(googlePlace.getPhotos()[0].getPhoto_reference(), 1000);
                        }

                        LatLng latLng = new LatLng(lat, lng);

                        markerOptions.position(latLng);
                        markerOptions.snippet(String.valueOf(i));

                        String isOpening = "";

                        if (googlePlace.getOpening_hours() != null) {
                            if (googlePlace.getOpening_hours().getOpen_now().equals("true")) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_open));
                                isOpening = "Open";
                            } else {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_close));
                                isOpening = "Close";
                            }
                        } else {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_undefined));
                            isOpening = "Unidentified";
                        }


                        mMap.addMarker(markerOptions);
                        Nearby nearby = new Nearby(placeName, review, photo_reference, isOpening, latLng);


                        nearbyList.add(nearby);

                    }

                    mDatabase.child("restaurant2").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (nearbyList.size() > firebaseRecorder) {
                                nearbyList.subList(firebaseRecorder, nearbyList.size()).clear();
                            }

                            for (DataSnapshot data : dataSnapshot.getChildren()) {

                                Firebase_Restaurant restaurant = data.getValue(Firebase_Restaurant.class);

                                LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.snippet(String.valueOf(firebaseCounter));
                                markerOptions.title(data.getKey());
                                firebaseCounter++;

                                Location locA = new Location("Point A");
                                locA.setLongitude(longitude);
                                locA.setLatitude(latitude);

                                Location locB = new Location("Point B");
                                locB.setLongitude(restaurant.getLongitude());
                                locB.setLatitude(restaurant.getLatitude());

                                if (locB.distanceTo(locA) <= 500) {

                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                                    String isOpening = "";
                                    try {

                                        Date d = new Date();
                                        String curr = sdf.format(d);
                                        Date d0 = sdf.parse(curr);
                                        Date d1 = sdf.parse(restaurant.getOpentime());
                                        Date d2 = sdf.parse(restaurant.getClosetime());

                                        if (d0.after(d1) && d0.before(d2)) {
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_open));
                                            isOpening = "Open";
                                        } else {
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_close));
                                            isOpening = "Close";
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    int rating = 0;

                                    if (restaurant.getRatings() != 0) {
                                        rating = restaurant.getRatings();
                                    }

                                    mMap.addMarker(markerOptions);

                                    List<String> image = restaurant.getImage();
                                    //Later edit
                                    Nearby nearby = new Nearby(restaurant.getName(), String.valueOf(rating), image.get(0), isOpening, latLng);
                                    nearbyList.add(nearby);
                                }


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    listener.passList(nearbyList);
                }
            }

            @Override
            public void onFailure(Call<MyPlaces> call, Throwable t) {

            }
        });

        if (counter == 0) {
            mPageLeft.setVisibility(View.INVISIBLE);
        } else {
            mPageLeft.setVisibility(View.VISIBLE);
        }
    }


    /*private void writeNewRestaurant(Double latitude, Double longitude) {
        Firebase_Restaurant restaurant = new Firebase_Restaurant(latitude, longitude, "", 1, "", "", "","");
        String key = mDatabase.child("restaurant").push().getKey();
        mDatabase.child("restaurant").child(key).setValue(restaurant);
    }*/

    private void init() {

        mSearchText.setOnItemClickListener(mAutoCompleteClickListener);

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGeoDataClient, LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(placeAutocompleteAdapter);


        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        mPageLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPrevPage();
            }
        });

        mPageRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNextPage();
            }
        });

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                    hideKeyboardFrom(getContext(), getView());
                    mSearchText.dismissDropDown();
                }
                return false;
            }
        });

    }

    private AdapterView.OnItemClickListener mAutoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            hideKeyboardFrom(getContext(), getView());

            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Toast.makeText(getActivity(), "Place query did not complete successfully: " + places.getStatus().toString(), Toast.LENGTH_LONG).show();
                places.release();
                return;
            }
            final Place place = places.get(0);


            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            latitude = place.getViewport().getCenter().latitude;
            longitude = place.getViewport().getCenter().longitude;

            LatLng latLng = new LatLng(latitude, longitude);

            //MarkerOptions markerOptions = new MarkerOptions();
            //markerOptions.position(latLng);
            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            //markerOptions.title("Search location");

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.5f));
            //mCurrLocationMarker = mMap.addMarker(markerOptions);

            places.release();

        }
    };

    private void geoLocate() {
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (Exception e) {

            Log.e("GEOLOCATION", e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            /*if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }*/

            latitude = address.getLatitude();
            longitude = address.getLongitude();

            LatLng latLng = new LatLng(latitude, longitude);

            //MarkerOptions markerOptions = new MarkerOptions();
            //markerOptions.position(latLng);
            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            //markerOptions.title("Search location");

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.5f));
            //mCurrLocationMarker = mMap.addMarker(markerOptions);
        }

    }


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void getDeviceLocation() {


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                final Task location = mFusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            final Location currentLocation = (Location) task.getResult();

                            latitude = currentLocation.getLatitude();
                            longitude = currentLocation.getLongitude();

                            LatLng latLng = new LatLng(latitude, longitude);

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.5f));


                        } else {

                            Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

        } catch (SecurityException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PageOneListener) {
            listener = (PageOneListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PageOneListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
