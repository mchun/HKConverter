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

import com.parse.DeleteCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

//ArrayAdapter holds the data/rows in a list or array
//it passes this row object to getView with a position
public class FollowListAdapter extends ArrayAdapter<Follow> implements
		View.OnClickListener {

	private static String tag = "FollowListAdapter";

	// String[] wordArray;
	Context ctx = null;
	LayoutInflater lif = null;

	public FollowListAdapter(Context context, List<Follow> followList) {
		super(context, R.layout.follow_list_item_layout, R.id.foll_DetailId,
				followList);
		ctx = context;
		lif = LayoutInflater.from(ctx);
	}

	// This class saves references to the buttons that are displayed
	// for each row. if there are 10 rows then there are 10 buttons
	// and 10 instances of view holder
	static class ViewHolder {
		public ViewHolder(View rowView, View.OnClickListener ocl) {
			tv_FollowDetail = (TextView) rowView
					.findViewById(R.id.foll_DetailId);
			itemDescTextView = (TextView) rowView
					.findViewById(R.id.foll_ItemDescId);
			coverPicImgView = (ImageView) rowView.findViewById(R.id.foll_coverFoto);
		}

		// Holds the meaning line
		public TextView itemDescTextView;
		public TextView tv_FollowDetail;
		public ImageView coverPicImgView;
		// Model Object representing word meaning
		public Follow postFollowAtThisRow;

		// should be user/date who created this meaning
	}

	// For a given position return a view
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		View thisView;
		if (convertView == null) {
			// create the view
			View rowView = lif.inflate(R.layout.follow_list_item_layout, null);
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

		Follow curpostFollow = this.getItem(position);
		vh.itemDescTextView.setText(curpostFollow.getPost().getPostDesc());
		vh.tv_FollowDetail.setText("followed this on "
				+ android.text.format.DateFormat.format("dd-MMM",
						curpostFollow.getCreatedAt()).toString());
		ParseFile photoFile = curpostFollow.getPost().getPhotoFile(1); // code abit7
		if (photoFile != null) {
			vh.coverPicImgView.setVisibility(View.VISIBLE);
			Picasso.with(ctx).load(photoFile.getUrl())
					.placeholder(R.drawable.placeholder).fit().centerCrop()
					.into(vh.coverPicImgView);
		} else {
			vh.coverPicImgView.setVisibility(View.GONE);
		}
		// Notice how we are placing the tags
		// in the respective buttons

		vh.postFollowAtThisRow = curpostFollow;
		return thisView;
	}

	@Override
	public void onClick(View v) {
		
	}

}// eof-class
