package com.hwttnet.mobileassistant.util;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.hwttnet.mobileassistant.service.MoblieAssistantService;

public class MyUtil {
	/* byte[]转Int */
	public static int bytesToInt(byte[] bytes) {
		int addr = bytes[0] & 0xFF;
		addr |= ((bytes[1] << 8) & 0xFF00);
		addr |= ((bytes[2] << 16) & 0xFF0000);
		addr |= ((bytes[3] << 24) & 0xFF000000);
		return addr;
	}

	/* Int转byte[] */
	public static byte[] intToByte(int i) {
		byte[] abyte0 = new byte[4];
		abyte0[0] = (byte) (0xff & i);
		abyte0[1] = (byte) ((0xff00 & i) >> 8);
		abyte0[2] = (byte) ((0xff0000 & i) >> 16);
		abyte0[3] = (byte) ((0xff000000 & i) >> 24);
		return abyte0;
	}

	// wu0wu
	public static void log(Context context, String str) {
		android.util.Log.v("log>>>>>", "***wu:***"
				+ context.getClass().getName() + "-->" + str);
	}

	// wu0wu
	public static void log(String tag, String str) {
		android.util.Log.v("log>>>>>", "***wu:***" + tag + "-->" + str);
	}

	public static String accountFileSize(Long lSize) {
		long m = lSize / (1024 * 1024);
		if (m > 0) {
			long k = lSize % (1024 * 1024) / 1024 / 10;
			return m + "." + k + "M";
		} else {
			long k = lSize % (1024 * 1024) / 1024;
			return k + "k";
		}

	}

	public static void mkdirs(String path) {
		File root = new File(path);
		if (!root.exists()) {
			root.mkdirs();
		}
	}

	public static String getCurrTime() {
		Date date = new Date();
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		String dFormat = sDateFormat.format(date);
		return dFormat;
	}
}
