package com.freesth;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//ArrayAdapter holds the data/rows in a list or array
//it passes this row object to getView with a position
public class CommentListAdapter 
extends ArrayAdapter<Comment>
implements View.OnClickListener
{

	private static String tag = "CommentListAdapter";
	
	//String[] wordArray;
	Context ctx = null;
	LayoutInflater lif = null;
	
	public CommentListAdapter(Context context, 
			List<Comment> commentList)
	{
	  	  super(context
	  			   ,R.layout.comment_list_item_layout
	  			   ,R.id.pcl_CommentId
	  			   ,commentList);
	  	  ctx = context;
	      lif = LayoutInflater.from(ctx);
	}
	
	//This class saves references to the buttons that are displayed
	//for each row. if there are 10 rows then there are 10 buttons
	//and 10 instances of view holder
	static class ViewHolder
	{
		public ViewHolder(View rowView, View.OnClickListener ocl)
		{
			tv_commentAuthor =
				(TextView)rowView.findViewById(R.id.pcl_CommentAuthorId);
			tv_commentAuthor.setOnClickListener(ocl);
			tv_comment =
				(TextView)rowView.findViewById(R.id.pcl_CommentId);
			tv_date = (TextView)rowView.findViewById(R.id.pcl_DateId);
		}
		
		//Holds the meaning line
		public TextView tv_commentAuthor, tv_comment, tv_date;
		//Model Object representing word meaning
		public Comment postCommentAtThisRow;
		
		//should be user/date who created this meaning
	}

	//For a given position return a view
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder vh;
		View thisView;
		if (convertView == null)
		{
			//create the view
			View rowView =
				lif.inflate(R.layout.comment_list_item_layout,null);
			vh = new ViewHolder(rowView,this);
			rowView.setTag(vh);
			thisView = rowView;
		}
		else
		{
			//populate the view
			vh = (ViewHolder)convertView.getTag();
			thisView = convertView;
		}
		//got a view holder
		//and a view.
		
		Comment curpostComment = this.getItem(position);
		vh.tv_commentAuthor.setText(curpostComment.getCreatedBy().getString("name"));
		vh.tv_comment.setText(curpostComment.getComment());
		vh.tv_date.setText(android.text.format.DateFormat.format("dd-MMM kk:mm", curpostComment.getCreatedAt()).toString());
		//Notice how we are placing the tags
		//in the respective buttons
		
		vh.postCommentAtThisRow = curpostComment;
		return thisView;
	}

	@Override
	public void onClick(View arg0) {
		
	}
	
}//eof-class
