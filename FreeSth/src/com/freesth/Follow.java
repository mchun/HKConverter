package com.freesth;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class Follow extends ParseObjectWrapper {
	//Design the table first
	public static String t_tablename = "FollowObject";
	public static String f_post = "post";

	public Follow(Post inParentPost) {
		super(t_tablename);
		setPost(inParentPost);
	}

	//Make sure there is a way to construct with a straight 
	//Parse object
	public Follow(ParseObject po) {
		//Create a check in the future if it is not of the same type
		super(po);
	}

	public void setPost(ParseObjectWrapper post) {
		po.put(f_post, post.po);
	}

	public Post getPost() {
		return new Post(po.getParseObject(f_post));
	}

	public String toString() {
		if (po == null)
			return "Other user";
		else
			return this.getCreatedByUser().username;
	}
}