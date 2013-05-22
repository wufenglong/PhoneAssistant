package com.hwttnet.mobileassistant.util;

public enum EnumAction {
	GET_INSTALLEDAPP_LIST, // 命令1： 获得手机已安装软件列表
	GET_DEVICEINFO_LIST, // 命令2：读取设备信息
	GET_BOOT_COMPLETED_LIST, // 命令3：读取开机启动信息
	GET_RUNNINGTASKS_INFO_LIST, // 命令4：正在运行的程序的信息
	GET_CONTACTS_INFO_LIST, // 命令5：获得电话本信息contacts
	GET_SMSINBOX_INFO_LIST, // 命令6：获得短信SMS收件箱Inbox
	GET_SMSSENTBOX_INFO_LIST, // 命令7：获得短信SMS发件箱sentbox
	UPDATE_CONTACTS, // 命令8：更新电话本
	UPDATE_SMSINBOX, // 命令9：更新收件箱
	UPDATE_SMSSENTBOX, // 命令10：更新已发送发件箱
	CLOSE_RUNNINGTASKS, // 命令11：关闭正在运行的程序
	CLEAN_TRASH, // 命令12：清理垃圾
	GET_SHUT_BOOTAPP_LIST, // 命令13：获得被关闭的开机启动程序列表
	UPDATE_SHUT_BOOTAPP, // 命令14：更新被关闭的开机启动程序列表 ShutAutoStart.txt
	CLEAN_SMSDRAFT, // 命令15：清理SMS草稿箱
	CLEAN_SMSINBOX, // 命令16：清理SMS收件箱
	CLEAN_SMSSENTBOX, // 命令17：清理SMS已发送发件箱
	CLEAN_SMSOUTBOX, // 命令18：清理SMS发件箱

	TEST, // 命令：测试连接
	TEST_SENTFILE, // 命令：测试发送文件
	EXIT,
	// 退出
}
