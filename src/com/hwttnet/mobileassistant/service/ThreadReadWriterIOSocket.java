package com.hwttnet.mobileassistant.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.hwttnet.mobileassistant.util.CMDExecute;
import com.hwttnet.mobileassistant.util.EnumAction;
import com.hwttnet.mobileassistant.util.MyUtil;
import com.hwttnet.mobileassistant.util.ThreadUpdateOperate;
import com.hwttnet.mobileassistant.util.ioHelper;

/**
 * 功能：用于socket的交互
 * 
 * @author wufenglong
 * 
 */
public class ThreadReadWriterIOSocket implements Runnable {
	private Socket client;
	private Context context;
	private ArrayList<ThreadUpdateOperate> UpdateContactsList;// 更新电话本线程列表
	private ArrayList<ThreadUpdateOperate> UpdateSMSInboxList;// 更新收件箱线程列表
	private ArrayList<ThreadUpdateOperate> UpdateSMSSentboxList;// 更新发件箱线程列表

	ThreadReadWriterIOSocket(Context context, Socket client) {
		UpdateContactsList = new ArrayList<ThreadUpdateOperate>();
		UpdateSMSInboxList = new ArrayList<ThreadUpdateOperate>();
		UpdateSMSSentboxList = new ArrayList<ThreadUpdateOperate>();
		this.client = client;
		this.context = context;
	}

	@Override
	public void run() {
		Log.d(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "a client has connected to server!");
		BufferedOutputStream out;
		BufferedInputStream in;
		try {
			/* PC端发来的数据msg */
			String currCMD = "";
			out = new BufferedOutputStream(client.getOutputStream());
			in = new BufferedInputStream(client.getInputStream());

			MoblieAssistantService.ioThreadFlag = true;
			while (MoblieAssistantService.ioThreadFlag) {
				try {
					if (!client.isConnected()) {
						break;
					}

					/* 接收PC发来的数据 */
					Log.v(MoblieAssistantService.TAG, Thread.currentThread()
							.getName()
							+ "---->" + "will read......");
					/* 读操作命令 */
					currCMD = ioHelper.readCMDFromSocket(in);
					/* 解析PC命令 */
					/* 根据命令分别处理数据 */
					// Log.v(MoblieAssistantService.TAG, Thread.currentThread()
					// .getName()
					// + "---->" + "currCMD=" + currCMD);
					switch (EnumAction.valueOf(currCMD)) {
					case GET_INSTALLEDAPP_LIST:// 获得手机已安装软件列表
						CMDExecute.GetInstalledAPPList(context, out);
						break;
					case GET_DEVICEINFO_LIST:// 读取设备信息
						CMDExecute.GetDeviceInfoList(context, out);
						break;
					case GET_BOOT_COMPLETED_LIST:// 读取开机启动信息
						CMDExecute.GetBootCompletedAPPList(context, out);
						break;
					case GET_RUNNINGTASKS_INFO_LIST:// 正在运行的程序的信息
						CMDExecute.GetRunningTasksInfoList(context, out);
						break;
					case GET_CONTACTS_INFO_LIST:// 获得电话本信息contacts
						CMDExecute.GetContactsInfoList(context, out);
						break;
					case GET_SMSINBOX_INFO_LIST:// 获得短信SMS收件箱Inbox
						CMDExecute.GetSMSInboxInfoList(context, out);
						break;
					case GET_SMSSENTBOX_INFO_LIST:// 获得短信SMS发件箱sentbox
						CMDExecute.GetSMSSentboxInfoList(context, out);
						break;
					case UPDATE_CONTACTS:// 更新电话本
						CMDExecute.UpdateContacts(context, in, out,
								UpdateContactsList);
						break;
					case UPDATE_SMSINBOX:// 更新收件箱
						CMDExecute.UpdateSMSInbox(context, in, out,
								UpdateSMSInboxList);
						break;
					case UPDATE_SMSSENTBOX:// 更新发件箱
						CMDExecute.UpdateSMSSentbox(context, in, out,
								UpdateSMSSentboxList);
						break;
					case CLOSE_RUNNINGTASKS:// 关闭正在运行的程序
						CMDExecute.CloseRunningTasks(context, in, out);
						break;
					case UPDATE_SHUT_BOOTAPP:// 更新被关闭的开机启动程序列表
						// ShutAutoStart.txt
						CMDExecute.UpdateShutAutoStartTXT(context, out);
						break;
					case GET_SHUT_BOOTAPP_LIST:// 获得被关闭的开机启动程序列表
						// ShutAutoStart.txt
						CMDExecute
								.GetShutAutoStartInfoListFromTXT(context, out);
						break;
					case CLEAN_SMSDRAFT:// 清理SMS草稿箱
						CMDExecute.ClearSMSDraft(context, in, out);
						break;
					case CLEAN_SMSINBOX:// 清理SMS收件箱
						CMDExecute.ClearSMSInbox(context, in, out);
						break;
					case CLEAN_SMSSENTBOX:// 清理SMS已发送发件箱
						CMDExecute.ClearSMSSentbox(context, in, out);
						break;
					case CLEAN_SMSOUTBOX:// 清理SMS发件箱
						CMDExecute.ClearSMSOutbox(context, in, out);
						break;
					case CLEAN_TRASH:// 清理垃圾
						CMDExecute.CleanTrash(context, out);
						break;
					case TEST_SENTFILE:// 测试发送接收文件
						CMDExecute.TestReceiveFile(context, in, out);
						break;
					case TEST:// 测试连接
						try {
							Log.v(MoblieAssistantService.TAG, Thread
									.currentThread().getName()
									+ "---->" + "**currCMD = TEST");
							out.write("TEST ok".getBytes("utf-8"));
							out.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						break;
					case EXIT:
						Log.v(MoblieAssistantService.TAG, Thread
								.currentThread().getName()
								+ "---->" + "**currCMD = EXIT");
						try {
							out.write("EXIT ok".getBytes("utf-8"));
							out.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						MoblieAssistantService.ioThreadFlag = false;
						break;
					default:
						// Log.v(Service139.TAG,
						// Thread.currentThread().getName()
						// + "---->" + "**other cmd is ok");
						// try {
						// out.write("test ok".getBytes("utf-8"));
						// out.flush();
						// } catch (IOException e1) {
						// e1.printStackTrace();
						// }
						break;
					}
				} catch (Exception e) {
					try {
						out.write("error".getBytes("utf-8"));
						out.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					Log.e(MoblieAssistantService.TAG, Thread.currentThread()
							.getName()
							+ "---->" + "read write error111111");
				}
			}
			out.close();
			in.close();
		} catch (Exception e) {
			Log.e(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "read write error222222");
			e.printStackTrace();
		} finally {
			try {
				if (client != null) {
					Log.v(MoblieAssistantService.TAG, Thread.currentThread()
							.getName()
							+ "---->" + "client.close()");
					client.close();
				}
			} catch (IOException e) {
				Log.e(MoblieAssistantService.TAG, Thread.currentThread()
						.getName()
						+ "---->" + "read write error333333");
				e.printStackTrace();
			}
		}
	}
}
