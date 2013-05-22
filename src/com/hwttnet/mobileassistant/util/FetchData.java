package com.hwttnet.mobileassistant.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hwttnet.mobileassistant.service.MoblieAssistantService;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.os.Build;
import android.provider.Contacts;
import android.util.DisplayMetrics;
import android.util.Log;

public class FetchData {

	private static String versionName_installed;
	private static ArrayList<Map<String, Object>> listInfo;

	/* 修改版本号：1.0.0格式 */
	public static String modify_VersionName(String versionName) {
		if (versionName == null) {
			// Log.v(Service139.TAG, versionName + "=null");
			versionName = "0.0.0";
			return versionName;
		}
		int start = 0;
		int index = 0;
		int sedindex = 0;
		int num = 0;

		while (index != -1) {
			index = versionName.indexOf(".", start);
			// Log.v(Service139.TAG, "index=" + index);
			if (index != -1) {
				start = index + 1;
				num++;
			}
			if (num == 2) {
				sedindex = start;
			}
		}

		if (num == 1) {
			versionName += ".0";
			// Log.v(Service139.TAG, "versionName=" + versionName);
		} else if (num > 2) {
			versionName = versionName.substring(0, sedindex) + "0";
			// Log.v(Service139.TAG, "versionName=" + versionName);
		}
		return versionName;
	}

	// 读取非系统安装软件信息
	public static String fetch_installed_apps(Context context) {
		List<ApplicationInfo> packages = context.getPackageManager()
				.getInstalledApplications(0);
		listInfo = new ArrayList<Map<String, Object>>(packages.size());
		Iterator<ApplicationInfo> l = packages.iterator();
		/* 生成一个TXT，用于生成列表 */

		String str_appDetail = "";
		while (l.hasNext()) {
			// Log.v("TAG", "fetch installed_apps 1111");
			// Map<String, Object> map = new HashMap<String, Object>();
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
				String fileSize = appFile.length() + "";// 当前APK的大小

				String label = "";
				try {
					label = context.getPackageManager()
							.getApplicationLabel(app).toString();// Label程序名
					PackageInfo pInfo = context.getPackageManager()
							.getPackageInfo(packageName, Context.MODE_APPEND);
					// apk的flag
					int appflags = app.flags;
					// versioncode =pInfo.versionCode+"";//版本号
					versionName_installed = pInfo.versionName + "";// 版本名
					/* 修改版本号：1.0.0格式 */
					versionName_installed = modify_VersionName(versionName_installed);
					// Drawable drawImg = pInfo.applicationInfo.loadIcon(context
					// .getPackageManager());

					str_appDetail += "[" + packageName + "]" + "[" + label
							+ "]" + "[" + versionName_installed + "]" + "["
							+ fileSize + "]" + "\r\n";
				} catch (Exception e) {
					Log.e(MoblieAssistantService.TAG, Thread.currentThread()
							.getName()
							+ "---->" + e.toString());
					return "error";
				}
			}
		}
		return str_appDetail;
	}

	/* 获得屏幕信息 */
	public static String fetch_DisplayMetrics(Context cx) {
		String str = "";
		DisplayMetrics dm = new DisplayMetrics();
		dm = cx.getApplicationContext().getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		// float density = dm.density;
		// float xdpi = dm.xdpi;
		// float ydpi = dm.ydpi;
		str += String.valueOf(screenWidth) + "x";
		str += String.valueOf(screenHeight);
		// str += "The logical density of the display.:" +
		// String.valueOf(density)
		// + "\n";
		// str += "X dimension :" + String.valueOf(xdpi) + "pixels per inch\n";
		// str += "Y dimension :" + String.valueOf(ydpi) + "pixels per inch\n";
		return str;
	}

	/* 获得操作版本 */
	public static String fetch_AndroidSdkVerSion() {
		String str_sdkVerSion = "";
		int Num_sdkVerSion = new Integer(Build.VERSION.SDK).intValue();

		switch (Num_sdkVerSion) {
		case 2:
			str_sdkVerSion = "Android 1.1";
			break;
		case 3:
			str_sdkVerSion = "Android 1.5";
			break;
		case 4:
			str_sdkVerSion = "Android 1.6";
			break;
		case 5:
			str_sdkVerSion = "Android 2.0";
			break;
		case 6:
			str_sdkVerSion = "Android 2.0.1";
			break;
		case 7:
			str_sdkVerSion = "Android 2.1";
			break;
		case 8:
			str_sdkVerSion = "Android 2.2";
			break;

		default:
			break;
		}
		return str_sdkVerSion;
	}

	/* 从Cursor 中读取出全部contacts信息 */
	public static String fetch_Contacts_Info(Cursor c) {
		/* 逐条读取记录信息 */
		int Num = c.getCount();
		String all_Contacts_info = "";// 
		String name, number;
		for (int i = 0; i < Num; i++) {
			c.moveToPosition(i);
			name = c.getString(c.getColumnIndexOrThrow(Contacts.People.NAME));
			number = c.getString(c
					.getColumnIndexOrThrow(Contacts.People.NUMBER));
			number = number == null ? "无输入电话" : number;// 当找不到电话时显示"无输入电话"
			all_Contacts_info += "姓:" + name + "\r\n" + "名:" + "\r\n" + "电话号码:"
					+ number + "\r\n";
		}
		return all_Contacts_info;
	}

	/* 从Cursor 中读取出全部SMS收件箱信息 XML方式 */
	public static String fetch_SMS_Info_returnString(Cursor cursor) {
		String all_SMSInbox_Info = "";

		ArrayList<SMSUnit> smsUnitList = null;
		smsUnitList = new ArrayList<SMSUnit>();

		smsUnitList = fetch_SMS_Info_returnArrayList(cursor);

		/* 生成XML，返回字符串 */
		all_SMSInbox_Info = XmlUtil.CreateSMSXMLToString(smsUnitList);

		return all_SMSInbox_Info;
	}

	/* 查询SMS，返回SMSUnit数组 */
	public static ArrayList<SMSUnit> fetch_SMS_Info_returnArrayList(
			Cursor cursor) {
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName() +
		// "---->"
		// + "******fetch_SMS_Info_returnArrayList start");

		ArrayList<SMSUnit> _smsUnitList = null;
		_smsUnitList = new ArrayList<SMSUnit>();

		int numcount = cursor.getCount();

		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName() +
		// "---->"
		// + "sms in phone numcount	=" + numcount);

		for (int i = 0; i < numcount; i++) {
			SMSUnit smsunit = new SMSUnit();

			cursor.moveToPosition(i);
			String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));// id
			// String thread_id = cursor.getString(cursor
			// .getColumnIndexOrThrow("thread_id"));
			String address = cursor.getString(cursor
					.getColumnIndexOrThrow("address"));// 电话号
			String date = cursor
					.getString(cursor.getColumnIndexOrThrow("date"));
			// String read = cursor
			// .getString(cursor.getColumnIndexOrThrow("read"));
			// String status = cursor.getString(cursor
			// .getColumnIndexOrThrow("status"));
			String type = cursor
					.getString(cursor.getColumnIndexOrThrow("type"));
			String body = cursor
					.getString(cursor.getColumnIndexOrThrow("body"));// 短信内容

			smsunit.set_id(_id);
			// smsunit.setThread_id(thread_id);
			smsunit.setAddress(address);
			smsunit.setDate(date);
			// smsunit.setRead(read);
			// smsunit.setStatus(status);
			smsunit.setType(type);
			smsunit.setBody(body);

			_smsUnitList.add(smsunit);

			// Log.v(Service139.TAG, Thread.currentThread().getName() + "---->"
			// + "sms in phone _id	=" + _id);
			// Log.v(Service139.TAG, Thread.currentThread().getName() + "---->"
			// + "sms in phone address	=" + address);

		}
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName() +
		// "---->"
		// + "smsUnitList.size	=" + _smsUnitList.size());
		return _smsUnitList;
	}

	/* 从Cursor 中读取出全部SMS收件箱信息 TXT方式 */
	// public static String fetch_SMSInbox_Info(Cursor cursor) {
	// String all_SMSInbox_Info = "";
	// int numcount = cursor.getCount();
	// for (int i = 0; i < numcount; i++) {
	// cursor.moveToPosition(i);
	// String body = cursor
	// .getString(cursor.getColumnIndexOrThrow("body"));// 短信内容
	// String number = cursor.getString(cursor
	// .getColumnIndexOrThrow("address"));// 电话号
	// String smsid = cursor
	// .getString(cursor.getColumnIndexOrThrow("_id"));// id
	// String thread_id = cursor.getString(cursor
	// .getColumnIndexOrThrow("thread_id"));
	// all_SMSInbox_Info += number + "*\r\n" + body + "*\r\n" + smsid
	// + "*\r\n";
	// }
	//
	// return all_SMSInbox_Info;
	// }

	// public static String fetch_SMSOutbox_Info(Cursor cursor) {
	// // TODO Auto-generated method stub
	// String all_SMSOutbox_Info = "";
	// int numcount = cursor.getCount();
	// for (int i = 0; i < numcount; i++) {
	// cursor.moveToPosition(i);
	// String body = cursor
	// .getString(cursor.getColumnIndexOrThrow("body"));// 短信内容
	// String number = cursor.getString(cursor
	// .getColumnIndexOrThrow("address"));// 电话号
	// String smsid = cursor
	// .getString(cursor.getColumnIndexOrThrow("_id"));// id
	// String thread_id = cursor.getString(cursor
	// .getColumnIndexOrThrow("thread_id"));
	// all_SMSOutbox_Info += number + "*\r\n" + body + "*\r\n" + smsid
	// + "*\r\n";
	// }
	// return all_SMSOutbox_Info;
	// }

	// netstat info
	public static String fetch_version_info() {
		String result = null;
		CMDExecute cmdexe = new CMDExecute();
		try {
			String[] args = { "/system/bin/cat", "/proc/version" };
			result = cmdexe.run(args, "/system/bin/");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// Log.v(Service139.TAG, result);
		return result;
	}

}
