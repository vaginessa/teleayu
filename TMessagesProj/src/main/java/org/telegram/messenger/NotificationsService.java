/*
 * This is the source code of Telegram for Android v. 1.3.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.messenger;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.radolyn.ayugram.AyuConfig;
import org.telegram.ui.LaunchActivity;

import java.util.Random;

public class NotificationsService extends Service {
    private static final String[] notifications = new String[]{
            "Don't swipe me!",
            "Letting you receive notifications…",
            "Helping you receive pushes…",
            "dontkillmyapp.com",
            "⊂(◉‿◉)つ",
            "(｡◕‿‿◕｡)",
            "(｡◕‿◕｡)",
            "¯\\_(ツ)_/¯",
            "\\(^-^)/",
            "＼(＾O＾)／",
            "\\(ᵔᵕᵔ)/",
            "ԅ(≖‿≖ԅ)",
            "(⊃｡•́‿•̀｡)⊃",
            "•ᴗ•",
            "(~‾▿‾)~",
            "｡^‿^｡",
            "(⁎˃ᆺ˂)",
    };

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationLoader.postInitApplication();

        if (AyuConfig.keepAliveService && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "ayugram_push";
            NotificationChannelCompat channel = new NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                    .setName("AyuGram Push Service")
                    .setLightsEnabled(false)
                    .setVibrationEnabled(false)
                    .setSound(null, null)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(channel);

            var cuteText = notifications[new Random().nextInt(notifications.length)];

            var startIntent = new Intent(this, LaunchActivity.class);
            var pendingIntent = PendingIntent.getActivity(this, 0, startIntent, PendingIntent.FLAG_MUTABLE);

            var notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.msg_premium_badge)
                    .setShowWhen(false)
                    .setOngoing(true)
                    .setContentText(cuteText)
                    .setCategory(NotificationCompat.CATEGORY_STATUS)
                    .build();

            startForeground(9999, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = MessagesController.getGlobalNotificationsSettings();
        if (preferences.getBoolean("pushService", true)) {
            Intent intent = new Intent("org.telegram.start");
            sendBroadcast(intent);
        }
    }
}
