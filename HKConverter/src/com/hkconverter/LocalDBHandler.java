package com.hkconverter;

import java.util.ArrayList;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBHandler extends SQLiteAssetHelper{
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "HKConverter";
	public static final String TABLE_WEIGHT = "Weight";
	public static final String TABLE_AREA = "Area";
	public static final String TABLE_SPEED = "Speed";
	public static final String TABLE_VOLUME = "Volume";
	public static final String TABLE_LENGTH = "Length";
	private static final String KEY_UNIT = "Unit";
	private static final String KEY_QTY = "Quantity";
//	private static final String TABLE_WEIGHT_CREATE = "CREATE TABLE "
//			+ TABLE_WEIGHT + " (" + KEY_UNIT + " TEXT, " + KEY_QTY + " TEXT);";
//	private static final String TABLE_AREA_CREATE = "CREATE TABLE "
//			+ TABLE_AREA + " (" + KEY_UNIT + " TEXT, " + KEY_QTY + " TEXT);";
//	private static final String TABLE_SPEED_CREATE = "CREATE TABLE "
//			+ TABLE_SPEED + " (" + KEY_UNIT + " TEXT, " + KEY_QTY + " TEXT);";
//	private static final String TABLE_LENGTH_CREATE = "CREATE TABLE "
//			+ TABLE_LENGTH + " (" + KEY_UNIT + " TEXT, " + KEY_QTY + " TEXT);";
//	private static final String TABLE_VOLUME_CREATE = "CREATE TABLE "
//			+ TABLE_VOLUME + " (" + KEY_UNIT + " TEXT, " + KEY_QTY + " TEXT);";

	public LocalDBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		setForcedUpgrade();
	}
//
//	@Override
//	public void onCreate(SQLiteDatabase db) {
//		db.execSQL(TABLE_WEIGHT_CREATE);
//		db.execSQL(TABLE_AREA_CREATE);
//		db.execSQL(TABLE_SPEED_CREATE);
//		db.execSQL(TABLE_LENGTH_CREATE);
//		db.execSQL(TABLE_VOLUME_CREATE);
//		
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		// Drop older table if existed
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AREA);
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT);
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPEED);
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LENGTH);
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOLUME);
//		// Create tables again
//		onCreate(db);
//	}
//
//	public void addRecord(String unit, String qty, String table) {
//		SQLiteDatabase db = this.getReadableDatabase();
//		ContentValues values = new ContentValues();
//		values.put(KEY_QTY, qty);
//		values.put(KEY_UNIT, unit);
//
//		// Inserting Row
//		db.insert(table, null, values);
//		db.close(); // Closing database connection
//	}

	public String findRecord(String unit, String table) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(table, new String[] { KEY_UNIT, KEY_QTY },
				KEY_UNIT + "=?", new String[] { unit }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		if (cursor.getCount() < 1) {
			cursor.close();
			db.close();
			return "1";
		}
		// return QTY
		String s = cursor.getString(1);
		cursor.close();
		db.close();
		return s;
	}

	public ArrayList<String> allUnits(String table) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(table, new String[] { KEY_UNIT, KEY_QTY },
				null, null, null, null, null, null);

		ArrayList<String> allUnits = new ArrayList<String>();
		if (cursor != null)
			cursor.moveToFirst();
		if (cursor.getCount() < 1) {
			cursor.close();
			db.close();
			return null;
		}
		// return postID

		for (int i = 0; i < cursor.getCount(); i++) {

			allUnits.add(cursor.getString(0));
			cursor.moveToNext();
		}

		cursor.close();
		db.close();
		return allUnits;
	}

	//	public void deleteFollowRecord(String postID) {
	//		SQLiteDatabase db = this.getWritableDatabase();
	//		db.delete(TABLE_FollowRec,
	//				KEY_POSTID + " = ? and " + KEY_USERID + "=?", new String[] {
	//						postID, ParseUser.getCurrentUser().getObjectId() });
	//		db.close();
	//	}
	//
	//	public void deleteAllFollowRecord(String userID) {
	//		SQLiteDatabase db = this.getWritableDatabase();
	//		db.delete(TABLE_FollowRec, KEY_USERID + "=?", new String[] { ParseUser
	//				.getCurrentUser().getObjectId() });
	//		db.close();
	//	}

	//	public ArrayList<String> allFavoritePost(String userID) {
	//		SQLiteDatabase db = this.getReadableDatabase();
	//		Cursor cursor = db.query(TABLE_CommentRec, new String[] { KEY_USERID,
	//				KEY_POSTID, KEY_COMMENTID }, KEY_USERID + "=?",
	//				new String[] { ParseUser.getCurrentUser().getObjectId() },
	//				null, null, null, null);
	//		//read commented posts
	//		ArrayList<String> allFavorite = new ArrayList<String>();
	//		if (cursor != null)
	//			cursor.moveToFirst();
	//		if (cursor.getCount() < 1) {
	//			cursor.close();
	//		} else {
	//			// return postID
	//			for (int i = 0; i < cursor.getCount(); i++) {
	//
	//				allFavorite.add(cursor.getString(1));
	//				cursor.moveToNext();
	//			}
	//			cursor.close();
	//		}
	//		//read followed posts
	//		cursor = db.query(TABLE_FollowRec, new String[] { KEY_USERID,
	//				KEY_POSTID, KEY_FOLLOWID }, KEY_USERID + "=?",
	//				new String[] { ParseUser.getCurrentUser().getObjectId() },
	//				null, null, null, null);
	//		if (cursor != null)
	//			cursor.moveToFirst();
	//		if (cursor.getCount() < 1) {
	//			cursor.close();
	//			db.close();
	//			return allFavorite;
	//		}
	//		for (int i = 0; i < cursor.getCount(); i++) {
	//			if (!allFavorite.contains(cursor.getString(1)))
	//				allFavorite.add(cursor.getString(1));
	//			cursor.moveToNext();
	//		}
	//		cursor.close();
	//		db.close();
	//		return allFavorite;
	//	}

	//	public void deleteAllCommentRecord(String userID) {
	//		SQLiteDatabase db = this.getWritableDatabase();
	//		db.delete(TABLE_CommentRec, KEY_USERID + "=?", new String[] { ParseUser
	//				.getCurrentUser().getObjectId() });
	//		db.close();
	//	}
	//
	//	public void deleteAllPostRecord(String userID) {
	//		SQLiteDatabase db = this.getWritableDatabase();
	//		db.delete(TABLE_PostRec, KEY_USERID + "=?", new String[] { ParseUser
	//				.getCurrentUser().getObjectId() });
	//		db.close();
	//	}

}
