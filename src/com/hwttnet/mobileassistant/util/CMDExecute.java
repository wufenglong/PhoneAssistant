package com.hwttnet.mobileassistant.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Contacts;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.hwttnet.mobileassistant.service.MoblieAssistantService;

public class CMDExecute {
	private final static String TAG = "CMDExecute";

	/* 获得手机已安装软件列表，并把内容发给PC端 */
	public static void GetInstalledAPPList(Context context, OutputStream out) {
		MyUtil.log(TAG, "**currCMD = GET_INSTALLEDAPP_LIST");
		String str_appDetail = FetchData.fetch_installed_apps(context);
		/* 判断数据为空 */
		if (str_appDetail == null || str_appDetail.trim().equals("")) {
			MyUtil.log(TAG, "str_appDetail is null");
			str_appDetail = " ";// 数据为空时，发送一个空格
		}
		try {
			// buffer = FileHelper.readFile(FetchData.AppInfoTXTFileName);
			// Log.v(MoblieAssistantService.TAG, "str_appDetail size="
			// + str_appDetail.length());
			out.write(str_appDetail.getBytes("utf-8"));
			out.flush();
			// Log.v(Service139.TAG, "write buffer end");
		} catch (IOException e) {
			e.printStackTrace();
			try {
				out.write("error".getBytes("utf-8"));
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* 获得手机已安装软件列表,返回软件数 */
	public static int GetInstalledAPPListSize(Context context) {

		List<ApplicationInfo> packages = context.getPackageManager()
				.getInstalledApplications(0);
		return packages.size();
	}

	/* 读取设备信息，并把内容发给PC端 */
	public static void GetDeviceInfoList(Context context, OutputStream out) {
		MyUtil.log(TAG, "**currCMD = GET_DEVICEINFO_LIST");
		try {
			// Log.v(Service139.TAG, "fetch_DeviceInfo start");
			/* 获得操作版本 */
			String str_sdkVerSion = FetchData.fetch_AndroidSdkVerSion();

			/* 获得TelephoneManager */
			TelephonyManager telMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String str_IMEI = telMgr.getDeviceId();
			String str_IMSI = telMgr.getSubscriberId();
			String str_DisplayMetrics = FetchData.fetch_DisplayMetrics(context);
			String str_Phone_DEVICE = Build.BRAND;// 商标，品牌(厂商)
			String str_PHone_MODEL = Build.MODEL;// 型号
			String str_trashfiel_size = "0";// 垃圾文件大小
			// Log.v(Service139.TAG, "fetch_DeviceInfo 111111");

			// String str_OS_Version = FetchData.fetch_version_info();
			// String str_OS_Version = FetchData.fetch_version_info();
			// Log.v(Service139.TAG, "str_OS_Version =" + str_OS_Version);
			/* 临时写死了Linux版本号 */
			// String str_OS_Version =
			// "Linux version 2.6.25 (LGE) (gcc version 4.1.1) #1 Tue Jun 8 19:31:46 KST 2010";
			String str_OS_Version = "Linux version 2.6.25 (LGE) (gcc version 4.1.1)";
			// Log.v(Service139.TAG, "fetch_DeviceInfo 222222");
			/* 新方法 */
			// String str_OS_Version = System.getProperty("os.name") + " "
			// + System.getProperty("os.version");
			String str_deviceinfo = str_sdkVerSion + "\r\n" + str_IMEI + "\r\n"
					+ str_DisplayMetrics + "\r\n" + str_Phone_DEVICE + "\r\n"
					+ str_PHone_MODEL + "\r\n" + str_IMSI + "\r\n"
					+ str_trashfiel_size + "\r\n" + str_OS_Version + "\r\n"
					+ ConstantsList.CURR_INSIDE_VERSIONNAME + "\r\n";

			/* 判断数据为空 */
			if (str_deviceinfo == null || str_deviceinfo.trim().equals("")) {
				Log.v(MoblieAssistantService.TAG, "str_deviceinfo is null");
				str_deviceinfo = " ";// 数据为空时，发送一个空格
			}
			// Log.v(MoblieAssistantService.TAG, "str_OS_Version ="
			// + str_OS_Version);
			// Log.v(MoblieAssistantService.TAG, "str_deviceinfo ="
			// + str_deviceinfo);
			// Log.v(MoblieAssistantService.TAG, "str_deviceinfo size="
			// + str_deviceinfo.length());
			out.write(str_deviceinfo.getBytes("utf-8"));
			out.flush();
		} catch (Exception e) {
			MyUtil.log(TAG, "fetch_DeviceInfo error");
			try {
				out.write("error".getBytes("utf-8"));
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/** 读取设备信息 */
	public static String[] GetDeviceInfoList(Context context) {
		String[] deviceInfo = new String[6];
		/* 获得操作版本 */
		deviceInfo[0] = FetchData.fetch_AndroidSdkVerSion();

		/* 获得TelephoneManager */
		TelephonyManager telMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceInfo[1] = telMgr.getSubscriberId();
		deviceInfo[2] = telMgr.getDeviceId();
		deviceInfo[3] = FetchData.fetch_DisplayMetrics(context);
		deviceInfo[4] = Build.BRAND;// 商标，品牌(厂商)
		deviceInfo[5] = Build.MODEL;// 型号
		return deviceInfo;
	}

	/* 读取开机启动信息 ，并把内容发给PC端 */
	public static void GetBootCompletedAPPList(Context context, OutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = GET_BOOT_COMPLETED_LIST");
		PackageManager packageManager = context.getPackageManager();

		Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED, null);// 开机启动的intent
		List<ResolveInfo> appsList = packageManager.queryBroadcastReceivers(
				intent, 0);// 搜索开机启动的intent的BroadcastReceivers
		Iterator<ResolveInfo> l = appsList.iterator();

		String strBootCompleted = "";
		while (l.hasNext()) {
			try {
				ResolveInfo app = (ResolveInfo) l.next();
				/* 如果ApplicationInfo为非系统程序，则读取出来 */
				boolean flag = false;
				if ((app.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					flag = true;
				}
				if (flag) {
					String packageName = app.activityInfo.packageName;// 包名
					/* 本守护程序不显示给用户看 */
					if (!packageName
							.equals(ConstantsList.MOBILE_ASSISTANT_PACKAGENAME)) {
						String label = (String) app.activityInfo
								.loadLabel(packageManager);// 程序名
						String appDir = app.activityInfo.applicationInfo.publicSourceDir;// 程序的路径
						File appFile = new File(appDir);// 由路径创建一个File
						String fileSize = appFile.length() + "";// 当前程序的大小
						PackageInfo pInfo = packageManager.getPackageInfo(
								packageName, Context.MODE_APPEND);
						String versionName_boot = pInfo.versionName;
						/* 修改版本号：1.0.0格式 */
						versionName_boot = FetchData
								.modify_VersionName(versionName_boot);
						strBootCompleted += "[" + packageName + "]" + "["
								+ label + "]" + "[" + versionName_boot + "]"
								+ "[" + fileSize + "]" + "\r\n";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/* 判断数据为空 */
		if (strBootCompleted == null || strBootCompleted.trim().equals("")) {
			Log.v(MoblieAssistantService.TAG, "strBootCompleted is null");
			strBootCompleted = " ";// 数据为空时，发送一个空格
		}
		try {
			// Log.v(MoblieAssistantService.TAG, "boot app list="
			// + strBootCompleted);
			// Log.v(MoblieAssistantService.TAG, "strBootCompleted size="
			// + strBootCompleted.length());
			out.write(strBootCompleted.getBytes("utf-8"));
			out.flush();
			// Log.v(MoblieAssistantService.TAG, "boot app list write over");
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "GetBootCompletedList error");
			try {
				out.write("error".getBytes("utf-8"));
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* 读取开机启动信息：返回一个列表 */
	public static ArrayList<String> GetBootCompletedAPPList(Context context) {
		PackageManager packageManager = context.getPackageManager();

		ArrayList<String> bootAppList = new ArrayList<String>();

		Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED, null);// 开机启动的intent
		List<ResolveInfo> appsList = packageManager.queryBroadcastReceivers(
				intent, 0);// 搜索开机启动的intent的BroadcastReceivers
		Iterator<ResolveInfo> l = appsList.iterator();

		while (l.hasNext()) {
			try {
				ResolveInfo app = (ResolveInfo) l.next();
				/* 如果ApplicationInfo为非系统程序，则读取出来 */
				boolean flag = false;
				if ((app.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					flag = true;
				}
				if (flag) {
					String packageName = app.activityInfo.packageName;// 包名
					// String label = (String) app.activityInfo
					// .loadLabel(packageManager);// 程序名
					bootAppList.add(packageName);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return bootAppList;
	}

	/* 读取开机启动信息：返回软件数 */
	public static int GetBootCompletedAPPListSize(Context context) {
		PackageManager packageManager = context.getPackageManager();

		Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED, null);// 开机启动的intent
		List<ResolveInfo> appsList = packageManager.queryBroadcastReceivers(
				intent, 0);// 搜索开机启动的intent的BroadcastReceivers

		return appsList.size();
	}

	/* 获得正在运行的程序列表 ，并把内容发给PC端 */
	public static void GetRunningTasksInfoList(Context context, OutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = GET_RUNNINGTASKS_INFO_LIST");
		PackageManager packageManager1 = context.getPackageManager();

		final ActivityManager activityManager1 = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager1.getRunningTasks(100);
		Iterator<RunningTaskInfo> l = tasks.iterator();
		String strRunningTasksInfo = "";
		while (l.hasNext()) {

			RunningTaskInfo ti = (RunningTaskInfo) l.next();

			String packageName = ti.baseActivity.getPackageName();// 包名
			PackageInfo pInfo = null;
			try {
				pInfo = packageManager1.getPackageInfo(packageName,
						Context.MODE_APPEND);
			} catch (NameNotFoundException e) {
				Log.e(MoblieAssistantService.TAG, "NameNotFoundException");
				e.printStackTrace();
			}

			String apppath = pInfo.applicationInfo.publicSourceDir;
			File appFile = new File(apppath);// 由路径创建一个File
			String fileSize = appFile.length() + "";// 当前程序的大小
			String labelname = (String) pInfo.applicationInfo
					.loadLabel(packageManager1);
			String versionName = pInfo.versionName;
			/* 修改版本号：1.0.0格式 */
			versionName = FetchData.modify_VersionName(versionName);
			// Log.v(Service139.TAG, "GetRunningTasksInfoList start3333333");
			strRunningTasksInfo += "[" + packageName + "]" + "[" + labelname
					+ "]" + "[" + versionName + "]" + "[" + fileSize + "]"
					+ "\r\n";
		}
		// Log.v(Service139.TAG, "strRunningTasksInfo=" + strRunningTasksInfo);
		/* 判断数据为空 */
		if (strRunningTasksInfo == null
				|| strRunningTasksInfo.trim().equals("")) {
			Log.v(MoblieAssistantService.TAG, "strRunningTasksInfo is null");
			strRunningTasksInfo = " ";// 数据为空时，发送一个空格
		}
		try {
			// Log.v(MoblieAssistantService.TAG, "strRunningTasksInfo size="
			// + strRunningTasksInfo.length());
			out.write(strRunningTasksInfo.getBytes("utf-8"));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "strRunningTasksInfo error");
			try {
				out.write("error".getBytes("utf-8"));
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* 获得正在运行的程序列表 ，返回软件数 */
	public static int GetRunningTasksInfoListSize(Context context) {

		ActivityManager activityManager1 = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager1.getRunningTasks(100);

		return tasks.size();
	}

	/* 获得contacts内容并生成XML文件,发送一个string */
	public static void GetContactsInfoList(Context context, OutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = GET_CONTACTS_INFO_LIST");
		/* 存放所有联系人列表 */
		ArrayList<ContactUnit> contactsUnitList = new ArrayList<ContactUnit>();
		/* 取得ContentResolver */
		ContentResolver content = context.getContentResolver();

		/* 生成所有ContactUnit对象，获得所有电话号码，把信息写入一个ContactUnit对象中 */
		getAllContactsPhonesNumber(content, contactsUnitList);
		/* 获得组织信息，写入对应的ContactUnit对象中 */
		getAllContactsOrganizationsInfo(content, contactsUnitList);
		/* 获得Email,IM,Postal信息，写入对应的ContactUnit对象中 */
		getAllContactsEmail_IM_PostalInfo(content, contactsUnitList);
		/* 根据contactsUnitList的内容，生成XML */
		String allContactsInfo = XmlUtil
				.CreateContactsXMLToString(contactsUnitList);

		// Log.e(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "allContactsInfo =" + allContactsInfo);

		/* 判断数据为空 */
		if (allContactsInfo == null || allContactsInfo.trim().equals("")) {
			Log.v(MoblieAssistantService.TAG, "all_Contacts_info is null");
			allContactsInfo = " ";// 数据为空时，发送一个空格
		}

		try {
			// Log.v(MoblieAssistantService.TAG, "all_Contacts_info size="
			// + allContactsInfo.length());
			out.write(allContactsInfo.getBytes("utf-8"));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "GetContactsInfoList error");
			try {
				out.write("error".getBytes("utf-8"));
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		/* 测试：把String写成XML */
		// File f = FileHelper.newFile("tmpcontact.txt");
		// try {
		// FileHelper.writeFile(f, allContactsInfo.getBytes());
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// Log.e(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "GetContactsInfoList() write XML error");
		// e.printStackTrace();
		// }
	}

	/* 获得contacts内容并生成XML文件,发送一个string */
	public static String GetContactsInfoList(Context context) {
		/* 存放所有联系人列表 */
		ArrayList<ContactUnit> contactsUnitList = new ArrayList<ContactUnit>();
		/* 取得ContentResolver */
		ContentResolver content = context.getContentResolver();

		/* 生成所有ContactUnit对象，获得所有电话号码，把信息写入一个ContactUnit对象中 */
		getAllContactsPhonesNumber(content, contactsUnitList);
		/* 获得组织信息，写入对应的ContactUnit对象中 */
		getAllContactsOrganizationsInfo(content, contactsUnitList);
		/* 获得Email,IM,Postal信息，写入对应的ContactUnit对象中 */
		getAllContactsEmail_IM_PostalInfo(content, contactsUnitList);
		/* 根据contactsUnitList的内容，生成XML */
		String allContactsInfo = XmlUtil
				.CreateContactsXMLToString(contactsUnitList);
		return allContactsInfo;

	}

	/* 返回电话本人数 */
	public static int getContactsSize(Context context) {
		/* 取得通讯录的People表的cursor */
		Cursor cursorPeopleContact = context.getContentResolver().query(
				Uri.parse("content://contacts/people"), null, null, null, null);
		int numPeople = cursorPeopleContact.getCount();// contacts中的人数
		return numPeople;
	}

	/* 获得所有电话号码，把信息写入一个ContactUnit对象中 */
	private static void getAllContactsPhonesNumber(
			ContentResolver contentResolver,
			ArrayList<ContactUnit> contactsUnitList) {
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "getAllContactsPhonesNumber() start..");
		/* 取得通讯录的People表的cursor */
		Cursor cursorPeopleContact = contentResolver.query(Uri
				.parse("content://contacts/people"), null, null, null, null);
		int numPeople = cursorPeopleContact.getCount();// contacts中的人数

//		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
//				+ "---->" + "Contacts people num =" + numPeople);

		/* 从Phones表中查询某人的所有电话数据 */

		Cursor cursorPhonesContact = contentResolver.query(Uri
				.parse("content://contacts/phones"), null, null, null, null);

		if (cursorPhonesContact == null) {
			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "?????cursorPhonesContact=null");
		}

		int numPhones = cursorPhonesContact.getCount();

		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "Contacts people numPhones =" + numPhones);
		/* 在联系人列表中加入所有联系人的电话信息 */
		for (int i = 0; i < numPeople; i++) {
			ContactUnit contactsUnit = new ContactUnit();

			cursorPeopleContact.moveToPosition(i);

			String _personid = cursorPeopleContact
					.getString(cursorPeopleContact
							.getColumnIndexOrThrow(Contacts.People._ID));
			String _name = cursorPeopleContact.getString(cursorPeopleContact
					.getColumnIndexOrThrow(Contacts.People.NAME));

			// Log.d(MoblieAssistantService.TAG,
			// Thread.currentThread().getName()
			// + "---->" + "getAllContactsPhonesNumber() _personid="
			// + _personid);
			/* 设置Person_id */
			contactsUnit.setPerson_id(_personid);
			/* 设置name */
			contactsUnit.setName(_name);

			/* 从Phones的cursor的找出与以上_personid相同的 */
			for (int indexPhones = 0; indexPhones < numPhones; indexPhones++) {
				cursorPhonesContact.moveToPosition(indexPhones);
				String personid_phone = cursorPhonesContact
						.getString(cursorPhonesContact
								.getColumnIndexOrThrow(Contacts.Phones.PERSON_ID));
				if (personid_phone.equals(_personid)) {
					String type_phone = cursorPhonesContact
							.getString(cursorPhonesContact
									.getColumnIndexOrThrow(Contacts.Phones.TYPE));
					String number_phone = cursorPhonesContact
							.getString(cursorPhonesContact
									.getColumnIndexOrThrow(Contacts.Phones.NUMBER));
					int indexTypePhones = Integer.parseInt(type_phone);
					// Log.v(MoblieAssistantService.TAG,
					// "Contacts people indexTypePhones =" + indexTypePhones);
					/* 设置电话号 */
					switch (indexTypePhones) {
					case ContactUnit.TYPE_PHONE_CUSTOM:
						String customlabel_phone = cursorPhonesContact
								.getString(cursorPhonesContact
										.getColumnIndexOrThrow(Contacts.Phones.LABEL));
						contactsUnit.setPhone_customlabel(customlabel_phone);
						contactsUnit.setPhone_custom(number_phone);
						break;
					case ContactUnit.TYPE_PHONE_FAX_HOME:
						contactsUnit.setPhone_Fax_home(number_phone);
						break;
					case ContactUnit.TYPE_PHONE_FAX_WORK:
						contactsUnit.setPhone_Fax_work(number_phone);
						break;
					case ContactUnit.TYPE_PHONE_HOME:
						contactsUnit.setPhone_home(number_phone);
						break;
					case ContactUnit.TYPE_PHONE_MOBILE:
						contactsUnit.setPhone_moblie(number_phone);
						break;
					case ContactUnit.TYPE_PHONE_OTHER:
						contactsUnit.setPhone_other(number_phone);
						break;
					case ContactUnit.TYPE_PHONE_PAGER:
						contactsUnit.setPhone_pager(number_phone);
						break;
					case ContactUnit.TYPE_PHONE_WORK:
						contactsUnit.setPhone_work(number_phone);
					default:
						break;
					}
				}
			}
			contactsUnitList.add(contactsUnit);
		}

		// Log.d(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "getAllContactsPhonesNumber() end..");
	}

	/* 获得组织信息，写入对应的ContactUnit对象中 */
	private static void getAllContactsOrganizationsInfo(
			ContentResolver contentResolver,
			ArrayList<ContactUnit> contactsUnitList) {
		// Log.d(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "getAllContactsOrganizationsInfo() start..");

		Cursor cursorOrganizationsContact = contentResolver.query(
				Contacts.Organizations.CONTENT_URI, null, null, null,
				Contacts.Organizations.DEFAULT_SORT_ORDER);

		int numOrganizations = cursorOrganizationsContact.getCount();// contacts中的Organizations数
		/* 遍历Organizations列表 */
		for (int indexOrg = 0; indexOrg < numOrganizations; indexOrg++) {
			cursorOrganizationsContact.moveToPosition(indexOrg);
			/* 取出person_id */
			String _person_id = cursorOrganizationsContact
					.getString(cursorOrganizationsContact
							.getColumnIndexOrThrow(Contacts.Organizations.PERSON_ID));

			/*
			 * 遍历contactsUnitList列表
			 * 根据person_id，从contactsUnitList中取出id=person_id的对象
			 */
			ContactUnit _contactUnit = new ContactUnit();
			for (int indexcul = 0; indexcul < contactsUnitList.size(); indexcul++) {
				String person_id_contactUnit = contactsUnitList.get(indexcul)
						.getPerson_id();
				/* ID相等时 */
				if (person_id_contactUnit.equals(_person_id)) {
					_contactUnit = contactsUnitList.get(indexcul);
					break;
				}
			}
			/* 取出type */
			String _typeOrganizations = cursorOrganizationsContact
					.getString(cursorOrganizationsContact
							.getColumnIndexOrThrow(Contacts.Organizations.TYPE));
			String _companyOrganizations = cursorOrganizationsContact
					.getString(cursorOrganizationsContact
							.getColumnIndexOrThrow(Contacts.Organizations.COMPANY));
			String _titleOrganizations = cursorOrganizationsContact
					.getString(cursorOrganizationsContact
							.getColumnIndexOrThrow(Contacts.Organizations.TITLE));

			/* 根据type分别设置_contactUnit的内容 */
			switch (Integer.parseInt(_typeOrganizations)) {
			case ContactUnit.TYPE_ORGANIZATION_CUSTOM:
				String _labelOrganizations = cursorOrganizationsContact
						.getString(cursorOrganizationsContact
								.getColumnIndexOrThrow(Contacts.Organizations.LABEL));

				_contactUnit.setOrganization_customlabel(_labelOrganizations);
				_contactUnit
						.setOrganization_custom_company(_companyOrganizations);
				_contactUnit.setOrganization_custom_title(_titleOrganizations);
				break;
			case ContactUnit.TYPE_ORGANIZATION_OTHER:
				_contactUnit
						.setOrganization_other_company(_companyOrganizations);
				_contactUnit.setOrganization_other_title(_titleOrganizations);
				break;
			case ContactUnit.TYPE_ORGANIZATION_WORK:
				_contactUnit
						.setOrganization_work_company(_companyOrganizations);
				_contactUnit.setOrganization_work_title(_titleOrganizations);
				break;
			default:
				break;
			}
		}

		// Log.d(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "getAllContactsOrganizationsInfo() end..");
	}

	/* 获得emali信息，写入对应的ContactUnit对象中 */
	private static void getAllContactsEmail_IM_PostalInfo(
			ContentResolver contentResolver,
			ArrayList<ContactUnit> contactsUnitList) {
		// Log.d(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "getAllContactsEmail_IM_PostalInfo() start..");

		Cursor cursorEmail_IM_PostalContact = contentResolver.query(
				Contacts.ContactMethods.CONTENT_URI, null, null, null,
				Contacts.ContactMethods.DEFAULT_SORT_ORDER);

		int numEmail_IM_Postal = cursorEmail_IM_PostalContact.getCount();// contacts中的Email_IM_PostalContact数
		/* 遍历Email_IM_Postal列表 */
		for (int indexEIP = 0; indexEIP < numEmail_IM_Postal; indexEIP++) {
			cursorEmail_IM_PostalContact.moveToPosition(indexEIP);
			/* 取出person_id */
			String _person_id = cursorEmail_IM_PostalContact
					.getString(cursorEmail_IM_PostalContact
							.getColumnIndexOrThrow(Contacts.ContactMethods.PERSON_ID));

			/*
			 * 遍历contactsUnitList列表
			 * 根据person_id，从contactsUnitList中取出id=person_id的对象
			 */
			ContactUnit _contactUnit = new ContactUnit();
			for (int indexcul = 0; indexcul < contactsUnitList.size(); indexcul++) {
				String person_id_contactUnit = contactsUnitList.get(indexcul)
						.getPerson_id();
				/* ID相等时 */
				if (person_id_contactUnit.equals(_person_id)) {
					_contactUnit = contactsUnitList.get(indexcul);
					break;
				}
			}
			/* 获得KIND */
			String _kindEmail_IM_Postal = cursorEmail_IM_PostalContact
					.getString(cursorEmail_IM_PostalContact
							.getColumnIndexOrThrow(Contacts.ContactMethods.KIND));
			/* 获得data */
			String _dataEmail_IM_Postal = cursorEmail_IM_PostalContact
					.getString(cursorEmail_IM_PostalContact
							.getColumnIndexOrThrow(Contacts.ContactMethods.DATA));
			/* 获得type */
			String _typeEmail_IM_Postal = cursorEmail_IM_PostalContact
					.getString(cursorEmail_IM_PostalContact
							.getColumnIndexOrThrow(Contacts.ContactMethods.TYPE));
			/* 类型：EMALI;IM;POSTAL */
			switch (Integer.parseInt(_kindEmail_IM_Postal)) {
			case Contacts.KIND_EMAIL:// email

				/* EMALI里的四个分类 */
				switch (Integer.parseInt(_typeEmail_IM_Postal)) {
				case Contacts.ContactMethods.TYPE_CUSTOM:
					String _labelEmail = cursorEmail_IM_PostalContact
							.getString(cursorEmail_IM_PostalContact
									.getColumnIndexOrThrow(Contacts.ContactMethods.LABEL));
					_contactUnit.setEmail_custom(_dataEmail_IM_Postal);
					_contactUnit.setEmail_customlabel(_labelEmail);
					break;
				case Contacts.ContactMethods.TYPE_HOME:
					_contactUnit.setEmail_home(_dataEmail_IM_Postal);
					break;
				case Contacts.ContactMethods.TYPE_OTHER:
					_contactUnit.setEmail_other(_dataEmail_IM_Postal);
					break;
				case Contacts.ContactMethods.TYPE_WORK:
					_contactUnit.setEmail_work(_dataEmail_IM_Postal);
					break;
				default:
					break;
				}
				break;
			case Contacts.KIND_POSTAL:// postal
				/* POSTAL里的四个分类 */
				switch (Integer.parseInt(_typeEmail_IM_Postal)) {
				case Contacts.ContactMethods.TYPE_CUSTOM:
					String _labelPostal = cursorEmail_IM_PostalContact
							.getString(cursorEmail_IM_PostalContact
									.getColumnIndexOrThrow(Contacts.ContactMethods.LABEL));
					_contactUnit.setPostal_custom(_dataEmail_IM_Postal);
					_contactUnit.setPostal_customlabel(_labelPostal);
					break;
				case Contacts.ContactMethods.TYPE_HOME:
					_contactUnit.setPostal_home(_dataEmail_IM_Postal);
					break;
				case Contacts.ContactMethods.TYPE_OTHER:
					_contactUnit.setPostal_other(_dataEmail_IM_Postal);
					break;
				case Contacts.ContactMethods.TYPE_WORK:
					_contactUnit.setPostal_work(_dataEmail_IM_Postal);
					break;
				default:
					break;
				}
				break;
			case Contacts.KIND_IM:// IM
				// String _auxdata = cursorEmail_IM_PostalContact
				// .getString(cursorEmail_IM_PostalContact
				// .getColumnIndexOrThrow(Contacts.ContactMethods.AUX_DATA));
				// /* IM里的8个分类 */
				// if (_auxdata.equals("pre:0")) {// aim
				// _contactUnit.setIm_aim(_dataEmail_IM_Postal);
				// } else if (_auxdata.equals("pre:1")) {// windowlive
				// _contactUnit.setIm_windowslive(_dataEmail_IM_Postal);
				// } else if (_auxdata.equals("pre:2")) {// yahoo
				// _contactUnit.setIm_yahoo(_dataEmail_IM_Postal);
				// } else if (_auxdata.equals("pre:3")) {// skepe
				// _contactUnit.setIm_skepe(_dataEmail_IM_Postal);
				// } else if (_auxdata.equals("pre:4")) {// qq
				// _contactUnit.setIm_qq(_dataEmail_IM_Postal);
				// } else if (_auxdata.equals("pre:5")) {// google talk
				// _contactUnit.setIm_googletalk(_dataEmail_IM_Postal);
				// } else if (_auxdata.equals("pre:6")) {// icq
				// _contactUnit.setIm_icq(_dataEmail_IM_Postal);
				// } else if (_auxdata.equals("pre:7")) {// jabber
				// _contactUnit.setIm_jabber(_dataEmail_IM_Postal);
				// }

				break;
			default:
				break;
			}
		}
		// Log.d(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "getAllContactsEmail_IM_PostalInfo() end..");
	}

	/* 获得短信SMS收件箱Inbox ，并把内容发给PC端 */
	public static void GetSMSInboxInfoList(Context context, OutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = GET_SMSINBOX_INFO_LIST");

		/* 查询收件箱 */
		Cursor cursor = context.getContentResolver().query(
				Uri.parse("content://sms/inbox"), null, null, null, null);

		/* 返回字符串 */
		String all_SMSInbox_Info = FetchData
				.fetch_SMS_Info_returnString(cursor);

		/* 判断数据为空 */
		if (all_SMSInbox_Info == null || all_SMSInbox_Info.trim().equals("")) {
			Log.v(MoblieAssistantService.TAG, "all_Contacts_info is null");
			// 数据为空时用空格代替
			all_SMSInbox_Info = " ";
		}

		/* 把信息文件写到SD卡里 */
		// File f = FileHelper.newFile("tmpInbox.xml");
		// try {
		// FileHelper.writeFile(f, all_SMSInbox_Info.getBytes("UTF-8"));
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		try {
			// Log.v(MoblieAssistantService.TAG, "all_SMSInbox_Info size="
			// + all_SMSInbox_Info.length());
			out.write(all_SMSInbox_Info.getBytes("utf-8"));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "GetSMSInboxInfoList error");
			try {
				out.write("error".getBytes("utf-8"));
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* 手机端:获得短信SMS收件箱Inbox ，返回String */
	public static String GetSMSInboxInfoListForPhone(Context context) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = GET_SMSINBOX_INFO_LIST");

		/* 查询收件箱 */
		Cursor cursor = context.getContentResolver().query(
				Uri.parse("content://sms/inbox"), null, null, null, null);

		/* 返回字符串 */
		String all_SMSInbox_Info = FetchData
				.fetch_SMS_Info_returnString(cursor);

		return all_SMSInbox_Info;
	}

	/* 获得短信SMS信息，返回ArrayList<SMSUnit> */
	public static ArrayList<SMSUnit> GetSMSInboxInfoList(Context context) {

		/* 查询收件箱 */
		Cursor cursor = context.getContentResolver().query(
				Uri.parse("content://sms/inbox"), null, null, null, null);

		/* 返回字符串 */
		ArrayList<SMSUnit> _smsUnit = FetchData
				.fetch_SMS_Info_returnArrayList(cursor);
		cursor.close();
		return _smsUnit;

	}

	public static int getSMSInboxSize(Context context) {
		/* 查询收件箱 */
		Cursor cursor = context.getContentResolver().query(
				Uri.parse("content://sms/inbox"), null, null, null, null);
		int num = cursor.getCount();
		cursor.close();
		return num;
	}

	public static void GetSMSSentboxInfoList(Context context, OutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = GET_SMSSENTBOX_INFO_LIST");

		/* 查询收件箱 */
		Cursor cursor = context.getContentResolver().query(
				Uri.parse("content://sms/sent"), null, null, null, null);

		/* 返回字符串 */
		String all_SMSSentbox_Info = FetchData
				.fetch_SMS_Info_returnString(cursor);

		/* 判断数据为空 */
		if (all_SMSSentbox_Info == null
				|| all_SMSSentbox_Info.trim().equals("")) {
			Log.v(MoblieAssistantService.TAG, "all_Contacts_info is null");
			// 数据为空时用空格代替
			all_SMSSentbox_Info = " ";
		}

		/* 把信息文件写到SD卡里 */
		// File f = FileHelper.newFile("tmpSentbox.xml");
		// try {
		// FileHelper.writeFile(f, all_SMSSentbox_Info.getBytes("UTF-8"));
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		try {
			// Log.v(MoblieAssistantService.TAG, "all_SMSSentbox_Info size="
			// + all_SMSSentbox_Info.length());
			out.write(all_SMSSentbox_Info.getBytes("utf-8"));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "GetSMSSentboxInfo error");
			try {
				out.write("error".getBytes("utf-8"));
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* 手机端 */
	public static String GetSMSSentboxInfoListForPhone(Context context) {

		/* 查询收件箱 */
		Cursor cursor = context.getContentResolver().query(
				Uri.parse("content://sms/sent"), null, null, null, null);

		/* 返回字符串 */
		String all_SMSSentbox_Info = FetchData
				.fetch_SMS_Info_returnString(cursor);
		cursor.close();
		return all_SMSSentbox_Info;
	}

	/* 获得短信SMS信息，返回ArrayList<SMSUnit> */
	public static ArrayList<SMSUnit> GetSMSSentboxInfoList(Context context) {

		/* 查询收件箱 */
		Cursor cursor = context.getContentResolver().query(
				Uri.parse("content://sms/sent"), null, null, null, null);

		/* 返回字符串 */
		ArrayList<SMSUnit> _smsUnit = FetchData
				.fetch_SMS_Info_returnArrayList(cursor);
		cursor.close();
		return _smsUnit;

	}

	public static int getSMSSentboxSize(Context context) {
		/* 查询收件箱 */
		Cursor cursor = context.getContentResolver().query(
				Uri.parse("content://sms/sent"), null, null, null, null);
		int num = cursor.getCount();
		cursor.close();
		return num;
	}

	/* 更新电话本 */
	public static void UpdateContacts(Context context, InputStream in,
			OutputStream out, ArrayList<ThreadUpdateOperate> updateContactsList) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = UPDATE_CONTACTS");
		int numUpdateContactsList = updateContactsList.size();
		/* 结束列表中的线程 */
		for (int indexUpdateContactsThread = 0; indexUpdateContactsThread < numUpdateContactsList; indexUpdateContactsThread++) {
			updateContactsList.get(indexUpdateContactsThread).stopMe();
		}
		updateContactsList.clear();// 清空线程列表
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ThreadUpdateOperate.currcommand = ThreadUpdateOperate.UPDATE_CONTACTS;
		ThreadUpdateOperate newUpdateContactsThread = new ThreadUpdateOperate(
				context);
		/* 当前线程加入线程列表 */
		updateContactsList.add(newUpdateContactsThread);
		new Thread(newUpdateContactsThread).start();
		// 给PC写更新结束
		try {
			out.write("UpdateContacts end".getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 更新收件箱 */
	public static void UpdateSMSInbox(Context context, InputStream in,
			OutputStream out, ArrayList<ThreadUpdateOperate> _updateSMSInboxList) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = UPDATE_SMSINBOX");
		int numUpdateSMSInboxList = _updateSMSInboxList.size();
		/* 结束列表中的线程 */
		for (int indexUpdateSMSInboxThread = 0; indexUpdateSMSInboxThread < numUpdateSMSInboxList; indexUpdateSMSInboxThread++) {
			_updateSMSInboxList.get(indexUpdateSMSInboxThread).stopMe();
		}
		_updateSMSInboxList.clear();// 清空线程列表
		ThreadUpdateOperate.currcommand = ThreadUpdateOperate.UPDATE_SMSINBOX;
		ThreadUpdateOperate newUpdateSMSInboxThread = new ThreadUpdateOperate(
				context);
		/* 当前线程加入线程列表 */
		_updateSMSInboxList.add(newUpdateSMSInboxThread);
		new Thread(newUpdateSMSInboxThread).start();
		/* 准备接收文件数据 */
		try {
			out.write("service receive OK".getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 更新发件箱 */
	public static void UpdateSMSSentbox(Context context, InputStream in,
			OutputStream out,
			ArrayList<ThreadUpdateOperate> _updateSMSSentboxList) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = UPDATE_SMSSENTBOX");
		int numUpdateSMSSentboxList = _updateSMSSentboxList.size();
		/* 结束列表中的线程 */
		for (int indexUpdateSMSSentboxThread = 0; indexUpdateSMSSentboxThread < numUpdateSMSSentboxList; indexUpdateSMSSentboxThread++) {
			_updateSMSSentboxList.get(indexUpdateSMSSentboxThread).stopMe();
		}
		_updateSMSSentboxList.clear();// 清空线程列表
		ThreadUpdateOperate.currcommand = ThreadUpdateOperate.UPDATE_SMSSENTBOX;
		ThreadUpdateOperate newUpdateSMSSentboxThread = new ThreadUpdateOperate(
				context);
		/* 当前线程加入线程列表 */
		_updateSMSSentboxList.add(newUpdateSMSSentboxThread);
		new Thread(newUpdateSMSSentboxThread).start();
		/* 准备接收文件数据 */
		try {
			out.write("service receive OK".getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 清理垃圾 */
	public static void CleanTrash(Context context, OutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = CLEAN_TRASH");

		/* 清理：Cookie */
		CookieSyncManager.createInstance(context);
		CookieManager.getInstance().removeAllCookie();
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "*********1*********");
		/* 清理上网痕迹 ：删除com.android.browser下的文件夹（除lib） */

		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "*********4*********");
		try {
			out.write("service receive OK".getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 清理垃圾 */
	public static void CleanTrash(Context context) {
		/* 清理：Cookie */
		CookieSyncManager.createInstance(context);
		CookieManager.getInstance().removeAllCookie();
	}

	/* 关闭正在运行的程序,读写两 ，与PC端交互 */
	public static void CloseRunningTasks(Context context, InputStream in,
			OutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = CLOSE_RUNNINGTASKS");
		/* 命令接收成功 */
		try {
			out.write("service ready OK".getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* 再接收要关闭程序的包名 */
		String strCloseTask = ioHelper.readCMDFromSocket(in);
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "close task is packagename=" + strCloseTask);
		/* 关闭程序 */
		ActivityManager activityManager1 = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager1.getRunningTasks(100);
		Iterator<RunningTaskInfo> l = tasks.iterator();
		while (l.hasNext()) {
			RunningTaskInfo ti = (RunningTaskInfo) l.next();
			String packageName = ti.baseActivity.getPackageName();// 包名
			/* 判断要关闭的包名是否在列表里 */
			if (packageName.equals(strCloseTask)) {
				// Log.v(MoblieAssistantService.TAG, Thread.currentThread()
				// .getName()
				// + "---->"
				// + "the will close tast is in runningtask list");
				// activityManager1.restartPackage(strCloseTask);
				// Log.v(MoblieAssistantService.TAG, Thread.currentThread()
				// .getName()
				// + "---->" + "close task is OK");
				break;
			}
		}

		/* 发送关闭成功 */
		try {
			out.write("close running tast OK".getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "send close task is OK");
	}

	/* 关闭多个正在运行的程序 :disabledBootList为禁止列表 */
	public static void CloseRunningTasks(Context context,
			ArrayList<String> disabledBootList) {
		Log.v(MoblieAssistantService.TAG, "CloseRunningTasks start");
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ActivityManager activityManager1 = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		Iterator dbI = disabledBootList.iterator();

		while (dbI.hasNext()) {
			String _packageName = (String) dbI.next();
			Log.v(MoblieAssistantService.TAG, "Close _packageName="
					+ _packageName.trim());
			activityManager1.restartPackage(_packageName.trim());
		}

		// /* 先获得正在运行的程序列表，再关闭程序（问题：目前此方法不能获得正在运行的service） */
		// ActivityManager activityManager1 = (ActivityManager) context
		// .getSystemService(Context.ACTIVITY_SERVICE);
		// List<RunningTaskInfo> tasks = activityManager1.getRunningTasks(100);
		// Iterator<RunningTaskInfo> l = tasks.iterator();
		// while (l.hasNext()) {
		// RunningTaskInfo ti = (RunningTaskInfo) l.next();
		// String packageName = ti.baseActivity.getPackageName();// 包名
		// Log.v(Service139.TAG, Thread.currentThread().getName() + "---->"
		// + packageName);
		// /* 判断要关闭的包名是否在列表里 */
		// if (disabledBootList.contains(packageName)) {
		// Log.v(Service139.TAG, Thread.currentThread().getName()
		// + "---->" + "close " + packageName);
		// activityManager1.restartPackage(packageName);
		// Log.v(Service139.TAG, Thread.currentThread().getName()
		// + "---->" + "close task is OK");
		// break;
		// }
		// }
		Log.v(MoblieAssistantService.TAG, "CloseRunningTasks end");
	}

	/* 从本程序目录下读出"被关闭的开机启动程序列表"，并把内容发给PC端 */
	public static void GetShutAutoStartInfoListFromTXT(Context context,
			BufferedOutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = GET_SHUT_BOOTAPP_LIST");
		InputStream is = null;
		boolean bOpenfileError = false;// 打开文件错误
		try {
			is = context.openFileInput(ConstantsList.FILENAME_SHUT_AUTOSTART);
		} catch (FileNotFoundException e2) {
			bOpenfileError = true;
			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "!!!!!!bOpenfileError!!!!!!!!");
			e2.printStackTrace();
		}
		if (!bOpenfileError) {
			try {

				int len = is.available();
				Log.v(MoblieAssistantService.TAG, Thread.currentThread()
						.getName()
						+ "---->" + "len=" + len);
				byte[] bytes = new byte[len];
				is.read(bytes);

				String shutAutoStartInfo = new String(bytes).trim();
				Log.v(MoblieAssistantService.TAG, "shutAutoStartInfo="
						+ shutAutoStartInfo);
				String shutInfo = shutAutoStartInfo.trim();
				if (shutAutoStartInfo.trim() == null) {
					Log
							.v(MoblieAssistantService.TAG,
									"shutAutoStartInfo==null");
					shutAutoStartInfo = " **";
				}
				try {
					// buffer =
					// FileHelper.readFile(FetchData.AppInfoTXTFileName);
					out.write(shutAutoStartInfo.getBytes("utf-8"));
					out.flush();
					Log.v(MoblieAssistantService.TAG, "write buffer end");
				} catch (IOException e) {
					e.printStackTrace();
					try {
						out.write("error".getBytes("utf-8"));
						out.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				try {
					out.write("error".getBytes("utf-8"));
					out.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				try {
					out.write("error".getBytes("utf-8"));
					out.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		} else {// 出错或为空时
			try {
				out.write(" **".getBytes("utf-8"));
				out.flush();
				Log
						.v(
								MoblieAssistantService.TAG,
								Thread.currentThread().getName()
										+ "---->"
										+ "!!!!!!GetShutAutoStartInfoListFromTXT 111111111!!!!!!!!");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/* 从本程序目录下读出"被关闭的开机启动程序列表"，返回一个ArrayList<String> */
	public static ArrayList<String> GetShutAutoStartInfoListFromTXT(
			Context context) {
		ArrayList<String> shutstartList = new ArrayList<String>();
		String shutAutoStartInfo = "";
		InputStream is = null;
		boolean bOpenfileError = false;// 打开文件错误
		try {
			is = context.openFileInput(ConstantsList.FILENAME_SHUT_AUTOSTART);
		} catch (FileNotFoundException e2) {
			bOpenfileError = true;
			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "!!!!!!bOpenfileError!!!!!!!!");
			e2.printStackTrace();
		}
		if (!bOpenfileError) {
			try {
				int len = is.available();
				byte[] bytes = new byte[len];
				is.read(bytes);

				shutAutoStartInfo = new String(bytes);
				Log.v(MoblieAssistantService.TAG, "shutAutoStartInfo="
						+ shutAutoStartInfo);
				/* 解析字符串，格式为：包名+"**" */
				int _startIndex_packagename = 0;// 每个包号开始的索引
				String _packagename = "";// 解析的包名
				int index_Star = 0;// 当前星号的索引位置
				boolean bRun = true;
				while (bRun) {
					index_Star = shutAutoStartInfo.indexOf("**",
							_startIndex_packagename);
					Log.v(MoblieAssistantService.TAG, "index_Star="
							+ index_Star);
					if (index_Star != -1) {
						_packagename = shutAutoStartInfo.substring(
								_startIndex_packagename, index_Star);
						_startIndex_packagename = index_Star + 2;
						shutstartList.add(_packagename);
						Log.v(MoblieAssistantService.TAG, "_packagename="
								+ _packagename);
					} else {
						Log.v(MoblieAssistantService.TAG, "end");
						bRun = false;
					}
				}

			} catch (FileNotFoundException e) {
				Log.v(MoblieAssistantService.TAG, "FileNotFoundException");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return shutstartList;
	}

	/*
	 * 功能： 更新当前目录下保存的"禁止开机启动列表"
	 * 
	 * 文件格式：不分行，包名+"**"(包名加两个星号)
	 * 
	 * 步骤：
	 * 
	 * 1.从/data/local/tmp里读出PC发来的ShutAutoStart.txt
	 * 2.把内容写入程序当前目录下的ShutAutoStart.txt
	 * （/data/data/com.hwttnet.moblieassistant139 .service）
	 */

	public static void UpdateShutAutoStartTXT(Context context, OutputStream out) {

		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = UPDATE_SHUT_BOOTAPP");

		boolean bfileNotExist = false;// 标记文件存在不存在
		/* 从指定目录下读文件：读出byte[] */
		File file = new File(ConstantsList.PATH_SHUT_AUTOSTART_FROM_PC,
				ConstantsList.FILENAME_SHUT_AUTOSTART);
		if (!file.exists()) {
			Log.v(MoblieAssistantService.TAG, "****file is no exists");
			bfileNotExist = true;
		}

		if (!bfileNotExist) {// 文件存在
			boolean bReadFileErro = false;// 文件读取错误
			FileInputStream fis;
			byte[] b = null;
			try {
				file.createNewFile();
				fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				int leng = bis.available();
				// Log.v(Service139.TAG, "filesize = " + leng);
				b = new byte[leng];
				bis.read(b, 0, leng);
				bis.close();
				Log.v(MoblieAssistantService.TAG, "ShutAutoStart.txt="
						+ new String(b));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				bReadFileErro = true;
			} catch (IOException e) {
				bReadFileErro = true;
				e.printStackTrace();
			}

			if (!bReadFileErro) {
				String shutTXT = new String(b).trim();
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
		}

		/* 写给Pc端OK */
		try {
			out.write("UPDATE_SHUT_BOOTAPP is ok".getBytes("utf-8"));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "GetContactsInfoList error");
			try {
				out.write("error".getBytes("utf-8"));
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/* 清理SMS草稿箱 */
	public static void ClearSMSDraft(Context context, InputStream in,
			OutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = CLEAN_SMSDRAFT");

		ThreadUpdateOperate.currcommand = ThreadUpdateOperate.CLEAN_SMSDRAFT;
		new Thread(new ThreadUpdateOperate(context)).start();

		/* clear end */
		try {
			out.write("ClearSMSDraft OK".getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 清理SMS收件箱 */
	public static void ClearSMSInbox(Context context, InputStream in,
			OutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = CLEAN_SMSINBOX");

		ThreadUpdateOperate.currcommand = ThreadUpdateOperate.CLEAN_SMSINBOX;
		new Thread(new ThreadUpdateOperate(context)).start();

		/* clear end */
		try {
			out.write("ClearSMSInbox OK".getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 清理SMS已发送发件箱 */
	public static void ClearSMSSentbox(Context context, InputStream in,
			OutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = CLEAN_SMSSENTBOX");

		ThreadUpdateOperate.currcommand = ThreadUpdateOperate.CLEAN_SMSSENTBOX;
		new Thread(new ThreadUpdateOperate(context)).start();

		/* clear end */
		try {
			out.write("ClearSMSSentbox OK".getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 清理SMS发件箱 */
	public static void ClearSMSOutbox(Context context, InputStream in,
			OutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = CLEAN_SMSOUTBOX");

		ThreadUpdateOperate.currcommand = ThreadUpdateOperate.CLEAN_SMSOUTBOX;
		new Thread(new ThreadUpdateOperate(context)).start();

		/* clear end */
		try {
			out.write("ClearSMSOutbox OK".getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 测试传输文件 */
	public static void TestReceiveFile(Context context, InputStream in,
			OutputStream out) {
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "**currCMD = TEST_SENTFILE");

		/* 准备接收文件数据 */
		try {
			out.write("service receive OK".getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* 接收文件数据，4字节文件长度，4字节文件格式，其后是文件数据 */
		byte[] filelength = new byte[4];
		byte[] fileformat = new byte[4];
		byte[] filebytes = null;

		/* 从socket流中读取完整文件数据 */
		filebytes = ioHelper.receiveFileFromSocket(in, out, filelength,
				fileformat);

		// Log.v(Service139.TAG, "receive data =" + new String(filebytes));
		try {
			/* 生成文件 */
			File file = FileHelper.newFile("QQWubi_Setup_11_220.exe");
			FileHelper.writeFile(file, filebytes, 0, filebytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			out.write("read file ok".getBytes("utf-8"));
			out.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public synchronized String run(String[] cmd, String workdirectory)
			throws IOException {
		String result = "";

		try {
			ProcessBuilder builder = new ProcessBuilder(cmd);
			// set working directory
			if (workdirectory != null)
				builder.directory(new File(workdirectory));
			builder.redirectErrorStream(true);
			Process process = builder.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[1024];
			while (in.read(re) != -1) {
				System.out.println(new String(re));
				result = result + new String(re);
			}
			in.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		String result = null;
		CMDExecute cmdexe = new CMDExecute();
		try {
			result = cmdexe.run(args, "D:\\MyProject\\colimas\\axis_c");
			System.out.println(result);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/** 手机端程序读XML文件用的 */
	public static ArrayList<ContactUnit> readContactsXMLFileForPhone(
			String path, String filename) {
		File inFile = new File(path, filename);
		if (!inFile.exists()) {
			/* 文件不存在，就不进行更新操作 */
			return null;
		}
		try {
			inFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
		}
		Document doc = null;
		try {
			doc = db.parse(inFile);
		} catch (DOMException dom) {
			return null;
		} catch (IOException ioe) {
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		}

		ArrayList<ContactUnit> contactUnitList = new ArrayList<ContactUnit>();
		// 下面是解析XML的全过程，比较简单，先取根元素"contactlist"
		Element root = doc.getDocumentElement();
		// 取"contact"元素列表
		NodeList contactList = root.getElementsByTagName("contact");
		int contactnum = contactList.getLength();
		for (int indexContact = 0; indexContact < contactnum; indexContact++) {
			// 依次取每个"contact"元素
			Element contactelement = (Element) contactList.item(indexContact);
			if (contactelement.hasChildNodes()) {
				// 创建一个ContactUnit的实例
				ContactUnit contactUnit = new ContactUnit();
				/* 取"id"节点 */
				NodeList _idList = contactelement.getElementsByTagName("id");
				if (_idList.getLength() == 1) {
					Element e = (Element) _idList.item(0);
					if (e.hasChildNodes()) {
						Text t = (Text) e.getFirstChild();
						contactUnit.setPerson_id(t.getNodeValue());
					}
				}

				/* 取"name"节点 */
				NodeList _nameList = contactelement
						.getElementsByTagName("name");
				if (_nameList.getLength() == 1) {
					Element e = (Element) _nameList.item(0);
					if (e.hasChildNodes()) {
						Text t = (Text) e.getFirstChild();
						contactUnit.setName(t.getNodeValue());
					}
				}

				/* 取"phone"节点 */
				NodeList _phoneList = contactelement
						.getElementsByTagName("phone");
				int _phonenum = _phoneList.getLength();
				for (int indexphone = 0; indexphone < _phonenum; indexphone++) {
					// 依次取每个"phone"节点
					Element phoneElement = (Element) _phoneList
							.item(indexphone);
					// 取"home"元素，下面类同
					NodeList _homephone = phoneElement
							.getElementsByTagName("home");
					if (_homephone.getLength() == 1) {
						Element e = (Element) _homephone.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPhone_home(t.getNodeValue());
						}
					}
					// 取"mobile"元素，下面类同
					NodeList _mobilephone = phoneElement
							.getElementsByTagName("mobile");
					if (_mobilephone.getLength() == 1) {
						Element e = (Element) _mobilephone.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPhone_moblie(t.getNodeValue());
						}
					}
					// 取"work"元素，下面类同
					NodeList _workphone = phoneElement
							.getElementsByTagName("work");
					if (_workphone.getLength() == 1) {
						Element e = (Element) _workphone.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPhone_work(t.getNodeValue());
						}
					}
					// 取"fax_work"元素，下面类同
					NodeList _fax_workphone = phoneElement
							.getElementsByTagName("fax_work");
					if (_fax_workphone.getLength() == 1) {
						Element e = (Element) _fax_workphone.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPhone_Fax_work(t.getNodeValue());
						}
					}
					// 取"fax_home"元素，下面类同
					NodeList _fax_homephone = phoneElement
							.getElementsByTagName("fax_home");
					if (_fax_homephone.getLength() == 1) {
						Element e = (Element) _fax_homephone.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPhone_Fax_home(t.getNodeValue());
						}
					}
					// 取"pager"元素，下面类同
					NodeList _pagerphone = phoneElement
							.getElementsByTagName("pager");
					if (_pagerphone.getLength() == 1) {
						Element e = (Element) _pagerphone.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPhone_pager(t.getNodeValue());
						}
					}
					// 取"other"元素，下面类同
					NodeList _otherphone = phoneElement
							.getElementsByTagName("other");
					if (_otherphone.getLength() == 1) {
						Element e = (Element) _otherphone.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPhone_other(t.getNodeValue());
						}
					}
					// 取"custom"元素，下面类同
					NodeList _customphone = phoneElement
							.getElementsByTagName("custom");
					if (_customphone.getLength() == 1) {
						Element e = (Element) _customphone.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPhone_custom(t.getNodeValue());
						}
					}
					// 取"customlabel"元素，下面类同
					NodeList _customlabelphone = phoneElement
							.getElementsByTagName("customlabel");
					if (_customlabelphone.getLength() == 1) {
						Element e = (Element) _customlabelphone.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPhone_customlabel(t.getNodeValue());
						}
					}
				}

				/* 取"email"节点 */
				NodeList _emailList = contactelement
						.getElementsByTagName("email");
				int _emailnum = _emailList.getLength();
				for (int indexEmail = 0; indexEmail < _emailnum; indexEmail++) {
					// 依次取每个"email"节点
					Element emailElement = (Element) _emailList
							.item(indexEmail);
					// 取"home"元素，下面类同
					NodeList _homeEmail = emailElement
							.getElementsByTagName("home");
					if (_homeEmail.getLength() == 1) {
						Element e = (Element) _homeEmail.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setEmail_home(t.getNodeValue());
						}
					}
					// 取"work"元素，下面类同
					NodeList _workEmail = emailElement
							.getElementsByTagName("work");
					if (_workEmail.getLength() == 1) {
						Element e = (Element) _workEmail.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setEmail_work(t.getNodeValue());
						}
					}
					// 取"other"元素，下面类同
					NodeList _otherEmail = emailElement
							.getElementsByTagName("other");
					if (_otherEmail.getLength() == 1) {
						Element e = (Element) _otherEmail.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setEmail_other(t.getNodeValue());
						}
					}
					// 取"custom"元素，下面类同
					NodeList _customEmail = emailElement
							.getElementsByTagName("custom");
					if (_customEmail.getLength() == 1) {
						Element e = (Element) _customEmail.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setEmail_custom(t.getNodeValue());
						}
					}
					// 取"customlabel"元素，下面类同
					NodeList _customlabelEmail = emailElement
							.getElementsByTagName("customlabel");
					if (_customlabelEmail.getLength() == 1) {
						Element e = (Element) _customlabelEmail.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setEmail_customlabel(t.getNodeValue());
						}
					}
				}
				/* 取"im"节点 */
				// NodeList _imList =
				// contactelement.getElementsByTagName("im");
				// int _imnum = _imList.getLength();
				// for (int indexIM = 0; indexIM < _imnum; indexIM++) {
				// // 依次取每个"IM"节点
				// Element imElement = (Element) _imList.item(indexIM);
				// // 取"aim"元素，下面类同
				// NodeList _aimIM = imElement.getElementsByTagName("aim");
				// if (_aimIM.getLength() == 1) {
				// Element e = (Element) _aimIM.item(0);
				// Text t = (Text) e.getFirstChild();
				// contactUnit.setIm_aim(t.getNodeValue());
				// }
				// // 取"windowlive"元素，下面类同
				// NodeList _windowliveIM = imElement
				// .getElementsByTagName("windowlive");
				// if (_windowliveIM.getLength() == 1) {
				// Element e = (Element) _windowliveIM.item(0);
				// Text t = (Text) e.getFirstChild();
				// contactUnit.setIm_windowslive(t.getNodeValue());
				// }
				// // 取"yahoo"元素，下面类同
				// NodeList _yahooIM =
				// imElement.getElementsByTagName("yahoo");
				// if (_yahooIM.getLength() == 1) {
				// Element e = (Element) _yahooIM.item(0);
				// Text t = (Text) e.getFirstChild();
				// contactUnit.setIm_yahoo(t.getNodeValue());
				// }
				// // 取"skype"元素，下面类同
				// NodeList _skypeIM =
				// imElement.getElementsByTagName("skype");
				// if (_skypeIM.getLength() == 1) {
				// Element e = (Element) _skypeIM.item(0);
				// Text t = (Text) e.getFirstChild();
				// contactUnit.setIm_skepe(t.getNodeValue());
				// }
				// // 取"qq"元素，下面类同
				// NodeList _qqIM = imElement.getElementsByTagName("qq");
				// if (_qqIM.getLength() == 1) {
				// Element e = (Element) _qqIM.item(0);
				// Text t = (Text) e.getFirstChild();
				// contactUnit.setIm_qq(t.getNodeValue());
				// }
				// // 取"googletalk"元素，下面类同
				// NodeList _googletalkIM = imElement
				// .getElementsByTagName("googletalk");
				// if (_googletalkIM.getLength() == 1) {
				// Element e = (Element) _googletalkIM.item(0);
				// Text t = (Text) e.getFirstChild();
				// contactUnit.setIm_googletalk(t.getNodeValue());
				// }
				// // 取"icq"元素，下面类同
				// NodeList _icqIM = imElement.getElementsByTagName("icq");
				// if (_icqIM.getLength() == 1) {
				// Element e = (Element) _icqIM.item(0);
				// Text t = (Text) e.getFirstChild();
				// contactUnit.setIm_icq(t.getNodeValue());
				// }
				// // 取"jabber"元素，下面类同
				// NodeList _jabberIM =
				// imElement.getElementsByTagName("jabber");
				// if (_jabberIM.getLength() == 1) {
				// Element e = (Element) _jabberIM.item(0);
				// Text t = (Text) e.getFirstChild();
				// contactUnit.setIm_jabber(t.getNodeValue());
				// }
				// }
				/* 取"postal"节点 */
				NodeList _postalList = contactelement
						.getElementsByTagName("postal");
				int _postalnum = _postalList.getLength();
				for (int indexPostal = 0; indexPostal < _postalnum; indexPostal++) {
					// 依次取每个"postal"节点
					Element postalElement = (Element) _postalList
							.item(indexPostal);
					// 取"home"元素，下面类同
					NodeList _homePostal = postalElement
							.getElementsByTagName("home");
					if (_homePostal.getLength() == 1) {
						Element e = (Element) _homePostal.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPostal_home(t.getNodeValue());
						}
					}
					// 取"work"元素，下面类同
					NodeList _workPostal = postalElement
							.getElementsByTagName("work");
					if (_workPostal.getLength() == 1) {
						Element e = (Element) _workPostal.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPostal_work(t.getNodeValue());
						}
					}
					// 取"other"元素，下面类同
					NodeList _otherPostal = postalElement
							.getElementsByTagName("other");
					if (_otherPostal.getLength() == 1) {
						Element e = (Element) _otherPostal.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPostal_other(t.getNodeValue());
						}
					}
					// 取"custom"元素，下面类同
					NodeList _customPostal = postalElement
							.getElementsByTagName("custom");
					if (_customPostal.getLength() == 1) {
						Element e = (Element) _customPostal.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPostal_custom(t.getNodeValue());
						}
					}
					// 取"customlabel"元素，下面类同
					NodeList _customlabelPostal = postalElement
							.getElementsByTagName("customlabel");
					if (_customlabelPostal.getLength() == 1) {
						Element e = (Element) _customlabelPostal.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPostal_customlabel(t.getNodeValue());
						}
					}
				}

				/* 取"organizations"节点 */
				NodeList _organizationsList = contactelement
						.getElementsByTagName("organizations");
				int _organizationsnum = _organizationsList.getLength();
				for (int indexOrganizations = 0; indexOrganizations < _organizationsnum; indexOrganizations++) {
					// 依次取每个"organizations"节点
					Element organizationsElement = (Element) _organizationsList
							.item(indexOrganizations);
					// 取"work"元素，下面类同
					NodeList _workOrganizationsList = organizationsElement
							.getElementsByTagName("work");
					int _workOrganizationsNum = _workOrganizationsList
							.getLength();
					for (int indexWorkOrganizations = 0; indexWorkOrganizations < _workOrganizationsNum; indexWorkOrganizations++) {
						// 依次取每个"organizations"节点中work子节点
						Element _workOrganizationsElement = (Element) _workOrganizationsList
								.item(indexWorkOrganizations);
						// 取"company"子元素，下面类同
						NodeList _companyWorkOrganizations = _workOrganizationsElement
								.getElementsByTagName("company");
						if (_companyWorkOrganizations.getLength() == 1) {
							Element e = (Element) _companyWorkOrganizations
									.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setOrganization_work_company(t
										.getNodeValue());
							}
						}
						// 取"title"子元素，下面类同
						NodeList _titleWorkOrganizations = _workOrganizationsElement
								.getElementsByTagName("title");
						if (_titleWorkOrganizations.getLength() == 1) {
							Element e = (Element) _titleWorkOrganizations
									.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setOrganization_work_title(t
										.getNodeValue());
							}
						}
					}

					// 取"other"元素，下面类同
					NodeList _otherOrganizationsList = organizationsElement
							.getElementsByTagName("other");
					int _otherOrganizationsNum = _otherOrganizationsList
							.getLength();
					for (int indexotherOrganizations = 0; indexotherOrganizations < _otherOrganizationsNum; indexotherOrganizations++) {
						// 依次取每个"organizations"节点中other子节点
						Element _otherOrganizationsElement = (Element) _otherOrganizationsList
								.item(indexotherOrganizations);
						// 取"company"子元素，下面类同
						NodeList _companyotherOrganizations = _otherOrganizationsElement
								.getElementsByTagName("company");
						if (_companyotherOrganizations.getLength() == 1) {
							Element e = (Element) _companyotherOrganizations
									.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setOrganization_other_company(t
										.getNodeValue());
							}
						}
						// 取"title"子元素，下面类同
						NodeList _titleotherOrganizations = _otherOrganizationsElement
								.getElementsByTagName("title");
						if (_titleotherOrganizations.getLength() == 1) {
							Element e = (Element) _titleotherOrganizations
									.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setOrganization_other_title(t
										.getNodeValue());
							}
						}
					}

					// 取"custom"元素，下面类同
					NodeList _customOrganizationsList = organizationsElement
							.getElementsByTagName("custom");
					int _customOrganizationsNum = _customOrganizationsList
							.getLength();
					for (int indexCustomOrganizations = 0; indexCustomOrganizations < _customOrganizationsNum; indexCustomOrganizations++) {
						// 依次取每个"organizations"节点中other子节点
						Element _customOrganizationsElement = (Element) _customOrganizationsList
								.item(indexCustomOrganizations);
						// 取"company"子元素，下面类同
						NodeList _companycustomOrganizations = _customOrganizationsElement
								.getElementsByTagName("company");
						if (_companycustomOrganizations.getLength() == 1) {
							Element e = (Element) _companycustomOrganizations
									.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setOrganization_custom_company(t
										.getNodeValue());
							}
						}
						// 取"title"子元素，下面类同
						NodeList _titlecustomOrganizations = _customOrganizationsElement
								.getElementsByTagName("title");
						if (_titlecustomOrganizations.getLength() == 1) {
							Element e = (Element) _titlecustomOrganizations
									.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setOrganization_custom_title(t
										.getNodeValue());
							}
						}
						// 取"customlabel"子元素，下面类同
						NodeList _customlabelOrganizations = _customOrganizationsElement
								.getElementsByTagName("customlabel");
						if (_customlabelOrganizations.getLength() == 1) {
							Element e = (Element) _customlabelOrganizations
									.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setOrganization_customlabel(t
										.getNodeValue());
							}
						}
					}
				}
				contactUnitList.add(contactUnit);
			} else {
				break;
			}
		}
		return contactUnitList;
	}

	/* 删除手机中所有contacts */
	public static void deleteContactsItemInPhone(Context context) {
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "deleteContactsItemInPhone() start..");
		/* 取得ContentResolver */
		ContentResolver contentResolver = context.getContentResolver();
		/* 取得通讯录的People表的cursor */
		// Cursor cursorPeopleContact = contentResolver.query(
		// Contacts.People.CONTENT_URI, null, null, null,
		// Contacts.People.DEFAULT_SORT_ORDER);
		//
		// int numPeople = cursorPeopleContact.getCount();// contacts中的人数
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "before deleteContactsItemInPhone() numPeople="
		// + numPeople);
		/* 在联系人列表中删除所有联系人的电话信息 */
		contentResolver.delete(Contacts.People.CONTENT_URI, null, null);

		// for (int i = 0; i < numPeople; i++) {
		// cursorPeopleContact.moveToPosition(i);
		// String _personid = cursorPeopleContact
		// .getString(cursorPeopleContact
		// .getColumnIndexOrThrow(Contacts.People._ID));
		//
		// /* ****此delete代码在1.5 1.6 乐phone1.6中运行正常 ，但在ophone2.0中异常**** */
		// // contentResolver.delete(Contacts.People.CONTENT_URI,
		// // Contacts.People._ID + "=?", new String[] { _personid });
		//
		// /* ****此delete代码在2.1 ophone2.0中运行正常 ，但在1.5 1.6 乐phone1.6中异常**** */
		// // contentResolver.delete(Contacts.People.CONTENT_URI,
		// // "Contacts.People._ID =" + _personid + "", null);
		//
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "delete _personid=" + _personid);
		// }

		// wu0wu********************
		// cursorPeopleContact = contentResolver.query(
		// Contacts.People.CONTENT_URI, null, null, null,
		// Contacts.People.DEFAULT_SORT_ORDER);
		// numPeople = cursorPeopleContact.getCount();// contacts中的人数
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "after deleteContactsItemInPhone() numPeople="
		// + numPeople);

		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "deleteContactsItemInPhone() end..");
	}

	/** 插入所有XML中的contacts,用于手机端程序 */
	public static void insertContactsItemForPhone(Context context,
			ArrayList<ContactUnit> _contactlistFromXML) {
		/* 取得ContentResolver */
		ContentResolver contentResolver = context.getContentResolver();
		/* 循环_contactlistFromXML插入内容 */
		int numContactlist = _contactlistFromXML.size();
		for (int indexContactUnit = 0; indexContactUnit < numContactlist; indexContactUnit++) {
			/* 获得一个ContactUnit对象 */
			ContactUnit _ContactUnit = _contactlistFromXML
					.get(indexContactUnit);

			ContentValues values = new ContentValues();
			Uri phoneUri = null;
			String _name = _ContactUnit.getName();
			/* 添加姓名 */
			values.put(Contacts.People.NAME, _name);
			Uri uriname = contentResolver.insert(Contacts.People.CONTENT_URI,
					values);
			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "uriname=" + uriname);
			String _personId = uriname.toString().substring(26);
			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "_personId=" + _personId);
			// 映射关系:1 = 新的联系方式加入 favorites,0 = 新的联系方式不是加入 favorites
			// values.put(Contacts.People.STARRED, 0);
			// Uri uri = Contacts.People.createPersonInMyContactsGroup(
			// contentResolver, values);
			// Log.v(MoblieAssistantService.TAG, Thread.currentThread()
			// .getName()
			// + "---->" + "insert NAME");
			/* 计算personId:从people表里查出此姓名最大的一个personId */
			/* 取得通讯录的People表的cursor */
			// Cursor cursorPeopleContact = contentResolver.query(
			// Contacts.People.CONTENT_URI, null, Contacts.People.NAME
			// + "=?", new String[] { _name },
			// Contacts.People.DEFAULT_SORT_ORDER);
			// int numname = cursorPeopleContact.getCount();
			// /* 从同名的列表中找到ID最大的 */
			// String _personId = null;
			// int maxId = 0;
			// for (int i = 0; i < numname; i++) {
			// cursorPeopleContact.moveToPosition(i);
			// String tmppersonId = cursorPeopleContact
			// .getString(cursorPeopleContact
			// .getColumnIndexOrThrow(Contacts.People._ID));
			// if (Integer.parseInt(tmppersonId) > maxId) {
			// _personId = tmppersonId;
			// maxId = Integer.parseInt(tmppersonId);
			// }
			// }
			// Log.v(TAG, Thread.currentThread().getName() + "---->"
			// + "insert _personId=" + _personId);
			/* 添加电话号码 */
			// 最好的办法是先得到People表的Uri，然后使用Uri的静态方法withAppendedPath来获取一个新的Uri作为我们新要插入数据的Uri
			phoneUri = Uri.withAppendedPath(uriname,
					Contacts.People.Phones.CONTENT_DIRECTORY);
			// Log.v(MoblieAssistantService.TAG, Thread.currentThread()
			// .getName()
			// + "---->" + "phoneUri=" + phoneUri);
			// phoneUri = Contacts.Phones.CONTENT_URI;
			String numberPhoneMobile = _ContactUnit.getPhone_moblie();
			String numberPhoneHome = _ContactUnit.getPhone_home();
			String numberPhoneWork = _ContactUnit.getPhone_work();
			String numberPhoneFax_work = _ContactUnit.getPhone_Fax_work();
			String numberPhoneFax_home = _ContactUnit.getPhone_Fax_home();
			String numberPhonePager = _ContactUnit.getPhone_pager();
			String numberPhoneOther = _ContactUnit.getPhone_other();
			String numberPhoneCustom = _ContactUnit.getPhone_custom();
			String numberPhoneCustomLabel = _ContactUnit.getPhone_customlabel();

			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "insert Phones");
			if (numberPhoneHome != null && !numberPhoneHome.trim().equals("")) {// home
				values.clear();
				// values.put(Contacts.Phones.PERSON_ID, _personId);
				values.put(Contacts.Phones.TYPE, Contacts.Phones.TYPE_HOME);
				values.put(Contacts.Phones.NUMBER, numberPhoneHome);
				contentResolver.insert(phoneUri, values);
			}
			if (numberPhoneMobile != null
					&& !numberPhoneMobile.trim().equals("")) {// mobile
				values.clear();
				// values.put(Contacts.Phones.PERSON_ID, _personId);
				values.put(Contacts.Phones.TYPE, Contacts.Phones.TYPE_MOBILE);
				values.put(Contacts.Phones.NUMBER, numberPhoneMobile);
				contentResolver.insert(phoneUri, values);
			}
			if (numberPhoneWork != null && !numberPhoneWork.trim().equals("")) {// work
				values.clear();
				// values.put(Contacts.Phones.PERSON_ID, _personId);
				values.put(Contacts.Phones.TYPE, Contacts.Phones.TYPE_WORK);
				values.put(Contacts.Phones.NUMBER, numberPhoneWork);
				contentResolver.insert(phoneUri, values);
			}
			if (numberPhoneFax_work != null
					&& !numberPhoneFax_work.trim().equals("")) {// Fax_work
				values.clear();
				// values.put(Contacts.Phones.PERSON_ID, _personId);
				values.put(Contacts.Phones.TYPE, Contacts.Phones.TYPE_FAX_WORK);
				values.put(Contacts.Phones.NUMBER, numberPhoneFax_work);
				contentResolver.insert(phoneUri, values);
			}
			if (numberPhoneFax_home != null
					&& !numberPhoneFax_home.trim().equals("")) {// Fax_home
				values.clear();
				// values.put(Contacts.Phones.PERSON_ID, _personId);
				values.put(Contacts.Phones.TYPE, Contacts.Phones.TYPE_FAX_HOME);
				values.put(Contacts.Phones.NUMBER, numberPhoneFax_home);
				contentResolver.insert(phoneUri, values);
			}
			if (numberPhonePager != null && !numberPhonePager.trim().equals("")) {// Pager
				values.clear();
				// values.put(Contacts.Phones.PERSON_ID, _personId);
				values.put(Contacts.Phones.TYPE, Contacts.Phones.TYPE_PAGER);
				values.put(Contacts.Phones.NUMBER, numberPhonePager);
				contentResolver.insert(phoneUri, values);
			}
			if (numberPhoneOther != null && !numberPhoneOther.trim().equals("")) {// Other
				values.clear();
				// values.put(Contacts.Phones.PERSON_ID, _personId);
				values.put(Contacts.Phones.TYPE, Contacts.Phones.TYPE_OTHER);
				values.put(Contacts.Phones.NUMBER, numberPhoneOther);
				contentResolver.insert(phoneUri, values);
			}
			if (numberPhoneCustom != null
					&& !numberPhoneCustom.trim().equals("")) {// Custom
				values.clear();
				// values.put(Contacts.Phones.PERSON_ID, _personId);
				values.put(Contacts.Phones.TYPE, Contacts.Phones.TYPE_CUSTOM);
				values.put(Contacts.Phones.LABEL, numberPhoneCustomLabel);
				values.put(Contacts.Phones.NUMBER, numberPhoneCustom);
				contentResolver.insert(phoneUri, values);
			}

			/* 添加Email */
			Uri emailUri = Uri.withAppendedPath(uriname,
					Contacts.People.ContactMethods.CONTENT_DIRECTORY);
			// Uri emailUri = Contacts.ContactMethods.CONTENT_URI;
			String homeEmail = _ContactUnit.getEmail_home();
			String workEmail = _ContactUnit.getEmail_work();
			String otherEmail = _ContactUnit.getEmail_other();
			String customEmail = _ContactUnit.getEmail_custom();
			String customEmailLabel = _ContactUnit.getEmail_customlabel();

			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "insert email");
			if (homeEmail != null && !homeEmail.trim().equals("")) {// email
				// home
				values.clear();
				// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
				// ContactMethods.KIND 是用来区分像email,im等等不同联系方式
				values.put(Contacts.ContactMethods.KIND, Contacts.KIND_EMAIL);
				values.put(Contacts.ContactMethods.DATA, homeEmail);
				values.put(Contacts.ContactMethods.TYPE,
						Contacts.ContactMethods.TYPE_HOME);
				contentResolver.insert(emailUri, values);
			}
			if (workEmail != null && !workEmail.trim().equals("")) {// email
				// work
				values.clear();
				// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
				// ContactMethods.KIND 是用来区分像email,im等等不同联系方式
				values.put(Contacts.ContactMethods.KIND, Contacts.KIND_EMAIL);
				values.put(Contacts.ContactMethods.DATA, workEmail);
				values.put(Contacts.ContactMethods.TYPE,
						Contacts.ContactMethods.TYPE_WORK);
				contentResolver.insert(emailUri, values);
			}
			if (otherEmail != null && !otherEmail.trim().equals("")) {// email
				// other
				values.clear();
				// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
				// ContactMethods.KIND 是用来区分像email,im等等不同联系方式
				values.put(Contacts.ContactMethods.KIND, Contacts.KIND_EMAIL);
				values.put(Contacts.ContactMethods.DATA, otherEmail);
				values.put(Contacts.ContactMethods.TYPE,
						Contacts.ContactMethods.TYPE_OTHER);
				contentResolver.insert(emailUri, values);
			}
			if (customEmail != null && !customEmail.trim().equals("")) {// email
				// custom
				values.clear();
				// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
				// ContactMethods.KIND 是用来区分像email,im等等不同联系方式
				values.put(Contacts.ContactMethods.KIND, Contacts.KIND_EMAIL);
				values.put(Contacts.ContactMethods.DATA, customEmail);
				values.put(Contacts.ContactMethods.LABEL, customEmailLabel);
				values.put(Contacts.ContactMethods.TYPE,
						Contacts.ContactMethods.TYPE_CUSTOM);
				contentResolver.insert(emailUri, values);
			}

			/* 添加IM */
			// Uri imUri = Uri.withAppendedPath(uri,
			// Contacts.People.ContactMethods.CONTENT_DIRECTORY);
			// String aimIM = _ContactUnit.getIm_aim();
			// String windowliveIM = _ContactUnit.getIm_windowslive();
			// String yahooIM = _ContactUnit.getIm_yahoo();
			// String skypeIM = _ContactUnit.getIm_skepe();
			// String qqIM = _ContactUnit.getIm_qq();
			// String googletalkIM = _ContactUnit.getIm_googletalk();
			// String icqIM = _ContactUnit.getIm_icq();
			// String jabberIM = _ContactUnit.getIm_jabber();
			//
			// if (aimIM != null && !aimIM.trim().equals("")) {// im aim
			// values.clear();
			// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
			// values.put(Contacts.ContactMethods.DATA, aimIM);
			// values.put(Contacts.ContactMethods.TYPE,
			// Contacts.ContactMethods.PROTOCOL_AIM);
			// values.put(Contacts.ContactMethods.AUX_DATA, "pre:0");
			// contentResolver.insert(imUri, values);
			// }
			// if (windowliveIM != null && !windowliveIM.trim().equals(""))
			// {//
			// im
			// // msn
			// values.clear();
			// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
			// values.put(Contacts.ContactMethods.DATA, windowliveIM);
			// values.put(Contacts.ContactMethods.TYPE,
			// Contacts.ContactMethods.PROTOCOL_MSN);
			// values.put(Contacts.ContactMethods.AUX_DATA, "pre:1");
			// contentResolver.insert(imUri, values);
			// }
			// if (yahooIM != null && !yahooIM.trim().equals("")) {// im
			// yahoo
			// values.clear();
			// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
			// values.put(Contacts.ContactMethods.DATA, yahooIM);
			// values.put(Contacts.ContactMethods.TYPE,
			// Contacts.ContactMethods.PROTOCOL_YAHOO);
			// values.put(Contacts.ContactMethods.AUX_DATA, "pre:2");
			// contentResolver.insert(imUri, values);
			// }
			// if (skypeIM != null && !skypeIM.trim().equals("")) {// im
			// skype
			// values.clear();
			// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
			// values.put(Contacts.ContactMethods.DATA, skypeIM);
			// values.put(Contacts.ContactMethods.TYPE,
			// Contacts.ContactMethods.PROTOCOL_SKYPE);
			// values.put(Contacts.ContactMethods.AUX_DATA, "pre:3");
			// contentResolver.insert(imUri, values);
			// }
			// if (qqIM != null && !qqIM.trim().equals("")) {// im qq
			// values.clear();
			// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
			// values.put(Contacts.ContactMethods.DATA, qqIM);
			// values.put(Contacts.ContactMethods.TYPE,
			// Contacts.ContactMethods.PROTOCOL_QQ);
			// values.put(Contacts.ContactMethods.AUX_DATA, "pre:4");
			// contentResolver.insert(imUri, values);
			// }
			// if (googletalkIM != null && !googletalkIM.trim().equals(""))
			// {//
			// im
			// // googletalk
			// values.clear();
			// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
			// values.put(Contacts.ContactMethods.DATA, googletalkIM);
			// values.put(Contacts.ContactMethods.TYPE,
			// Contacts.ContactMethods.PROTOCOL_GOOGLE_TALK);
			// values.put(Contacts.ContactMethods.AUX_DATA, "pre:5");
			// contentResolver.insert(imUri, values);
			// }
			// if (icqIM != null && !icqIM.trim().equals("")) {// im icq
			// values.clear();
			// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
			// values.put(Contacts.ContactMethods.DATA, icqIM);
			// values.put(Contacts.ContactMethods.TYPE,
			// Contacts.ContactMethods.PROTOCOL_ICQ);
			// values.put(Contacts.ContactMethods.AUX_DATA, "pre:6");
			// contentResolver.insert(imUri, values);
			// }
			// if (jabberIM != null && !jabberIM.trim().equals("")) {// im
			// jabber
			// values.clear();
			// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
			// values.put(Contacts.ContactMethods.DATA, jabberIM);
			// values.put(Contacts.ContactMethods.TYPE,
			// Contacts.ContactMethods.PROTOCOL_JABBER);
			// values.put(Contacts.ContactMethods.AUX_DATA, "pre:7");
			// contentResolver.insert(imUri, values);
			// }

			/* 添加postal地址 */
			Uri PostalUri = Uri.withAppendedPath(uriname,
					Contacts.People.ContactMethods.CONTENT_DIRECTORY);

			// Uri PostalUri = Contacts.ContactMethods.CONTENT_URI;
			String homePostal = _ContactUnit.getPostal_home();
			String workPostal = _ContactUnit.getPostal_work();
			String otherPostal = _ContactUnit.getPostal_other();
			String customPostal = _ContactUnit.getPostal_custom();
			String customlabelPostal = _ContactUnit.getPostal_customlabel();

			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "insert postal");
			if (homePostal != null && !homePostal.trim().equals("")) {// postal
				// home
				values.clear();
				// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
				values.put(Contacts.ContactMethods.KIND, Contacts.KIND_POSTAL);
				values.put(Contacts.ContactMethods.DATA, homePostal);
				values.put(Contacts.ContactMethods.TYPE,
						Contacts.ContactMethods.TYPE_HOME);
				contentResolver.insert(PostalUri, values);
			}
			if (workPostal != null && !workPostal.trim().equals("")) {// postal
				// work
				values.clear();
				// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
				values.put(Contacts.ContactMethods.KIND, Contacts.KIND_POSTAL);
				values.put(Contacts.ContactMethods.DATA, workPostal);
				values.put(Contacts.ContactMethods.TYPE,
						Contacts.ContactMethods.TYPE_WORK);
				contentResolver.insert(PostalUri, values);
			}
			if (otherPostal != null && !otherPostal.trim().equals("")) {// postal
				// other
				values.clear();
				// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
				values.put(Contacts.ContactMethods.KIND, Contacts.KIND_POSTAL);
				values.put(Contacts.ContactMethods.DATA, otherPostal);
				values.put(Contacts.ContactMethods.TYPE,
						Contacts.ContactMethods.TYPE_OTHER);
				contentResolver.insert(PostalUri, values);
			}
			if (customPostal != null && !customPostal.trim().equals("")) {// postal
				// custom
				values.clear();
				// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
				values.put(Contacts.ContactMethods.KIND, Contacts.KIND_POSTAL);
				values.put(Contacts.ContactMethods.DATA, customPostal);
				values.put(Contacts.ContactMethods.TYPE,
						Contacts.ContactMethods.TYPE_CUSTOM);
				values.put(Contacts.ContactMethods.LABEL, customlabelPostal);
				contentResolver.insert(PostalUri, values);
			}

			/* 添加organizations地址 */
			// Uri OrganizationsUri = Uri.withAppendedPath(uriname,
			// Contacts.Organizations.CONTENT_DIRECTORY);

			Uri OrganizationsUri = Contacts.Organizations.CONTENT_URI;
			String companyWorkOrganizations = _ContactUnit
					.getOrganization_work_company();
			String titleWorkOrganizations = _ContactUnit
					.getOrganization_work_title();
			String companyOtherOrganizations = _ContactUnit
					.getOrganization_other_company();
			String titleOtherOrganizations = _ContactUnit
					.getOrganization_other_title();
			String companyCustomOrganizations = _ContactUnit
					.getOrganization_custom_company();
			String titleCustomOrganizations = _ContactUnit
					.getOrganization_custom_title();
			String customlabelCustomOrganizations = _ContactUnit
					.getOrganization_customlabel();

			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "insert organizations");
			int numNull = 0;// 公司和职位不能都为空。为空++
			if (companyWorkOrganizations != null
					&& !companyWorkOrganizations.trim().equals("")) {// Organizations
				values.clear();
				// work
				values.put(Contacts.Organizations.PERSON_ID, _personId);
				values.put(Contacts.Organizations.COMPANY,
						companyWorkOrganizations);
				values.put(Contacts.Organizations.TYPE,
						Contacts.Organizations.TYPE_WORK);
			} else {
				values.clear();
				values.put(Contacts.Organizations.PERSON_ID, _personId);
				values.put(Contacts.Organizations.COMPANY, " ");
				values.put(Contacts.Organizations.TYPE,
						Contacts.Organizations.TYPE_WORK);
				numNull++;
			}
			if (titleWorkOrganizations != null
					&& !titleWorkOrganizations.trim().equals("")) {// Organizations
				// work
				values
						.put(Contacts.Organizations.TITLE,
								titleWorkOrganizations);
				contentResolver.insert(OrganizationsUri, values);
			} else {// 在有些手机中"职位"为空的话，不显示公司属性
				numNull++;
				if (numNull != 2) {
					values.put(Contacts.Organizations.TITLE, " ");
					contentResolver.insert(OrganizationsUri, values);
				}
			}
			numNull = 0;
			if (companyOtherOrganizations != null
					&& !companyOtherOrganizations.trim().equals("")) {// Organizations
				// Other
				values.clear();
				values.put(Contacts.Organizations.PERSON_ID, _personId);
				values.put(Contacts.Organizations.COMPANY,
						companyOtherOrganizations);
				values.put(Contacts.Organizations.TYPE,
						Contacts.Organizations.TYPE_OTHER);
			} else {
				values.clear();
				values.put(Contacts.Organizations.PERSON_ID, _personId);
				values.put(Contacts.Organizations.COMPANY,
						companyOtherOrganizations);
				values.put(Contacts.Organizations.TYPE,
						Contacts.Organizations.TYPE_OTHER);
				numNull++;
			}
			if (titleOtherOrganizations != null
					&& !titleOtherOrganizations.trim().equals("")) {// Organizations
				// Other
				values.put(Contacts.Organizations.TITLE,
						titleOtherOrganizations);
				contentResolver.insert(OrganizationsUri, values);
			} else {
				numNull++;
				if (numNull != 2) {
					values.put(Contacts.Organizations.TITLE, " ");
					contentResolver.insert(OrganizationsUri, values);
				}
			}
			numNull = 0;
			if (companyCustomOrganizations != null
					&& !companyCustomOrganizations.trim().equals("")) {// Organizations
				// Custom
				values.clear();
				values.put(Contacts.Organizations.PERSON_ID, _personId);
				values.put(Contacts.Organizations.COMPANY,
						companyCustomOrganizations);
				values.put(Contacts.Organizations.LABEL,
						customlabelCustomOrganizations);
				values.put(Contacts.Organizations.TYPE,
						Contacts.Organizations.TYPE_CUSTOM);
			} else {
				values.clear();
				values.put(Contacts.Organizations.PERSON_ID, _personId);
				values.put(Contacts.Organizations.COMPANY, " ");
				values.put(Contacts.Organizations.LABEL,
						customlabelCustomOrganizations);
				values.put(Contacts.Organizations.TYPE,
						Contacts.Organizations.TYPE_CUSTOM);
				numNull++;
			}

			if (titleCustomOrganizations != null
					&& !titleCustomOrganizations.trim().equals("")) {// Organizations
				// Custom
				values.put(Contacts.Organizations.TITLE,
						titleCustomOrganizations);
				contentResolver.insert(OrganizationsUri, values);
			} else {
				numNull++;
				if (numNull != 2) {
					values.put(Contacts.Organizations.TITLE, " ");
					contentResolver.insert(OrganizationsUri, values);
				}
			}
		}
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "insertContactsItemInPhoneSDK() end..");

		/* 取得通讯录的People表的cursor */
		// Cursor cursorPeopleContact = contentResolver.query(
		// Contacts.People.CONTENT_URI, null, null, null,
		// Contacts.People.DEFAULT_SORT_ORDER);
		//
		// int numinsertPeople = cursorPeopleContact.getCount();// contacts中的人数
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "after insert numPeople=" + numinsertPeople);
	}

	/* 手机端程序:从XML文件读取SMS数据 */
	public static ArrayList<SMSUnit> readSMSXMLFileForPhone(String path,
			String filename) {
		File inFile = new File(path, filename);
		if (!inFile.exists()) {
			/* 文件不存在，就不进行更新操作 */
			return null;
		}
		try {
			inFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
		}
		Document doc = null;
		try {
			doc = db.parse(inFile);
		} catch (DOMException dom) {
			return null;
		} catch (IOException ioe) {
			return null;
		} catch (SAXException e) {
			return null;
		}
		ArrayList<SMSUnit> smsUnitList = new ArrayList<SMSUnit>();
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "******readSMSXMLFile 1******");
		// 下面是解析XML的全过程，比较简单，先取根元素"smslist"
		Element root = doc.getDocumentElement();
		// 取"SMS"元素列表
		NodeList smsList = root.getElementsByTagName("sms");

		int _smsnumInXML = smsList.getLength();
		for (int i = 0; i < _smsnumInXML; i++) {
			// 依次取每个"sms"元素
			Element smselement = (Element) smsList.item(i);
			// 创建一个SMSUnit的实例
			SMSUnit smsunit = new SMSUnit();
			// 取"_id"元素，下面类同
			NodeList _idList = smselement.getElementsByTagName("_id");
			if (_idList.getLength() == 1) {
				Element e = (Element) _idList.item(0);
				Text t = (Text) e.getFirstChild();
				smsunit.set_id(t.getNodeValue());
				// Log.v(Service139.TAG, Thread.currentThread().getName()
				// + "---->" + "******readSMSXMLFile smsunit id ="
				// + t.getNodeValue());
			}

			// NodeList thread_idList = smselement
			// .getElementsByTagName("thread_id");
			// if (thread_idList.getLength() == 1) {
			// Element e = (Element) thread_idList.item(0);
			// Text t = (Text) e.getFirstChild();
			// smsunit.setThread_id(t.getNodeValue());
			// }

			NodeList addressList = smselement.getElementsByTagName("address");
			if (addressList.getLength() == 1) {
				Element e = (Element) addressList.item(0);
				Text t = (Text) e.getFirstChild();
				smsunit.setAddress(t.getNodeValue());
				// Log.v(Service139.TAG, Thread.currentThread().getName()
				// + "---->" + "******readSMSXMLFile smsunit Address ="
				// + t.getNodeValue());
			}

			NodeList dateList = smselement.getElementsByTagName("date");
			if (dateList.getLength() == 1) {
				Element e = (Element) dateList.item(0);
				Text t = (Text) e.getFirstChild();
				smsunit.setDate(t.getNodeValue());
				// Log.v(Service139.TAG, Thread.currentThread().getName()
				// + "---->" + "******readSMSXMLFile smsunit date ="
				// + t.getNodeValue());
			}

			// NodeList readList = smselement.getElementsByTagName("read");
			// if (readList.getLength() == 1) {
			// Element e = (Element) readList.item(0);
			// Text t = (Text) e.getFirstChild();
			// smsunit.setRead(t.getNodeValue());
			// }
			//
			// NodeList statusList = smselement.getElementsByTagName("status");
			// if (statusList.getLength() == 1) {
			// Element e = (Element) statusList.item(0);
			// Text t = (Text) e.getFirstChild();
			// smsunit.setStatus(t.getNodeValue());
			// }

			NodeList typeList = smselement.getElementsByTagName("type");
			if (typeList.getLength() == 1) {
				Element e = (Element) typeList.item(0);
				Text t = (Text) e.getFirstChild();
				smsunit.setType(t.getNodeValue());
				// Log.v(Service139.TAG, Thread.currentThread().getName()
				// + "---->" + "******readSMSXMLFile smsunit type ="
				// + t.getNodeValue());
			}

			NodeList bodyList = smselement.getElementsByTagName("body");
			if (bodyList.getLength() == 1) {
				Element e = (Element) bodyList.item(0);
				Text t = (Text) e.getFirstChild();
				smsunit.setBody(t.getNodeValue());
				// Log.v(Service139.TAG, Thread.currentThread().getName()
				// + "---->" + "******readSMSXMLFile smsunit body ="
				// + t.getNodeValue());
			}

			smsUnitList.add(smsunit);
			// Log.v(Service139.TAG, Thread.currentThread().getName() + "---->"
			// + "******readSMSXMLFile smsUnitList.add(smsunit) ");
		}
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "readSMSXMLFile smsUnitList.size="
		// + smsUnitList.size());
		return smsUnitList;
	}

	/* 更新SMS */
	// private void updateSMS(ArrayList<SMSUnit> smslistFromXML,
	// ArrayList<SMSUnit> smslistFromPhone) {
	// // Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
	// // + "---->" + "******updateSMS start******");
	// ArrayList<String> _idsInXML = new ArrayList<String>();
	// ArrayList<String> _idsInPhone = new ArrayList<String>();
	//
	// /* 两个for循环先把_id提出到ArrayList里 */
	// for (int i = 0; i < smslistFromXML.size(); i++) {
	// String _idInXml = smslistFromXML.get(i).get_id();
	// _idsInXML.add(_idInXml);
	// }
	// // Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
	// // + "---->" + "******updateSMS 1******");
	// for (int j = 0; j < smslistFromPhone.size(); j++) {
	// String _idInPhone = smslistFromPhone.get(j).get_id();
	// _idsInPhone.add(_idInPhone);
	// }
	// // Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
	// // + "---->" + "******updateSMS 2******");
	// /* 如果XML文件里的ID在手机里不存在，作插入操作 */
	// for (int i = 0; i < smslistFromXML.size(); i++) {
	// String idxml = _idsInXML.get(i);
	// if (!_idsInPhone.contains(idxml)) {// 不包含
	// insertSMSItemInPhone(smslistFromXML.get(i));// 插入
	// } else {// 包含
	// /* 对原有SMS内容修改：存在则更新替换body */
	// updateSMSItemInPhone(smslistFromXML.get(i));
	// }
	// }
	// // Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
	// // + "---->" + "******updateSMS 3******");
	// /* 如果手机SMS中的ID在XML文件中不存在，作删除操作 */
	// for (int i = 0; i < smslistFromPhone.size(); i++) {
	// String idPhone = _idsInPhone.get(i);
	// if (!_idsInXML.contains(idPhone)) {// 不包含
	// deleteSMSItemInPhone(smslistFromPhone.get(i));// 删除
	// }
	// }
	// // Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
	// // + "---->" + "******updateSMS end******");
	// }

	/* 更新SMS：对原有SMS内容修改，存在则更新替换body */
	// private void updateSMSItemInPhone(SMSUnit smsUnit) {
	// // Log.v(Service139.TAG, "updateSMSItemInPhone");
	// final String BODY = "body";
	// ContentResolver contentResolver = context.getContentResolver();
	//
	// ContentValues values = new ContentValues();
	// String replaceId = smsUnit.get_id();
	// String replaceBody = smsUnit.getBody();
	// values.put(BODY, replaceBody);
	// contentResolver.update(Uri.parse("content://sms"), values, "_id= "
	// + replaceId, null);
	// // Log.v(Service139.TAG, "updateSMSItemInPhone");
	// }

	/* 删除SMS */
	public static void deleteSMSItemInPhone(Context context, SMSUnit smsUnit) {

		String _deleteid = smsUnit.get_id();
		/* 通过主键id来找到他对应的thread_id,然后删除 */
		String uriInbox = "content://sms";// uri
		Uri uriSms = Uri.parse(uriInbox);
		String[] projection = new String[] { "thread_id" };
		String where = "_id= " + _deleteid;

		Cursor querymessage = context.getContentResolver().query(uriSms,
				projection, where, null, null);

		String _thread_id = "";
		int column = 0;
		if (querymessage.getCount() != 0) {
			querymessage.moveToLast();
			column = querymessage.getColumnIndex("thread_id");
			_thread_id = querymessage.getString(column);
		}
		// String _deleteid = "6";
		// String thread_id = smsUnit.getThread_id();
		// String thread_id = "3";
		context.getContentResolver().delete(
				Uri.parse("content://sms/conversations/" + _thread_id),
				"_id= " + _deleteid, null);
	}

	public static void insertSmsItemInPhone(Context context,
			ArrayList<SMSUnit> SMSUnits) {
		for (int i = 0; i < SMSUnits.size(); i++) {
			insertSMSItemInPhone(context, SMSUnits.get(i));
		}
	}

	/* 插入SMS */
	public static void insertSMSItemInPhone(Context context, SMSUnit smsUnit) {
		final String ADDRESS = "address";
		final String THREAD_ID = "thread_id";
		final String DATE = "date";
		final String READ = "read";
		final String STATUS = "status";
		final String TYPE = "type";
		final String BODY = "body";
		final String ID = "_id";
		// int MESSAGE_TYPE_INBOX = 1;
		// int MESSAGE_TYPE_SENT = 2;

		ContentValues values = new ContentValues();
		/* 手机号 */
		values.put(ADDRESS, smsUnit.getAddress());
		/* 时间 */
		values.put(DATE, smsUnit.getDate());
		values.put(READ, "1");// 直接标记为已读
		values.put(STATUS, "-1");
		// values.put(ID, smsUnit.get_id());
		// values.put(THREAD_ID, smsUnit.getThread_id());
		/* 类型1为收件箱，2为发件箱 */
		values.put(TYPE, smsUnit.getType());
		/* 短信体内容 */
		values.put(BODY, smsUnit.getBody());
		/* 插入数据库操作 */
		// Log.v(MoblieAssistantService.TAG, "******insert*******");
		Uri inserted = context.getContentResolver().insert(
				Uri.parse("content://sms"), values);
		// Log
		// .v(MoblieAssistantService.TAG, "******insert******* uri="
		// + inserted);
	}

	/** 备份电话本 */
	public static void backupContacts(Context context) {
		// 从手机里读出电话本,生成xml,写到SD卡里
		String contactsInfo = GetContactsInfoList(context);
		File file = FileHelper.newFile(ConstantsList.PATH_BACKUP_SDCARD_DIR,
				ConstantsList.FILENAME_BACKUP_CONTACTS_SDCARD);
		try {
			FileHelper.writeFile(file, contactsInfo.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void backupSMSInbox(Context context) {
		String smsInbox = GetSMSInboxInfoListForPhone(context);
		File file = FileHelper.newFile(ConstantsList.PATH_BACKUP_SDCARD_DIR,
				ConstantsList.FILENAME_BACKUP_SMSINBOX_SDCARD);
		try {
			FileHelper.writeFile(file, smsInbox.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void backupSMSSentbox(Context context) {
		String smsSentbox = GetSMSSentboxInfoListForPhone(context);
		File file = FileHelper.newFile(ConstantsList.PATH_BACKUP_SDCARD_DIR,
				ConstantsList.FILENAME_BACKUP_SMSSENTBOX_SDCARD);
		try {
			FileHelper.writeFile(file, smsSentbox.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
