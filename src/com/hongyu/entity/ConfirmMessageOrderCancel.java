package com.hongyu.entity;

import java.util.Date;

import com.hongyu.util.Constants;

/**
 * 江泰旅游取消订单的参数实体。
 * @author li_yang
 *
 */
public class ConfirmMessageOrderCancel implements java.io.Serializable {
	private String channel = Constants.CHANNEL;     //渠道信息（自定义，双方同步）
	private String channelComCode = Constants.CHANNEL_COM_CODE;//渠道机构代码（江泰提供）
	private String travelAgencyCode = Constants.TRAVEL_AGENCY_CODE;//旅行社代码（江泰提供）
	private String travelAgencyLicenseCode = Constants.TRAVEL_AGENCY_LICENSE_CODE;//旅行社营业许可证号（总社）（根据国家旅游总局公布）
	private String channelTradeCode = Constants.CHANNEL_TRADE_CODE_CANCEL_ORDER;//渠道交易代码（公共字段说明
	private String channelBusinessCode = Constants.CHANNEL_BUSINESS_CODE;//渠道业务代码（江泰提供）
	private String channelTradeDate;//渠道交易日期,格式yyyy-MM-dd HH:mm:ss
	private String channelTradeSerialNo;//渠道交易流水号
	private String channelOperateCode = Constants.CHANNEL_OPERATE_CODE;//渠道操作员代码
	public ConfirmMessageOrderCancel(String channelTradeCode, String channelTradeDate, String channelTradeSerialNo,
			String channelOperateCode) {
		super();
		this.channelTradeCode = channelTradeCode;
		this.channelTradeDate = channelTradeDate;
		this.channelTradeSerialNo = channelTradeSerialNo;
		this.channelOperateCode = channelOperateCode;
	}
	
	public ConfirmMessageOrderCancel() {
		super();
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
	
}
