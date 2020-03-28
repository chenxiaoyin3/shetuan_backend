package com.hongyu.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * RefundRecords 退款详情
 *
 * @author xyy
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_refund_records")
public class RefundRecords implements java.io.Serializable {

	private Long id;
	private Long refundInfoId;
	private String orderCode;
	private String touristName;
	/**需要和HyOrder的source一致 订单来源 0门店，1官网不选门店，2官网选择门店*/
	private Integer signUpMethod;
	private Long payMethod;
	private BigDecimal amount;
    /** 1:预存款 2:支付宝 3:微信支付*/
	private Long refundMethod;
	private String payAccount;
	private String touristAccount;
	private Date payDate;
	private String payer;
	private String storeName;
	private Long storeId;
	private Long orderId;

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "refund_info_id")
	public Long getRefundInfoId() {
		return this.refundInfoId;
	}

	public void setRefundInfoId(Long refundInfoId) {
		this.refundInfoId = refundInfoId;
	}

	@Column(name = "order_code")
	public String getOrderCode() {
		return this.orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	@Column(name = "tourist_name")
	public String getTouristName() {
		return this.touristName;
	}

	public void setTouristName(String touristName) {
		this.touristName = touristName;
	}

	@Column(name = "sign_up_method")
	public Integer getSignUpMethod() {
		return this.signUpMethod;
	}

	public void setSignUpMethod(Integer signUpMethod) {
		this.signUpMethod = signUpMethod;
	}

	@Column(name = "pay_method")
	public Long getPayMethod() {
		return this.payMethod;
	}

	public void setPayMethod(Long payMethod) {
		this.payMethod = payMethod;
	}

	@Column(name = "amount")
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "refund_method")
	public Long getRefundMethod() {
		return this.refundMethod;
	}

	public void setRefundMethod(Long refundMethod) {
		this.refundMethod = refundMethod;
	}

	@Column(name = "pay_account")
	public String getPayAccount() {
		return this.payAccount;
	}

	public void setPayAccount(String payAccount) {
		this.payAccount = payAccount;
	}

	@Column(name = "tourist_account")
	public String getTouristAccount() {
		return this.touristAccount;
	}

	public void setTouristAccount(String touristAccount) {
		this.touristAccount = touristAccount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_date")
	public Date getPayDate() {
		return this.payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	@Column(name = "payer")
	public String getPayer() {
		return this.payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	@Column(name = "store_name")
	public String getStoreName() {
		return this.storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	@Column(name = "store_id")
	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	

}
