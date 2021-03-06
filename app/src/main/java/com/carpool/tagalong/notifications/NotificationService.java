package com.carpool.tagalong.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.carpool.tagalong.R;
import com.carpool.tagalong.activities.ChatActivity;
import com.carpool.tagalong.activities.FreeRoamActivity;
import com.carpool.tagalong.activities.HomeActivity;
import com.carpool.tagalong.constants.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = NotificationService.class.getName();

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        try {
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                String message;
                message = remoteMessage.getData().get("body");
                try {
                    sendNotification(remoteMessage, message, remoteMessage.getData().get("title"), remoteMessage.getData().get("type"));
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    sendNotification(remoteMessage, remoteMessage.getData().get("body"), remoteMessage.getData().get("title"),"");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // [END receive_message]
    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(RemoteMessage remoteMessage, String messageBody, String title, String type) {

        Intent intent = null;

        if (type.equals(Constants.TYPE_PANIC_BUTTON)) {

        } else if (type.equals(Constants.TYPE_PICKUP)) {

            intent = new Intent(this, HomeActivity.class);
            intent.putExtra(Constants.START_RIDE,  Constants.CURRENTUPCOMING);
            Intent intent1 = new Intent(Constants.PICKED_UP);

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
        } else if (type.equals(Constants.TYPE_DROP)) {

            intent = new Intent(this, HomeActivity.class);
            intent.putExtra(Constants.START_RIDE, Constants.CURRENTUPCOMING);
            Intent intent1 = new Intent(Constants.DROPPED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
        } else if (type.equals(Constants.TYPE_QUICKRIDE)) {

            intent = new Intent(this, FreeRoamActivity.class);
            Intent intent1 = new Intent(Constants.RIDELISTENER);

            intent1.putExtra(Constants.RIDEID, remoteMessage.getData().get(Constants.RIDEID));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
        } else if (type.equals(Constants.TYPE_CHAT)) {

            String senderId = remoteMessage.getData().get(Constants.SENDER);
            intent = new Intent(this, ChatActivity.class);
            intent.putExtra(Constants.RECEIVERID, senderId);
            Intent intent1 = new Intent(Constants.CHAT_MESSAGE);
            intent1.putExtra(Constants.RECEIVERID, senderId);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
        } else if (title.contains("Payment")) {
            // nothing to do in payment notification because
        } else {
            intent = new Intent(this, HomeActivity.class);
            intent.putExtra(Constants.START_RIDE, title);
            Intent intent1 = new Intent(Constants.LAUNCH_CURRENT_RIDE_FRAGMENT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.app_name) + System.currentTimeMillis();

        Uri defaultSoundUri = null;

        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_tg)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(messageBody))
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(defaultSoundUri, audioAttributes);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}