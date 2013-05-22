package com.hwttnet.mobileassistant.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

import com.hwttnet.mobileassistant.service.MoblieAssistantService;

public class ioHelper {
	/**
	 * 功能：从socket流中读取完整文件数据
	 * 
	 * InputStream in：socket输入流
	 * 
	 * byte[] filelength: 流的前4个字节存储要转送的文件的字节数
	 * 
	 * byte[] fileformat：流的前5-8字节存储要转送的文件的格式（如.apk）
	 * 
	 * */
	public static byte[] receiveFileFromSocket(InputStream in,
			OutputStream out, byte[] filelength, byte[] fileformat) {// 三次读写交互
		byte[] filebytes = null;// 文件数据
		try {
			in.read(filelength);// 读文件长度
			int filelen = MyUtil.bytesToInt(filelength);//
			out.write(filelength);
			out.flush();
			filebytes = new byte[filelen];
			int pos = 0;
			int rcvLen = 0;
			while ((rcvLen = in.read(filebytes, pos, filelen - pos)) > 0) {
				pos += rcvLen;
			}
			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName() + "---->"
					+ "read file OK:file size=" + filebytes.length);

		} catch (Exception e) {
			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName() + "---->"
					+ "receiveFileFromSocket error");
			e.printStackTrace();
		}
		return filebytes;
	}

	/* 读取命令 */
	static int numerrorioHelper = 0;//

	public static String readCMDFromSocket(InputStream in) {
		int MAX_BUFFER_BYTES = 4048;
		String msg = "";
		byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
		try {
			int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
			// Log.v(Service139.TAG, Thread.currentThread().getName() + "---->"
			// + "numReadedBytes=" + numReadedBytes);

			if (numReadedBytes > 0) {
				msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");
			}
			// tempbuffer = null;
		} catch (Exception e) {
			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName() + "---->"
					+ "readFromSocket error");
			e.printStackTrace();
		}
		// Log.v(Service139.TAG, "msg=" + msg);
		return msg;
	}
}
