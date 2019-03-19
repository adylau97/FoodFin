package com.mhp.foodfinder.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mhp.foodfinder.Model.DirectionJSONParser;
import com.mhp.foodfinder.R;
import com.mhp.foodfinder.Remote.Common;
import com.mhp.foodfinder.Remote.IGoogleAPIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewDirection extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    Location mLastLocation;
    Marker mCurrentMarker;
    Polyline polyline;

    IGoogleAPIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_direction);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mService = Common.getGoogleAPIServiceScalars();
        buildLocationRequest();
        buildLocationCallBack();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mLastLocation = locationResult.getLastLocation();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
                        .title("Your location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrentMarker = mMap.addMarker(markerOptions);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()), 19.5f));

                //Marker for destination
                LatLng destinationLatLng = new LatLng(Double.parseDouble(Common.currentResult.getGeometry().getLocation().getLat()),Double.parseDouble(Common.currentResult.getGeometry().getLocation().getLng()));

                mMap.addMarker(new MarkerOptions()
                        .position(destinationLatLng)
                        .title(Common.currentResult.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                drawPath(mLastLocation,Common.currentResult.getGeometry().getLocation());
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mLastLocation = location;
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
                        .title("Your location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrentMarker = mMap.addMarker(markerOptions);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()), 19.5f));

                //Marker for destination
                LatLng destinationLatLng = new LatLng(Double.parseDouble(Common.currentResult.getGeometry().getLocation().getLat()),Double.parseDouble(Common.currentResult.getGeometry().getLocation().getLng()));

                mMap.addMarker(new MarkerOptions()
                        .position(destinationLatLng)
                        .title(Common.currentResult.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                drawPath(mLastLocation,Common.currentResult.getGeometry().getLocation());
            }
        });
    }

    private void drawPath(Location mLastLocation, com.mhp.foodfinder.Model.Location location){
        if(polyline!=null){
            polyline.remove();
        }

        final String origin = new StringBuilder(String.valueOf(mLastLocation.getLatitude()))
                .append(",")
                .append(String.valueOf(mLastLocation.getLongitude()))
                .toString();

        final String destination = new StringBuilder(location.getLat())
                .append(",")
                .append(location.getLng())
                .toString();

        String key = getResources().getString(R.string.browser_key2);

        mService.getDirection(origin,destination,key).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    new ParserTask().execute(response.body());
                    Log.d("direction: ", origin + "," + destination);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("direction: ", "fail");
            }
        });
    }

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>{
        AlertDialog waitingDialog = new SpotsDialog(ViewDirection.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waitingDialog.show();
            waitingDialog.setMessage("In progress....");
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);

            ArrayList points = new ArrayList();
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(12);
            polylineOptions.color(Color.RED);
            polylineOptions.geodesic(true);


            for(int i=0; i<lists.size();i++){

                List<HashMap<String, String>> path = lists.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng position = new LatLng(lat,lng);
                    points.add(position);
                }
                polylineOptions.addAll(points);
            }
                polyline = mMap.addPolyline(polylineOptions);
                waitingDialog.dismiss();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jsonObject = new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();
                routes = parser.parse(jsonObject);
                Log.d("direction: ", jsonObject.toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
            return routes;
        }
    }
}
