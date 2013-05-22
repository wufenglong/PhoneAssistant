package com.hwttnet.mobileassistant.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hwttnet.mobileassistant.R;
import com.hwttnet.mobileassistant.util.AppInfo;
import com.hwttnet.mobileassistant.util.ConstantsList;
import com.hwttnet.mobileassistant.util.MyUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
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

/**
 * 五.软件管理
 * 
 * 管理安装的软件
 * 
 * 1.查看软件信息（启动系统的View）
 * 
 * 2.卸载
 * 
 * */
public class InstallSoftManageActivity extends Activity {
	private static final String TAG = "InstallSoftManageActivity";
	private ListView mListView;
	private ArrayList<AppInfo> installedAppList;
	private ProgressDialog progressDialog;
	private myInstalSoftManageAdapter mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题
		setContentView(R.layout.installsoftmanage_layout);
		Log.v(TAG, "onCreate()");
		iniView();
		addList();
		setAdapter();
		super.onCreate(savedInstanceState);
	}

	private void setAdapter() {
		mListAdapter = new myInstalSoftManageAdapter(this, installedAppList);
		mListView.setAdapter(mListAdapter);
	}

	private void addList() {
		installedAppList = getInstalledAppList(this);
	}

	private void iniView() {
		mListView = (ListView) findViewById(R.id.mListView_installSoftManage);
	}

	@Override
	protected void onStart() {
		Log.v(TAG, "onStart()");
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onResume()");
		mListAdapter.updateList();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void uninstallApp(int position) {
		// TODO Auto-generated method stub
		AppInfo appInfo = installedAppList.get(position);
		Uri packageURI = Uri.parse("package:" + appInfo.packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		startActivity(uninstallIntent);
	}

	// 读取非系统安装软件信息
	public ArrayList<AppInfo> getInstalledAppList(Context context) {
		List<ApplicationInfo> packages = context.getPackageManager()
				.getInstalledApplications(0);
		ArrayList<AppInfo> applist = new ArrayList<AppInfo>();
		Iterator<ApplicationInfo> l = packages.iterator();
		/* 生成一个TXT，用于生成列表 */
		while (l.hasNext()) {
			AppInfo appInfo = new AppInfo();
			ApplicationInfo app = (ApplicationInfo) l.next();
			String packageName = app.packageName;// APK包名
			/* 如果ApplicationInfo为非系统程序，则读取出来 */
			if (packageName.equals("com.nd.android.pandareader")) {// 熊猫看书
				app.flags = 68;
			}
			boolean flag = false;
			if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				flag = true;
			}

			if (flag
					&& !packageName
							.equals(ConstantsList.MOBILE_ASSISTANT_PACKAGENAME)) {
				// String packageName = app.packageName;// APK包名
				String appDir = app.publicSourceDir;// APK的路径
				// int appUid = app.uid;
				File appFile = new File(appDir);// 由路径创建一个File
				long fileSize = appFile.length();// 当前APK的大小
				String fileSizeK = MyUtil.accountFileSize(fileSize);
				String label = "";
				label = context.getPackageManager().getApplicationLabel(app)
						.toString();// Label程序名
				PackageInfo pInfo = null;
				try {
					pInfo = context.getPackageManager().getPackageInfo(
							packageName, Context.MODE_APPEND);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				appInfo.packageName = packageName;
				appInfo.name = label;
				appInfo.size = fileSizeK;
				appInfo.icon = pInfo.applicationInfo.loadIcon(context
						.getPackageManager());
				applist.add(appInfo);
				// apk的flag
				// int appflags = app.flags;
				// versioncode =pInfo.versionCode+"";//版本号
				// versionName_installed = pInfo.versionName + "";// 版本名
				/* 修改版本号：1.0.0格式 */
				// versionName_installed =
				// modify_VersionName(versionName_installed);
				// Drawable drawImg = pInfo.applicationInfo.loadIcon(context
				// .getPackageManager());
			}
		}
		return applist;
	}

	private class myInstalSoftManageAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<AppInfo> installedAppList;
		private LayoutInflater mLayoutInflater;

		public myInstalSoftManageAdapter(Context context,
				ArrayList<AppInfo> installedAppList) {
			this.context = context;
			this.installedAppList = installedAppList;
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void updateList() {
			installedAppList = getInstalledAppList(context);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			
			return installedAppList.size();
		}

		@Override
		public Object getItem(int position) {
			return installedAppList.get(position);
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
						R.layout.installsoftmanage_item, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.app_icon_installSoftManage);
				holder.imageButton = (ImageButton) convertView
						.findViewById(R.id.itemcheck_installSoftManage);
				holder.name = (TextView) convertView
						.findViewById(R.id.app_name_installSoftManage);
				holder.size = (TextView) convertView
						.findViewById(R.id.app_size_installSoftManage);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			AppInfo app = installedAppList.get(position);
			holder.icon.setImageDrawable(app.icon);
			holder.name.setText(app.name);
			holder.size.setText(app.size);
			// 对卸载按钮设置监听,当点卸载后,卸载当被选的,并从新刷新列表
			holder.imageButton.setImageResource(R.drawable.xiezai);
			OnTouchListener onTouchListener = new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						switch (v.getId()) {
						case R.id.itemcheck_installSoftManage:
							// TODO 卸载并刷新列表
							uninstallApp(position);
							break;
						default:
							break;
						}
					}
					return false;
				}

			};
			holder.imageButton.setOnTouchListener(onTouchListener);
			return convertView;
		}

		private class ViewHolder {
			ImageView icon;
			TextView name;
			TextView size;
			ImageButton imageButton;
		}

	}
}
