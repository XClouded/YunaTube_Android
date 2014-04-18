package ca.paulshin.yunatube.services;

import java.util.Locale;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YunaTubeApplication;
import ca.paulshin.yunatube.common.Constants;
import ca.paulshin.yunatube.common.Preference;
import ca.paulshin.yunatube.common.Utils;
import ca.paulshin.yunatube.common.WebViewFragment;
import ca.paulshin.yunatube.main.HotNewsActivity;
import ca.paulshin.yunatube.main.MainActivity;
import ca.paulshin.yunatube.services.broadcast.ChatDataChangeReceiver;
import ca.paulshin.yunatube.services.broadcast.ChatUserChangeReceiver;
import ca.paulshin.yunatube.services.broadcast.GCMBroadcastReceiver;
import ca.paulshin.yunatube.youtube.YouTubeActivity;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    
    public static final String EXTRA_DO_UPDATE = "do_update";

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
        	Utils.debug(extras.toString());
           
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
//                sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras);
            }
        }
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle bundle) {
    	String locale = getResources().getConfiguration().locale.toString();
    	boolean isKorean = Locale.KOREA.toString().contains(locale);
    	if (!isKorean) return;
    	
    	Utils.debug("Push: " + Preference.getBoolean(Constants.NOTIFICATION));
    	
    	if (TextUtils.equals(bundle.getString("MODULE"), "chat_user")) {
    		Intent intent = new Intent();
			intent.setAction(ChatUserChangeReceiver.ACTION);
			intent.putExtra(ChatUserChangeReceiver.EXTRA_STATUS, bundle.getString("STATUS"));
			intent.putExtra(ChatUserChangeReceiver.EXTRA_USER, bundle.getString("USER"));
			intent.putExtra(ChatUserChangeReceiver.EXTRA_TO, bundle.getString("TO"));
			YunaTubeApplication.broadcast.sendBroadcast(intent);
    	}
    	else if (TextUtils.equals(bundle.getString("MODULE"), "chat_data")) {
    		Intent intent = new Intent();
			intent.setAction(ChatDataChangeReceiver.ACTION);
			intent.putExtra(ChatDataChangeReceiver.EXTRA_USER, bundle.getString("USER"));
			intent.putExtra(ChatDataChangeReceiver.EXTRA_ICON_ID, bundle.getString("ICON_ID"));
			intent.putExtra(ChatDataChangeReceiver.EXTRA_TEXT, bundle.getString("TEXT"));
			YunaTubeApplication.broadcast.sendBroadcast(intent);
    	}
    	else if (TextUtils.equals("Y", bundle.getString("MARKET_UPDATE")) || TextUtils.equals("Y", bundle.getString("SURVEY")) || Preference.getBoolean(Constants.NOTIFICATION)) {
    		String titleText = bundle.getString("TITLE");
    		String contentText = bundle.getString("CONTENT");
    		String contentInfo = bundle.getString("CONTENT_INFO");
    		String urlText = bundle.getString("URL");
    		String youtubeText = bundle.getString("YTID");
    		boolean vibrate = TextUtils.equals("Y", bundle.getString("VIBRATE"));
    		
    		if (!YunaTubeApplication.debuggable) {
	    		// save title
	    		String prevNoticeTitle = "notice_title";
	    		if (Preference.getString(prevNoticeTitle).equals(titleText))
	    			return;
	    		Preference.put(prevNoticeTitle, titleText);
    		}
    		
    		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            PendingIntent contentIntent = null;
            Intent intent = null;
            
            if (TextUtils.equals("Y", bundle.getString("MARKET_UPDATE"))) {
            	intent = new Intent(Intent.ACTION_VIEW);
            	intent.setData(Uri.parse("market://details?id=ca.paulshin.yunatube"));
            }
            else if (TextUtils.equals("Y", bundle.getString("SURVEY"))) {
            	// Redirect to Survey activity
            	intent = new Intent(this, MainActivity.class);
    			String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
            	intent.putExtra(MainActivity.EXTRA_FRAGMENT, WebViewFragment.class);
            	intent.putExtra(WebViewFragment.EXTRA_TITLE, R.string.supermenu_survey);
            	intent.putExtra(WebViewFragment.EXTRA_URL, Constants.SURVEY_URL + deviceId);
            }
            else if (TextUtils.isEmpty(urlText) && TextUtils.isEmpty(youtubeText)) {
            	// Redirect to HotNews activity
            	intent = new Intent(this, HotNewsActivity.class);
            	intent.putExtra(HotNewsActivity.EXTRA_FROM_GCM, true);
            }
            else if (!TextUtils.isEmpty(urlText)) {
            	// Launch a browser
            	Uri uri = Uri.parse(urlText);
            	intent = new Intent(Intent.ACTION_VIEW, uri);
            }
            else if (!TextUtils.isEmpty(youtubeText)) {
            	// Redirect to YouTube activity
            	Bundle youtubeBundle = new Bundle();
            	Preference.put(YouTubeActivity.EXTRA_PUSH_YTID, youtubeText);
            	intent = new Intent(this, YouTubeActivity.class);
            	intent.putExtras(youtubeBundle);
            }

        	contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        	
            int bigIcons [] = {R.drawable.ic_notification_6, R.drawable.ic_notification_7, R.drawable.ic_notification_8, R.drawable.ic_notification_9};
            int randomIndex = (int)(Math.random() * bigIcons.length);
            
            // Vibration pattern
            int dot = 200;      // Length of a Morse Code "dot" in milliseconds
            int dash = 500;     // Length of a Morse Code "dash" in milliseconds
            int short_gap = 200;    // Length of Gap Between dots/dashes
            int medium_gap = 500;   // Length of Gap Between Letters
            int long_gap = 1000;    // Length of Gap Between Words
            long[] pattern = {
                0,  // Start immediately
                dot, short_gap, dot, short_gap, dash
            };
            
            Bitmap icon = BitmapFactory.decodeResource(getResources(), bigIcons[randomIndex]);
                        
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.notification_small)
            .setContentTitle(titleText)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText).setBigContentTitle(titleText).setSummaryText(contentInfo))
            .setLargeIcon(icon)
            .setContentText(contentText);
            
            if (vibrate)
            	mBuilder.setVibrate(pattern);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            
            // Ref: http://developer.android.com/guide/topics/ui/notifiers/notifications.html#ApplyStyle
    	}
    }
}