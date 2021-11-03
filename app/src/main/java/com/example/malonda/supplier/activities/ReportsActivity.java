
package com.example.malonda.supplier.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.malonda.R;
import com.example.malonda.models.Sale;
import com.example.malonda.room.AppDatabase;
import com.example.malonda.storage.SharedPrefManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {
    private BarChart chart;

    TextView textViewFilterValue;
    AlertDialog alertDialog;
    LinearLayout linearLayoutCustom;
    RadioGroup radioGroupOptions;
    EditText editTextDate1, editTextDate2;

    AppDatabase room_db;

    List<Integer> numArr = new ArrayList<>();
    List<String> xValues = new ArrayList<>();
    List<Sale> saleList;
    BarData data;
    int user_id = -1;


    final Context context = this;
    String today = null, yesterday = null, tomorrow = null, sevenDaysAgo = null, thirtyDaysAgo = null, filter_date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);


        chart = findViewById(R.id.chart1);
        textViewFilterValue = findViewById(R.id.filteDateTXT);

        room_db = AppDatabase.getDbInstance(this);

        user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();


        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

        today = dateFormat.format(cal.getTime());

        cal.add(Calendar.DATE, +1);
        tomorrow = dateFormat.format(cal.getTime());

        cal.add(Calendar.DATE, -1);
        yesterday = dateFormat.format(cal.getTime());

        cal.add(Calendar.DATE, -7);
        sevenDaysAgo = dateFormat.format(cal.getTime());

        cal.add(Calendar.DATE, -30);
        thirtyDaysAgo = dateFormat.format(cal.getTime());

        saleList = room_db.saleDao().getAllSalesByBusinessID(user_id);
        plotGraph();

    }

    private void plotGraph() {


    }

    // creating list of x-axis values
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < xValues.size(); i++) {
            labels.add(xValues.get(i));
        }

        return labels;
    }

    // this method is used to create data for Bar graph
    public BarData barData() {
        ArrayList<BarEntry> group1 = new ArrayList<BarEntry>();
        for (int i = 0; i < numArr.size(); i++) {
            group1.add(new BarEntry(i, numArr.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(group1, "Sales Overview");
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setDrawValues(false);


        BarData barData = new BarData(barDataSet);

        return barData;
    }

    public void openSummaryActivity(View view) {
        Intent intent = new Intent(this, SalesSummaryActivity.class);
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply activity transition

            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="robot"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, findViewById(R.id.layoutTrans), "summary");
            // start the new activity
            startActivity(intent, options.toBundle());
        } else {
            // Swap without transition
            startActivity(intent);
        }
    }

    public void openDailySummary(View view) {
        Intent intent = new Intent(this, DailySaleReportActivity.class);
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply activity transition

            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="robot"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, findViewById(R.id.layoutDaily), "dailySummary");
            // start the new activity
            startActivity(intent, options.toBundle());
        } else {
            // Swap without transition
            startActivity(intent);
        }
    }

    public void openFilterDialog(View view) {


    }

}