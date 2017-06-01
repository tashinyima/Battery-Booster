package com.receptix.batterybuddy;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.receptix.batterybuddy.helper.LogUtil;

import static com.receptix.batterybuddy.helper.Constants.BatteryParams.BATTERY_LEVEL;
import static com.receptix.batterybuddy.helper.Constants.Params.INTENT_ACTION_UPDATE_WIDGET_BATTERY_SERVICE;
import static com.receptix.batterybuddy.helper.Constants.Params.USED_RAM_PERCENTAGE;

/**
 * Implementation of App Widget functionality.
 */
public class BatteryBuddyAppWidget extends AppWidgetProvider {

    private static final String TAG = BatteryBuddyAppWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.battery_buddy_app_widget);
        /*views.setTextViewText(R.id.appwidget_text, widgetText);*/
        try {
            Intent intent_startService = new Intent(context, BatteryUpdateService.class);
            context.startService(intent_startService);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        LogUtil.e("intent.getAction()", intent.getAction());
        if (intent.getAction().equalsIgnoreCase(INTENT_ACTION_UPDATE_WIDGET_BATTERY_SERVICE)) {
            // Calculate Battery Charging Level
            int batteryLevel = intent.getIntExtra(BATTERY_LEVEL, 0);
            int usedRamPercentage = intent.getIntExtra(USED_RAM_PERCENTAGE, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.battery_buddy_app_widget);
            views.setTextViewText(R.id.battery_text, batteryLevel + " %");
            views.setTextViewText(R.id.ram_text, usedRamPercentage + " %");
            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, BatteryBuddyAppWidget.class), views);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        LogUtil.e(TAG, "onEnabled(Context context)");
        // Enter relevant functionality for when the first widget is created
        //call battery update service
        /*try {
            Intent intent_startService = new Intent(context, BatteryUpdateService.class);
            context.startService(intent_startService);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

