package com.freesth;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

public class FreeSthApplication extends Application {

	private static String tag = "ParseApplication";

	private static String PARSE_APPLICATION_ID = "lkoo4xZX4aqk0uVz4a6oM1uArSe1dpEchHBpzDed";

	private static String PARSE_CLIENT_KEY = "MLDhaCrqsmJntEm9ZksWgmKujot51r2KL43VR4Fh";

	private static String FACEBOOK_APP_ID = "492809610856111";

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(tag, "initializing with keys");
		// Add your initialization code here
		Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);

		ParseFacebookUtils.initialize(FACEBOOK_APP_ID);
		// This will automatically create an annonymous user
		// The data associated to this user is abandoned when it is
		// logged out.
		// ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();

		// If you would like all objects to be private by default, remove this
		// line.
		defaultACL.setPublicReadAccess(true);
		defaultACL.setPublicWriteAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);

		
		//Enable to receive push
		PushService.setDefaultPushCallback(this, ListPostActivity.class); //default response activity for advanced push
		ParseInstallation pi = ParseInstallation.getCurrentInstallation();

//		//Register a channel to test push channels
//		PushService.subscribe(this.getApplicationContext(), "ch1",
//				ViewPostActivity.class); //default response activity for channel push

		pi.saveEventually();

		Log.d(tag, "initializing app complete");
	}

}
