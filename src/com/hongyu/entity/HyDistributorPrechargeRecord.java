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

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@Table(name = "hy_distributor_precharge_record")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
public class HyDistributorPrechargeRecord implements Serializable {
    private Long id;
    private BigDecimal chargeMoney;
    private Integer chargeType; //1-cash,2-pos,3-account transfer
    private Integer checkStatus; //1-checking,2-passed,3-reject
    private Date chargeDate;
    private BankList bankList;
    private String remark;
    private HyAdmin operator;
    private HyDistributorManagement distributor;
    private String processInstanceId;
    private BigDecimal balance;
    private HyAdmin auditor;
    private Date checkDate;
    private String checkComment;
    

	@Column(name="process_instance_id")
    public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="charge_money")
	public BigDecimal getChargeMoney() {
		return chargeMoney;
	}
	public void setChargeMoney(BigDecimal chargeMoney) {
		this.chargeMoney = chargeMoney;
	}
	
	@Column(name="charge_type")
	public Integer getChargeType() {
		return chargeType;
	}
	public void setChargeType(Integer chargeType) {
		this.chargeType = chargeType;
	}
	
	@Column(name="check_status")
	public Integer getCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(Integer checkStatus) {
		this.checkStatus = checkStatus;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="charge_date", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getChargeDate() {
		return chargeDate;
	}
	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="distributor_management_id")
	public HyDistributorManagement getDistributor() {
		return distributor;
	}
	public void setDistributor(HyDistributorManagement distributor) {
		this.distributor = distributor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="bank_account_id")
	public BankList getBankList() {
		return bankList;
	}
	public void setBankList(BankList bankList) {
		this.bankList = bankList;
	}
	
	@Column(name="remark")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="operator")
	public HyAdmin getOperator() {
		return operator;
	}
	public void setOperator(HyAdmin operator) {
		this.operator = operator;
	}
	
	@Column(name="balance")
    public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="auditor")
	public HyAdmin getAuditor() {
		return auditor;
	}
	public void setAuditor(HyAdmin auditor) {
		this.auditor = auditor;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="check_date")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	
	@Column(name="check_comment")
	public String getCheckComment() {
		return checkComment;
	}
	public void setCheckComment(String checkComment) {
		this.checkComment = checkComment;
	}
}
