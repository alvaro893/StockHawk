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
    public static final int LOADER_ID = 0;
    private static final int TEXT_COLOR = Color.WHITE;
    private static final int COLOR_HIGHLIGHT = Color.GREEN;
    private static final String SELECTION = QuoteColumns.SYMBOL + "= ?";
    private final static String[] STOCK_COLUMNS = {
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE};
    private static final int INDEX_SYMBOL = 0;
    private static final int INDEX_BID_PRICE = 1;
    private static final String STOCK_PRICES = "Stock prices";
    private static final float TEXT_SIZE = 15f;
    private final Context mContext;
    private final LineChart mChart;
    private int mFillColor = Color.argb(150, 51, 181, 229);
    private String mStockSymbol;
    private ArrayList<Entry> dataEntries;
    private String mDescription;

    public ChartConfig(Context context, LineChart chart, String stockSymbol) {
        this.mContext = context;
        this.mStockSymbol = stockSymbol;
        this.mChart = chart;
        this.dataEntries = new ArrayList<>();
        this.mDescription = "";

        configureChart(mChart);
        configureAxis(mChart);
    }

    private void configureChart(LineChart mChart) {
        mChart.setDescription(mDescription);
        mChart.setDescriptionColor(TEXT_COLOR);
        mChart.setDescriptionTextSize(TEXT_SIZE);
    }

    private void configureAxis(LineChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);
        xAxis.setDrawGridLines(false);

        YAxis yAxisRight = chart.getAxis(YAxis.AxisDependency.RIGHT);
        yAxisRight.setEnabled(false);

        YAxis yAxis = chart.getAxis(YAxis.AxisDependency.LEFT);
        yAxis.setTextSize(TEXT_SIZE);
        yAxis.setTextColor(TEXT_COLOR);
        yAxis.setDrawGridLines(false);
    }
    private void addDataToChart(ArrayList<Entry> dataEntries) {
        LineDataSet set = new LineDataSet(dataEntries, STOCK_PRICES);

        set.setValueTextColor(TEXT_COLOR);
        set.setDrawValues(false);
        set.setColor(TEXT_COLOR);
        set.setDrawFilled(true);
        set.setFillColor(mFillColor);
        set.setHighLightColor(COLOR_HIGHLIGHT);


        mChart.setData(new LineData(set));
        mChart.invalidate(); // refresh

        // select callbacks
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float bidPrice = e.getY();
                mChart.setDescription(mContext.getString(R.string.price, bidPrice));
            }

            @Override
            public void onNothingSelected() {
                mChart.setDescription(mContext.getString(R.string.price, 0f));
            }
        });
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
        addDataToChart(dataEntries);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}


}
