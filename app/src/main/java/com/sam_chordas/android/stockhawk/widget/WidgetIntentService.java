package com.sam_chordas.android.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class WidgetIntentService extends IntentService {
    private final static String[] STOCK_COLUMNS = {
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.CHANGE,
            QuoteColumns.ISUP,};

    private static final int INDEX_SYMBOL = 0;
    private static final int INDEX_BID_PRICE = 1;
    private static final int INDEX_CHANGE = 2;
    private static final int INDEX_IS_UP = 3;

    private static final String SELECTION = QuoteColumns.SYMBOL + "= ? AND " +
            QuoteColumns.ISCURRENT + "= ?";
    private static final String IS_CURRENT_TRUE = "1";


    public WidgetIntentService() {
        super("WidgetIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        final Context context = this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                StockWidgetProvider.class));



        // update each widget instance
        for(int appWidgetId : appWidgetIds){
            String symbol = StockWidgetConfigureActivity.loadStockQuotePref(context, appWidgetId);

            Cursor cursor = getContentResolver().query(
                    QuoteProvider.Quotes.CONTENT_URI,
                    STOCK_COLUMNS,
                    SELECTION,
                    new String[] { symbol, IS_CURRENT_TRUE },
                    null);

            if(cursor == null){
                return;
            }

            if(!cursor.moveToFirst()){
                cursor.close();
                return;
            }

            // get stock data
            float bidPrice = cursor.getFloat(INDEX_BID_PRICE);
            float change = cursor.getFloat(INDEX_CHANGE);
            int isUp = cursor.getInt(INDEX_IS_UP);
            //CharSequence widgetText = StockWidgetConfigureActivity.loadStockQuotePref(context, appWidgetId);
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_widget);


            // open main activity on click
            Intent intentStockActivity = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentStockActivity, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // set text
            views.setTextViewText(R.id.stock_symbol, symbol);
            views.setTextViewText(R.id.bid_price, String.valueOf(bidPrice));
            views.setTextViewText(R.id.change, String.valueOf(change));
            if(isUp > 0){
                views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            }else{
                views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }


}
