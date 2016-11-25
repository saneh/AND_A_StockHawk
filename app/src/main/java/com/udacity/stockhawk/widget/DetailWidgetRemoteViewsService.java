package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.StockDetailActivity;

/**
 * Created by sanehyadav1 on 11/25/16.
 */
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] QUOTE_COLUMNS = {
            Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE
    };
    //matching indices with projection
    private static final int INDEX_QUOTE_ID=0;
    private static final int INDEX_SYMBOL=1;
    private static final int INDEX_PRICE=2;
    private static final int INDEX_ABSOLUTE_CHANGE=3;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            @Override
            public void onCreate() {
            //nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if(data!=null){
                    data.close();
                }
                //This method is called by the app hosting the widget(e.g the launcher)
                //However our content provider is not exported so it doesnt  have access to the
                //data.Therefore we need to clear (and finally restore) the calling identity so
                //that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Contract.Quote.uri,QUOTE_COLUMNS,null,null,Contract.Quote.COLUMN_SYMBOL + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if(data!=null) {
                    data.close();
                    data=null;
                }
            }

            @Override
            public int getCount() {
                return data==null?0:data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if(position == AdapterView.INVALID_POSITION||data==null||!data.moveToPosition(position)){
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
                String symbol = data.getString(INDEX_SYMBOL);
                float price = data.getFloat(INDEX_PRICE);
                float price_change = data.getFloat(INDEX_ABSOLUTE_CHANGE);
                views.setTextViewText(R.id.symbol,symbol);
                views.setTextViewText(R.id.price,String.valueOf(price));
                views.setTextViewText(R.id.change, String.valueOf(price_change));

                final Intent fillInIntent = new Intent();
                fillInIntent.setData(Contract.Quote.makeUriForStock(symbol));
                views.setOnClickFillInIntent(R.id.widget_list_item,fillInIntent);
                return views;
            }
//            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//            private void setRemoteContentDescription(RemoteViews views,String description){
//                views.setContentDescription(R.id.widget_icon,description);
//            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(),R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if(data.moveToPosition(i)){
                    return data.getLong(INDEX_QUOTE_ID);
                }
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
