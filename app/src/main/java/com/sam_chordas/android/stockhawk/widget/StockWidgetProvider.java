package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sam_chordas.android.stockhawk.service.StockTaskService;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link StockWidgetConfigureActivity StockWidgetConfigureActivity}
 */
public class StockWidgetProvider extends AppWidgetProvider{

    static void updateAppWidget(Context context) {
        context.startService(new Intent(context, WidgetIntentService.class));

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            updateAppWidget(context);
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        Log.d("AppWidgetProvider", intent.getAction());
        if(StockTaskService.ACTION_DATA_UPDATE.equals(intent.getAction())){
            updateAppWidget(context);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            StockWidgetConfigureActivity.deleteStockQuotePref(context, appWidgetId);
        }
    }
}

