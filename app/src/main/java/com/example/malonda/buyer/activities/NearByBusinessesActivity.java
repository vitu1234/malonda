package com.example.malonda.buyer.activities;

import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.malonda.R;
import com.example.malonda.adapters.BusinessesNearbyAdapter;
import com.example.malonda.maphelpers.DistanceBetween;
import com.example.malonda.maphelpers.GpsTracker;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.BusinessLocationDistance;
import com.example.malonda.room.AppDatabase;
import com.mrntlu.toastie.Toastie;

import java.util.List;

public class NearByBusinessesActivity extends AppCompatActivity {
    double me_longtude = 0, me_latitude = 0;

    List<BusinessInfo> businessInfoList;
    List<BusinessLocationDistance> businessLocationDistanceList;
    BusinessesNearbyAdapter adapter;

    AppDatabase room_db;
    RecyclerView recyclerViewBusinessListFiltered;
    private GpsTracker gpsTracker;
    DistanceBetween distanceBetween;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_businesses);

        recyclerViewBusinessListFiltered = findViewById(R.id.businessListRecycler);

        room_db = AppDatabase.getDbInstance(this);
        getLocationPermissions();
        gpsTracker = new GpsTracker(this);
        distanceBetween = new DistanceBetween();
        room_db.businessLocationDistanceDao().deleteAllBusinessInfo();


        setViews();
    }


    private void setViews() {

        if (gpsTracker.canGetLocation()) {
            me_latitude = gpsTracker.getLatitude();
            me_longtude = gpsTracker.getLongitude();
            //27.658143,85.3199503
            //27.667491,85.3208583

            if (room_db.businessInfoDao().countAllBusinessInfo() > 0) {
                businessInfoList = room_db.businessInfoDao().getAllBusinessInfo();
                for (int i = 0; i < businessInfoList.size(); i++) {
                    BusinessInfo businessInfo = businessInfoList.get(i);
                    int business_id = businessInfo.getBusiness_id();
                    double latitude = Double.parseDouble(businessInfo.getLatitude()), longtude = Double.parseDouble(businessInfo.getLongtude());
                    double distance = distanceBetween.distance(me_latitude, me_longtude, latitude, longtude);
                    BusinessLocationDistance businessLocationDistance = new BusinessLocationDistance();
                    businessLocationDistance.setBusiness_id(business_id);
                    businessLocationDistance.setKm_from_me(distance);
                    room_db.businessLocationDistanceDao().insertBusinessInfo(businessLocationDistance);
                }

                if (room_db.businessLocationDistanceDao().countAllBusinessInfo() > 0) {
                    businessLocationDistanceList = room_db.businessLocationDistanceDao().getAllBusinessInfoOrderByDistance();
                    setRecylerView();
                } else {
                    //no nearby locations
                    Toastie.allCustom(this)
                            .setTypeFace(Typeface.DEFAULT_BOLD)
                            .setTextSize(16)
                            .setCardRadius(25)
                            .setCardElevation(10)
                            .setIcon(R.drawable.ic_error_black_24dp)
                            .setCardBackgroundColor(R.color.red)
                            .setMessage("No nearby locations, make sure location is enabled!")
                            .setGravity(Gravity.BOTTOM, 5, 5)
                            .createToast(Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                //no business info data found in the database
                Toastie.allCustom(this)
                        .setTypeFace(Typeface.DEFAULT_BOLD)
                        .setTextSize(16)
                        .setCardRadius(25)
                        .setCardElevation(10)
                        .setIcon(R.drawable.ic_error_black_24dp)
                        .setCardBackgroundColor(R.color.red)
                        .setMessage("No business info data found in the database!")
                        .setGravity(Gravity.BOTTOM, 5, 5)
                        .createToast(Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            gpsTracker.showSettingsAlert();
        }

    }

    private void setRecylerView() {

        adapter = new BusinessesNearbyAdapter(this, businessLocationDistanceList);
        recyclerViewBusinessListFiltered.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBusinessListFiltered.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//line between items
        recyclerViewBusinessListFiltered.setAdapter(adapter);

    }

    public void goBackMenu(View view) {
        onBackPressed();
    }

    private void getLocationPermissions() {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}