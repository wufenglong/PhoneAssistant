package com.hwttnet.mobileassistant.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.hwttnet.mobileassistant.service.MoblieAssistantService;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Contacts;
import android.util.Log;

/**
 * 功能：用于更新contacts,SMS
 * 
 * */
public class ThreadUpdateOperate implements Runnable {
	public static String TAG = "ThreadUpdateOperate";
	private Context context;
	/* 当前操作 */
	public static int currcommand = 0;
	/* 三个更新操作常量 */
	public static final int UPDATE_CONTACTS = 1;// 更新电话本
	public static final int UPDATE_SMSINBOX = 2;// 更新收件箱
	public static final int UPDATE_SMSSENTBOX = 3;// 更新已发送发件箱
	public static final int CLEAN_SMSDRAFT = 4;// 清理SMS草稿箱
	public static final int CLEAN_SMSINBOX = 5;// 清理SMS收件箱
	public static final int CLEAN_SMSSENTBOX = 6;// 清理SMS已发送发件箱
	public static final int CLEAN_SMSOUTBOX = 7;// 清理SMS发件箱
	private boolean bFlagCurrThreadRun = true;// 结束当前线程操作

	/* 读取PC文件路径 */
	public static final String READ_PC_FILE_PATHNAME = "/data/local/tmp";
	/* PC传来的电话本文件名 */
	public static final String UPDATE_CONTACTS_FILENAME = "contact.xml";
	/* PC传来的SMSInbox文件名 */
	public static final String UPDATE_SMSINBOX_FILENAME = "Inbox.xml";
	/* PC传来的SMSSentbox文件名 */
	public static final String UPDATE_SMSSENTBOX_FILENAME = "Sendbox.xml";

	// 构造
	ThreadUpdateOperate(Context context) {
		Log.v(TAG, "ThreadUpdateOperate(Context context)");
		this.context = context;
		bFlagCurrThreadRun = true;// 初始化线程运行
		bReadContactsXMLFileError = false;// 初始化读XML文件错误标记
		bReadSMSXmlError = false;// 初始化读XML文件错误标记
	}

	@Override
	public void run() {
		Log.v(TAG, "run() start...");
		switch (currcommand) {
		case UPDATE_CONTACTS:// 更新电话本
			updateContacts(context);
			break;
		case UPDATE_SMSINBOX:// 更新收件箱
			updateSMSInbox(context);
			break;
		case UPDATE_SMSSENTBOX:// 更新发件箱
			updateSMSSentbox(context);
			break;
		case CLEAN_SMSDRAFT:// 清理草稿箱
			ClearSMS(context, "3");
			break;
		case CLEAN_SMSINBOX:// 清理收件箱
			ClearSMS(context, "1");
			break;
		case CLEAN_SMSSENTBOX:// 清理已发送发件箱
			ClearSMS(context, "2");
			break;
		case CLEAN_SMSOUTBOX:// 清理SMS发件箱
			ClearSMS(context, "4");
			break;
		default:
			break;
		}
	}

	public void stopMe() {
		bFlagCurrThreadRun = false;
		Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
				+ "---->" + "===================> stopMe()");
	}

	/* 清理SMS（包括4个类型：草稿箱，收件箱，已发送发件箱，发件箱） */
	public void ClearSMS(Context context, String _typeSMS) {
		/* 查询 */
		Cursor cursor = context.getContentResolver().query(
				Uri.parse("content://sms"), null, null, null, null);
		int numcount = cursor.getCount();
		for (int i = 0; i < numcount; i++) {
			if (bFlagCurrThreadRun) {
				cursor.moveToPosition(i);
				String type = cursor.getString(cursor
						.getColumnIndexOrThrow("type"));
				if (type.equals(_typeSMS)) {
					String _id = cursor.getString(cursor
							.getColumnIndexOrThrow("_id"));// id
					String thread_id = cursor.getString(cursor
							.getColumnIndexOrThrow("thread_id"));
					context.getContentResolver().delete(
							Uri.parse("content://sms/conversations/"
									+ thread_id), "_id= " + _id, null);
				}
			}
		}
	}

	/*********************************************************************************************
	 * 
	 * 电话本contacts
	 * 
	 ******************************************************************************************/
	private boolean bReadContactsXMLFileError;// 读取Contacts XML文件错误

	/* contacts操作，增，删，改 */
	public void updateContacts(Context context) {
		if (bFlagCurrThreadRun) {
			bReadContactsXMLFileError = false;
			ArrayList<ContactUnit> contactlistFromXML = readContactsXMLFile(
					READ_PC_FILE_PATHNAME, UPDATE_CONTACTS_FILENAME);
			if (!bReadContactsXMLFileError && bFlagCurrThreadRun) {// 文件存在
				/* 删除手机中所有contacts */
				CMDExecute.deleteContactsItemInPhone(context);
				/* 插入所有XML中的contacts */
				// int Num_sdkVerSion = new
				// Integer(Build.VERSION.SDK).intValue();
				insertContactsItemInPhoneSDK(context, contactlistFromXML);
			}
		}
	}

	private ArrayList<ContactUnit> readContactsXMLFile(String path,
			String filename) {
		File inFile = new File(path, filename);
		if (!inFile.exists()) {
			/* 文件不存在，就不进行更新操作 */
			bReadContactsXMLFileError = true;
			return null;
		}
		try {
			inFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
		}
		Document doc = null;
		try {
			doc = db.parse(inFile);
		} catch (DOMException dom) {
			bReadContactsXMLFileError = true;
			return null;
		} catch (IOException ioe) {
			bReadContactsXMLFileError = true;
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			bReadContactsXMLFileError = true;
			return null;
		}

		ArrayList<ContactUnit> contactUnitList = new ArrayList<ContactUnit>();
		// 下面是解析XML的全过程，比较简单，先取根元素"contactlist"
		Element root = doc.getDocumentElement();
		// 取"contact"元素列表
		NodeList contactList = root.getElementsByTagName("contact");
		int contactnum = contactList.getLength();
		for (int indexContact = 0; indexContact < contactnum; indexContact++) {
			if (bFlagCurrThreadRun) {
				// 依次取每个"contact"元素
				Element contactelement = (Element) contactList
						.item(indexContact);
				if (contactelement.hasChildNodes()) {
					// 创建一个ContactUnit的实例
					ContactUnit contactUnit = new ContactUnit();
					/* 取"id"节点 */
					NodeList _idList = contactelement
							.getElementsByTagName("id");
					if (_idList.getLength() == 1) {
						Element e = (Element) _idList.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setPerson_id(t.getNodeValue());
						}
					}

					/* 取"name"节点 */
					NodeList _nameList = contactelement
							.getElementsByTagName("name");
					if (_nameList.getLength() == 1) {
						Element e = (Element) _nameList.item(0);
						if (e.hasChildNodes()) {
							Text t = (Text) e.getFirstChild();
							contactUnit.setName(t.getNodeValue());
						}
					}

					/* 取"phone"节点 */
					NodeList _phoneList = contactelement
							.getElementsByTagName("phone");
					int _phonenum = _phoneList.getLength();
					for (int indexphone = 0; indexphone < _phonenum; indexphone++) {
						// 依次取每个"phone"节点
						Element phoneElement = (Element) _phoneList
								.item(indexphone);
						// 取"home"元素，下面类同
						NodeList _homephone = phoneElement
								.getElementsByTagName("home");
						if (_homephone.getLength() == 1) {
							Element e = (Element) _homephone.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPhone_home(t.getNodeValue());
							}
						}
						// 取"mobile"元素，下面类同
						NodeList _mobilephone = phoneElement
								.getElementsByTagName("mobile");
						if (_mobilephone.getLength() == 1) {
							Element e = (Element) _mobilephone.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPhone_moblie(t.getNodeValue());
							}
						}
						// 取"work"元素，下面类同
						NodeList _workphone = phoneElement
								.getElementsByTagName("work");
						if (_workphone.getLength() == 1) {
							Element e = (Element) _workphone.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPhone_work(t.getNodeValue());
							}
						}
						// 取"fax_work"元素，下面类同
						NodeList _fax_workphone = phoneElement
								.getElementsByTagName("fax_work");
						if (_fax_workphone.getLength() == 1) {
							Element e = (Element) _fax_workphone.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPhone_Fax_work(t.getNodeValue());
							}
						}
						// 取"fax_home"元素，下面类同
						NodeList _fax_homephone = phoneElement
								.getElementsByTagName("fax_home");
						if (_fax_homephone.getLength() == 1) {
							Element e = (Element) _fax_homephone.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPhone_Fax_home(t.getNodeValue());
							}
						}
						// 取"pager"元素，下面类同
						NodeList _pagerphone = phoneElement
								.getElementsByTagName("pager");
						if (_pagerphone.getLength() == 1) {
							Element e = (Element) _pagerphone.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPhone_pager(t.getNodeValue());
							}
						}
						// 取"other"元素，下面类同
						NodeList _otherphone = phoneElement
								.getElementsByTagName("other");
						if (_otherphone.getLength() == 1) {
							Element e = (Element) _otherphone.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPhone_other(t.getNodeValue());
							}
						}
						// 取"custom"元素，下面类同
						NodeList _customphone = phoneElement
								.getElementsByTagName("custom");
						if (_customphone.getLength() == 1) {
							Element e = (Element) _customphone.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPhone_custom(t.getNodeValue());
							}
						}
						// 取"customlabel"元素，下面类同
						NodeList _customlabelphone = phoneElement
								.getElementsByTagName("customlabel");
						if (_customlabelphone.getLength() == 1) {
							Element e = (Element) _customlabelphone.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPhone_customlabel(t
										.getNodeValue());
							}
						}
					}

					/* 取"email"节点 */
					NodeList _emailList = contactelement
							.getElementsByTagName("email");
					int _emailnum = _emailList.getLength();
					for (int indexEmail = 0; indexEmail < _emailnum; indexEmail++) {
						// 依次取每个"email"节点
						Element emailElement = (Element) _emailList
								.item(indexEmail);
						// 取"home"元素，下面类同
						NodeList _homeEmail = emailElement
								.getElementsByTagName("home");
						if (_homeEmail.getLength() == 1) {
							Element e = (Element) _homeEmail.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setEmail_home(t.getNodeValue());
							}
						}
						// 取"work"元素，下面类同
						NodeList _workEmail = emailElement
								.getElementsByTagName("work");
						if (_workEmail.getLength() == 1) {
							Element e = (Element) _workEmail.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setEmail_work(t.getNodeValue());
							}
						}
						// 取"other"元素，下面类同
						NodeList _otherEmail = emailElement
								.getElementsByTagName("other");
						if (_otherEmail.getLength() == 1) {
							Element e = (Element) _otherEmail.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setEmail_other(t.getNodeValue());
							}
						}
						// 取"custom"元素，下面类同
						NodeList _customEmail = emailElement
								.getElementsByTagName("custom");
						if (_customEmail.getLength() == 1) {
							Element e = (Element) _customEmail.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setEmail_custom(t.getNodeValue());
							}
						}
						// 取"customlabel"元素，下面类同
						NodeList _customlabelEmail = emailElement
								.getElementsByTagName("customlabel");
						if (_customlabelEmail.getLength() == 1) {
							Element e = (Element) _customlabelEmail.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setEmail_customlabel(t
										.getNodeValue());
							}
						}
					}
					/* 取"im"节点 */
					// NodeList _imList =
					// contactelement.getElementsByTagName("im");
					// int _imnum = _imList.getLength();
					// for (int indexIM = 0; indexIM < _imnum; indexIM++) {
					// // 依次取每个"IM"节点
					// Element imElement = (Element) _imList.item(indexIM);
					// // 取"aim"元素，下面类同
					// NodeList _aimIM = imElement.getElementsByTagName("aim");
					// if (_aimIM.getLength() == 1) {
					// Element e = (Element) _aimIM.item(0);
					// Text t = (Text) e.getFirstChild();
					// contactUnit.setIm_aim(t.getNodeValue());
					// }
					// // 取"windowlive"元素，下面类同
					// NodeList _windowliveIM = imElement
					// .getElementsByTagName("windowlive");
					// if (_windowliveIM.getLength() == 1) {
					// Element e = (Element) _windowliveIM.item(0);
					// Text t = (Text) e.getFirstChild();
					// contactUnit.setIm_windowslive(t.getNodeValue());
					// }
					// // 取"yahoo"元素，下面类同
					// NodeList _yahooIM =
					// imElement.getElementsByTagName("yahoo");
					// if (_yahooIM.getLength() == 1) {
					// Element e = (Element) _yahooIM.item(0);
					// Text t = (Text) e.getFirstChild();
					// contactUnit.setIm_yahoo(t.getNodeValue());
					// }
					// // 取"skype"元素，下面类同
					// NodeList _skypeIM =
					// imElement.getElementsByTagName("skype");
					// if (_skypeIM.getLength() == 1) {
					// Element e = (Element) _skypeIM.item(0);
					// Text t = (Text) e.getFirstChild();
					// contactUnit.setIm_skepe(t.getNodeValue());
					// }
					// // 取"qq"元素，下面类同
					// NodeList _qqIM = imElement.getElementsByTagName("qq");
					// if (_qqIM.getLength() == 1) {
					// Element e = (Element) _qqIM.item(0);
					// Text t = (Text) e.getFirstChild();
					// contactUnit.setIm_qq(t.getNodeValue());
					// }
					// // 取"googletalk"元素，下面类同
					// NodeList _googletalkIM = imElement
					// .getElementsByTagName("googletalk");
					// if (_googletalkIM.getLength() == 1) {
					// Element e = (Element) _googletalkIM.item(0);
					// Text t = (Text) e.getFirstChild();
					// contactUnit.setIm_googletalk(t.getNodeValue());
					// }
					// // 取"icq"元素，下面类同
					// NodeList _icqIM = imElement.getElementsByTagName("icq");
					// if (_icqIM.getLength() == 1) {
					// Element e = (Element) _icqIM.item(0);
					// Text t = (Text) e.getFirstChild();
					// contactUnit.setIm_icq(t.getNodeValue());
					// }
					// // 取"jabber"元素，下面类同
					// NodeList _jabberIM =
					// imElement.getElementsByTagName("jabber");
					// if (_jabberIM.getLength() == 1) {
					// Element e = (Element) _jabberIM.item(0);
					// Text t = (Text) e.getFirstChild();
					// contactUnit.setIm_jabber(t.getNodeValue());
					// }
					// }
					/* 取"postal"节点 */
					NodeList _postalList = contactelement
							.getElementsByTagName("postal");
					int _postalnum = _postalList.getLength();
					for (int indexPostal = 0; indexPostal < _postalnum; indexPostal++) {
						// 依次取每个"postal"节点
						Element postalElement = (Element) _postalList
								.item(indexPostal);
						// 取"home"元素，下面类同
						NodeList _homePostal = postalElement
								.getElementsByTagName("home");
						if (_homePostal.getLength() == 1) {
							Element e = (Element) _homePostal.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPostal_home(t.getNodeValue());
							}
						}
						// 取"work"元素，下面类同
						NodeList _workPostal = postalElement
								.getElementsByTagName("work");
						if (_workPostal.getLength() == 1) {
							Element e = (Element) _workPostal.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPostal_work(t.getNodeValue());
							}
						}
						// 取"other"元素，下面类同
						NodeList _otherPostal = postalElement
								.getElementsByTagName("other");
						if (_otherPostal.getLength() == 1) {
							Element e = (Element) _otherPostal.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPostal_other(t.getNodeValue());
							}
						}
						// 取"custom"元素，下面类同
						NodeList _customPostal = postalElement
								.getElementsByTagName("custom");
						if (_customPostal.getLength() == 1) {
							Element e = (Element) _customPostal.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPostal_custom(t.getNodeValue());
							}
						}
						// 取"customlabel"元素，下面类同
						NodeList _customlabelPostal = postalElement
								.getElementsByTagName("customlabel");
						if (_customlabelPostal.getLength() == 1) {
							Element e = (Element) _customlabelPostal.item(0);
							if (e.hasChildNodes()) {
								Text t = (Text) e.getFirstChild();
								contactUnit.setPostal_customlabel(t
										.getNodeValue());
							}
						}
					}

					/* 取"organizations"节点 */
					NodeList _organizationsList = contactelement
							.getElementsByTagName("organizations");
					int _organizationsnum = _organizationsList.getLength();
					for (int indexOrganizations = 0; indexOrganizations < _organizationsnum; indexOrganizations++) {
						// 依次取每个"organizations"节点
						Element organizationsElement = (Element) _organizationsList
								.item(indexOrganizations);
						// 取"work"元素，下面类同
						NodeList _workOrganizationsList = organizationsElement
								.getElementsByTagName("work");
						int _workOrganizationsNum = _workOrganizationsList
								.getLength();
						for (int indexWorkOrganizations = 0; indexWorkOrganizations < _workOrganizationsNum; indexWorkOrganizations++) {
							// 依次取每个"organizations"节点中work子节点
							Element _workOrganizationsElement = (Element) _workOrganizationsList
									.item(indexWorkOrganizations);
							// 取"company"子元素，下面类同
							NodeList _companyWorkOrganizations = _workOrganizationsElement
									.getElementsByTagName("company");
							if (_companyWorkOrganizations.getLength() == 1) {
								Element e = (Element) _companyWorkOrganizations
										.item(0);
								if (e.hasChildNodes()) {
									Text t = (Text) e.getFirstChild();
									contactUnit.setOrganization_work_company(t
											.getNodeValue());
								}
							}
							// 取"title"子元素，下面类同
							NodeList _titleWorkOrganizations = _workOrganizationsElement
									.getElementsByTagName("title");
							if (_titleWorkOrganizations.getLength() == 1) {
								Element e = (Element) _titleWorkOrganizations
										.item(0);
								if (e.hasChildNodes()) {
									Text t = (Text) e.getFirstChild();
									contactUnit.setOrganization_work_title(t
											.getNodeValue());
								}
							}
						}

						// 取"other"元素，下面类同
						NodeList _otherOrganizationsList = organizationsElement
								.getElementsByTagName("other");
						int _otherOrganizationsNum = _otherOrganizationsList
								.getLength();
						for (int indexotherOrganizations = 0; indexotherOrganizations < _otherOrganizationsNum; indexotherOrganizations++) {
							// 依次取每个"organizations"节点中other子节点
							Element _otherOrganizationsElement = (Element) _otherOrganizationsList
									.item(indexotherOrganizations);
							// 取"company"子元素，下面类同
							NodeList _companyotherOrganizations = _otherOrganizationsElement
									.getElementsByTagName("company");
							if (_companyotherOrganizations.getLength() == 1) {
								Element e = (Element) _companyotherOrganizations
										.item(0);
								if (e.hasChildNodes()) {
									Text t = (Text) e.getFirstChild();
									contactUnit.setOrganization_other_company(t
											.getNodeValue());
								}
							}
							// 取"title"子元素，下面类同
							NodeList _titleotherOrganizations = _otherOrganizationsElement
									.getElementsByTagName("title");
							if (_titleotherOrganizations.getLength() == 1) {
								Element e = (Element) _titleotherOrganizations
										.item(0);
								if (e.hasChildNodes()) {
									Text t = (Text) e.getFirstChild();
									contactUnit.setOrganization_other_title(t
											.getNodeValue());
								}
							}
						}

						// 取"custom"元素，下面类同
						NodeList _customOrganizationsList = organizationsElement
								.getElementsByTagName("custom");
						int _customOrganizationsNum = _customOrganizationsList
								.getLength();
						for (int indexCustomOrganizations = 0; indexCustomOrganizations < _customOrganizationsNum; indexCustomOrganizations++) {
							// 依次取每个"organizations"节点中other子节点
							Element _customOrganizationsElement = (Element) _customOrganizationsList
									.item(indexCustomOrganizations);
							// 取"company"子元素，下面类同
							NodeList _companycustomOrganizations = _customOrganizationsElement
									.getElementsByTagName("company");
							if (_companycustomOrganizations.getLength() == 1) {
								Element e = (Element) _companycustomOrganizations
										.item(0);
								if (e.hasChildNodes()) {
									Text t = (Text) e.getFirstChild();
									contactUnit
											.setOrganization_custom_company(t
													.getNodeValue());
								}
							}
							// 取"title"子元素，下面类同
							NodeList _titlecustomOrganizations = _customOrganizationsElement
									.getElementsByTagName("title");
							if (_titlecustomOrganizations.getLength() == 1) {
								Element e = (Element) _titlecustomOrganizations
										.item(0);
								if (e.hasChildNodes()) {
									Text t = (Text) e.getFirstChild();
									contactUnit.setOrganization_custom_title(t
											.getNodeValue());
								}
							}
							// 取"customlabel"子元素，下面类同
							NodeList _customlabelOrganizations = _customOrganizationsElement
									.getElementsByTagName("customlabel");
							if (_customlabelOrganizations.getLength() == 1) {
								Element e = (Element) _customlabelOrganizations
										.item(0);
								if (e.hasChildNodes()) {
									Text t = (Text) e.getFirstChild();
									contactUnit.setOrganization_customlabel(t
											.getNodeValue());
								}
							}
						}
					}
					contactUnitList.add(contactUnit);
				}
			}
		}
		return contactUnitList;
	}

	/* 插入所有XML中的contacts */
	private void insertContactsItemInPhoneSDK(Context context,
			ArrayList<ContactUnit> _contactlistFromXML) {
		/* 取得ContentResolver */
		ContentResolver contentResolver = context.getContentResolver();
		/* 循环_contactlistFromXML插入内容 */
		int numContactlist = _contactlistFromXML.size();
		for (int indexContactUnit = 0; indexContactUnit < numContactlist; indexContactUnit++) {
			if (bFlagCurrThreadRun) {
				/* 获得一个ContactUnit对象 */
				ContactUnit _ContactUnit = _contactlistFromXML
						.get(indexContactUnit);

				ContentValues values = new ContentValues();
				Uri phoneUri = null;
				String _name = _ContactUnit.getName();
				/* 添加姓名 */
				values.put(Contacts.People.NAME, _name);
				Uri uriname = contentResolver.insert(
						Contacts.People.CONTENT_URI, values);
				Log.v(MoblieAssistantService.TAG, Thread.currentThread()
						.getName()
						+ "---->" + "uriname=" + uriname);
				String _personId = uriname.toString().substring(26);
				Log.v(MoblieAssistantService.TAG, Thread.currentThread()
						.getName()
						+ "---->" + "_personId=" + _personId);
				// 映射关系:1 = 新的联系方式加入 favorites,0 = 新的联系方式不是加入 favorites
				// values.put(Contacts.People.STARRED, 0);
				// Uri uri = Contacts.People.createPersonInMyContactsGroup(
				// contentResolver, values);
				// Log.v(MoblieAssistantService.TAG, Thread.currentThread()
				// .getName()
				// + "---->" + "insert NAME");
				/* 计算personId:从people表里查出此姓名最大的一个personId */
				/* 取得通讯录的People表的cursor */
				// Cursor cursorPeopleContact = contentResolver.query(
				// Contacts.People.CONTENT_URI, null, Contacts.People.NAME
				// + "=?", new String[] { _name },
				// Contacts.People.DEFAULT_SORT_ORDER);
				// int numname = cursorPeopleContact.getCount();
				// /* 从同名的列表中找到ID最大的 */
				// String _personId = null;
				// int maxId = 0;
				// for (int i = 0; i < numname; i++) {
				// cursorPeopleContact.moveToPosition(i);
				// String tmppersonId = cursorPeopleContact
				// .getString(cursorPeopleContact
				// .getColumnIndexOrThrow(Contacts.People._ID));
				// if (Integer.parseInt(tmppersonId) > maxId) {
				// _personId = tmppersonId;
				// maxId = Integer.parseInt(tmppersonId);
				// }
				// }
				// Log.v(TAG, Thread.currentThread().getName() + "---->"
				// + "insert _personId=" + _personId);
				/* 添加电话号码 */
				// 最好的办法是先得到People表的Uri，然后使用Uri的静态方法withAppendedPath来获取一个新的Uri作为我们新要插入数据的Uri
				phoneUri = Uri.withAppendedPath(uriname,
						Contacts.People.Phones.CONTENT_DIRECTORY);
				// Log.v(MoblieAssistantService.TAG, Thread.currentThread()
				// .getName()
				// + "---->" + "phoneUri=" + phoneUri);
				// phoneUri = Contacts.Phones.CONTENT_URI;
				String numberPhoneMobile = _ContactUnit.getPhone_moblie();
				String numberPhoneHome = _ContactUnit.getPhone_home();
				String numberPhoneWork = _ContactUnit.getPhone_work();
				String numberPhoneFax_work = _ContactUnit.getPhone_Fax_work();
				String numberPhoneFax_home = _ContactUnit.getPhone_Fax_home();
				String numberPhonePager = _ContactUnit.getPhone_pager();
				String numberPhoneOther = _ContactUnit.getPhone_other();
				String numberPhoneCustom = _ContactUnit.getPhone_custom();
				String numberPhoneCustomLabel = _ContactUnit
						.getPhone_customlabel();

				if (numberPhoneHome != null
						&& !numberPhoneHome.trim().equals("")) {// home
					values.clear();
					// values.put(Contacts.Phones.PERSON_ID, _personId);
					values.put(Contacts.Phones.TYPE, Contacts.Phones.TYPE_HOME);
					values.put(Contacts.Phones.NUMBER, numberPhoneHome);
					contentResolver.insert(phoneUri, values);
				}
				if (numberPhoneMobile != null
						&& !numberPhoneMobile.trim().equals("")) {// mobile
					values.clear();
					// values.put(Contacts.Phones.PERSON_ID, _personId);
					values.put(Contacts.Phones.TYPE,
							Contacts.Phones.TYPE_MOBILE);
					values.put(Contacts.Phones.NUMBER, numberPhoneMobile);
					contentResolver.insert(phoneUri, values);
				}
				if (numberPhoneWork != null
						&& !numberPhoneWork.trim().equals("")) {// work
					values.clear();
					// values.put(Contacts.Phones.PERSON_ID, _personId);
					values.put(Contacts.Phones.TYPE, Contacts.Phones.TYPE_WORK);
					values.put(Contacts.Phones.NUMBER, numberPhoneWork);
					contentResolver.insert(phoneUri, values);
				}
				if (numberPhoneFax_work != null
						&& !numberPhoneFax_work.trim().equals("")) {// Fax_work
					values.clear();
					// values.put(Contacts.Phones.PERSON_ID, _personId);
					values.put(Contacts.Phones.TYPE,
							Contacts.Phones.TYPE_FAX_WORK);
					values.put(Contacts.Phones.NUMBER, numberPhoneFax_work);
					contentResolver.insert(phoneUri, values);
				}
				if (numberPhoneFax_home != null
						&& !numberPhoneFax_home.trim().equals("")) {// Fax_home
					values.clear();
					// values.put(Contacts.Phones.PERSON_ID, _personId);
					values.put(Contacts.Phones.TYPE,
							Contacts.Phones.TYPE_FAX_HOME);
					values.put(Contacts.Phones.NUMBER, numberPhoneFax_home);
					contentResolver.insert(phoneUri, values);
				}
				if (numberPhonePager != null
						&& !numberPhonePager.trim().equals("")) {// Pager
					values.clear();
					// values.put(Contacts.Phones.PERSON_ID, _personId);
					values
							.put(Contacts.Phones.TYPE,
									Contacts.Phones.TYPE_PAGER);
					values.put(Contacts.Phones.NUMBER, numberPhonePager);
					contentResolver.insert(phoneUri, values);
				}
				if (numberPhoneOther != null
						&& !numberPhoneOther.trim().equals("")) {// Other
					values.clear();
					// values.put(Contacts.Phones.PERSON_ID, _personId);
					values
							.put(Contacts.Phones.TYPE,
									Contacts.Phones.TYPE_OTHER);
					values.put(Contacts.Phones.NUMBER, numberPhoneOther);
					contentResolver.insert(phoneUri, values);
				}
				if (numberPhoneCustom != null
						&& !numberPhoneCustom.trim().equals("")) {// Custom
					values.clear();
					// values.put(Contacts.Phones.PERSON_ID, _personId);
					values.put(Contacts.Phones.TYPE,
							Contacts.Phones.TYPE_CUSTOM);
					values.put(Contacts.Phones.LABEL, numberPhoneCustomLabel);
					values.put(Contacts.Phones.NUMBER, numberPhoneCustom);
					contentResolver.insert(phoneUri, values);
				}

				/* 添加Email */
				Uri emailUri = Uri.withAppendedPath(uriname,
						Contacts.People.ContactMethods.CONTENT_DIRECTORY);
				// Uri emailUri = Contacts.ContactMethods.CONTENT_URI;
				String homeEmail = _ContactUnit.getEmail_home();
				String workEmail = _ContactUnit.getEmail_work();
				String otherEmail = _ContactUnit.getEmail_other();
				String customEmail = _ContactUnit.getEmail_custom();
				String customEmailLabel = _ContactUnit.getEmail_customlabel();

				Log.v(MoblieAssistantService.TAG, Thread.currentThread()
						.getName()
						+ "---->" + "insert email");
				if (homeEmail != null && !homeEmail.trim().equals("")) {// email
					// home
					values.clear();
					// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
					// ContactMethods.KIND 是用来区分像email,im等等不同联系方式
					values.put(Contacts.ContactMethods.KIND,
							Contacts.KIND_EMAIL);
					values.put(Contacts.ContactMethods.DATA, homeEmail);
					values.put(Contacts.ContactMethods.TYPE,
							Contacts.ContactMethods.TYPE_HOME);
					contentResolver.insert(emailUri, values);
				}
				if (workEmail != null && !workEmail.trim().equals("")) {// email
					// work
					values.clear();
					// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
					// ContactMethods.KIND 是用来区分像email,im等等不同联系方式
					values.put(Contacts.ContactMethods.KIND,
							Contacts.KIND_EMAIL);
					values.put(Contacts.ContactMethods.DATA, workEmail);
					values.put(Contacts.ContactMethods.TYPE,
							Contacts.ContactMethods.TYPE_WORK);
					contentResolver.insert(emailUri, values);
				}
				if (otherEmail != null && !otherEmail.trim().equals("")) {// email
					// other
					values.clear();
					// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
					// ContactMethods.KIND 是用来区分像email,im等等不同联系方式
					values.put(Contacts.ContactMethods.KIND,
							Contacts.KIND_EMAIL);
					values.put(Contacts.ContactMethods.DATA, otherEmail);
					values.put(Contacts.ContactMethods.TYPE,
							Contacts.ContactMethods.TYPE_OTHER);
					contentResolver.insert(emailUri, values);
				}
				if (customEmail != null && !customEmail.trim().equals("")) {// email
					// custom
					values.clear();
					// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
					// ContactMethods.KIND 是用来区分像email,im等等不同联系方式
					values.put(Contacts.ContactMethods.KIND,
							Contacts.KIND_EMAIL);
					values.put(Contacts.ContactMethods.DATA, customEmail);
					values.put(Contacts.ContactMethods.LABEL, customEmailLabel);
					values.put(Contacts.ContactMethods.TYPE,
							Contacts.ContactMethods.TYPE_CUSTOM);
					contentResolver.insert(emailUri, values);
				}

				/* 添加IM */
				// Uri imUri = Uri.withAppendedPath(uri,
				// Contacts.People.ContactMethods.CONTENT_DIRECTORY);
				// String aimIM = _ContactUnit.getIm_aim();
				// String windowliveIM = _ContactUnit.getIm_windowslive();
				// String yahooIM = _ContactUnit.getIm_yahoo();
				// String skypeIM = _ContactUnit.getIm_skepe();
				// String qqIM = _ContactUnit.getIm_qq();
				// String googletalkIM = _ContactUnit.getIm_googletalk();
				// String icqIM = _ContactUnit.getIm_icq();
				// String jabberIM = _ContactUnit.getIm_jabber();
				//
				// if (aimIM != null && !aimIM.trim().equals("")) {// im aim
				// values.clear();
				// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
				// values.put(Contacts.ContactMethods.DATA, aimIM);
				// values.put(Contacts.ContactMethods.TYPE,
				// Contacts.ContactMethods.PROTOCOL_AIM);
				// values.put(Contacts.ContactMethods.AUX_DATA, "pre:0");
				// contentResolver.insert(imUri, values);
				// }
				// if (windowliveIM != null && !windowliveIM.trim().equals(""))
				// {//
				// im
				// // msn
				// values.clear();
				// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
				// values.put(Contacts.ContactMethods.DATA, windowliveIM);
				// values.put(Contacts.ContactMethods.TYPE,
				// Contacts.ContactMethods.PROTOCOL_MSN);
				// values.put(Contacts.ContactMethods.AUX_DATA, "pre:1");
				// contentResolver.insert(imUri, values);
				// }
				// if (yahooIM != null && !yahooIM.trim().equals("")) {// im
				// yahoo
				// values.clear();
				// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
				// values.put(Contacts.ContactMethods.DATA, yahooIM);
				// values.put(Contacts.ContactMethods.TYPE,
				// Contacts.ContactMethods.PROTOCOL_YAHOO);
				// values.put(Contacts.ContactMethods.AUX_DATA, "pre:2");
				// contentResolver.insert(imUri, values);
				// }
				// if (skypeIM != null && !skypeIM.trim().equals("")) {// im
				// skype
				// values.clear();
				// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
				// values.put(Contacts.ContactMethods.DATA, skypeIM);
				// values.put(Contacts.ContactMethods.TYPE,
				// Contacts.ContactMethods.PROTOCOL_SKYPE);
				// values.put(Contacts.ContactMethods.AUX_DATA, "pre:3");
				// contentResolver.insert(imUri, values);
				// }
				// if (qqIM != null && !qqIM.trim().equals("")) {// im qq
				// values.clear();
				// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
				// values.put(Contacts.ContactMethods.DATA, qqIM);
				// values.put(Contacts.ContactMethods.TYPE,
				// Contacts.ContactMethods.PROTOCOL_QQ);
				// values.put(Contacts.ContactMethods.AUX_DATA, "pre:4");
				// contentResolver.insert(imUri, values);
				// }
				// if (googletalkIM != null && !googletalkIM.trim().equals(""))
				// {//
				// im
				// // googletalk
				// values.clear();
				// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
				// values.put(Contacts.ContactMethods.DATA, googletalkIM);
				// values.put(Contacts.ContactMethods.TYPE,
				// Contacts.ContactMethods.PROTOCOL_GOOGLE_TALK);
				// values.put(Contacts.ContactMethods.AUX_DATA, "pre:5");
				// contentResolver.insert(imUri, values);
				// }
				// if (icqIM != null && !icqIM.trim().equals("")) {// im icq
				// values.clear();
				// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
				// values.put(Contacts.ContactMethods.DATA, icqIM);
				// values.put(Contacts.ContactMethods.TYPE,
				// Contacts.ContactMethods.PROTOCOL_ICQ);
				// values.put(Contacts.ContactMethods.AUX_DATA, "pre:6");
				// contentResolver.insert(imUri, values);
				// }
				// if (jabberIM != null && !jabberIM.trim().equals("")) {// im
				// jabber
				// values.clear();
				// values.put(Contacts.ContactMethods.KIND, Contacts.KIND_IM);
				// values.put(Contacts.ContactMethods.DATA, jabberIM);
				// values.put(Contacts.ContactMethods.TYPE,
				// Contacts.ContactMethods.PROTOCOL_JABBER);
				// values.put(Contacts.ContactMethods.AUX_DATA, "pre:7");
				// contentResolver.insert(imUri, values);
				// }

				/* 添加postal地址 */
				Uri PostalUri = Uri.withAppendedPath(uriname,
						Contacts.People.ContactMethods.CONTENT_DIRECTORY);

				// Uri PostalUri = Contacts.ContactMethods.CONTENT_URI;
				String homePostal = _ContactUnit.getPostal_home();
				String workPostal = _ContactUnit.getPostal_work();
				String otherPostal = _ContactUnit.getPostal_other();
				String customPostal = _ContactUnit.getPostal_custom();
				String customlabelPostal = _ContactUnit.getPostal_customlabel();

				if (homePostal != null && !homePostal.trim().equals("")) {// postal
					// home
					values.clear();
					// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
					values.put(Contacts.ContactMethods.KIND,
							Contacts.KIND_POSTAL);
					values.put(Contacts.ContactMethods.DATA, homePostal);
					values.put(Contacts.ContactMethods.TYPE,
							Contacts.ContactMethods.TYPE_HOME);
					contentResolver.insert(PostalUri, values);
				}
				if (workPostal != null && !workPostal.trim().equals("")) {// postal
					// work
					values.clear();
					// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
					values.put(Contacts.ContactMethods.KIND,
							Contacts.KIND_POSTAL);
					values.put(Contacts.ContactMethods.DATA, workPostal);
					values.put(Contacts.ContactMethods.TYPE,
							Contacts.ContactMethods.TYPE_WORK);
					contentResolver.insert(PostalUri, values);
				}
				if (otherPostal != null && !otherPostal.trim().equals("")) {// postal
					// other
					values.clear();
					// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
					values.put(Contacts.ContactMethods.KIND,
							Contacts.KIND_POSTAL);
					values.put(Contacts.ContactMethods.DATA, otherPostal);
					values.put(Contacts.ContactMethods.TYPE,
							Contacts.ContactMethods.TYPE_OTHER);
					contentResolver.insert(PostalUri, values);
				}
				if (customPostal != null && !customPostal.trim().equals("")) {// postal
					// custom
					values.clear();
					// values.put(Contacts.ContactMethods.PERSON_ID, _personId);
					values.put(Contacts.ContactMethods.KIND,
							Contacts.KIND_POSTAL);
					values.put(Contacts.ContactMethods.DATA, customPostal);
					values.put(Contacts.ContactMethods.TYPE,
							Contacts.ContactMethods.TYPE_CUSTOM);
					values
							.put(Contacts.ContactMethods.LABEL,
									customlabelPostal);
					contentResolver.insert(PostalUri, values);
				}

				/* 添加organizations地址 */
				// Uri OrganizationsUri = Uri.withAppendedPath(uriname,
				// Contacts.Organizations.CONTENT_DIRECTORY);

				Uri OrganizationsUri = Contacts.Organizations.CONTENT_URI;
				String companyWorkOrganizations = _ContactUnit
						.getOrganization_work_company();
				String titleWorkOrganizations = _ContactUnit
						.getOrganization_work_title();
				String companyOtherOrganizations = _ContactUnit
						.getOrganization_other_company();
				String titleOtherOrganizations = _ContactUnit
						.getOrganization_other_title();
				String companyCustomOrganizations = _ContactUnit
						.getOrganization_custom_company();
				String titleCustomOrganizations = _ContactUnit
						.getOrganization_custom_title();
				String customlabelCustomOrganizations = _ContactUnit
						.getOrganization_customlabel();

				int numNull = 0;// 公司和职位不能都为空。为空++
				if (companyWorkOrganizations != null
						&& !companyWorkOrganizations.trim().equals("")) {// Organizations
					values.clear();
					// work
					values.put(Contacts.Organizations.PERSON_ID, _personId);
					values.put(Contacts.Organizations.COMPANY,
							companyWorkOrganizations);
					values.put(Contacts.Organizations.TYPE,
							Contacts.Organizations.TYPE_WORK);
				} else {
					values.clear();
					values.put(Contacts.Organizations.PERSON_ID, _personId);
					values.put(Contacts.Organizations.COMPANY, " ");
					values.put(Contacts.Organizations.TYPE,
							Contacts.Organizations.TYPE_WORK);
					numNull++;
				}
				if (titleWorkOrganizations != null
						&& !titleWorkOrganizations.trim().equals("")) {// Organizations
					// work
					values.put(Contacts.Organizations.TITLE,
							titleWorkOrganizations);
					contentResolver.insert(OrganizationsUri, values);
				} else {// 在有些手机中"职位"为空的话，不显示公司属性
					numNull++;
					if (numNull != 2) {
						values.put(Contacts.Organizations.TITLE, " ");
						contentResolver.insert(OrganizationsUri, values);
					}
				}
				numNull = 0;
				if (companyOtherOrganizations != null
						&& !companyOtherOrganizations.trim().equals("")) {// Organizations
					// Other
					values.clear();
					values.put(Contacts.Organizations.PERSON_ID, _personId);
					values.put(Contacts.Organizations.COMPANY,
							companyOtherOrganizations);
					values.put(Contacts.Organizations.TYPE,
							Contacts.Organizations.TYPE_OTHER);
				} else {
					values.clear();
					values.put(Contacts.Organizations.PERSON_ID, _personId);
					values.put(Contacts.Organizations.COMPANY,
							companyOtherOrganizations);
					values.put(Contacts.Organizations.TYPE,
							Contacts.Organizations.TYPE_OTHER);
					numNull++;
				}
				if (titleOtherOrganizations != null
						&& !titleOtherOrganizations.trim().equals("")) {// Organizations
					// Other
					values.put(Contacts.Organizations.TITLE,
							titleOtherOrganizations);
					contentResolver.insert(OrganizationsUri, values);
				} else {
					numNull++;
					if (numNull != 2) {
						values.put(Contacts.Organizations.TITLE, " ");
						contentResolver.insert(OrganizationsUri, values);
					}
				}
				numNull = 0;
				if (companyCustomOrganizations != null
						&& !companyCustomOrganizations.trim().equals("")) {// Organizations
					// Custom
					values.clear();
					values.put(Contacts.Organizations.PERSON_ID, _personId);
					values.put(Contacts.Organizations.COMPANY,
							companyCustomOrganizations);
					values.put(Contacts.Organizations.LABEL,
							customlabelCustomOrganizations);
					values.put(Contacts.Organizations.TYPE,
							Contacts.Organizations.TYPE_CUSTOM);
				} else {
					values.clear();
					values.put(Contacts.Organizations.PERSON_ID, _personId);
					values.put(Contacts.Organizations.COMPANY, " ");
					values.put(Contacts.Organizations.LABEL,
							customlabelCustomOrganizations);
					values.put(Contacts.Organizations.TYPE,
							Contacts.Organizations.TYPE_CUSTOM);
					numNull++;
				}

				if (titleCustomOrganizations != null
						&& !titleCustomOrganizations.trim().equals("")) {// Organizations
					// Custom
					values.put(Contacts.Organizations.TITLE,
							titleCustomOrganizations);
					contentResolver.insert(OrganizationsUri, values);
				} else {
					numNull++;
					if (numNull != 2) {
						values.put(Contacts.Organizations.TITLE, " ");
						contentResolver.insert(OrganizationsUri, values);
					}
				}
			} else {
				return;
			}
		}

	}

	/*********************************************************************************************
	 * 
	 * 短信SMS
	 * 
	 ******************************************************************************************/
	/* 读取SMS XML文件错误 */
	private boolean bReadSMSXmlError = false;

	/* 更新Inbox收件箱 */
	public void updateSMSInbox(Context context) {
		bReadSMSXmlError = false;
		ArrayList<SMSUnit> smslistFromXML = readSMSXMLFile(
				READ_PC_FILE_PATHNAME, UPDATE_SMSINBOX_FILENAME);
		if (!bReadSMSXmlError && bFlagCurrThreadRun) {// 文件存在
			// Uri uriSMSInbox = Uri.parse("content://sms/inbox");
			// ArrayList<SMSUnit> smslistFromPhone =
			// Get_SMSInfo_returnArrayList(
			// context, uriSMSInbox);
			/* 先删除所有短信 */
			ClearSMS(context, "1");
			// int numSMSInPhone = smslistFromPhone.size();
			// // Log.v(MoblieAssistantService.TAG, "numSMSInPhone=" +
			// // numSMSInPhone);
			// Log.v(MoblieAssistantService.TAG,
			// Thread.currentThread().getName()
			// + "---->" + "******deleteSMSItemInPhone start*******");
			// for (int indxeDelSMS = 0; indxeDelSMS < numSMSInPhone;
			// indxeDelSMS++) {
			// if (bFlagCurrThreadRun) {
			//					
			// //deleteSMSItemInPhone(smslistFromPhone.get(indxeDelSMS));
			// }
			// }
			// Log.v(MoblieAssistantService.TAG,
			// Thread.currentThread().getName()
			// + "---->" + "******deleteSMSItemInPhone end*******");
			/* 插入所有XML文件中的SMS */
			int numSMSInXML = smslistFromXML.size();
			// Log.v(MoblieAssistantService.TAG, "numSMSInXML=" + numSMSInXML);
			/* 循环插入每一条SMS */
			for (int indexInsertSMS = 0; indexInsertSMS < numSMSInXML; indexInsertSMS++) {
				if (bFlagCurrThreadRun) {
					CMDExecute.insertSMSItemInPhone(context, smslistFromXML
							.get(indexInsertSMS));
				} else {
					return;
				}
			}
		}
	}

	/* 获得短信SMS信息，返回ArrayList<SMSUnit> */
	private ArrayList<SMSUnit> Get_SMSInfo_returnArrayList(Context context,
			Uri uriSMS) {

		/* 查询收件箱 */
		Cursor cursor = context.getContentResolver().query(uriSMS, null, null,
				null, null);

		/* 返回字符串 */

		ArrayList<SMSUnit> _smsUnitList = null;
		_smsUnitList = new ArrayList<SMSUnit>();

		int numcount = cursor.getCount();

		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "sms in phone numcount	=" + numcount);

		for (int i = 0; i < numcount; i++) {
			SMSUnit smsunit = new SMSUnit();

			cursor.moveToPosition(i);
			String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));// id
			// String thread_id = cursor.getString(cursor
			// .getColumnIndexOrThrow("thread_id"));
			// String address = cursor.getString(cursor
			// .getColumnIndexOrThrow("address"));// 电话号
			// String date = cursor
			// .getString(cursor.getColumnIndexOrThrow("date"));
			// String read = cursor
			// .getString(cursor.getColumnIndexOrThrow("read"));
			// String status = cursor.getString(cursor
			// .getColumnIndexOrThrow("status"));
			// String type = cursor
			// .getString(cursor.getColumnIndexOrThrow("type"));
			// String body = cursor
			// .getString(cursor.getColumnIndexOrThrow("body"));// 短信内容
			/**/
			smsunit.set_id(_id);
			// smsunit.setThread_id(thread_id);
			// smsunit.setAddress(address);
			// smsunit.setDate(date);
			// smsunit.setRead(read);
			// smsunit.setStatus(status);
			// smsunit.setType(type);
			// smsunit.setBody(body);

			_smsUnitList.add(smsunit);

			// Log.v(UpdateContactsSMS.TAG, Thread.currentThread().getName() +
			// "---->"
			// + "sms in phone _id	=" + _id);
			// Log.v(UpdateContactsSMS.TAG, Thread.currentThread().getName() +
			// "---->"
			// + "sms in phone address	=" + address);
			// Log.v(UpdateContactsSMS.TAG, Thread.currentThread().getName() +
			// "---->"
			// + "sms in phone body	=" + body);

		}
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "smsUnitList.size	=" + _smsUnitList.size());
		return _smsUnitList;
	}

	/* 从XML文件读取SMS数据 */
	private ArrayList<SMSUnit> readSMSXMLFile(String path, String filename) {
		File inFile = new File(path, filename);
		if (!inFile.exists()) {
			Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
					+ "---->" + "****file is no exists");
			/* 文件不存在，就不进行更新操作 */
			bReadSMSXmlError = true;
			return null;
		}
		try {
			inFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			// System.err.println(pce); // 出异常时输出异常信息，然后退出，下同
			// System.exit(1);
		}
		Document doc = null;
		try {
			doc = db.parse(inFile);
		} catch (DOMException dom) {
			bReadSMSXmlError = true;
			return null;
		} catch (IOException ioe) {
			bReadSMSXmlError = true;
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			bReadSMSXmlError = true;
			return null;
		}
		ArrayList<SMSUnit> smsUnitList = new ArrayList<SMSUnit>();
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "******readSMSXMLFile 1******");
		// 下面是解析XML的全过程，比较简单，先取根元素"smslist"
		Element root = doc.getDocumentElement();
		// 取"SMS"元素列表
		NodeList smsList = root.getElementsByTagName("sms");

		int _smsnumInXML = smsList.getLength();
		for (int i = 0; i < _smsnumInXML; i++) {
			// 依次取每个"sms"元素
			Element smselement = (Element) smsList.item(i);
			// 创建一个SMSUnit的实例
			SMSUnit smsunit = new SMSUnit();
			// 取"_id"元素，下面类同
			NodeList _idList = smselement.getElementsByTagName("_id");
			if (_idList.getLength() == 1) {
				Element e = (Element) _idList.item(0);
				Text t = (Text) e.getFirstChild();
				smsunit.set_id(t.getNodeValue());
				// Log.v(Service139.TAG, Thread.currentThread().getName()
				// + "---->" + "******readSMSXMLFile smsunit id ="
				// + t.getNodeValue());
			}

			// NodeList thread_idList = smselement
			// .getElementsByTagName("thread_id");
			// if (thread_idList.getLength() == 1) {
			// Element e = (Element) thread_idList.item(0);
			// Text t = (Text) e.getFirstChild();
			// smsunit.setThread_id(t.getNodeValue());
			// }

			NodeList addressList = smselement.getElementsByTagName("address");
			if (addressList.getLength() == 1) {
				Element e = (Element) addressList.item(0);
				Text t = (Text) e.getFirstChild();
				smsunit.setAddress(t.getNodeValue());
				// Log.v(Service139.TAG, Thread.currentThread().getName()
				// + "---->" + "******readSMSXMLFile smsunit Address ="
				// + t.getNodeValue());
			}

			NodeList dateList = smselement.getElementsByTagName("date");
			if (dateList.getLength() == 1) {
				Element e = (Element) dateList.item(0);
				Text t = (Text) e.getFirstChild();
				smsunit.setDate(t.getNodeValue());
				// Log.v(Service139.TAG, Thread.currentThread().getName()
				// + "---->" + "******readSMSXMLFile smsunit date ="
				// + t.getNodeValue());
			}

			// NodeList readList = smselement.getElementsByTagName("read");
			// if (readList.getLength() == 1) {
			// Element e = (Element) readList.item(0);
			// Text t = (Text) e.getFirstChild();
			// smsunit.setRead(t.getNodeValue());
			// }
			//
			// NodeList statusList = smselement.getElementsByTagName("status");
			// if (statusList.getLength() == 1) {
			// Element e = (Element) statusList.item(0);
			// Text t = (Text) e.getFirstChild();
			// smsunit.setStatus(t.getNodeValue());
			// }

			NodeList typeList = smselement.getElementsByTagName("type");
			if (typeList.getLength() == 1) {
				Element e = (Element) typeList.item(0);
				Text t = (Text) e.getFirstChild();
				smsunit.setType(t.getNodeValue());
				// Log.v(Service139.TAG, Thread.currentThread().getName()
				// + "---->" + "******readSMSXMLFile smsunit type ="
				// + t.getNodeValue());
			}

			NodeList bodyList = smselement.getElementsByTagName("body");
			if (bodyList.getLength() == 1) {
				Element e = (Element) bodyList.item(0);
				Text t = (Text) e.getFirstChild();
				smsunit.setBody(t.getNodeValue());
				// Log.v(Service139.TAG, Thread.currentThread().getName()
				// + "---->" + "******readSMSXMLFile smsunit body ="
				// + t.getNodeValue());
			}

			smsUnitList.add(smsunit);
			// Log.v(Service139.TAG, Thread.currentThread().getName() + "---->"
			// + "******readSMSXMLFile smsUnitList.add(smsunit) ");
		}
		// Log.v(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "readSMSXMLFile smsUnitList.size="
		// + smsUnitList.size());
		return smsUnitList;
	}

	public void updateSMSSentbox(Context context) {
		bReadSMSXmlError = false;
		ArrayList<SMSUnit> smslistFromXML = readSMSXMLFile(
				READ_PC_FILE_PATHNAME, UPDATE_SMSSENTBOX_FILENAME);
		if (!bReadSMSXmlError && bFlagCurrThreadRun) {// 文件存在
			// Uri uriSMSSentbox = Uri.parse("content://sms/sent");
			// ArrayList<SMSUnit> smslistFromPhone =
			// Get_SMSInfo_returnArrayList(
			// context, uriSMSSentbox);
			/* 先删除所有短信 */
			ClearSMS(context, "2");
			// int numSMSInPhone = smslistFromPhone.size();
			// // Log.v(MoblieAssistantService.TAG, "numSMSInPhone=" +
			// // numSMSInPhone);
			// Log.v(MoblieAssistantService.TAG,
			// Thread.currentThread().getName()
			// + "---->" + "******deleteSMSItemInPhone start*******");
			// for (int indxeDelSMS = 0; indxeDelSMS < numSMSInPhone;
			// indxeDelSMS++) {
			// if (bFlagCurrThreadRun) {
			// deleteSMSItemInPhone(smslistFromPhone.get(indxeDelSMS));
			// }
			// }
			// Log.v(MoblieAssistantService.TAG,
			// Thread.currentThread().getName()
			// + "---->" + "******deleteSMSItemInPhone end*******");
			/* 插入所有XML文件中的SMS */
			int numSMSInXML = smslistFromXML.size();
			// Log.v(MoblieAssistantService.TAG, "numSMSInXML=" + numSMSInXML);
			/* 循环插入每一条SMS */
			for (int indexInsertSMS = 0; indexInsertSMS < numSMSInXML; indexInsertSMS++) {
				if (bFlagCurrThreadRun) {
					CMDExecute.insertSMSItemInPhone(context, smslistFromXML
							.get(indexInsertSMS));
				} else {
					return;
				}
			}
		}
	}

}
