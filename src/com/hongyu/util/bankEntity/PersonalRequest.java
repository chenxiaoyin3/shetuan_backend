package com.hongyu.util.bankEntity;

import java.math.BigDecimal;

public class PersonalRequest {
	String transId;//必填-交易代码：IPER个人网银支付（B2C），EPER企业网银支付（B2B）
	
	String merchantId;//必填-商户代码
	
	String orderId;//必填-订单号
	
	BigDecimal transAmt;//必填-交易金额
	
	String transDateTime;//必填-交易时间yyyyMMddHHmmss	
	
	String currencyType;//必填-01-人民币
	
	String customerName;//非必填-订货人姓名
	
	String merSecName;//非必填-二级商户-联调测试时必测中文
	
	String productInfo;//非必填-商品信息-联调测试时必测中文
	
	String customerEmail;//非必填-订货人email
	
	String merURL;//必填-商户URL-用于后台通知商户
	
	String merURL1;//必填-商户URL1-用于后台通知商户失败或默认情况下，引导客户回商户页面
	
	String payIp;//非必填，客户在商户网站上生成订单时的客户IP
	
	String msgExt;//非必填,联调测试时必测中文，如果是二级商户订单，该项填写二级商户号

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(BigDecimal transAmt) {
		this.transAmt = transAmt;
	}

	public String getTransDateTime() {
		return transDateTime;
	}

	public void setTransDateTime(String transDateTime) {
		this.transDateTime = transDateTime;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getMerSecName() {
		return merSecName;
	}

	public void setMerSecName(String merSecName) {
		this.merSecName = merSecName;
	}

	public String getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(String productInfo) {
		this.productInfo = productInfo;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getMerURL() {
		return merURL;
	}

	public void setMerURL(String merURL) {
		this.merURL = merURL;
	}

	public String getMerURL1() {
		return merURL1;
	}

	public void setMerURL1(String merURL1) {
		this.merURL1 = merURL1;
	}

	public String getPayIp() {
		return payIp;
	}

	public void setPayIp(String payIp) {
		this.payIp = payIp;
	}

	public String getMsgExt() {
		return msgExt;
	}

	public void setMsgExt(String msgExt) {
		this.msgExt = msgExt;
	}
	
}
