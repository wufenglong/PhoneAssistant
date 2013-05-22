package com.hwttnet.mobileassistant.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.hwttnet.mobileassistant.R;
import com.hwttnet.mobileassistant.util.CMDExecute;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 一.系统检测
 * 
 * 1.设备信息
 * 
 * 2.检测系统信息（无异常）
 * 
 * 3.检测硬件信息（无异常）
 * 
 * 4.检测软件作息（有无升级）暂时不作
 * 
 * 5.开机驻留程序
 * 
 * 6.内存程序（正在运行的程序）
 * 
 * 
 * */
public class SystemDetectionActivity extends Activity {
	private String[] hardwareInfo_array;// <!-- 1.1硬件设备信息标题 -->
	private String[] systemDetection;
	private String[] detectionSystemInfo_array;// <!-- 1.2检测系统 -->
	private String[] detectionHardwareInfo_array;// <!-- 1.3检测硬件-->
	private String[] installedApplist_array;// <!-- 1.4 软件信息 -->
	private String[] bootAppList_array;// <!-- 1.5开机驻留程序 -->
	private String[] runningTaskAppList_array;// <!-- 1.6内存驻留程序 -->
	private ProgressDialog progressDialog;
	private String TAG = "SystemDetectionActivity";
	private String normal_detectionSystem;// 无异常提示
	private ListView mListView_systemdetection;
	/**
	 * 存放ListView的adapter的数据 type,title,info,id
	 */
	private ArrayList<HashMap<String, String>> list;
	private SystemDetectionListAdapter adapter;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题
		setContentView(R.layout.systemdetection_layout);
		Log.v(TAG, "onCreate()");
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("loading");
		progressDialog.setMessage("数据加载中,请稍后...");
		// progressDialog.show();
		Log.v(TAG, "onCreate()111111111");
		iniRes();
		iniView();
		adapter = new SystemDetectionListAdapter(this);
		loadDate();
		mListView_systemdetection.setAdapter(adapter);
	}

	/**
	 * 
	 * */
	private void iniRes() {
		title = getResources().getString(R.string.SystemDetection);
		setTitle(title);
		//
		list = new ArrayList<HashMap<String, String>>();
		// 大分类标题
		systemDetection = getResources()
				.getStringArray(R.array.systemDetection);
		// 各小分类标题
		hardwareInfo_array = getResources().getStringArray(
				R.array.hardwareInfo_array);
		detectionSystemInfo_array = getResources().getStringArray(
				R.array.detectionSystemInfo_array);
		detectionHardwareInfo_array = getResources().getStringArray(
				R.array.detectionHardwareInfo_array);
		installedApplist_array = getResources().getStringArray(
				R.array.installedApplist_array);
		bootAppList_array = getResources().getStringArray(
				R.array.bootAppList_array);
		runningTaskAppList_array = getResources().getStringArray(
				R.array.runningTaskAppList_array);
		// 无异常
		normal_detectionSystem = getResources().getString(
				R.string.normal_detectionSystem);
		Log.v(TAG, "iniRes() end...");
	}

	private void iniView() {
		mListView_systemdetection = (ListView) findViewById(R.id.mListView_systemdetection);
	}

	/**
	 * 线程加载数据
	 * */
	private void loadDate() {
		Log.v(TAG, "loadDate() run start...");
		String[] hardwareInfo = CMDExecute
				.GetDeviceInfoList(SystemDetectionActivity.this);
		int appNum = CMDExecute
				.GetInstalledAPPListSize(SystemDetectionActivity.this);
		int bootNum = CMDExecute
				.GetBootCompletedAPPListSize(SystemDetectionActivity.this);
		int runningNum = CMDExecute
				.GetRunningTasksInfoListSize(SystemDetectionActivity.this);

		setList(hardwareInfo, appNum, bootNum, runningNum);
		// handler.sendEmptyMessage(0);
		Log.v(TAG, "loadDate() run end...");
	}

	/**
	 * 向List里加数据
	 * */
	protected void setList(String[] hardwareInfo, int appNum, int bootNum,
			int runningNum) {
		for (int i = 0; i < systemDetection.length; i++) {
			putTitleToList(systemDetection[i], i);
			switch (i) {
			case 0:// 1.1硬件设备信息
				for (int j = 0; j < hardwareInfo.length; j++) {
					putInfoToList(hardwareInfo_array[j], hardwareInfo[j], i);
				}
				break;
			case 1:// 1.2检测系统
				putInfoToList(detectionSystemInfo_array[0],
						normal_detectionSystem, i);
				break;
			case 2:// 1.3检测硬件
				putInfoToList(detectionHardwareInfo_array[0],
						normal_detectionSystem, i);
				break;
			case 3:// 1.4 软件信息
				putInfoToList(installedApplist_array[0], appNum + "", i);
				break;
			case 4:// 1.5开机驻留程序
				putInfoToList(bootAppList_array[0], bootNum + "", i);
				break;
			case 5:// 1.6内存驻留程序
				putInfoToList(runningTaskAppList_array[0], runningNum + "", i);
				break;
			default:
				break;
			}
		}
	}

	private void putInfoToList(String info1, String info2, int j) {
		HashMap<String, String> map0 = new HashMap<String, String>();
		map0.put("info1", info1);
		map0.put("info2", info2);
		map0.put("type", "2");
		map0.put("id", j + "");
		// list.add(map0);
		adapter.addItem(map0);
	}

	private void putTitleToList(String title, int i) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("info1", title);
		map.put("info2", "");
		map.put("type", "1");
		map.put("id", i + "");
		// list.add(map);
		adapter.addTitleItem(map);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 
	 * */
	private class SystemDetectionListAdapter extends BaseAdapter {
		private static final int TYPE_ITEM = 0;
		private static final int TYPE_TITLE = 1;
		private static final int TYPE_MAX_COUNT = TYPE_TITLE + 1;

		private Context c;
		private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		private LayoutInflater mInflater;
		private TreeSet mTitleSet = new TreeSet();

		public SystemDetectionListAdapter(Context c) {
			this.c = c;
			mInflater = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void addTitleItem(HashMap<String, String> title) {
			list.add(title);
			mTitleSet.add(list.size() - 1);// 标题位置
			notifyDataSetChanged();
		}

		public void addItem(HashMap<String, String> item) {
			list.add(item);
			notifyDataSetChanged();
		}

		@Override
		public int getItemViewType(int position) {
			return mTitleSet.contains(position) ? TYPE_TITLE : TYPE_ITEM;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// RecentViewTitleHolder titleHolder = null;
			RecentViewItemHolder itemHolder = null;
			Log.v(TAG, "list.size()=" + list.size());
			HashMap<String, String> map = getItem(position);
			int type = getItemViewType(position);
			if (convertView == null) {
				itemHolder = new RecentViewItemHolder();
				if (type == TYPE_ITEM) {
					convertView = mInflater.inflate(
							R.layout.systemdetection_item, null);
					itemHolder.info1 = (TextView) convertView
							.findViewById(R.id.mTextView_info1_systemDetection);
					itemHolder.info2 = (TextView) convertView
							.findViewById(R.id.mTextView_info2_systemDetection);
				} else if (type == TYPE_TITLE) {
					convertView = mInflater.inflate(
							R.layout.systemdetection_title, null);
					itemHolder.info1 = (TextView) convertView
							.findViewById(R.id.mTextView_title1_systemDetection);
					itemHolder.info2 = (TextView) convertView
							.findViewById(R.id.mTextView_title2_systemDetection);
				}
				convertView.setTag(itemHolder);
			} else {
				itemHolder = (RecentViewItemHolder) convertView.getTag();
			}
			itemHolder.info1.setText(map.get("info1"));// 
			itemHolder.info2.setText(map.get("info2"));// 

			return convertView;
		}

		private class RecentViewTitleHolder {
			TextView title;
		}

		private class RecentViewItemHolder {
			TextView info1;
			TextView info2;
		}

	}
}
