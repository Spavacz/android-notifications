package powerup.androidnotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class RebootManager
extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String notifications = prefs.getString("Notifications", null);
        if (notifications == null) {
            return;
        }
        String[] ids = notifications.split(",");
        int i = 0;
        while (i < ids.length) {
            NotificationParams params = Storage.GetNotification(context, Integer.parseInt(ids[i]));
            if (params != null) {
                Controller.SetNotification(context, params);
            }
            ++i;
        }
    }
}

