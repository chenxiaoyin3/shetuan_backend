package com.hongyu.util.bankEntity;

import java.math.BigDecimal;

public class QueryRequest {
	/**实体值为空时，字段名也不能上送**/
	private String transId;//必填-交易代码，IQSR，单笔订单查询
	private String merchantId;//必填-商户ID
	private String originalorderId;//必填-原订单号
	private String originalTransDateTime;//必填-原交易时间
	private BigDecimal originalTransAmt;//非必填-原交易金额，如果该值为空，该字段名也不能上送
	private String merURL;//非必填项-商户URL，如果该值为空，该字段名也不能上送
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
	public String getOriginalorderId() {
		return originalorderId;
	}
	public void setOriginalorderId(String originalorderId) {
		this.originalorderId = originalorderId;
	}
	public String getOriginalTransDateTime() {
		return originalTransDateTime;
	}
	public void setOriginalTransDateTime(String originalTransDateTime) {
		this.originalTransDateTime = originalTransDateTime;
	}
	public BigDecimal getOriginalTransAmt() {
		return originalTransAmt;
	}
	public void setOriginalTransAmt(BigDecimal originalTransAmt) {
		this.originalTransAmt = originalTransAmt;
	}
	public String getMerURL() {
		return merURL;
	}
	public void setMerURL(String merURL) {
		this.merURL = merURL;
	}
}
