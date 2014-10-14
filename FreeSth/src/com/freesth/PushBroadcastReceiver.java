package com.freesth;

import java.net.URI;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseUser;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost.Settings;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PushBroadcastReceiver extends BroadcastReceiver {
	public static final String ACTION = "com.freesth.push";
	public static final String PARSE_EXTRA_DATA_KEY = "com.parse.Data";
	public static final String PARSE_JSON_ALERT_KEY = "alert";
	public static final String PARSE_JSON_CHANNELS_KEY = "com.parse.Channel";

	private static final String TAG = "PushBroadcastReceiver";

	Boolean isPushAll, isPushMyPost, isPushFav = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {

			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(context);
			isPushAll = settings.getBoolean("pref_push_all", true);
			isPushMyPost = settings.getBoolean("pref_push_mypost", true);
			isPushFav = settings.getBoolean("pref_push_favorite", true);
			Log.d(TAG,"isPushAll ="+isPushAll.toString()+", isPushMyPost) ="+isPushMyPost.toString()+", isPushFav = "+isPushFav.toString());
			JSONObject json = new JSONObject(intent.getExtras().getString(
					PARSE_EXTRA_DATA_KEY));

			if (!json.getString("author").equals( // all notification should be
													// activities done by others
					ParseUser.getCurrentUser().getString("name"))) {
				if (json.getString("oriAuthor").equals( // if I am the Post
														// author
						ParseUser.getCurrentUser().getString("name"))) {
					if (isPushAll)
						if (isPushMyPost)
							notify(context, intent, json, "forSeller");
				} else { // I am a buyer, only listen to comments by the Post
							// author
					if (json.getString("oriAuthor").equals(
							json.getString("author"))) {
						if (isPushAll)
							if (isPushFav)
								notify(context, intent, json, "forBuyer");
					}

				}

			}
		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}

	private void notify(Context ctx, Intent i, JSONObject dataObject,
			String pushType) throws JSONException {
		NotificationManager nm = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// int icon = R.drawable.ic_launcher;
		String title = dataObject.getString("type");
		String msg;
		// String tickerText;
		int mNotificationId = 1;
		;

		// long when = System.currentTimeMillis();
		NotificationCompat.Builder nb = new NotificationCompat.Builder(ctx)
				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(title)
				.setDefaults(Notification.DEFAULT_VIBRATE).setAutoCancel(true);

		if (title.equals("New Comment")) {
			mNotificationId = 1;
			if (pushType.equals("forSeller"))
				msg = "There are new comments in your post.";
			else
				msg = dataObject.getString("oriAuthor")
						+ " has posted a new comment.";
		} else if (title.equals("New Follow")) {
			mNotificationId = 2;
			msg = "There are new follows in your post.";
		} else {
			mNotificationId = 3;
			msg = dataObject.getString("oriAuthor") + " has updated the post.";
		}

		nb.setContentText(msg);
		// Notification n = new Notification(icon, tickerText, when);

		// Let the intent invoke the respond activity
		Intent intent = new Intent(ctx, ViewPostActivity.class);
		// Load it with parse data
		intent.putExtra("com.parse.Data",
				dataObject.getString(Post.PARCELABLE_POST_ID));
		int requestID = (int) System.currentTimeMillis(); //http://stackoverflow.com/questions/3009059/android-pending-intent-notification-problem
		PendingIntent pi = PendingIntent.getActivity(ctx, requestID, intent, 0);

		nb.setContentIntent(pi);

		// Builds the notification and issues it.
		nm.notify(dataObject.getString(Post.PARCELABLE_POST_ID),
				mNotificationId, nb.build());

	}
}// eof-class