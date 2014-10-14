package com.freesth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
//import com.handmark.pulltorefresh.library.PullToRefreshListView;
//import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class ListPostActivity extends ActionBarActivity implements TabListener {

	//	ProgressDialog pd = null;
	//	PullToRefreshListView ptrListView;
	ListView listView;
	String postType = "free";
	//	Boolean isPTR = false;
	private static final int writePoRequest = 1;
	private static final int settingRequest = 2;
	final String PREFS_NAME = "MyPref";
//	private MenuItem refresh;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("listpost", "start listpost");
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_list_post);
		listView = (ListView) findViewById(R.id.listview);
		//		ptrListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
		//			@Override
		//			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		//				String label = DateUtils.formatDateTime(
		//						getApplicationContext(), System.currentTimeMillis(),
		//						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
		//								| DateUtils.FORMAT_ABBREV_ALL);
		//				isPTR = true;
		//				// Update the LastUpdatedLabel
		//				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		//
		//				// Do work to refresh the list here.
		//				populatePostList(postType);
		//			}
		//		});
		//		listView = ptrListView.getRefreshableView();
		listView.setSmoothScrollbarEnabled(false);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section1)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section2)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section3)
				.setTabListener(this));
	}

//	@SuppressLint("NewApi")
	private void populatePostList(String postType) {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				Post.t_tablename);
		query.orderByDescending(Post.f_updatedAt);
		query.whereEqualTo(Post.f_postStatus, "Open");
		query.whereEqualTo(Post.f_postType, postType);
		query.include(Post.f_createdBy);
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); //use cache first, then get query from network
		query.setMaxCacheAge(15000L); //unit in milliseconds, 10000L represent long data type = 10s
//		if (refresh != null) {
//			if (android.os.Build.VERSION.SDK_INT >= 11) {
//				refresh.setActionView(R.layout.actionbar_indeterminate_progress);
//			} else {
//				MenuItemCompat.setActionView(refresh,
//						R.layout.actionbar_indeterminate_progress);
//			}
//		}
		setSupportProgressBarIndeterminateVisibility(true);
		query.findInBackground(new FindCallback<ParseObject>() {
//			@SuppressLint("NewApi")
			public void done(List<ParseObject> objects, ParseException e) {
//				if (refresh != null) {
//					if (android.os.Build.VERSION.SDK_INT >= 11) {
//						refresh.setActionView(null);
//					} else {
//						MenuItemCompat.setActionView(refresh, null);
//					}
//				}
				setSupportProgressBarIndeterminateVisibility(false);
				if (e == null) {
					// The query was successful.
					successfulQuery(objects);
				} else {
					// Something went wrong.
					queryFailure(e);
				}
			}
		});
	}

	private void successfulQuery(List<ParseObject> objects) {
		ArrayList<Post> postList = new ArrayList<Post>();
		for (ParseObject po : objects) {
			Post puw = new Post(po);
			postList.add(puw);
		}

		PostListAdapter listItemAdapter = new PostListAdapter(this, postList);
		listView.setAdapter(listItemAdapter);
	}

	private void queryFailure(ParseException x) {
		new AlertDialog.Builder(ListPostActivity.this)
				.setTitle("Error")
				.setMessage(x.getMessage())
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_post, menu);
//		refresh = menu.findItem(R.id.action_refresh);
		return true;
	}

	@Override
	public boolean onSearchRequested() {
		popUpSearch();
		return true;
	}

	private void popUpSearch() {
		final View promptsView = LayoutInflater.from(this).inflate(
				R.layout.search_pop_up, null);
		final Spinner mSpinner = (Spinner) promptsView
				.findViewById(R.id.searchCate);
		List<String> spinnerArray = ParseConfig.getCurrentConfig().getList(
				"Category");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(adapter);
		final RadioGroup radioGroup = (RadioGroup) promptsView
				.findViewById(R.id.radioGroup1);

		AlertDialog.Builder bd = new AlertDialog.Builder(ListPostActivity.this)
				.setTitle("Search")
				.setView(promptsView)
				.setIcon(android.R.drawable.ic_menu_search)
				.setPositiveButton(android.R.string.search_go,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								int selectedId = radioGroup
										.getCheckedRadioButtonId();
								RadioButton radioButton = (RadioButton) promptsView
										.findViewById(selectedId);
								String postType = "free";
								if (radioButton.getText().equals("Wanted"))
									postType = "wanted";
								else if (radioButton.getText().equals("Sale"))
									postType = "sale";
								else
									postType = "free";
								Intent i = new Intent(getBaseContext(),
										ListSearchResultActivity.class);
								i.putExtra("postTag", mSpinner
										.getSelectedItem().toString());
								i.putExtra("postType", postType);
								Map<String, String> dimensions = new HashMap<String, String>();
								dimensions.put("postTag", mSpinner
										.getSelectedItem().toString());
								dimensions.put("postType", postType);
								ParseAnalytics.trackEvent("search", dimensions);
								startActivity(i);
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						});
		bd.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//		int id = item.getItemId();

		switch (item.getItemId()) {
//		case R.id.action_refresh:
//			populatePostList(postType);
//			return true;
		case R.id.action_search:
			popUpSearch();
			return true;
		case R.id.action_my_post:
			Intent i = new Intent(this, BrowseMyPostActivity.class);
			startActivity(i);
			return true;
		case R.id.action_my_favorite:
			Intent l = new Intent(this, BrowseMyFavoriteActivity.class);
			startActivity(l);
			return true;
		case R.id.action_new:
			Intent j = new Intent(this, WritePostActivity.class);
			startActivityForResult(j, writePoRequest);
			return true;
		case R.id.action_settings:
			Intent k = new Intent(this, SettingsActivity.class);
			startActivityForResult(k, settingRequest);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent i) {
		if ((requestCode == writePoRequest)
				&& (resultCode == Activity.RESULT_OK)) {
			//			isPTR = false;
			populatePostList(postType);
		}
		if (requestCode == settingRequest) {
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			String phoneNum = settings.getString("prefUserPhone", "NULL");
			String name = settings.getString("prefUserName", "NULL");
			ParseUser p = ParseUser.getCurrentUser();
			p.put("telephone", phoneNum);
			p.put("name", name);
			p.saveInBackground();
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
		this.populatePostList(postType);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
		switch (tab.getPosition()) {
		case 0:
			postType = "free";
			break;
		case 1:
			postType = "sale";
			break;
		case 2:
			postType = "wanted";
			break;
		default:
			break;
		}
		//		isPTR = false;
		this.populatePostList(postType);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {

	}

	//	public boolean phoneCorrect(String phoneNumber) {
	//		if (!phoneNumber.substring(0, 4).equals("+852")) {
	//			Log.d("Splash", "substring " + phoneNumber.substring(0, 4));
	//			Log.d("Splash", "phone number not start with 852");
	//			return false;
	//		}
	//		if (phoneNumber.length() != 12)
	//			return false;
	//		return true;
	//	}
}
