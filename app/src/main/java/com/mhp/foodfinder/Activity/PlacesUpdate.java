package com.mhp.foodfinder.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.mhp.foodfinder.Adapter.ItemPagerAdapter;
import com.mhp.foodfinder.Adapter.PlaceAutocompleteAdapter;
import com.mhp.foodfinder.Model.CurrentReview_Firebase;
import com.mhp.foodfinder.Model.Firebase_Restaurant;
import com.mhp.foodfinder.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class PlacesUpdate extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private AutoCompleteTextView mSearchText;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private GeoDataClient mGeoDataClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );
    double lat, lng = 0;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private EditText name, open_time, close_time,averagePrice,description,website,foodStyle,editTextCarrierNumber;
    private CountryCodePicker ccp;
    private TimePickerDialog timePickerDialog;
    private String amPm;

    private Button img_select, menu_img_select;
    private FloatingTextButton update_btn;
    private TextView img_identifier, menu_img_identifier;
    //private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private final int PICK_IMAGEMENU_REQUEST = 22;

    //private Uri img_url;
    private String key;
    List<String> img_url_fire = new ArrayList<>();
    List<String> menu_url_fire = new ArrayList<>();
    private FirebaseUser user;

    private ViewPager res_viewPager, menu_viewPager;
    private ItemPagerAdapter adapter;

    String[] res_filePath, menu_filePath, img_transfer, menu_transfer= null;
    int ratings;
    List<CurrentReview_Firebase> reviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_update);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");


        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);

        name = (EditText) findViewById(R.id.name_txt);
        open_time = (EditText) findViewById(R.id.open_time);
        close_time = (EditText) findViewById(R.id.close_time);
        update_btn = (FloatingTextButton) findViewById(R.id.update_btn);

        averagePrice=(EditText)findViewById(R.id.averagePrice);
        description=(EditText)findViewById(R.id.descriptionText);
        website=(EditText)findViewById(R.id.website_text);
        foodStyle=(EditText)findViewById(R.id.foodStyle);
        editTextCarrierNumber=(EditText)findViewById(R.id.editText_carrierNumber);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        open_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timePickerDialog = new TimePickerDialog(PlacesUpdate.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        if(i>=12){
                            if(i!=12) {
                                amPm = "PM";
                                i = i - 12;
                            }else{
                                amPm = "PM";
                            }
                        }else{
                            if(i==0){
                                i=i+12;
                                amPm = "AM";
                            }else {
                                amPm = "AM";
                            }
                        }
                        open_time.setText(String.format("%02d:%02d",i,i1)+" "+amPm);
                        //open_time.setText(i+":"+i1+" "+amPm);
                    }
                },12,0,false);
                timePickerDialog.show();
            }
        });

        close_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog = new TimePickerDialog(PlacesUpdate.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        if(i>=12){
                            if(i!=12) {
                                amPm = "PM";
                                i = i - 12;
                            }else{
                                amPm = "PM";
                            }
                        }else{
                            if(i==0){
                                i=i+12;
                                amPm = "AM";
                            }else {
                                amPm = "AM";
                            }
                        }
                        close_time.setText(String.format("%02d:%02d",i,i1)+" "+amPm);
                    }
                },12,0,false);
                timePickerDialog.show();
            }
        });

        img_select = (Button) findViewById(R.id.img_select);
        img_identifier = (TextView) findViewById(R.id.img_identifier);

        menu_img_select = (Button) findViewById(R.id.menu_img_select);
        menu_img_identifier = (TextView) findViewById(R.id.menu_img_identifier);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();

        res_viewPager = (ViewPager) findViewById(R.id.res_viewPager);
        menu_viewPager = (ViewPager) findViewById(R.id.menu_viewPager);

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateRestaurant();
            }
        });

        img_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        menu_img_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseMenuImage();
            }
        });


        //name, open_time, close_time, ratings,lat,lng,img_url_fire

        mDatabase.child("restaurant2").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Firebase_Restaurant restaurant = dataSnapshot.getValue(Firebase_Restaurant.class);

                name.setText(restaurant.getName());
                mSearchText.setText(restaurant.getAddress());
                open_time.setText(restaurant.getOpentime());
                close_time.setText(restaurant.getClosetime());
                averagePrice.setText(restaurant.getAveragePrice());
                description.setText(restaurant.getDescription());
                website.setText(restaurant.getWebsite());
                foodStyle.setText(restaurant.getFoodStyle());
                ccp.registerCarrierNumberEditText(editTextCarrierNumber);
                ccp.setFullNumber(restaurant.getContact());
                //ratings.setText(String.valueOf(restaurant.getRatings()));
                lat = restaurant.getLatitude();
                lng = restaurant.getLongitude();

                if(restaurant.getImage() !=null) {
                    img_url_fire = restaurant.getImage();
                    img_transfer = new String[img_url_fire.size()];
                    for (int i = 0; i < img_url_fire.size(); i++) {
                        img_transfer[i] = img_url_fire.get(i);
                    }
                    adapter = new ItemPagerAdapter(getApplicationContext(), img_transfer);
                    res_viewPager.setAdapter(adapter);
                    res_viewPager.setVisibility(View.VISIBLE);
                    if(img_url_fire.size()>1) {
                        img_identifier.setText("Images are selected");
                    }else{
                        img_identifier.setText("Image is selected");
                    }
                }

                if(restaurant.getMenu() != null) {
                    menu_url_fire = restaurant.getMenu();
                    menu_transfer = new String[menu_url_fire.size()];
                    for (int i = 0; i < menu_url_fire.size(); i++) {
                        menu_transfer[i] = menu_url_fire.get(i);
                    }

                    adapter = new ItemPagerAdapter(getApplicationContext(), menu_transfer);
                    menu_viewPager.setAdapter(adapter);
                    menu_viewPager.setVisibility(View.VISIBLE);
                    if(menu_url_fire.size()>1) {
                        menu_img_identifier.setText("Images are selected");
                    }else{
                        menu_img_identifier.setText("Image is selected");
                    }
                }

                if(restaurant.getRatings()!=0){
                    ratings = restaurant.getRatings();
                }else{
                    ratings = 0;
                }

                if(restaurant.getReviews()!=null){
                    reviews = restaurant.getReviews();
                }else{
                    reviews = null;
                }


                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    markerOptions.title("Search location");
                    mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.5f));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        init();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void chooseMenuImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Menu Picture"), PICK_IMAGEMENU_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {
                res_filePath = new String[data.getClipData().getItemCount()];

                for (int i = 0; i < data.getClipData().getItemCount(); i++) {

                    res_filePath[i] = data.getClipData().getItemAt(i).getUri().toString();
                    //Toast.makeText(getApplicationContext(),filePath[i],Toast.LENGTH_SHORT).show();

                }

                adapter = new ItemPagerAdapter(getApplicationContext(), res_filePath);
                res_viewPager.setAdapter(adapter);

                img_identifier.setText("Images are selected");
                res_viewPager.setVisibility(View.VISIBLE);


            } else if (data.getData() != null) {
                res_filePath = new String[1];
                res_filePath[0] = data.getData().toString();

                adapter = new ItemPagerAdapter(getApplicationContext(), res_filePath);
                res_viewPager.setAdapter(adapter);

                img_identifier.setText("Image is selected");
                res_viewPager.setVisibility(View.VISIBLE);

            }

        } else if (requestCode == PICK_IMAGEMENU_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {

                menu_filePath = new String[data.getClipData().getItemCount()];

                for (int i = 0; i < data.getClipData().getItemCount(); i++) {

                    menu_filePath[i] = data.getClipData().getItemAt(i).getUri().toString();
                    //Toast.makeText(getApplicationContext(),filePath[i],Toast.LENGTH_SHORT).show();

                }

                adapter = new ItemPagerAdapter(getApplicationContext(), menu_filePath);
                menu_viewPager.setAdapter(adapter);

                menu_img_identifier.setText("Images are selected");
                menu_viewPager.setVisibility(View.VISIBLE);


            } else if (data.getData() != null) {
                menu_filePath = new String[1];
                menu_filePath[0] = data.getData().toString();

                adapter = new ItemPagerAdapter(getApplicationContext(), menu_filePath);
                menu_viewPager.setAdapter(adapter);

                menu_img_identifier.setText("Image is selected");
                menu_viewPager.setVisibility(View.VISIBLE);

            }
        }
    }

    private void init() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mGeoDataClient = Places.getGeoDataClient(getApplicationContext(), null);
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getApplicationContext(), mGeoDataClient, LAT_LNG_BOUNDS, null);

        mSearchText.setOnItemClickListener(mAutoCompleteClickListener);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_SEARCH || i == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                    mSearchText.dismissDropDown();

                }
                return false;
            }
        });
        hideSoftKeyboard();
    }

    private void geoLocate() {
        mMap.clear();
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(PlacesUpdate.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d("geoLocate: ", e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d("geoLocate: ", address.toString());
            lat = address.getLatitude();
            lng = address.getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("Search Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.5f));
            hideSoftKeyboard();
        }
    }

    private AdapterView.OnItemClickListener mAutoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            mMap.clear();
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Toast.makeText(getApplicationContext(), "Place query did not complete successfully: " + places.getStatus().toString(), Toast.LENGTH_LONG).show();
                places.release();
                return;
            }
            final Place place = places.get(0);

            lat = place.getViewport().getCenter().latitude;
            lng = place.getViewport().getCenter().longitude;

            LatLng latLng = new LatLng(lat, lng);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            markerOptions.title("Search location");
            mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.5f));

            places.release();

        }
    };

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
    }

    private void updateRestaurant(Double longitude, Double latitude, String name, List<String> image, String closetime, String opentime, String addByUser, List<String> menu, String averagePrice, String contact, String description, String foodStyle, String website, String address, int ratings, List<CurrentReview_Firebase>review) {
        Firebase_Restaurant restaurant = new Firebase_Restaurant(longitude, latitude, name, image, closetime, opentime, addByUser, menu, averagePrice,contact,description,foodStyle,website,address,ratings,review);
        mDatabase.child("restaurant2").child(key).setValue(restaurant);
        //Toast.makeText(getApplicationContext(), "Update successfully", Toast.LENGTH_SHORT).show();
    }

    private void UpdateRestaurant() {

        try {
            final String name_temp = name.getText().toString().trim();
            final String open_time_temp = open_time.getText().toString().trim();
            final String close_time_temp = close_time.getText().toString().trim();
            final String address_temp = mSearchText.getText().toString().trim();
            final String description_temp = description.getText().toString().trim();
            final String website_temp = website.getText().toString().trim();
            final String foodStyle_temp = foodStyle.getText().toString().trim();
            final String averagePrice_temp = averagePrice.getText().toString().trim();
            final String contact_temp = editTextCarrierNumber.getText().toString().trim();


            if (name_temp.isEmpty()) {
                name.setError("Name is required!!");
                name.requestFocus();
                return;
            }

            if (lat == 0 && lng == 0 && address_temp.isEmpty()) {
                mSearchText.setError("Location is required");
                mSearchText.requestFocus();
                return;
            }

            if(contact_temp.isEmpty()){
                editTextCarrierNumber.setError("Contact number is required!!");
                editTextCarrierNumber.requestFocus();
                return;
            }

            if(!ccp.isValidFullNumber()){
                editTextCarrierNumber.setError("Contact number is not valid!!");
                editTextCarrierNumber.requestFocus();
                return;
            }

            if(!website_temp.isEmpty()){
                if(!URLUtil.isHttpUrl(website_temp) || !Patterns.WEB_URL.matcher(website_temp).matches()){
                    website.setError("URL provided is not valid!!");
                    website.requestFocus();
                    return;
                }
            }

            if (open_time_temp.isEmpty()) {
                open_time.setError("Open time is required!!");
                open_time.requestFocus();
                return;
            }

            if (close_time_temp.isEmpty()) {
                close_time.setError("Close time is required!!");
                close_time.requestFocus();
                return;
            }


            if (res_filePath != null && menu_filePath != null) {

                final AlertDialog waitingDialog = new SpotsDialog(PlacesUpdate.this);
                waitingDialog.show();
                waitingDialog.setMessage("Updating....");

                img_url_fire.clear();
                menu_url_fire.clear();

                if(menu_transfer!=null) {
                    for (int i = 0; i < menu_transfer.length; i++) {
                        StorageReference imageRef = mStorage.getReferenceFromUrl(menu_transfer[i]);
                        imageRef.delete();
                    }
                }

                if(img_transfer!=null) {
                    for (int i = 0; i < img_transfer.length; i++) {
                        StorageReference imageRef = mStorage.getReferenceFromUrl(img_transfer[i]);
                        imageRef.delete();
                    }
                }

                for(int i=0;i<res_filePath.length;i++) {
                    final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
                    Uri file = Uri.parse(res_filePath[i]);
                    ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            img_url_fire.add(taskSnapshot.getDownloadUrl().toString());
                            //Later edit
                            updateRestaurant(lng, lat, name_temp,img_url_fire,close_time_temp,open_time_temp,user.getUid(), menu_url_fire, averagePrice_temp,ccp.getFullNumberWithPlus(),description_temp,foodStyle_temp,website_temp,address_temp,ratings,reviews);

                            if(img_url_fire.size() == res_filePath.length) {
                                for (int i = 0; i < menu_filePath.length; i++) {
                                    final StorageReference ref = storageReference.child("menus/" + UUID.randomUUID().toString());
                                    Uri file = Uri.parse(menu_filePath[i]);
                                    ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            menu_url_fire.add(taskSnapshot.getDownloadUrl().toString());
                                            updateRestaurant(lng, lat, name_temp,img_url_fire,close_time_temp,open_time_temp,user.getUid(), menu_url_fire, averagePrice_temp,ccp.getFullNumberWithPlus(),description_temp,foodStyle_temp,website_temp,address_temp,ratings,reviews);

                                            if (menu_url_fire.size() == menu_filePath.length) {
                                                Toast.makeText(getApplicationContext(), "Update successfully", Toast.LENGTH_SHORT).show();
                                                waitingDialog.dismiss();
                                                finish();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            waitingDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Update fail" + e.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Update fail" + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } else if (res_filePath != null) {

                img_url_fire.clear();

                if(img_transfer!=null) {
                    for (int i = 0; i < img_transfer.length; i++) {
                        StorageReference imageRef = mStorage.getReferenceFromUrl(img_transfer[i]);
                        imageRef.delete();
                    }
                }


                final AlertDialog waitingDialog = new SpotsDialog(PlacesUpdate.this);
                waitingDialog.show();
                waitingDialog.setMessage("Updating....");

                for(int i=0;i<res_filePath.length;i++) {
                    final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
                    Uri file = Uri.parse(res_filePath[i]);
                    ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            img_url_fire.add(taskSnapshot.getDownloadUrl().toString());
                            //Later edit
                            updateRestaurant(lng, lat, name_temp,img_url_fire,close_time_temp,open_time_temp,user.getUid(), menu_url_fire, averagePrice_temp,ccp.getFullNumberWithPlus(),description_temp,foodStyle_temp,website_temp,address_temp,ratings,reviews);

                            if(img_url_fire.size() == res_filePath.length) {
                                Toast.makeText(getApplicationContext(), "Update successfully", Toast.LENGTH_SHORT).show();
                                waitingDialog.dismiss();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Update fail" + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } else if (menu_filePath != null) {

                menu_url_fire.clear();

                if(menu_transfer!=null) {
                    for (int i = 0; i < menu_transfer.length; i++) {
                        StorageReference imageRef = mStorage.getReferenceFromUrl(menu_transfer[i]);
                        imageRef.delete();
                    }
                }


                final AlertDialog waitingDialog = new SpotsDialog(PlacesUpdate.this);
                waitingDialog.show();
                waitingDialog.setMessage("Updating....");

                for(int i=0;i<menu_filePath.length;i++) {
                    final StorageReference ref = storageReference.child("menus/" + UUID.randomUUID().toString());
                    Uri file = Uri.parse(menu_filePath[i]);
                    ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            menu_url_fire.add(taskSnapshot.getDownloadUrl().toString());
                            //Later edit
                            updateRestaurant(lng, lat, name_temp,img_url_fire,close_time_temp,open_time_temp,user.getUid(), menu_url_fire, averagePrice_temp,ccp.getFullNumberWithPlus(),description_temp,foodStyle_temp,website_temp,address_temp,ratings,reviews);

                            if(menu_url_fire.size() == menu_filePath.length) {
                                Toast.makeText(getApplicationContext(), "Update successfully", Toast.LENGTH_SHORT).show();
                                waitingDialog.dismiss();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Update fail" + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                //Later edit
                updateRestaurant(lng, lat, name_temp,img_url_fire,close_time_temp,open_time_temp,user.getUid(), menu_url_fire, averagePrice_temp,ccp.getFullNumberWithPlus(),description_temp,foodStyle_temp,website_temp,address_temp,ratings,reviews);
                Toast.makeText(getApplicationContext(), "Update successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Update fail" + e.toString(), Toast.LENGTH_LONG).show();
        }

    }
}
