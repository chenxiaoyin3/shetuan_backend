package com.hongyu.entity;

import java.math.BigDecimal;

/**
 * 江泰投保接口的返回信息
 * @author li_yang
 *
 */
public class JtOrderResponse {
	//渠道交易流水号（返回请求信息）
	private String channelTradeSerialNo;
	//投保单号
	private String insureNo;
	//保单号
	private String policyNo;
	//总保费
	private BigDecimal sumPremium;
	//折扣比例
	private BigDecimal discount;
	//江泰订单编号
	private String JtOrderNo;
	//返回码（公共字段说明）
	private String responseCode;
	//返回信息（成功/失败）
	private String responseMessage;

	public String getChannelTradeSerialNo() {
		return channelTradeSerialNo;
	}
	public void setChannelTradeSerialNo(String channelTradeSerialNo) {
		this.channelTradeSerialNo = channelTradeSerialNo;
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
	public BigDecimal getSumPremium() {
		return sumPremium;
	}
	public void setSumPremium(BigDecimal sumPremium) {
		this.sumPremium = sumPremium;
	}
	public BigDecimal getDiscount() {
		return discount;
	}
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	public String getJtOrderNo() {
		return JtOrderNo;
	}
	public void setJtOrderNo(String jtOrderNo) {
		JtOrderNo = jtOrderNo;
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
	@Override
	public String toString() {
		return "JtOrderResponse [channelTradeSerialNo=" + channelTradeSerialNo + ", insureNo=" + insureNo
				+ ", policyNo=" + policyNo + ", sumPremium=" + sumPremium + ", discount=" + discount + ", JtOrderNo="
				+ JtOrderNo + ", responseCode=" + responseCode + ", responseMessage=" + responseMessage + "]";
	}
	
	

}
