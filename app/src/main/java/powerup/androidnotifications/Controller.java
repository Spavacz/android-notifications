package powerup.androidnotifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.preference.PreferenceManager;

import com.unity3d.player.UnityPlayer;

public class Controller extends BroadcastReceiver {

    public static void SetNotification(int id, long delayMs, String title, String message, String ticker, int sound, int vibrate, int light, String largeIcon, String smallIcon, int smallIconColor, String unityClass) {
        Controller.SetNotification(id, delayMs, title, message, ticker, sound == 1, vibrate == 1, new long[]{1000, 1000}, light == 1, 3000, 3000, -16711936, largeIcon, smallIcon, smallIconColor, 0, unityClass);
    }

    public static void SetNotification(int id, long delayMs, String title, String message, String ticker, int sound, int vibrate, String vibrationString, int light, int lightOnMs, int lightOffMs, int lightColor, String largeIcon, String smallIcon, int smallIconColor, int executeMode, String unityClass) {
        String[] array = vibrationString.split(",");
        long[] vibration = new long[array.length];
        int i = 0;
        while (i < array.length) {
            vibration[i] = Long.parseLong(array[i]);
            ++i;
        }
        Controller.SetNotification(id, delayMs, title, message, ticker, sound == 1, vibrate == 1, vibration, light == 1, lightOnMs, lightOffMs, lightColor, largeIcon, smallIcon, smallIconColor, executeMode, unityClass);
    }

    public static void SetNotification(int id, long delayMs, String title, String message, String ticker, boolean sound, boolean vibrate, long[] vibration, boolean light, int lightOnMs, int lightOffMs, int lightColor, String largeIcon, String smallIcon, int smallIconColor, int executeMode, String unityClass) {
        NotificationParams params = new NotificationParams();
        params.Id = id;
        params.TriggerAtMillis = System.currentTimeMillis() + delayMs;
        params.Title = title;
        params.Message = message;
        params.Ticker = ticker;
        params.Sound = sound;
        params.Vibrate = vibrate;
        params.Vibration = vibration;
        params.Light = light;
        params.LightOnMs = lightOnMs;
        params.LightOffMs = lightOffMs;
        params.LightColor = lightColor;
        params.LargeIcon = largeIcon;
        params.SmallIcon = smallIcon;
        params.SmallIconColor = smallIconColor;
        params.ExecuteMode = executeMode;
        params.UnityClass = unityClass;
        Controller.SetNotification((Context) UnityPlayer.currentActivity, params);
    }

    public static void SetNotification(Context context, NotificationParams params) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Controller.class);
        intent.putExtra("id", params.Id);
        if (Build.VERSION.SDK_INT >= 23) {
            if (params.ExecuteMode == 2) {
                am.setExactAndAllowWhileIdle(0, params.TriggerAtMillis, PendingIntent.getBroadcast((Context) context, (int) params.Id, (Intent) intent, (int) 0));
            } else if (params.ExecuteMode == 1) {
                am.setExact(0, params.TriggerAtMillis, PendingIntent.getBroadcast((Context) context, (int) params.Id, (Intent) intent, (int) 0));
            } else {
                am.set(0, params.TriggerAtMillis, PendingIntent.getBroadcast((Context) context, (int) params.Id, (Intent) intent, (int) 0));
            }
        } else {
            am.set(0, params.TriggerAtMillis, PendingIntent.getBroadcast((Context) context, (int) params.Id, (Intent) intent, (int) 0));
        }
        Storage.AddNotification(context, params);
    }

    public static void CancelNotification(int id) {
        Activity currentActivity = UnityPlayer.currentActivity;
        AlarmManager am = (AlarmManager) currentActivity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent((Context) currentActivity, Controller.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast((Context) currentActivity, (int) id, (Intent) intent, (int) 0);
        am.cancel(pendingIntent);
        Storage.RemoveNotification((Context) currentActivity, id);
    }

    public static void CancelAllNotifications() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences((Context) UnityPlayer.currentActivity);
        String notifications = prefs.getString("Notifications", null);
        if (notifications == null) {
            return;
        }
        String[] ids = notifications.split(",");
        int i = 0;
        while (i < ids.length) {
            Controller.CancelNotification(Integer.parseInt(ids[i]));
            ++i;
        }
        Context context = (Context) UnityPlayer.currentActivity;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", 0);
        NotificationParams params = Storage.GetNotification(context, id);
        if (params == null) {
            return;
        }
        Resources res = context.getResources();
        Class unityClassActivity = null;
        try {
            unityClassActivity = Class.forName(params.UnityClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent notificationIntent = new Intent(context, unityClassActivity);
        PendingIntent contentIntent = PendingIntent.getActivity((Context) context, (int) 0, (Intent) notificationIntent, (int) 0);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(contentIntent).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle((CharSequence) params.Title).setContentText((CharSequence) params.Message);
        if (Build.VERSION.SDK_INT >= 21) {
            builder.setColor(params.SmallIconColor);
        }
        if (params.Ticker != null && params.Ticker.length() > 0) {
            builder.setTicker((CharSequence) params.Ticker);
        }
        if (params.SmallIcon != null && params.SmallIcon.length() > 0) {
            builder.setSmallIcon(res.getIdentifier(params.SmallIcon, "drawable", context.getPackageName()));
        }
        if (params.LargeIcon != null && params.LargeIcon.length() > 0) {
            builder.setLargeIcon(BitmapFactory.decodeResource((Resources) res, (int) res.getIdentifier(params.LargeIcon, "drawable", context.getPackageName())));
        }
        if (params.Sound) {
            builder.setSound(RingtoneManager.getDefaultUri((int) 2));
        }
        if (params.Vibrate) {
            builder.setVibrate(params.Vibration);
        }
        if (params.Light) {
            builder.setLights(params.LightColor, params.LightOnMs, params.LightOffMs);
        }
        Notification notification = Build.VERSION.SDK_INT <= 15 ? builder.getNotification() : builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
        Storage.RemoveNotification(context, id);
    }
}

