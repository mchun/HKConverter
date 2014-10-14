package com.freesth;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class BrowseMyPostActivity extends ActionBarActivity {

	ListView listView;
	String postType = "wants";
	Post parceledPost;
	ParseUser returnedUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_list);
		listView = (ListView) findViewById(R.id.List);

		final ActionBar actionBar = getSupportActionBar();

		actionBar.setTitle("My Posts");

		populateAuthorPostList(ParseUser.getCurrentUser());
	}

	private void populateAuthorPostList(ParseUser author) {
		// reconstruct a ParseUser for query
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				Post.t_tablename);
		query.whereEqualTo(Post.f_createdBy, author);
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
		new AlertDialog.Builder(BrowseMyPostActivity.this)
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