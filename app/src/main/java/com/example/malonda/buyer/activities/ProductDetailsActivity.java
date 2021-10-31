package com.example.malonda.buyer.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.malonda.R;
import com.example.malonda.api.RetrofitClient;
import com.example.malonda.maphelpers.FetchURL;
import com.example.malonda.maphelpers.TaskLoadedCallback;
import com.example.malonda.models.BusinessInfo;
import com.example.malonda.models.Category;
import com.example.malonda.models.Product;
import com.example.malonda.models.Unit;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.example.malonda.maphelpers.GpsTracker;
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
import com.squareup.picasso.Picasso;

public class ProductDetailsActivity extends AppCompatActivity {
    int product_id = -1, user_id = -1;
    String longtude = "", latitude = "", me_longtude = "",me_latitude = "";
    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    private GpsTracker gpsTracker;
    MapFragment mapFragment;


    AppDatabase room_db;

    ImageView imageViewProduct;
    TextView textViewCategory, textViewPriceUnit, textViewStock, textViewDesc, textViewBusName, textViewBusPhone, textViewBusAddress;
    RelativeLayout relativeLayoutBusDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        imageViewProduct = findViewById(R.id.big_image);
        textViewCategory = findViewById(R.id.prodDetails_prodCategory);
        textViewPriceUnit = findViewById(R.id.prodDetails_prodPriceUnit);
        textViewStock = findViewById(R.id.prodDetails_prodStock);
        textViewDesc = findViewById(R.id.prodDetails_prodDesc);
        textViewBusName = findViewById(R.id.prodDetails_prodBusName);
        textViewBusPhone = findViewById(R.id.prodDetails_prodBusPhone);
        textViewBusAddress = findViewById(R.id.prodDetails_prodAddress);
        relativeLayoutBusDetails = findViewById(R.id.busDetailsLayout);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFrag);


        Intent intent = this.getIntent();
        room_db = AppDatabase.getDbInstance(this);
        gpsTracker = new GpsTracker(this);

        product_id = intent.getIntExtra("product_id", -1);

        getLocationPermissions();

        setViews();
    }

    private void getLocationPermissions() {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setViews() {
        Product product = room_db.productDao().findByProductId(product_id);
        user_id = product.getUser_id();
        Category category = room_db.categoryDao().findByCategoryId(product.getCategory_id());
        Unit unit = room_db.unitDao().findByUnitId(product.getUnit_id());

        textViewCategory.setText("In " + category.getCategory_name());
        textViewPriceUnit.setText("MWK " + product.getPrice() + " - " + unit.getUnit_symbol());
        textViewStock.setText(product.getQty() + " In Stock");
        textViewDesc.setText(product.getDescription()+" ");

        String imageUri = RetrofitClient.BASE_URL2 + "images/products/" + product.getImg_url();

        Picasso.get().load(imageUri)
                .placeholder(R.drawable.image_icon)
                .error(R.drawable.image_icon)
                .into(imageViewProduct);


        if (room_db.businessInfoDao().getSingleBusinessInfoCountByBusinessInfoID(user_id) > 0) {

            BusinessInfo businessInfo = room_db.businessInfoDao().findByBusinessByUserId(user_id);
            relativeLayoutBusDetails.setVisibility(View.VISIBLE);
            textViewBusName.setText(businessInfo.getBusiness_name());
            textViewBusPhone.setText(businessInfo.getBusiness_phone());
            textViewBusAddress.setText(businessInfo.getBusiness_address());
            longtude = String.valueOf(Double.parseDouble(businessInfo.getLongtude()));
            latitude = String.valueOf(Double.parseDouble(businessInfo.getLatitude()));


            if(gpsTracker.canGetLocation()){
                me_latitude = String.valueOf(gpsTracker.getLatitude());
                me_longtude = String.valueOf(gpsTracker.getLongitude());
                //27.658143,85.3199503
                //27.667491,85.3208583

            }else{
                gpsTracker.showSettingsAlert();
            }

        }else{
            relativeLayoutBusDetails.setVisibility(View.GONE);
        }

    }



    public void goback(View view) {
        onBackPressed();
    }

    public void viewDirections(View view) {
        Intent intent= new Intent(this,DirectionsActivity.class);
        String business_name = room_db.businessInfoDao().findByBusinessByUserId(user_id).getBusiness_name();
        intent.putExtra("customer_lat",me_latitude);
        intent.putExtra("customer_lng",me_longtude);
        intent.putExtra("restaurant_lat",latitude);
        intent.putExtra("restaurant_lng",longtude);
        intent.putExtra("business",business_name);
        Log.e("me", String.valueOf(me_latitude));
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}