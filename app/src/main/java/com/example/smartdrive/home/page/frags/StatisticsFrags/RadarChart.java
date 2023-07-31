package com.example.smartdrive.home.page.frags.StatisticsFrags;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartdrive.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class RadarChart extends Fragment {


    //database ref
    FirebaseDatabase database;
    DatabaseReference myRef;


    com.github.mikephil.charting.charts.RadarChart radarChart;

    // Array of days of the week
    String[] daysOfWeek = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};

    // Array of congestion values for each day of the week
    float[] congestion = {10f, 20f, 10f, 30f, 10f, 5f, 5f};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.radar_chart, container, false);

        radarChart = view.findViewById(R.id.statisticsView2);

        // Get a reference to the Firebase Realtime Database node
        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("congestion_data");

        setupChartView();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Get the new congestion data
                List<Float> congestionList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Float value = dataSnapshot.getValue(Float.class);
                    congestionList.add(value);
                }

                // Update the congestion array with the new data
                for (int i = 0; i < congestionList.size(); i++) {
                    congestion[i] = congestionList.get(i);
                }

                // Update the chart view with the new data
                setupChartView();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void setupChartView() {
        // Create a new ArrayList to hold the chart data
        ArrayList<RadarEntry> chartData = new ArrayList<>();

        // Add each day of the week and its corresponding congestion value to the chart data
        for (int i = 0; i < daysOfWeek.length; i++) {
            chartData.add(new RadarEntry(congestion[i]));
        }

        // Set the chart data
        RadarDataSet dataSet = new RadarDataSet(chartData, "Congestion de la circulation");

        // Set colors
        dataSet.setColor(getResources().getColor(R.color.baseGreen));
        dataSet.setFillColor(getResources().getColor(R.color.baseGreen));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(50);

        // Set the text size and color for the chart values
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(getResources().getColor(R.color.grey));

        // Add the data set to the chart
        RadarData radarData = new RadarData(dataSet);
        radarChart.setData(radarData);

        // Set the chart title
        radarChart.getDescription().setEnabled(false);
        radarChart.setWebLineWidth(1f);
        radarChart.setWebColor(getResources().getColor(R.color.baseGreen));
        radarChart.setWebLineWidthInner(1f);
        radarChart.setWebColorInner(getResources().getColor(R.color.baseGreen));
        radarChart.setWebAlpha(100);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));
        xAxis.setTextColor(getResources().getColor(R.color.baseGreen));

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(12f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(30f);
        yAxis.setDrawLabels(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);

        radarChart.animateXY(1500, 1500, Easing.EaseInOutSine); // add chart animation on fragment open

        // Update the chart view
        radarChart.invalidate();








    }  //create View end






}  //class en
