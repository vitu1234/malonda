package com.example.malonda.supplier.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.malonda.R;
import com.example.malonda.common.SettingsProfileActivity;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.example.malonda.supplier.fragments.SupplierDashboardFragment;

public class SupplierDashboardActivity extends AppCompatActivity {
    LinearLayout contentView;
    SharedPrefManager sharedPrefManagera;
    BottomNavigationBar bottomNavigationBar;
    AppDatabase room_db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_dashboard);

        room_db = AppDatabase.getDbInstance(this);
//        sharedPrefManagera = new SharedPrefManager(getApplicationContext());

        contentView = findViewById(R.id.content);
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .setActiveColor(R.color.purple_500)
                .setInActiveColor(R.color.purple_700)
                .setBarBackgroundColor(R.color.white);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.home_icon, "Home"))
                .addItem(new BottomNavigationItem(R.drawable.help_icon, "Help"))
                .addItem(new BottomNavigationItem(R.drawable.settings_icon, "Settings"))
                .setFirstSelectedPosition(0)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                if (position == 0) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SupplierDashboardFragment(), null).commit();
                } else if (position == 1) {
//                    displayFragment(new ProductsMainFragment());
                } else if (position == 2) {
                    startActivity(new Intent(SupplierDashboardActivity.this, SettingsProfileActivity.class));
                } else if (position == 3) {
//                    startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
                } else if (position == 4) {
//                    logoutUser();
                }


            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });
    }
/*
    private void logoutUser() {
        sharedPrefManagera.logoutUser();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }*/

    //hooking fragments
    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, null).addToBackStack(null).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().beginTransaction().remove(new SupplierDashboardFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SupplierDashboardFragment(), null).commit();
        bottomNavigationBar.selectTab(0);
    }
}