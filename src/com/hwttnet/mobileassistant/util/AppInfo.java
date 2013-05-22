package com.hwttnet.mobileassistant.util;

import android.graphics.drawable.Drawable;

public class AppInfo {
	public String name;
	public String version;
	public String packageName;
	public Drawable icon;
	public boolean isSelected = false;// 多选框被选标记
	public boolean isDisabled = false;// 开机启动禁止标记
	public String size;

	public AppInfo() {

	}
}
