package com.example.where_is_my_time;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private final static int dbVersion = 1;
	private final static String dbName = "app_time.db";
	public final static String tableName = "app_time";
	
	public DBHelper(Context context) {
		super(context, dbName, null, dbVersion);
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		final String SQL = "CREATE TABLE IF NOT EXISTS " + tableName + "( " +                                                                                                             
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				"_APP_NAME VARCHAR(50), " + 
				"_TIMES INTEGER, " + 
				"_DATE VARCHAR(50)" +
				");";
		db.execSQL(SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		final String SQL = "DROP TABLE " + tableName;
		db.execSQL(SQL);
		Log.d("DB", "Destroy table");
	}

}
