package com.hongyu.entity;
// Generated 2018-1-2 16:59:39 by Hibernate Tools 3.6.0.Final

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 商城销售对账表
 */
@Entity
@Table(name = "hy_coupon_sale_account")
public class CouponSaleAccount implements java.io.Serializable {

	private Long id;
	private String receiverPhone;
	private Date issueTime;
	private Float sum;
	private Integer num;
	private Integer bindNum;
	private Float discount;
	private Float total;
	private String payType;
	private String saler;
	private Date confirmTime;
	private String confirmer;
	
	private String suffixUrl;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "receiver_phone")
	public String getReceiverPhone() {
		return this.receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "issue_time")
	public Date getIssueTime() {
		return this.issueTime;
	}

	public void setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
	}

	@Column(name = "sum")
	public Float getSum() {
		return this.sum;
	}

	public void setSum(Float sum) {
		this.sum = sum;
	}

	
	@Column(name = "num")
	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	@Column(name = "bind_num")
	public Integer getBindNum() {
		return bindNum;
	}

	public void setBindNum(Integer bindNum) {
		this.bindNum = bindNum;
	}

	@Column(name = "discount")
	public Float getDiscount() {
		return discount;
	}

	public void setDiscount(Float discount) {
		this.discount = discount;
	}

	@Column(name = "total")
	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	@Column(name = "pay_type")
	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	@Column(name = "saler")
	public String getSaler() {
		return saler;
	}

	public void setSaler(String saler) {
		this.saler = saler;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "confirm_time")
	public Date getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	@Column(name = "confirmer")
	public String getConfirmer() {
		return confirmer;
	}

	public void setConfirmer(String confirmer) {
		this.confirmer = confirmer;
	}

	@Column(name = "suffix_url")
	public String getSuffixUrl() {
		return suffixUrl;
	}

	public void setSuffixUrl(String suffixUrl) {
		this.suffixUrl = suffixUrl;
	}

}
