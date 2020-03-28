package com.hongyu.util.contract;

import com.hongyu.util.Constants;

public class  AutoSignRequest {
	private String transactionId;
	//这里默认是虹宇的客户id，也就是虹宇的CA号
	private String customerId = Constants.FDD_HYCUSTOMERID;
	private String clientRole = "1";
	private String contractId;
	private String docTitle;
	private String keyword ="";
	private Integer positionType;
	private String signaturePositions;
	private String keyStrategy = "2";
	private String notifyUrl = Constants.AUTO_SIGN_NOTIFY_URL;
	
	public AutoSignRequest() {
		super();
	}
	/**
	 * 
	 * @param transactionId		交易号
	 * @param customerId	客户编号
	 * @param clientRole	客户角色
	 * @param contractId	合同编号
	 * @param docTitle	文档标题
	 * @param keyword	盖章的关键字
	 * @param keyStrategy	“0”在所有关键字处盖章，“1”只在第一个关键字处盖章  “2”在最后一个关键字处盖章
	 * @param notifyUrl		异步通知地址
	 */
	public AutoSignRequest(String transactionId, String customerId, String clientRole, String contractId,
			String docTitle, String keyword, String keyStrategy, String notifyUrl) {
		super();
		this.transactionId = transactionId;
		this.customerId = customerId;
		this.clientRole = clientRole;
		this.contractId = contractId;
		this.docTitle = docTitle;
		this.keyword = keyword;
		this.keyStrategy = keyStrategy;
		this.notifyUrl = notifyUrl;
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
	public String getClientRole() {
		return clientRole;
	}
	public void setClientRole(String clientRole) {
		this.clientRole = clientRole;
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
	public String getKeyStrategy() {
		return keyStrategy;
	}
	public void setKeyStrategy(String keyStrategy) {
		this.keyStrategy = keyStrategy;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	
}
