package com.freesth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.DeleteCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import converters.User;

//ArrayAdapter holds the data/rows in a list or array
//it passes this row object to getView with a position
public class PostFollowListAdapter extends ArrayAdapter<Follow> implements
		View.OnClickListener {

	private static String tag = "PostFollowListAdapter";

	//String[] wordArray;
	Context ctx = null;
	LayoutInflater lif = null;

	public PostFollowListAdapter(Context context, List<Follow> followList) {
		super(context, R.layout.post_follow_list_item_layout,
				R.id.pfl_Follower, followList);
		ctx = context;
		lif = LayoutInflater.from(ctx);
	}

	//This class saves references to the buttons that are displayed
	//for each row. if there are 10 rows then there are 10 buttons
	//and 10 instances of view holder
	static class ViewHolder {
		public ViewHolder(View rowView, View.OnClickListener ocl) {
			tv_follower = (TextView) rowView.findViewById(R.id.pfl_Follower);
			tv_date = (TextView) rowView.findViewById(R.id.pfl_DateId);
			tv_phone = (TextView) rowView.findViewById(R.id.pfl_Telephone);
			profilePic = (ProfilePictureView) rowView.findViewById(R.id.userProfilePicture);
			profilePic.setOnClickListener(ocl);
			tv_follower.setOnClickListener(ocl);
			tv_date.setOnClickListener(ocl);
		}

		//Holds the meaning line
		public TextView tv_follower, tv_date, tv_phone;
		//Model Object representing word meaning
		public Follow postFollowAtThisRow;
		public ProfilePictureView profilePic;
		//should be user/date who created this meaning
	}

	//For a given position return a view
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		View thisView;
		if (convertView == null) {
			//create the view
			View rowView = lif.inflate(R.layout.post_follow_list_item_layout,
					null);
			vh = new ViewHolder(rowView, this);
			rowView.setTag(vh);
			thisView = rowView;
		} else {
			//populate the view
			vh = (ViewHolder) convertView.getTag();
			thisView = convertView;
		}
		//got a view holder
		//and a view.

		Follow curpostFollow = this.getItem(position);
		vh.tv_follower.setText(curpostFollow.getCreatedBy().getString("name"));
		vh.tv_date.setText(android.text.format.DateFormat.format(
				"dd-MMM kk:mm", curpostFollow.getCreatedAt()).toString());
		vh.tv_phone
				.setText(curpostFollow.getCreatedBy().getString("telephone"));
		vh.profilePic.setProfileId(curpostFollow.getCreatedByUser().userfbid);
		//Notice how we are placing the tags
		//in the respective buttons

		//		vh.postFollowAtThisRow = curpostFollow;		
		vh.profilePic.setTag(curpostFollow.getCreatedByUser());
		vh.tv_date.setTag(curpostFollow.getCreatedByUser());
		vh.tv_follower.setTag(curpostFollow.getCreatedByUser());
		return thisView;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.pfl_Follower)
			listAuthorPost((User) v.getTag());
		if (v.getId() == R.id.pfl_DateId)
			listAuthorPost((User) v.getTag());
		if (v.getId() == R.id.userProfilePicture)
			listAuthorPost((User) v.getTag());
	}

	private void listAuthorPost(User user) {
		Intent i = new Intent(ctx, ListAuthorPostActivity.class);
		i.putExtra("userid", user.userid);
		i.putExtra("username", user.username);
		ctx.startActivity(i);
	}

}//eof-class
