package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mTitle = "Details";
        restoreActionBar();

        LineChart chart = (LineChart) findViewById(R.id.chart);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);





        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(32.5f, 0));
        entries.add(new Entry(36.5f, 1));
        entries.add(new Entry(39.5f, 2));
        entries.add(new Entry(39.9f, 3));
        entries.add(new Entry(38.9f, 4));
        entries.add(new Entry(33.9f, 5));
        LineDataSet setComp1 = new LineDataSet(entries, "stock 1");
        //setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<Entry> entries2 = new ArrayList<>();
        entries2.add(new Entry(31.5f, 0));
        entries2.add(new Entry(32.5f, 1));
        entries2.add(new Entry(33.5f, 2));
        entries2.add(new Entry(34.9f, 3));
        entries2.add(new Entry(35.9f, 4));
        entries2.add(new Entry(36.9f, 5));
        LineDataSet setComp2 = new LineDataSet(entries2, "stock 2");
        //setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);



        LineData data = new LineData();
        data.addDataSet(setComp1);
        data.addDataSet(setComp2);
        chart.setData(data);
        chart.invalidate(); // refresh
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
}
