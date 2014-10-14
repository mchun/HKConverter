package com.freesth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * Activity that displays the settings screen.
 */
public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		EditTextPreference etPhoneNum = (EditTextPreference) findPreference("prefUserPhone");
		etPhoneNum
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if (!phoneCorrect(newValue.toString())) {
							Toast.makeText(getApplicationContext(),
									"Incorrect number. Please retry.",
									Toast.LENGTH_SHORT).show();
							return false;
						}
						return true;

					}
				});
	}

	public boolean phoneCorrect(String phoneNumber) {
		if (!phoneNumber.substring(0, 4).equals("+852")) {
			Log.d("Splash", "substring " + phoneNumber.substring(0, 4));
			Log.d("Splash", "phone number not start with 852");
			return false;
		}
		if (phoneNumber.length() != 12)
			return false;
		return true;
	}
}