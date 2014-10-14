package com.freesth;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.ParseFile;
import com.squareup.picasso.Picasso;

import converters.User;

//ArrayAdapter holds the data/rows in a list or array
//it passes this row object to getView with a position
public class PostListAdapter extends ArrayAdapter<Post> implements
		View.OnClickListener {

	private static String tag = "PostListAdapter";

	// String[] postArray;
	Context ctx = null;
	LayoutInflater lif = null;

	public PostListAdapter(Context context, List<Post> postList) {
		super(context, R.layout.post_list_item_relative_layout,
				R.id.postListItemDescId, postList); // 3rd component not sure
		ctx = context;
		lif = LayoutInflater.from(ctx);
	}

	// This class saves references to the buttons that are displayed
	// for each row. if there are 10 rows then there are 10 buttons
	// and 10 instances of view holder
	static class ViewHolder {
		public ViewHolder(View rowView, View.OnClickListener ocl) {
//			editButton = (ImageView) rowView
//					.findViewById(R.id.postListEditBtnId);
			itemDescTextView = (TextView) rowView
					.findViewById(R.id.postListItemDescId);
			coverPicImgView = (ImageView) rowView.findViewById(R.id.coverFoto);
			authorTextView = (TextView) rowView
					.findViewById(R.id.postListAuthorId);
			profilePic = (ProfilePictureView) rowView
					.findViewById(R.id.userProfilePicture);
			profilePic.setOnClickListener(ocl);
//			editButton.setOnClickListener(ocl);
			coverPicImgView.setOnClickListener(ocl);
			authorTextView.setOnClickListener(ocl);
			itemDescTextView.setOnClickListener(ocl);
			//			rowView.setOnClickListener(ocl);
		}

		public TextView itemDescTextView;
		public TextView authorTextView;
//		public ImageView editButton;
		public ImageView coverPicImgView;
		public ProfilePictureView profilePic;
		// this is the object
		public Post postAtThisRow;
	}

	// For a given position return a view
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		View thisView;
		if (convertView == null) {
			// create the view
			View rowView = lif.inflate(R.layout.post_list_item_relative_layout,
					null);
			vh = new ViewHolder(rowView, this);
			rowView.setTag(vh);
			thisView = rowView;
		} else {
			// populate the view
			vh = (ViewHolder) convertView.getTag();
			thisView = convertView;
		}
		// got a view holder
		// and a view.

		final Post curpost = this.getItem(position);
		//		if (curpost.getPostType().equals("free"))
		//			vh.authorTextView.setText(curpost.getAuthor() + " frees");
		//		else
		vh.authorTextView.setText(curpost.titleText());
		vh.itemDescTextView.setText(curpost.getPostDesc());
		//		if (curpost.getCreatedBy().getString("fbid") != null) {
		//			if (curpost.getCreatedBy().getString("fbid") != null) {
		//				String facebookId = curpost.getCreatedBy().getString("fbid");
		//				vh.profilePic.setProfileId(facebookId);
		//			} else {
		//				vh.profilePic.setProfileId("0");
		//			}
		//		} else {
		//			vh.profilePic.setProfileId("0");
		//		}
		vh.profilePic.setProfileId(curpost.getFBid());
		//		if (curpost.getCreatedBy().getObjectId()
		//				.equals(ParseUser.getCurrentUser().getObjectId())) {
		//			vh.editButton.setVisibility(View.VISIBLE);
		//		} else {
		//			vh.editButton.setVisibility(View.GONE);
		//		}
//		vh.editButton.setVisibility(curpost.editBtnStatus());
		ParseFile photoFile = curpost.getPhotoFile(1); // code abit7
		if (photoFile != null) {
			vh.coverPicImgView.setVisibility(View.VISIBLE);
			Picasso.with(ctx).load(photoFile.getUrl())
					.placeholder(R.drawable.placeholder).fit().centerCrop()
					.into(vh.coverPicImgView);
		} else {
			vh.coverPicImgView.setVisibility(View.GONE);
		}
		//		if (curpost.getPostStatus().equals("Closed"))
		//			vh.authorTextView.setTextColor(Color.RED);
		//		else if (curpost.getPostStatus().equals("Completed"))
		//			vh.authorTextView.setTextColor(Color.GRAY);
		//		else
		vh.authorTextView.setTextColor(curpost.titleColour());
		// Notice how we are placing the tags
		// in the respective buttons
//		vh.editButton.setTag(curpost);
		vh.authorTextView.setTag(curpost.getCreatedByUser());
		vh.coverPicImgView.setTag(curpost);
		vh.itemDescTextView.setTag(curpost);
		vh.profilePic.setTag(curpost.getCreatedByUser());
		vh.postAtThisRow = curpost;
		return thisView;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.postListAuthorId)// click on post
		// anywhere
		{
			Log.d(tag, "clicked author name");
			listAuthorPost((User) v.getTag());
//		} else if (v.getId() == R.id.postListEditBtnId) {
//			Log.d(tag, "clicked edit Button");
//			editPost((Post) v.getTag());
		} else if (v.getId() == R.id.coverFoto) {
			Log.d(tag, "clicked coverFoto");
			viewPost((Post) v.getTag());
		} else if (v.getId() == R.id.postListItemDescId) {
			Log.d(tag, "clicked item desc");
			viewPost((Post) v.getTag());
		} else if (v.getId() == R.id.userProfilePicture) {
			Log.d(tag, "clicked profile pic");
			listAuthorPost((User) v.getTag());
		}
	}

	private void listAuthorPost(User user) {
		Intent i = new Intent(ctx, ListAuthorPostActivity.class);
		i.putExtra("userid", user.userid);
		i.putExtra("username", user.username);
		ctx.startActivity(i);
	}

//	private void editPost(Post postRef) {
//		Intent i = new Intent(ctx, EditPostActivity.class);
//		i.putExtra(Post.PARCELABLE_POST_ID, postRef.po.getObjectId());
//		for (int j = 1; j < 4; j++) {
//			if (postRef.getPhotoFile(j) != null)
//				i.putExtra("Pic" + j, postRef.getPhotoFile(j).getUrl());
//		}
//		i.putExtra(Post.t_tablename, postRef);
//		ctx.startActivity(i);
//	}

	private void viewPost(Post postRef) {
		Intent i = new Intent(ctx, ViewPostActivity.class);
		i.putExtra(Post.PARCELABLE_POST_ID, postRef.po.getObjectId());
		for (int j = 1; j < 4; j++) {
			if (postRef.getPhotoFile(j) != null)
				i.putExtra("Pic" + j, postRef.getPhotoFile(j).getUrl());
		}
		i.putExtra(Post.t_tablename, postRef);
		ctx.startActivity(i);
	}
}// eof-class
