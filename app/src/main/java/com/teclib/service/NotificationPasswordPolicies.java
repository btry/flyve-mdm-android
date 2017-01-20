/*
 * Copyright (C) 2016 Teclib'
 *
 * This file is part of Flyve MDM Android.
 *
 * Flyve MDM Android is a subproject of Flyve MDM. Flyve MDM is a mobile
 * device management software.
 *
 * Flyve MDM Android is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Flyve MDM Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ------------------------------------------------------------------------------
 * @author    Dorian LARGET
 * @copyright Copyright (c) 2016 Flyve MDM
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyvemdm/flyvemdm-android
 * @link      http://www.glpi-project.org/
 * ------------------------------------------------------------------------------
 */

package com.teclib.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.teclib.api.FlyveLog;
import com.teclib.flyvemdm.ActiveGPSActivity;
import com.teclib.flyvemdm.R;

public class NotificationPasswordPolicies extends Service {
    final static String ACTION = "NotifyServiceActionKillNotification";

    NotificationPasswordPolicies.ServiceReceiverNotification notifyServiceReceiver;
    @Override
    public void onCreate() {
        notifyServiceReceiver = new ServiceReceiverNotification();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(notifyServiceReceiver, intentFilter);
        CustomNotification();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        FlyveLog.d("onDestroy");
        this.unregisterReceiver(notifyServiceReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    public void CustomNotification() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.notification_install_apps);
        Intent intent =
                new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_white_stork)
                .setOngoing(true)
                .setTicker(getString(R.string.passwordPoliciesNotification_string))
                .setContentIntent(pIntent)
                .setContent(remoteViews);

        Notification notificationInstall = builder.build();
        notificationInstall.flags |= Notification.FLAG_AUTO_CANCEL;

        remoteViews.setImageViewResource(R.id.imagenotileft, R.mipmap.ic_notification_install_apps);

        remoteViews.setTextViewText(R.id.title, getString(R.string.app_name));
        remoteViews.setTextViewText(R.id.text, getString(R.string.passwordPoliciesNotification_string));

        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.notify(5, builder.build());

    }

    public class ServiceReceiverNotification extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            FlyveLog.d("ReceiverDelete");
            stopSelf();
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .cancel(5);
        }
    }

}
