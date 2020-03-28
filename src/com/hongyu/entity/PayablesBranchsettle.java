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


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.util.Constants.DeductLine;
@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","hyGroup",
	})
@Table(name = "hy_payables_branchsettle")
public class PayablesBranchsettle implements Serializable {
    private Long id;
    private HyGroup hyGroup;
    private DeductLine koudianType;
    private BigDecimal koudian;
    private BigDecimal koudianMoney;
    private BigDecimal yufuMoney;
    private BigDecimal shifuMoney; //实际支付金额,包含退款
    private BigDecimal realMoney; //不包含退款
    private Date applyTime;
    private HyAdmin applyName;
    private String processInstanceId;
    private Integer auditStatus; //0-未提交,1-审核中,2-审核通过,3-审核驳回
    private String adjustReason; //调整原因,只有在驳回以后才有
    private BigDecimal adjustMoney; //调整金额，只有在驳回以后才有
    private String payNumber; //打款单号
    private Boolean applySource; //true-首次提交，false-驳回后提交
    private Integer step; //0:驳回,待计调处理；1:待市场部副总限额审核 2:待总公司财务审核,3:财务审核通过
    private Boolean isModify;
    private BigDecimal debt;
  
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	public HyGroup getHyGroup() {
		return hyGroup;
	}
	public void setHyGroup(HyGroup hyGroup) {
		this.hyGroup = hyGroup;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="apply_time", length=19)
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "apply_name")
	public HyAdmin getApplyName() {
		return applyName;
	}
	public void setApplyName(HyAdmin applyName) {
		this.applyName = applyName;
	}
	
	@Column(name="audit_status")
	public Integer getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}
	
	@Column(name="koudian_type")
	public DeductLine getKoudianType() {
		return koudianType;
	}
	public void setKoudianType(DeductLine koudianType) {
		this.koudianType = koudianType;
	}
	
	@Column(name="koudian")
	public BigDecimal getKoudian() {
		return koudian;
	}
	public void setKoudian(BigDecimal koudian) {
		this.koudian = koudian;
	}
	
	@Column(name="koudian_money")
	public BigDecimal getKoudianMoney() {
		return koudianMoney;
	}
	public void setKoudianMoney(BigDecimal koudianMoney) {
		this.koudianMoney = koudianMoney;
	}
	
	@Column(name="yufu_money")
	public BigDecimal getYufuMoney() {
		return yufuMoney;
	}
	public void setYufuMoney(BigDecimal yufuMoney) {
		this.yufuMoney = yufuMoney;
	}
	
	@Column(name="shifu_money")
	public BigDecimal getShifuMoney() {
		return shifuMoney;
	}
	public void setShifuMoney(BigDecimal shifuMoney) {
		this.shifuMoney = shifuMoney;
	}
	
	@Column(name="real_money")
	public BigDecimal getRealMoney() {
		return realMoney;
	}
	public void setRealMoney(BigDecimal realMoney) {
		this.realMoney = realMoney;
	}
	
	@Column(name="process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	@Column(name="adjust_reason")
	public String getAdjustReason() {
		return adjustReason;
	}
	public void setAdjustReason(String adjustReason) {
		this.adjustReason = adjustReason;
	}
	
	@Column(name="adjust_money")
	public BigDecimal getAdjustMoney() {
		return adjustMoney;
	}
	public void setAdjustMoney(BigDecimal adjustMoney) {
		this.adjustMoney = adjustMoney;
	}
	
	@Column(name="pay_number")
	public String getPayNumber() {
		return payNumber;
	}
	public void setPayNumber(String payNumber) {
		this.payNumber = payNumber;
	}
	
	@Column(name="apply_source")
	public Boolean getApplySource() {
		return applySource;
	}
	public void setApplySource(Boolean applySource) {
		this.applySource = applySource;
	}
	
	@Column(name="step")
	public Integer getStep() {
		return step;
	}
	public void setStep(Integer step) {
		this.step = step;
	}
	
	@Column(name="is_modify")
	public Boolean getIsModify() {
		return isModify;
	}
	public void setIsModify(Boolean isModify) {
		this.isModify = isModify;
	}
	public BigDecimal getDebt() {
		return debt;
	}
	public void setDebt(BigDecimal debt) {
		this.debt = debt;
	}
	
}
