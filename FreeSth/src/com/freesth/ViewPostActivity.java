package com.freesth;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/* Activity to view individual post
 */

public class ViewPostActivity extends ActionBarActivity {
	private Menu menu;
	Post parceledPost;
	ListView commentList;
	EditText commentNew;
	ImageView im_foto1;
	ImageView im_foto2;
	ImageView im_foto3;
	ImageView edButton;
	String pic1, pic2, pic3, followID = null;
	ArrayList<Comment> commentArrayList;
	CommentListAdapter listItemAdapter;
	Boolean isNewPost = false;
	public ProfilePictureView profilePic;
	public static final String PARSE_EXTRA_DATA_KEY = "com.parse.Data";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parceledPost = this.getParceledPostFromIntent(); // or recover new post
		setContentView(R.layout.activity_view_post);
		commentList = (ListView) findViewById(R.id.lvComments);
		commentList.setSmoothScrollbarEnabled(false);
		View header = getLayoutInflater().inflate(
				R.layout.activity_view_post_header, null);
		commentList.addHeaderView(header);
		TextView viewPostAuthor = (TextView) findViewById(R.id.viewPostAuthor);
		TextView viewPostDesc = (TextView) findViewById(R.id.viewPostDesc);
		TextView createdTime = (TextView) findViewById(R.id.createdTime);
		TextView postStatus = (TextView) findViewById(R.id.viewPostStatus);
		if (!parceledPost.getPostStatus().equals("Open")) {
			postStatus.setVisibility(View.VISIBLE);
			postStatus.setText("Status: " + parceledPost.getPostStatus());
		}
		im_foto1 = (ImageView) findViewById(R.id.foto1);
		im_foto2 = (ImageView) findViewById(R.id.foto2);
		im_foto3 = (ImageView) findViewById(R.id.foto3);
		edButton = (ImageView) findViewById(R.id.viewPostEditBtnId);
		profilePic = (ProfilePictureView) findViewById(R.id.viewPostProfilePic);
		profilePic.setProfileId(parceledPost.getCreatedByUser().userfbid);

		if (parceledPost.getPostStatus().equals("Open")) { // if post is open, allow format and comment
			View footer = getLayoutInflater().inflate(
					R.layout.activity_view_post_footer, null);
			commentList.addFooterView(footer);
			commentNew = (EditText) findViewById(R.id.etComment);
		}

		if (isNewPost) {
			if (parceledPost.getCreatedBy().getObjectId() // if this is my
															// post
					.equals(ParseUser.getCurrentUser().getObjectId())) {

				edButton.setVisibility(View.VISIBLE);
			}
			if (parceledPost.getPostType().equals("free"))
				viewPostAuthor.setText(parceledPost.getCreatedBy().getString(
						"name")
						+ " frees");
			else
				viewPostAuthor.setText(parceledPost.getCreatedBy().getString(
						"name")
						+ " wants");
		} else {
			if (parceledPost.getCreatedByUser().userid.equals(ParseUser // if this is my post
					.getCurrentUser().getObjectId())) {
				edButton.setVisibility(View.VISIBLE);
			}
			if (parceledPost.getPostType().equals("free"))
				viewPostAuthor.setText(parceledPost.getCreatedByUser().username
						+ " frees");
			else
				viewPostAuthor.setText(parceledPost.getCreatedByUser().username
						+ " wants");
		}

		viewPostDesc.setText(parceledPost.getPostDesc());
		createdTime.setText("created on "
				+ android.text.format.DateFormat.format("dd-MMM kk:mm",
						parceledPost.getCreatedAt()).toString());

		followID = readFollowLocal(parceledPost.po.getObjectId());
		if (pic1 != null)
			Picasso.with(this.getBaseContext()).load(pic1)
					.placeholder(R.drawable.placeholder).into(this.im_foto1);
		if (pic2 != null)
			Picasso.with(this.getBaseContext()).load(pic2)
					.placeholder(R.drawable.placeholder).into(this.im_foto2);
		if (pic3 != null)
			Picasso.with(this.getBaseContext()).load(pic3)
					.placeholder(R.drawable.placeholder).into(this.im_foto3);
		commentArrayList = new ArrayList<Comment>();
		commentList.setAdapter(listItemAdapter);
		populateCommentList(parceledPost);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_post, menu);
		this.menu = menu;
		if (isNewPost) {
			if (parceledPost.getCreatedBy().getObjectId()
					.equals(ParseUser.getCurrentUser().getObjectId())) //new post and I am author
			{
				menu.getItem(0).setIcon(R.drawable.ic_action_person);
				menu.getItem(0).setTitle("Show Followers");
			} else {//new post and I am buyer
				//Default "Follow" icon appear
				if (followID != null)
					menu.getItem(0).setTitle("Unfollow");
				if (!parceledPost.getPostStatus().equals("Open"))
					menu.getItem(0).setVisible(false);
			}
		} else {
			if (parceledPost.getCreatedByUser().userid.equals(ParseUser
					.getCurrentUser().getObjectId())) //old post and I am author
			{
				menu.getItem(0).setIcon(R.drawable.ic_action_person);
				menu.getItem(0).setTitle("Show Followers");
			} else {//old post and I am buyer
				//Default "Follow" icon appear
				if (followID != null) // if i followed this post
					menu.getItem(0).setTitle("Unfollow");
				if (!parceledPost.getPostStatus().equals("Open"))
					menu.getItem(0).setVisible(false);
			}
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.action_follow) {
			if (isNewPost) {
				if (parceledPost.getCreatedBy().getObjectId()
						.equals(ParseUser.getCurrentUser().getObjectId())) //new post and I am author
				{
					Intent i = new Intent(this, ListFollowActivity.class);
					i.putExtra(Post.PARCELABLE_POST_ID,
							parceledPost.po.getObjectId());
					i.putExtra(Post.t_tablename, parceledPost);
					startActivity(i);
				} else
					toggleFollow();
			} else {
				if (parceledPost.getCreatedByUser().userid.equals(ParseUser //old post and I am author
						.getCurrentUser().getObjectId())) {
					Intent i = new Intent(this, ListFollowActivity.class);
					i.putExtra(Post.PARCELABLE_POST_ID,
							parceledPost.po.getObjectId());
					i.putExtra(Post.t_tablename, parceledPost);
					startActivity(i);
				} else
					toggleFollow();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private Post getParceledPostFromIntent() {
		Intent i = this.getIntent();
		if (i == null) {
			throw new RuntimeException("Sorry no intent found");
		}
		// intent is available
		if (i.getStringExtra("com.parse.Data") != null) {

			Log.d("get from push", i.getStringExtra("com.parse.Data"));
			try {
				ParseQuery<ParseObject> query = ParseQuery
						.getQuery(Post.t_tablename);
				query.include(Post.f_createdBy);
				Post po = new Post(
						query.get(i.getStringExtra("com.parse.Data")));
				if (po.getPhotoFile(1) != null)
					pic1 = po.getPhotoFile(1).getUrl();
				if (po.getPhotoFile(2) != null)
					pic2 = po.getPhotoFile(2).getUrl();
				if (po.getPhotoFile(3) != null)
					pic3 = po.getPhotoFile(3).getUrl();
				isNewPost = true;

				return po;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
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
		if (i.getStringExtra("Pic1") != null)
			pic1 = i.getStringExtra("Pic1");
		if (i.getStringExtra("Pic2") != null)
			pic2 = i.getStringExtra("Pic2");
		if (i.getStringExtra("Pic3") != null)
			pic3 = i.getStringExtra("Pic3");
		return parceledPost;
	}

	public void saveComment(View v) {
		String comment = this.commentNew.getText().toString();
		final Comment cm = new Comment(comment, parceledPost);
		cm.po.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					commentArrayList.add(cm);
					listItemAdapter.notifyDataSetChanged();
					commentNew.getText().clear();
					PushService.subscribe(getApplicationContext(), "post"
							+ parceledPost.po.getObjectId(),
							ViewPostActivity.class);
					Log.d("ViewPost", "post" + parceledPost.po.getObjectId());
					if (isNewPost) {
						if (!parceledPost.getCreatedBy().getObjectId().equals(
								ParseUser.getCurrentUser().getObjectId()))
							saveCommentLocal(cm.po.getObjectId());
					} else {
						if (!parceledPost.getCreatedByUser().userid.equals(ParseUser.getCurrentUser().getObjectId()))
								saveCommentLocal(cm.po.getObjectId());	
					}

					pushNewComment();
				} else {
					Failed(e);
				}
			}
		});
	}

	private JSONObject getJSONDataMessage(String type) {
		try {
			JSONObject data = new JSONObject();
			data.put(Post.PARCELABLE_POST_ID, parceledPost.po.getObjectId());
			data.put("action", PushBroadcastReceiver.ACTION);
			data.put("author", ParseUser.getCurrentUser().getString("name"));
			if (isNewPost)
				data.put("oriAuthor",
						parceledPost.getCreatedBy().getString("name"));
			else
				data.put("oriAuthor", parceledPost.getCreatedByUser().username);
			data.put("type", type);
			return data;
		} catch (JSONException x) {
			throw new RuntimeException("Something wrong with JSON", x);
		}
	}

	public void editPost(View v) {
		Intent i = new Intent(this, EditPostActivity.class);
		i.putExtra(Post.PARCELABLE_POST_ID, parceledPost.po.getObjectId());
		if (pic1 != null)
			i.putExtra("Pic1", pic1);
		if (pic2 != null)
			i.putExtra("Pic2", pic2);
		if (pic3 != null)
			i.putExtra("Pic3", pic3);
		i.putExtra(Post.t_tablename, parceledPost);
		startActivity(i);
	}

	public void listAuthorPost(View v) {
		Intent i = new Intent(this, ListAuthorPostActivity.class);
		if (isNewPost) {
			i.putExtra("userid", parceledPost.getCreatedBy().getObjectId());
			i.putExtra("username", parceledPost.getAuthor());
			Log.d("TAG","username is "+parceledPost.getCreatedBy().getUsername());
		} else {
			i.putExtra("userid", parceledPost.getCreatedByUser().userid);
			i.putExtra("username", parceledPost.getCreatedByUser().username);
		}
		startActivity(i);
	}

	public void toggleFollow() {
		if (followID != null) {
			ParseObject p = new ParseObject(Follow.t_tablename);
			p.setObjectId(followID);
			Log.d("delete FolloID: ", followID);
			p.deleteInBackground(new DeleteCallback() {
				@Override
				public void done(ParseException e) {
					if (e == null) {
						menu.getItem(0).setTitle("Follow");
						deleteFollowLocal(parceledPost.po.getObjectId());
					} else {
						Failed(e);
					}
				}
			});
		} else {
			new AlertDialog.Builder(ViewPostActivity.this)
					.setTitle("Follow this post?")
					.setMessage(
							"Your contact number will be shown to the author.")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									final Follow fl = new Follow(parceledPost);
									fl.po.saveInBackground(new SaveCallback() {
										@Override
										public void done(ParseException e) {

											if (e == null) {
												// no exception
												menu.getItem(0).setTitle(
														"Unfollow");
												saveFollowLocal(fl.po
														.getObjectId());
												PushService
														.subscribe(
																getApplicationContext(),
																"post"
																		+ parceledPost.po
																				.getObjectId(),
																ViewPostActivity.class);
												pushNewFollow();
											} else {
												Failed(e);
											}
										}
									});
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// continue with delete
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();

		}
	}

	private void saveFollowLocal(String follow) {
		LocalDBHandler db = new LocalDBHandler(this);
		db.addFollowRecord(ParseUser.getCurrentUser().getObjectId(),
				parceledPost.po.getObjectId(), follow);
		followID = follow;
		Log.d("toggle follow", "saved follow record locally");
	}

	private void saveCommentLocal(String comment) {
		LocalDBHandler db = new LocalDBHandler(this);
		db.addCommentRecord(ParseUser.getCurrentUser().getObjectId(),
				parceledPost.po.getObjectId(), comment);
		Log.d("ViewPost", "saved comment record locally");
	}

	private String readFollowLocal(String postID) {
		LocalDBHandler db = new LocalDBHandler(this);
		return db.findFollowRecord(postID);
	}

	private void deleteFollowLocal(String postID) {
		LocalDBHandler db = new LocalDBHandler(this);
		db.deleteFollowRecord(postID);
		followID = null;
		Log.d("toggle follow", "deleted follow record locally");
	}

	private void Failed(ParseException x) {
		new AlertDialog.Builder(ViewPostActivity.this)
				.setTitle("Error!")
				.setMessage(x.getMessage())
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	private void pushNewComment() {
		// send push message with parceledpost
		JSONObject data = getJSONDataMessage("New Comment");
		ParsePush push = new ParsePush();
		push.setChannel("post" + parceledPost.po.getObjectId());
		push.setData(data);
		push.sendInBackground();
	}

	private void pushNewFollow() {
		// send push message with parceledpost
		JSONObject data = getJSONDataMessage("New Follow");
		ParsePush push = new ParsePush();
		push.setChannel("post" + parceledPost.po.getObjectId());
		push.setData(data);
		push.sendInBackground();
	}

	private void populateCommentList(Post post) {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				Comment.t_tablename);
		query.whereEqualTo(Comment.f_post, post.po);
		query.orderByAscending(Comment.f_createdAt);
		query.include(Comment.f_createdBy);
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
		for (ParseObject po : objects) {
			Comment puw = new Comment(po);
			commentArrayList.add(puw);
		}

		listItemAdapter = new CommentListAdapter(this, commentArrayList);
		commentList.setAdapter(listItemAdapter);
	}

}
