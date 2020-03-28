package com.hongyu.util.contract;

import com.hongyu.util.Constants;

public class SignRequest {
	private Boolean method = false;//false为不设置有效期和次数，true为设置有效期和次数
	private String transactionId;
	private String customerId;
	private String contractId;
	private Integer positionType; //定位方式 0为关键字定位（默认）  1为坐标定位
	private String signaturePositions; //坐标点集合
	private String docTitle;
	private String keyword = "customer_mark";
	private String validity;  //有效期，单位为分钟的有效时间，大于0小于10080的整数。最多七天。
	private String quantity;	//有效次数格式为大于0的整数
	private String returnUrl = Constants.CUSTOMER_RETURN_URL;//签章完成之后，法大大将自动跳转到该地址(如果设置为空字符串的话，就会跳转到法大大自己提供的下载页面)，这个地址跳转到前端，一般是下载页面，这里最好设置哦默认值
	private String notifyUrl = Constants.CUSTOMER_NOTIFY_URL;//签署完成之后，法大大向此接口发送签署结果
	
	
	public SignRequest() {
		super();
	}
	/**
	 * 不设置有效期和次数的构造函数
	 * @param method	方式默认为false
	 * @param transactionId		交易号
	 * @param customerId	客户id
	 * @param contractId	合同编号
	 * @param docTitle		合同标题
	 * @param keyword		签章位置关键字
	 * @param returnUrl		签章完成之后的跳转地址
	 * @param notifyUrl		签章完成之后的异步通知地址
	 */
	public SignRequest(Boolean method,String transactionId, String customerId, String contractId, String docTitle, String keyword,
			String returnUrl, String notifyUrl) {
		super();
		this.method = method;
		this.transactionId = transactionId;
		this.customerId = customerId;
		this.contractId = contractId;
		this.docTitle = docTitle;
		this.keyword = keyword;
		this.returnUrl = returnUrl;
		this.notifyUrl = notifyUrl;
	}
	/**
	 * 
	 * @param method	方式
	 * @param transactionId		交易号
	 * @param customerId	客户id
	 * @param contractId	合同编号
	 * @param docTitle		合同标题
	 * @param keyword		签章位置关键字
	 * @param validity		有效期，单位为分钟的有效时间，大于0小于10080的整数。最多七天。
	 * @param quantity		有效次数格式为大于0的整数
	 * @param returnUrl		签章完成之后的跳转地址
	 * @param notifyUrl		签章完成之后的异步通知地址
	 */
	public SignRequest(Boolean method,String transactionId, String customerId, String contractId, String docTitle, String keyword,
			String validity,String quantity,String returnUrl, String notifyUrl) {
		super();
		this.method = method;
		this.transactionId = transactionId;
		this.customerId = customerId;
		this.contractId = contractId;
		this.docTitle = docTitle;
		this.keyword = keyword;
		this.validity = validity;
		this.quantity = quantity;
		this.returnUrl = returnUrl;
		this.notifyUrl = notifyUrl;
	}
	
	public Boolean getMethod() {
		return method;
	}
	public void setMethod(Boolean method) {
		this.method = method;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	public String getDocTitle() {
		return docTitle;
	}
	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getValidity() {
		return validity;
	}
	public void setValidity(String validity) {
		this.validity = validity;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public Integer getPositionType() {
		return positionType;
	}
	public void setPositionType(Integer positionType) {
		this.positionType = positionType;
	}
	public String getSignaturePositions() {
		return signaturePositions;
	}
	public void setSignaturePositions(String signaturePositions) {
		this.signaturePositions = signaturePositions;
	}
	
	
}
