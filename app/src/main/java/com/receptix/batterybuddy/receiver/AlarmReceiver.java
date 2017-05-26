package com.receptix.batterybuddy.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.receptix.batterybuddy.R;
import com.receptix.batterybuddy.activities.OptimizerActivity;

import static com.receptix.batterybuddy.helper.Constants.Params.REFERRER_JSON_OBJECT;

/**
 * Created by hello on 5/22/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    NotificationManager notificationManager;
    Intent intent;
    PendingIntent pendingIntent;
    final  static int NOTIFICATION_ID = 999;

    @Override
    public void onReceive(Context context, Intent intent) {

        sendCustomNotification(context);

        if(intent!=null && intent.getExtras()!=null)
        {
            //get referrerJsonObject
            String referrerJsonObject = intent.getExtras().getString(REFERRER_JSON_OBJECT, null);

        }

    }

    private void sendCustomNotification(Context context) {

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        contentView.setImageViewResource(R.id.image, R.drawable.brush_notification);
        contentView.setTextViewText(R.id.title, context.getString(R.string.notification_title_optimize));
        contentView.setTextViewText(R.id.text, context.getString(R.string.notification_description_optimize));
        intent = new Intent(context, OptimizerActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification_icon)
                .setAutoCancel(false)
                .setContent(contentView);

        contentView.setOnClickPendingIntent(R.id.notificationOptimizerBtn, pendingIntent);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }



}
