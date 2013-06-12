package com.example.where_is_my_time;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class EntryFragment extends ListFragment {

	private static List<Map<String, String>> globalList;
	private static final String Role = "id";
	
	public String getRole() {
		return getArguments().getString(Role);
	}
	public interface entryfragmentTunnel {
		public List<Map<String, String>> getViewListData(String role, String app_name);
		public String get_app_name();
		public void showDetail(String id);
		public void refreshCategoryBar(String role);
		public void deleteEntry(String id);
	}
	private entryfragmentTunnel tunnel;

	public static EntryFragment newInstance(String name) {
		EntryFragment f = new EntryFragment();
		Bundle args = new Bundle();
		args.putString(Role, name);
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
		String role = getRole();
		if(role.equals("titles")){
			setViewList(tunnel.getViewListData(role, null));
		}
		else{
			String app_name = tunnel.get_app_name();
			if(app_name != null){
				setViewList(tunnel.getViewListData(role, app_name));
			}
			else{
				Log.d("VT", "null app_name");
			}
		}
    	tunnel.refreshCategoryBar(role);
		return super.onCreateView(inflater, container, bundle);
	}
	@Override
	public void onAttach (Activity activity) {
		super.onAttach(activity);
		tunnel = (entryfragmentTunnel)activity;
	}
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String role = getRole();
		if(role.equals("titles")){
			tunnel.showDetail(globalList.get(position).get("_APP_NAME"));
		}
		else{
			Log.d("VT", position + " " + globalList.get(position).get("_APP_NAME"));
			tunnel.deleteEntry(globalList.get(position).get("_id"));
		}
	}

	
	private void setViewList(List<Map<String, String>> nameAry){
		if(nameAry.isEmpty()){
			Map<String, String> map = (Map<String, String>)new HashMap<String, String>();
			map.put("_APP_NAME", "Empty");
			map.put("_CONTENT", "Empty");
			nameAry.add(map);
		}
		globalList = nameAry;
		ListAdapter listAdapter = new SimpleAdapter(getActivity(), nameAry, 
                R.layout.listview,
                new String[]{"_APP_NAME", "_CONTENT"}, 
                new int[]{R.id.name, R.id.text});
		setListAdapter(listAdapter);
	}
	
}
