package com.hwttnet.mobileassistant.util;

import java.io.StringWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import com.hwttnet.mobileassistant.service.MoblieAssistantService;

import android.util.Log;
import android.util.Xml;

public class XmlUtil {
	/** Contacts中 生成XML，返回字符串 */
	public static String CreateContactsXMLToString(
			ArrayList<ContactUnit> _contactsUnitList) {
		// Log.d(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "CreateContactsXMLToString() start..");

		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();

		try {
			serializer.setOutput(writer);

			// <?xml version="1.0″ encoding="UTF-8″ standalone="yes"?>
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "contactslist");

			/* 循环填加contact */
			for (int i = 0; i < _contactsUnitList.size(); i++) {
				serializer.startTag("", "contact");// Node start
				/* id */
				String _id = _contactsUnitList.get(i).getPerson_id();
				if (_id != null) {
					serializer.startTag("", "id");
					serializer.cdsect(_id);
					serializer.endTag("", "id");
				}

				/* name */
				String _name = _contactsUnitList.get(i).getName();
				if (_name != null) {
					serializer.startTag("", "name");
					serializer.cdsect(_name);
					serializer.endTag("", "name");
				}

				/* phone */
				String _phonehome = _contactsUnitList.get(i).getPhone_home();
				String _phonemobile = _contactsUnitList.get(i)
						.getPhone_moblie();
				String _phonework = _contactsUnitList.get(i).getPhone_work();
				String _phonefax_work = _contactsUnitList.get(i)
						.getPhone_Fax_work();
				String _phonefax_home = _contactsUnitList.get(i)
						.getPhone_Fax_home();
				String _phonepager = _contactsUnitList.get(i).getPhone_pager();
				String _phoneother = _contactsUnitList.get(i).getPhone_other();
				String _phonecustom = _contactsUnitList.get(i)
						.getPhone_custom();
				String _phonecustomlabel = _contactsUnitList.get(i)
						.getPhone_customlabel();

				if ((_phonehome != null) || (_phonemobile != null)
						|| (_phonework != null) || (_phonefax_work != null)
						|| (_phonefax_home != null) || (_phonepager != null)
						|| (_phoneother != null) || (_phonecustom != null)
						|| (_phonecustomlabel != null)) {

					serializer.startTag("", "phone");// start phone

					if (_phonehome != null) {
						serializer.startTag("", "home");
						serializer.cdsect(_phonehome);
						serializer.endTag("", "home");
					}

					if (_phonemobile != null) {
						serializer.startTag("", "mobile");
						serializer.cdsect(_phonemobile);
						serializer.endTag("", "mobile");
					}

					if (_phonework != null) {
						serializer.startTag("", "work");
						serializer.cdsect(_phonework);
						serializer.endTag("", "work");
					}

					if (_phonefax_work != null) {
						serializer.startTag("", "fax_work");
						serializer.cdsect(_phonefax_work);
						serializer.endTag("", "fax_work");
					}

					if (_phonefax_home != null) {
						serializer.startTag("", "fax_home");
						serializer.cdsect(_phonefax_home);
						serializer.endTag("", "fax_home");
					}

					if (_phonepager != null) {
						serializer.startTag("", "pager");
						serializer.cdsect(_phonepager);
						serializer.endTag("", "pager");
					}

					if (_phoneother != null) {
						serializer.startTag("", "other");
						serializer.cdsect(_phoneother);
						serializer.endTag("", "other");
					}

					if (_phonecustom != null) {
						serializer.startTag("", "custom");
						serializer.cdsect(_phonecustom);
						serializer.endTag("", "custom");
					}

					if (_phonecustomlabel != null) {
						serializer.startTag("", "customlabel");
						serializer.cdsect(_phonecustomlabel);
						serializer.endTag("", "customlabel");
					}
					serializer.endTag("", "phone");// end phone
				}

				/* email */
				String _emailhome = _contactsUnitList.get(i).getEmail_home();
				String _emailwork = _contactsUnitList.get(i).getEmail_work();
				String _emailother = _contactsUnitList.get(i).getEmail_other();
				String _emailcustom = _contactsUnitList.get(i)
						.getEmail_custom();
				String _emailcustomlabel = _contactsUnitList.get(i)
						.getEmail_customlabel();

				if ((_emailhome != null) || (_emailwork != null)
						|| (_emailother != null) || (_emailcustom != null)
						|| (_emailcustomlabel != null)) {

					serializer.startTag("", "email");// start email

					if (_emailhome != null) {
						serializer.startTag("", "home");
						serializer.cdsect(_emailhome);
						serializer.endTag("", "home");
					}

					if (_emailwork != null) {
						serializer.startTag("", "work");
						serializer.cdsect(_emailwork);
						serializer.endTag("", "work");
					}

					if (_emailother != null) {
						serializer.startTag("", "other");
						serializer.cdsect(_emailother);
						serializer.endTag("", "other");
					}

					if (_emailcustom != null) {
						serializer.startTag("", "custom");
						serializer.cdsect(_emailcustom);
						serializer.endTag("", "custom");
					}

					if (_emailcustomlabel != null) {
						serializer.startTag("", "customlabel");
						serializer.cdsect(_emailcustomlabel);
						serializer.endTag("", "customlabel");
					}
					serializer.endTag("", "email");// end email
				}

				/* im */
				// String _imaim = _contactsUnitList.get(i).getIm_aim();
				// String _imwindowlive = _contactsUnitList.get(i)
				// .getIm_windowslive();
				// String _imyahoo = _contactsUnitList.get(i).getIm_yahoo();
				// String _imskype = _contactsUnitList.get(i).getIm_skepe();
				// String _imqq = _contactsUnitList.get(i).getIm_qq();
				// String _imgoogletalk = _contactsUnitList.get(i)
				// .getIm_googletalk();
				// String _imicq = _contactsUnitList.get(i).getIm_icq();
				// String _imjabber = _contactsUnitList.get(i).getIm_jabber();
				//
				// if ((_imaim != null) || (_imwindowlive != null)
				// || (_imyahoo != null) || (_imskype != null)
				// || (_imqq != null) || (_imgoogletalk != null)
				// || (_imicq != null) || (_imjabber != null)) {
				//
				// serializer.startTag("", "im");// start im
				//
				// if (_imaim != null) {
				// serializer.startTag("", "aim");
				// serializer.cdsect(_imaim);
				// serializer.endTag("", "aim");
				// }
				//
				// if (_imwindowlive != null) {
				// serializer.startTag("", "windowlive");
				// serializer.cdsect(_imwindowlive);
				// serializer.endTag("", "windowlive");
				// }
				//
				// if (_imyahoo != null) {
				// serializer.startTag("", "yahoo");
				// serializer.cdsect(_imyahoo);
				// serializer.endTag("", "yahoo");
				// }
				//
				// if (_imskype != null) {
				// serializer.startTag("", "skype");
				// serializer.cdsect(_imskype);
				// serializer.endTag("", "skype");
				// }
				//
				// if (_imqq != null) {
				// serializer.startTag("", "qq");
				// serializer.cdsect(_imqq);
				// serializer.endTag("", "qq");
				// }
				//
				// if (_imgoogletalk != null) {
				// serializer.startTag("", "googletalk");
				// serializer.cdsect(_imgoogletalk);
				// serializer.endTag("", "googletalk");
				// }
				//
				// if (_imicq != null) {
				// serializer.startTag("", "icq");
				// serializer.cdsect(_imicq);
				// serializer.endTag("", "icq");
				// }
				//
				// if (_imjabber != null) {
				// serializer.startTag("", "jabber");
				// serializer.cdsect(_imjabber);
				// serializer.endTag("", "jabber");
				// }
				// serializer.endTag("", "im");// end im
				// }

				/* postal */
				String _postalhome = _contactsUnitList.get(i).getPostal_home();
				String _postalwork = _contactsUnitList.get(i).getPostal_work();
				String _postalother = _contactsUnitList.get(i)
						.getPostal_other();
				String _postalcustom = _contactsUnitList.get(i)
						.getPostal_custom();
				String _postalcustomlabel = _contactsUnitList.get(i)
						.getPostal_customlabel();
				if ((_postalhome != null) || (_postalwork != null)
						|| (_postalother != null) || (_postalcustom != null)
						|| (_postalcustomlabel != null)) {

					serializer.startTag("", "postal");// start postal

					if (_postalhome != null) {
						serializer.startTag("", "home");
						serializer.cdsect(_postalhome);
						serializer.endTag("", "home");
					}

					if (_postalwork != null) {
						serializer.startTag("", "work");
						serializer.cdsect(_postalwork);
						serializer.endTag("", "work");
					}

					if (_postalother != null) {
						serializer.startTag("", "other");
						serializer.cdsect(_postalother);
						serializer.endTag("", "other");
					}

					if (_postalcustom != null) {
						serializer.startTag("", "custom");
						serializer.cdsect(_postalcustom);
						serializer.endTag("", "custom");
					}

					if (_postalcustomlabel != null) {
						serializer.startTag("", "customlabel");
						serializer.cdsect(_postalcustomlabel);
						serializer.endTag("", "customlabel");
					}
					serializer.endTag("", "postal");// end postal
				}

				/* organizations */
				String _organizationsWorkCompany = _contactsUnitList.get(i)
						.getOrganization_work_company();
				String _organizationsWorktitle = _contactsUnitList.get(i)
						.getOrganization_work_title();
				String _organizationsOtherCompany = _contactsUnitList.get(i)
						.getOrganization_other_company();
				String _organizationsOthertitle = _contactsUnitList.get(i)
						.getOrganization_other_title();
				String _organizationsCustomCompany = _contactsUnitList.get(i)
						.getOrganization_custom_company();
				String _organizationsCustomtitle = _contactsUnitList.get(i)
						.getOrganization_custom_title();
				String _organizationsCustomlabel = _contactsUnitList.get(i)
						.getOrganization_customlabel();

				if ((_organizationsWorkCompany != null)
						|| (_organizationsWorktitle != null)
						|| (_organizationsOtherCompany != null)
						|| (_organizationsOthertitle != null)
						|| (_organizationsCustomCompany != null)
						|| (_organizationsCustomtitle != null)
						|| (_organizationsCustomlabel != null)) {

					serializer.startTag("", "organizations");// start
					// organizations
					if ((_organizationsWorkCompany != null)
							|| (_organizationsWorktitle != null)) {// organizations
						// work

						serializer.startTag("", "work");

						if (_organizationsWorkCompany != null) {
							serializer.startTag("", "company");
							serializer.cdsect(_organizationsWorkCompany);
							serializer.endTag("", "company");
						}

						if (_organizationsWorktitle != null) {
							serializer.startTag("", "title");
							serializer.cdsect(_organizationsWorktitle);
							serializer.endTag("", "title");
						}
						serializer.endTag("", "work");
					}

					if ((_organizationsOtherCompany != null)
							|| (_organizationsOthertitle != null)) {// organizations
						// other
						serializer.startTag("", "other");

						if (_organizationsOtherCompany != null) {
							serializer.startTag("", "company");
							serializer.cdsect(_organizationsOtherCompany);
							serializer.endTag("", "company");
						}

						if (_organizationsOthertitle != null) {
							serializer.startTag("", "title");
							serializer.cdsect(_organizationsOthertitle);
							serializer.endTag("", "title");
						}
						serializer.endTag("", "other");
					}

					if ((_organizationsCustomCompany != null)
							|| (_organizationsCustomtitle != null)
							|| (_organizationsCustomlabel != null)) {
						serializer.startTag("", "custom");

						if (_organizationsCustomCompany != null) {
							serializer.startTag("", "company");
							serializer.cdsect(_organizationsCustomCompany);
							serializer.endTag("", "company");
						}

						if (_organizationsCustomtitle != null) {
							serializer.startTag("", "title");
							serializer.cdsect(_organizationsCustomtitle);
							serializer.endTag("", "title");
						}

						if (_organizationsCustomlabel != null) {
							serializer.startTag("", "customlabel");
							serializer.cdsect(_organizationsCustomlabel);
							serializer.endTag("", "customlabel");
						}
						serializer.endTag("", "custom");
					}

					serializer.endTag("", "organizations");// end organizations
				}

				serializer.endTag("", "contact");// Node end
			}

			serializer.endTag("", "contactslist");
			serializer.endDocument();
			serializer.flush();
		} catch (Exception e) {
			Log.v(MoblieAssistantService.TAG,
					"**********CreateSMSXMLToString  error**********");
		}
		// Log.d(MoblieAssistantService.TAG, Thread.currentThread().getName()
		// + "---->" + "CreateContactsXMLToString() end..");
		return writer.toString();
	}

	/** SMS中 生成XML，返回字符串 */
	public static String CreateSMSXMLToString(ArrayList<SMSUnit> _smsUnitList) {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();

		try {
			serializer.setOutput(writer);

			// <?xml version="1.0″ encoding="UTF-8″ standalone="yes"?>
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "smslist");

			/* 循环填加sms */
			for (int i = 0; i < _smsUnitList.size(); i++) {
				String _id = _smsUnitList.get(i).get_id();
				String _address = _smsUnitList.get(i).getAddress();
				String _date = _smsUnitList.get(i).getDate();
				String _type = _smsUnitList.get(i).getType();
				String strbody = _smsUnitList.get(i).getBody();

				if ((_id != null) || (_address != null) || (_date != null)
						|| (_type != null) || (strbody != null)) {

					serializer.startTag("", "sms");// Node start
					if (_id != null) {
						serializer.startTag("", "_id");
						serializer.text(_id);
						serializer.endTag("", "_id");
					}

					// serializer.startTag("", "thread_id");
					// serializer.text(_smsUnitList.get(i).getThread_id());
					// serializer.endTag("", "thread_id");
					if (_address != null) {
						serializer.startTag("", "address");
						serializer.text(_address);
						serializer.endTag("", "address");
					}
					if (_date != null) {
						serializer.startTag("", "date");
						serializer.text(_date);
						serializer.endTag("", "date");
					}

					// serializer.startTag("", "read");
					// serializer.text(_smsUnitList.get(i).getRead());
					// serializer.endTag("", "read");
					//
					// serializer.startTag("", "status");
					// serializer.text(_smsUnitList.get(i).getStatus());
					// serializer.endTag("", "status");
					if (_type != null) {
						serializer.startTag("", "type");
						serializer.text(_type);
						serializer.endTag("", "type");
					}
					if (strbody != null) {
						serializer.startTag("", "body");
						// /* 把body节点里的 <> &这三个符号替换 */
						// String strnewbody = replaceString(strbody);
						serializer.cdsect(strbody);
						serializer.endTag("", "body");
					}

					serializer.endTag("", "sms");// Node end
				}

			}

			serializer.endTag("", "smslist");
			serializer.endDocument();
			serializer.flush();
		} catch (Exception e) {
			Log.v(MoblieAssistantService.TAG,
					"**********CreateSMSXMLToString  error**********");
		}

		return writer.toString();
	}

	/* 把body节点里的 <> &这三个符号替换 */
	private static String replaceString(String strbody) {
		/* 左尖括号替换成"(" */
		String left_angle_brackets = "<";
		String replace_left_angle_brackets = "\\(";
		/* 右尖括号替换成")" */
		String right_angle_brackets = ">";
		String replace_right_angle_brackets = "\\)";
		/* "&"替换成空格 */
		String strand = "&";
		String replacestrand = "\\ ";

		String str1 = "";
		String str2 = "";
		String str3 = "";
		str1 = strbody.replaceAll(left_angle_brackets,
				replace_left_angle_brackets);
		str2 = str1.replaceAll(right_angle_brackets,
				replace_right_angle_brackets);
		str3 = str2.replaceAll(strand, replacestrand);
		return str3;
	}
}
