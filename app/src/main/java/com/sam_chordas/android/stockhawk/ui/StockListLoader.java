/**
 * Copyright (C) 2016 Alvaro Bolanos Rodriguez
 */
package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.touch_helper.SimpleItemTouchHelperCallback;

/*
 * TODO: Create JavaDoc
 */
public class StockListLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int CURSOR_LOADER_ID = 0;
    private TextView mNoItemsView;
    private QuoteCursorAdapter mCursorAdapter;
    private Context mContext;
    private Cursor mCursor;
    private RecyclerView mRecyclerView;

    public StockListLoader(Context context, RecyclerView mRecyclerView, TextView noItemsView) {
        this.mContext = context;
        this.mRecyclerView = mRecyclerView;
        this.mNoItemsView = noItemsView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mCursorAdapter = new QuoteCursorAdapter(mContext, null);

        mRecyclerView.setAdapter(mCursorAdapter);



        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        //if(id == CURSOR_LOADER_ID) {
            // This narrows the return to only the stocks that are most current.
            return new CursorLoader(mContext, QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                    QuoteColumns.ISCURRENT + " = ?",
                    new String[]{"1"},
                    null);
//        }
//        return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data){

        mCursorAdapter.swapCursor(data);
        mCursor = data;
        boolean thereIsNoItems = mCursorAdapter.getItemCount() < 1;
        if(thereIsNoItems){
            mNoItemsView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            mNoItemsView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader){
        mCursorAdapter.swapCursor(null);
    }
}
