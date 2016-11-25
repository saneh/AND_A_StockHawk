package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int STOCK_HISTORY_LOADER = 1;
    //protected static final String SYMBOL = "symbol";
    public static final String STOCK_URI = "stock_uri";
    //private String symbol;
    private Uri uri;
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        Intent intent = getIntent();
        //symbol = intent.getStringExtra(SYMBOL);
        if (intent.getData() != null) {
            uri = intent.getData();
        } else if (intent.hasExtra(STOCK_URI)) {
            uri = Uri.parse(intent.getStringExtra(STOCK_URI));
        } else {
            Timber.d("Null Uri passed in Detail Activity");
        }
        getSupportLoaderManager().initLoader(STOCK_HISTORY_LOADER, null, this);
        chart = (LineChart) findViewById(R.id.chart);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stock_detail, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Uri uri = Contract.Quote.makeUriForStock(symbol);
        return new CursorLoader(this, uri, Contract.Quote.QUOTE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            String history = cursor.getString(Contract.Quote.POSITION_HISTORY);
            List<Entry> entries = new ArrayList<Entry>();
            List<String> xVal = new ArrayList<String>();
            String[] historical_quotes = history.split("\\n");
            for (int counter = 0; counter < historical_quotes.length; counter++) {
                //this ensures that date increases as we move in positive x direction
                String[] date_closing = historical_quotes[historical_quotes.length - counter - 1].split(", ");
                entries.add(new Entry(Float.valueOf(date_closing[1]), counter));
                //Converting date (in milliseconds) in simple date format
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(date_closing[0]));
                xVal.add(dateFormat.format(calendar.getTime()));
            }
            LineDataSet dataSet = new LineDataSet(entries, "Historical data");
            dataSet.setColor(Color.CYAN);
            dataSet.setValueTextColor(Color.CYAN);
            //Add LineDataSet object to LineData object
            LineData lineData = new LineData(xVal, dataSet);
            chart.setData(lineData);
            chart.invalidate(); //refresh

        } else {
            Timber.d("Error fetching historical data from database");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
