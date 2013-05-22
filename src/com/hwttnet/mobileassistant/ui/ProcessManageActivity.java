package com.hwttnet.mobileassistant.ui;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
import com.hwttnet.mobileassistant.service.MoblieAssistantService;
import com.hwttnet.mobileassistant.util.AppInfo;
import com.hwttnet.mobileassistant.util.ConstantsList;
import com.hwttnet.mobileassistant.util.FetchData;
import com.hwttnet.mobileassistant.util.MyUtil;

/**
 * 四.任务管理
 * 
 * 正在运行的程序（关闭）
 * 
 * */
public class ProcessManageActivity extends Activity {
	private static final String TAG = "ProcessManageActivity";
	private ListView mListView;
	private ArrayList<AppInfo> runningTaskList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题
		setContentView(R.layout.processmanage_layout);
		iniView();
		addList();
		setAdapter();
		super.onCreate(savedInstanceState);
	}

	private void setAdapter() {

		mListView.setAdapter(new myProcessManageAdapter(this, runningTaskList));
	}

	private void addList() {
		runningTaskList = GetRunningTasksInfoList(this);
	}

	private void iniView() {
		mListView = (ListView) findViewById(R.id.mListView_processManage);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/* 获得正在运行的程序列表 */
	private ArrayList<AppInfo> GetRunningTasksInfoList(Context context) {
		PackageManager packageManager1 = context.getPackageManager();
		ArrayList<AppInfo> _runningTaskList = new ArrayList<AppInfo>();
		final ActivityManager activityManager1 = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager1.getRunningTasks(100);
		Iterator<RunningTaskInfo> l = tasks.iterator();
		while (l.hasNext()) {
			AppInfo app = new AppInfo();
			RunningTaskInfo ti = (RunningTaskInfo) l.next();
			String packageName = ti.baseActivity.getPackageName();// 包名
			if (!packageName.equals(ConstantsList.MOBILE_ASSISTANT_PACKAGENAME)) {
				PackageInfo pInfo = null;
				try {
					pInfo = packageManager1.getPackageInfo(packageName,
							Context.MODE_APPEND);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

				String apppath = pInfo.applicationInfo.publicSourceDir;
				File appFile = new File(apppath);// 由路径创建一个File
				long fileSize = appFile.length();// 当前程序的大小
				String fileSizeK = MyUtil.accountFileSize(fileSize);
				String labelname = (String) pInfo.applicationInfo
						.loadLabel(packageManager1);
				app.icon = pInfo.applicationInfo.loadIcon(packageManager1);
				app.name = labelname;
				app.packageName = packageName;
				app.size = fileSizeK;
				_runningTaskList.add(app);
				// String versionName = pInfo.versionName;
			}

		}
		return _runningTaskList;
	}

	private class myProcessManageAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<AppInfo> runningTaskList;
		private LayoutInflater mLayoutInflater;

		public myProcessManageAdapter(Context context,
				ArrayList<AppInfo> runningTaskList) {
			this.context = context;
			this.runningTaskList = runningTaskList;
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return runningTaskList.size();
		}

		@Override
		public Object getItem(int position) {
			return runningTaskList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.processmanage_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.app_icon_processmanage);
				holder.imageButton = (ImageButton) convertView
						.findViewById(R.id.itemcheck_processmanage);
				holder.name = (TextView) convertView
						.findViewById(R.id.app_name_processmanage);
				holder.size = (TextView) convertView
						.findViewById(R.id.app_size_processmanage);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			AppInfo app = runningTaskList.get(position);
			holder.icon.setImageDrawable(app.icon);
			holder.name.setText(app.name);
			holder.size.setText(app.size);
			// 对关闭按钮设置监听,当点关闭后,关闭当被选的,并从新刷新列表
			OnTouchListener onTouchListener = new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						switch (v.getId()) {
						case R.id.itemcheck_processmanage:
							// 关闭
							closeRunningTask(position);
							notifyDataSetChanged();
							break;
						default:
							break;
						}
					}
					return false;
				}

			};
			holder.imageButton.setOnTouchListener(onTouchListener);
			holder.imageButton.setImageResource(R.drawable.guanbi);

			return convertView;
		}

		private void closeRunningTask(int position) {
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			AppInfo app = runningTaskList.get(position);
			activityManager.restartPackage(app.packageName);
			// runningTaskList.remove(position);
			// 从新加载一次
			runningTaskList = GetRunningTasksInfoList(context);
		}

		private class ViewHolder {
			ImageView icon;
			TextView name;
			TextView size;
			ImageButton imageButton;
		}
	}
}
