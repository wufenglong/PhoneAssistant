package com.hwttnet.mobileassistant.service;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.hwttnet.mobileassistant.util.MyUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 设置：android手机
 * 
 * 项目名称：139手机助手守护程序
 * 
 * 版本：1.0
 * */
public class MoblieAssistantService extends Service {
	public static final String TAG = "MoblieAssistantService";
	public static Boolean mainThreadFlag = true;
	public static Boolean ioThreadFlag = true;
	ServerSocket serverSocket = null;
	final int SERVER_PORT = 10086;
	File testFile;

	// private sysBroadcastReceiver sysBR;

	@Override
	public void onCreate() {
		super.onCreate();
		MyUtil.log(TAG, "onCreate()");
		/* 创建内部类sysBroadcastReceiver 并注册registerReceiver */
		// sysRegisterReceiver();
		new Thread() {
			public void run() {
				doListen();
			};
		}.start();
	}

	private void doListen() {
		MyUtil.log(TAG, "doListen()");
		serverSocket = null;
		try {
			MyUtil.log(TAG, "doListen() new serverSocket");
			serverSocket = new ServerSocket(SERVER_PORT);

			boolean mainThreadFlag = true;
			while (mainThreadFlag) {
				MyUtil.log(TAG, "doListen() listen");
				Socket client = serverSocket.accept();

				new Thread(new ThreadReadWriterIOSocket(this, client)).start();
			}
		} catch (IOException e1) {
			MyUtil.log(TAG, "new serverSocket error");
			e1.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// 关闭线程
		mainThreadFlag = false;
		ioThreadFlag = false;
		// 关闭服务器
		try {
			MyUtil.log(TAG, "serverSocket.close()");
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		MyUtil.log(TAG, "****************Service139 onDestroy****************");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}