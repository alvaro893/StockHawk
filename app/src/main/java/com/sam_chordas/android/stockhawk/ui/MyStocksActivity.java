package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.RecyclerViewItemClickListener;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.melnykov.fab.FloatingActionButton;

public class MyStocksActivity extends AppCompatActivity{

  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */

  /**
   * Used to store the last screen title. For use in {@link #restoreActionBar()}.
   */
  private CharSequence mTitle;
  private Intent mServiceIntent;
  private Context mContext;
  boolean isConnected;
  private StockListLoader mStockListLoader;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_stocks);
    mContext = this;
    checkNetwork();
    runStockService(savedInstanceState);

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    TextView textView = (TextView) findViewById(R.id.no_network_view);

    mStockListLoader = new StockListLoader(mContext, recyclerView, textView);
    getLoaderManager().initLoader(StockListLoader.CURSOR_LOADER_ID, null, mStockListLoader);
    setItemClickRecicleView(recyclerView);
    setFab(recyclerView);

    mTitle = getTitle();
    setPeriodicTaskToGcm();
  }

  private void runStockService(Bundle savedInstanceState) {
    // The intent service is for executing immediate pulls from the Yahoo API
    // GCMTaskService can only schedule tasks, they cannot execute immediately
    mServiceIntent = new Intent(this, StockIntentService.class);
    if (savedInstanceState == null){
      // Run the initialize task service so that some stocks appear upon an empty database
      mServiceIntent.putExtra("tag", "init");
      if (isConnected){
        startService(mServiceIntent);
      } else{
        networkToast();
      }
    }
  }

  private void checkNetwork() {
    ConnectivityManager cm =
            (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    isConnected = activeNetwork != null &&
            activeNetwork.isConnectedOrConnecting();
  }

  private void setItemClickRecicleView(RecyclerView recyclerView) {
    recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
            new RecyclerViewItemClickListener.OnItemClickListener() {
              @Override public void onItemClick(View v, int position) {
                TextView stockSymbolView = (TextView) v.findViewById(R.id.stock_symbol);
                String symbol = stockSymbolView.getText().toString();
                Intent intent = new Intent(MyStocksActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.STOCK_SYMBOL_ARG, symbol);
                startActivity(intent);
              }
            }));

  }

  private void setFab(RecyclerView recyclerView) {
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.attachToRecyclerView(recyclerView);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (isConnected){
          new MaterialDialog.Builder(mContext).title(R.string.symbol_search)
                  .content(R.string.content_test)
                  .inputType(InputType.TYPE_CLASS_TEXT)
                  .negativeText(R.string.cancel)
                  .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                    @Override public void onInput(MaterialDialog dialog, CharSequence input) {
                      // On FAB click, receive user input. Make sure the stock doesn't already exist
                      // in the DB and proceed accordingly
                      Cursor c = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                              new String[] { QuoteColumns.SYMBOL }, QuoteColumns.SYMBOL + "= ?",
                              new String[] { input.toString() }, null);
                      if (c.getCount() != 0) {
                        Toast toast =
                                Toast.makeText(MyStocksActivity.this, R.string.stock_already_saved_warning,
                                        Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
                        toast.show();
                        return;
                      } else {
                        // Add the stock to DB
                        mServiceIntent.putExtra("tag", "add");
                        mServiceIntent.putExtra("symbol", input.toString());
                        startService(mServiceIntent);
                      }
                    }
                  })
                  .show();
        } else {
          networkToast();
        }

      }
    });
  }

  private void setPeriodicTaskToGcm() {
    if (isConnected){
      long period = 3600L;
      long flex = 10L;
      String periodicTag = "periodic";

      // create a periodic task to pull stocks once every hour after the app has been opened. This
      // is so Widget data stays up to date.
      PeriodicTask periodicTask = new PeriodicTask.Builder()
              .setService(StockTaskService.class)
              .setPeriod(period)
              .setFlex(flex)
              .setTag(periodicTag)
              .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
              .setRequiresCharging(false)
              .build();
      // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
      // are updated.
      GcmNetworkManager.getInstance(this).schedule(periodicTask);
    }
  }


  @Override
  public void onResume() {
    super.onResume();
    getLoaderManager().restartLoader(StockListLoader.CURSOR_LOADER_ID, null, mStockListLoader);
  }

  public void networkToast(){
    Toast.makeText(mContext, getString(R.string.network_toast), Toast.LENGTH_SHORT).show();
  }

  public void restoreActionBar() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setDisplayShowTitleEnabled(true);
    actionBar.setTitle(mTitle);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.my_stocks, menu);
      restoreActionBar();
      return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    if (id == R.id.action_change_units){
      // this is for changing stock changes from percent value to dollar value
      Utils.showPercent = !Utils.showPercent;
      this.getContentResolver().notifyChange(QuoteProvider.Quotes.CONTENT_URI, null);
    }

    if(id == R.id.action_force_update){
      checkNetwork();
      runStockService(null);
      getLoaderManager().initLoader(StockListLoader.CURSOR_LOADER_ID, null, mStockListLoader);
    }

    return super.onOptionsItemSelected(item);
  }


}
