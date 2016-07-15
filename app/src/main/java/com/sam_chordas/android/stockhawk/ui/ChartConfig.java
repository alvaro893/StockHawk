/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

/*
 * TODO: Create JavaDoc
 */
public class ChartConfig implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int TEXT_COLOR = Color.WHITE;
    private static final int COLOR_HIGHLIGHT = Color.GREEN;
    private static final String SELECTION = QuoteColumns.SYMBOL + "= ?";
    private final static String[] STOCK_COLUMNS = {
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE};
    private static final int INDEX_SYMBOL = 0;
    private static final int INDEX_BID_PRICE = 1;
    private static final String STOCK_PRICES = "Stock prices";
    public static final int LOADER_ID = 0;
    private static final float TEXT_SIZE = 15f;


    private final Context mContext;
    private final LineChart mChart;
    private String mStockSymbol;
    private ArrayList<Entry> dataEntries;
    private String mDescription;

    public ChartConfig(Context context, LineChart chart, String stockSymbol) {
        this.mContext = context;
        this.mStockSymbol = stockSymbol;
        this.mChart = chart;
        this.dataEntries = new ArrayList<>();
        this.mDescription = "0";

        configureAxis(mChart);
    }

    private void configureAxis(LineChart chart) {
        chart.setDescription(mDescription);
        chart.setDescriptionColor(TEXT_COLOR);
        chart.setDescriptionTextSize(TEXT_SIZE);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        YAxis yAxisRight = chart.getAxis(YAxis.AxisDependency.RIGHT);
        yAxisRight.setEnabled(false);

        YAxis yAxis = chart.getAxis(YAxis.AxisDependency.LEFT);
        yAxis.setTextSize(TEXT_SIZE);
        yAxis.setTextColor(TEXT_COLOR);
        yAxis.setDrawGridLines(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                QuoteProvider.Quotes.CONTENT_URI,
                STOCK_COLUMNS, SELECTION, new String[]{mStockSymbol}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int count = 0;
        for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()){
            float bidPrice = data.getFloat(INDEX_BID_PRICE);
            dataEntries.add(new Entry(count, bidPrice));
            count++;
        }
        LineDataSet lineDataSet = new LineDataSet(dataEntries, STOCK_PRICES);

        lineDataSet.setValueTextColor(TEXT_COLOR);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineDataSet.setHighLightColor(COLOR_HIGHLIGHT);

        mChart.setData(lineData);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.invalidate(); // refresh
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float bidPrice = e.getY();
                mChart.setDescription(mContext.getString(R.string.price) + bidPrice);
            }

            @Override
            public void onNothingSelected() {
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}


}
