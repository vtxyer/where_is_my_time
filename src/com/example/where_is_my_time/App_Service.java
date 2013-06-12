package com.example.where_is_my_time;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


public class App_Service extends Service {
	private Handler handler = new Handler();
	public int minGap = 5000;
	private DBHelper dbHelper;
	private ActivityManager _activityManager;
	private String[] colum = { "_id", "_APP_NAME", "_TIMES", "_DATE"};
	private SQLiteDatabase dbwr;
	private SQLiteDatabase dbr;
	private String ID;
	
	public static String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	    Date date = new Date();
	    return dateFormat.format(date);
	}
	private String getForeTaskName(){
		String runingTaskName;// = _activityManager.getRunningAppProcesses().get(0).processName;
		//List< ActivityManager.RunningTaskInfo > taskInfo = _activityManager.getRunningTasks(1);
		List< ActivityManager.RunningTaskInfo > taskInfo = _activityManager.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		runingTaskName = componentInfo.getPackageName();
		final PackageManager pm = getApplicationContext().getPackageManager();
		ApplicationInfo ai;
		try {
		    ai = pm.getApplicationInfo( runingTaskName, 0);
		} catch (final NameNotFoundException e) {
		    ai = null;
		}
		return (String)(ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
		
	}
	private int query(String appName){
		String date = getDateTime();
		Cursor _cursor = null;
		_cursor = dbr.query(DBHelper.tableName, colum, "_APP_NAME=?", new String[] {appName}, null, null, null);
		
		while(_cursor.moveToNext()){
			if(_cursor.getString(1).equalsIgnoreCase(appName) && _cursor.getString(3).equalsIgnoreCase(date)){
				ID = _cursor.getString(0);
				return Integer.valueOf(_cursor.getString(2));
			}
		}
		ID = null;
		return 0;
	}
	private void add(String appName){
		ContentValues value = new ContentValues();
		String date = getDateTime();
		value.put("_APP_NAME", appName);
		value.put("_TIMES", 1);
		value.put("_DATE", date);
		long check = dbwr.insert(DBHelper.tableName, null, value);
		if(check <= 0){
			Log.d("Error", "insert error");
		}
	}
	private void update(String appName, int times){
		//Log.d("VT", appName + " " + times);
		ContentValues value = new ContentValues();

		value.put("_TIMES", times + "");
		long check = dbwr.update(DBHelper.tableName, value, "_id=" + ID + "", null);
		if(check <= 0){
			Log.d("Error", "insert error");
		}	
	}
    private Runnable recordTask = new Runnable() {
        public void run() {
        	String appName = getForeTaskName();
        	if(!appName.equalsIgnoreCase("ui")){
        		int times = query(appName);
        		//Log.d("TEST", "!!" + ID);
        		if(times == 0){
        			add(appName);
        		}
        		else{
        			int newTimes = times += 1;
        			//Log.d("UPDATE", Integer.toString(newTimes));
        			update(appName, newTimes);
        		}
        	}
            handler.postDelayed(this, minGap);
        }
    };
	
    
    
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		_activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		
		dbHelper = new DBHelper(this);
		dbwr = dbHelper.getWritableDatabase();
		dbr = dbHelper.getReadableDatabase();
		
		Log.d("VT", "Start service ");
		super.onCreate();
	}
    @SuppressWarnings("deprecation")
	@Override
    public void onStart(Intent intent, int startId) {
        handler.postDelayed(recordTask, minGap);
        super.onStart(intent, startId);
    }
 
    @Override
    public void onDestroy() {
        handler.removeCallbacks(recordTask);
        dbHelper.close();
        Log.d("VT", "Stop Service");
        super.onDestroy();
    }

}
