package com.freesth;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.parse.ConfigCallback;
import com.parse.FindCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginDispatchActivity;

/**
 * Activity which starts an intent for either the logged in (PostListActivity)
 * or logged out (SignUpOrLoginActivity) activity.
 */
public class DispatchActivity extends ParseLoginDispatchActivity {

	List<ParseObject> app_version;



	@Override
	protected Class<?> getTargetClass() {
		return ListPostActivity.class; //TODO override for testing
	}

}
