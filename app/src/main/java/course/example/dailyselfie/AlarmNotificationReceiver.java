package course.example.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

public class AlarmNotificationReceiver extends BroadcastReceiver {


    // Notification ID to allow for future updates
    private static final int MY_NOTIFICATION_ID = 1;
    private static final String TAG = "AlarmNotificationReceiver";

    // Notification Text Elements
    private final CharSequence tickerText = "It's time for selfie!";
    private final CharSequence contentTitle = "Time for selfie";
    private final CharSequence contentText = "Take a picture!";

    // Notification Action Elements
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        // The Intent to be used when the user clicks on the Notification View
        mNotificationIntent = new Intent(context, MainActivity.class);

        // The PendingIntent that wraps the underlying Intent
        mContentIntent = PendingIntent.getActivity(context, 0,
                mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        // Build the Notification
        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker(tickerText)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setAutoCancel(true).setContentTitle(contentTitle)
                .setContentText(contentText).setContentIntent(mContentIntent);

        // Get the NotificationManager
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Pass the Notification to the NotificationManager:
        mNotificationManager.notify(MY_NOTIFICATION_ID,
                notificationBuilder.build());

        // Log occurence of notify() call
        Log.i(TAG, "Sending notification at:"
                + DateFormat.getDateTimeInstance().format(new Date()));

    }
}
