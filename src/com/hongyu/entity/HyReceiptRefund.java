package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="hy_receipt_refund")
//@JsonIgnoreProperties({"order","operator","store","createTime"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","createTime"})
public class HyReceiptRefund implements Serializable {
	private Long id;	//id
	private Integer type;	//类型，0收款；1退款
	private HyOrder order;	//订单
	private BigDecimal money;	//受退款金额
	private String method;		//受退款方式
	private HyAdmin operator;	//受退款人
	private Store store;	//门店
	private Date collectionTime;	//受退款时间
	private Date createTime;	//创建时间
	private String remark;	//备注
	private String bankNum;	//银行卡号
	private Integer status;	//状态 0待财务确认，1财务已确认
	private HyAdmin cwAuditor;	//财务审核人
	private Department branch;	//所属分公司
	private String cusName;	//游客姓名
	private String cusBank;	//游客银行卡号
	private String cusUninum;	//游客联行号
	private String reason;	//理由
	private BigDecimal adjustMoney;	//调整金额
	
	@Id
	@Column(name="id",unique=true,nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="type")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="order_id")
	public HyOrder getOrder() {
		return order;
	}
	public void setOrder(HyOrder order) {
		this.order = order;
	}
	
	@Column(name="money")
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	
	@Column(name="method")
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operator")
	public HyAdmin getOperator() {
		return operator;
	}
	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="store_id")
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	@Column(name="collection_time",length=19)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCollectionTime() {
		return collectionTime;
	}
	public void setCollectionTime(Date collectionTime) {
		this.collectionTime = collectionTime;
	}
	
	@Column(name="create_time",length=19)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name="remark")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Column(name="bank_num")
	public String getBankNum() {
		return bankNum;
	}
	public void setBankNum(String bankNum) {
		this.bankNum = bankNum;
	}
	
	@Column(name="status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cw_auditor")
	public HyAdmin getCwAuditor() {
		return cwAuditor;
	}
	public void setCwAuditor(HyAdmin cwAuditor) {
		this.cwAuditor = cwAuditor;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="branch")
	public Department getBranch() {
		return branch;
	}
	public void setBranch(Department branch) {
		this.branch = branch;
	}
	
	@Column(name="cus_name")
	public String getCusName() {
		return cusName;
	}
	public void setCusName(String cusName) {
		this.cusName = cusName;
	}
	@Column(name="cus_bank")
	public String getCusBank() {
		return cusBank;
	}
	public void setCusBank(String cusBank) {
		this.cusBank = cusBank;
	}
	@Column(name="cus_uninum")
	public String getCusUninum() {
		return cusUninum;
	}
	public void setCusUninum(String cusUninum) {
		this.cusUninum = cusUninum;
	}
	@Column(name="reason")
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	@Column(name="adjust_money")
	public BigDecimal getAdjustMoney() {
		return adjustMoney;
	}
	public void setAdjustMoney(BigDecimal adjustMoney) {
		this.adjustMoney = adjustMoney;
	}
	

	
	
	

}
