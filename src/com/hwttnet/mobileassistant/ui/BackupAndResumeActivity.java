package com.hwttnet.mobileassistant.ui;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hwttnet.mobileassistant.R;
import com.hwttnet.mobileassistant.util.CMDExecute;
import com.hwttnet.mobileassistant.util.ConstantsList;
import com.hwttnet.mobileassistant.util.ContactUnit;
import com.hwttnet.mobileassistant.util.MyUtil;
import com.hwttnet.mobileassistant.util.SMSUnit;

/**
 * 六.备份与恢复
 * 
 * 1.短信
 * 
 * 2.电话本
 * 
 * */
public class BackupAndResumeActivity extends Activity implements
		OnClickListener {
	private final static String TAG = "BackupAndResumeActivity";
	private final static int INT_BACKUP_CONTACTS = 0;
	private final static int INT_BACKUP_SMS = 1;
	private final static int INT_RESUME_CONTACTS = 2;
	private final static int INT_RESUME_SMS = 3;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case INT_BACKUP_CONTACTS:// 备份电话本成功,修改备份时间
				String currTime = MyUtil.getCurrTime();
				edit = sp.edit();
				edit.putString(LAST_BACKUP_CONTACTS_TIMES, currTime);
				edit.commit();
				mTextView_lastBackupContactsTime.setText(currTime);
				if (!mButton_ResumeContacts.isEnabled()) {
					mButton_ResumeContacts.setEnabled(true);
				}
				progressDialog.dismiss();
				break;
			case INT_RESUME_CONTACTS:// 恢复电话本成功,修改恢复时间
				String currTime1 = MyUtil.getCurrTime();
				edit = sp.edit();
				edit.putString(LAST_RESUME_CONTACTS_TIMES, currTime1);
				edit.commit();
				mTextView_lastResumeContactsTime.setText(currTime1);
				progressDialog.dismiss();
				break;
			case INT_BACKUP_SMS:// 备份SMS成功,修改备份时间
				String currTime2 = MyUtil.getCurrTime();
				edit = sp.edit();
				edit.putString(LAST_BACKUP_SMS_TIMES, currTime2);
				edit.commit();
				mTextView_lastBackupSMSTime.setText(currTime2);
				if (!mButton_ResumeSMS.isEnabled()) {
					mButton_ResumeSMS.setEnabled(true);
				}
				progressDialog.dismiss();
				break;
			case INT_RESUME_SMS:// 恢复SMS成功,修功恢复时间
				String currTime3 = MyUtil.getCurrTime();
				edit = sp.edit();
				edit.putString(LAST_RESUME_SMS_TIMES, currTime3);
				edit.commit();
				mTextView_lastResumeSMSTime.setText(currTime3);
				progressDialog.dismiss();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};
	private TextView mTextView_lastBackupContactsTime;
	private TextView mTextView_lastBackupSMSTime;
	private TextView mTextView_lastResumeContactsTime;
	private TextView mTextView_lastResumeSMSTime;
	private SharedPreferences sp;
	private Editor edit;
	private Button mButton_backupContacts;
	private Button mButton_backupSMS;
	private Button mButton_ResumeContacts;
	private Button mButton_ResumeSMS;
	private ProgressDialog progressDialog;
	public final static String BACKUP_AND_RESUME_TIMES_NAME = "backupAndResumeTimes";
	public final static String LAST_BACKUP_CONTACTS_TIMES = "lastBackupContactsTime";
	public final static String LAST_BACKUP_SMS_TIMES = "lastBackupSMSTime";
	public final static String LAST_RESUME_CONTACTS_TIMES = "lastResumeContactsTime";
	public final static String LAST_RESUME_SMS_TIMES = "lastResumeSMSTime";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题
		setContentView(R.layout.backupandresume_layout);
		iniView();
		iniDate();
		super.onCreate(savedInstanceState);
	}

	private void iniView() {
		mTextView_lastBackupContactsTime = (TextView) findViewById(R.id.mTextView_lastBackupContactsTime);
		mTextView_lastBackupSMSTime = (TextView) findViewById(R.id.mTextView_lastBackupSMSTime);
		mTextView_lastResumeContactsTime = (TextView) findViewById(R.id.mTextView_lastResumeContactsTime);
		mTextView_lastResumeSMSTime = (TextView) findViewById(R.id.mTextView_lastResumeSMSTime);
		mButton_backupContacts = (Button) findViewById(R.id.mButton_backupContacts);
		mButton_backupSMS = (Button) findViewById(R.id.mButton_backupSMS);
		mButton_ResumeContacts = (Button) findViewById(R.id.mButton_ResumeContacts);
		mButton_ResumeSMS = (Button) findViewById(R.id.mButton_ResumeSMS);

		mButton_backupContacts.setOnClickListener(this);
		mButton_backupSMS.setOnClickListener(this);
		mButton_ResumeContacts.setOnClickListener(this);
		mButton_ResumeSMS.setOnClickListener(this);
	}

	/** 读取上次备份恢复时间 */
	private void iniDate() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("备份恢复");
		progressDialog.setMessage("处理中,请稍后...");
		progressDialog.setCancelable(false);
		//
		String textUnknow = getResources().getString(
				R.string.textUnknow_BackupAndResume);
		//
		sp = getSharedPreferences(BACKUP_AND_RESUME_TIMES_NAME, 0);

		String lastBackupContactsTimes = sp.getString(
				LAST_BACKUP_CONTACTS_TIMES, null);
		String lastBackupSMSTime = sp.getString(LAST_BACKUP_SMS_TIMES, null);
		String lastResumeContactsTime = sp.getString(
				LAST_RESUME_CONTACTS_TIMES, null);
		String lastResumeSMSTime = sp.getString(LAST_RESUME_SMS_TIMES, null);

		Log.v(TAG, "lastBackupContactsTimes=" + lastBackupContactsTimes);
		Log.v(TAG, "lastBackupSMSTime=" + lastBackupSMSTime);
		Log.v(TAG, "lastResumeContactsTime=" + lastResumeContactsTime);
		Log.v(TAG, "lastResumeSMSTime=" + lastResumeSMSTime);

		if (lastBackupContactsTimes == null) {// 从没备份过通讯录
			mTextView_lastBackupContactsTime.setText(textUnknow);
			// 恢复按钮不可用
			mButton_ResumeContacts.setEnabled(false);
		} else {
			mTextView_lastBackupContactsTime.setText(lastBackupContactsTimes);
		}

		if (lastBackupSMSTime == null) {// 从没备份过短信
			mTextView_lastBackupSMSTime.setText(textUnknow);
			// 恢复按钮不可用
			mButton_ResumeSMS.setEnabled(false);
		} else {
			mTextView_lastBackupSMSTime.setText(lastBackupSMSTime);
		}

		if (lastResumeContactsTime == null) {// 从没恢复过通讯录
			mTextView_lastResumeContactsTime.setText(textUnknow);
		} else {
			mTextView_lastResumeContactsTime.setText(lastResumeContactsTime);
		}

		if (lastResumeSMSTime == null) {// 从没恢复过短信
			mTextView_lastResumeSMSTime.setText(textUnknow);
		} else {
			mTextView_lastResumeSMSTime.setText(lastResumeSMSTime);
		}
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
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mButton_backupContacts:// 备份通讯录
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				onClickButtonBackupContacts(this);
			} else {
				// 弹出对话框,无sd卡或sd卡写保护
				new AlertDialog.Builder(this).setTitle("注意").setMessage(
						"SDCard 不存在或写保护!").setPositiveButton("确定", null).show();
			}
			break;
		case R.id.mButton_backupSMS:// 备份短信
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				onClickButtonbackupSMS(this);
			} else {
				// 弹出对话框,无sd卡或sd卡写保护
				new AlertDialog.Builder(this).setTitle("注意").setMessage(
						"SDCard 不存在或写保护!").setPositiveButton("确定", null).show();
			}
			break;
		case R.id.mButton_ResumeContacts:// 恢复通讯录
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				onClickButtonResumeContacts(this);
			} else {
				// 弹出对话框,无sd卡或sd卡写保护
				new AlertDialog.Builder(this).setTitle("注意").setMessage(
						"SDCard 不存在或写保护!").setPositiveButton("确定", null).show();
			}
			break;
		case R.id.mButton_ResumeSMS:// 恢复短信
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				onClickButtonResumeSMS(this);
			} else {
				// 弹出对话框,无sd卡或sd卡写保护
				new AlertDialog.Builder(this).setTitle("注意").setMessage(
						"SDCard 不存在或写保护!").setPositiveButton("确定", null).show();
			}
			break;
		default:
			break;
		}
	}

	private void onClickButtonBackupContacts(Context context) {
		// TODO 判断SDCARD可不可用,不可用提示退出
		int numContacts = CMDExecute.getContactsSize(this);
		if (numContacts == 0) {
			// 弹出对话框,电话本为空无需备份
			new AlertDialog.Builder(context).setTitle("注意").setMessage(
					"通讯录为空,无需备份!").setPositiveButton("确定", null).show();
		} else {
			// 弹出进度条
			progressDialog.show();
			doThread(0);
		}
	}

	private void onClickButtonbackupSMS(Context context) {
		// 判断收件,发件箱数
		int inboxNum = CMDExecute.getSMSInboxSize(context);
		int sentboxNum = CMDExecute.getSMSSentboxSize(context);

		if (inboxNum == 0 && sentboxNum == 0) {
			// 弹出对话框,短信收,发件都为空无需备份
			new AlertDialog.Builder(context).setTitle("注意").setMessage(
					"短信收件箱,发件箱都为空,无需备份!").setPositiveButton("确定", null).show();
		} else {
			// 弹出进度条
			progressDialog.show();
			doThread(1);
		}
	}

	private void onClickButtonResumeContacts(Context context) {
		// 先判断有没有之前备份的文件
		File file = new File(ConstantsList.PATH_BACKUP_SDCARD_DIR,
				ConstantsList.FILENAME_BACKUP_CONTACTS_SDCARD);
		if (file.exists()) {
			// 弹出对话框:是,否恢复
			new AlertDialog.Builder(context).setTitle("注意").setMessage(
					"恢复通讯录操作").setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							progressDialog.show();
							doThread(2);
						}
					}).setNegativeButton("取消", null).show();
		} else {
			// 弹出对话框,电话本为空无需备份
			new AlertDialog.Builder(context).setTitle("注意").setMessage(
					"指定路径下无备份文件!").setPositiveButton("确定", null).show();
		}

	}

	private void onClickButtonResumeSMS(Context context) {
		// 先判断有没有之前备份的文件
		File inboxfile = new File(ConstantsList.PATH_BACKUP_SDCARD_DIR,
				ConstantsList.FILENAME_BACKUP_SMSINBOX_SDCARD);
		File sentboxfile = new File(ConstantsList.PATH_BACKUP_SDCARD_DIR,
				ConstantsList.FILENAME_BACKUP_SMSSENTBOX_SDCARD);

		if (inboxfile.exists() || sentboxfile.exists()) {
			// 弹出对话框:是,否恢复
			new AlertDialog.Builder(context).setTitle("注意").setMessage(
					"恢复短信收件箱 发件箱操作").setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							progressDialog.show();
							doThread(3);
						}
					}).setNegativeButton("取消", null).show();
		} else {
			// 弹出对话框,SMS为空无需备份
			new AlertDialog.Builder(context).setTitle("注意").setMessage(
					"指定路径下无备份文件!").setPositiveButton("确定", null).show();
		}
	}

	private void doThread(final int i) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				switch (i) {
				case 0:// 备份通讯录
					backupContacts(BackupAndResumeActivity.this);
					break;
				case 1:// 备份短信
					backupSMS(BackupAndResumeActivity.this);
					break;
				case 2:// 恢复通讯录
					resumeContacts(BackupAndResumeActivity.this);
					break;
				case 3:// 恢复短信
					resumeSMS(BackupAndResumeActivity.this);
					break;
				default:
					break;
				}

			}
		}).start();

	}

	private void backupContacts(Context context) {
		CMDExecute.backupContacts(context);
		handler.sendEmptyMessage(INT_BACKUP_CONTACTS);
	}

	private void backupSMS(Context context) {
		// 分别备份收件,发件箱
		// 判断收件,发件箱数
		int inboxNum = CMDExecute.getSMSInboxSize(context);
		int sentboxNum = CMDExecute.getSMSSentboxSize(context);
		if (inboxNum != 0) {
			CMDExecute.backupSMSInbox(context);
		}

		if (sentboxNum != 0) {
			CMDExecute.backupSMSSentbox(context);
		}

		handler.sendEmptyMessage(INT_BACKUP_SMS);
	}

	/** 恢复通讯录:读文件,直接插入电话本里 */
	private void resumeContacts(Context context) {
		ArrayList<ContactUnit> contactUnits = CMDExecute
				.readContactsXMLFileForPhone(
						ConstantsList.PATH_BACKUP_SDCARD_DIR,
						ConstantsList.FILENAME_BACKUP_CONTACTS_SDCARD);
		// int num = contactUnits.size();
		CMDExecute.insertContactsItemForPhone(context, contactUnits);
		handler.sendEmptyMessage(INT_RESUME_CONTACTS);
	}

	private void resumeSMS(Context context) {
		File inboxfile = new File(ConstantsList.PATH_BACKUP_SDCARD_DIR,
				ConstantsList.FILENAME_BACKUP_SMSINBOX_SDCARD);
		File sentboxfile = new File(ConstantsList.PATH_BACKUP_SDCARD_DIR,
				ConstantsList.FILENAME_BACKUP_SMSSENTBOX_SDCARD);

		if (inboxfile.exists()) {
			ArrayList<SMSUnit> smsInboxUnit = CMDExecute
					.readSMSXMLFileForPhone(
							ConstantsList.PATH_BACKUP_SDCARD_DIR,
							ConstantsList.FILENAME_BACKUP_SMSINBOX_SDCARD);
			CMDExecute.insertSmsItemInPhone(context, smsInboxUnit);
		}

		if (sentboxfile.exists()) {
			ArrayList<SMSUnit> smsSentboxUnit = CMDExecute
					.readSMSXMLFileForPhone(
							ConstantsList.PATH_BACKUP_SDCARD_DIR,
							ConstantsList.FILENAME_BACKUP_SMSSENTBOX_SDCARD);
			CMDExecute.insertSmsItemInPhone(context, smsSentboxUnit);
		}
		handler.sendEmptyMessage(INT_RESUME_SMS);
	}

}
