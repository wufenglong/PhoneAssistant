package com.hwttnet.mobileassistant.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;

import com.hwttnet.mobileassistant.R;

/**
 * 八.系统帮助
 * 
 * */
public class SystemHelpActivity extends Activity {

	public static final String TAG = "SystemHelpActivity";
	private WebView mWebView;
	private sys sys;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.systemhelp_layout);
		sys = new sys();
		mWebView = (WebView) findViewById(R.id.myWebView_systemhelp);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(sys, "sys");
		mWebView.setBackgroundColor(0);
		mWebView.loadUrl("file:///android_asset/systemhelp.html");
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

	public class sys {
		public void linknet(String url) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}

		public void sendEmail(String url) {
			Uri uri = Uri.parse("mailto:" + url);
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			startActivity(intent);
		}

		public void callTo(String url) {
			Uri uri = Uri.parse("tel:" + url);
			Intent intent = new Intent(Intent.ACTION_DIAL, uri);
			startActivity(intent);
		}
	}
}
