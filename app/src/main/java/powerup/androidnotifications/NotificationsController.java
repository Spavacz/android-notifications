package powerup.androidnotifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;

import com.unity3d.player.UnityPlayer;

public class NotificationsController extends BroadcastReceiver {

    public static void SetNotification(int id, long delayMs, String title, String message, String ticker, boolean sound, boolean vibrate, boolean light, String largeIcon, String smallIcon, int smallIconColor, String unityClass) {
        NotificationsController.SetNotification(id, delayMs, title, message, ticker, sound, vibrate, light, largeIcon, smallIcon, smallIconColor, 0, unityClass);
    }

    public static void SetNotification(int id, long delayMs, String title, String message, String ticker, boolean sound, boolean vibrate, boolean light, String largeIcon, String smallIcon, int smallIconColor, int executeMode, String unityClass) {
        Context context = UnityPlayer.currentActivity;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationsController.class);
        intent.putExtra("id", id);
        intent.putExtra("ticker", ticker);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("color", smallIconColor);
        intent.putExtra("sound", sound);
        intent.putExtra("vibrate", vibrate);
        intent.putExtra("lights", light);
        intent.putExtra("largeicon", largeIcon);
        intent.putExtra("smallicon", smallIcon);
        intent.putExtra("activity", unityClass);

        long triggerAtMillis = System.currentTimeMillis() + delayMs;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (executeMode == 2) {
                am.setExactAndAllowWhileIdle(0, triggerAtMillis, PendingIntent.getBroadcast(context, id, intent, 0));
            } else if (executeMode == 1) {
                am.setExact(0, triggerAtMillis, PendingIntent.getBroadcast(context, id, intent, 0));
            } else {
                am.set(0, triggerAtMillis, PendingIntent.getBroadcast(context, id, intent, 0));
            }
        } else {
            am.set(0, triggerAtMillis, PendingIntent.getBroadcast(context, id, intent, 0));
        }
    }

    public static void CancelNotification(int id) {
        Context context = UnityPlayer.currentActivity;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationsController.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            am.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static void CancelAll() {
        NotificationManager notificationManager = (NotificationManager) UnityPlayer.currentActivity.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String ticker = intent.getStringExtra("ticker");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        String smallIcon = intent.getStringExtra("smallicon");
        String largeIcon = intent.getStringExtra("largeicon");
        int color = intent.getIntExtra("color", 0);
        String unityClass = intent.getStringExtra("activity");
        Boolean sound = intent.getBooleanExtra("sound", false);
        Boolean vibrate = intent.getBooleanExtra("vibrate", false);
        Boolean lights = intent.getBooleanExtra("lights", false);
        int id = intent.getIntExtra("id", 0);

        Resources res = context.getResources();

        Class<?> unityClassActivity = null;
        try {
            unityClassActivity = Class.forName(unityClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Intent notificationIntent = new Intent(context, unityClassActivity);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(color);
        }

        if (ticker != null && ticker.length() > 0) {
            builder.setTicker(ticker);
        }

        if (smallIcon != null && smallIcon.length() > 0) {
            builder.setSmallIcon(res.getIdentifier(smallIcon, "drawable", context.getPackageName()));
        }

        if (largeIcon != null && largeIcon.length() > 0) {
            builder.setLargeIcon(BitmapFactory.decodeResource(res, res.getIdentifier(largeIcon, "drawable", context.getPackageName())));
        }

        if (sound) {
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        }

        if (vibrate) {
            builder.setVibrate(new long[]{1000L, 1000L});
        }

        if (lights) {
            builder.setLights(Color.GREEN, 3000, 3000);
        }

        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }

        notificationManager.notify(id, notification);
    }


}

