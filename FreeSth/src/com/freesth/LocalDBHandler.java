package com.freesth;

import java.util.ArrayList;

import com.parse.ParseUser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "FreeSth";
	private static final String TABLE_FollowRec = "FollowRecord";
	private static final String TABLE_CommentRec = "CommentRecord";
	private static final String TABLE_PostRec = "PostRecord";
	private static final String KEY_USERID = "userID";
	private static final String KEY_POSTID = "postID";
	private static final String KEY_FOLLOWID = "followID";
	private static final String KEY_COMMENTID = "commentID";
	private static final String TABLE_COMMENT_CREATE = "CREATE TABLE "
			+ TABLE_CommentRec + " (" + KEY_USERID + " TEXT, " + KEY_POSTID
			+ " TEXT, " + KEY_COMMENTID + " TEXT);";
	private static final String TABLE_FOLLOW_CREATE = "CREATE TABLE "
			+ TABLE_FollowRec + " (" + KEY_USERID + " TEXT, " + KEY_POSTID
			+ " TEXT, " + KEY_FOLLOWID + " TEXT);";
	private static final String TABLE_POST_CREATE = "CREATE TABLE "
			+ TABLE_PostRec + " (" + KEY_USERID + " TEXT, " + KEY_POSTID
			+ " TEXT);";

	LocalDBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_COMMENT_CREATE);
		db.execSQL(TABLE_FOLLOW_CREATE);
		db.execSQL(TABLE_POST_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FollowRec);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CommentRec);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PostRec);
		// Create tables again
		onCreate(db);
	}

	public void addFollowRecord(String userID, String postID, String followID) {
		SQLiteDatabase db = this.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_USERID, userID);
		values.put(KEY_POSTID, postID); // Contact Name
		values.put(KEY_FOLLOWID, followID); // Contact Phone

		// Inserting Row
		db.insert(TABLE_FollowRec, null, values);
		db.close(); // Closing database connection
	}

	public String findFollowRecord(String postID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_FollowRec, new String[] { KEY_USERID,
				KEY_POSTID, KEY_FOLLOWID }, KEY_POSTID + "=? and " + KEY_USERID
				+ "=?", new String[] { postID,
				ParseUser.getCurrentUser().getObjectId() }, null, null, null,
				null);
		if (cursor != null)
			cursor.moveToFirst();
		if (cursor.getCount() < 1) {
			cursor.close();
			db.close();
			return null;
		}
		// return followID
		String s = cursor.getString(2);
		cursor.close();
		db.close();
		return s;
	}

	public ArrayList<String> allFollowedPost(String userID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_FollowRec, new String[] { KEY_USERID,
				KEY_POSTID, KEY_FOLLOWID }, KEY_USERID + "=?",
				new String[] { ParseUser.getCurrentUser().getObjectId() },
				null, null, null, null);

		ArrayList<String> allFollow = new ArrayList<String>();
		if (cursor != null)
			cursor.moveToFirst();
		if (cursor.getCount() < 1) {
			cursor.close();
			db.close();
			return null;
		}
		// return postID

		for (int i = 0; i < cursor.getCount(); i++) {

			allFollow.add(cursor.getString(1));
			cursor.moveToNext();
		}

		cursor.close();
		db.close();
		return allFollow;
	}

	public void deleteFollowRecord(String postID) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FollowRec,
				KEY_POSTID + " = ? and " + KEY_USERID + "=?", new String[] {
						postID, ParseUser.getCurrentUser().getObjectId() });
		db.close();
	}

	public void deleteAllFollowRecord(String userID) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FollowRec, KEY_USERID + "=?", new String[] { ParseUser
				.getCurrentUser().getObjectId() });
		db.close();
	}

	public void addCommentRecord(String userID, String postID, String commentID) {
		SQLiteDatabase db = this.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_USERID, userID);
		values.put(KEY_POSTID, postID); // Contact Name
		values.put(KEY_COMMENTID, commentID); // Contact Phone

		// Inserting Row
		db.insert(TABLE_CommentRec, null, values);
		db.close(); // Closing database connection
	}

	public ArrayList<String> allFavoritePost(String userID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CommentRec, new String[] { KEY_USERID,
				KEY_POSTID, KEY_COMMENTID }, KEY_USERID + "=?",
				new String[] { ParseUser.getCurrentUser().getObjectId() },
				null, null, null, null);
		//read commented posts
		ArrayList<String> allFavorite = new ArrayList<String>();
		if (cursor != null)
			cursor.moveToFirst();
		if (cursor.getCount() < 1) {
			cursor.close();
		} else {
			// return postID
			for (int i = 0; i < cursor.getCount(); i++) {

				allFavorite.add(cursor.getString(1));
				cursor.moveToNext();
			}
			cursor.close();
		}
		//read followed posts
		cursor = db.query(TABLE_FollowRec, new String[] { KEY_USERID,
				KEY_POSTID, KEY_FOLLOWID }, KEY_USERID + "=?",
				new String[] { ParseUser.getCurrentUser().getObjectId() },
				null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		if (cursor.getCount() < 1) {
			cursor.close();
			db.close();
			return allFavorite;
		}
		for (int i = 0; i < cursor.getCount(); i++) {
			if (!allFavorite.contains(cursor.getString(1)))
				allFavorite.add(cursor.getString(1));
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return allFavorite;
	}

	public void deleteAllCommentRecord(String userID) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CommentRec, KEY_USERID + "=?", new String[] { ParseUser
				.getCurrentUser().getObjectId() });
		db.close();
	}

	public void deleteAllPostRecord(String userID) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PostRec, KEY_USERID + "=?", new String[] { ParseUser
				.getCurrentUser().getObjectId() });
		db.close();
	}

	public void addPostRecord(String userID, String postID) {
		SQLiteDatabase db = this.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_USERID, userID);
		values.put(KEY_POSTID, postID); // Contact Name

		// Inserting Row
		db.insert(TABLE_PostRec, null, values);
		db.close(); // Closing database connection
	}

}
