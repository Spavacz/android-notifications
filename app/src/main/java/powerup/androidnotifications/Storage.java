package powerup.androidnotifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class Storage {
    public static void AddNotification(Context context, NotificationParams params) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        String notifications = prefs.getString("Notifications", null);
        int id = params.Id;
        editor.putString("Notifications", notifications == null ? Integer.toString(id) : String.valueOf(notifications) + "," + id);
        editor.putLong("Notification.TriggerAtMillis." + id, params.TriggerAtMillis);
        editor.putString("Notification.Title." + id, params.Title);
        editor.putString("Notification.Message." + id, params.Message);
        editor.putString("Notification.Ticker." + id, params.Ticker);
        editor.putBoolean("Notification.Sound." + id, params.Sound);
        editor.putBoolean("Notification.Vibrate." + id, params.Vibrate);
        editor.putString("Notification.Vibration." + id, Arrays.toString(params.Vibration));
        editor.putBoolean("Notification.Light." + id, params.Light);
        editor.putInt("Notification.LightOnMs." + id, params.LightOnMs);
        editor.putInt("Notification.LightOffMs." + id, params.LightOffMs);
        editor.putInt("Notification.LightColor." + id, params.LightColor);
        editor.putString("Notification.LargeIcon." + id, params.LargeIcon);
        editor.putString("Notification.SmallIcon." + id, params.SmallIcon);
        editor.putInt("Notification.SmallIconColor." + id, params.SmallIconColor);
        editor.putInt("Notification.ExecuteMode." + id, params.ExecuteMode);
        editor.putString("Notification.UnityClass." + id, params.UnityClass);
        editor.apply();
    }

    public static NotificationParams GetNotification(Context context, int id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        NotificationParams params = new NotificationParams();
        if (!prefs.contains("Notification.TriggerAtMillis." + id)) {
            return null;
        }
        params.Id = id;
        params.TriggerAtMillis = prefs.getLong("Notification.TriggerAtMillis." + id, 0);
        params.Title = prefs.getString("Notification.Title." + id, null);
        params.Message = prefs.getString("Notification.Message." + id, null);
        params.Ticker = prefs.getString("Notification.Ticker." + id, null);
        params.Sound = prefs.getBoolean("Notification.Sound." + id, false);
        params.Vibrate = prefs.getBoolean("Notification.Vibrate." + id, false);
        params.Light = prefs.getBoolean("Notification.Light." + id, false);
        params.LightOnMs = prefs.getInt("Notification.LightOnMs." + id, 0);
        params.LightOffMs = prefs.getInt("Notification.LightOffMs." + id, 0);
        params.LightColor = prefs.getInt("Notification.LightColor." + id, 0);
        params.LargeIcon = prefs.getString("Notification.LargeIcon." + id, null);
        params.SmallIcon = prefs.getString("Notification.SmallIcon." + id, null);
        params.SmallIconColor = prefs.getInt("Notification.SmallIconColor." + id, 0);
        params.ExecuteMode = prefs.getInt("Notification.ExecuteMode." + id, 0);
        params.UnityClass = prefs.getString("Notification.UnityClass." + id, null);
        String[] vibration = prefs.getString("Notification.Vibration." + id, "").replace("[", "").replace("]", "").replace(" ", "").split(",");
        params.Vibration = new long[vibration.length];
        int i = 0;
        while (i < vibration.length) {
            params.Vibration[i] = Long.parseLong(vibration[i]);
            ++i;
        }
        return params;
    }

    public static void RemoveNotification(Context context, int id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String notifications = prefs.getString("Notifications", null);
        if (notifications == null) {
            return;
        }
        LinkedList<String> ids = new LinkedList<>(Arrays.asList(notifications.split(",")));
        ids.removeAll(Collections.singletonList(Integer.toString(id)));
        notifications = TextUtils.join(",", ids);
        if (notifications.equals("")) {
            notifications = null;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Notifications", notifications);
        editor.remove("Notification.TriggerAtMillis." + id);
        editor.remove("Notification.Title." + id);
        editor.remove("Notification.Message." + id);
        editor.remove("Notification.Ticker." + id);
        editor.remove("Notification.Sound." + id);
        editor.remove("Notification.Vibrate." + id);
        editor.remove("Notification.Vibration." + id);
        editor.remove("Notification.Light." + id);
        editor.remove("Notification.LightOnMs." + id);
        editor.remove("Notification.LightOffMs." + id);
        editor.remove("Notification.LightColor." + id);
        editor.remove("Notification.LargeIcon." + id);
        editor.remove("Notification.SmallIcon." + id);
        editor.remove("Notification.SmallIconColor." + id);
        editor.remove("Notification.ExecuteMode." + id);
        editor.remove("Notification.UnityClass." + id);
        editor.apply();
    }
}

