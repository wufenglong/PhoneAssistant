package com.hwttnet.mobileassistant.util;

public class ConstantsList {
	/* PC发来的禁止开机启动文件所在路径 */
	public static String PATH_SHUT_AUTOSTART_FROM_PC = "/data/local/tmp";
	public static String FILENAME_SHUT_AUTOSTART = "ShutAutoStart.txt";
	/* 本手机守护的包名 */
	public static String MOBILE_ASSISTANT_PACKAGENAME = "com.hwttnet.mobileassistant";
	/* 当前内部版本号 */
	public static final String CURR_INSIDE_VERSIONNAME = "204";// 2011.04.13-15:36
	/* 手机端数据存放文件夹路径 */
	public static final String PATH_SDCARD_DIR = "/sdcard/HwttAssistant";
	/* 备份数据存放文件夹路径 */
	public static final String PATH_BACKUP_SDCARD_DIR = "/sdcard/HwttAssistant/backup";
	/* 备份电话本XML文件名 */
	public static final String FILENAME_BACKUP_CONTACTS_SDCARD = "contacts.xml";
	/* 备份收件箱inbox XML文件名 */
	public static final String FILENAME_BACKUP_SMSINBOX_SDCARD = "inbox.xml";
	/* 备份发件箱sentbox XML文件名 */
	public static final String FILENAME_BACKUP_SMSSENTBOX_SDCARD = "sentbox.xml";
}
