package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.sam_chordas.android.stockhawk.R;

public class DetailActivity extends AppCompatActivity {

    public static final String STOCK_SYMBOL_ARG = "symbol";
    private String mTitle;
    private String mStockSymbol;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mContext = this;
        mStockSymbol = getIntent().getExtras().getString(STOCK_SYMBOL_ARG);
        mTitle = mStockSymbol;
        restoreActionBar();

        LineChart chart = (LineChart) findViewById(R.id.chart);
        ChartConfig chartConfig = new ChartConfig(mContext, chart, mStockSymbol);
        getSupportLoaderManager().initLoader(ChartConfig.LOADER_ID, null, chartConfig);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
}
