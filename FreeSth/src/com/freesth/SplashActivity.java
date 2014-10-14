package com.freesth;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

public class SplashActivity extends Activity {
	ParseConfig config = null;
	PackageInfo pInfo = null;
	Thread listPoThread = null;
	Thread syncLocalDB = null;
	final String PREFS_NAME = "MyPref";
	SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("splash", "start splash");
		setContentView(R.layout.initialization);
		//		settings = getSharedPreferences(PREFS_NAME, 0);
		ParseAnalytics.trackAppOpened(this.getIntent());
		
		listPoThread = new Thread() {

			@Override
			public void run() {
				boolean noNetwork = false;
				try {
					pInfo = getPackageManager().getPackageInfo(
							getPackageName(), 0);
					config = ParseConfig.get();
				} catch (Exception e) {
					noNetwork = true;
				}
				if (noNetwork) {
					showToast("Internet connection not available. Please retry later.");
					finish();//finish should be needed to close the activity, but seems not work inside catch block
				} else {
					if (!config.getString("AppVersion").equals(
							pInfo.versionName)) {
						Log.d("Splash", "app is outdated");
						showToast("Your app is outdated, please update.");
						finish();
					} else {
						Intent i = new Intent(SplashActivity.this,
								ListPostActivity.class);
						startActivity(i);
						finish();
					}
				}

			}
		};
		
		settings = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		//if firstrun, check phonenumber entered or not and sync local db for post, comment and follow
		if (settings.getBoolean("isFirstRun", true)) {
			settings.edit().putBoolean("isFirstRun", false).commit();
			//Telephone check
			if (ParseUser.getCurrentUser().get("telephone") == null) {
				TelephonyManager phoneManager = (TelephonyManager) getApplicationContext()
						.getSystemService(Context.TELEPHONY_SERVICE);
				final String phoneNumber = phoneManager.getLine1Number();
				//found phone number from simcard
				if (phoneNumber != null && phoneCorrect(phoneNumber)) {
					new AlertDialog.Builder(SplashActivity.this)
							.setTitle("Is this your number?")
							.setMessage(phoneNumber.substring(4, 12))
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											ParseUser p = ParseUser
													.getCurrentUser();
											p.put("telephone", phoneNumber);
											p.saveInBackground();
											SharedPreferences.Editor editor = settings
													.edit();
											editor.putString("prefUserName",
													ParseUser.getCurrentUser()
															.getString("name"));
											editor.putString("prefUserPhone",
													phoneNumber);
											editor.commit();
											listPoThread.start();
										}
									})
							.setNegativeButton("No",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											promptNumber();
										}
									})
							.setIcon(android.R.drawable.ic_dialog_alert).show();
				} else { //no sim card
					promptNumber();
				}
			} else { //have telephone in ParseUser
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("prefUserName", ParseUser.getCurrentUser()
						.getString("name"));
				editor.putString("prefUserPhone", ParseUser.getCurrentUser()
						.getString("telephone"));
				editor.commit();
				listPoThread.start();
			}

			//Synchronize comment and follow list for subscription and favorite post
			syncLocalDB = new Thread() {

				@Override
				public void run() {
					//For synchronizing db follow record
					Log.d("Splash","syncLocalDB run");
					ParseQuery<ParseObject> query = ParseQuery
							.getQuery(Follow.t_tablename);
					query.whereEqualTo(Follow.f_createdBy,
							ParseUser.getCurrentUser());
					query.include(Follow.f_post);
					query.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> objects,
								ParseException e) {
							if (e == null) {
								// The query was successful.
								deleteAllFollowLocal();
								for (ParseObject po : objects) {
									Follow puw = new Follow(po);
									saveFollowLocal(puw);
								}
							}
						}
					});

					//For synchronizing db comment record
					ParseQuery<ParseObject> q = ParseQuery
							.getQuery(Comment.t_tablename);
					q.whereEqualTo(Comment.f_createdBy,
							ParseUser.getCurrentUser());
					q.include(Comment.f_post);
					q.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> objects,
								ParseException e) {
							if (e == null) {
								// The query was successful.
								deleteAllCommentLocal();
								for (ParseObject pot : objects) {
									Comment cmt = new Comment(pot);
									saveCommentLocal(cmt);
								}
							}
						}
					});

					//For synchronizing db post record
					ParseQuery<ParseObject> qu = ParseQuery
							.getQuery(Post.t_tablename);
					qu.whereEqualTo(Post.f_createdBy,
							ParseUser.getCurrentUser());
					//		qu.include();
					qu.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> objects,
								ParseException e) {
							if (e == null) {
								// The query was successful.
								deleteAllPostLocal();
								for (ParseObject pot : objects) {
									Post pt = new Post(pot);
									savePostLocal(pt);
								}
							}
						}
					});
				}
			};
			syncLocalDB.start();
		}else{
			listPoThread.start();
		}
		
	}

	public void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(SplashActivity.this, toast, Toast.LENGTH_LONG)
						.show();
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

	public void promptNumber() {
		AlertDialog.Builder ad = new AlertDialog.Builder(SplashActivity.this);
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_PHONE);
		ad.setTitle("Contact number required")
				.setMessage("Enter your phone number:")
				.setView(input)
				.setCancelable(false)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String phoneNum = "+852" + input.getText();
								if (phoneCorrect(phoneNum)) {
									ParseUser p = ParseUser.getCurrentUser();
									p.put("telephone", phoneNum);
									p.saveInBackground();
									SharedPreferences.Editor editor = settings
											.edit();
									editor.putString("prefUserName", ParseUser
											.getCurrentUser().getString("name"));
									editor.putString("prefUserPhone", phoneNum);
									editor.commit();
									listPoThread.start();
								} else {
									showToast("Format incorrect.");
									promptNumber();
								}
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	private void saveFollowLocal(Follow fol) {
		LocalDBHandler db = new LocalDBHandler(this);
		db.addFollowRecord(ParseUser.getCurrentUser().getObjectId(),
				fol.getPost().po.getObjectId(), fol.po.getObjectId());
		PushService.subscribe(getApplicationContext(), "post"
				+ fol.getPost().po.getObjectId(), ViewPostActivity.class);
		Log.d("subcribe follow", fol.getPost().po.getObjectId());
		Log.d("initialize localdb", "saved follow locally");
	}

	private void deleteAllFollowLocal() {
		LocalDBHandler db = new LocalDBHandler(this);
		db.deleteAllFollowRecord(ParseUser.getCurrentUser().getObjectId());
		//		Set<String> setOfAllSubscriptions = PushService
		//				.getSubscriptions(getApplicationContext());
		//		Log.d("current subscription", setOfAllSubscriptions.toString());
		//		for (String sub : setOfAllSubscriptions) {
		//			if (!sub.equals("ch1"))
		//				PushService.unsubscribe(getApplicationContext(), sub);
		//		}
		Log.d("initialize localdb", "deleted all follow locally");
	}

	private void saveCommentLocal(Comment cmt) {
		if (!cmt.getPost().getCreatedBy().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
			LocalDBHandler db = new LocalDBHandler(this);
			db.addCommentRecord(ParseUser.getCurrentUser().getObjectId(),
					cmt.getPost().po.getObjectId(), cmt.po.getObjectId());
					PushService.subscribe(getApplicationContext(), "post"
							+ cmt.getPost().po.getObjectId(), ViewPostActivity.class);
			Log.d("subcribe comment", cmt.getPost().po.getObjectId());
			Log.d("initialize localdb", "saved comment locally");
		}
	}

	private void deleteAllCommentLocal() {
		LocalDBHandler db = new LocalDBHandler(this);
		db.deleteAllCommentRecord(ParseUser.getCurrentUser().getObjectId());
		Log.d("initialize localdb", "deleted all comment locally");
	}

	private void savePostLocal(Post pot) {
		LocalDBHandler db = new LocalDBHandler(this);
		db.addPostRecord(ParseUser.getCurrentUser().getObjectId(),
				pot.po.getObjectId());
				PushService.subscribe(getApplicationContext(),
						"post" + pot.po.getObjectId(), ViewPostActivity.class);
		Log.d("subcribe post", pot.po.getObjectId());
		Log.d("initialize localdb", "saved post locally");
	}

	private void deleteAllPostLocal() {
		LocalDBHandler db = new LocalDBHandler(this);
		db.deleteAllPostRecord(ParseUser.getCurrentUser().getObjectId());
		Log.d("initialize localdb", "deleted all post locally");
	}

}
