package com.hwttnet.mobileassistant.util;

import java.io.File;

import android.graphics.Bitmap;

public class FileItem implements Comparable<FileItem> {

	public static final int FILE_IMAGE = 0;
	public static final int FILE_AUDIO = 1;
	public static final int FILE_VIDEO = 2;
	public static final int FILE_OTHER = 3;
	public static final int FILE_APK = 4;
	public static final int FILE_TXT = 5;

	public static final int FILE_DIR = 6;
	public static final int FILE_ROOT = 7;
	public static final int ROOT_ID = 8;
	public static final String ROOT_NAME = "上级目录";
	public static final String ROOT_PATH = "/sdcard";

	private String name;
	private String path;
	private String size;

	private int fileType;//
	private String MIMEType;
	private Bitmap image;

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(File f) {
		if (!f.isDirectory()) {
			String fName = f.getName();
			/* 取得扩展名 */
			String end = fName.substring(fName.lastIndexOf(".") + 1,
					fName.length()).toLowerCase();

			/* 依附档名的类型决定MimeType */
			if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
					|| end.equals("xmf") || end.equals("ogg")
					|| end.equals("wav")) {
				fileType = FILE_AUDIO;
				MIMEType = "audio/*";
			} else if (end.equals("3gp") || end.equals("mp4")) {
				fileType = FILE_VIDEO;
				MIMEType = "video/*";
			} else if (end.equals("jpg") || end.equals("gif")
					|| end.equals("png") || end.equals("jpeg")
					|| end.equals("bmp")) {
				fileType = FILE_IMAGE;
				MIMEType = "image/*";
			} else if (end.equals("txt")) {
				fileType = FILE_TXT;
				MIMEType = "text/plain";
			} else if (end.equals("apk")) {
				fileType = FILE_APK;
				MIMEType = "application/vnd.android.package-archive";
			} else {
				MIMEType = "*/*";
				fileType = FILE_OTHER;
			}
		} else {
			fileType = FILE_DIR;
		}
	}

	public int compareTo(FileItem another) {
		return this.name.compareTo(another.getName());
	}

	public String getMIMEType() {
		return MIMEType;
	}


}
