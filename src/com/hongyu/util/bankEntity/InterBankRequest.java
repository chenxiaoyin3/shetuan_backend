package com.hongyu.util.bankEntity;

import java.math.BigDecimal;

public class InterBankRequest {
	/*******本实体所有字段必须上送，非必填项可以为空
	 * merUrl1不能带参数，
	 * 前台通知地址固定跳转merURL1
	 * *********/
	public String transId;//必填项-交易代码-IPER个人网银支付，EPER企业网银支付
	
	public String merchantId;//必填项-商户代码
	
	public String orderId;//必填项-订单号
	
	public BigDecimal transAmt;//必填项-交易金额
	
	public String transDateTime;//必填项-交易时间yyyyMMddHHmmss
	
	public String currencyType;//必填项-币种，01人民币
	
	/***B2C:09兴业银行，12民生银行，13华夏，15北京，16浦发，19广发，21交通，25工商，27建设，28招商，29农业，33中信，40北京农商行，45中国银行，46邮储，49南京，51杭州，53浙商，54上海，55渤海，69上海农村商业银行
	 * *B2B:70工商，71农业，72建设，75北京农商行，76浦发，78招商，79中国，80交通*/
	public String payBankNo;//必填项-他行行号，测试环境仅支持09=兴业银行
	
	public String customerName;//非必填-订货人姓名
	
	public String merSecName;//非必填项-二级商户，联调测试时必填中文
	
	public String productInfo;//非必填项-商品信息，联调测试时必填中午
	
	public String customerEmail;//非必填项-订货人Email
	
	public String merURL;//必填项-商户URL，用于后台通知商户
	
	public String merURL1;//必填项-商户URL1，用于后台通知商户失败或默认情况下,引导客户回商户页面
	
	public String payIp;//非必填项-支付地址，客户在商户网站上生成订单时的客户ip
	
	public String msgExt;//非必填项-附加信息，联调测试时必测中文，如果是二级商户订单，该项填写二级商户号

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

	public String getPayBankNo() {
		return payBankNo;
	}

	public void setPayBankNo(String payBankNo) {
		this.payBankNo = payBankNo;
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
