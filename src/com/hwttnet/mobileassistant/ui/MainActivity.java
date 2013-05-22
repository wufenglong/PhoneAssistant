package com.hwttnet.mobileassistant.ui;

import com.hwttnet.mobileassistant.R;
import com.hwttnet.mobileassistant.util.ConstantsList;
import com.hwttnet.mobileassistant.util.MyUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 程序入口
 * 
 * ============
 * 
 * 九宫格
 * 
 * 1.系统检测
 * 
 * 2.清理垃圾
 * 
 * 3.开机加速
 * 
 * 4.任务管理
 * 
 * 5.软件管理
 * 
 * 6.文件管理
 * 
 * 7.一健优化
 * 
 * 8.备份恢复
 * 
 * 9.系统帮助
 * 
 * */
public class MainActivity extends Activity {
	private final static String TAG = "MainActivity";
	/* 九宫格的九图 */
	private int[] gridRes = { R.drawable.xitongjiance1, R.drawable.qinglilaji1,
			R.drawable.kaijijiasu1, R.drawable.renwuguanli1,
			R.drawable.ruanjianguanli1, R.drawable.filemanager,
			R.drawable.yijianyouhua1, R.drawable.beifenhuifu1,
			R.drawable.xitongbangzhu1 };
	/* 九宫格被选中图 */
	private int[] gridSelectedRes = { R.drawable.xitongjiance2,
			R.drawable.qinglilaji2, R.drawable.kaijijiasu2,
			R.drawable.renwuguanli2, R.drawable.ruanjianguanli2,
			R.drawable.filemanager2, R.drawable.yijianyouhua2,
			R.drawable.beifenhuifu2, R.drawable.xitongbangzhu2 };
	private String[] title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题
		setContentView(R.layout.main);
		mkdir();
		iniRes();
		GridView gridView = (GridView) findViewById(R.id.main_gridview);
		gridView.setAdapter(new ImageButtonAdapter(this));
		super.onCreate(savedInstanceState);
	}

	private void iniRes() {
		// TODO Auto-generated method stub
		title = getResources().getStringArray(R.array.main_array);
	}

	private void mkdir() {
		MyUtil.mkdirs(ConstantsList.PATH_BACKUP_SDCARD_DIR);
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

	/**
	 * 九宫格adapter
	 * 
	 * 用imageButton来现实被选效果
	 * */
	private class ImageButtonAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mLayoutInflater;

		public ImageButtonAdapter(Context c) {
			mContext = c;
			mLayoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return gridRes.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
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
						R.layout.main_gridview_item, null);
				holder = new ViewHolder();
				holder.imageButton = (ImageButton) convertView
						.findViewById(R.id.mImageButton_mainGridView);
				holder.name = (TextView) convertView
						.findViewById(R.id.mTextView_mainGridView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(title[position]);
			holder.imageButton.setBackgroundResource(gridRes[position]);
			holder.imageButton.setOnTouchListener(new Button.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						v.setBackgroundResource(gridSelectedRes[position]);
					} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
						v.setBackgroundResource(gridRes[position]);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						v.setBackgroundResource(gridRes[position]);
						/* 跳转 */
						skipControl(position);
					}
					return false;
				}

			});
			return convertView;
		}

		private class ViewHolder {
			ImageButton imageButton;
			TextView name;
		}
	}

	/** 跳转 */
	private void skipControl(int position) {
		Intent intent = null;
		switch (position) {
		case 0:// 系统检测
			intent = new Intent(MainActivity.this,
					SystemDetectionActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case 1:// 清理垃圾
			intent = new Intent(MainActivity.this,
					CleaningRubbishActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case 2:// 开机加速
			intent = new Intent(MainActivity.this, BootSpeedActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case 3:// 任务管理
			intent = new Intent(MainActivity.this, ProcessManageActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case 4:// 软件管理
			intent = new Intent(MainActivity.this,
					InstallSoftManageActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case 5:// 文件管理
			intent = new Intent(MainActivity.this, FileManager.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case 6:// 一键优化
			intent = new Intent(MainActivity.this,
					OneClickOptimizationActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case 7:// 备份恢复
			intent = new Intent(MainActivity.this,
					BackupAndResumeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case 8:// 系统帮助
			intent = new Intent(MainActivity.this, SystemHelpActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
