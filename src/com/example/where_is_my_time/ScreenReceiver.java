package com.example.where_is_my_time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class ScreenReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		if(arg1.getAction().equals(Intent.ACTION_SCREEN_ON)){
			Intent _intent = new Intent();
			_intent.setClass(context, App_Service.class);
			context.startService(_intent);
			Log.d("Receiver", "ON");
		}
		else if(arg1.getAction().equals(Intent.ACTION_SCREEN_OFF)){
			Intent _intent = new Intent();
			_intent.setClass(context, App_Service.class);
			context.stopService(_intent);
			Log.d("Receiver", "OFF");
		}
	}

}
