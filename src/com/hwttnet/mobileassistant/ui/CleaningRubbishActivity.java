package com.hwttnet.mobileassistant.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.hwttnet.mobileassistant.R;
import com.hwttnet.mobileassistant.service.MoblieAssistantService;

/**
 * 二.垃圾清理
 * 
 * 1.清理临时文件
 * 
 * 2.清理SMS收件箱
 * 
 * 3.清理已发送短信
 * 
 * 4.清理草稿箱
 * 
 * 5.清理发件箱
 * 
 * */
public class CleaningRubbishActivity extends Activity {
	private final static String TAG = "CleaningRubbishActivity";
	private final static int CLEAN_TEMP_FILE = 0;// 清理临时文件
	private final static int CLEAN_SMS_INBOX = 1;// 清理SMS收件箱
	private final static int CLEAN_SMS_SENTBOX = 2;// 清理已发送短信
	private final static int CLEAN_SMS_DRAFT = 3;// 清理草稿箱
	private final static int CLEAN_SMS_OUTBOX = 4;// 清理发件箱
	private CheckBox mCheckBox_CleanTempFile;
	private CheckBox mCheckBox_CleanSMSINBOX;
	private CheckBox mCheckBox_CleanSMSSENTBOX;
	private CheckBox mCheckBox_CleanSMSDRAFT;
	private CheckBox mCheckBox_CleanSMSOUTBOX;
	private ImageButton mImageButton_submit;
	private ArrayList<Integer> checkedList;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				progressDialog.dismiss();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};
	private ProgressDialog progressDialog;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题
		setContentView(R.layout.cleaningrubbish_layout);
		iniView();// 加载view
		iniRes();
		setListener();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void iniRes() {
		title = getResources().getString(R.string.CleaningRubbish);
		setTitle(title);
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("垃圾清理");
		progressDialog.setMessage("清理中,请稍后...");
		progressDialog.setCancelable(false);

		checkedList = new ArrayList<Integer>();
		if (mCheckBox_CleanTempFile.isChecked()) {
			checkedList.add(CLEAN_TEMP_FILE);
		}
		if (mCheckBox_CleanSMSINBOX.isChecked()) {
			checkedList.add(CLEAN_SMS_INBOX);
		}
		if (mCheckBox_CleanSMSSENTBOX.isChecked()) {
			checkedList.add(CLEAN_SMS_SENTBOX);
		}
		if (mCheckBox_CleanSMSDRAFT.isChecked()) {
			checkedList.add(CLEAN_SMS_DRAFT);
		}
		if (mCheckBox_CleanSMSOUTBOX.isChecked()) {
			checkedList.add(CLEAN_SMS_OUTBOX);
		}
	}

	private void iniView() {
		mCheckBox_CleanTempFile = (CheckBox) findViewById(R.id.mCheckBox_CleanTempFile_cleaningRubbish);
		mCheckBox_CleanSMSINBOX = (CheckBox) findViewById(R.id.mCheckBox_CleanSMSINBOX_cleaningRubbish);
		mCheckBox_CleanSMSSENTBOX = (CheckBox) findViewById(R.id.mCheckBox_CleanSMSSENTBOX_cleaningRubbish);
		mCheckBox_CleanSMSDRAFT = (CheckBox) findViewById(R.id.mCheckBox_CleanSMSDRAFT_cleaningRubbish);
		mCheckBox_CleanSMSOUTBOX = (CheckBox) findViewById(R.id.mCheckBox_CleanSMSOUTBOX_cleaningRubbish);
		mImageButton_submit = (ImageButton) findViewById(R.id.mImageButton_submit_cleaningRubbish);
	}

	private void setListener() {
		mCheckBox_CleanTempFile
				.setOnCheckedChangeListener(checkedChangeListener);
		mCheckBox_CleanSMSINBOX
				.setOnCheckedChangeListener(checkedChangeListener);
		mCheckBox_CleanSMSSENTBOX
				.setOnCheckedChangeListener(checkedChangeListener);
		mCheckBox_CleanSMSDRAFT
				.setOnCheckedChangeListener(checkedChangeListener);
		mCheckBox_CleanSMSOUTBOX
				.setOnCheckedChangeListener(checkedChangeListener);
		mImageButton_submit.setOnTouchListener(new Button.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				progressDialog.show();
				// 根据5个CheckBox被选情况,执行清除垃圾操作
				if (event.getAction() == MotionEvent.ACTION_UP) {
					doCleaningRubbishThread();
				}
				return false;
			}

		});
	}

	/* 根据5个CheckBox被选情况,执行清除垃圾操作 */
	protected void doCleaningRubbishThread() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.v(TAG, "checkedList.size()=" + checkedList.size());
				Iterator iterator = checkedList.iterator();
				while (iterator.hasNext()) {
					int i = (Integer) iterator.next();
					switch (i) {
					case CLEAN_TEMP_FILE:
						CleanTrash();
						break;
					case CLEAN_SMS_INBOX:
						ClearSMS("1");
						break;
					case CLEAN_SMS_SENTBOX:
						ClearSMS("2");
						break;
					case CLEAN_SMS_DRAFT:
						ClearSMS("3");
						break;
					case CLEAN_SMS_OUTBOX:
						ClearSMS("4");
						break;
					default:
						break;
					}
				}

				handler.sendEmptyMessage(0);
			}
		}).start();
	}

	private OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// 把被选的,存在一个ArrayList里
			CheckBox mCheckBox = (CheckBox) buttonView;
			// setArrayList
			updateArrayList(mCheckBox, isChecked);
		}

	};

	private void updateArrayList(CheckBox mCheckBox, boolean isChecked) {
		if (mCheckBox.equals(mCheckBox_CleanTempFile)) {
			if (isChecked) {
				if (!checkedList.contains(CLEAN_TEMP_FILE)) {
					checkedList.add(CLEAN_TEMP_FILE);
				}
			} else {
				if (checkedList.contains(CLEAN_TEMP_FILE)) {
					checkedList.remove(Integer.valueOf(CLEAN_TEMP_FILE));
				}
			}
		}

		if (mCheckBox.equals(mCheckBox_CleanSMSINBOX)) {
			if (isChecked) {
				if (!checkedList.contains(Integer.valueOf(CLEAN_SMS_INBOX))) {
					checkedList.add(Integer.valueOf(CLEAN_SMS_INBOX));
				}
			} else {
				if (checkedList.contains(Integer.valueOf(CLEAN_SMS_INBOX))) {
					checkedList.remove(Integer.valueOf(CLEAN_SMS_INBOX));
				}
			}
		}

		if (mCheckBox.equals(mCheckBox_CleanSMSSENTBOX)) {
			if (isChecked) {
				if (!checkedList.contains(Integer.valueOf(CLEAN_SMS_SENTBOX))) {
					checkedList.add(Integer.valueOf(CLEAN_SMS_SENTBOX));
				}
			} else {
				if (checkedList.contains(Integer.valueOf(CLEAN_SMS_SENTBOX))) {
					checkedList.remove(Integer.valueOf(CLEAN_SMS_SENTBOX));
				}
			}
		}

		if (mCheckBox.equals(mCheckBox_CleanSMSDRAFT)) {
			if (isChecked) {
				if (!checkedList.contains(Integer.valueOf(CLEAN_SMS_DRAFT))) {
					checkedList.add(Integer.valueOf(CLEAN_SMS_DRAFT));
				}
			} else {
				if (checkedList.contains(Integer.valueOf(CLEAN_SMS_DRAFT))) {
					checkedList.remove(Integer.valueOf(CLEAN_SMS_DRAFT));
				}
			}
		}

		if (mCheckBox.equals(mCheckBox_CleanSMSOUTBOX)) {
			if (isChecked) {
				if (!checkedList.contains(Integer.valueOf(CLEAN_SMS_OUTBOX))) {
					checkedList.add(Integer.valueOf(CLEAN_SMS_OUTBOX));
				}
			} else {
				if (checkedList.contains(Integer.valueOf(CLEAN_SMS_OUTBOX))) {
					checkedList.remove(Integer.valueOf(CLEAN_SMS_OUTBOX));
				}
			}
		}
	}

	/* 清理垃圾 */
	private void CleanTrash() {
		/* 清理：Cookie */
		CookieSyncManager.createInstance(this);
		CookieManager.getInstance().removeAllCookie();
	}

	/* 清理SMS（包括4个类型：草稿箱3，收件箱1，已发送发件箱2，发件箱4） */
	private void ClearSMS(String _typeSMS) {
		Log.v(TAG, "ClearSMS() start...." + "_typeSMS=" + _typeSMS);
		/* 查询 */
		Cursor cursor = this.getContentResolver().query(
				Uri.parse("content://sms"), null, null, null, null);
		int numcount = cursor.getCount();
		for (int i = 0; i < numcount; i++) {
			cursor.moveToPosition(i);
			String type = cursor
					.getString(cursor.getColumnIndexOrThrow("type"));
			if (type.equals(_typeSMS)) {
				String _id = cursor.getString(cursor
						.getColumnIndexOrThrow("_id"));// id
				String thread_id = cursor.getString(cursor
						.getColumnIndexOrThrow("thread_id"));
				this.getContentResolver().delete(
						Uri.parse("content://sms/conversations/" + thread_id),
						"_id= " + _id, null);
				Log.v(TAG, "delete sms >>>>>>" + "_typeSMS=" + _typeSMS
						+ " id=" + _id);
			}
		}
	}
}
