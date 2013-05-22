package com.hwttnet.mobileassistant.ui;

import com.hwttnet.mobileassistant.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * 九.人工服务
 * 
 * */
public class ManualServiceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.manualservice_layout);
		super.onCreate(savedInstanceState);
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
