package com.mhp.foodfinder.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.mhp.foodfinder.Adapter.ItemPagerAdapter;
import com.mhp.foodfinder.Adapter.PlaceAutocompleteAdapter;
import com.mhp.foodfinder.Fragment.PageOne;
import com.mhp.foodfinder.Model.CurrentReview_Firebase;
import com.mhp.foodfinder.Model.Firebase_Restaurant;
import com.mhp.foodfinder.Model.PlaceDetail;
import com.mhp.foodfinder.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class PlacesInsert extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

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
    CountryCodePicker ccp;
    TimePickerDialog timePickerDialog;
    String amPm;

    private Button img_select, menu_img_select;
    private FloatingTextButton create_btn;
    private TextView img_identifier, menu_img_identifier;
    //private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private final int PICK_IMAGEMENU_REQUEST = 22;

    //private Uri img_url;
    String[] res_filePath, menu_filePath = null;
    List<String> res_img_url = new ArrayList<>();
    List<String> menu_img_url = new ArrayList<>();
    String key;

    private ViewPager res_viewPager, menu_viewPager;
    private ItemPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_insert);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);

        name = (EditText) findViewById(R.id.name_txt);
        open_time = (EditText) findViewById(R.id.open_time);
        close_time = (EditText) findViewById(R.id.close_time);
        create_btn = (FloatingTextButton) findViewById(R.id.create_btn);

        averagePrice=(EditText)findViewById(R.id.averagePrice);
        description=(EditText)findViewById(R.id.descriptionText);
        website=(EditText)findViewById(R.id.website_text);
        foodStyle=(EditText)findViewById(R.id.foodStyle);
        editTextCarrierNumber=(EditText)findViewById(R.id.editText_carrierNumber);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);

        open_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timePickerDialog = new TimePickerDialog(PlacesInsert.this, new TimePickerDialog.OnTimeSetListener() {
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
                timePickerDialog = new TimePickerDialog(PlacesInsert.this, new TimePickerDialog.OnTimeSetListener() {
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
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();

        key = mDatabase.child("restaurant2").push().getKey();

        res_viewPager = (ViewPager) findViewById(R.id.res_viewPager);
        menu_viewPager = (ViewPager) findViewById(R.id.menu_viewPager);

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateRestaurant();
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
    }


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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK /*&& data != null && data.getData() != null*/) {

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
        Geocoder geocoder = new Geocoder(PlacesInsert.this);
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
        imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
    }

    private void writeNewRestaurant(Double longitude, Double latitude, String name, List<String> image, String closetime, String opentime, String addByUser, List<String> menu, String averagePrice, String contact, String description, String foodStyle, String website, String address, int ratings, List<CurrentReview_Firebase> review) {
        Firebase_Restaurant restaurant = new Firebase_Restaurant(longitude, latitude, name, image, closetime, opentime, addByUser, menu, averagePrice,contact,description,foodStyle,website,address,ratings,review);
        //later edit
        mDatabase.child("restaurant2").child(key).setValue(restaurant);
    }

    private void CreateRestaurant() {

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

            ccp.registerCarrierNumberEditText(editTextCarrierNumber);

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


            if (res_filePath == null) {
                img_identifier.setText("No image is selected");
                img_identifier.requestFocus();
                return;
            } else {
                final AlertDialog waitingDialog = new SpotsDialog(PlacesInsert.this);
                waitingDialog.show();
                waitingDialog.setMessage("Creating....");

                //menu_img_url.add("null");

                for (int i = 0; i < res_filePath.length; i++) {
                    final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
                    Uri file = Uri.parse(res_filePath[i]);
                    ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            res_img_url.add(taskSnapshot.getDownloadUrl().toString());

                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewRestaurant(lng, lat, name_temp, res_img_url,close_time_temp,open_time_temp,user.getUid(), menu_img_url,averagePrice_temp , ccp.getFullNumberWithPlus(),description_temp,foodStyle_temp,website_temp,address_temp,0,null);


                            if (res_img_url.size() == res_filePath.length && menu_filePath!=null) {

                                //menu_img_url.clear();

                                for (int i = 0; i < menu_filePath.length; i++) {
                                    final StorageReference ref = storageReference.child("menus/" + UUID.randomUUID().toString());
                                    Uri file = Uri.parse(menu_filePath[i]);
                                    ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            menu_img_url.add(taskSnapshot.getDownloadUrl().toString());
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            writeNewRestaurant(lng, lat, name_temp, res_img_url,close_time_temp,open_time_temp,user.getUid(), menu_img_url,averagePrice_temp , ccp.getFullNumberWithPlus(),description_temp,foodStyle_temp,website_temp,address_temp,0,null);

                                            if (menu_img_url.size() == menu_filePath.length) {
                                                Toast.makeText(getApplicationContext(), "Create successfully", Toast.LENGTH_SHORT).show();
                                                waitingDialog.dismiss();
                                                finish();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            waitingDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Create fail" + e.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }


                            }
                            else if (res_img_url.size() == res_filePath.length && menu_filePath==null){
                                Toast.makeText(getApplicationContext(), "Create successfully", Toast.LENGTH_SHORT).show();
                                waitingDialog.dismiss();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Create fail" + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                }


            }


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Create fail" + e.toString(), Toast.LENGTH_LONG).show();
        }

    }

}
