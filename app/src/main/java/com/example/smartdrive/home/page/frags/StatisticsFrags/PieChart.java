package com.example.smartdrive.home.page.frags.StatisticsFrags;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartdrive.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PieChart extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef;


    com.github.mikephil.charting.charts.PieChart statisticsView1;

    // Array of days of the week
    String[] daysOfWeek = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};

    // Array of congestion values for each day of the week
    float[] congestion = {10f, 20f, 10f, 30f, 10f, 5f, 5f};


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.pie_chart, container, false);

        statisticsView1 = v.findViewById(R.id.statisticsView1);

        // Configure chart settings
        statisticsView1.setUsePercentValues(true);
        statisticsView1.getDescription().setEnabled(false);
        statisticsView1.setExtraOffsets(5, 10, 5, 5);
        statisticsView1.setDragDecelerationFrictionCoef(0.95f);
        statisticsView1.setDrawHoleEnabled(true);
        statisticsView1.setHoleColor(Color.WHITE);
        statisticsView1.setTransparentCircleColor(Color.WHITE);
        statisticsView1.setTransparentCircleAlpha(110);
        statisticsView1.setHoleRadius(58f);
        statisticsView1.setTransparentCircleRadius(61f);
        statisticsView1.setDrawCenterText(true);
        statisticsView1.setRotationAngle(0);
        statisticsView1.setRotationEnabled(true);


        setupChartView(statisticsView1);

        // Get a reference to the Firebase Realtime Database node
        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("congestion_data");



//        // Create a new ArrayList<Float> object and add the converted values from the congestion array
//        ArrayList<Float> congestionList = new ArrayList<>();
//        for (float value : congestion) {
//            congestionList.add(Float.valueOf(value));
//        }
//
//        // Set the value of the "congestion" node to the congestionList object
//        databaseReference.setValue(congestionList).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//                Toast.makeText(getContext(), "data successfully added", Toast.LENGTH_SHORT).show();
//
//            }
//        });





        //upload data on real time
        // Listen for changes in the "congestion_data" node
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the new congestion data
                List<Float> congestionList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Float value = snapshot.getValue(Float.class);
                    congestionList.add(value);
                }

                // Update the congestion array with the new data
                for (int i = 0; i < congestionList.size(); i++) {
                    congestion[i] = congestionList.get(i);
                }

                // Update the chart view with the new data
                setupChartView(statisticsView1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PieChart", "Failed to read value.", databaseError.toException());
            }
        });





        return v;



    }  //onCreate View end























    private void setupChartView(com.github.mikephil.charting.charts.PieChart pieChart) {
        // Create a new ArrayList to hold the chart data
        ArrayList<PieEntry> chartData = new ArrayList<>();

        // Add each day of the week and its corresponding congestion value to the chart data
        for (int i = 0; i < daysOfWeek.length; i++) {
            chartData.add(new PieEntry(congestion[i], daysOfWeek[i]));
        }

        // Set the chart data and colors
        PieDataSet dataSet = new PieDataSet(chartData, "Congestion de la circulation");

        // Set colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.chartGreen));
        colors.add(getResources().getColor(R.color.chartGreen2));
        colors.add(getResources().getColor(R.color.chartGreen));
        colors.add(getResources().getColor(R.color.chartYellow));
        colors.add(getResources().getColor(R.color.chartGreen2));
        colors.add(getResources().getColor(R.color.chartYellow));
        colors.add(getResources().getColor(R.color.chartBlue));
        dataSet.setColors(colors);

        // Set the text size and color for the chart values
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        // Add the data set to the chart
        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);

        // Set the chart title
        pieChart.setCenterText("Congestion par jour de la semaine");

        Typeface typefaceAlegreya = ResourcesCompat.getFont(requireContext(), R.font.alegreya_sans);
        pieChart.setCenterTextTypeface(typefaceAlegreya);
        pieChart.setCenterTextColor(getResources().getColor(R.color.baseGreen));

        // Set the chart legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTextColor(getResources().getColor(R.color.baseGreen));
        legend.setDrawInside(false);



        // Set other chart properties
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.animateY(1500, Easing.EaseInOutSine); // add chart animation on fragment open

        // Update the chart view
        pieChart.invalidate();

    }








}       //class end