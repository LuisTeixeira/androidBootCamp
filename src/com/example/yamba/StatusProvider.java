package com.example.yamba;

import winterwell.jtwitter.Twitter.Status;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class StatusProvider extends ContentProvider{
	
	public static final String AUTHORITY = "content://com.example.yamba.provider.status";
	public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);

	public static final String TABLE = "status";
	public static final String C_ID = BaseColumns._ID;
	public static final String C_CREATED_AT = "created_at";
	public static final String C_USER = "username";
	public static final String C_TEXT = "status_text";
	public static final String DB_NAME = "timeline.db";
	public static final int DB_VERSION = 1;
	
	SQLiteDatabase db;
	DbHelper dbHelper;
	
	@Override
	public boolean onCreate() {
		dbHelper = new DbHelper(getContext());
		return true;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		db = dbHelper.getWritableDatabase();
		long id = db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		if(id != -1)
			uri = Uri.withAppendedPath(uri, Long.toString(id));
		return uri;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		db = dbHelper.getReadableDatabase();
		return db.query(TABLE, projection, selection, selectionArgs, null, null, sortOrder);
	}
	
	@Override
	public String getType(Uri uri) {
		if(uri.getLastPathSegment()==null)
			return "vnd.android.cursor.item/vnd.example.yamba.status";
		else
			return "vnd.android.cursor.dir/vnd.example.yamba.status";
	}
	
	public static ContentValues statusToValues(Status status){
		ContentValues values = new ContentValues();
		values.put(C_ID, status.id);
		values.put(C_CREATED_AT, status.createdAt.getTime());
		values.put(C_USER, status.user.name);
		values.put(C_TEXT, status.text);
		return values;
	}
	
	
	class DbHelper extends SQLiteOpenHelper {

		public static final String TAG = "DbHelper";

		public DbHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = String.format("create table %s "
					+ "(%s int primary key, %s int, %s text, %s text)",
					TABLE, C_ID, C_CREATED_AT,
					C_USER, C_TEXT);
			Log.d(TAG, "Create with SQL: " + sql);
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Usually ALTER TABLE
			db.execSQL("drop table if exists " + TABLE);
			onCreate(db);
		}

	}
}
