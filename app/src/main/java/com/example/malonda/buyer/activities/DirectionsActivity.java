package com.example.malonda.buyer.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.malonda.R;
import com.example.malonda.maphelpers.TaskLoadedCallback;
import com.example.malonda.room.AppDatabase;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mrntlu.toastie.Toastie;

public class DirectionsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;

    Double customer_lat, customer_lng, restaurant_lat, restaurant_lng;
    String business_name = "", phone = "";

    int business_id = -1;

    AppDatabase room_db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        room_db = AppDatabase.getDbInstance(this);

        //get coordinates
        //check received any data
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            Intent intent = getIntent();
            if (extras != null) {
                if (intent.hasExtra("customer_lat") && intent.hasExtra("customer_lng") && intent.hasExtra("restaurant_lat") && intent.hasExtra("restaurant_lng")) {
                    customer_lat = Double.valueOf(getIntent().getStringExtra("customer_lat"));
                    customer_lng = Double.valueOf(intent.getStringExtra("customer_lng"));
                    restaurant_lat = Double.valueOf(getIntent().getStringExtra("restaurant_lat"));
                    restaurant_lng = Double.valueOf(getIntent().getStringExtra("restaurant_lng"));
                    business_name = getIntent().getStringExtra("business");
                    business_id = getIntent().getIntExtra("business_id", -1);
                } else {
                    Toastie.allCustom(this)
                            .setTypeFace(Typeface.DEFAULT_BOLD)
                            .setTextSize(16)
                            .setCardRadius(25)
                            .setCardElevation(10)
                            .setIcon(R.drawable.ic_error_black_24dp)
                            .setCardBackgroundColor(R.color.red)
                            .setMessage("nothing here")
                            .setGravity(Gravity.BOTTOM, 5, 5)
                            .createToast(Toast.LENGTH_LONG)
                            .show();
                    finish();
                }
            } else {
                Toastie.allCustom(this)
                        .setTypeFace(Typeface.DEFAULT_BOLD)
                        .setTextSize(16)
                        .setCardRadius(25)
                        .setCardElevation(10)
                        .setIcon(R.drawable.ic_error_black_24dp)
                        .setCardBackgroundColor(R.color.red)
                        .setMessage("nothing here")
                        .setGravity(Gravity.BOTTOM, 5, 5)
                        .createToast(Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
        //27.658143,85.3199503
        //27.667491,85.3208583
        Log.e("dest", String.valueOf(restaurant_lng));
        place2 = new MarkerOptions().position(new LatLng(restaurant_lat, restaurant_lng)).title(business_name + " Location");
        place1 = new MarkerOptions().position(new LatLng(customer_lat, customer_lng)).title("My Location");


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        Log.d("mylog", "Added Markers");
        mMap.addMarker(place1);
        mMap.addMarker(place2);

//        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(restaurant_lat, restaurant_lng)).zoom(14.0f).build();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(restaurant_lat, restaurant_lng)).zoom(15.0f).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }


    public void closeMap(View view) {
        finish();
    }

    public void goback(View view) {
        onBackPressed();
    }

    public void directionGo(View view) {
//        new FetchURL(DirectionsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
        Uri navigation = Uri.parse("google.navigation:q=" + restaurant_lat + "," + restaurant_lng + "");
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
        navigationIntent.setPackage("com.google.android.apps.maps");
        startActivity(navigationIntent);
    }

    public void callBusiness(View view) {
        phone = room_db.businessInfoDao().findByBusinessInfoId(business_id).getBusiness_phone();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        final int CALL_PERMISSION = 1001;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            } else {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION);
            }
        } else {
            callIntent.setData(Uri.parse("tel:" + phone));
            startActivity(callIntent);

        }
    }
}