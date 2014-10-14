package com.freesth;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import converters.User;

public class ListAuthorPostActivity extends ActionBarActivity implements
		TabListener {

	ListView listView;
	String userid,username=null;
	ParseUser returnedUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = this.getIntent();
		if (i == null) {
			throw new RuntimeException("Sorry no intent found");
		}
		// intent is available
		userid = i.getStringExtra("userid");
		username = i.getStringExtra("username");
		if (userid == null||username==null) {
			throw new RuntimeException("post id not found");
		}
		
		setContentView(R.layout.simple_list);
		listView = (ListView) findViewById(R.id.List);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab()
				.setText(R.string.tab1_listAuthorPost).setTabListener(this));
		actionBar.addTab(actionBar.newTab()
				.setText(R.string.tab2_listAuthorPost).setTabListener(this));
		actionBar.addTab(actionBar.newTab()
				.setText(R.string.tab3_listAuthorPost).setTabListener(this));
		actionBar.setTitle(username
				+ "'s Activities");

	}

	private void populateAuthorPostList(String userid) {
		// reconstruct a ParseUser for query
		ParseUser p = new ParseUser();
		p.setObjectId(userid);
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				Post.t_tablename);
		query.whereEqualTo(Post.f_createdBy, p);
		query.include(Post.f_createdBy);
		query.orderByDescending(Post.f_updatedAt);
		query.orderByDescending(Post.f_postStatus);
		// // query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		// //use cache first, then get query from network
		// // query.setMaxCacheAge(100000L); //unit in milliseconds, 100000L
		// represent long data type = 100s
		//
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					// The query was successful.
					successfulQuery(objects);
				} else {
					// Something went wrong.
					Failed(e);
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

	private void Failed(ParseException x) {
		new AlertDialog.Builder(ListAuthorPostActivity.this)
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
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
		if (tab.getPosition() == 0) {
			populateAuthorPostList(userid);
		} else if (tab.getPosition()==1){
			Log.d("ListAuthorPost", "populate Follow list");
			populateFollowList(userid);
		} else{
			Log.d("ListAuthorPost", "populate Trade list");
			populateTradeList(userid);
		}
	}

	private void populateFollowList(String userid) {
		ParseUser p = new ParseUser();
		p.setObjectId(userid);
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				Follow.t_tablename);
		query.whereEqualTo(Follow.f_createdBy, p);
		query.include(Follow.f_post);
		query.include(Follow.f_createdBy);
		query.orderByDescending(Follow.f_createdBy);
		// // query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		// //use cache first, then get query from network
		// // query.setMaxCacheAge(100000L); //unit in milliseconds, 100000L
		// represent long data type = 100s
		//
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					// The query was successful.
					successfulFolQuery(objects);
				} else {
					// Something went wrong.
					Failed(e);
				}
			}
		});
	}
	
	private void populateTradeList(String userid) {
		ParseUser p = new ParseUser();
		p.setObjectId(userid);
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				Post.t_tablename);
		query.whereEqualTo(Post.f_trader, p);
		query.include(Post.f_createdBy);
		query.orderByDescending(Post.f_createdBy);
		// // query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		// //use cache first, then get query from network
		// // query.setMaxCacheAge(100000L); //unit in milliseconds, 100000L
		// represent long data type = 100s
		//
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					// The query was successful.
					successfulQuery(objects);
				} else {
					// Something went wrong.
					Failed(e);
				}
			}
		});
	}
	
	private void successfulFolQuery(List<ParseObject> objects) {
		ArrayList<Follow> followList = new ArrayList<Follow>();
		for (ParseObject po : objects) {
			Follow puw = new Follow(po);
			followList.add(puw);
		}

		FollowListAdapter listItemAdapter = new FollowListAdapter(this, followList);
		listView.setAdapter(listItemAdapter);
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {

	}
}
