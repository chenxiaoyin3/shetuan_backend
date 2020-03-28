package com.hongyu.entity;

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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 供货商结算明细
 * @author JZhong
 *
 */

@Entity
@Table(name="hy_provider_balance")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProviderBalance implements java.io.Serializable{
	private Long id;
	private Provider provider;
	//供货商名称
	private String name;
	//结算类型, 0:单结, 1:月结
	private Integer balanceType;
	//结算金额
	private BigDecimal balanceMoney;
	//结算开始时间
	private Date startTime;
	//结算结束时间
	private Date endTime;
	//结算付款时间
	private Date balanceTime;
	//生成时间
	private Date createTime;
	//状态,false:未结算. true:已结算
	private Boolean state;
	//结算操作员
	private HyAdmin operator;
	//是否审核,false:未审核,true:已审核
	private Boolean isAudited;
	//收款方名称
	private String payeeName;
	//收款方账户
	private String payeeAccount;
	//收款方开户行
	private String payeeBank;
	//付款方名称
	private String payerName;
	//付款方开户行
	private String payerBank;
	//付款方开户行
	private String payerAccount;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="provider_id")
	public Provider getProvider() {
		return provider;
	}
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="balance_type")
	public Integer getBalanceType() {
		return balanceType;
	}
	public void setBalanceType(Integer balanceType) {
		this.balanceType = balanceType;
	}
	
	@Column(name="balance_money")
	public BigDecimal getBalanceMoney() {
		return balanceMoney;
	}
	public void setBalanceMoney(BigDecimal balanceMoney) {
		this.balanceMoney = balanceMoney;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_time", length=19)
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_time", length=19)
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="balance_time", length=19)
	public Date getBalanceTime() {
		return balanceTime;
	}
	public void setBalanceTime(Date balanceTime) {
		this.balanceTime = balanceTime;
	}
	
	@Column(name="state")
	public Boolean getState() {
		return state;
	}
	public void setState(Boolean state) {
		this.state = state;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operator")
	public HyAdmin getOperator() {
		return operator;
	}
	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time", length=19)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name="is_audited")
	public Boolean getIsAudited() {
		return isAudited;
	}
	public void setIsAudited(Boolean isAudited) {
		this.isAudited = isAudited;
	}
	
	@Column(name="payee_name")
	public String getPayeeName() {
		return payeeName;
	}
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	
	@Column(name="payee_account")
	public String getPayeeAccount() {
		return payeeAccount;
	}
	public void setPayeeAccount(String payeeAccount) {
		this.payeeAccount = payeeAccount;
	}
	
	@Column(name="payee_bank")
	public String getPayeeBank() {
		return payeeBank;
	}
	public void setPayeeBank(String payeeBank) {
		this.payeeBank = payeeBank;
	}
	
	@Column(name="payer_name")
	public String getPayerName() {
		return payerName;
	}
	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}
	
	@Column(name="payer_bank")
	public String getPayerBank() {
		return payerBank;
	}
	public void setPayerBank(String payerBank) {
		this.payerBank = payerBank;
	}
	
	@Column(name="payer_account")
	public String getPayerAccount() {
		return payerAccount;
	}
	public void setPayerAccount(String payerAccount) {
		this.payerAccount = payerAccount;
	}
	
	@PrePersist
	public void prePersist() {
		this.createTime = new Date();
		this.state = false;
		this.isAudited = false;
	}
	
	
}
