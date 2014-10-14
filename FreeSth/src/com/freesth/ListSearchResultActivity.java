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

public class ListSearchResultActivity extends ActionBarActivity {

	ProgressDialog pd = null;
	ListView listView;
	ParseUser returnedUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = this.getIntent();
		if (i == null) {
			throw new RuntimeException("Sorry no intent found");
		}
		// intent is available
		//		userid = i.getStringExtra("userid");
		//		username = i.getStringExtra("username");
		
		String postTag = i.getStringExtra("postTag");
		String postType = i.getStringExtra("postType");
		if (postTag == null || postType == null) {
			throw new RuntimeException("post tag not found");
		}
		setContentView(R.layout.simple_list);
		listView = (ListView) findViewById(R.id.List);
		populateSearchResultList(postTag, postType);
	}

	private void populateSearchResultList(String postTag, String postType) {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				Post.t_tablename);
		query.whereEqualTo(Post.f_postTag, postTag);
		query.whereEqualTo(Post.f_postType, postType);
		query.whereEqualTo(Post.f_postStatus, "Open");
		query.include(Post.f_createdBy);
		query.orderByDescending(Post.f_updatedAt);
		query.orderByDescending(Post.f_postStatus);
		// // query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		// //use cache first, then get query from network
		// // query.setMaxCacheAge(100000L); //unit in milliseconds, 100000L
		// represent long data type = 100s
		//
		pd = ProgressDialog.show(this, "Loading", "Please wait...");
		pd.setCancelable(true);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				pd.cancel();
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
		new AlertDialog.Builder(ListSearchResultActivity.this)
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
}
