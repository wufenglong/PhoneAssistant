package com.hwttnet.mobileassistant.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hwttnet.mobileassistant.R;
import com.hwttnet.mobileassistant.util.AppInfo;
import com.hwttnet.mobileassistant.util.ConstantsList;

/**
 * 三.开机加速
 * 
 * 开机启动的程序（开启和禁止）
 * 
 * */
public class BootSpeedActivity extends Activity {
	private static final String TAG = "BootSpeechActivity";
	private ArrayList<String> shutstartList;// 被禁止启动列表
	private ArrayList<AppInfo> bootAppList;// 开机启动列表
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题
		setContentView(R.layout.bootspeed_layout);
		iniView();
		addList();
		setAdapter();
		super.onCreate(savedInstanceState);
	}

	private void setAdapter() {
		mListView.setAdapter(new myBootSpeechAdapter(this, bootAppList));
	}

	private void iniView() {
		mListView = (ListView) findViewById(R.id.mListView_bootSpeed);
	}

	private void addList() {
		shutstartList = getDisabledBootList(this);
		bootAppList = getBootCompletedAPPList(this, shutstartList);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.v(TAG, "<<<<<<<<<<<onPause()>>>>>>>>>>>>>>>>");
		// 保存shutstartList
		saveShutStartList(this, shutstartList);

		super.onPause();
	}

	private void saveShutStartList(Context context,
			ArrayList<String> shutstartList2) {
		Iterator iterator = shutstartList2.iterator();
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("**");
		while (iterator.hasNext()) {
			String packageName = (String) iterator.next();
			stringBuffer.append(packageName);
			stringBuffer.append("**");
		}

		String shutTXT = stringBuffer.toString();
		Log.v(TAG, "shutTXT=" + shutTXT);
		/* 把读出的byte[]写入本程序目录下的文件中 */
		if (shutTXT != null) {
			try {
				OutputStream os = context.openFileOutput(
						ConstantsList.FILENAME_SHUT_AUTOSTART,
						Context.MODE_PRIVATE);
				os.write(shutTXT.getBytes());
				os.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
	}

	private void updateShutstartList(String packageName, boolean isDisabled) {
		if (shutstartList.contains(packageName)) {
			if (!isDisabled) {
				shutstartList.remove(packageName);
			}
		} else {
			if (isDisabled) {
				shutstartList.add(packageName);
			}
		}
		Log.v(TAG, "shutstartList=" + shutstartList.toString());
	}

	public ArrayList<String> getDisabledBootList(Context context) {
		ArrayList<String> shutstartList = new ArrayList<String>();
		String shutAutoStartInfo = "";
		InputStream is = null;
		boolean bOpenfileError = false;// 打开文件错误
		try {
			is = context.openFileInput(ConstantsList.FILENAME_SHUT_AUTOSTART);
		} catch (FileNotFoundException e2) {
			bOpenfileError = true;
			e2.printStackTrace();
		}

		if (!bOpenfileError) {
			int len;
			byte[] bytes = null;
			try {
				len = is.available();
				bytes = new byte[len];
				is.read(bytes);

			} catch (IOException e) {
				e.printStackTrace();
			}
			shutAutoStartInfo = new String(bytes);

			/* 解析字符串，格式为：包名+"**" */
			int _startIndex_packagename = 0;// 每个包号开始的索引
			String _packagename = "";// 解析的包名
			int index_Star = 0;// 当前星号的索引位置
			boolean bRun = true;
			while (bRun) {
				index_Star = shutAutoStartInfo.indexOf("**",
						_startIndex_packagename);
				if (index_Star != -1) {
					_packagename = shutAutoStartInfo.substring(
							_startIndex_packagename, index_Star);
					_startIndex_packagename = index_Star + 2;
					if (!_packagename.equals("")
							&& !shutstartList.contains(_packagename)) {
						shutstartList.add(_packagename.trim());
					}
				} else {
					bRun = false;
				}
			}
		}
		return shutstartList;
	}

	/* 读取开机启动信息：返回一个列表 */
	private ArrayList<AppInfo> getBootCompletedAPPList(Context context,
			ArrayList<String> _shutstartList) {
		PackageManager packageManager = context.getPackageManager();

		ArrayList<AppInfo> bootAppList = new ArrayList<AppInfo>();

		Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED, null);// 开机启动的intent
		List<ResolveInfo> appsList = packageManager.queryBroadcastReceivers(
				intent, 0);// 搜索开机启动的intent的BroadcastReceivers
		Iterator<ResolveInfo> l = appsList.iterator();

		while (l.hasNext()) {
			try {
				AppInfo appInfo = new AppInfo();
				ResolveInfo app = (ResolveInfo) l.next();
				/* 如果ApplicationInfo为非系统程序，则读取出来 */
				boolean flag = false;
				if ((app.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					flag = true;
				}
				if (flag) {
					String packageName = app.activityInfo.packageName;// 包名
					if (!packageName
							.equals(ConstantsList.MOBILE_ASSISTANT_PACKAGENAME)) {
						appInfo.packageName = packageName;
						appInfo.name = (String) app.activityInfo
								.loadLabel(packageManager);// 程序名
						appInfo.icon = app.activityInfo
								.loadIcon(packageManager);
						if (_shutstartList.contains(packageName)
								&& !_shutstartList.equals(null)) {
							appInfo.isDisabled = true;
						} else {
							appInfo.isDisabled = false;
						}
						bootAppList.add(appInfo);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bootAppList;
	}

	/**
	 * 
	 * */
	private class myBootSpeechAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<AppInfo> bootAppList;
		private LayoutInflater mLayoutInflater;

		public myBootSpeechAdapter(Context context,
				ArrayList<AppInfo> bootAppList) {
			this.context = context;
			this.bootAppList = bootAppList;
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return bootAppList.size();
		}

		@Override
		public Object getItem(int position) {
			return bootAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.bootspeed_item,
						null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.app_icon_BootSpeed);
				holder.imageButton = (ImageButton) convertView
						.findViewById(R.id.itemcheck_BootSpeed);
				holder.name = (TextView) convertView
						.findViewById(R.id.app_name_BootSpeed);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final AppInfo appInfo = bootAppList.get(position);
			holder.name.setText(appInfo.name);
			holder.icon.setImageDrawable(appInfo.icon);

			OnTouchListener onTouchListener = new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						switch (v.getId()) {
						case R.id.itemcheck_BootSpeed:
							appInfo.isDisabled = !appInfo.isDisabled;

							Log.v(TAG, "appInfo.name=" + appInfo.name
									+ "appInfo.isDisabled="
									+ appInfo.isDisabled);
							notifyDataSetChanged();
							// 更新shutstartList
							updateShutstartList(appInfo.packageName,
									appInfo.isDisabled);
							break;
						default:
							break;
						}
					}
					return false;
				}

			};

			holder.imageButton.setOnTouchListener(onTouchListener);
			if (appInfo.isDisabled) {
				holder.imageButton
						.setImageResource(R.drawable.disabled_buttom_bootspeech);
			} else {
				holder.imageButton
						.setImageResource(R.drawable.start_buttom_bootspeech);
			}
			return convertView;
		}

		private class ViewHolder {
			ImageView icon;
			TextView name;
			ImageButton imageButton;
		}
	}

}
