package com.hwttnet.mobileassistant.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.hwttnet.mobileassistant.R;
import com.hwttnet.mobileassistant.util.FileItem;
import com.hwttnet.mobileassistant.util.FilesFilter;
import com.hwttnet.mobileassistant.util.FolderFilter;
import com.hwttnet.mobileassistant.util.MediaScanner;
import com.hwttnet.mobileassistant.util.MyUtil;

/**
 * 文件管理器
 * */
public class FileManager extends Activity implements OnItemClickListener,
		OnItemLongClickListener, OnTouchListener {
	private final static String TAG = "FileManager";
	private Button backRoot_Button;
	private Button backParent_Button;
	private ListView listView;
	private TextView path;
	private String currPath;
	private String rootPath = "/sdcard";
	private FileListAdapter adapter;
	private ArrayList<FileItem> items;
	private FilesFilter filesFilter = new FilesFilter();
	private FolderFilter folderFilter = new FolderFilter();
	private Bitmap folderIcon;
	private Bitmap apkIcon;
	private Bitmap audioIcon;
	private Bitmap imageIcon;
	private Bitmap otherIcon;
	private Bitmap txtIcon;
	private Bitmap videoIcon;
	private String prompt;
	private String promptNoFile;
	protected View myView;
	protected EditText myEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.filemanager_layout);
		iniRes();
		iniView();
		getFileDir(FileItem.ROOT_PATH);
		super.onCreate(savedInstanceState);
	}

	private void iniRes() {
		prompt = this.getResources().getString(
				R.string.prompt_FileManager_layout);
		promptNoFile = this.getResources().getString(
				R.string.prompt_NOFile_FileManager_layout);
		folderIcon = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.folder);
		apkIcon = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.apk_filemanager);
		audioIcon = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.audio_filemanager);
		imageIcon = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.image_filemanager);
		otherIcon = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.other_filemanager);
		txtIcon = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.txt_filemanager);
		videoIcon = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.video_filemanager);
	}

	private void iniView() {
		path = (TextView) findViewById(R.id.path_TextView_FileManager_layout);
		backRoot_Button = (Button) findViewById(R.id.backRoot_Button_FileManager_layout);
		backParent_Button = (Button) findViewById(R.id.backParent_Button_FileManager_layout);
		listView = (ListView) findViewById(R.id.listView_FileManager_layout);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		backRoot_Button.setOnTouchListener(this);
		backParent_Button.setOnTouchListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	/* 取得文件信息 */
	private void getFileDir(String filePath) {
		path.setText(filePath);
		currPath = filePath;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			items = new ArrayList<FileItem>();
			File parent = new File(filePath);
			// 获取当前目录下的子目录
			File[] files = parent.listFiles(folderFilter);
			Arrays.sort(files);
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				FileItem bean = new FileItem();
				bean.setName(file.getName());
				bean.setFileType(file);
				/* 设置图标 */
				setImageForFileItem(bean);
				bean.setPath(file.getAbsolutePath());
				items.add(bean);
			}

			// 获取当前目录下的文件
			files = parent.listFiles(filesFilter);
			Arrays.sort(files);
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				FileItem bean = new FileItem();
				bean.setName(file.getName());
				bean.setFileType(file);
				/* 设置图标 */
				setImageForFileItem(bean);
				bean.setPath(file.getAbsolutePath());
				long fileSize = new File(file.getAbsolutePath()).length();// 当前APK的大小
				String fileSizeK = MyUtil.accountFileSize(fileSize);
				bean.setSize(fileSizeK);
				items.add(bean);
			}
			setAdapter(items);
		} else {
			// 弹出对话框,无sd卡或sd卡写保护
			new AlertDialog.Builder(this).setTitle("注意").setMessage(
					"SDCard 不存在或写保护!").setPositiveButton("确定", null).show();
		}
	}

	/* 设置图标 */
	private void setImageForFileItem(FileItem bean) {
		switch (bean.getFileType()) {
		case FileItem.FILE_APK:
			bean.setImage(apkIcon);
			break;
		case FileItem.FILE_AUDIO:
			bean.setImage(audioIcon);
			break;
		case FileItem.FILE_DIR:
			bean.setImage(folderIcon);
			break;
		case FileItem.FILE_IMAGE:
			bean.setImage(imageIcon);
			break;
		case FileItem.FILE_OTHER:
			bean.setImage(otherIcon);
			break;
		case FileItem.FILE_TXT:
			bean.setImage(txtIcon);
			break;
		case FileItem.FILE_VIDEO:
			bean.setImage(videoIcon);
			break;
		default:
			break;
		}
	}

	private void setAdapter(ArrayList<FileItem> items2) {
		// 设置adapter
		listView.setAdapter(new FileListAdapter(this, items2));
	}

	/** 按钮事件 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			switch (v.getId()) {
			case R.id.backRoot_Button_FileManager_layout:
				if (!currPath.equals(FileItem.ROOT_PATH)) {
					getFileDir(FileItem.ROOT_PATH);
				} else {
					Toast.makeText(this, prompt, Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.backParent_Button_FileManager_layout:
				if (!currPath.equals(FileItem.ROOT_PATH)) {
					int index = currPath.lastIndexOf("/");
					currPath = currPath.substring(0, index);
					getFileDir(currPath);
				} else {
					Toast.makeText(this, prompt, Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
		return false;
	}

	/** 用于打开文件夹和文件 */
	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position,
			long id) {
		FileItem bean = (FileItem) adapter.getItemAtPosition(position);
		if (bean != null) {
			int fileType = bean.getFileType();
			if (fileType == FileItem.FILE_DIR) {// 文件夹
				getFileDir(bean.getPath());
			} else {// 打开文件
				openFile(bean);
			}
		}
	}

	/** 长按时弹出菜单,删除文件重命名 */
	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View v,
			int position, long id) {
		FileItem bean = (FileItem) adapter.getItemAtPosition(position);
		if (bean != null) {
			int fileType = bean.getFileType();
			if (fileType != FileItem.FILE_DIR) {//
				fileHandle(bean);
			}
		}
		return false;
	}

	private void openFile(FileItem bean) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		/* 调用getMIMEType()来取得MimeType */
		String type = bean.getMIMEType();
		/* 设置intent的file与MimeType */
		intent.setDataAndType(Uri.parse("file://" + bean.getPath()), type);
		startActivity(intent);
	}

	/* 处理文件的方法 */
	private void fileHandle(final FileItem bean) {

		/* 点击文件时的OnClickListener */
		OnClickListener listener1 = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					/* 选择的item为打开文件 */
					openFile(bean);
				} else if (which == 1) {
					renameFile(bean);
				} else {
					deleteFile(bean);
				}
			}
		};

		/* 选择一个文件时，跳出要如何处理文件的ListDialog */
		String[] menu = { "打开文件", "更改文件名", "删除文件" };
		new AlertDialog.Builder(FileManager.this).setTitle("你要做甚么?").setItems(
				menu, listener1).setPositiveButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	protected void deleteFile(FileItem bean) {
		final File file = new File(bean.getPath());
		/* 选择的item为删除文件 */
		new AlertDialog.Builder(FileManager.this).setTitle("注意!").setMessage(
				"确定要删除文件吗?").setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						/* 删除文件 */
						file.delete();
						getFileDir(file.getParent());
					}
				}).setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	protected void renameFile(final FileItem bean) {
		final File file = new File(bean.getPath());
		// TODO Auto-generated method stub
		/* 选择的item为更改档名 */
		LayoutInflater factory = LayoutInflater.from(FileManager.this);
		/* 初始化myChoiceView，使用rename_alert_dialog为layout */
		myView = factory
				.inflate(R.layout.rename_alert_dialog_filemanager, null);
		myEditText = (EditText) myView
				.findViewById(R.id.mEdit_rename_FileManager);
		/* 将原始文件名先放入EditText中 */
		myEditText.setText(file.getName());

		/* new一个更改文件名的Dialog的确定按钮的listener */
		OnClickListener listener2 = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				/* 取得修改后的文件路径 */
				String modName = myEditText.getText().toString();
				final String pFile = file.getParentFile().getPath() + "/";
				final String newPath = pFile + modName;
				final MediaScanner mediaScanner = new MediaScanner(
						FileManager.this);
				/* 判断档名是否已存在 */
				if (new File(newPath).exists()) {
					/* 排除修改档名时没修改直接送出的状况 */
					if (!modName.equals(file.getName())) {
						/* 跳出Alert警告档名重复，并确认是否修改 */
						new AlertDialog.Builder(FileManager.this).setTitle(
								"注意!").setMessage("档名已经存在，是否要覆盖?")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												/* 档名重复仍然修改会覆改掉已存在的文件 */
												file
														.renameTo(new File(
																newPath));
												/* 重新产生文件列表的ListView */
												getFileDir(pFile);
												/* 把修改的文件加入煤体库:这样才能点击有正确的打开方式 */
												mediaScanner.scanFile(newPath,
														bean.getMIMEType());
											}
										}).setNegativeButton("取消",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
											}
										}).show();
					}
				} else {
					/* 档名不存在，直接做修改动作 */
					file.renameTo(new File(newPath));
					/* 重新产生文件列表的ListView */
					getFileDir(pFile);
					/* 把修改的文件加入煤体库:这样才能点击有正确的打开方式 */
					mediaScanner.scanFile(newPath, bean.getMIMEType());
				}
			}
		};

		/* create更改档名时跳出的Dialog */
		AlertDialog renameDialog = new AlertDialog.Builder(FileManager.this)
				.create();
		renameDialog.setView(myView);

		/* 设置更改档名点击确认后的Listener */
		renameDialog.setButton("确定", listener2);
		renameDialog.setButton2("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		renameDialog.show();
	}

	/******************************************************************
	 * 
	 * 文件列表Adapter
	 ***************************************************************/
	private class FileListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private ArrayList<FileItem> items;

		public FileListAdapter(Context context, ArrayList<FileItem> items2) {
			inflater = LayoutInflater.from(context);
			this.items = items2;
			if (items.size() == 0) {
				Toast.makeText(context, promptNoFile, Toast.LENGTH_SHORT)
						.show();
			}
		}

		public int getCount() {
			return items.size();
		}

		public FileItem getItem(int position) {
			return items.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.filemanager_item,
						parent, false);

				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.mImageView_FileManager_item);
				holder.name = (TextView) convertView
						.findViewById(R.id.mFileName_FileManager_item);
				holder.size = (TextView) convertView
						.findViewById(R.id.mFileSize_FileManager_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			FileItem bean = items.get(position);
			holder.image.setImageBitmap(bean.getImage());
			holder.name.setText(bean.getName());
			holder.size.setText(bean.getSize());
			return convertView;
		}

		private class ViewHolder {
			ImageView image;// 图片
			TextView name;// 名称
			TextView size;// 大小
		}
	}

}
