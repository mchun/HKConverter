package com.freesth;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.view.View;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import converters.ValueField;

public class Post extends ParseObjectWrapper {
	public static String t_tablename = "PostObject";

	public static String PARCELABLE_POST_ID = "PostObjectId";

	// Fields in Post
	public static String f_postDesc = "postDesc";
	public static String f_postType = "postType"; // free or sale or wanted
	public static String f_postStatus = "postStatus";
	public static String f_postTag = "postTag";
	public static String f_trader = "Trader";

	public Post(String postDesc, String postType, String tag) {
		super(t_tablename);
		setPostDesc(postDesc);
		setPostType(postType);
		setPostTag(tag);
		setPostStatus("Open");
	}

	public Post(ParseObject po) {
		super(po);
	}

	public Post(ParseObjectWrapper inPow) {
		super(inPow);
	}

	public String getPostDesc() {
		return po.getString(f_postDesc);
	}

	public void setPostDesc(String in) {
		po.put(f_postDesc, in);
	}

	public String getPostTag() {
		return po.getString(f_postTag);
	}

	public void setPostTag(String in) {
		po.put(f_postTag, in);
	}

	public String getPostType() {
		return po.getString(f_postType);
	}

	public void setPostType(String in) {
		po.put(f_postType, in);
	}

	public void setPhoto(ParseFile file, int i) {
		po.put("photo" + i, file);
	}

	public ParseFile getPhotoFile(int i) {
		return po.getParseFile("photo" + i);
	}

	public String getAuthor() {
		return getCreatedBy().getString("name");
	}

	public void setPostStatus(String postStatus) {
		po.put(f_postStatus, postStatus);
	}

	public String getPostStatus() {
		return po.getString(f_postStatus);
	}

	public ParseUser getTrader() {
		return (ParseUser) po.get(f_trader);
	}

	public String getFBid() {
		if (getCreatedBy().getString("fbid") == null)
			return "0";
		return getCreatedBy().getString("fbid");
	}

	public int editBtnStatus() {
		if (getCreatedBy().getObjectId().equals(
				ParseUser.getCurrentUser().getObjectId()))
			return View.VISIBLE;
		else
			return View.GONE;
	}

	public int titleColour() {
		if (getPostStatus().equals("Closed"))
			return Color.RED;
		else if (getPostStatus().equals("Completed"))
			return Color.GRAY;
		else
			return Color.BLACK;
	}

	public String titleText() {
		if (getPostType().equals("free"))
			return getAuthor() + " frees";
		else if (getPostType().equals("sale"))
			return getAuthor() + " sells";
		else
			return getAuthor() + " wants";
	}

	// have the children override this
	@Override
	public List<ValueField> getFieldList() {
		ArrayList<ValueField> fields = new ArrayList<ValueField>();
		fields.add(ValueField.getStringField(Post.f_postDesc));
		fields.add(ValueField.getStringField(Post.f_postType));
		fields.add(ValueField.getStringField(Post.f_postStatus));
		fields.add(ValueField.getStringField(Post.f_postTag));
		return fields;
	}

}// eof-class
