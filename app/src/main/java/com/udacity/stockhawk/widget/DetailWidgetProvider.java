package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.ui.StockDetailActivity;

/**
 * AppWidgetProvider
 */
public class DetailWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //perform this loop for all the app widgets
        for(int appWidgetId:appWidgetIds){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_detail);

            //Create an intent to launch Main Activity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
            views.setOnClickPendingIntent(R.id.widget,pendingIntent);

            //Set up the collection
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                setRemoteAdapter(context,views);
            }else
            {
                setRemoteAdapterV11(context,views);
            }
            boolean useDetailActivity = context.getResources().getBoolean(R.bool.use_detail_activity);
            Intent clickIntentTemplate = useDetailActivity ? new Intent(context, StockDetailActivity.class): new Intent(context,MainActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context).addNextIntentWithParentStack(clickIntentTemplate).getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list,clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_list,R.id.widget_empty);

            //Tell the appWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId,views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //to check
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context,@NonNull final RemoteViews views){
        views.setRemoteAdapter(R.id.widget_list, new Intent(context, DetailWidgetRemoteViewsService.class));
    }
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context,@NonNull final RemoteViews views){
        views.setRemoteAdapter(0,R.id.widget_list, new Intent(context,DetailWidgetRemoteViewsService.class));
    }
}
