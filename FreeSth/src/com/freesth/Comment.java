package com.freesth;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class Comment extends ParseObjectWrapper
{
	//Design the table first
	public static String t_tablename = "CommentObject";
	public static String f_post = "post";
	public static String f_comment = "comment";
	
	public Comment(String comment, Post inParentPost)
	{
		super(t_tablename);
		setComment(comment);
		setPost(inParentPost);
	}

	//Make sure there is a way to construct with a straight 
	//Parse object
	public Comment(ParseObject po)
	{
		//Create a check in the future if it is not of the same type
		super(po);
	}
	public void setComment(String comment)
	{
		po.put(f_comment, comment);
	}
	public void setPost(ParseObjectWrapper post)
	{
		po.put(f_post, post.po);
	}
	public String getComment()
	{
		return po.getString(f_comment);
	}
	public Post getPost()
	{
		return new Post(po.getParseObject(f_post));
	}
}