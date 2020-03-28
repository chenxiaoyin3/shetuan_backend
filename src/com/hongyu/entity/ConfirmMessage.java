package com.hongyu.entity;

import java.util.List;

import com.hongyu.util.Constants;
import com.hongyu.util.JiangtaiUtil;
/**
 * 江泰保险投保的接口参数实体
 * @author li_yang
 *
 */
//confirmMessage中需要手动填写的参数有以下几个
		/*
		 * channelTradeDate:渠道交易日期,格式yyyy-MM-dd HH:mm:ss
		 * channelTradeSerialNo:渠道交易流水号
		 * channelOperateCode:渠道操作员代码
		 * startDate:起保日期,yyyy-MM-dd HH:mm:ss
		 * endDate:终保日期:yyyy-MM-dd HH:mm:ss
		 * contactName:保单联系人
		 * contactPhone:联系人电话
		 * payType:缴费方式
		 * travelRoute:旅行路线
		 * travelGroupNo:旅行团编号
		 * productCode:产品方案代码（江泰提供）
		 * sumQuantity:投保份数；默认=1
		 * insuredList:被保险人列表（不得超过1000人）
		 */
public class ConfirmMessage implements java.io.Serializable {
	private String channel = Constants.CHANNEL;    //渠道信息（自定义，双方同步）
	private String channelComCode = Constants.CHANNEL_COM_CODE;//渠道机构代码（江泰提供）
	private String travelAgencyCode = Constants.TRAVEL_AGENCY_CODE;//旅行社代码（江泰提供）
	private String travelAgencyLicenseCode = Constants.TRAVEL_AGENCY_LICENSE_CODE;//旅行社营业许可证号（总社）（根据国家旅游总局公布）
	private String channelTradeCode = Constants.CHANNEL_TRADE_CODE_ORDER;//渠道交易代码（公共字段说明)
	private String channelBusinessCode = Constants.CHANNEL_BUSINESS_CODE;//渠道业务代码（江泰提供）
	private String channelTradeDate;//渠道交易日期,格式yyyy-MM-dd HH:mm:ss
	private String channelTradeSerialNo;//渠道交易流水号
	private String channelOperateCode = Constants.CHANNEL_OPERATE_CODE;//渠道操作员代码 可以为空
	private String startDate;//起保日期,yyyy-MM-dd HH:mm:ss
	private String endDate;//终保日期:yyyy-MM-dd HH:mm:ss
	private String contactName;//保单联系人
	private String contactPhone;//联系人电话
	private Integer payType = 1;//缴费方式
	private String travelRoute;//旅行线路ss
	private String travelGroupNo;//旅行团编号
	private String productCode ;//产品方案代码（江泰提供）
	private Integer sumQuantity = 1;//投保份数；默认=1
	private Integer isSendSms = 0;//1为发送，0为不发；默认=0
	private String invoiceTitle;//发票抬头
	private List<InsureInfo> insuredList;//被保险人列表（不得超过1000人）
	
	public Integer getIsSendSms() {
		return isSendSms;
	}

	public void setIsSendSms(Integer isSendSms) {
		this.isSendSms = isSendSms;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	public ConfirmMessage() {
		super();
	}
	
	public ConfirmMessage(String channelTradeDate, String channelTradeSerialNo, String channelOperateCode,
			String startDate, String endDate, String contactName, String contactPhone, Integer payType,
			String travelRoute, String travelGroupNo, String productCode, Integer sumQuantity,
			List<InsureInfo> insuredList) {
		super();
		this.channelTradeDate = channelTradeDate;
		this.channelTradeSerialNo = channelTradeSerialNo;
		this.channelOperateCode = channelOperateCode;
		this.startDate = startDate;
		this.endDate = endDate;
		this.contactName = contactName;
		this.contactPhone = contactPhone;
		this.payType = payType;
		this.travelRoute = travelRoute;
		this.travelGroupNo = travelGroupNo;
		this.productCode = productCode;
		this.sumQuantity = sumQuantity;
		this.insuredList = insuredList;
	}

	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getChannelComCode() {
		return channelComCode;
	}
	public void setChannelComCode(String channelComCode) {
		this.channelComCode = channelComCode;
	}
	public String getTravelAgencyCode() {
		return travelAgencyCode;
	}
	public void setTravelAgencyCode(String travelAgencyCode) {
		this.travelAgencyCode = travelAgencyCode;
	}
	public String getTravelAgencyLicenseCode() {
		return travelAgencyLicenseCode;
	}
	public void setTravelAgencyLicenseCode(String travelAgencyLicenseCode) {
		this.travelAgencyLicenseCode = travelAgencyLicenseCode;
	}
	public String getChannelTradeCode() {
		return channelTradeCode;
	}
	public void setChannelTradeCode(String channelTradeCode) {
		this.channelTradeCode = channelTradeCode;
	}
	public String getChannelBusinessCode() {
		return channelBusinessCode;
	}
	public void setChannelBusinessCode(String channelBusinessCode) {
		this.channelBusinessCode = channelBusinessCode;
	}
	public String getChannelTradeDate() {
		return channelTradeDate;
	}
	public void setChannelTradeDate(String channelTradeDate) {
		this.channelTradeDate = channelTradeDate;
	}
	public String getChannelTradeSerialNo() {
		return channelTradeSerialNo;
	}
	public void setChannelTradeSerialNo(String channelTradeSerialNo) {
		this.channelTradeSerialNo = channelTradeSerialNo;
	}
	public String getChannelOperateCode() {
		return channelOperateCode;
	}
	public void setChannelOperateCode(String channelOperateCode) {
		this.channelOperateCode = channelOperateCode;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	public String getTravelRoute() {
		return travelRoute;
	}
	public void setTravelRoute(String travelRoute) {
		this.travelRoute = travelRoute;
	}
	public String getTravelGroupNo() {
		return travelGroupNo;
	}
	public void setTravelGroupNo(String travelGroupNo) {
		this.travelGroupNo = travelGroupNo;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public Integer getSumQuantity() {
		return sumQuantity;
	}
	public void setSumQuantity(Integer sumQuantity) {
		this.sumQuantity = sumQuantity;
	}
	public List<InsureInfo> getInsuredList() {
		return insuredList;
	}
	public void setInsuredList(List<InsureInfo> insuredList) {
		this.insuredList = insuredList;
	}

	
}
