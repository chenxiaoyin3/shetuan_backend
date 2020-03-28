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
@Table(name = "hy_distributor_settlement")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
public class HyDistributorSettlement implements Serializable {
    private Long id;
    private HyDistributorManagement distributor;
    private BigDecimal money;
	private Date startDate;
	private Date endDate;
    private Date payDate;
    private BankList bankList;
    private String remark;
    private HyAdmin operator;
    private Integer checkStatus; //1-checking,2-passed,3-reject
    private String comment;
    private HyAdmin auditor;
    private Date checkTime;
    private BigDecimal chargeBalance;
    private String processInstanceId;
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="distributor_id")
	public HyDistributorManagement getDistributor() {
		return distributor;
	}
	public void setDistributor(HyDistributorManagement distributor) {
		this.distributor = distributor;
	}
	
	@Column(name="distribution_money")
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_date", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="pay_date", length=19)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "collection_account_id")
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
	public HyAdmin getOperator(){
		return operator;
	}
	public void setOperator(HyAdmin operator){
		this.operator=operator;
	}
	
	@Column(name="check_status")
	public Integer getCheckStatus(){
		return checkStatus;
	}
	public void setCheckStatus(Integer checkStatus){
		this.checkStatus=checkStatus;
	}
	
	@Column(name="check_comment")
	public String getComment(){
		return comment;
	}
	public void setComment(String comment){
		this.comment=comment;
	}
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="auditor")
	public HyAdmin getAuditor(){
		return auditor;
	}
	public void setAuditor(HyAdmin auditor){
		this.auditor=auditor;
	}
	
	@Column(name="check_time")
	public Date getCheckTime(){
		return checkTime;
	}
	public void setCheckTime(Date checkTime){
		this.checkTime=checkTime;
	}
	
	@Column(name="charge_balance")
    public BigDecimal getChargeBalance() {
		return chargeBalance;
	}
	public void setChargeBalance(BigDecimal chargeBalance) {
		this.chargeBalance = chargeBalance;
	}
	
	@Column(name="process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
}
