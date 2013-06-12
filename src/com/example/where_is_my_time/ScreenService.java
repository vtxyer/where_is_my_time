package com.example.where_is_my_time;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ScreenService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate() {
		try{
			IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			BroadcastReceiver mReceiver = new ScreenReceiver();
			registerReceiver(mReceiver, filter);
			Log.d("VT", "Screen start");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		super.onCreate();
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		Intent _intent = new Intent(this, App_Service.class);
		this.startService(_intent);
		super.onStart(intent, startId);
	}
	
}
