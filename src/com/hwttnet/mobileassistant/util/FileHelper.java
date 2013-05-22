package com.hwttnet.mobileassistant.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import android.content.Context;

import com.hwttnet.mobileassistant.service.MoblieAssistantService;

import android.util.Log;

public class FileHelper {
	public static String FILEPATH = "/data/local/tmp";

	// private static String FILEPATH = "/sdcard";

	// private static String FILEPATH = "/tmp";

	public static File newFile(String fileName) {
		File file = null;
		try {
			file = new File(FILEPATH, fileName);
			file.delete();
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}

	public static File newFile(String path, String fileName) {
		File file = null;
		try {
			file = new File(path, fileName);
			file.delete();
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}

	public static void writeFile(File file, byte[] data, int offset, int count)
			throws IOException {

		FileOutputStream fos = new FileOutputStream(file, true);
		fos.write(data, offset, count);
		fos.flush();
		fos.close();
	}

	public static void writeFile(File file, byte[] data) throws IOException {

		FileOutputStream fos = new FileOutputStream(file, true);
		fos.write(data);
		fos.flush();
		fos.close();
	}

	/* 把字符串写入包名下的文件夹的文件中 */
	public static boolean WriteStringToFile(String name, String txt,
			Context context) {
		try {
			OutputStream os = context
					.openFileOutput(name, Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(txt);
			osw.close();
			os.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public static byte[] readFile(String fileName) throws IOException {
		File file = new File(FILEPATH, fileName);
		file.createNewFile();
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		int leng = bis.available();
		Log.v(MoblieAssistantService.TAG, "filesize = " + leng);
		byte[] b = new byte[leng];
		bis.read(b, 0, leng);
		// Input in = new Input(fis);
		// byte[] ret = in.readAll();
		// in.close();
		bis.close();
		return b;
	}
}
