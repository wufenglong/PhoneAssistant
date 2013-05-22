package com.hwttnet.mobileassistant.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hwttnet.mobileassistant.util.CMDExecute;
import com.hwttnet.mobileassistant.util.ConstantsList;
import com.hwttnet.mobileassistant.util.MyUtil;

public class ServiceBroadcastReceiver extends BroadcastReceiver {
	private final static String TAG = "ServiceBroadcastReceiver";
	private static String START_ACTION = "com.hwttnet.moblieassistant139.service.NotifyServiceStart";
	private static String STOP_ACTION = "com.hwttnet.moblieassistant139.service.NotifyServiceStop";
	private static String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		MyUtil.log(TAG, "ServiceBroadcastReceiver onReceive");
		String action = intent.getAction();
		MyUtil.log(TAG, "action =" + action);
		if (START_ACTION.equalsIgnoreCase(action)) {
			context.startService(new Intent(context,
					MoblieAssistantService.class));

		} else if (STOP_ACTION.equalsIgnoreCase(action)) {
			context.stopService(new Intent(context,
					MoblieAssistantService.class));
		} else if (BOOT_ACTION.equalsIgnoreCase(action)) {
			/* 关闭开机启动程序 */
			closeAssignBootTasks(context);
		}
	}

	/* 关闭开机启动程序 */
	private void closeAssignBootTasks(Context context) {
		/* 从TXT文件里存出要禁止的程序列表 */
		ArrayList<String> disabledBootList = CMDExecute
				.GetShutAutoStartInfoListFromTXT(context);
		/* 关闭 */
		if (disabledBootList.size() != 0) {
			CMDExecute.CloseRunningTasks(context, disabledBootList);
		}
	}

}
