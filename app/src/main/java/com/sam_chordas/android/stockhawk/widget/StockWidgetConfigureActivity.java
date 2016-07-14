package com.sam_chordas.android.stockhawk.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.RecyclerViewItemClickListener;
import com.sam_chordas.android.stockhawk.ui.StockListLoader;


/**
 * The configuration screen for the {@link StockWidgetProvider StockWidgetProvider} AppWidget.
 */
public class StockWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.sam_chordas.android.stockhawk.widget.StockWidgetProvider";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final int LOADER_ID = 0;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    RecyclerView mAppWidgetList;


    RecyclerViewItemClickListener.OnItemClickListener mOnItemClickListener =
            new RecyclerViewItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            final Context context = StockWidgetConfigureActivity.this;

            // When the button is clicked, store the item locally
            TextView symbolView = (TextView) view.findViewById(R.id.stock_symbol);
            String widgetSymbol = symbolView.getText().toString();
            saveStockQuotePref(context, mAppWidgetId, widgetSymbol);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            StockWidgetProvider.updateAppWidget(context);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public StockWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveStockQuotePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadStockQuotePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteStockQuotePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.stock_widget_configure);
        mAppWidgetList = (RecyclerView) findViewById(R.id.stock_quotes_configure_widget);
        StockListLoader stockListLoader = new StockListLoader(this, mAppWidgetList);
        getLoaderManager().initLoader(LOADER_ID, null, stockListLoader);

        mAppWidgetList.addOnItemTouchListener(
                new RecyclerViewItemClickListener(this, mOnItemClickListener));

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }
}

