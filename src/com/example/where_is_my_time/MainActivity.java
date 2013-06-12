package com.example.where_is_my_time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import com.example.where_is_my_time.EntryFragment.entryfragmentTunnel;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActivityManager;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MainActivity extends Activity implements entryfragmentTunnel {

	private DBHelper dbHelper;
	private static String[] colum = { "_id", "_APP_NAME", "_TIMES", "_DATE"};
	private static SQLiteDatabase dbr;
	private SQLiteDatabase dbwr;
	public static int minGap = 5;
	private static Fragment titlesFragment;
	private static Fragment detailsFragment;
	private String now_app_name; 
	private TextView titleText;
	private CategoryBar mCategoryBar;
	private static List<Map<String, String>> nameAry = new ArrayList<Map<String, String>>();
	TextView top1, top2, top3, top4, top5, topOther, totalTime, serviceStatus;

	public class ComparatorList implements Comparator{
		@Override
		public int compare(Object a1, Object a2) {
			int val1, val2;
			val1 = getMapInt((Map<String, String>) a1);
			val2 = getMapInt((Map<String, String>) a2);
			return val2 - val1;
		}
	}
	int getMapInt(Map<String, String> a1){
		String key;
		int val = 0;
		if(a1 == null){
			return val;
		}
		for (Map.Entry<String, String> entry : a1.entrySet()){
			try {
				key = entry.getKey();
				if(key.equals("_TIMES")){
					val = Integer.valueOf(entry.getValue());
				}
			}catch(Exception e){
				Log.d("VT", e.toString());
			}
		}
		return val;
	}
	
	
	private AlertDialog getAlertDialog(String title,String message, final String id){                                                                                                                                                         
	    Builder builder = new AlertDialog.Builder(MainActivity.this);
	    builder.setTitle(title);
	    builder.setMessage(message);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            //按下按鈕時顯示快顯 
	        	if(id == null){
	        		emptyAllData();
	        	}
	        	else{
	        		long check = dbwr.delete(DBHelper.tableName, "_id=" + id + "", null);
	        		if(check <= 0){
	        			Log.d("Error", "insert error");
	        		}	
	            	refreshFragment(detailsFragment);
	        	}
	            Toast.makeText(MainActivity.this, "Delete", Toast.LENGTH_SHORT).show();
	        }
	    });
	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) { 
	        }
	    });
	    return builder.create();
	}
	
	private boolean isMyServiceRunning(String name) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(100)) {
	    	//Log.d("VT", service.service.getClassName());
	        if (name.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	private String changeTimeFormat(String arg){
		int times = Integer.valueOf(arg);
		times = times * minGap;
		int hr = times/3600;
		int min = (times%3600) / 60;
		int sec = times % 60;
		String msg = hr + " hr " + min + " m " + sec + " s ";
		return msg;
	}
	public List<Map<String, String>> getViewListData(String role, String app_name){
		String date = App_Service.getDateTime();
		Cursor _cursor = null;
		if(role.equals("titles")){
			_cursor = dbr.query(DBHelper.tableName, colum, "_DATE=?", new String[] { date }, null, null, null);
		}
		else {
			_cursor = dbr.query(DBHelper.tableName, colum, "_APP_NAME=?", new String[] { app_name }, null, null, null);
		}
		
		nameAry.clear();
		while(_cursor.moveToNext()){
			Map<String, String> map = new HashMap<String, String>();
			//ColumData tmp = new ColumData(_cursor.getString(1));
			map.put("_id", _cursor.getString(0));
			map.put("_APP_NAME", _cursor.getString(1));
			map.put("_TIMES", _cursor.getString(2));
			map.put("_DATE", _cursor.getString(3));
			String msg = _cursor.getString(2);
			map.put("_CONTENT", changeTimeFormat(msg) );
			nameAry.add(map);
		}
		if(!nameAry.isEmpty()){
			Collections.sort(nameAry, new ComparatorList());
		}
		return nameAry;
	}
	public String get_app_name(){
		return now_app_name;
	}
	public void showDetail(String app_name){
		now_app_name = app_name;
		if(detailsFragment == null){
			detailsFragment = addFragment("details");
			replaceFragment(detailsFragment);
		}
		titleText.setText( "     " + app_name + " Time Usage");
		replaceFragment(detailsFragment);
	}
	
	@Override
	public void onBackPressed() {
		if (titlesFragment.isVisible()) {
			super.onBackPressed();
		}
		else {
			titleText.setText("     System Time Usage");
			replaceFragment(titlesFragment);
		}
	}

	public void empty(SQLiteDatabase db){
		Log.d("VT", "Empty table");
		String SQL = "DELETE from " + DBHelper.tableName;
		db.execSQL(SQL);		
	}
    
	Fragment addFragment(String name) {
        // Instantiate a new fragment.
        Fragment newFragment = EntryFragment.newInstance(name);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.mainfragment, newFragment, name);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        getFragmentManager().executePendingTransactions();
        return newFragment;
    }
    void attatchFragment(Fragment newFragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.add(R.id.mainfragment, newFragment);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //ft.addToBackStack("details");
        ft.replace(R.id.mainfragment, newFragment);
        ft.commit();
        getFragmentManager().executePendingTransactions();
    }
    void removeFragment(Fragment fragment) {
    	FragmentTransaction ft = getFragmentManager().beginTransaction();
    	ft.remove(fragment);
    	ft.commit();
    	getFragmentManager().executePendingTransactions();
    }
    void refreshFragment(Fragment fragment) {
    	setServiceStatus();
    	removeFragment(fragment);
    	attatchFragment(fragment);
    }
    void replaceFragment(Fragment fragment) {
    	setServiceStatus();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.mainfragment, fragment);
        //ft.addToBackStack(null);
        ft.commit();
    }
    void setServiceStatus(){
    	if(isMyServiceRunning(App_Service.class.getName()) && isMyServiceRunning(ScreenService.class.getName() )){
    		serviceStatus.setText("Status: Running");
    	}
    	else {
    		serviceStatus.setText("Status: Stop");
    	}
    }
    
    

    private void setupCategoryInfo() {
        mCategoryBar = (CategoryBar)findViewById(R.id.category_bar);
        int[] imgs = new int[] {
                R.drawable.category_bar_music, 
                R.drawable.category_bar_video,
                R.drawable.category_bar_picture,
                R.drawable.category_bar_document, 
                R.drawable.category_bar_apk, 
                R.drawable.category_bar_other
        };
        for (int i = 0; i < imgs.length; i++) {
            mCategoryBar.addCategory(imgs[i]);
        }
    }
    private int setCategoryFullValue(){
    	int fullValue = 0;
    	mCategoryBar = (CategoryBar)findViewById(R.id.category_bar);
    	for(Map<String, String> map: nameAry){
    		String tmp = map.get("_TIMES");
    		if(tmp != null){
    			fullValue += Integer.valueOf(tmp);
    		}
    	}
    	mCategoryBar.setFullValue(fullValue);
    	return fullValue;
    }
    private void setCategoryBarValue(int index, int value) {
    	mCategoryBar = (CategoryBar)findViewById(R.id.category_bar);
        if (mCategoryBar == null) {
            mCategoryBar = (CategoryBar)findViewById(R.id.category_bar);
        }
        mCategoryBar.setCategoryValue(index, value);
    }
    private String getPointAftertwo(Double num){
    	if(num < 0){
    		return "0";
    	}
		String tmp = Double.toString(num);
		int endIndex = tmp.length();
		if(tmp.contains(".")){
			endIndex = tmp.indexOf(".") + 3;
		}
		endIndex = tmp.length() < endIndex? tmp.length():endIndex; 
		return tmp.substring(0, endIndex);
    }
    public void refreshCategoryBar(String role){
    	int count = 0;
    	int otherTime = 0;
    	int topTime = 0;
    	int fullValue = setCategoryFullValue();
    	String percent = null;
    	double otherPercent = 100;
    	
    	for( Map<String, String>map : nameAry ){
    		if(count < 5){
    			String title = null, times;
    			if(role.equals("titles")){
    				title = map.get("_APP_NAME");
    			}
    			else{
    				title = map.get("_DATE");
    			}
    			times = map.get("_TIMES");
    			if(times != null){
    				topTime += Integer.valueOf(times);
    				setCategoryBarValue(count, Integer.valueOf(times));
    				double result = ((double)(Integer.valueOf(times) * 100)) /
    									((double)fullValue);
    				otherPercent -= result;
    				percent = " " + getPointAftertwo(result) + "%";
    			}
    			else{
    				setCategoryBarValue(count, 0);
    				percent = " 100%";
    			}
    			
    			setTopViewText(count, title + percent);
    		}
    		else{
    			String times;
    			times = map.get("_TIMES");
    			if(times != null){
    				otherTime += Integer.valueOf(times);
    			}
    		}
    		count = count + 1;
    	}
    	for(int i=count; i<6; i++){
    		setCategoryBarValue(i, 0);
    		setTopViewText(i, "");
    	}
    	setCategoryBarValue(5, otherTime);
    	
    	String tmp = getPointAftertwo(otherPercent);
    	setTopViewText(5, "other " + tmp + "%");
    	String times = Integer.toString(topTime+otherTime);
    	totalTime.setText("Total time: " + changeTimeFormat(times));
    	mCategoryBar.startAnimation();
    }
    
    private void initView(){
    	totalTime = (TextView)findViewById(R.id.total_time);
    	top1 = (TextView)findViewById(R.id.category_legend_top1);
    	top2 = (TextView)findViewById(R.id.category_legend_top2);
    	top3 = (TextView)findViewById(R.id.category_legend_top3);
    	top4 = (TextView)findViewById(R.id.category_legend_top4);
    	top5 = (TextView)findViewById(R.id.category_legend_top5);
    	topOther = (TextView)findViewById(R.id.category_legend_other);
    	serviceStatus = (TextView)findViewById(R.id.service_status);
    }
    private void setTopViewText(int index, String msg){
    	switch(index){
    		case 0:
    			top1.setText(msg);
    			break;
    		case 1:
    			top2.setText(msg);
    			break;
       		case 2:
       			top3.setText(msg);
    			break;
       		case 3:
       			top4.setText(msg);
    			break;
       		case 4:
       			top5.setText(msg);
    			break;
       		case 5:
       			topOther.setText(msg);
    			break;
    		default:
    			break;
    	}
    }
    public void deleteEntry(String id){
    	final String ID = id;
    	final AlertDialog alertDialog = getAlertDialog("Delete", "Do you really want to delete?", ID);
    	alertDialog.show();
    }
    
    
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		dbHelper = new DBHelper(this);
		dbwr = dbHelper.getWritableDatabase();
		dbr = dbHelper.getReadableDatabase();
		if(!isMyServiceRunning(ScreenService.class.getName())){
			Intent serviceIntent = new Intent();
			serviceIntent.setClass(this, ScreenService.class);
			this.startService(serviceIntent);
		}
		now_app_name = null;
		//detailsFragment = addFragment("details");
		titlesFragment = addFragment("titles");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		titleText = (TextView)findViewById(R.id.appNameText);
		titleText.setText("     System Time Usage");
		setupCategoryInfo();
		refreshFragment(titlesFragment);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, 0, 0, "Empty All Data");
		menu.add(0, 1, 1, "Refresh");
		menu.add(0, 2, 2, "Start Monitoring");
		menu.add(0, 3, 3, "Stop Monitoring");
		return super.onCreateOptionsMenu(menu);
		//getMenuInflater().inflate(R.menu.main, menu);
		//return true;
	}
	@Override
	public void onResume(){
		if (titlesFragment.isVisible()) {
			titleText.setText("     System Time Usage");
			refreshFragment(titlesFragment);
		}
		super.onResume();
	}
	@Override                                                                                                                                                                          
	public boolean onOptionsItemSelected(MenuItem item) {
		final AlertDialog alertDialog = getAlertDialog("Alert", "Do you really clean all data?", null);
	    switch(item.getItemId()) {
	        case 0:
	        	alertDialog.show();
	            break;
	        case 1:
	        	refreshClick();
	        	Toast.makeText(this,"Refresh",Toast.LENGTH_SHORT).show();
	            break;
	        case 2:
	        	startService();
	        	refreshClick();
	        	Toast.makeText(this,"Start Monitoring",Toast.LENGTH_SHORT).show();
	        	break;
	        case 3:
	        	stopService();
	        	refreshClick();
	        	Toast.makeText(this,"Stop Monitoring",Toast.LENGTH_SHORT).show();
	        	break;
	        default:
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	
	
	public void emptyAllData() {
		empty(dbwr);
		titleText.setText("     System Time Usage");
		refreshFragment(titlesFragment);
	}
	public void stopService() {
		Intent _intent = new Intent();
		_intent.setClass(this, ScreenService.class);
		this.stopService(new Intent(this, App_Service.class));
		this.stopService(_intent);
	}
	public void startService() {
		Intent _intent = new Intent();
		_intent.setClass(this, ScreenService.class);
		this.startService(_intent);
		this.startService(new Intent(this, App_Service.class));
	}
	public void refreshClick() {
		refreshFragment(titlesFragment);
	}
	
}
