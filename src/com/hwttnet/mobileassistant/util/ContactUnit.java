package com.hwttnet.mobileassistant.util;

/**
 * 一.电话本单元 节点：
 * 
 * id
 * 
 * name,
 * 
 * phone, 电话
 * 
 * email， 邮箱
 * 
 * im， 即时通
 * 
 * postal， 地址
 * 
 * organizations，组织
 * 
 * 
 * 二.phone节点分如下子节点：
 * 
 * work，工作电话
 * 
 * mobile，手机
 * 
 * home，家庭
 * 
 * fax_work，工作传真
 * 
 * fax_home，家庭传真
 * 
 * pager，传呼
 * 
 * other，其它
 * 
 * custom，自定义
 * 
 * customlabel，自定义电话的标题名
 * 
 *三. email节点分如下子节点：
 * 
 * home，家庭
 * 
 * work，工作
 * 
 * other，其它
 * 
 * custom，自定义
 * 
 * customlabel，自定义email的标题名
 * 
 * 四.IM节点分如下子节点：
 * 
 * aim,
 * 
 * windowlive,
 * 
 * yahoo,
 * 
 * skype,
 * 
 * qq,
 * 
 * googletalk,
 * 
 * icq,
 * 
 * jabber,
 * 
 *五. postal节点分如下子节点：
 * 
 * home，家庭
 * 
 * work，工作
 * 
 * other，其它
 * 
 * custom，自定义
 * 
 * customlabel，自定义postal的标题名
 * 
 * 六.organizations节点分如下子节点：(组织这三个节点里还有2到3个子节点)
 * 
 * work,{company,title}
 * 
 * other,{company,title}其它
 * 
 * custom,{company,title,customlabel}自定义
 * 
 * 
 * 
 * 
 * */
public class ContactUnit {
	/* person_id作为主键，唯一标识联系人 */
	private String person_id;

	/* name和person_id关联 */
	private String name;

	/* 电话 */
	private String phone_home;
	private String phone_moblie;
	private String phone_work;
	private String phone_Fax_work;
	private String phone_Fax_home;
	private String phone_pager;
	private String phone_other;
	private String phone_custom;
	private String phone_customlabel;
	/* 电话类型常量 */
	public static final int TYPE_PHONE_CUSTOM = 0;
	public static final int TYPE_PHONE_HOME = 1;
	public static final int TYPE_PHONE_MOBILE = 2;
	public static final int TYPE_PHONE_WORK = 3;
	public static final int TYPE_PHONE_FAX_WORK = 4;
	public static final int TYPE_PHONE_FAX_HOME = 5;
	public static final int TYPE_PHONE_PAGER = 6;
	public static final int TYPE_PHONE_OTHER = 7;

	/* email */
	private String email_home;
	private String email_work;
	private String email_other;
	private String email_custom;
	private String email_customlabel;

	/* 限时联系方式：im */
	private String im_aim;
	private String im_windowslive;
	private String im_yahoo;
	private String im_skepe;
	private String im_qq;
	private String im_googletalk;
	private String im_icq;
	private String im_jabber;
	/* 地址 */
	private String postal_home;
	private String postal_work;
	private String postal_other;
	private String postal_custom;
	private String postal_customlabel;

	/* 组织 */
	private String organization_work_company;
	private String organization_work_title;
	private String organization_other_company;
	private String organization_other_title;
	private String organization_custom_company;
	private String organization_custom_title;
	private String organization_customlabel;

	/* 组织类型常量 */
	public static final int TYPE_ORGANIZATION_CUSTOM = 0;
	public static final int TYPE_ORGANIZATION_WORK = 1;
	public static final int TYPE_ORGANIZATION_OTHER = 2;

	public String getPerson_id() {
		return person_id;
	}

	public void setPerson_id(String personId) {
		person_id = personId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/* 对电话的get,set操作 */
	public String getPhone_home() {
		return phone_home;
	}

	public void setPhone_home(String phoneHome) {
		phone_home = phoneHome;
	}

	public String getPhone_moblie() {
		return phone_moblie;
	}

	public void setPhone_moblie(String phoneMoblie) {
		phone_moblie = phoneMoblie;
	}

	public String getPhone_work() {
		return phone_work;
	}

	public void setPhone_work(String phoneWork) {
		phone_work = phoneWork;
	}

	public String getPhone_Fax_work() {
		return phone_Fax_work;
	}

	public void setPhone_Fax_work(String phoneFaxWork) {
		phone_Fax_work = phoneFaxWork;
	}

	public String getPhone_Fax_home() {
		return phone_Fax_home;
	}

	public void setPhone_Fax_home(String phoneFaxHome) {
		phone_Fax_home = phoneFaxHome;
	}

	public String getPhone_pager() {
		return phone_pager;
	}

	public void setPhone_pager(String phonePager) {
		phone_pager = phonePager;
	}

	public String getPhone_other() {
		return phone_other;
	}

	public void setPhone_other(String phoneOther) {
		phone_other = phoneOther;
	}

	public String getPhone_custom() {
		return phone_custom;
	}

	public void setPhone_custom(String phoneCustom) {
		phone_custom = phoneCustom;
	}

	/* 对地址的get,set操作 */
	public String getPostal_home() {
		return postal_home;
	}

	public void setPostal_home(String postalHome) {
		postal_home = postalHome;
	}

	public String getPostal_work() {
		return postal_work;
	}

	public void setPostal_work(String postalWork) {
		postal_work = postalWork;
	}

	public String getPostal_other() {
		return postal_other;
	}

	public void setPostal_other(String postalOther) {
		postal_other = postalOther;
	}

	public String getPostal_custom() {
		return postal_custom;
	}

	public void setPostal_custom(String postalCustom) {
		postal_custom = postalCustom;
	}

	/* 对email的get,set操作 */
	public String getEmail_home() {
		return email_home;
	}

	public void setEmail_home(String emailHome) {
		email_home = emailHome;
	}

	public String getEmail_work() {
		return email_work;
	}

	public void setEmail_work(String emailWork) {
		email_work = emailWork;
	}

	public String getEmail_other() {
		return email_other;
	}

	public void setEmail_other(String emailOther) {
		email_other = emailOther;
	}

	public String getEmail_custom() {
		return email_custom;
	}

	public void setEmail_custom(String emailCustom) {
		email_custom = emailCustom;
	}

	/* 对IM的get,set操作 */
	public String getIm_aim() {
		return im_aim;
	}

	public void setIm_aim(String imAim) {
		im_aim = imAim;
	}

	public String getIm_windowslive() {
		return im_windowslive;
	}

	public void setIm_windowslive(String imWindowslive) {
		im_windowslive = imWindowslive;
	}

	public String getIm_yahoo() {
		return im_yahoo;
	}

	public void setIm_yahoo(String imYahoo) {
		im_yahoo = imYahoo;
	}

	public String getIm_skepe() {
		return im_skepe;
	}

	public void setIm_skepe(String imSkepe) {
		im_skepe = imSkepe;
	}

	public String getIm_qq() {
		return im_qq;
	}

	public void setIm_qq(String imQq) {
		im_qq = imQq;
	}

	public String getIm_googletalk() {
		return im_googletalk;
	}

	public void setIm_googletalk(String imGoogletalk) {
		im_googletalk = imGoogletalk;
	}

	public String getIm_icq() {
		return im_icq;
	}

	public void setIm_icq(String imIcq) {
		im_icq = imIcq;
	}

	public String getIm_jabber() {
		return im_jabber;
	}

	public void setIm_jabber(String imJabber) {
		im_jabber = imJabber;
	}

	/* 对组织的get,set操作 */
	public String getOrganization_work_title() {
		return organization_work_title;
	}

	public void setOrganization_work_title(String organizationWorkTitle) {
		organization_work_title = organizationWorkTitle;
	}

	public String getOrganization_work_company() {
		return organization_work_company;
	}

	public void setOrganization_work_company(String organizationWorkCompany) {
		organization_work_company = organizationWorkCompany;
	}

	public String getOrganization_other_company() {
		return organization_other_company;
	}

	public void setOrganization_other_company(String organizationOtherCompany) {
		organization_other_company = organizationOtherCompany;
	}

	public String getOrganization_other_title() {
		return organization_other_title;
	}

	public void setOrganization_other_title(String organizationOtherTitle) {
		organization_other_title = organizationOtherTitle;
	}

	public String getOrganization_custom_company() {
		return organization_custom_company;
	}

	public void setOrganization_custom_company(String organizationCustomCompany) {
		organization_custom_company = organizationCustomCompany;
	}

	public String getOrganization_custom_title() {
		return organization_custom_title;
	}

	public void setOrganization_custom_title(String organizationCustomTitle) {
		organization_custom_title = organizationCustomTitle;
	}

	public String getPhone_customlabel() {
		return phone_customlabel;
	}

	public void setPhone_customlabel(String phoneCustomlabel) {
		phone_customlabel = phoneCustomlabel;
	}

	public String getEmail_customlabel() {
		return email_customlabel;
	}

	public void setEmail_customlabel(String emailCustomlabel) {
		email_customlabel = emailCustomlabel;
	}

	public String getPostal_customlabel() {
		return postal_customlabel;
	}

	public void setPostal_customlabel(String postalCustomlabel) {
		postal_customlabel = postalCustomlabel;
	}

	public String getOrganization_customlabel() {
		return organization_customlabel;
	}

	public void setOrganization_customlabel(String organizationCustomlabel) {
		organization_customlabel = organizationCustomlabel;
	}
}
