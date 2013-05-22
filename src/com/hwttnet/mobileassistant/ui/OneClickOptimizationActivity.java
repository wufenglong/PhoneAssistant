package com.hwttnet.mobileassistant.ui;

import com.hwttnet.mobileassistant.R;
import com.hwttnet.mobileassistant.util.CMDExecute;
import com.hwttnet.mobileassistant.util.MyUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * 七.一键优化
 * 
 * 1.垃圾文件清理
 * 
 * 2.短信及通讯录的备份
 * 
 * */
public class OneClickOptimizationActivity extends Activity {
	private Editor edit;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:// 备份电话本,修改时间
				String currTime = MyUtil.getCurrTime();
				edit = sp.edit();
				edit.putString(
						BackupAndResumeActivity.LAST_BACKUP_CONTACTS_TIMES,
						currTime);
				edit.commit();
				break;
			case 1:// 备份sms,修改时间
				String currTime1 = MyUtil.getCurrTime();
				edit = sp.edit();
				edit.putString(BackupAndResumeActivity.LAST_BACKUP_SMS_TIMES,
						currTime1);
				edit.commit();
				break;
			case -1:// 
				progressDialog.dismiss();
				finish();
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}

	};
	private ProgressDialog progressDialog;
	private SharedPreferences sp;
	private Button button_oneClick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.oneclickoptimization_layout);
		iniView();
		iniRes();
		setOnClick();

		super.onCreate(savedInstanceState);
	}

	private void setOnClick() {
		button_oneClick.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					progressDialog.show();
					OneClickOptimization();
				} else {
					// 弹出对话框,无sd卡或sd卡写保护
					new AlertDialog.Builder(OneClickOptimizationActivity.this)
							.setTitle("注意").setMessage("SDCard 不存在或写保护!")
							.setPositiveButton("确定", new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).show();
				}
			}

		});
	}

	private void iniView() {
		// TODO Auto-generated method stub
		button_oneClick = (Button) findViewById(R.id.mButton_oneClickOptimization);
	}

	private void OneClickOptimization() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				CMDExecute.CleanTrash(OneClickOptimizationActivity.this);
				backupContacts(OneClickOptimizationActivity.this);
				backupSMS(OneClickOptimizationActivity.this);
				try {
					Thread.sleep(5000);
					handler.sendEmptyMessage(-1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}).start();

	}

	private void iniRes() {
		sp = getSharedPreferences(
				BackupAndResumeActivity.BACKUP_AND_RESUME_TIMES_NAME, 0);
		// TODO Auto-generated method stub
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("一键优化");
		progressDialog.setMessage("处理中,请稍后...");
		progressDialog.setCancelable(false);
	}

	private void backupContacts(Context context) {
		int numContacts = CMDExecute.getContactsSize(this);
		if (numContacts != 0) {
			CMDExecute.backupContacts(context);
			handler.sendEmptyMessage(0);
		}
	}

	private void backupSMS(Context context) {
		// 分别备份收件,发件箱
		// 判断收件,发件箱数
		int inboxNum = CMDExecute.getSMSInboxSize(context);
		int sentboxNum = CMDExecute.getSMSSentboxSize(context);
		if (inboxNum != 0) {
			CMDExecute.backupSMSInbox(context);
			handler.sendEmptyMessage(1);
		}

		if (sentboxNum != 0) {
			CMDExecute.backupSMSSentbox(context);
			handler.sendEmptyMessage(1);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
