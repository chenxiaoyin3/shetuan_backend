package com.hongyu.entity;

import java.math.BigDecimal;
/**
 * 江泰撤保的返回信息
 * @author liyang
 *
 */
public class JtCancelOrderResponse {
	//渠道信息
	private String channel;
	//渠道业务代码
	private String channelBusinessCode;
	//渠道机构代码
	private String channelComCode;
	//渠道交易代码
	private String channelTradeCode;
	//旅行社代码（返回请求信息）
	private String TravelAgencyCode;
	//渠道交易日期,格式yyyy-MM-dd HH:mm:ss
	private String channelTradeDate;
	//渠道交易流水号（返回请求信息）
	private String channelTradeSerialNo;
	//渠道操作员代码（返回请求信息）
	private String channelOperateCode;
	//投保单号（默认=空）
	private String insureNo;
	//保单号
	private String policyNo;
	//起保日期（默认=空）
	private String startDate;
	//终保日期（默认=空）
	private String endDate;
	//产品方案代码（默认=空）
	private String productCode;
	//投保份数（默认=空）
	private Integer sumQuantity;
	//总保费（默认=空）
	private BigDecimal sumPremium;
	//缴费方式（默认=空）
	private Integer payType;
	//折扣比例（默认=空）
	private BigDecimal discount;
	//返回码（公共字段说明）
	private String responseCode;
	//返回信息（成功/失败）
	private String responseMessage;
	/**新加的10个预留字段，真坑*/
	private String Properties1;
	private String Properties2;
	private String Properties3;
	private String Properties4;
	private String Properties5;
	private String Properties6;
	private String Properties7;
	private String Properties8;
	private String Properties9;
	private String Properties10;
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getChannelBusinessCode() {
		return channelBusinessCode;
	}
	public void setChannelBusinessCode(String channelBusinessCode) {
		this.channelBusinessCode = channelBusinessCode;
	}
	public String getChannelComCode() {
		return channelComCode;
	}
	public void setChannelComCode(String channelComCode) {
		this.channelComCode = channelComCode;
	}
	public String getChannelTradeCode() {
		return channelTradeCode;
	}
	public void setChannelTradeCode(String channelTradeCode) {
		this.channelTradeCode = channelTradeCode;
	}
	public String getTravelAgencyCode() {
		return TravelAgencyCode;
	}
	public void setTravelAgencyCode(String travelAgencyCode) {
		TravelAgencyCode = travelAgencyCode;
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
	public String getInsureNo() {
		return insureNo;
	}
	public void setInsureNo(String insureNo) {
		this.insureNo = insureNo;
	}
	public String getPolicyNo() {
		return policyNo;
	}
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
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
	public BigDecimal getSumPremium() {
		return sumPremium;
	}
	public void setSumPremium(BigDecimal sumPremium) {
		this.sumPremium = sumPremium;
	}
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	public BigDecimal getDiscount() {
		return discount;
	}
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public String getProperties1() {
		return Properties1;
	}
	public void setProperties1(String properties1) {
		Properties1 = properties1;
	}
	public String getProperties2() {
		return Properties2;
	}
	public void setProperties2(String properties2) {
		Properties2 = properties2;
	}
	public String getProperties3() {
		return Properties3;
	}
	public void setProperties3(String properties3) {
		Properties3 = properties3;
	}
	public String getProperties4() {
		return Properties4;
	}
	public void setProperties4(String properties4) {
		Properties4 = properties4;
	}
	public String getProperties5() {
		return Properties5;
	}
	public void setProperties5(String properties5) {
		Properties5 = properties5;
	}
	public String getProperties6() {
		return Properties6;
	}
	public void setProperties6(String properties6) {
		Properties6 = properties6;
	}
	public String getProperties7() {
		return Properties7;
	}
	public void setProperties7(String properties7) {
		Properties7 = properties7;
	}
	public String getProperties8() {
		return Properties8;
	}
	public void setProperties8(String properties8) {
		Properties8 = properties8;
	}
	public String getProperties9() {
		return Properties9;
	}
	public void setProperties9(String properties9) {
		Properties9 = properties9;
	}
	public String getProperties10() {
		return Properties10;
	}
	public void setProperties10(String properties10) {
		Properties10 = properties10;
	}
	
}
