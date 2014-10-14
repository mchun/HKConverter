package com.freesth;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ListFollowActivity extends ActionBarActivity {

	ProgressDialog pd = null;
	ListView followList;
	Post parceledPost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_list);
		followList = (ListView) findViewById(R.id.List);
		parceledPost = getParceledPostFromIntent();
		populateFollowList(parceledPost);
		this.setTitle("List of Followers");
	}

	private void populateFollowList(Post post) {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				Follow.t_tablename);
		query.whereEqualTo(Follow.f_post, post.po);
		query.orderByAscending(Follow.f_createdAt);

		// Include who created me
		query.include(Follow.f_createdBy);

		// Include who the parent word is
		// query.include(Comment.f_post);

		// How can I include the owner of the word
		// query.include(WordMeaning.f_word + "." + Word.f_createdBy);
		pd = ProgressDialog.show(this, "Loading",
				"Please wait...");
		pd.setCancelable(true);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				pd.cancel();
				if (e == null) {
					// The query was successful.
					successfulFolQuery(objects);
				} else {
					// Something went wrong.
					queryFailure(e);
				}
			}
		});
	}

	private Post getParceledPostFromIntent() {
		Intent i = this.getIntent();
		if (i == null) {
			throw new RuntimeException("Sorry no intent found");
		}
		// intent is available
		String postId = i.getStringExtra(Post.PARCELABLE_POST_ID);
		if (postId == null) {
			throw new RuntimeException("post id not found");
		}

		ParseObjectWrapper pow = (ParseObjectWrapper) i
				.getParcelableExtra(Post.t_tablename);

		if (pow == null) {
			throw new RuntimeException("ParceledPost not found");
		}
		Post parceledPost = new Post(pow);
		return parceledPost;
	}

	private void successfulFolQuery(List<ParseObject> objects) {
		ArrayList<Follow> followArrayList = new ArrayList<Follow>();
		for (ParseObject po : objects) {
			Follow puw = new Follow(po);
			followArrayList.add(puw);
		}

		PostFollowListAdapter folListItemAdapter = new PostFollowListAdapter(
				this, followArrayList);
		followList.setAdapter(folListItemAdapter);
	}

	private void queryFailure(ParseException x) {
		new AlertDialog.Builder(ListFollowActivity.this)
				.setTitle("Error")
				.setMessage(x.getMessage())
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}
}
